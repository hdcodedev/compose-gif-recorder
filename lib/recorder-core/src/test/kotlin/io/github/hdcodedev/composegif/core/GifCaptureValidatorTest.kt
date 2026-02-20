package io.github.hdcodedev.composegif.core

import kotlin.test.Test
import kotlin.test.assertFailsWith

class GifCaptureValidatorTest {
    @Test
    fun rejectsInvalidFps() {
        assertFailsWith<GifValidationException> {
            GifCaptureValidator.validate(
                GifCaptureConfig(fps = 0),
            )
        }
    }

    @Test
    fun rejectsInvalidDuration() {
        assertFailsWith<GifValidationException> {
            GifCaptureValidator.validate(
                GifCaptureConfig(durationMs = 0),
            )
        }
    }

    @Test
    fun rejectsInvalidWidth() {
        assertFailsWith<GifValidationException> {
            GifCaptureValidator.validate(
                GifCaptureConfig(widthPx = 0),
            )
        }
    }

    @Test
    fun acceptsValidConfig() {
        GifCaptureValidator.validate(
            GifCaptureConfig(durationMs = 1800, fps = 50, widthPx = 540, heightPx = 0, theme = GifTheme.DARK),
        )
    }

    @Test
    fun rejectsGesturesWithoutNodeTag() {
        assertFailsWith<GifValidationException> {
            GifCaptureValidator.validate(
                GifCaptureConfig(
                    gestures =
                        listOf(
                            GifGestureStep(
                                type = GifGestureType.PAUSE,
                                frames = 4,
                            ),
                        ),
                ),
            )
        }
    }

    @Test
    fun rejectsInvalidTapGestureFractions() {
        assertFailsWith<GifValidationException> {
            GifCaptureValidator.validate(
                GifCaptureConfig(
                    interactionNodeTag = "ChartPlot",
                    gestures =
                        listOf(
                            GifGestureStep(
                                type = GifGestureType.TAP,
                                xFraction = 1.2f,
                                yFraction = 0.5f,
                                framesAfter = 2,
                            ),
                        ),
                ),
            )
        }
    }

    @Test
    fun rejectsInvalidDragGesturePoints() {
        assertFailsWith<GifValidationException> {
            GifCaptureValidator.validate(
                GifCaptureConfig(
                    interactionNodeTag = "ChartPlot",
                    gestures =
                        listOf(
                            GifGestureStep(
                                type = GifGestureType.DRAG_PATH,
                                points = listOf(GifFractionPoint(x = 0.1f, y = 0.5f)),
                                holdStartFrames = 2,
                                framesPerWaypoint = 2,
                                releaseFrames = 2,
                            ),
                        ),
                ),
            )
        }
    }

    @Test
    fun rejectsNegativeTapFramesAfter() {
        assertFailsWith<GifValidationException> {
            GifCaptureValidator.validate(
                GifCaptureConfig(
                    interactionNodeTag = "ChartPlot",
                    gestures =
                        listOf(
                            GifGestureStep(
                                type = GifGestureType.TAP,
                                xFraction = 0.5f,
                                yFraction = 0.5f,
                                framesAfter = -1,
                            ),
                        ),
                ),
            )
        }
    }

    @Test
    fun rejectsNegativeDragTimingValues() {
        assertFailsWith<GifValidationException> {
            GifCaptureValidator.validate(
                GifCaptureConfig(
                    interactionNodeTag = "ChartPlot",
                    gestures =
                        listOf(
                            GifGestureStep(
                                type = GifGestureType.DRAG_PATH,
                                points =
                                    listOf(
                                        GifFractionPoint(0.2f, 0.5f),
                                        GifFractionPoint(0.8f, 0.5f),
                                    ),
                                holdStartFrames = -1,
                                framesPerWaypoint = 2,
                                releaseFrames = 2,
                            ),
                        ),
                ),
            )
        }
        assertFailsWith<GifValidationException> {
            GifCaptureValidator.validate(
                GifCaptureConfig(
                    interactionNodeTag = "ChartPlot",
                    gestures =
                        listOf(
                            GifGestureStep(
                                type = GifGestureType.DRAG_PATH,
                                points =
                                    listOf(
                                        GifFractionPoint(0.2f, 0.5f),
                                        GifFractionPoint(0.8f, 0.5f),
                                    ),
                                holdStartFrames = 1,
                                framesPerWaypoint = -2,
                                releaseFrames = 2,
                            ),
                        ),
                ),
            )
        }
        assertFailsWith<GifValidationException> {
            GifCaptureValidator.validate(
                GifCaptureConfig(
                    interactionNodeTag = "ChartPlot",
                    gestures =
                        listOf(
                            GifGestureStep(
                                type = GifGestureType.DRAG_PATH,
                                points =
                                    listOf(
                                        GifFractionPoint(0.2f, 0.5f),
                                        GifFractionPoint(0.8f, 0.5f),
                                    ),
                                holdStartFrames = 1,
                                framesPerWaypoint = 2,
                                releaseFrames = -2,
                            ),
                        ),
                ),
            )
        }
    }

    @Test
    fun rejectsNonFiniteFractions() {
        assertFailsWith<GifValidationException> {
            GifCaptureValidator.validate(
                GifCaptureConfig(
                    interactionNodeTag = "ChartPlot",
                    gestures =
                        listOf(
                            GifGestureStep(
                                type = GifGestureType.TAP,
                                xFraction = Float.NaN,
                                yFraction = 0.5f,
                                framesAfter = 2,
                            ),
                        ),
                ),
            )
        }
        assertFailsWith<GifValidationException> {
            GifCaptureValidator.validate(
                GifCaptureConfig(
                    interactionNodeTag = "ChartPlot",
                    gestures =
                        listOf(
                            GifGestureStep(
                                type = GifGestureType.DRAG_PATH,
                                points =
                                    listOf(
                                        GifFractionPoint(Float.POSITIVE_INFINITY, 0.5f),
                                        GifFractionPoint(0.8f, 0.5f),
                                    ),
                                holdStartFrames = 1,
                                framesPerWaypoint = 2,
                                releaseFrames = 1,
                            ),
                        ),
                ),
            )
        }
    }

    @Test
    fun acceptsValidGestureConfig() {
        GifCaptureValidator.validate(
            GifCaptureConfig(
                durationMs = 2200,
                fps = 50,
                widthPx = 540,
                heightPx = 0,
                theme = GifTheme.DARK,
                interactionNodeTag = "LineChartPlot",
                gestures =
                    listOf(
                        GifGestureStep(type = GifGestureType.PAUSE, frames = 20),
                        GifGestureStep(
                            type = GifGestureType.TAP,
                            xFraction = 0.55f,
                            yFraction = 0.62f,
                            framesAfter = 8,
                        ),
                        GifGestureStep(
                            type = GifGestureType.DRAG_PATH,
                            points =
                                listOf(
                                    GifFractionPoint(0.12f, 0.60f),
                                    GifFractionPoint(0.40f, 0.56f),
                                    GifFractionPoint(0.78f, 0.62f),
                                ),
                            holdStartFrames = 6,
                            framesPerWaypoint = 6,
                            releaseFrames = 6,
                        ),
                    ),
            ),
        )
    }

    @Test
    fun acceptsBoundaryFractions() {
        GifCaptureValidator.validate(
            GifCaptureConfig(
                interactionNodeTag = "ChartPlot",
                gestures =
                    listOf(
                        GifGestureStep(
                            type = GifGestureType.TAP,
                            xFraction = 0f,
                            yFraction = 1f,
                            framesAfter = 1,
                        ),
                        GifGestureStep(
                            type = GifGestureType.DRAG_PATH,
                            points =
                                listOf(
                                    GifFractionPoint(0f, 0f),
                                    GifFractionPoint(1f, 1f),
                                ),
                            holdStartFrames = 1,
                            framesPerWaypoint = 1,
                            releaseFrames = 1,
                        ),
                    ),
            ),
        )
    }
}
