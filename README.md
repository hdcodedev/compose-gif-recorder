# compose-gif-recorder

<p align="center">
  <a href="https://central.sonatype.com/repository/maven-snapshots/io/github/hdcodedev/compose-gif-recorder-annotations/maven-metadata.xml">
    <img src="https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fio%2Fgithub%2Fhdcodedev%2Fcompose-gif-recorder-annotations%2Fmaven-metadata.xml&label=Snapshots&color=4285F4" />
  </a>
  <img src="https://img.shields.io/badge/Jetpack_Compose-1.10.3-4285F4?logo=jetpackcompose" />
  <img src="https://img.shields.io/badge/Kotlin-2.3.10-0095D5?logo=kotlin" />
  <img src="https://img.shields.io/badge/AGP-9.0.1-2E7D32?logo=android" />
  <img src="https://img.shields.io/badge/KSP-2.3.0-FF6F00" />
</p>

Standalone workspace for deterministic GIF capture from annotated Compose composables.

## Layout

- `lib/`: publishable library stack (annotations, core, KSP, Android runtime, Gradle plugin)
- `app/`: wizard-based Android demo app consuming recorder artifacts from `mavenLocal()`
- `tools/bootstrap-local.sh`: publishes library artifacts to local Maven
- `tools/record-gifs.sh`: publishes + runs end-to-end GIF capture in the demo app

## Quick start

```bash
cd /Users/hd/Projects/compose-gif-recorder
./tools/bootstrap-local.sh
./gradlew :app:listGifScenarios
./gradlew :app:recordGifsDebug
```

Single scenario:

```bash
./gradlew :app:recordGifDebug -PgifScenario=floating_orb_demo
```

GIF outputs are written under:

`app/artifacts/gifs`

## Add a new scenario

1. Create a top-level, parameterless `@Composable` function.
2. Add `@RecordGif` annotation.
3. Run `./gradlew :app:listGifScenarios` to verify discovery.
4. Run `./gradlew :app:recordGifsDebug` (or `recordGifDebug` for one scenario).

Example:

```kotlin
import androidx.compose.runtime.Composable
import io.github.hdcodedev.composegif.annotations.RecordGif

@Composable
@RecordGif(name = "my_new_animation", durationMs = 2200)
fun MyNewAnimationScenario() {
    // animation content
}
```

## ADB auto-discovery

`recordGifDebug` / `recordGifsDebug` try to find `adb` automatically in this order:

1. `PATH`
2. `$ANDROID_SDK_ROOT/platform-tools/adb`
3. `$ANDROID_HOME/platform-tools/adb`
4. `~/Library/Android/sdk/platform-tools/adb` (macOS common location)
5. `~/Android/Sdk/platform-tools/adb` (Linux common location)

If none are found, set `adbBin` explicitly in `app/build.gradle.kts`:

```kotlin
gifRecorder {
    adbBin.set("${System.getProperty(\"user.home\")}/Library/Android/sdk/platform-tools/adb")
}
```

## AGP/KSP compatibility note

This demo app uses built-in Kotlin and KSP together with:

`android.disallowKotlinSourceSets=false`

in `gradle.properties`.
