package io.github.hdcodedev.composegif.core

import androidx.compose.runtime.Composable

/** Contract implemented by generated scenario registries. */
public interface GifScenarioRegistry {
    /** Returns all registered capture scenarios. */
    public fun scenarios(): List<GifScenarioSpec>

    /** Renders the scenario content for the provided [name]. */
    @Composable
    public fun Render(name: String)
}
