package cn.enaium.naiveui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class NCalendarDisplayTest {
    @Test
    fun rendersMonthYearAndDayGrid() = runComposeUiTest {
        setNContent {
            NCalendar(value = LocalDate(2022, 5, 19), onValueChange = {})
        }
        onNodeWithText("May").assertExists()
        onNodeWithText("2022").assertExists()
        onNodeWithText("19").assertExists()
    }

    @Test
    fun previousMonthNavigatesPanel() = runComposeUiTest {
        val panels = mutableListOf<Pair<Int, Int>>()
        setNContent {
            NCalendar(
                value = LocalDate(2022, 5, 19),
                onValueChange = {},
                onPanelChange = { year, month -> panels += year to month },
            )
        }
        onNodeWithContentDescription("Previous").performClick()
        onNodeWithText("April").assertExists()
        onNodeWithText("May").assertDoesNotExist()
        assertEquals(listOf(2022 to 4), panels)
    }

    @Test
    fun nextMonthNavigatesPanel() = runComposeUiTest {
        val panels = mutableListOf<Pair<Int, Int>>()
        setNContent {
            NCalendar(
                value = LocalDate(2022, 5, 19),
                onValueChange = {},
                onPanelChange = { year, month -> panels += year to month },
            )
        }
        onNodeWithContentDescription("Next").performClick()
        onNodeWithText("June").assertExists()
        assertEquals(listOf(2022 to 6), panels)
    }

    @Test
    fun clickingDayReportsDate() = runComposeUiTest {
        var selected: LocalDate? = null
        setNContent {
            NCalendar(value = LocalDate(2022, 5, 19), onValueChange = { selected = it })
        }
        onNodeWithText("12").performClick()
        runOnIdle { assertEquals(LocalDate(2022, 5, 12), selected) }
    }

    @Test
    fun disabledDateDoesNotReport() = runComposeUiTest {
        var selected: LocalDate? = null
        setNContent {
            NCalendar(
                value = LocalDate(2022, 5, 19),
                onValueChange = { selected = it },
                isDateDisabled = { it.day == 20 },
            )
        }
        onNodeWithText("20").performClick()
        runOnIdle { assertEquals(null, selected) }
        onNodeWithText("19").performClick()
        runOnIdle { assertEquals(LocalDate(2022, 5, 19), selected) }
    }

    @Test
    fun monthPanelSelectsMonth() = runComposeUiTest {
        val panels = mutableListOf<Pair<Int, Int>>()
        setNContent {
            NCalendar(
                value = LocalDate(2022, 5, 19),
                onValueChange = {},
                onPanelChange = { year, month -> panels += year to month },
            )
        }
        onNodeWithText("May").performClick()
        onNodeWithText("August").performClick()
        onNodeWithText("August").assertExists()
        assertEquals(listOf(2022 to 8), panels)
    }

    @Test
    fun hidesHeaderLabelsWhenDisabled() = runComposeUiTest {
        setNContent {
            NCalendar(
                value = LocalDate(2022, 5, 19),
                onValueChange = {},
                showMonth = false,
                showYear = false,
            )
        }
        onNodeWithText("2022-05").assertExists()
    }

    @Test
    fun datetimeTypeShowsTimePicker() = runComposeUiTest {
        setNContent {
            NCalendar(
                value = LocalDate(2022, 5, 19),
                onValueChange = {},
                type = NCalendarType.Datetime,
                time = LocalTime(10, 30),
                onTimeChange = {},
            )
        }
        onNodeWithText("Select Time").assertExists()
    }

    @Test
    fun controlledValueSwitchesPanelMonth() = runComposeUiTest {
        var value by mutableStateOf(LocalDate(2022, 5, 19))
        setNContent {
            NCalendar(value = value, onValueChange = { value = it })
        }
        onNodeWithText("May").assertExists()
        value = LocalDate(2022, 9, 1)
        waitForIdle()
        onNodeWithText("September").assertExists()
        onNodeWithText("May").assertDoesNotExist()
    }
}
