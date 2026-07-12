package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.theme.SageDeep
import com.example.ui.theme.SagePrimary
import com.example.ui.theme.SageTintBg
import com.example.ui.theme.SoftGrey

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: MainViewModel
) {
    val page by viewModel.onboardingPage.collectAsState()

    val title = when (page) {
        0 -> "به آراما خوش آمدید"
        1 -> "مکالمه ایمن و بی‌قضاوت"
        else -> "چک‌این حال روحی"
    }

    val description = when (page) {
        0 -> "همراه هوشمند و دلسوز شما در مسیر آرامش و بهبود سلامت روان. آراما همواره در کنار شماست تا با گوش دادن صمیمانه، خستگی‌های روزانه را تسکین دهد."
        1 -> "هر زمان مایل بودی گفتگو کن. به لطف هوش مصنوعی پیشرفته جمینای و حریم خصوصی پیش‌فرض، گفتگوی شما کاملاً محرمانه و در حافظه محلی تلفن شما نگهداری می‌شود."
        else -> "احوال روزانه خود را در چند ثانیه بسنجید. روند تغییرات روحی را در قالب یک نمودار صمیمی و آرامش‌بخش، بدون استرس و فشارهای ملامت‌گرانه دنبال کنید."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .testTag("onboarding_root"),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top skipping area
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            TextButton(
                onClick = { viewModel.onboardingSkip() },
                modifier = Modifier.testTag("skip_onboarding")
            ) {
                Text(
                    text = "رد کردن معرفی",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // Illustration / Banner Area (Custom vector drawing with canvas for highly stylized aesthetic)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
                .clip(RoundedCornerShape(24.dp))
                .background(SageTintBg)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            OnboardingArtwork(page = page)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Content Area (Transition animation)
        AnimatedContent(
            targetState = page,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(300))
            },
            modifier = Modifier.fillMaxWidth()
        ) { targetPage ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 26.sp,
                        color = SageDeep,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 28.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bottom Nav Area (Indicator dots + Primary Button)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Indicator dots
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0..2) {
                    val isSelected = i == page
                    val size = if (isSelected) 12.dp else 8.dp
                    val color = if (isSelected) SagePrimary else SoftGrey
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(size)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Button(
                onClick = { viewModel.onboardingNext() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("onboarding_next_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SagePrimary,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (page == 2) {
                        Text(
                            text = "شروع کنیم",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    } else {
                        Text(
                            text = "صفحه بعد",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // logical left in RTL
                            contentDescription = "بعدی",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingArtwork(page: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val translationY by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "translationY"
    )

    val drawableRes = when (page) {
        0 -> R.drawable.illustration_onboarding_leaf
        1 -> R.drawable.illustration_onboarding_chat
        else -> R.drawable.illustration_onboarding_chart
    }
    Image(
        painter = painterResource(id = drawableRes),
        contentDescription = "تصویر معرفی آراما",
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                this.translationY = translationY
            }
    )
}
