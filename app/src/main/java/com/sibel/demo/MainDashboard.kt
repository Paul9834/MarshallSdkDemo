package com.sibel.demo

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.com.aratek.fp.FingerprintScanner
import kotlin.math.max

private val LFD_OPTIONS = listOf(
    "OFF" to FingerprintScanner.LFD_LEVEL_OFF,
    "LOW" to 1,
    "MID" to 2,
    "HIGH" to 3
)

private fun timeLabel(v: Long?) = when {
    v == null || v < 0 -> "—"
    v < 1 -> "<1ms"
    else -> "${v}ms"
}

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
    val shape = RoundedCornerShape(20.dp)
    val colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    val elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    val border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    val header: @Composable () -> Unit = {
        Row(
            Modifier.fillMaxWidth().padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
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
        Card(onClick = onClick, enabled = enabled, modifier = modifier, shape = shape, colors = colors, elevation = elevation, border = border) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp).heightIn(min = minHeight),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) { header(); content() }
        }
    } else {
        Card(modifier = modifier, shape = shape, colors = colors, elevation = elevation, border = border) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp).heightIn(min = minHeight),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) { header(); content() }
        }
    }
}

@Composable
private fun Chip(text: String) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(50)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)).padding(horizontal = 12.dp, vertical = 6.dp)
    ) { Text(text, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge) }
}

@Composable
private fun StatusPill(online: Boolean, label: String) {
    val bg = if (online) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
    val fg = if (online) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    Box(
        modifier = Modifier.clip(RoundedCornerShape(24.dp)).background(bg).padding(horizontal = 10.dp, vertical = 6.dp)
    ) { Text(label, color = fg, style = MaterialTheme.typography.labelLarge, maxLines = 1) }
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
    val raw = value ?: 0
    val clamped = raw.coerceIn(valueRange.first, valueRange.last)
    val norm = if (valueRange.last == valueRange.first) 0f else (clamped - valueRange.first).toFloat() / (valueRange.last - valueRange.first)
    val progress = if (goodLowIsBetter) 1f - norm else norm
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(if (value == null) "—" else raw.toString(), style = MaterialTheme.typography.labelLarge)
        }
        LinearProgressIndicator(progress = { if (value == null) 0f else progress }, modifier = Modifier.fillMaxWidth().height(8.dp))
    }
}

@Composable
private fun TwoColumns(left: @Composable ColumnScope.() -> Unit, right: @Composable ColumnScope.() -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) { left() }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) { right() }
    }
}

@Composable
private fun StatusRow(icon: ImageVector, text: String, tone: Color = MaterialTheme.colorScheme.primary) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, null, tint = tone)
        Text(text, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
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
        properties = DialogProperties(usePlatformDefaultWidth = true, dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.widthIn(min = 280.dp).padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DeviceInfoChips(
    fpFw: String?,
    fpSn: String?,
    fpModel: String?,
    qrFw: String?,
    qrSn: String?,
    bioneVersion: String?
) {
    val chipData = remember(fpFw, fpSn, fpModel, qrFw, qrSn, bioneVersion) {
        listOf(
            "fp_fw" to "FP • FW: ${fpFw ?: "—"}",
            "fp_sn" to "SN: ${fpSn ?: "—"}",
            "fp_model" to "Modelo: ${fpModel ?: "—"}",
            "qr_fw" to "QR • FW: ${qrFw ?: "—"}",
            "qr_sn" to "SN: ${qrSn ?: "—"}",
            "bione" to "Bione: ${bioneVersion ?: "—"}"
        )
    }

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        chipData.forEach { (key, text) ->
            key(key) {
                Chip(text)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActionChipsRow(
    isFpOpen: Boolean,
    isQrOpen: Boolean,
    busy: BusyStateVM,
    lfdLabel: String,
    fpStateLabel: String,
    qrStateLabel: String,
    onFpToggle: () -> Unit,
    onQrToggle: () -> Unit,
    onSetLfd: (Int) -> Unit,
    onRefreshInfo: () -> Unit
) {
    val isIdle = busy == BusyStateVM.IDLE

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AssistChip(
            enabled = isIdle,
            onClick = onFpToggle,
            label = { Text("FP: $fpStateLabel") }
        )

        AssistChip(
            enabled = isIdle,
            onClick = onQrToggle,
            label = { Text("QR: $qrStateLabel") }
        )

        var expanded by remember { mutableStateOf(false) }
        OutlinedButton(
            enabled = isFpOpen && isIdle,
            onClick = { expanded = true }
        ) {
            Icon(Icons.Filled.Security, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("LFD: $lfdLabel")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            LFD_OPTIONS.forEach { (label, value) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        expanded = false
                        onSetLfd(value)
                    }
                )
            }
        }

        Button(
            enabled = isIdle,
            onClick = onRefreshInfo
        ) {
            Icon(Icons.Filled.Refresh, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("Actualizar info")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FingerprintActionsRow(
    isFpOpen: Boolean,
    busy: BusyStateVM,
    onRunFpShow: () -> Unit,
    onRunFpEnroll: () -> Unit,
    onRunFpVerify: () -> Unit,
    onRunFpIdentify: () -> Unit,
    onClearFpDb: () -> Unit
) {
    val isIdle = isFpOpen && busy == BusyStateVM.IDLE

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(enabled = isIdle, onClick = onRunFpShow) {
            Icon(Icons.Filled.Visibility, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("Mostrar")
        }
        Button(enabled = isIdle, onClick = onRunFpEnroll) {
            Icon(Icons.Filled.PersonAdd, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("Enroll")
        }
        Button(enabled = isIdle, onClick = onRunFpVerify) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("Verify")
        }
        Button(enabled = isIdle, onClick = onRunFpIdentify) {
            Icon(Icons.Filled.TravelExplore, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("Identify")
        }
        OutlinedButton(enabled = isIdle, onClick = onClearFpDb) {
            Icon(Icons.Filled.CleaningServices, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("Limpiar DB")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun MainDashboard(vm: MainDashboardViewModel) {
    val s by vm.ui.collectAsState()
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val lfdLabel by remember {
        derivedStateOf {
            LFD_OPTIONS.firstOrNull { it.second == s.lfdLevel }?.first ?: "OFF"
        }
    }

    val fpImage by remember {
        derivedStateOf { s.fpBitmap?.asImageBitmap() }
    }

    val fpThumbImage by remember {
        derivedStateOf { s.fpThumb?.asImageBitmap() }
    }

    val onFpToggle = remember(vm) {
        { if (s.isFpOpen) vm.closeFp() else vm.openFp() }
    }
    val onQrToggle = remember(vm) {
        { if (s.isQrOpen) vm.closeQr() else vm.openQr() }
    }
    val onSetLfd = remember(vm) { { value: Int -> vm.setLfd(value) } }
    val onRefreshInfo = remember(vm) { { vm.openFp(); vm.openQr() } }
    val onRunFpShow = remember(vm) { { vm.runFp("show") } }
    val onRunFpEnroll = remember(vm) { { vm.runFp("enroll") } }
    val onRunFpVerify = remember(vm) { { vm.runFp("verify") } }
    val onRunFpIdentify = remember(vm) { { vm.runFp("identify") } }
    val onClearFpDb = remember(vm) { { vm.clearFpDb() } }
    val onRunQr = remember(vm) { { vm.runQr() } }
    val onCancelCurrent = remember(vm) { { vm.cancelCurrent() } }

    val headerRow = @Composable {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), modifier = Modifier.fillMaxWidth()) {
            Box(Modifier.weight(1f)) {
                SectionCard("Dispositivo", minHeight = 200.dp) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusPill(s.isFpOpen, if (s.isFpOpen) "FP activo" else "FP cerrado")
                        StatusPill(s.isQrOpen, if (s.isQrOpen) "QR activo" else "QR cerrado")
                    }

                    DeviceInfoChips(
                        fpFw = s.fpFw,
                        fpSn = s.fpSn,
                        fpModel = s.fpModel,
                        qrFw = s.qrFw,
                        qrSn = s.qrSn,
                        bioneVersion = s.bioneVersion
                    )

                    ActionChipsRow(
                        isFpOpen = s.isFpOpen,
                        isQrOpen = s.isQrOpen,
                        busy = s.busy,
                        lfdLabel = lfdLabel,
                        fpStateLabel = s.fpStateLabel,
                        qrStateLabel = s.qrStateLabel,
                        onFpToggle = onFpToggle,
                        onQrToggle = onQrToggle,
                        onSetLfd = onSetLfd,
                        onRefreshInfo = onRefreshInfo
                    )
                }
            }
            Box(Modifier.weight(1f)) {
                SectionCard("Resumen y métricas", minHeight = 200.dp) {
                    TwoColumns(
                        left = {
                            Text("Estados", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            val captured = fpImage != null
                            StatusRow(icon = if (captured) Icons.Filled.CheckCircle else Icons.Filled.Visibility, text = if (captured) "Capturada" else "Sin captura")
                            Spacer(Modifier.height(8.dp))
                            InfoRow("Tamaño", s.fpSize ?: "—")
                            InfoRow("Calidad (Bione)", s.quality?.toString() ?: "—")
                            InfoRow("NFIQ (1=mejor)", s.nfiq?.toString() ?: "—")
                            InfoRow("Cap", timeLabel(s.captureTime))
                            Spacer(Modifier.height(12.dp))
                            Text("Estados rápidos", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            InfoRow("FP", s.fpResult ?: "—")
                            InfoRow("QR", s.qrResult ?: "—")
                            InfoRow("Último ID", s.lastId?.toString() ?: "—")
                            InfoRow("Score", s.lastScore?.toString() ?: "—")
                        },
                        right = {
                            Text("Tiempos", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            KeyValueRow("Captura", timeLabel(s.captureTime))
                            KeyValueRow("Extracción", timeLabel(s.extractTime))
                            KeyValueRow("Template", timeLabel(s.generalizeTime))
                            KeyValueRow("Verificación", timeLabel(s.verifyTime))
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                    MetricBar(label = "Calidad (Bione)", value = s.quality, valueRange = 0..100)
                    Spacer(Modifier.height(6.dp))
                    MetricBar(label = "NFIQ (1=mejor)", value = s.nfiq?.let { max(1, it) }, valueRange = 1..5, goodLowIsBetter = true)
                    if (s.lastError != null) {
                        AssistChip(
                            onClick = {},
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Filled.Error, contentDescription = null)
                                    Text(s.lastError ?: "")
                                }
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                labelColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        )
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
                        val img = fpImage
                        if (img != null) {
                            Image(
                                bitmap = img,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp),
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
                SectionCard("Lector QR", minHeight = 200.dp) {
                    Text(s.qrResult ?: if (s.isQrOpen) "Listo para escanear" else "Cerrado", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))
                    TwoColumns(
                        left = {
                            Text("Acciones", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Button(enabled = s.isQrOpen && s.busy == BusyStateVM.IDLE, onClick = onRunQr) {
                                Icon(Icons.Filled.QrCodeScanner, contentDescription = null)
                                Spacer(Modifier.size(8.dp))
                                Text("Probar QR")
                            }
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
                SectionCard("Huella dactilar", minHeight = 200.dp) {
                    Text(s.fpResult ?: if (s.isFpOpen) "Listo para capturar" else "Cerrado", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val thumbImg = fpThumbImage
                        if (thumbImg != null) {
                            Image(thumbImg, null, modifier = Modifier.size(96.dp).clip(RoundedCornerShape(12.dp)))
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            KeyValueRow("Tamaño", s.fpSize ?: "—")
                            KeyValueRow("LFD", lfdLabel)
                            KeyValueRow("Bione", s.bioneVersion ?: "—")
                        }
                    }
                    Spacer(Modifier.height(12.dp))

                    FingerprintActionsRow(
                        isFpOpen = s.isFpOpen,
                        busy = s.busy,
                        onRunFpShow = onRunFpShow,
                        onRunFpEnroll = onRunFpEnroll,
                        onRunFpVerify = onRunFpVerify,
                        onRunFpIdentify = onRunFpIdentify,
                        onClearFpDb = onClearFpDb
                    )
                }
            }
        }
    }

    val listState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(if (isLandscape) 24.dp else 16.dp),
        verticalArrangement = Arrangement.spacedBy(if (isLandscape) 24.dp else 20.dp)
    ) {
        item(key = "header") { headerRow() }
        item(key = "action") { actionRow() }
    }

    val (title, subtitle, cancelHandler) = remember(s.busy) {
        when (s.busy) {
            BusyStateVM.PREP_FP -> Triple(
                "Preparando lector de huellas…",
                "Inicializando sensor y motor biométrico.",
                null
            )
            BusyStateVM.CAPTURE_FP -> Triple(
                "Capturando huella…",
                "Coloca el dedo y mantén presión.",
                onCancelCurrent
            )
            BusyStateVM.PREP_QR -> Triple(
                "Preparando escáner de códigos…",
                "Inicializando lector.",
                null
            )
            BusyStateVM.SCAN_QR -> Triple(
                "Escaneando código…",
                "Alinea el QR o código de barras.",
                onCancelCurrent
            )
            BusyStateVM.IDLE -> Triple("", "", null)
        }
    }

    BlockingLoadingDialog(
        visible = s.busy != BusyStateVM.IDLE,
        title = title,
        subtitle = subtitle,
        onCancel = cancelHandler
    )
}