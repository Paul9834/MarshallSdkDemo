// AppTypographyLandscape.kt
package com.sibel.demo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.unit.sp

val AppTypographyLandscape: Typography = AppTypography.copy(
    // ejemplo: títulos un pelín más pequeños y cuerpo igual
    headlineLarge  = AppTypography.headlineLarge.copy(fontSize = 26.sp),
    headlineMedium = AppTypography.headlineMedium.copy(fontSize = 22.sp),
    headlineSmall  = AppTypography.headlineSmall.copy(fontSize = 20.sp)
)
