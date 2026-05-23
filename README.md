# Camscanner-Alter

A privacy-focused, high-quality document scanner app built with modern Android architecture.

## Vision

- Local-first scanning and processing.
- Smooth, modern UI using Jetpack Compose.
- High-contrast ink enhancement and a strong "magic filter".
- Accurate auto-cropping with manual adjustment.
- Efficient battery usage and minimal background work.

## What’s included

- Project plan in `PLAN.md`
- Android app scaffold under `app/`
- Initial Compose UI and app entry point
- CameraX capture screen with interactive crop overlay
- Scan preview screen with magic and ink filters

## Getting started

Open the project in Android Studio or use Gradle from the terminal.

```bash
cd /workspaces/Camscanner-Alter
./gradlew :app:assembleDebug
```

If the wrapper is not available, install Gradle and use:

```bash
gradle :app:assembleDebug
```

## Next steps

1. Add edge detection and crop overlay.
2. Build the filter engine with magic filter and ink contrast.
3. Add local document storage and PDF export.
4. Polish the UI and optimize battery usage.
