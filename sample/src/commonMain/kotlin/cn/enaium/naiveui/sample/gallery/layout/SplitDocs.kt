package cn.enaium.naiveui.sample.gallery.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NSplit
import cn.enaium.naiveui.components.NSplitDirection
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

@Composable
fun SplitDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Split",
        description = "Split divides an area into two resizable panes. Drag the divider to change the " +
            "fraction of the first pane; min and max clamp the result.",
    ) {
        DemoSection(
            title = "Basic",
            description = "A horizontal split with a fixed height.",
            code = """
                NSplit(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    pane1 = { Pane("Pane 1") },
                    pane2 = { Pane("Pane 2") },
                )
            """.trimIndent(),
        ) {
            NSplit(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                pane1 = { SplitPane("Pane 1", theme.colors.primary.copy(alpha = 0.14f)) },
                pane2 = { SplitPane("Pane 2", theme.colors.primary.copy(alpha = 0.24f)) },
            )
        }

        DemoSection(
            title = "Vertical",
            description = "Set direction to vertical to stack the panes and drag the divider up and down.",
        ) {
            NSplit(
                modifier = Modifier.fillMaxWidth().height(220.dp),
                direction = NSplitDirection.Vertical,
                pane1 = { SplitPane("Pane 1", theme.colors.primary.copy(alpha = 0.14f)) },
                pane2 = { SplitPane("Pane 2", theme.colors.primary.copy(alpha = 0.24f)) },
            )
        }

        DemoSection(
            title = "Min and max",
            description = "The first pane is clamped between 25% and 75% of the available size.",
            code = """
                NSplit(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    min = 0.25f,
                    max = 0.75f,
                    onDragEnd = { sizes -> println(sizes) },
                    pane1 = { Pane("Pane 1") },
                    pane2 = { Pane("Pane 2") },
                )
            """.trimIndent(),
        ) {
            NSplit(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                min = 0.25f,
                max = 0.75f,
                pane1 = { SplitPane("25% – 75%", theme.colors.primary.copy(alpha = 0.16f)) },
                pane2 = { SplitPane("Pane 2", theme.colors.primary.copy(alpha = 0.24f)) },
            )
        }

        DemoSection(
            title = "Controlled",
            description = "Pass sizes and update them from onSizesChange to keep the split in sync with your state.",
        ) {
            var sizes by remember { mutableStateOf(listOf(0.5f, 0.5f)) }
            Column(Modifier.fillMaxWidth()) {
                NText(
                    "First pane: ${(sizes.first() * 100).toInt()}%",
                    color = theme.colors.text3,
                )
                NSplit(
                    modifier = Modifier.fillMaxWidth().height(180.dp).padding(top = 10.dp),
                    sizes = sizes,
                    onSizesChange = { sizes = it },
                    onDragEnd = { sizes = it },
                    pane1 = { SplitPane("Pane 1", theme.colors.primary.copy(alpha = 0.14f)) },
                    pane2 = { SplitPane("Pane 2", theme.colors.primary.copy(alpha = 0.24f)) },
                )
            }
        }
    }
}

@Composable
private fun SplitPane(label: String, color: Color) {
    val theme = LocalNTheme.current
    Box(
        Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(theme.borderRadius))
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        NText(label, color = theme.colors.text1, strong = true)
    }
}
