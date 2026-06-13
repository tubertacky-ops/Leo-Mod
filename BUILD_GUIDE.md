# Leo-Mod APK Build & Installation Guide

## Quick Start

### Prerequisites
- Android SDK (API 34)
- JDK 11 or higher
- Gradle 8.0 or higher
- Android device or emulator with USB debugging enabled

### Build Instructions

#### Option 1: Using Build Script (Recommended)

```bash
# Make script executable
chmod +x build-apk.sh

# Run build
./build-apk.sh
```

This will:
1. Clean previous builds
2. Build debug APK
3. Build release APK
4. Copy APKs to `build/outputs/apk/`

#### Option 2: Using Gradle Directly

```bash
# Debug build
./gradlew assembleDebug

# Release build (unsigned)
./gradlew assembleRelease

# Clean build
./gradlew clean assembleDebug
```

#### Option 3: Using Android Studio

1. Open project in Android Studio
2. Select Build → Build Bundle(s) / APK(s) → Build APK(s)
3. APKs will be generated in `app/build/outputs/apk/`

## Installation

### Via ADB (Automated)

```bash
# Make script executable
chmod +x install-apk.sh

# Run installer
./install-apk.sh

# Follow prompts to select Debug or Release APK
```

### Via ADB (Manual)

```bash
# Install debug APK
adb install -r build/outputs/apk/leo-mod-debug.apk

# Install release APK
adb install -r build/outputs/apk/leo-mod-release.apk

# Launch app
adb shell am start -n com.leo.bgmimod/.MainActivity
```

### Via USB (Windows)

1. Connect Android device via USB
2. Enable USB Debugging on device
3. Run in Command Prompt:
   ```cmd
   adb install -r build\outputs\apk\leo-mod-debug.apk
   ```

## Build Outputs

```
build/outputs/apk/
├── leo-mod-debug.apk      # Debug version with logs
└── leo-mod-release.apk    # Release version (unsigned)
```

## Troubleshooting

### "gradlew: command not found"
```bash
chmod +x gradlew
./gradlew --version
```

### "JAVA_HOME not set"
```bash
# Linux/Mac
export JAVA_HOME=/path/to/jdk

# Windows
set JAVA_HOME=C:\\Program Files\\Java\\jdk-11
```

### "Device offline" or "No devices found"
```bash
# List devices
adb devices

# Reconnect device
adb kill-server
adb start-server
```

### Build fails with "out of memory"
```bash
# Increase heap size
export _JAVA_OPTIONS="-Xmx4096m"
./gradlew assembleDebug
```

## APK Details

- **Package Name:** com.leo.bgmimod
- **Min SDK:** 21 (Android 5.0)
- **Target SDK:** 34 (Android 14)
- **Debug APK Size:** ~5-10 MB
- **Release APK Size:** ~3-5 MB (with ProGuard)

## Features Included

✅ ESP (Enemy Seeing Perspective)
✅ Aimbot with smoothing
✅ Real-time player detection
✅ Game memory hooking
✅ Reflection-based game interaction

## Next Steps

1. Build APK using `./build-apk.sh`
2. Connect Android device
3. Install using `./install-apk.sh`
4. Launch Leo-Mod and enable ESP/Aimbot
5. Run BGMI and enjoy!

## Support

For issues or questions, check the repository issues or refer to build logs:
```bash
./gradlew assembleDebug --stacktrace
```
