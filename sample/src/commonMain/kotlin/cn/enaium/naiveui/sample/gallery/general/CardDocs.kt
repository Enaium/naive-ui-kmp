package cn.enaium.naiveui.sample.gallery.general

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import cn.enaium.naiveui.components.NScrollbar
import cn.enaium.naiveui.components.NSkeleton
import cn.enaium.naiveui.components.NSkeletonType
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.components.NTabPane
import cn.enaium.naiveui.components.NTabs
import cn.enaium.naiveui.components.NTabsType
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
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
            description = "A basic card with a title and content.",
        ) {
            NCard(title = "Card", modifier = Modifier.width(300.dp)) {
                NText("Card Content")
            }
        }

        DemoSection(
            title = "Border",
            description = "Cards can be used without a border.",
        ) {
            NCard(title = "Borderless Card", bordered = false, modifier = Modifier.width(300.dp)) {
                NText("Card Content")
            }
        }

        DemoSection(
            title = "Closable",
            description = "Closable cards report the close action; the card can be restored from the button.",
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
            title = "Content scrollable",
            description = "Give the body a bounded height and scroll it while the header and footer stay fixed.",
        ) {
            NCard(
                title = "Task List",
                footer = {
                    NText("Footer actions stay fixed")
                },
                modifier = Modifier.width(340.dp),
            ) {
                NScrollbar(Modifier.height(140.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(20) { index ->
                            NText("Task ${index + 1}: only the content area scrolls.")
                        }
                    }
                }
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
            title = "Custom style",
            description = "Cards can host custom content such as tabs. `NTabs` supplies its own header and pane padding.",
        ) {
            val panes = listOf(
                NTabPane("PARKLIFE", "PARKLIFE") { NText("PARKLIFE pane") },
                NTabPane("ROCKLIFE", "ROCKLIFE") { NText("ROCKLIFE pane") },
            )
            NCard(modifier = Modifier.width(360.dp)) {
                NTabs(type = NTabsType.Line, size = NSize.Large, panes = panes)
            }
        }

        DemoSection(
            title = "Embedded",
            description = "Embedded cards use a darker background so they stand out against the page.",
        ) {
            NCard(
                title = "Embedded Card",
                embedded = true,
                bordered = false,
                modifier = Modifier.width(300.dp),
            ) {
                NText("Tonight I'm a rock 'n' roll star")
                NText("Tonight I'm a rock 'n' roll star")
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
            title = "Loading",
            description = "Use skeletons to simulate a loading card. The switch-like button toggles the state.",
        ) {
            var loading by remember { mutableStateOf(true) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NButton(onClick = { loading = !loading }) {
                    NText(if (loading) "Stop loading" else "Start loading")
                }
                NCard(modifier = Modifier.width(340.dp)) {
                    if (loading) {
                        NSkeleton(type = NSkeletonType.Text, repeat = 5)
                    } else {
                        NText("Lorem ipsum dolor sit amet,")
                        NText("consectetur adipiscing elit,")
                        NText("sed do eiusmod tempor incididunt")
                        NText("ut labore et dolore magna aliqua.")
                        NText("Ut enim ad minim veniam.")
                    }
                }
            }
        }

        DemoSection(
            title = "No title",
            description = "Nobody said a card must have a title.",
        ) {
            NCard(modifier = Modifier.width(300.dp)) {
                NText("No title")
            }
        }

        DemoSection(
            title = "Segment",
            description = "`segmented` separates the header and body with a divider.",
        ) {
            NCard(
                title = "Card Segmented Demo",
                segmented = true,
                modifier = Modifier.width(320.dp),
            ) {
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
            title = "Slots",
            description = "The header extra, content and footer slots compose a complete card.",
        ) {
            NCard(
                title = "Card Slots Demo",
                modifier = Modifier.width(360.dp),
                headerExtra = {
                    NTag(text = "#header-extra", size = NSize.Small, type = NStatus.Success)
                },
                footer = {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        NText("#footer")
                        NButton(text = true, type = NButtonType.Primary) { NText("#action") }
                    }
                },
            ) {
                NText("Card Content")
            }
        }
    }
}
