package cn.enaium.naiveui.sample

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.enaium.naiveui.components.NEmpty
import cn.enaium.naiveui.components.NPagination
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.theme.NConfigProvider
import cn.enaium.naiveui.theme.NGlobalStyle
import cn.enaium.naiveui.theme.NLocale
import cn.enaium.naiveui.theme.NLocales
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LocaleTest {
    @Test
    fun allNaiveLocalesAreAvailable() {
        assertEquals(79, NLocales.all.size)
        assertEquals(79, NLocales.all.map { it.name }.toSet().size)
        assertEquals(79, NLocales.all.map { it.displayName }.toSet().size)
        assertTrue(NLocales.all.all { it.date.months.size == 12 })
        assertTrue(NLocales.all.all { it.date.monthsShort.size == 12 })
        assertTrue(NLocales.all.all { it.date.weekdays.size == 7 })
        assertTrue(NLocales.all.all { it.date.weekdaysShort.size == 7 })
        assertTrue(NLocales.all.all { it.date.firstDayOfWeek in 1..7 })
        assertTrue(NLocales.all.all { it.extra.search.isNotBlank() })
        assertEquals("zh-CN", NLocale.Chinese.name)
        assertEquals("en-US", NLocale.English.name)
        assertTrue(NLocales.all.first { it.name.startsWith("ar") }.isRtl)
        assertTrue(NLocales.all.first { it.name.startsWith("he") }.isRtl)
        assertTrue(!NLocales.ZhCN.isRtl)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun languagePickerSwitchesToChinese() = runComposeUiTest {
        setContent { App() }
        onAllNodesWithText("English")[0].performClick()
        waitUntil { onAllNodesWithText("简体中文").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("简体中文").performScrollTo().performClick()
        waitUntil { onAllNodesWithText("按钮 Button").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("按钮 Button")[0].assertExists()
        onNodeWithText("搜索组件").assertExists()
        onNodeWithText("通用组件").assertExists()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun languagePickerListsAllLanguages() = runComposeUiTest {
        setContent { App() }
        onAllNodesWithText("English")[0].performClick()
        waitUntil { onAllNodesWithText("日本語").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("日本語").performScrollTo().performClick()
        waitUntil { onAllNodesWithText("日本語").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("日本語").assertExists()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun languageDropdownStaysOpenWhileHoveringOptions() = runComposeUiTest {
        setContent { App() }
        onAllNodesWithText("English")[0].performClick()
        waitUntil { onAllNodesWithText("简体中文").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("简体中文").performScrollTo().performMouseInput {
            moveTo(Offset(4f, 4f))
        }
        mainClock.advanceTimeBy(600)
        onNodeWithText("简体中文").assertExists()
        onNodeWithText("简体中文").performClick()
        waitUntil { onAllNodesWithText("按钮 Button").fetchSemanticsNodes().isNotEmpty() }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun japaneseLocaleLocalizesComponents() = runComposeUiTest {
        setContent {
            NConfigProvider(locale = NLocales.JaJP) {
                NGlobalStyle {
                    androidx.compose.foundation.layout.Column {
                        NPagination(page = 1, pageCount = 10, onPageChange = {}, showQuickJumper = true)
                        NEmpty()
                    }
                }
            }
        }
        onNodeWithText("ページジャンプ").assertExists()
        onNodeWithText("データなし").assertExists()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun rightToLeftLocalesProvideRtlLayoutDirection() = runComposeUiTest {
        val arabic = NLocales.all.first { it.name.startsWith("ar") }
        var direction by mutableStateOf<LayoutDirection?>(null)
        setContent {
            NConfigProvider(locale = arabic, rtl = arabic.isRtl) {
                direction = LocalLayoutDirection.current
                NText("RTL")
            }
        }
        runOnIdle { assertEquals(LayoutDirection.Rtl, direction) }
    }
}
