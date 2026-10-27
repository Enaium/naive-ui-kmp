package cn.enaium.naiveui.sample.gallery.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import cn.enaium.naiveui.components.NCard
import cn.enaium.naiveui.components.NInput
import cn.enaium.naiveui.components.NTabPane
import cn.enaium.naiveui.components.NTabs
import cn.enaium.naiveui.components.NTabsDisplayDirective
import cn.enaium.naiveui.components.NTabsJustify
import cn.enaium.naiveui.components.NTabsPlacement
import cn.enaium.naiveui.components.NTabsTrigger
import cn.enaium.naiveui.components.NTabsType
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.rememberNTabsState
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

@Composable
private fun songPanes(): List<NTabPane> = listOf(
    NTabPane("oasis", "Oasis") { NText("Wonderwall") },
    NTabPane("the-beatles", "the Beatles") { NText("Hey Jude") },
    NTabPane("jay-chou", "Jay Chou") { NText("Qilixiang") },
)

@Composable
private fun numberedPanes(count: Int): List<NTabPane> = (1..count).map { value ->
    NTabPane(value.toString(), value.toString()) { NText("Content $value") }
}

@Composable
fun TabsDocs() {
    DocsPage(
        title = "Tabs",
        description = "Tabs switch between panes that share one area, with line, bar, card and segment styles, scrolling, custom labels and configurable activation.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Line tabs with an animated indicator, plus a card of form tabs.",
            code = """
NTabs(
    value = value,
    onValueChange = { value = it },
    type = NTabsType.Line,
    panes = listOf(
        NTabPane("oasis", "Oasis") { NText("Wonderwall") },
        NTabPane("api", "API") { NText("Hey Jude") },
    ),
)
            """.trimIndent(),
        ) {
            var lineValue by remember { mutableStateOf<String?>("oasis") }
            var cardValue by remember { mutableStateOf<String?>("signin") }
            var username by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                NCard(title = "Song of") {
                    NTabs(
                        value = lineValue,
                        onValueChange = { lineValue = it },
                        type = NTabsType.Line,
                        animated = true,
                        panes = songPanes(),
                    )
                }
                NCard {
                    NTabs(
                        value = cardValue,
                        onValueChange = { cardValue = it },
                        type = NTabsType.Bar,
                        size = NSize.Large,
                        animated = true,
                        panes = listOf(
                            NTabPane("signin", "Sign in") {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    NInput(
                                        value = username,
                                        onValueChange = { username = it },
                                        placeholder = "Username",
                                    )
                                    NInput(
                                        value = password,
                                        onValueChange = { password = it },
                                        placeholder = "Password",
                                    )
                                    NButton(onClick = {}) { NText("Sign In") }
                                }
                            },
                            NTabPane("signup", "Sign up") {
                                NText("Create an account to keep your playlists.")
                            },
                        ),
                    )
                }
            }
        }

        DemoSection(
            title = "Segment",
            description = "Segment tabs share a rounded rail and highlight the active segment.",
        ) {
            var value by remember { mutableStateOf<String?>("oasis") }
            NTabs(
                value = value,
                onValueChange = { value = it },
                type = NTabsType.Segment,
                animated = true,
                panes = songPanes(),
            )
        }

        DemoSection(
            title = "Card",
            description = "Card tabs can be closed. At least one tab is kept.",
        ) {
            var value by remember { mutableStateOf<String?>("1") }
            var panels by remember { mutableStateOf((1..15).toList()) }
            var message by remember { mutableStateOf("") }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NTabs(
                    value = value,
                    onValueChange = { value = it },
                    type = NTabsType.Card,
                    closable = true,
                    tabMinWidth = 80.dp,
                    panes = panels.map { panel ->
                        NTabPane(panel.toString(), panel.toString()) {
                            NText("Panel $panel")
                        }
                    },
                    onClose = { name ->
                        if (panels.size == 1) {
                            message = "The last one!"
                        } else {
                            val remaining = panels.filterNot { it.toString() == name }
                            panels = remaining
                            if (value == name) value = remaining.firstOrNull()?.toString()
                            message = "Closed $name"
                        }
                    },
                )
                if (message.isNotEmpty()) {
                    NText(message, color = cn.enaium.naiveui.theme.LocalNTheme.current.colors.text3)
                }
            }
        }

        DemoSection(
            title = "Flex tabs",
            description = "`justifyContent` distributes line and bar tabs across the available width.",
        ) {
            var lineValue by remember { mutableStateOf<String?>("oasis") }
            var barValue by remember { mutableStateOf<String?>("oasis") }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                NCard(title = "Space evenly") {
                    NTabs(
                        value = lineValue,
                        onValueChange = { lineValue = it },
                        type = NTabsType.Line,
                        justifyContent = NTabsJustify.SpaceEvenly,
                        panes = songPanes(),
                    )
                }
                NTabs(
                    value = barValue,
                    onValueChange = { barValue = it },
                    justifyContent = NTabsJustify.SpaceAround,
                    panes = songPanes(),
                )
            }
        }

        DemoSection(
            title = "Prefix and suffix",
            description = "Add supporting content before and after the tab list.",
        ) {
            var value by remember { mutableStateOf<String?>("oasis") }
            NTabs(
                value = value,
                onValueChange = { value = it },
                prefix = { NText("Prefix") },
                suffix = { NText("Suffix") },
                panes = songPanes(),
            )
        }

        DemoSection(
            title = "Size",
            description = "Small, medium and large tabs, including a closable card variant.",
        ) {
            var size by remember { mutableStateOf(NSize.Medium) }
            var lineValue by remember { mutableStateOf<String?>("oasis") }
            var barValue by remember { mutableStateOf<String?>("oasis") }
            var cardValue by remember { mutableStateOf<String?>("oasis") }
            var cardNames by remember { mutableStateOf(listOf("oasis", "the-beatles", "jay-chou")) }
            val cardPanes = cardNames.map { name ->
                NTabPane(name, name) { NText("Content of $name") }
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NButton(onClick = { size = NSize.Small }) { NText("Small") }
                    NButton(onClick = { size = NSize.Medium }) { NText("Medium") }
                    NButton(onClick = { size = NSize.Large }) { NText("Large") }
                }
                NTabs(
                    value = lineValue,
                    onValueChange = { lineValue = it },
                    type = NTabsType.Line,
                    size = size,
                    panes = songPanes(),
                )
                NTabs(
                    value = barValue,
                    onValueChange = { barValue = it },
                    type = NTabsType.Bar,
                    size = size,
                    panes = songPanes(),
                )
                NTabs(
                    value = cardValue,
                    onValueChange = { cardValue = it },
                    type = NTabsType.Card,
                    size = size,
                    closable = true,
                    panes = cardPanes,
                    onClose = { name ->
                        val remaining = cardNames.filterNot { it == name }
                        if (remaining.isNotEmpty()) {
                            cardNames = remaining
                            if (cardValue == name) cardValue = remaining.first()
                        }
                    },
                )
            }
        }

        DemoSection(
            title = "Display directive",
            description = "`If` resets pane state when leaving; `Show` and `ShowLazy` retain it. The lazy variant composes a pane only after its first visit.",
        ) {
            var value by remember { mutableStateOf<String?>("if") }
            val panes = listOf(
                NTabPane(
                    name = "if",
                    tab = "if",
                    displayDirective = NTabsDisplayDirective.If,
                ) {
                    var text by remember { mutableStateOf("") }
                    NInput(
                        value = text,
                        onValueChange = { text = it },
                        placeholder = "My content will be reset",
                    )
                },
                NTabPane(
                    name = "show",
                    tab = "show",
                    displayDirective = NTabsDisplayDirective.Show,
                ) {
                    var text by remember { mutableStateOf("") }
                    NInput(
                        value = text,
                        onValueChange = { text = it },
                        placeholder = "My content won't be reset",
                    )
                },
                NTabPane(
                    name = "lazy",
                    tab = "show:lazy",
                    displayDirective = NTabsDisplayDirective.ShowLazy,
                ) {
                    var text by remember { mutableStateOf("") }
                    NInput(
                        value = text,
                        onValueChange = { text = it },
                        placeholder = "Lazily loaded, then retained",
                    )
                },
            )
            NTabs(
                value = value,
                onValueChange = { value = it },
                panes = panes,
            )
        }

        DemoSection(
            title = "Addable",
            description = "Card tabs can add and remove panes. Add is disabled at ten tabs.",
        ) {
            var value by remember { mutableStateOf<String?>("1") }
            var panels by remember { mutableStateOf((1..5).toList()) }
            NTabs(
                value = value,
                onValueChange = { value = it },
                type = NTabsType.Card,
                addable = panels.size < 10,
                closable = panels.size > 1,
                tabMinWidth = 80.dp,
                prefix = { NText("Prefix") },
                suffix = { NText("Suffix") },
                panes = panels.map { panel ->
                    NTabPane(panel.toString(), panel.toString()) {
                        NText("Panel $panel")
                    }
                },
                onAdd = {
                    val next = (panels.maxOrNull() ?: 0) + 1
                    panels = panels + next
                    value = next.toString()
                },
                onClose = { name ->
                    val remaining = panels.filterNot { it.toString() == name }
                    panels = remaining
                    if (value == name) value = remaining.lastOrNull()?.toString()
                },
            )
        }

        DemoSection(
            title = "Before leave",
            description = "`onBeforeLeave` can veto switching. This example blocks the \"Not allowed\" tab while the lock is enabled.",
        ) {
            var locked by remember { mutableStateOf(true) }
            var value by remember { mutableStateOf<String?>("okay") }
            var message by remember { mutableStateOf("Freshly opened") }
            val panes = listOf(
                NTabPane("wait", "Wait for 1s") { NText("+1s") },
                NTabPane("not-allowed", "Not allowed") { NText("???") },
                NTabPane("okay", "Okay") { NText("Just so so") },
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NButton(onClick = { locked = !locked }) {
                    NText(if (locked) "Unlock switching" else "Lock switching")
                }
                NTabs(
                    value = value,
                    onValueChange = { value = it },
                    onBeforeLeave = { target, _ ->
                        if (locked && target == "not-allowed") {
                            message = "Not allowed"
                            false
                        } else {
                            message = "Switched to $target"
                            true
                        }
                    },
                    panes = panes,
                )
                NText(message, color = cn.enaium.naiveui.theme.LocalNTheme.current.colors.text3)
            }
        }

        DemoSection(
            title = "No pane",
            description = "Leave pane content null to render headers only.",
        ) {
            var value by remember { mutableStateOf<String?>("Riders On the Storm") }
            val panes = listOf(
                NTabPane("Riders On the Storm", "Riders On the Storm"),
                NTabPane("Light My Fire", "Light My Fire"),
                NTabPane("Break On Through", "Break On Through"),
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NTabs(
                    value = value,
                    onValueChange = { value = it },
                    type = NTabsType.Line,
                    panes = panes,
                )
                NText("Selected: ${value ?: "none"}")
            }
        }

        DemoSection(
            title = "Update bar manually",
            description = "Reordering tabs while keeping the value stable is an edge case; `syncBarPosition` nudges the indicator.",
        ) {
            val tabsState = rememberNTabsState()
            var order by remember { mutableStateOf(listOf("a", "b")) }
            var value by remember { mutableStateOf<String?>("a") }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NButton(
                    onClick = {
                        order = order.reversed()
                        value = "a"
                        tabsState.syncBarPosition()
                    },
                ) { NText("Useless change") }
                NTabs(
                    state = tabsState,
                    value = value,
                    onValueChange = { value = it },
                    panes = order.map { name -> NTabPane(name, "I'm $name") },
                )
            }
        }

        DemoSection(
            title = "Bar width",
            description = "Set the line indicator length with `barWidth`.",
        ) {
            var value by remember { mutableStateOf<String?>("oasis") }
            NTabs(
                value = value,
                onValueChange = { value = it },
                type = NTabsType.Line,
                barWidth = 28.dp,
                panes = songPanes(),
            )
        }

        DemoSection(
            title = "Trigger",
            description = "Hover tabs to activate them with `trigger = Hover`.",
        ) {
            var value by remember { mutableStateOf<String?>("oasis") }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NText("Move the pointer across the tabs.")
                NTabs(
                    value = value,
                    onValueChange = { value = it },
                    type = NTabsType.Line,
                    trigger = NTabsTrigger.Hover,
                    panes = songPanes(),
                )
            }
        }

        DemoSection(
            title = "Placement",
            description = "Place the header on any side and switch between card, bar and line styles.",
        ) {
            var placement by remember { mutableStateOf(NTabsPlacement.Left) }
            var type by remember { mutableStateOf(NTabsType.Card) }
            var value by remember { mutableStateOf<String?>("1") }
            val vertical = placement == NTabsPlacement.Left || placement == NTabsPlacement.Right
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NTabsPlacement.entries.forEach { option ->
                        NButton(onClick = { placement = option }) {
                            NText(
                                when (option) {
                                    NTabsPlacement.Top -> "top"
                                    NTabsPlacement.Bottom -> "bottom"
                                    NTabsPlacement.Left -> "left"
                                    NTabsPlacement.Right -> "right"
                                },
                            )
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(NTabsType.Card, NTabsType.Bar, NTabsType.Line).forEach { option ->
                        NButton(onClick = { type = option }) {
                            NText(option.name.lowercase())
                        }
                    }
                }
                NTabs(
                    value = value,
                    onValueChange = { value = it },
                    type = type,
                    placement = placement,
                    animated = true,
                    modifier = Modifier.then(if (vertical) Modifier.height(240.dp) else Modifier),
                    panes = numberedPanes(6),
                )
            }
        }

        DemoSection(
            title = "Show scroll button",
            description = "Scroll buttons appear when the tab strip overflows.",
        ) {
            var showScrollButton by remember { mutableStateOf(true) }
            var placement by remember { mutableStateOf(NTabsPlacement.Top) }
            var type by remember { mutableStateOf(NTabsType.Card) }
            var value by remember { mutableStateOf<String?>("1") }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NButton(onClick = { showScrollButton = !showScrollButton }) {
                        NText(if (showScrollButton) "Hide buttons" else "Show buttons")
                    }
                    NButton(onClick = { placement = NTabsPlacement.Top }) { NText("Top") }
                    NButton(onClick = { placement = NTabsPlacement.Bottom }) { NText("Bottom") }
                    NButton(onClick = { type = NTabsType.Card }) { NText("Card") }
                    NButton(onClick = { type = NTabsType.Line }) { NText("Line") }
                }
                NTabs(
                    value = value,
                    onValueChange = { value = it },
                    type = type,
                    placement = placement,
                    showScrollButton = showScrollButton,
                    panes = numberedPanes(15),
                )
            }
        }

        DemoSection(
            title = "Center active tab",
            description = "`centerActiveTab` scrolls the active tab into the middle of the strip.",
        ) {
            var value by remember { mutableStateOf<String?>("1") }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("1", "8", "15").forEach { tab ->
                        NButton(onClick = { value = tab }) {
                            NText("Select $tab")
                        }
                    }
                }
                NTabs(
                    value = value,
                    onValueChange = { value = it },
                    type = NTabsType.Card,
                    centerActiveTab = true,
                    panes = numberedPanes(15),
                )
            }
        }
    }
}
