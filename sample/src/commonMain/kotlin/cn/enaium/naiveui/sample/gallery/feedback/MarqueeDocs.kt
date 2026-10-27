package cn.enaium.naiveui.sample.gallery.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NMarquee
import cn.enaium.naiveui.components.NMarqueeDirection
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus

private const val marqueeText =
    "In 2020 Noel returned to the legendary Rockfield Studios in Wales for the first time since " +
        "the band recorded the album, looking back at his memories and reflecting on the album's legacy."

@Composable
fun MarqueeDocs() {
    DocsPage(
        title = "Marquee",
        description = "Marquee scrolls its content in an infinite loop. It supports horizontal and vertical " +
            "axes, forward and reverse directions, speed or fixed duration, and plain text list content.",
    ) {
        DemoSection(
            title = "Basic",
            description = "The content slot is duplicated automatically to make the loop seamless.",
            code = "NMarquee(speed = 80f) {\n    NText(\"Scrolling content\", maxLines = 1)\n}",
        ) {
            NMarquee(modifier = Modifier.fillMaxWidth(), speed = 80f) {
                NText(text = marqueeText, maxLines = 1)
            }
        }

        DemoSection(
            title = "Auto fill",
            description = "`autoFill = true` duplicates the content enough times to fill the whole blank space.",
        ) {
            NMarquee(
                modifier = Modifier.fillMaxWidth(),
                autoFill = true,
                speed = 60f,
            ) {
                Row(
                    Modifier.padding(end = 48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NTag(text = "Auto fill", type = NStatus.Info, size = NSize.Small)
                    NText("  fills the empty space  ", maxLines = 1)
                }
            }
        }

        DemoSection(
            title = "Image",
            description = "Any composable can be scrolled, including images or icons.",
        ) {
            val theme = LocalNTheme.current
            NMarquee(modifier = Modifier.fillMaxWidth(), autoFill = true, speed = 50f) {
                listOf(
                    Color(0xFFE57373),
                    Color(0xFF64B5F6),
                    Color(0xFF81C784),
                    Color(0xFFFFB74D),
                ).forEach { color ->
                    Box(
                        Modifier
                            .padding(end = 24.dp)
                            .size(80.dp)
                            .clip(RoundedCornerShape(theme.borderRadius))
                            .background(color),
                        contentAlignment = Alignment.Center,
                    ) {
                        NIcon(NIcons.Photo, size = 40.dp, color = Color.White)
                    }
                }
            }
        }

        DemoSection(
            title = "Direction",
            description = "`Normal` scrolls left, `Reverse` scrolls right.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NMarquee(modifier = Modifier.fillMaxWidth(), speed = 60f) {
                    NText(text = "Normal direction — $marqueeText", maxLines = 1)
                }
                NMarquee(
                    modifier = Modifier.fillMaxWidth(),
                    direction = NMarqueeDirection.Reverse,
                    speed = 60f,
                ) {
                    NText(text = "Reverse direction — $marqueeText", maxLines = 1)
                }
            }
        }

        DemoSection(
            title = "Speed & duration",
            description = "`speed` is measured in dp per second. A fixed `duration` in milliseconds overrides it.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NMarquee(modifier = Modifier.fillMaxWidth(), speed = 160f) {
                    NText(text = "Fast (160 dp/s) — $marqueeText", maxLines = 1)
                }
                NMarquee(modifier = Modifier.fillMaxWidth(), duration = 6000) {
                    NText(text = "Fixed 6s loop — $marqueeText", maxLines = 1)
                }
            }
        }

        DemoSection(
            title = "Items",
            description = "The list overload scrolls a row of plain text entries with configurable spacing.",
        ) {
            NMarquee(
                items = listOf(
                    "Naive UI",
                    "Compose Multiplatform",
                    "Kotlin",
                    "Design System",
                    "Open Source",
                ),
                itemSpacing = 40.dp,
                speed = 50f,
            )
        }

        DemoSection(
            title = "Vertical",
            description = "Set `vertical` and give the marquee a fixed height to create a vertical ticker.",
        ) {
            NMarquee(
                modifier = Modifier.height(110.dp).fillMaxWidth(),
                vertical = true,
                speed = 40f,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    (1..6).forEach { index ->
                        NText("Vertical notice $index — the marquee loops forever.", maxLines = 1)
                    }
                }
            }
        }

        DemoSection(
            title = "Custom content",
            description = "Any composable can scroll, including tags and icons.",
        ) {
            NMarquee(modifier = Modifier.fillMaxWidth(), speed = 70f) {
                Row(
                    Modifier.padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    listOf(
                        NStatus.Success,
                        NStatus.Info,
                        NStatus.Warning,
                        NStatus.Error,
                    ).forEach { status ->
                        Row(
                            Modifier
                                .width(180.dp)
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            NIcon(NIcons.Info, size = 16.dp)
                            NTag(text = status.name, type = status, size = NSize.Small)
                        }
                    }
                }
            }
        }
    }
}
