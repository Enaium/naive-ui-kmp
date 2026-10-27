package cn.enaium.naiveui.sample.gallery.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NAlign
import cn.enaium.naiveui.components.NFlex
import cn.enaium.naiveui.components.NJustify
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus

@Composable
fun FlexDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Flex",
        description = "Flex is the low-level layout primitive behind Space. It maps directly onto a row, " +
            "a column or a wrapping flow and supports the usual justify and align options.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Content flows horizontally and wraps only when wrap is enabled.",
            code = """
                NFlex {
                    NButton { NText("Oops!") }
                    NButton { NText("Oops!") }
                    NButton { NText("Oops!") }
                }
            """.trimIndent(),
        ) {
            NFlex {
                NButton { NText("Oops!") }
                NButton { NText("Oops!") }
                NButton { NText("Oops!") }
                NButton { NText("Long! Long! Cross the line!") }
            }
        }

        DemoSection(
            title = "Justify",
            description = "SpaceBetween pushes the first and last items to the edges.",
        ) {
            NFlex(Modifier.fillMaxWidth(), justify = NJustify.SpaceBetween) {
                NButton { NText("Start") }
                NButton { NText("Middle") }
                NButton { NText("End") }
            }
        }

        DemoSection(
            title = "Align",
            description = "Center aligns items of different heights on the cross axis.",
        ) {
            NFlex(
                Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(theme.borderRadius))
                    .background(theme.colors.action)
                    .padding(12.dp),
                align = NAlign.Center,
                size = 8.dp,
            ) {
                NButton(size = NSize.Small) { NText("Small") }
                NButton(size = NSize.Medium) { NText("Medium") }
                NButton(size = NSize.Large) { NText("Large") }
            }
        }

        DemoSection(
            title = "Wrap",
            description = "Wrapped flows move overflowing items onto the next line.",
        ) {
            NFlex(Modifier.fillMaxWidth(), wrap = true, size = 8.dp) {
                repeat(10) { index ->
                    NTag(
                        text = "Tag ${index + 1}",
                        type = if (index % 2 == 0) NStatus.Primary else NStatus.Default,
                    )
                }
            }
        }

        DemoSection(
            title = "Size",
            description = "size is the gap between items on both axes.",
        ) {
            NFlex(Modifier.fillMaxWidth(), size = 20.dp) {
                NButton { NText("First") }
                NButton { NText("Second") }
                NButton { NText("Third") }
            }
        }

        DemoSection(
            title = "Vertical",
            description = "Set vertical to stack items; size controls the gap.",
        ) {
            Column(Modifier.fillMaxWidth()) {
                NFlex(vertical = true, size = 10.dp) {
                    NButton { NText("First") }
                    NButton { NText("Second") }
                    NButton { NText("Third") }
                }
            }
        }
    }
}
