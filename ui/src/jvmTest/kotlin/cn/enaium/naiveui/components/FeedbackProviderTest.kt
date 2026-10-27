package cn.enaium.naiveui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Imperative provider API tests for messages and notifications.
 */
@OptIn(ExperimentalTestApi::class)
class FeedbackProviderTest {
    @Test
    fun messageProviderShowsAndHandleCloses() = runComposeUiTest {
        var handle: NMessageHandle? = null
        setNContent {
            NMessageProvider {
                val message = rememberNMessage()
                NButton(
                    onClick = { handle = message.success("Saved successfully", duration = 0) },
                ) { NText("Show message") }
            }
        }
        onNodeWithText("Show message").performClick()
        waitUntil { onAllNodesWithText("Saved successfully").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Saved successfully").assertExists()
        assertNotNull(handle)

        runOnIdle { handle!!.close() }
        waitUntil { onAllNodesWithText("Saved successfully").fetchSemanticsNodes().isEmpty() }
        onNodeWithText("Saved successfully").assertDoesNotExist()
    }

    @Test
    fun messageHandleUpdatesTextAndType() = runComposeUiTest {
        var handle: NMessageHandle? = null
        setNContent {
            NMessageProvider {
                val message = rememberNMessage()
                NButton(onClick = { handle = message.loading("Working", duration = 0) }) {
                    NText("Start loading")
                }
            }
        }
        onNodeWithText("Start loading").performClick()
        waitUntil { onAllNodesWithText("Working").fetchSemanticsNodes().isNotEmpty() }

        runOnIdle {
            handle!!.text = "Still working"
            handle!!.type = NMessageType.Success
        }
        waitUntil { onAllNodesWithText("Still working").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Working").assertDoesNotExist()
    }

    @Test
    fun messageDestroyAllClearsMessages() = runComposeUiTest {
        setNContent {
            NMessageProvider {
                val message = rememberNMessage()
                Column {
                    NButton(
                        onClick = {
                            message.success("First message", duration = 0)
                            message.info("Second message", duration = 0)
                        },
                    ) { NText("Show messages") }
                    NButton(onClick = { message.destroyAll() }) { NText("Clear messages") }
                }
            }
        }
        onNodeWithText("Show messages").performClick()
        waitUntil { onAllNodesWithText("First message").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Second message").assertExists()

        onNodeWithText("Clear messages").performClick()
        waitUntil { onAllNodesWithText("First message").fetchSemanticsNodes().isEmpty() }
        waitUntil { onAllNodesWithText("Second message").fetchSemanticsNodes().isEmpty() }
    }

    @Test
    fun messageCustomContentRenders() = runComposeUiTest {
        setNContent {
            NMessageProvider {
                val message = rememberNMessage()
                NButton(
                    onClick = {
                        message.custom(duration = 0) { NText("Custom message body") }
                    },
                ) { NText("Show custom") }
            }
        }
        onNodeWithText("Show custom").performClick()
        waitUntil { onAllNodesWithText("Custom message body").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Custom message body").assertExists()
    }

    @Test
    fun notificationProviderShowsAndActionCloses() = runComposeUiTest {
        var actions by mutableStateOf(0)
        setNContent {
            NNotificationProvider {
                val notification = rememberNNotification()
                NButton(
                    onClick = {
                        notification.create(
                            NNotificationOptions(
                                title = "Sync complete",
                                content = "All files are synced",
                                type = NNotificationType.Success,
                                duration = 0,
                                actionText = "Undo",
                                onAction = { actions++ },
                            ),
                        )
                    },
                ) { NText("Notify") }
            }
        }
        onNodeWithText("Notify").performClick()
        waitUntil { onAllNodesWithText("Sync complete").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("All files are synced").assertExists()

        onNodeWithText("Undo").performClick()
        runOnIdle { assertEquals(1, actions) }
        waitUntil { onAllNodesWithText("Sync complete").fetchSemanticsNodes().isEmpty() }
    }

    @Test
    fun notificationHandleUpdatesContentAndCloses() = runComposeUiTest {
        var handle: NNotificationHandle? = null
        setNContent {
            NNotificationProvider {
                val notification = rememberNNotification()
                NButton(
                    onClick = {
                        handle = notification.create(
                            title = "Update available",
                            content = "Version 1",
                            duration = 0,
                        )
                    },
                ) { NText("Show notification") }
            }
        }
        onNodeWithText("Show notification").performClick()
        waitUntil { onAllNodesWithText("Version 1").fetchSemanticsNodes().isNotEmpty() }
        assertNotNull(handle)

        runOnIdle { handle!!.content = "Version 2" }
        waitUntil { onAllNodesWithText("Version 2").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Version 1").assertDoesNotExist()

        runOnIdle { handle!!.close() }
        waitUntil { onAllNodesWithText("Update available").fetchSemanticsNodes().isEmpty() }
    }

    @Test
    fun notificationDestroyAllClearsCards() = runComposeUiTest {
        setNContent {
            NNotificationProvider {
                val notification = rememberNNotification()
                Column {
                    NButton(
                        onClick = {
                            notification.success("First notice", duration = 0)
                            notification.warning("Second notice", duration = 0)
                        },
                    ) { NText("Show notices") }
                    NButton(onClick = { notification.destroyAll() }) { NText("Clear notices") }
                }
            }
        }
        onNodeWithText("Show notices").performClick()
        waitUntil { onAllNodesWithText("First notice").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Second notice").assertExists()

        onNodeWithText("Clear notices").performClick()
        waitUntil { onAllNodesWithText("First notice").fetchSemanticsNodes().isEmpty() }
        waitUntil { onAllNodesWithText("Second notice").fetchSemanticsNodes().isEmpty() }
    }
}
