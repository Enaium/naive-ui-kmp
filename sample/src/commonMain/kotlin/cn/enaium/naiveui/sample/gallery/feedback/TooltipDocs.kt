package cn.enaium.naiveui.sample.gallery.feedback

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NTooltip
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NTrigger
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

private const val tooltipText = "I wish they all could be California girls"

@Composable
fun TooltipDocs() {
    DocsPage(
        title = "Tooltip",
        description = "Tooltip provides a short hint about an element. It opens on hover by default and supports " +
            "click or manual triggers, delayed display, all placements, arrow toggling, body width and show events.",
    ) {
        DemoSection(
            title = "Basic",
            description = "If it looks like a duck, walks like a duck, and quacks like a duck... it must be a duck.",
            code = "NTooltip(text = \"Duck\") {\n    NButton { NText(\"Duck\") }\n}",
        ) {
            NTooltip(text = "If it looks like a duck, walks like a duck, and quacks like a duck...it must be a duck.") {
                NButton { NText("Duck") }
            }
        }

        DemoSection(
            title = "Placement",
            description = "The demo page uses `top-start`. All twelve placements are available.",
        ) {
            NSpace(size = 8.dp, wrap = true) {
                listOf(
                    NPlacement.TopStart,
                    NPlacement.Top,
                    NPlacement.TopEnd,
                    NPlacement.BottomStart,
                    NPlacement.Bottom,
                    NPlacement.BottomEnd,
                    NPlacement.Left,
                    NPlacement.Right,
                ).forEach { placement ->
                    NTooltip(text = "Woo, awesome! ($placement)", placement = placement) {
                        NButton(size = NSize.Small) { NText(placement.name) }
                    }
                }
            }
        }

        DemoSection(
            title = "Trigger",
            description = "Hover and click are automatic; a manual tooltip is driven by `show`.",
        ) {
            var manualShow by remember { mutableStateOf(false) }
            NSpace(size = 8.dp) {
                NTooltip(text = tooltipText, placement = NPlacement.Bottom, trigger = NTrigger.Hover) {
                    NButton { NText("Hover") }
                }
                NTooltip(text = tooltipText, placement = NPlacement.Bottom, trigger = NTrigger.Click) {
                    NButton { NText("Click") }
                }
                NTooltip(
                    text = tooltipText,
                    placement = NPlacement.Bottom,
                    trigger = NTrigger.Manual,
                    show = manualShow,
                    onUpdateShow = { manualShow = it },
                ) {
                    NButton(onClick = { manualShow = !manualShow }) { NText("Manual") }
                }
            }
        }

        DemoSection(
            title = "No arrow",
            description = "The arrow is rendered by default. Set `showArrow = false` to get a plain surface.",
        ) {
            NSpace(size = 8.dp) {
                NTooltip(text = "The same as popover", showArrow = true) {
                    NButton { NText("Show arrow by default") }
                }
                NTooltip(text = "The same as popover", showArrow = false) {
                    NButton { NText("No arrow") }
                }
            }
        }

        DemoSection(
            title = "Body style",
            description = "Constrain the body with `maxWidth`; long and short text wrap inside the same width.",
        ) {
            NSpace(size = 8.dp, wrap = true) {
                NTooltip(
                    maxWidth = 400.dp,
                    trigger = NTrigger.Click,
                    text = "I wish they all could be California girls. I wish they all could be California girls. " +
                        "I wish they all could be California girls.",
                ) {
                    NButton { NText("California Girls") }
                }
                NTooltip(
                    maxWidth = 400.dp,
                    trigger = NTrigger.Click,
                    text = "I wish ...",
                ) {
                    NButton { NText("California Girls (short)") }
                }
            }
        }

        DemoSection(
            title = "Event",
            description = "`onUpdateShow` reports hover and click changes. Manual tooltips are driven by the parent " +
                "and do not emit an event.",
        ) {
            var event by remember { mutableStateOf("No event yet") }
            var manualShow by remember { mutableStateOf(false) }
            Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                NSpace(size = 8.dp) {
                    NTooltip(
                        text = tooltipText,
                        placement = NPlacement.Bottom,
                        trigger = NTrigger.Hover,
                        onUpdateShow = { event = "show: $it" },
                    ) {
                        NButton { NText("Hover") }
                    }
                    NTooltip(
                        text = tooltipText,
                        placement = NPlacement.Bottom,
                        trigger = NTrigger.Click,
                        onUpdateShow = { event = "show: $it" },
                    ) {
                        NButton { NText("Click") }
                    }
                    NTooltip(
                        text = tooltipText,
                        placement = NPlacement.Bottom,
                        trigger = NTrigger.Manual,
                        show = manualShow,
                        onUpdateShow = { manualShow = it },
                    ) {
                        NButton(onClick = {
                            manualShow = !manualShow
                            event = "manual: $manualShow"
                        }) { NText("Manual") }
                    }
                }
                NText(event, size = 13.sp)
            }
        }
    }
}
