package com.sibel.demo.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.com.aratek.fp.FingerprintScanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FancyTopBar(
    title: String,
    subtitle: String? = null,
    onRefresh: () -> Unit,
    onQuickLfd: (Int) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    var menuOpen by remember { mutableStateOf(false) }
    var lfdOpen by remember { mutableStateOf(false) }

    // Gradiente FERXXO (verde → violeta → verde)
    val gradient = Brush.horizontalGradient(
        listOf(
            BrandPrimary,                 // #008F39
            MaterialTheme.colorScheme.tertiary, // tu violeta
            BrandPrimary
        )
    )

    // Barra transparente porque pintamos el fondo con el gradiente
    val colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = Color.Transparent,
        scrolledContainerColor = Color.Transparent,
        titleContentColor = BrandOnPrimary,
        actionIconContentColor = BrandOnPrimary.copy(alpha = 0.92f),
        navigationIconContentColor = BrandOnPrimary.copy(alpha = 0.92f)
    )

    Column(
        modifier = Modifier
            .background(gradient)   // fondo de la zona superior
            .statusBarsPadding()
    ) {
        CenterAlignedTopAppBar(
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = BrandOnPrimary
                        )
                    )
                    if (!subtitle.isNullOrBlank()) {
                        // píldora de estado
                        AssistChip(
                            onClick = {},
                            label = { Text(subtitle) },
                            enabled = false,
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = BrandOnPrimary.copy(alpha = 0.18f),
                                labelColor = BrandOnPrimary
                            )
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = { lfdOpen = true }) {
                    Icon(Icons.Filled.Security, contentDescription = "Nivel LFD")
                }
                DropdownMenu(expanded = lfdOpen, onDismissRequest = { lfdOpen = false }) {
                    DropdownMenuItem(text = { Text("LFD: OFF") },
                        onClick = { lfdOpen = false; onQuickLfd(FingerprintScanner.LFD_LEVEL_OFF) })
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

        // Tira de acciones rápidas (chips) con color que “conecta” con tus cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FilterChip(
                selected = false,
                onClick = { onQuickLfd(FingerprintScanner.LFD_LEVEL_OFF) },
                label = { Text("LFD OFF") },
                leadingIcon = { Icon(Icons.Filled.Security, contentDescription = null) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = BrandOnPrimary.copy(alpha = 0.14f),
                    labelColor = BrandOnPrimary,
                    iconColor = BrandOnPrimary         // <- antes: leadingIconColor
                )
            )
            FilterChip(
                selected = false, onClick = { onQuickLfd(1) }, label = { Text("LOW") },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = BrandOnPrimary.copy(alpha = 0.14f),
                    labelColor = BrandOnPrimary
                )
            )
            FilterChip(
                selected = false, onClick = { onQuickLfd(2) }, label = { Text("MID") },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = BrandOnPrimary.copy(alpha = 0.14f),
                    labelColor = BrandOnPrimary
                )
            )
            FilterChip(
                selected = false, onClick = { onQuickLfd(3) }, label = { Text("HIGH") },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = BrandOnPrimary.copy(alpha = 0.14f),
                    labelColor = BrandOnPrimary
                )
            )
        }

        // “sombra” suave para que se funda con las cards
        Divider(
            thickness = 1.dp,
            color = BrandOnPrimary.copy(alpha = 0.10f)
        )
    }
}
