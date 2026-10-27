package cn.enaium.naiveui.sample.gallery.general

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NDropdown
import cn.enaium.naiveui.components.NDropdownOption
import cn.enaium.naiveui.components.NFlex
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NTooltip
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NTrigger
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import kotlin.math.roundToInt

private val tripOptions = listOf<NDropdownOption>(
    NDropdownOption.Label("marina bay sands", "Marina Bay Sands", disabled = true),
    NDropdownOption.Label("browns hotel, london", "Brown's Hotel, London"),
    NDropdownOption.Label("atlantis bahamas, nassau", "Atlantis Bahamas, Nassau"),
    NDropdownOption.Label("the beverly hills hotel", "The Beverly Hills Hotel, Los Angeles"),
)

private val cascadeOptions = listOf<NDropdownOption>(
    NDropdownOption.Label("jay gatsby", "Jay Gatsby"),
    NDropdownOption.Label("daisy buchanan", "Daisy Buchanan"),
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
                children = listOf(
                    NDropdownOption.Label("chicken", "Chicken"),
                    NDropdownOption.Label("beef", "Beef"),
                ),
            ),
        ),
    ),
)

@Composable
fun DropdownDocs() {
    DocsPage(
        title = "Dropdown",
        description = "A menu that appears on hover, click or manual control, with " +
            "groups, dividers, icons, custom rendering and cascading submenus.",
    ) {
        var selected by remember { mutableStateOf("none") }
        val theme = LocalNTheme.current

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
            title = "Trigger",
            description = "Hover, click and manual triggers. Manual mode is driven by " +
                "`show` and `onShowChange`.",
        ) {
            var manualShow by remember { mutableStateOf(false) }
            NFlex(size = 12.dp, wrap = true) {
                NDropdown(
                    options = tripOptions,
                    trigger = NTrigger.Hover,
                    onSelect = { selected = it },
                ) {
                    NButton { NText("Hover!") }
                }
                NDropdown(
                    options = tripOptions,
                    trigger = NTrigger.Click,
                    onSelect = { selected = it },
                ) {
                    NButton { NText("Click!") }
                }
                NDropdown(
                    options = tripOptions,
                    trigger = NTrigger.Manual,
                    show = manualShow,
                    onShowChange = { manualShow = it },
                    onSelect = { selected = it },
                ) {
                    NButton { NText("Manual") }
                }
                NButton(onClick = { manualShow = !manualShow }) {
                    NText(if (manualShow) "Hide manual" else "Show manual")
                }
            }
        }

        DemoSection(
            title = "Placement",
            description = "Align the menu with `placement`.",
        ) {
            NFlex(size = 12.dp, wrap = true) {
                NDropdown(
                    options = tripOptions,
                    trigger = NTrigger.Click,
                    placement = NPlacement.BottomStart,
                    onSelect = { selected = it },
                ) { NButton { NText("Bottom start") } }
                NDropdown(
                    options = tripOptions,
                    trigger = NTrigger.Click,
                    placement = NPlacement.BottomEnd,
                    onSelect = { selected = it },
                ) { NButton { NText("Bottom end") } }
                NDropdown(
                    options = tripOptions,
                    trigger = NTrigger.Click,
                    placement = NPlacement.TopStart,
                    onSelect = { selected = it },
                ) { NButton { NText("Top start") } }
            }
        }

        DemoSection(
            title = "Arrow",
            description = "`showArrow` draws an arrow pointing at the trigger.",
        ) {
            NDropdown(
                options = tripOptions,
                trigger = NTrigger.Click,
                showArrow = true,
                onSelect = { selected = it },
            ) {
                NButton { NText("Go For a Trip") }
            }
        }

        DemoSection(
            title = "Icon",
            description = "Options can carry an icon.",
        ) {
            val options = listOf<NDropdownOption>(
                NDropdownOption.Label("profile", "User Profile", icon = NIcons.Person),
                NDropdownOption.Label("editProfile", "Edit Profile", icon = NIcons.File),
                NDropdownOption.Label("logout", "Logout", icon = NIcons.ArrowBack),
            )
            NDropdown(options = options, trigger = NTrigger.Hover, onSelect = { selected = it }) {
                NButton { NText("User Profile") }
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
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NDropdown(
                    options = cascadeOptions,
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
            title = "Batch Render",
            description = "`renderLabel` and `renderIcon` customize every option in one place.",
        ) {
            val options = listOf<NDropdownOption>(
                NDropdownOption.Group(
                    key = "main",
                    label = "People & Food",
                    children = listOf(
                        NDropdownOption.Label("jay gatsby", "Jay Gatsby"),
                        NDropdownOption.Label("daisy buchanan", "Daisy Buchanan"),
                        NDropdownOption.Label("nick carraway", "Nick Carraway"),
                        NDropdownOption.Label(
                            key = "food",
                            label = "Food",
                            children = listOf(
                                NDropdownOption.Label("chicken", "Chicken"),
                                NDropdownOption.Label("beef", "Beef"),
                            ),
                        ),
                    ),
                ),
            )
            NDropdown(
                options = options,
                trigger = NTrigger.Click,
                placement = NPlacement.BottomStart,
                onSelect = { selected = it },
                renderLabel = { option ->
                    when (option) {
                        is NDropdownOption.Group -> NText(
                            text = option.label,
                            color = theme.colors.text3,
                            size = 12.sp,
                            strong = true,
                        )
                        is NDropdownOption.Label -> NText(
                            text = option.label,
                            color = theme.colors.primary,
                            underline = true,
                        )
                        else -> Unit
                    }
                },
                renderIcon = { option ->
                    NIcon(
                        NIcons.Person,
                        size = 16.dp,
                        color = theme.colors.icon,
                    )
                },
            ) {
                NButton { NText("Batch Rendered") }
            }
        }

        DemoSection(
            title = "Custom Option Props",
            description = "Per-option `onClick` callbacks run before selection.",
        ) {
            var action by remember { mutableStateOf("none") }
            val options = listOf<NDropdownOption>(
                NDropdownOption.Label(
                    key = "marina bay sands",
                    label = "Marina Bay Sands",
                    onClick = { action = "Marina clicked" },
                ),
                NDropdownOption.Label(
                    key = "browns hotel",
                    label = "Brown's Hotel, London",
                    children = listOf(
                        NDropdownOption.Label(
                            key = "chicken",
                            label = "Chicken",
                            disabled = true,
                            onClick = { action = "Chicken clicked" },
                        ),
                        NDropdownOption.Label("beef", "Beef"),
                    ),
                ),
                NDropdownOption.Label(
                    key = "atlantis",
                    label = "Atlantis Bahamas, Nassau",
                    onClick = { action = "Atlantis clicked" },
                ),
            )
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NDropdown(options = options, trigger = NTrigger.Hover, onSelect = { selected = it }) {
                    NButton { NText("Go For a Trip") }
                }
                NText("Action: $action")
            }
        }

        DemoSection(
            title = "Pure Render Content",
            description = "`NDropdownOption.Content` renders arbitrary composables inside the menu.",
        ) {
            val options = listOf<NDropdownOption>(
                NDropdownOption.Content("header") {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(theme.colors.primary),
                            contentAlignment = Alignment.Center,
                        ) {
                            NText("OW", color = theme.colors.base, strong = true)
                        }
                        Column(Modifier.padding(start = 12.dp)) {
                            NText("Office Worker")
                            NText(
                                "No doubt, you are the brightest star in the office",
                                color = theme.colors.text3,
                                size = 12.sp,
                            )
                        }
                    }
                },
                NDropdownOption.Divider("header-divider"),
                NDropdownOption.Label("stmt1", "Processed 342 group messages"),
                NDropdownOption.Label("stmt2", "Mentioned 58 times"),
                NDropdownOption.Label("stmt3", "Joined 17 groups"),
            )
            NDropdown(options = options, trigger = NTrigger.Hover, onSelect = { selected = it }) {
                NButton { NText("2021 Week 36") }
            }
        }

        DemoSection(
            title = "Add Tooltip for Option",
            description = "`optionWrapper` wraps each option, for example with a tooltip.",
        ) {
            NDropdown(
                options = tripOptions,
                trigger = NTrigger.Hover,
                onSelect = { selected = it },
                optionWrapper = { option, optionContent ->
                    NTooltip(text = option.key) {
                        optionContent()
                    }
                },
            ) {
                NButton { NText("Go For a Trip") }
            }
        }

        DemoSection(
            title = "Size",
            description = "Dropdowns support the small, medium, large and huge sizes.",
        ) {
            NFlex(size = 12.dp, wrap = true) {
                NDropdown(
                    options = cascadeOptions,
                    trigger = NTrigger.Click,
                    size = NSize.Small,
                    onSelect = { selected = it },
                ) { NButton { NText("Small") } }
                NDropdown(
                    options = cascadeOptions,
                    trigger = NTrigger.Click,
                    size = NSize.Medium,
                    onSelect = { selected = it },
                ) { NButton { NText("Medium") } }
                NDropdown(
                    options = cascadeOptions,
                    trigger = NTrigger.Click,
                    size = NSize.Large,
                    onSelect = { selected = it },
                ) { NButton { NText("Large") } }
                NDropdown(
                    options = cascadeOptions,
                    trigger = NTrigger.Click,
                    size = NSize.Tiny,
                    onSelect = { selected = it },
                ) { NButton { NText("Tiny") } }
            }
        }

        DemoSection(
            title = "Manual Position",
            description = "Long-press anywhere in the area to open the menu at that position " +
                "with `offset`.",
        ) {
            var showManual by remember { mutableStateOf(false) }
            var position by remember { mutableStateOf(IntOffset.Zero) }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(theme.borderRadius))
                        .background(Color(0x3300A000))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = { point ->
                                    position = IntOffset(
                                        point.x.roundToInt(),
                                        point.y.roundToInt(),
                                    )
                                    showManual = true
                                },
                            )
                        },
                ) {
                    NDropdown(
                        options = cascadeOptions,
                        trigger = NTrigger.Manual,
                        placement = NPlacement.BottomStart,
                        show = showManual,
                        onShowChange = { showManual = it },
                        offset = position,
                        onSelect = { selected = it },
                    ) {
                        Box(Modifier.size(1.dp)) {}
                    }
                    NText("Long-press to position the menu", Modifier.align(Alignment.Center))
                }
                NText("Selected: $selected")
            }
        }
    }
}
