package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.MoodEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeeklyMoodChart(
    moods: List<MoodEntity>,
    modifier: Modifier = Modifier
) {
    // Standard weekly moods map
    val last7Moods = moods.take(7).reversed()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weekly_mood_chart_container"),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if (last7Moods.size >= 2) {
            // Summary Card (Insight)
            WeeklyMoodInsightCard(last7Moods)

            // Line Chart Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "نمودار نوسانات خلق و خو (۷ روز گذشته)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDeep,
                                fontSize = 15.sp
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "روند",
                            tint = SagePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    WeeklyMoodLineChart(last7Moods = last7Moods)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Chart Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(SagePrimary, shape = RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "احوال روحی",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Distribution Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "توزیع و درصد تکرار احساسات",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDeep,
                            fontSize = 15.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    WeeklyMoodDistributionBarChart(last7Moods = last7Moods)
                }
            }
        }
    }
}

@Composable
fun WeeklyMoodLineChart(
    last7Moods: List<MoodEntity>
) {
    // Mood values mapped to scale 1-5
    fun getMoodScore(emoji: String): Float {
        return when (emoji) {
            "😊" -> 5f // Great / عالی
            "🙂" -> 4f // Good / خوب
            "😐" -> 3f // Neutral / معمولی
            "😔" -> 2f // Bored/Tired / خسته
            "😢" -> 1f // Sad / غمگین
            "😡" -> 1f // Angry / عصبانی
            else -> 3f
        }
    }

    // Helper to get Persian weekday name
    fun getPersianDayName(dateString: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return try {
            val date = sdf.parse(dateString) ?: return dateString.takeLast(5)
            val calendar = Calendar.getInstance()
            calendar.time = date
            when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SATURDAY -> "شنبه"
                Calendar.SUNDAY -> "۱شنبه"
                Calendar.MONDAY -> "۲شنبه"
                Calendar.TUESDAY -> "۳شنبه"
                Calendar.WEDNESDAY -> "۴شنبه"
                Calendar.THURSDAY -> "۵شنبه"
                Calendar.FRIDAY -> "جمعه"
                else -> dateString.takeLast(5)
            }
        } catch (e: Exception) {
            dateString.takeLast(5)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 8.dp)
    ) {
        val strokeColor = SagePrimary
        val gradientColor = SageTintBg

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Draw horizontal grid lines and Y-axis markers (Emoji labels)
            val stepsY = 4
            val stepHeight = height / stepsY

            for (i in 0..stepsY) {
                val y = i * stepHeight
                drawLine(
                    color = SoftGrey.copy(alpha = 0.5f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 2f
                )
            }

            // 2. Draw Bezier Curve & Fill
            if (last7Moods.size >= 2) {
                val stepX = width / (last7Moods.size - 1)
                val points = last7Moods.mapIndexed { index, mood ->
                    val score = getMoodScore(mood.moodValue)
                    // Normalise score from [1..5] to ratio [0..1]
                    // 5 is at y = height * 0.1 (top), 1 is at y = height * 0.9 (bottom)
                    val normalizedScore = (score - 1f) / 4f
                    val x = index * stepX
                    val y = height - (normalizedScore * (height * 0.8f) + (height * 0.1f))
                    Offset(x, y)
                }

                // Create curved path using cubic bezier curves
                val curvePath = Path().apply {
                    if (points.isNotEmpty()) {
                        moveTo(points.first().x, points.first().y)
                        for (i in 0 until points.size - 1) {
                            val p0 = points[i]
                            val p1 = points[i + 1]
                            // Control points
                            val conX1 = p0.x + (p1.x - p0.x) / 2f
                            val conY1 = p0.y
                            val conX2 = p0.x + (p1.x - p0.x) / 2f
                            val conY2 = p1.y

                            cubicTo(
                                x1 = conX1, y1 = conY1,
                                x2 = conX2, y2 = conY2,
                                x3 = p1.x, y3 = p1.y
                            )
                        }
                    }
                }

                // Fill Path underneath
                val fillPath = Path().apply {
                    addPath(curvePath)
                    lineTo(points.last().x, height)
                    lineTo(points.first().x, height)
                    close()
                }

                // Draw filled gradient
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(gradientColor.copy(alpha = 0.45f), Color.Transparent),
                        startY = 0f,
                        endY = height
                    )
                )

                // Draw curved line
                drawPath(
                    path = curvePath,
                    color = strokeColor,
                    style = Stroke(
                        width = 4.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Draw node points
                points.forEach { point ->
                    // Outer circle glowing effect
                    drawCircle(
                        color = strokeColor.copy(alpha = 0.2f),
                        radius = 12.dp.toPx(),
                        center = point
                    )
                    // Inner node border
                    drawCircle(
                        color = strokeColor,
                        radius = 6.dp.toPx(),
                        center = point
                    )
                    // Inner core white node
                    drawCircle(
                        color = Color.White,
                        radius = 3.5.dp.toPx(),
                        center = point
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // X-Axis Labels (Day name & Emoji on top)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        last7Moods.forEach { mood ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = mood.moodValue,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                )
                Text(
                    text = getPersianDayName(mood.date),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = SageDeep.copy(alpha = 0.8f),
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

@Composable
fun WeeklyMoodDistributionBarChart(
    last7Moods: List<MoodEntity>
) {
    // Count occurrences
    val totalLogs = last7Moods.size.toFloat()
    val moodGroups = last7Moods.groupBy { it.moodValue }

    val allMoodTypes = listOf(
        Triple("😊", "عالی", Color(0xFF10B77F)),
        Triple("🙂", "خوب", Color(0xFF34D399)),
        Triple("😐", "معمولی", Color(0xFFFBBF24)),
        Triple("😔", "خسته", Color(0xFF60A5FA)),
        Triple("😢", "غمگین", Color(0xFFF87171)),
        Triple("😡", "عصبانی", Color(0xFFEF4444))
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        allMoodTypes.forEach { (emoji, label, color) ->
            val count = moodGroups[emoji]?.size ?: 0
            val fraction = if (totalLogs > 0) count / totalLogs else 0f
            
            // Animation for bar expansion
            val animatedWidth by animateFloatAsState(
                targetValue = fraction,
                animationSpec = tween(durationMillis = 800)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Label (Emoji + Label Name)
                Row(
                    modifier = Modifier.width(90.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = emoji, fontSize = 16.sp)
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDeep
                        )
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Progress Bar Track
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SoftGrey)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(if (animatedWidth > 0f) animatedWidth else 0.01f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(color)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Percentage/Count Indicator
                Text(
                    text = "$count بار (${(fraction * 100).toInt()}%)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SageDeep.copy(alpha = 0.7f),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.width(60.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
fun WeeklyMoodInsightCard(
    last7Moods: List<MoodEntity>
) {
    fun getMoodScore(emoji: String): Float {
        return when (emoji) {
            "😊" -> 5f
            "🙂" -> 4f
            "😐" -> 3f
            "😔" -> 2f
            "😢" -> 1f
            "😡" -> 1f
            else -> 3f
        }
    }

    val averageScore = last7Moods.map { getMoodScore(it.moodValue) }.average().toFloat()
    
    val averageLabel = when {
        averageScore >= 4.5f -> "بسیار عالی و با نشاط"
        averageScore >= 3.5f -> "خوب و پایدار"
        averageScore >= 2.5f -> "متوسط و معمولی"
        averageScore >= 1.5f -> "کمی کسل یا غمگین"
        else -> "پرتنش یا ناراحت"
    }

    val averageEmoji = when {
        averageScore >= 4.5f -> "😊"
        averageScore >= 3.5f -> "🙂"
        averageScore >= 2.5f -> "😐"
        averageScore >= 1.5f -> "😔"
        else -> "😢"
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = SageTintBg,
            contentColor = SageDeep
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.White, shape = RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = averageEmoji,
                    fontSize = 36.sp,
                    textAlign = TextAlign.Center
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "تحلیل خلاصه احوال این هفته",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SageDeep.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = averageLabel,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SageDeep,
                        fontSize = 18.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "میانگین نمره روحی شما ${String.format(Locale.ENGLISH, "%.1f", averageScore)} از ۵ است. ذهن شما در مسیر صلح و آرامش قرار دارد.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SageDeep.copy(alpha = 0.8f),
                        lineHeight = 18.sp
                    )
                )
            }
        }
    }
}
