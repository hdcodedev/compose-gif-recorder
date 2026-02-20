package io.github.hdcodedev.composegif.ksp

import io.github.hdcodedev.composegif.annotations.GifGestureType
import io.github.hdcodedev.composegif.annotations.GifInteractionTarget
import io.github.hdcodedev.composegif.annotations.GifInteractionType
import io.github.hdcodedev.composegif.annotations.GifSwipeDirection
import io.github.hdcodedev.composegif.annotations.GifSwipeDistance
import io.github.hdcodedev.composegif.annotations.GifSwipeSpeed
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InteractionGestureExpanderTest {
    @Test
    fun mapsPauseInteractionToPauseGesture() {
        val gestures =
            InteractionGestureExpander.expand(
                InteractionSpec(
                    type = GifInteractionType.PAUSE.name,
                    frames = 12,
                ),
            )

        assertEquals(1, gestures.size)
        assertEquals(GifGestureType.PAUSE.name, gestures.single().type)
        assertEquals(12, gestures.single().frames)
    }

    @Test
    fun mapsTapInteractionToTargetPointAndUsesFramesFallback() {
        val gestures =
            InteractionGestureExpander.expand(
                InteractionSpec(
                    type = GifInteractionType.TAP.name,
                    target = GifInteractionTarget.RIGHT.name,
                    frames = 7,
                    framesAfter = 0,
                ),
            )

        val tap = gestures.single()
        assertEquals(GifGestureType.TAP.name, tap.type)
        assertEquals(0.70f, tap.xFraction)
        assertEquals(0.5f, tap.yFraction)
        assertEquals(7, tap.framesAfter)
    }

    @Test
    fun mapsSwipeInteractionWithFramesAfterToDragAndTrailingPause() {
        val gestures =
            InteractionGestureExpander.expand(
                InteractionSpec(
                    type = GifInteractionType.SWIPE.name,
                    target = GifInteractionTarget.TOP.name,
                    direction = GifSwipeDirection.RIGHT_TO_LEFT.name,
                    distance = GifSwipeDistance.LONG.name,
                    travelFrames = 9,
                    holdStartFrames = 4,
                    releaseFrames = 3,
                    framesAfter = 5,
                ),
            )

        assertEquals(2, gestures.size)
        val drag = gestures[0]
        assertEquals(GifGestureType.DRAG_PATH.name, drag.type)
        assertEquals(4, drag.holdStartFrames)
        assertEquals(9, drag.framesPerWaypoint)
        assertEquals(3, drag.releaseFrames)
        assertEquals(2, drag.points.size)
        assertTrue(drag.points[0].x > drag.points[1].x)
        assertEquals(0.30f, drag.points[0].y)
        assertEquals(GifGestureType.PAUSE.name, gestures[1].type)
        assertEquals(5, gestures[1].frames)
    }

    @Test
    fun mapsSwipeInteractionSpeedPresetToTimingFrames() {
        val gestures =
            InteractionGestureExpander.expand(
                InteractionSpec(
                    type = GifInteractionType.SWIPE.name,
                    speed = GifSwipeSpeed.NORMAL,
                ),
            )

        val drag = gestures.single()
        assertEquals(24, drag.holdStartFrames)
        assertEquals(36, drag.framesPerWaypoint)
        assertEquals(24, drag.releaseFrames)
    }

    @Test
    fun mapsSlowSwipeSpeedPresetToTimingFrames() {
        val gestures =
            InteractionGestureExpander.expand(
                InteractionSpec(
                    type = GifInteractionType.SWIPE.name,
                    speed = GifSwipeSpeed.SLOW,
                ),
            )

        val drag = gestures.single()
        assertEquals(88, drag.holdStartFrames)
        assertEquals(112, drag.framesPerWaypoint)
        assertEquals(88, drag.releaseFrames)
    }

    @Test
    fun customSwipeSpeedUsesManualTimingFields() {
        val gestures =
            InteractionGestureExpander.expand(
                InteractionSpec(
                    type = GifInteractionType.SWIPE.name,
                    speed = GifSwipeSpeed.CUSTOM,
                    holdStartFrames = 3,
                    travelFrames = 11,
                    releaseFrames = 4,
                ),
            )

        val drag = gestures.single()
        assertEquals(3, drag.holdStartFrames)
        assertEquals(11, drag.framesPerWaypoint)
        assertEquals(4, drag.releaseFrames)
    }

    @Test
    fun speedPresetOverridesManualTimingFields() {
        val gestures =
            InteractionGestureExpander.expand(
                InteractionSpec(
                    type = GifInteractionType.SWIPE.name,
                    speed = GifSwipeSpeed.FAST,
                    holdStartFrames = 99,
                    travelFrames = 99,
                    releaseFrames = 99,
                ),
            )

        val drag = gestures.single()
        assertEquals(10, drag.holdStartFrames)
        assertEquals(24, drag.framesPerWaypoint)
        assertEquals(10, drag.releaseFrames)
    }

    @Test
    fun mapsAllSwipeDirections() {
        val leftToRight =
            InteractionGestureExpander
                .expand(
                    InteractionSpec(
                        type = GifInteractionType.SWIPE.name,
                        direction = GifSwipeDirection.LEFT_TO_RIGHT.name,
                    ),
                ).first()
        val rightToLeft =
            InteractionGestureExpander
                .expand(
                    InteractionSpec(
                        type = GifInteractionType.SWIPE.name,
                        direction = GifSwipeDirection.RIGHT_TO_LEFT.name,
                    ),
                ).first()
        val topToBottom =
            InteractionGestureExpander
                .expand(
                    InteractionSpec(
                        type = GifInteractionType.SWIPE.name,
                        direction = GifSwipeDirection.TOP_TO_BOTTOM.name,
                    ),
                ).first()
        val bottomToTop =
            InteractionGestureExpander
                .expand(
                    InteractionSpec(
                        type = GifInteractionType.SWIPE.name,
                        direction = GifSwipeDirection.BOTTOM_TO_TOP.name,
                    ),
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
                    InteractionSpec(
                        type = GifInteractionType.SWIPE.name,
                        distance = GifSwipeDistance.SHORT.name,
                    ),
                ).first()
        val mediumSwipe =
            InteractionGestureExpander
                .expand(
                    InteractionSpec(
                        type = GifInteractionType.SWIPE.name,
                        distance = GifSwipeDistance.MEDIUM.name,
                    ),
                ).first()
        val longSwipe =
            InteractionGestureExpander
                .expand(
                    InteractionSpec(
                        type = GifInteractionType.SWIPE.name,
                        distance = GifSwipeDistance.LONG.name,
                    ),
                ).first()

        val shortDelta = shortSwipe.points.last().x - shortSwipe.points.first().x
        val mediumDelta = mediumSwipe.points.last().x - mediumSwipe.points.first().x
        val longDelta = longSwipe.points.last().x - longSwipe.points.first().x

        assertTrue(shortDelta < mediumDelta)
        assertTrue(mediumDelta < longDelta)
    }

    @Test
    fun horizontalSwipeUsesTopBottomTargetLaneAndIgnoresLeftRightTarget() {
        val topLane =
            InteractionGestureExpander
                .expand(
                    InteractionSpec(
                        type = GifInteractionType.SWIPE.name,
                        direction = GifSwipeDirection.LEFT_TO_RIGHT.name,
                        target = GifInteractionTarget.TOP.name,
                    ),
                ).first()
        val bottomLane =
            InteractionGestureExpander
                .expand(
                    InteractionSpec(
                        type = GifInteractionType.SWIPE.name,
                        direction = GifSwipeDirection.LEFT_TO_RIGHT.name,
                        target = GifInteractionTarget.BOTTOM.name,
                    ),
                ).first()
        val leftTarget =
            InteractionGestureExpander
                .expand(
                    InteractionSpec(
                        type = GifInteractionType.SWIPE.name,
                        direction = GifSwipeDirection.LEFT_TO_RIGHT.name,
                        target = GifInteractionTarget.LEFT.name,
                    ),
                ).first()
        val rightTarget =
            InteractionGestureExpander
                .expand(
                    InteractionSpec(
                        type = GifInteractionType.SWIPE.name,
                        direction = GifSwipeDirection.LEFT_TO_RIGHT.name,
                        target = GifInteractionTarget.RIGHT.name,
                    ),
                ).first()

        assertEquals(0.30f, topLane.points.first().y)
        assertEquals(0.70f, bottomLane.points.first().y)
        assertEquals(0.5f, leftTarget.points.first().y)
        assertEquals(0.5f, rightTarget.points.first().y)
    }

    @Test
    fun verticalSwipeUsesLeftRightTargetLaneAndIgnoresTopBottomTarget() {
        val leftLane =
            InteractionGestureExpander
                .expand(
                    InteractionSpec(
                        type = GifInteractionType.SWIPE.name,
                        direction = GifSwipeDirection.TOP_TO_BOTTOM.name,
                        target = GifInteractionTarget.LEFT.name,
                    ),
                ).first()
        val rightLane =
            InteractionGestureExpander
                .expand(
                    InteractionSpec(
                        type = GifInteractionType.SWIPE.name,
                        direction = GifSwipeDirection.TOP_TO_BOTTOM.name,
                        target = GifInteractionTarget.RIGHT.name,
                    ),
                ).first()
        val topTarget =
            InteractionGestureExpander
                .expand(
                    InteractionSpec(
                        type = GifInteractionType.SWIPE.name,
                        direction = GifSwipeDirection.TOP_TO_BOTTOM.name,
                        target = GifInteractionTarget.TOP.name,
                    ),
                ).first()
        val bottomTarget =
            InteractionGestureExpander
                .expand(
                    InteractionSpec(
                        type = GifInteractionType.SWIPE.name,
                        direction = GifSwipeDirection.TOP_TO_BOTTOM.name,
                        target = GifInteractionTarget.BOTTOM.name,
                    ),
                ).first()

        assertEquals(0.30f, leftLane.points.first().x)
        assertEquals(0.70f, rightLane.points.first().x)
        assertEquals(0.5f, topTarget.points.first().x)
        assertEquals(0.5f, bottomTarget.points.first().x)
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
