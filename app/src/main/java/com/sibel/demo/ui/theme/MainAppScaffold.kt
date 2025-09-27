
package com.sibel.demo.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.sibel.demo.MainDashboard
import com.sibel.demo.MainDashboardViewModel
import com.sibel.demo.ui.theme.BrandPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(vm: MainDashboardViewModel) {
    // Comportamiento de colapso al hacer scroll
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppTopBar(
                title = "Sibel Demo",
                subtitle = if (vm.ui.collectAsState().value.isFpOpen || vm.ui.collectAsState().value.isQrOpen)
                    "Dispositivos activos"
                else "Dispositivos inactivos",
                onRefresh = { vm.openFp(); vm.openQr() },
                onQuickLfd = { level -> vm.setLfd(level) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { inner ->
        // Contenido principal
        androidx.compose.foundation.layout.Box(Modifier.padding(inner)) {
            MainDashboard(vm)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(
    title: String,
    subtitle: String? = null,
    onRefresh: () -> Unit,
    onQuickLfd: (Int) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    var menuOpen by remember { mutableStateOf(false) }
    var lfdOpen by remember { mutableStateOf(false) }

    // Colores del AppBar con elevación al scrollear
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    // Usa CenterAligned para look moderno y compacto
    CenterAlignedTopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        // Acento sutil en el título
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        },
        navigationIcon = {
            // “Escudo” como acceso rápido a LFD
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
            IconButton(onClick = onRefresh) {
                Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
            }
            IconButton(onClick = { menuOpen = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "Más")
            }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                DropdownMenuItem(text = { Text("Tema: Sistema") }, onClick = { menuOpen = false })
                DropdownMenuItem(text = { Text("Ayuda") }, onClick = { menuOpen = false })
                DropdownMenuItem(text = { Text("Acerca de") }, onClick = { menuOpen = false })
            }
        },
        scrollBehavior = scrollBehavior,
        colors = colors
    )

    // Línea de acento sutil bajo el AppBar (opcional)
    Divider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)
    )
}
