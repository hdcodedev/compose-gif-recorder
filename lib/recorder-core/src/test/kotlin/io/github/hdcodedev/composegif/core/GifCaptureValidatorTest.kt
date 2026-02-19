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
}
