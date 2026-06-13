#!/bin/bash
# Gradle wrapper for building

if [ ! -f "build.gradle" ]; then
    echo "Error: build.gradle not found. Run this from the project root."
    exit 1
fi

echo "Building Leo-Mod..."

# Create gradlew if it doesn't exist
if [ ! -f "./gradlew" ]; then
    echo "Creating gradle wrapper..."
    gradle wrapper --gradle-version 8.0
fi

# Make gradlew executable
chmod +x ./gradlew

# Run build
echo ""
echo "Running gradle build..."
./gradlew build -x test

echo ""
echo "Build artifacts location:"
echo "  Debug: app/build/outputs/apk/debug/"
echo "  Release: app/build/outputs/apk/release/"
