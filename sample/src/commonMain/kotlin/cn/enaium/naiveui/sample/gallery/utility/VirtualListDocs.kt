package cn.enaium.naiveui.sample.gallery.utility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NVirtualList
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

private val longMessages = listOf(
    "Life is like a grand magic show, we sometimes play the magician, and sometimes become the audience.",
    "Complexity isn't going away, it's just going to shift, so stay awake when you hear elaborate models.",
    "Virtual lists make a list feel infinite, but they are really just hiding the elements you cannot see.",
    "Sometimes the question itself is the answer, and the process of pursuit is already the answer.",
    "Freud read dreams, Hemingway read the sea, Van Gogh read the wheat fields, and Camus read Kafka.",
)

@Composable
fun VirtualListDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Virtual List",
        description = "Only the visible rows are composed, so a list of ten thousand items stays cheap. " +
            "The list needs a bounded height: set it with Modifier.height.",
    ) {
        DemoSection(
            title = "Basic",
            description = "10,000 fixed-height items rendered in a 240dp viewport.",
            code = """
                val items = remember { List(10_000) { it } }
                NVirtualList(
                    items = items,
                    modifier = Modifier.fillMaxWidth().height(240.dp),
                    itemHeight = 40.dp,
                ) { index ->
                    NText("Item ${'$'}index")
                }
            """.trimIndent(),
        ) {
            val items = remember { List(10_000) { it } }
            NVirtualList(
                items = items,
                modifier = Modifier.fillMaxWidth().height(240.dp),
                itemHeight = 40.dp,
            ) { index ->
                Row(
                    Modifier
                        .fillMaxSize()
                        .background(if (index % 2 == 0) theme.colors.action else theme.colors.card)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    androidx.compose.foundation.layout.Box(
                        Modifier
                            .clip(CircleShape)
                            .background(theme.colors.primary.copy(alpha = 0.18f))
                            .width(26.dp)
                            .height(26.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        NText(
                            "${index % 10}",
                            color = theme.colors.primary,
                        )
                    }
                    NText(
                        "Item ${index + 1}",
                        modifier = Modifier.padding(start = 10.dp),
                        color = theme.colors.text2,
                    )
                }
            }
        }

        DemoSection(
            title = "Variable height",
            description = "Set itemResizable when rows have an unknown height; rows are measured as they appear.",
        ) {
            val messages = remember { List(10_000) { longMessages[it % longMessages.size] } }
            NVirtualList(
                items = messages,
                modifier = Modifier.fillMaxWidth().height(260.dp),
                itemHeight = 42.dp,
                itemResizable = true,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 6.dp),
            ) { message ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    androidx.compose.foundation.layout.Box(
                        Modifier
                            .padding(top = 2.dp)
                            .clip(CircleShape)
                            .background(theme.colors.success.copy(alpha = 0.2f))
                            .width(24.dp)
                            .height(24.dp),
                        contentAlignment = Alignment.Center,
                    ) {}
                    NText(message, color = theme.colors.text2)
                }
            }
        }
    }
}
