package cn.enaium.naiveui.sample.gallery.general

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NButtonType
import cn.enaium.naiveui.components.NCard
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus

@Composable
fun CardDocs() {
    DocsPage(
        title = "Card",
        description = "Just put something in it. Cards group related content with an optional header, footer and cover.",
    ) {
        DemoSection(
            title = "Basic",
            description = "A basic card with a title.",
        ) {
            NCard(title = "Card", modifier = Modifier.width(300.dp)) {
                NText("Card Content")
            }
        }

        DemoSection(
            title = "Title & header extra",
            description = "The header can carry a title and extra content aligned to the right.",
        ) {
            NCard(
                title = "Card",
                modifier = Modifier.width(320.dp),
                headerExtra = {
                    NTag(text = "New", type = NStatus.Success, size = NSize.Small)
                },
            ) {
                NText("Card Content")
            }
        }

        DemoSection(
            title = "Closable",
            description = "You may need a closable card when using it in a modal.",
        ) {
            var visible by remember { mutableStateOf(true) }
            if (visible) {
                NCard(
                    title = "Card",
                    closable = true,
                    onClose = { visible = false },
                    modifier = Modifier.width(300.dp),
                ) {
                    NText("Card Content")
                }
            } else {
                NButton(onClick = { visible = true }) { NText("Show card") }
            }
        }

        DemoSection(
            title = "Cover",
            description = "Cards can display a cover area above the content. This demo uses a colored box.",
        ) {
            NCard(title = "Card with Cover", modifier = Modifier.width(300.dp)) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color(0xFF8A2BE2).copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center,
                ) {
                    NText("Cover", color = Color(0xFF8A2BE2))
                }
                NText("Card Content")
            }
        }

        DemoSection(
            title = "Borderless",
            description = "Cards can be used without a border.",
        ) {
            NCard(title = "Borderless Card", bordered = false, modifier = Modifier.width(300.dp)) {
                NText("Card Content")
            }
        }

        DemoSection(
            title = "Hoverable",
            description = "Hoverable cards elevate their shadow on pointer hover.",
        ) {
            NCard(title = "Card", hoverable = true, modifier = Modifier.width(300.dp)) {
                NText("Card Content")
            }
        }

        DemoSection(
            title = "Size",
            description = "Cards support `small`, `medium` and `large` sizes; padding and title size follow the size.",
        ) {
            NSpace(vertical = true, size = 12.dp) {
                NCard(title = "Small Card", size = NSize.Small, modifier = Modifier.fillMaxWidth()) {
                    NText("Card Content")
                }
                NCard(title = "Medium Card", size = NSize.Medium, modifier = Modifier.fillMaxWidth()) {
                    NText("Card Content")
                }
                NCard(title = "Large Card", size = NSize.Large, modifier = Modifier.fillMaxWidth()) {
                    NText("Card Content")
                }
            }
        }

        DemoSection(
            title = "Segmented & embedded",
            description = "`segmented` separates the header and body; `embedded` uses a slightly darker background.",
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                NCard(title = "Card Segmented Demo", segmented = true, modifier = Modifier.width(320.dp)) {
                    NText("Card Content")
                }
                NCard(
                    title = "Embedded Card",
                    embedded = true,
                    bordered = false,
                    modifier = Modifier.width(320.dp),
                ) {
                    NText("Tonight I'm a rock 'n' roll star")
                    NText("Tonight I'm a rock 'n' roll star")
                }
            }
        }

        DemoSection(
            title = "Footer",
            description = "A footer sits below a divider, typically holding actions.",
        ) {
            NCard(
                title = "Card with Footer",
                modifier = Modifier.width(360.dp),
                footer = {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        NButton(text = true) { NText("Cancel") }
                        NButton(type = NButtonType.Primary) {
                            NText("Confirm")
                        }
                    }
                },
            ) {
                NText("Card Content")
            }
        }

        DemoSection(
            title = "Content padding",
            description = "Content is padded by the card size. Use `segmented` and a full-width box to fill the body edge to edge.",
        ) {
            val theme = LocalNTheme.current
            NCard(
                title = "Padding",
                segmented = true,
                size = NSize.Large,
                modifier = Modifier.width(320.dp),
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(theme.colors.action),
                    contentAlignment = Alignment.Center,
                ) {
                    NText("Large padding")
                }
            }
        }
    }
}
