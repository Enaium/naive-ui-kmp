package cn.enaium.naiveui.sample.gallery.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NAlign
import cn.enaium.naiveui.components.NJustify
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

@Composable
fun SpaceDocs() {
    DocsPage(
        title = "Space",
        description = "Space puts items in a row or column with a consistent gap and optional wrapping and alignment.",
    ) {
        DemoSection(
            title = "Basic",
            description = "A horizontal space with a default gap of 8dp.",
        ) {
            NSpace {
                NButton { NText("Oops!") }
                NButton { NText("Oops!") }
                NButton { NText("Oops!") }
                NButton { NText("Long! Long! Cross the line!") }
            }
        }

        DemoSection(
            title = "Vertical",
            description = "Set `vertical = true` to stack items in a column.",
        ) {
            NSpace(vertical = true) {
                NButton { NText("Oops!") }
                NButton { NText("Oops!") }
                NButton { NText("Oops!") }
            }
        }

        DemoSection(
            title = "Size",
            description = "Control the gap with `size`, and use `verticalSize` for a separate row gap when wrapping.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NSpace(size = 4.dp) {
                    NButton(size = NSize.Small) { NText("4dp") }
                    NButton(size = NSize.Small) { NText("4dp") }
                }
                NSpace(size = 24.dp) {
                    NButton(size = NSize.Small) { NText("24dp") }
                    NButton(size = NSize.Small) { NText("24dp") }
                }
            }
        }

        DemoSection(
            title = "Wrap",
            description = "With `wrap = true` items flow onto the next line when they run out of room.",
        ) {
            NSpace(size = 8.dp, verticalSize = 12.dp, wrap = true, modifier = Modifier.fillMaxWidth()) {
                repeat(12) { index ->
                    NButton { NText("Item ${index + 1}") }
                }
            }
        }

        DemoSection(
            title = "Align & justify",
            description = "`justify` distributes items on the main axis, `align` controls the cross axis.",
        ) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NSpace(justify = NJustify.Center, modifier = Modifier.fillMaxWidth()) {
                    repeat(3) { NButton { NText("Center") } }
                }
                NSpace(justify = NJustify.End, modifier = Modifier.fillMaxWidth()) {
                    repeat(3) { NButton { NText("End") } }
                }
                NSpace(justify = NJustify.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    repeat(3) { NButton { NText("Between") } }
                }
                NSpace(
                    justify = NJustify.SpaceAround,
                    size = 16.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    repeat(3) { NButton { NText("Around") } }
                }
                NSpace(align = NAlign.Center, size = 12.dp) {
                    NButton(size = NSize.Tiny) { NText("Tiny") }
                    NButton(size = NSize.Large) { NText("Large") }
                }
            }
        }

        DemoSection(
            title = "Inline",
            description = "A space can sit inline with surrounding text instead of starting a new block.",
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                NText("Tags:")
                NSpace(size = 6.dp, wrap = true) {
                    NButton(size = NSize.Small) { NText("Alpha") }
                    NButton(size = NSize.Small) { NText("Beta") }
                    NButton(size = NSize.Small) { NText("Gamma") }
                }
            }
        }
    }
}
