<h1 align="center">compose-gif-recorder</h1>

<p align="center">
  <a href="https://central.sonatype.com/artifact/io.github.hdcodedev/compose-gif-recorder-gradle-plugin">
    <img src="https://img.shields.io/maven-central/v/io.github.hdcodedev/compose-gif-recorder-gradle-plugin?label=Release&color=2E7D32" />
  </a>
  <img src="https://img.shields.io/badge/Jetpack_Compose-1.10.3-4285F4?logo=jetpackcompose" />
  <img src="https://img.shields.io/badge/Kotlin-2.3.10-0095D5?logo=kotlin" />
  <img src="https://img.shields.io/badge/AGP-9.0.1-2E7D32?logo=android" />
  <img src="https://img.shields.io/badge/KSP-2.3.0-FF6F00" />
</p>

<p align="center">
  Deterministic GIF recording for Jetpack Compose using a Gradle plugin
</p>

<p align="center">
<img width="566" height="221" alt="Screenshot 2026-02-20 at 11 12 21" src="https://github.com/user-attachments/assets/adb13344-d0b1-4c9a-a0f1-02e00601e98d" />
</p>

## Motivation

This plugin was originally created to automate GIF generation
for the [Charts wiki documentation](https://charts.harisdautovic.com/2.2.0/wiki/examples).

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

### 4. Record interactions

Use `interactionNodeTag` plus high-level `interactions`.

```kotlin
import androidx.compose.runtime.Composable
import io.github.hdcodedev.composegif.annotations.GifInteraction
import io.github.hdcodedev.composegif.annotations.GifInteractionTarget
import io.github.hdcodedev.composegif.annotations.GifInteractionType
import io.github.hdcodedev.composegif.annotations.GifSwipeDirection
import io.github.hdcodedev.composegif.annotations.GifSwipeDistance
import io.github.hdcodedev.composegif.annotations.GifSwipeSpeed
import io.github.hdcodedev.composegif.annotations.RecordGif

@RecordGif(
    name = "line_chart_with_interaction",
    durationMs = 2600,
    interactionStartDelayMs = 1000,
    interactionNodeTag = "LineChartPlot",
    interactions = [
        GifInteraction(type = GifInteractionType.PAUSE, frames = 24),
        GifInteraction(
            type = GifInteractionType.SWIPE,
            target = GifInteractionTarget.CENTER,
            direction = GifSwipeDirection.LEFT_TO_RIGHT,
            distance = GifSwipeDistance.MEDIUM,
            speed = GifSwipeSpeed.NORMAL,
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

- `interactionNodeTag` must match a test tag in your composable tree.
- `interactions` are expanded to deterministic low-level gestures by the KSP generator.
- `framesAfter` is part of the public friendly API (`GifInteraction`) and is still supported.
- Use `frames` for `PAUSE`; use `framesAfter` to add settle frames after `TAP`/`SWIPE`.
- For `TAP`, when `framesAfter = 0`, the processor falls back to `frames`.
- `interactionStartDelayMs` defaults to `1000` (1 second) so entry animations can settle before interactions begin.
- Set `interactionStartDelayMs = 0` if you want interactions to start immediately.
- `durationMs` is treated as a minimum. If configured interactions need more time, the recorder extends effective duration automatically.
- For swipes, prefer `speed = GifSwipeSpeed.FAST|NORMAL|SLOW` for high-level timing presets.
- Use `speed = GifSwipeSpeed.CUSTOM` with `travelFrames` / `holdStartFrames` / `releaseFrames` only when you need exact frame-level control.

#### Interaction API combinations (complete reference)

You can mix any supported enum values. The full friendly API surface is:

- `GifInteractionType`: `PAUSE`, `TAP`, `SWIPE`
- `GifInteractionTarget`: `CENTER`, `TOP`, `BOTTOM`, `LEFT`, `RIGHT`
- `GifSwipeDirection`: `LEFT_TO_RIGHT`, `RIGHT_TO_LEFT`, `TOP_TO_BOTTOM`, `BOTTOM_TO_TOP`
- `GifSwipeDistance`: `SHORT`, `MEDIUM`, `LONG`
- `GifSwipeSpeed`: `FAST`, `NORMAL`, `SLOW`, `CUSTOM`

Field usage by interaction type:

- `PAUSE`: use `frames`
- `TAP`: use `target`, optional `framesAfter` (or `frames` fallback when `framesAfter = 0`)
- `SWIPE`: use `target`, `direction`, `distance`, `speed`; optional `framesAfter`
- `SWIPE` + `CUSTOM`: also set `travelFrames`, `holdStartFrames`, `releaseFrames`

```kotlin
@RecordGif(
    interactionNodeTag = "Chart",
    interactions = [
        GifInteraction(type = GifInteractionType.PAUSE, frames = 12),
        GifInteraction(
            type = GifInteractionType.SWIPE,
            target = GifInteractionTarget.RIGHT,
            direction = GifSwipeDirection.BOTTOM_TO_TOP,
            distance = GifSwipeDistance.MEDIUM,
            speed = GifSwipeSpeed.CUSTOM,
            travelFrames = 10,
            holdStartFrames = 6,
            releaseFrames = 6,
            framesAfter = 6,
        ),
    ],
)
```

If you need exact control, `gestures` (coordinate-based) is still available as an advanced API.

### 5. Advance

#### Gesture API combinations (complete reference)

Low-level `gestures` support all three gesture types:

- `GifGestureType.PAUSE`: `frames`
- `GifGestureType.TAP`: `xFraction`, `yFraction`, `framesAfter`
- `GifGestureType.DRAG_PATH`: `points`, `holdStartFrames`, `framesPerWaypoint`, `releaseFrames`

```kotlin
@RecordGif(
    interactionNodeTag = "Chart",
    gestures = [
        GifGestureStep(type = GifGestureType.PAUSE, frames = 12),
        GifGestureStep(
            type = GifGestureType.TAP,
            xFraction = 0.8f,
            yFraction = 0.3f,
            framesAfter = 8,
        ),
        GifGestureStep(
            type = GifGestureType.DRAG_PATH,
            points = [
                GifFractionPoint(x = 0.2f, y = 0.5f),
                GifFractionPoint(x = 0.5f, y = 0.2f),
                GifFractionPoint(x = 0.8f, y = 0.5f),
            ],
            holdStartFrames = 6,
            framesPerWaypoint = 12,
            releaseFrames = 6,
        ),
    ],
)
```

### 6. Run tasks

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
