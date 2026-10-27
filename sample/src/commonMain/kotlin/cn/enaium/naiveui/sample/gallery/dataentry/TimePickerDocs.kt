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
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NTimePicker
import cn.enaium.naiveui.components.NTimePickerAction
import cn.enaium.naiveui.components.rememberNTimePickerController
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone

@Composable
fun TimePickerDocs() {
    DocsPage(
        title = "Time Picker",
        description = "Hour, minute and second columns bound to a LocalTime value.",
    ) {
        BasicDemo()
        ActionsDemo()
        ConfirmDemo()
        DisabledTimeDemo()
        FocusDemo()
        FormatDemo()
        FormattedDemo()
        Hours12Demo()
        SizeDemo()
        StatusDemo()
        StepDemo()
        TimeZoneDemo()
    }
}

@Composable
private fun BasicDemo() {
    DemoSection(
        title = "Basic",
        description = "An empty picker shows a placeholder; the second starts at a fixed time.",
        code = "NTimePicker(value = time, onValueChange = { time = it }, clearable = true)",
    ) {
        var empty by remember { mutableStateOf<LocalTime?>(null) }
        var fixed by remember { mutableStateOf<LocalTime?>(LocalTime(0, 12, 13)) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NSpace(size = 12.dp) {
                NTimePicker(value = empty, onValueChange = { empty = it }, clearable = true)
                NTimePicker(value = fixed, onValueChange = { fixed = it }, clearable = true)
            }
            NText("Empty: ${empty ?: "null"} · Fixed: ${fixed ?: "null"}", depth = 3)
        }
    }
}

@Composable
private fun ActionsDemo() {
    DemoSection(
        title = "Actions",
        description = "actions controls the footer. Empty hides it; values still update as columns are clicked.",
        code = "NTimePicker(..., actions = setOf(NTimePickerAction.Now, NTimePickerAction.Clear))\n" +
            "NTimePicker(..., actions = emptySet())",
    ) {
        var first by remember { mutableStateOf<LocalTime?>(null) }
        var second by remember { mutableStateOf<LocalTime?>(LocalTime(10, 30)) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NTimePicker(
                value = first,
                onValueChange = { first = it },
                actions = setOf(NTimePickerAction.Now, NTimePickerAction.Clear),
                clearable = true,
            )
            NTimePicker(
                value = second,
                onValueChange = { second = it },
                actions = emptySet(),
                clearable = true,
            )
            NText("First: ${first ?: "null"} · Second: ${second ?: "null"}", depth = 3)
        }
    }
}

@Composable
private fun ConfirmDemo() {
    DemoSection(
        title = "Confirm",
        description = "onConfirm fires when the confirm footer action is clicked.",
        code = "NTimePicker(\n" +
            "    value = time,\n" +
            "    onValueChange = { time = it },\n" +
            "    onConfirm = { log = \"Confirm \$it\" },\n" +
            ")",
    ) {
        var time by remember { mutableStateOf<LocalTime?>(LocalTime(0, 12, 13)) }
        var log by remember { mutableStateOf("No confirm yet") }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NTimePicker(
                value = time,
                onValueChange = { time = it },
                onConfirm = { log = "Confirm $it" },
                clearable = true,
            )
            NText(log, depth = 3)
        }
    }
}

@Composable
private fun DisabledTimeDemo() {
    DemoSection(
        title = "Disable time",
        description = "Even hours are disabled; before noon minutes below 30 are disabled.",
        code = "NTimePicker(\n" +
            "    value = time,\n" +
            "    onValueChange = { time = it },\n" +
            "    isHourDisabled = { it % 2 == 0 },\n" +
            "    isMinuteDisabled = { it < 30 },\n" +
            ")",
    ) {
        var time by remember { mutableStateOf<LocalTime?>(null) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NTimePicker(
                value = time,
                onValueChange = { time = it },
                isHourDisabled = { it % 2 == 0 },
                isMinuteDisabled = { minute ->
                    val hour = time?.hour
                    hour != null && hour < 12 && minute < 30
                },
                isSecondDisabled = { it < 40 },
                clearable = true,
            )
            NText("Value: ${time ?: "null"}", depth = 3)
        }
    }
}

@Composable
private fun FocusDemo() {
    DemoSection(
        title = "Focus and blur",
        description = "A controller focuses the picker and blurs it after one second.",
        code = "val controller = rememberNTimePickerController()\n" +
            "NButton(onClick = { controller.focus() }) { NText(\"Focus\") }\n" +
            "NTimePicker(value = time, onValueChange = { time = it }, controller = controller)",
    ) {
        val controller = rememberNTimePickerController()
        val scope = rememberCoroutineScope()
        var time by remember { mutableStateOf<LocalTime?>(LocalTime(9, 30)) }
        NSpace(size = 12.dp) {
            NButton(onClick = {
                controller.focus()
                scope.launch {
                    delay(1000)
                    controller.blur()
                }
            }) { NText("Focus then blur in 1 second") }
            NTimePicker(
                value = time,
                onValueChange = { time = it },
                controller = controller,
                modifier = Modifier.width(200.dp),
            )
        }
    }
}

@Composable
private fun FormatDemo() {
    DemoSection(
        title = "Format",
        description = "Format only changes the trigger text; seconds are hidden when the format has no seconds token.",
        code = "NTimePicker(value = time, onValueChange = { time = it }, format = \"h:mm a\", use12Hours = true)",
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

@Composable
private fun FormattedDemo() {
    DemoSection(
        title = "Use formatted value",
        description = "formattedValue overrides the trigger text while onFormattedValueChange reports the token-formatted value.",
        code = "var formatted by remember { mutableStateOf(\"4~1~8\") }\n" +
            "NTimePicker(\n" +
            "    value = time,\n" +
            "    onValueChange = { time = it },\n" +
            "    format = \"H~m~s\",\n" +
            "    formattedValue = formatted,\n" +
            "    onFormattedValueChange = { formatted = it },\n" +
            ")",
    ) {
        var time by remember { mutableStateOf<LocalTime?>(LocalTime(4, 1, 8)) }
        var formatted by remember { mutableStateOf<String?>("4~1~8") }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NTimePicker(
                value = time,
                onValueChange = { time = it },
                format = "H~m~s",
                formattedValue = formatted,
                onFormattedValueChange = { formatted = it },
                clearable = true,
            )
            NText("Typed: ${time ?: "null"} · Formatted: ${formatted ?: "null"}", depth = 3)
        }
    }
}

@Composable
private fun Hours12Demo() {
    DemoSection(
        title = "12 hours",
        description = "use12Hours swaps the hour column for a 1-12 clock plus an AM/PM column.",
        code = "NTimePicker(value = time, onValueChange = { time = it }, use12Hours = true)",
    ) {
        var empty by remember { mutableStateOf<LocalTime?>(null) }
        var fixed by remember { mutableStateOf<LocalTime?>(LocalTime(15, 40, 5)) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NSpace(size = 12.dp) {
                NTimePicker(
                    value = empty,
                    onValueChange = { empty = it },
                    use12Hours = true,
                    clearable = true,
                )
                NTimePicker(
                    value = fixed,
                    onValueChange = { fixed = it },
                    use12Hours = true,
                    format = "h:mm:ss a",
                    clearable = true,
                )
            }
            NText("Empty: ${empty ?: "null"} · Fixed: ${fixed ?: "null"}", depth = 3)
        }
    }
}

@Composable
private fun SizeDemo() {
    DemoSection(
        title = "Size",
        description = "Small, medium and large pickers share one value.",
        code = "NTimePicker(value = time, onValueChange = { time = it }, size = NSize.Small)",
    ) {
        var time by remember { mutableStateOf<LocalTime?>(LocalTime(10, 30)) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NTimePicker(value = time, onValueChange = { time = it }, size = NSize.Small)
            NTimePicker(value = time, onValueChange = { time = it }, size = NSize.Medium)
            NTimePicker(value = time, onValueChange = { time = it }, size = NSize.Large)
        }
    }
}

@Composable
private fun StatusDemo() {
    DemoSection(
        title = "Validation status",
        description = "Warning and error statuses color the trigger border.",
        code = "NTimePicker(value = null, onValueChange = {}, status = NStatus.Warning, placeholder = \"\")",
    ) {
        var warning by remember { mutableStateOf<LocalTime?>(null) }
        var error by remember { mutableStateOf<LocalTime?>(null) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NTimePicker(
                value = warning,
                onValueChange = { warning = it },
                status = NStatus.Warning,
                placeholder = "",
            )
            NTimePicker(
                value = error,
                onValueChange = { error = it },
                status = NStatus.Error,
                placeholder = "",
            )
        }
    }
}

@Composable
private fun StepDemo() {
    DemoSection(
        title = "Step time",
        description = "Steps skip column values; values outside a step are simply not shown.",
        code = "NTimePicker(\n" +
            "    value = time,\n" +
            "    onValueChange = { time = it },\n" +
            "    hourStep = 3,\n" +
            "    minuteStep = 15,\n" +
            "    secondStep = 30,\n" +
            ")",
    ) {
        var time by remember { mutableStateOf<LocalTime?>(LocalTime(9, 0, 0)) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
}

@Composable
private fun TimeZoneDemo() {
    DemoSection(
        title = "Time zone",
        description = "Values are local times; timeZone decides which wall clock the Now action uses. Try Now in each picker.",
        code = "NTimePicker(\n" +
            "    value = time,\n" +
            "    onValueChange = { time = it },\n" +
            "    timeZone = TimeZone.of(\"Asia/Shanghai\"),\n" +
            ")",
    ) {
        var time by remember { mutableStateOf<LocalTime?>(LocalTime(10, 0)) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NText("Shanghai", depth = 3)
            NTimePicker(
                value = time,
                onValueChange = { time = it },
                timeZone = TimeZone.of("Asia/Shanghai"),
                actions = setOf(NTimePickerAction.Now),
                clearable = true,
            )
            NText("Pittsburgh", depth = 3)
            NTimePicker(
                value = time,
                onValueChange = { time = it },
                timeZone = TimeZone.of("America/New_York"),
                actions = setOf(NTimePickerAction.Now),
                clearable = true,
            )
            NText("UTC", depth = 3)
            NTimePicker(
                value = time,
                onValueChange = { time = it },
                timeZone = TimeZone.UTC,
                actions = setOf(NTimePickerAction.Now),
                clearable = true,
            )
            NText("Value: ${time ?: "null"}", depth = 3)
        }
    }
}
