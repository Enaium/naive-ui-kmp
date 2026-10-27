package cn.enaium.naiveui.sample.gallery.config

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoRow
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NConfigProvider
import cn.enaium.naiveui.theme.NGlobalStyle
import cn.enaium.naiveui.theme.NTheme

@Composable
fun GlobalStyleDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Global Style",
        description = "NGlobalStyle paints the body background and text color of the current theme for its subtree, " +
            "matching naive-ui's global body styles.",
    ) {
        DemoSection(
            title = "Body background",
            description = "Toggle the theme and the styled area follows the body and text color tokens.",
            code = """
                NConfigProvider(theme = if (dark) NTheme.Dark else NTheme.Light) {
                    NGlobalStyle {
                        NText("Body content")
                    }
                }
            """.trimIndent(),
        ) {
            var dark by remember { mutableStateOf(false) }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DemoRow {
                    NButton(onClick = { dark = false }) { NText("Light") }
                    NButton(onClick = { dark = true }) { NText("Dark") }
                }
                NConfigProvider(theme = if (dark) NTheme.Dark else NTheme.Light) {
                    val current = LocalNTheme.current
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(theme.borderRadius)),
                    ) {
                        NGlobalStyle {
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                NText(
                                    if (current.isDark) "Dark body background" else "Light body background",
                                    color = current.colors.text1,
                                    strong = true,
                                )
                                NText(
                                    "The area below uses the body color token from the configured theme.",
                                    color = current.colors.text2,
                                )
                                NText(
                                    "body = ${current.colors.body}",
                                    color = current.colors.text3,
                                )
                            }
                        }
                    }
                }
            }
        }

        DemoSection(
            title = "Text color",
            description = "Descendants inherit the theme text color unless they override it.",
        ) {
            NConfigProvider(theme = NTheme.Dark) {
                val current = LocalNTheme.current
                Box(Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(current.borderRadius))) {
                    NGlobalStyle {
                        Column(Modifier.fillMaxSize().padding(20.dp)) {
                            NText("This text uses the dark theme text color.")
                        }
                    }
                }
            }
        }
    }
}
