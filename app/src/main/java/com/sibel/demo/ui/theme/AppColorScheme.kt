// AppColorScheme.kt
package com.sibel.demo.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// ---------- Claro (mínimo compatible con tu Color.kt) ----------
val LightColors = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = BrandOnPrimary,
    primaryContainer = BrandPrimaryContainer,
    onPrimaryContainer = BrandOnPrimaryContainer,

    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,

    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,

    // Usa solo outline; (surfaceVariant/outlineVariant no son necesarios)
    outline = Outline
)

// ---------- Oscuro OLED (true black) ----------
val OledDarkColors = darkColorScheme(
    primary = BrandPrimary,
    onPrimary = BrandOnPrimary,
    primaryContainer = BrandPrimaryContainer,
    onPrimaryContainer = BrandOnPrimaryContainer,

    background = BackgroundOled,
    onBackground = OnBackgroundOled,
    surface = SurfaceOled,
    onSurface = OnSurfaceOled,

    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,

    outline = OutlineOled
)

object AppColorScheme {
    @Composable
    fun provide(
        darkTheme: Boolean = isSystemInDarkTheme(),
        trueBlack: Boolean = true,      // hoy no diferenciamos otros dark, pero lo dejamos por si lo amplías
        useDynamicColor: Boolean = true
    ): ColorScheme {
        val ctx = LocalContext.current
        if (useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        return if (darkTheme) OledDarkColors else LightColors
    }
}
