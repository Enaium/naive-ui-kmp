package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NEmpty
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

@Composable
fun EmptyDocs() {
    DocsPage(
        title = "Empty",
        description = "Placeholder for empty states, with optional icon, description and extra actions.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Provide a description and extra content for the default empty state.",
            code = "NEmpty(description = \"You can't find anything\") {\n    NButton(size = NSize.Small) { NText(\"Find Something New\") }\n}",
        ) {
            NEmpty(description = "You can't find anything") {
                NButton(size = NSize.Small) { NText("Find Something New") }
            }
        }
        DemoSection(
            title = "Icon",
            description = "Replace the default icon through the icon parameter.",
            code = "NEmpty(description = \"Custom your icon\", icon = NIcons.Search)",
        ) {
            NEmpty(
                description = "Custom your icon",
                icon = NIcons.Search,
            ) {
                NButton(size = NSize.Small) { NText("Find Something New") }
            }
        }
        DemoSection(
            title = "Size",
            description = "Empty supports tiny, small, medium and large sizes.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                NSize.entries.forEach { size ->
                    NEmpty(size = size, description = "Size ${size.label}")
                }
            }
        }
        DemoSection(
            title = "Locale",
            description = "Without an explicit description the localized empty description is used.",
            code = "NEmpty()",
        ) {
            NEmpty()
        }
        DemoSection(
            title = "Without icon",
            description = "The icon can be hidden while keeping the description.",
        ) {
            NEmpty(showIcon = false, description = "There is no icon here")
        }
    }
}
