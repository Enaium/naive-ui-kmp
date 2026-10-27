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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NSelect
import cn.enaium.naiveui.components.NSelectEntry
import cn.enaium.naiveui.components.NSelectGroupOption
import cn.enaium.naiveui.components.NSelectOption
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus

private val songOptions: List<NSelectEntry> = listOf(
    NSelectOption(
        "Everybody's Got Something to Hide Except Me and My Monkey",
        "song0",
        disabled = true,
    ),
    NSelectOption("Drive My Car", "song1"),
    NSelectOption("Norwegian Wood", "song2"),
    NSelectOption("You Won't See", "song3", disabled = true),
    NSelectOption("Nowhere Man", "song4"),
    NSelectOption("Think For Yourself", "song5"),
    NSelectOption("The Word", "song6"),
    NSelectOption("Michelle", "song7", disabled = true),
    NSelectOption("What goes on", "song8"),
    NSelectOption("Girl", "song9"),
    NSelectOption("I'm looking through you", "song10"),
    NSelectOption("In My Life", "song11"),
    NSelectOption("Wait", "song12"),
)

private val groupedOptions: List<NSelectEntry> = listOf(
    NSelectGroupOption(
        label = "Rubber Soul",
        children = listOf(
            NSelectOption("Drive My Car", "song1"),
            NSelectOption("Norwegian Wood", "song2"),
            NSelectOption("Nowhere Man", "song4"),
            NSelectOption("In My Life", "song11"),
        ),
    ),
    NSelectGroupOption(
        label = "Let It Be",
        children = listOf(
            NSelectOption("Two Of Us", "two-of-us"),
            NSelectOption("Dig A Pony", "dig-a-pony"),
            NSelectOption("Across The Universe", "across-the-universe"),
            NSelectOption("Let It Be", "let-it-be"),
        ),
    ),
)

@Composable
fun SelectDocs() {
    DocsPage(
        title = "Select",
        description = "Select a single value or many values from a popup menu, with " +
            "filtering, grouping, tags and validation states.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Pass options and a value. Disabled options cannot be picked.",
            code = """NSelect(
    options = options,
    value = value,
    onValueChange = { value = it },
    clearable = true,
)""",
        ) {
            var value by remember { mutableStateOf<Any?>("song2") }
            Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NSelect(
                    options = songOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Please select a song",
                    clearable = true,
                )
                NSelect(
                    options = songOptions,
                    value = "Norwegian Wood",
                    onValueChange = {},
                    placeholder = "Disabled",
                    disabled = true,
                )
            }
        }

        DemoSection(
            title = "Multiple",
            description = "Selected values become closable tags. The menu stays open while " +
                "picking several values.",
            code = """NSelect(
    options = options,
    value = values,
    onValueChange = { values = it },
    multiple = true,
    clearable = true,
)""",
        ) {
            var values by remember {
                mutableStateOf<Any?>(listOf<Any>("song1", "song4"))
            }
            Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NSelect(
                    options = songOptions,
                    value = values,
                    onValueChange = { values = it },
                    placeholder = "Please select songs",
                    multiple = true,
                    clearable = true,
                )
                NSelect(
                    options = songOptions,
                    value = listOf<Any>("song1"),
                    onValueChange = {},
                    placeholder = "Disabled",
                    multiple = true,
                    disabled = true,
                )
            }
        }

        DemoSection(
            title = "Filterable",
            description = "Type to narrow the options. The keyboard can be used to move " +
                "through the menu and confirm with Enter.",
            code = """NSelect(
    options = options,
    value = value,
    onValueChange = { value = it },
    filterable = true,
)""",
        ) {
            var single by remember { mutableStateOf<Any?>(null) }
            var many by remember { mutableStateOf<Any?>(listOf<Any>()) }
            Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NSelect(
                    options = songOptions,
                    value = single,
                    onValueChange = { single = it },
                    placeholder = "Please select a song",
                    filterable = true,
                    clearable = true,
                )
                NSelect(
                    options = songOptions,
                    value = many,
                    onValueChange = { many = it },
                    placeholder = "Please select songs",
                    multiple = true,
                    filterable = true,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Max tag count",
            description = "Limit the number of visible tags; the rest are summarized as +N.",
        ) {
            var values by remember {
                mutableStateOf<Any?>(listOf<Any>("song1", "song2", "song4", "song5", "song6"))
            }
            Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NSelect(
                    options = songOptions,
                    value = values,
                    onValueChange = { values = it },
                    placeholder = "maxTagCount = 3",
                    multiple = true,
                    maxTagCount = 3,
                    clearable = true,
                )
                NSelect(
                    options = songOptions,
                    value = values,
                    onValueChange = { values = it },
                    placeholder = "No tag limit",
                    multiple = true,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Group",
            description = "Options may be grouped under a label. Groups are not selectable.",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.width(300.dp)) {
                    NSelect(
                        options = groupedOptions,
                        value = value,
                        onValueChange = { value = it },
                        placeholder = "Grouped options",
                        clearable = true,
                    )
                }
            }
        }

        DemoSection(
            title = "Size and status",
            description = "Four sizes are available, and validation status recolors the border.",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            var warningValue by remember { mutableStateOf<Any?>(null) }
            var errorValue by remember { mutableStateOf<Any?>(null) }
            var successValue by remember { mutableStateOf<Any?>(null) }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.width(190.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    NSelect(
                        options = songOptions,
                        value = value,
                        onValueChange = { value = it },
                        placeholder = "Tiny",
                        size = NSize.Tiny,
                    )
                    NSelect(
                        options = songOptions,
                        value = value,
                        onValueChange = { value = it },
                        placeholder = "Small",
                        size = NSize.Small,
                    )
                    NSelect(
                        options = songOptions,
                        value = value,
                        onValueChange = { value = it },
                        placeholder = "Medium",
                        size = NSize.Medium,
                    )
                    NSelect(
                        options = songOptions,
                        value = value,
                        onValueChange = { value = it },
                        placeholder = "Large",
                        size = NSize.Large,
                    )
                }
                Column(Modifier.width(190.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    NSelect(
                        options = songOptions,
                        value = warningValue,
                        onValueChange = { warningValue = it },
                        placeholder = "Warning",
                        status = NStatus.Warning,
                    )
                    NSelect(
                        options = songOptions,
                        value = errorValue,
                        onValueChange = { errorValue = it },
                        placeholder = "Error",
                        status = NStatus.Error,
                    )
                    NSelect(
                        options = songOptions,
                        value = successValue,
                        onValueChange = { successValue = it },
                        placeholder = "Success",
                        status = NStatus.Success,
                    )
                }
            }
        }

        DemoSection(
            title = "Loading and empty",
            description = "Loading replaces the arrow with a spinner; an empty option list " +
                "renders the empty state inside the menu.",
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.width(190.dp)) {
                    NSelect(
                        options = songOptions,
                        placeholder = "Loading",
                        loading = true,
                    )
                }
                Column(Modifier.width(190.dp)) {
                    NSelect(
                        options = emptyList(),
                        placeholder = "No options",
                    )
                }
            }
        }
    }
}
