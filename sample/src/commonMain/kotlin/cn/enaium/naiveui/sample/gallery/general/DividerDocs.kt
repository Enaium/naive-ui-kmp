package cn.enaium.naiveui.sample.gallery.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NDivider
import cn.enaium.naiveui.components.NDividerTitlePlacement
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage

@Composable
fun DividerDocs() {
    DocsPage(
        title = "Divider",
        description = "A divider separates content vertically or horizontally, and can carry a title.",
    ) {
        DemoSection(
            title = "Basic",
            description = "A horizontal divider between blocks of text.",
        ) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NText("Oops")
                NDivider()
                NText("Oops")
            }
        }

        DemoSection(
            title = "Dashed",
            description = "Pass `dashed` to render a dashed line.",
        ) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NText("A dashed line follows")
                NDivider(dashed = true)
                NText("and the content continues")
            }
        }

        DemoSection(
            title = "Content",
            description = "A divider can hold a title aligned to the left, center or right, and can be dashed.",
        ) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NText("Oops")
                NDivider(title = "Left", titlePlacement = NDividerTitlePlacement.Left)
                NText("Oops")
                NDivider(title = "Center", titlePlacement = NDividerTitlePlacement.Center)
                NText("Oops")
                NDivider(title = "Right", titlePlacement = NDividerTitlePlacement.Right)
                NText("Oops")
                NDivider(title = "Dashed", dashed = true)
                NText("Oops")
            }
        }

        DemoSection(
            title = "Vertical",
            description = "Vertical dividers separate inline content such as links.",
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                NText("It is")
                NDivider(vertical = true)
                NText("not clear")
                NDivider(vertical = true)
                NText("to see, emmm...")
            }
        }
    }
}
