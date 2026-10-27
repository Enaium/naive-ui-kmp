package cn.enaium.naiveui.sample.gallery.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import cn.enaium.naiveui.components.NAnchor
import cn.enaium.naiveui.components.NAnchorItem
import cn.enaium.naiveui.components.NAnchorLink
import cn.enaium.naiveui.components.NAnchorType
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

private val anchorItems = listOf(
    NAnchorItem(
        id = "demos",
        title = "Demos",
        children = listOf(
            NAnchorItem("basic", "Basic"),
            NAnchorItem("rail", "Rail and background"),
            NAnchorItem("affix", "Affix"),
        ),
    ),
    NAnchorItem("api", "API"),
    NAnchorItem("faq", "FAQ"),
)

@Composable
fun AnchorDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Anchor",
        description = "Anchor provides a table of contents with a rail and nested links. Selecting a link reports its id.",
    ) {
        DemoSection(
            title = "Basic",
            description = "`activeId` controls the highlighted link; without it the component keeps its own selection.",
        ) {
            var active by remember { mutableStateOf<String?>("basic") }
            NAnchor(
                items = anchorItems,
                activeId = active,
                onSelect = { active = it },
                modifier = Modifier.width(220.dp),
            )
        }

        DemoSection(
            title = "Rail and background",
            description = "Toggle the rail and the rounded active background.",
        ) {
            var active by remember { mutableStateOf<String?>("basic") }
            var showRail by remember { mutableStateOf(true) }
            var showBackground by remember { mutableStateOf(true) }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NButton(onClick = { showRail = !showRail }) {
                        NText(if (showRail) "Hide rail" else "Show rail")
                    }
                    NButton(onClick = { showBackground = !showBackground }) {
                        NText(if (showBackground) "Hide background" else "Show background")
                    }
                }
                NAnchor(
                    items = anchorItems,
                    activeId = active,
                    onSelect = { active = it },
                    showRail = showRail,
                    showBackground = showBackground,
                    modifier = Modifier.width(220.dp),
                )
            }
        }

        DemoSection(
            title = "Block type",
            description = "Block type uses hover and active backgrounds instead of a rail.",
        ) {
            var active by remember { mutableStateOf<String?>("api") }
            NAnchor(
                items = anchorItems,
                activeId = active,
                onSelect = { active = it },
                type = NAnchorType.Block,
                showRail = false,
                modifier = Modifier.width(220.dp),
            )
        }

        DemoSection(
            title = "Affix",
            description = "When affixed, the anchor pins itself while the surrounding container scrolls.",
        ) {
            var active by remember { mutableStateOf<String?>("basic") }
            val scrollState = rememberScrollState()
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(theme.borderRadius))
                    .background(theme.colors.body)
                    .nBorder(theme.colors.border, 1.dp, theme.borderRadius),
            ) {
                NScrollbar(Modifier.fillMaxSize(), scrollState = scrollState) {
                    Column(Modifier.padding(16.dp)) {
                        Spacer(Modifier.height(120.dp))
                        NAnchor(
                            items = anchorItems,
                            activeId = active,
                            onSelect = { active = it },
                            affix = true,
                            scrollState = scrollState,
                            modifier = Modifier.width(220.dp),
                        )
                        Spacer(Modifier.height(420.dp))
                    }
                }
            }
        }

        DemoSection(
            title = "Custom links",
            description = "The `content` slot accepts `NAnchorLink` entries for full control.",
            code = """
NAnchor(activeId = active, onSelect = { active = it }) {
    NAnchorLink("Introduction", "#intro")
    NAnchorLink("Installation", "#install")
}
            """.trimIndent(),
        ) {
            var active by remember { mutableStateOf<String?>("intro") }
            NAnchor(
                activeId = active,
                onSelect = { active = it },
                modifier = Modifier.width(220.dp),
            ) {
                NAnchorLink("Introduction", "#intro")
                NAnchorLink("Installation", "#install")
                NAnchorLink("Usage", "#usage")
            }
        }
    }
}
