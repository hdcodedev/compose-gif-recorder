package io.github.hdcodedev.composegif.core

public enum class GifTheme {
    LIGHT,
    DARK,
}

public enum class GifGestureType {
    PAUSE,
    TAP,
    DRAG_PATH,
}

public data class GifFractionPoint(
    val x: Float,
    val y: Float,
)

public data class GifGestureStep(
    val type: GifGestureType = GifGestureType.PAUSE,
    val frames: Int = 0,
    val xFraction: Float = 0.5f,
    val yFraction: Float = 0.5f,
    val framesAfter: Int = 0,
    val points: List<GifFractionPoint> = emptyList(),
    val holdStartFrames: Int = 0,
    val framesPerWaypoint: Int = 0,
    val releaseFrames: Int = 0,
)

public data class GifCaptureConfig(
    val durationMs: Int = 1800,
    val fps: Int = 50,
    val widthPx: Int = 540,
    val heightPx: Int = 0,
    val theme: GifTheme = GifTheme.DARK,
    val interactionNodeTag: String = "",
    val gestures: List<GifGestureStep> = emptyList(),
)

public data class GifScenarioSpec(
    val name: String,
    val capture: GifCaptureConfig,
)

public class GifValidationException(
    message: String,
) : IllegalArgumentException(message)

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
        if (config.interactionNodeTag.isBlank() && config.gestures.isNotEmpty()) {
            throw GifValidationException("interactionNodeTag must be provided when gestures are configured.")
        }
        config.gestures.forEachIndexed { index, gesture ->
            validateGesture(index, gesture)
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

    private fun validateGesture(
        index: Int,
        gesture: GifGestureStep,
    ) {
        when (gesture.type) {
            GifGestureType.PAUSE -> {
                if (gesture.frames < 0) {
                    throw GifValidationException("Gesture[$index] pause frames must be >= 0.")
                }
            }
            GifGestureType.TAP -> {
                requireFraction(index, "xFraction", gesture.xFraction)
                requireFraction(index, "yFraction", gesture.yFraction)
                if (gesture.framesAfter < 0) {
                    throw GifValidationException("Gesture[$index] tap framesAfter must be >= 0.")
                }
            }
            GifGestureType.DRAG_PATH -> {
                if (gesture.points.size < 2) {
                    throw GifValidationException("Gesture[$index] drag path must contain at least 2 points.")
                }
                gesture.points.forEachIndexed { pointIndex, point ->
                    requireFraction(index, "points[$pointIndex].x", point.x)
                    requireFraction(index, "points[$pointIndex].y", point.y)
                }
                if (gesture.holdStartFrames < 0) {
                    throw GifValidationException("Gesture[$index] drag holdStartFrames must be >= 0.")
                }
                if (gesture.framesPerWaypoint < 0) {
                    throw GifValidationException("Gesture[$index] drag framesPerWaypoint must be >= 0.")
                }
                if (gesture.releaseFrames < 0) {
                    throw GifValidationException("Gesture[$index] drag releaseFrames must be >= 0.")
                }
            }
        }
    }

    private fun requireFraction(
        gestureIndex: Int,
        field: String,
        value: Float,
    ) {
        if (!value.isFinite() || value < 0f || value > 1f) {
            throw GifValidationException("Gesture[$gestureIndex] $field must be in range [0.0, 1.0].")
        }
    }
}
