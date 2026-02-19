package com.harisdautovic.gifdemo

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harisdautovic.gifdemo.ui.theme.DemoTheme
import io.github.dautovicharis.composegif.annotations.RecordGif
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun AnimationDemoScreen(modifier: Modifier = Modifier) {
    val demos =
        listOf(
            AnimationDemo(
                title = "Floating Orb",
                subtitle = "Position, scale, and color interpolation",
                content = { FloatingOrbDemo() },
            ),
            AnimationDemo(
                title = "Wave Bars",
                subtitle = "Procedural waveform animation",
                content = { WaveBarsDemo() },
            ),
            AnimationDemo(
                title = "Orbit Dots",
                subtitle = "Canvas-based orbital motion",
                content = { OrbitDotsDemo() },
            ),
            AnimationDemo(
                title = "Aurora Pulse",
                subtitle = "Rotating gradient with breathing scale",
                content = { AuroraPulseDemo() },
            ),
        )

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                Text(
                    text = "Animation Playground",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            item {
                Text(
                    text = "Random composable animations suitable for demo GIF recording.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            items(demos) { demo ->
                DemoCard(demo = demo)
            }
        }
    }
}

private data class AnimationDemo(
    val title: String,
    val subtitle: String,
    val content: @Composable () -> Unit,
)

@Composable
private fun DemoCard(demo: AnimationDemo) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = demo.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = demo.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            demo.content()
        }
    }
}

@Composable
@RecordGif(name = "floating_orb_demo", durationMs = 2200)
fun FloatingOrbDemo() {
    val transition = rememberInfiniteTransition(label = "floatingOrb")
    val travel =
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
            label = "travel",
        )
    val pulse =
        transition.animateFloat(
            initialValue = 0.85f,
            targetValue = 1.18f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
            label = "pulse",
        )
    val hue =
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = 2600, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
            label = "hue",
        )

    BoxWithConstraints(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    brush =
                        Brush.horizontalGradient(
                            colors =
                                listOf(
                                    Color(0xFF0F172A),
                                    Color(0xFF1E293B),
                                    Color(0xFF0F172A),
                                ),
                        ),
                )
                .padding(12.dp),
    ) {
        val orbSize = 58.dp
        val distance = maxWidth - orbSize
        val orbColor = lerp(Color(0xFF00BCD4), Color(0xFFFF6F61), hue.value)

        Box(
            modifier =
                Modifier
                    .offset(x = distance * travel.value)
                    .size(orbSize)
                    .graphicsLayer {
                        scaleX = pulse.value
                        scaleY = pulse.value
                    }
                    .clip(CircleShape)
                    .background(orbColor)
                    .border(width = 2.dp, color = Color.White.copy(alpha = 0.85f), shape = CircleShape),
        )
    }
}

@Composable
@RecordGif(name = "wave_bars_demo", durationMs = 2200)
fun WaveBarsDemo() {
    val transition = rememberInfiniteTransition(label = "waveBars")
    val phase =
        transition.animateFloat(
            initialValue = 0f,
            targetValue = (2f * PI.toFloat()),
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = 1800, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "phase",
        )

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF101827)),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            repeat(9) { index ->
                val amplitude = abs(sin(phase.value + (index * 0.6f)))
                val barHeight = 20.dp + (92.dp * amplitude)
                Box(
                    modifier =
                        Modifier
                            .width(14.dp)
                            .height(barHeight)
                            .clip(RoundedCornerShape(50))
                            .background(
                                brush =
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(0xFF22D3EE),
                                            Color(0xFF3B82F6),
                                            Color(0xFFA855F7),
                                        ),
                                    ),
                            ),
                )
            }
        }
    }
}

@Composable
@RecordGif(name = "orbit_dots_demo", durationMs = 2600)
fun OrbitDotsDemo() {
    val transition = rememberInfiniteTransition(label = "orbitDots")
    val angle =
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = 3800, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "angle",
        )

    Canvas(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF131722))
                .drawWithContent { drawContent() },
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val orbitRadius = min(size.width, size.height) * 0.28f

        drawCircle(
            color = Color(0xFF475569),
            radius = orbitRadius + 14f,
            center = center,
            style = Stroke(width = 3f, cap = StrokeCap.Round),
        )

        val points = 8
        repeat(points) { index ->
            val theta = Math.toRadians((angle.value + ((360f / points) * index)).toDouble())
            val x = center.x + (cos(theta).toFloat() * orbitRadius)
            val y = center.y + (sin(theta).toFloat() * orbitRadius)
            val blend = index.toFloat() / points.toFloat()
            val dotColor = lerp(Color(0xFF38BDF8), Color(0xFFF472B6), blend)
            val dotRadius = 7f + (3f * abs(sin(theta + angle.value / 90f).toFloat()))
            drawCircle(color = dotColor, radius = dotRadius, center = Offset(x, y))
        }

        drawCircle(color = Color(0xFF94A3B8), radius = 10f, center = center)
    }
}

@Composable
@RecordGif(name = "aurora_pulse_demo", durationMs = 2600)
fun AuroraPulseDemo() {
    val transition = rememberInfiniteTransition(label = "auroraPulse")
    val rotation =
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = 5200, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "rotation",
        )
    val breath =
        transition.animateFloat(
            initialValue = 0.9f,
            targetValue = 1.1f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
            label = "breath",
        )

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF0F172A)),
        contentAlignment = Alignment.Center,
    ) {
        val shape = RoundedCornerShape(34.dp)
        Box(
            modifier =
                Modifier
                    .size(132.dp)
                    .graphicsLayer {
                        rotationZ = rotation.value
                        scaleX = breath.value
                        scaleY = breath.value
                    }
                    .clip(shape)
                    .background(
                        brush =
                            Brush.sweepGradient(
                                listOf(
                                    Color(0xFF22D3EE),
                                    Color(0xFF818CF8),
                                    Color(0xFFF472B6),
                                    Color(0xFF22D3EE),
                                ),
                            ),
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.45f), shape),
        )

        Box(
            modifier =
                Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.22f)),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnimationDemoScreenPreview() {
    DemoTheme(dynamicColor = false) {
        AnimationDemoScreen()
    }
}
