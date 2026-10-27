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
import cn.enaium.naiveui.components.NCollapseItem
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NFlex
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NStatus

@Composable
fun CollapseDocs() {
    DocsPage(
        title = "Collapse",
        description = "Vertically stacked collapsible panels with accordion mode, " +
            "arrow placement and animated content.",
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
            title = "Default expanded & disabled",
            description = "Panels can start expanded; disabled items ignore clicks.",
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
                NCollapseItem("4", title = "disabled light", disabled = true) {
                    NText("You cannot open this panel.")
                }
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
            title = "Header extra & item header click",
            description = "An extra slot is rendered at the end of the header; " +
                "onItemHeaderClick reports the toggled item.",
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
                    NCollapseItem(
                        name = "1",
                        title = "With extra",
                        extra = { NTag("Extra", type = NStatus.Info) },
                    ) {
                        NText("The header has a tag in its extra slot.")
                    }
                    NCollapseItem(
                        name = "2",
                        title = "Another panel",
                        extra = { NTag("Info", type = NStatus.Success) },
                    ) {
                        NText("Click either header to see the callback output.")
                    }
                }
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
                    NButton(
                        onClick = { expanded = listOf("1", "2", "3") },
                    ) { NText("Expand all") }
                    NButton(
                        onClick = { expanded = emptyList() },
                    ) { NText("Collapse all") }
                }
                Spacer(Modifier.height(8.dp))
                NCollapse(
                    expandedNames = expanded,
                    onExpandedNamesChange = { expanded = it },
                ) {
                    NCollapseItem("1", title = "Panel one") { NText("First panel") }
                    NCollapseItem("2", title = "Panel two") { NText("Second panel") }
                    NCollapseItem("3", title = "Panel three") { NText("Third panel") }
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
    }
}
