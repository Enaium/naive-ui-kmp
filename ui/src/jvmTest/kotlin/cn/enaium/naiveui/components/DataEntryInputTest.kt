package cn.enaium.naiveui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class DataEntryInputTest {
    @Test
    fun inputAcceptsTypedText() = runComposeUiTest {
        var value by mutableStateOf("")
        setNContent {
            NInput(value = value, onValueChange = { value = it }, placeholder = "Name")
        }
        onNode(hasSetTextAction()).performTextInput("Hello")
        runOnIdle { assertEquals("Hello", value) }
    }

    @Test
    fun inputMaxlengthTruncates() = runComposeUiTest {
        var value by mutableStateOf("")
        setNContent {
            NInput(value = value, onValueChange = { value = it }, maxlength = 3)
        }
        onNode(hasSetTextAction()).performTextInput("abcdef")
        runOnIdle { assertEquals("abc", value) }
    }

    @Test
    fun inputAllowInputCanRejectText() = runComposeUiTest {
        var value by mutableStateOf("")
        setNContent {
            NInput(
                value = value,
                onValueChange = { value = it },
                allowInput = { !it.contains("x") },
            )
        }
        onNode(hasSetTextAction()).performTextInput("ax")
        runOnIdle { assertEquals("", value) }
    }

    @Test
    fun inputTrimStripsWhitespace() = runComposeUiTest {
        var value by mutableStateOf("")
        setNContent {
            NInput(value = value, onValueChange = { value = it }, trim = true)
        }
        onNode(hasSetTextAction()).performTextInput("  hi  ")
        runOnIdle { assertEquals("hi", value) }
    }

    @Test
    fun inputControllerClearEmptiesValue() = runComposeUiTest {
        val controller = NInputController()
        var cleared = false
        var value by mutableStateOf("abc")
        setNContent {
            NInput(
                value = value,
                onValueChange = { value = it },
                controller = controller,
                onClear = { cleared = true },
            )
        }
        runOnIdle { controller.clear() }
        waitForIdle()
        runOnIdle {
            assertEquals("", value)
            assertTrue(cleared)
        }
    }

    @Test
    fun inputDisabledIsNotEditable() = runComposeUiTest {
        var value by mutableStateOf("fixed")
        setNContent {
            NInput(value = value, onValueChange = { value = it }, disabled = true)
        }
        assertTrue(onAllNodes(hasSetTextAction()).fetchSemanticsNodes().isEmpty())
        runOnIdle { assertEquals("fixed", value) }
    }

    @Test
    fun inputEnterKeyInvokesCallback() = runComposeUiTest {
        var value by mutableStateOf("")
        var pressed = 0
        setNContent {
            NInput(
                value = value,
                onValueChange = { value = it },
                onPressEnter = { pressed++ },
            )
        }
        onNode(hasSetTextAction()).performTextInput("query")
        onNode(hasSetTextAction()).performKeyInput { pressKey(Key.Enter) }
        runOnIdle { assertEquals(1, pressed) }
    }

    @Test
    fun inputPairEditsBothSides() = runComposeUiTest {
        var value by mutableStateOf("a" to "b")
        setNContent {
            NInput(value = value, onValueChange = { value = it }, placeholders = "First" to "Second")
        }
        onAllNodes(hasSetTextAction())[0].performTextReplacement("start")
        runOnIdle { assertEquals("start" to "b", value) }
        onAllNodes(hasSetTextAction())[1].performTextReplacement("end")
        runOnIdle { assertEquals("start" to "end", value) }
    }

    @Test
    fun inputNumberTypingUpdatesValue() = runComposeUiTest {
        var value by mutableStateOf<Double?>(null)
        setNContent {
            NInputNumber(value = value, onValueChange = { value = it })
        }
        onNode(hasSetTextAction()).performTextInput("42")
        runOnIdle { assertEquals(42.0, value) }
    }

    @Test
    fun inputNumberAppliesPrecision() = runComposeUiTest {
        var value by mutableStateOf<Double?>(0.0)
        setNContent {
            NInputNumber(value = value, onValueChange = { value = it }, precision = 2)
        }
        onNode(hasSetTextAction()).performTextReplacement("1.23456")
        runOnIdle { assertEquals(1.23, assertNotNull(value), 1e-9) }
    }

    @Test
    fun inputNumberClampsToMin() = runComposeUiTest {
        var value by mutableStateOf<Double?>(0.0)
        setNContent {
            NInputNumber(value = value, onValueChange = { value = it }, min = 0.0)
        }
        onNode(hasSetTextAction()).performClick()
        onNode(hasSetTextAction()).performKeyInput { pressKey(Key.DirectionDown) }
        runOnIdle { assertEquals(0.0, value) }
    }

    @Test
    fun inputNumberArrowUpStepsByStep() = runComposeUiTest {
        var value by mutableStateOf<Double?>(0.0)
        setNContent {
            NInputNumber(value = value, onValueChange = { value = it }, step = 2.0, max = 10.0)
        }
        onNode(hasSetTextAction()).performClick()
        onNode(hasSetTextAction()).performKeyInput { pressKey(Key.DirectionUp) }
        runOnIdle { assertEquals(2.0, value) }
        onNode(hasSetTextAction()).performKeyInput { pressKey(Key.DirectionUp) }
        runOnIdle { assertEquals(4.0, value) }
    }

    @Test
    fun inputNumberCommitOnInputDisabled() = runComposeUiTest {
        var value by mutableStateOf<Double?>(null)
        setNContent {
            NInputNumber(
                value = value,
                onValueChange = { value = it },
                updateValueOnInput = false,
            )
        }
        onNode(hasSetTextAction()).performTextInput("5")
        runOnIdle { assertNull(value) }
        onNode(hasSetTextAction()).performKeyInput { pressKey(Key.Enter) }
        runOnIdle { assertEquals(5.0, value) }
    }

    @Test
    fun inputNumberValidatorRejectsValues() = runComposeUiTest {
        var value by mutableStateOf<Double?>(null)
        setNContent {
            NInputNumber(
                value = value,
                onValueChange = { value = it },
                validator = { it >= 0.0 },
            )
        }
        onNode(hasSetTextAction()).performTextInput("-5")
        runOnIdle { assertNull(value) }
        onNode(hasSetTextAction()).performTextReplacement("5")
        runOnIdle { assertEquals(5.0, value) }
    }

    @Test
    fun inputNumberDisabledIsNotEditable() = runComposeUiTest {
        var value by mutableStateOf<Double?>(3.0)
        setNContent {
            NInputNumber(value = value, onValueChange = { value = it }, disabled = true)
        }
        assertTrue(onAllNodes(hasSetTextAction()).fetchSemanticsNodes().isEmpty())
        runOnIdle { assertEquals(3.0, value) }
    }

    @Test
    fun inputOtpDistributesMultiCharInput() = runComposeUiTest {
        var value by mutableStateOf("")
        var finished: String? = null
        setNContent {
            NInputOtp(
                value = value,
                onValueChange = { value = it },
                length = 6,
                onFinish = { finished = it },
            )
        }
        onAllNodes(hasSetTextAction())[0].performTextInput("123456")
        runOnIdle {
            assertEquals("123456", value)
            assertEquals("123456", finished)
        }
    }

    @Test
    fun inputOtpStopsAtLength() = runComposeUiTest {
        var value by mutableStateOf("")
        setNContent {
            NInputOtp(value = value, onValueChange = { value = it }, length = 4)
        }
        onAllNodes(hasSetTextAction())[0].performTextInput("1234567890")
        runOnIdle { assertEquals("1234", value) }
    }

    @Test
    fun inputOtpAllowInputFiltersChars() = runComposeUiTest {
        var value by mutableStateOf("")
        setNContent {
            NInputOtp(
                value = value,
                onValueChange = { value = it },
                length = 4,
                allowInput = { char, _, _ -> char != "9" },
            )
        }
        onAllNodes(hasSetTextAction())[0].performTextInput("129")
        runOnIdle { assertEquals("12", value) }
    }

    @Test
    fun inputOtpDisabledCellsAreNotEditable() = runComposeUiTest {
        var value by mutableStateOf("12")
        setNContent {
            NInputOtp(value = value, onValueChange = { value = it }, length = 4, disabled = true)
        }
        assertTrue(onAllNodes(hasSetTextAction()).fetchSemanticsNodes().isEmpty())
        runOnIdle { assertEquals("12", value) }
    }

    @Test
    fun mentionSelectsOption() = runComposeUiTest {
        var value by mutableStateOf("")
        var selected: NMentionOption? = null
        setNContent {
            NMention(
                value = value,
                onValueChange = { value = it },
                options = listOf(NMentionOption("Alice"), NMentionOption("Bob")),
                placeholder = "Comment",
                onSelect = { selected = it },
            )
        }
        onNode(hasSetTextAction()).performTextInput("@")
        waitUntil { onAllNodesWithText("Alice").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("Alice")[0].performClick()
        runOnIdle {
            assertEquals("@Alice", value)
            assertEquals("Alice", selected?.value)
        }
    }

    @Test
    fun mentionFiltersBySearchText() = runComposeUiTest {
        var value by mutableStateOf("")
        setNContent {
            NMention(
                value = value,
                onValueChange = { value = it },
                options = listOf(NMentionOption("Alice"), NMentionOption("Bob")),
            )
        }
        onNode(hasSetTextAction()).performTextInput("@B")
        waitUntil { onAllNodesWithText("Bob").fetchSemanticsNodes().isNotEmpty() }
        assertTrue(onAllNodesWithText("Alice").fetchSemanticsNodes().isEmpty())
        onAllNodesWithText("Bob")[0].performClick()
        runOnIdle { assertEquals("@Bob", value) }
    }

    @Test
    fun mentionDoesNotOpenInsideWord() = runComposeUiTest {
        var value by mutableStateOf("")
        setNContent {
            NMention(
                value = value,
                onValueChange = { value = it },
                options = listOf(NMentionOption("Alice")),
            )
        }
        onNode(hasSetTextAction()).performTextInput("x@")
        waitForIdle()
        assertTrue(onAllNodesWithText("Alice").fetchSemanticsNodes().isEmpty())
    }

    @Test
    fun mentionDisabledIsNotEditable() = runComposeUiTest {
        var value by mutableStateOf("")
        setNContent {
            NMention(
                value = value,
                onValueChange = { value = it },
                options = listOf(NMentionOption("Alice")),
                disabled = true,
            )
        }
        assertTrue(onAllNodes(hasSetTextAction()).fetchSemanticsNodes().isEmpty())
        runOnIdle { assertEquals("", value) }
    }

    @Test
    fun autoCompleteSelectsFilteredOption() = runComposeUiTest {
        var value by mutableStateOf("")
        var selected: String? = null
        setNContent {
            NAutoComplete(
                value = value,
                onValueChange = { value = it },
                options = listOf(
                    NAutoCompleteOption("Alpha", "alpha"),
                    NAutoCompleteOption("Beta", "beta"),
                ),
                placeholder = "Search",
                onSelect = { selected = it },
            )
        }
        onNode(hasSetTextAction()).performTextInput("Al")
        waitUntil { onAllNodesWithText("Alpha").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("Alpha")[0].performClick()
        runOnIdle {
            assertEquals("alpha", value)
            assertEquals("alpha", selected)
        }
    }

    @Test
    fun autoCompleteClearAfterSelectClearsValue() = runComposeUiTest {
        var value by mutableStateOf("")
        setNContent {
            NAutoComplete(
                value = value,
                onValueChange = { value = it },
                options = listOf(NAutoCompleteOption("Alpha", "alpha")),
                clearAfterSelect = true,
            )
        }
        onNode(hasSetTextAction()).performTextInput("A")
        waitUntil { onAllNodesWithText("Alpha").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("Alpha")[0].performClick()
        runOnIdle { assertEquals("", value) }
    }

    @Test
    fun autoCompleteShowOptionsWhenCanHideMenu() = runComposeUiTest {
        var value by mutableStateOf("")
        setNContent {
            NAutoComplete(
                value = value,
                onValueChange = { value = it },
                options = listOf(NAutoCompleteOption("Alpha", "alpha")),
                showOptionsWhen = { false },
            )
        }
        onNode(hasSetTextAction()).performTextInput("a")
        waitForIdle()
        assertTrue(onAllNodesWithText("Alpha").fetchSemanticsNodes().isEmpty())
        runOnIdle { assertFalse(value.isEmpty()) }
    }

    @Test
    fun autoCompleteDisabledIsNotEditable() = runComposeUiTest {
        var value by mutableStateOf("")
        setNContent {
            NAutoComplete(
                value = value,
                onValueChange = { value = it },
                options = listOf(NAutoCompleteOption("Alpha", "alpha")),
                disabled = true,
            )
        }
        assertTrue(onAllNodes(hasSetTextAction()).fetchSemanticsNodes().isEmpty())
    }
}
