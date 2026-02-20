package io.github.hdcodedev.composegif.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
public annotation class RecordGif(
    val name: String = "",
    val durationMs: Int = 3000,
    val fps: Int = 50,
    val widthPx: Int = 540,
    val heightPx: Int = 0,
    val theme: GifTheme = GifTheme.DARK,
    val interactionNodeTag: String = "",
    val interactions: Array<GifInteraction> = [],
    val gestures: Array<GifGestureStep> = [],
)

public enum class GifTheme {
    LIGHT,
    DARK,
}

public enum class GifGestureType {
    PAUSE,
    TAP,
    DRAG_PATH,
}

public enum class GifInteractionType {
    PAUSE,
    TAP,
    SWIPE,
}

public enum class GifInteractionTarget {
    CENTER,
    TOP,
    BOTTOM,
    LEFT,
    RIGHT,
}

public enum class GifSwipeDirection {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT,
    TOP_TO_BOTTOM,
    BOTTOM_TO_TOP,
}

public enum class GifSwipeDistance {
    SHORT,
    MEDIUM,
    LONG,
}

public enum class GifSwipeSpeed {
    CUSTOM,
    FAST,
    NORMAL,
    SLOW,
}

public annotation class GifInteraction(
    val type: GifInteractionType = GifInteractionType.PAUSE,
    val frames: Int = 0,
    val framesAfter: Int = 0,
    /**
     * Target lane for the interaction.
     *
     * For taps, this is the tap point.
     *
     * For swipes, this selects the lane perpendicular to the swipe direction:
     * - horizontal swipes (`LEFT_TO_RIGHT`, `RIGHT_TO_LEFT`) use `TOP`/`BOTTOM` for Y lane,
     * - vertical swipes (`TOP_TO_BOTTOM`, `BOTTOM_TO_TOP`) use `LEFT`/`RIGHT` for X lane,
     * - other values fall back to center lane.
     */
    val target: GifInteractionTarget = GifInteractionTarget.CENTER,
    val direction: GifSwipeDirection = GifSwipeDirection.LEFT_TO_RIGHT,
    val distance: GifSwipeDistance = GifSwipeDistance.MEDIUM,
    /**
     * High-level swipe timing preset.
     *
     * Use `CUSTOM` to control timing with `travelFrames`, `holdStartFrames`, and `releaseFrames`.
     */
    val speed: GifSwipeSpeed = GifSwipeSpeed.CUSTOM,
    val travelFrames: Int = 8,
    val holdStartFrames: Int = 0,
    val releaseFrames: Int = 0,
)

public annotation class GifFractionPoint(
    val x: Float,
    val y: Float,
)

public annotation class GifGestureStep(
    val type: GifGestureType = GifGestureType.PAUSE,
    val frames: Int = 0,
    val xFraction: Float = 0.5f,
    val yFraction: Float = 0.5f,
    val framesAfter: Int = 0,
    val points: Array<GifFractionPoint> = [],
    val holdStartFrames: Int = 0,
    val framesPerWaypoint: Int = 0,
    val releaseFrames: Int = 0,
)
