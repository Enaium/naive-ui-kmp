package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NDatePicker
import cn.enaium.naiveui.components.NDatePickerAction
import cn.enaium.naiveui.components.NDatePickerType
import cn.enaium.naiveui.components.NDateTimeRangePicker
import cn.enaium.naiveui.components.rememberNDatePickerController
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.components.NSwitch
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus

@Composable
fun DatePickerDocs() {
    DocsPage(
        title = "Date Picker",
        description = "Date, date-time, range, month, year, quarter and week pickers built on kotlinx-datetime.",
    ) {
        DateDemo()
        DateTimeDemo()
        DateRangeDemo()
        DateTimeRangeDemo()
        MonthDemo()
        MonthRangeDemo()
        YearDemo()
        YearRangeDemo()
        QuarterDemo()
        QuarterRangeDemo()
        WeekDemo()
        SizeDemo()
        DefaultTimeDemo()
        DisabledDemo()
        DisabledTimeDemo()
        ActionsDemo()
        ShortcutsDemo()
        EventsDemo()
        FormatDemo()
        FooterSlotDemo()
        UpdateOnCloseDemo()
        FocusDemo()
        StatusDemo()
        IconDemo()
        PanelDemo()
        ClosePanelOnSelectDemo()
        FormattedValueDemo()
    }
}

@Composable
private fun DateDemo() {
    DemoSection(
        title = "Date",
        description = "A date picker bound to a nullable LocalDate.",
        code = "var date by remember { mutableStateOf<LocalDate?>(LocalDate(2024, 5, 20)) }\n" +
            "NDatePicker(value = date, onValueChange = { date = it })",
    ) {
        var date by remember { mutableStateOf<LocalDate?>(LocalDate(2024, 5, 20)) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(value = date, onValueChange = { date = it }, clearable = true)
            NText("Value: ${date ?: "null"}", depth = 3)
        }
    }
}

@Composable
private fun DateTimeDemo() {
    DemoSection(
        title = "Date time",
        description = "Pass a LocalDateTime to get the date-time panel with confirm and cancel.",
        code = "var value by remember { mutableStateOf<LocalDateTime?>(null) }\n" +
            "NDatePicker(value = value, onValueChange = { value = it }, clearable = true)",
    ) {
        var value by remember {
            mutableStateOf<LocalDateTime?>(LocalDateTime(2024, 5, 20, 14, 30, 0))
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(value = value, onValueChange = { value = it }, clearable = true)
            NText("Value: ${value ?: "null"}", depth = 3)
        }
    }
}

@Composable
private fun DateRangeDemo() {
    DemoSection(
        title = "Date range",
        description = "The range overload takes a Pair<LocalDate, LocalDate> and shows two independent calendars.",
        code = "var range by remember { mutableStateOf<Pair<LocalDate, LocalDate>?>(null) }\n" +
            "NDatePicker(value = range, onValueChange = { range = it }, clearable = true)",
    ) {
        var range by remember {
            mutableStateOf<Pair<LocalDate, LocalDate>?>(
                LocalDate(2024, 5, 12) to LocalDate(2024, 5, 22),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(value = range, onValueChange = { range = it }, clearable = true)
            NText("Value: ${range ?: "null"}", depth = 3)
        }
    }
}

@Composable
private fun DateTimeRangeDemo() {
    DemoSection(
        title = "Date time range",
        description = "NDateTimeRangePicker has a time column set under each calendar.",
        code = "var range by remember {\n" +
            "    mutableStateOf<Pair<LocalDateTime, LocalDateTime>?>(null)\n" +
            "}\n" +
            "NDateTimeRangePicker(value = range, onValueChange = { range = it })",
    ) {
        var range by remember {
            mutableStateOf<Pair<LocalDateTime, LocalDateTime>?>(
                LocalDateTime(2024, 5, 12, 8, 0) to LocalDateTime(2024, 5, 16, 18, 30),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDateTimeRangePicker(value = range, onValueChange = { range = it }, clearable = true)
            NText("Value: ${range ?: "null"}", depth = 3)
        }
    }
}

@Composable
private fun MonthDemo() {
    DemoSection(
        title = "Month",
        description = "Type Month picks the first day of a month. monthFormat controls the month cells and format controls the trigger.",
        code = "NDatePicker(\n" +
            "    value = month,\n" +
            "    onValueChange = { month = it },\n" +
            "    type = NDatePickerType.Month,\n" +
            "    format = \"y MMM\",\n" +
            "    monthFormat = \"MMM\",\n" +
            ")",
    ) {
        var month by remember { mutableStateOf<LocalDate?>(LocalDate(2024, 5, 1)) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(
                value = month,
                onValueChange = { month = it },
                type = NDatePickerType.Month,
                format = "y MMM",
                monthFormat = "MMM",
                clearable = true,
            )
            NDatePicker(
                value = month,
                onValueChange = { month = it },
                type = NDatePickerType.Month,
                clearable = true,
            )
            NText("Value: ${month ?: "null"}", depth = 3)
        }
    }
}

@Composable
private fun MonthRangeDemo() {
    DemoSection(
        title = "Month range",
        description = "Pair range with NDatePickerType.MonthRange.",
        code = "NDatePicker(\n" +
            "    value = range,\n" +
            "    onValueChange = { range = it },\n" +
            "    type = NDatePickerType.MonthRange,\n" +
            ")",
    ) {
        var range by remember {
            mutableStateOf<Pair<LocalDate, LocalDate>?>(
                LocalDate(2024, 3, 1) to LocalDate(2024, 6, 1),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(
                value = range,
                onValueChange = { range = it },
                type = NDatePickerType.MonthRange,
                clearable = true,
            )
            NText("Value: ${range ?: "null"}", depth = 3)
        }
    }
}

@Composable
private fun YearDemo() {
    DemoSection(
        title = "Year",
        description = "Type Year resolves to January 1st.",
        code = "NDatePicker(value = year, onValueChange = { year = it }, type = NDatePickerType.Year)",
    ) {
        var year by remember { mutableStateOf<LocalDate?>(LocalDate(2024, 1, 1)) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(
                value = year,
                onValueChange = { year = it },
                type = NDatePickerType.Year,
                clearable = true,
            )
            NText("Value: ${year ?: "null"}", depth = 3)
        }
    }
}

@Composable
private fun YearRangeDemo() {
    DemoSection(
        title = "Year range",
        description = "Two decade panels select the start and end year.",
        code = "NDatePicker(\n" +
            "    value = range,\n" +
            "    onValueChange = { range = it },\n" +
            "    type = NDatePickerType.YearRange,\n" +
            ")",
    ) {
        var range by remember {
            mutableStateOf<Pair<LocalDate, LocalDate>?>(
                LocalDate(2022, 1, 1) to LocalDate(2025, 1, 1),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(
                value = range,
                onValueChange = { range = it },
                type = NDatePickerType.YearRange,
                clearable = true,
            )
            NText("Value: ${range ?: "null"}", depth = 3)
        }
    }
}

@Composable
private fun QuarterDemo() {
    DemoSection(
        title = "Quarter",
        description = "Type Quarter picks the first day of a quarter.",
        code = "NDatePicker(\n" +
            "    value = quarter,\n" +
            "    onValueChange = { quarter = it },\n" +
            "    type = NDatePickerType.Quarter,\n" +
            ")",
    ) {
        var quarter by remember { mutableStateOf<LocalDate?>(LocalDate(2024, 4, 1)) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(
                value = quarter,
                onValueChange = { quarter = it },
                type = NDatePickerType.Quarter,
                clearable = true,
            )
            NText("Value: ${quarter ?: "null"}", depth = 3)
        }
    }
}

@Composable
private fun QuarterRangeDemo() {
    DemoSection(
        title = "Quarter range",
        description = "Select a start and end quarter across two panels.",
        code = "NDatePicker(\n" +
            "    value = range,\n" +
            "    onValueChange = { range = it },\n" +
            "    type = NDatePickerType.QuarterRange,\n" +
            ")",
    ) {
        var range by remember {
            mutableStateOf<Pair<LocalDate, LocalDate>?>(
                LocalDate(2024, 1, 1) to LocalDate(2024, 7, 1),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(
                value = range,
                onValueChange = { range = it },
                type = NDatePickerType.QuarterRange,
                clearable = true,
            )
            NText("Value: ${range ?: "null"}", depth = 3)
        }
    }
}

@Composable
private fun WeekDemo() {
    DemoSection(
        title = "Week",
        description = "Picking any day selects its whole week; the value is the week start.",
        code = "NDatePicker(value = week, onValueChange = { week = it }, type = NDatePickerType.Week)",
    ) {
        var week by remember { mutableStateOf<LocalDate?>(LocalDate(2024, 5, 20)) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(
                value = week,
                onValueChange = { week = it },
                type = NDatePickerType.Week,
                clearable = true,
            )
            NText("Week start: ${week ?: "null"}", depth = 3)
        }
    }
}

@Composable
private fun SizeDemo() {
    DemoSection(
        title = "Size",
        description = "Small, medium and large sizes share the same value.",
        code = "NDatePicker(value = date, onValueChange = { date = it }, size = NSize.Small)",
    ) {
        var date by remember { mutableStateOf<LocalDate?>(LocalDate(2024, 5, 20)) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(value = date, onValueChange = { date = it }, size = NSize.Small)
            NDatePicker(value = date, onValueChange = { date = it }, size = NSize.Medium)
            NDatePicker(value = date, onValueChange = { date = it }, size = NSize.Large)
        }
    }
}

@Composable
private fun DefaultTimeDemo() {
    DemoSection(
        title = "Default time",
        description = "defaultTime seeds the time columns when the value is empty. Range pickers take separate defaults.",
        code = "NDatePicker(\n" +
            "    value = value,\n" +
            "    onValueChange = { value = it },\n" +
            "    defaultTime = LocalTime(13, 22, 11),\n" +
            ")",
    ) {
        var dateTime by remember { mutableStateOf<LocalDateTime?>(null) }
        var range by remember {
            mutableStateOf<Pair<LocalDateTime, LocalDateTime>?>(null)
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(
                value = dateTime,
                onValueChange = { dateTime = it },
                defaultTime = LocalTime(13, 22, 11),
                clearable = true,
            )
            NDateTimeRangePicker(
                value = range,
                onValueChange = { range = it },
                defaultStartTime = LocalTime(13, 22, 11),
                defaultEndTime = LocalTime(16, 0, 0),
                clearable = true,
            )
            NText("Single: ${dateTime ?: "null"} · Range: ${range ?: "null"}", depth = 3)
        }
    }
}

@Composable
private fun DisabledDemo() {
    DemoSection(
        title = "Disabled",
        description = "Toggle the switch to disable every picker.",
        code = "NDatePicker(value = date, onValueChange = { date = it }, disabled = disabled)",
    ) {
        var disabled by remember { mutableStateOf(true) }
        var date by remember { mutableStateOf<LocalDate?>(null) }
        var dateTime by remember { mutableStateOf<LocalDateTime?>(null) }
        var range by remember { mutableStateOf<Pair<LocalDate, LocalDate>?>(null) }
        var timeRange by remember {
            mutableStateOf<Pair<LocalDateTime, LocalDateTime>?>(null)
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(value = date, onValueChange = { date = it }, disabled = disabled)
            NDatePicker(
                value = dateTime,
                onValueChange = { dateTime = it },
                disabled = disabled,
            )
            NDatePicker(value = range, onValueChange = { range = it }, disabled = disabled)
            NDateTimeRangePicker(
                value = timeRange,
                onValueChange = { timeRange = it },
                disabled = disabled,
            )
            NSwitch(checked = disabled, onCheckedChange = { disabled = it })
        }
    }
}

@Composable
private fun DisabledTimeDemo() {
    DemoSection(
        title = "Disabled time",
        description = "isDateDisabled rejects dates; isHourDisabled/isMinuteDisabled reject time column values.",
        code = "NDatePicker(\n" +
            "    value = value,\n" +
            "    onValueChange = { value = it },\n" +
            "    isDateDisabled = { it.day < 15 },\n" +
            "    isHourDisabled = { it < 12 },\n" +
            ")",
    ) {
        var date by remember { mutableStateOf<LocalDate?>(LocalDate(2024, 5, 20)) }
        var dateTime by remember {
            mutableStateOf<LocalDateTime?>(LocalDateTime(2024, 5, 20, 15, 0))
        }
        var range by remember {
            mutableStateOf<Pair<LocalDate, LocalDate>?>(
                LocalDate(2024, 5, 10) to LocalDate(2024, 5, 20),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(
                value = date,
                onValueChange = { date = it },
                isDateDisabled = { it.day < 15 },
                clearable = true,
            )
            NDatePicker(
                value = dateTime,
                onValueChange = { dateTime = it },
                isDateDisabled = { it.day < 15 },
                isHourDisabled = { it < 12 },
                isMinuteDisabled = { it % 15 != 0 },
                clearable = true,
            )
            NDatePicker(
                value = range,
                onValueChange = { range = it },
                isDateDisabled = { candidate ->
                    val current = range
                    current != null && candidate > current.first &&
                        candidate < current.first.plus(7, DateTimeUnit.DAY)
                },
                clearable = true,
            )
            var timeRange by remember {
                mutableStateOf<Pair<LocalDateTime, LocalDateTime>?>(null)
            }
            NDateTimeRangePicker(
                value = timeRange,
                onValueChange = { timeRange = it },
                isDateDisabled = { it.dayOfWeek == DayOfWeek.SUNDAY },
                isHourDisabled = { it < 8 },
                clearable = true,
            )
            NText("At least 7 days between range endpoints; Sunday and early hours are disabled.", depth = 3)
        }
    }
}

@Composable
private fun ActionsDemo() {
    DemoSection(
        title = "Actions",
        description = "actions controls the footer buttons. The default depends on the picker type; an empty set hides the footer.",
        code = "NDatePicker(..., actions = setOf(NDatePickerAction.Now))\n" +
            "NDatePicker(..., actions = emptySet())",
    ) {
        var date by remember { mutableStateOf<LocalDate?>(null) }
        var dateTime by remember { mutableStateOf<LocalDateTime?>(null) }
        var range by remember { mutableStateOf<Pair<LocalDate, LocalDate>?>(null) }
        var timeRange by remember {
            mutableStateOf<Pair<LocalDateTime, LocalDateTime>?>(null)
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(
                value = date,
                onValueChange = { date = it },
                actions = setOf(NDatePickerAction.Now),
            )
            NDatePicker(
                value = dateTime,
                onValueChange = { dateTime = it },
                actions = setOf(NDatePickerAction.Now),
            )
            NDatePicker(
                value = range,
                onValueChange = { range = it },
                actions = emptySet(),
                updateValueOnClose = true,
            )
            NDateTimeRangePicker(
                value = timeRange,
                onValueChange = { timeRange = it },
                actions = setOf(NDatePickerAction.Clear),
                updateValueOnClose = true,
            )
            NText("Clear the last picker from its footer.", depth = 3)
        }
    }
}

@Composable
private fun ShortcutsDemo() {
    DemoSection(
        title = "Shortcuts",
        description = "The shortcuts slot receives a selector for the picker's value type.",
        code = "NDatePicker(\n" +
            "    value = date,\n" +
            "    onValueChange = { date = it },\n" +
            "    shortcuts = { select ->\n" +
            "        NButton(text = true, onClick = { select(LocalDate(2024, 5, 20)) }) {\n" +
            "            NText(\"Today\")\n" +
            "        }\n" +
            "    },\n" +
            ")",
    ) {
        var date by remember { mutableStateOf<LocalDate?>(null) }
        var dateTime by remember { mutableStateOf<LocalDateTime?>(null) }
        var range by remember { mutableStateOf<Pair<LocalDate, LocalDate>?>(null) }
        var timeRange by remember {
            mutableStateOf<Pair<LocalDateTime, LocalDateTime>?>(null)
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(
                value = date,
                onValueChange = { date = it },
                shortcuts = { select ->
                    NButton(text = true, size = NSize.Small, onClick = {
                        select(LocalDate(2024, 5, 20))
                    }) { NText("Today") }
                    NButton(text = true, size = NSize.Small, onClick = {
                        select(LocalDate(2024, 12, 31))
                    }) { NText("Year end") }
                },
            )
            NDatePicker(
                value = dateTime,
                onValueChange = { dateTime = it },
                shortcuts = { select ->
                    NButton(text = true, size = NSize.Small, onClick = {
                        select(LocalDateTime(2024, 5, 20, 13, 22, 11))
                    }) { NText("Honey birthday") }
                },
            )
            NDatePicker(
                value = range,
                onValueChange = { range = it },
                shortcuts = { select ->
                    NButton(text = true, size = NSize.Small, onClick = {
                        select(LocalDate(2024, 5, 1) to LocalDate(2024, 5, 7))
                    }) { NText("First week") }
                },
            )
            NDateTimeRangePicker(
                value = timeRange,
                onValueChange = { timeRange = it },
                shortcuts = { select ->
                    NButton(text = true, size = NSize.Small, onClick = {
                        select(
                            LocalDateTime(2024, 5, 1, 0, 0) to
                                LocalDateTime(2024, 5, 31, 23, 59),
                        )
                    }) { NText("May") }
                },
            )
        }
    }
}

@Composable
private fun EventsDemo() {
    DemoSection(
        title = "Events",
        description = "Focus, blur, change, clear and confirm callbacks are all reported below.",
        code = "NDatePicker(\n" +
            "    value = date,\n" +
            "    onValueChange = { date = it },\n" +
            "    onFocus = { log = \"focus\" },\n" +
            "    onBlur = { log = \"blur\" },\n" +
            "    onClear = { log = \"clear\" },\n" +
            ")",
    ) {
        var log by remember { mutableStateOf("No events yet") }
        fun record(event: String) {
            log = if (log == "No events yet") event else "$log · $event"
        }
        var date by remember { mutableStateOf<LocalDate?>(null) }
        var dateTime by remember { mutableStateOf<LocalDateTime?>(null) }
        var range by remember { mutableStateOf<Pair<LocalDate, LocalDate>?>(null) }
        var disabled by remember { mutableStateOf(false) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(
                value = date,
                onValueChange = {
                    date = it
                    record("date change: $it")
                },
                disabled = disabled,
                onFocus = { record("date focus") },
                onBlur = { record("date blur") },
                onClear = { record("date clear") },
            )
            NDatePicker(
                value = dateTime,
                onValueChange = {
                    dateTime = it
                    record("datetime change: $it")
                },
                disabled = disabled,
                onFocus = { record("datetime focus") },
                onBlur = { record("datetime blur") },
            )
            NDatePicker(
                value = range,
                onValueChange = {
                    range = it
                    record("range change: $it")
                },
                disabled = disabled,
                onFocus = { record("range focus") },
                onBlur = { record("range blur") },
                onClear = { record("range clear") },
                onConfirm = { record("range confirm: $it") },
            )
            NSwitch(checked = disabled, onCheckedChange = { disabled = it })
            NText(log, depth = 3)
        }
    }
}

@Composable
private fun FormatDemo() {
    DemoSection(
        title = "Format",
        description = "The format token set includes yyyy/MM/dd, HH:mm, h:mm a, MMMM and EEEE.",
        code = "NDatePicker(value = value, onValueChange = { value = it }, format = \"yyyy/MM/dd - HH:mm\")",
    ) {
        var empty by remember { mutableStateOf<LocalDateTime?>(null) }
        var fixed by remember {
            mutableStateOf<LocalDateTime?>(LocalDateTime(2024, 5, 20, 14, 30))
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(
                value = empty,
                onValueChange = { empty = it },
                format = "yyyy/MM/dd - HH:mm",
                clearable = true,
            )
            NDatePicker(
                value = fixed,
                onValueChange = { fixed = it },
                format = "EEEE, MMMM d yyyy h:mm a",
                use12Hours = true,
                clearable = true,
            )
        }
    }
}

@Composable
private fun FooterSlotDemo() {
    DemoSection(
        title = "Extra footer",
        description = "The footer slot is rendered below the action buttons.",
        code = "NDatePicker(\n" +
            "    value = date,\n" +
            "    onValueChange = { date = it },\n" +
            "    footer = { NText(\"extra footer\", depth = 3) },\n" +
            ")",
    ) {
        var date by remember { mutableStateOf<LocalDate?>(LocalDate(2024, 5, 20)) }
        var dateTime by remember {
            mutableStateOf<LocalDateTime?>(LocalDateTime(2024, 5, 20, 10, 0))
        }
        var range by remember { mutableStateOf<Pair<LocalDate, LocalDate>?>(null) }
        var timeRange by remember {
            mutableStateOf<Pair<LocalDateTime, LocalDateTime>?>(null)
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(
                value = date,
                onValueChange = { date = it },
                footer = { NText("extra footer", depth = 3) },
            )
            NDatePicker(
                value = dateTime,
                onValueChange = { dateTime = it },
                footer = { NText("extra footer", depth = 3) },
            )
            NDatePicker(
                value = range,
                onValueChange = { range = it },
                footer = { NText("extra footer", depth = 3) },
            )
            NDateTimeRangePicker(
                value = timeRange,
                onValueChange = { timeRange = it },
                footer = { NText("extra footer", depth = 3) },
            )
        }
    }
}

@Composable
private fun UpdateOnCloseDemo() {
    DemoSection(
        title = "Update value on close",
        description = "With updateValueOnClose the draft is committed when the popup closes instead of on every click.",
        code = "NDatePicker(\n" +
            "    value = value,\n" +
            "    onValueChange = { value = it },\n" +
            "    updateValueOnClose = true,\n" +
            ")",
    ) {
        var updateOnClose by remember { mutableStateOf(true) }
        var dateTime by remember {
            mutableStateOf<LocalDateTime?>(LocalDateTime(2024, 5, 20, 9, 0))
        }
        var range by remember {
            mutableStateOf<Pair<LocalDate, LocalDate>?>(
                LocalDate(2024, 5, 12) to LocalDate(2024, 5, 16),
            )
        }
        var timeRange by remember {
            mutableStateOf<Pair<LocalDateTime, LocalDateTime>?>(
                LocalDateTime(2024, 5, 12, 8, 0) to LocalDateTime(2024, 5, 16, 18, 0),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(
                value = dateTime,
                onValueChange = { dateTime = it },
                updateValueOnClose = updateOnClose,
                clearable = true,
            )
            NDatePicker(
                value = range,
                onValueChange = { range = it },
                updateValueOnClose = updateOnClose,
                clearable = true,
            )
            NDateTimeRangePicker(
                value = timeRange,
                onValueChange = { timeRange = it },
                updateValueOnClose = updateOnClose,
                clearable = true,
            )
            NSwitch(
                checked = updateOnClose,
                onCheckedChange = { updateOnClose = it },
            )
            NText("Datetime: $dateTime · Range: $range · Time range: $timeRange", depth = 3)
        }
    }
}

@Composable
private fun FocusDemo() {
    DemoSection(
        title = "Focus and blur",
        description = "A controller can focus and blur the picker programmatically.",
        code = "val controller = rememberNDatePickerController()\n" +
            "NButton(onClick = { controller.focus() }) { NText(\"Focus\") }\n" +
            "NDatePicker(value = date, onValueChange = { date = it }, controller = controller)",
    ) {
        val controller = rememberNDatePickerController()
        val scope = rememberCoroutineScope()
        var date by remember { mutableStateOf<LocalDate?>(LocalDate(2024, 5, 20)) }
        NSpace(size = 12.dp) {
            NButton(onClick = {
                controller.focus()
                scope.launch {
                    delay(1000)
                    controller.blur()
                }
            }) { NText("Focus then blur in 1 second") }
            NDatePicker(
                value = date,
                onValueChange = { date = it },
                controller = controller,
                modifier = Modifier.width(220.dp),
            )
        }
    }
}

@Composable
private fun StatusDemo() {
    DemoSection(
        title = "Validation status",
        description = "status applies warning or error coloring to the trigger border.",
        code = "NDatePicker(value = date, onValueChange = { date = it }, status = NStatus.Warning)",
    ) {
        var warning by remember { mutableStateOf<LocalDate?>(null) }
        var error by remember { mutableStateOf<LocalDate?>(null) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(
                value = warning,
                onValueChange = { warning = it },
                status = NStatus.Warning,
            )
            NDatePicker(
                value = error,
                onValueChange = { error = it },
                status = NStatus.Error,
            )
        }
    }
}

@Composable
private fun IconDemo() {
    DemoSection(
        title = "Icon",
        description = "The date icon, navigation arrows and range separator accept custom content.",
        code = "NDatePicker(\n" +
            "    value = date,\n" +
            "    onValueChange = { date = it },\n" +
            "    dateIcon = { NIcon(NIcons.Date) },\n" +
            "    prevMonthIcon = { NIcon(NIcons.ChevronLeft) },\n" +
            ")",
    ) {
        var date by remember { mutableStateOf<LocalDate?>(LocalDate(2024, 5, 20)) }
        var range by remember {
            mutableStateOf<Pair<LocalDate, LocalDate>?>(
                LocalDate(2024, 5, 12) to LocalDate(2024, 5, 22),
            )
        }
        val primary = LocalNTheme.current.colors.primary
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(
                value = date,
                onValueChange = { date = it },
                dateIcon = { NIcon(NIcons.Date, size = 16.dp, color = primary) },
                prevYearIcon = { NIcon(NIcons.ChevronLeft, size = 16.dp, color = primary) },
                prevMonthIcon = { NIcon(NIcons.ChevronLeft, size = 16.dp, color = primary) },
                nextMonthIcon = { NIcon(NIcons.ChevronRight, size = 16.dp, color = primary) },
                nextYearIcon = { NIcon(NIcons.ChevronRight, size = 16.dp, color = primary) },
            )
            NDatePicker(
                value = range,
                onValueChange = { range = it },
                dateIcon = { NIcon(NIcons.Date, size = 16.dp, color = primary) },
                separator = { NIcon(NIcons.To, size = 16.dp, color = primary) },
            )
        }
    }
}

@Composable
private fun PanelDemo() {
    DemoSection(
        title = "Use panel only",
        description = "panel renders the calendar inline without a trigger or popup.",
        code = "NDatePicker(value = date, onValueChange = { date = it }, panel = true)",
    ) {
        var date by remember { mutableStateOf<LocalDate?>(LocalDate(2024, 5, 20)) }
        NDatePicker(
            value = date,
            onValueChange = { date = it },
            panel = true,
            modifier = Modifier.width(320.dp),
        )
    }
}

@Composable
private fun ClosePanelOnSelectDemo() {
    DemoSection(
        title = "Close panel on select year or month",
        description = "fastYearSelect and fastMonthSelect close the whole popup as soon as a year or month is clicked.",
        code = "NDatePicker(\n" +
            "    value = date,\n" +
            "    onValueChange = { date = it },\n" +
            "    fastYearSelect = true,\n" +
            "    fastMonthSelect = true,\n" +
            ")",
    ) {
        var date by remember { mutableStateOf<LocalDate?>(LocalDate(2024, 5, 20)) }
        var dateTime by remember {
            mutableStateOf<LocalDateTime?>(LocalDateTime(2024, 5, 20, 10, 0))
        }
        var range by remember {
            mutableStateOf<Pair<LocalDate, LocalDate>?>(
                LocalDate(2024, 5, 12) to LocalDate(2024, 5, 22),
            )
        }
        var timeRange by remember {
            mutableStateOf<Pair<LocalDateTime, LocalDateTime>?>(
                LocalDateTime(2024, 5, 12, 8, 0) to LocalDateTime(2024, 5, 22, 18, 0),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(
                value = date,
                onValueChange = { date = it },
                fastYearSelect = true,
                fastMonthSelect = true,
                clearable = true,
            )
            NDatePicker(
                value = dateTime,
                onValueChange = { dateTime = it },
                fastYearSelect = true,
                fastMonthSelect = true,
                clearable = true,
            )
            NDatePicker(
                value = range,
                onValueChange = { range = it },
                fastYearSelect = true,
                fastMonthSelect = true,
                clearable = true,
            )
            NDateTimeRangePicker(
                value = timeRange,
                onValueChange = { timeRange = it },
                fastYearSelect = true,
                fastMonthSelect = true,
                clearable = true,
            )
        }
    }
}

@Composable
private fun FormattedValueDemo() {
    DemoSection(
        title = "Formatted value",
        description = "formattedValue controls the trigger text and onFormattedValueChange reports the formatted value.",
        code = "var formatted by remember { mutableStateOf(\"2007-06-30 12:08:55\") }\n" +
            "NDatePicker(\n" +
            "    value = value,\n" +
            "    onValueChange = { value = it },\n" +
            "    formattedValue = formatted,\n" +
            "    onFormattedValueChange = { formatted = it },\n" +
            "    format = \"yyyy-MM-dd HH:mm:ss\",\n" +
            ")",
    ) {
        var value by remember {
            mutableStateOf<LocalDateTime?>(LocalDateTime(2007, 6, 30, 12, 8, 55))
        }
        var formatted by remember { mutableStateOf<String?>("2007-06-30 12:08:55") }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDatePicker(
                value = value,
                onValueChange = { value = it },
                formattedValue = formatted,
                onFormattedValueChange = { formatted = it },
                format = "yyyy-MM-dd HH:mm:ss",
                clearable = true,
            )
            NText("Typed: ${value ?: "null"} · Formatted: ${formatted ?: "null"}", depth = 3)
        }
    }
}
