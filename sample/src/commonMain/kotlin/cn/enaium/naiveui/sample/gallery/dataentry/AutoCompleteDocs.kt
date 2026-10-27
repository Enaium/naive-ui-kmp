package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NAutoComplete
import cn.enaium.naiveui.components.NAutoCompleteOption
import cn.enaium.naiveui.components.NInput
import cn.enaium.naiveui.components.NInputType
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun emailOptions(value: String, group: Boolean = false): List<NAutoCompleteOption> {
    val prefix = value.substringBefore('@')
    return listOf("Google" to "@gmail.com", "Netease" to "@163.com", "Tencent" to "@qq.com")
        .map { (name, suffix) ->
            val candidate = prefix + suffix
            NAutoCompleteOption(
                label = candidate,
                value = candidate,
                group = if (group) name else null,
            )
        }
}

@Composable
fun AutoCompleteDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Auto Complete",
        description = "An input that suggests matching options below it. Navigate with the arrow keys " +
            "and confirm with Enter.",
    ) {
        var basic by remember { mutableStateOf("") }
        DemoSection(
            title = "Basic",
            description = "Start typing to see email domain suggestions.",
            code = "NAutoComplete(value = value, onValueChange = { value = it }, options = options)",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NAutoComplete(
                    value = basic,
                    onValueChange = { basic = it },
                    modifier = Modifier.width(320.dp),
                    options = emailOptions(basic),
                    placeholder = "Email",
                    clearable = true,
                )
                NText(
                    text = if (basic.isEmpty()) "Value: (empty)" else "Value: $basic",
                    color = theme.colors.text3,
                    size = theme.typography.fontSize.small,
                )
            }
        }

        DemoSection(
            title = "Size",
            description = "Small, medium and large sizes.",
        ) {
            var sized by remember { mutableStateOf("") }
            val options = emailOptions(sized)
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NAutoComplete(
                    value = sized,
                    onValueChange = { sized = it },
                    options = options,
                    size = NSize.Small,
                    placeholder = "Email",
                )
                NAutoComplete(
                    value = sized,
                    onValueChange = { sized = it },
                    options = options,
                    size = NSize.Medium,
                    placeholder = "Email",
                )
                NAutoComplete(
                    value = sized,
                    onValueChange = { sized = it },
                    options = options,
                    size = NSize.Large,
                    placeholder = "Email",
                )
            }
        }

        DemoSection(
            title = "Group",
            description = "Options can declare a group; a header is rendered whenever the group changes.",
        ) {
            var grouped by remember { mutableStateOf("") }
            NAutoComplete(
                value = grouped,
                onValueChange = { grouped = it },
                modifier = Modifier.width(320.dp),
                options = emailOptions(grouped, group = true),
                filterable = false,
                placeholder = "Email",
            )
        }

        DemoSection(
            title = "Custom input element",
            description = "Replace the input element with any composition, for example a textarea.",
        ) {
            var custom by remember { mutableStateOf("") }
            NAutoComplete(
                value = custom,
                onValueChange = { custom = it },
                modifier = Modifier.width(320.dp),
                options = emailOptions(custom),
                customInput = { value, onValueChange, onFocus, onBlur ->
                    NInput(
                        value = value,
                        onValueChange = onValueChange,
                        type = NInputType.Textarea,
                        placeholder = "Email",
                        minLines = 2,
                        maxLines = 3,
                        onFocus = onFocus,
                        onBlur = onBlur,
                    )
                },
            )
        }

        DemoSection(
            title = "Actions after select",
            description = "clearAfterSelect resets the input and blurAfterSelect releases focus after choosing an option.",
        ) {
            var clearValue by remember { mutableStateOf("") }
            var blurValue by remember { mutableStateOf("") }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NAutoComplete(
                    value = clearValue,
                    onValueChange = { clearValue = it },
                    options = emailOptions(clearValue),
                    clearAfterSelect = true,
                    placeholder = "Clear After Select",
                )
                NAutoComplete(
                    value = blurValue,
                    onValueChange = { blurValue = it },
                    options = emailOptions(blurValue),
                    blurAfterSelect = true,
                    placeholder = "Blur After Select",
                )
            }
        }

        DemoSection(
            title = "Show options by value",
            description = "showOptionsWhen decides whether the menu may show for the current value. " +
                "This example only opens when the value is exactly \"a\".",
        ) {
            var conditional by remember { mutableStateOf("") }
            NAutoComplete(
                value = conditional,
                onValueChange = { conditional = it },
                modifier = Modifier.width(320.dp),
                options = emailOptions(conditional),
                showOptionsWhen = { it == "a" },
                placeholder = "Input 'a' to show menu",
            )
        }

        DemoSection(
            title = "Customized rendering",
            description = "renderOption replaces the default label rendering of each suggestion.",
        ) {
            var rendered by remember { mutableStateOf("") }
            NAutoComplete(
                value = rendered,
                onValueChange = { rendered = it },
                modifier = Modifier.width(320.dp),
                options = emailOptions(rendered),
                placeholder = "Email",
                renderOption = { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        NText(option.label, color = theme.colors.text2)
                        NTag("Email", type = NStatus.Info, size = NSize.Small)
                    }
                },
            )
        }

        DemoSection(
            title = "Append",
            description = "With append on, the chosen text is appended instead of replacing the value. " +
                "Combine it with showOptionsWhen to open the menu after typing @.",
        ) {
            var appendValue by remember { mutableStateOf("") }
            val suffixes = listOf("gmail.com", "163.com", "qq.com")
                .map { NAutoCompleteOption(label = it, value = it) }
            NAutoComplete(
                value = appendValue,
                onValueChange = { appendValue = it },
                modifier = Modifier.width(320.dp),
                options = suffixes,
                filterable = false,
                append = true,
                showOptionsWhen = { it.endsWith("@") },
                placeholder = "Select and append",
            )
        }

        DemoSection(
            title = "Load remote options",
            description = "Update options asynchronously and use loading to indicate a pending request.",
        ) {
            var remoteValue by remember { mutableStateOf("") }
            var remoteOptions by remember { mutableStateOf(emptyList<NAutoCompleteOption>()) }
            var remoteLoading by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()
            NAutoComplete(
                value = remoteValue,
                onValueChange = { next ->
                    remoteValue = next
                    remoteLoading = true
                    scope.launch {
                        delay(500)
                        remoteOptions = emailOptions(next)
                        remoteLoading = false
                    }
                },
                modifier = Modifier.width(320.dp),
                options = remoteOptions,
                loading = remoteLoading,
                placeholder = "Email",
                clearable = true,
            )
        }

        DemoSection(
            title = "Validation status",
            description = "Validation status can be applied outside a form.",
        ) {
            var warning by remember { mutableStateOf("") }
            var error by remember { mutableStateOf("") }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NAutoComplete(
                    value = warning,
                    onValueChange = { warning = it },
                    options = emailOptions(warning),
                    status = NStatus.Warning,
                    placeholder = "Warning",
                )
                NAutoComplete(
                    value = error,
                    onValueChange = { error = it },
                    options = emailOptions(error),
                    status = NStatus.Error,
                    placeholder = "Error",
                )
            }
        }
    }
}
