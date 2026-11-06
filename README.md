# YD Car Launcher

A simple landscape home-screen launcher for Android car players (head units), made for YD-brand units running Android 6.0 or newer (built for Android 11 compatibility).

It registers itself as a **HOME launcher**, shows a clock and date, gives quick buttons for Maps, Music, Phone, Bluetooth and Settings, and lists every app installed on the head unit.

> **Note:** This is a starter launcher. It does not read vehicle CAN data, steering-wheel buttons, radio tuner controls, or any proprietary YD hardware features.

## Features

- Landscape, full-screen, immersive car-mode UI
- Live clock and date
- Quick buttons: Maps, Music, Phone, Bluetooth, Settings
- Auto-generated grid of all installed apps, sorted A–Z
- Can be set as the default Home app

## Requirements

| Item | Version |
|------|---------|
| Android on the head unit | 6.0 (API 23) or newer |
| Target SDK | 30 (Android 11) |
| Compile SDK | 35 |
| Build tools | 35.0.0 |
| Gradle | 8.9 |
| Android Gradle Plugin | 8.7.3 |
| JDK (on the build machine) | 17 or newer |

## 1. Build the APK

You only need the Android SDK on the computer that builds the APK. The car player only needs the finished `app-debug.apk`.

### Option A: GitHub Actions (nothing to install)

1. Push this repository to GitHub with the project files at the repository root.
2. Make sure `.github/workflows/build.yml` exists (see below).
3. Open the **Actions** tab, wait for the **Build APK** run to finish (green check).
4. Open the run and download the **app-debug** artifact at the bottom. Unzip it to get `app-debug.apk`.

Workflow file `.github/workflows/build.yml`:

```yaml
name: Build APK
on: [push, workflow_dispatch]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 17
      - uses: gradle/actions/setup-gradle@v4
        with:
          gradle-version: 8.9
      - run: gradle assembleDebug
      - uses: actions/upload-artifact@v4
        with:
          name: app-debug
          path: app/build/outputs/apk/debug/app-debug.apk
```

### Option B: One-click build script

Install JDK 17+, then run:

- Windows: `build-apk.bat`
- Linux / macOS: `./build-apk.sh`

The script installs the Android SDK (platform 35, build-tools 35.0.0, platform-tools) and Gradle 8.9, then builds the APK.

### Option C: Android Studio

1. Open the project folder in Android Studio.
2. Let Gradle sync and accept the SDK Platform 35 install prompt.
3. Choose **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

In every case the APK is created at:

```
app/build/outputs/apk/debug/app-debug.apk
```

## 2. Install on the YD car player

### Method 1: USB drive (recommended)

1. Copy `app-debug.apk` to the root of a USB drive (FAT32 or exFAT).
2. Plug the drive into the head unit's USB port.
3. Open the head unit's **File Manager** app (install one if it has none).
4. Browse to the USB drive and tap `app-debug.apk`.
5. If prompted, allow **Install unknown apps** for the file manager (Settings > Apps > Special access > Install unknown apps).
6. Tap **Install**, then **Open**.

### Method 2: ADB

1. On the head unit, enable Developer options (tap the build number 7 times in Settings > About) and turn on **USB debugging** or **Wireless debugging**.
2. From a computer with `platform-tools` installed:

```bash
adb connect <head-unit-ip>:5555   # only for Wi-Fi ADB
adb install -r app-debug.apk
```

## 3. Set as the default Home app

1. Press the **Home** button on the head unit.
2. In the chooser, select **YD Car Launcher** and tap **Always** (or **Just once**).
3. If no chooser appears, go to **Settings > Apps > Default apps > Home app** (wording varies by manufacturer) and select **YD Car Launcher**.

## Troubleshooting

| Problem | What to try |
|---------|-------------|
| "App not installed" | Uninstall any older copy signed with a different key, then install again. |
| "Parse error" | The head unit runs Android below 6.0, which is not supported. |
| No Home-app option | Many head units lock the launcher in firmware. Look for a "Launcher" or "Home" option in the unit's factory or system settings. If locked, open the app from the app list instead. |
| Reverts to the stock launcher after reboot | Some firmware resets the default Home app at startup. Select it again in the system Home-app setting. |
| Maps does nothing | Install Google Maps. Without it, the app opens maps.google.com in a browser. |
| Music says no player found | Install or enable a music app on the head unit. |
| GitHub build fails | Check the project files are at the repository root (not inside a subfolder) and that `build.yml` is indented exactly as shown. |

## Uninstall / go back to the stock launcher

1. Go to **Settings > Apps > Default apps > Home app** and select the original launcher.
2. Or uninstall **YD Car Launcher** from **Settings > Apps**.

If the screen is stuck on a blank launcher, connect with ADB and run:

```bash
adb uninstall com.jaliya.ydcarlauncher
```

## Project structure

```
YDCarLauncher/
├── .github/workflows/build.yml     # optional CI build
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/jaliya/ydcarlauncher/MainActivity.java
│       └── res/drawable/ic_launcher.xml
├── gradle/wrapper/gradle-wrapper.properties
├── build.gradle
├── settings.gradle
├── gradle.properties
├── build-apk.bat
└── build-apk.sh
```

## Limitations

- Debug-signed APK only; it is meant for personal use, not Play Store release.
- No access to CAN bus, steering-wheel keys, radio, or other YD-specific hardware.
- Behavior depends on the head unit's firmware, which may restrict replacing the stock launcher.

## License

Add your preferred license here (for example MIT).
