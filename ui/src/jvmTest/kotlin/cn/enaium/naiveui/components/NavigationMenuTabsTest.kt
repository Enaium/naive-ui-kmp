package cn.enaium.naiveui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Behaviour tests for [NMenu] and [NTabs].
 */
@OptIn(ExperimentalTestApi::class)
class NavigationMenuTabsTest {
    private val menuOptions = listOf(
        NMenuOption(key = "home", label = "Home"),
        NMenuOption(
            key = "docs",
            label = "Docs",
            children = listOf(
                NMenuOption(key = "intro", label = "Introduction"),
                NMenuOption(key = "api", label = "API"),
            ),
        ),
        NMenuOption(key = "disabled", label = "Blocked", disabled = true),
    )

    @Test
    fun menuSelectsOption() = runComposeUiTest {
        var selected by mutableStateOf<String?>(null)
        setNContent {
            NMenu(
                options = menuOptions,
                value = selected,
                onValueChange = { selected = it },
            )
        }
        onNodeWithText("Home").performClick()
        runOnIdle { assertEquals("home", selected) }
    }

    @Test
    fun menuExpandsSubmenuAndSelectsChild() = runComposeUiTest {
        var selected by mutableStateOf<String?>(null)
        setNContent {
            NMenu(
                options = menuOptions,
                value = selected,
                onValueChange = { selected = it },
            )
        }
        onNodeWithText("Introduction").assertDoesNotExist()
        onNodeWithText("Docs").performClick()
        waitUntil { onAllNodesWithText("Introduction").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Introduction").performClick()
        runOnIdle { assertEquals("intro", selected) }
    }

    @Test
    fun menuIgnoresBlockedOption() = runComposeUiTest {
        var selected by mutableStateOf<String?>(null)
        setNContent {
            NMenu(
                options = menuOptions,
                value = selected,
                onValueChange = { selected = it },
            )
        }
        onNodeWithText("Blocked").performClick()
        runOnIdle { assertEquals(null, selected) }
    }

    @Test
    fun menuStateShowOptionExpandsAncestors() = runComposeUiTest {
        var selected by mutableStateOf<String?>(null)
        var state: NMenuState? = null
        setNContent {
            val menuState = rememberNMenuState()
            state = menuState
            NMenu(
                options = menuOptions,
                value = selected,
                onValueChange = { selected = it },
                state = menuState,
            )
        }
        onNodeWithText("API").assertDoesNotExist()
        runOnIdle { state!!.showOption("api") }
        waitUntil { onAllNodesWithText("API").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("API").performClick()
        runOnIdle { assertEquals("api", selected) }
    }

    @Test
    fun collapsedMenuOpensPopupWithChildren() = runComposeUiTest {
        var selected by mutableStateOf<String?>(null)
        setNContent {
            NMenu(
                options = menuOptions,
                value = selected,
                onValueChange = { selected = it },
                collapsed = true,
            )
        }
        // Collapsed rows render the first character of the label.
        onNodeWithText("D").performClick()
        waitUntil { onAllNodesWithText("Introduction").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Introduction").performClick()
        runOnIdle { assertEquals("intro", selected) }
    }

    private fun tabs() = listOf(
        NTabPane("basic", "Basic") { NText("Basic pane") },
        NTabPane("account", "Account") { NText("Account pane") },
        NTabPane("locked", "Locked", disabled = true) { NText("Locked pane") },
    )

    @Test
    fun tabsSwitchPanes() = runComposeUiTest {
        var value by mutableStateOf("basic")
        var reported: String? = null
        setNContent {
            NTabs(
                value = value,
                onValueChange = {
                    value = it
                    reported = it
                },
                panes = tabs(),
            )
        }
        onNodeWithText("Basic pane").assertExists()
        onNodeWithText("Account pane").assertDoesNotExist()

        onNodeWithText("Account").performClick()
        waitUntil { onAllNodesWithText("Account pane").fetchSemanticsNodes().isNotEmpty() }
        runOnIdle { assertEquals("account", reported) }
        onNodeWithText("Basic pane").assertDoesNotExist()
    }

    @Test
    fun tabsUncontrolledSelectionWorks() = runComposeUiTest {
        setNContent { NTabs(panes = tabs()) }
        onNodeWithText("Account pane").assertDoesNotExist()
        onNodeWithText("Account").performClick()
        waitUntil { onAllNodesWithText("Account pane").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Account pane").assertExists()
    }

    @Test
    fun tabsIgnoreDisabledTab() = runComposeUiTest {
        var value by mutableStateOf("basic")
        setNContent {
            NTabs(
                value = value,
                onValueChange = { value = it },
                panes = tabs(),
            )
        }
        onNodeWithText("Locked").performClick()
        runOnIdle { assertEquals("basic", value) }
        onNodeWithText("Basic pane").assertExists()
        onNodeWithText("Locked pane").assertDoesNotExist()
    }

    @Test
    fun tabsOnBeforeLeaveCanVetoSwitch() = runComposeUiTest {
        var value by mutableStateOf("basic")
        var vetoed = 0
        setNContent {
            NTabs(
                value = value,
                onValueChange = { value = it },
                onBeforeLeave = { _, _ ->
                    vetoed++
                    false
                },
                panes = tabs(),
            )
        }
        onNodeWithText("Account").performClick()
        runOnIdle { assertEquals("basic", value) }
        assertEquals(1, vetoed)
        onNodeWithText("Basic pane").assertExists()
        onNodeWithText("Account pane").assertDoesNotExist()
    }

    @Test
    fun tabsRenderContentPlacementAndPadding() = runComposeUiTest {
        setNContent {
            NTabs(
                placement = NTabsPlacement.Left,
                type = NTabsType.Line,
                panes = tabs(),
            )
        }
        onNodeWithText("Basic").assertExists()
        onNodeWithText("Account").assertExists()
        onAllNodesWithText("Basic pane").assertCountEquals(1)
    }
}
