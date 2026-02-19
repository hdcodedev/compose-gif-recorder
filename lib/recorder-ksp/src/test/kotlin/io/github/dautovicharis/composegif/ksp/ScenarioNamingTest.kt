package io.github.dautovicharis.composegif.ksp

import kotlin.test.Test
import kotlin.test.assertEquals

class ScenarioNamingTest {
    @Test
    fun convertsCamelCaseToSnakeCase() {
        assertEquals("line_chart_demo", ScenarioNaming.defaultName("LineChartDemo"))
    }

    @Test
    fun handlesBlankNames() {
        assertEquals("scenario", ScenarioNaming.defaultName(""))
    }
}
