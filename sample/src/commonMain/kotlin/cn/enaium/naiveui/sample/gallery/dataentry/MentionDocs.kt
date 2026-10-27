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
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NForm
import cn.enaium.naiveui.components.NFormItem
import cn.enaium.naiveui.components.NFormRule
import cn.enaium.naiveui.components.NInputType
import cn.enaium.naiveui.components.NMention
import cn.enaium.naiveui.components.NMentionController
import cn.enaium.naiveui.components.NMentionOption
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.nFormRules
import cn.enaium.naiveui.components.rememberNFormState
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val mentionPeople = listOf("07akioni", "star-kirby", "Guandong-Road", "No.5-Yiheyuan-Road")
    .map { NMentionOption(label = it, value = it) }

private val mentionWords = listOf("We", "all", "live", "in", "a", "yellow", "submarine")
    .map { NMentionOption(label = it, value = it, prefix = "#") }

@Composable
fun MentionDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Mention",
        description = "An input that opens a suggestion menu when a trigger prefix is typed, with " +
            "textarea support, custom option rendering, imperative focus and form integration.",
    ) {
        var basic by remember { mutableStateOf("@") }
        var selected by remember { mutableStateOf("") }
        DemoSection(
            title = "Basic usage",
            description = "Type @ to open the suggestions. Selecting an entry inserts prefix + label.",
            code = "NMention(value = value, onValueChange = { value = it }, options = options)",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NMention(
                    value = basic,
                    onValueChange = { basic = it },
                    modifier = Modifier.width(320.dp),
                    options = mentionPeople,
                    onSelect = { selected = it.value },
                )
                NText(
                    text = if (selected.isEmpty()) "Value: $basic" else "Selected: $selected",
                    color = theme.colors.text3,
                    size = theme.typography.fontSize.small,
                )
            }
        }

        DemoSection(
            title = "Textarea",
            description = "Set type to Textarea to enter mentions in a multi-line field.",
        ) {
            var textarea by remember { mutableStateOf("") }
            NMention(
                value = textarea,
                onValueChange = { textarea = it },
                modifier = Modifier.width(320.dp),
                options = mentionPeople,
                type = NInputType.Textarea,
            )
        }

        DemoSection(
            title = "Autosize",
            description = "minLines and maxLines control how the textarea grows as content is added.",
        ) {
            var autosize by remember { mutableStateOf("") }
            NMention(
                value = autosize,
                onValueChange = { autosize = it },
                modifier = Modifier.width(320.dp),
                options = mentionPeople,
                type = NInputType.Textarea,
                minLines = 2,
                maxLines = 4,
            )
        }

        DemoSection(
            title = "Load remote options",
            description = "onSearch receives the current pattern and prefix; update options when the " +
                "request resolves and show the loading indicator in the menu.",
        ) {
            var remoteValue by remember { mutableStateOf("@") }
            var remoteOptions by remember { mutableStateOf(emptyList<NMentionOption>()) }
            var remoteLoading by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NMention(
                    value = remoteValue,
                    onValueChange = { remoteValue = it },
                    modifier = Modifier.width(320.dp),
                    options = remoteOptions,
                    loading = remoteLoading,
                    onSearch = { pattern, _ ->
                        remoteLoading = true
                        scope.launch {
                            delay(600)
                            remoteOptions = mentionWords
                                .map { it.copy(prefix = null, label = pattern + it.label) }
                            remoteLoading = false
                        }
                    },
                )
                NText(
                    text = "Try typing @ followed by letters.",
                    color = theme.colors.text3,
                    size = theme.typography.fontSize.small,
                )
            }
        }

        DemoSection(
            title = "Work with form",
            description = "Mention fields participate in n-form validation, including a textarea variant.",
        ) {
            val form = rememberNFormState()
            var cool by remember { mutableStateOf("") }
            var veryCool by remember { mutableStateOf("") }
            var result by remember { mutableStateOf("") }
            Column(Modifier.width(360.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NForm(
                    model = mapOf("cool" to cool, "veryCool" to veryCool),
                    rules = nFormRules(
                        "cool" to NFormRule(required = true, message = "Cool is required"),
                        "veryCool" to NFormRule(
                            required = true,
                            message = "07akioni should be very cool!",
                            validator = { (it as? String)?.contains("@07akioni") == true },
                        ),
                    ),
                    state = form,
                ) {
                    NFormItem(path = "cool", label = "Cool") {
                        NMention(value = cool, onValueChange = { cool = it }, options = mentionPeople)
                    }
                    NFormItem(path = "veryCool", label = "Very Cool") {
                        NMention(
                            value = veryCool,
                            onValueChange = { veryCool = it },
                            options = mentionPeople,
                            type = NInputType.Textarea,
                        )
                    }
                }
                NButton(onClick = { result = if (form.validate()) "Valid" else "Invalid" }) {
                    NText("Validate")
                }
                if (result.isNotEmpty()) {
                    NText(
                        result,
                        color = if (result == "Valid") theme.colors.success else theme.colors.error,
                        size = theme.typography.fontSize.small,
                    )
                }
            }
        }

        DemoSection(
            title = "Render label",
            description = "renderOption replaces the default label rendering inside the menu.",
        ) {
            var rendered by remember { mutableStateOf("") }
            NMention(
                value = rendered,
                onValueChange = { rendered = it },
                modifier = Modifier.width(320.dp),
                options = mentionPeople,
                renderOption = { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        NIcon(NIcons.Person, size = 18.dp, color = theme.colors.primary)
                        NText(option.value, color = theme.colors.text2)
                    }
                },
            )
        }

        DemoSection(
            title = "Custom trigger prefix",
            description = "Use the prefix prop to change the trigger character.",
        ) {
            var words by remember { mutableStateOf("") }
            NMention(
                value = words,
                onValueChange = { words = it },
                modifier = Modifier.width(320.dp),
                options = mentionWords,
                prefix = "#",
                placeholder = "Type # to mention a word",
            )
        }

        DemoSection(
            title = "Manually trigger focus and blur",
            description = "NMentionController focuses and blurs the field programmatically.",
        ) {
            val controller = remember { NMentionController() }
            var manual by remember { mutableStateOf("@") }
            Column(Modifier.width(360.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NButton(onClick = { controller.focus() }) { NText("Focus") }
                    NButton(onClick = { controller.blur() }) { NText("Blur") }
                }
                NMention(
                    value = manual,
                    onValueChange = { manual = it },
                    options = mentionPeople,
                    controller = controller,
                )
            }
        }

        DemoSection(
            title = "Option prefixes",
            description = "Options can declare a trigger prefix. Only options matching the active prefix " +
                "are suggested, so the # entries stay hidden while typing @.",
        ) {
            var mixed by remember { mutableStateOf("@") }
            NMention(
                value = mixed,
                onValueChange = { mixed = it },
                modifier = Modifier.width(320.dp),
                options = mentionPeople + mentionWords,
                onSelect = { selected = it.value },
            )
        }

        DemoSection(
            title = "Validation status",
            description = "Validation status can be applied outside a form.",
        ) {
            var success by remember { mutableStateOf("") }
            var warning by remember { mutableStateOf("") }
            var error by remember { mutableStateOf("") }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NMention(
                    value = success,
                    onValueChange = { success = it },
                    options = mentionPeople,
                    status = NStatus.Success,
                    placeholder = "Success",
                )
                NMention(
                    value = warning,
                    onValueChange = { warning = it },
                    options = mentionPeople,
                    status = NStatus.Warning,
                    placeholder = "Warning",
                )
                NMention(
                    value = error,
                    onValueChange = { error = it },
                    options = mentionPeople,
                    status = NStatus.Error,
                    placeholder = "Error",
                )
            }
        }

        DemoSection(
            title = "Disabled",
            description = "A disabled mention cannot be focused or typed into.",
        ) {
            NMention(
                value = "@07akioni",
                onValueChange = {},
                modifier = Modifier.width(320.dp),
                options = mentionPeople,
                disabled = true,
            )
        }
    }
}
