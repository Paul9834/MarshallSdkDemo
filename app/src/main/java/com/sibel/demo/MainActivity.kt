package com.sibel.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import com.sibel.demo.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    private val vm: MainDashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Carga de settings del terminal (si falla, no crashea)
        runCatching {
            assets.open("terminal.xml").use { cn.com.aratek.dev.Terminal.loadSettings(it) }
        }

        setContent {
            AppTheme {
                MainDashboard(vm)
            }
        }
    }
}
// Sugerencia: mueve esto a Motion.kt
object Motion {
    fun fadeThrough() =
        fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow)) togetherWith
                fadeOut(animationSpec = spring(stiffness = Spring.StiffnessVeryLow))

    fun scaleInDialog() = scaleIn(
        initialScale = 5.96f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    ) + fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow))

    fun scaleOutDialog() = scaleOut(
        targetScale = 5.96f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    ) + fadeOut(animationSpec = spring(stiffness = Spring.StiffnessVeryLow))
}