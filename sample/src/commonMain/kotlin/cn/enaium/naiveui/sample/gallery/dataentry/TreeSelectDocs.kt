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
import cn.enaium.naiveui.components.NTreeSelect
import cn.enaium.naiveui.components.NTreeSelectOption
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus

private val albumOptions: List<NTreeSelectOption> = listOf(
    NTreeSelectOption(
        label = "Rubber Soul",
        key = "Rubber Soul",
        children = listOf(
            NTreeSelectOption(
                "Everybody's Got Something to Hide Except Me and My Monkey",
                "Everybody's Got Something to Hide Except Me and My Monkey",
            ),
            NTreeSelectOption("Drive My Car", "Drive My Car", disabled = true),
            NTreeSelectOption("Norwegian Wood", "Norwegian Wood"),
            NTreeSelectOption("You Won't See", "You Won't See", disabled = true),
            NTreeSelectOption("Nowhere Man", "Nowhere Man"),
            NTreeSelectOption("Think For Yourself", "Think For Yourself"),
            NTreeSelectOption("In My Life", "In My Life"),
        ),
    ),
    NTreeSelectOption(
        label = "Let It Be",
        key = "Let It Be Album",
        children = listOf(
            NTreeSelectOption("Two Of Us", "Two Of Us"),
            NTreeSelectOption("Dig A Pony", "Dig A Pony"),
            NTreeSelectOption("Across The Universe", "Across The Universe"),
            NTreeSelectOption("Let It Be", "Let It Be"),
            NTreeSelectOption("Get Back", "Get Back"),
        ),
    ),
    NTreeSelectOption(
        label = "Abbey Road",
        key = "Abbey Road",
        children = listOf(
            NTreeSelectOption("Come Together", "Come Together"),
            NTreeSelectOption("Something", "Something", disabled = true),
            NTreeSelectOption("Here Comes the Sun", "Here Comes the Sun"),
        ),
    ),
)

@Composable
fun TreeSelectDocs() {
    DocsPage(
        title = "Tree Select",
        description = "Pick values from hierarchical data. Tree select supports single and " +
            "multiple selection, cascading checkboxes, filtering and path display.",
    ) {
        DemoSection(
            title = "Basic",
            description = "A single key is selected. Branches expand with the switcher and " +
                "the selected path can be shown in the trigger.",
            code = """NTreeSelect(
    options = options,
    value = value,
    onValueChange = { value = it },
    defaultExpandAll = true,
)""",
        ) {
            var value by remember { mutableStateOf<Any?>("Norwegian Wood") }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NTreeSelect(
                    options = albumOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Please select a song",
                    defaultExpandAll = true,
                    showPath = true,
                    clearable = true,
                )
                NTreeSelect(
                    options = albumOptions,
                    value = "Drive My Car",
                    onValueChange = {},
                    placeholder = "Disabled",
                    disabled = true,
                )
            }
        }

        DemoSection(
            title = "Multiple",
            description = "Multiple keys are selected and displayed as tags.",
            code = """NTreeSelect(
    options = options,
    value = values,
    onValueChange = { values = it },
    multiple = true,
)""",
        ) {
            var values by remember {
                mutableStateOf<Any?>(
                    listOf<Any>("Norwegian Wood", "Two Of Us", "Get Back"),
                )
            }
            Column(Modifier.width(320.dp)) {
                NTreeSelect(
                    options = albumOptions,
                    value = values,
                    onValueChange = { values = it },
                    placeholder = "Please select songs",
                    defaultExpandAll = true,
                    multiple = true,
                    maxTagCount = 3,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Checkable",
            description = "Checkboxes with cascading checks. Checking a parent checks every " +
                "descendant; partially checked parents are indeterminate.",
            code = """NTreeSelect(
    options = options,
    value = values,
    onValueChange = { values = it },
    checkable = true,
    cascade = true,
    multiple = true,
)""",
        ) {
            var values by remember {
                mutableStateOf<Any?>(
                    listOf<Any>("Drive My Car", "Norwegian Wood", "Nowhere Man"),
                )
            }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NTreeSelect(
                    options = albumOptions,
                    value = values,
                    onValueChange = { values = it },
                    placeholder = "Check some songs",
                    defaultExpandAll = true,
                    multiple = true,
                    checkable = true,
                    cascade = true,
                    clearable = true,
                )
                NTreeSelect(
                    options = albumOptions,
                    value = listOf<Any>("Drive My Car"),
                    onValueChange = {},
                    placeholder = "Independent checks",
                    defaultExpandAll = true,
                    multiple = true,
                    checkable = true,
                    cascade = false,
                    disabled = true,
                )
            }
        }

        DemoSection(
            title = "Filterable",
            description = "Filter matches leaves and keeps their ancestors visible while the " +
                "query is active.",
        ) {
            var values by remember { mutableStateOf<Any?>(emptyList<Any>()) }
            Column(Modifier.width(320.dp)) {
                NTreeSelect(
                    options = albumOptions,
                    value = values,
                    onValueChange = { values = it },
                    placeholder = "Search songs",
                    filterable = true,
                    multiple = true,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Size and status",
            description = "Tree select shares the size and validation tokens of other inputs.",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            var warningValue by remember { mutableStateOf<Any?>(null) }
            var errorValue by remember { mutableStateOf<Any?>(null) }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.width(200.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    NTreeSelect(
                        options = albumOptions,
                        value = value,
                        onValueChange = { value = it },
                        placeholder = "Small",
                        defaultExpandAll = true,
                        size = NSize.Small,
                    )
                    NTreeSelect(
                        options = albumOptions,
                        value = value,
                        onValueChange = { value = it },
                        placeholder = "Medium",
                        defaultExpandAll = true,
                        size = NSize.Medium,
                    )
                    NTreeSelect(
                        options = albumOptions,
                        value = value,
                        onValueChange = { value = it },
                        placeholder = "Large",
                        defaultExpandAll = true,
                        size = NSize.Large,
                    )
                }
                Column(Modifier.width(200.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    NTreeSelect(
                        options = albumOptions,
                        value = warningValue,
                        onValueChange = { warningValue = it },
                        placeholder = "Warning",
                        defaultExpandAll = true,
                        status = NStatus.Warning,
                    )
                    NTreeSelect(
                        options = albumOptions,
                        value = errorValue,
                        onValueChange = { errorValue = it },
                        placeholder = "Error",
                        defaultExpandAll = true,
                        status = NStatus.Error,
                    )
                    NTreeSelect(
                        options = emptyList(),
                        placeholder = "No data",
                    )
                }
            }
        }
    }
}
