package io.github.dautovicharis.composegif.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
public annotation class RecordGif(
    val name: String = "",
    val durationMs: Int = 1800,
    val fps: Int = 50,
    val widthPx: Int = 540,
    val heightPx: Int = 0,
    val theme: GifTheme = GifTheme.DARK,
)

public enum class GifTheme {
    LIGHT,
    DARK,
}
