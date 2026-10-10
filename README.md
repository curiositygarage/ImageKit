# ImageKit

A high-performance, **100% offline** native Android image editing suite — built for the
everyday home-office jobs: optimizing photos for web/HTML upload forms, preparing
passport & visa photos to exact spec, shrinking large camera shots, cleaning up
receipts and documents, and redacting personal information before sharing.

Built with **Kotlin** and **Jetpack Compose**, with an Apple iOS/HIG-inspired design
language (grouped card layouts, a floating glass tab bar, monochrome vector glyphs,
haptic feedback, and fluid gestures).

---

## Table of Contents

1. [Features](#features)
2. [How to Use](#how-to-use)
3. [Requirements](#requirements)
4. [Building & Running](#building--running)
5. [Architecture](#architecture)
6. [Privacy & Logging](#privacy--logging)
7. [Restrictions & Known Limitations](#restrictions--known-limitations)
8. [License](#license)

---

## Features

### Studio Editor
- **Crop & Orient** — freeform 8-handle crop overlay, plus aspect-ratio presets
  (Passport 2×2, Visa/ID 3:4, A4/Letter, Receipt, 16:9, 4:3, 3:2), 90° rotations,
  and horizontal/vertical flips.
- **Optimize** (Resize & Convert) — output as JPEG, PNG, or WebP; compression quality
  slider; scale by percentage or exact pixel dimensions (aspect-locked); web/HTML
  presets; and **target-byte compression** (e.g. `< 100 KB`, `< 240 KB`, `< 500 KB`,
  `< 1 MB`) using a binary-search quality search.
- **Document & Lens Filters** — Clean Document, Whiteboard Pop, Magic Color,
  Auto Clarity, B&W Contrast, Vivid, Warm Craft, Cool Slate, and Invert Schematic.
- **Adjustments** — brightness, contrast, saturation, warmth, vignette, and sharpen.
- **Privacy & Markup** — blackout boxes, pixelation, blur boxes, highlights, arrow
  callouts, and text notes drawn directly on the canvas.
- **Compare** — hold-to-reveal the original while pressed.

### Batch Processing
- Queue multiple images (or the built-in demo samples) and apply one recipe
  (format, downscale, quality, target size) across all of them, with per-item
  progress and an aggregate storage-savings summary.

### Library
- A history of saved exports with format, dimensions, size, and timestamp. Each entry
  can be shared; open the Library empty state to jump back into the Studio.

### Built-in Demo Samples
No real photos required to try it out: built-in procedural samples include a
passport portrait, a hardware receipt, a high-resolution "raw" desk photo, and a
shipping label (great for testing redaction).

---

## How to Use

ImageKit has four tabs along the bottom: **Home**, **Studio**, **Batch**, and **Library**.

### 1. Load an image
- On **Home**, tap the large **Open Photo or Document** card to pick an image from your
  device gallery, **or**
- Tap any row under **Home Office Playground** to load a built-in demo sample
  (passport, receipt, raw photo, shipping label), **or**
- Use the **Quick Studio Tools** grid to jump straight into a tool.

### 2. Edit in the Studio
1. Choose a tool from the editor's tool dock:
   **Optimize · Crop · Filters · Auto · Adjust · Markup**.
2. Apply edits. The live canvas shows the result immediately, and every change is
   pushed onto an undo/redo history (use **Undo** / **Redo** in the top bar;
   **Reset** returns to the original).
3. Use **Compare** to hold-and-reveal the original while pressed.

### 3. Export or Save
- Open **Optimize** and set:
  - **Output Format** — JPEG / PNG / WebP,
  - **Resolution** — percentage scale or exact pixels,
  - **Compression Quality** — 1–100%, and/or
  - a **target size** (e.g. `< 500 KB`) to auto-fit via binary search.
- Tap **Save**. The result is written to the app's local export store and appears
  in the **Library** tab, and is handed to the system photo gallery.

### 4. Batch process
1. On **Batch**, tap **Import Images** to queue multiple photos, or **Add Demo Queue**
   to load samples.
2. Set the batch **Output Settings** (downscale, format, quality).
3. Tap **Process & Convert All** and watch per-image progress.

### 5. Review history
- Open **Library** to see all saved exports with their size and dimensions.

---

## Requirements

| Requirement | Value |
|---|---|
| **Android version** | 7.0 (API 24) and above |
| **Target / compile SDK** | API 36 |
| **Language** | Kotlin 2.2.x |
| **Build system** | Gradle (Kotlin DSL) + Android Gradle Plugin 9.1.x |
| **JDK** | 17+ (Android Studio bundled JDK recommended) |
| **Android Studio** | Current stable recommended |
| **Network** | **None.** The app is fully offline and needs no API keys. |
| **Permissions** | Gallery/photo picker access only (via the system photo picker). No broad storage, camera, or location permissions. |

---

## Building & Running

### 1. Get the prerequisites (not in the repo)
Machine-specific files are intentionally gitignored. Create them locally:

**`local.properties`** in the project root, pointing at your Android SDK:
```properties
sdk.dir=/path/to/your/Android/Sdk
```
Alternatively, set the `ANDROID_HOME` / `ANDROID_SDK_ROOT` environment variable.

**`debug.keystore`** in the project root (used for debug signing). Android Studio
auto-creates one on first debug build, or generate your own:
```bash
keytool -genkey -v -keystore debug.keystore -storepass android -alias androiddebugkey \
  -keypass android -dname "CN=Android Debug,O=Android,C=US"
```
This file is gitignored and never committed.

### 2. Build
From the project root:
```bash
./gradlew assembleDebug
```
If the Gradle wrapper is not present in your checkout, use your local Gradle:
```bash
gradle assembleDebug
```
The APK is produced at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### 3. Install to a connected device
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

> **Note:** The APK is not committed to this repository (build outputs are
> gitignored). Build it locally with the steps above, or download it from the
> project's Releases page if one is published.

### 4. Running the tests
```bash
./gradlew testDebugUnitTest
```

---

## Architecture

### Tech Stack
- **Language:** Kotlin 2.2.x
- **UI:** Jetpack Compose (Material 3 base, customized to an iOS-HIG visual language)
- **Concurrency:** Kotlin Coroutines (`Dispatchers.IO` / `Dispatchers.Default`), `StateFlow`
- **Image processing:** Android Canvas, `android.graphics.Bitmap`, `ColorMatrix`, `RenderEffect`
- **Logging:** Timber (debug + production trees)

### Package Structure
```
app/src/main/java/com/example/
├── ImageKitApplication.kt         # Application entry; Timber logging init
├── MainActivity.kt                # Single activity, edge-to-edge
├── model/
│   └── ImageModels.kt             # Data classes, enums, presets, tool definitions
├── util/
│   ├── BitmapProcessor.kt         # Core image mutation, filters, compression, storage
│   └── SampleImages.kt            # Procedural demo-image generators
├── viewmodel/
│   └── ImageEditorViewModel.kt    # Editor state, undo/redo history, batch, save
└── ui/
    ├── MainScreen.kt              # Root scaffold + floating glass tab bar
    ├── components/
    │   └── CommonComponents.kt    # Grouped cards, segmented controls, capsule buttons
    ├── screens/
    │   ├── HomeScreen.kt          # Import workbench, demo samples, quick tools
    │   ├── StudioEditorScreen.kt  # Canvas + tool dock + panel orchestration
    │   ├── BatchProcessScreen.kt  # Batch queue + progress
    │   ├── LibraryScreen.kt       # Export history
    │   └── editor/
    │       ├── AdjustmentSliderPanel.kt
    │       ├── AspectRatioPicker.kt
    │       ├── CompareToggle.kt
    │       ├── CropOverlayView.kt
    │       ├── EditorPreviewCanvas.kt
    │       ├── ExportOptionsPanel.kt
    │       ├── FilterStrip.kt
    │       └── MarkupToolbar.kt
    └── theme/
        ├── Color.kt               # iOS system semantic palette
        ├── Theme.kt               # Dark/light theme
        └── Type.kt                # Typographic scale
```

### Data Flow
Unidirectional: the UI observes immutable `EditorUiState` exposed via `StateFlow`
from `ImageEditorViewModel`. Non-destructive edits are kept on an `ArrayDeque<Bitmap>`
history stack (undo/redo). All filtering, cropping, scaling, and compression runs off
the main thread with cancellation support.

---

## Privacy & Logging

- **100% offline.** No network calls, no analytics, no crash reporting SDKs, no
  third-party tracking. Nothing leaves the device.
- **Local logging only** via Timber (tag `ImageKit`). Debug builds log diagnostics;
  production builds suppress verbose output and record no personally identifiable data.
- **Sandboxed storage.** Exports are written to the application's internal storage
  and shared back through the system photo picker — no broad external-storage
  permission is required.

---

## Restrictions & Known Limitations

- **Android only.** This is a native Android app (Kotlin/Compose); it does not build
  or run on iOS, desktop, or web.
- **Offline by design.** There is no cloud sync, account, or server component.
- **Debug builds only out of the box.** The repo ships a debug signing config. To
  produce a release build you must supply your own signing keystore via the
  `KEYSTORE_PATH` / `STORE_PASSWORD` / `KEY_PASSWORD` environment variables
  (see `app/build.gradle.kts`).
- **No EXIF metadata editing.** Image metadata (e.g. GPS, camera info) is not
  stripped or edited by the app.
- Internal Kotlin package namespace is `com.curiositygarage.imagekit` (a template default); the
  application ID is `com.curiositygarage.imagekit`.

---

## License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file.

Copyright (c) 2026 Curiosity Garage.
