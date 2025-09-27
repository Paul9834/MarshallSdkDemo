// AppTheme.kt
package com.sibel.demo.ui.theme

import android.os.Build
import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.unit.dp

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    useDynamicColor: Boolean = true,
    useTrueBlackDark: Boolean = true, // ya lo usas para OLED
    content: @Composable () -> Unit
) {
    val ctx = LocalContext.current
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    // 1) Paleta base (la que ya definiste: LightColors / OledDarkColors o dinámica)
    val baseScheme =
        if (useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        } else {
            if (darkTheme) OledDarkColors else LightColors
        }

    // 2) Ajustes SOLO para landscape (puedes tunear lo que quieras aquí)
    val colorScheme =
        if (isLandscape) {
            // Ejemplo: un poco más de contraste y superficies más “planas” en landscape
            baseScheme.copy(
                // mantén tus verdes “Feid”
                primary = BrandPrimary,
                // en landscape usamos más “negro/flat” de fondo-superficie
                background = baseScheme.background,
                surface = baseScheme.background,
                surfaceVariant = baseScheme.surface
            )
        } else {
            baseScheme
        }

    // 3) Tipografía opcional para landscape (más compacta). Si no quieres cambios, usa AppTypography en ambos.
    val typography =
        if (isLandscape) AppTypographyLandscape else AppTypography

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = colorScheme.background,
            contentColor = colorScheme.onBackground,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            content()
        }
    }
}
