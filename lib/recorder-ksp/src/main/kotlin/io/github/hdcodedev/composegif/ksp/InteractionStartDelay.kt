package io.github.hdcodedev.composegif.ksp

internal const val DEFAULT_INTERACTION_START_DELAY_MS = 1000

internal fun applyInteractionStartDelay(
    gestures: List<GestureSpec>,
    interactionStartDelayMs: Int,
    fps: Int,
): List<GestureSpec> {
    if (gestures.isEmpty()) return gestures
    val delayFrames = interactionStartDelayFrames(interactionStartDelayMs, fps)
    if (delayFrames <= 0) return gestures
    return listOf(GestureSpec(type = "PAUSE", frames = delayFrames)) + gestures
}

internal fun interactionStartDelayFrames(
    interactionStartDelayMs: Int,
    fps: Int,
): Int {
    val clampedDelayMs = interactionStartDelayMs.coerceAtLeast(0).toLong()
    val safeFps = fps.coerceAtLeast(1).toLong()
    val frames = ((clampedDelayMs * safeFps) + 999L) / 1000L
    return frames.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
}
