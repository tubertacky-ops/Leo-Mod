#!/bin/bash
# Quick setup and build script

set -e

echo "Setting up Leo-Mod project..."
echo ""

# Check for Java
if ! command -v java &> /dev/null; then
    echo "❌ Java not found. Install JDK 11 or higher."
    exit 1
fi

echo "✅ Java found: $(java -version 2>&1 | head -n 1)"

# Create necessary directories
echo "Creating project directories..."
mkdir -p src/main/kotlin/com/leo/bgmimod
mkdir -p src/main/java
mkdir -p res/layout
mkdir -p res/values
mkdir -p build/outputs/apk

echo "✅ Directories created"

# Make scripts executable
echo "Making scripts executable..."
chmod +x build-apk.sh
chmod +x build.sh
chmod +x install-apk.sh

echo "✅ Scripts ready"

echo ""
echo "Setup complete! Ready to build."
echo ""
echo "Next steps:"
echo "1. Run: ./build-apk.sh"
echo "2. Connect Android device via USB"
echo "3. Run: ./install-apk.sh"
