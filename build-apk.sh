#!/bin/bash
# Build script for Leo-Mod APK

echo "================================"
echo "Leo-Mod APK Build Script"
echo "================================"

# Clean previous builds
echo "[1/5] Cleaning previous builds..."
./gradlew clean

if [ $? -ne 0 ]; then
    echo "❌ Clean failed"
    exit 1
fi

# Build debug APK
echo ""
echo "[2/5] Building debug APK..."
./gradlew assembleDebug

if [ $? -ne 0 ]; then
    echo "❌ Debug build failed"
    exit 1
fi

# Build release APK
echo ""
echo "[3/5] Building release APK..."
./gradlew assembleRelease

if [ $? -ne 0 ]; then
    echo "❌ Release build failed"
    exit 1
fi

# Create output directory
echo ""
echo "[4/5] Creating output directory..."
mkdir -p build/outputs/apk

# Copy APKs
echo ""
echo "[5/5] Copying APKs to output directory..."
cp app/build/outputs/apk/debug/*.apk build/outputs/apk/leo-mod-debug.apk 2>/dev/null || echo "Debug APK not found"
cp app/build/outputs/apk/release/*.apk build/outputs/apk/leo-mod-release.apk 2>/dev/null || echo "Release APK not found"

echo ""
echo "================================"
echo "✅ Build complete!"
echo "================================"
echo ""
echo "Output APKs:"
ls -lh build/outputs/apk/*.apk 2>/dev/null || echo "No APKs found"
echo ""
echo "To install debug APK:"
echo "  adb install -r build/outputs/apk/leo-mod-debug.apk"
echo ""
echo "To install release APK:"
echo "  adb install -r build/outputs/apk/leo-mod-release.apk"
