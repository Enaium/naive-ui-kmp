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
import cn.enaium.naiveui.components.NCheckbox
import cn.enaium.naiveui.components.NCheckboxGroup
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

private val fruits = listOf("Apple", "Banana", "Orange", "Mango")

@Composable
fun CheckboxDocs() {
    DocsPage(
        title = "Checkbox",
        description = "Checkboxes let the user select one or more values from a set. They can be used standalone, grouped, or rendered in a grid.",
    ) {
        DemoSection(
            title = "Basic",
            description = "A checkbox holds a boolean value. Use the disabled prop to prevent interaction.",
            code = """
var checked by remember { mutableStateOf(false) }
var disabled by remember { mutableStateOf(true) }

NCheckbox(checked, { checked = it }, label = "Checkbox")
NCheckbox(checked, { checked = it }, disabled = disabled, label = "Checkbox")
""",
        ) {
            var checked by remember { mutableStateOf(false) }
            var disabled by remember { mutableStateOf(true) }
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NCheckbox(checked = checked, onCheckedChange = { checked = it }, label = "Checkbox")
                NCheckbox(checked = checked, onCheckedChange = { checked = it })
                NCheckbox(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    disabled = disabled,
                    label = "Checkbox",
                )
                NButton(onClick = { disabled = !disabled }, size = NSize.Small) {
                    NText("Toggle disabled")
                }
            }
        }

        DemoSection(
            title = "Size",
            description = "Checkbox supports small, medium and large sizes.",
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                var small by remember { mutableStateOf(true) }
                var medium by remember { mutableStateOf(true) }
                var large by remember { mutableStateOf(true) }
                NCheckbox(small, { small = it }, size = NSize.Small, label = "Small")
                NCheckbox(medium, { medium = it }, size = NSize.Medium, label = "Medium")
                NCheckbox(large, { large = it }, size = NSize.Large, label = "Large")
            }
        }

        DemoSection(
            title = "Checkbox group",
            description = "Wrap checkboxes in a group to manage the selected values together. Each checkbox identifies itself with a value.",
            code = """
var cities by remember { mutableStateOf<List<Any>>(emptyList()) }

NCheckboxGroup(cities, { cities = it }) {
    NCheckbox(checked = false, value = "Beijing", label = "Beijing")
    NCheckbox(checked = false, value = "Shanghai", label = "Shanghai")
}
""",
        ) {
            var cities by remember { mutableStateOf<List<Any>>(emptyList()) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NCheckboxGroup(cities, { cities = it }) {
                    NCheckbox(checked = false, value = "Beijing", label = "Beijing")
                    NCheckbox(checked = false, value = "Shanghai", label = "Shanghai")
                    NCheckbox(checked = false, value = "Guangzhou", label = "Guangzhou")
                    NCheckbox(checked = false, value = "Shenzhen", label = "Shenzhen")
                }
                NText(
                    if (cities.isEmpty()) "Nothing selected" else "Selected: ${cities.joinToString(", ")}",
                )
            }
        }

        DemoSection(
            title = "Options and limits",
            description = "Render a group from an option list and clamp the selection with min and max.",
        ) {
            var selection by remember { mutableStateOf<List<Any>>(emptyList()) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NCheckboxGroup(selection, { selection = it }, max = 2) {
                    fruits.forEach { fruit ->
                        NCheckbox(checked = false, value = fruit, label = fruit)
                    }
                }
                NText("Pick at most two fruits: ${selection.size}/2 selected")
            }
        }

        DemoSection(
            title = "Grid",
            description = "Checkboxes can be arranged in any layout, for example a two column grid.",
        ) {
            var selected by remember { mutableStateOf<List<Any>>(emptyList()) }
            val items = listOf(
                "Pushes Open", "The Window", "And Raises", "The Spyglass",
            )
            NCheckboxGroup(selected, { selected = it }) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items.chunked(2).forEach { rowItems ->
                        Row(Modifier.fillMaxWidth()) {
                            rowItems.forEach { item ->
                                Column(Modifier.width(180.dp)) {
                                    NCheckbox(checked = false, value = item, label = item)
                                }
                            }
                        }
                    }
                }
            }
        }

        DemoSection(
            title = "Indeterminate",
            description = "The indeterminate state is useful for a \"select all\" checkbox that is partially selected.",
            code = """
NCheckbox(
    checked = checked,
    onCheckedChange = { checked = it },
    indeterminate = indeterminate,
    label = "Checkbox",
)
""",
        ) {
            var checked by remember { mutableStateOf(false) }
            var indeterminate by remember { mutableStateOf(false) }
            NSpace(vertical = true, size = 12.dp) {
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    NCheckbox(checked, { checked = it }, indeterminate = indeterminate, label = "Checkbox")
                    NCheckbox(checked, { checked = it }, indeterminate = indeterminate)
                    NCheckbox(checked, { checked = it }, indeterminate = indeterminate, disabled = true)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    NButton(onClick = { indeterminate = !indeterminate }, size = NSize.Small) {
                        NText(if (indeterminate) "Not indeterminate" else "Indeterminate")
                    }
                    NButton(onClick = { checked = !checked }, size = NSize.Small) {
                        NText("Toggle checked")
                    }
                }
            }
        }

        DemoSection(
            title = "Controlled and event",
            description = "Bind the checked state and react to changes through the callback.",
        ) {
            var accepted by remember { mutableStateOf(false) }
            var events by remember { mutableStateOf(listOf<String>()) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NCheckbox(
                    checked = accepted,
                    onCheckedChange = {
                        accepted = it
                        events = (events + "onCheckedChange: $it").takeLast(3)
                    },
                    label = "I accept the terms",
                )
                NText(if (events.isEmpty()) "No events yet" else events.joinToString("  •  "))
            }
        }
    }
}
