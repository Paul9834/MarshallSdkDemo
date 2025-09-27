@file:OptIn(ExperimentalLayoutApi::class)

package com.sibel.demo

import android.content.res.Configuration
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.com.aratek.fp.FingerprintScanner
import kotlin.math.max


@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    minHeight: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    expanded: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val transition = updateTransition(targetState = expanded, label = "SectionCard")

    val elev by transition.animateDp(label = "elev") { if (it) 12.dp else 4.dp }
    val bg by transition.animateColor(label = "bg") {
        if (it) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
    }

    val shape = RoundedCornerShape(26.dp)
    val colors = CardDefaults.cardColors(containerColor = bg)
    val elevation = CardDefaults.cardElevation(defaultElevation = elev)
    val border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)

    val header: @Composable () -> Unit = {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            if (onClick != null) {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(if (expanded) 180f else 0f)
                )
            }
        }
    }

    if (onClick != null) {
        Card(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier,
            shape = shape,
            colors = colors,
            elevation = elevation,
            border = border
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 18.dp)
                    .heightIn(min = minHeight)
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                header()
                content()
            }
        }
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = colors,
            elevation = elevation,
            border = border
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 18.dp)
                    .heightIn(min = minHeight)
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                header()
                content()
            }
        }
    }
}




@Composable
private fun Chip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun StatusPill(online: Boolean, label: String) {
    val bg = if (online) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    else MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
    val fg = if (online) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.error

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            label,
            color = fg,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1
        )
    }
}


@Composable
private fun KeyValueRow(k: String, v: String, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(k, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(v, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun MetricBar(label: String, value: Int?, valueRange: IntRange, goodLowIsBetter: Boolean = false) {
    val v = value ?: 0
    val clamped = v.coerceIn(valueRange.first, valueRange.last)
    val norm = if (valueRange.last == valueRange.first) 0f else (clamped - valueRange.first).toFloat() / (valueRange.last - valueRange.first)
    val progress = if (goodLowIsBetter) 1f - norm else norm
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(if (value == null) "—" else v.toString(), style = MaterialTheme.typography.labelLarge)
        }
        LinearProgressIndicator(progress = { if (value == null) 0f else progress }, modifier = Modifier.fillMaxWidth().height(8.dp))
    }
}

@Composable
private fun TwoColumns(left: @Composable ColumnScope.() -> Unit, right: @Composable ColumnScope.() -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) { left() }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) { right() }
    }
}

@Composable
private fun BlockingLoadingDialog(
    visible: Boolean,
    title: String,
    subtitle: String,
    onCancel: (() -> Unit)? = null
) {
    if (!visible) return
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .widthIn(min = 360.dp)
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Text(title, style = MaterialTheme.typography.titleMedium)
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium)
                    onCancel?.let { TextButton(onClick = it) { Text("Cancelar") } }
                }
            }
        }
    }
}

@Composable
fun MainDashboard(vm: MainDashboardViewModel) {
    val s by vm.ui.collectAsState()
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scroll = rememberScrollState()

    fun timeLabel(v: Long?) = when { v == null || v < 0 -> "—"; v < 1 -> "<1ms"; else -> "${v}ms" }
    val lfdLabel = listOf("OFF" to FingerprintScanner.LFD_LEVEL_OFF, "LOW" to 1, "MID" to 2, "HIGH" to 3).firstOrNull { it.second == s.lfdLevel }?.first ?: "OFF"

    val headerRow = @Composable {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), modifier = Modifier.fillMaxWidth()) {
            Box(Modifier.weight(1f)) {
                SectionCard("Dispositivo", minHeight = 240.dp) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusPill(s.isFpOpen, if (s.isFpOpen) "FP activo" else "FP cerrado")
                        StatusPill(s.isQrOpen, if (s.isQrOpen) "QR activo" else "QR cerrado")
                    }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Chip("FP • FW: ${s.fpFw ?: "—"}"); Chip("SN: ${s.fpSn ?: "—"}"); Chip("Modelo: ${s.fpModel ?: "—"}")
                        Chip("QR • FW: ${s.qrFw ?: "—"}"); Chip("SN: ${s.qrSn ?: "—"}"); Chip("Bione: ${s.bioneVersion ?: "—"}")
                    }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        AssistChip(enabled = s.busy == BusyStateVM.IDLE, onClick = { if (s.isFpOpen) vm.closeFp() else vm.openFp() }, label = { Text("FP: ${s.fpStateLabel}") })
                        AssistChip(enabled = s.busy == BusyStateVM.IDLE, onClick = { if (s.isQrOpen) vm.closeQr() else vm.openQr() }, label = { Text("QR: ${s.qrStateLabel}") })
                        var expanded by remember { mutableStateOf(false) }
                        OutlinedButton(enabled = s.isFpOpen && s.busy == BusyStateVM.IDLE, onClick = { expanded = true }) { Text("🛡️ LFD: $lfdLabel") }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            listOf("OFF" to FingerprintScanner.LFD_LEVEL_OFF, "LOW" to 1, "MID" to 2, "HIGH" to 3).forEach { (label, value) ->
                                DropdownMenuItem(text = { Text(label) }, onClick = { expanded = false; vm.setLfd(value) })
                            }
                        }
                        Button(enabled = s.busy == BusyStateVM.IDLE, onClick = { vm.openFp(); vm.openQr() }) { Text("🔄 Actualizar info") }
                    }
                }
            }
            Box(Modifier.weight(1f)) {
                SectionCard("Resumen y métricas", minHeight = 240.dp) {
                    TwoColumns(
                        left = {
                            Text("Estados", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            KeyValueRow("🖐️ FP", s.fpResult ?: "—")
                            KeyValueRow("📷 QR", s.qrResult ?: "—")
                            KeyValueRow("🆔 Último ID", s.lastId?.toString() ?: "—")
                            KeyValueRow("🎯 Score", s.lastScore?.toString() ?: "—")
                        },
                        right = {
                            Text("Tiempos", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            KeyValueRow("⚡ Captura", timeLabel(s.captureTime))
                            KeyValueRow("🧬 Extracción", timeLabel(s.extractTime))
                            KeyValueRow("🧪 Template", timeLabel(s.generalizeTime))
                            KeyValueRow("🔎 Verificación", timeLabel(s.verifyTime))
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                    MetricBar(label = "Calidad (Bione)", value = s.quality, valueRange = 0..100)
                    Spacer(Modifier.height(6.dp))
                    MetricBar(label = "NFIQ (1=mejor)", value = s.nfiq?.let { max(1, it) }, valueRange = 1..5, goodLowIsBetter = true)
                    if (s.lastError != null) {
                        Spacer(Modifier.height(8.dp))
                        AssistChip(onClick = {}, label = { Text("⚠️ ${s.lastError}") }, colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.errorContainer, labelColor = MaterialTheme.colorScheme.onErrorContainer))
                    }
                }
            }
            Box(Modifier.weight(0.7f)) {
                SectionCard("Vista de huella", minHeight = 160.dp) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        if (s.fpBitmap != null) {
                            Image(
                                bitmap = s.fpBitmap!!.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth().height(160.dp),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Text("Sin imagen")
                        }
                    }
                }
            }
        }
    }

    val actionRow = @Composable {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), modifier = Modifier.fillMaxWidth()) {
            Box(Modifier.weight(1f)) {
                SectionCard("Lector QR", minHeight = 220.dp) {
                    Text(s.qrResult ?: if (s.isQrOpen) "Listo para escanear" else "Cerrado", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))
                    TwoColumns(
                        left = {
                            Text("Acciones", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Button(enabled = s.isQrOpen && s.busy == BusyStateVM.IDLE, onClick = { vm.runQr() }) { Text("🔍 Probar QR") }
                        },
                        right = {
                            Text("Detalles", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            KeyValueRow("FW", s.qrFw ?: "—")
                            KeyValueRow("Serial", s.qrSn ?: "—")
                        }
                    )
                }
            }
            Box(Modifier.weight(1f)) {
                SectionCard("🧬 Huella dactilar", minHeight = 220.dp) {
                    Text(s.fpResult ?: if (s.isFpOpen) "Listo para capturar" else "Cerrado", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (s.fpThumb != null) {
                            Image(s.fpThumb!!.asImageBitmap(), null, modifier = Modifier.size(96.dp).clip(RoundedCornerShape(12.dp)))
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            KeyValueRow("Tamaño", s.fpSize ?: "—")
                            KeyValueRow("LFD", lfdLabel)
                            KeyValueRow("Bione", s.bioneVersion ?: "—")
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(enabled = s.isFpOpen && s.busy == BusyStateVM.IDLE, onClick = { vm.runFp("show") }) { Text("👁️ Mostrar") }
                        Button(enabled = s.isFpOpen && s.busy == BusyStateVM.IDLE, onClick = { vm.runFp("enroll") }) { Text("➕ Enroll") }
                        Button(enabled = s.isFpOpen && s.busy == BusyStateVM.IDLE, onClick = { vm.runFp("verify") }) { Text("✅ Verify") }
                        Button(enabled = s.isFpOpen && s.busy == BusyStateVM.IDLE, onClick = { vm.runFp("identify") }) { Text("🧭 Identify") }
                        OutlinedButton(enabled = s.isFpOpen && s.busy == BusyStateVM.IDLE, onClick = { vm.clearFpDb() }) { Text("🧹 Limpiar DB") }
                    }
                }
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(if (isLandscape) 24.dp else 16.dp)
            .verticalScroll(scroll),
        verticalArrangement = Arrangement.spacedBy(if (isLandscape) 24.dp else 20.dp)
    ) {
        headerRow()
        actionRow()
    }

    val busy = s.busy
    val title = when (busy) {
        BusyStateVM.PREP_FP -> "🛠️ Preparando lector de huellas…"
        BusyStateVM.CAPTURE_FP -> "Capturando huella…"
        BusyStateVM.PREP_QR -> "⚙️ Preparando escáner de códigos…"
        BusyStateVM.SCAN_QR -> "🔎 Escaneando código…"
        BusyStateVM.IDLE -> ""
    }
    val subtitle = when (busy) {
        BusyStateVM.PREP_FP -> "Inicializando sensor y motor biométrico."
        BusyStateVM.CAPTURE_FP -> "Coloca el dedo y mantén presión."
        BusyStateVM.PREP_QR -> "Inicializando lector."
        BusyStateVM.SCAN_QR -> "Alinea el QR o código de barras."
        BusyStateVM.IDLE -> ""
    }
    val cancelHandler = when (busy) {
        BusyStateVM.CAPTURE_FP, BusyStateVM.SCAN_QR -> ({ vm.cancelCurrent() })
        else -> null
    }

    BlockingLoadingDialog(
        visible = busy != BusyStateVM.IDLE,
        title = title,
        subtitle = subtitle,
        onCancel = cancelHandler
    )
}
