package cn.enaium.naiveui.sample.gallery.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NBadge
import cn.enaium.naiveui.components.NFlex
import cn.enaium.naiveui.components.NFloatButton
import cn.enaium.naiveui.components.NFloatButtonGroup
import cn.enaium.naiveui.components.NFloatButtonMenuTrigger
import cn.enaium.naiveui.components.NFloatButtonShape
import cn.enaium.naiveui.components.NFloatButtonType
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NTooltip
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NStatus
import androidx.compose.ui.graphics.Color

@Composable
fun FloatButtonDocs() {
    DocsPage(
        title = "Float Button",
        description = "A floating action button and a vertical group. Positioning is left " +
            "to the caller, so it can be embedded anywhere.",
    ) {
        var clicks by remember { mutableStateOf(0) }

        DemoSection(
            title = "Basic",
            description = "Circle and square shapes, default and primary types.",
            code = """
NFloatButton(onClick = { ... }) {
    NIcon(NIcons.Add)
}
""".trimIndent(),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NFlex(size = 12.dp, wrap = true) {
                    NFloatButton(onClick = { clicks += 1 }) { NIcon(NIcons.Add) }
                    NFloatButton(
                        shape = NFloatButtonShape.Square,
                        onClick = { clicks += 1 },
                    ) { NIcon(NIcons.Download) }
                    NFloatButton(
                        type = NFloatButtonType.Primary,
                        onClick = { clicks += 1 },
                    ) { NIcon(NIcons.Search, color = Color.White) }
                    NFloatButton(
                        type = NFloatButtonType.Primary,
                        shape = NFloatButtonShape.Square,
                        onClick = { clicks += 1 },
                    ) { NIcon(NIcons.File, color = Color.White) }
                }
                NText("Clicks: $clicks", size = 13.sp)
            }
        }

        DemoSection(
            title = "Description",
            description = "A description is rendered below the icon and increases the " +
                "minimum height.",
        ) {
            NFlex(size = 12.dp, wrap = true) {
                NFloatButton(shape = NFloatButtonShape.Square, description = "Docs") {
                    NIcon(NIcons.File)
                }
                NFloatButton(shape = NFloatButtonShape.Square, description = "Search") { }
                NFloatButton(shape = NFloatButtonShape.Square, description = "Long Docs") {
                    NIcon(NIcons.File)
                }
            }
        }

        DemoSection(
            title = "Badge",
            description = "Wrap the float button in `NBadge` so the marker sits on the outside edge.",
        ) {
            NFlex(size = 12.dp, wrap = true) {
                NBadge(value = 9, offset = DpOffset(6.dp, -8.dp)) {
                    NFloatButton { NIcon(NIcons.Add) }
                }
                NBadge(value = 100, max = 99, offset = DpOffset(6.dp, -8.dp)) {
                    NFloatButton { NIcon(NIcons.Add) }
                }
                NBadge(dot = true, offset = DpOffset(4.dp, -4.dp)) {
                    NFloatButton { NIcon(NIcons.Add) }
                }
                NBadge(value = "New", offset = DpOffset(6.dp, -6.dp)) {
                    NFloatButton { NIcon(NIcons.Add) }
                }
            }
        }

        DemoSection(
            title = "Show menu",
            description = "`menuTrigger` folds more float buttons into one; the menu " +
                "unfolds above the button.",
        ) {
            NFlex(size = 32.dp, wrap = true) {
                NFloatButton(
                    type = NFloatButtonType.Primary,
                    menuTrigger = NFloatButtonMenuTrigger.Hover,
                    menu = {
                        NFloatButton(shape = NFloatButtonShape.Square, type = NFloatButtonType.Primary) {
                            NIcon(NIcons.Add, color = Color.White)
                        }
                        NFloatButton { NIcon(NIcons.Search) }
                        NFloatButton { NIcon(NIcons.File) }
                    },
                ) {
                    NIcon(NIcons.MenuIcon, color = Color.White)
                }
                NFloatButton(
                    type = NFloatButtonType.Primary,
                    menuTrigger = NFloatButtonMenuTrigger.Click,
                    menu = {
                        NFloatButton(shape = NFloatButtonShape.Square, type = NFloatButtonType.Primary) {
                            NIcon(NIcons.Add, color = Color.White)
                        }
                        NFloatButton { NIcon(NIcons.Search) }
                        NFloatButton { NIcon(NIcons.File) }
                    },
                ) {
                    NIcon(NIcons.Add, color = Color.White)
                }
            }
        }

        DemoSection(
            title = "Tooltip",
            description = "Wrap the button in `NTooltip` for a hover hint.",
        ) {
            NFlex(size = 12.dp) {
                NTooltip(text = "Create", placement = NPlacement.Right) {
                    NFloatButton { NIcon(NIcons.Add) }
                }
                NTooltip(text = "Search", placement = NPlacement.Right) {
                    NFloatButton { NIcon(NIcons.Search) }
                }
            }
        }

        DemoSection(
            title = "Group",
            description = "Within a square group the buttons share one surface and shadow; " +
                "circular groups keep individual buttons.",
        ) {
            NFlex(size = 24.dp, wrap = true, align = cn.enaium.naiveui.components.NAlign.Start) {
                NFloatButtonGroup(shape = NFloatButtonShape.Square) {
                    NFloatButton(onClick = { clicks += 1 }) { NIcon(NIcons.Add) }
                    NFloatButton(onClick = { clicks += 1 }) { NIcon(NIcons.Search) }
                    NFloatButton(onClick = { clicks += 1 }) { NIcon(NIcons.Download) }
                }
                NFloatButtonGroup {
                    NFloatButton(onClick = { clicks += 1 }) { NIcon(NIcons.Add) }
                    NFloatButton(onClick = { clicks += 1 }) { NIcon(NIcons.Search) }
                    NFloatButton(onClick = { clicks += 1 }) { NIcon(NIcons.Attach) }
                }
            }
        }

        DemoSection(
            title = "Feedback",
            description = "Buttons report clicks through `onClick`.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NFloatButton(
                    type = NFloatButtonType.Primary,
                    onClick = { clicks += 1 },
                ) { NIcon(NIcons.Checkmark, color = Color.White) }
                NTag("Total clicks: $clicks", type = NStatus.Primary)
                NText("Use the buttons above and watch the counter.", size = 13.sp)
            }
        }
    }
}
