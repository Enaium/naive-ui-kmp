package cn.enaium.naiveui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Behaviour tests for inline feedback components.
 */
@OptIn(ExperimentalTestApi::class)
class FeedbackStaticTest {
    @Test
    fun alertRendersContentAndCloses() = runComposeUiTest {
        var closed by mutableStateOf(0)
        setNContent {
            NAlert(
                type = NAlertType.Warning,
                title = "Heads up",
                closable = true,
                onClose = { closed++ },
            ) {
                NText("Disk almost full")
            }
        }
        onNodeWithText("Heads up").assertExists()
        onNodeWithText("Disk almost full").assertExists()

        // The only clickable node inside the alert is its close affordance.
        onAllNodes(hasClickAction())[0].performClick()
        runOnIdle { assertEquals(1, closed) }
        onNodeWithText("Heads up").assertDoesNotExist()
    }

    @Test
    fun alertRendersActionSlot() = runComposeUiTest {
        var actions by mutableStateOf(0)
        setNContent {
            NAlert(
                type = NAlertType.Info,
                title = "Update available",
                action = {
                    NButton(onClick = { actions++ }) { NText("Install") }
                },
            ) {
                NText("Version 2 is ready")
            }
        }
        onNodeWithText("Update available").assertExists()
        onNodeWithText("Install").performClick()
        runOnIdle { assertEquals(1, actions) }
    }

    @Test
    fun badgeShowsValueAndMax() = runComposeUiTest {
        setNContent {
            Column {
                NBadge(value = 5) { NText("Inbox") }
                NBadge(value = 120, max = 99) { NText("Alerts") }
            }
        }
        onNodeWithText("5").assertExists()
        onNodeWithText("99+").assertExists()
        onNodeWithText("Inbox").assertExists()
        onNodeWithText("Alerts").assertExists()
    }

    @Test
    fun badgeHonoursShowZeroShowAndCustomContent() = runComposeUiTest {
        setNContent {
            Column {
                NBadge(value = 0) { NText("Zero hidden") }
                NBadge(value = 0, showZero = true) { NText("Zero shown") }
                NBadge(value = 7, show = false) { NText("Badge hidden") }
                NBadge(valueContent = { NText("new") }) { NText("Updates") }
            }
        }
        onAllNodesWithText("0").assertCountEquals(1)
        onNodeWithText("7").assertDoesNotExist()
        onNodeWithText("new").assertExists()
    }

    @Test
    fun badgeDotRendersWithoutText() = runComposeUiTest {
        setNContent {
            NBadge(dot = true, value = 4) { NText("Status") }
        }
        onNodeWithText("Status").assertExists()
        onNodeWithText("4").assertDoesNotExist()
    }

    @Test
    fun marqueeDuplicatesContent() = runComposeUiTest {
        setNContent {
            NMarquee(items = listOf("Alpha", "Beta"))
        }
        onAllNodesWithText("Alpha").assertCountEquals(2)
        onAllNodesWithText("Beta").assertCountEquals(2)
    }

    @Test
    fun progressShowsPercentageAndClamps() = runComposeUiTest {
        setNContent {
            Column {
                NProgress(percentage = 42f)
                NProgress(percentage = 150f)
                NProgress(percentage = 50f, unit = " / 100")
            }
        }
        onNodeWithText("42%").assertExists()
        onNodeWithText("100%").assertExists()
        onNodeWithText("50 / 100").assertExists()
    }

    @Test
    fun progressSupportsCircleAndCustomIndicator() = runComposeUiTest {
        setNContent {
            Column {
                NProgress(type = NProgressType.Circle, percentage = 75f)
                NProgress(percentage = 33f, indicator = { NText("custom indicator") })
            }
        }
        onNodeWithText("75%").assertExists()
        onNodeWithText("custom indicator").assertExists()
        onNodeWithText("33%").assertDoesNotExist()
    }

    @Test
    fun progressCanHideIndicator() = runComposeUiTest {
        setNContent {
            NProgress(percentage = 42f, showIndicator = false)
        }
        onNodeWithText("42%").assertDoesNotExist()
    }

    @Test
    fun resultRendersStatusTitleDescriptionAndExtra() = runComposeUiTest {
        var actions by mutableStateOf(0)
        setNContent {
            NResult(
                status = NResultStatus.Success,
                title = "Payment complete",
                description = "Your order has been confirmed",
                extra = {
                    NButton(onClick = { actions++ }) { NText("View order") }
                },
            ) {
                NText("Order #42")
            }
        }
        onNodeWithText("Payment complete").assertExists()
        onNodeWithText("Your order has been confirmed").assertExists()
        onNodeWithText("Order #42").assertExists()
        onNodeWithText("View order").performClick()
        runOnIdle { assertEquals(1, actions) }
    }

    @Test
    fun resultRendersErrorStatus() = runComposeUiTest {
        setNContent {
            NResult(
                status = NResultStatus.Error,
                title = "Something went wrong",
                description = "Try again later",
            )
        }
        onNodeWithText("Something went wrong").assertExists()
        onNodeWithText("Try again later").assertExists()
    }

    @Test
    fun skeletonRendersAlongsideContent() = runComposeUiTest {
        setNContent {
            Column {
                NSkeleton(type = NSkeletonType.Text, repeat = 3, animated = false)
                NSkeletonItem(type = NSkeletonType.Circle, width = 40.dp, height = 40.dp, animated = false)
                NText("Content loaded")
            }
        }
        onAllNodesWithText("Content loaded").assertCountEquals(1)
    }

    @Test
    fun spinShowsDescriptionAndContentThenHidesOverlay() = runComposeUiTest {
        var show by mutableStateOf(true)
        setNContent {
            NSpin(show = show, description = "Loading data") {
                NText("Body content")
            }
        }
        onNodeWithText("Loading data").assertExists()
        onNodeWithText("Body content").assertExists()

        runOnIdle { show = false }
        waitUntil { onAllNodesWithText("Loading data").fetchSemanticsNodes().isEmpty() }
        onNodeWithText("Body content").assertExists()
    }

    @Test
    fun spinWithoutContentShowsDescription() = runComposeUiTest {
        setNContent {
            NSpin(description = "Fetching", rotate = false)
        }
        onNodeWithText("Fetching").assertExists()
    }
}
