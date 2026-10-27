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
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NAlign
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus

@Composable
fun TagDocs() {
    DocsPage(
        title = "Tag",
        description = "Tags categorize content and can be checked, closed, colored or used inline with other text.",
    ) {
        DemoSection(
            title = "Type",
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
            title = "Borderless & strong",
            description = "Remove the border with `bordered = false`, or use `strong` for a solid tag.",
        ) {
            NSpace(size = 8.dp, wrap = true) {
                NTag(text = "Real Love", bordered = false)
                NTag(text = "Yes It Is", type = NStatus.Success, bordered = false)
                NTag(text = "I'm Down", type = NStatus.Warning, bordered = false)
                NTag(text = "Real Love", strong = true)
                NTag(text = "Yes It Is", type = NStatus.Success, strong = true)
            }
        }

        DemoSection(
            title = "Closable",
            description = "Closable tags report the close action through `onClose`; this demo removes them from a list.",
        ) {
            var tags by remember {
                mutableStateOf(listOf("Real Love", "Yes It Is", "I'm Down", "Yesterday"))
            }
            ColumnTags(
                tags = tags,
                onClose = { tag -> tags = tags - tag },
                trailing = {
                    if (tags.isEmpty()) {
                        NButton(size = NSize.Tiny, onClick = {
                            tags = listOf("Real Love", "Yes It Is", "I'm Down", "Yesterday")
                        }) {
                            NText("Reset")
                        }
                    }
                },
            )
        }

        DemoSection(
            title = "Checkable",
            description = "Checked tags are filled with their type color. Checked and unchecked states are controlled.",
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
            title = "Shape & size",
            description = "Round tags look like capsules. Tags support tiny, small, medium and large sizes.",
        ) {
            NSpace(size = 8.dp, align = NAlign.Center, wrap = true) {
                NTag(text = "Tiny", size = NSize.Tiny)
                NTag(text = "Small", size = NSize.Small, type = NStatus.Success)
                NTag(text = "Medium", size = NSize.Medium, type = NStatus.Warning)
                NTag(text = "Large", size = NSize.Large, type = NStatus.Error)
                NTag(text = "Round", round = true, closable = true)
                NTag(text = "Round", round = true, type = NStatus.Success, closable = true)
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
    }
}

@Composable
private fun ColumnTags(
    tags: List<String>,
    onClose: (String) -> Unit,
    trailing: @Composable () -> Unit = {},
) {
    NSpace(size = 8.dp, wrap = true) {
        tags.forEach { tag ->
            NTag(text = tag, closable = true, onClose = { onClose(tag) })
        }
        trailing()
    }
}
