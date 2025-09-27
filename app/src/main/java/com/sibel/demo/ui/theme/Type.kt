package com.sibel.demo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.sibel.demo.R

// Usa TODAS las variantes que ya están en res/font/
val AppFontFamily = FontFamily(
    Font(R.font.poppins_thin,             FontWeight.Thin,       FontStyle.Normal),
    Font(R.font.poppins_thinitalic,       FontWeight.Thin,       FontStyle.Italic),
    Font(R.font.poppins_extralight,       FontWeight.ExtraLight, FontStyle.Normal),
    Font(R.font.poppins_extralightitalic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(R.font.poppins_light,            FontWeight.Light,      FontStyle.Normal),
    Font(R.font.poppins_lightitalic,      FontWeight.Light,      FontStyle.Italic),
    Font(R.font.poppins_regular,          FontWeight.Normal,     FontStyle.Normal),
    Font(R.font.poppins_italic,           FontWeight.Normal,     FontStyle.Italic),
    Font(R.font.poppins_medium,           FontWeight.Medium,     FontStyle.Normal),
    Font(R.font.poppins_mediumitalic,     FontWeight.Medium,     FontStyle.Italic),
    Font(R.font.poppins_semibold,         FontWeight.SemiBold,   FontStyle.Normal),
    Font(R.font.poppins_semibolditalic,   FontWeight.SemiBold,   FontStyle.Italic),
    Font(R.font.poppins_bold,             FontWeight.Bold,       FontStyle.Normal),
    Font(R.font.poppins_bolditalic,       FontWeight.Bold,       FontStyle.Italic),
    Font(R.font.poppins_extrabold,        FontWeight.ExtraBold,  FontStyle.Normal),
    Font(R.font.poppins_extrabolditalic,  FontWeight.ExtraBold,  FontStyle.Italic),
    Font(R.font.poppins_black,            FontWeight.Black,      FontStyle.Normal),
    Font(R.font.poppins_blackitalic,      FontWeight.Black,      FontStyle.Italic),
)

val AppTypography = Typography(
    displayLarge   = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Black),
    displayMedium  = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.ExtraBold),
    displaySmall   = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Bold),

    headlineLarge  = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold),
    headlineMedium = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold),
    headlineSmall  = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium),

    titleLarge     = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold),
    titleMedium    = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium),
    titleSmall     = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium),

    bodyLarge      = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Normal),
    bodyMedium     = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Normal),
    bodySmall      = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Light),

    labelLarge     = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold),
    labelMedium    = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium),
    labelSmall     = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium),
)
