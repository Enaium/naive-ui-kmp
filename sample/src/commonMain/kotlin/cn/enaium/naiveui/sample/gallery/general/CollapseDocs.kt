package cn.enaium.naiveui.sample.gallery.general

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NCollapse
import cn.enaium.naiveui.components.NCollapseArrowPlacement
import cn.enaium.naiveui.components.NCollapseDisplayDirective
import cn.enaium.naiveui.components.NCollapseItem
import cn.enaium.naiveui.components.NCollapseTriggerArea
import cn.enaium.naiveui.components.NFlex
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NStatus

@Composable
fun CollapseDocs() {
    DocsPage(
        title = "Collapse",
        description = "Vertically stacked collapsible panels with accordion mode, arrow " +
            "placement, trigger areas and kept-mounted content.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Click a header to expand its panel.",
            code = """
NCollapse {
    NCollapseItem("1", title = "right") { NText("good") }
    NCollapseItem("2", title = "right") { NText("nice") }
}
""".trimIndent(),
        ) {
            NCollapse {
                NCollapseItem("1", title = "right") { NText("good") }
                NCollapseItem("2", title = "right") { NText("nice") }
                NCollapseItem("3", title = "right") { NText("very good") }
            }
        }

        DemoSection(
            title = "Accordion",
            description = "Only one panel can be opened at a time.",
        ) {
            NCollapse(defaultExpandedNames = listOf("1"), accordion = true) {
                NCollapseItem("1", title = "right") { NText("good") }
                NCollapseItem("2", title = "right") { NText("good") }
            }
        }

        DemoSection(
            title = "Default expanded",
            description = "Panels can start expanded, including nested collapses.",
        ) {
            NCollapse(defaultExpandedNames = listOf("2", "3")) {
                NCollapseItem("1", title = "green light") {
                    NCollapse(defaultExpandedNames = listOf("2")) {
                        NCollapseItem("1", title = "on") { NText("pass") }
                        NCollapseItem("2", title = "flashing") { NText("pass fastly") }
                    }
                }
                NCollapseItem("2", title = "red light") { NText("stop") }
                NCollapseItem("3", title = "amber light") { NText("warn") }
            }
        }

        DemoSection(
            title = "Disabled",
            description = "Disabled items ignore clicks.",
        ) {
            NCollapse {
                NCollapseItem("1", title = "right") { NText("good") }
                NCollapseItem("2", title = "right", disabled = true) { NText("nice") }
                NCollapseItem("3", title = "right") { NText("very good") }
            }
        }

        DemoSection(
            title = "Arrow placement",
            description = "The arrow can be placed on the left or on the right of the header.",
        ) {
            Column {
                NCollapse(arrowPlacement = NCollapseArrowPlacement.Left) {
                    NCollapseItem("1", title = "Left arrow") { NText("good") }
                }
                Spacer(Modifier.height(12.dp))
                NCollapse(arrowPlacement = NCollapseArrowPlacement.Right) {
                    NCollapseItem("1", title = "Right arrow") { NText("nice") }
                    NCollapseItem("2", title = "Right arrow") { NText("very good") }
                }
            }
        }

        DemoSection(
            title = "Customize icon",
            description = "The `arrow` and `headerExtra` slots on `NCollapse` apply to every " +
                "item that does not provide its own.",
        ) {
            NCollapse(
                arrow = { NIcon(NIcons.Add, size = 16.dp) },
                headerExtra = { NIcon(NIcons.Success, size = 16.dp) },
            ) {
                NCollapseItem("1", title = "Bronze") { NText("good") }
                NCollapseItem("2", title = "Silver") { NText("nice") }
                NCollapseItem("3", title = "Gold") { NText("very good") }
            }
        }

        DemoSection(
            title = "Extra info in header",
            description = "The `extra` slot puts information on the right side of an item.",
        ) {
            NCollapse {
                NCollapseItem("1", title = "right", extra = { NText("good") }) { NText("good") }
                NCollapseItem("2", title = "right", extra = { NText("nice") }) { NText("nice") }
                NCollapseItem("3", title = "right", extra = { NText("very good") }) {
                    NText("very good")
                }
            }
        }

        DemoSection(
            title = "Click on item header",
            description = "Header content can be provided as a slot; `onItemHeaderClick` " +
                "reports the toggled item.",
        ) {
            var lastClicked by remember { mutableStateOf("nothing yet") }
            Column {
                NText("Last clicked: $lastClicked")
                Spacer(Modifier.height(8.dp))
                NCollapse(
                    onItemHeaderClick = { info ->
                        lastClicked = info.name + if (info.expanded) " (expanded)" else " (collapsed)"
                    },
                ) {
                    NCollapseItem("1", header = { NText("Bronze") }) { NText("Okay") }
                    NCollapseItem("2", header = { NText("Silver") }) { NText("Good") }
                    NCollapseItem("3", header = { NText("Gold") }) { NText("Excellent") }
                }
            }
        }

        DemoSection(
            title = "Trigger areas",
            description = "Choose which header regions toggle the panel.",
        ) {
            var main by remember { mutableStateOf(true) }
            var extra by remember { mutableStateOf(true) }
            var arrow by remember { mutableStateOf(true) }
            val areas = buildSet {
                if (main) add(NCollapseTriggerArea.Main)
                if (extra) add(NCollapseTriggerArea.Extra)
                if (arrow) add(NCollapseTriggerArea.Arrow)
            }
            Column {
                NFlex(size = 8.dp) {
                    NTag(
                        text = "main",
                        checkable = true,
                        checked = main,
                        onCheckedChange = { main = it },
                    )
                    NTag(
                        text = "extra",
                        checkable = true,
                        checked = extra,
                        onCheckedChange = { extra = it },
                    )
                    NTag(
                        text = "arrow",
                        checkable = true,
                        checked = arrow,
                        onCheckedChange = { arrow = it },
                    )
                }
                Spacer(Modifier.height(8.dp))
                NCollapse(triggerAreas = areas) {
                    NCollapseItem("1", title = "right", extra = { NText("Extra") }) {
                        NText("good")
                    }
                    NCollapseItem("2", title = "right", extra = { NText("Extra") }) {
                        NText("nice")
                    }
                }
            }
        }

        DemoSection(
            title = "Display directive",
            description = "`displayDirective = Show` keeps the content mounted while " +
                "collapsed; this button keeps its counter.",
        ) {
            var clicks by remember { mutableStateOf(0) }
            NCollapse(displayDirective = NCollapseDisplayDirective.Show) {
                NCollapseItem("1", title = "Kept mounted") {
                    NButton(onClick = { clicks += 1 }) { NText("Clicked $clicks") }
                }
                NCollapseItem("2", title = "Also mounted") {
                    NText("Collapse and expand the panels to compare.")
                }
            }
        }

        DemoSection(
            title = "Nested",
            description = "Collapses can be nested inside a panel.",
        ) {
            NCollapse {
                NCollapseItem("1", title = "Outer panel") {
                    NText("Outer content", Modifier.padding(bottom = 8.dp))
                    NCollapse {
                        NCollapseItem("1", title = "Inner one") { NText("good") }
                        NCollapseItem("2", title = "Inner two") { NText("good") }
                    }
                }
                NCollapseItem("2", title = "Second outer panel") { NText("good") }
            }
        }

        DemoSection(
            title = "Controlled expanded names",
            description = "Pass `expandedNames` to control the component and mirror the " +
                "state through `onExpandedNamesChange`.",
        ) {
            var expanded by remember { mutableStateOf(listOf("1")) }
            Column {
                NFlex(size = 8.dp) {
                    NButton(onClick = { expanded = listOf("1", "2") }) { NText("Expand all") }
                    NButton(onClick = { expanded = emptyList() }) { NText("Collapse all") }
                }
                Spacer(Modifier.height(8.dp))
                NCollapse(
                    expandedNames = expanded,
                    onExpandedNamesChange = { expanded = it },
                ) {
                    NCollapseItem("1", title = "Panel one") { NText("First panel") }
                    NCollapseItem("2", title = "Panel two") { NText("Second panel") }
                }
            }
        }
    }
}
