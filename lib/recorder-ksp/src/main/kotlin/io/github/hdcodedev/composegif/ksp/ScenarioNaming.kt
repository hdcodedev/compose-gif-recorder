package io.github.hdcodedev.composegif.ksp

internal object ScenarioNaming {
    fun defaultName(functionName: String): String = functionName.ifBlank { "scenario" }
}
