#!/usr/bin/env bash
# Installs Android SDK + Gradle (if missing) and builds app-debug.apk
# Works on Linux and macOS. Requires JDK 17+ and internet access.
set -euo pipefail
cd "$(dirname "$0")"

command -v java >/dev/null || { echo "ERROR: JDK 17+ not found. Install it first (e.g. 'sudo apt install openjdk-17-jdk' or 'brew install openjdk@17')."; exit 1; }

case "$(uname -s)" in
  Darwin) OS=mac ;;
  *)      OS=linux ;;
esac

export ANDROID_HOME="${ANDROID_HOME:-$HOME/android-sdk}"
GRADLE_VER=8.9
TOOLS="$HOME/.yd-build-tools"
mkdir -p "$ANDROID_HOME/cmdline-tools" "$TOOLS"

if [ ! -x "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" ]; then
  echo ">> Downloading Android command-line tools..."
  curl -fL -o "$TOOLS/cmdtools.zip" "https://dl.google.com/android/repository/commandlinetools-${OS}-11076708_latest.zip"
  rm -rf "$TOOLS/cmdline-tools"
  unzip -q "$TOOLS/cmdtools.zip" -d "$TOOLS"
  rm -rf "$ANDROID_HOME/cmdline-tools/latest"
  mv "$TOOLS/cmdline-tools" "$ANDROID_HOME/cmdline-tools/latest"
fi

SDKM="$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager"
echo ">> Accepting licenses and installing SDK packages..."
yes | "$SDKM" --licenses >/dev/null || true
"$SDKM" "platform-tools" "platforms;android-35" "build-tools;35.0.0"

echo "sdk.dir=$ANDROID_HOME" > local.properties

if [ -x ./gradlew ]; then
  GRADLE=./gradlew
else
  if [ ! -x "$TOOLS/gradle-$GRADLE_VER/bin/gradle" ]; then
    echo ">> Downloading Gradle $GRADLE_VER..."
    curl -fL -o "$TOOLS/gradle.zip" "https://services.gradle.org/distributions/gradle-$GRADLE_VER-bin.zip"
    unzip -q "$TOOLS/gradle.zip" -d "$TOOLS"
  fi
  GRADLE="$TOOLS/gradle-$GRADLE_VER/bin/gradle"
fi

echo ">> Building debug APK..."
"$GRADLE" assembleDebug

echo
echo "DONE: $(pwd)/app/build/outputs/apk/debug/app-debug.apk"
