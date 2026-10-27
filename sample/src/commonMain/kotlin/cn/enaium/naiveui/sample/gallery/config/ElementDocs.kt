package cn.enaium.naiveui.sample.gallery.config

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NElement
import cn.enaium.naiveui.components.NCard
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

@Composable
fun ElementDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Element",
        description = "NElement is a generic styled container. In naive-ui it exposes theme variables; " +
            "here it hosts content and can override the inherited content color for its descendants.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Any content can be placed inside an element; layout tokens still come from the current theme.",
            code = """
                NCard {
                    NElement(Modifier.padding(16.dp)) {
                        NText("I am inside an NElement.")
                    }
                }
            """.trimIndent(),
        ) {
            NCard {
                NElement(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(theme.borderRadius))
                        .background(theme.colors.action)
                        .padding(16.dp),
                ) {
                    NText("I am inside an NElement.")
                }
            }
        }

        DemoSection(
            title = "Content color",
            description = "contentColor overrides the color inherited by text and icons below the element.",
        ) {
            NCard {
                Column(Modifier.fillMaxWidth()) {
                    NElement(Modifier.padding(vertical = 6.dp)) {
                        NText("Inherited theme text color.")
                    }
                    NElement(
                        modifier = Modifier.padding(vertical = 6.dp),
                        contentColor = theme.colors.primary,
                    ) {
                        NText("Primary content color from NElement.")
                    }
                }
            }
        }

        DemoSection(
            title = "Tag parity",
            description = "tag and abstract are accepted for parity with the Vue API but are ignored: Compose " +
                "has no host element to render.",
        ) {
            NCard {
                NElement(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    tag = "span",
                    abstract = true,
                ) {
                    NText("Rendered as a Compose Box, not a span.")
                }
            }
        }
    }
}
