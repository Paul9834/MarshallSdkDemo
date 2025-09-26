@ECHO OFF
SET DIR=%~dp0
SET JAVA_EXE=%JAVA_HOME%\bin\java.exe
IF NOT EXIST "%JAVA_EXE%" (
  ECHO JAVA_HOME is not set; using IDE Gradle.
  EXIT /B 0
)
"%JAVA_EXE%" -Dorg.gradle.appname=gradlew -classpath "%DIR%\gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
