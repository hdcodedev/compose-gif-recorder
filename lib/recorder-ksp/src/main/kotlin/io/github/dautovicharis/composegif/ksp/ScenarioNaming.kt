package io.github.dautovicharis.composegif.ksp

internal object ScenarioNaming {
    fun defaultName(functionName: String): String {
        if (functionName.isBlank()) return "scenario"
        val snakeCase =
            functionName
                .replace(Regex("([a-z0-9])([A-Z])"), "$1_$2")
                .replace(Regex("[^A-Za-z0-9_]+"), "_")
                .trim('_')
                .lowercase()
        return if (snakeCase.isBlank()) "scenario" else snakeCase
    }
}
