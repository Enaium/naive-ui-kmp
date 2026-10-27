package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import cn.enaium.naiveui.components.NCheckbox
import cn.enaium.naiveui.components.NDynamicInput
import cn.enaium.naiveui.components.NDynamicInputPair
import cn.enaium.naiveui.components.NDynamicInputPreset
import cn.enaium.naiveui.components.NForm
import cn.enaium.naiveui.components.NFormItem
import cn.enaium.naiveui.components.NFormRule
import cn.enaium.naiveui.components.NInput
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.rememberNFormState
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize

private data class CustomInputValue(val checked: Boolean, val text: String)

private data class FormRowValue(val name: String, val value: String)

@Composable
fun DynamicInputDocs() {
    DocsPage(
        title = "Dynamic Input",
        description = "Edit a dynamic list of values with add, remove and sort actions.",
    ) {
        DemoSection(
            title = "Basic",
            description = "The default item is an input. Set min and max to bound the list.",
            code = """
var values by remember { mutableStateOf(listOf("", "", "")) }
NDynamicInput(
    values = values,
    onValueChange = { values = it },
    placeholder = "Please type here",
    min = 3,
    max = 6,
)
""".trimIndent(),
        ) {
            BasicDemo()
        }
        DemoSection(
            title = "Using pair preset",
            description = "The pair preset edits a key and a value per row.",
            code = """
NDynamicInput(
    values = pairs,
    onValueChange = { pairs = it },
    preset = NDynamicInputPreset.Pair,
    keyPlaceholder = "Please input the key",
    valuePlaceholder = "Please input the value",
)
""".trimIndent(),
        ) {
            PairDemo()
        }
        DemoSection(
            title = "Customizing input content",
            description = "Pass item and onCreate to render arbitrary content for each value.",
            code = """
NDynamicInput(
    values = values,
    onValueChange = { values = it },
    onCreate = { CustomValue(false, "A String") },
    createButtonContent = { NText("Add whatever you want") },
    item = { value, index, onValueChange -> ... },
)
""".trimIndent(),
        ) {
            CustomDemo()
        }
        DemoSection(
            title = "Using it in a form",
            description = "Put form items inside the item slot and validate each indexed path.",
            code = """
NDynamicInput(values = rows, onValueChange = { rows = it }, item = { row, index, onChange ->
    NFormItem(
        path = "rows[${'$'}index].name",
        rule = NFormRule(required = true, message = "Please input a name"),
        showLabel = false,
    ) { NInput(value = row.name, onValueChange = { onChange(row.copy(name = it)) }) }
})
""".trimIndent(),
        ) {
            FormDemo()
        }
        DemoSection(
            title = "Show sort button",
            description = "showSortButton adds move-up and move-down actions.",
            code = """
NDynamicInput(
    values = values,
    onValueChange = { values = it },
    showSortButton = true,
)
""".trimIndent(),
        ) {
            MoveDemo()
        }
        DemoSection(
            title = "Customizing action",
            description = "Pass the action slot to replace the default add/remove/sort buttons.",
            code = """
NDynamicInput(
    values = values,
    onValueChange = { values = it },
    min = 3,
    max = 6,
    action = { value, index, create, remove, move -> ... },
)
""".trimIndent(),
        ) {
            CustomActionDemo()
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
    var values by remember { mutableStateOf(listOf("", "", "")) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NDynamicInput(
            values = values,
            onValueChange = { values = it },
            placeholder = "Please type here",
            min = 3,
            max = 6,
        )
        NText("Values: $values", color = LocalNTheme.current.colors.text3)
    }
}

@Composable
private fun PairDemo() {
    var pairs by remember { mutableStateOf(listOf(NDynamicInputPair("", ""))) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NDynamicInput(
            values = pairs,
            onValueChange = { pairs = it },
            preset = NDynamicInputPreset.Pair,
            keyPlaceholder = "Please input the key",
            valuePlaceholder = "Please input the value",
        )
        NText("Pairs: $pairs", color = LocalNTheme.current.colors.text3)
    }
}

@Composable
private fun CustomDemo() {
    var values by remember {
        mutableStateOf(listOf(CustomInputValue(checked = true, text = "A String")))
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NDynamicInput(
            values = values,
            onValueChange = { values = it },
            onCreate = { CustomInputValue(checked = false, text = "A String") },
            createButtonContent = { NText("Add whatever you want") },
            item = { value, _, onValueChange ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NCheckbox(
                        checked = value.checked,
                        onCheckedChange = { onValueChange(value.copy(checked = it)) },
                    )
                    NInput(
                        value = value.text,
                        onValueChange = { onValueChange(value.copy(text = it)) },
                        placeholder = "A String",
                        modifier = Modifier.weight(1f),
                    )
                }
            },
        )
        NText("Values: $values", color = LocalNTheme.current.colors.text3)
    }
}

@Composable
private fun FormDemo() {
    val form = rememberNFormState()
    var rows by remember { mutableStateOf(listOf(FormRowValue("", ""))) }
    var result by remember { mutableStateOf<Boolean?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        NForm(
            model = mapOf(
                "rows" to rows.map { mapOf("name" to it.name, "value" to it.value) },
            ),
            state = form,
            validateCallback = { result = it },
            modifier = Modifier.width(620.dp),
        ) {
            NDynamicInput(
                values = rows,
                onValueChange = { rows = it },
                onCreate = { FormRowValue("", "") },
                item = { row, index, onChange ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        NFormItem(
                            path = "rows[$index].name",
                            rule = NFormRule(required = true, message = "Please input a name"),
                            showLabel = false,
                            first = true,
                            modifier = Modifier.weight(1f),
                        ) {
                            NInput(
                                value = row.name,
                                onValueChange = { onChange(row.copy(name = it)) },
                                placeholder = "Name",
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        NText("=")
                        NFormItem(
                            path = "rows[$index].value",
                            rule = NFormRule(required = true, message = "Please input a value"),
                            showLabel = false,
                            first = true,
                            modifier = Modifier.weight(1f),
                        ) {
                            NInput(
                                value = row.value,
                                onValueChange = { onChange(row.copy(value = it)) },
                                placeholder = "Value",
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                },
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            NButton(type = NButtonType.Primary, onClick = { form.validate() }) { NText("Validate") }
            ResultText(result)
        }
    }
}

@Composable
private fun MoveDemo() {
    var values by remember { mutableStateOf(listOf("", "", "")) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NDynamicInput(
            values = values,
            onValueChange = { values = it },
            placeholder = "Come on",
            showSortButton = true,
        )
        NText("Values: $values", color = LocalNTheme.current.colors.text3)
    }
}

@Composable
private fun CustomActionDemo() {
    var values by remember { mutableStateOf(listOf("Vue", "Vue3", "React")) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NDynamicInput(
            values = values,
            onValueChange = { values = it },
            placeholder = "Come on",
            min = 3,
            max = 6,
            action = { _, index, create, remove, move ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NButton(
                        onClick = { create(index) },
                        size = NSize.Small,
                        circle = true,
                        icon = { NIcon(NIcons.Add, size = 15.dp) },
                    )
                    NButton(
                        onClick = { remove(index) },
                        size = NSize.Small,
                        circle = true,
                        disabled = values.size <= 3,
                        icon = { NIcon(NIcons.Remove, size = 15.dp) },
                    )
                    NButton(
                        onClick = { move(index, -1) },
                        size = NSize.Small,
                        circle = true,
                        disabled = index == 0,
                        icon = { NIcon(NIcons.ArrowUp, size = 14.dp) },
                    )
                    NButton(
                        onClick = { move(index, 1) },
                        size = NSize.Small,
                        circle = true,
                        disabled = index == values.lastIndex,
                        icon = { NIcon(NIcons.ArrowDown, size = 14.dp) },
                    )
                }
            },
        )
        NText("Values: $values", color = LocalNTheme.current.colors.text3)
    }
}
