package cn.enaium.naiveui.sample.gallery.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NFloatButton
import cn.enaium.naiveui.components.NFloatButtonGroup
import cn.enaium.naiveui.components.NFloatButtonShape
import cn.enaium.naiveui.components.NFloatButtonType
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NFlex
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.icons.NIcon
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
