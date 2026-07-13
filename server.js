const express = require('express');
const axios = require('axios');
require('dotenv').config();

const app = express();
app.use(express.json());

const PORT = process.env.PORT || 3000;
const GEMINI_API_KEY = process.env.GEMINI_API_KEY;

/**
 * Chat Endpoint
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
app.post('/chat', async (req, res) => {
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

        // Return secure response to client app
        res.json({ response: responseText });

    } catch (error) {
        console.error('Error executing chat generation:', error.response?.data || error.message);
        const status = error.response?.status || 500;
        const errMsg = error.response?.data?.error?.message || 'Failed to communicate with official Gemini API.';
        res.status(status).json({ error: errMsg });
    }
});

// Start listening
app.listen(PORT, () => {
    console.log(`Secure Arama Proxy Server is running on port ${PORT}`);
});
