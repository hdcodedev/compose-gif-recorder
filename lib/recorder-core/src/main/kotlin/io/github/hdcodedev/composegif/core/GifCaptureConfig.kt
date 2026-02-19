package io.github.hdcodedev.composegif.core

public enum class GifTheme {
    LIGHT,
    DARK,
}

public data class GifCaptureConfig(
    val durationMs: Int = 1800,
    val fps: Int = 50,
    val widthPx: Int = 540,
    val heightPx: Int = 0,
    val theme: GifTheme = GifTheme.DARK,
)

public data class GifScenarioSpec(
    val name: String,
    val capture: GifCaptureConfig,
)

public class GifValidationException(message: String) : IllegalArgumentException(message)

public object GifCaptureValidator {
    public fun validate(spec: GifScenarioSpec) {
        requireName(spec.name)
        validate(spec.capture)
    }

    public fun validate(config: GifCaptureConfig) {
        if (config.durationMs <= 0) {
            throw GifValidationException("durationMs must be greater than 0.")
        }
        if (config.fps !in 1..120) {
            throw GifValidationException("fps must be in range [1, 120].")
        }
        if (config.widthPx <= 0) {
            throw GifValidationException("widthPx must be greater than 0.")
        }
        if (config.heightPx < 0) {
            throw GifValidationException("heightPx must be 0 (auto) or greater.")
        }
    }

    public fun requireName(name: String) {
        if (name.isBlank()) {
            throw GifValidationException("Scenario name cannot be blank.")
        }
        if (!name.matches(Regex("[a-zA-Z0-9_\\-]+"))) {
            throw GifValidationException("Scenario name must match [a-zA-Z0-9_-]+.")
        }
    }
}
