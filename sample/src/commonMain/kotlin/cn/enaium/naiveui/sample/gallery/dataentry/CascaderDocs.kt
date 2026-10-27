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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NButtonType
import cn.enaium.naiveui.components.NCascader
import cn.enaium.naiveui.components.NCascaderCheckStrategy
import cn.enaium.naiveui.components.NCascaderExpandTrigger
import cn.enaium.naiveui.components.NCascaderOption
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import kotlinx.coroutines.delay

private val regionOptions: List<NCascaderOption> = listOf(
    NCascaderOption(
        label = "Zhejiang",
        value = "zhejiang",
        children = listOf(
            NCascaderOption(
                label = "Hangzhou",
                value = "hangzhou",
                children = listOf(
                    NCascaderOption("West Lake", "west-lake"),
                    NCascaderOption("Lingyin Temple", "lingyin"),
                    NCascaderOption("Leifeng Pagoda", "leifeng", disabled = true),
                ),
            ),
            NCascaderOption(
                label = "Ningbo",
                value = "ningbo",
                children = listOf(
                    NCascaderOption("Tianyi Pavilion", "tianyi"),
                    NCascaderOption("Dongqian Lake", "dongqian"),
                ),
            ),
        ),
    ),
    NCascaderOption(
        label = "Jiangsu",
        value = "jiangsu",
        children = listOf(
            NCascaderOption(
                label = "Nanjing",
                value = "nanjing",
                children = listOf(
                    NCascaderOption("Sun Yat-sen Mausoleum", "sun-yat-sen"),
                    NCascaderOption("Confucius Temple", "confucius"),
                ),
            ),
            NCascaderOption(
                label = "Suzhou",
                value = "suzhou",
                children = listOf(
                    NCascaderOption("Humble Administrator's Garden", "humble-garden"),
                    NCascaderOption("Tiger Hill", "tiger-hill"),
                ),
            ),
        ),
    ),
    NCascaderOption(
        label = "Beijing",
        value = "beijing",
        disabled = true,
        children = listOf(
            NCascaderOption("Forbidden City", "forbidden-city"),
        ),
    ),
)

private fun buildOptions(
    depth: Int,
    iterator: Int = 1,
    prefix: String = "",
    length: Int = 3,
): List<NCascaderOption> = (1..length).map { index ->
    val value = if (iterator == 1) "$index" else "$prefix-$index"
    NCascaderOption(
        label = value,
        value = value,
        disabled = index % 5 == 0,
        children = if (iterator < depth) {
            buildOptions(depth, iterator + 1, value, length)
        } else {
            emptyList()
        },
    )
}

private val checkStrategyOptions: List<NCascaderOption> = buildOptions(depth = 4, length = 3)

private val largeCascaderOptions: List<NCascaderOption> = (1..1000).map { i ->
    NCascaderOption(
        label = "l-$i",
        value = "v-$i",
        disabled = i % 5 == 0,
        children = (1..2).map { j ->
            NCascaderOption(
                label = "l-$i-$j",
                value = "v-$i-$j",
                children = (1..2).map { k ->
                    NCascaderOption("l-$i-$j-$k", "v-$i-$j-$k")
                },
            )
        },
    )
}

private data class City(val name: String, val code: String)

private data class County(
    val name: String,
    val code: String,
    val cities: List<City>,
)

private val customCounties = listOf(
    County(
        "Zhejiang",
        "custom-zhejiang",
        listOf(
            City("Hangzhou", "custom-hangzhou"),
            City("Ningbo", "custom-ningbo"),
        ),
    ),
    County(
        "Jiangsu",
        "custom-jiangsu",
        listOf(
            City("Nanjing", "custom-nanjing"),
            City("Suzhou", "custom-suzhou"),
        ),
    ),
)

private fun County.toCascaderOption(): NCascaderOption = NCascaderOption(
    label = name,
    value = code,
    children = cities.map { NCascaderOption(it.name, it.code) },
)

private fun replaceCascaderChildren(
    nodes: List<NCascaderOption>,
    key: Any,
    children: List<NCascaderOption>,
): List<NCascaderOption> = nodes.map { node ->
    when {
        node.value == key -> node.copy(children = children, isLeaf = false)
        node.children.isEmpty() -> node
        else -> node.copy(children = replaceCascaderChildren(node.children, key, children))
    }
}

@Composable
fun CascaderDocs() {
    DocsPage(
        title = "Cascader",
        description = "Select values from tree-structured data by walking through columns. " +
            "Supports single, multiple and checkbox selection, async loading, filtering and " +
            "path display.",
    ) {
        DemoSection(
            title = "Single",
            description = "Click an option to open the next column and pick a leaf. The " +
                "trigger shows the full path; the second cascader opens columns on hover.",
            code = """NCascader(
    options = options,
    value = value,
    onValueChange = { value = it },
    clearable = true,
    showPath = true,
)""",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            var hoverValue by remember { mutableStateOf<Any?>("tianyi") }
            Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NCascader(
                    options = regionOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Please select a place",
                    clearable = true,
                    showPath = true,
                )
                NCascader(
                    options = regionOptions,
                    value = hoverValue,
                    onValueChange = { hoverValue = it },
                    placeholder = "Hover to expand",
                    clearable = true,
                    showPath = true,
                    expandTrigger = NCascaderExpandTrigger.Hover,
                )
            }
        }

        DemoSection(
            title = "Multiple",
            description = "Multiple mode selects several leaves with cascading checkboxes. " +
                "The menu stays open and selected leaves are shown as tags.",
            code = """NCascader(
    options = options,
    value = values,
    onValueChange = { values = it },
    multiple = true,
    clearable = true,
)""",
        ) {
            var values by remember {
                mutableStateOf<Any?>(listOf<Any>("west-lake", "tianyi"))
            }
            Column(Modifier.width(300.dp)) {
                NCascader(
                    options = regionOptions,
                    value = values,
                    onValueChange = { values = it },
                    placeholder = "Select several places",
                    multiple = true,
                    maxTagCount = 3,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Size",
            description = "Cascader uses the shared control heights.",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            Column(Modifier.width(220.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NCascader(
                    options = regionOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Small",
                    size = NSize.Small,
                    clearable = true,
                )
                NCascader(
                    options = regionOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Medium",
                    size = NSize.Medium,
                    clearable = true,
                )
                NCascader(
                    options = regionOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Large",
                    size = NSize.Large,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Single (async)",
            description = "A node with isLeaf = false and no children loads its children on " +
                "expand. loadingKeys shows a spinner on the node while the caller fetches data.",
            code = """NCascader(
    options = options,
    onLoad = { option -> load(option) },
    loadingKeys = loadingKeys,
)""",
        ) {
            var options by remember {
                mutableStateOf(listOf(NCascaderOption("l-0", "v-0", isLeaf = false)))
            }
            var loadingKeys by remember { mutableStateOf(emptySet<Any>()) }
            var pending by remember { mutableStateOf<NCascaderOption?>(null) }
            var value by remember { mutableStateOf<Any?>(null) }
            LaunchedEffect(pending) {
                val node = pending ?: return@LaunchedEffect
                delay(700)
                val children = (0..2).map { index ->
                    NCascaderOption("${node.label}-$index", "${node.value}-$index")
                }
                options = replaceCascaderChildren(options, node.value, children)
                loadingKeys = loadingKeys - node.value
                pending = null
            }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NCascader(
                    options = options,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Click the root to load",
                    loadingKeys = loadingKeys,
                    onLoad = { option ->
                        loadingKeys = loadingKeys + option.value
                        pending = option
                    },
                    clearable = true,
                )
                NText("value: ${value ?: "none"}", size = 12.sp, depth = 3)
            }
        }

        DemoSection(
            title = "Multiple (async)",
            description = "Async children combined with checkable multiple selection.",
        ) {
            var options by remember {
                mutableStateOf(listOf(NCascaderOption("l-0", "v-0", isLeaf = false)))
            }
            var loadingKeys by remember { mutableStateOf(emptySet<Any>()) }
            var pending by remember { mutableStateOf<NCascaderOption?>(null) }
            var values by remember { mutableStateOf<Any?>(emptyList<Any>()) }
            LaunchedEffect(pending) {
                val node = pending ?: return@LaunchedEffect
                delay(700)
                val children = (0..2).map { index ->
                    NCascaderOption("${node.label}-$index", "${node.value}-$index")
                }
                options = replaceCascaderChildren(options, node.value, children)
                loadingKeys = loadingKeys - node.value
                pending = null
            }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NCascader(
                    options = options,
                    value = values,
                    onValueChange = { values = it },
                    placeholder = "Expand then check",
                    multiple = true,
                    checkable = true,
                    cascade = true,
                    loadingKeys = loadingKeys,
                    onLoad = { option ->
                        loadingKeys = loadingKeys + option.value
                        pending = option
                    },
                    clearable = true,
                )
                NText("value: ${values.toString()}", size = 12.sp, depth = 3)
            }
        }

        DemoSection(
            title = "Action",
            description = "Header, action and suffix slots customize the menu chrome.",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            Column(Modifier.width(300.dp)) {
                NCascader(
                    options = regionOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Please select a place",
                    clearable = true,
                    suffix = {
                        NIcon(NIcons.Filter, size = 16.dp)
                    },
                    header = {
                        NText("Standing on a bridge", size = 12.sp, depth = 3)
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
                                onClick = { value = "west-lake" },
                            ) { NText("Pick West Lake") }
                        }
                    },
                )
            }
        }

        DemoSection(
            title = "Virtual (large data)",
            description = "Long columns are rendered with virtual scrolling; this example " +
                "contains 1000 roots with 7000 entries in total.",
            code = """NCascader(
    options = largeOptions,
    value = value,
    onValueChange = { value = it },
    virtualScroll = true,
)""",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            Column(Modifier.width(300.dp)) {
                NCascader(
                    options = largeCascaderOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Meaningless values",
                    clearable = true,
                    virtualScroll = true,
                )
            }
        }

        DemoSection(
            title = "Check strategy",
            description = "All reports every checked node, Parent keeps only the highest fully " +
                "checked node and Child reports leaves only.",
        ) {
            var strategy by remember { mutableStateOf(NCascaderCheckStrategy.All) }
            var values by remember { mutableStateOf<Any?>(emptyList<Any>()) }
            Column(Modifier.width(340.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NCascaderCheckStrategy.entries.forEach { entry ->
                        NButton(
                            type = if (strategy == entry) NButtonType.Primary else NButtonType.Default,
                            size = NSize.Small,
                            onClick = {
                                strategy = entry
                                values = emptyList<Any>()
                            },
                        ) { NText(entry.name) }
                    }
                }
                NCascader(
                    options = checkStrategyOptions,
                    value = values,
                    onValueChange = { values = it },
                    placeholder = "Check some nodes",
                    multiple = true,
                    cascade = true,
                    checkStrategy = strategy,
                    clearable = true,
                )
                NText("value: ${values.toString()}", size = 12.sp, depth = 3)
            }
        }

        DemoSection(
            title = "Custom fields",
            description = "Adapt domain objects to cascader options; typed options replace " +
                "label-field and value-field configuration.",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            val options = customCounties.map { it.toCascaderOption() }
            Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NCascader(
                    options = options,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Custom data class",
                    clearable = true,
                )
                NText("value: ${value ?: "none"}", size = 12.sp, depth = 3)
            }
        }

        DemoSection(
            title = "Custom rendering",
            description = "renderLabel replaces the option label while keeping the check, " +
                "arrow and disabled behavior.",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            Column(Modifier.width(300.dp)) {
                NCascader(
                    options = regionOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Custom labels",
                    filterable = true,
                    clearable = true,
                    renderLabel = { option, _ ->
                        NText("prefix ${option.label}", maxLines = 1)
                    },
                )
            }
        }

        DemoSection(
            title = "Focus",
            description = "Manual focus through focusRequester, with a blur a second later.",
        ) {
            val focusRequester = remember { FocusRequester() }
            val focusManager = LocalFocusManager.current
            var focusing by remember { mutableStateOf(false) }
            LaunchedEffect(focusing) {
                if (focusing) {
                    focusRequester.requestFocus()
                    delay(1000)
                    focusManager.clearFocus()
                    focusing = false
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NButton(onClick = { focusing = true }) {
                    NText("Focus then blur in 1 second")
                }
                NCascader(
                    modifier = Modifier.width(200.dp),
                    options = regionOptions,
                    placeholder = "Please select a place",
                    focusRequester = focusRequester,
                )
            }
        }

        DemoSection(
            title = "Status",
            description = "Validation status recolors the trigger border outside a form.",
        ) {
            var warning by remember { mutableStateOf<Any?>(null) }
            var error by remember { mutableStateOf<Any?>(null) }
            Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NCascader(
                    options = regionOptions,
                    value = warning,
                    onValueChange = { warning = it },
                    placeholder = "Warning",
                    status = NStatus.Warning,
                    clearable = true,
                )
                NCascader(
                    options = regionOptions,
                    value = error,
                    onValueChange = { error = it },
                    placeholder = "Error",
                    status = NStatus.Error,
                    clearable = true,
                )
                NCascader(
                    options = emptyList(),
                    placeholder = "No data",
                )
            }
        }

        DemoSection(
            title = "Filterable",
            description = "Search every level; matches show their parent path.",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            Column(Modifier.width(300.dp)) {
                NCascader(
                    options = regionOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Search places",
                    filterable = true,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Change on select",
            description = "When changeOnSelect is enabled a branch can be selected without " +
                "walking to a leaf.",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            Column(Modifier.width(300.dp)) {
                NCascader(
                    options = regionOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Any level is selectable",
                    changeOnSelect = true,
                    clearable = true,
                )
            }
        }
    }
}
