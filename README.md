# MarshallSdkDemo (single-module)

- Todo el código vive en `:app` bajo el paquete `com.sibel.demo` (sub‑paquetes `domain/`, `data/`, `drivers/`).
- Copia tus **JARs de Aratek** en `app/libs` y los **.so** en `app/src/main/jniLibs/{abi}`.
- El proyecto usa **AGP 8.5.2** / **Kotlin 1.9.24** / **Gradle 8.7**.

> Si Android Studio muestra aviso del wrapper, puede:
> - Abrir con el Gradle del IDE (Gradle JDK) o
> - Ejecutar `gradle wrapper` para que genere `gradle/wrapper/gradle-wrapper.jar`.
