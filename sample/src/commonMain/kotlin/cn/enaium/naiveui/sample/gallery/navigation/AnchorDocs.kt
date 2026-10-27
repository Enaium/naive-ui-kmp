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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NAnchor
import cn.enaium.naiveui.components.NAnchorItem
import cn.enaium.naiveui.components.NAnchorLink
import cn.enaium.naiveui.components.NAnchorType
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import kotlinx.coroutines.launch

private val anchorItems = listOf(
    NAnchorItem(
        id = "demos",
        title = "Demos",
        children = listOf(
            NAnchorItem("basic", "Basic"),
            NAnchorItem("ignore-gap", "Ignore-Gap"),
            NAnchorItem("affix", "Affix"),
            NAnchorItem("scroll-to", "Scroll To"),
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
        description = "Anchor provides a table of contents with a rail, nested links, optional affix behavior and programmatic scrolling.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Toggle the rail and background, then select links. The active link drives the rail indicator.",
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
            title = "Ignore gap",
            description = "`ignoreGap = true` keeps the rail indicator exactly on the link bounds; the default adds a `bound` gap around it.",
        ) {
            var activeA by remember { mutableStateOf<String?>("basic") }
            var activeB by remember { mutableStateOf<String?>("basic") }
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    NText("ignoreGap = false", color = theme.colors.text3)
                    NAnchor(
                        items = anchorItems,
                        activeId = activeA,
                        onSelect = { activeA = it },
                        modifier = Modifier.width(190.dp),
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    NText("ignoreGap = true", color = theme.colors.text3)
                    NAnchor(
                        items = anchorItems,
                        activeId = activeB,
                        onSelect = { activeB = it },
                        ignoreGap = true,
                        modifier = Modifier.width(190.dp),
                    )
                }
            }
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
            title = "Scroll to",
            description = "Selecting a link scrolls the linked content container. This is the Compose equivalent of `anchor.scrollTo(href)`.",
            code = """
val positions = remember { mutableStateMapOf<String, Float>() }
NAnchor(
    items = items,
    activeId = active,
    onSelect = { id ->
        active = id
        scope.launch { scrollState.animateScrollTo(positions[id]!!.toInt()) }
    },
)
            """.trimIndent(),
        ) {
            val scrollState = rememberScrollState()
            val scope = rememberCoroutineScope()
            val positions = remember { mutableStateMapOf<String, Float>() }
            var active by remember { mutableStateOf<String?>("demos") }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                NAnchor(
                    items = anchorItems,
                    activeId = active,
                    onSelect = { id ->
                        active = id
                        scope.launch {
                            scrollState.animateScrollTo((positions[id] ?: 0f).toInt())
                        }
                    },
                    modifier = Modifier.width(190.dp),
                )
                Box(
                    Modifier
                        .weight(1f)
                        .height(240.dp)
                        .clip(RoundedCornerShape(theme.borderRadius))
                        .background(theme.colors.body)
                        .nBorder(theme.colors.border, 1.dp, theme.borderRadius),
                ) {
                    NScrollbar(Modifier.fillMaxSize(), scrollState = scrollState) {
                        Column(Modifier.padding(16.dp)) {
                            ScrollSection("demos", "Demos", positions) {
                                NText("Choose a section from the anchor to scroll here.")
                            }
                            ScrollSection("basic", "Basic", positions) {
                                NText("The Basic demo shows the rail and background.")
                            }
                            ScrollSection("affix", "Affix", positions) {
                                NText("The Affix demo keeps the anchor pinned.")
                            }
                            ScrollSection("api", "API", positions) {
                                NText("Anchor accepts items, activeId, onSelect and affix.")
                            }
                            ScrollSection("faq", "FAQ", positions) {
                                NText("Compose replaces DOM scrolling with a ScrollState.")
                            }
                            Spacer(Modifier.height(240.dp))
                        }
                    }
                }
            }
        }

        DemoSection(
            title = "Custom links",
            description = "The `content` slot accepts `NAnchorLink` entries for full control.",
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
    }
}

@Composable
private fun ScrollSection(
    id: String,
    title: String,
    positions: MutableMap<String, Float>,
    content: @Composable () -> Unit,
) {
    val theme = LocalNTheme.current
    Column(
        Modifier
            .fillMaxWidth()
            .onGloballyPositioned { positions[id] = it.positionInParent().y }
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        NText(title, color = theme.colors.text1, size = theme.typography.fontSize.large)
        content()
    }
}
