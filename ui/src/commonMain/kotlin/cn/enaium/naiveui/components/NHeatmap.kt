package cn.enaium.naiveui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nIsLight
import cn.enaium.naiveui.internal.nShadow
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NTheme
import kotlin.math.roundToInt

/** A single value in the heatmap grid. `x` is the column, `y` the row. */
@Immutable
data class NHeatmapCell(
    val x: Int,
    val y: Int,
    val value: Float,
)

private fun nDefaultHeatmapScale(theme: NTheme): List<Color> = if (theme.isDark) {
    listOf(
        Color(0xFF161B22),
        Color(0xFF0E4429),
        Color(0xFF006D32),
        Color(0xFF26A641),
        Color(0xFF39D353),
    )
} else {
    listOf(
        Color(0xFFEBEDF0),
        Color(0xFF9BE9A8),
        Color(0xFF40C463),
        Color(0xFF30A14E),
        Color(0xFF216E39),
    )
}

/** Built-in heatmap palettes from lightest to darkest. */
enum class NHeatmapColorTheme { Green, Blue, Orange, Purple, Red }

private fun nHeatmapThemeScale(theme: NHeatmapColorTheme, dark: Boolean): List<Color> = when (theme) {
    NHeatmapColorTheme.Green -> if (dark) {
        listOf(Color(0xFF161B22), Color(0xFF0E4429), Color(0xFF006D32), Color(0xFF26A641), Color(0xFF39D353))
    } else {
        listOf(Color(0xFFEBEDF0), Color(0xFF9BE9A8), Color(0xFF40C463), Color(0xFF30A14E), Color(0xFF216E39))
    }
    NHeatmapColorTheme.Blue -> if (dark) {
        listOf(Color(0xFF161B22), Color(0xFF0B2E4F), Color(0xFF11508A), Color(0xFF1F78C8), Color(0xFF58A6FF))
    } else {
        listOf(Color(0xFFEBEDF0), Color(0xFFBBDDF5), Color(0xFF6FB6EA), Color(0xFF2F82C9), Color(0xFF0B5A9B))
    }
    NHeatmapColorTheme.Orange -> if (dark) {
        listOf(Color(0xFF1F1A16), Color(0xFF4A2A0E), Color(0xFF7A4412), Color(0xFFB4661A), Color(0xFFF0A020))
    } else {
        listOf(Color(0xFFEBEDF0), Color(0xFFFBD8A8), Color(0xFFF5B05C), Color(0xFFE68115), Color(0xFFB35A00))
    }
    NHeatmapColorTheme.Purple -> if (dark) {
        listOf(Color(0xFF191621), Color(0xFF2E2350), Color(0xFF4A347E), Color(0xFF6E4BB5), Color(0xFFA06FE8))
    } else {
        listOf(Color(0xFFEBEDF0), Color(0xFFDCCDF2), Color(0xFFB394E0), Color(0xFF8A5BC8), Color(0xFF5F2E9B))
    }
    NHeatmapColorTheme.Red -> if (dark) {
        listOf(Color(0xFF211616), Color(0xFF4E1C1C), Color(0xFF7E2626), Color(0xFFB03333), Color(0xFFE05555))
    } else {
        listOf(Color(0xFFEBEDF0), Color(0xFFF8C9C9), Color(0xFFF08F8F), Color(0xFFD94F4F), Color(0xFFA61B1B))
    }
}

private fun nNormalizeHeatmapValue(value: Float, min: Float, max: Float): Float {
    if (max <= min) return if (value > 0f) 1f else 0f
    return ((value - min) / (max - min)).coerceIn(0f, 1f)
}

private fun nHeatmapScaleColor(scale: List<Color>, fraction: Float): Color {
    if (scale.isEmpty()) return Color.Transparent
    if (scale.size == 1) return scale[0]
    val position = fraction.coerceIn(0f, 1f) * (scale.size - 1)
    val index = position.toInt().coerceAtMost(scale.size - 2)
    return lerp(scale[index], scale[index + 1], position - index)
}

/**
 * Grid heatmap.
 *
 * [data] is addressed by column ([NHeatmapCell.x]) and row ([NHeatmapCell.y]).
 * Cells without data are rendered with the theme's action color. [colorScale]
 * interpolates between its stops by the normalized value, and hovering a cell
 * shows a tooltip with both axis labels and the formatted value.
 */
@Composable
private fun NHeatmapGrid(
    data: List<NHeatmapCell>,
    modifier: Modifier = Modifier,
    xLabels: List<String> = emptyList(),
    yLabels: List<String> = emptyList(),
    colorScale: List<Color> = emptyList(),
    colorTheme: NHeatmapColorTheme? = null,
    cellSize: Dp = 26.dp,
    cellGap: Dp = 3.dp,
    showValues: Boolean = false,
    tooltip: Boolean = true,
    customTooltip: (@Composable (NHeatmapCell?) -> Unit)? = null,
    valueFormatter: (Float) -> String = { value -> value.roundToInt().toString() },
) {
    val theme = LocalNTheme.current
    val density = LocalDensity.current
    val measurer = rememberTextMeasurer()
    val cellSizePx = with(density) { cellSize.toPx() }
    val gapPx = with(density) { cellGap.toPx() }
    val cornerPx = with(density) { 3.dp.toPx() }
    val labelGapPx = with(density) { 8.dp.toPx() }
    val labelStyle = remember(theme) {
        TextStyle(
            fontFamily = theme.typography.fontFamily,
            fontSize = 11.sp,
            color = theme.colors.text3,
        )
    }
    val valueStyle = remember(theme) {
        TextStyle(
            fontFamily = theme.typography.fontFamily,
            fontSize = 10.sp,
        )
    }
    val columns = maxOf(data.maxOfOrNull { it.x + 1 } ?: 0, xLabels.size, 1)
    val rows = maxOf(data.maxOfOrNull { it.y + 1 } ?: 0, yLabels.size, 1)
    val minValue = data.minOfOrNull { it.value } ?: 0f
    val maxValue = data.maxOfOrNull { it.value } ?: 1f
    val scale = remember(colorScale, colorTheme, theme.isDark) {
        when {
            colorScale.isNotEmpty() -> colorScale
            colorTheme != null -> nHeatmapThemeScale(colorTheme, theme.isDark)
            else -> nDefaultHeatmapScale(theme)
        }
    }
    val cells = remember(data) { data.associateBy { it.x to it.y } }
    val yLabelWidth = if (yLabels.isEmpty()) {
        0f
    } else {
        yLabels.maxOf { measurer.measure(it, labelStyle).size.width }.toFloat() + labelGapPx
    }
    val xLabelHeight = if (xLabels.isEmpty()) {
        0f
    } else {
        measurer.measure("0", labelStyle).size.height.toFloat() + with(density) { 4.dp.toPx() }
    }
    val gridWidth = columns * cellSizePx + (columns - 1) * gapPx
    val gridHeight = rows * cellSizePx + (rows - 1) * gapPx
    val canvasWidth = with(density) { (yLabelWidth + gridWidth).toDp() }
    val canvasHeight = with(density) { (xLabelHeight + gridHeight).toDp() }
    var hovered by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    Box(modifier) {
        Canvas(
            Modifier
                .size(canvasWidth, canvasHeight)
                .pointerInput(columns, rows, cellSizePx, gapPx, yLabelWidth, xLabelHeight) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            when (event.type) {
                                PointerEventType.Move, PointerEventType.Enter -> {
                                    val position = event.changes.firstOrNull()?.position
                                    if (position == null) {
                                        hovered = null
                                    } else {
                                        val localX = position.x - yLabelWidth
                                        val localY = position.y - xLabelHeight
                                        if (localX < 0f || localY < 0f) {
                                            hovered = null
                                        } else {
                                            val column = (localX / (cellSizePx + gapPx)).toInt()
                                            val row = (localY / (cellSizePx + gapPx)).toInt()
                                            val offsetX = localX - column * (cellSizePx + gapPx)
                                            val offsetY = localY - row * (cellSizePx + gapPx)
                                            hovered = if (
                                                column in 0 until columns &&
                                                row in 0 until rows &&
                                                offsetX <= cellSizePx &&
                                                offsetY <= cellSizePx
                                            ) {
                                                column to row
                                            } else {
                                                null
                                            }
                                        }
                                    }
                                }
                                PointerEventType.Exit -> hovered = null
                                else -> Unit
                            }
                        }
                    }
                },
        ) {
            drawHeatmapLabels(
                measurer = measurer,
                labelStyle = labelStyle,
                xLabels = xLabels,
                yLabels = yLabels,
                yLabelWidth = yLabelWidth,
                xLabelHeight = xLabelHeight,
                cellSizePx = cellSizePx,
                gapPx = gapPx,
                labelGapPx = labelGapPx,
            )
            for (row in 0 until rows) {
                for (column in 0 until columns) {
                    val cell = cells[column to row]
                    val topLeft = Offset(
                        x = yLabelWidth + column * (cellSizePx + gapPx),
                        y = xLabelHeight + row * (cellSizePx + gapPx),
                    )
                    val fill = if (cell == null) {
                        theme.colors.action
                    } else {
                        nHeatmapScaleColor(
                            scale,
                            nNormalizeHeatmapValue(cell.value, minValue, maxValue),
                        )
                    }
                    drawRoundRect(
                        color = fill,
                        topLeft = topLeft,
                        size = Size(cellSizePx, cellSizePx),
                        cornerRadius = CornerRadius(cornerPx, cornerPx),
                    )
                    if (showValues && cell != null) {
                        val textColor = if (nIsLight(fill)) theme.colors.text2 else Color.White
                        val layout = measurer.measure(valueFormatter(cell.value), valueStyle.copy(color = textColor))
                        drawText(
                            textLayoutResult = layout,
                            topLeft = Offset(
                                x = topLeft.x + (cellSizePx - layout.size.width) / 2f,
                                y = topLeft.y + (cellSizePx - layout.size.height) / 2f,
                            ),
                        )
                    }
                }
            }
            val hoveredCell = hovered
            if (hoveredCell != null) {
                val topLeft = Offset(
                    x = yLabelWidth + hoveredCell.first * (cellSizePx + gapPx),
                    y = xLabelHeight + hoveredCell.second * (cellSizePx + gapPx),
                )
                drawRoundRect(
                    color = theme.colors.text2,
                    topLeft = topLeft,
                    size = Size(cellSizePx, cellSizePx),
                    cornerRadius = CornerRadius(cornerPx, cornerPx),
                    style = Stroke(width = with(density) { 1.5.dp.toPx() }),
                )
            }
        }
        val hoveredCell = hovered
        if (tooltip && hoveredCell != null) {
            val cell = cells[hoveredCell]
            val xLabel = xLabels.getOrNull(hoveredCell.first)
            val yLabel = yLabels.getOrNull(hoveredCell.second)
            Column(
                Modifier
                    .offset {
                        IntOffset(
                            x = (yLabelWidth + hoveredCell.first * (cellSizePx + gapPx)).roundToInt(),
                            y = (xLabelHeight + hoveredCell.second * (cellSizePx + gapPx) + cellSizePx + gapPx).roundToInt(),
                        )
                    }
                    .nShadow(theme, 1, RoundedCornerShape(theme.borderRadius))
                    .clip(RoundedCornerShape(theme.borderRadius))
                    .background(theme.colors.popover)
                    .nBorder(theme.colors.border, 1.dp, theme.borderRadius)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
            ) {
                if (customTooltip != null) {
                    customTooltip(cell)
                } else {
                    val title = listOfNotNull(yLabel, xLabel).joinToString(", ")
                    if (title.isNotEmpty()) {
                        NText(
                            text = title,
                            size = theme.typography.fontSize.small,
                            color = theme.colors.text3,
                        )
                    }
                    NText(
                        text = valueFormatter(cell?.value ?: 0f),
                        size = theme.typography.fontSize.small,
                        color = theme.colors.text1,
                        strong = true,
                    )
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHeatmapLabels(
    measurer: TextMeasurer,
    labelStyle: TextStyle,
    xLabels: List<String>,
    yLabels: List<String>,
    yLabelWidth: Float,
    xLabelHeight: Float,
    cellSizePx: Float,
    gapPx: Float,
    labelGapPx: Float,
) {
    yLabels.forEachIndexed { row, label ->
        val layout = measurer.measure(label, labelStyle)
        drawText(
            textLayoutResult = layout,
            topLeft = Offset(
                x = yLabelWidth - labelGapPx - layout.size.width,
                y = xLabelHeight + row * (cellSizePx + gapPx) + (cellSizePx - layout.size.height) / 2f,
            ),
        )
    }
    xLabels.forEachIndexed { column, label ->
        val layout = measurer.measure(label, labelStyle)
        drawText(
            textLayoutResult = layout,
            topLeft = Offset(
                x = yLabelWidth + column * (cellSizePx + gapPx),
                y = 0f,
            ),
        )
    }
}

/**
 * Grid heatmap with optional footer and indicator slots.
 *
 * [colorTheme] selects one of the built-in palettes; an explicit [colorScale]
 * takes priority. [footer] aligns left, [indicator] right below the grid.
 */
@Composable
fun NHeatmap(
    data: List<NHeatmapCell>,
    modifier: Modifier = Modifier,
    xLabels: List<String> = emptyList(),
    yLabels: List<String> = emptyList(),
    colorScale: List<Color> = emptyList(),
    colorTheme: NHeatmapColorTheme? = null,
    cellSize: Dp = 26.dp,
    cellGap: Dp = 3.dp,
    showValues: Boolean = false,
    tooltip: Boolean = true,
    customTooltip: (@Composable (NHeatmapCell?) -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    indicator: (@Composable () -> Unit)? = null,
    valueFormatter: (Float) -> String = { value -> value.roundToInt().toString() },
) {
    Column(modifier) {
        NHeatmapGrid(
            data = data,
            xLabels = xLabels,
            yLabels = yLabels,
            colorScale = colorScale,
            colorTheme = colorTheme,
            cellSize = cellSize,
            cellGap = cellGap,
            showValues = showValues,
            tooltip = tooltip,
            customTooltip = customTooltip,
            valueFormatter = valueFormatter,
        )
        if (footer != null || indicator != null) {
            Row(
                Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box { footer?.invoke() }
                Box { indicator?.invoke() }
            }
        }
    }
}
