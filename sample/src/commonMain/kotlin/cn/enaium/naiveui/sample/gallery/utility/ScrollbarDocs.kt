package cn.enaium.naiveui.sample.gallery.utility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NScrollbar
import cn.enaium.naiveui.components.NScrollbarPlacement
import cn.enaium.naiveui.components.NScrollbarTrigger
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoRow
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NConfigProvider
import cn.enaium.naiveui.theme.NTheme

private val passage = "And as I sat there, brooding on the old unknown world, I thought of Gatsby's wonder " +
    "when he first picked out the green light at the end of Daisy's dock. He had come a long way to this " +
    "blue lawn and his dream must have seemed so close that he could hardly fail to grasp it. He did not " +
    "know that it was already behind him, somewhere back in that vast obscurity beyond the city, where " +
    "the dark fields of the republic rolled on under the night.\n\n" +
    "Gatsby believed in the green light, the orgastic future that year by year recedes before us. It " +
    "eluded us then, but that's no matter - tomorrow we will run faster, stretch out our arms farther. " +
    "And one fine morning -\n\n" +
    "So we beat on, boats against the current, borne back ceaselessly into the past."

@Composable
fun ScrollbarDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Scrollbar",
        description = "A scroll container with a naive-ui style overlay scrollbar. " +
            "Give it a bounded size with Modifier.height so the content can scroll.",
    ) {
        DemoSection(
            title = "Basic",
            description = "The scrollbar thumb appears while scrolling or hovering and can be dragged.",
            code = """
                NScrollbar(Modifier.fillMaxWidth().height(120.dp)) {
                    NText(passage)
                }
            """.trimIndent(),
        ) {
            NScrollbar(Modifier.fillMaxWidth().height(120.dp)) {
                NText(passage, color = theme.colors.text2)
            }
        }

        DemoSection(
            title = "Scroll horizontally",
            description = "Set horizontal to scroll a wide row; the x placement decides which edge hosts the thumb.",
        ) {
            NScrollbar(Modifier.fillMaxWidth(), horizontal = true) {
                Row(
                    Modifier.width(900.dp).padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    repeat(10) { index ->
                        Box(
                            Modifier
                                .width(120.dp)
                                .height(72.dp)
                                .clip(RoundedCornerShape(theme.borderRadius))
                                .background(theme.colors.primary.copy(alpha = 0.12f + (index % 3) * 0.04f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            NText("Item ${index + 1}", color = theme.colors.primary)
                        }
                    }
                }
            }
        }

        DemoSection(
            title = "Trigger",
            description = "trigger = None keeps the thumb visible; the default Hover trigger shows it " +
                "only while hovering or scrolling.",
        ) {
            DemoRow {
                NScrollbar(
                    modifier = Modifier.weight(1f).height(120.dp),
                    trigger = NScrollbarTrigger.None,
                ) {
                    NText(passage, color = theme.colors.text2)
                }
                NScrollbar(
                    modifier = Modifier.weight(1f).height(120.dp),
                    trigger = NScrollbarTrigger.Hover,
                ) {
                    NText(passage, color = theme.colors.text2)
                }
            }
        }

        DemoSection(
            title = "Trailing space",
            description = "Spacing after the last item is part of the scroll extent. contentPadding makes " +
                "that space explicit, matching how the browser margin case is handled by keeping the " +
                "content box in sync with the scroll range.",
        ) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NText("contentPadding bottom = 90dp", color = theme.colors.text3)
                NScrollbar(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentPadding = PaddingValues(bottom = 90.dp),
                ) {
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(4) { index ->
                            NText("Row ${index + 1}", color = theme.colors.text2)
                        }
                    }
                }
                NText("No trailing padding", color = theme.colors.text3)
                NScrollbar(Modifier.fillMaxWidth().height(120.dp)) {
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(4) { index ->
                            NText("Row ${index + 1}", color = theme.colors.text2)
                        }
                    }
                }
            }
        }

        DemoSection(
            title = "Custom scrollbar style",
            description = "color, colorHover and thickness customize the thumb without touching the theme.",
        ) {
            NScrollbar(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                color = theme.colors.primary.copy(alpha = 0.4f),
                colorHover = theme.colors.primary,
                thickness = 8.dp,
            ) {
                NText(passage, color = theme.colors.text2)
            }
        }

        DemoSection(
            title = "Theme",
            description = "Without explicit colors the scrollbar follows the theme provided by " +
                "NConfigProvider. Toggle the theme to see the default thumb color change.",
        ) {
            var dark by remember { mutableStateOf(false) }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DemoRow {
                    NButton(onClick = { dark = false }) { NText("Light") }
                    NButton(onClick = { dark = true }) { NText("Dark") }
                }
                NConfigProvider(theme = if (dark) NTheme.Dark else NTheme.Light) {
                    NScrollbar(Modifier.fillMaxWidth().height(140.dp)) {
                        NText(passage, color = LocalNTheme.current.colors.text2)
                    }
                }
            }
        }

        DemoSection(
            title = "Placement",
            description = "yPlacement and xPlacement move the scrollbar to another edge.",
        ) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DemoRow {
                    NScrollbar(
                        modifier = Modifier.weight(1f).height(120.dp),
                        yPlacement = NScrollbarPlacement.Left,
                        trigger = NScrollbarTrigger.None,
                    ) {
                        NText(passage, color = theme.colors.text2)
                    }
                    NScrollbar(
                        modifier = Modifier.weight(1f).height(120.dp),
                        yPlacement = NScrollbarPlacement.Right,
                        trigger = NScrollbarTrigger.None,
                    ) {
                        NText(passage, color = theme.colors.text2)
                    }
                }
                NScrollbar(
                    modifier = Modifier.fillMaxWidth(),
                    horizontal = true,
                    xPlacement = NScrollbarPlacement.Top,
                    trigger = NScrollbarTrigger.None,
                ) {
                    Row(
                        Modifier.width(900.dp).padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        repeat(10) { index ->
                            Box(
                                Modifier
                                    .width(120.dp)
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(theme.borderRadius))
                                    .background(theme.colors.success.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                NText("Top ${index + 1}", color = theme.colors.success)
                            }
                        }
                    }
                }
            }
        }
    }
}
