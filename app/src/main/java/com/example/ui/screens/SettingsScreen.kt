package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Check
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.HorizontalDivider
import com.example.data.model.SubscriptionPlans
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrisisRed
import com.example.ui.theme.SageDeep
import com.example.ui.theme.SagePrimary
import com.example.ui.theme.SageTintBg

@Composable
fun SettingsScreen(
    viewModel: MainViewModel
) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val isLocalPrivacy by viewModel.isLocalOnlyPrivacy.collectAsState()
    val email by viewModel.userEmail.collectAsState()
    val isVerifyingPayment by viewModel.isPaymentVerifying.collectAsState()

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var reportCategory by remember { mutableStateOf("نیاز به کمک فوری (بحران استرس شدید)") }
    var reportContent by remember { mutableStateOf("") }
    var showReportSuccessDialog by remember { mutableStateOf(false) }
    var isSubmittingReport by remember { mutableStateOf(false) }

    // State variables for Subscription & Security UI Features
    var discountCodeText by remember { mutableStateOf("") }
    var discountMessage by remember { mutableStateOf<String?>(null) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var selectedUpgradePlan by remember { mutableStateOf("PREMIUM") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isInitiatingPayment by remember { mutableStateOf(false) }
    var initiationError by remember { mutableStateOf<String?>(null) }

    // State variables for Local Backups & Disasters Recovery UI
    var backupString by remember { mutableStateOf("") }
    var showBackupSuccess by remember { mutableStateOf<String?>(null) }
    var importStringInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("settings_screen_root"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "تنظیمات",
                tint = SagePrimary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "تنظیمات برنامه",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SageDeep
                ),
                textAlign = TextAlign.Center
            )
        }

        // Profile Section
        val userName by viewModel.userName.collectAsState()
        var isEditingName by remember { mutableStateOf(false) }
        var editedNameText by remember { mutableStateOf(userName) }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "کاربر",
                        tint = SagePrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        if (isEditingName) {
                            androidx.compose.material3.OutlinedTextField(
                                value = editedNameText,
                                onValueChange = { editedNameText = it },
                                placeholder = { Text("نام شما...") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("edit_name_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SagePrimary,
                                    focusedLabelColor = SagePrimary
                                )
                            )
                        } else {
                            Text(
                                text = if (userName.isNotEmpty()) userName else "نام ثبت نشده",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = email,
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "خروج از حساب",
                            tint = CrisisRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (isEditingName) {
                        TextButton(onClick = { isEditingName = false }) {
                            Text("انصراف", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (editedNameText.trim().isNotEmpty()) {
                                    viewModel.setUserName(editedNameText.trim())
                                    isEditingName = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SagePrimary)
                        ) {
                            Text("ذخیره تغییرات", color = Color.White)
                        }
                    } else {
                        TextButton(
                            onClick = {
                                editedNameText = userName
                                isEditingName = true
                            }
                        ) {
                            Text("ویرایش نام من", color = SagePrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Display Settings Block
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ظاهر برنامه",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SageDeep
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = "تم",
                            tint = SagePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "حالت تیره (Dark Mode)",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { viewModel.setDarkTheme(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = SagePrimary
                        ),
                        modifier = Modifier.testTag("dark_mode_switch")
                    )
                }
            }
        }



        // Privacy Block
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "حریم خصوصی با پیش‌فرض محافظه‌کارانه",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SageDeep
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "برنامه آراما طبق اصول اخلاقی خود، اطلاعات مکالمات و احوال روزانه را فقط در حافظه امن گوشی شما ذخیره می‌کند و هیچ داده‌ای بدون اجازه شما به فضای ابری فرستاده نخواهد شد.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Justify
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "ذخیره سازی محلی",
                            tint = SagePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "فقط ذخیره‌سازی محلی (آفلاین)",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "داده‌ها فقط روی گوشی بمانند.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                    Switch(
                        checked = isLocalPrivacy,
                        onCheckedChange = { viewModel.setLocalOnlyPrivacy(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = SagePrimary
                        ),
                        modifier = Modifier.testTag("privacy_local_switch")
                    )
                }
            }
        }

        // --- Architecture Upgrade Card: Subscriptions ---
        val subPlan by viewModel.subscriptionPlan.collectAsState()
        val subStatus by viewModel.subscriptionStatus.collectAsState()
        val subExpiry by viewModel.subscriptionExpiry.collectAsState()
        val chatLimitExceeded by viewModel.chatLimitExceeded.collectAsState()

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().testTag("subscription_card"),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "اشتراک",
                        tint = SagePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "مدیریت اشتراک و طرح کاربری",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDeep
                        ),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "پلن فعلی شما:", style = MaterialTheme.typography.bodyMedium)
                    Box(
                        modifier = Modifier
                            .background(
                                if (subPlan == "FREE") Color(0xFFF3F4F6) else SagePrimary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = when (subPlan) {
                                "FREE" -> "رایگان (Free)"
                                "MONTHLY" -> "ماهانه (Monthly) 🌟"
                                "YEARLY" -> "سالانه (Yearly) 🚀"
                                "PROFESSIONAL" -> "حرفه‌ای (Professional) 💼"
                                "PREMIUM" -> "طلایی (Premium) 🌟"
                                "CORPORATE" -> "سازمانی (Corporate) 🏢"
                                else -> subPlan
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (subPlan == "FREE") Color.DarkGray else SageDeep
                            )
                        )
                    }
                }

                if (subPlan == "FREE") {
                    Text(
                        text = "⚠️ شما در طرح محدود قرار دارید (حداکثر ۵ پیام در روز). برای باز کردن مکالمات نامحدود و مدل‌های تحلیلی، اشتراک خود را ارتقا دهید.",
                        style = MaterialTheme.typography.bodySmall.copy(color = CrisisRed, lineHeight = 16.sp),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    val expiryDate = java.text.SimpleDateFormat("yyyy/MM/dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date(subExpiry))
                    Text(
                        text = "✅ اشتراک شما فعال است. انقضا: $expiryDate",
                        style = MaterialTheme.typography.bodySmall.copy(color = SageDeep, fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (subPlan == "FREE") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        androidx.compose.material3.OutlinedTextField(
                            value = discountCodeText,
                            onValueChange = { discountCodeText = it },
                            placeholder = { Text("کد تخفیف (مثال: FREEPREM)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).height(54.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SagePrimary,
                                focusedLabelColor = SagePrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Button(
                            onClick = {
                                val discount = viewModel.applyDiscountCode(discountCodeText)
                                if (discount != null) {
                                    discountMessage = "کد با موفقیت اعمال شد!"
                                    if (discount == "FREE") {
                                        viewModel.upgradeSubscription("MONTHLY", "DISCOUNT-100")
                                    }
                                } else {
                                    discountMessage = "کد تخفیف معتبر نیست!"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(54.dp)
                        ) {
                            Text("اعمال")
                        }
                    }

                    if (discountMessage != null) {
                        Text(
                            text = discountMessage!!,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (discountMessage!!.contains("موفقیت")) SageDeep else CrisisRed,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Plan selection list containing the 4 detailed plans
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SubscriptionPlans.values().forEach { planItem ->
                        val isCurrent = subPlan == planItem.id
                        val isPaid = planItem.id != "FREE"

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrent) SagePrimary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            border = if (isCurrent) BorderStroke(1.5.dp, SagePrimary) else null,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("plan_card_${planItem.id.lowercase()}")
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = planItem.persianName,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SageDeep)
                                        )
                                        
                                        if (planItem.badge != null) {
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        color = if (planItem.id == "MONTHLY") SagePrimary else CrisisRed,
                                                        shape = RoundedCornerShape(6.dp)
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = planItem.badge,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White,
                                                        fontSize = 10.sp
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = if (planItem.priceTomans == 0L) "رایگان" else "${java.text.DecimalFormat("#,###").format(planItem.priceTomans)} تومان / ${planItem.period}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = SagePrimary)
                                    )
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), thickness = 0.5.dp)

                                // Features list with checkmarks
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    planItem.features.forEach { feature ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "ویژگی",
                                                tint = SagePrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = feature,
                                                style = MaterialTheme.typography.bodySmall,
                                                textAlign = TextAlign.Start
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Action button
                                if (isCurrent) {
                                    Button(
                                        onClick = {},
                                        enabled = false,
                                        colors = ButtonDefaults.buttonColors(
                                            disabledContainerColor = SagePrimary.copy(alpha = 0.15f),
                                            disabledContentColor = SageDeep
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth().height(40.dp)
                                    ) {
                                        Text("طرح فعال شما 🌱", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                    }
                                } else if (isPaid) {
                                    var isThisPlanInitiating by remember { mutableStateOf(false) }
                                    
                                    Button(
                                        onClick = {
                                            if (!isInitiatingPayment && !isVerifyingPayment) {
                                                coroutineScope.launch {
                                                    isInitiatingPayment = true
                                                    isThisPlanInitiating = true
                                                    initiationError = null
                                                    val result = com.example.data.api.AramaPaymentClient.requestPayment(
                                                        amount = planItem.priceTomans,
                                                        description = "خرید اشتراک ${planItem.persianName} آراما",
                                                        plan = planItem.id
                                                    )
                                                    isInitiatingPayment = false
                                                    isThisPlanInitiating = false
                                                    if (result.isSuccess) {
                                                        val payRes = result.getOrNull()
                                                        if (payRes != null) {
                                                            try {
                                                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(payRes.paymentUrl))
                                                                context.startActivity(browserIntent)
                                                            } catch (e: Exception) {
                                                                initiationError = "خطا در باز کردن درگاه پرداخت: ${e.localizedMessage}"
                                                            }
                                                        } else {
                                                            initiationError = "پاسخ نامعتبر از سرور پرداخت."
                                                        }
                                                    } else {
                                                        initiationError = result.exceptionOrNull()?.localizedMessage ?: "خطا در اتصال به درگاه پرداخت."
                                                    }
                                                }
                                            }
                                        },
                                        enabled = !isInitiatingPayment && !isVerifyingPayment,
                                        colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth().height(40.dp)
                                    ) {
                                        if (isThisPlanInitiating) {
                                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                        } else {
                                            Text("ارتقا به این طرح 🚀", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), thickness = 1.dp)

                // Arama AI Engine Selection
                val currentModelMode by viewModel.geminiModelMode.collectAsState()
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = "هوش مصنوعی آراما",
                            tint = SagePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "تنظیمات موتور هوش مصنوعی آراما",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDeep
                            ),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Start
                        )
                    }
                    Text(
                        text = "دقت و سرعت تحلیل هوش مصنوعی آراما را متناسب با اشتراک خود تنظیم کنید:",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    // Option 1: Fast
                    Card(
                        onClick = { viewModel.setGeminiModelMode("fast") },
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentModelMode == "fast") SagePrimary.copy(alpha = 0.08f) else Color.Transparent
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            if (currentModelMode == "fast") 2.dp else 1.dp,
                            if (currentModelMode == "fast") SagePrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "سرعت فوق‌العاده (موتور سبک آراما)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "پاسخ‌دهی آنی و بهینه برای مکالمات ساده و احوال‌پرسی (رایگان)",
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            androidx.compose.material3.RadioButton(
                                selected = currentModelMode == "fast",
                                onClick = { viewModel.setGeminiModelMode("fast") },
                                colors = androidx.compose.material3.RadioButtonDefaults.colors(selectedColor = SagePrimary)
                            )
                        }
                    }

                    // Option 2: General
                    Card(
                        onClick = { viewModel.setGeminiModelMode("general") },
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentModelMode == "general") SagePrimary.copy(alpha = 0.08f) else Color.Transparent
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            if (currentModelMode == "general") 2.dp else 1.dp,
                            if (currentModelMode == "general") SagePrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "عادی و متعادل (موتور استاندارد آراما)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "گفتگوی صمیمی، هوشمند و متعادل (توصیه شده - نیازمند اشتراک ماهانه و بالاتر)",
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            androidx.compose.material3.RadioButton(
                                selected = currentModelMode == "general",
                                onClick = { viewModel.setGeminiModelMode("general") },
                                colors = androidx.compose.material3.RadioButtonDefaults.colors(selectedColor = SagePrimary)
                            )
                        }
                    }

                    // Option 3: Complex
                    Card(
                        onClick = { viewModel.setGeminiModelMode("complex") },
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentModelMode == "complex") SagePrimary.copy(alpha = 0.08f) else Color.Transparent
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            if (currentModelMode == "complex") 2.dp else 1.dp,
                            if (currentModelMode == "complex") SagePrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "تفکر عمیق و تحلیلی (موتور فوق‌پیشرفته آراما)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "استدلال عمیق با تفکر بالا برای تحلیل‌های دقیق‌تر روان‌شناختی (نیازمند اشتراک سالانه یا حرفه‌ای)",
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            androidx.compose.material3.RadioButton(
                                selected = currentModelMode == "complex",
                                onClick = { viewModel.setGeminiModelMode("complex") },
                                colors = androidx.compose.material3.RadioButtonDefaults.colors(selectedColor = SagePrimary)
                            )
                        }
                    }
                }
            }
        }

        // --- Security, RBAC & Monitoring Card ---
        val userRole by viewModel.userRole.collectAsState()
        val isConsentGiven by viewModel.isConsentGiven.collectAsState()
        val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState()
        val securityLogs by viewModel.allSecurityLogs.collectAsState()
        val queryPerformanceMs by viewModel.queryPerformanceMs.collectAsState()
        val dbSizeOnDisk by viewModel.databaseSizeOnDisk.collectAsState()

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().testTag("security_monitoring_card"),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "امنیت و مانیتورینگ",
                        tint = SagePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "امنیت پیشرفته و پایش سیستم",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDeep
                        ),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )
                }

                // Monitoring Panel
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF9FAFB), shape = RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "📊 داشبورد سلامت سیستم و مانیتورینگ داده:",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = SageDeep),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "زمان پاسخ کوئری محلی دیتابیس:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text(text = "$queryPerformanceMs میلی‌ثانیه", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = SageDeep)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "حجم فیزیکی پایگاه داده روی دیسک:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text(text = dbSizeOnDisk, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = SageDeep)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "سطح دسترسی شما (RBAC):", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text(
                                text = when (userRole) {
                                    "admin" -> "مدیر سیستم (Admin)"
                                    "support" -> "کارشناس پشتیبانی (Support)"
                                    else -> "کاربر عادی (Regular User)"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = SagePrimary
                            )
                        }
                    }
                }

                // Consent toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "موافقت با ثبت گزارش‌های امنیتی",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "برای مانیتورینگ امنیتی محلی.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Switch(
                        checked = isConsentGiven,
                        onCheckedChange = { viewModel.setConsentGiven(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SagePrimary)
                    )
                }

                // Biometrics toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "قفل امنیتی اثر انگشت / زیست‌سنجی",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "درخواست تایید هویت در هنگام شروع برنامه.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Switch(
                        checked = isBiometricEnabled,
                        onCheckedChange = { viewModel.setBiometricEnabled(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SagePrimary)
                    )
                }

                // Log display list
                Text(
                    text = "🛡️ گزارش رخدادهای امنیتی (Audit Trails):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = SageDeep),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
                if (securityLogs.isEmpty()) {
                    Text(
                        text = "هیچ رخدادی ثبت نشده است.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .background(Color(0xFFF3F4F6), shape = RoundedCornerShape(8.dp))
                            .padding(8.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        securityLogs.take(25).forEach { log ->
                            val timeStr = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(log.timestamp))
                            Text(
                                text = "[$timeStr] ${log.eventType}: ${log.details}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = if (log.eventType.contains("FAILURE") || log.eventType.contains("LIMIT")) CrisisRed else Color.Black,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        // --- Backup and Disaster Recovery Card ---

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().testTag("backup_recovery_card"),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = "پشتیبان‌گیری",
                        tint = SagePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "پشتیبان‌گیری محلی و بازیابی داده‌ها (Disaster Recovery)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDeep
                        ),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )
                }

                Text(
                    text = "از آنجا که اطلاعات شما به صورت ابری ذخیره نمی‌شود، می‌توانید یک نسخه پشتیبان متنی ایجاد کرده و ذخیره نمایید تا بعدا مجدد بازیابی کنید.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            backupString = viewModel.exportBackup()
                            showBackupSuccess = "کد پشتیبان با موفقیت ساخته شد!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Text("ساخت فایل پشتیبان (Export)")
                    }
                }

                if (backupString.isNotEmpty()) {
                    androidx.compose.material3.OutlinedTextField(
                        value = backupString,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SagePrimary,
                            focusedLabelColor = SagePrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "بازیابی نسخه پشتیبان:",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                androidx.compose.material3.OutlinedTextField(
                    value = importStringInput,
                    onValueChange = { importStringInput = it },
                    placeholder = { Text("کد پشتیبان کپی شده را اینجا پیست کنید...") },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SagePrimary,
                        focusedLabelColor = SagePrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Button(
                    onClick = {
                        val success = viewModel.importBackup(importStringInput)
                        showBackupSuccess = if (success) "اطلاعات با موفقیت بازیابی شد!" else "کد پشتیبان نامعتبر است!"
                        if (success) {
                            importStringInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SageDeep),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Text("بازیابی نسخه پشتیبان (Import)")
                }

                if (showBackupSuccess != null) {
                    Text(
                        text = showBackupSuccess!!,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (showBackupSuccess!!.contains("موفقیت")) SageDeep else CrisisRed,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Support and Report Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().testTag("support_card"),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "پشتیبانی",
                        tint = SagePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "پشتیبانی و گزارش مشکلات داخلاپ",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDeep
                        ),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )
                }

                Text(
                    text = "در صورت داشتن هرگونه مشکل فنی، سوال، انتقاد یا نیاز مبرم به ارتباط مستقیم با پشتیبانی آراما، پیام خود را در زیر ثبت کنید.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category selector buttons
                Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "دسته‌بندی گزارش:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                    val categories = listOf(
                        "نیاز به کمک فوری (بحران استرس شدید)",
                        "گزارش مشکل فنی در برنامه",
                        "پیشنهاد یا بازخورد برای بهبود"
                    )
                    categories.forEach { cat ->
                        val isSelected = reportCategory == cat
                        Card(
                            onClick = { reportCategory = cat },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) SagePrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, SagePrimary) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isSelected) "● $cat" else "○ $cat",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isSelected) SageDeep else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }

                androidx.compose.material3.OutlinedTextField(
                    value = reportContent,
                    onValueChange = { reportContent = it },
                    placeholder = { Text("توضیحات خود را اینجا بنویسید...") },
                    modifier = Modifier.fillMaxWidth().height(100.dp).testTag("support_input"),
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SagePrimary,
                        focusedLabelColor = SagePrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = {
                        if (reportContent.trim().isNotEmpty()) {
                            isSubmittingReport = true
                            viewModel.sendSupportReport(reportCategory, reportContent) {
                                isSubmittingReport = false
                                showReportSuccessDialog = true
                                reportContent = ""
                            }
                        }
                    },
                    enabled = reportContent.trim().isNotEmpty() && !isSubmittingReport,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SagePrimary,
                        disabledContainerColor = SagePrimary.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("submit_report_button")
                ) {
                    Text(
                        text = if (isSubmittingReport) "در حال ارسال..." else "ارسال گزارش به پشتیبانی",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                }
            }
        }

        // Danger Zone
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFEF2F2),
                contentColor = Color(0xFF991B1B)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ناحیه حساس کاربری",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF991B1B)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "با پاک کردن داده‌ها، تمامی تاریخچه پیام‌ها با هوش مصنوعی و سوابق ثبت‌شده از نوسانات حال روحی به طور دائم از روی گوشی شما حذف شده و غیر قابل بازیابی خواهند بود.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        lineHeight = 18.sp,
                        color = Color(0xFF7F1D1D)
                    ),
                    textAlign = TextAlign.Justify
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showDeleteConfirmDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CrisisRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("wipe_data_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "پاک کردن",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "حذف کامل داده‌ها و ریست برنامه",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }

        // Footer / Branding
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Spa,
                contentDescription = "برندینگ",
                tint = SagePrimary.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "آراما — نسخه آزمایشی تلفن همراه (v1.0.0)",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                textAlign = TextAlign.Center
            )
            Text(
                text = "طراحی شده با عشق به صلح و تندرستی روان 🌱",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant),
                textAlign = TextAlign.Center
            )
        }
    }

    // Deletion confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text(
                    text = "آیا کاملاً مطمئن هستید؟",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "این عمل غیر قابل بازگشت است. تمام پیام‌ها و اطلاعات حال روحی شما برای همیشه پاک خواهند شد و برنامه به حالت اول بازمی‌گردد.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.clearAllUserData()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrisisRed)
                ) {
                    Text("بله، کاملا پاک کن", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmDialog = false }
                ) {
                    Text("انصراف", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.testTag("wipe_confirm_dialog")
        )
    }

    if (showReportSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showReportSuccessDialog = false },
            title = {
                Text(
                    text = "گزارش ثبت شد 🌱",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "پیام شما با موفقیت ثبت شد. در صورت نیاز به پیگیری فوری، کارشناسان مربیگری و سلامت روان آراما از طریق آدرس ایمیل شما ارتباط خواهند گرفت.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = { showReportSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SagePrimary)
                ) {
                    Text("متوجه شدم", color = Color.White)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // --- Simulated ZarinPal Checkout Gateway ---
    if (showPaymentDialog) {
        AlertDialog(
            onDismissRequest = { showPaymentDialog = false },
            title = {
                Text(
                    text = "درگاه پرداخت امن آراما (شبیه‌ساز زرین‌پال)",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = SageDeep),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "شما در حال ارتقای حساب خود به پلن طلایی (Premium) هستید.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "مبلغ قابل پرداخت: ۴۹,۰۰۰ تومان",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "شماره کارت شبیه‌سازی شده: ۶۰۳۷-۹۹۷۵-۱۲۳۴-۵۶۷۸",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "سرویس‌دهنده واسط: شرکت پرداخت الکترونیک زرین‌پال",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPaymentDialog = false
                        viewModel.upgradeSubscription(selectedUpgradePlan)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SagePrimary)
                ) {
                    Text("پرداخت موفقیت‌آمیز (شبیه‌سازی)", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPaymentDialog = false
                        viewModel.logSecurityEvent("PAYMENT_CANCELLED", "تراکنش خرید اشتراک طلایی لغو شد.")
                    }
                ) {
                    Text("انصراف و بازگشت", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    val paymentSuccessMsg by viewModel.paymentSuccessMessage.collectAsState()
    val paymentErrorMsg by viewModel.paymentErrorMessage.collectAsState()

    // 1. Loading Dialog for verifying payment
    if (isVerifyingPayment) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = "تأیید تراکنش پرداخت",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = SageDeep),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    CircularProgressIndicator(color = SagePrimary)
                    Text(
                        text = "در حال تأیید تراکنش از درگاه پی‌پینگ. لطفاً شکیبا باشید...",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start
                    )
                }
            },
            confirmButton = {},
            shape = RoundedCornerShape(20.dp)
        )
    }

    // 2. Success Dialog
    if (paymentSuccessMsg != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearPaymentMessages() },
            title = {
                Text(
                    text = "ارتقای اشتراک موفقیت‌آمیز 🌱",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = SageDeep),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = paymentSuccessMsg!!,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearPaymentMessages() },
                    colors = ButtonDefaults.buttonColors(containerColor = SagePrimary)
                ) {
                    Text("متوجه شدم", color = Color.White)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // 3. Error Dialog
    if (paymentErrorMsg != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearPaymentMessages() },
            title = {
                Text(
                    text = "خطا در فرآیند پرداخت",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = CrisisRed),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = paymentErrorMsg!!,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearPaymentMessages() },
                    colors = ButtonDefaults.buttonColors(containerColor = CrisisRed)
                ) {
                    Text("بستن", color = Color.White)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // 4. Initiation Error Dialog
    if (initiationError != null) {
        AlertDialog(
            onDismissRequest = { initiationError = null },
            title = {
                Text(
                    text = "خطای اتصال به درگاه",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = CrisisRed),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = initiationError!!,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = { initiationError = null },
                    colors = ButtonDefaults.buttonColors(containerColor = CrisisRed)
                ) {
                    Text("بستن", color = Color.White)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}
