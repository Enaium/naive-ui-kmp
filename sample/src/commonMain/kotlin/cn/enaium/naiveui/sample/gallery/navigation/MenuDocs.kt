package cn.enaium.naiveui.sample.gallery.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NMenu
import cn.enaium.naiveui.components.NMenuItem
import cn.enaium.naiveui.components.NMenuMode
import cn.enaium.naiveui.components.NMenuOption
import cn.enaium.naiveui.components.NSubMenu
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage

private val menuOptions = listOf(
    NMenuOption("dashboard", "Dashboard", icon = NIcons.MenuIcon),
    NMenuOption(
        key = "workspace",
        label = "Workspace",
        icon = NIcons.File,
        children = listOf(
            NMenuOption("projects", "Projects"),
            NMenuOption("tasks", "Tasks", extra = "3"),
            NMenuOption("archive", "Archive", disabled = true),
        ),
    ),
    NMenuOption("people", "People", icon = NIcons.Person),
    NMenuOption("calendar", "Calendar", icon = NIcons.Date),
)

@Composable
fun MenuDocs() {
    DocsPage(
        title = "Menu",
        description = "Menus organize navigation into a vertical list or a horizontal bar, with nested submenus, icons and collapse support.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Pass an options tree and a selected key. Icons, extras and disabled entries are supported.",
            code = """
var value by remember { mutableStateOf("dashboard") }
NMenu(
    options = options,
    value = value,
    onValueChange = { value = it },
)
            """.trimIndent(),
        ) {
            var value by remember { mutableStateOf<String?>("dashboard") }
            NMenu(
                options = menuOptions,
                value = value,
                onValueChange = { value = it },
            )
        }

        DemoSection(
            title = "Horizontal",
            description = "Horizontal mode lays top-level entries in a row; submenus open in a popover.",
        ) {
            var value by remember { mutableStateOf<String?>("dashboard") }
            NMenu(
                options = menuOptions,
                value = value,
                onValueChange = { value = it },
                mode = NMenuMode.Horizontal,
            )
        }

        DemoSection(
            title = "Inverted",
            description = "Inverted menus use the theme's inverted surface and light labels.",
        ) {
            var value by remember { mutableStateOf<String?>("dashboard") }
            NMenu(
                options = menuOptions,
                value = value,
                onValueChange = { value = it },
                inverted = true,
            )
        }

        DemoSection(
            title = "Collapsed",
            description = "Collapse the menu to icons only. `collapsedWidth` controls the rail width.",
        ) {
            var collapsed by remember { mutableStateOf(false) }
            var value by remember { mutableStateOf<String?>("dashboard") }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NButton(onClick = { collapsed = !collapsed }) {
                        if (collapsed) {
                            NText("Expand")
                        } else {
                            NText("Collapse")
                        }
                    }
                }
                NMenu(
                    options = menuOptions,
                    value = value,
                    onValueChange = { value = it },
                    collapsed = collapsed,
                    collapsedWidth = 56.dp,
                )
            }
        }

        DemoSection(
            title = "Indent and accordion",
            description = "`indent` controls nesting indentation. With `accordion`, only one submenu stays open at a time.",
        ) {
            var value by remember { mutableStateOf<String?>("projects") }
            Column(Modifier.padding(vertical = 4.dp)) {
                NMenu(
                    options = menuOptions,
                    value = value,
                    onValueChange = { value = it },
                    indent = 24.dp,
                    defaultExpandedKeys = setOf("workspace"),
                    accordion = true,
                    width = 260.dp,
                )
            }
        }

        DemoSection(
            title = "Slot API",
            description = "Use `NMenuItem` and `NSubMenu` when entries need custom composition.",
            code = """
NMenu(value = value, onValueChange = { value = it }) {
    NMenuItem("Dashboard", key = "dashboard")
    NSubMenu("Workspace", key = "workspace") {
        NMenuItem("Projects", key = "projects")
    }
}
            """.trimIndent(),
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
