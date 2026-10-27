package cn.enaium.naiveui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class DataEntryPickerTest {
    @Test
    fun datePickerSelectsDay() = runComposeUiTest {
        var value by mutableStateOf<LocalDate?>(LocalDate(2024, 1, 1))
        setNContent {
            NDatePicker(
                value = value,
                onValueChange = { value = it },
                format = "yyyy-MM-dd",
            )
        }
        onNodeWithText("2024-01-01").performClick()
        waitUntil { onAllNodesWithText("15").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("15")[0].performClick()
        runOnIdle { assertEquals(LocalDate(2024, 1, 15), value) }
    }

    @Test
    fun datePickerMinDisablesEarlierDays() = runComposeUiTest {
        var value by mutableStateOf<LocalDate?>(LocalDate(2024, 1, 15))
        setNContent {
            NDatePicker(
                value = value,
                onValueChange = { value = it },
                format = "yyyy-MM-dd",
                min = LocalDate(2024, 1, 10),
            )
        }
        onNodeWithText("2024-01-15").performClick()
        waitUntil { onAllNodesWithText("20").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("20")[0].performClick()
        runOnIdle { assertEquals(LocalDate(2024, 1, 20), value) }
        onNodeWithText("2024-01-20").performClick()
        waitUntil { onAllNodesWithText("5").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("5")[0].performClick()
        runOnIdle { assertEquals(LocalDate(2024, 1, 20), value) }
    }

    @Test
    fun datePickerMonthTypeSelectsMonth() = runComposeUiTest {
        var value by mutableStateOf<LocalDate?>(LocalDate(2024, 1, 1))
        setNContent {
            NDatePicker(
                value = value,
                onValueChange = { value = it },
                type = NDatePickerType.Month,
            )
        }
        onNodeWithText("2024-01").performClick()
        waitUntil { onAllNodesWithText("Mar").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("Mar")[0].performClick()
        runOnIdle { assertEquals(LocalDate(2024, 3, 1), value) }
    }

    @Test
    fun datePickerPlaceholderOpensPanel() = runComposeUiTest {
        var value by mutableStateOf<LocalDate?>(null)
        setNContent {
            NDatePicker(value = value, onValueChange = { value = it })
        }
        onNodeWithText("Select Date").assertExists()
        onNodeWithText("Select Date").performClick()
        waitUntil { onAllNodesWithText("Now").fetchSemanticsNodes().isNotEmpty() }
        runOnIdle { assertNull(value) }
    }

    @Test
    fun datePickerRangeShortcutCommitsPair() = runComposeUiTest {
        var value by mutableStateOf<Pair<LocalDate, LocalDate>?>(null)
        setNContent {
            NDatePicker(
                value = value,
                onValueChange = { value = it },
                shortcuts = { select ->
                    NButton(onClick = { select(LocalDate(2024, 1, 1) to LocalDate(2024, 1, 31)) }) {
                        NText("This month")
                    }
                },
            )
        }
        onNodeWithText("Start Date").performClick()
        waitUntil { onAllNodesWithText("This month").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("This month").performClick()
        runOnIdle {
            assertEquals(LocalDate(2024, 1, 1) to LocalDate(2024, 1, 31), value)
        }
    }

    @Test
    fun datePickerPanelUpdatesInline() = runComposeUiTest {
        var value by mutableStateOf<LocalDate?>(LocalDate(2024, 1, 1))
        setNContent {
            NDatePicker(
                value = value,
                onValueChange = { value = it },
                format = "yyyy-MM-dd",
                panel = true,
            )
        }
        onAllNodesWithText("12")[0].performClick()
        runOnIdle { assertEquals(LocalDate(2024, 1, 12), value) }
    }

    @Test
    fun datePickerControllerFocusAndBlur() = runComposeUiTest {
        var value by mutableStateOf<LocalDate?>(LocalDate(2024, 1, 1))
        lateinit var controller: NDatePickerController
        setNContent {
            controller = rememberNDatePickerController()
            NDatePicker(
                value = value,
                onValueChange = { value = it },
                controller = controller,
            )
        }
        runOnIdle { controller.focus() }
        waitUntil { onAllNodesWithText("Now").fetchSemanticsNodes().isNotEmpty() }
        runOnIdle { controller.blur() }
        waitUntil { onAllNodesWithText("Now").fetchSemanticsNodes().isEmpty() }
    }

    @Test
    fun datePickerDisabledDoesNotOpen() = runComposeUiTest {
        var value by mutableStateOf<LocalDate?>(LocalDate(2024, 1, 1))
        setNContent {
            NDatePicker(
                value = value,
                onValueChange = { value = it },
                format = "yyyy-MM-dd",
                disabled = true,
            )
        }
        onNodeWithText("2024-01-01").performClick()
        waitForIdle()
        assertTrue(onAllNodesWithText("15").fetchSemanticsNodes().isEmpty())
    }

    @Test
    fun timePickerSelectsHourImmediately() = runComposeUiTest {
        var value by mutableStateOf<LocalTime?>(LocalTime(1, 30, 0))
        setNContent {
            NTimePicker(value = value, onValueChange = { value = it })
        }
        onNodeWithText("01:30:00").performClick()
        waitUntil { onAllNodesWithText("02").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("02")[0].performClick()
        runOnIdle { assertEquals(LocalTime(2, 30, 0), value) }
    }

    @Test
    fun timePickerRespectsDisabledHours() = runComposeUiTest {
        var value by mutableStateOf<LocalTime?>(LocalTime(1, 30, 0))
        setNContent {
            NTimePicker(
                value = value,
                onValueChange = { value = it },
                isHourDisabled = { it == 2 },
            )
        }
        onNodeWithText("01:30:00").performClick()
        waitUntil { onAllNodesWithText("02").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("02")[0].performClick()
        runOnIdle { assertEquals(LocalTime(1, 30, 0), value) }
        onAllNodesWithText("03")[0].performClick()
        runOnIdle { assertEquals(LocalTime(3, 30, 0), value) }
    }

    @Test
    fun timePickerClearActionClearsValue() = runComposeUiTest {
        var value by mutableStateOf<LocalTime?>(LocalTime(1, 30, 0))
        var cleared = false
        setNContent {
            NTimePicker(
                value = value,
                onValueChange = { value = it },
                actions = setOf(NTimePickerAction.Clear),
                onClear = { cleared = true },
            )
        }
        onNodeWithText("01:30:00").performClick()
        waitUntil { onAllNodesWithText("Clear").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Clear").performClick()
        runOnIdle {
            assertNull(value)
            assertTrue(cleared)
        }
    }

    @Test
    fun timePickerConfirmActionReportsValue() = runComposeUiTest {
        var value by mutableStateOf<LocalTime?>(LocalTime(1, 30, 0))
        var confirmed: LocalTime? = null
        setNContent {
            NTimePicker(
                value = value,
                onValueChange = { value = it },
                actions = setOf(NTimePickerAction.Confirm),
                onConfirm = { confirmed = it },
            )
        }
        onNodeWithText("01:30:00").performClick()
        waitUntil { onAllNodesWithText("Confirm").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Confirm").performClick()
        runOnIdle { assertEquals(LocalTime(1, 30, 0), confirmed) }
    }

    @Test
    fun timePickerDisabledDoesNotOpen() = runComposeUiTest {
        var value by mutableStateOf<LocalTime?>(LocalTime(1, 30, 0))
        setNContent {
            NTimePicker(value = value, onValueChange = { value = it }, disabled = true)
        }
        onNodeWithText("01:30:00").performClick()
        waitForIdle()
        assertTrue(onAllNodesWithText("Confirm").fetchSemanticsNodes().isEmpty())
    }

    @Test
    fun colorPickerHexInputUpdatesValue() = runComposeUiTest {
        var value by mutableStateOf(Color(0xFF18A058))
        setNContent {
            NColorPickerPanel(
                value = value,
                onValueChange = { value = it },
                showAlpha = false,
                modes = listOf(NColorPickerMode.Hex),
            )
        }
        onNodeWithText("HEX").assertExists()
        onNode(hasSetTextAction()).performTextReplacement("#ff0000")
        runOnIdle {
            assertEquals(1f, value.red, 0.001f)
            assertEquals(0f, value.green, 0.001f)
            assertEquals(0f, value.blue, 0.001f)
        }
    }

    @Test
    fun colorPickerModeTabsSwitchFormat() = runComposeUiTest {
        var value by mutableStateOf(Color(0xFF18A058))
        setNContent {
            NColorPickerPanel(
                value = value,
                onValueChange = { value = it },
                showAlpha = false,
                modes = listOf(NColorPickerMode.Rgb, NColorPickerMode.Hex),
            )
        }
        onNode(hasSetTextAction()).assertTextEquals("rgb(24, 160, 88)")
        onNodeWithText("HEX").performClick()
        waitForIdle()
        onNode(hasSetTextAction()).assertTextEquals("#18a058")
    }

    @Test
    fun colorPickerControlledShowOpensPanel() = runComposeUiTest {
        var show by mutableStateOf(false)
        var value by mutableStateOf(Color(0xFF18A058))
        setNContent {
            NColorPicker(
                value = value,
                onValueChange = { value = it },
                show = show,
                onShowChange = { show = it },
                showAlpha = false,
                modes = listOf(NColorPickerMode.Hex),
            )
        }
        onNodeWithText("#18a058").performClick()
        waitUntil { onAllNodesWithText("HEX").fetchSemanticsNodes().isNotEmpty() }
    }

    @Test
    fun colorPickerDisabledDoesNotOpen() = runComposeUiTest {
        var value by mutableStateOf(Color(0xFF18A058))
        setNContent {
            NColorPicker(
                value = value,
                onValueChange = { value = it },
                showAlpha = false,
                modes = listOf(NColorPickerMode.Hex),
                disabled = true,
            )
        }
        onNodeWithText("#18a058").performClick()
        waitForIdle()
        assertTrue(onAllNodesWithText("HEX").fetchSemanticsNodes().isEmpty())
    }

    @Test
    fun colorPickerConfirmActionReportsColor() = runComposeUiTest {
        var value by mutableStateOf(Color(0xFF18A058))
        var confirmed: Color? = null
        setNContent {
            NColorPicker(
                value = value,
                onValueChange = { value = it },
                show = true,
                showAlpha = false,
                modes = listOf(NColorPickerMode.Hex),
                actions = listOf(NColorPickerAction.Confirm),
                onConfirm = { confirmed = it },
            )
        }
        onNodeWithText("Confirm").performClick()
        runOnIdle {
            val result = assertNotNull(confirmed)
            assertEquals(0.094f, result.red, 0.01f)
            assertEquals(0.627f, result.green, 0.01f)
        }
    }

    @Test
    fun sliderClickAtRightReachesMax() = runComposeUiTest {
        var value by mutableStateOf(0f)
        setNContent {
            NSlider(
                value = value,
                onValueChange = { value = it },
                min = 0f,
                max = 100f,
                step = 1f,
                modifier = Modifier.testTag("slider"),
            )
        }
        onNodeWithTag("slider").performTouchInput { click(Offset(right - 1f, center.y)) }
        runOnIdle { assertEquals(100f, value) }
    }

    @Test
    fun sliderClickAtLeftReachesMin() = runComposeUiTest {
        var value by mutableStateOf(50f)
        setNContent {
            NSlider(
                value = value,
                onValueChange = { value = it },
                min = 0f,
                max = 100f,
                step = 1f,
                modifier = Modifier.testTag("slider"),
            )
        }
        onNodeWithTag("slider").performTouchInput { click(Offset(0f, center.y)) }
        runOnIdle { assertEquals(0f, value) }
    }

    @Test
    fun sliderSnapsToStep() = runComposeUiTest {
        var value by mutableStateOf(0f)
        setNContent {
            NSlider(
                value = value,
                onValueChange = { value = it },
                min = 0f,
                max = 10f,
                step = 3f,
                modifier = Modifier.testTag("slider"),
            )
        }
        onNodeWithTag("slider").performTouchInput { click(Offset(width / 2f, center.y)) }
        runOnIdle { assertEquals(6f, value) }
    }

    @Test
    fun sliderRangeMovesSecondHandle() = runComposeUiTest {
        var value by mutableStateOf(20f)
        var second by mutableStateOf(80f)
        setNContent {
            Column {
                NSlider(
                    value = value,
                    onValueChange = { value = it },
                    secondValue = second,
                    onSecondValueChange = { second = it },
                    modifier = Modifier.testTag("slider"),
                )
            }
        }
        onNodeWithTag("slider").performTouchInput { click(Offset(right - 1f, center.y)) }
        runOnIdle {
            assertEquals(20f, value)
            assertEquals(100f, second)
        }
    }

    @Test
    fun sliderSnapsToMarks() = runComposeUiTest {
        var value by mutableStateOf(0f)
        setNContent {
            NSlider(
                value = value,
                onValueChange = { value = it },
                min = 0f,
                max = 100f,
                step = 1f,
                marks = mapOf(0f to "0", 50f to "50", 100f to "100"),
                snapToMarks = true,
                modifier = Modifier.testTag("slider"),
            )
        }
        onNodeWithTag("slider").performTouchInput { click(Offset(width * 0.45f, center.y)) }
        runOnIdle { assertEquals(50f, value) }
    }

    @Test
    fun sliderDisabledIgnoresClicks() = runComposeUiTest {
        var value by mutableStateOf(20f)
        setNContent {
            NSlider(
                value = value,
                onValueChange = { value = it },
                disabled = true,
                modifier = Modifier.testTag("slider"),
            )
        }
        onNodeWithTag("slider").performTouchInput { click(Offset(right - 1f, center.y)) }
        runOnIdle { assertEquals(20f, value) }
    }

    @Test
    fun rateClickSelectsStarCount() = runComposeUiTest {
        var value by mutableStateOf(0)
        setNContent {
            NRate(
                value = value,
                onValueChange = { value = it },
                modifier = Modifier.testTag("rate"),
            )
        }
        onNodeWithTag("rate").performTouchInput { click(Offset(62.dp.toPx(), center.y)) }
        runOnIdle { assertEquals(3, value) }
    }

    @Test
    fun rateClearableResetsOnSameStar() = runComposeUiTest {
        var value by mutableStateOf(0)
        setNContent {
            NRate(
                value = value,
                onValueChange = { value = it },
                clearable = true,
                modifier = Modifier.testTag("rate"),
            )
        }
        onNodeWithTag("rate").performTouchInput { click(Offset(62.dp.toPx(), center.y)) }
        runOnIdle { assertEquals(3, value) }
        onNodeWithTag("rate").performTouchInput { click(Offset(62.dp.toPx(), center.y)) }
        runOnIdle { assertEquals(0, value) }
    }

    @Test
    fun ratePreciseSelectsHalfStar() = runComposeUiTest {
        var value by mutableStateOf(0f)
        setNContent {
            NRatePrecise(
                value = value,
                onValueChange = { value = it },
                modifier = Modifier.testTag("rate"),
            )
        }
        onNodeWithTag("rate").performTouchInput { click(Offset(80.dp.toPx(), center.y)) }
        runOnIdle { assertEquals(3.5f, value) }
    }

    @Test
    fun rateDisabledIgnoresClicks() = runComposeUiTest {
        var value by mutableStateOf(2)
        setNContent {
            NRate(
                value = value,
                onValueChange = { value = it },
                disabled = true,
                modifier = Modifier.testTag("rate"),
            )
        }
        onNodeWithTag("rate").performTouchInput { click(Offset(62.dp.toPx(), center.y)) }
        runOnIdle { assertEquals(2, value) }
    }
}
