package cn.enaium.naiveui.sample.gallery.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NEllipsis
import cn.enaium.naiveui.components.NEllipsisExpandTrigger
import cn.enaium.naiveui.components.NPerformantEllipsis
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage

private const val LONG_TEXT =
    "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium " +
        "doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore " +
        "veritatis et quasi architecto beatae vitae dicta sunt explicabo."

@Composable
fun EllipsisDocs() {
    DocsPage(
        title = "Ellipsis",
        description = "Truncates text after a number of lines and reveals the full " +
            "content in a tooltip or by expanding.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Single line ellipsis with a tooltip on hover.",
            code = """
NText("Hover the truncated text to reveal the full content")
NEllipsis(LONG_TEXT, Modifier.width(240.dp))
""".trimIndent(),
        ) {
            NEllipsis(LONG_TEXT, Modifier.width(240.dp))
        }

        DemoSection(
            title = "Customize tooltip content",
            description = "Use the `tooltipContent` slot to render rich tooltip content.",
        ) {
            NEllipsis(
                LONG_TEXT,
                Modifier.width(240.dp),
                tooltipContent = {
                    Column {
                        NText("Lorem Ipsum", strong = true)
                        NText(
                            "Sed ut perspiciatis unde omnis iste natus error sit " +
                                "voluptatem accusantium doloremque laudantium.",
                        )
                    }
                },
            )
        }

        DemoSection(
            title = "Better render performance",
            description = "`NPerformantEllipsis` shares the same layout pipeline and is " +
                "suited to long lists of truncated text.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { index ->
                    NPerformantEllipsis(
                        "$index. $LONG_TEXT",
                        Modifier.width(240.dp),
                    )
                }
            }
        }

        DemoSection(
            title = "Line clamp",
            description = "`lineClamp` limits the text to a number of lines.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NEllipsis(
                    "Two lines: " + LONG_TEXT,
                    Modifier.width(260.dp),
                    lineClamp = 2,
                )
                NEllipsis(
                    "Three lines: " + LONG_TEXT,
                    Modifier.width(260.dp),
                    lineClamp = 3,
                )
            }
        }

        DemoSection(
            title = "Expand trigger",
            description = "With `expandTrigger = Click` the text expands in place when clicked; " +
                "`Hover` expands while the pointer is over it.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NText("Click to expand")
                NEllipsis(
                    LONG_TEXT,
                    Modifier.width(280.dp),
                    lineClamp = 2,
                    tooltip = false,
                    expandTrigger = NEllipsisExpandTrigger.Click,
                )
                NText("Hover to expand")
                NEllipsis(
                    LONG_TEXT,
                    Modifier.width(280.dp),
                    lineClamp = 2,
                    tooltip = false,
                    expandTrigger = NEllipsisExpandTrigger.Hover,
                )
            }
        }

        DemoSection(
            title = "Tooltip max width",
            description = "The tooltip is constrained by `tooltipMaxWidth`.",
        ) {
            NEllipsis(
                LONG_TEXT,
                Modifier.width(200.dp),
                lineClamp = 1,
                tooltipMaxWidth = 260.dp,
            )
        }

        DemoSection(
            title = "Custom content",
            description = "Pass a content slot to render rich text; the tooltip still shows " +
                "the supplied text.",
        ) {
            NEllipsis(
                text = "Read the full article about gradient text and ellipsis components.",
                modifier = Modifier.width(210.dp),
                tooltip = true,
            ) {
                NText("Read the full article about gradient text and ellipsis components.")
            }
        }
    }
}
