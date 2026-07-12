package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.ui.draw.scale
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.LinearEasing
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
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

data class MoodItem(
    val emoji: String,
    val label: String,
    val color: Color
)

@Composable
fun MoodScreen(
    viewModel: MainViewModel
) {
    val selectedEmoji by viewModel.selectedMoodEmoji.collectAsState()
    val selectedLabel by viewModel.selectedMoodLabel.collectAsState()
    val note by viewModel.moodNote.collectAsState()
    val haptic = LocalHapticFeedback.current

    val moodOptions = listOf(
        MoodItem("😊", "عالی", Color(0xFF10B77F)),
        MoodItem("🙂", "خوب", Color(0xFF34D399)),
        MoodItem("😐", "معمولی", Color(0xFFFBBF24)),
        MoodItem("😔", "خسته", Color(0xFF60A5FA)),
        MoodItem("😢", "غمگین", Color(0xFFF87171)),
        MoodItem("😡", "عصبانی", Color(0xFFEF4444))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("mood_screen_root"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome and intro
        Icon(
            imageVector = Icons.Default.Spa,
            contentDescription = "حال روحی",
            tint = SagePrimary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "احوال امروز شما چطور است؟",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = SageDeep
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "یک ایموجی را انتخاب کنید. حال روحی شما فوراً و با امنیت ذخیره می‌شود.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Grid of Mood Emojis
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            moodOptions.take(3).forEach { mood ->
                MoodButton(
                    mood = mood,
                    isSelected = selectedEmoji == mood.emoji,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.selectMood(mood.emoji, mood.label)
                        viewModel.saveMoodCheckIn()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            moodOptions.takeLast(3).forEach { mood ->
                MoodButton(
                    mood = mood,
                    isSelected = selectedEmoji == mood.emoji,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.selectMood(mood.emoji, mood.label)
                        viewModel.saveMoodCheckIn()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // If mood has been selected, show auto-save confirmation and optional note
        if (selectedEmoji != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = SageTintBg.copy(alpha = 0.5f),
                    contentColor = SageDeep
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("autosave_confirmation_card")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "ثبت شد",
                        tint = SagePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "حال روحی شما با عنوان «${selectedLabel}» ثبت خودکار شد!",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "در صورت تمایل می‌توانید یادداشتی اضافه کنید:",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = note,
                onValueChange = {
                    viewModel.setMoodNote(it)
                    // Auto-saving the note dynamically
                    viewModel.saveMoodCheckIn()
                },
                label = { Text("یادداشت روزانه (اختیاری)") },
                placeholder = { Text("مثال: امروز تمرین تنفس را انجام دادم و احساس آرامش بیشتری می‌کنم...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "یادداشت",
                        tint = SagePrimary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .testTag("mood_note_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SagePrimary,
                    focusedLabelColor = SagePrimary
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "✓ تغییرات یادداشت شما فوراً ذخیره می‌شود.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = SagePrimary,
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.navigate("dashboard") },
                colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("mood_return_to_dashboard_button")
            ) {
                Text(
                    text = "مشاهده روند و نمودار احساسات 📊",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Meditation Music Player Card
        val tracks = remember {
            listOf(
                MeditationTrack("صدای باران در جنگل", "03:00", "ریتم آرامش‌بخش قطرات باران میان برگ درختان", "🌧️"),
                MeditationTrack("موج آرام اقیانوس", "04:30", "حرکت منظم امواج بر روی ساحل صخره‌ای", "🌊"),
                MeditationTrack("پیانوی کهکشانی ذهن", "05:00", "تک‌نوازی ملایم پیانو در فرکانس آلفا", "🎹"),
                MeditationTrack("فرکانس شفابخش ۵۲۸ هرتز", "06:15", "فرکانس معروف به بازسازی سلول‌ها و آرامش عمیق", "🧘")
            )
        }

        var isPlaying by remember { mutableStateOf(false) }
        var currentTrackIndex by remember { mutableStateOf(0) }
        var elapsedTimeSeconds by remember { mutableStateOf(0) }
        var volumeValue by remember { mutableStateOf(0.7f) }

        val currentTrack = tracks[currentTrackIndex]
        val durationParts = currentTrack.duration.split(":")
        val totalSeconds = durationParts[0].toInt() * 60 + durationParts[1].toInt()
        val trackProgress = if (totalSeconds > 0) elapsedTimeSeconds.toFloat() / totalSeconds.toFloat() else 0f

        LaunchedEffect(isPlaying, currentTrackIndex) {
            if (isPlaying) {
                while (isPlaying) {
                    kotlinx.coroutines.delay(1000)
                    elapsedTimeSeconds += 1
                    if (elapsedTimeSeconds >= totalSeconds) {
                        elapsedTimeSeconds = 0
                        currentTrackIndex = (currentTrackIndex + 1) % tracks.size
                    }
                }
            }
        }

        fun formatElapsedTime(seconds: Int): String {
            val m = seconds / 60
            val s = seconds % 60
            return String.format(java.util.Locale.US, "%02d:%02d", m, s)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "موسیقی مدیتیشن",
                    tint = SagePrimary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "آواهای آرامش‌بخش مدیتیشن 🎧",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SageDeep
                    ),
                    textAlign = TextAlign.Start
                )
            }
            Text(
                text = "جهت هماهنگی با تمرین تنفس یا آرامش ذهن، یکی از آواهای زیر را پخش کنید:",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 16.dp)
            )

            // Dynamic Player Controller
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("meditation_player_card"),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(SageTintBg, shape = RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentTrack.emoji,
                                    fontSize = 28.sp
                                )
                            }
                            Column {
                                Text(
                                    text = currentTrack.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SageDeep
                                    )
                                )
                                Text(
                                    text = currentTrack.description,
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                    maxLines = 1
                                )
                            }
                        }

                        // Audio Visualizer
                        AudioVisualizer(isPlaying = isPlaying)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress bar
                    Column(modifier = Modifier.fillMaxWidth()) {
                        val isDark = isSystemInDarkTheme()
                        Slider(
                            value = trackProgress,
                            onValueChange = { newValue ->
                                elapsedTimeSeconds = (newValue * totalSeconds).toInt()
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = SagePrimary,
                                activeTrackColor = SagePrimary,
                                inactiveTrackColor = if (isDark) Color.White.copy(alpha = 0.25f) else SagePrimary.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = currentTrack.duration,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isDark) Color(0xFFDDE3D6) else SageDeep.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                text = formatElapsedTime(elapsedTimeSeconds),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isDark) Color(0xFFDDE3D6) else SagePrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Media Control Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                isPlaying = false
                                elapsedTimeSeconds = 0
                                currentTrackIndex = if (currentTrackIndex > 0) currentTrackIndex - 1 else tracks.size - 1
                                isPlaying = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "آهنگ قبلی",
                                tint = SageDeep,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        IconButton(
                            onClick = { isPlaying = !isPlaying },
                            modifier = Modifier
                                .size(50.dp)
                                .background(SagePrimary, shape = androidx.compose.foundation.shape.CircleShape)
                                .testTag("play_pause_button")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "توقف" else "پخش",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        IconButton(
                            onClick = {
                                isPlaying = false
                                elapsedTimeSeconds = 0
                                currentTrackIndex = (currentTrackIndex + 1) % tracks.size
                                isPlaying = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "آهنگ بعدی",
                                tint = SageDeep,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Volume slider row
                    Row(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "صدا",
                            tint = SageDeep.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                        Slider(
                            value = volumeValue,
                            onValueChange = { volumeValue = it },
                            colors = SliderDefaults.colors(
                                thumbColor = SagePrimary.copy(alpha = 0.8f),
                                activeTrackColor = SagePrimary.copy(alpha = 0.6f),
                                inactiveTrackColor = SagePrimary.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Playlist Selection Grid / Column
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tracks.forEachIndexed { index, track ->
                        val isCurrent = index == currentTrackIndex
                        val cardBg = if (isCurrent) SageTintBg else Color.Transparent
                        val cardBorder = if (isCurrent) SagePrimary else Color.Transparent

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(cardBg, RoundedCornerShape(12.dp))
                                .border(1.dp, cardBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    isPlaying = false
                                    elapsedTimeSeconds = 0
                                    currentTrackIndex = index
                                    isPlaying = true
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = track.emoji,
                                    fontSize = 24.sp
                                )
                                Column {
                                    Text(
                                        text = track.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isCurrent) SageDeep else MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = track.duration,
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }
                            }

                            if (isCurrent && isPlaying) {
                                Text(
                                    text = "در حال پخش...",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SagePrimary,
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

data class MeditationTrack(
    val title: String,
    val duration: String,
    val description: String,
    val emoji: String
)

@Composable
fun AudioVisualizer(isPlaying: Boolean) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.height(24.dp)
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "music_visualizer")
        repeat(7) { index ->
            val heightFraction by if (isPlaying) {
                infiniteTransition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 350 + index * 120, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "bar_height"
                )
            } else {
                remember { mutableStateOf(0.2f) }
            }
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(24.dp * heightFraction)
                    .background(SagePrimary, RoundedCornerShape(2.dp))
            )
        }
    }
}

@Composable
fun MoodButton(
    mood: MoodItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) SagePrimary else Color.Transparent
    val backgroundColor = if (isSelected) SageTintBg else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "mood_button_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(96.dp)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
            .testTag("mood_option_${mood.label}")
    ) {
        Text(
            text = mood.emoji,
            fontSize = 32.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = mood.label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) SageDeep else MaterialTheme.colorScheme.onSurface
            ),
            textAlign = TextAlign.Center
        )
    }
}
