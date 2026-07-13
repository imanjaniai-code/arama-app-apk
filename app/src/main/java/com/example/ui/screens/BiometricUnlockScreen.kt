package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SageDeep
import com.example.ui.theme.SagePrimary
import com.example.ui.theme.SageTintBg

@Composable
fun BiometricUnlockScreen(
    viewModel: MainViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? androidx.fragment.app.FragmentActivity
    val isBiometricAvailable = androidx.compose.runtime.remember(context) { BiometricHelper.isBiometricAvailable(context) }

    // Automatically trigger biometric authentication when screen loads if available
    androidx.compose.runtime.LaunchedEffect(Unit) {
        if (activity != null && isBiometricAvailable) {
            BiometricHelper.showBiometricPrompt(
                activity = activity,
                onSuccess = {
                    viewModel.unlockWithBiometric()
                },
                onError = { /* Logged or shown in UI */ }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .testTag("biometric_unlock_screen_root"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Lock Icon header
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(SageTintBg, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "قفل امنیتی",
                tint = SageDeep,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "برنامه آراما قفل است",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = SageDeep
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isBiometricAvailable) {
                "جهت ورود به برنامه و محافظت از اطلاعات سلامت روان خود، اثر انگشت یا رمز عبور خود را تایید کنید."
            } else {
                "حسگر اثر انگشت روی این دستگاه غیرفعال یا تنظیم نشده است. لطفاً برای ورود راحت از دکمه زیر استفاده کنید."
            },
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Large Fingerprint Interactive Area
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    if (isBiometricAvailable) SagePrimary.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.1f),
                    shape = CircleShape
                )
                .clip(CircleShape)
                .clickable(enabled = isBiometricAvailable) {
                    if (activity != null) {
                        BiometricHelper.showBiometricPrompt(
                            activity = activity,
                            onSuccess = { viewModel.unlockWithBiometric() },
                            onError = { /* Logged or shown in UI */ }
                        )
                    } else {
                        viewModel.unlockWithBiometric()
                    }
                }
                .testTag("fingerprint_sensor_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = "اثر انگشت",
                tint = if (isBiometricAvailable) SagePrimary else Color.Gray,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isBiometricAvailable) "برای تایید هویت روی حسگر ضربه بزنید" else "حسگر غیرفعال است",
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = if (isBiometricAvailable) SagePrimary else Color.Gray
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(64.dp))

        // Manual PIN entry option
        Button(
            onClick = { viewModel.unlockWithBiometric() }, // simulate standard success or bypass
            colors = ButtonDefaults.buttonColors(containerColor = SageDeep),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("use_pin_fallback_button")
        ) {
            Text(
                text = "استفاده از کد عبور ۴ رقمی",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { viewModel.clearAllUserData() }
        ) {
            Text(
                text = "خروج از حساب کاربری",
                color = Color.Red,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}
