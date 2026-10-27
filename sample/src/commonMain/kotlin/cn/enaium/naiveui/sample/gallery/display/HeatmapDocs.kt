package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NHeatmap
import cn.enaium.naiveui.components.NHeatmapCell
import cn.enaium.naiveui.components.NHeatmapColorTheme
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

private val weekdayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

private val weekLabels = listOf("Week 1", "Week 2", "Week 3", "Week 4", "Week 5")

private fun heatmapData(columns: Int = 7, rows: Int = 5): List<NHeatmapCell> =
    List(columns * rows) { index ->
        val column = index % columns
        val row = index / columns
        NHeatmapCell(
            x = column,
            y = row,
            value = ((column * 3 + row * 5 + 1) % 13).toFloat(),
        )
    }

@Composable
fun HeatmapDocs() {
    val theme = LocalNTheme.current
    val data = remember { heatmapData() }
    DocsPage(
        title = "Heatmap",
        description = "Display values on a grid with interpolated colors, themed palettes and hover tooltips.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Cells are addressed by column and row; the default scale follows the theme.",
            code = "NHeatmap(data = cells, xLabels = weekdays, yLabels = weeks)",
        ) {
            NHeatmap(
                data = data,
                xLabels = weekdayLabels,
                yLabels = weekLabels,
            )
        }
        DemoSection(
            title = "Built-in themes",
            description = "colorTheme selects green, blue, orange, purple or red palettes.",
            code = "NHeatmap(data = data, colorTheme = NHeatmapColorTheme.Blue)",
        ) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                listOf(
                    NHeatmapColorTheme.Green,
                    NHeatmapColorTheme.Blue,
                    NHeatmapColorTheme.Orange,
                    NHeatmapColorTheme.Purple,
                    NHeatmapColorTheme.Red,
                ).forEach { colorTheme ->
                    NText(
                        text = colorTheme.name,
                        size = theme.typography.fontSize.small,
                        color = theme.colors.text3,
                    )
                    NHeatmap(
                        data = data,
                        colorTheme = colorTheme,
                        cellSize = 20.dp,
                    )
                }
            }
        }
        DemoSection(
            title = "Custom colors",
            description = "colorScale interpolates between its stops from the minimum to the maximum value.",
        ) {
            NHeatmap(
                data = data,
                xLabels = weekdayLabels,
                yLabels = weekLabels,
                colorScale = listOf(
                    Color(0xFFFFF3E0),
                    Color(0xFFFFCC80),
                    Color(0xFFFB8C00),
                    Color(0xFFE65100),
                ),
            )
        }
        DemoSection(
            title = "Slots",
            description = "footer and indicator render below the grid; customTooltip replaces the hover card.",
            code = "NHeatmap(data = data, footer = { ... }, indicator = { ... }, customTooltip = { cell -> ... })",
        ) {
            NHeatmap(
                data = data,
                xLabels = weekdayLabels,
                yLabels = weekLabels,
                footer = {
                    NText(
                        text = "Left footer slot",
                        size = theme.typography.fontSize.small,
                        color = theme.colors.text3,
                    )
                },
                indicator = {
                    NText(
                        text = "Right indicator slot",
                        size = theme.typography.fontSize.small,
                        color = theme.colors.text3,
                    )
                },
                customTooltip = { cell ->
                    Column {
                        NText(
                            text = "Custom tooltip",
                            size = theme.typography.fontSize.small,
                            color = theme.colors.text3,
                        )
                        NText(
                            text = "Value ${cell?.value?.toInt() ?: 0}",
                            size = theme.typography.fontSize.small,
                            color = theme.colors.text1,
                            strong = true,
                        )
                    }
                },
            )
        }
    }
}
