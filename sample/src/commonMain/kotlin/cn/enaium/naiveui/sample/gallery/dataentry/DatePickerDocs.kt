package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NDatePicker
import cn.enaium.naiveui.components.NDatePickerType
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@Composable
fun DatePickerDocs() {
    DocsPage(
        title = "Date Picker",
        description = "Pick a date, a date range, a month or a year. Values are plain kotlinx-datetime types.",
    ) {
        DemoSection(
            title = "Basic",
            description = "A date picker bound to a nullable LocalDate. The clear button resets the value to null.",
            code = "var date by remember { mutableStateOf<LocalDate?>(LocalDate(2024, 5, 20)) }\n" +
                "NDatePicker(value = date, onValueChange = { date = it }, clearable = true)",
        ) {
            var date by remember { mutableStateOf<LocalDate?>(LocalDate(2024, 5, 20)) }
            NSpace(vertical = true, size = 10.dp) {
                NDatePicker(value = date, onValueChange = { date = it }, clearable = true)
                NText("Value: ${date ?: "null"}", depth = 3)
            }
        }

        DemoSection(
            title = "Date time",
            description = "Pass a LocalDateTime to get the date-time panel. The panel keeps a draft until Confirm is pressed, and the format token set is shared with naive-ui.",
            code = "var value by remember { mutableStateOf<LocalDateTime?>(null) }\n" +
                "NDatePicker(\n" +
                "    value = value,\n" +
                "    onValueChange = { value = it },\n" +
                "    format = \"yyyy/MM/dd HH:mm\",\n" +
                ")",
        ) {
            var value by remember {
                mutableStateOf<LocalDateTime?>(LocalDateTime(2024, 5, 20, 14, 30, 0))
            }
            NSpace(vertical = true, size = 10.dp) {
                NDatePicker(
                    value = value,
                    onValueChange = { value = it },
                    clearable = true,
                    format = "yyyy/MM/dd HH:mm",
                )
                NText("Value: ${value ?: "null"}", depth = 3)
            }
        }

        DemoSection(
            title = "Range",
            description = "The range overload takes a Pair<LocalDate, LocalDate>. Click a start and an end date, then confirm.",
            code = "var range by remember {\n" +
                "    mutableStateOf<Pair<LocalDate, LocalDate>?>(null)\n" +
                "}\n" +
                "NDatePicker(value = range, onValueChange = { range = it }, clearable = true)",
        ) {
            var range by remember {
                mutableStateOf<Pair<LocalDate, LocalDate>?>(
                    LocalDate(2024, 5, 12) to LocalDate(2024, 5, 22),
                )
            }
            NSpace(vertical = true, size = 10.dp) {
                NDatePicker(value = range, onValueChange = { range = it }, clearable = true)
                NText(
                    "Value: ${range?.let { "${it.first} to ${it.second}" } ?: "null"}",
                    depth = 3,
                )
            }
        }

        DemoSection(
            title = "Month and year",
            description = "Use NDatePickerType.Month or NDatePickerType.Year to pick a coarser value. Month resolves to the first day of the month and year to January 1st.",
            code = "NDatePicker(value = month, onValueChange = { month = it }, type = NDatePickerType.Month)\n" +
                "NDatePicker(value = year, onValueChange = { year = it }, type = NDatePickerType.Year)",
        ) {
            var month by remember { mutableStateOf<LocalDate?>(LocalDate(2024, 5, 1)) }
            var year by remember { mutableStateOf<LocalDate?>(LocalDate(2024, 1, 1)) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NDatePicker(
                    value = month,
                    onValueChange = { month = it },
                    type = NDatePickerType.Month,
                    format = "MMMM yyyy",
                    clearable = true,
                )
                NDatePicker(
                    value = year,
                    onValueChange = { year = it },
                    type = NDatePickerType.Year,
                    format = "yyyy",
                    clearable = true,
                )
                NText("Month: ${month ?: "null"} · Year: ${year ?: "null"}", depth = 3)
            }
        }

        DemoSection(
            title = "Min, max and disabled dates",
            description = "min and max bound the selectable range; isDateDisabled can reject individual dates such as weekends.",
            code = "NDatePicker(\n" +
                "    value = date,\n" +
                "    onValueChange = { date = it },\n" +
                "    min = LocalDate(2024, 5, 10),\n" +
                "    max = LocalDate(2024, 5, 25),\n" +
                "    isDateDisabled = {\n" +
                "        it.dayOfWeek == DayOfWeek.SATURDAY || it.dayOfWeek == DayOfWeek.SUNDAY\n" +
                "    },\n" +
                ")",
        ) {
            var date by remember { mutableStateOf<LocalDate?>(LocalDate(2024, 5, 15)) }
            NSpace(vertical = true, size = 10.dp) {
                NDatePicker(
                    value = date,
                    onValueChange = { date = it },
                    min = LocalDate(2024, 5, 10),
                    max = LocalDate(2024, 5, 25),
                    isDateDisabled = {
                        it.dayOfWeek == DayOfWeek.SATURDAY || it.dayOfWeek == DayOfWeek.SUNDAY
                    },
                    clearable = true,
                )
                NText("Value: ${date ?: "null"}", depth = 3)
            }
        }

        DemoSection(
            title = "Shortcuts",
            description = "The shortcuts slot receives a selector that commits the value and closes the panel. Range pickers get a selector for pairs.",
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
            var range by remember {
                mutableStateOf<Pair<LocalDate, LocalDate>?>(null)
            }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NDatePicker(
                    value = date,
                    onValueChange = { date = it },
                    shortcuts = { select ->
                        NButton(
                            text = true,
                            size = NSize.Small,
                            onClick = { select(LocalDate(2024, 5, 20)) },
                        ) { NText("Today") }
                        NButton(
                            text = true,
                            size = NSize.Small,
                            onClick = { select(LocalDate(2024, 12, 31)) },
                        ) { NText("Year end") }
                    },
                )
                NDatePicker(
                    value = range,
                    onValueChange = { range = it },
                    shortcuts = { select ->
                        NButton(
                            text = true,
                            size = NSize.Small,
                            onClick = {
                                select(LocalDate(2024, 5, 1) to LocalDate(2024, 5, 7))
                            },
                        ) { NText("First week") }
                        NButton(
                            text = true,
                            size = NSize.Small,
                            onClick = {
                                select(LocalDate(2024, 6, 1) to LocalDate(2024, 6, 30))
                            },
                        ) { NText("June") }
                    },
                )
                NText("Date: ${date ?: "null"} · Range: ${range ?: "null"}", depth = 3)
            }
        }
    }
}
