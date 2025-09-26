#!/usr/bin/env sh
# Minimal bootstrapper – Android Studio puede regenerarlo si es necesario.
DIR="$(cd "$(dirname "$0")" && pwd)"
exec "${JAVA_HOME:-$(/usr/libexec/java_home 2>/dev/null)}"/bin/java -Dorg.gradle.appname=gradlew -classpath "$DIR/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
