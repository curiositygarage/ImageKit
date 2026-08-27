# ImageKit Studio — Offline Image Optimization & Editing Suite for Android

ImageKit Studio is a high-performance, **100% offline**, native Android image processing, batch compression, and editing application. Built using modern **Jetpack Compose**, **Kotlin Coroutines**, and **Android Canvas/NDK-grade Bitmap pipelines**, the application delivers an Apple Photos/iOS Human Interface Guidelines (HIG)-inspired design language with zero emoji iconography, translucent grouped surfaces, haptic feedback, and fluid gesture-based interactions.

---

## Table of Contents

1. [Executive Summary & Product Vision](#executive-summary--product-vision)
2. [Key Capabilities & Feature Specifications](#key-capabilities--feature-specifications)
   - [Studio Editor](#studio-editor)
   - [Batch Processing Engine](#batch-processing-engine)
   - [Library & History Manager](#library--history-manager)
   - [Sample Workbench Assets](#sample-workbench-assets)
3. [Design System & UI/UX Architecture](#design-system--uiux-architecture)
   - [Apple HIG & iOS Redesign Language](#apple-hig--ios-redesign-language)
   - [Zero-Emoji Vector Glyph Standard](#zero-emoji-vector-glyph-standard)
   - [Typography & Metric Rhythm](#typography--metric-rhythm)
   - [Translucent Surfaces & Layering](#translucent-surfaces--layering)
4. [Technical Architecture & Frameworks](#technical-architecture--frameworks)
   - [Tech Stack Overview](#tech-stack-overview)
   - [Core Directory & Package Structure](#core-directory--package-structure)
   - [State Management & MVVM Data Flow](#state-management--mvvm-data-flow)
   - [Bitmap Processing Engine (`BitmapProcessor.kt`)](#bitmap-processing-engine-bitmapprocessorkt)
5. [Storage, Privacy & Telemetry / Logging Architecture](#storage-privacy--telemetry--logging-architecture)
6. [Testing, Build, and Verification](#testing-build-and-verification)
7. [Building Locally](#building-locally)
8. [License & Credits](#license--credits)

---

## Executive Summary & Product Vision

ImageKit Studio was engineered to solve the everyday mobile bottlenecks of preparing digital assets for online forms, government visa/passport portals, web publishing, messaging platforms, and responsive UI design without sacrificing image quality or user privacy.

### Core Tenets
- **100% Offline & Private**: Zero network dependencies, no external cloud telemetry, and no unsolicited data transmission. All processing executes on the device's local CPU/GPU cores.
- **Native iOS Aesthetic & Tactile Polish**: Meticulous implementation of iOS grouped cards, rounded capsule controls, 0.5dp hairline separators, frosted glass bottom bars, and smooth transitions.
- **Precision Target-Byte Optimization**: Sophisticated binary search compression algorithm that hits strict file size limits (e.g. `< 200 KB` or `< 500 KB` requirements for government upload forms).
- **Non-Destructive Editing Workflow**: Full support for undo/redo history, before/after split-slider/long-press comparisons, and real-time live preview canvas.

---

## Key Capabilities & Feature Specifications

### 1. Studio Editor
- **Manual Freeform & Standardized Crop**:
  - Direct 8-handle touch overlay (4 corners + 4 center edge bars) with 48dp minimum touch targets.
  - Aspect Ratio presets: Freeform, Original, Passport 2×2 (1:1), Visa / ID (3:4), A4 / Letter Document (1:1.414), 16:9 Landscape, 9:16 Story/Reel, 4:3 Classic, 3:2 Photo.
  - Multi-axis orientation: 90° Clockwise/Counter-Clockwise rotations, Horizontal and Vertical flipping.
- **Web & Storage Optimization (Resize / Convert)**:
  - Format converter: JPEG, PNG, and modern WebP.
  - Compression quality slider (1% to 100%) with live size estimation and savings percentage badge.
  - Scale multiplier (25%, 50%, 75%, 100%) or manual exact pixel resolution locking with aspect ratio preservation.
  - Instant Web presets: Web Banner (1920×600), Thumbnail (400×400), Email Attachment (< 1 MB), WebP Max Efficiency.
  - Government / Visa Target Size Preset buttons: 100 KB, 200 KB, 500 KB, 1 MB, 2 MB with binary search quantization.
- **Color & Tone Adjustments**:
  - Live hardware-accelerated adjustments for Brightness (-100 to +100), Contrast (-100 to +100), Saturation (-100 to +100), Temperature / White Balance (-100 to +100), Vignette (0 to 100), and Sharpness (0 to 100).
- **Artistic Filter Presets**:
  - Original, Vivid Boost, Dramatic Monochrome, Warm Amber, Cool Marine, Cyberpunk / Neon, Vintage Film, Document B&W Scanner, Sepia Warmth, Invert Negative.
- **Redaction & Privacy Markup**:
  - Redaction types: Gaussian Blur Mask, Blackout Solid Box, and Pixelate Censorship.
  - Tap-and-drag interactive region bounding with deletion handles.
- **Live Comparison & Canvas Viewport**:
  - Split-slider interactive comparator.
  - Hold-to-reveal original button (simulates Apple Photos comparison behavior).
  - Dynamic aspect-preserving canvas with grid overlay toggles.

### 2. Batch Processing Engine
- Multi-image selection from internal gallery or synthetic test suites.
- Universal recipe application: apply unified format conversion (JPEG/PNG/WebP), resolution downscaling, target byte limit, and compression presets across up to 50 photos simultaneously.
- Asynchronous parallel coroutine worker with per-item progress indicators, error boundaries, aggregate storage savings summary, and single-tap batch export to the local gallery.

### 3. Library & History Manager
- Grouped list of recent export artifacts with format badges, dimensions, byte sizes, and timestamps.
- Single-tap reloading back into the studio editor, direct image preview modals, and local disk deletion management.

### 4. Sample Workbench Assets
- Built-in vector and high-resolution synthetic image generators for instant testing without needing real device photos:
  - High-Resolution Landscape Sample (HDR mountains, sunset gradients, foreground flora).
  - High-Contrast Document / Receipt Sample (simulated invoice typography, barcodes, watermarks).
  - High-Density RAW Workbench Photo (fine detail test patterns, 2400×1600 px target).

---

## Design System & UI/UX Architecture

### Apple HIG & iOS Redesign Language
The app follows Apple Human Interface Guidelines implemented in Jetpack Compose:
- **Grouped Table Layouts**: Rounded inset cards (`RoundedCornerShape(12.dp)`) nestled on high-contrast backgrounds (`Color(0xFF000000)` / `Color(0xFF1C1C1E)`).
- **Hairline Separators**: Subtle 0.5dp borders (`Color(0x33545458)`) defining boundaries between list rows and control blocks.
- **Floating Glass Tab Bar**: A pill-shaped floating surface (`Color(0xE61C1C1E)` with `RoundedCornerShape(32.dp)` and 16dp elevation) floating above the Android navigation gesture area with `navigationBarsPadding()`.
- **Sliding Segmented Controls**: Capsule switchers with animated sliding indicator pills (`Color(0xFF2C2C2E)` active on `Color(0xFF1C1C1E)` base).
- **Prominent Capsule Buttons**: Full-width action buttons with iOS System Blue (`#0A84FF`), spring press animations, and high-contrast typography.

### Zero-Emoji Vector Glyph Standard
All UI elements and data models strictly use monochromatic, scalable vector icons (`androidx.compose.material.icons` and custom `ImageVector` paths):
- Navigation: `Icons.Filled.Home`, `Icons.Filled.AutoFixHigh`, `Icons.Filled.Layers`, `Icons.Filled.PhotoLibrary`
- Editing Tools: `Icons.Default.Crop`, `Icons.Default.Tune`, `Icons.Default.PhotoSizeSelectLarge`, `Icons.Default.Security`
- Transformations: `Icons.Default.RotateRight`, `Icons.Default.RotateLeft`, `Icons.Default.Flip`
- Markup & Safety: `Icons.Default.VisibilityOff`, `Icons.Default.Check`, `Icons.Default.Close`, `Icons.Default.Delete`

### Typography & Metric Rhythm
Defined in `com.curiositygarage.imagekit.ui.theme.Type.kt`:
- **Display / Large Title**: 34sp Bold with tight tracking (`(-0.4).sp`) for iOS page headings.
- **Headline / Title Medium**: 17sp–20sp SemiBold with `(-0.3).sp` tracking.
- **Body Regular**: 15sp with `(-0.1).sp` tracking.
- **Subheadline / Callout**: 13sp with neutral tracking.
- **Footnote / Caption**: 10sp–11sp SemiBold for tab bars and badges.

### Color Palette (Semantic Tokens)
Defined in `com.curiositygarage.imagekit.ui.theme.Color.kt`:
```kotlin
val IosSystemBlue = Color(0xFF0A84FF)
val IosSystemGreen = Color(0xFF30D158)
val IosSystemRed = Color(0xFFFF453A)
val IosSystemOrange = Color(0xFFFF9F0A)
val IosSystemGray = Color(0xFF8E8E93)

val IosSystemBackgroundDark = Color(0xFF000000)
val IosSecondarySystemBackgroundDark = Color(0xFF1C1C1E)
val IosTertiarySystemBackgroundDark = Color(0xFF2C2C2E)
val IosGroupedBackgroundDark = Color(0xFF000000)
val IosSecondaryGroupedBackgroundDark = Color(0xFF1C1C1E)

val IosLabelPrimary = Color(0xFFFFFFFF)
val IosLabelSecondary = Color(0x99EBEBF5)
val IosLabelTertiary = Color(0x4DEBEBF5)
val IosSeparatorOpaque = Color(0x33545458)
```

---

## Technical Architecture & Frameworks

### Tech Stack Overview
- **Language**: 100% Kotlin (1.9.x+)
- **UI Framework**: Jetpack Compose (Material 3 base customized to iOS HIG)
- **Concurrency & Pipelines**: Kotlin Coroutines (`Dispatchers.IO`, `Dispatchers.Default`), `StateFlow`, `SharedFlow`
- **Image Processing**: Android Native Canvas, `android.graphics.Bitmap`, `android.graphics.ColorMatrix`, `android.graphics.Paint`, `android.graphics.RenderEffect`
- **Build System**: Gradle Kotlin DSL (`build.gradle.kts`) with Version Catalog (`libs.versions.toml`)

### Core Directory & Package Structure

```
app/src/main/java/com/example/
├── ImageKitApplication.kt         # Application lifecycle entry point
├── MainActivity.kt                # Single activity with edge-to-edge window insets
│
├── model/
│   └── ImageModels.kt             # Data classes, enums, tool definitions, filter presets
│
├── util/
│   ├── BitmapProcessor.kt         # Core image mutation, filters, and compression algorithms
│   ├── ImageStorageManager.kt     # Local disk I/O, cache management, and artifact persistence
│   └── SampleImages.kt            # High-fidelity procedural test image generators
│
├── viewmodel/
│   ├── ImageEditorViewModel.kt    # Studio editor state holder, history stack, undo/redo
│   └── BatchProcessViewModel.kt   # Batch execution queue, parallel worker orchestration
│
└── ui/
    ├── MainScreen.kt              # Root scaffold, floating glass tab bar, tab routing
    ├── components/
    │   └── CommonComponents.kt    # iOS grouped cards, segmented controls, capsule buttons
    ├── screens/
    │   ├── HomeScreen.kt          # Quick launch workbench, preset shortcuts, featured metrics
    │   ├── StudioEditorScreen.kt  # Hero canvas, tool dock, active editor panel orchestration
    │   ├── BatchProcessScreen.kt  # Multi-file queue, preset applicator, progress monitor
    │   ├── LibraryScreen.kt       # Export history, artifact viewer, storage footprint
    │   └── editor/
    │       ├── AdjustmentSliderPanel.kt # Brightness, contrast, vignette sliders
    │       ├── AspectRatioPicker.kt     # Freeform & preset crop selector + orientation
    │       ├── CompareToggle.kt         # Hold-to-reveal and split-slider comparison
    │       ├── CropOverlayView.kt       # 8-handle touch gesture crop viewport
    │       ├── EditorPreviewCanvas.kt   # Interactive photo viewport with pinch & pan
    │       ├── ExportOptionsPanel.kt    # Format, resolution, target-byte controls
    │       ├── FilterStrip.kt           # Real-time filter thumbnail picker
    │       └── MarkupToolbar.kt         # Blur, blackout, and pixelation tools
    └── theme/
        ├── Color.kt               # iOS System semantic color palette
        ├── Theme.kt               # Centralized Dark/Light theme composable
        └── Type.kt                # SF-Pro typographic scale and letter-spacing
```

### State Management & MVVM Data Flow
1. **Unidirectional Data Flow**: The UI observes immutable `EditorUiState` and `BatchUiState` exposed via `StateFlow` from ViewModels.
2. **History Management**: Non-destructive transformations are kept in an `ArrayDeque<Bitmap>` stack inside `ImageEditorViewModel`, supporting full multi-level Undo and Redo operations.
3. **Off-Main-Thread Processing**: Every filtering, cropping, scaling, and compression pass is dispatched strictly to `Dispatchers.Default` or `Dispatchers.IO` with cancellation support.

### Bitmap Processing Engine (`BitmapProcessor.kt`)
- **Binary Search Target-Byte Compression**: Computes the optimal quality factor `Q ∈ [5, 100]` to compress an image within a given byte threshold using iterative binary search in fewer than 6 iterations.
- **Color Matrix Filters**: Uses optimized 4×5 `ColorMatrix` arrays for fast hardware-accelerated color conversions (Monochrome, Warm, Cool, Sepia, Invert, Vivid).
- **Pixelation & Redaction Algorithms**: Downsamples bounding sub-rectangles onto scaled intermediate bitmaps with nearest-neighbor interpolation or box-blur kernels before blitting back onto the target canvas.

---

## Storage, Privacy & Telemetry / Logging Architecture

- **Zero Remote Logging**: No TemplateDep Crashlytics, Google Analytics, or third-party tracking libraries are included.
- **Local Application Logging**: Standard Android `android.util.Log` wrappers are utilized for debug diagnostics:
  - Tag: `ImageKit_Studio`
  - Lifecycle and memory profiling log levels: `Log.d` for compression timing metrics, `Log.w` for memory warnings, `Log.e` for unrecoverable I/O faults.
- **Isolated Storage**: Output images are saved strictly to the application's internal sandboxed storage directory (`context.filesDir/exports/`) or passed to the system photo picker without requiring broad external storage permissions.

---

## Testing, Build, and Verification

### Compilation
The project compiles cleanly using standard Android Gradle toolchains:
```bash
gradle assembleDebug
```

### Verification Checks
- **Emoji Elimination**: UI layer contains zero Unicode emoji characters in text strings, resource XMLs, and data classes.
- **Accessibility**: All interactive elements maintain touch targets >= 48dp with semantic `testTag` descriptors (e.g., `testTag("nav_tab_home")`, `testTag("apply_crop_button")`).
- **Memory Safety**: `Bitmap.recycle()` is managed properly to prevent OOM errors during high-resolution processing.

---

## Building Locally

### Prerequisites (not in the repo — add your own)
The repo intentionally gitignores machine-specific files. Before building, create these locally:

1. `local.properties` in the project root:
   ```properties
   sdk.dir=/path/to/your/Android/Sdk
   ```
   (Replace with your actual Android SDK path.)
   - OR set the `ANDROID_HOME` / `ANDROID_SDK_ROOT` environment variable instead.

2. `debug.keystore` in the project root (used by `debugConfig` signing in app/build.gradle.kts).
   - A fresh debug keystore is auto-created the first time you run a debug build in Android Studio, OR you can generate one with:
     ```bash
     keytool -genkey -v -keystore debug.keystore -storepass android -alias androiddebugkey \
       -keypass android -dname "CN=Android Debug,O=Android,C=US"
     ```
   - Note: the file is gitignored so it never gets committed.

### Build
From the project root:
```bash
gradle assembleDebug
```
This produces the APK at:
`app/build/outputs/apk/debug/app-debug.apk`

### Install to a connected device
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Notes
- This is a fully offline app — it does not require any API keys or `.env` credentials. The `.env.example` is optional and unused by default; remove the `signingConfig = signingConfigs.getByName("debugConfig")` line only if you set up release signing, not for local debug builds.
- If `gradle` is not on your PATH, use the Gradle wrapper (`./gradlew`) or the full path to your Gradle install.

---

## License & Credits

Built with Kotlin & Jetpack Compose. Designed following the Apple Human Interface Guidelines and Material 3 foundational engineering principles.
