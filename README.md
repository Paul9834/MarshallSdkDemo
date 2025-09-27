# MarshallSdkDemo · Bitácora creativa del ingeniero Paul Montealegre

> "Si el hardware late, la app lo cuenta" — Paul Montealegre

## Panorama del laboratorio
MarshallSdkDemo es el showcase personal de Paul Montealegre para el Marshall 8: una app Android en Compose que conecta sensores biométricos de Aratek con una experiencia de tablero digna de sala de control. El proyecto vive en el módulo `:app`, con los SDK nativos empacados en `libs/` y `jniLibs/`, listo para cargar la configuración del terminal al iniciar.

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
