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
  Deterministic GIF recording for Jetpack Compose scenarios using a Gradle plugin + <a href="lib/recorder-annotations/src/main/kotlin/io/github/hdcodedev/composegif/annotations/RecordGif.kt"><code>RecordGif.kt</code></a>
</p>

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

@Composable
@RecordGif(name = "my_new_animation", durationMs = 2200)
fun MyNewAnimationScenario() {
    // UI content
}
```

### 4. Run tasks

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
