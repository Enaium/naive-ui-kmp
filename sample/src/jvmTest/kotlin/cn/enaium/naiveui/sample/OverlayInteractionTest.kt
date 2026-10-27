package cn.enaium.naiveui.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import cn.enaium.naiveui.components.NColorPicker
import cn.enaium.naiveui.components.NDialogProvider
import cn.enaium.naiveui.components.NDrawer
import cn.enaium.naiveui.components.NNotificationProvider
import cn.enaium.naiveui.components.NPopconfirm
import cn.enaium.naiveui.components.NPopselect
import cn.enaium.naiveui.components.NPopselectOption
import cn.enaium.naiveui.components.NTooltip
import cn.enaium.naiveui.components.rememberNDialog
import cn.enaium.naiveui.components.rememberNNotification
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.internal.NTrigger
import cn.enaium.naiveui.theme.NConfigProvider
import cn.enaium.naiveui.theme.NGlobalStyle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class OverlayInteractionTest {
    private fun ComposeUiTest.setTestContent(content: @Composable () -> Unit) {
        setContent {
            NConfigProvider {
                NGlobalStyle { content() }
            }
        }
    }

    @Test
    fun notificationProviderShowsNotification() = runComposeUiTest {
        setTestContent {
            NNotificationProvider {
                val notification = rememberNNotification()
                NButton(onClick = { notification.success("Saved", "Everything is up to date") }) {
                    NText("Notify")
                }
            }
        }
        onNodeWithText("Notify").performClick()
        waitUntil { onAllNodesWithText("Saved").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Saved").assertExists()
        onNodeWithText("Everything is up to date").assertExists()
    }

    @Test
    fun dialogProviderOpensAndConfirms() = runComposeUiTest {
        var confirmed = false
        setTestContent {
            NDialogProvider {
                val dialog = rememberNDialog()
                NButton(
                    onClick = {
                        dialog.create(
                            title = "Delete item",
                            content = "This cannot be undone.",
                            positiveText = "Delete",
                            onPositiveClick = { confirmed = true },
                        )
                    },
                ) { NText("Ask") }
            }
        }
        onNodeWithText("Ask").performClick()
        waitUntil { onAllNodesWithText("Delete item").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("This cannot be undone.").assertExists()
        onNodeWithText("Delete").performClick()
        waitUntil { onAllNodesWithText("Delete item").fetchSemanticsNodes().isEmpty() }
        runOnIdle { assertTrue(confirmed) }
    }

    @Test
    fun drawerOpens() = runComposeUiTest {
        var show by mutableStateOf(false)
        setTestContent {
            Column {
                NButton(onClick = { show = true }) { NText("Open drawer") }
                NDrawer(show = show, onUpdateShow = { show = it }, title = "Drawer title") {
                    NText("Drawer body")
                }
            }
        }
        onNodeWithText("Open drawer").performClick()
        waitUntil { onAllNodesWithText("Drawer title").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Drawer body").assertExists()
    }

    @Test
    fun popconfirmConfirms() = runComposeUiTest {
        var confirmed = false
        setTestContent {
            NPopconfirm(
                text = "Are you sure?",
                positiveText = "Yes",
                negativeText = "No",
                onPositiveClick = { confirmed = true },
            ) {
                NButton { NText("Delete") }
            }
        }
        onNodeWithText("Delete").performClick()
        waitUntil { onAllNodesWithText("Are you sure?").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Yes").performClick()
        runOnIdle { assertTrue(confirmed) }
        waitUntil { onAllNodesWithText("Are you sure?").fetchSemanticsNodes().isEmpty() }
    }

    @Test
    fun popselectSelectsOption() = runComposeUiTest {
        var value by mutableStateOf<String?>(null)
        setTestContent {
            NPopselect(
                options = listOf(
                    NPopselectOption("First", "first"),
                    NPopselectOption("Second", "second"),
                ),
                value = value,
                onValueChange = { value = it },
                placeholder = "Choose",
            )
        }
        onNodeWithText("Choose").performClick()
        waitUntil { onAllNodesWithText("Second").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Second").performClick()
        runOnIdle { assertEquals("second", value) }
    }

    @Test
    fun tooltipShowsOnClick() = runComposeUiTest {
        setTestContent {
            NTooltip(text = "Tooltip text", trigger = NTrigger.Click) {
                NButton { NText("Trigger") }
            }
        }
        onNodeWithText("Trigger").performClick()
        waitUntil { onAllNodesWithText("Tooltip text").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Tooltip text").assertExists()
    }

    @Test
    fun colorPickerPanelOpens() = runComposeUiTest {
        var value by mutableStateOf(Color(0xFF18A058))
        setTestContent {
            NColorPicker(
                value = value,
                onValueChange = { value = it },
                show = true,
                showAlpha = false,
                swatches = listOf(Color.Red, Color.Blue),
            )
        }
        onNodeWithText("HEX").assertExists()
        onNodeWithText("RGB").assertExists()
    }
}
