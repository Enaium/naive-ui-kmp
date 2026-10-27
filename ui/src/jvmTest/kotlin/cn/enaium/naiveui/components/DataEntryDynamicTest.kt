package cn.enaium.naiveui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class DataEntryDynamicTest {
    @Test
    fun dynamicInputCreateAddsRow() = runComposeUiTest {
        var values by mutableStateOf(emptyList<String>())
        setNContent {
            NDynamicInput(
                values = values,
                onValueChange = { values = it },
                onCreate = { "row" },
            )
        }
        onNodeWithText("Create").performClick()
        runOnIdle { assertEquals(listOf("row"), values) }
        onNode(hasSetTextAction()).assertExists()
    }

    @Test
    fun dynamicInputEditUpdatesIndex() = runComposeUiTest {
        var values by mutableStateOf(listOf("a", "b"))
        setNContent {
            NDynamicInput(values = values, onValueChange = { values = it })
        }
        onAllNodes(hasSetTextAction())[0].performTextReplacement("x")
        runOnIdle { assertEquals(listOf("x", "b"), values) }
    }

    @Test
    fun dynamicInputDisabledDoesNotCreate() = runComposeUiTest {
        var values by mutableStateOf(emptyList<String>())
        setNContent {
            NDynamicInput(
                values = values,
                onValueChange = { values = it },
                disabled = true,
            )
        }
        onNodeWithText("Create").performClick()
        runOnIdle { assertTrue(values.isEmpty()) }
    }

    @Test
    fun dynamicInputMaxBlocksCreate() = runComposeUiTest {
        var values by mutableStateOf(emptyList<String>())
        setNContent {
            NDynamicInput(
                values = values,
                onValueChange = { values = it },
                max = 0,
            )
        }
        onNodeWithText("Create").performClick()
        runOnIdle { assertTrue(values.isEmpty()) }
    }

    @Test
    fun dynamicInputPairPresetEditsValue() = runComposeUiTest {
        var values by mutableStateOf(listOf(NDynamicInputPair("k", "v")))
        setNContent {
            NDynamicInput(
                values = values,
                onValueChange = { values = it },
                preset = NDynamicInputPreset.Pair,
                keyPlaceholder = "Key",
                valuePlaceholder = "Value",
            )
        }
        onAllNodes(hasSetTextAction())[1].performTextReplacement("v2")
        runOnIdle { assertEquals(listOf(NDynamicInputPair("k", "v2")), values) }
    }

    @Test
    fun dynamicTagsAddsTagOnEnter() = runComposeUiTest {
        var tags by mutableStateOf(emptyList<String>())
        setNContent {
            NDynamicTags(value = tags, onValueChange = { tags = it })
        }
        onNode(hasClickAction()).performClick()
        waitForIdle()
        onNode(hasSetTextAction()).performTextInput("Kotlin")
        onNode(hasSetTextAction()).performKeyInput { pressKey(Key.Enter) }
        runOnIdle { assertEquals(listOf("Kotlin"), tags) }
        onNodeWithText("Kotlin").assertExists()
    }

    @Test
    fun dynamicTagsMaxBlocksInput() = runComposeUiTest {
        var tags by mutableStateOf(emptyList<String>())
        setNContent {
            NDynamicTags(value = tags, onValueChange = { tags = it }, max = 0)
        }
        onNode(hasClickAction()).performClick()
        waitForIdle()
        assertTrue(onAllNodes(hasSetTextAction()).fetchSemanticsNodes().isEmpty())
        runOnIdle { assertTrue(tags.isEmpty()) }
    }

    @Test
    fun dynamicTagsDisabledBlocksInput() = runComposeUiTest {
        var tags by mutableStateOf(emptyList<String>())
        setNContent {
            NDynamicTags(value = tags, onValueChange = { tags = it }, disabled = true)
        }
        onNode(hasClickAction()).performClick()
        waitForIdle()
        assertTrue(onAllNodes(hasSetTextAction()).fetchSemanticsNodes().isEmpty())
        runOnIdle { assertTrue(tags.isEmpty()) }
    }

    @Test
    fun dynamicTagsRendersLabels() = runComposeUiTest {
        var tags by mutableStateOf(listOf("Kotlin", "Compose"))
        setNContent {
            NDynamicTags(value = tags, onValueChange = { tags = it })
        }
        onNodeWithText("Kotlin").assertExists()
        onNodeWithText("Compose").assertExists()
    }
}
