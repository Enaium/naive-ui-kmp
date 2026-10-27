package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NDynamicInput
import cn.enaium.naiveui.components.NInput
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

@Composable
fun DynamicInputDocs() {
    DocsPage(
        title = "Dynamic Input",
        description = "Edit a dynamic list of input values with add, remove and sort actions.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Bind the value list and set the minimum and maximum item counts. The default item is an input.",
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
            title = "Min and max",
            description = "Removal stops at min and insertion stops at max; both buttons are disabled accordingly.",
            code = """
NDynamicInput(
    values = values,
    onValueChange = { values = it },
    min = 1,
    max = 3,
)
""".trimIndent(),
        ) {
            MinMaxDemo()
        }
        DemoSection(
            title = "Custom item",
            description = "Pass the item slot to render any content for each value.",
            code = """
NDynamicInput(
    values = values,
    onValueChange = { values = it },
    item = { value, index, onValueChange ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            NText("${'$'}{index + 1}.")
            NInput(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
            )
        }
    },
)
""".trimIndent(),
        ) {
            CustomItemDemo()
        }
        DemoSection(
            title = "Sort button",
            description = "Show move-up and move-down actions to reorder values.",
            code = """
NDynamicInput(
    values = values,
    onValueChange = { values = it },
    showSortButton = true,
)
""".trimIndent(),
        ) {
            SortDemo()
        }
    }
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
private fun MinMaxDemo() {
    var values by remember { mutableStateOf(listOf("", "")) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NDynamicInput(
            values = values,
            onValueChange = { values = it },
            min = 1,
            max = 3,
            placeholder = "At most 3 items",
        )
        NText("${values.size} item(s), min 1, max 3", color = LocalNTheme.current.colors.text3)
    }
}

@Composable
private fun CustomItemDemo() {
    var values by remember { mutableStateOf(listOf("A String")) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NDynamicInput(
            values = values,
            onValueChange = { values = it },
            item = { value, index, onValueChange ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NText(
                        text = "${index + 1}.",
                        color = LocalNTheme.current.colors.text3,
                    )
                    NInput(
                        value = value,
                        onValueChange = onValueChange,
                        placeholder = "Custom content",
                        modifier = Modifier.weight(1f),
                    )
                }
            },
        )
        NText("Values: $values", color = LocalNTheme.current.colors.text3)
    }
}

@Composable
private fun SortDemo() {
    var values by remember { mutableStateOf(listOf("First", "Second", "Third")) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NDynamicInput(
            values = values,
            onValueChange = { values = it },
            showSortButton = true,
        )
        NText("Order: ${values.joinToString(" -> ")}", color = LocalNTheme.current.colors.text3)
    }
}
