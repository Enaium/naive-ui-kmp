package cn.enaium.naiveui.sample.gallery.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NLayout
import cn.enaium.naiveui.components.NLayoutCollapseMode
import cn.enaium.naiveui.components.NLayoutContent
import cn.enaium.naiveui.components.NLayoutFooter
import cn.enaium.naiveui.components.NLayoutHeader
import cn.enaium.naiveui.components.NLayoutPosition
import cn.enaium.naiveui.components.NLayoutSider
import cn.enaium.naiveui.components.NLayoutSiderPlacement
import cn.enaium.naiveui.components.NLayoutSiderTrigger
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoRow
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import kotlinx.coroutines.launch

@Composable
fun LayoutDocs() {
    val theme = LocalNTheme.current
    val shape = RoundedCornerShape(theme.borderRadius)
    DocsPage(
        title = "Layout",
        description = "Layout arranges a header, an optional sider, the content area and a footer. " +
            "Set hasSider on the enclosing layout whenever a sider is used so children flow in a row.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Header, content and footer stack vertically; a sider can host the page " +
                "navigation or wrap the whole layout.",
            code = """
                NLayout {
                    NLayoutHeader { NText("Header") }
                    NLayoutContent(contentPadding = PaddingValues(24.dp)) {
                        NText("Content")
                    }
                    NLayoutFooter { NText("Footer") }
                }
            """.trimIndent(),
        ) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                NLayout(Modifier.clip(shape)) {
                    NLayoutHeader { NText("Yiheyuan Road") }
                    NLayoutContent(contentPadding = PaddingValues(24.dp)) {
                        NText("Pingshan Road")
                    }
                    NLayoutFooter { NText("Chengfu Road") }
                }
                NLayout(Modifier.clip(shape)) {
                    NLayoutHeader { NText("Yiheyuan Road") }
                    NLayout(hasSider = true) {
                        NLayoutSider(contentPadding = PaddingValues(24.dp)) {
                            NText("Handian Bridge")
                        }
                        NLayoutContent(contentPadding = PaddingValues(24.dp)) {
                            NText("Pingshan Road")
                        }
                    }
                    NLayoutFooter { NText("Chengfu Road") }
                }
                NLayout(hasSider = true, modifier = Modifier.clip(shape)) {
                    NLayoutSider(contentPadding = PaddingValues(24.dp)) {
                        NText("Handian Bridge")
                    }
                    NLayout {
                        NLayoutHeader { NText("Yiheyuan Road") }
                        NLayoutContent(contentPadding = PaddingValues(24.dp)) {
                            NText("Pingshan Road")
                        }
                        NLayoutFooter { NText("Chengfu Road") }
                    }
                }
            }
        }

        DemoSection(
            title = "Set padding",
            description = "Prefer contentPadding on the sider over padding its children: the padding " +
                "then belongs to the scrollable content area instead of the sider box.",
        ) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                NLayout(hasSider = true, modifier = Modifier.clip(shape)) {
                    NLayoutSider {
                        Column(Modifier.padding(24.dp)) {
                            repeat(3) { NText("... Not Recommended") }
                        }
                    }
                    NLayoutContent(contentPadding = PaddingValues(24.dp)) {
                        NText("Pingshan Road")
                    }
                }
                NLayout(hasSider = true, modifier = Modifier.clip(shape)) {
                    NLayoutSider(contentPadding = PaddingValues(24.dp)) {
                        repeat(3) { NText("Recommended") }
                    }
                    NLayoutContent(contentPadding = PaddingValues(24.dp)) {
                        NText("Pingshan Road")
                    }
                }
            }
        }

        DemoSection(
            title = "Border",
            description = "Sider, header and footer all accept bordered to draw their divider.",
        ) {
            NLayout(hasSider = true, modifier = Modifier.clip(shape)) {
                NLayoutSider(bordered = true, contentPadding = PaddingValues(24.dp)) {
                    NText("Handian Bridge")
                }
                NLayout {
                    NLayoutHeader(bordered = true) { NText("Yiheyuan Road") }
                    NLayoutContent(contentPadding = PaddingValues(24.dp)) {
                        NText("Pingshan Road")
                    }
                    NLayoutFooter(bordered = true) { NText("Chengfu Road") }
                }
            }
        }

        DemoSection(
            title = "Embedded",
            description = "Embedded layouts use the darker action background so cards stand out.",
        ) {
            NLayout(embedded = true, modifier = Modifier.clip(shape)) {
                NLayoutContent(contentPadding = PaddingValues(20.dp)) {
                    NText(
                        "All you need to do is look at those clouds, and every day be in a good mood.",
                    )
                }
            }
        }

        DemoSection(
            title = "Absolute position",
            description = "Set position to Absolute to fill a fixed area. Header and footer are " +
                "overlaid with the Box scope, and the sider and content scroll inside the remaining space.",
        ) {
            Box(Modifier.fillMaxWidth().height(360.dp).clip(shape)) {
                NLayout(
                    hasSider = true,
                    position = NLayoutPosition.Absolute,
                    modifier = Modifier.padding(top = 64.dp, bottom = 54.dp),
                ) {
                    NLayoutSider(
                        bordered = true,
                        scrollable = true,
                        contentPadding = PaddingValues(24.dp),
                    ) {
                        repeat(12) { NText("Handian Bridge") }
                    }
                    NLayout(scrollable = true) {
                        NLayoutContent(contentPadding = PaddingValues(24.dp)) {
                            repeat(12) { NText("Pingshan Road") }
                        }
                    }
                }
                NLayoutHeader(
                    modifier = Modifier.align(Alignment.TopStart),
                    bordered = true,
                    height = 64.dp,
                ) { NText("Yiheyuan Road") }
                NLayoutFooter(
                    modifier = Modifier.align(Alignment.BottomStart),
                    bordered = true,
                ) { NText("Chengfu Road") }
            }
        }

        DemoSection(
            title = "Use built-in scrollbar",
            description = "scrollable renders a naive-ui style scrollbar on the layout and on the sider.",
        ) {
            Box(Modifier.fillMaxWidth().height(360.dp).clip(shape)) {
                NLayout(
                    hasSider = true,
                    position = NLayoutPosition.Absolute,
                    modifier = Modifier.padding(top = 64.dp, bottom = 54.dp),
                ) {
                    NLayoutSider(
                        bordered = true,
                        scrollable = true,
                        contentPadding = PaddingValues(24.dp),
                    ) {
                        repeat(12) { NText("Handian Bridge") }
                    }
                    NLayout(scrollable = true) {
                        NLayoutContent(contentPadding = PaddingValues(24.dp)) {
                            repeat(12) { NText("Pingshan Road") }
                        }
                    }
                }
                NLayoutHeader(
                    modifier = Modifier.align(Alignment.TopStart),
                    bordered = true,
                    height = 64.dp,
                ) { NText("Yiheyuan Road") }
                NLayoutFooter(
                    modifier = Modifier.align(Alignment.BottomStart),
                    bordered = true,
                ) { NText("Chengfu Road") }
            }
        }

        DemoSection(
            title = "Collapse sider",
            description = "showTrigger adds the built-in trigger. The bar style uses a full-height bar; " +
                "the arrow style uses the circular button. Width and transform collapse modes are shown.",
        ) {
            var barCollapsed by remember { mutableStateOf(false) }
            var arrowCollapsed by remember { mutableStateOf(false) }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                NLayout(hasSider = true, modifier = Modifier.clip(shape)) {
                    NLayoutSider(
                        width = 240.dp,
                        collapsedWidth = 120.dp,
                        collapsed = barCollapsed,
                        onCollapsedChange = { barCollapsed = it },
                        showTrigger = true,
                        triggerStyle = NLayoutSiderTrigger.Bar,
                        bordered = true,
                        contentPadding = PaddingValues(24.dp),
                    ) {
                        NText(
                            "Handian Bridge Handian Bridge Handian Bridge Handian Bridge Handian Bridge",
                        )
                    }
                    NLayoutContent(contentPadding = PaddingValues(24.dp)) {
                        NText("Pingshan Road")
                    }
                }
                NLayout(hasSider = true, modifier = Modifier.clip(shape)) {
                    NLayoutSider(
                        width = 240.dp,
                        collapsedWidth = 120.dp,
                        collapsed = arrowCollapsed,
                        onCollapsedChange = { arrowCollapsed = it },
                        collapseMode = NLayoutCollapseMode.Transform,
                        showTrigger = true,
                        triggerStyle = NLayoutSiderTrigger.ArrowCircle,
                        bordered = true,
                        contentPadding = PaddingValues(24.dp),
                    ) {
                        NText("Handian Bridge")
                    }
                    NLayoutContent(contentPadding = PaddingValues(24.dp)) {
                        NText("Pingshan Road")
                    }
                }
            }
        }

        DemoSection(
            title = "Sider placement",
            description = "siderPlacement puts the sider on the right while the content keeps the " +
                "remaining width. Both triggers adapt to the side automatically.",
        ) {
            var collapsed by remember { mutableStateOf(false) }
            var barCollapsed by remember { mutableStateOf(false) }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                NLayout(
                    hasSider = true,
                    siderPlacement = NLayoutSiderPlacement.Right,
                    modifier = Modifier.clip(shape),
                ) {
                    NLayoutContent(contentPadding = PaddingValues(24.dp)) {
                        NText("Pingshan Road")
                    }
                    NLayoutSider(
                        width = 240.dp,
                        collapsedWidth = 120.dp,
                        collapsed = collapsed,
                        onCollapsedChange = { collapsed = it },
                        showTrigger = true,
                        triggerStyle = NLayoutSiderTrigger.ArrowCircle,
                        bordered = true,
                        contentPadding = PaddingValues(24.dp),
                    ) {
                        repeat(3) { NText("Handian Bridge") }
                    }
                }
                NLayout(
                    hasSider = true,
                    siderPlacement = NLayoutSiderPlacement.Right,
                    modifier = Modifier.clip(shape),
                ) {
                    NLayoutContent(contentPadding = PaddingValues(24.dp)) {
                        NText("Pingshan Road")
                    }
                    NLayoutSider(
                        width = 240.dp,
                        collapsedWidth = 64.dp,
                        collapsed = barCollapsed,
                        onCollapsedChange = { barCollapsed = it },
                        collapseMode = NLayoutCollapseMode.Transform,
                        showTrigger = true,
                        triggerStyle = NLayoutSiderTrigger.Bar,
                        bordered = true,
                        contentPadding = PaddingValues(16.dp),
                    ) {
                        repeat(3) { NText("Handian Bridge") }
                    }
                }
            }
        }

        DemoSection(
            title = "Inverted",
            description = "Inverted sider, header and footer use the inverted background token for contrast.",
        ) {
            var inverted by remember { mutableStateOf(true) }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DemoRow {
                    NButton(onClick = { inverted = !inverted }) {
                        NText(if (inverted) "Use normal colors" else "Use inverted colors")
                    }
                }
                NLayout(hasSider = true, modifier = Modifier.clip(shape)) {
                    NLayoutSider(
                        inverted = inverted,
                        bordered = true,
                        contentPadding = PaddingValues(24.dp),
                    ) {
                        NText("Handian Bridge")
                    }
                    NLayout {
                        NLayoutHeader(inverted = inverted) { NText("Yiheyuan Road") }
                        NLayoutContent(contentPadding = PaddingValues(24.dp)) {
                            NText("Pingshan Road")
                        }
                        NLayoutFooter(inverted = inverted) { NText("Chengfu Road") }
                    }
                }
            }
        }

        DemoSection(
            title = "Hide sider content",
            description = "showCollapsedContent = false hides the sider content while it is collapsed.",
        ) {
            var collapsed by remember { mutableStateOf(false) }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NLayout(hasSider = true, modifier = Modifier.clip(shape)) {
                    NLayoutSider(
                        width = 240.dp,
                        collapsedWidth = 120.dp,
                        collapsed = collapsed,
                        onCollapsedChange = { collapsed = it },
                        showCollapsedContent = false,
                        showTrigger = true,
                        bordered = true,
                        contentPadding = PaddingValues(24.dp),
                    ) {
                        NText("Handian Bridge Handian Bridge Handian Bridge")
                    }
                    NLayoutContent(contentPadding = PaddingValues(24.dp)) {
                        NText("Pingshan Road")
                    }
                }
                NLayout(hasSider = true, modifier = Modifier.clip(shape)) {
                    NLayoutSider(
                        width = 240.dp,
                        collapsedWidth = 120.dp,
                        collapsed = collapsed,
                        onCollapsedChange = { collapsed = it },
                        showTrigger = true,
                        bordered = true,
                        contentPadding = PaddingValues(24.dp),
                    ) {
                        NText("Handian Bridge")
                    }
                    NLayoutContent(contentPadding = PaddingValues(24.dp)) {
                        NText("Pingshan Road")
                    }
                }
            }
        }

        DemoSection(
            title = "Scroll to",
            description = "Hoist a ScrollState and call animateScrollTo to move the sider or the content.",
        ) {
            val density = LocalDensity.current
            val siderScroll = rememberScrollState()
            val contentScroll = rememberScrollState()
            val scope = rememberCoroutineScope()
            val target = with(density) { 120.dp.roundToPx() }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DemoRow {
                    NButton(onClick = { scope.launch { siderScroll.animateScrollTo(target) } }) {
                        NText("Sider scroll to 120px")
                    }
                    NButton(onClick = { scope.launch { contentScroll.animateScrollTo(target) } }) {
                        NText("Content scroll to 120px")
                    }
                }
                Box(Modifier.fillMaxWidth().height(320.dp).clip(shape)) {
                    NLayout(
                        hasSider = true,
                        position = NLayoutPosition.Absolute,
                        modifier = Modifier.padding(top = 56.dp, bottom = 54.dp),
                    ) {
                        NLayoutSider(
                            bordered = true,
                            scrollable = true,
                            scrollState = siderScroll,
                            contentPadding = PaddingValues(24.dp),
                        ) {
                            repeat(12) { NText("Handian Bridge") }
                        }
                        NLayout(scrollable = true, scrollState = contentScroll) {
                            NLayoutContent(contentPadding = PaddingValues(24.dp)) {
                                repeat(12) { NText("Pingshan Road") }
                            }
                        }
                    }
                    NLayoutHeader(
                        modifier = Modifier.align(Alignment.TopStart),
                        bordered = true,
                        height = 56.dp,
                    ) { NText("Yiheyuan Road") }
                    NLayoutFooter(
                        modifier = Modifier.align(Alignment.BottomStart),
                        bordered = true,
                    ) { NText("Chengfu Road") }
                }
            }
        }
    }
}
