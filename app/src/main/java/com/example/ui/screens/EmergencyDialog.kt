package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalPhone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CrisisRed
import com.example.ui.theme.CrisisRedBg

@Composable
fun EmergencyDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .testTag("emergency_dialog_surface"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_emergency_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن دیالوگ کمک فوری",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "کمک فوری و اضطراری",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = CrisisRed,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "هشدار بحران",
                            tint = CrisisRed,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Alert intro text
                Text(
                    text = "کاربر گرامی آراما، اگر شما یا اطرافیانتان با افکار آسیب به خود، بحران روحی شدید یا فوریت‌های پزشکی مواجه هستید، لطفاً فوراً با یکی از خطوط امدادی زیر تماس بگیرید. این خدمات رایگان، شبانه‌روزی و محرمانه هستند.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Emergency Numbers
                EmergencyCardItem(
                    title = "صدای مشاور بهزیستی",
                    number = "1480",
                    description = "مشاوره تلفنی تخصصی، رایگان و کاملاً محرمانه در زمینه‌های روان‌شناختی و بحران‌های فردی. (ساعت ۸ الی ۲۴)",
                    context = context
                )

                Spacer(modifier = Modifier.height(12.dp))

                EmergencyCardItem(
                    title = "اورژانس پیش‌بیمارستانی",
                    number = "115",
                    description = "فوریت‌های پزشکی و خدمات درمانی اضطراری در شرایطی که خطر جانی وجود دارد.",
                    context = context
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("dismiss_emergency"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "متوجه شدم و بازگشت به برنامه",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun EmergencyCardItem(
    title: String,
    number: String,
    description: String,
    context: Context
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = CrisisRedBg,
            contentColor = Color(0xFF7F1D1D)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                    textAlign = TextAlign.Justify
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(CrisisRed)
                    .clickable {
                        try {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast
                                .makeText(context, "امکان شماره‌گیری خودکار یافت نشد.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .testTag("call_button_$number")
            ) {
                Icon(
                    imageVector = Icons.Default.LocalPhone,
                    contentDescription = "تماس با $number",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = number,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}
