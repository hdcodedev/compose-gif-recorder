package io.github.hdcodedev.composegif.plugin

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class ScenarioParserTest {
    @Test
    fun parsesUniqueScenarioNames() {
        val file = File.createTempFile("generated", ".kt")
        file.writeText(
            """
            object GeneratedGifScenarioRegistry {
              private val scenarios = listOf(
                GifScenarioSpec(name = "line_chart_startup", capture = GifCaptureConfig()),
                GifScenarioSpec(name = "line_chart_startup", capture = GifCaptureConfig()),
                GifScenarioSpec(name = "bar_chart_startup", capture = GifCaptureConfig())
              )
            }
            """.trimIndent(),
        )

        val names = parseScenarioNames(file)
        assertEquals(listOf("line_chart_startup", "bar_chart_startup"), names)

        file.delete()
    }
}
