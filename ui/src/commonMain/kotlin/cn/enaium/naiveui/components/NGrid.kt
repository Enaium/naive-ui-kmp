package cn.enaium.naiveui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.roundToInt

/** Responsive column count used by [NGrid]. */
@Immutable
data class NGridBreakpoint(val width: Dp, val cols: Int)

/** Responsive span/offset applied by [NGridItem] at its matching width. */
@Immutable
data class NGridItemBreakpoint(
    val width: Dp,
    val span: Int,
    val offset: Int = 0,
)

private data class NGridItemData(
    val span: Int,
    val offset: Int,
    val responsive: List<NGridItemBreakpoint>,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@NGridItemData
}

/**
 * Grid container with [cols] columns. [NGridItem] children declare their span
 * and offset; gaps are applied between columns and rows.
 *
 * [responsiveCols] picks the column count from the widest matching breakpoint.
 * With [collapsed] only the first [collapsedRows] rows are shown. Set
 * [layoutShiftDisabled] to ignore all responsive behavior and use fixed
 * [cols].
 */
@Composable
fun NGrid(
    modifier: Modifier = Modifier,
    cols: Int = 24,
    xGap: Dp = 0.dp,
    yGap: Dp = 0.dp,
    collapsed: Boolean = false,
    collapsedRows: Int = 1,
    responsiveCols: List<NGridBreakpoint> = emptyList(),
    layoutShiftDisabled: Boolean = false,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val resolvedCols = if (layoutShiftDisabled) {
            cols
        } else {
            responsiveCols
                .filter { maxWidth >= it.width }
                .maxByOrNull { it.width }
                ?.cols
                ?: cols
        }
        val columns = resolvedCols.coerceAtLeast(1)
        Layout(content = content) { measurables, constraints ->
            val gapX = xGap.roundToPx()
            val gapY = yGap.roundToPx()
            val cell = (
                (constraints.maxWidth - gapX * (columns - 1)).coerceAtLeast(0).toFloat() / columns
                )
            fun spanWidth(span: Int): Int =
                (cell * span + gapX * (span - 1)).roundToInt().coerceAtLeast(0)
            val containerWidth = constraints.maxWidth.toDp()

            var row = 0
            var column = 0
            val slots = ArrayList<IntArray>(measurables.size) // index, span, column, row, visible
            measurables.forEachIndexed { index, measurable ->
                val data = measurable.parentData as? NGridItemData
                var span = data?.span ?: 1
                var offset = data?.offset ?: 0
                if (!layoutShiftDisabled) {
                    val breakpoint = data?.responsive
                        ?.filter { containerWidth >= it.width }
                        ?.maxByOrNull { it.width }
                    if (breakpoint != null) {
                        span = breakpoint.span
                        offset = breakpoint.offset
                    }
                } else {
                    offset = 0
                }
                span = span.coerceIn(0, columns)
                offset = offset.coerceIn(0, columns - 1)
                if (span == 0) {
                    slots += intArrayOf(index, 0, 0, row, 0)
                    return@forEachIndexed
                }
                var start = column + offset
                if (start + span > columns) {
                    row += 1
                    column = 0
                    start = offset.coerceAtMost(columns - span)
                }
                val visible = !collapsed || row < collapsedRows
                slots += intArrayOf(index, span, start, row, if (visible) 1 else 0)
                column = start + span
                if (column >= columns) {
                    row += 1
                    column = 0
                }
            }

            val placeables = measurables.mapIndexed { index, measurable ->
                val slot = slots[index]
                val visible = slot[4] == 1
                val width = spanWidth(slot[1])
                measurable.measure(
                    if (visible) {
                        Constraints(minWidth = 0, maxWidth = width, minHeight = 0, maxHeight = Constraints.Infinity)
                    } else {
                        Constraints.fixed(0, 0)
                    },
                )
            }

            val rowHeights = HashMap<Int, Int>()
            slots.forEachIndexed { index, slot ->
                if (slot[4] != 1) return@forEachIndexed
                val current = rowHeights[slot[3]] ?: 0
                rowHeights[slot[3]] = max(current, placeables[index].height)
            }
            val rowOffsets = HashMap<Int, Int>()
            var y = 0
            var rowIndex = 0
            while (rowIndex <= (rowHeights.keys.maxOrNull() ?: -1)) {
                rowOffsets[rowIndex] = y
                y += (rowHeights[rowIndex] ?: 0) + gapY
                rowIndex += 1
            }

            var maxRight = 0
            var maxBottom = 0
            slots.forEachIndexed { index, slot ->
                if (slot[4] != 1) return@forEachIndexed
                val placeable = placeables[index]
                val x = (slot[2] * (cell + gapX)).roundToInt()
                val itemY = rowOffsets[slot[3]] ?: 0
                maxRight = max(maxRight, x + placeable.width)
                maxBottom = max(maxBottom, itemY + placeable.height)
            }

            val width = if (constraints.hasBoundedWidth) constraints.maxWidth else maxRight
            val height = if (collapsed) {
                rowOffsets[collapsedRows - 1]?.let { it + (rowHeights[collapsedRows - 1] ?: 0) } ?: maxBottom
            } else {
                maxBottom
            }
            layout(width, height) {
                slots.forEachIndexed { index, slot ->
                    if (slot[4] != 1) return@forEachIndexed
                    val placeable = placeables[index]
                    val x = (slot[2] * (cell + gapX)).roundToInt()
                    placeable.placeRelative(x, rowOffsets[slot[3]] ?: 0)
                }
            }
        }
    }
}

/**
 * Grid cell. [span] is the number of columns it occupies, [offset] shifts it
 * to the right before placement. [responsive] overrides both at matching grid
 * widths; a span of 0 hides the item.
 */
@Composable
fun NGridItem(
    modifier: Modifier = Modifier,
    span: Int = 1,
    offset: Int = 0,
    responsive: List<NGridItemBreakpoint> = emptyList(),
    content: @Composable () -> Unit,
) {
    Box(modifier.then(NGridItemData(span, offset, responsive))) { content() }
}
