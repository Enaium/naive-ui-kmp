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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NInput
import cn.enaium.naiveui.components.NInputController
import cn.enaium.naiveui.components.NInputGroup
import cn.enaium.naiveui.components.NInputGroupLabel
import cn.enaium.naiveui.components.NInputNumber
import cn.enaium.naiveui.components.NInputType
import cn.enaium.naiveui.components.NSwitch
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus

@Composable
fun InputDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Input",
        description = "Text inputs with text, password and textarea types, prefix/suffix slots, " +
            "clearable, word count, formatting constraints and input groups.",
    ) {
        var basic by remember { mutableStateOf("") }
        DemoSection(
            title = "Basic",
            description = "Basic use of n-input, as text and as a textarea.",
            code = "NInput(value = value, onValueChange = { value = it }, placeholder = \"Basic Input\")",
        ) {
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInput(value = basic, onValueChange = { basic = it }, placeholder = "Basic Input")
                NInput(
                    value = basic,
                    onValueChange = { basic = it },
                    placeholder = "Basic Textarea",
                    type = NInputType.Textarea,
                )
            }
        }

        DemoSection(
            title = "Size",
            description = "Tiny, small, medium and large inputs.",
        ) {
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInput(value = "", onValueChange = {}, size = NSize.Tiny, placeholder = "Tiny Input")
                NInput(value = "", onValueChange = {}, size = NSize.Small, placeholder = "Small Input")
                NInput(value = "", onValueChange = {}, placeholder = "Medium Input")
                NInput(value = "", onValueChange = {}, size = NSize.Large, placeholder = "Large Input")
            }
        }

        DemoSection(
            title = "Round corner",
            description = "Inputs can have rounded corners.",
        ) {
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInput(value = "", onValueChange = {}, size = NSize.Small, round = true, placeholder = "Small")
                NInput(value = "", onValueChange = {}, round = true, placeholder = "Medium")
                NInput(value = "", onValueChange = {}, size = NSize.Large, round = true, placeholder = "Large")
            }
        }

        DemoSection(
            title = "Prefix & suffix",
            description = "Set a prefix and/or suffix for the input.",
        ) {
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInput(
                    value = "",
                    onValueChange = {},
                    placeholder = "Flash",
                    prefix = { NIcon(NIcons.Search, size = 16.dp, color = theme.colors.icon) },
                )
                NInput(
                    value = "",
                    onValueChange = {},
                    placeholder = "1,400,000",
                    round = true,
                    suffix = { NText("\$", color = theme.colors.text3) },
                )
                NInput(
                    value = "",
                    onValueChange = {},
                    placeholder = "Flash",
                    round = true,
                    suffix = { NIcon(NIcons.Search, size = 16.dp, color = theme.colors.icon) },
                )
            }
        }

        DemoSection(
            title = "Loading",
            description = "The loading prop reserves space for a spinner inside the input.",
        ) {
            NInput(
                value = "",
                onValueChange = {},
                modifier = Modifier.width(320.dp),
                placeholder = "Loading",
                loading = true,
            )
        }

        DemoSection(
            title = "Password",
            description = "Password inputs show a visibility toggle.",
        ) {
            var password by remember { mutableStateOf("") }
            var visiblePassword by remember { mutableStateOf("") }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInput(
                    value = password,
                    onValueChange = { password = it },
                    type = NInputType.Password,
                    placeholder = "Password",
                    maxlength = 8,
                )
                NInput(
                    value = visiblePassword,
                    onValueChange = { visiblePassword = it },
                    type = NInputType.Password,
                    placeholder = "Password with clear button",
                    maxlength = 8,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Disabled",
            description = "Inputs can be disabled and re-enabled at runtime.",
        ) {
            var active by remember { mutableStateOf(false) }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInput(
                    value = "",
                    onValueChange = {},
                    size = NSize.Small,
                    placeholder = "Oops! It is disabled.",
                    round = true,
                    disabled = !active,
                )
                NInput(
                    value = "",
                    onValueChange = {},
                    type = NInputType.Textarea,
                    size = NSize.Small,
                    placeholder = "Oops! It is disabled.",
                    round = true,
                    disabled = !active,
                )
                NSwitch(checked = active, onCheckedChange = { active = it })
            }
        }

        DemoSection(
            title = "Clearable",
            description = "Make the input clearable after a value is set.",
        ) {
            var clearable by remember { mutableStateOf("Content is clearable") }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInput(
                    value = clearable,
                    onValueChange = { clearable = it },
                    placeholder = "Content is clearable",
                    clearable = true,
                )
                NInput(
                    value = clearable,
                    onValueChange = { clearable = it },
                    placeholder = "Clearable textarea",
                    type = NInputType.Textarea,
                    round = true,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Autosize textarea",
            description = "Textareas can grow between a minimum and maximum number of lines.",
        ) {
            var autoSize by remember { mutableStateOf("") }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInput(
                    value = autoSize,
                    onValueChange = { autoSize = it },
                    type = NInputType.Textarea,
                    placeholder = "Autosizable",
                    minLines = 3,
                    maxLines = 5,
                )
                NInput(
                    value = autoSize,
                    onValueChange = { autoSize = it },
                    type = NInputType.Textarea,
                    placeholder = "Minimum of three lines",
                    size = NSize.Small,
                    minLines = 3,
                )
            }
        }

        DemoSection(
            title = "Pairwise value",
            description = "The pair overload renders two values in a single frame, separated by a custom separator.",
        ) {
            var from by remember { mutableStateOf("") }
            var toValue by remember { mutableStateOf("") }
            var pairEvents by remember { mutableStateOf("Pair: no events") }
            NInput(
                value = from to toValue,
                onValueChange = {
                    from = it.first
                    toValue = it.second
                },
                modifier = Modifier.width(360.dp),
                placeholders = "From" to "To",
                separator = "-",
                clearable = true,
                suffix = { NIcon(NIcons.Search, size = 16.dp, color = theme.colors.icon) },
                onFocus = { pairEvents = "Pair: focus" },
                onBlur = { pairEvents = "Pair: blur" },
            )
            NText(pairEvents, color = theme.colors.text3, size = theme.typography.fontSize.small)
        }

        DemoSection(
            title = "Input group",
            description = "Inputs, numbers and labels can be joined into a single group.",
        ) {
            var groupStart by remember { mutableStateOf("") }
            var groupNumber by remember { mutableStateOf<Int?>(0) }
            var groupEnd by remember { mutableStateOf("") }
            var groupUrl by remember { mutableStateOf("") }
            Column(Modifier.width(440.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInputGroup(Modifier.width(440.dp)) {
                    NInput(
                        value = groupStart,
                        onValueChange = { groupStart = it },
                        modifier = Modifier.weight(1f),
                        size = NSize.Medium,
                    )
                    NInputNumber(
                        value = groupNumber,
                        onValueChange = { groupNumber = it },
                        modifier = Modifier.weight(1f),
                        showButton = false,
                    )
                    NInput(
                        value = groupEnd,
                        onValueChange = { groupEnd = it },
                        modifier = Modifier.weight(1f),
                    )
                }
                NInputGroup(Modifier.width(440.dp)) {
                    NInputGroupLabel { NText("https://www.") }
                    NInput(
                        value = groupUrl,
                        onValueChange = { groupUrl = it },
                        modifier = Modifier.weight(1f),
                    )
                    NInputGroupLabel { NText(".com") }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NButton(onClick = {}) { NText("Search") }
                    NInput(
                        value = groupUrl,
                        onValueChange = { groupUrl = it },
                        modifier = Modifier.width(200.dp),
                    )
                    NButton(onClick = {}, ghost = true, type = cn.enaium.naiveui.components.NButtonType.Primary) {
                        NText("Search")
                    }
                }
            }
        }

        DemoSection(
            title = "Passively activated",
            description = "Passively activated inputs are read-only until Enter is pressed while focused.",
        ) {
            var passive by remember { mutableStateOf("") }
            var passiveEvents by remember { mutableStateOf("Passive: focus a field and press Enter") }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInput(
                    value = passive,
                    onValueChange = { passive = it },
                    placeholder = "Focus, then press Enter to edit",
                    passivelyActivated = true,
                    onFocus = { passiveEvents = "Passive: focus" },
                    onBlur = { passiveEvents = "Passive: blur" },
                )
                NInput(
                    value = passive,
                    onValueChange = { passive = it },
                    placeholder = "Passive textarea",
                    type = NInputType.Textarea,
                    passivelyActivated = true,
                )
                NText(passiveEvents, color = theme.colors.text3, size = theme.typography.fontSize.small)
            }
        }

        DemoSection(
            title = "Word limit",
            description = "maxlength limits input and showCount displays the current word count.",
        ) {
            var countValue by remember { mutableStateOf("") }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInput(
                    value = countValue,
                    onValueChange = { countValue = it },
                    maxlength = 30,
                    showCount = true,
                    clearable = true,
                )
                NInput(
                    value = countValue,
                    onValueChange = { countValue = it },
                    type = NInputType.Textarea,
                    maxlength = 30,
                    showCount = true,
                )
            }
        }

        DemoSection(
            title = "Count graphemes",
            description = "countGraphemes changes both the displayed count and the maxlength check so " +
                "emoji clusters are counted as single characters.",
        ) {
            var graphemeValue by remember { mutableStateOf("🌷🏳️‍🌈") }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInput(
                    value = graphemeValue,
                    onValueChange = { graphemeValue = it },
                    placeholder = "Default count",
                    maxlength = 12,
                    showCount = true,
                )
                NInput(
                    value = graphemeValue,
                    onValueChange = { graphemeValue = it },
                    placeholder = "Grapheme count",
                    maxlength = 12,
                    showCount = true,
                    countGraphemes = { countGraphemes(it) },
                )
            }
        }

        DemoSection(
            title = "Imperative handle",
            description = "Use NInputController to clear, focus and blur the field programmatically.",
        ) {
            val controller = remember { NInputController() }
            var imperative by remember { mutableStateOf("Select me, then use the buttons") }
            Column(Modifier.width(360.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NButton(onClick = { controller.clear() }) { NText("Clear") }
                    NButton(onClick = { controller.focus() }) { NText("Focus") }
                    NButton(onClick = { controller.blur() }) { NText("Blur") }
                }
                NInput(value = imperative, onValueChange = { imperative = it }, controller = controller)
            }
        }

        DemoSection(
            title = "Events",
            description = "Use onFocus, onBlur, onPressEnter and onValueChange to react to interaction.",
        ) {
            var eventValue by remember { mutableStateOf("") }
            var lastEvent by remember { mutableStateOf("No events yet") }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NInput(
                    value = eventValue,
                    onValueChange = {
                        eventValue = it
                        lastEvent = "Input: $it"
                    },
                    placeholder = "Interact to trigger events",
                    onPressEnter = { lastEvent = "Enter pressed: $eventValue" },
                    onFocus = { lastEvent = "Focus" },
                    onBlur = { lastEvent = "Blur" },
                )
                NText(lastEvent, color = theme.colors.text3, size = theme.typography.fontSize.small)
            }
        }

        DemoSection(
            title = "Input props",
            description = "keyboardType selects the platform keyboard for URL and phone inputs.",
        ) {
            var url by remember { mutableStateOf("") }
            var tel by remember { mutableStateOf("") }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInput(
                    value = url,
                    onValueChange = { url = it },
                    placeholder = "URL",
                    keyboardType = KeyboardType.Uri,
                )
                NInput(
                    value = tel,
                    onValueChange = { tel = it },
                    placeholder = "Tel",
                    keyboardType = KeyboardType.Phone,
                )
            }
        }

        DemoSection(
            title = "Validation status",
            description = "Validation status can be applied outside a form.",
        ) {
            var success by remember { mutableStateOf("Looks good") }
            var warning by remember { mutableStateOf("") }
            var error by remember { mutableStateOf("") }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInput(
                    value = success,
                    onValueChange = { success = it },
                    status = NStatus.Success,
                    placeholder = "Success",
                )
                NInput(
                    value = warning,
                    onValueChange = { warning = it },
                    status = NStatus.Warning,
                    placeholder = "Warning",
                )
                NInput(
                    value = error,
                    onValueChange = { error = it },
                    status = NStatus.Error,
                    placeholder = "Error",
                )
            }
        }

        DemoSection(
            title = "Limit input format",
            description = "allowInput rejects values that do not match the desired format.",
        ) {
            var digits by remember { mutableStateOf("") }
            var noSpace by remember { mutableStateOf("") }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInput(
                    value = digits,
                    onValueChange = { digits = it },
                    allowInput = { it.all(Char::isDigit) },
                    placeholder = "Only allow number",
                )
                NInput(
                    value = noSpace,
                    onValueChange = { noSpace = it },
                    allowInput = { it.none(Char::isWhitespace) },
                    type = NInputType.Textarea,
                    placeholder = "No leading or trailing space",
                )
            }
        }

        DemoSection(
            title = "Trim",
            description = "trim removes whitespace at both ends of the value.",
        ) {
            var trimmed by remember { mutableStateOf("") }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInput(
                    value = trimmed,
                    onValueChange = { trimmed = it },
                    trim = true,
                    showCount = true,
                    placeholder = "Trimmed text",
                )
                NInput(
                    value = trimmed,
                    onValueChange = { trimmed = it },
                    type = NInputType.Textarea,
                    trim = true,
                    showCount = true,
                    placeholder = "Trimmed textarea",
                )
            }
        }
    }
}

private fun codePointAt(text: String, index: Int): Int {
    val high = text[index]
    if (high.isHighSurrogate() && index + 1 < text.length) {
        val low = text[index + 1]
        if (low.isLowSurrogate()) {
            return 0x10000 + ((high.code - 0xD800) shl 10) + (low.code - 0xDC00)
        }
    }
    return high.code
}

/**
 * Simplified grapheme cluster counter: handles surrogate pairs, ZWJ sequences,
 * variation selectors, skin tone modifiers and regional indicator pairs.
 */
private fun countGraphemes(text: String): Int {
    var count = 0
    var index = 0
    var joined = false
    while (index < text.length) {
        val codePoint = codePointAt(text, index)
        index += if (codePoint > 0xFFFF) 2 else 1
        when {
            codePoint == 0x200D -> joined = true
            codePoint == 0xFE0F || codePoint == 0xFE0E -> Unit
            codePoint in 0x1F3FB..0x1F3FF -> Unit
            codePoint in 0x0300..0x036F -> Unit
            joined -> joined = false
            codePoint in 0x1F1E6..0x1F1FF -> {
                if (index < text.length) {
                    val next = codePointAt(text, index)
                    if (next in 0x1F1E6..0x1F1FF) {
                        index += if (next > 0xFFFF) 2 else 1
                    }
                }
                count++
            }
            else -> count++
        }
    }
    return count
}
