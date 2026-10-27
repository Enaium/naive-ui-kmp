package cn.enaium.naiveui.sample.gallery.utility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NScrollbar
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

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
            title = "Horizontal",
            description = "Set horizontal to scroll a wide row.",
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
            title = "Always show",
            description = "alwaysShow keeps the thumb visible so users can see there is more content.",
        ) {
            NScrollbar(
                modifier = Modifier.fillMaxWidth().height(140.dp),
                alwaysShow = true,
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    repeat(12) { index ->
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(34.dp)
                                .clip(RoundedCornerShape(theme.borderRadiusSmall))
                                .background(if (index % 2 == 0) theme.colors.action else theme.colors.card)
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            NText("Row ${index + 1}", color = theme.colors.text2)
                        }
                    }
                }
            }
        }
    }
}
