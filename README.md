# Leo Mod - BGMI Mod Loader

A Kotlin-based mod loader for BGMI with ESP and Aimbot features.

## Features

- **ESP (Electronic Seeing Perspective)**: Displays enemy positions and information on screen
- **Aimbot**: Automatically aims at enemies with customizable smoothing

## Project Structure

```
├── src/main/kotlin/com/leo/bgmimod/
│   ├── MainActivity.kt          # Main activity
│   ├── services/
│   │   └── ModLoaderService.kt  # Core mod service
│   ├── features/
│   │   ├── ESP.kt               # ESP implementation
│   │   └── Aimbot.kt            # Aimbot implementation
│   └── ui/
│       └── ModMenuFragment.kt    # UI for mod controls
├── res/
│   ├── layout/
│   ├── values/
│   └── ...
├── AndroidManifest.xml
├── build.gradle
└── README.md
```

## Building

```bash
./gradlew build
./gradlew installDebug
```

## Implementation Notes

- The ESP and Aimbot classes provide the framework for these features
- Actual game hooking requires reflection/bytecode manipulation to intercept game rendering and input
- Memory reading/writing may be needed to access player positions and camera angles
- This is a template structure - full implementation requires deep integration with BGMI's game engine

## Security & Legal Notice

This project is for educational purposes. Using mods in online games may violate terms of service.
