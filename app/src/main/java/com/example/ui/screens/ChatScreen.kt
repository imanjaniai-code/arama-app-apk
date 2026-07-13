package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.sp
import com.example.data.database.ChatEntity
import com.example.ui.theme.SageDeep
import com.example.ui.theme.SagePrimary
import com.example.ui.theme.SageTintBg
import com.example.ui.theme.SoftGrey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    viewModel: MainViewModel
) {
    val userName by viewModel.userName.collectAsState()
    var nameInputText by remember { mutableStateOf("") }
    val messages by viewModel.chatMessages.collectAsState()
    val visibleMessages = remember(messages) { messages.filter { it.text.trim().isNotEmpty() } }
    val haptic = LocalHapticFeedback.current
    val isTyping by viewModel.isTyping.collectAsState()
    var inputTexValue by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    // Scroll to bottom when new messages arrive or typing starts
    LaunchedEffect(visibleMessages.size, isTyping) {
        if (visibleMessages.isNotEmpty()) {
            listState.animateScrollToItem(visibleMessages.size - 1)
        }
    }

    val icebreakers = listOf(
        "تمرین تنفس عمیق می‌خوام 🧘‍♀️",
        "احساس اضطراب شدیدی دارم 😞",
        "چگونه خواب راحت‌تری داشته باشم؟ 🌙",
        "روزم را خیلی بی‌انگیزه شروع کردم 🥀"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("chat_screen_root")
    ) {
        if (userName.isEmpty()) {
            // Asking user's name layout with beautiful RTL layout
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "آراما",
                    tint = SagePrimary,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "به آراما خوش آمدید",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SageDeep
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "برای اینکه بتوانم در گفتگوها شما را صمیمانه به نام خودتان صدا بزنم، لطفاً نام خود را وارد کنید:",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 26.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = nameInputText,
                    onValueChange = { nameInputText = it },
                    placeholder = { Text("نام شما...") },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .testTag("name_input_field"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SagePrimary,
                        focusedLabelColor = SagePrimary
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (nameInputText.trim().isNotEmpty()) {
                            viewModel.setUserName(nameInputText.trim())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(50.dp)
                        .testTag("confirm_name_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SagePrimary)
                ) {
                    Text(
                        text = "شروع گفتگو",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        } else {
            // Welcome and Warning banner if conversation is empty
            if (visibleMessages.isEmpty()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = "آرامش",
                        tint = SagePrimary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "سلام $userName عزیز، من آراما هستم",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDeep
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "دستیار سلامت روان شما به زبان فارسی. هر صحبتی که اینجا انجام می‌دهید در حریم امن تلفن شما می‌ماند. چه موضوعی در ذهن شماست؟",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 26.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Render Icebreakers
                    Text(
                        text = "برای شروع می‌توانید یکی از موارد زیر را انتخاب کنید:",
                        style = MaterialTheme.typography.labelLarge.copy(color = SagePrimary),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        icebreakers.forEach { suggestion ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        viewModel.sendMessage(suggestion)
                                    }
                                    .testTag("icebreaker_${suggestion.take(5)}")
                            ) {
                                Text(
                                    text = suggestion,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            } else {
                // Chat history stream
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("chat_messages_list"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(visibleMessages) { message ->
                        ChatMessageBubble(message = message, onRetry = { viewModel.retryMessage(it) })
                    }

                    // Typing Indicator
                    if (isTyping) {
                        item {
                            ChatTypingIndicator()
                        }
                    }
                }
            }

            // Quick disclaimer text above input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "سلب مسئولیت",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "آراما دستیار هوشمند است و جایگزین درمان تخصصی روان‌پزشکی نمی‌شود.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            val chatLimitExceeded by viewModel.chatLimitExceeded.collectAsState()

            if (chatLimitExceeded) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.95f),
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .testTag("chat_limit_banner")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🚫 حد مجاز پیام‌های روزانه به پایان رسید",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "شما به عنوان کاربر طرح رایگان، امروز ۵ پیام فرستاده‌اید. برای باز کردن قفل پیام‌های نامحدود، هم‌اکنون به طرح طلایی بپیوندید.",
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.navigate("settings") },
                                colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("ارتقای آنی اشتراک 🌟", color = Color.White)
                            }
                            Button(
                                onClick = { viewModel.resetDailyLimitsSimulated() },
                                colors = ButtonDefaults.buttonColors(containerColor = SageDeep),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("ریست آزمایشی حد مجاز", color = Color.White)
                            }
                        }
                    }
                }
            } else {
                // Message input area
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputTexValue,
                        onValueChange = { inputTexValue = it },
                        placeholder = { Text("اینجا بنویسید...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_text_input"),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SagePrimary,
                            focusedLabelColor = SagePrimary
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (inputTexValue.trim().isNotEmpty()) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.sendMessage(inputTexValue)
                                inputTexValue = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(SagePrimary)
                            .testTag("send_message_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "ارسال پیام",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatEntity, onRetry: ((ChatEntity) -> Unit)? = null) {
    val isUser = message.sender == "user"
    val bubbleColor = if (message.isFailed) {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
    } else if (isUser) {
        SagePrimary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (message.isFailed) {
        MaterialTheme.colorScheme.onErrorContainer
    } else if (isUser) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val alignment = if (isUser) Alignment.Start else Alignment.End

    val corners = if (isUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 2.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 2.dp)
    }

    val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isUser) Arrangement.Start else Arrangement.End,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            if (isUser) {
                // User bubble (No avatar, aligned to right/Start)
                Column {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = bubbleColor,
                            contentColor = textColor
                        ),
                        shape = corners,
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                            Text(
                                text = message.text,
                                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                                textAlign = TextAlign.Start
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = timeString,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.sp,
                                    color = textColor.copy(alpha = 0.6f)
                                ),
                                textAlign = TextAlign.End,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }

                    if (message.isFailed && onRetry != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = "ارسال ناموفق بود.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Row(
                                modifier = Modifier
                                    .clickable { onRetry(message) }
                                    .padding(vertical = 2.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "تلاش مجدد",
                                    tint = SagePrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "تلاش مجدد",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SagePrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            } else {
                // AI message (Bubble first in RTL Row, then Avatar Box so it sits on the left outer edge)
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = bubbleColor,
                            contentColor = textColor
                        ),
                        shape = corners,
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                            Text(
                                text = message.text,
                                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                                textAlign = TextAlign.Start
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = timeString,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.sp,
                                    color = textColor.copy(alpha = 0.6f)
                                ),
                                textAlign = TextAlign.Start,
                                modifier = Modifier.align(Alignment.Start)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Beautiful AI Avatar with brand leaf (Spa) logo
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(SagePrimary.copy(alpha = 0.15f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = "آراما",
                        tint = SagePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatTypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    
    val dot1Scale by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2Scale by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, delayMillis = 150, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dot3Scale by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, delayMillis = 300, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    // Gentle breathing pulse for the Spa icon
    val iconPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = androidx.compose.animation.core.EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconPulse"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(0.85f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "آراما در حال نوشتن",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    
                    // Pulsing dots container
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .scale(dot1Scale)
                                .alpha(dot1Scale)
                                .background(SagePrimary, shape = CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .scale(dot2Scale)
                                .alpha(dot2Scale)
                                .background(SagePrimary, shape = CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .scale(dot3Scale)
                                .alpha(dot3Scale)
                                .background(SagePrimary, shape = CircleShape)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Soft breathing outer circle around the icon (avatar)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .scale(iconPulse)
                    .alpha((1.4f - iconPulse).coerceIn(0.1f, 0.8f))
                    .background(SagePrimary.copy(alpha = 0.15f), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = "آراما در حال نوشتن...",
                    tint = SagePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
