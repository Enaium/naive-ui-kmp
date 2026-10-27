package cn.enaium.naiveui.sample.gallery.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NDropdown
import cn.enaium.naiveui.components.NDropdownOption
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NFlex
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NTrigger
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NStatus

private val tripOptions = listOf<NDropdownOption>(
    NDropdownOption.Label("marina bay sands", "Marina Bay Sands", disabled = true),
    NDropdownOption.Label("browns hotel, london", "Brown's Hotel, London"),
    NDropdownOption.Label("atlantis bahamas, nassau", "Atlantis Bahamas, Nassau"),
    NDropdownOption.Label("the beverly hills hotel, los angeles", "The Beverly Hills Hotel, Los Angeles"),
)

@Composable
fun DropdownDocs() {
    DocsPage(
        title = "Dropdown",
        description = "A menu that appears on hover, click or manual control, with " +
            "groups, dividers, icons and cascading submenus.",
    ) {
        var selected by remember { mutableStateOf("none") }

        DemoSection(
            title = "Basic",
            description = "Hover the button to open the menu. Disabled options ignore clicks.",
            code = """
NDropdown(options = options, trigger = NTrigger.Hover, onSelect = { key -> ... }) {
    NButton(onClick = {}) { NText("Go For a Trip") }
}
""".trimIndent(),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NDropdown(
                    options = tripOptions,
                    trigger = NTrigger.Hover,
                    onSelect = { selected = it },
                ) {
                    NButton { NText("Go For a Trip") }
                }
                NText("Selected: $selected")
            }
        }

        DemoSection(
            title = "Trigger & placement",
            description = "Click opens the menu below; manual mode is driven by `show` " +
                "and `onShowChange`.",
        ) {
            var manualShow by remember { mutableStateOf(false) }
            NFlex(size = 12.dp, wrap = true) {
                NDropdown(
                    options = tripOptions,
                    trigger = NTrigger.Click,
                    placement = NPlacement.BottomStart,
                    onSelect = { selected = it },
                ) {
                    NButton { NText("Click me") }
                }
                NDropdown(
                    options = tripOptions,
                    trigger = NTrigger.Manual,
                    placement = NPlacement.BottomEnd,
                    show = manualShow,
                    onShowChange = { manualShow = it },
                    onSelect = { selected = it },
                ) {
                    NButton { NText("Manual") }
                }
                NButton(onClick = { manualShow = !manualShow }) {
                    NText(if (manualShow) "Hide menu" else "Show menu")
                }
            }
        }

        DemoSection(
            title = "Group, header & divider",
            description = "Use `Group`, `Header` and `Divider` entries to structure a long menu.",
        ) {
            val options = listOf<NDropdownOption>(
                NDropdownOption.Header("header", "Series"),
                NDropdownOption.Group(
                    key = "podcasts",
                    label = "Podcasts",
                    children = listOf(
                        NDropdownOption.Label("design", "Design Notes", extra = "12"),
                        NDropdownOption.Label("code", "Code Radio", extra = "8"),
                    ),
                ),
                NDropdownOption.Divider("d1"),
                NDropdownOption.Label("about", "About"),
                NDropdownOption.Label("contact", "Contact", disabled = true),
            )
            NDropdown(options = options, trigger = NTrigger.Click, onSelect = { selected = it }) {
                NButton { NText("Browse") }
            }
        }

        DemoSection(
            title = "Cascade (nested submenu)",
            description = "Options with `children` open a cascading submenu.",
        ) {
            val options = listOf<NDropdownOption>(
                NDropdownOption.Label("jay gatsby", "Jay Gatsby"),
                NDropdownOption.Label("daisy buchanan", "Daisy Buchanan", icon = NIcons.Person),
                NDropdownOption.Divider("d1"),
                NDropdownOption.Label("nick carraway", "Nick Carraway"),
                NDropdownOption.Label(
                    key = "others1",
                    label = "Others",
                    children = listOf(
                        NDropdownOption.Label("jordan baker", "Jordan Baker"),
                        NDropdownOption.Label("tom buchanan", "Tom Buchanan"),
                        NDropdownOption.Label(
                            key = "others2",
                            label = "Others",
                            disabled = true,
                            children = listOf(
                                NDropdownOption.Label("chicken", "Chicken"),
                                NDropdownOption.Label("beef", "Beef"),
                            ),
                        ),
                    ),
                ),
            )
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NDropdown(
                    options = options,
                    trigger = NTrigger.Click,
                    placement = NPlacement.BottomStart,
                    onSelect = { selected = it },
                ) {
                    NButton { NText("People & Food") }
                }
                NText("Selected: $selected")
            }
        }

        DemoSection(
            title = "Icon & show arrow",
            description = "Options accept icons and the menu can show a pointing arrow.",
        ) {
            val options = listOf<NDropdownOption>(
                NDropdownOption.Label("search", "Search", icon = NIcons.Search),
                NDropdownOption.Label("download", "Download", icon = NIcons.Download),
                NDropdownOption.Label("share", "Share", icon = NIcons.Attach),
                NDropdownOption.Label("settings", "Settings", icon = NIcons.Filter),
            )
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NDropdown(
                    options = options,
                    trigger = NTrigger.Hover,
                    showArrow = true,
                    onSelect = { selected = it },
                ) {
                    NButton { NIcon(NIcons.MenuIcon); NText("Actions", Modifier.padding(start = 6.dp)) }
                }
                NTag("Selected: $selected", type = NStatus.Primary)
            }
        }
    }
}
