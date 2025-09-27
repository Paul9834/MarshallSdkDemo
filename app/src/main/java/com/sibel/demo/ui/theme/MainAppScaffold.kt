// MainAppScaffold.kt (puede estar en ui.theme o donde lo tengas)
package com.sibel.demo.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.sibel.demo.MainDashboard
import com.sibel.demo.MainDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(vm: MainDashboardViewModel) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            val ui = vm.ui.collectAsState().value
            AppTopBarColored(
                title = "EPOC - Marshall 8 / Paul Montealegre Melo",
                subtitle = if (ui.isFpOpen || ui.isQrOpen) "Dispositivo activo" else "Dispositivo inactivo ",
                fpOpen = ui.isFpOpen,
                qrOpen = ui.isQrOpen,
                onRefresh = { vm.openFp(); vm.openQr() },
                onQuickLfd = { vm.setLfd(it) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { inner ->
        Box(Modifier.padding(inner)) {
            MainDashboard(vm)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBarColored(
    title: String,
    subtitle: String?,
    fpOpen: Boolean,
    qrOpen: Boolean,
    onRefresh: () -> Unit,
    onQuickLfd: (Int) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    var menuOpen by remember { mutableStateOf(false) }
    var lfdOpen by remember { mutableStateOf(false) }

    // Colores del AppBar que combinan con las tarjetas (primaryContainer)
    val colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
    )

    Column {
        CenterAlignedTopAppBar(
            title = {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                            )
                        )
                    }
                    // Chips de estado (combinan con el brand)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SmallPill(
                            text = if (fpOpen) "FP activo" else "FP cerrado",
                            bg = if (fpOpen)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            else
                                MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                            fg = if (fpOpen)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.error
                        )
                        SmallPill(
                            text = if (qrOpen) "QR activo" else "QR cerrado",
                            bg = if (qrOpen)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            else
                                MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                            fg = if (qrOpen)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = { lfdOpen = true }) {
                    Icon(Icons.Filled.Security, contentDescription = "Nivel LFD")
                }
                DropdownMenu(expanded = lfdOpen, onDismissRequest = { lfdOpen = false }) {
                    DropdownMenuItem(
                        text = { Text("LFD: OFF") },
                        onClick = { lfdOpen = false; onQuickLfd(cn.com.aratek.fp.FingerprintScanner.LFD_LEVEL_OFF) }
                    )
                    DropdownMenuItem(text = { Text("LFD: LOW") }, onClick = { lfdOpen = false; onQuickLfd(1) })
                    DropdownMenuItem(text = { Text("LFD: MID") }, onClick = { lfdOpen = false; onQuickLfd(2) })
                    DropdownMenuItem(text = { Text("LFD: HIGH") }, onClick = { lfdOpen = false; onQuickLfd(3) })
                }
            },
            actions = {
                IconButton(onClick = onRefresh) { Icon(Icons.Filled.Refresh, contentDescription = "Actualizar") }
                IconButton(onClick = { menuOpen = true }) { Icon(Icons.Filled.MoreVert, contentDescription = "Más") }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(text = { Text("Tema: Sistema") }, onClick = { menuOpen = false })
                    DropdownMenuItem(text = { Text("Ayuda") }, onClick = { menuOpen = false })
                    DropdownMenuItem(text = { Text("Acerca de") }, onClick = { menuOpen = false })
                }
            },
            scrollBehavior = scrollBehavior,
            colors = colors
        )

        // Acento inferior: línea degradada que “une” con las cards
        Box(
            Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary,
                            MaterialTheme.colorScheme.primary
                        )
                    )
                )
        )
    }
}

@Composable
private fun SmallPill(text: String, bg: Color, fg: Color) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelMedium, color = fg)
    }
}
