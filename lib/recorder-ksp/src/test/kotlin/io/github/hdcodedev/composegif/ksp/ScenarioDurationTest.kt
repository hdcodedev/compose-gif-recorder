package io.github.hdcodedev.composegif.ksp

import kotlin.test.Test
import kotlin.test.assertEquals

class ScenarioDurationTest {
    @Test
    fun keepsDurationWhenNoGestures() {
        val result =
            ensureDurationMsAtLeastGestureBudget(
                durationMs = 2600,
                fps = 50,
                gestures = emptyList(),
            )

        assertEquals(2600, result)
    }

    @Test
    fun extendsDurationToPauseFrameBudget() {
        val result =
            ensureDurationMsAtLeastGestureBudget(
                durationMs = 1000,
                fps = 50,
                gestures = listOf(GestureSpec(type = "PAUSE", frames = 80)),
            )

        // 80 frames at 50 fps => 1600 ms.
        assertEquals(1600, result)
    }

    @Test
    fun includesDragHoldTravelAndReleaseInFrameBudget() {
        val frames =
            requiredGestureFrames(
                gestures =
                    listOf(
                        GestureSpec(
                            type = "DRAG_PATH",
                            points =
                                listOf(
                                    PointSpec(0.1f, 0.5f),
                                    PointSpec(0.5f, 0.5f),
                                    PointSpec(0.9f, 0.5f),
                                ),
                            holdStartFrames = 2,
                            framesPerWaypoint = 4,
                            releaseFrames = 3,
                        ),
                    ),
            )

        // 2 hold + (2 waypoints * 4) + 3 release = 13.
        assertEquals(13, frames)
        assertEquals(260, framesToMsCeil(frames = frames, fps = 50))
    }

    @Test
    fun dragWithZeroWaypointFramesDoesNotAddTravelFrames() {
        val frames =
            requiredGestureFrames(
                gestures =
                    listOf(
                        GestureSpec(
                            type = "DRAG_PATH",
                            points =
                                listOf(
                                    PointSpec(0.1f, 0.5f),
                                    PointSpec(0.9f, 0.5f),
                                ),
                            holdStartFrames = 4,
                            framesPerWaypoint = 0,
                            releaseFrames = 4,
                        ),
                    ),
            )

        assertEquals(8, frames)
    }
}
