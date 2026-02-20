package io.github.hdcodedev.composegif.ksp

internal fun ensureDurationMsAtLeastGestureBudget(
    durationMs: Int,
    fps: Int,
    gestures: List<GestureSpec>,
): Int {
    val requiredFrames = requiredGestureFrames(gestures)
    if (requiredFrames <= 0) return durationMs
    val requiredMs = framesToMsCeil(requiredFrames, fps)
    return maxOf(durationMs, requiredMs)
}

internal fun requiredGestureFrames(gestures: List<GestureSpec>): Int =
    gestures.sumOf { gesture ->
        when (gesture.type) {
            "PAUSE" -> gesture.frames.coerceAtLeast(0)
            "TAP" -> gesture.framesAfter.coerceAtLeast(0)
            "DRAG_PATH" -> {
                val holdFrames = gesture.holdStartFrames.coerceAtLeast(0)
                val releaseFrames = gesture.releaseFrames.coerceAtLeast(0)
                val waypointFrames = gesture.framesPerWaypoint.coerceAtLeast(0)
                val waypointCount = (gesture.points.size - 1).coerceAtLeast(0)
                holdFrames + (waypointFrames * waypointCount) + releaseFrames
            }
            else -> 0
        }
    }

internal fun framesToMsCeil(
    frames: Int,
    fps: Int,
): Int {
    if (frames <= 0) return 0
    val safeFps = fps.coerceAtLeast(1).toLong()
    val framesLong = frames.toLong().coerceAtLeast(0L)
    val ms = ((framesLong * 1000L) + safeFps - 1L) / safeFps
    return ms.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
}
