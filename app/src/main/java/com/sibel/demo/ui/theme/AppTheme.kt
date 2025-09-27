import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
import com.sibel.demo.ui.theme.AppTypography
import com.sibel.demo.ui.theme.OledDarkColors
import com.sibel.demo.ui.theme.LightColors

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val scheme = if (darkTheme) OledDarkColors else LightColors
    MaterialTheme(
        colorScheme = scheme,
        typography  = AppTypography, // tus Poppins
        // shapes   = AppShapes, // opcional
        content     = content
    )
}