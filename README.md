# MarshallSdkDemo · Bitácora creativa del ingeniero Paul Montealegre

> "Si el hardware late, la app lo cuenta" — Paul Montealegre

## Panorama del laboratorio
MarshallSdkDemo es el showcase personal de Paul Montealegre para el Marshall 8: una app Android en Compose que conecta sensores biométricos de Aratek con una experiencia de tablero digna de sala de control. El proyecto vive en el módulo `:app`, con los SDK nativos empacados en `libs/` y `jniLibs/`, listo para cargar la configuración del terminal al iniciar.

## Arquitectura Compose + BMAPI
- **Vista y navegación**. `MainActivity` habilita `enableEdgeToEdge()`, carga las preferencias del terminal con `Terminal.loadSettings` y monta `MainAppScaffold`, donde vive la `FancyTopBar` y el dashboard completo.
- **Estado**. `MainDashboardViewModel` (AndroidViewModel) gobierna el `DashState`, ejecuta operaciones de hardware en `Dispatchers.IO` y activa flags de cancelación para captura de huellas (`cancelFp`) o escaneo QR (`cancelQr`).
- **Dominio y repositorios**. `FingerprintGateway`, `FingerFeature` e `InMemoryFingerprintRepo` permiten simular enrolamiento y verificación sin depender todavía del SDK nativo.
- **Drivers**. `BmapiDriverPlaceholder` reserva el punto de conexión para encapsular los `AraBMApi*.jar` y librerías JNI cuando se integre el hardware real.

## Explorador de clases clave
| Archivo | Rol narrativo |
| --- | --- |
| `MainDashboard.kt` | Composable con `SectionCard`, chips de estado y un diálogo bloqueante que acompaña cada paso biométrico. |
| `MainDashboardViewModel.kt` | Orquesta `FingerprintScanner`, `Bione` y `CodeScanner`: abre/cierra periféricos, mide tiempos (captura, extracción, template, verificación) y traduce errores a mensajes UX. |
| `ui/theme/*.kt` | Define la identidad Material 3 true black, tipografías landscape y shapes de 26 dp consistentes para tablet. |
| `domain/` + `data/` | Expone un pipeline de enrolamiento/verificación con IDs libres para experimentar sin arriesgar la base real. |
| `drivers/BmapiDriverPlaceholder.kt` | Cascarón donde aterrizarán las llamadas directas al SDK BMAPI. |

## Flujos operativos esenciales
1. **Habilitar hardware** — `openFp()`/`openQr()` siguen la receta oficial `getInstance → powerOn → open` y registran firmware, serial y modelo para la bitácora.
2. **Capturar y procesar huellas** — `runFp(mode)` prepara el sensor, usa `capture(timeout)` cuando está disponible, genera miniaturas y ejecuta `Bione.extractFeature`, `makeTemplate`, `enroll`, `verify` o `identify` según el modo solicitado.
3. **Lectura de códigos** — `runQr()` gestiona timeouts, reintentos de apertura (`DEVICE_NOT_OPEN`) y devuelve el texto del QR en UTF-8 o el error asociado.
4. **UX resiliente** — `BlockingLoadingDialog` bloquea interacciones peligrosas, mientras que las métricas en tarjeta muestran tiempos, NFIQ, calidad y los últimos IDs/score.

## Bitácora ampliada y recursos
- Consulta `docs/Informe_Integracion_Aratek_Marshall8_APA.txt` para ver el informe APA con decisiones de theming, dependencias y recomendaciones de campo.
- El tema Material 3 (`AppTheme`, `AppColorScheme`, `FancyTopBar`) ya soporta true black en Marshall 8 y está listo para activar Dynamic Color cuando la misión lo requiera.

## Logros clave de Paul

### 🛰️ Orquestación de sensores Marshall
Paul creó un ViewModel que enciende y apaga el lector de huellas y el escáner QR, ajusta el nivel LFD y expone en estado reactivo firmware, serie y versiones del motor biométrico. Toda operación larga vive en corrutinas `Dispatchers.IO`, manteniendo la UI fluida incluso mientras se prepara hardware o se limpia la base de datos.

### 🖥️ Dashboard vivo en Compose
El panel principal combina tarjetas animadas, métricas en vivo y galerías de acciones rápidas para cada sensor. Los estados se pintan con chips, barras de progreso NFIQ/calidad y miniaturas de la huella capturada, además de diálogos bloqueantes con microcopys personalizados para cada paso del flujo.

### 🔬 Flujo biométrico extremo a extremo
Desde la captura cruda hasta enrolar, verificar o identificar huellas, Paul encapsuló tiempos de captura/extracción, generación de templates y respuestas de Bione, guardando los últimos IDs y puntajes para storytelling instantáneo en la UI.

### 🎨 Identidad visual "verde Ferxxo"
El tema Material 3 se adapta a portrait/landscape, aplica una paleta negra para OLED y ofrece un top bar degradado con atajos rápidos para el nivel LFD y refrescos instantáneos del dispositivo, manteniendo la estética de marca que Paul definió.

### 🧪 Infraestructura de pruebas en memoria
Mientras se integran los drivers reales, existe un repositorio en memoria que modela enrolamiento y verificación para escenarios offline, además de un placeholder para el driver BMAPI definitivo.

## Kit de exploración
1. **Clonar y sincronizar**: `./gradlew sync` descarga dependencias; el wrapper usa Kotlin 1.9.24 y AGP 8.5.2.
2. **Lanzar en hardware Marshall 8** para aprovechar `terminal.xml` y los binarios nativos.
3. **Interactuar con la UI**: abre/cierra sensores, juega con los niveles LFD y sigue la narrativa en tiempo real.

## Próxima misión sugerida
- Integrar el driver BMAPI real sobre la clase placeholder.
- Persistir enrolamientos fuera de memoria (SQLCipher o Room).
- Añadir captura de logs y exportación para soporte de campo.

## Crédito
Bitácora compilada por Paul Montealegre Melo, ingeniero biométrico y narrador del Marshall 8.
