package cn.enaium.naiveui.sample.gallery.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NPopover
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NButtonType
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NTrigger
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

@Composable
fun PopoverDocs() {
    DocsPage(
        title = "Popover",
        description = "Popover is a generic floating surface. It can be opened on hover or click, has twelve " +
            "placements, an optional fixed width and a delay before showing.",
    ) {
        DemoSection(
            title = "Basic",
            description = "The `content` slot holds arbitrary composables.",
        ) {
            NPopover(
                content = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        NText("Popover", strong = true, size = 15.sp)
                        NText("Here is some popover content.")
                        NButton(size = NSize.Small) { NText("Action") }
                    }
                },
            ) {
                NButton { NText("Hover Me") }
            }
        }

        DemoSection(
            title = "Trigger",
            description = "Hover is the default trigger. Click keeps the panel open until the outside is clicked.",
        ) {
            NSpace(size = 8.dp) {
                NPopover(
                    trigger = NTrigger.Hover,
                    content = { NText("Hover content") },
                ) {
                    NButton { NText("Hover Trigger") }
                }
                NPopover(
                    trigger = NTrigger.Click,
                    content = { NText("Click content") },
                ) {
                    NButton { NText("Click Trigger") }
                }
            }
        }

        DemoSection(
            title = "Placement",
            description = "Control the side used to anchor the popup with `placement`.",
        ) {
            NSpace(size = 8.dp, wrap = true) {
                listOf(
                    NPlacement.Top,
                    NPlacement.Bottom,
                    NPlacement.Left,
                    NPlacement.Right,
                ).forEach { placement ->
                    NPopover(
                        placement = placement,
                        content = { NText("Placed at ${placement.name}") },
                    ) {
                        NButton(size = NSize.Small) { NText(placement.name) }
                    }
                }
            }
        }

        DemoSection(
            title = "Width & delay",
            description = "Provide a fixed `width` and a `delay` in milliseconds before the popup appears.",
        ) {
            NPopover(
                trigger = NTrigger.Click,
                width = 240.dp,
                delay = 300,
                content = {
                    NText("This popover has a fixed width and opens after 300ms.")
                },
            ) {
                NButton { NText("Delayed Popover") }
            }
        }

        DemoSection(
            title = "Event",
            description = "Popover content is a real composition, so it can hold state and actions.",
        ) {
            var count by remember { mutableStateOf(0) }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NPopover(
                    trigger = NTrigger.Click,
                    content = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            NText("Count: $count")
                            NButton(
                                size = NSize.Small,
                                type = NButtonType.Primary,
                                onClick = { count++ },
                            ) {
                                NText("Increment")
                            }
                        }
                    },
                ) {
                    NButton { NText("Click me") }
                }
                NText("Count: $count", size = 13.sp)
            }
        }
    }
}
