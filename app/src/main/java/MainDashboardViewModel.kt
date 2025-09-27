package com.sibel.demo

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cn.com.aratek.fp.Bione
import cn.com.aratek.fp.FingerprintImage
import cn.com.aratek.fp.FingerprintScanner
import cn.com.aratek.qrc.CodeScanner
import cn.com.aratek.util.Result as AraResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

enum class BusyStateVM { PREP_FP, CAPTURE_FP, PREP_QR, SCAN_QR, IDLE }

data class DashState(
    val isFpOpen: Boolean = false,
    val isQrOpen: Boolean = false,
    val busy: BusyStateVM = BusyStateVM.IDLE,
    val cancelQr: Boolean = false,
    val cancelFp: Boolean = false,
    val qrResult: String? = null,
    val fpResult: String? = null,
    val fpBitmap: Bitmap? = null,
    val fpThumb: Bitmap? = null,
    val fpFw: String? = null,
    val fpSn: String? = null,
    val fpModel: String? = null,
    val qrFw: String? = null,
    val qrSn: String? = null,
    val bioneVersion: String? = null,
    val captureTime: Long? = null,
    val extractTime: Long? = null,
    val generalizeTime: Long? = null,
    val verifyTime: Long? = null,
    val nfiq: Int? = null,
    val quality: Int? = null,
    val fpSize: String? = null,
    val lastId: Int? = null,
    val lastScore: Int? = null,
    val lastError: String? = null,
    val fpStateLabel: String = "Cerrado",
    val qrStateLabel: String = "Cerrado",
    val lfdLevel: Int = FingerprintScanner.LFD_LEVEL_OFF
)

class MainDashboardViewModel(app: Application) : AndroidViewModel(app) {

    private val _ui = MutableStateFlow(DashState())
    val ui: StateFlow<DashState> = _ui

    private val ctx get() = getApplication<Application>()

    private fun setBusy(b: BusyStateVM) { _ui.value = _ui.value.copy(busy = b) }

    fun setLfd(level: Int) { _ui.value = _ui.value.copy(lfdLevel = level) }

    fun openFp() {
        if (_ui.value.isFpOpen || _ui.value.busy != BusyStateVM.IDLE) return
        setBusy(BusyStateVM.PREP_FP)
        _ui.value = _ui.value.copy(fpStateLabel = "Preparando…", lastError = null)
        viewModelScope.launch(Dispatchers.IO) {
            val fp = FingerprintScanner.getInstance(ctx)
            fp.powerOn()
            val err = fp.open()
            if (err == FingerprintScanner.RESULT_OK) {
                runCatching { fp.setLfdLevel(_ui.value.lfdLevel) }
                val dbPath = File(ctx.filesDir, "fp.db").absolutePath
                val init = Bione.initialize(ctx, dbPath)
                var lastErr: String? = null
                if (init != Bione.RESULT_OK) lastErr = "Bione.init err=$init"
                val fw = (fp.javaClass.getMethod("getFirmwareVersion").invoke(fp) as? AraResult)?.data as? String
                val sn = (fp.javaClass.getMethod("getSerial").invoke(fp) as? AraResult)?.data as? String
                val model = (fp.javaClass.getMethod("getModel").invoke(fp) as? AraResult)?.data?.toString()
                val ver = runCatching { Bione.getVersion().toString() }.getOrNull()
                _ui.value = _ui.value.copy(
                    isFpOpen = true,
                    fpStateLabel = "Listo",
                    fpFw = fw, fpSn = sn, fpModel = model, bioneVersion = ver,
                    lastError = lastErr
                )
            } else {
                fp.powerOff()
                _ui.value = _ui.value.copy(isFpOpen = false, fpStateLabel = "Cerrado", lastError = "FP open() err=$err")
            }
            setBusy(BusyStateVM.IDLE)
        }
    }

    fun closeFp() {
        if (!_ui.value.isFpOpen || _ui.value.busy != BusyStateVM.IDLE) return
        setBusy(BusyStateVM.PREP_FP)
        _ui.value = _ui.value.copy(fpStateLabel = "Cerrando…")
        viewModelScope.launch(Dispatchers.IO) {
            val fp = FingerprintScanner.getInstance(ctx)
            runCatching { Bione.exit() }
            runCatching { fp.close() }
            runCatching { fp.powerOff() }
            _ui.value = _ui.value.copy(isFpOpen = false, fpStateLabel = "Cerrado")
            setBusy(BusyStateVM.IDLE)
        }
    }

    private fun ms(v: Long?): String = when {
        v == null || v < 0 -> "—"
        v < 1 -> "<1 ms"
        else -> "${v} ms"
    }

    private fun sizeStr(fi: FingerprintImage): String = "${fi.width}×${fi.height}"

    fun runFp(mode: String) {
        if (!_ui.value.isFpOpen || _ui.value.busy != BusyStateVM.IDLE) return
        setBusy(BusyStateVM.CAPTURE_FP)
        _ui.value = _ui.value.copy(
            cancelFp = false,
            fpStateLabel = "Capturando…",
            lastError = null,
            captureTime = null,
            extractTime = null,
            generalizeTime = null,
            verifyTime = null,
            fpResult = "🖐️ Listo para capturar…"
        )
        viewModelScope.launch(Dispatchers.IO) {
            val fp = FingerprintScanner.getInstance(ctx)
            var fi: FingerprintImage? = null
            try {
                fp.prepare()
                while (!_ui.value.cancelFp) {
                    val t0 = System.currentTimeMillis()
                    val res = (fp.javaClass.methods.firstOrNull { it.name == "capture" && it.parameterTypes.size == 1 }?.invoke(fp, 800)
                        ?: fp.capture()) as AraResult
                    val cap = System.currentTimeMillis() - t0
                    _ui.value = _ui.value.copy(captureTime = cap)

                    if (res.error == FingerprintScanner.NO_FINGER || res.error == FingerprintScanner.TIMEOUT) continue
                    if (res.error == FingerprintScanner.FAKE_FINGER) { _ui.value = _ui.value.copy(fpResult = "⚠️ Dedo falso detectado"); break }
                    if (res.error != FingerprintScanner.RESULT_OK) { _ui.value = _ui.value.copy(fpResult = "❌ Error de captura (${res.error})"); break }
                    fi = res.data as? FingerprintImage
                    break
                }
                fp.finish()

                if (_ui.value.cancelFp) { _ui.value = _ui.value.copy(fpResult = "⛔ Captura cancelada"); return@launch }
                if (fi == null) { _ui.value = _ui.value.copy(fpResult = "❌ Sin imagen de huella"); return@launch }

                val bmpBytes = fi.convert2Bmp()
                val bmp = bmpBytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
                val thumb = bmp?.let {
                    val w = 160
                    val h = (w.toFloat() * it.height / it.width).toInt().coerceAtLeast(1)
                    Bitmap.createScaledBitmap(it, w, h, true)
                }

                val nfiqVal = runCatching { Bione.getFingerprintNfiqQuality(fi) }.getOrNull()?.takeIf { it >= 0 }
                val qaVal = runCatching { Bione.getFingerprintQuality(fi) }.getOrNull()
                val size = sizeStr(fi)

                _ui.value = _ui.value.copy(
                    fpBitmap = bmp,
                    fpThumb = thumb,
                    fpSize = size,
                    nfiq = nfiqVal,
                    quality = qaVal
                )

                if (mode == "show") {
                    val msg = buildString {
                        append("✅ Capturada ")
                        append(size)
                        append(" • Calidad ")
                        append(qaVal?.toString() ?: "—")
                        append(" • NFIQ ")
                        append(nfiqVal?.toString() ?: "—")
                        append(" • Cap ")
                        append(ms(_ui.value.captureTime))
                    }
                    _ui.value = _ui.value.copy(fpResult = msg)
                    return@launch
                }

                val t1 = System.currentTimeMillis()
                val ex = Bione.extractFeature(fi)
                val ext = System.currentTimeMillis() - t1
                _ui.value = _ui.value.copy(extractTime = ext)

                if (ex.error != Bione.RESULT_OK) {
                    _ui.value = _ui.value.copy(fpResult = "❌ Error extrayendo rasgos (${ex.error}) • Ext ${ms(ext)}")
                    return@launch
                }

                val feat = ex.data as? ByteArray ?: run {
                    _ui.value = _ui.value.copy(fpResult = "❌ Rasgos vacíos • Ext ${ms(ext)}")
                    return@launch
                }

                when (mode) {
                    "enroll" -> {
                        val t2 = System.currentTimeMillis()
                        val mk = Bione.makeTemplate(feat, feat, feat)
                        val gen = System.currentTimeMillis() - t2
                        _ui.value = _ui.value.copy(generalizeTime = gen)

                        if (mk.error != Bione.RESULT_OK) {
                            _ui.value = _ui.value.copy(fpResult = "❌ Error generando template (${mk.error}) • Gen ${ms(gen)}")
                            return@launch
                        }

                        val temp = mk.data as? ByteArray ?: run {
                            _ui.value = _ui.value.copy(fpResult = "❌ Template vacío • Gen ${ms(gen)}")
                            return@launch
                        }

                        val id = Bione.getFreeID()
                        if (id < 0) {
                            _ui.value = _ui.value.copy(fpResult = "❌ Sin ID disponible (${id})")
                            return@launch
                        }

                        val en = Bione.enroll(id, temp)
                        if (en != Bione.RESULT_OK) {
                            _ui.value = _ui.value.copy(fpResult = "❌ Falló enrolamiento (${en})")
                            return@launch
                        }

                        val count = runCatching { Bione.getEnrolledCount() }.getOrNull()
                        _ui.value = _ui.value.copy(
                            lastId = id,
                            fpResult = "✅ Enrolado ID $id • Total ${count ?: "?"} • Cap ${ms(_ui.value.captureTime)} • Ext ${ms(ext)} • Gen ${ms(gen)}"
                        )
                    }

                    "verify" -> {
                        val id = _ui.value.lastId ?: run {
                            _ui.value = _ui.value.copy(fpResult = "ℹ️ Verifica: no hay ID enrolado")
                            return@launch
                        }
                        val t3 = System.currentTimeMillis()
                        val vr = Bione.verify(id, feat)
                        val ver = System.currentTimeMillis() - t3

                        if (vr.error != Bione.RESULT_OK) {
                            _ui.value = _ui.value.copy(fpResult = "❌ Error de verificación (${vr.error}) • Ver ${ms(ver)}", verifyTime = ver)
                            return@launch
                        }

                        val ok = (vr.data as? Boolean) == true
                        val score = vr.arg1
                        _ui.value = _ui.value.copy(
                            verifyTime = ver,
                            lastScore = score,
                            fpResult = if (ok)
                                "✅ Coincide con ID $id • Score $score • Ver ${ms(ver)}"
                            else
                                "❌ No coincide con ID $id • Score $score • Ver ${ms(ver)}"
                        )
                    }

                    "identify" -> {
                        val enrolled = runCatching { Bione.getEnrolledCount() }.getOrNull() ?: 0
                        if (enrolled <= 0) {
                            _ui.value = _ui.value.copy(fpResult = "ℹ️ Identificar: base de datos vacía")
                            return@launch
                        }
                        val t4 = System.currentTimeMillis()
                        val id2 = Bione.identify(feat)
                        val ver = System.currentTimeMillis() - t4

                        if (id2 < 0) {
                            _ui.value = _ui.value.copy(verifyTime = ver, fpResult = "❌ No se encontró coincidencia • Ver ${ms(ver)}")
                        } else {
                            _ui.value = _ui.value.copy(verifyTime = ver, lastId = id2, fpResult = "✅ Identificado ID $id2 • Ver ${ms(ver)}")
                        }
                    }
                }
            } finally {
                val lbl = if (_ui.value.isFpOpen) "Listo" else "Cerrado"
                _ui.value = _ui.value.copy(fpStateLabel = lbl)
                setBusy(BusyStateVM.IDLE)
            }
        }
    }

    fun clearFpDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val r = Bione.clear()
            _ui.value = _ui.value.copy(fpResult = if (r == Bione.RESULT_OK) "DB limpia" else "Clear err $r", lastId = null, lastScore = null)
        }
    }

    fun openQr() {
        if (_ui.value.isQrOpen || _ui.value.busy != BusyStateVM.IDLE) return
        _ui.value = _ui.value.copy(qrStateLabel = "Preparando…", lastError = null)
        setBusy(BusyStateVM.PREP_QR)
        viewModelScope.launch(Dispatchers.IO) {
            val cs = CodeScanner.getInstance(ctx)
            cs.powerOn()
            val err = cs.open()
            if (err == CodeScanner.RESULT_OK) {
                val fw = (cs.javaClass.getMethod("getFirmwareVersion").invoke(cs) as? AraResult)?.data as? String
                val sn = (cs.javaClass.getMethod("getSerial").invoke(cs) as? AraResult)?.data as? String
                _ui.value = _ui.value.copy(isQrOpen = true, qrStateLabel = "Listo", qrFw = fw, qrSn = sn)
            } else {
                cs.powerOff()
                _ui.value = _ui.value.copy(isQrOpen = false, qrStateLabel = "Cerrado", lastError = "QR open err=$err")
            }
            setBusy(BusyStateVM.IDLE)
        }
    }

    fun closeQr() {
        if (!_ui.value.isQrOpen || _ui.value.busy != BusyStateVM.IDLE) return
        _ui.value = _ui.value.copy(qrStateLabel = "Cerrando…")
        setBusy(BusyStateVM.PREP_QR)
        viewModelScope.launch(Dispatchers.IO) {
            val cs = CodeScanner.getInstance(ctx)
            runCatching { cs.close() }
            runCatching { cs.powerOff() }
            _ui.value = _ui.value.copy(isQrOpen = false, qrStateLabel = "Cerrado")
            setBusy(BusyStateVM.IDLE)
        }
    }

    fun runQr() {
        if (!_ui.value.isQrOpen || _ui.value.busy != BusyStateVM.IDLE) return
        _ui.value = _ui.value.copy(cancelQr = false, qrStateLabel = "Escaneando…")
        setBusy(BusyStateVM.SCAN_QR)
        viewModelScope.launch(Dispatchers.IO) {
            val cs = CodeScanner.getInstance(ctx)
            try {
                var keep = true
                while (keep && !_ui.value.cancelQr) {
                    val m = cs.javaClass.methods.firstOrNull { it.name == "scan" && it.parameterTypes.size == 1 }
                    val res: AraResult = try {
                        if (m != null) m.invoke(cs, 1000) as AraResult else cs.scan() as AraResult
                    } catch (t: Throwable) {
                        _ui.value = _ui.value.copy(qrResult = "QR fallo: ${t.message}")
                        break
                    }
                    when (res.error) {
                        CodeScanner.RESULT_OK -> { _ui.value = _ui.value.copy(qrResult = res.data?.let { String((it as ByteArray), Charsets.UTF_8).trim() }.takeIf { !it.isNullOrBlank() } ?: "QR leído (sin texto)"); keep = false }
                        CodeScanner.TIMEOUT -> {}
                        CodeScanner.DEVICE_NOT_OPEN -> {
                            val e = cs.open()
                            if (e != CodeScanner.RESULT_OK) { _ui.value = _ui.value.copy(qrResult = "QR open2() err=$e"); keep = false }
                        }
                        else -> { _ui.value = _ui.value.copy(qrResult = "QR error ${res.error}"); keep = false }
                    }
                }
                if (_ui.value.cancelQr) _ui.value = _ui.value.copy(qrResult = "Escaneo cancelado")
            } finally {
                val lbl = if (_ui.value.isQrOpen) "Listo" else "Cerrado"
                _ui.value = _ui.value.copy(qrStateLabel = lbl)
                setBusy(BusyStateVM.IDLE)
            }
        }
    }

    fun cancelCurrent() {
        when (_ui.value.busy) {
            BusyStateVM.CAPTURE_FP -> _ui.value = _ui.value.copy(cancelFp = true)
            BusyStateVM.SCAN_QR -> _ui.value = _ui.value.copy(cancelQr = true)
            else -> {}
        }
    }
}
