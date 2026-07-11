package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DarkColorScheme =
  darkColorScheme(
    primary = SagePrimaryDark,
    secondary = SageDeepDark,
    tertiary = GreigeDark,
    background = MainDarkBg,
    surface = SurfaceDark,
    onPrimary = MainDarkBg,
    onSecondary = MainDarkBg,
    onBackground = Color(0xFFECEAE4),
    onSurface = Color(0xFFECEAE4),
    outline = Color(0xFF4A5046)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = SageDeep,
    secondary = SagePrimary,
    tertiary = Greige,
    background = MainLightBg,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Charcoal,
    onSurface = Charcoal,
    outline = GreigeLight,
    outlineVariant = Greige
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic colors by default so our custom emerald theme shines consistently
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val context = LocalContext.current
  val typography = androidx.compose.runtime.remember(context) { getSafeTypography(context) }

  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  // Force LayoutDirection.Rtl globally for Persian UI support
  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    MaterialTheme(colorScheme = colorScheme, typography = typography, content = content)
  }
}
