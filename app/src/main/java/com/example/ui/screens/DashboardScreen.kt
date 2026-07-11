package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.MoodEntity
import com.example.ui.components.WeeklyMoodChart
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MintGreen
import com.example.ui.theme.SoftGrey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: MainViewModel
) {
    val moods by viewModel.allMoods.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("dashboard_screen_root"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Title & Header
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.InsertChart,
                    contentDescription = "داشبورد روند",
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "روند احوال روحی شما",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "بررسی نوسانات خلقی به صورت کاملاً خصوصی و امن در حافظه محلی تلفن شما.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center
                )

                // If they have data, show a quick re-generate sample button for evaluation
                if (moods.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(
                        onClick = { viewModel.generateSampleWeeklyData() },
                        modifier = Modifier.testTag("regenerate_demo_data_button")
                    ) {
                        Text(
                            text = "🔄 بازسازی نمونه ۷ روز گذشته (برای ارزیابی)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        )
                    }
                }
            }
        }

        // Anti-Anxiety Deep Breathing Exercise Card
        item {
            var isExpanded by remember { mutableStateOf(false) }
            var isBreathingActive by remember { mutableStateOf(false) }
            var breathPhase by remember { mutableStateOf("idle") } // "idle", "in", "hold", "out", "completed"
            var phaseSecondsLeft by remember { mutableStateOf(4) }
            var completedCycles by remember { mutableStateOf(0) }
            val maxCycles = 5

            // Manage the phase transition
            LaunchedEffect(isBreathingActive, breathPhase) {
                if (isBreathingActive) {
                    if (breathPhase == "idle") {
                        breathPhase = "in"
                        phaseSecondsLeft = 4
                    }
                    while (isBreathingActive && breathPhase != "completed") {
                        kotlinx.coroutines.delay(1000)
                        phaseSecondsLeft -= 1
                        if (phaseSecondsLeft <= 0) {
                            when (breathPhase) {
                                "in" -> {
                                    breathPhase = "hold"
                                    phaseSecondsLeft = 4
                                }
                                "hold" -> {
                                    breathPhase = "out"
                                    phaseSecondsLeft = 4
                                }
                                "out" -> {
                                    completedCycles += 1
                                    if (completedCycles >= maxCycles) {
                                        breathPhase = "completed"
                                        isBreathingActive = false
                                    } else {
                                        breathPhase = "in"
                                        phaseSecondsLeft = 4
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (breathPhase != "completed") {
                        breathPhase = "idle"
                        completedCycles = 0
                        phaseSecondsLeft = 4
                    }
                }
            }

            // Circle animation based on breath phase
            val circleScale by animateFloatAsState(
                targetValue = if (breathPhase == "in" || breathPhase == "hold") 1.0f else 0.5f,
                animationSpec = tween(durationMillis = if (breathPhase == "hold") 0 else 4000, easing = LinearOutSlowInEasing),
                label = "breath_scale"
            )

            // Pulse glow effect when holding breath
            val infiniteTransition = rememberInfiniteTransition(label = "glow_transition")
            val glowAlpha by if (breathPhase == "hold") {
                infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 0.8f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "glow_alpha"
                )
            } else {
                remember { mutableStateOf(0.3f) }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("breathing_practice_card"),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = !isExpanded }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MintGreen, shape = androidx.compose.foundation.shape.CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Spa,
                                    contentDescription = "تنفس عمیق",
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "تمرین تنفس عمیق تعاملی 🧘",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldDark
                                    ),
                                    textAlign = TextAlign.Right
                                )
                                Text(
                                    text = "کاهش آنی اضطراب و استرس به روش تنفس ۴-۴-۴",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                    textAlign = TextAlign.Right
                                )
                            }
                        }
                        Text(
                            text = if (isExpanded) "بستن ▲" else "شروع تمرین ▼",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(20.dp))

                        if (breathPhase == "completed") {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "✨ تمرین با موفقیت انجام شد ✨",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary
                                    ),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "عالی بود! شما ۵ چرخه تنفس عمیق را به پایان رساندید. این کار به آرامش سیستم عصبی و کاهش فوری ضربان قلب کمک می‌کند. هم‌اکنون چه احساسی دارید؟",
                                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        breathPhase = "idle"
                                        completedCycles = 0
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth(0.6f)
                                ) {
                                    Text("تکرار مجدد تمرین")
                                }
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val statusText = when (breathPhase) {
                                    "in" -> "دم عمیق (از بینی)..."
                                    "hold" -> "حبس نفس..."
                                    "out" -> "بازدم آرام (از دهان)..."
                                    else -> "آماده شروع تمرین هستید؟"
                                }
                                val explanationText = when (breathPhase) {
                                    "in" -> "شکم خود را پر از هوا کنید"
                                    "hold" -> "آرامش را در بدن خود احساس کنید"
                                    "out" -> "تنش‌ها را کاملاً بیرون بریزید"
                                    else -> "۵ چرخه تنفس آگاهانه و منظم"
                                }

                                Text(
                                    text = statusText,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldDark,
                                        fontSize = 22.sp
                                    ),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = explanationText,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                Box(
                                    modifier = Modifier
                                        .size(260.dp)
                                        .background(
                                            color = EmeraldPrimary.copy(alpha = if (breathPhase == "hold") glowAlpha * 0.2f else 0.08f),
                                            shape = androidx.compose.foundation.shape.CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(240.dp)
                                            .scale(circleScale)
                                            .background(
                                                brush = Brush.radialGradient(
                                                    colors = listOf(
                                                        MintGreen,
                                                        EmeraldPrimary
                                                    )
                                                ),
                                                shape = androidx.compose.foundation.shape.CircleShape
                                            )
                                            .clip(androidx.compose.foundation.shape.CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            if (isBreathingActive) {
                                                Text(
                                                    text = phaseSecondsLeft.toString(),
                                                    style = MaterialTheme.typography.headlineLarge.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White,
                                                        fontSize = 48.sp
                                                    )
                                                )
                                                Text(
                                                    text = "ثانیه",
                                                    style = MaterialTheme.typography.labelMedium.copy(color = Color.White.copy(alpha = 0.8f))
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.Spa,
                                                    contentDescription = "شروع",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(56.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                if (isBreathingActive) {
                                    Text(
                                        text = "چرخه $completedCycles از $maxCycles",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldPrimary
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    if (!isBreathingActive) {
                                        Button(
                                            onClick = {
                                                isBreathingActive = true
                                                breathPhase = "in"
                                                phaseSecondsLeft = 4
                                                completedCycles = 0
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                            shape = RoundedCornerShape(14.dp),
                                            modifier = Modifier
                                                .fillMaxWidth(0.7f)
                                                .height(50.dp)
                                                .testTag("start_breathing_button")
                                        ) {
                                            Text(
                                                text = "شروع تمرین آرامش 🌱",
                                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    } else {
                                        OutlinedButton(
                                            onClick = {
                                                isBreathingActive = false
                                                breathPhase = "idle"
                                            },
                                            shape = RoundedCornerShape(14.dp),
                                            modifier = Modifier
                                                .fillMaxWidth(0.7f)
                                                .height(50.dp)
                                                .testTag("stop_breathing_button"),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                                        ) {
                                            Text(
                                                text = "توقف تمرین تنفس 🛑",
                                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
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

        if (moods.isEmpty()) {
            // Empty state (Supportive, non-shaming) with mock generator option
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MintGreen.copy(alpha = 0.5f),
                        contentColor = EmeraldDark
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .testTag("dashboard_empty_card")
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = "آرامش",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "هر وقت آماده بودی ما اینجاییم...",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "ثبت روزانه احوال روحی فاقد هرگونه جریمه یا فشار است. هر زمان تمایل داشتید، احساس فعلی خود را ثبت کنید تا به مرور خودآگاهی عمیق‌تری نسبت به احساسات خود پیدا کنید.",
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                            textAlign = TextAlign.Center
                        )
                        
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.navigate("mood") },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "اولین ثبت حال روحی 🌱",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            
                            OutlinedButton(
                                onClick = { viewModel.generateSampleWeeklyData() },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("generate_sample_button")
                            ) {
                                Text(
                                    text = "شبیه‌سازی داده‌های آزمایشی ۷ روز گذشته 📊",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Display the beautiful custom WeeklyMoodChart component
            item {
                WeeklyMoodChart(moods = moods)
            }

            // Historical Log Entries List header
            item {
                Text(
                    text = "سوابق احوال ثبت‌شده",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    textAlign = TextAlign.Right
                )
            }

            // List of historical logs
            items(moods) { mood ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mood.moodValue,
                            fontSize = 32.sp,
                            modifier = Modifier.testTag("mood_log_emoji_${mood.id}")
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "حس و حال: ${mood.moodLabel}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    textAlign = TextAlign.Right
                                )
                                Text(
                                    text = mood.date,
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                            if (mood.note.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = mood.note,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 20.sp
                                    ),
                                    textAlign = TextAlign.Right
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
