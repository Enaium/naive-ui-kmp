package cn.enaium.naiveui.sample.gallery.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NTabPane
import cn.enaium.naiveui.components.NTabs
import cn.enaium.naiveui.components.NTabsPlacement
import cn.enaium.naiveui.components.NTabsType
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

@Composable
private fun samplePanes(): List<NTabPane> = listOf(
    NTabPane("basic", "Basic") { NText("The basic tab pane.") },
    NTabPane("api", "API") { NText("Tabs expose value, onValueChange, type and placement.") },
    NTabPane("disabled", "Disabled", disabled = true) { NText("Not reachable.") },
)

@Composable
fun TabsDocs() {
    DocsPage(
        title = "Tabs",
        description = "Tabs switch between panes that share one area. Choose line, bar, card or segment styling and place the header on any side.",
    ) {
        DemoSection(
            title = "Basic",
            description = "The default bar type with a controlled value.",
            code = """
var value by remember { mutableStateOf("basic") }
NTabs(
    value = value,
    onValueChange = { value = it },
    panes = listOf(
        NTabPane("basic", "Basic") { NText("Basic pane") },
        NTabPane("api", "API") { NText("API pane") },
    ),
)
            """.trimIndent(),
        ) {
            var value by remember { mutableStateOf<String?>("basic") }
            NTabs(
                value = value,
                onValueChange = { value = it },
                panes = samplePanes(),
            )
        }

        DemoSection(
            title = "Line type",
            description = "Line has a divider under the header and an animated primary indicator.",
        ) {
            var value by remember { mutableStateOf<String?>("basic") }
            NTabs(
                value = value,
                onValueChange = { value = it },
                type = NTabsType.Line,
                panes = samplePanes(),
            )
        }

        DemoSection(
            title = "Card type",
            description = "Card tabs look like a row of attached cards.",
        ) {
            var value by remember { mutableStateOf<String?>("basic") }
            NTabs(
                value = value,
                onValueChange = { value = it },
                type = NTabsType.Card,
                panes = samplePanes(),
            )
        }

        DemoSection(
            title = "Segment type",
            description = "Segment tabs share a rounded rail and highlight the active segment.",
        ) {
            var value by remember { mutableStateOf<String?>("basic") }
            NTabs(
                value = value,
                onValueChange = { value = it },
                type = NTabsType.Segment,
                panes = samplePanes(),
                tabPadding = 10.dp,
            )
        }

        DemoSection(
            title = "Placement",
            description = "Place the header on the top, bottom, left or right.",
        ) {
            var value by remember { mutableStateOf<String?>("basic") }
            var placement by remember { mutableStateOf(NTabsPlacement.Top) }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NTabsPlacement.entries.forEach { option ->
                        NButton(onClick = { placement = option }) {
                            NText(
                                when (option) {
                                    NTabsPlacement.Top -> "Top"
                                    NTabsPlacement.Bottom -> "Bottom"
                                    NTabsPlacement.Left -> "Left"
                                    NTabsPlacement.Right -> "Right"
                                },
                            )
                        }
                    }
                }
                NTabs(
                    value = value,
                    onValueChange = { value = it },
                    placement = placement,
                    type = NTabsType.Line,
                    panes = samplePanes(),
                )
            }
        }

        DemoSection(
            title = "Closable and addable",
            description = "Close buttons and an add button manipulate the pane list.",
        ) {
            var value by remember { mutableStateOf<String?>("one") }
            var next by remember { mutableStateOf(2) }
            var panes by remember {
                mutableStateOf(
                    listOf(
                        NTabPane("one", "One") { NText("First pane") },
                        NTabPane("two", "Two") { NText("Second pane") },
                    ),
                )
            }
            NTabs(
                value = value,
                onValueChange = { value = it },
                panes = panes,
                closable = true,
                addable = true,
                type = NTabsType.Card,
                onClose = { name ->
                    val remaining = panes.filterNot { it.name == name }
                    panes = remaining
                    if (value == name) value = remaining.firstOrNull()?.name
                },
                onAdd = {
                    val name = "tab-$next"
                    panes = panes + NTabPane(
                        name = name,
                        tab = "Tab $next",
                    ) { NText("Content of tab $next") }
                    value = name
                    next += 1
                },
            )
        }

        DemoSection(
            title = "Sizes",
            description = "Use `size` to scale tab labels.",
        ) {
            var smallValue by remember { mutableStateOf<String?>("basic") }
            var largeValue by remember { mutableStateOf<String?>("basic") }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                NTabs(
                    value = smallValue,
                    onValueChange = { smallValue = it },
                    size = NSize.Small,
                    panes = samplePanes(),
                )
                NTabs(
                    value = largeValue,
                    onValueChange = { largeValue = it },
                    size = NSize.Large,
                    panes = samplePanes(),
                )
            }
        }
    }
}
