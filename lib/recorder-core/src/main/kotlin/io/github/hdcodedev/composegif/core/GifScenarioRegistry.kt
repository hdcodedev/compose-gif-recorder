package io.github.hdcodedev.composegif.core

import androidx.compose.runtime.Composable

public interface GifScenarioRegistry {
    public fun scenarios(): List<GifScenarioSpec>

    @Composable
    public fun Render(name: String)
}
