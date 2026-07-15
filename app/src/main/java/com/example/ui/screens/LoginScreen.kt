package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.AlertDialog
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SageDeep
import com.example.ui.theme.SagePrimary
import com.example.ui.theme.SageTintBg

@Composable
fun LoginScreen(
    viewModel: MainViewModel
) {
    val phone by viewModel.loginPhone.collectAsState()
    val otp by viewModel.loginOtp.collectAsState()
    val loginStep by viewModel.loginStep.collectAsState()
    val countdown by viewModel.otpCountdown.collectAsState()
    val generatedOtp by viewModel.generatedOtp.collectAsState()
    val loginError by viewModel.loginError.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    // Local profile setup states
    var nameInput by remember { mutableStateOf("") }
    var pinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var isPinVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    var showMeliConfigDialog by remember { mutableStateOf(false) }
    
    val savedUser by viewModel.customMelipayamakUsername.collectAsState()
    val savedPass by viewModel.customMelipayamakPassword.collectAsState()
    val savedFrom by viewModel.customMelipayamakFrom.collectAsState()
    val testSmsResult by viewModel.testSmsResult.collectAsState()

    var inputUser by remember(savedUser) { mutableStateOf(savedUser) }
    var inputPass by remember(savedPass) { mutableStateOf(savedPass) }
    var inputFrom by remember(savedFrom) { mutableStateOf(savedFrom) }
    var testPhoneInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
            .testTag("login_root"),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo Lockup (Elegant, aligned beautifully)
        Spacer(modifier = Modifier.height(28.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Spa,
                contentDescription = "آیکون آراما",
                tint = SagePrimary,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "آراما",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 38.sp,
                    color = SageDeep,
                    fontWeight = FontWeight.Black
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = "arama",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    color = SagePrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Animated transitions between Phone, OTP, and Profile Setup states
        AnimatedContent(
            targetState = loginStep,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "LoginStates"
        ) { step ->
            when (step) {
                LoginStep.PHONE_INPUT -> {
                    // State 1: Enter Phone Number
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ورود یا ثبت‌نام",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDeep,
                                fontSize = 22.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "برای شروع گفتگو و شخصی‌سازی تمرینات آرامش، لطفا شماره موبایل خود را وارد کنید تا کد فعال‌سازی برایتان ارسال شود.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 24.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { viewModel.setLoginPhone(it) },
                            label = { Text("شماره موبایل") },
                            placeholder = { Text("مثال: ۰۹۱۲۳۴۵۶۷۸۹") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.PhoneAndroid,
                                    contentDescription = "شماره موبایل",
                                    tint = SagePrimary
                                )
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("phone_input"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { viewModel.sendOtpCode() }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SagePrimary,
                                focusedLabelColor = SagePrimary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )

                        loginError?.let { err ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = err,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_error_text")
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { viewModel.sendOtpCode() },
                            enabled = !isTyping,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SagePrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("submit_phone_button")
                        ) {
                            if (isTyping) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    text = "ارسال کد تأیید",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = { viewModel.signInWithGoogle(context) },
                            enabled = !isTyping,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("google_signin_button"),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = SagePrimary
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SagePrimary)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "ورود با گوگل",
                                    tint = SagePrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "ورود با حساب گوگل (Google)",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(
                            onClick = { viewModel.startBypassOtpFlow() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("quick_bypass_button")
                        ) {
                            Text(
                                text = "ورود سریع آزمایشی (بدون نیاز به پیامک) 🔑",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = SagePrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = { 
                                viewModel.clearTestSmsResult()
                                showMeliConfigDialog = true 
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("meli_config_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "تنظیمات فرستنده پیامک",
                                    tint = SagePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "تنظیمات پنل ملی‌پیامک شما 🛠️",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = SagePrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
                LoginStep.OTP_INPUT -> {
                    // State 2: Enter Verification Code (OTP)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "تأیید شماره موبایل",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDeep,
                                fontSize = 22.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Display current phone with an elegant edit action
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .background(SageTintBg, shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "کد تأیید برای ${phone.toPersianDigits()} ارسال شد",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = SageDeep,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { viewModel.cancelOtpFlow() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "ویرایش شماره",
                                    tint = SagePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = otp,
                            onValueChange = { if (it.length <= 5) viewModel.setLoginOtp(it) },
                            label = { Text("کد تأیید ۵ رقمی") },
                            placeholder = { Text("مثال: ۱۲۳۴۵") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "کد تأیید",
                                    tint = SagePrimary
                                )
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("otp_input"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { viewModel.verifyOtpCode() }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SagePrimary,
                                focusedLabelColor = SagePrimary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )



                        loginError?.let { err ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = err,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_error_text")
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Countdown text & Resend Option
                        if (countdown > 0) {
                            val minutes = countdown / 60
                            val seconds = countdown % 60
                            val timeStr = String.format("%02d:%02d", minutes, seconds).toPersianDigits()
                            Text(
                                text = "ارسال مجدد کد پس از $timeStr ثانیه",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        } else {
                            TextButton(
                                onClick = { viewModel.sendOtpCode() },
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text(
                                    text = "ارسال مجدد کد تأیید",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = SagePrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.cancelOtpFlow() },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .testTag("back_button"),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = SagePrimary
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SagePrimary)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "بازگشت",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "بازگشت",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            Button(
                                onClick = { viewModel.verifyOtpCode() },
                                enabled = !isTyping,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SagePrimary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(56.dp)
                                    .testTag("submit_otp_button")
                            ) {
                                if (isTyping) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Text(
                                        text = "تأیید و ورود",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                LoginStep.PROFILE_SETUP -> {
                    // State 3: Choose Name and Password/PIN (تکمیل پروفایل)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "تنظیم مشخصات کاربری",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDeep,
                                fontSize = 22.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "نام و رمز عبور (پین یا پسورد دلخواه) خود را برای فعال‌سازی ایمن و اختصاصی حساب آراما تعیین فرمایید.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 24.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Name input field
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it; localError = null },
                            label = { Text("نام و نام‌خانوادگی") },
                            placeholder = { Text("مثال: ایمان جانی") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "نام",
                                    tint = SagePrimary
                                )
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("name_setup_input"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SagePrimary,
                                focusedLabelColor = SagePrimary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password / PIN input field
                        OutlinedTextField(
                            value = pinInput,
                            onValueChange = { pinInput = it; localError = null },
                            label = { Text("رمز عبور (حداقل ۴ کاراکتر)") },
                            placeholder = { Text("مثال: ۱۲۳۴") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "رمز عبور",
                                    tint = SagePrimary
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPinVisible = !isPinVisible }) {
                                    Icon(
                                        imageVector = if (isPinVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "نمایش رمز عبور",
                                        tint = SagePrimary
                                    )
                                }
                            },
                            visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pin_setup_input"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SagePrimary,
                                focusedLabelColor = SagePrimary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Confirm Password / PIN input field
                        OutlinedTextField(
                            value = confirmPinInput,
                            onValueChange = { confirmPinInput = it; localError = null },
                            label = { Text("تکرار رمز عبور") },
                            placeholder = { Text("رمز را دوباره وارد کنید") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "تأیید رمز عبور",
                                    tint = SagePrimary
                                )
                            },
                            visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("confirm_pin_setup_input"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (nameInput.trim().isEmpty()) {
                                        localError = "لطفاً نام و نام‌خانوادگی خود را وارد کنید."
                                    } else if (pinInput.length < 4) {
                                        localError = "رمز عبور باید حداقل ۴ رقم/کاراکتر باشد."
                                    } else if (pinInput != confirmPinInput) {
                                        localError = "تکرار رمز عبور مطابقت ندارد."
                                    } else {
                                        viewModel.registerAndLogin(nameInput, pinInput)
                                    }
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SagePrimary,
                                focusedLabelColor = SagePrimary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )

                        // Error messages
                        val activeError = localError ?: loginError
                        if (activeError != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = activeError,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_error_text")
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.cancelOtpFlow() },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .testTag("profile_setup_back_button"),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = SagePrimary
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SagePrimary)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "انصراف",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "انصراف",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    if (nameInput.trim().isEmpty()) {
                                        localError = "لطفاً نام و نام‌خانوادگی خود را وارد کنید."
                                    } else if (pinInput.length < 4) {
                                        localError = "رمز عبور باید حداقل ۴ رقم/کاراکتر باشد."
                                    } else if (pinInput != confirmPinInput) {
                                        localError = "تکرار رمز عبور مطابقت ندارد."
                                    } else {
                                        viewModel.registerAndLogin(nameInput, pinInput)
                                    }
                                },
                                enabled = !isTyping,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SagePrimary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(56.dp)
                                    .testTag("submit_profile_setup_button")
                            ) {
                                if (isTyping) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Text(
                                        text = "تکمیل و ورود",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
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

    if (showMeliConfigDialog) {
        var isTestPhoneTyping by remember { mutableStateOf(false) }
        var isDialogPinVisible by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showMeliConfigDialog = false },
            title = {
                Text(
                    text = "تنظیمات اختصاصی پنل ملی‌پیامک 🛠️",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "جهت ارسال واقعی کد تأیید، می‌توانید مشخصات پنل ملی‌پیامک خود را در زیر وارد کنید. در صورت خالی گذاشتن، از پنل پیش‌فرض استفاده می‌شود.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = inputUser,
                        onValueChange = { inputUser = it },
                        label = { Text("نام کاربری ملی‌پیامک") },
                        placeholder = { Text("شماره تماس یا نام کاربری") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SagePrimary,
                            focusedLabelColor = SagePrimary
                        )
                    )

                    OutlinedTextField(
                        value = inputPass,
                        onValueChange = { inputPass = it },
                        label = { Text("کلمه عبور ملی‌پیامک") },
                        placeholder = { Text("کلمه عبور پنل") },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { isDialogPinVisible = !isDialogPinVisible }) {
                                Icon(
                                    imageVector = if (isDialogPinVisible) androidx.compose.material.icons.Icons.Default.VisibilityOff else androidx.compose.material.icons.Icons.Default.Visibility,
                                    contentDescription = "نمایش رمز"
                                )
                            }
                        },
                        visualTransformation = if (isDialogPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SagePrimary,
                            focusedLabelColor = SagePrimary
                        )
                    )

                    OutlinedTextField(
                        value = inputFrom,
                        onValueChange = { inputFrom = it },
                        label = { Text("شماره فرستنده (Sender Line)") },
                        placeholder = { Text("مثال: 5000400196 یا خط اختصاصی") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SagePrimary,
                            focusedLabelColor = SagePrimary
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.updateMelipayamakConfig(inputUser, inputPass, inputFrom)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SagePrimary)
                        ) {
                            Text("ذخیره تنظیمات", color = Color.White)
                        }

                        OutlinedButton(
                            onClick = {
                                inputUser = ""
                                inputPass = ""
                                inputFrom = ""
                                viewModel.updateMelipayamakConfig("", "", "")
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SagePrimary),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SagePrimary)
                        ) {
                            Text("ریست به پیش‌فرض")
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        text = "🧪 تست ارسال پیامک (اختیاری):",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SageDeep),
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = testPhoneInput,
                        onValueChange = { testPhoneInput = it },
                        label = { Text("شماره موبایل جهت تست") },
                        placeholder = { Text("مثال: 09123456789") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SagePrimary,
                            focusedLabelColor = SagePrimary
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                isTestPhoneTyping = true
                                viewModel.sendTestSms(testPhoneInput, inputUser, inputPass, inputFrom)
                            },
                            enabled = testPhoneInput.isNotEmpty(),
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SagePrimary)
                        ) {
                            Text("ارسال پیامک تست 🚀", color = Color.White)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.runDiagnosticConnectionTest(inputUser, inputPass, inputFrom)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SagePrimary),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SagePrimary)
                        ) {
                            Text("تست عیب‌یابی اتصال 🔍")
                        }
                    }

                    testSmsResult?.let { result ->
                        val isReport = result.startsWith("--- گزارش")
                        val isSuccess = result.startsWith("موفقیت") || result.contains("تبریک!")
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 240.dp)
                                .background(
                                    color = if (isSuccess) SagePrimary.copy(alpha = 0.1f) else Color(0xFFFFEBEE),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp)
                        ) {
                            if (isReport) {
                                androidx.compose.foundation.lazy.LazyColumn(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    item {
                                        Text(
                                            text = result,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = if (result.contains("❌") || result.contains("⚠️")) Color(0xFFC62828) else SageDeep,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            textAlign = TextAlign.Right,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = result,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isSuccess) SageDeep else Color(0xFFC62828),
                                        fontWeight = FontWeight.Bold
                                    ),
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMeliConfigDialog = false }) {
                    Text("بستن و اعمال نهایی", color = SagePrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}


// Utility extension function to translate English digits to beautiful Persian digits
fun String.toPersianDigits(): String {
    var result = this
    val persianDigits = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
    for (i in 0..9) {
        result = result.replace(i.toString(), persianDigits[i])
    }
    return result
}
