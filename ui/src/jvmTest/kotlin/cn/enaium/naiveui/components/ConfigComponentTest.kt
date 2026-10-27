package cn.enaium.naiveui.components

import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import cn.enaium.naiveui.theme.LocalNComponentSize
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NConfigProvider
import cn.enaium.naiveui.theme.NGlobalStyle
import cn.enaium.naiveui.theme.NLocale
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NTheme
import cn.enaium.naiveui.theme.NThemeOverrides
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ConfigComponentTest {
    @Test
    fun configProviderInheritsSizeAndLocale() = runComposeUiTest {
        var size: NSize? = null
        var locale: NLocale? = null
        setNContent {
            NConfigProvider(size = NSize.Small, locale = NLocale.Chinese) {
                val resolvedSize = LocalNComponentSize.current
                val resolvedLocale = LocalNLocale.current
                SideEffect {
                    size = resolvedSize
                    locale = resolvedLocale
                }
                NText("Configured content")
            }
        }
        onNodeWithText("Configured content").assertExists()
        runOnIdle {
            assertEquals(NSize.Small, size)
            assertEquals("zh-CN", locale?.name)
        }
    }

    @Test
    fun configProviderAppliesTheme() = runComposeUiTest {
        var isDark = false
        setNContent {
            NConfigProvider(theme = NTheme.Dark) {
                val dark = LocalNTheme.current.isDark
                SideEffect { isDark = dark }
                NText("Dark themed content")
            }
        }
        onNodeWithText("Dark themed content").assertExists()
        runOnIdle { assertTrue(isDark) }
    }

    @Test
    fun configProviderAppliesThemeOverrides() = runComposeUiTest {
        var primary: Color? = null
        val custom = Color(0xFF123456)
        setNContent {
            NConfigProvider(themeOverrides = NThemeOverrides(primaryColor = custom)) {
                val resolvedPrimary = LocalNTheme.current.colors.primary
                SideEffect { primary = resolvedPrimary }
                NText("Overridden content")
            }
        }
        onNodeWithText("Overridden content").assertExists()
        runOnIdle { assertEquals(custom, primary) }
    }

    @Test
    fun globalStyleRendersContent() = runComposeUiTest {
        setNContent {
            NGlobalStyle {
                NText("Globally styled content")
            }
        }
        onNodeWithText("Globally styled content").assertExists()
    }

    @Test
    fun elementRendersArbitraryContent() = runComposeUiTest {
        setNContent {
            NElement {
                NElement(contentColor = Color.White) {
                    NText("Nested element content")
                }
                NText("Element content")
            }
        }
        onNodeWithText("Nested element content").assertExists()
        onNodeWithText("Element content").assertExists()
    }
}
