package cn.enaium.naiveui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.runComposeUiTest
import cn.enaium.naiveui.internal.NTrigger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Open/close and action tests for overlay feedback components.
 */
@OptIn(ExperimentalTestApi::class)
class FeedbackOverlayTest {
    @Test
    fun modalOpensAndClosesFromDialogPreset() = runComposeUiTest {
        var show by mutableStateOf(false)
        setNContent {
            Column {
                NButton(onClick = { show = true }) { NText("Open modal") }
                NModal(
                    show = show,
                    onUpdateShow = { show = it },
                    preset = NModalPreset.Dialog,
                    title = "Modal title",
                    contentText = "Modal body",
                    positiveText = "Close modal",
                )
            }
        }
        onNodeWithText("Open modal").performClick()
        waitUntil { onAllNodesWithText("Modal title").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Modal body").assertExists()
        onNodeWithText("Close modal").performClick()
        waitUntil { onAllNodesWithText("Modal title").fetchSemanticsNodes().isEmpty() }
        runOnIdle { assertFalse(show) }
    }

    @Test
    fun modalProviderCreatesAndClosesModal() = runComposeUiTest {
        var handle: NModalHandle? = null
        setNContent {
            NModalProvider {
                val modal = rememberNModal()
                NButton(
                    onClick = {
                        handle = modal.create(
                            title = "Provider modal",
                            contentText = "Created by API",
                            positiveText = "Dismiss",
                        )
                    },
                ) { NText("Create modal") }
            }
        }
        onNodeWithText("Create modal").performClick()
        waitUntil { onAllNodesWithText("Provider modal").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Created by API").assertExists()
        assertNotNull(handle)
        onNodeWithText("Dismiss").performClick()
        waitUntil { onAllNodesWithText("Provider modal").fetchSemanticsNodes().isEmpty() }
    }

    @Test
    fun dialogProviderConfirms() = runComposeUiTest {
        var confirmed by mutableStateOf(false)
        setNContent {
            NDialogProvider {
                val dialog = rememberNDialog()
                NButton(
                    onClick = {
                        dialog.create(
                            title = "Delete item",
                            content = "This cannot be undone.",
                            positiveText = "Delete",
                            negativeText = "Keep",
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
    fun dialogProviderCancels() = runComposeUiTest {
        var cancelled by mutableStateOf(false)
        setNContent {
            NDialogProvider {
                val dialog = rememberNDialog()
                NButton(
                    onClick = {
                        dialog.create(
                            title = "Remove file",
                            content = "The file will be removed.",
                            positiveText = "Remove",
                            negativeText = "Cancel",
                            onNegativeClick = { cancelled = true },
                        )
                    },
                ) { NText("Prompt") }
            }
        }
        onNodeWithText("Prompt").performClick()
        waitUntil { onAllNodesWithText("Remove file").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Cancel").performClick()
        waitUntil { onAllNodesWithText("Remove file").fetchSemanticsNodes().isEmpty() }
        runOnIdle { assertTrue(cancelled) }
    }

    @Test
    fun dialogInfoUsesDefaultPositiveTextAndCloses() = runComposeUiTest {
        setNContent {
            NDialogProvider {
                val dialog = rememberNDialog()
                NButton(onClick = { dialog.info(title = "Info dialog", content = "Info body") }) {
                    NText("Show info")
                }
            }
        }
        onNodeWithText("Show info").performClick()
        waitUntil { onAllNodesWithText("Info dialog").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Info body").assertExists()
        onNodeWithText("OK").performClick()
        waitUntil { onAllNodesWithText("Info dialog").fetchSemanticsNodes().isEmpty() }
    }

    @Test
    fun dialogHandleContentIsReactive() = runComposeUiTest {
        var handle: NDialogHandle? = null
        setNContent {
            NDialogProvider {
                val dialog = rememberNDialog()
                NButton(
                    onClick = {
                        handle = dialog.create(
                            title = "Working",
                            content = "Step 1",
                            positiveText = "Done",
                        )
                    },
                ) { NText("Start work") }
            }
        }
        onNodeWithText("Start work").performClick()
        waitUntil { onAllNodesWithText("Step 1").fetchSemanticsNodes().isNotEmpty() }
        runOnIdle { handle!!.content = "Step 2" }
        waitUntil { onAllNodesWithText("Step 2").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Step 1").assertDoesNotExist()
    }

    @Test
    fun drawerClosesOnMaskClick() = runComposeUiTest {
        var show by mutableStateOf(true)
        setNContent {
            NDrawer(
                show = show,
                onUpdateShow = { show = it },
                title = "Drawer title",
                embedded = true,
            ) {
                NText("Drawer body")
            }
        }
        onNodeWithText("Drawer title").assertExists()
        onNodeWithText("Drawer body").assertExists()

        // The drawer is anchored on the right, so the top-left corner is scrim.
        onRoot().performMouseInput { click(Offset(4f, 4f)) }
        waitUntil { onAllNodesWithText("Drawer body").fetchSemanticsNodes().isEmpty() }
        runOnIdle { assertFalse(show) }
    }

    @Test
    fun drawerFooterCanCloseIt() = runComposeUiTest {
        var show by mutableStateOf(false)
        setNContent {
            Box(Modifier.fillMaxSize()) {
                NButton(onClick = { show = true }) { NText("Open drawer") }
                NDrawer(
                    show = show,
                    onUpdateShow = { show = it },
                    title = "Settings",
                    embedded = true,
                    footer = {
                        NButton(onClick = { show = false }) { NText("Close drawer") }
                    },
                ) {
                    NText("Drawer content")
                }
            }
        }
        onNodeWithText("Open drawer").performClick()
        waitUntil { onAllNodesWithText("Settings").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Drawer content").assertExists()
        onNodeWithText("Close drawer").performClick()
        waitUntil { onAllNodesWithText("Settings").fetchSemanticsNodes().isEmpty() }
        runOnIdle { assertFalse(show) }
    }

    @Test
    fun popconfirmConfirmsAndCloses() = runComposeUiTest {
        var confirmed by mutableStateOf(0)
        setNContent {
            NPopconfirm(
                text = "Are you sure?",
                positiveText = "Yes",
                negativeText = "No",
                onPositiveClick = { confirmed++ },
            ) {
                NButton { NText("Remove") }
            }
        }
        onNodeWithText("Remove").performClick()
        waitUntil { onAllNodesWithText("Are you sure?").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Yes").performClick()
        runOnIdle { assertEquals(1, confirmed) }
        waitUntil { onAllNodesWithText("Are you sure?").fetchSemanticsNodes().isEmpty() }
    }

    @Test
    fun popconfirmCancelsAndCloses() = runComposeUiTest {
        var cancelled by mutableStateOf(0)
        var confirmed by mutableStateOf(0)
        setNContent {
            NPopconfirm(
                text = "Delete this?",
                positiveText = "Delete",
                negativeText = "Cancel",
                onPositiveClick = { confirmed++ },
                onNegativeClick = { cancelled++ },
            ) {
                NButton { NText("Ask delete") }
            }
        }
        onNodeWithText("Ask delete").performClick()
        waitUntil { onAllNodesWithText("Delete this?").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Cancel").performClick()
        runOnIdle {
            assertEquals(1, cancelled)
            assertEquals(0, confirmed)
        }
        waitUntil { onAllNodesWithText("Delete this?").fetchSemanticsNodes().isEmpty() }
    }

    @Test
    fun popoverOpensAndClosesOnOutsideClick() = runComposeUiTest {
        setNContent {
            Column {
                NPopover(
                    trigger = NTrigger.Click,
                    content = { NText("Popover body") },
                ) {
                    NButton { NText("Show popover") }
                }
                NButton { NText("Elsewhere") }
            }
        }
        onNodeWithText("Popover body").assertDoesNotExist()
        onNodeWithText("Show popover").performClick()
        waitUntil { onAllNodesWithText("Popover body").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Popover body").assertExists()

        onNodeWithText("Elsewhere").performClick()
        waitUntil { onAllNodesWithText("Popover body").fetchSemanticsNodes().isEmpty() }
    }

    @Test
    fun popoverControlledShowRendersContent() = runComposeUiTest {
        setNContent {
            NPopover(
                show = true,
                trigger = NTrigger.Manual,
                content = { NText("Controlled popover") },
            ) {
                NText("Anchor")
            }
        }
        onNodeWithText("Anchor").assertExists()
        onNodeWithText("Controlled popover").assertExists()
    }

    @Test
    fun popselectSelectsSingleOption() = runComposeUiTest {
        var value by mutableStateOf<String?>(null)
        setNContent {
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
        waitUntil { onAllNodesWithText("Second").fetchSemanticsNodes().size == 1 }
    }

    @Test
    fun popselectSupportsMultipleValues() = runComposeUiTest {
        var values by mutableStateOf(listOf<String>())
        setNContent {
            NPopselect(
                options = listOf(
                    NPopselectOption("First", "first"),
                    NPopselectOption("Second", "second"),
                ),
                multiple = true,
                values = values,
                onValuesChange = { values = it },
                placeholder = "Choose many",
            )
        }
        onNodeWithText("Choose many").performClick()
        waitUntil { onAllNodesWithText("First").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("First")[0].performClick()
        runOnIdle { assertEquals(listOf("first"), values) }
        onAllNodesWithText("Second")[0].performClick()
        runOnIdle { assertEquals(listOf("first", "second"), values) }
    }

    @Test
    fun popselectIgnoresDisabledOption() = runComposeUiTest {
        var value by mutableStateOf<String?>(null)
        setNContent {
            NPopselect(
                options = listOf(
                    NPopselectOption("Blocked", "blocked", disabled = true),
                    NPopselectOption("Allowed", "allowed"),
                ),
                value = value,
                onValueChange = { value = it },
                placeholder = "Pick",
            )
        }
        onNodeWithText("Pick").performClick()
        waitUntil { onAllNodesWithText("Blocked").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Blocked").performClick()
        runOnIdle { assertNull(value) }
        onNodeWithText("Allowed").performClick()
        runOnIdle { assertEquals("allowed", value) }
    }

    @Test
    fun tooltipOpensOnClickAndCloses() = runComposeUiTest {
        setNContent {
            Column {
                NTooltip(text = "Tooltip text", trigger = NTrigger.Click) {
                    NButton { NText("Hover me") }
                }
                NButton { NText("Outside") }
            }
        }
        onNodeWithText("Tooltip text").assertDoesNotExist()
        onNodeWithText("Hover me").performClick()
        waitUntil { onAllNodesWithText("Tooltip text").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Tooltip text").assertExists()

        onNodeWithText("Outside").performClick()
        waitUntil { onAllNodesWithText("Tooltip text").fetchSemanticsNodes().isEmpty() }
    }

    @Test
    fun disabledTooltipNeverShows() = runComposeUiTest {
        setNContent {
            NTooltip(text = "Hidden hint", trigger = NTrigger.Click, disabled = true) {
                NButton { NText("Disabled tooltip anchor") }
            }
        }
        onNodeWithText("Disabled tooltip anchor").performClick()
        waitForIdle()
        onNodeWithText("Hidden hint").assertDoesNotExist()
    }
}
