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

    @Test
    fun returnsEmptyNamesWhenRegistryMissing() {
        val file = File("build/tmp/does-not-exist-generated-registry.kt")
        assertEquals(emptyList(), parseScenarioNames(file))
    }

    @Test
    fun parsesScenarioFpsForMatchingScenario() {
        val file = File.createTempFile("generated", ".kt")
        file.writeText(
            """
            object GeneratedGifScenarioRegistry {
              private val scenarios = listOf(
                GifScenarioSpec(name = "line_chart_demo", capture = GifCaptureConfig(durationMs = 1800, fps = 50)),
                GifScenarioSpec(name = "bar_chart_demo", capture = GifCaptureConfig(durationMs = 1800, fps = 24))
              )
            }
            """.trimIndent(),
        )

        assertEquals(24, parseScenarioFps(file, "bar_chart_demo"))
        file.delete()
    }

    @Test
    fun returnsNullFpsWhenScenarioMissing() {
        val file = File.createTempFile("generated", ".kt")
        file.writeText(
            """
            object GeneratedGifScenarioRegistry {
              private val scenarios = listOf(
                GifScenarioSpec(name = "line_chart_demo", capture = GifCaptureConfig(durationMs = 1800, fps = 50))
              )
            }
            """.trimIndent(),
        )

        assertEquals(null, parseScenarioFps(file, "missing_scenario"))
        file.delete()
    }

    @Test
    fun returnsNullFpsWhenRegistryMissing() {
        val file = File("build/tmp/does-not-exist-generated-registry.kt")
        assertEquals(null, parseScenarioFps(file, "any"))
    }

    @Test
    fun parsesScenarioFpsForHyphenatedName() {
        val file = File.createTempFile("generated", ".kt")
        file.writeText(
            """
            object GeneratedGifScenarioRegistry {
              private val scenarios = listOf(
                GifScenarioSpec(name = "line-chart-demo", capture = GifCaptureConfig(durationMs = 1800, fps = 33))
              )
            }
            """.trimIndent(),
        )

        assertEquals(33, parseScenarioFps(file, "line-chart-demo"))
        file.delete()
    }

    @Test
    fun doesNotCrossScenarioBoundariesWhenParsingFps() {
        val file = File.createTempFile("generated", ".kt")
        file.writeText(
            """
            object GeneratedGifScenarioRegistry {
              private val scenarios = listOf(
                GifScenarioSpec(name = "scenario_a", capture = GifCaptureConfig(durationMs = 1800)),
                GifScenarioSpec(name = "scenario_b", capture = GifCaptureConfig(fps = 24))
              )
            }
            """.trimIndent(),
        )

        assertEquals(null, parseScenarioFps(file, "scenario_a"))
        assertEquals(24, parseScenarioFps(file, "scenario_b"))
        file.delete()
    }
}
