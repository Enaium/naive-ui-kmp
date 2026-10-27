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
import cn.enaium.naiveui.components.NButtonType
import cn.enaium.naiveui.components.NDynamicTags
import cn.enaium.naiveui.components.NDynamicTagsOption
import cn.enaium.naiveui.components.NForm
import cn.enaium.naiveui.components.NFormItem
import cn.enaium.naiveui.components.NFormRule
import cn.enaium.naiveui.components.NInput
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.rememberNFormState
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus

@Composable
fun DynamicTagsDocs() {
    DocsPage(
        title = "Dynamic Tags",
        description = "Maintain a dynamic list of tags with an inline add input.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Tags are closable by default and the add button reveals an inline input.",
            code = """
var tags by remember { mutableStateOf(listOf("teacher", "programmer")) }
NDynamicTags(value = tags, onValueChange = { tags = it })
""".trimIndent(),
        ) {
            BasicDemo()
        }
        DemoSection(
            title = "Max tag count",
            description = "When the maximum is reached the add button is disabled.",
            code = """
NDynamicTags(value = tags, onValueChange = { tags = it }, max = 3)
""".trimIndent(),
        ) {
            MaxDemo()
        }
        DemoSection(
            title = "Use in form",
            description = "Rules validate the tag list; the form item renders the message below the tags.",
            code = """
NFormItem(path = "tags", showLabel = false) {
    NDynamicTags(value = tags, onValueChange = { tags = it })
}
// rule
NFormRule(validator = { (it as? List<*>)?.size?.let { it < 5 } == true }, message = "Up to 4 tags")
""".trimIndent(),
        ) {
            FormDemo()
        }
        DemoSection(
            title = "Customizing input or trigger",
            description = "inputSlot replaces the inline input and triggerSlot replaces the add button.",
            code = """
NDynamicTags(
    value = tags,
    onValueChange = { tags = it },
    inputSlot = { submit, deactivate -> NInput(...) },
    triggerSlot = { activate, disabled -> NButton(onClick = activate) { ... } },
)
""".trimIndent(),
        ) {
            SlotDemo()
        }
        DemoSection(
            title = "Custom render for each tag",
            description = "renderTag draws each tag yourself and receives a close callback.",
            code = """
NDynamicTags(
    value = tags,
    onValueChange = { tags = it },
    renderTag = { tag, index, onClose ->
        NTag(text = tag, type = if (index < 3) NStatus.Success else NStatus.Error, onClose = onClose)
    },
)
""".trimIndent(),
        ) {
            RenderTagDemo()
        }
        DemoSection(
            title = "Object formatted value",
            description = "onCreate can produce object values; provide label to choose the displayed text.",
            code = """
NDynamicTags(
    value = options,
    onValueChange = { options = it },
    label = { it.label },
    onCreate = { label -> NDynamicTagsOption(label, "v-${'$'}label") },
)
""".trimIndent(),
        ) {
            OptionFormatDemo()
        }
        DemoSection(
            title = "Custom tag creation",
            description = "onCreate transforms the typed label into the stored value.",
            code = """
NDynamicTags(
    value = tags,
    onValueChange = { tags = it },
    onCreate = { label -> "empower " + label },
)
""".trimIndent(),
        ) {
            OnCreateDemo()
        }
    }
}

@Composable
private fun ResultText(result: Boolean?) {
    if (result == null) return
    val theme = LocalNTheme.current
    NText(
        text = if (result) "Valid" else "Invalid",
        color = if (result) theme.colors.success else theme.colors.error,
    )
}

@Composable
private fun BasicDemo() {
    var tags by remember { mutableStateOf(listOf("teacher", "programmer")) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NDynamicTags(value = tags, onValueChange = { tags = it })
        NText("Tags: $tags", color = LocalNTheme.current.colors.text3)
    }
}

@Composable
private fun MaxDemo() {
    var tags by remember { mutableStateOf(listOf("teacher", "programmer")) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NDynamicTags(value = tags, onValueChange = { tags = it }, max = 3)
        NText("${tags.size} of 3 tags", color = LocalNTheme.current.colors.text3)
    }
}

@Composable
private fun FormDemo() {
    val form = rememberNFormState()
    var tags by remember { mutableStateOf(listOf("teacher", "programmer")) }
    var result by remember { mutableStateOf<Boolean?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        NForm(
            model = mapOf("tags" to tags),
            rules = mapOf(
                "tags" to listOf(
                    NFormRule(
                        validator = { (it as? List<*>)?.size?.let { size -> size < 5 } == true },
                        message = "Up to 4 tags",
                    ),
                ),
            ),
            state = form,
            validateCallback = { result = it },
            modifier = Modifier.width(460.dp),
        ) {
            NFormItem(path = "tags", showLabel = false, first = true) {
                NDynamicTags(value = tags, onValueChange = { tags = it })
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            NButton(onClick = { form.validate() }) { NText("Validate") }
            ResultText(result)
        }
    }
}

@Composable
private fun SlotDemo() {
    var tags by remember { mutableStateOf(listOf("Teacher", "Programmer")) }
    var draft by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NDynamicTags(
            value = tags,
            onValueChange = { tags = it },
            inputSlot = { submit, deactivate ->
                NInput(
                    value = draft,
                    onValueChange = { draft = it },
                    placeholder = "Email",
                    size = NSize.Small,
                    autofocus = true,
                    onPressEnter = {
                        if (draft.isNotBlank()) {
                            submit(draft)
                            draft = ""
                        }
                    },
                    onBlur = {
                        deactivate()
                        draft = ""
                    },
                    modifier = Modifier.width(200.dp),
                )
            },
            triggerSlot = { activate, disabled ->
                NButton(
                    size = NSize.Small,
                    type = NButtonType.Primary,
                    dashed = true,
                    disabled = disabled,
                    icon = { NIcon(NIcons.Add, size = 14.dp) },
                    onClick = activate,
                ) { NText("New Tag") }
            },
        )
        NText("Tags: $tags", color = LocalNTheme.current.colors.text3)
    }
}

@Composable
private fun RenderTagDemo() {
    var tags by remember { mutableStateOf(listOf("teacher", "programmer", "designer", "engineer", "artist")) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NDynamicTags(
            value = tags,
            onValueChange = { tags = it },
            renderTag = { tag, index, onClose ->
                NTag(
                    text = tag,
                    type = if (index < 3) NStatus.Success else NStatus.Error,
                    disabled = index > 3,
                    closable = true,
                    onClose = onClose,
                )
            },
        )
        NText("Tags: $tags", color = LocalNTheme.current.colors.text3)
    }
}

@Composable
private fun OptionFormatDemo() {
    var options by remember {
        mutableStateOf(
            listOf(
                NDynamicTagsOption("Dig It", "hello world 1"),
                NDynamicTagsOption("Maggie Mae", "hello world 2"),
                NDynamicTagsOption("I Me Mine", "hello world 3"),
            ),
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NDynamicTags(
            value = options,
            onValueChange = { options = it },
            label = { it.label },
            onCreate = { label -> NDynamicTagsOption(label, "v-$label") },
        )
        NText(
            text = "Options: " + options.joinToString { it.label + "=" + it.value },
            color = LocalNTheme.current.colors.text3,
        )
    }
}

@Composable
private fun OnCreateDemo() {
    var tags by remember { mutableStateOf(emptyList<String>()) }
    val prefixes = remember { listOf("empower ", "unlock ", "align ") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NDynamicTags(
            value = tags,
            onValueChange = { tags = it },
            onCreate = { label -> prefixes.random() + label },
        )
        NText("Tags: $tags", color = LocalNTheme.current.colors.text3)
    }
}
