# MarshallSdkDemo · Informe técnico de integración del SDK Marshall 8

## 1. Resumen ejecutivo
MarshallSdkDemo es mi entorno controlado para validar la integración del SDK biométrico de Aratek sobre hardware Marshall 8. El proyecto reside en el módulo `:app`, construido con Kotlin, Jetpack Compose y una capa de interoperabilidad JNI que expone los binarios nativos ubicados en `app/src/main/jniLibs/`. El objetivo principal es garantizar que captura, enrolamiento, verificación e identificación funcionen con latencia estable (<250 ms para extracción) y con trazabilidad completa para soporte de campo.

## 2. Arquitectura del sistema
- **Capas**: UI en Compose, ViewModels orquestando corrutinas, repositorio biométrico que abstrae el driver Marshall y una capa de datos en memoria como doble del repositorio persistente definitivo.
- **Configuración del dispositivo**: el archivo `terminal.xml` inicializa puertos, nivel LFD y parámetros de energía al arrancar la aplicación.
- **Gestión de dependencias**: el wrapper Gradle (`./gradlew`) fija Kotlin 1.9.24 y Android Gradle Plugin 8.5.2 para asegurar compatibilidad con Compose Multiplatform.

## 3. Integración del SDK biométrico
- Inicializo el driver Marshall desde un ViewModel dedicado, aislado en `Dispatchers.IO` para no bloquear la UI.
- Implementé una fachada que expone operaciones de enrolamiento, verificación e identificación como funciones suspend. Cada operación registra métricas: duración de captura, tiempo de extracción de template, tamaño del template y puntajes NFIQ.
- El repositorio en memoria reproduce el comportamiento del servicio BMAPI final, permitiendo validar la lógica de negocio mientras la conexión con el SDK real se habilita.
- Los últimos estados de firmware, número de serie y versión de motor se publican como `StateFlow` para su consumo en Compose.

## 4. Orquestación de sensores y flujo operativo
1. **Inicialización**: al abrir la pantalla principal, energizo lector de huellas y escáner QR, aplico nivel LFD configurado y valido firmware.
2. **Captura**: el controlador de huella expone frames crudos que limpio y transformo en templates; los tiempos se almacenan para diagnóstico.
3. **Post-procesamiento**: guardo templates en caché en memoria, con posibilidad de exportación posterior a Room/SQLCipher.
4. **Fallbacks**: implementé limpieza de base de datos temporal y reinicio de sensores para escenarios de error.

## 5. Interfaz y telemetría en Compose
- Compose muestra tarjetas de estado, chips de sensor y barras de calidad NFIQ en tiempo real a partir de `StateFlow`.
- Cada acción crítica abre diálogos bloqueantes con controles personalizados que evitan cierres accidentales durante la captura.
- Integré accesos directos en el top bar para ajustar LFD y refrescar información del dispositivo sin salir del flujo biométrico.

## 6. Evidencia visual
![Panel principal con telemetría de sensores](https://i.imgur.com/spYDd5h.png)

![Detalle de captura biométrica en Compose](https://i.imgur.com/vg79b8f.png)

## 7. Pruebas y verificación
- **Pruebas en memoria**: ejecuto escenarios de enrolamiento/verificación con datos sintéticos para validar reglas de negocio antes de conectar el BMAPI real.
- **Monitoreo de rendimiento**: mido tiempos de corrutinas y anoto logs clave para asegurar que los umbrales de latencia se cumplen.
- **Pruebas manuales en hardware**: el flujo se valida en un Marshall 8 real para confirmar inicialización de sensores, lectura de firmware y ajuste LFD.

## 8. Próximas acciones
1. Conectar el driver BMAPI definitivo y eliminar el doble en memoria.
2. Persistir enrolamientos en almacenamiento cifrado con Room + SQLCipher.
3. Instrumentar captura de logs y exportación automática para soporte de campo.
4. Automatizar pruebas end-to-end con Android Test Orchestrator sobre hardware físico.

---
**Autor**: Paul Montealegre Melo · Ingeniero biométrico responsable de la integración Marshall 8.
