package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NTimePicker
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import kotlinx.datetime.LocalTime

@Composable
fun TimePickerDocs() {
    DocsPage(
        title = "Time Picker",
        description = "Like a digital clock. Hour, minute and second columns update a LocalTime value.",
    ) {
        DemoSection(
            title = "Basic",
            description = "An empty picker shows a placeholder; the second picker starts at a fixed time. The footer exposes Now and Confirm.",
            code = "var time by remember { mutableStateOf<LocalTime?>(null) }\n" +
                "NTimePicker(value = time, onValueChange = { time = it }, clearable = true)",
        ) {
            var empty by remember { mutableStateOf<LocalTime?>(null) }
            var fixed by remember { mutableStateOf<LocalTime?>(LocalTime(0, 12, 13)) }
            NSpace(size = 12.dp) {
                NTimePicker(value = empty, onValueChange = { empty = it }, clearable = true)
                NTimePicker(value = fixed, onValueChange = { fixed = it }, clearable = true)
            }
            NText("Empty: ${empty ?: "null"} · Fixed: ${fixed ?: "null"}", depth = 3)
        }

        DemoSection(
            title = "12 hours",
            description = "use12Hours replaces the hour column with a 1-12 clock and an AM/PM column.",
            code = "NTimePicker(\n" +
                "    value = time,\n" +
                "    onValueChange = { time = it },\n" +
                "    use12Hours = true,\n" +
                "    format = \"h:mm:ss a\",\n" +
                ")",
        ) {
            var time by remember { mutableStateOf<LocalTime?>(LocalTime(15, 40, 5)) }
            NSpace(vertical = true, size = 10.dp) {
                NTimePicker(
                    value = time,
                    onValueChange = { time = it },
                    use12Hours = true,
                    format = "h:mm:ss a",
                    clearable = true,
                )
                NText("Value: ${time ?: "null"}", depth = 3)
            }
        }

        DemoSection(
            title = "Step time",
            description = "Steps reduce the values shown in each column. Steps larger than one skip values; a value outside the step is not highlighted.",
            code = "NTimePicker(\n" +
                "    value = time,\n" +
                "    onValueChange = { time = it },\n" +
                "    hourStep = 3,\n" +
                "    minuteStep = 15,\n" +
                "    secondStep = 30,\n" +
                ")",
        ) {
            var time by remember { mutableStateOf<LocalTime?>(LocalTime(9, 0, 0)) }
            NSpace(vertical = true, size = 10.dp) {
                NTimePicker(
                    value = time,
                    onValueChange = { time = it },
                    hourStep = 3,
                    minuteStep = 15,
                    secondStep = 30,
                    clearable = true,
                )
                NText("Value: ${time ?: "null"}", depth = 3)
            }
        }

        DemoSection(
            title = "Format",
            description = "The format controls the trigger text, not the stored value. When the format has no seconds token the seconds column is hidden.",
            code = "NTimePicker(value = time, onValueChange = { time = it }, format = \"h:mm a\", use12Hours = true)\n" +
                "NTimePicker(value = time, onValueChange = { time = it }, format = \"HH:mm\")",
        ) {
            var twelve by remember { mutableStateOf<LocalTime?>(LocalTime(8, 5)) }
            var twentyFour by remember { mutableStateOf<LocalTime?>(LocalTime(20, 45)) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NTimePicker(
                    value = twelve,
                    onValueChange = { twelve = it },
                    format = "h:mm a",
                    use12Hours = true,
                    clearable = true,
                )
                NTimePicker(
                    value = twentyFour,
                    onValueChange = { twentyFour = it },
                    format = "HH:mm",
                    showSeconds = false,
                    clearable = true,
                )
                NText("12h: ${twelve ?: "null"} · 24h: ${twentyFour ?: "null"}", depth = 3)
            }
        }
    }
}
