package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.ContentItemEntity
import com.example.ui.components.WeeklyMoodChart
import com.example.data.database.MoodEntity
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.isSystemInDarkTheme
import com.arama.app.R
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Date
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentLibraryScreen(
    viewModel: MainViewModel,
    initialTab: Int = 0
) {
    val items by viewModel.contentItems.collectAsState()
    val subscriptionPlan by viewModel.subscriptionPlan.collectAsState()
    val isPremiumUser = subscriptionPlan != "FREE"
    val moods by viewModel.allMoods.collectAsState()

    var selectedTab by remember(initialTab) { mutableStateOf(initialTab) }
    var activeItem by remember { mutableStateOf<ContentItemEntity?>(null) }
    var showUpgradeDialog by remember { mutableStateOf(false) }

    // Filter items by category
    val meditations = items.filter { it.category == "MEDITATION" }
    val breathings = items.filter { it.category == "BREATHING" }
    val sounds = items.filter { it.category == "SOUND" }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (activeItem == null) {
            // Main Browse View with Tabs
            Scaffold(
                topBar = {
                    Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = "آرامش و احوال روحی آراما",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SageDeep
                                    )
                                )
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = Color.Transparent
                            )
                        )
                        
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = Color.Transparent,
                            contentColor = SagePrimary,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = SagePrimary
                                )
                            }
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                text = { Text("کتابخانه آرامش 🧘", fontWeight = FontWeight.Bold) },
                                selectedContentColor = SagePrimary,
                                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                text = { Text("روند و ثبت احوال 📊", fontWeight = FontWeight.Bold) },
                                selectedContentColor = SagePrimary,
                                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                containerColor = Color.Transparent,
                modifier = Modifier.testTag("content_library_browse_root")
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    if (selectedTab == 0) {
                        // Header / Intro
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .padding(top = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "محتوای آرامش ذهن، مستقل از هوش مصنوعی",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SageDeep
                                    ),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "مدیتیشن‌های صوتی متنی، تمرینات تنفس و آواهای آرامش‌بخش را برای دسترسی آنی و بدون نیاز به چت تجربه کنید.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 22.sp
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // 1. MEDITATIONS
                        if (meditations.isNotEmpty()) {
                            item {
                                CategoryRow(
                                    title = "مدیتیشن‌های هدایت‌شده متنی 🧘",
                                    items = meditations,
                                    isPremiumUser = isPremiumUser,
                                    onItemClick = { item ->
                                        if (item.isFree || isPremiumUser) {
                                            activeItem = item
                                        } else {
                                            showUpgradeDialog = true
                                        }
                                    }
                                )
                            }
                        }

                        // 2. BREATHING
                        if (breathings.isNotEmpty()) {
                            item {
                                CategoryRow(
                                    title = "تمرینات تنفس عمیق آگاهانه 🌬️",
                                    items = breathings,
                                    isPremiumUser = isPremiumUser,
                                    onItemClick = { item ->
                                        if (item.isFree || isPremiumUser) {
                                            activeItem = item
                                        } else {
                                            showUpgradeDialog = true
                                        }
                                    }
                                )
                            }
                        }

                        // 3. SOUNDS
                        if (sounds.isNotEmpty()) {
                            item {
                                CategoryRow(
                                    title = "آواهای آرامش‌بخش طبیعت 🎧",
                                    items = sounds,
                                    isPremiumUser = isPremiumUser,
                                    onItemClick = { item ->
                                        if (item.isFree || isPremiumUser) {
                                            activeItem = item
                                        } else {
                                            showUpgradeDialog = true
                                        }
                                    }
                                )
                            }
                        }
                    } else {
                        // Tab 1: Mood Trends & Logs
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .padding(top = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "روند احوال روحی شما",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SageDeep
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
                                                color = SagePrimary
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Daily Mood Check-In Card
                        item {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        text = "احوال امروز شما چطور است؟ 🌱",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = SageDeep
                                        )
                                    )
                                    
                                    val haptic = LocalHapticFeedback.current
                                    val selectedEmoji by viewModel.selectedMoodEmoji.collectAsState()
                                    val selectedLabel by viewModel.selectedMoodLabel.collectAsState()
                                    val note by viewModel.moodNote.collectAsState()

                                    val moodOptions = listOf(
                                        LibraryMoodItem("😊", "عالی", Color(0xFF10B77F)),
                                        LibraryMoodItem("🙂", "خوب", Color(0xFF34D399)),
                                        LibraryMoodItem("😐", "معمولی", Color(0xFFFBBF24)),
                                        LibraryMoodItem("😔", "خسته", Color(0xFF60A5FA)),
                                        LibraryMoodItem("😢", "غمگین", Color(0xFFF87171)),
                                        LibraryMoodItem("😡", "عصبانی", Color(0xFFEF4444))
                                    )

                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            moodOptions.take(3).forEach { mood ->
                                                MoodButtonMini(
                                                    mood = mood,
                                                    isSelected = selectedEmoji == mood.emoji,
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        viewModel.selectMood(mood.emoji, mood.label)
                                                    }
                                                )
                                            }
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            moodOptions.takeLast(3).forEach { mood ->
                                                MoodButtonMini(
                                                    mood = mood,
                                                    isSelected = selectedEmoji == mood.emoji,
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        viewModel.selectMood(mood.emoji, mood.label)
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    if (selectedEmoji != null) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        OutlinedTextField(
                                            value = note,
                                            onValueChange = {
                                                viewModel.setMoodNote(it)
                                            },
                                            label = { Text("یادداشت روزانه (اختیاری)") },
                                            placeholder = { Text("امروز چطور گذشت؟...") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(90.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = SagePrimary,
                                                focusedLabelColor = SagePrimary
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Button(
                                            onClick = {
                                                viewModel.saveMoodCheckIn()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp)
                                        ) {
                                            Text(
                                                text = "ثبت حال روحی با عنوان «${selectedLabel}»",
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Voice Journaling Section with AI Summaries
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            VoiceJournalSection(viewModel = viewModel)
                        }

                        if (moods.isEmpty()) {
                            // Empty State Card
                            item {
                                val isDark = isSystemInDarkTheme()
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isDark) SageTintBgDark else SageTintBg.copy(alpha = 0.35f),
                                        contentColor = if (isDark) Color.White else SageDeep
                                    ),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp)
                                        .testTag("dashboard_empty_card")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.illustration_empty_dashboard),
                                            contentDescription = "بدون ثبت احوال",
                                            modifier = Modifier.size(120.dp)
                                        )
                                        Text(
                                            text = "هر وقت آماده بودی ما اینجاییم...",
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "ثبت روزانه احوال روحی فاقد هرگونه جریمه یا فشار است. هر زمان تمایل داشتید، احساس فعلی خود را ثبت کنید تا به مرور خودآگاهی عمیق‌تری پیدا کنید.",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                lineHeight = 24.sp,
                                                color = if (isDark) Color.White.copy(alpha = 0.85f) else SageDeep.copy(alpha = 0.85f)
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                        
                                        Button(
                                            onClick = { viewModel.generateSampleWeeklyData() },
                                            colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth().testTag("generate_sample_button")
                                        ) {
                                            Text(
                                                text = "شبیه‌سازی داده‌های آزمایشی ۷ روز گذشته 📊",
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // Weekly Mood Chart
                            item {
                                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                                    WeeklyMoodChart(moods = moods)
                                }
                            }

                            // Historical Logs Title
                            item {
                                Text(
                                    text = "سوابق احوال ثبت‌شده",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SageDeep
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp)
                                        .padding(top = 12.dp),
                                    textAlign = TextAlign.Start
                                )
                            }

                            // List of logs
                            items(moods) { mood ->
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp),
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
                                                    textAlign = TextAlign.Start
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
                                                    textAlign = TextAlign.Start
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
        } else {
            // Player / Detail View
            val currentItem = activeItem!!
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                when (currentItem.category) {
                    "MEDITATION" -> MeditationPlayer(
                        item = currentItem,
                        onBack = { activeItem = null }
                    )
                    "BREATHING" -> BreathingPlayer(
                        item = currentItem,
                        onBack = { activeItem = null }
                    )
                    "SOUND" -> SoundPlayer(
                        item = currentItem,
                        onBack = { activeItem = null }
                    )
                }
            }
        }

        // Upgrade Alert Dialog
        if (showUpgradeDialog) {
            AlertDialog(
                onDismissRequest = { showUpgradeDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "قفل",
                        tint = SagePrimary,
                        modifier = Modifier.size(36.dp)
                    )
                },
                title = {
                    Text(
                        text = "دسترسی به بخش ویژه",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Text(
                        text = "این تمرین جزو محتوای ویژه آراما است. با خرید اشتراک طلایی، به تمامی مدیتیشن‌ها، الگوهای تنفس و آواهای آرامش‌بخش دسترسی نامحدود پیدا کنید.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showUpgradeDialog = false
                            viewModel.navigate("settings")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SagePrimary)
                    ) {
                        Text("خرید اشتراک آراما 🚀")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showUpgradeDialog = false }
                    ) {
                        Text("بعداً", color = SageDeep)
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}

@Composable
fun CategoryRow(
    title: String,
    items: List<ContentItemEntity>,
    isPremiumUser: Boolean,
    onItemClick: (ContentItemEntity) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = SageDeep
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            textAlign = TextAlign.Start
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                ContentItemCard(
                    item = item,
                    isPremiumUser = isPremiumUser,
                    onClick = { onItemClick(item) }
                )
            }
        }
    }
}

@Composable
fun ContentItemCard(
    item: ContentItemEntity,
    isPremiumUser: Boolean,
    onClick: () -> Unit
) {
    val isLocked = !item.isFree && !isPremiumUser
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .width(180.dp)
            .height(200.dp)
            .clickable { onClick() }
            .testTag("content_item_card_${item.id}")
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header Row (Emoji + Badge/Lock)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(SageTintBg, shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.iconEmoji,
                            fontSize = 22.sp
                        )
                    }

                    if (isLocked) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "محتوای ویژه",
                            tint = Greige,
                            modifier = Modifier.size(18.dp)
                        )
                    } else if (item.isFree) {
                        Box(
                            modifier = Modifier
                                .background(SagePrimary.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "رایگان",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SagePrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                // Title and description
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDeep
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.shortDescription,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Duration Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "زمان",
                        tint = SagePrimary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (item.category == "BREATHING") {
                            "تمرین ${item.inhaleSeconds}-${item.holdSeconds}-${item.exhaleSeconds}"
                        } else {
                            "${item.durationSeconds / 60} دقیقه"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SagePrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------------------------
// 1. Meditation Player (Text Scroll View)
// ----------------------------------------------------------------------
@Composable
fun MeditationPlayer(
    item: ContentItemEntity,
    onBack: () -> Unit
) {
    val bgSynthesizer = remember { AmbientSoundSynthesizer() }
    var isPlaying by remember { mutableStateOf(false) }
    var secondsElapsed by remember { mutableStateOf(0) }
    var selectedBgSound by remember { mutableStateOf("zen") } // Default to our beautiful Zen Solfeggio drone
    var bgVolume by remember { mutableStateOf(0.4f) }

    // Control sound play state and disposal on navigate back
    DisposableEffect(Unit) {
        onDispose {
            bgSynthesizer.stopPlaying()
        }
    }

    LaunchedEffect(isPlaying, selectedBgSound) {
        if (isPlaying && selectedBgSound != "none") {
            bgSynthesizer.startPlaying(selectedBgSound, bgVolume)
        } else {
            bgSynthesizer.stopPlaying()
        }
    }

    LaunchedEffect(bgVolume) {
        bgSynthesizer.setVolume(bgVolume)
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isPlaying && secondsElapsed < item.durationSeconds) {
                delay(1000)
                secondsElapsed += 1
            }
            if (secondsElapsed >= item.durationSeconds) {
                isPlaying = false
            }
        }
    }

    val progress = secondsElapsed.toFloat() / item.durationSeconds.toFloat()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val bgOptions = listOf(
        Triple("none", "سکوت 🔕", "بدون موسیقی زمینه"),
        Triple("zen", "فرکانس ۵۲۸ هرتز 🧘", "هارمونی التیام‌بخش ذن"),
        Triple("chimes", "ناقوس تبتی 🔔", "طنین شفا‌بخش کاسه‌های تبت"),
        Triple("rain", "باران پاییزی 🌧️", "ریزش ملایم و آرام باران"),
        Triple("ocean", "امواج اقیانوس 🌊", "جزر و مد صخره‌های ساحلی"),
        Triple("forest", "نسیم جنگل 🌲", "باد پاییزی در شاخسار بید"),
        Triple("fire", "آتش کلبه 🔥", "نوستالژی ترکیدن هیزم‌ها")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("meditation_player_screen")
    ) {
        // Navigation / Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    bgSynthesizer.stopPlaying()
                    isPlaying = false
                    onBack()
                },
                modifier = Modifier.background(SageTintBg, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "بازگشت",
                    tint = SageDeep
                )
            }
            Text(
                text = "مدیتیشن و موسیقی آرامش",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SagePrimary
                )
            )
            Box(modifier = Modifier.size(48.dp)) // Spacer
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card displaying current session
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pulsating Icon + Title
                Box(
                    modifier = Modifier.height(72.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPlaying) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .scale(pulseScale)
                                .background(SagePrimary.copy(alpha = pulseAlpha), CircleShape)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(SageTintBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = item.iconEmoji, fontSize = 32.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SageDeep
                    ),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable text body
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            RoundedCornerShape(16.dp)
                        )
                        .border(1.dp, SageTintBg, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = item.guidedText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 26.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // MUSIC / SONG CHOOSER (UI/UX enhancement)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "موسیقی و آوای پس‌زمینه 🎵",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDeep
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        reverseLayout = true // Right to Left for Persian readability
                    ) {
                        items(bgOptions) { option ->
                            val isSelected = selectedBgSound == option.first
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) SagePrimary else SageTintBg.copy(alpha = 0.5f)
                                    )
                                    .clickable {
                                        selectedBgSound = option.first
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = option.second,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isSelected) Color.White else SageDeep,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                    
                    if (selectedBgSound != "none") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Slider(
                                value = bgVolume,
                                onValueChange = { bgVolume = it },
                                valueRange = 0f..1f,
                                colors = SliderDefaults.colors(
                                    activeTrackColor = SagePrimary,
                                    inactiveTrackColor = SageTintBg,
                                    thumbColor = SageDeep
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "ولوم موسیقی",
                                tint = SagePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progression Bar
                Column(modifier = Modifier.fillMaxWidth()) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = SagePrimary,
                        trackColor = SageTintBg
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(item.durationSeconds - secondsElapsed),
                            style = MaterialTheme.typography.bodySmall.copy(color = SagePrimary, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = formatTime(secondsElapsed),
                            style = MaterialTheme.typography.bodySmall.copy(color = SageDeep)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Play / Pause controls
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { secondsElapsed = 0; isPlaying = false },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay,
                            contentDescription = "شروع مجدد",
                            tint = SageDeep,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier
                            .size(56.dp)
                            .background(SagePrimary, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "توقف" else "پخش",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    IconButton(
                        onClick = {
                            secondsElapsed = (secondsElapsed + 15).coerceAtMost(item.durationSeconds)
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "۱۵ ثانیه بعد",
                            tint = SageDeep,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------
// 2. Breathing Player
// ----------------------------------------------------------------------
@Composable
fun BreathingPlayer(
    item: ContentItemEntity,
    onBack: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var isPlaying by remember { mutableStateOf(false) }
    var breathPhase by remember { mutableStateOf("idle") } // "idle", "in", "hold", "out", "completed"
    var phaseSecondsLeft by remember { mutableStateOf(item.inhaleSeconds) }
    var completedCycles by remember { mutableStateOf(0) }
    val maxCycles = 5

    // Manage the phase transition
    LaunchedEffect(isPlaying, breathPhase) {
        if (isPlaying) {
            if (breathPhase == "idle") {
                breathPhase = "in"
                phaseSecondsLeft = item.inhaleSeconds
            }
            while (isPlaying && breathPhase != "completed") {
                delay(1000)
                phaseSecondsLeft -= 1
                if (phaseSecondsLeft <= 0) {
                    when (breathPhase) {
                        "in" -> {
                            if (item.holdSeconds > 0) {
                                breathPhase = "hold"
                                phaseSecondsLeft = item.holdSeconds
                            } else {
                                breathPhase = "out"
                                phaseSecondsLeft = item.exhaleSeconds
                            }
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                        "hold" -> {
                            breathPhase = "out"
                            phaseSecondsLeft = item.exhaleSeconds
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                        "out" -> {
                            completedCycles += 1
                            if (completedCycles >= maxCycles) {
                                breathPhase = "completed"
                                isPlaying = false
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            } else {
                                breathPhase = "in"
                                phaseSecondsLeft = item.inhaleSeconds
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                    }
                }
            }
        } else {
            if (breathPhase != "completed") {
                breathPhase = "idle"
                completedCycles = 0
                phaseSecondsLeft = item.inhaleSeconds
            }
        }
    }

    // Circle animation based on breath phase
    val animDuration = when (breathPhase) {
        "in" -> item.inhaleSeconds * 1000
        "hold" -> 0
        "out" -> item.exhaleSeconds * 1000
        else -> 0
    }
    val circleScale by animateFloatAsState(
        targetValue = if (breathPhase == "in" || breathPhase == "hold") 1.0f else 0.5f,
        animationSpec = if (animDuration > 0) tween(durationMillis = animDuration, easing = LinearOutSlowInEasing) else spring(),
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("breathing_player_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Navigation / Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    isPlaying = false
                    onBack()
                },
                modifier = Modifier.background(SageTintBg, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "بازگشت",
                    tint = SageDeep
                )
            }
            Text(
                text = "تمرین تنفس تعاملی",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SagePrimary
                )
            )
            Box(modifier = Modifier.size(48.dp)) // Spacer
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Intro title & sub
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDeep
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "الگوی تنفس ${item.inhaleSeconds} ثانیه دم، ${item.holdSeconds} ثانیه حبس، ${item.exhaleSeconds} ثانیه بازدم",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        textAlign = TextAlign.Center
                    )
                }

                if (breathPhase == "completed") {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "✨ تمرین تنفس تکمیل شد ✨",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = SagePrimary
                            ),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "تبریک! شما ۵ چرخه تنفس عمیق را با موفقیت تمام کردید. این کار باعث ثبات ضربان قلب و تسکین آنی استرس شما می‌شود.",
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                breathPhase = "idle"
                                completedCycles = 0
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("شروع مجدد تمرین")
                        }
                    }
                } else {
                    // Breathing Guide Circle
                    val statusText = when (breathPhase) {
                        "in" -> "دم عمیق (از بینی)..."
                        "hold" -> "حبس نفس..."
                        "out" -> "بازدم آرام (از دهان)..."
                        else -> "آماده شروع تمرین هستید؟"
                    }
                    val explanationText = when (breathPhase) {
                        "in" -> "ریه‌های خود را پر از اکسیژن کنید"
                        "hold" -> "آرامش را در تمام عضلات حفظ کنید"
                        "out" -> "تمام خستگی‌ها را رها کنید"
                        else -> "کافیست دکمه شروع را لمس کنید"
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDeep,
                                fontSize = 20.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = explanationText,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .background(
                                color = SagePrimary.copy(alpha = if (breathPhase == "hold") glowAlpha * 0.2f else 0.08f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(220.dp)
                                .scale(circleScale)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            SageTintBg,
                                            SagePrimary
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (isPlaying) {
                                    Text(
                                        text = phaseSecondsLeft.toString(),
                                        style = MaterialTheme.typography.headlineLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 44.sp
                                        )
                                    )
                                    Text(
                                        text = "ثانیه",
                                        style = MaterialTheme.typography.labelMedium.copy(color = Color.White.copy(alpha = 0.8f))
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Spa,
                                        contentDescription = "آماده",
                                        tint = Color.White,
                                        modifier = Modifier.size(52.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (isPlaying) {
                        Text(
                            text = "چرخه $completedCycles از $maxCycles",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SagePrimary
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (!isPlaying) {
                            Button(
                                onClick = {
                                    isPlaying = true
                                    breathPhase = "in"
                                    phaseSecondsLeft = item.inhaleSeconds
                                    completedCycles = 0
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(50.dp)
                                    .testTag("start_content_breathing_button")
                            ) {
                                Text(
                                    text = "شروع تمرین تنفس 🌱",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    isPlaying = false
                                    breathPhase = "idle"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EarthyError),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(50.dp)
                                    .testTag("stop_content_breathing_button")
                            ) {
                                Text(
                                    text = "توقف تمرین 🛑",
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

// ----------------------------------------------------------------------
// 3. Sound Player (Ambient Sounds Synthesizer Integration)
// ----------------------------------------------------------------------
@Composable
fun SoundPlayer(
    item: ContentItemEntity,
    onBack: () -> Unit
) {
    val synthesizer = remember { AmbientSoundSynthesizer() }
    var isPlaying by remember { mutableStateOf(false) }
    var volume by remember { mutableStateOf(0.7f) }
    var secondsElapsed by remember { mutableStateOf(0) }

    // Control sound play state and disposal on navigate back
    DisposableEffect(item.soundType) {
        onDispose {
            synthesizer.stopPlaying()
        }
    }

    LaunchedEffect(isPlaying, item.soundType) {
        if (isPlaying) {
            synthesizer.startPlaying(item.soundType, volume)
            while (isPlaying && secondsElapsed < item.durationSeconds) {
                delay(1000)
                secondsElapsed += 1
            }
            if (secondsElapsed >= item.durationSeconds) {
                isPlaying = false
                synthesizer.stopPlaying()
            }
        } else {
            synthesizer.stopPlaying()
        }
    }

    LaunchedEffect(volume) {
        synthesizer.setVolume(volume)
    }

    val progress = secondsElapsed.toFloat() / item.durationSeconds.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("sound_player_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Navigation / Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    synthesizer.stopPlaying()
                    isPlaying = false
                    onBack()
                },
                modifier = Modifier.background(SageTintBg, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "بازگشت",
                    tint = SageDeep
                )
            }
            Text(
                text = "آوای آرامش‌بخش طبیعت",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SagePrimary
                )
            )
            Box(modifier = Modifier.size(48.dp)) // Spacer
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDeep
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.shortDescription,
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        textAlign = TextAlign.Center
                    )
                }

                // Wave Visualizer Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(SageTintBg, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = item.iconEmoji, fontSize = 38.sp)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Render beautiful dynamic visualizer bars when playing
                        AudioVisualizer(isPlaying = isPlaying)
                    }
                }

                // Progress Bar
                Column(modifier = Modifier.fillMaxWidth()) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = SagePrimary,
                        trackColor = SageTintBg
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(item.durationSeconds - secondsElapsed),
                            style = MaterialTheme.typography.bodySmall.copy(color = SagePrimary, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = formatTime(secondsElapsed),
                            style = MaterialTheme.typography.bodySmall.copy(color = SageDeep)
                        )
                    }
                }

                // Volume slider
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "حجم صدا",
                        tint = SageDeep.copy(alpha = 0.8f),
                        modifier = Modifier.size(22.dp)
                    )
                    Slider(
                        value = volume,
                        onValueChange = { volume = it },
                        colors = SliderDefaults.colors(
                            thumbColor = SagePrimary,
                            activeTrackColor = SagePrimary,
                            inactiveTrackColor = SageTintBg
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Play & pause button
                IconButton(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier
                        .size(64.dp)
                        .background(SagePrimary, CircleShape)
                        .testTag("sound_play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "توقف" else "پخش",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

// Helper formatting function
private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format(Locale.US, "%02d:%02d", mins, secs)
}

data class LibraryMoodItem(
    val emoji: String,
    val label: String,
    val color: Color
)

@Composable
fun MoodButtonMini(
    mood: LibraryMoodItem,
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
            .width(80.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(10.dp)
            .testTag("mood_option_${mood.label}")
    ) {
        Text(
            text = mood.emoji,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = mood.label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) SageDeep else MaterialTheme.colorScheme.onSurface,
                fontSize = 11.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun VoiceJournalSection(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val voiceJournals by viewModel.allVoiceJournals.collectAsState()
    val isSummarizing by viewModel.isSummarizingJournal.collectAsState()
    val journalError by viewModel.journalError.collectAsState()

    var transcribedText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("دکمه میکروفون را بزنید و شروع به صحبت کنید 🎙️") }
    var secondsElapsed by remember { mutableStateOf(0) }
    var rmsValue by remember { mutableStateOf(0f) }

    // State for Collapsed/Expanded full raw text of entries
    var expandedEntryId by remember { mutableStateOf<Int?>(null) }

    // Init speech recognizer safely
    val speechRecognizer = remember {
        try {
            SpeechRecognizer.createSpeechRecognizer(context)
        } catch (e: Exception) {
            null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                speechRecognizer?.destroy()
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    // Timer effect
    LaunchedEffect(isListening) {
        if (isListening) {
            secondsElapsed = 0
            while (isListening) {
                delay(1000)
                secondsElapsed++
            }
        }
    }

    val speechListener = remember {
        object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
                statusText = "در حال شنیدن... صحبت کنید 🎤"
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {
                rmsValue = rmsdB
            }
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                statusText = "در حال پردازش صدا... ⏳"
            }
            override fun onError(error: Int) {
                isListening = false
                val message = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "خطای صوتی."
                    SpeechRecognizer.ERROR_CLIENT -> "سرویس گفتار در دسترس نیست یا متوقف شد."
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "عدم دسترسی به میکروفون."
                    SpeechRecognizer.ERROR_NETWORK -> "خطای شبکه."
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "اتمام زمان شبکه."
                    SpeechRecognizer.ERROR_NO_MATCH -> "کلمه‌ای تشخیص داده نشد."
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "سرویس شلوغ است. لطفاً کمی صبر کنید."
                    SpeechRecognizer.ERROR_SERVER -> "خطای سرور گوگل."
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "سکوت طولانی تشخیص داده شد."
                    else -> "سرویس تشخیص گفتار فعال نشد."
                }
                statusText = "مکث ضبط ($message)"
            }
            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    transcribedText = matches[0]
                    statusText = "متن با موفقیت پیاده‌سازی شد ✅"
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    transcribedText = matches[0]
                }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    // Set listener
    LaunchedEffect(speechRecognizer) {
        speechRecognizer?.setRecognitionListener(speechListener)
    }

    val recordPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Start listening
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa-IR")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "fa-IR")
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "fa-IR")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }
            try {
                speechRecognizer?.startListening(intent)
                isListening = true
            } catch (e: Exception) {
                statusText = "خطا در راه‌اندازی ضبط: ${e.localizedMessage}"
            }
        } else {
            statusText = "برای ضبط خاطرات باید دسترسی میکروفون را تأیید کنید. ⚠️"
        }
    }

    fun toggleListening() {
        if (isListening) {
            try {
                speechRecognizer?.stopListening()
            } catch (e: Exception) {
                // ignore
            }
            isListening = false
        } else {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa-IR")
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "fa-IR")
                    putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "fa-IR")
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                }
                try {
                    speechRecognizer?.startListening(intent)
                    isListening = true
                } catch (e: Exception) {
                    statusText = "خطا در راه‌اندازی ضبط: ${e.localizedMessage}"
                }
            } else {
                recordPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .testTag("voice_journal_card"),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ثبت صوتی خاطرات و خلاصه هوش مصنوعی 🎙️✨",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SageDeep
                    ),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Right
                )
            }

            Text(
                text = "کافیست با لمس دکمه زیر صحبت کنید. هوش مصنوعی آراما خاطرات صوتی شما را خلاصه‌سازی، تحلیل و به صورت ایمن ذخیره می‌کند.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )

            // Dynamic microphone pulsating ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp)
            ) {
                if (isListening) {
                    // Pulsating ring based on audio level (rmsValue)
                    val pulseScale by animateFloatAsState(
                        targetValue = 1f + (rmsValue.coerceIn(0f, 10f) / 10f) * 0.4f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                        label = "rms_pulse"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(pulseScale)
                            .background(SagePrimary.copy(alpha = 0.25f), CircleShape)
                    )
                }

                IconButton(
                    onClick = { toggleListening() },
                    modifier = Modifier
                        .size(68.dp)
                        .background(if (isListening) CrisisRed else SagePrimary, CircleShape)
                        .testTag("microphone_record_button")
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (isListening) "توقف ضبط" else "شروع ضبط صوتی",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Status and Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isListening) {
                    val formattedTime = String.format(Locale.US, "%02d:%02d", secondsElapsed / 60, secondsElapsed % 60)
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CrisisRed
                        )
                    )
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (isListening) CrisisRed else SageDeep
                    ),
                    textAlign = TextAlign.Right
                )
            }

            // Real-time or editable transcription text box
            OutlinedTextField(
                value = transcribedText,
                onValueChange = { transcribedText = it },
                label = { Text("متن گفتار پیاده‌سازی شده") },
                placeholder = { Text("صحبت کنید تا متن شما اینجا نمایان شود یا خودتان خاطره را بنویسید...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("voice_journal_text_field"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SagePrimary,
                    focusedLabelColor = SagePrimary
                )
            )

            // Submit to Gemini Summarization Button
            Button(
                onClick = {
                    if (transcribedText.isNotBlank()) {
                        viewModel.summarizeAndSaveVoiceJournal(transcribedText, secondsElapsed)
                        transcribedText = ""
                        statusText = "خاطره با موفقیت برای خلاصه‌سازی به جمینای ارسال شد ✨"
                    }
                },
                enabled = transcribedText.isNotBlank() && !isSummarizing,
                colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("summarize_journal_button")
            ) {
                if (isSummarizing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "در حال خلاصه‌سازی با هوش مصنوعی... 🧠",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "خلاصه‌سازی جمینای",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "تحلیل و خلاصه‌سازی با هوش مصنوعی جمینای",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            if (journalError != null) {
                Text(
                    text = journalError ?: "",
                    style = MaterialTheme.typography.bodySmall.copy(color = CrisisRed),
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Historical list of voice journals
    if (voiceJournals.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "دفترچه خاطرات صوتی گذشته 📔✨",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = SageDeep
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            textAlign = TextAlign.Right
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            voiceJournals.forEach { journal ->
                val isExpanded = expandedEntryId == journal.id
                val moodEmoji = when (journal.moodSuggestion) {
                    "عالی" -> "😊"
                    "خوب" -> "🙂"
                    "معمولی" -> "😐"
                    "خسته" -> "😔"
                    "غمگین" -> "😢"
                    "عصبانی" -> "😡"
                    else -> "📝"
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("voice_journal_item_${journal.id}"),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Header of log card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = moodEmoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    val journalDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(java.util.Date(journal.timestamp))
                                    Text(
                                        text = if (journal.recordedText.length > 25) journal.recordedText.take(25) + "..." else "خاطره صوتی جدید",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        textAlign = TextAlign.Right
                                    )
                                    Text(
                                        text = "$journalDate • ضبط صوتی",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.deleteVoiceJournal(journal.id) },
                                modifier = Modifier.testTag("delete_voice_journal_${journal.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "حذف خاطره صوتی",
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Summary Section
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SageTintBg.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "تحلیل و خلاصه هوش مصنوعی 🧠✨",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = SageDeep
                                        )
                                    )
                                    Text(
                                        text = "وضعیت حسی: ${journal.moodSuggestion}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = SagePrimary
                                        )
                                    )
                                }
                                Text(
                                    text = journal.summary,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        lineHeight = 22.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Raw text collapsible
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedEntryId = if (isExpanded) null else journal.id
                                    }
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "مشاهده متن کامل",
                                    tint = SageDeep
                                )
                                Text(
                                    text = "مشاهده گفتار خام پیاده‌سازی شده",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SageDeep
                                    )
                                )
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Text(
                                    text = journal.recordedText,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        lineHeight = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
