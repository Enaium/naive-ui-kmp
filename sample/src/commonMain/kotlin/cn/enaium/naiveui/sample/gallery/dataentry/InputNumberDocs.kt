package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NInputNumber
import cn.enaium.naiveui.components.NInputNumberButtonPlacement
import cn.enaium.naiveui.components.NSwitch
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import kotlin.math.abs

@Composable
fun InputNumberDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Input Number",
        description = "A numerical input with text editing, keyboard stepping, custom parsing and " +
            "increment/decrement controls.",
    ) {
        var basic by remember { mutableStateOf<Int?>(0) }
        DemoSection(
            title = "Basic",
            description = "Type a number or use the step buttons.",
            code = "NInputNumber(value = value, onValueChange = { value = it })",
        ) {
            NInputNumber(
                value = basic,
                onValueChange = { basic = it },
                modifier = Modifier.width(240.dp),
                clearable = true,
            )
        }

        DemoSection(
            title = "Min and max",
            description = "Values are clamped to the min and max bounds; the buttons disable at the limits.",
        ) {
            var minMax by remember { mutableStateOf<Int?>(0) }
            Column(Modifier.width(240.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInputNumber(
                    value = minMax,
                    onValueChange = { minMax = it },
                    min = -3,
                    max = 5,
                    placeholder = "Min -3, max 5",
                )
                NInputNumber(
                    value = minMax,
                    onValueChange = { minMax = it },
                    min = -5,
                    max = 3,
                    placeholder = "Min -5, max 3",
                )
            }
        }

        DemoSection(
            title = "Precision",
            description = "precision controls the number of decimal places applied when the value is committed.",
        ) {
            var precise by remember { mutableStateOf<Double?>(null) }
            Column(Modifier.width(240.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInputNumber(
                    value = precise,
                    onValueChange = { precise = it },
                    precision = 2,
                    step = 0.1,
                    clearable = true,
                )
                NInputNumber(
                    value = precise,
                    onValueChange = { precise = it },
                    precision = 0,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Step",
            description = "Set how much the buttons and arrow keys change the value.",
            code = "NInputNumber(value = value, onValueChange = { value = it }, step = 2)",
        ) {
            var stepped by remember { mutableStateOf<Int?>(0) }
            NInputNumber(
                value = stepped,
                onValueChange = { stepped = it },
                modifier = Modifier.width(240.dp),
                step = 2,
            )
        }

        DemoSection(
            title = "Button placement",
            description = "Buttons can sit on both ends of the input, with optional prefix and suffix content.",
        ) {
            var both by remember { mutableStateOf<Double?>(0.0) }
            Column(Modifier.width(260.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInputNumber(
                    value = both,
                    onValueChange = { both = it },
                    buttonPlacement = NInputNumberButtonPlacement.Both,
                )
                NInputNumber(
                    value = both,
                    onValueChange = { both = it },
                    buttonPlacement = NInputNumberButtonPlacement.Both,
                    prefix = { NText("\$", color = theme.colors.text3) },
                )
                NInputNumber(
                    value = both,
                    onValueChange = { both = it },
                    buttonPlacement = NInputNumberButtonPlacement.Both,
                    suffix = { NText("฿", color = theme.colors.text3) },
                )
            }
        }

        DemoSection(
            title = "Hide button",
            description = "showButton controls whether the increment/decrement controls are rendered.",
        ) {
            var showButton by remember { mutableStateOf(true) }
            var value by remember { mutableStateOf<Int?>(0) }
            Row(
                Modifier.width(260.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NButton(onClick = { showButton = !showButton }) {
                    NText(if (showButton) "Hide buttons" else "Show buttons")
                }
                NInputNumber(
                    value = value,
                    onValueChange = { value = it },
                    modifier = Modifier.weight(1f),
                    showButton = showButton,
                )
            }
        }

        DemoSection(
            title = "Size",
            description = "Tiny, small, medium and large sizes.",
        ) {
            var sized by remember { mutableStateOf<Int?>(0) }
            Column(Modifier.width(240.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInputNumber(value = sized, onValueChange = { sized = it }, size = NSize.Tiny)
                NInputNumber(value = sized, onValueChange = { sized = it }, size = NSize.Small)
                NInputNumber(value = sized, onValueChange = { sized = it }, size = NSize.Medium)
                NInputNumber(value = sized, onValueChange = { sized = it }, size = NSize.Large)
            }
        }

        DemoSection(
            title = "Validation status",
            description = "Validation status can be applied outside a form.",
        ) {
            var warning by remember { mutableStateOf<Double?>(null) }
            var error by remember { mutableStateOf<Double?>(null) }
            Column(Modifier.width(240.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInputNumber(
                    value = warning,
                    onValueChange = { warning = it },
                    status = NStatus.Warning,
                    placeholder = "Warning",
                )
                NInputNumber(
                    value = error,
                    onValueChange = { error = it },
                    status = NStatus.Error,
                    placeholder = "Error",
                )
            }
        }

        DemoSection(
            title = "Disabled",
            description = "A disabled input ignores typing and button presses.",
        ) {
            NInputNumber(
                value = 42.0,
                onValueChange = {},
                modifier = Modifier.width(240.dp),
                disabled = true,
            )
        }

        DemoSection(
            title = "Timing to change",
            description = "Set updateValueOnInput to false to commit the value only on blur or Enter.",
        ) {
            var timing by remember { mutableStateOf<Double?>(35.0) }
            Column(Modifier.width(260.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInputNumber(
                    value = timing,
                    onValueChange = { timing = it },
                    updateValueOnInput = false,
                    min = 20.0,
                    max = 50.0,
                    placeholder = "Updates on blur",
                )
                NInputNumber(
                    value = timing,
                    onValueChange = { timing = it },
                    min = 20.0,
                    max = 50.0,
                    placeholder = "Updates on input",
                )
                NText("Value: ${timing ?: "null"}", color = theme.colors.text3, size = theme.typography.fontSize.small)
            }
        }

        DemoSection(
            title = "Custom icon",
            description = "Override the increment and decrement icons.",
        ) {
            var iconValue by remember { mutableStateOf<Int?>(0) }
            NInputNumber(
                value = iconValue,
                onValueChange = { iconValue = it },
                modifier = Modifier.width(240.dp),
                minusIcon = NIcons.ArrowDown,
                addIcon = NIcons.ArrowUp,
            )
        }

        DemoSection(
            title = "Disable keyboard arrow up & down",
            description = "keyboardEnabled = false stops ArrowUp and ArrowDown from changing the value.",
        ) {
            var keyboardValue by remember { mutableStateOf<Int?>(0) }
            NInputNumber(
                value = keyboardValue,
                onValueChange = { keyboardValue = it },
                modifier = Modifier.width(240.dp),
                keyboardEnabled = false,
            )
        }

        DemoSection(
            title = "Event",
            description = "onFocus, onBlur and onValueChange expose the interaction lifecycle.",
        ) {
            var eventValue by remember { mutableStateOf<Double?>(0.0) }
            var lastEvent by remember { mutableStateOf("No events yet") }
            Column(Modifier.width(260.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NInputNumber(
                    value = eventValue,
                    onValueChange = {
                        eventValue = it
                        lastEvent = "Value: ${it ?: "null"}"
                    },
                    onFocus = { lastEvent = "Focus" },
                    onBlur = { lastEvent = "Blur" },
                )
                NText(lastEvent, color = theme.colors.text3, size = theme.typography.fontSize.small)
            }
        }

        DemoSection(
            title = "Prefix & suffix",
            description = "Add prefix and suffix content around the number field.",
        ) {
            var iconValue by remember { mutableStateOf<Int?>(0) }
            Column(Modifier.width(240.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInputNumber(
                    value = iconValue,
                    onValueChange = { iconValue = it },
                    prefix = { NText("\$", color = theme.colors.text3) },
                )
                NInputNumber(
                    value = iconValue,
                    onValueChange = { iconValue = it },
                    suffix = { NText("%", color = theme.colors.text3) },
                )
                NInputNumber(
                    value = iconValue,
                    onValueChange = { iconValue = it },
                    showButton = false,
                    suffix = { NText("%", color = theme.colors.text3) },
                )
            }
        }

        DemoSection(
            title = "Loading",
            description = "The loading prop shows a spinner inside the number field.",
        ) {
            var loadingValue by remember { mutableStateOf<Int?>(0) }
            var loading by remember { mutableStateOf(false) }
            Row(
                Modifier.width(260.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NInputNumber(
                    value = loadingValue,
                    onValueChange = { loadingValue = it },
                    modifier = Modifier.weight(1f),
                    clearable = true,
                    loading = loading,
                )
                NSwitch(checked = loading, onCheckedChange = { loading = it })
            }
        }

        DemoSection(
            title = "Custom parsing",
            description = "parse and format add thousand separators or currency suffixes. Custom parsing " +
                "defers the value update until the field is committed.",
        ) {
            var grouped by remember { mutableStateOf<Double?>(1075.0) }
            var currency by remember { mutableStateOf<Double?>(1075.0) }
            Column(Modifier.width(260.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInputNumber(
                    value = grouped,
                    onValueChange = { grouped = it },
                    parse = { parseGrouped(it) },
                    format = { formatGrouped(it) },
                )
                NInputNumber(
                    value = currency,
                    onValueChange = { currency = it },
                    parse = { parseCurrency(it) },
                    format = { it?.let { value -> groupThousands(value) + " \$" } ?: "" },
                )
            }
        }

        DemoSection(
            title = "Validator",
            description = "validator rejects values that do not pass the custom check; the field reverts on blur.",
        ) {
            var validated by remember { mutableStateOf<Double?>(0.0) }
            Column(Modifier.width(260.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NInputNumber(
                    value = validated,
                    onValueChange = { validated = it },
                    validator = { it > 0 },
                )
                NText(
                    text = "Must be greater than 0. Current: ${validated ?: "null"}",
                    color = theme.colors.text3,
                    size = theme.typography.fontSize.small,
                )
            }
        }
    }
}

private fun groupThousands(value: Double): String {
    val negative = value < 0
    val absolute = abs(value)
    val whole = absolute.toLong()
    val grouped = whole.toString().reversed().chunked(3).joinToString(",").reversed()
    val fraction = absolute.toString().substringAfter('.', "")
    val text = if (fraction.isEmpty()) grouped else "$grouped.$fraction"
    return if (negative) "-$text" else text
}

private fun parseGrouped(input: String): Double? {
    val cleaned = input.replace(",", "").trim()
    if (cleaned.isEmpty()) return null
    return cleaned.toDoubleOrNull()
}

private fun formatGrouped(value: Double?): String =
    value?.let { groupThousands(it) } ?: ""

private fun parseCurrency(input: String): Double? {
    val cleaned = input.replace("\$", "").replace(",", "").trim()
    if (cleaned.isEmpty()) return null
    return cleaned.toDoubleOrNull()
}
