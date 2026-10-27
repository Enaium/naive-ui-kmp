package cn.enaium.naiveui.sample.gallery.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NGrid
import cn.enaium.naiveui.components.NGridBreakpoint
import cn.enaium.naiveui.components.NGridItem
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
                repeat(4) {
                    NGridItem { GridCell(theme.colors.primary.copy(alpha = 0.16f)) }
                }
            }
        }

        DemoSection(
            title = "Span",
            description = "An item can occupy more than one column.",
        ) {
            NGrid(cols = 6, xGap = 12.dp, yGap = 12.dp) {
                NGridItem(span = 2) { GridCell(theme.colors.primary.copy(alpha = 0.16f)) }
                NGridItem(span = 4) { GridCell(theme.colors.primary.copy(alpha = 0.28f)) }
                NGridItem(span = 3) { GridCell(theme.colors.primary.copy(alpha = 0.2f)) }
                NGridItem(span = 3) { GridCell(theme.colors.primary.copy(alpha = 0.24f)) }
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
            title = "Gaps",
            description = "Combine a column gap with a row gap.",
        ) {
            NGrid(cols = 4, xGap = 12.dp, yGap = 8.dp) {
                repeat(8) { index ->
                    NGridItem {
                        GridCell(
                            if (index % 2 == 0) {
                                theme.colors.primary.copy(alpha = 0.16f)
                            } else {
                                theme.colors.primary.copy(alpha = 0.26f)
                            },
                            height = 56.dp,
                        )
                    }
                }
            }
        }

        DemoSection(
            title = "Responsive",
            description = "Breakpoints select a column count from the measured grid width. " +
                "Resize the preview to see the number of columns change.",
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
            val breakpoints = remember {
                listOf(
                    NGridBreakpoint(0.dp, 2),
                    NGridBreakpoint(420.dp, 3),
                    NGridBreakpoint(720.dp, 4),
                )
            }
            NGrid(cols = 2, xGap = 8.dp, yGap = 8.dp, responsiveCols = breakpoints) {
                repeat(8) { index ->
                    NGridItem {
                        GridCell(theme.colors.primary.copy(alpha = 0.16f), label = "${index + 1}")
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
