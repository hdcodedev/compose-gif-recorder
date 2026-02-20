package io.github.hdcodedev.composegif.ksp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InteractionGestureExpanderTest {
    @Test
    fun mapsPauseInteractionToPauseGesture() {
        val gestures =
            InteractionGestureExpander.expand(
                InteractionSpec(
                    type = "PAUSE",
                    frames = 12,
                ),
            )

        assertEquals(1, gestures.size)
        assertEquals("PAUSE", gestures.single().type)
        assertEquals(12, gestures.single().frames)
    }

    @Test
    fun mapsTapInteractionToTargetPointAndUsesFramesFallback() {
        val gestures =
            InteractionGestureExpander.expand(
                InteractionSpec(
                    type = "TAP",
                    target = "RIGHT",
                    frames = 7,
                    framesAfter = 0,
                ),
            )

        val tap = gestures.single()
        assertEquals("TAP", tap.type)
        assertEquals(0.70f, tap.xFraction)
        assertEquals(0.5f, tap.yFraction)
        assertEquals(7, tap.framesAfter)
    }

    @Test
    fun mapsSwipeInteractionWithFramesAfterToDragAndTrailingPause() {
        val gestures =
            InteractionGestureExpander.expand(
                InteractionSpec(
                    type = "SWIPE",
                    target = "TOP",
                    direction = "RIGHT_TO_LEFT",
                    distance = "LONG",
                    travelFrames = 9,
                    holdStartFrames = 4,
                    releaseFrames = 3,
                    framesAfter = 5,
                ),
            )

        assertEquals(2, gestures.size)
        val drag = gestures[0]
        assertEquals("DRAG_PATH", drag.type)
        assertEquals(4, drag.holdStartFrames)
        assertEquals(9, drag.framesPerWaypoint)
        assertEquals(3, drag.releaseFrames)
        assertEquals(2, drag.points.size)
        assertTrue(drag.points[0].x > drag.points[1].x)
        assertEquals(0.30f, drag.points[0].y)
        assertEquals("PAUSE", gestures[1].type)
        assertEquals(5, gestures[1].frames)
    }

    @Test
    fun mapsAllSwipeDirections() {
        val leftToRight =
            InteractionGestureExpander
                .expand(
                    InteractionSpec(type = "SWIPE", direction = "LEFT_TO_RIGHT"),
                ).first()
        val rightToLeft =
            InteractionGestureExpander
                .expand(
                    InteractionSpec(type = "SWIPE", direction = "RIGHT_TO_LEFT"),
                ).first()
        val topToBottom =
            InteractionGestureExpander
                .expand(
                    InteractionSpec(type = "SWIPE", direction = "TOP_TO_BOTTOM"),
                ).first()
        val bottomToTop =
            InteractionGestureExpander
                .expand(
                    InteractionSpec(type = "SWIPE", direction = "BOTTOM_TO_TOP"),
                ).first()

        assertTrue(leftToRight.points.first().x < leftToRight.points.last().x)
        assertTrue(rightToLeft.points.first().x > rightToLeft.points.last().x)
        assertTrue(topToBottom.points.first().y < topToBottom.points.last().y)
        assertTrue(bottomToTop.points.first().y > bottomToTop.points.last().y)
    }

    @Test
    fun mapsSwipeDistances() {
        val shortSwipe =
            InteractionGestureExpander
                .expand(
                    InteractionSpec(type = "SWIPE", distance = "SHORT"),
                ).first()
        val mediumSwipe =
            InteractionGestureExpander
                .expand(
                    InteractionSpec(type = "SWIPE", distance = "MEDIUM"),
                ).first()
        val longSwipe =
            InteractionGestureExpander
                .expand(
                    InteractionSpec(type = "SWIPE", distance = "LONG"),
                ).first()

        val shortDelta = shortSwipe.points.last().x - shortSwipe.points.first().x
        val mediumDelta = mediumSwipe.points.last().x - mediumSwipe.points.first().x
        val longDelta = longSwipe.points.last().x - longSwipe.points.first().x

        assertTrue(shortDelta < mediumDelta)
        assertTrue(mediumDelta < longDelta)
    }

    @Test
    fun unknownInteractionTypeProducesNoGestures() {
        val gestures =
            InteractionGestureExpander.expand(
                InteractionSpec(type = "UNKNOWN"),
            )
        assertTrue(gestures.isEmpty())
    }
}
