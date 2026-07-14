package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.SubscriptionPlans
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZarinpalPaymentDialog(
    planItem: SubscriptionPlans,
    onDismiss: () -> Unit,
    onPaymentSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    // Form fields
    var cardNumber by remember { mutableStateOf("") }
    var cvv2 by remember { mutableStateOf("") }
    var expMonth by remember { mutableStateOf("") }
    var expYear by remember { mutableStateOf("") }
    var secondPassword by remember { mutableStateOf("") }
    var captchaInput by remember { mutableStateOf("") }

    // Captcha generation
    var captchaCode by remember { mutableStateOf("") }
    fun generateCaptcha() {
        captchaCode = (1000..9999).random().toString()
    }
    LaunchedEffect(Unit) {
        generateCaptcha()
    }

    // Dynamic password state
    var otpSecondsLeft by remember { mutableStateOf(0) }
    var generatedOtp by remember { mutableStateOf("") }

    // Navigation/Progress States: "form", "loading", "success", "error"
    var paymentStep by remember { mutableStateOf("form") }
    var processingMessage by remember { mutableStateOf("در حال بررسی اطلاعات...") }
    var refIdResult by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Bank detection based on card number
    val detectedBank = remember(cardNumber) {
        val clean = cardNumber.replace(" ", "").replace("-", "")
        if (clean.length >= 6) {
            val prefix = clean.substring(0, 6)
            when (prefix) {
                "603799" -> "بانک ملی ایران"
                "610433" -> "بانک ملت"
                "621986" -> "بانک سامان"
                "627412" -> "بانک اقتصاد نوین"
                "589210" -> "بانک سپه"
                "627353" -> "بانک تجارت"
                "502229" -> "بانک پاسارگاد"
                "505785" -> "بانک ایران زمین"
                "622106" -> "بانک پارسیان"
                "639346" -> "بانک سینا"
                "502908" -> "بانک توسعه تعاون"
                "603770" -> "بانک کشاورزی"
                "628023" -> "بانک مسکن"
                "627760" -> "بانک پست بانک"
                else -> "کارت شتابی معتبر"
            }
        } else {
            "کارت عضو شتاب"
        }
    }

    // Timer effect for OTP
    LaunchedEffect(otpSecondsLeft) {
        if (otpSecondsLeft > 0) {
            delay(1000)
            otpSecondsLeft--
        }
    }

    Dialog(
        onDismissRequest = { if (paymentStep != "loading") onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = paymentStep != "loading",
            dismissOnClickOutside = paymentStep != "loading"
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F6F6)),
            color = Color(0xFFF6F6F6)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header of Zarinpal Gate
                ZarinpalGateHeader(onDismiss = onDismiss, canDismiss = paymentStep != "loading")

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedContent(
                    targetState = paymentStep,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "payment_step_transition"
                ) { step ->
                    when (step) {
                        "form" -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Merchant & Invoice Details Card
                                InvoiceDetailsCard(planItem = planItem)

                                // Main Card Details Input Card
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            text = "اطلاعات کارت بانکی",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF2E2E2E)
                                            ),
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Start
                                        )

                                        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                                        // Card Number Input
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "شماره کارت ۱۶ رقمی",
                                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                                                )
                                                Text(
                                                    text = detectedBank,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = SagePrimary,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            OutlinedTextField(
                                                value = cardNumber,
                                                onValueChange = { input ->
                                                    // Only allow digits and format with spaces
                                                    val clean = input.filter { it.isDigit() }
                                                    if (clean.length <= 16) {
                                                        // Format card number with hyphens or spaces
                                                        val formatted = clean.chunked(4).joinToString("-")
                                                        cardNumber = formatted
                                                        if (clean.length == 16) {
                                                            focusManager.moveFocus(FocusDirection.Down)
                                                        }
                                                    }
                                                },
                                                placeholder = { Text("xxxx-xxxx-xxxx-xxxx") },
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Number,
                                                    imeAction = ImeAction.Next
                                                ),
                                                leadingIcon = {
                                                    Icon(
                                                        imageVector = Icons.Default.CreditCard,
                                                        contentDescription = null,
                                                        tint = Color(0xFFF0B518)
                                                    )
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("zp_card_number"),
                                                shape = RoundedCornerShape(10.dp),
                                                colors = TextFieldDefaults.colors(
                                                    focusedContainerColor = Color.White,
                                                    unfocusedContainerColor = Color.White,
                                                    focusedIndicatorColor = Color(0xFFF0B518),
                                                    unfocusedIndicatorColor = Color(0xFFE0E0E0)
                                                )
                                            )
                                        }

                                        // CVV2 & Expiration Date Row
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            // CVV2
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "کد امنیتی CVV2",
                                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                                                    textAlign = TextAlign.Start,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                OutlinedTextField(
                                                    value = cvv2,
                                                    onValueChange = { input ->
                                                        val clean = input.filter { it.isDigit() }
                                                        if (clean.length <= 4) {
                                                            cvv2 = clean
                                                            if (clean.length >= 3) {
                                                                // auto-focus expiration
                                                            }
                                                        }
                                                    },
                                                    placeholder = { Text("۳ یا ۴ رقم") },
                                                    keyboardOptions = KeyboardOptions(
                                                        keyboardType = KeyboardType.Number,
                                                        imeAction = ImeAction.Next
                                                    ),
                                                    visualTransformation = PasswordVisualTransformation(),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .testTag("zp_cvv2"),
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = TextFieldDefaults.colors(
                                                        focusedContainerColor = Color.White,
                                                        unfocusedContainerColor = Color.White,
                                                        focusedIndicatorColor = Color(0xFFF0B518),
                                                        unfocusedIndicatorColor = Color(0xFFE0E0E0)
                                                    )
                                                )
                                            }

                                            // Expiration Date
                                            Column(modifier = Modifier.weight(1.2f)) {
                                                Text(
                                                    text = "تاریخ انقضا کارت",
                                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                                                    textAlign = TextAlign.Start,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    // Month
                                                    OutlinedTextField(
                                                        value = expMonth,
                                                        onValueChange = { input ->
                                                            val clean = input.filter { it.isDigit() }
                                                            if (clean.length <= 2) {
                                                                expMonth = clean
                                                                if (clean.length == 2) {
                                                                    focusManager.moveFocus(FocusDirection.Right)
                                                                }
                                                            }
                                                        },
                                                        placeholder = { Text("ماه") },
                                                        keyboardOptions = KeyboardOptions(
                                                            keyboardType = KeyboardType.Number,
                                                            imeAction = ImeAction.Next
                                                        ),
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .testTag("zp_exp_month"),
                                                        shape = RoundedCornerShape(10.dp),
                                                        colors = TextFieldDefaults.colors(
                                                            focusedContainerColor = Color.White,
                                                            unfocusedContainerColor = Color.White,
                                                            focusedIndicatorColor = Color(0xFFF0B518),
                                                            unfocusedIndicatorColor = Color(0xFFE0E0E0)
                                                        )
                                                    )

                                                    Text("/", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)

                                                    // Year
                                                    OutlinedTextField(
                                                        value = expYear,
                                                        onValueChange = { input ->
                                                            val clean = input.filter { it.isDigit() }
                                                            if (clean.length <= 2) {
                                                                expYear = clean
                                                            }
                                                        },
                                                        placeholder = { Text("سال") },
                                                        keyboardOptions = KeyboardOptions(
                                                            keyboardType = KeyboardType.Number,
                                                            imeAction = ImeAction.Next
                                                        ),
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .testTag("zp_exp_year"),
                                                        shape = RoundedCornerShape(10.dp),
                                                        colors = TextFieldDefaults.colors(
                                                            focusedContainerColor = Color.White,
                                                            unfocusedContainerColor = Color.White,
                                                            focusedIndicatorColor = Color(0xFFF0B518),
                                                            unfocusedIndicatorColor = Color(0xFFE0E0E0)
                                                        )
                                                    )
                                                }
                                            }
                                        }

                                        // Security Captcha Code
                                        Column {
                                            Text(
                                                text = "کد امنیتی تصویر",
                                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                OutlinedTextField(
                                                    value = captchaInput,
                                                    onValueChange = { input ->
                                                        val clean = input.filter { it.isDigit() }
                                                        if (clean.length <= 4) captchaInput = clean
                                                    },
                                                    placeholder = { Text("کد تصویر") },
                                                    keyboardOptions = KeyboardOptions(
                                                        keyboardType = KeyboardType.Number,
                                                        imeAction = ImeAction.Next
                                                    ),
                                                    modifier = Modifier
                                                        .weight(1.2f)
                                                        .testTag("zp_captcha"),
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = TextFieldDefaults.colors(
                                                        focusedContainerColor = Color.White,
                                                        unfocusedContainerColor = Color.White,
                                                        focusedIndicatorColor = Color(0xFFF0B518),
                                                        unfocusedIndicatorColor = Color(0xFFE0E0E0)
                                                    )
                                                )

                                                // Image representation of captcha
                                                Row(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(54.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(Color(0xFFEAEAEA))
                                                        .clickable { generateCaptcha() },
                                                    horizontalArrangement = Arrangement.Center,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = captchaCode,
                                                        fontSize = 20.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        letterSpacing = 4.sp,
                                                        color = Color.DarkGray
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Icon(
                                                        imageVector = Icons.Default.Refresh,
                                                        contentDescription = "کد جدید",
                                                        tint = Color.Gray,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }

                                        // Second Password / Dynamic OTP
                                        Column {
                                            Text(
                                                text = "رمز دوم کارت (رمز پویا)",
                                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                OutlinedTextField(
                                                    value = secondPassword,
                                                    onValueChange = { input ->
                                                        val clean = input.filter { it.isDigit() }
                                                        if (clean.length <= 8) secondPassword = clean
                                                    },
                                                    placeholder = { Text("رمز دوم یا رمز پویا") },
                                                    keyboardOptions = KeyboardOptions(
                                                        keyboardType = KeyboardType.Number,
                                                        imeAction = ImeAction.Done
                                                    ),
                                                    visualTransformation = PasswordVisualTransformation(),
                                                    modifier = Modifier
                                                        .weight(1.3f)
                                                        .testTag("zp_otp"),
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = TextFieldDefaults.colors(
                                                        focusedContainerColor = Color.White,
                                                        unfocusedContainerColor = Color.White,
                                                        focusedIndicatorColor = Color(0xFFF0B518),
                                                        unfocusedIndicatorColor = Color(0xFFE0E0E0)
                                                    )
                                                )

                                                // OTP Request button
                                                Button(
                                                    onClick = {
                                                        if (otpSecondsLeft == 0) {
                                                            otpSecondsLeft = 120
                                                            val generated = (100000..999999).random().toString()
                                                            generatedOtp = generated
                                                            Toast.makeText(
                                                                context,
                                                                "درخواست رمز پویا با موفقیت ارسال شد.",
                                                                Toast.LENGTH_SHORT
                                                            ).show()

                                                            // Simulate SMS Receive delay
                                                            scope.launch {
                                                                delay(2500)
                                                                Toast.makeText(
                                                                    context,
                                                                    "[زرین‌پال] رمز پویای شما برای خرید اشتراک آراما: $generated (معتبر تا ۲ دقیقه)",
                                                                    Toast.LENGTH_LONG
                                                                ).show()
                                                                // Auto fill OTP for ultimate user experience
                                                                secondPassword = generated
                                                            }
                                                        }
                                                    },
                                                    enabled = otpSecondsLeft == 0,
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = Color(0xFFF0B518),
                                                        disabledContainerColor = Color(0xFFEEEEEE),
                                                        disabledContentColor = Color.Gray
                                                    ),
                                                    shape = RoundedCornerShape(10.dp),
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(54.dp)
                                                ) {
                                                    Text(
                                                        text = if (otpSecondsLeft > 0) "$otpSecondsLeft ثانیه" else "رمز پویا 📱",
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (otpSecondsLeft > 0) Color.DarkGray else Color.White
                                                        ),
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Security advice and actions
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9E6)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = null,
                                            tint = Color(0xFFF0B518),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "پرداخت شما مستقیماً توسط پروتکل شاپرک تحت درگاه امن زرین‌پال پردازش می‌شود. رمز پویا به شماره همراه ثبت شده ارسال می‌گردد.",
                                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF5C4E1A)),
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Bottom actions
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            // Validate inputs
                                            val cleanCard = cardNumber.replace("-", "")
                                            if (cleanCard.length != 16) {
                                                Toast.makeText(context, "لطفاً شماره کارت ۱۶ رقمی را کامل کنید.", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            if (cvv2.length < 3) {
                                                Toast.makeText(context, "لطفاً کد امنیتی CVV2 را به درستی وارد کنید.", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            if (expMonth.isEmpty() || expYear.isEmpty()) {
                                                Toast.makeText(context, "لطفاً تاریخ انقضای کارت خود را کامل کنید.", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            if (captchaInput != captchaCode) {
                                                Toast.makeText(context, "کد امنیتی تصویر اشتباه وارد شده است.", Toast.LENGTH_SHORT).show()
                                                generateCaptcha()
                                                return@Button
                                            }
                                            if (secondPassword.length < 5) {
                                                Toast.makeText(context, "رمز دوم کارت (رمز پویا) نامعتبر است.", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }

                                            // Start simulation payment processing
                                            scope.launch {
                                                paymentStep = "loading"
                                                processingMessage = "اتصال ایمن با زرین‌پال برقرار شد..."
                                                delay(1200)
                                                processingMessage = "ارسال اطلاعات پرداخت به سامانه شتاب..."
                                                delay(1500)
                                                processingMessage = "تأیید تراکنش با موفقیت انجام شد. شناسه مرجع تولید شد..."
                                                delay(1000)

                                                val randomRefId = (10000000..99999999).random().toString()
                                                refIdResult = randomRefId
                                                paymentStep = "success"
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(1.5f)
                                            .height(50.dp)
                                            .testTag("zp_pay_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "پرداخت امن",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = onDismiss,
                                        shape = RoundedCornerShape(12.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(50.dp)
                                    ) {
                                        Text(
                                            text = "انصراف",
                                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray)
                                        )
                                    }
                                }
                            }
                        }

                        "loading" -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFFF0B518),
                                    strokeWidth = 4.dp,
                                    modifier = Modifier.size(64.dp)
                                )
                                Text(
                                    text = processingMessage,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = "لطفاً از دکمه بازگشت یا بستن برنامه خودداری کنید.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        "success" -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(RoundedCornerShape(36.dp))
                                        .background(Color(0xFFE8F5E9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "پرداخت موفقیت‌آمیز",
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(48.dp)
                                    )
                                }

                                Text(
                                    text = "تراکنش با موفقیت ثبت شد! 🌱",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    ReceiptRow(label = "نوع خرید:", value = "ارتقای اشتراک آراما (${planItem.persianName})")
                                    ReceiptRow(label = "شناسه مرجع تراکنش (RefID):", value = "ZP-$refIdResult")
                                    ReceiptRow(label = "کد درگاه زرین‌پال:", value = "1b4e78bd-a0d6-4c15-9867-62f693dd8f60")
                                    ReceiptRow(label = "مبلغ کل پرداختی:", value = "${java.text.DecimalFormat("#,###").format(planItem.priceTomans)} تومان")
                                    ReceiptRow(label = "پروتکل ارتباطی:", value = "SSL Secure v3")
                                    ReceiptRow(label = "وضعیت پرداخت:", value = "موفق و فعال شده")
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        onPaymentSuccess("ZP-$refIdResult")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("zp_back_app_button")
                                ) {
                                    Text(
                                        text = "بازگشت به اپلیکیشن آراما",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ZarinpalGateHeader(onDismiss: () -> Unit, canDismiss: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF0B518)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Payment,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = "درگاه پرداخت امن زرین‌پال",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.DarkGray)
                )
                Text(
                    text = "Zarinpal Secure Payment Gate",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 9.sp)
                )
            }
        }

        if (canDismiss) {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "انصراف",
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
fun InvoiceDetailsCard(planItem: SubscriptionPlans) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "جزئیات فاکتور خرید",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E2E2E)
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("پذیرنده فروشگاه:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text("هوش مصنوعی آراما (Arama AI)", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("شناسه پذیرنده زرین‌پال:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text("1b4e78bd-a0d6-4c15-9867-62f693dd8f60", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.DarkGray)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("پلن ارتقا اشتراک:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(planItem.persianName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = SagePrimary))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("مبلغ تراکنش (تومان):", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text("${java.text.DecimalFormat("#,###").format(planItem.priceTomans)} تومان", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFFE53935)))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("مبلغ تراکنش (ریال):", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text("${java.text.DecimalFormat("#,###").format(planItem.priceTomans * 10)} ریال", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
            }
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.DarkGray)
    }
}
