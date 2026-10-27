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
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NPopselect
import cn.enaium.naiveui.components.NPopselectOption
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

private val beatlesOptions = listOf(
    NPopselectOption("Drive My Car", "Drive My Car"),
    NPopselectOption("Norwegian Wood", "Norwegian Wood"),
    NPopselectOption("You Won't See", "You Won't See", disabled = true),
    NPopselectOption("Nowhere Man", "Nowhere Man"),
    NPopselectOption("Think For Yourself", "Think For Yourself"),
    NPopselectOption("The Word", "The Word"),
    NPopselectOption("Michelle", "Michelle", disabled = true),
    NPopselectOption("What goes on", "What goes on"),
    NPopselectOption("Girl", "Girl"),
    NPopselectOption("I'm looking through you", "I'm looking through you"),
    NPopselectOption("In My Life", "In My Life"),
    NPopselectOption("Wait", "Wait"),
)

private val cancelOptions = listOf(
    NPopselectOption("Everybody's Got Something to Hide Except Me and My Monkey", "song0", disabled = true),
    NPopselectOption("Drive My Car", "song1"),
    NPopselectOption("Norwegian Wood", "song2"),
    NPopselectOption("You Won't See", "song3", disabled = true),
    NPopselectOption("Nowhere Man", "song4"),
    NPopselectOption("Think For Yourself", "song5"),
    NPopselectOption("The Word", "song6"),
    NPopselectOption("Michelle", "song7", disabled = true),
)

private val multipleOptions = listOf(
    NPopselectOption("Drive My Car", "Drive My Car"),
    NPopselectOption("Norwegian Wood", "Norwegian Wood"),
    NPopselectOption("You Won't See", "You Won't See"),
    NPopselectOption("Nowhere Man", "Nowhere Man"),
    NPopselectOption("Think For Yourself", "Think For Yourself"),
    NPopselectOption("The Word", "The Word"),
)

@Composable
fun PopselectDocs() {
    DocsPage(
        title = "Popselect",
        description = "Popselect combines a select trigger with a popup menu. It supports single, cancelable and " +
            "multiple selection, custom trigger/menu slots, sizes and scrollable option lists.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Bind a value and render any trigger through the `children` slot.",
            code = "NPopselect(options = options, value = value, onValueChange = { value = it }) {\n    NButton { NText(value ?: \"Popselect\") }\n}",
        ) {
            var value by remember { mutableStateOf<String?>("Drive My Car") }
            NPopselect(
                options = beatlesOptions,
                value = value,
                onValueChange = { value = it },
            ) {
                NButton { NText(value ?: "Popselect") }
            }
        }

        DemoSection(
            title = "Cancelable",
            description = "With `cancelable` a single selection can be cancelled by clicking the selected option again.",
        ) {
            var value by remember { mutableStateOf<String?>("song1") }
            NPopselect(
                options = cancelOptions,
                value = value,
                onValueChange = { value = it },
                cancelable = true,
            ) {
                NButton { NText(value ?: "Popselect") }
            }
        }

        DemoSection(
            title = "Multiple",
            description = "Set `multiple` and use `values`/`onValuesChange`; every click toggles one value.",
        ) {
            var values by remember { mutableStateOf(emptyList<String>()) }
            NPopselect(
                options = multipleOptions,
                multiple = true,
                values = values,
                onValuesChange = { values = it },
            ) {
                NButton { NText(values.joinToString(", ").ifEmpty { "Popselect" }) }
            }
        }

        DemoSection(
            title = "Scrollable",
            description = "The menu scrolls when it exceeds the configured maximum height.",
        ) {
            var value by remember { mutableStateOf<String?>("Drive My Car") }
            NPopselect(
                options = beatlesOptions,
                value = value,
                onValueChange = { value = it },
                maxHeight = 240.dp,
                size = NSize.Medium,
            ) {
                NButton { NText(value ?: "Popselect") }
            }
        }

        DemoSection(
            title = "Size",
            description = "`size` follows the naive-ui control heights. These demos use the built-in trigger.",
        ) {
            var value by remember { mutableStateOf<String?>("Drive My Car") }
            NSpace(size = 8.dp) {
                NPopselect(
                    options = beatlesOptions,
                    value = value,
                    onValueChange = { value = it },
                    size = NSize.Medium,
                )
                NPopselect(
                    options = beatlesOptions,
                    value = value,
                    onValueChange = { value = it },
                    size = NSize.Large,
                )
            }
        }

        DemoSection(
            title = "Slot",
            description = "`header`, `empty` and `action` extend an empty menu.",
        ) {
            var value by remember { mutableStateOf<String?>("Empty") }
            NPopselect(
                options = emptyList(),
                value = value,
                onValueChange = { value = it },
                header = { NText("Don't know what to put") },
                empty = { NText("Nothing to see, here is empty.") },
                action = { NText("If you click this demo, you may need it.") },
            ) {
                NButton { NText(value ?: "Popselect") }
            }
        }

        DemoSection(
            title = "Custom trigger & tags",
            description = "Multiple selection with the built-in trigger folds extra tags after `maxTagCount`.",
        ) {
            var values by remember { mutableStateOf(listOf("Drive My Car", "Norwegian Wood", "The Word")) }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NPopselect(
                    options = multipleOptions,
                    multiple = true,
                    values = values,
                    onValuesChange = { values = it },
                    maxTagCount = 2,
                    width = 220.dp,
                )
                NText("Selected: ${values.joinToString(", ")}", size = 13.sp)
            }
        }
    }
}
