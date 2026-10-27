package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NDescriptions
import cn.enaium.naiveui.components.NDescriptionsItem
import cn.enaium.naiveui.components.NDescriptionsLabelPlacement
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

@Composable
fun DescriptionsDocs() {
    DocsPage(
        title = "Descriptions",
        description = "Descriptions displays label and content pairs in a table-like layout with configurable columns, spans and borders.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Labels are placed above content by default. Items automatically flow into rows of three columns.",
            code = """
NDescriptions(title = "Description") {
    NDescriptionsItem(label = "Breakfast") { NText("Apple") }
    NDescriptionsItem(label = "Lunch") { NText("Apple") }
    NDescriptionsItem(label = "Supper", span = 2) { NText("Two apples") }
}
""",
        ) {
            NDescriptions(title = "Description") {
                NDescriptionsItem(labelContent = { NText("Breakfast") }) { NText("Apple") }
                NDescriptionsItem(label = "Brunch") { NText("Apple") }
                NDescriptionsItem(label = "Lunch") { NText("Apple") }
                NDescriptionsItem(label = "Supper", span = 2) { NText("Two\nApples") }
                NDescriptionsItem(label = "Midnight Snack") { NText("Apple") }
            }
        }

        DemoSection(
            title = "Bordered",
            description = "Bordered draws a grid with a header background. It works with both top and left label placement.",
            code = """
NDescriptions(bordered = true) { ... }
NDescriptions(bordered = true, labelPlacement = NDescriptionsLabelPlacement.Left) { ... }
""",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                NDescriptions(bordered = true) {
                    NDescriptionsItem(label = "Breakfast") { NText("Apple") }
                    NDescriptionsItem(label = "Lunch") { NText("Apple") }
                    NDescriptionsItem(label = "Supper") { NText("Apple") }
                    NDescriptionsItem(label = "Why Long") { NText("Why\nLong\nLong") }
                    NDescriptionsItem(label = "Why Long") { NText("Why\nLong\nLong") }
                }
                NDescriptions(
                    bordered = true,
                    labelPlacement = NDescriptionsLabelPlacement.Left,
                ) {
                    NDescriptionsItem(label = "Breakfast") { NText("Apple") }
                    NDescriptionsItem(label = "Lunch") { NText("Apple") }
                    NDescriptionsItem(label = "Supper") { NText("Apple") }
                }
            }
        }

        DemoSection(
            title = "Columns and span",
            description = "Change column to control items per row. An item can span multiple columns, including the full row.",
            code = """
NDescriptions(column = 4, bordered = true) {
    NDescriptionsItem(label = "One") { NText("1") }
    NDescriptionsItem(label = "Two", span = 2) { NText("2") }
    NDescriptionsItem(label = "Three") { NText("3") }
}
""",
        ) {
            NDescriptions(column = 4, bordered = true) {
                NDescriptionsItem(label = "Breakfast") { NText("Apple") }
                NDescriptionsItem(label = "Brunch") { NText("Apple") }
                NDescriptionsItem(label = "Lunch") { NText("Apple") }
                NDescriptionsItem(label = "Supper") { NText("Apple") }
                NDescriptionsItem(label = "Dinner") { NText("Apple") }
                NDescriptionsItem(label = "Midnight Snack") { NText("Apple") }
                NDescriptionsItem(label = "Spans two", span = 2) { NText("Apple") }
                NDescriptionsItem(label = "Spans three", span = 3) { NText("Apple") }
            }
        }

        DemoSection(
            title = "Label placement",
            description = "With left placement the label sits inline with content; separator customizes the divider between them.",
            code = """
NDescriptions(
    labelPlacement = NDescriptionsLabelPlacement.Left,
    separator = ":",
    title = "Description",
) { ... }
""",
        ) {
            NDescriptions(
                labelPlacement = NDescriptionsLabelPlacement.Left,
                title = "Description",
            ) {
                NDescriptionsItem(labelContent = { NText("Breakfast") }) { NText("Apple") }
                NDescriptionsItem(label = "Brunch") { NText("Apple") }
                NDescriptionsItem(label = "Lunch") { NText("Apple") }
                NDescriptionsItem(label = "Supper") { NText("Apple") }
                NDescriptionsItem(label = "Midnight Snack") { NText("Apple") }
            }
        }

        DemoSection(
            title = "Size",
            description = "Descriptions supports small, medium and large sizes, affecting font size and cell padding.",
            code = """
NDescriptions(size = NSize.Small, bordered = true) { ... }
NDescriptions(size = NSize.Large, bordered = true) { ... }
""",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                NDescriptions(size = NSize.Small, bordered = true, title = "Small") {
                    NDescriptionsItem(label = "Breakfast") { NText("Apple") }
                    NDescriptionsItem(label = "Lunch") { NText("Apple") }
                    NDescriptionsItem(label = "Supper", span = 2) { NText("Two apples") }
                }
                NDescriptions(size = NSize.Medium, bordered = true, title = "Medium") {
                    NDescriptionsItem(label = "Breakfast") { NText("Apple") }
                    NDescriptionsItem(label = "Lunch") { NText("Apple") }
                    NDescriptionsItem(label = "Supper", span = 2) { NText("Two apples") }
                }
                NDescriptions(size = NSize.Large, bordered = true, title = "Large") {
                    NDescriptionsItem(label = "Breakfast") { NText("Apple") }
                    NDescriptionsItem(label = "Lunch") { NText("Apple") }
                    NDescriptionsItem(label = "Supper", span = 2) { NText("Two apples") }
                }
            }
        }
    }
}
