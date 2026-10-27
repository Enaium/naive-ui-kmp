package cn.enaium.naiveui.sample.gallery.utility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NCollapseTransition
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

@Composable
fun CollapseTransitionDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Collapse Transition",
        description = "Animates the appearance and disappearance of its content. The default axis is " +
            "vertical; set horizontal for width animations.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Toggle the button to expand or collapse the panel.",
            code = """
                var show by remember { mutableStateOf(true) }
                NButton(onClick = { show = !show }) { NText("Toggle") }
                NCollapseTransition(show) {
                    NText("Collapsible content")
                }
            """.trimIndent(),
        ) {
            var show by remember { mutableStateOf(true) }
            Column(Modifier.fillMaxWidth()) {
                NButton(onClick = { show = !show }) {
                    NText(if (show) "Collapse" else "Expand")
                }
                NCollapseTransition(show) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .clip(RoundedCornerShape(theme.borderRadius))
                            .background(theme.colors.action)
                            .padding(16.dp),
                    ) {
                        NText(
                            "This panel animates its height when show changes.",
                            color = theme.colors.text2,
                        )
                    }
                }
            }
        }

        DemoSection(
            title = "Horizontal",
            description = "Set horizontal to animate width instead of height.",
        ) {
            var show by remember { mutableStateOf(true) }
            Row(Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                NButton(onClick = { show = !show }) {
                    NText(if (show) "Hide" else "Show")
                }
                Box(Modifier.padding(start = 12.dp)) {
                    NCollapseTransition(show, horizontal = true) {
                        Box(
                            Modifier
                                .width(160.dp)
                                .clip(RoundedCornerShape(theme.borderRadius))
                                .background(theme.colors.primary.copy(alpha = 0.16f))
                                .padding(12.dp),
                        ) {
                            NText("Width", color = theme.colors.primary, strong = true)
                        }
                    }
                }
            }
        }
    }
}
