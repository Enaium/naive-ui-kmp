package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NButtonType
import cn.enaium.naiveui.components.NCheckbox
import cn.enaium.naiveui.components.NTreeSelect
import cn.enaium.naiveui.components.NTreeSelectNodeBehavior
import cn.enaium.naiveui.components.NTreeSelectOption
import cn.enaium.naiveui.components.NTreeCheckStrategy
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import kotlinx.coroutines.delay

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

private val fileOptions: List<NTreeSelectOption> = listOf(
    NTreeSelectOption(
        "Folder-1",
        "Folder-1",
        children = listOf(
            NTreeSelectOption("File-1-1", "File-1-1"),
            NTreeSelectOption(
                "Folder-1-2",
                "Folder-1-2",
                children = listOf(
                    NTreeSelectOption("File-1-2-1", "File-1-2-1"),
                    NTreeSelectOption("File-1-2-2", "File-1-2-2"),
                ),
            ),
        ),
    ),
    NTreeSelectOption(
        "Folder-2",
        "Folder-2",
        children = listOf(
            NTreeSelectOption("File-2-1", "File-2-1"),
            NTreeSelectOption("File-2-2", "File-2-2"),
        ),
    ),
)

private data class AlbumSong(val title: String, val id: String)

private data class Album(
    val name: String,
    val code: String,
    val songs: List<AlbumSong>,
)

private val customAlbums = listOf(
    Album(
        "Rubber Soul",
        "rubber-soul",
        listOf(
            AlbumSong("Drive My Car", "rs-1"),
            AlbumSong("Norwegian Wood", "rs-2"),
            AlbumSong("Nowhere Man", "rs-3"),
        ),
    ),
    Album(
        "Let It Be",
        "let-it-be",
        listOf(
            AlbumSong("Two Of Us", "lib-1"),
            AlbumSong("Get Back", "lib-2"),
        ),
    ),
)

private fun Album.toTreeOption(): NTreeSelectOption = NTreeSelectOption(
    label = name,
    key = code,
    children = songs.map { NTreeSelectOption(it.title, it.id) },
)

private fun replaceTreeChildren(
    nodes: List<NTreeSelectOption>,
    key: Any,
    children: List<NTreeSelectOption>,
): List<NTreeSelectOption> = nodes.map { node ->
    when {
        node.key == key -> node.copy(children = children, isLeaf = false)
        node.children.isEmpty() -> node
        else -> node.copy(children = replaceTreeChildren(node.children, key, children))
    }
}

@Composable
fun TreeSelectDocs() {
    DocsPage(
        title = "Tree Select",
        description = "Pick values from hierarchical data. Tree select supports single and " +
            "multiple selection, cascading checkboxes, async loading, filtering and path display.",
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
            title = "Checkbox",
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
            title = "Check strategy",
            description = "All reports every checked node, Parent keeps only the highest fully " +
                "checked node and Child reports leaves only.",
        ) {
            var strategy by remember { mutableStateOf(NTreeCheckStrategy.All) }
            var value by remember {
                mutableStateOf<Any?>(
                    listOf<Any>("Drive My Car", "Norwegian Wood", "Nowhere Man"),
                )
            }
            Column(Modifier.width(340.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NTreeCheckStrategy.entries.forEach { entry ->
                        NButton(
                            type = if (strategy == entry) NButtonType.Primary else NButtonType.Default,
                            size = NSize.Small,
                            onClick = {
                                strategy = entry
                                value = listOf<Any>("Drive My Car", "Norwegian Wood", "Nowhere Man")
                            },
                        ) { NText(entry.name) }
                    }
                }
                NTreeSelect(
                    options = albumOptions,
                    value = value,
                    onValueChange = { value = it },
                    defaultExpandAll = true,
                    multiple = true,
                    checkable = true,
                    cascade = true,
                    checkStrategy = strategy,
                    placeholder = "Check some songs",
                )
                NText("value: ${value.toString()}", size = 12.sp, depth = 3)
            }
        }

        DemoSection(
            title = "Show line",
            description = "Tree connecting lines are drawn for each level.",
        ) {
            var value by remember { mutableStateOf<Any?>("Drive My Car") }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NTreeSelect(
                    options = albumOptions,
                    value = value,
                    onValueChange = { value = it },
                    defaultExpandAll = true,
                    showLine = true,
                    showPath = true,
                    clearable = true,
                )
                NTreeSelect(
                    options = albumOptions,
                    value = value,
                    onValueChange = { value = it },
                    defaultExpandAll = true,
                    showLine = false,
                    placeholder = "Without lines",
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Filterable",
            description = "Filter matches leaves and keeps their ancestors visible while the " +
                "query is active.",
        ) {
            var single by remember { mutableStateOf<Any?>("Drive My Car") }
            var many by remember { mutableStateOf<Any?>(listOf<Any>("Norwegian Wood")) }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NTreeSelect(
                    options = albumOptions,
                    value = single,
                    onValueChange = { single = it },
                    defaultExpandAll = true,
                    filterable = true,
                    clearable = true,
                )
                NTreeSelect(
                    options = albumOptions,
                    value = many,
                    onValueChange = { many = it },
                    defaultExpandAll = true,
                    filterable = true,
                    multiple = true,
                    checkable = true,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Action",
            description = "Header and action slots surround the tree menu.",
        ) {
            var value by remember { mutableStateOf<Any?>("Drive My Car") }
            Column(Modifier.width(320.dp)) {
                NTreeSelect(
                    options = albumOptions,
                    value = value,
                    onValueChange = { value = it },
                    defaultExpandAll = true,
                    header = {
                        NText("Don't know what to put", size = 12.sp, depth = 3)
                    },
                    action = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            NButton(
                                size = NSize.Small,
                                text = true,
                                onClick = { value = null },
                            ) { NText("Clear") }
                            NButton(
                                size = NSize.Small,
                                text = true,
                                onClick = { value = "Norwegian Wood" },
                            ) { NText("Pick Norwegian Wood") }
                        }
                    },
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Async",
            description = "A node with isLeaf = false and no children is loaded on expand. " +
                "onLoad reports the node; the caller supplies children and loadingKeys shows a " +
                "spinner.",
            code = """NTreeSelect(
    options = options,
    onLoad = { node -> load(node) },
    loadingKeys = loadingKeys,
    checkable = true,
    multiple = true,
)""",
        ) {
            var options by remember {
                mutableStateOf(listOf(NTreeSelectOption("l-0", "v-0", isLeaf = false)))
            }
            var loadingKeys by remember { mutableStateOf(emptySet<Any>()) }
            var pending by remember { mutableStateOf<NTreeSelectOption?>(null) }
            var value by remember { mutableStateOf<Any?>(null) }
            LaunchedEffect(pending) {
                val node = pending ?: return@LaunchedEffect
                delay(700)
                val children = (0..2).map { index ->
                    NTreeSelectOption(
                        label = "${node.label}-$index",
                        key = "${node.key}-$index",
                        isLeaf = true,
                    )
                }
                options = replaceTreeChildren(options, node.key, children)
                loadingKeys = loadingKeys - node.key
                pending = null
            }
            Column(Modifier.width(340.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NTreeSelect(
                    options = options,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Expand the root to load children",
                    multiple = true,
                    checkable = true,
                    cascade = true,
                    loadingKeys = loadingKeys,
                    onLoad = { node ->
                        loadingKeys = loadingKeys + node.key
                        pending = node
                    },
                    clearable = true,
                )
                NText("value: ${value.toString()}", size = 12.sp, depth = 3)
            }
        }

        DemoSection(
            title = "Status",
            description = "Validation status recolors the trigger border outside a form.",
        ) {
            var warning by remember { mutableStateOf<Any?>(null) }
            var error by remember { mutableStateOf<Any?>(null) }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NTreeSelect(
                    options = albumOptions,
                    value = warning,
                    onValueChange = { warning = it },
                    placeholder = "Warning",
                    defaultExpandAll = true,
                    status = NStatus.Warning,
                    clearable = true,
                )
                NTreeSelect(
                    options = albumOptions,
                    value = error,
                    onValueChange = { error = it },
                    placeholder = "Error",
                    defaultExpandAll = true,
                    status = NStatus.Error,
                    clearable = true,
                )
                NTreeSelect(
                    options = emptyList(),
                    placeholder = "No data",
                )
            }
        }

        DemoSection(
            title = "File picker",
            description = "nodeClickBehavior makes folders expand instead of selecting, so " +
                "only files produce a value.",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            Column(Modifier.width(340.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NTreeSelect(
                    options = fileOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Select a file",
                    clearable = true,
                    nodeClickBehavior = { option ->
                        if (option.children.isNotEmpty()) {
                            NTreeSelectNodeBehavior.ToggleExpand
                        } else {
                            NTreeSelectNodeBehavior.Default
                        }
                    },
                )
                NText("selected file: ${value ?: "none"}", size = 12.sp, depth = 3)
            }
        }

        DemoSection(
            title = "Debug",
            description = "Toggle the main props against a four level tree and inspect the " +
                "reported value.",
        ) {
            var multiple by remember { mutableStateOf(false) }
            var checkable by remember { mutableStateOf(false) }
            var cascade by remember { mutableStateOf(false) }
            var filterable by remember { mutableStateOf(false) }
            var value by remember { mutableStateOf<Any?>(null) }
            Column(Modifier.width(360.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NCheckbox(
                        checked = multiple,
                        label = "Multiple",
                        onCheckedChange = {
                            multiple = it
                            value = null
                        },
                    )
                    NCheckbox(
                        checked = checkable,
                        label = "Checkable",
                        onCheckedChange = {
                            checkable = it
                            value = null
                        },
                    )
                    NCheckbox(
                        checked = cascade,
                        label = "Cascade",
                        onCheckedChange = {
                            cascade = it
                            value = null
                        },
                    )
                    NCheckbox(
                        checked = filterable,
                        label = "Filterable",
                        onCheckedChange = { filterable = it },
                    )
                }
                NTreeSelect(
                    options = albumOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Debug tree select",
                    defaultExpandAll = true,
                    multiple = multiple,
                    checkable = checkable,
                    cascade = cascade,
                    filterable = filterable,
                    clearable = true,
                )
                NText("value: ${value.toString()}", size = 12.sp, depth = 3)
            }
        }

        DemoSection(
            title = "Custom fields",
            description = "Map domain objects to tree options once; typed options replace " +
                "label-field and key-field configuration.",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            val options = customAlbums.map { it.toTreeOption() }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NTreeSelect(
                    options = options,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Custom data class",
                    defaultExpandAll = true,
                    clearable = true,
                )
                NText("value: ${value ?: "none"}", size = 12.sp, depth = 3)
            }
        }
    }
}
