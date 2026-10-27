package cn.enaium.naiveui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class UtilityComponentTest {
    @Test
    fun collapseTransitionTogglesVisibility() = runComposeUiTest {
        var show by mutableStateOf(false)
        setNContent {
            NCollapseTransition(show = show) {
                NText("Transitional content")
            }
        }
        onNodeWithText("Transitional content").assertDoesNotExist()
        runOnIdle { show = true }
        waitUntil(timeoutMillis = 2_000) {
            onAllNodesWithText("Transitional content").fetchSemanticsNodes().isNotEmpty()
        }
        runOnIdle { show = false }
        waitUntil(timeoutMillis = 2_000) {
            onAllNodesWithText("Transitional content").fetchSemanticsNodes().isEmpty()
        }
    }

    @Test
    fun collapseTransitionHorizontalRendersContent() = runComposeUiTest {
        setNContent {
            NCollapseTransition(show = true, horizontal = true) {
                NText("Horizontal content")
            }
        }
        onNodeWithText("Horizontal content").assertExists()
    }

    @Test
    fun discreteMessageAppearsAndCanBeDismissed() = runComposeUiTest {
        lateinit var api: NDiscreteApi
        setNContent {
            val remembered = rememberNDiscreteApi()
            api = remembered
            NDiscreteApiHost(remembered) { NText("Host content") }
        }
        onNodeWithText("Host content").assertExists()
        runOnIdle { api.message.success("Saved successfully") }
        waitUntil { onAllNodesWithText("Saved successfully").fetchSemanticsNodes().isNotEmpty() }
        onAllNodes(hasClickAction())[0].performClick()
        waitUntil(timeoutMillis = 2_000) {
            onAllNodesWithText("Saved successfully").fetchSemanticsNodes().isEmpty()
        }
    }

    @Test
    fun discreteNotificationRendersTitleAndContent() = runComposeUiTest {
        lateinit var api: NDiscreteApi
        setNContent {
            val remembered = rememberNDiscreteApi()
            api = remembered
            NDiscreteApiHost(remembered) { NText("Notification host") }
        }
        runOnIdle { api.notification.warning("Heads up", "Notification body") }
        waitUntil { onAllNodesWithText("Heads up").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Notification body").assertExists()
    }

    @Test
    fun discreteDialogConfirms() = runComposeUiTest {
        lateinit var api: NDiscreteApi
        var confirmed by mutableStateOf(false)
        setNContent {
            val remembered = rememberNDiscreteApi()
            api = remembered
            NDiscreteApiHost(remembered) { NText("Dialog host") }
        }
        runOnIdle {
            api.dialog.create(
                title = "Delete item",
                content = "This cannot be undone",
                positiveText = "Confirm",
                negativeText = "Cancel",
                onPositive = { confirmed = true },
            )
        }
        waitUntil { onAllNodesWithText("Delete item").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("This cannot be undone").assertExists()
        onAllNodes(hasText("Confirm") and hasClickAction())[0].performClick()
        waitUntil { confirmed }
        waitUntil(timeoutMillis = 2_000) {
            onAllNodesWithText("Delete item").fetchSemanticsNodes().isEmpty()
        }
    }

    @Test
    fun scrollbarScrollsContent() = runComposeUiTest {
        var scrollState: ScrollState? = null
        setNContent {
            val state = rememberScrollState()
            scrollState = state
            NScrollbar(
                modifier = Modifier.height(100.dp),
                scrollState = state,
                alwaysShow = true,
            ) {
                Column {
                    repeat(30) { NText("Line $it") }
                }
            }
        }
        onNodeWithText("Line 0").assertExists()
        onNodeWithText("Line 25").performScrollTo()
        runOnIdle { assertTrue((scrollState?.value ?: 0) > 0) }
    }

    @Test
    fun virtualListRendersVisibleItemsAndScrolls() = runComposeUiTest {
        var listState: LazyListState? = null
        val items = (0 until 100).map { "Row $it" }
        setNContent {
            val state = rememberLazyListState()
            listState = state
            NVirtualList(
                items = items,
                modifier = Modifier.height(180.dp),
                state = state,
            ) { item -> NText(item) }
        }
        onNodeWithText("Row 0").assertExists()
        onNodeWithText("Row 50").assertDoesNotExist()
        onNode(hasScrollToIndexAction()).performScrollToIndex(50)
        waitUntil { onAllNodesWithText("Row 50").fetchSemanticsNodes().isNotEmpty() }
        runOnIdle { assertTrue((listState?.firstVisibleItemIndex ?: 0) >= 40) }
    }
}
