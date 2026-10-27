package cn.enaium.naiveui.sample.gallery.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NPopselect
import cn.enaium.naiveui.components.NPopselectOption
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

private val popselectOptions = listOf(
    NPopselectOption("John Brown", "john"),
    NPopselectOption("Jim Green", "jim"),
    NPopselectOption("Joe Black", "joe"),
    NPopselectOption("Summer", "summer", disabled = true),
    NPopselectOption("Lucy", "lucy"),
)

private val manyPopselectOptions = (1..40).map { index ->
    NPopselectOption("Option $index", "option$index")
}

@Composable
fun PopselectDocs() {
    DocsPage(
        title = "Popselect",
        description = "Popselect combines a select trigger with a popup menu. It supports single and multiple " +
            "selection, tag folding, clearing, sizes and scrollable option lists.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Pass a list of `NPopselectOption` and keep the value in state.",
            code = "var value by remember { mutableStateOf<String?>(null) }\nNPopselect(\n    options = options,\n    value = value,\n    onValueChange = { value = it },\n    placeholder = \"Select a person\",\n)",
        ) {
            var value by remember { mutableStateOf<String?>(null) }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NPopselect(
                    options = popselectOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Select a person",
                )
                NText("Selected: ${value ?: "none"}", size = 13.sp)
            }
        }

        DemoSection(
            title = "Multiple",
            description = "Set `multiple` and use `values`/`onValuesChange`. Tags beyond `maxTagCount` collapse into a counter.",
        ) {
            var values by remember { mutableStateOf(listOf("john", "jim")) }
            NPopselect(
                options = popselectOptions,
                multiple = true,
                values = values,
                onValuesChange = { values = it },
                maxTagCount = 2,
                placeholder = "Select people",
            )
        }

        DemoSection(
            title = "Clearable",
            description = "Selected values can be cleared from the trigger; disable it with `clearable = false`.",
        ) {
            var value by remember { mutableStateOf<String?>("lucy") }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NPopselect(
                    options = popselectOptions,
                    value = value,
                    onValueChange = { value = it },
                    clearable = true,
                )
                NText("Selected: ${value ?: "none"}", size = 13.sp)
            }
        }

        DemoSection(
            title = "Size",
            description = "The trigger follows the naive-ui control heights through `size`.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NSize.entries.forEach { size ->
                    NPopselect(
                        options = popselectOptions,
                        size = size,
                        placeholder = size.label,
                    )
                }
            }
        }

        DemoSection(
            title = "Scrollable",
            description = "Long option lists scroll inside the popup with a maximum height of 240dp.",
        ) {
            var value by remember { mutableStateOf<String?>("option1") }
            NPopselect(
                options = manyPopselectOptions,
                value = value,
                onValueChange = { value = it },
                width = 200.dp,
            )
        }

        DemoSection(
            title = "Disabled",
            description = "Disabled triggers ignore clicks; disabled options stay visible but cannot be picked.",
        ) {
            NPopselect(
                options = popselectOptions,
                disabled = true,
                placeholder = "Disabled popselect",
            )
        }
    }
}
