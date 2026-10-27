package cn.enaium.naiveui.sample.gallery.config

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NButtonType
import cn.enaium.naiveui.components.NCard
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoRow
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NConfigProvider
import cn.enaium.naiveui.theme.NLocale
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import cn.enaium.naiveui.theme.NTheme
import cn.enaium.naiveui.theme.NThemeOverrides

@Composable
fun ConfigProviderDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Config Provider",
        description = "NConfigProvider supplies the theme, theme overrides, locale and component size to " +
            "every naive-ui component in its subtree.",
    ) {
        DemoSection(
            title = "Theme",
            description = "Switch between the light and dark themes for the wrapped preview.",
            code = """
                NConfigProvider(theme = if (dark) NTheme.Dark else NTheme.Light) {
                    NCard(title = "Card") { NText("Content") }
                }
            """.trimIndent(),
        ) {
            var dark by remember { mutableStateOf(false) }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DemoRow {
                    NButton(onClick = { dark = false }) { NText("Light") }
                    NButton(onClick = { dark = true }) { NText("Dark") }
                }
                ProviderPreview(
                    dark = dark,
                    overrides = null,
                    locale = NLocale.English,
                    size = NSize.Medium,
                )
            }
        }

        DemoSection(
            title = "Theme overrides",
            description = "primaryColor and borderRadius are overridden without replacing the whole theme.",
            code = """
                NConfigProvider(
                    themeOverrides = NThemeOverrides(
                        primaryColor = Color(0xFF2080F0),
                        borderRadius = 10.dp,
                    ),
                ) { /* content */ }
            """.trimIndent(),
        ) {
            var primary by remember { mutableStateOf(Color(0xFF18A058)) }
            var radius by remember { mutableStateOf(3.dp) }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DemoRow {
                    NButton(color = Color(0xFF18A058), onClick = { primary = Color(0xFF18A058) }) {
                        NText("Green", color = Color.White)
                    }
                    NButton(color = Color(0xFF2080F0), onClick = { primary = Color(0xFF2080F0) }) {
                        NText("Blue", color = Color.White)
                    }
                    NButton(color = Color(0xFFD03050), onClick = { primary = Color(0xFFD03050) }) {
                        NText("Red", color = Color.White)
                    }
                }
                DemoRow {
                    NButton(onClick = { radius = 3.dp }) { NText("Radius 3") }
                    NButton(onClick = { radius = 10.dp }) { NText("Radius 10") }
                    NButton(onClick = { radius = 16.dp }) { NText("Radius 16") }
                }
                ProviderPreview(
                    dark = false,
                    overrides = NThemeOverrides(primaryColor = primary, borderRadius = radius),
                    locale = NLocale.English,
                    size = NSize.Medium,
                )
            }
        }

        DemoSection(
            title = "Locale",
            description = "Locale changes the text rendered by components that expose localized labels.",
        ) {
            var chinese by remember { mutableStateOf(false) }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DemoRow {
                    NButton(onClick = { chinese = false }) { NText("en-US") }
                    NButton(onClick = { chinese = true }) { NText("zh-CN") }
                }
                ProviderPreview(
                    dark = theme.isDark,
                    overrides = null,
                    locale = if (chinese) NLocale.Chinese else NLocale.English,
                    size = NSize.Medium,
                )
            }
        }

        DemoSection(
            title = "Component size",
            description = "The provider size is inherited by components that accept a size, such as buttons and tags.",
        ) {
            var size by remember { mutableStateOf(NSize.Small) }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DemoRow {
                    NButton(onClick = { size = NSize.Small }) { NText("Small") }
                    NButton(onClick = { size = NSize.Medium }) { NText("Medium") }
                    NButton(onClick = { size = NSize.Large }) { NText("Large") }
                }
                ProviderPreview(
                    dark = theme.isDark,
                    overrides = null,
                    locale = NLocale.English,
                    size = size,
                )
            }
        }
    }
}

@Composable
private fun ProviderPreview(
    dark: Boolean,
    overrides: NThemeOverrides?,
    locale: NLocale,
    size: NSize,
) {
    NConfigProvider(
        theme = if (dark) NTheme.Dark else NTheme.Light,
        themeOverrides = overrides,
        locale = locale,
        size = size,
    ) {
        val current = LocalNTheme.current
        val currentLocale = LocalNLocale.current
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(current.borderRadius))
                .background(current.colors.body)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            NText(
                if (current.isDark) "Dark theme" else "Light theme",
                color = current.colors.text1,
                strong = true,
            )
            DemoRow {
                NButton(type = NButtonType.Primary) { NText("Primary") }
                NButton { NText("Default") }
                NTag(text = "Tag", type = NStatus.Primary, size = size)
            }
            NText(
                "${currentLocale.ok} / ${currentLocale.cancel} / ${currentLocale.confirm}",
                color = current.colors.text3,
            )
            NCard(title = "Card") {
                NText(
                    "This card inherits the provider theme, overrides, locale and size.",
                    color = current.colors.text2,
                )
            }
        }
    }
}
