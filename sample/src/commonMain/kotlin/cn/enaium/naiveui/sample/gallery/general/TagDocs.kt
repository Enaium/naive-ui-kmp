package cn.enaium.naiveui.sample.gallery.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NAlign
import cn.enaium.naiveui.components.NAvatar
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus

private const val TAG_AVATAR_URL = "https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg"

@Composable
fun TagDocs() {
    DocsPage(
        title = "Tag",
        description = "Tags categorize content and can be checked, closed, colored or used inline with other text.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Tags come in default, success, warning, error and info types.",
        ) {
            NSpace(size = 8.dp, wrap = true) {
                NTag(text = "Real Love")
                NTag(text = "Yes It Is", type = NStatus.Success)
                NTag(text = "I'm Down", type = NStatus.Warning)
                NTag(text = "Yesterday", type = NStatus.Error)
                NTag(text = "I'm Looking Through You", type = NStatus.Info)
            }
        }

        DemoSection(
            title = "Bordered",
            description = "Remove the border with `bordered = false`, or use `strong` for a solid tag.",
        ) {
            NSpace(size = 8.dp, wrap = true) {
                NTag(text = "Real Love", bordered = false)
                NTag(text = "Yes It Is", type = NStatus.Success, bordered = false)
                NTag(text = "I'm Down", type = NStatus.Warning, bordered = false)
                NTag(text = "Yesterday", type = NStatus.Error, bordered = false)
                NTag(text = "I'm Looking Through You", type = NStatus.Info, bordered = false)
                NTag(text = "Strong", strong = true)
                NTag(text = "Strong success", type = NStatus.Success, strong = true)
            }
        }

        DemoSection(
            title = "Avatar",
            description = "A tag can carry an avatar through its content slot, inline before the label.",
        ) {
            NSpace(size = 8.dp, wrap = true) {
                NTag {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        NAvatar(size = 18.dp, src = TAG_AVATAR_URL, text = "A")
                        NText("Anyway.FM")
                    }
                }
                NTag(round = true, bordered = false) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        NAvatar(size = 18.dp, src = TAG_AVATAR_URL, text = "A", round = true)
                        NText("Anyway.FM")
                    }
                }
            }
        }

        DemoSection(
            title = "Checkable",
            description = "Checked tags are filled with their type color. The tags below share one controlled value.",
        ) {
            var checked by remember { mutableStateOf(false) }
            NSpace(size = 8.dp, align = NAlign.Center, wrap = true) {
                NTag(
                    text = "Real Love",
                    checkable = true,
                    checked = checked,
                    onCheckedChange = { checked = it },
                )
                NTag(
                    text = "Yes It Is",
                    type = NStatus.Success,
                    checkable = true,
                    checked = checked,
                    onCheckedChange = { checked = it },
                )
                NTag(
                    text = "I'm Down",
                    type = NStatus.Warning,
                    checkable = true,
                    checked = checked,
                    onCheckedChange = { checked = it },
                )
            }
        }

        DemoSection(
            title = "Closable",
            description = "Closable tags report the close action through `onClose`; this demo removes them from a list.",
        ) {
            var tags by remember {
                mutableStateOf(listOf("Real Love", "Yes It Is", "I'm Down", "Yesterday"))
            }
            NSpace(size = 8.dp, wrap = true) {
                tags.forEach { tag ->
                    NTag(text = tag, closable = true, onClose = { tags = tags - tag })
                }
                if (tags.isEmpty()) {
                    NButton(size = NSize.Tiny, onClick = {
                        tags = listOf("Real Love", "Yes It Is", "I'm Down", "Yesterday")
                    }) {
                        NText("Reset")
                    }
                }
            }
        }

        DemoSection(
            title = "Color",
            description = "Use `color` and `textColor` to match your own palette.",
        ) {
            NSpace(size = 8.dp, wrap = true) {
                NTag(
                    text = "Farewell to the night, waiting for dawn",
                    color = Color(0xFFBBBBBB),
                    textColor = Color(0xFF555555),
                )
                NTag(text = "Custom color", color = Color(0xFF8A2BE2), textColor = Color.White)
            }
        }

        DemoSection(
            title = "Disabled",
            description = "Disabled tags ignore clicks and are rendered with reduced opacity.",
        ) {
            var disabled by remember { mutableStateOf(true) }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NTag(text = "Real Love", closable = true, disabled = disabled)
                NTag(text = "Yes It Is", type = NStatus.Success, closable = true, disabled = disabled)
                NTag(text = "Yesterday", type = NStatus.Error, checkable = true, disabled = disabled)
                NButton(onClick = { disabled = !disabled }) { NText("Toggle disabled") }
            }
        }

        DemoSection(
            title = "Icon",
            description = "The content slot lets a tag carry an icon alongside its text.",
        ) {
            NSpace(size = 8.dp, wrap = true) {
                NTag(type = NStatus.Success) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        NIcon(NIcons.Checkmark, size = 14.dp, color = Color(0xFF18A058))
                        NText("Checked")
                    }
                }
                NTag(type = NStatus.Success, round = true, bordered = false) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        NIcon(NIcons.Checkmark, size = 14.dp, color = Color(0xFF18A058))
                        NText("Checked")
                    }
                }
            }
        }

        DemoSection(
            title = "Shape",
            description = "Round tags look like capsules.",
        ) {
            NSpace(size = 8.dp, align = NAlign.Center, wrap = true) {
                NTag(text = "Real Love", size = NSize.Small, round = true, closable = true)
                NTag(text = "Yes It Is", type = NStatus.Success, size = NSize.Large, round = true, closable = true)
                NTag(text = "I'm Down", type = NStatus.Warning, round = true, closable = true)
            }
        }

        DemoSection(
            title = "Size",
            description = "Tags support tiny, small, medium and large sizes.",
        ) {
            NSpace(size = 8.dp, align = NAlign.Center, wrap = true) {
                NTag(text = "Real Love", size = NSize.Tiny, closable = true)
                NTag(text = "Yes It Is", type = NStatus.Success, size = NSize.Small, closable = true)
                NTag(text = "I'm Down", type = NStatus.Warning, size = NSize.Medium, closable = true)
                NTag(text = "Yesterday", type = NStatus.Error, size = NSize.Large, closable = true)
            }
        }
    }
}
