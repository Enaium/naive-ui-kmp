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
import cn.enaium.naiveui.components.NInput
import cn.enaium.naiveui.components.NInputType
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
            "clearable and word count affordances.",
    ) {
        var basic by remember { mutableStateOf("") }
        DemoSection(
            title = "Basic",
            description = "Basic use of n-input, as text and as a textarea.",
            code = "NInput(value = value, onValueChange = { value = it }, placeholder = \"Basic Input\")",
        ) {
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInput(
                    value = basic,
                    onValueChange = { basic = it },
                    placeholder = "Basic Input",
                )
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
            title = "Password",
            description = "Password inputs show a visibility toggle.",
        ) {
            var password by remember { mutableStateOf("") }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInput(
                    value = password,
                    onValueChange = { password = it },
                    type = NInputType.Password,
                    placeholder = "Password",
                    maxlength = 8,
                )
                NInput(
                    value = password,
                    onValueChange = { password = it },
                    type = NInputType.Password,
                    placeholder = "Password with clear button",
                    maxlength = 8,
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
            title = "Disabled & readonly",
            description = "Disabled inputs ignore all interaction; readonly inputs can still be focused.",
        ) {
            Row(
                Modifier.width(320.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NInput(
                    value = "Disabled",
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    disabled = true,
                )
                NInput(
                    value = "Readonly",
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    readonly = true,
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
            title = "Events",
            description = "Use onPressEnter, onFocus and onBlur to react to user interaction.",
        ) {
            var eventValue by remember { mutableStateOf("") }
            var lastEvent by remember { mutableStateOf("No events yet") }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NInput(
                    value = eventValue,
                    onValueChange = { eventValue = it },
                    placeholder = "Interact to trigger events",
                    onPressEnter = { lastEvent = "Enter pressed: $eventValue" },
                    onFocus = { lastEvent = "Focus" },
                    onBlur = { lastEvent = "Blur" },
                )
                NText(lastEvent, color = theme.colors.text3, size = theme.typography.fontSize.small)
            }
        }
    }
}
