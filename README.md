<h1 align="center">compose-gif-recorder</h1>

<p align="center">
  <a href="https://central.sonatype.com/artifact/io.github.hdcodedev/compose-gif-recorder-gradle-plugin">
    <img src="https://img.shields.io/maven-central/v/io.github.hdcodedev/compose-gif-recorder-gradle-plugin?label=Release&color=2E7D32" />
  </a>
  <a href="https://central.sonatype.com/repository/maven-snapshots/io/github/hdcodedev/compose-gif-recorder-annotations/maven-metadata.xml">
    <img src="https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fio%2Fgithub%2Fhdcodedev%2Fcompose-gif-recorder-annotations%2Fmaven-metadata.xml&label=Snapshot&color=4285F4" />
  </a>
  <img src="https://img.shields.io/badge/Jetpack_Compose-1.10.3-4285F4?logo=jetpackcompose" />
  <img src="https://img.shields.io/badge/Kotlin-2.3.10-0095D5?logo=kotlin" />
  <img src="https://img.shields.io/badge/AGP-9.0.1-2E7D32?logo=android" />
  <img src="https://img.shields.io/badge/KSP-2.3.0-FF6F00" />
</p>

<p align="center">
  Deterministic GIF recording for Jetpack Compose scenarios using a Gradle plugin
</p>

<p align="center">
<img width="566" height="221" alt="Screenshot 2026-02-20 at 11 12 21" src="https://github.com/user-attachments/assets/adb13344-d0b1-4c9a-a0f1-02e00601e98d" />
</p>

## Motivation

This plugin was originally created to automate GIF generation
for the Compose [Charts wiki documentation](https://charts.harisdautovic.com/2.2.0/wiki/examples).

Whenever chart styles, animations, or APIs change,
all documentation GIFs can be easily regenerated in an automated way.

## Requirements

- Android app module using Jetpack Compose
- Gradle plugins: Android application, Kotlin Compose, KSP
- Installed tools on `PATH`: `adb`, `ffmpeg`, `ffprobe`, `gifsicle`
- Running emulator or connected Android device

## Use In Your App

### 1. Apply plugins in your app module

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("io.github.hdcodedev.compose-gif-recorder") version "<version>"
}
```

The plugin wires recorder dependencies automatically (`annotations`, `core`, `android`, `ksp`).

### 2. Configure the recorder

```kotlin
gifRecorder {
    applicationId.set("com.example.app")
    // Optional; default is "artifacts/gifs" in the app module
    outputDir.set(layout.projectDirectory.dir("artifacts/gifs"))
}
```

### 3. Add a GIF scenario

Rules:

- Top-level function
- `@Composable`
- No parameters

```kotlin
import androidx.compose.runtime.Composable
import io.github.hdcodedev.composegif.annotations.RecordGif

@RecordGif
@Composable
fun MyNewAnimationScenario() {
    // UI content
}
```

### 4. Optional: record interactions (friendly API)

Use `interactionNodeTag` plus high-level `interactions` to replay deterministic input without coordinates.

```kotlin
import androidx.compose.runtime.Composable
import io.github.hdcodedev.composegif.annotations.GifInteraction
import io.github.hdcodedev.composegif.annotations.GifInteractionTarget
import io.github.hdcodedev.composegif.annotations.GifInteractionType
import io.github.hdcodedev.composegif.annotations.GifSwipeDirection
import io.github.hdcodedev.composegif.annotations.GifSwipeDistance
import io.github.hdcodedev.composegif.annotations.RecordGif

@RecordGif(
    name = "line_chart_with_interaction",
    durationMs = 2600,
    interactionNodeTag = "LineChartPlot",
    interactions = [
        GifInteraction(type = GifInteractionType.PAUSE, frames = 24),
        GifInteraction(
            type = GifInteractionType.SWIPE,
            target = GifInteractionTarget.CENTER,
            direction = GifSwipeDirection.LEFT_TO_RIGHT,
            distance = GifSwipeDistance.MEDIUM,
            travelFrames = 8,
            holdStartFrames = 8,
            releaseFrames = 8,
        ),
        GifInteraction(
            type = GifInteractionType.TAP,
            target = GifInteractionTarget.RIGHT,
            framesAfter = 10,
        ),
    ],
)
@Composable
fun LineChartWithInteraction() {
    // UI content
}
```

`interactionNodeTag` must match a test tag in your composable tree.
`interactions` are expanded to deterministic low-level gestures by the KSP generator.

If you need exact control, `gestures` (coordinate-based) is still available as an advanced API.

### 5. Run tasks

List available scenarios:

```bash
./gradlew :app:listGifScenarios
```

Record one scenario:

```bash
./gradlew :app:recordGifDebug -PgifScenario=my_new_animation
```

Record all scenarios:

```bash
./gradlew :app:recordGifsDebug
```

Generated GIFs are written to `app/artifacts/gifs` (or your configured `outputDir`).
If your application module is not named `app`, replace `:app:` in the commands.

## Common Configuration

You can override binaries/device selection when needed:

```kotlin
gifRecorder {
    adbSerial.set("emulator-5554") // default: auto
    adbBin.set("adb")
    ffmpegBin.set("ffmpeg")
    ffprobeBin.set("ffprobe")
    gifsicleBin.set("gifsicle")
}
```
