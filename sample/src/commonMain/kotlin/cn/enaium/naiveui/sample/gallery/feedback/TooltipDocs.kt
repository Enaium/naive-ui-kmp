package cn.enaium.naiveui.sample.gallery.feedback

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NTooltip
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NTrigger
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

private val tooltipPlacements = listOf(
    NPlacement.Top,
    NPlacement.Bottom,
    NPlacement.Left,
    NPlacement.Right,
    NPlacement.TopStart,
    NPlacement.TopEnd,
    NPlacement.BottomStart,
    NPlacement.BottomEnd,
)

@Composable
fun TooltipDocs() {
    DocsPage(
        title = "Tooltip",
        description = "Tooltip provides a short hint about an element. It opens on hover by default and supports " +
            "click triggers, delayed display, placements and a constrained width.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Wrap the trigger with `NTooltip` and pass the hint through `text`.",
            code = "NTooltip(text = \"I am a tooltip\") {\n    NButton { NText(\"Hover Me\") }\n}",
        ) {
            NTooltip(text = "I am a tooltip") {
                NButton { NText("Hover Me") }
            }
        }

        DemoSection(
            title = "Placement",
            description = "There are twelve placements, including start and end aligned variants.",
        ) {
            NSpace(size = 8.dp, wrap = true) {
                tooltipPlacements.forEach { placement ->
                    NTooltip(text = placement.name, placement = placement) {
                        NButton(size = NSize.Small) { NText(placement.name) }
                    }
                }
            }
        }

        DemoSection(
            title = "Trigger",
            description = "`trigger` selects between hover and click. Click is useful on devices without a pointer.",
            code = "NTooltip(text = \"Click triggered\", trigger = NTrigger.Click) {\n    NButton { NText(\"Click Me\") }\n}",
        ) {
            NSpace(size = 8.dp) {
                NTooltip(text = "Opened on hover", trigger = NTrigger.Hover) {
                    NButton { NText("Hover") }
                }
                NTooltip(text = "Opened on click", trigger = NTrigger.Click) {
                    NButton { NText("Click") }
                }
            }
        }

        DemoSection(
            title = "Width",
            description = "Constrain the popup with `width`. Without it the tooltip wraps at 280dp.",
        ) {
            NTooltip(
                text = "Tooltips can wrap long text over multiple lines when a width is provided.",
                width = 180.dp,
            ) {
                NButton { NText("Wrapped Tooltip") }
            }
        }

        DemoSection(
            title = "Custom content",
            description = "The `content` slot replaces the text body for richer hints.",
        ) {
            NTooltip(
                content = {
                    Column(Modifier.width(160.dp)) {
                        NText("Custom content", strong = true)
                        NText("Any composable can be placed inside the tooltip.")
                    }
                },
            ) {
                NButton { NText("Custom Content") }
            }
        }

        DemoSection(
            title = "Disabled",
            description = "Set `disabled` to keep the trigger from opening the tooltip.",
        ) {
            NSpace(size = 8.dp) {
                NTooltip(text = "You cannot see me", disabled = true) {
                    NButton { NText("Disabled Tooltip") }
                }
                NTooltip(text = "This one still works", delay = 400) {
                    NButton { NText("Delayed Tooltip") }
                }
            }
        }
    }
}
