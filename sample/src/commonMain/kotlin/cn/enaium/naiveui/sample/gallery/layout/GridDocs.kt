package cn.enaium.naiveui.sample.gallery.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NGrid
import cn.enaium.naiveui.components.NGridBreakpoint
import cn.enaium.naiveui.components.NGridItem
import cn.enaium.naiveui.components.NGridItemBreakpoint
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoRow
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

@Composable
fun GridDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Grid",
        description = "A 24-column grid by default. NGridItem spans columns and can be offset; " +
            "columns shrink with the container and breakpoints let the count adapt to the available width.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Four columns with a horizontal gap.",
            code = """
                NGrid(cols = 4, xGap = 12.dp) {
                    repeat(4) { NGridItem { GridCell() } }
                }
            """.trimIndent(),
        ) {
            NGrid(cols = 4, xGap = 12.dp) {
                repeat(4) { index ->
                    NGridItem {
                        GridCell(if (index % 2 == 0) theme.colors.primary.copy(alpha = 0.16f) else theme.colors.primary.copy(alpha = 0.26f))
                    }
                }
            }
        }

        DemoSection(
            title = "Gap",
            description = "xGap and yGap are applied between columns and between rows.",
        ) {
            NGrid(cols = 4, xGap = 12.dp, yGap = 8.dp) {
                repeat(8) { index ->
                    NGridItem {
                        GridCell(
                            if (index % 2 == 0) theme.colors.primary.copy(alpha = 0.16f) else theme.colors.primary.copy(alpha = 0.26f),
                            height = 56.dp,
                        )
                    }
                }
            }
        }

        DemoSection(
            title = "Offset",
            description = "Offsets shift an item before it is placed.",
        ) {
            NGrid(cols = 4, xGap = 12.dp) {
                NGridItem(offset = 1) { GridCell(theme.colors.primary.copy(alpha = 0.16f)) }
                NGridItem(offset = 1) { GridCell(theme.colors.primary.copy(alpha = 0.28f)) }
            }
        }

        DemoSection(
            title = "Responsive columns",
            description = "Breakpoints select a column count from the measured grid width. Resize the " +
                "preview to see the number of columns change.",
            code = """
                NGrid(
                    cols = 2,
                    responsiveCols = listOf(
                        NGridBreakpoint(0.dp, 2),
                        NGridBreakpoint(420.dp, 3),
                        NGridBreakpoint(720.dp, 4),
                    ),
                ) {
                    repeat(8) { index -> NGridItem { GridCell(label = "${'$'}{index + 1}") } }
                }
            """.trimIndent(),
        ) {
            val selfBreakpoints = remember {
                listOf(
                    NGridBreakpoint(0.dp, 2),
                    NGridBreakpoint(420.dp, 3),
                    NGridBreakpoint(720.dp, 4),
                )
            }
            val screenBreakpoints = remember {
                listOf(
                    NGridBreakpoint(0.dp, 2),
                    NGridBreakpoint(520.dp, 3),
                    NGridBreakpoint(760.dp, 4),
                    NGridBreakpoint(1000.dp, 5),
                )
            }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NText("Self responsive", color = theme.colors.text3)
                NGrid(cols = 2, xGap = 8.dp, yGap = 8.dp, responsiveCols = selfBreakpoints) {
                    repeat(8) { index ->
                        NGridItem {
                            GridCell(theme.colors.primary.copy(alpha = 0.16f), label = "${index + 1}")
                        }
                    }
                }
                NText("Screen responsive", color = theme.colors.text3)
                NGrid(cols = 2, xGap = 8.dp, yGap = 8.dp, responsiveCols = screenBreakpoints) {
                    repeat(8) { index ->
                        NGridItem {
                            GridCell(theme.colors.primary.copy(alpha = 0.24f), label = "${index + 1}")
                        }
                    }
                }
            }
        }

        DemoSection(
            title = "Collapse",
            description = "collapsed shows only collapsedRows rows. Adjust the item count, the visible " +
                "rows and the overflow label below the grid.",
        ) {
            var itemCount by remember { mutableStateOf(4) }
            var collapsed by remember { mutableStateOf(false) }
            var collapsedRows by remember { mutableStateOf(1) }
            var showSuffix by remember { mutableStateOf(true) }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DemoRow {
                    NButton(onClick = { itemCount = (itemCount - 1).coerceAtLeast(1) }) {
                        NText("- item")
                    }
                    NButton(onClick = { itemCount += 1 }) { NText("+ item") }
                    NButton(onClick = { collapsedRows = 1 }) { NText("1 row") }
                    NButton(onClick = { collapsedRows = 2 }) { NText("2 rows") }
                }
                DemoRow {
                    NButton(onClick = { collapsed = !collapsed }) {
                        NText(if (collapsed) "Expand" else "Collapse")
                    }
                    NButton(onClick = { showSuffix = !showSuffix }) {
                        NText(if (showSuffix) "Hide suffix" else "Show suffix")
                    }
                }
                NGrid(
                    cols = 5,
                    xGap = 8.dp,
                    yGap = 8.dp,
                    collapsed = collapsed,
                    collapsedRows = collapsedRows,
                ) {
                    repeat(itemCount) { index ->
                        NGridItem {
                            GridCell(
                                if (index % 2 == 0) {
                                    theme.colors.primary.copy(alpha = 0.16f)
                                } else {
                                    theme.colors.primary.copy(alpha = 0.26f)
                                },
                                label = "${index + 1}",
                                height = 80.dp,
                            )
                        }
                    }
                }
                if (showSuffix) {
                    val rows = if (collapsed) collapsedRows else (itemCount + 4) / 5
                    val overflow = itemCount > rows * 5
                    NText(
                        if (overflow) "Suffix: node overflow exists" else "Suffix: no node overflows",
                        color = if (overflow) theme.colors.error else theme.colors.text3,
                    )
                }
            }
        }

        DemoSection(
            title = "Avoid layout shift",
            description = "layoutShiftDisabled ignores responsive columns, responsive items and offsets, " +
                "so the grid keeps its fixed column count without measuring the container.",
        ) {
            var disabled by remember { mutableStateOf(false) }
            val breakpoints = remember {
                listOf(
                    NGridBreakpoint(0.dp, 2),
                    NGridBreakpoint(520.dp, 4),
                )
            }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DemoRow {
                    NButton(onClick = { disabled = !disabled }) {
                        NText(if (disabled) "Enable responsive layout" else "Disable layout shift")
                    }
                }
                NGrid(
                    cols = 2,
                    xGap = 12.dp,
                    yGap = 12.dp,
                    responsiveCols = breakpoints,
                    layoutShiftDisabled = disabled,
                ) {
                    NGridItem { GridCell(theme.colors.primary.copy(alpha = 0.16f)) }
                    NGridItem(span = 2) { GridCell(theme.colors.primary.copy(alpha = 0.28f)) }
                    NGridItem { GridCell(theme.colors.primary.copy(alpha = 0.22f)) }
                }
                NText(
                    if (disabled) {
                        "Fixed two columns, responsive breakpoints ignored."
                    } else {
                        "Uses two columns below 520dp and four columns above."
                    },
                    color = theme.colors.text3,
                )
            }
        }

        DemoSection(
            title = "Responsive grid item",
            description = "Per-item breakpoints override the span and can hide the item with a span of 0.",
        ) {
            val selfResponsive = remember {
                listOf(
                    NGridItemBreakpoint(0.dp, 0),
                    NGridItemBreakpoint(400.dp, 1),
                    NGridItemBreakpoint(600.dp, 2),
                    NGridItemBreakpoint(800.dp, 3),
                )
            }
            val screenResponsive = remember {
                listOf(
                    NGridItemBreakpoint(0.dp, 0),
                    NGridItemBreakpoint(600.dp, 1),
                    NGridItemBreakpoint(900.dp, 2),
                )
            }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NText("Self responsive", color = theme.colors.text3)
                NGrid(cols = 4, xGap = 8.dp) {
                    NGridItem(responsive = selfResponsive) {
                        GridCell(theme.colors.primary.copy(alpha = 0.16f), height = 120.dp, label = "0..3")
                    }
                    NGridItem {
                        GridCell(theme.colors.primary.copy(alpha = 0.26f), height = 120.dp, label = "2")
                    }
                }
                NText("Screen responsive", color = theme.colors.text3)
                NGrid(cols = 4, xGap = 8.dp) {
                    NGridItem(responsive = screenResponsive) {
                        GridCell(theme.colors.primary.copy(alpha = 0.16f), height = 120.dp, label = "0..2")
                    }
                    NGridItem {
                        GridCell(theme.colors.primary.copy(alpha = 0.26f), height = 120.dp, label = "2")
                    }
                }
            }
        }
    }
}

@Composable
private fun GridCell(
    color: Color,
    modifier: Modifier = Modifier,
    height: Dp = 64.dp,
    label: String? = null,
) {
    val theme = LocalNTheme.current
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(theme.borderRadius))
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        if (label != null) {
            NText(label, color = theme.colors.primary, strong = true)
        }
    }
}
