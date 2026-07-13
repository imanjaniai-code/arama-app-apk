package com.example.ui.theme

import android.content.Context
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.arama.app.R

// Custom Persian typography builder, 
// featuring generous vertical breathing room (increased line-height) to render RTL characters beautifully.
fun getTypography(fontFamily: FontFamily): Typography {
    return Typography(
        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 46.sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            lineHeight = 30.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 26.sp,
            letterSpacing = 0.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 28.sp, // Open line height for Persian diacritics
            letterSpacing = 0.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 24.sp, // Open line height for Persian diacritics
            letterSpacing = 0.sp
        ),
        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.sp
        )
    )
}

// Static fallback Typography (always safe)
val Typography = getTypography(FontFamily.Default)

// Safe dynamic typography loading with fallback
fun getSafeTypography(context: Context): Typography {
    val isLoadable = try {
        ResourcesCompat.getFont(context, R.font.yekanbakh_regular) != null &&
        ResourcesCompat.getFont(context, R.font.yekanbakh_bold) != null
    } catch (e: Throwable) {
        false
    }

    val fontFamily = if (isLoadable) {
        FontFamily(
            Font(R.font.yekanbakh_regular, FontWeight.Normal),
            Font(R.font.yekanbakh_regular, FontWeight.Medium),
            Font(R.font.yekanbakh_bold, FontWeight.Bold)
        )
    } else {
        FontFamily.Default
    }

    return getTypography(fontFamily)
}

