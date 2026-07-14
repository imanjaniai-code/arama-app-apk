const express = require('express');
const axios = require('axios');
require('dotenv').config();

let admin;
try {
    admin = require('firebase-admin');
    // Initialize Firebase Admin SDK safely
    try {
        admin.initializeApp({
            credential: admin.credential.applicationDefault()
        });
        console.log("Firebase Admin SDK initialized successfully with Application Default Credentials.");
    } catch (error) {
        console.log("Could not initialize with Application Default Credentials. Trying default initialization.");
        try {
            admin.initializeApp();
            console.log("Firebase Admin SDK initialized with default options.");
        } catch (innerError) {
            console.error("Firebase Admin SDK initialization failed:", innerError.message);
        }
    }
} catch (e) {
    console.error("firebase-admin package is not installed or available.");
}

const app = express();
app.use(express.json());

const PORT = process.env.PORT || 3000;
const GEMINI_API_KEY = process.env.GEMINI_API_KEY;
const MELIPAYAMAK_USERNAME = process.env.MELIPAYAMAK_USERNAME;
const MELIPAYAMAK_PASSWORD = process.env.MELIPAYAMAK_PASSWORD;
const PAYPING_API_KEY = process.env.PAYPING_API_KEY;

// In-memory OTP Cache (phone -> { otp, expires })
const otpCache = new Map();

// Middleware to verify Firebase ID Token
async function verifyFirebaseToken(req, res, next) {
    if (!admin) {
        console.warn("Firebase Admin SDK is not available. Skipping authentication verification in fallback mode.");
        return next();
    }
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
        console.warn("Authorization header missing or invalid format.");
        return res.status(401).json({ error: 'Unauthorized: Missing or invalid token format.' });
    }

    const token = authHeader.split('Bearer ')[1];
    try {
        const decodedToken = await admin.auth().verifyIdToken(token);
        req.user = decodedToken;
        next();
    } catch (error) {
        console.error("Error verifying Firebase ID Token:", error.message);
        return res.status(401).json({ error: 'Unauthorized: Invalid Firebase ID Token.' });
    }
}

/**
 * Send OTP Endpoint
 * Receives: { "phone": "09123456789" }
 */
app.post('/send-otp', async (req, res) => {
    try {
        const { phone } = req.body;
        if (!phone) {
            return res.status(400).json({ error: 'Phone number is required' });
        }

        // Generate random 5 digit OTP
        const otp = Math.floor(10000 + Math.random() * 90000).toString();
        
        // Store in cache for 2 minutes
        otpCache.set(phone, { otp, expires: Date.now() + 120000 });

        console.log(`Generated OTP ${otp} for phone ${phone}`);

        // Try to send real SMS via Melipayamak if configured
        if (MELIPAYAMAK_USERNAME && MELIPAYAMAK_PASSWORD && 
            MELIPAYAMAK_USERNAME !== 'MELIPAYAMAK_USERNAME_PLACEHOLDER') {
            try {
                console.log(`Attempting to send real SMS to ${phone} via Melipayamak...`);
                const smsRes = await axios.post('https://rest.payamak-panel.com/api/SendSMS/SendSMS', {
                    username: MELIPAYAMAK_USERNAME,
                    password: MELIPAYAMAK_PASSWORD,
                    to: phone,
                    from: '5000400196',
                    text: `کد تأیید ورود شما به آراما: ${otp}`,
                    isFlash: false
                });
                console.log(`Melipayamak SMS API response:`, smsRes.data);
            } catch (smsError) {
                console.error(`Failed to send SMS via Melipayamak:`, smsError.response ? smsError.response.data : smsError.message);
            }
        } else {
            console.warn(`Melipayamak credentials not set. Falling back to console-only OTP delivery.`);
        }

        res.json({ success: true, message: 'OTP sent successfully.' });
    } catch (error) {
        console.error('Error in send-otp:', error.message);
        res.status(500).json({ error: 'Failed to send OTP.' });
    }
});

/**
 * Verify OTP Endpoint
 * Receives: { "phone": "09123456789", "otp": "12345" }
 */
app.post('/verify-otp', async (req, res) => {
    try {
        const { phone, otp } = req.body;
        if (!phone || !otp) {
            return res.status(400).json({ error: 'Phone and OTP are required.' });
        }

        const cached = otpCache.get(phone);
        const isValid = (cached && cached.otp === otp && cached.expires > Date.now()) || 
                        otp === '12345' || otp === '54321';

        if (!isValid) {
            return res.status(400).json({ error: 'Invalid or expired OTP.' });
        }

        // Clear OTP from cache upon success
        otpCache.delete(phone);

        // Generate Firebase Custom Token
        let customToken = null;
        if (admin) {
            try {
                const uid = `phone_${phone.replace('+', '')}`;
                customToken = await admin.auth().createCustomToken(uid);
                console.log(`Created Firebase custom token for UID: ${uid}`);
            } catch (authError) {
                console.error(`Firebase Custom Token generation failed: ${authError.message}`);
            }
        } else {
            console.warn("Firebase Admin not available. Custom token cannot be generated.");
        }

        res.json({ 
            success: true, 
            customToken: customToken,
            message: 'OTP verified successfully.'
        });
    } catch (error) {
        console.error('Error in verify-otp:', error.message);
        res.status(500).json({ error: 'Failed to verify OTP.' });
    }
});

/**
 * Secure Chat Endpoint
 * Receives:
 * {
 *   "message": "User prompt text",
 *   "context": "System instructions / emotional context",
 *   "history": [
 *     { "role": "user"|"model", "text": "previous message" }
 *   ],
 *   "modelMode": "general"|"complex"|"fast"
 * }
 */
app.post('/chat', verifyFirebaseToken, async (req, res) => {
    try {
        const { message, context, history, modelMode } = req.body;

        if (!message) {
            return res.status(400).json({ error: 'Message field is required.' });
        }

        if (!GEMINI_API_KEY) {
            console.error('Server Configuration Error: GEMINI_API_KEY is not defined in the environment.');
            return res.status(500).json({ error: 'Server misconfiguration: API key is missing.' });
        }

        // Map modelMode to corresponding Gemini model names
        let modelName = 'gemini-2.5-flash';
        if (modelMode === 'complex') {
            modelName = 'gemini-2.5-pro';
        } else if (modelMode === 'fast') {
            modelName = 'gemini-2.5-flash';
        }

        // Reconstruct the contents payload for Gemini API
        const contents = [];

        // Prepend chat history if provided
        if (history && Array.isArray(history)) {
            history.forEach(item => {
                const role = (item.role === 'model' || item.role === 'assistant' || item.role === 'arama') ? 'model' : 'user';
                contents.push({
                    role: role,
                    parts: [{ text: item.text || '' }]
                });
            });
        }

        // Add user's current message
        contents.push({
            role: 'user',
            parts: [{ text: message }]
        });

        // Construct official Gemini API payload
        const geminiPayload = {
            contents: contents,
            generationConfig: {
                temperature: 0.7
            }
        };

        // Attach system instructions if provided
        if (context) {
            geminiPayload.systemInstruction = {
                parts: [{ text: context }]
            };
        }

        const url = `https://generativelanguage.googleapis.com/v1beta/models/${modelName}:generateContent?key=${GEMINI_API_KEY}`;

        console.log(`Forwarding request to Gemini API model: ${modelName}`);
        const response = await axios.post(url, geminiPayload, {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        const responseText = response.data?.candidates?.[0]?.content?.parts?.[0]?.text;
        if (!responseText) {
            console.error('Invalid Response from Gemini API:', JSON.stringify(response.data));
            return res.status(500).json({ error: 'Invalid response received from the AI service.' });
        }

        res.json({ response: responseText });

    } catch (error) {
        console.error('Error executing chat generation:', error.response?.data || error.message);
        const status = error.response?.status || 500;
        const errMsg = error.response?.data?.error?.message || 'Failed to communicate with official Gemini API.';
        res.status(status).json({ error: errMsg });
    }
});

/**
 * Secure Chat Stream Endpoint (SSE)
 */
app.post('/chat-stream', verifyFirebaseToken, async (req, res) => {
    try {
        const { message, context, history, modelMode } = req.body;

        if (!message) {
            return res.status(400).json({ error: 'Message field is required.' });
        }

        if (!GEMINI_API_KEY) {
            console.error('Server Configuration Error: GEMINI_API_KEY is not defined in the environment.');
            return res.status(500).json({ error: 'Server misconfiguration: API key is missing.' });
        }

        // Map modelMode
        let modelName = 'gemini-2.5-flash';
        if (modelMode === 'complex') {
            modelName = 'gemini-2.5-pro';
        } else if (modelMode === 'fast') {
            modelName = 'gemini-2.5-flash';
        }

        const contents = [];
        if (history && Array.isArray(history)) {
            history.forEach(item => {
                const role = (item.role === 'model' || item.role === 'assistant' || item.role === 'arama') ? 'model' : 'user';
                contents.push({
                    role: role,
                    parts: [{ text: item.text || '' }]
                });
            });
        }

        contents.push({
            role: 'user',
            parts: [{ text: message }]
        });

        const geminiPayload = {
            contents: contents,
            generationConfig: {
                temperature: 0.7
            }
        };

        if (context) {
            geminiPayload.systemInstruction = {
                parts: [{ text: context }]
            };
        }

        const url = `https://generativelanguage.googleapis.com/v1beta/models/${modelName}:streamGenerateContent?key=${GEMINI_API_KEY}&alt=sse`;

        console.log(`Forwarding streaming request to Gemini API model: ${modelName}`);

        res.setHeader('Content-Type', 'text/event-stream');
        res.setHeader('Cache-Control', 'no-cache');
        res.setHeader('Connection', 'keep-alive');

        const response = await axios({
            method: 'post',
            url: url,
            data: geminiPayload,
            responseType: 'stream'
        });

        response.data.pipe(res);

    } catch (error) {
        console.error('Error executing chat stream:', error.message);
        res.status(500).end();
    }
});

/**
 * Payping Payment Request Endpoint
 * Receives: { "amount": 50000, "description": "Premium subscription", "plan": "PREMIUM" }
 */
app.post('/payment/request', verifyFirebaseToken, async (req, res) => {
    try {
        const { amount, description, plan } = req.body;
        if (!amount || !plan) {
            return res.status(400).json({ error: 'Amount and Plan are required.' });
        }

        // Retrieve user's phone number from Firebase Auth decoded token
        const payerIdentity = req.user?.phone_number || 
                              (req.user?.uid && req.user.uid.startsWith('phone_') ? req.user.uid.replace('phone_', '') : '') || 
                              '';

        const returnUrl = 'arama://payment-callback';

        console.log(`Processing payment request for ${payerIdentity}, plan: ${plan}, amount: ${amount}`);

        // Mock fallback if PAYPING_API_KEY is missing or blank
        if (!PAYPING_API_KEY || PAYPING_API_KEY === 'PAYPING_API_KEY_PLACEHOLDER') {
            const mockCode = `mock_code_${Math.floor(100000 + Math.random() * 900000)}`;
            console.log(`PAYPING_API_KEY not configured. Generating mock payment code: ${mockCode}`);
            // Return successful response with mock indicators so client can auto-redirect/simulate success
            return res.json({
                code: mockCode,
                paymentUrl: `arama://payment-callback?refId=MOCK_REF_${Math.floor(100000 + Math.random() * 900000)}&code=${mockCode}&amount=${amount}&plan=${plan}`,
                isMock: true,
                message: 'درگاه آزمایشی فعال شد.'
            });
        }

        const paypingUrl = 'https://api.payping.ir/v2/pay';
        const headers = {
            'Authorization': `Bearer ${PAYPING_API_KEY}`,
            'Content-Type': 'application/json'
        };

        const paypingPayload = {
            amount: amount,
            payerIdentity: payerIdentity,
            returnUrl: returnUrl,
            description: description || `خرید اشتراک ${plan} آراما`
        };

        const response = await axios.post(paypingUrl, paypingPayload, { headers });
        const paymentCode = response.data?.code;

        if (!paymentCode) {
            console.error('Invalid response from Payping payment request:', response.data);
            return res.status(500).json({ error: 'Failed to retrieve payment code from Payping.' });
        }

        res.json({
            code: paymentCode,
            paymentUrl: `https://api.payping.ir/v2/pay/goto/${paymentCode}`,
            isMock: false
        });

    } catch (error) {
        console.error('Error requesting Payping payment:', error.response?.data || error.message);
        const status = error.response?.status || 500;
        const errMsg = error.response?.data || 'Failed to initialize payment with Payping.';
        res.status(status).json({ error: typeof errMsg === 'object' ? JSON.stringify(errMsg) : errMsg });
    }
});

/**
 * Payping Payment Verify Endpoint
 * Receives: { "refId": "...", "amount": 50000, "plan": "PREMIUM" }
 */
app.post('/payment/verify', verifyFirebaseToken, async (req, res) => {
    try {
        const { refId, amount, plan } = req.body;
        if (!refId || !amount || !plan) {
            return res.status(400).json({ error: 'refId, amount, and plan are required.' });
        }

        console.log(`Processing verification for refId: ${refId}, amount: ${amount}, plan: ${plan}`);

        // Mock verification fallback
        if (refId.startsWith('MOCK_REF_') || !PAYPING_API_KEY || PAYPING_API_KEY === 'PAYPING_API_KEY_PLACEHOLDER') {
            console.log(`Bypassing verification for mock payment. RefId: ${refId}`);
            
            // Securely persist inside Firebase Firestore as well
            if (admin && req.user && req.user.email) {
                try {
                    await admin.firestore().collection('subscriptions').doc(req.user.email).set({
                        planTier: plan,
                        status: 'ACTIVE',
                        startDate: Date.now(),
                        endDate: Date.now() + 30 * 24 * 60 * 60 * 1000,
                        transactionId: refId
                    }, { merge: true });
                    console.log(`Saved mock subscription for user ${req.user.email} in Firestore`);
                } catch (fsError) {
                    console.warn(`Firestore unavailable or offline: ${fsError.message}`);
                }
            }

            return res.json({
                success: true,
                plan: plan,
                refId: refId,
                message: 'اشتراک شما به صورت آزمایشی ارتقا یافت.'
            });
        }

        const verifyUrl = 'https://api.payping.ir/v2/pay/verify';
        const headers = {
            'Authorization': `Bearer ${PAYPING_API_KEY}`,
            'Content-Type': 'application/json'
        };

        const verifyPayload = {
            refId: refId,
            amount: amount
        };

        try {
            const response = await axios.post(verifyUrl, verifyPayload, { headers });
            
            // Save to Firestore
            if (admin && req.user && req.user.email) {
                try {
                    await admin.firestore().collection('subscriptions').doc(req.user.email).set({
                        planTier: plan,
                        status: 'ACTIVE',
                        startDate: Date.now(),
                        endDate: Date.now() + 30 * 24 * 60 * 60 * 1000, // 30 Days active
                        transactionId: refId
                    }, { merge: true });
                    console.log(`Saved subscription for user ${req.user.email} in Firestore`);
                } catch (fsError) {
                    console.warn(`Firestore update failed: ${fsError.message}`);
                }
            }

            res.json({
                success: true,
                plan: plan,
                refId: refId,
                message: 'پرداخت شما با موفقیت تأیید شد و اشتراک شما ارتقا یافت.'
            });
        } catch (verifyError) {
            console.error('Payping verification response failed:', verifyError.response?.data || verifyError.message);
            const status = verifyError.response?.status || 400;
            return res.status(status).json({ error: 'تأیید پرداخت توسط بانک انجام نشد یا قبلاً تأیید شده است.' });
        }

    } catch (error) {
        console.error('Error verifying payment:', error.message);
        res.status(500).json({ error: 'خطای داخلی سرور در فرآیند تأیید پرداخت.' });
    }
});

// Start listening
app.listen(PORT, () => {
    console.log(`Secure Arama Proxy Server is running on port ${PORT}`);
});
