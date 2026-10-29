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
import cn.enaium.naiveui.components.NLayout
import cn.enaium.naiveui.components.NLayoutCollapseMode
import cn.enaium.naiveui.components.NLayoutSider
import cn.enaium.naiveui.components.NLayoutSiderTrigger
import cn.enaium.naiveui.components.NMenu
import cn.enaium.naiveui.components.NMenuDivider
import cn.enaium.naiveui.components.NMenuItem
import cn.enaium.naiveui.components.NMenuMode
import cn.enaium.naiveui.components.NMenuOption
import cn.enaium.naiveui.components.NSubMenu
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.rememberNMenuState
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

private val bookMenuOptions = listOf(
    NMenuOption("hear-the-wind-sing", "Hear the Wind Sing", icon = NIcons.File),
    NMenuOption(
        key = "pinball-1973",
        label = "Pinball 1973",
        icon = NIcons.File,
        disabled = true,
        children = listOf(NMenuOption("rat", "Rat")),
    ),
    NMenuOption("a-wild-sheep-chase", "A Wild Sheep Chase", icon = NIcons.File, disabled = true),
    NMenuOption(
        key = "dance-dance-dance",
        label = "Dance Dance Dance",
        icon = NIcons.File,
        children = listOf(
            NMenuOption("narrator", "Narrator", icon = NIcons.Person),
            NMenuOption("sheep-man", "Sheep Man", icon = NIcons.Person),
            NMenuOption(
                key = "beverage",
                label = "Beverage",
                icon = NIcons.File,
                children = listOf(NMenuOption("whisky", "Whisky")),
            ),
            NMenuOption(
                key = "food",
                label = "Food",
                children = listOf(NMenuOption("sandwich", "Sandwich")),
            ),
            NMenuOption("the-past", "The past increases. The future recedes."),
        ),
    ),
)

private val fishMenuOptions = listOf(
    NMenuOption(
        key = "fish",
        label = "Fish",
        icon = NIcons.File,
        children = listOf(
            NMenuOption(
                key = "braise",
                label = "Braise",
                children = listOf(NMenuOption("spicy", "Spicy")),
            ),
            NMenuOption(
                key = "steamed",
                label = "Steamed",
                children = listOf(NMenuOption("no-green-onion", "No Green Onion")),
            ),
        ),
    ),
    NMenuOption(
        key = "bear-paw",
        label = "Bear Paw",
        icon = NIcons.Person,
        children = listOf(NMenuOption("protect-wild-animals", "Protect Wild Animals")),
    ),
    NMenuOption(
        key = "both",
        label = "Both",
        icon = NIcons.MenuIcon,
        children = listOf(NMenuOption("can-not", "You can't have your cake and eat it")),
    ),
)

private val longLabelOptions = listOf(
    NMenuOption(
        "1",
        "The lights are extinguished. Matter is transformed and the heavens have shifted. Clay figures vanish into the sea.",
    ),
    NMenuOption(
        "2",
        "The darkness is like a boulder pressing down upon the breast.",
    ),
)

private val routerOptions = listOf(
    NMenuOption("go-back-home", "Go Home", icon = NIcons.MenuIcon),
    NMenuOption("go-to-work", "Go to work", icon = NIcons.File),
)

private val expandOptions = listOf(
    NMenuOption(
        key = "1 parent",
        label = "1 parent",
        children = listOf(NMenuOption("1", "1")),
    ),
    NMenuOption(
        key = "2 parent",
        label = "2 parent",
        children = listOf(NMenuOption("2", "2")),
    ),
    NMenuOption(
        key = "3 parent",
        label = "3 parent",
        children = listOf(NMenuOption("3", "3")),
    ),
)

private class BackendOption(
    val whateverKey: String,
    val whateverLabel: String,
    val whateverDisabled: Boolean = false,
    val whateverChildren: List<BackendOption> = emptyList(),
) {
    fun toMenuOption(): NMenuOption = NMenuOption(
        key = whateverKey,
        label = whateverLabel,
        disabled = whateverDisabled,
        children = whateverChildren.map { it.toMenuOption() },
    )
}

private val backendOptions = listOf(
    BackendOption("hear-the-wind-sing", "Hear the Wind Sing"),
    BackendOption(
        whateverKey = "pinball-1973",
        whateverLabel = "Pinball 1973",
        whateverDisabled = true,
        whateverChildren = listOf(BackendOption("rat", "Rat")),
    ),
    BackendOption("a-wild-sheep-chase", "A Wild Sheep Chase", whateverDisabled = true),
    BackendOption(
        whateverKey = "dance-dance-dance",
        whateverLabel = "Dance Dance Dance",
        whateverChildren = listOf(
            BackendOption("narrator", "Narrator"),
            BackendOption("sheep-man", "Sheep Man"),
            BackendOption(
                whateverKey = "beverage",
                whateverLabel = "Beverage",
                whateverChildren = listOf(BackendOption("whisky", "Whisky")),
            ),
            BackendOption(
                whateverKey = "food",
                whateverLabel = "Food",
                whateverChildren = listOf(BackendOption("sandwich", "Sandwich")),
            ),
            BackendOption("the-past", "The past increases. The future recedes."),
        ),
    ),
)

@Composable
fun MenuDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Menu",
        description = "Menus organize navigation into a vertical list or a horizontal bar, with nested submenus, icons, controlled state and collapse support.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Pass an options tree and a selected key. Icons, extras and disabled entries are supported.",
            code = """
var value by remember { mutableStateOf("hear-the-wind-sing") }
NMenu(
    options = options,
    value = value,
    onValueChange = { value = it },
)
            """.trimIndent(),
        ) {
            var value by remember { mutableStateOf<String?>("hear-the-wind-sing") }
            NMenu(
                options = bookMenuOptions,
                value = value,
                onValueChange = { value = it },
            )
        }

        DemoSection(
            title = "Horizontal",
            description = "Horizontal mode lays top-level entries in a row; submenus open in a popover.",
        ) {
            var value by remember { mutableStateOf<String?>("hear-the-wind-sing") }
            NMenu(
                options = bookMenuOptions,
                value = value,
                onValueChange = { value = it },
                mode = NMenuMode.Horizontal,
            )
        }

        DemoSection(
            title = "Inverted",
            description = "Inverted menus use the theme's inverted surface and light labels.",
        ) {
            var inverted by remember { mutableStateOf(true) }
            var value by remember { mutableStateOf<String?>("hear-the-wind-sing") }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NButton(onClick = { inverted = !inverted }) {
                    NText(if (inverted) "Turn off inverted" else "Turn on inverted")
                }
                NMenu(
                    options = bookMenuOptions,
                    value = value,
                    onValueChange = { value = it },
                    inverted = inverted,
                )
            }
        }

        DemoSection(
            title = "Collapsed",
            description = "Use a collapsible layout sider with `showTrigger`. The trigger sits on the " +
                "sider edge and collapses the menu to icons.",
            code = """
var collapsed by remember { mutableStateOf(true) }
NLayout(hasSider = true) {
    NLayoutSider(
        bordered = true,
        collapseMode = NLayoutCollapseMode.Width,
        collapsedWidth = 64.dp,
        width = 240.dp,
        collapsed = collapsed,
        onCollapsedChange = { collapsed = it },
        showTrigger = true,
        triggerStyle = NLayoutSiderTrigger.ArrowCircle,
    ) {
        NMenu(
            options = options,
            value = value,
            onValueChange = { value = it },
            collapsed = collapsed,
            collapsedWidth = 64.dp,
        )
    }
    NText("Content")
}
            """.trimIndent(),
        ) {
            var collapsed by remember { mutableStateOf(true) }
            var value by remember { mutableStateOf<String?>("hear-the-wind-sing") }
            NLayout(
                modifier = Modifier.height(420.dp),
                hasSider = true,
            ) {
                NLayoutSider(
                    bordered = true,
                    collapseMode = NLayoutCollapseMode.Width,
                    collapsedWidth = 64.dp,
                    width = 240.dp,
                    collapsed = collapsed,
                    onCollapsedChange = { collapsed = it },
                    showTrigger = true,
                    triggerStyle = NLayoutSiderTrigger.ArrowCircle,
                ) {
                    NMenu(
                        options = bookMenuOptions,
                        value = value,
                        onValueChange = { value = it },
                        collapsed = collapsed,
                        collapsedWidth = 64.dp,
                        width = 240.dp,
                    )
                }
                Column(Modifier.padding(24.dp)) {
                    NText("Content", color = theme.colors.text1)
                }
            }
        }

        DemoSection(
            title = "Default expanded keys",
            description = "`defaultExpandedKeys` expands submenus initially. `onExpandedKeysChange` reports user expansions.",
        ) {
            var expanded by remember { mutableStateOf(setOf("dance-dance-dance", "food")) }
            var value by remember { mutableStateOf<String?>(null) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NMenu(
                    options = bookMenuOptions,
                    value = value,
                    onValueChange = { value = it },
                    defaultExpandedKeys = setOf("dance-dance-dance", "food"),
                    onExpandedKeysChange = { expanded = it },
                )
                val expandedText = expanded.sorted().joinToString(", ")
                NText(
                    "Expanded: " + (if (expandedText.isEmpty()) "none" else expandedText),
                    color = theme.colors.text3,
                )
            }
        }

        DemoSection(
            title = "Expand selected option",
            description = "When the value is set from outside, `showOption` expands its ancestors so the selection is visible.",
            code = """
val state = rememberNMenuState()
state.showOption("2")
NMenu(state = state, value = selected, options = options)
            """.trimIndent(),
        ) {
            val menuState = rememberNMenuState()
            var selected by remember { mutableStateOf<String?>("1") }
            var accordion by remember { mutableStateOf(false) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NButton(onClick = { accordion = !accordion }) {
                        NText(if (accordion) "Accordion" else "Normal")
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("1", "2", "3").forEach { key ->
                        NButton(
                            onClick = {
                                selected = key
                                menuState.showOption(key)
                            },
                        ) {
                            NText("Select child $key")
                        }
                    }
                }
                NMenu(
                    state = menuState,
                    options = expandOptions,
                    value = selected,
                    onValueChange = { selected = it },
                    accordion = accordion,
                )
            }
        }

        DemoSection(
            title = "Indent",
            description = "`rootIndent` controls the first level and `indent` controls each nested level.",
        ) {
            var value by remember { mutableStateOf<String?>(null) }
            NMenu(
                options = bookMenuOptions,
                value = value,
                onValueChange = { value = it },
                rootIndent = 36.dp,
                indent = 12.dp,
                defaultExpandedKeys = setOf("dance-dance-dance", "beverage"),
            )
        }

        DemoSection(
            title = "Accordion",
            description = "Accordion mode keeps only one submenu branch open at a time. Deep nesting is supported.",
        ) {
            var value by remember { mutableStateOf<String?>(null) }
            NMenu(
                options = fishMenuOptions,
                value = value,
                onValueChange = { value = it },
                accordion = true,
                defaultExpandedKeys = setOf("fish", "braise"),
            )
        }

        DemoSection(
            title = "Long label",
            description = "Long labels truncate with an ellipsis; constrain the menu width to make it visible.",
        ) {
            var value by remember { mutableStateOf<String?>("1") }
            NMenu(
                options = longLabelOptions,
                value = value,
                onValueChange = { value = it },
                width = 180.dp,
            )
        }

        DemoSection(
            title = "Render label and icon",
            description = "`renderLabel`, `renderIcon` and `expandIcon` customize every option at once.",
            code = """
NMenu(
    options = options,
    renderLabel = { option -> NText(option.label, color = primary) },
    renderIcon = { option -> NIcon(NIcons.File) },
)
            """.trimIndent(),
        ) {
            var value by remember { mutableStateOf<String?>(null) }
            var collapsed by remember { mutableStateOf(false) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NButton(onClick = { collapsed = !collapsed }) {
                    NText(if (collapsed) "Expand" else "Collapse")
                }
                NMenu(
                    options = bookMenuOptions,
                    value = value,
                    onValueChange = { value = it },
                    collapsed = collapsed,
                    collapsedWidth = 64.dp,
                    renderLabel = { option ->
                        NText(
                            option.label,
                            color = if (option.disabled) theme.colors.textDisabled else theme.colors.text1,
                            maxLines = 1,
                        )
                    },
                    renderIcon = { option ->
                        if (option.key != "food") {
                            NIcon(NIcons.File, size = 16.dp, color = theme.colors.text2)
                        }
                    },
                    expandIcon = { _ ->
                        NIcon(NIcons.ChevronDown, size = 14.dp, color = theme.colors.text2)
                    },
                )
            }
        }

        DemoSection(
            title = "Router link",
            description = "Render labels as links and perform routing in `onValueChange`; the selected key identifies the destination.",
        ) {
            var selected by remember { mutableStateOf<String?>(null) }
            var navigated by remember { mutableStateOf("none") }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NMenu(
                    options = routerOptions,
                    value = selected,
                    onValueChange = {
                        selected = it
                        navigated = it
                    },
                    renderLabel = { option ->
                        NText(
                            option.label,
                            color = theme.colors.primary,
                            underline = true,
                        )
                    },
                )
                NText("Route target: $navigated", color = theme.colors.text3)
            }
        }

        DemoSection(
            title = "Select",
            description = "Selection is reported with the selected key. The slot API supports dividers between groups.",
        ) {
            var selected by remember { mutableStateOf<String?>("home") }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NMenu(
                    value = selected,
                    onValueChange = { selected = it },
                    defaultExpandedKeys = setOf("dance"),
                ) {
                    NMenuItem("Going Home", key = "home", icon = NIcons.MenuIcon)
                    NMenuDivider()
                    NMenuItem("Hear the Wind Sing", key = "hear", icon = NIcons.File)
                    NSubMenu("Dance Dance Dance", key = "dance", icon = NIcons.File) {
                        NMenuItem("Narrator", key = "narrator")
                        NMenuItem("Sheep Man", key = "sheep-man")
                    }
                }
                NText("Selected key: ${selected ?: "none"}", color = theme.colors.text3)
            }
        }

        DemoSection(
            title = "Customizing fields",
            description = "Compose options are typed, so backend records are mapped once into `NMenuOption` before rendering.",
        ) {
            var collapsed by remember { mutableStateOf(true) }
            var value by remember { mutableStateOf<String?>(null) }
            val options = remember { backendOptions.map { it.toMenuOption() } }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NButton(onClick = { collapsed = !collapsed }) {
                    NText(if (collapsed) "Expand" else "Collapse")
                }
                NMenu(
                    options = options,
                    value = value,
                    onValueChange = { value = it },
                    collapsed = collapsed,
                    collapsedWidth = 64.dp,
                )
            }
        }

        DemoSection(
            title = "Control option visibility",
            description = "Set `show = false` (or omit options from the list) to hide entries for the current role.",
        ) {
            var showSecond by remember { mutableStateOf(true) }
            var value by remember { mutableStateOf<String?>(null) }
            val options = listOf(
                NMenuOption(
                    key = "parent1",
                    label = "parent 1",
                    icon = NIcons.Person,
                    children = listOf(NMenuOption("1.1", "1.1")),
                ),
                NMenuOption(
                    key = "parent2",
                    label = "parent 2",
                    icon = NIcons.File,
                    show = showSecond,
                    children = listOf(
                        NMenuOption("2.1", "2.1"),
                        NMenuOption("2.2", "2.2"),
                        NMenuOption("2.3", "2.3"),
                    ),
                ),
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NButton(onClick = { showSecond = !showSecond }) {
                    NText(if (showSecond) "Hide second parent" else "Show second parent")
                }
                NMenu(
                    options = options,
                    value = value,
                    onValueChange = { value = it },
                )
            }
        }

        DemoSection(
            title = "Slot API",
            description = "Use `NMenuItem` and `NSubMenu` when entries need custom composition.",
        ) {
            var value by remember { mutableStateOf<String?>("dashboard") }
            NMenu(
                value = value,
                onValueChange = { value = it },
                defaultExpandedKeys = setOf("workspace"),
            ) {
                NMenuItem("Dashboard", key = "dashboard", icon = NIcons.MenuIcon)
                NSubMenu("Workspace", key = "workspace", icon = NIcons.File) {
                    NMenuItem("Projects", key = "projects")
                    NMenuItem("Tasks", key = "tasks")
                }
                NMenuItem("People", key = "people", icon = NIcons.Person)
            }
        }
    }
}
