YD Car Launcher - Android 11

This is an Android Studio source project for a basic landscape car launcher.
It registers itself as a HOME launcher and lists apps installed on the head unit.

BUILD AN APK (EASIEST: ONE-CLICK SCRIPT)
Install JDK 17+, then run build-apk.bat (Windows) or ./build-apk.sh (Linux/macOS).
It installs the Android SDK (platform 35, build-tools 35.0.0, platform-tools) and Gradle 8.9, then builds the APK.

BUILD AN APK (ANDROID STUDIO)
1. Install Android Studio on a computer.
2. Open this folder (YDCarLauncher) in Android Studio.
3. Allow Gradle sync. Install Android SDK Platform 35 if Android Studio asks.
4. Select Build > Build Bundle(s) / APK(s) > Build APK(s).
5. The APK is normally created at:
   app/build/outputs/apk/debug/app-debug.apk

INSTALL / SET AS HOME
1. Copy app-debug.apk to a USB drive and install it on the car player.
2. Press the Home button.
3. Choose "YD Car Launcher" and select "Always" / "Just once" as desired.
   If the chooser does not appear, open Settings > Apps > Default apps > Home app
   (wording differs by head-unit manufacturer) and select YD Car Launcher.

NOTES
- The source targets Android 11 compatibility (targetSdk 30), with minSdk 23.
- Maps opens Google Maps if installed; otherwise it tries the web maps page.
- Music opens the default music app available on the device.
- Some car-player firmware hides Home-app selection or overrides launchers.
  In that case the manufacturer firmware may prevent replacing the stock launcher.
- This starter launcher does not read vehicle CAN data, steering-wheel buttons,
  radio tuner controls, or proprietary YD hardware features.
