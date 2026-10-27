package cn.enaium.naiveui.sample.gallery.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import cn.enaium.naiveui.components.NCard
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NLayout
import cn.enaium.naiveui.components.NLayoutContent
import cn.enaium.naiveui.components.NLayoutFooter
import cn.enaium.naiveui.components.NLayoutHeader
import cn.enaium.naiveui.components.NLayoutSider
import cn.enaium.naiveui.sample.gallery.DemoRow
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

@Composable
fun LayoutDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Layout",
        description = "Layout arranges a header, an optional sider, the content area and a footer. " +
            "Set hasSider on the enclosing layout whenever a sider is used so children flow in a row.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Header, content and footer stack vertically inside the layout.",
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
            NLayout(Modifier.clip(RoundedCornerShape(theme.borderRadius))) {
                NLayoutHeader { NText("Yiheyuan Road") }
                NLayoutContent(contentPadding = PaddingValues(24.dp)) {
                    NText("Pingshan Road")
                }
                NLayoutFooter { NText("Chengfu Road") }
            }
        }

        DemoSection(
            title = "With sider",
            description = "The sider keeps a fixed width and can be collapsed from its built-in trigger.",
            code = """
                NLayout(hasSider = true) {
                    NLayoutSider(collapsed = collapsed, onCollapsedChange = { collapsed = it }, showTrigger = true) {
                        NText("Sider")
                    }
                    NLayout { /* header, content, footer */ }
                }
            """.trimIndent(),
        ) {
            var collapsed by remember { mutableStateOf(false) }
            NLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(theme.borderRadius)),
                hasSider = true,
            ) {
                NLayoutSider(
                    collapsed = collapsed,
                    onCollapsedChange = { collapsed = it },
                    showTrigger = true,
                ) {
                    Column(Modifier.padding(16.dp)) {
                        NText(if (collapsed) "N" else "Handian Bridge")
                    }
                }
                NLayout {
                    NLayoutHeader { NText("Yiheyuan Road") }
                    NLayoutContent(
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 32.dp),
                    ) {
                        NText("Pingshan Road")
                    }
                    NLayoutFooter { NText("Chengfu Road") }
                }
            }
        }

        DemoSection(
            title = "Footer",
            description = "Footers accept arbitrary content and can show a top border.",
        ) {
            NLayout(Modifier.clip(RoundedCornerShape(theme.borderRadius))) {
                NLayoutHeader(bordered = true) { NText("Header") }
                NLayoutContent(contentPadding = PaddingValues(24.dp)) {
                    NText("The footer below hosts actions.")
                }
                NLayoutFooter(bordered = true) {
                    DemoRow {
                        NButton { NText("Cancel") }
                        NButton { NText("Confirm") }
                    }
                }
            }
        }

        DemoSection(
            title = "Embedded",
            description = "Embedded layouts use the darker action background so cards stand out.",
            code = """
                NLayout(embedded = true) {
                    NLayoutContent(contentPadding = PaddingValues(24.dp)) {
                        NCard(title = "Card") { NText("Content") }
                    }
                }
            """.trimIndent(),
        ) {
            NLayout(embedded = true, modifier = Modifier.clip(RoundedCornerShape(theme.borderRadius))) {
                NLayoutContent(
                    contentPadding = PaddingValues(20.dp),
                    content = {
                        NCard(title = "Embedded layout") {
                            NText(
                                "All you need to do is look at those clouds, and every day be in a good mood.",
                                color = theme.colors.text2,
                            )
                        }
                    },
                )
            }
        }
    }
}
