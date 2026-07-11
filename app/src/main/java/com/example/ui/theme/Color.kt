package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// Earthy Sage Green & Greige Theme Palette
// (Low-saturation, calm, therapeutic-zen)
// ==========================================

// --- Light Mode Palette ---
val SageDeep = Color(0xFF5A6B52)      // Main anchor, header, prominent cards, buttons on light bg
val SagePrimary = Color(0xFF7C9070)   // Active/selected state, icons, mid-level indicators
val Greige = Color(0xFFA99C8D)        // Neutral warm, text highlight on dark background, secondary detail on light bg
val SageTintBg = Color(0xFFDDE3D6)    // Soft background for cards/secondary sections
val GreigeTintBg = Color(0xFFEDE9E1)  // Soft background for cards/secondary sections
val GreigeLight = Color(0xFFD9D2C4)   // Delicate borders & dividers
val MainLightBg = Color(0xFFFBF9F5)   // Main screen light background (very light warm cream)

// --- Dark Mode Adjusted Palette ---
// Sage is slightly lighter/softer, Greige is warmer for contrast & readability (WCAG AA compliant)
val SageDeepDark = Color(0xFF7E9275)
val SagePrimaryDark = Color(0xFF98AA8E)
val GreigeDark = Color(0xFFC7BCAE)
val SageTintBgDark = Color(0xFF2C3527)
val GreigeTintBgDark = Color(0xFF3B352E)
val MainDarkBg = Color(0xFF1E221B)     // Deep earthy forest charcoal background
val SurfaceDark = Color(0xFF292E25)    // Slightly lighter than background for cards

// --- Status/State Colors (Low-saturation, earthy) ---
val EarthySuccess = Color(0xFF708264)  // Soft sage-tinted success green
val EarthyWarning = Color(0xFFC4A484)  // Soft ochre/warm warning
val EarthyError = Color(0xFFB87D7A)    // Soft terracotta error red
val EarthyRedBg = Color(0xFFF7ECEB)    // Very soft desaturated red bg for errors/crisis

// --- Backward Compatibility Aliases & Helpers ---
// This guarantees we don't break existing layouts and automatically upgrades them to the new palette.
val EmeraldPrimary = SagePrimary
val EmeraldDark = SageDeep
val EmeraldMedium = SagePrimary
val EmeraldLightBg = MainLightBg
val MintGreen = SageTintBg
val SageGreen = SagePrimary

// Neutral / Structural Colors
val WarmWhite = Color(0xFFFBF9F5)
val Charcoal = Color(0xFF2B2D29)       // Deep earthy charcoal instead of cold blue-gray
val SlateGrey = Color(0xFF6E726A)      // Earthy warm slate gray
val SoftGrey = GreigeTintBg

// Crisis/Emergency mapping
val CrisisRed = EarthyError
val CrisisRedBg = EarthyRedBg
