package com.harisdautovic.gifdemo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harisdautovic.gifdemo.ui.theme.DemoTheme
import io.github.dautovicharis.charts.BarChart
import io.github.dautovicharis.charts.LineChart
import io.github.dautovicharis.charts.PieChart
import io.github.dautovicharis.charts.model.toChartDataSet
import io.github.dautovicharis.charts.model.toMultiChartDataSet
import io.github.hdcodedev.composegif.annotations.GifInteraction
import io.github.hdcodedev.composegif.annotations.GifInteractionTarget
import io.github.hdcodedev.composegif.annotations.GifInteractionType
import io.github.hdcodedev.composegif.annotations.GifSwipeDirection
import io.github.hdcodedev.composegif.annotations.GifSwipeDistance
import io.github.hdcodedev.composegif.annotations.RecordGif

@Composable
fun ChartDemoScreen(modifier: Modifier = Modifier) {
    val demos = listOf(
        ChartDemo(
            title = "Bar Chart",
            subtitle = "Daily net cash flow overview",
            content = { BarChartDemo() },
        ),
        ChartDemo(
            title = "Line Chart",
            subtitle = "Weekly temperature trend",
            content = { LineChartDemo() },
        ),
        ChartDemo(
            title = "Pie Chart",
            subtitle = "Market share by category",
            content = { PieChartDemo() },
        ),
        ChartDemo(
            title = "Multi Line Chart",
            subtitle = "Compare multiple data series",
            content = { MultiLineChartDemo() },
        ),
    )

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                Text(
                    text = "Charts Playground",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            item {
                Text(
                    text = "Chart demos powered by dautovicharis/charts.",
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

private data class ChartDemo(
    val title: String,
    val subtitle: String,
    val content: @Composable () -> Unit,
)

@Composable
private fun DemoCard(demo: ChartDemo) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
@RecordGif(name = "bar_chart_demo", durationMs = 2500)
fun BarChartDemo() {
    val dataSet = listOf(45f, -12f, 38f, 27f, -19f, 42f, 31f).toChartDataSet(
        title = "Daily Net Cash Flow",
        labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
    )
    BarChart(dataSet = dataSet)
}

@Composable
@RecordGif(
    name = "line_chart_demo",
    durationMs = 2500,
    interactionNodeTag = "LineChartPlot",
    interactions = [
        GifInteraction(type = GifInteractionType.PAUSE, frames = 20),
        GifInteraction(
            type = GifInteractionType.SWIPE,
            target = GifInteractionTarget.CENTER,
            direction = GifSwipeDirection.LEFT_TO_RIGHT,
            distance = GifSwipeDistance.MEDIUM,
            holdStartFrames = 6,
            travelFrames = 8,
            releaseFrames = 6,
        ),
        GifInteraction(
            type = GifInteractionType.TAP,
            target = GifInteractionTarget.RIGHT,
            framesAfter = 10,
        ),
    ],
)
fun LineChartDemo() {
    val dataSet = listOf(5f, 12f, 8f, 20f, 15f, 25f, 18f, 30f).toChartDataSet(
        title = "Temperature (°C)",
        postfix = "°",
        labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Mon+"),
    )
    LineChart(dataSet = dataSet)
}

@Composable
@RecordGif(name = "pie_chart_demo", durationMs = 2500)
fun PieChartDemo() {
    val dataSet = listOf(30f, 25f, 20f, 15f, 10f).toChartDataSet(
        title = "Market Share",
        postfix = "%",
        labels = listOf("Android", "iOS", "Web", "Desktop", "Other"),
    )
    PieChart(dataSet = dataSet)
}

@Composable
@RecordGif(name = "multi_line_chart_demo", durationMs = 2500)
fun MultiLineChartDemo() {
    val items = listOf(
        "Web Store" to listOf(420f, 510f, 480f, 530f, 560f, 590f),
        "Mobile App" to listOf(360f, 420f, 410f, 460f, 500f, 540f),
        "Partner Sales" to listOf(280f, 320f, 340f, 360f, 390f, 420f),
    )
    val dataSet = items.toMultiChartDataSet(
        title = "Weekly Revenue by Channel",
        prefix = "$",
        categories = listOf("Week 1", "Week 2", "Week 3", "Week 4", "Week 5", "Week 6"),
    )
    LineChart(dataSet = dataSet)
}

@Preview(showBackground = true)
@Composable
private fun ChartDemoScreenPreview() {
    DemoTheme(dynamicColor = false) {
        ChartDemoScreen()
    }
}
