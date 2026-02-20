package io.github.hdcodedev.composegif.ksp

internal data class InteractionSpec(
    val type: String,
    val frames: Int = 0,
    val framesAfter: Int = 0,
    val target: String = "CENTER",
    val direction: String = "LEFT_TO_RIGHT",
    val distance: String = "MEDIUM",
    val speed: String = "CUSTOM",
    val travelFrames: Int = 8,
    val holdStartFrames: Int = 0,
    val releaseFrames: Int = 0,
)

internal data class GestureSpec(
    val type: String,
    val frames: Int = 0,
    val xFraction: Float = 0.5f,
    val yFraction: Float = 0.5f,
    val framesAfter: Int = 0,
    val points: List<PointSpec> = emptyList(),
    val holdStartFrames: Int = 0,
    val framesPerWaypoint: Int = 0,
    val releaseFrames: Int = 0,
)

internal data class PointSpec(
    val x: Float,
    val y: Float,
)

internal object InteractionGestureExpander {
    fun expand(spec: InteractionSpec): List<GestureSpec> =
        when (spec.type) {
            "PAUSE" ->
                listOf(
                    GestureSpec(
                        type = "PAUSE",
                        frames = spec.frames,
                    ),
                )
            "TAP" -> {
                val point = targetToPoint(spec.target)
                listOf(
                    GestureSpec(
                        type = "TAP",
                        xFraction = point.x,
                        yFraction = point.y,
                        framesAfter = if (spec.framesAfter > 0) spec.framesAfter else spec.frames,
                    ),
                )
            }
            "SWIPE" -> {
                val swipePoints =
                    swipePoints(target = spec.target, direction = spec.direction, distance = spec.distance)
                val timing = swipeTiming(spec)
                buildList {
                    add(
                        GestureSpec(
                            type = "DRAG_PATH",
                            points = swipePoints,
                            holdStartFrames = timing.holdStartFrames,
                            framesPerWaypoint = timing.travelFrames,
                            releaseFrames = timing.releaseFrames,
                        ),
                    )
                    if (spec.framesAfter > 0) {
                        add(
                            GestureSpec(
                                type = "PAUSE",
                                frames = spec.framesAfter,
                            ),
                        )
                    }
                }
            }
            else -> emptyList()
        }

    fun targetToPoint(target: String): PointSpec =
        when (target) {
            "TOP" -> PointSpec(x = 0.5f, y = 0.30f)
            "BOTTOM" -> PointSpec(x = 0.5f, y = 0.70f)
            "LEFT" -> PointSpec(x = 0.30f, y = 0.5f)
            "RIGHT" -> PointSpec(x = 0.70f, y = 0.5f)
            else -> PointSpec(x = 0.5f, y = 0.5f)
        }

    fun swipePoints(
        target: String,
        direction: String,
        distance: String,
    ): List<PointSpec> {
        val offset =
            when (distance) {
                "SHORT" -> 0.18f
                "LONG" -> 0.40f
                else -> 0.30f
            }
        val horizontalLaneY =
            when (target) {
                "TOP" -> 0.30f
                "BOTTOM" -> 0.70f
                else -> 0.5f
            }
        val verticalLaneX =
            when (target) {
                "LEFT" -> 0.30f
                "RIGHT" -> 0.70f
                else -> 0.5f
            }

        return when (direction) {
            "RIGHT_TO_LEFT" ->
                listOf(
                    PointSpec(x = (0.5f + offset).coerceIn(0f, 1f), y = horizontalLaneY),
                    PointSpec(x = (0.5f - offset).coerceIn(0f, 1f), y = horizontalLaneY),
                )
            "TOP_TO_BOTTOM" ->
                listOf(
                    PointSpec(x = verticalLaneX, y = (0.5f - offset).coerceIn(0f, 1f)),
                    PointSpec(x = verticalLaneX, y = (0.5f + offset).coerceIn(0f, 1f)),
                )
            "BOTTOM_TO_TOP" ->
                listOf(
                    PointSpec(x = verticalLaneX, y = (0.5f + offset).coerceIn(0f, 1f)),
                    PointSpec(x = verticalLaneX, y = (0.5f - offset).coerceIn(0f, 1f)),
                )
            else ->
                listOf(
                    PointSpec(x = (0.5f - offset).coerceIn(0f, 1f), y = horizontalLaneY),
                    PointSpec(x = (0.5f + offset).coerceIn(0f, 1f), y = horizontalLaneY),
                )
        }
    }

    private fun swipeTiming(spec: InteractionSpec): SwipeTiming =
        if (spec.speed == "CUSTOM") {
            SwipeTiming(
                travelFrames = spec.travelFrames,
                holdStartFrames = spec.holdStartFrames,
                releaseFrames = spec.releaseFrames,
            )
        } else {
            speedToTiming(spec.speed)
        }

    private fun speedToTiming(speed: String): SwipeTiming =
        when (speed) {
            "FAST" -> SwipeTiming(travelFrames = 6, holdStartFrames = 2, releaseFrames = 2)
            "SLOW" -> SwipeTiming(travelFrames = 12, holdStartFrames = 10, releaseFrames = 10)
            else -> SwipeTiming(travelFrames = 8, holdStartFrames = 6, releaseFrames = 6)
        }
}

private data class SwipeTiming(
    val travelFrames: Int,
    val holdStartFrames: Int,
    val releaseFrames: Int,
)
