package io.github.dautovicharis.composegif.core

import androidx.compose.runtime.Composable

public interface GifScenarioRegistry {
    public fun scenarios(): List<GifScenarioSpec>

    @Composable
    public fun Render(name: String)
}
