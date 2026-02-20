package io.github.hdcodedev.composegif.ksp

import kotlin.test.Test
import kotlin.test.assertEquals

class InteractionStartDelayTest {
    @Test
    fun convertsDelayMsToFramesUsingCeil() {
        assertEquals(50, interactionStartDelayFrames(interactionStartDelayMs = 1000, fps = 50))
        assertEquals(10, interactionStartDelayFrames(interactionStartDelayMs = 333, fps = 30))
    }

    @Test
    fun negativeDelayProducesZeroFrames() {
        assertEquals(0, interactionStartDelayFrames(interactionStartDelayMs = -100, fps = 50))
    }

    @Test
    fun prependsPauseWhenGesturesExistAndDelayIsPositive() {
        val gestures =
            listOf(
                GestureSpec(type = "TAP", framesAfter = 3),
            )

        val result = applyInteractionStartDelay(gestures = gestures, interactionStartDelayMs = 1000, fps = 50)

        assertEquals(2, result.size)
        assertEquals("PAUSE", result.first().type)
        assertEquals(50, result.first().frames)
        assertEquals("TAP", result[1].type)
    }

    @Test
    fun doesNotPrependPauseWhenDelayIsZero() {
        val gestures =
            listOf(
                GestureSpec(type = "PAUSE", frames = 4),
            )

        val result = applyInteractionStartDelay(gestures = gestures, interactionStartDelayMs = 0, fps = 50)

        assertEquals(1, result.size)
        assertEquals("PAUSE", result.first().type)
        assertEquals(4, result.first().frames)
    }
}
