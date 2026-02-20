package io.github.hdcodedev.composegif.ksp

import kotlin.test.Test
import kotlin.test.assertEquals

class ScenarioNamingTest {
    @Test
    fun keepsFunctionNameAsIs() {
        assertEquals("LineChartDemo", ScenarioNaming.defaultName("LineChartDemo"))
    }

    @Test
    fun handlesBlankNames() {
        assertEquals("scenario", ScenarioNaming.defaultName(""))
    }
}
