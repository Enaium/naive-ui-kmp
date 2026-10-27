package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NCalendar
import cn.enaium.naiveui.components.NCalendarType
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Composable
fun CalendarDocs() {
    DocsPage(
        title = "Calendar",
        description = "A month calendar with selectable days, custom day content and an optional time picker.",
    ) {
        DemoSection(
            title = "Basic",
            description = "The panel starts on the value's month. Clicking the month or year name opens the month and year panels; onPanelChange reports every panel navigation.",
            code = "var date by remember { mutableStateOf(LocalDate(2024, 5, 20)) }\n" +
                "var panel by remember { mutableStateOf(\"\") }\n" +
                "NCalendar(\n" +
                "    value = date,\n" +
                "    onValueChange = { date = it },\n" +
                "    onPanelChange = { year, month -> panel = \"\$year-\$month\" },\n" +
                ")",
        ) {
            var date by remember { mutableStateOf(LocalDate(2024, 5, 20)) }
            var panel by remember { mutableStateOf("2024-05") }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NCalendar(
                    value = date,
                    onValueChange = { date = it },
                    modifier = Modifier.fillMaxWidth(),
                    onPanelChange = { year, month ->
                        panel = "$year-${month.toString().padStart(2, '0')}"
                    },
                )
                NText("Selected: $date · Panel: $panel", depth = 3)
            }
        }

        DemoSection(
            title = "Events and slots",
            description = "The day slot renders events inside each cell. The header slot replaces the default month/year label.",
            code = "NCalendar(\n" +
                "    value = date,\n" +
                "    onValueChange = { date = it },\n" +
                "    day = { day ->\n" +
                "        if (day.day in eventDays) {\n" +
                "            Box(Modifier.size(6.dp).clip(CircleShape).background(\n" +
                "                LocalNTheme.current.colors.success,\n" +
                "            ))\n" +
                "        }\n" +
                "    },\n" +
                ")",
        ) {
            var date by remember { mutableStateOf(LocalDate(2024, 5, 8)) }
            val eventDays = remember { setOf(3, 8, 12, 21, 27) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NCalendar(
                    value = date,
                    onValueChange = { date = it },
                    modifier = Modifier.fillMaxWidth(),
                    header = { year, month ->
                        NText(
                            "${LocalNLocale.current.date.months[month - 1]} $year",
                            size = 15.sp,
                            strong = true,
                            color = LocalNTheme.current.colors.text1,
                        )
                    },
                    day = { day ->
                        if (day.day in eventDays) {
                            Box(
                                Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(LocalNTheme.current.colors.success),
                            )
                        }
                    },
                )
                NText("Selected: $date · Event days: ${eventDays.sorted()}", depth = 3)
            }
        }

        DemoSection(
            title = "Week start and header actions",
            description = "firstDayOfWeek uses ISO numbering (1 = Monday, 7 = Sunday). showMonth and showYear control which header labels can open the month and year panels.",
            code = "NCalendar(\n" +
                "    value = date,\n" +
                "    onValueChange = { date = it },\n" +
                "    firstDayOfWeek = 7,\n" +
                "    showMonth = true,\n" +
                "    showYear = false,\n" +
                ")",
        ) {
            var date by remember { mutableStateOf(LocalDate(2024, 2, 29)) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NCalendar(
                    value = date,
                    onValueChange = { date = it },
                    modifier = Modifier.fillMaxWidth(),
                    firstDayOfWeek = 7,
                    showMonth = true,
                    showYear = false,
                )
                NText("Selected: $date (leap day)", depth = 3)
            }
        }

        DemoSection(
            title = "Date time",
            description = "With NCalendarType.Datetime a time picker is appended below the grid; the date and time are reported separately.",
            code = "NCalendar(\n" +
                "    value = date,\n" +
                "    onValueChange = { date = it },\n" +
                "    type = NCalendarType.Datetime,\n" +
                "    time = time,\n" +
                "    onTimeChange = { time = it },\n" +
                ")",
        ) {
            var date by remember { mutableStateOf(LocalDate(2024, 5, 20)) }
            var time by remember { mutableStateOf<LocalTime?>(LocalTime(9, 30)) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NCalendar(
                    value = date,
                    onValueChange = { date = it },
                    modifier = Modifier.fillMaxWidth(),
                    type = NCalendarType.Datetime,
                    time = time,
                    onTimeChange = { time = it },
                )
                NText("Selected: $date ${time ?: ""}", depth = 3)
            }
        }
    }
}
