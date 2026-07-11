package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spa
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrisisRed
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MintGreen

@Composable
fun SettingsScreen(
    viewModel: MainViewModel
) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val isLocalPrivacy by viewModel.isLocalOnlyPrivacy.collectAsState()
    val email by viewModel.userEmail.collectAsState()

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

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
                tint = EmeraldPrimary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "تنظیمات برنامه",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDark
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
                        tint = EmeraldPrimary,
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
                                    focusedBorderColor = EmeraldPrimary,
                                    focusedLabelColor = EmeraldPrimary
                                )
                            )
                        } else {
                            Text(
                                text = if (userName.isNotEmpty()) userName else "نام ثبت نشده",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = email,
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                textAlign = TextAlign.Right,
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
                            imageVector = Icons.Default.Logout,
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
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
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
                            Text("ویرایش نام من", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
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
                        color = EmeraldDark
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
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
                            tint = EmeraldPrimary,
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
                            checkedTrackColor = EmeraldPrimary
                        ),
                        modifier = Modifier.testTag("dark_mode_switch")
                    )
                }
            }
        }

        // Gemini Model Selection Block
        val currentModelMode by viewModel.geminiModelMode.collectAsState()
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
                        imageVector = Icons.Default.Spa,
                        contentDescription = "هوش مصنوعی",
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "تنظیمات هوش مصنوعی آراما",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        ),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Right
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "مدل جمینای مورد استفاده برای پاسخ‌دهی را انتخاب کنید:",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Option 1: Fast (Low-Latency)
                Card(
                    onClick = { viewModel.setGeminiModelMode("fast") },
                    colors = CardDefaults.cardColors(
                        containerColor = if (currentModelMode == "fast") EmeraldPrimary.copy(alpha = 0.12f) else Color.Transparent
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (currentModelMode == "fast") EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "سرعت فوق‌العاده (Lite)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "پاسخ‌دهی آنی با مدل gemini-3.1-flash-lite",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        androidx.compose.material3.RadioButton(
                            selected = currentModelMode == "fast",
                            onClick = { viewModel.setGeminiModelMode("fast") },
                            colors = androidx.compose.material3.RadioButtonDefaults.colors(selectedColor = EmeraldPrimary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Option 2: General (Standard)
                Card(
                    onClick = { viewModel.setGeminiModelMode("general") },
                    colors = CardDefaults.cardColors(
                        containerColor = if (currentModelMode == "general") EmeraldPrimary.copy(alpha = 0.12f) else Color.Transparent
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (currentModelMode == "general") EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "عادی و متعادل (Standard)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "گفتگوی متعادل و روان با مدل gemini-3.5-flash",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        androidx.compose.material3.RadioButton(
                            selected = currentModelMode == "general",
                            onClick = { viewModel.setGeminiModelMode("general") },
                            colors = androidx.compose.material3.RadioButtonDefaults.colors(selectedColor = EmeraldPrimary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Option 3: Complex (High-Thinking)
                Card(
                    onClick = { viewModel.setGeminiModelMode("complex") },
                    colors = CardDefaults.cardColors(
                        containerColor = if (currentModelMode == "complex") EmeraldPrimary.copy(alpha = 0.12f) else Color.Transparent
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (currentModelMode == "complex") EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "تفکر عمیق و تحلیلی (High Thinking)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "استدلال عمیق با مدل gemini-3.1-pro-preview و تفکر بالا",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        androidx.compose.material3.RadioButton(
                            selected = currentModelMode == "complex",
                            onClick = { viewModel.setGeminiModelMode("complex") },
                            colors = androidx.compose.material3.RadioButtonDefaults.colors(selectedColor = EmeraldPrimary)
                        )
                    }
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
                        color = EmeraldDark
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
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
                            tint = EmeraldPrimary,
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
                            checkedTrackColor = EmeraldPrimary
                        ),
                        modifier = Modifier.testTag("privacy_local_switch")
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
                    textAlign = TextAlign.Right
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
                tint = EmeraldPrimary.copy(alpha = 0.5f),
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
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "این عمل غیر قابل بازگشت است. تمام پیام‌ها و اطلاعات حال روحی شما برای همیشه پاک خواهند شد و برنامه به حالت اول بازمی‌گردد.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Right,
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
}
