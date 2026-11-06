@echo off
REM Installs Android SDK + Gradle (if missing) and builds app-debug.apk on Windows.
REM Requires JDK 17+ (on PATH or JAVA_HOME) and internet access. Windows 10/11.
setlocal
cd /d "%~dp0"

where java >nul 2>nul
if errorlevel 1 (
  echo ERROR: JDK 17+ not found. Install Temurin 17 from https://adoptium.net then re-run.
  exit /b 1
)

if "%ANDROID_HOME%"=="" set "ANDROID_HOME=%USERPROFILE%\android-sdk"
set "TOOLS=%USERPROFILE%\.yd-build-tools"
set "GRADLE_VER=8.9"
if not exist "%TOOLS%" mkdir "%TOOLS%"
if not exist "%ANDROID_HOME%\cmdline-tools" mkdir "%ANDROID_HOME%\cmdline-tools"

if not exist "%ANDROID_HOME%\cmdline-tools\latest\bin\sdkmanager.bat" (
  echo ^>^> Downloading Android command-line tools...
  curl -fL -o "%TOOLS%\cmdtools.zip" https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip || exit /b 1
  if exist "%TOOLS%\cmdline-tools" rmdir /s /q "%TOOLS%\cmdline-tools"
  tar -xf "%TOOLS%\cmdtools.zip" -C "%TOOLS%" || exit /b 1
  if exist "%ANDROID_HOME%\cmdline-tools\latest" rmdir /s /q "%ANDROID_HOME%\cmdline-tools\latest"
  move "%TOOLS%\cmdline-tools" "%ANDROID_HOME%\cmdline-tools\latest" >nul
)

set "SDKM=%ANDROID_HOME%\cmdline-tools\latest\bin\sdkmanager.bat"
echo ^>^> Accepting licenses and installing SDK packages...
(for /l %%i in (1,1,30) do @echo y) | call "%SDKM%" --licenses >nul
call "%SDKM%" "platform-tools" "platforms;android-35" "build-tools;35.0.0" || exit /b 1

set "SDKDIR=%ANDROID_HOME:\=/%"
echo sdk.dir=%SDKDIR%> local.properties

if exist gradlew.bat (
  set "GRADLE=gradlew.bat"
) else (
  if not exist "%TOOLS%\gradle-%GRADLE_VER%\bin\gradle.bat" (
    echo ^>^> Downloading Gradle %GRADLE_VER%...
    curl -fL -o "%TOOLS%\gradle.zip" https://services.gradle.org/distributions/gradle-%GRADLE_VER%-bin.zip || exit /b 1
    tar -xf "%TOOLS%\gradle.zip" -C "%TOOLS%" || exit /b 1
  )
  set "GRADLE=%TOOLS%\gradle-%GRADLE_VER%\bin\gradle.bat"
)

echo ^>^> Building debug APK...
call "%GRADLE%" assembleDebug || exit /b 1

echo.
echo DONE: %CD%\app\build\outputs\apk\debug\app-debug.apk
endlocal
