#!/bin/bash
# Deploy APK to connected Android device via ADB

echo "Leo-Mod APK Installer"
echo "====================="
echo ""

# Check if ADB is installed
if ! command -v adb &> /dev/null; then
    echo "❌ ADB not found. Install Android SDK Platform Tools."
    exit 1
fi

# Check for connected devices
echo "Checking for connected devices..."
DEVICES=$(adb devices | grep -v "List" | grep "device" | wc -l)

if [ $DEVICES -eq 0 ]; then
    echo "❌ No devices found. Connect an Android device via USB."
    exit 1
fi

echo "✅ Found $DEVICES device(s)"
echo ""

# Select build type
echo "Select APK to install:"
echo "1) Debug APK (leo-mod-debug.apk)"
echo "2) Release APK (leo-mod-release.apk)"
read -p "Enter choice [1-2]: " choice

case $choice in
    1)
        APK="build/outputs/apk/leo-mod-debug.apk"
        echo "Installing Debug APK..."
        ;;
    2)
        APK="build/outputs/apk/leo-mod-release.apk"
        echo "Installing Release APK..."
        ;;
    *)
        echo "❌ Invalid choice"
        exit 1
        ;;
esac

if [ ! -f "$APK" ]; then
    echo "❌ APK not found: $APK"
    echo "Build the APK first using: ./build-apk.sh"
    exit 1
fi

echo "Installing $APK..."
adb install -r "$APK"

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Installation successful!"
    echo ""
    echo "To launch the app:"
    echo "  adb shell am start -n com.leo.bgmimod/.MainActivity"
else
    echo ""
    echo "❌ Installation failed!"
    exit 1
fi
