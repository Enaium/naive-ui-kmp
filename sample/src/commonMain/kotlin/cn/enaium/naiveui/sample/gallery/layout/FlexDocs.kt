package cn.enaium.naiveui.sample.gallery.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NAlign
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NFlex
import cn.enaium.naiveui.components.NJustify
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage

@Composable
fun FlexDocs() {
    DocsPage(
        title = "Flex",
        description = "Flex is a lower-level layout container built on the flexbox model. Prefer it over Space when you need gaps and alignment.",
    ) {
        DemoSection(
            title = "Basic",
            description = "A flex container lays its children out in a row.",
        ) {
            NFlex(size = 8.dp) {
                NButton { NText("Oops!") }
                NButton { NText("Oops!") }
                NButton { NText("Oops!") }
                NButton { NText("Long! Long! Cross the line!") }
            }
        }

        DemoSection(
            title = "Vertical",
            description = "Set `vertical = true` to lay children out in a column.",
        ) {
            NFlex(vertical = true, size = 8.dp) {
                NButton { NText("Oops!") }
                NButton { NText("Oops!") }
                NButton { NText("Oops!") }
            }
        }

        DemoSection(
            title = "Center",
            description = "Center children on the main axis with `justify = NJustify.Center`.",
        ) {
            NFlex(justify = NJustify.Center, modifier = Modifier.fillMaxWidth()) {
                repeat(3) { NButton { NText("Oops!") } }
            }
        }

        DemoSection(
            title = "From end",
            description = "Push children to the end of the row with `justify = NJustify.End`.",
        ) {
            NFlex(justify = NJustify.End, modifier = Modifier.fillMaxWidth()) {
                repeat(3) { NButton { NText("Oops!") } }
            }
        }

        DemoSection(
            title = "Space between",
            description = "`NJustify.SpaceBetween` puts the first and last child at the edges.",
        ) {
            NFlex(justify = NJustify.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                repeat(3) { NButton { NText("Oops!") } }
            }
        }

        DemoSection(
            title = "Space around",
            description = "`NJustify.SpaceAround` adds equal space around every child.",
        ) {
            NFlex(justify = NJustify.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                repeat(3) { NButton { NText("Oops!") } }
            }
        }

        DemoSection(
            title = "Align",
            description = "`align` positions children on the cross axis.",
        ) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NFlex(align = NAlign.Start, size = 8.dp, modifier = Modifier.height(60.dp)) {
                    NButton { NText("Start") }
                    NButton { NText("Start") }
                }
                NFlex(align = NAlign.Center, size = 8.dp, modifier = Modifier.height(60.dp)) {
                    NButton { NText("Center") }
                    NButton { NText("Center") }
                }
                NFlex(align = NAlign.End, size = 8.dp, modifier = Modifier.height(60.dp)) {
                    NButton { NText("End") }
                    NButton { NText("End") }
                }
            }
        }

        DemoSection(
            title = "Wrap",
            description = "With `wrap = true` children flow onto new lines, spaced by `size`.",
        ) {
            NFlex(size = 8.dp, wrap = true, modifier = Modifier.fillMaxWidth()) {
                repeat(12) { index ->
                    NButton { NText("Item ${index + 1}") }
                }
            }
        }

        DemoSection(
            title = "Gap",
            description = "`size` is the gap between children on both axes when wrapping.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NFlex(size = 4.dp) {
                    repeat(4) { NButton { NText("4dp") } }
                }
                NFlex(size = 20.dp) {
                    repeat(4) { NButton { NText("20dp") } }
                }
            }
        }

        DemoSection(
            title = "Inline",
            description = "Flex containers can be nested inline with text.",
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                NText("Inline: ")
                NFlex(size = 6.dp) {
                    NButton { NText("Alpha") }
                    NButton { NText("Beta") }
                    NButton { NText("Gamma") }
                }
            }
        }
    }
}
