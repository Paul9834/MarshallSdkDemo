# MarshallSdkDemo · README técnico

![Marshall 8 Frontal](https://imgur.com/spYDd5h.png)
![Marshall 8 Posterior](https://imgur.com/vg79b8f.png)

## Descripción
Demo Android (Jetpack Compose) para integrar el **Marshall 8** de Aratek con el **SDK BMAPI v3.x**. Expone flujos de huella (enrolar, verificar, identificar) y escaneo de QR, con arquitectura orientada a pruebas y desacople de drivers.

## Estructura de módulos
- **:app** — UI Compose, navegación y ViewModels.
- **libs/** — JARs de BMAPI.
- **src/main/jniLibs/** — librerías nativas por ABI.
- **assets/terminal.xml** — configuración del terminal Marshall (no modificar en actualizaciones del SDK).

## Arquitectura
- **UI (Compose)**: `MainActivity` habilita `enableEdgeToEdge()` y monta `MainAppScaffold`. `MainDashboard.kt` presenta tarjetas de estado, métricas y diálogos bloqueantes para operaciones críticas.
- **Estado**: `MainDashboardViewModel` (AndroidViewModel) orquesta corrutinas en `Dispatchers.IO`, administra cancelaciones (`cancelFp`, `cancelQr`) y publica `DashState`.
- **Dominio/Datos**: `FingerprintGateway`, `FingerFeature` e `InMemoryFingerprintRepo` simulan enrolamiento/verificación para pruebas sin hardware.
- **Drivers**: `BmapiDriverPlaceholder` define el punto de integración con `Terminal`, `FingerprintScanner`, `Bione` y `CodeScanner`.

## Integración con hardware (BMAPI)
### Ciclo general recomendado
`getInstance → powerOn → open → [operación] → close → powerOff`

### Clases relevantes
- **Terminal**: versión de SDK, utilidades del terminal.
- **FingerprintScanner**: `powerOn/open/prepare/capture/finish/close/powerOff`, versión de driver/firmware, modelo, SN, LFD.
- **Bione**: `initialize/extractFeature|Iso|Ansi/makeTemplate/enroll/verify/identify/clear`. Soporta BIONE™, ISO/IEC 19794-2, ANSI-378.
- **CodeScanner**: `powerOn/open/scan/read/close/powerOff`.

### Manejo de energía
- **Prioridad batería**: encender/abrir antes de usar y cerrar/apagar al terminar.
- **Prioridad velocidad**: mantener encendido/abierto durante la sesión (mayor consumo).
- **Balance**: abrir al iniciar la app y apagar por inactividad o al cerrar.

## Flujos operativos
- **Huella (enrolar)**: `Bione.initialize → FingerprintScanner.prepare → capture → extractFeature → makeTemplate → enroll → finish → Bione.exit`.
- **Huella (verificar/identificar)**: `capture → extractFeature → verify(id, feature) | identify(feature)`.
- **QR**: `CodeScanner.scan()` y validar `data != null`.
- **Errores comunes**: `DEVICE_NOT_OPEN`, `TIMEOUT`, `NOT_ENOUGH_MEMORY`, `NO_FINGER`, etc.

## UI y UX (Compose)
- **Dashboard**: tarjetas con métricas (tiempos de captura/extracción/matching, NFIQ/calidad), miniaturas de huella y últimos IDs/scores.
- **Seguridad UX**: `BlockingLoadingDialog` evita interacciones durante operaciones críticas.
- **Temado**: Material 3 con esquema “true black” y soporte landscape. Prep listo para Dynamic Color.

## Requisitos del entorno
- **AGP/Kotlin**: AGP 8.5.2, Kotlin 1.9.24.
- **Android**: BMAPI compatible desde Android 4.4+ (API 19).
- **Permisos**: agregar permisos de almacenamiento/lectura de estado según demo.
- **ABI**: conservar solo el directorio de `jniLibs` correspondiente a la arquitectura destino.

## Instalación e importación del SDK
1. Copiar **todos** los JAR a `libs/` y **todas** las carpetas de `jniLibs/` al proyecto.  
2. Copiar `assets/terminal.xml` (solo en primera integración; **no** sobreescribir al actualizar).  
3. Verificar permisos en `AndroidManifest` y runtime permissions.  
4. Sincronizar con Gradle y compilar.

## Buenas prácticas
- **Autorización Bione**: abrir exitosamente el lector **antes** de `Bione.initialize`, o retornará `-1014`.
- **Capacidad 1:N**: motor integrado soporta hasta ~10 000 templates (BIONE™).
- **Depuración sin USB**: usar `adb tcpip` cuando el puerto esté ocupado por periféricos.
- **Actualizaciones del SDK**: reemplazar **todas** las libs; no mezclar versiones.

## Roadmap
- Implementar el driver real en `BmapiDriverPlaceholder`.
- Persistir enrolamientos con Room/SQLCipher.
- Exportar logs y trazas para soporte de campo.
- Activar Dynamic Color y perfiles de tema por escenario.

## Especificaciones del dispositivo objetivo (Marshall 8)
Android 11+, lector FAP 30 (A700) con LFD, QR, NFC/contacto, 4G/Wi-Fi/BT/GPS, batería 10 000 mAh, IP65. Aplicaciones: registro de votantes, KYC, censo y control de acceso.
