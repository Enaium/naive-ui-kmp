package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NButtonType
import cn.enaium.naiveui.components.NInput
import cn.enaium.naiveui.components.NSwitch
import cn.enaium.naiveui.components.NTree
import cn.enaium.naiveui.components.NTreeCheckboxPlacement
import cn.enaium.naiveui.components.NTreeClickBehavior
import cn.enaium.naiveui.components.NTreeOption
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NTextType
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoRow
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TreeDocs() {
    DocsPage(
        title = "Tree",
        description = "Display hierarchical data with expandable nodes, selection and optional " +
            "cascade checking. Nodes can be loaded lazily and rendered with custom labels.",
    ) {
        DemoSection(
            title = "Basic",
            description = "A tree is generated from nested options. Expanded keys are provided " +
                "through defaultExpandedKeys; blockLine makes the whole row selectable.",
            code = """
                NTree(
                    data = data,
                    blockLine = true,
                    expandOnClick = true,
                    defaultExpandedKeys = listOf("40", "41"),
                )
            """.trimIndent(),
        ) {
            NTree(
                data = remember { treeData() },
                blockLine = true,
                expandOnClick = true,
                defaultExpandedKeys = listOf("40", "41"),
            )
        }

        DemoSection(
            title = "Checkable",
            description = "Set checkable to render checkboxes. Without cascade, checking a node " +
                "does not affect its parent or children.",
            code = """
                NTree(
                    data = data,
                    checkable = true,
                    blockLine = true,
                    defaultExpandedKeys = listOf("40"),
                )
            """.trimIndent(),
        ) {
            NTree(
                data = remember { treeData() },
                checkable = true,
                blockLine = true,
                defaultExpandedKeys = listOf("40"),
            )
        }

        DemoSection(
            title = "Cascade checking",
            description = "With cascade enabled, checking a node checks its whole subtree and " +
                "parents become checked once every child is checked. Partially checked parents " +
                "are shown as indeterminate.",
            code = """
                NTree(
                    data = data,
                    checkable = true,
                    cascade = true,
                    defaultExpandedKeys = listOf("40", "4030", "403020"),
                    defaultCheckedKeys = listOf("40302010"),
                )
            """.trimIndent(),
        ) {
            NTree(
                data = remember { treeData() },
                checkable = true,
                cascade = true,
                blockLine = true,
                defaultExpandedKeys = listOf("40", "4030", "403020"),
                defaultCheckedKeys = listOf("40302010"),
            )
        }

        DemoSection(
            title = "Default expand all",
            description = "defaultExpandAll expands every node that already has children. " +
                "showLine adds guide lines between nested nodes.",
            code = """
                NTree(
                    data = data,
                    blockLine = true,
                    showLine = true,
                    defaultExpandAll = true,
                )
            """.trimIndent(),
        ) {
            NTree(
                data = remember { compactTreeData() },
                blockLine = true,
                showLine = true,
                defaultExpandAll = true,
            )
        }

        DemoSection(
            title = "Selected",
            description = "Selected keys can be controlled. Multiple selection allows several " +
                "nodes to be active at once; the current keys are shown above the tree.",
            code = """
                var selected by remember { mutableStateOf(listOf("40")) }
                NTree(
                    data = data,
                    selectedKeys = selected,
                    onUpdateSelectedKeys = { selected = it },
                    multiple = true,
                    blockLine = true,
                )
            """.trimIndent(),
        ) {
            var selected by remember { mutableStateOf(listOf("40")) }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NText("Selected keys: ${selected.joinToString().ifEmpty { "none" }}")
                NTree(
                    data = remember { treeData() },
                    selectedKeys = selected,
                    onUpdateSelectedKeys = { selected = it },
                    multiple = true,
                    blockLine = true,
                    defaultExpandedKeys = listOf("40", "41"),
                )
            }
        }

        DemoSection(
            title = "Async loading",
            description = "A node with isLeaf = false and no children is treated as unloaded. " +
                "Expanding it calls onLoad; the returned options become its children.",
            code = """
                NTree(
                    data = data,
                    blockLine = true,
                    onLoad = { node ->
                        delay(600)
                        listOf(
                            NTreeOption(
                                label = nextLabel(node.label),
                                key = "${'$'}{node.key}-child",
                                isLeaf = false,
                            )
                        )
                    },
                )
            """.trimIndent(),
        ) {
            NTree(
                data = remember { asyncTreeData() },
                blockLine = true,
                onLoad = { node ->
                    delay(600)
                    listOf(
                        NTreeOption(
                            label = nextTreeLabel(node.label),
                            key = "${node.key}-child",
                            isLeaf = false,
                        ),
                    )
                },
            )
        }

        DemoSection(
            title = "Block line",
            description = "blockLine stretches the hover and selection background across the " +
                "whole row, while indent controls the width of each nesting level.",
            code = """
                NTree(
                    data = data,
                    blockLine = true,
                    indent = 20.dp,
                    defaultExpandedKeys = listOf("40", "41"),
                )
            """.trimIndent(),
        ) {
            NTree(
                data = remember { treeData() },
                blockLine = true,
                indent = 20.dp,
                defaultExpandedKeys = listOf("40", "41"),
            )
        }

        DemoSection(
            title = "Drag and drop",
            description = "Set draggable and handle onDrop to reorder nodes. The callback reports " +
                "the dragged node, the target node and whether it was dropped before, inside or after it.",
            code = """
NTree(
    data = nodes,
    draggable = true,
    onDrop = { info -> nodes = reorder(nodes, info) },
)
""",
        ) {
            var dragNodes by remember {
                mutableStateOf(
                    listOf(
                        NTreeOption(label = "Alpha", key = "alpha"),
                        NTreeOption(label = "Beta", key = "beta"),
                        NTreeOption(label = "Gamma", key = "gamma"),
                        NTreeOption(label = "Delta", key = "delta"),
                    ),
                )
            }
            var lastDrop by remember { mutableStateOf("Drag a node onto another one") }
            NTree(
                data = dragNodes,
                draggable = true,
                onDrop = { info ->
                    lastDrop = "${info.dragNode.label} dropped ${info.dropPosition.name.lowercase()} ${info.node.label}"
                    val list = dragNodes.toMutableList()
                    val from = list.indexOfFirst { it.key == info.dragNode.key }
                    val to = list.indexOfFirst { it.key == info.node.key }
                    if (from >= 0 && to >= 0) {
                        list.removeAt(from)
                        val index = if (info.dropPosition.name == "After") to + 1 else to
                        list.add(index.coerceIn(0, list.size), info.dragNode)
                        dragNodes = list
                    }
                },
            )
            NText(lastDrop, color = LocalNTheme.current.colors.text3)
        }

        DemoSection(
            title = "Disabled nodes",
            description = "Disabled nodes cannot be selected, checked or expanded. A node with " +
                "checkboxDisabled keeps its own interactions while its checkbox is disabled.",
            code = """
                NTree(
                    data = data,
                    checkable = true,
                    cascade = true,
                    defaultExpandAll = true,
                    defaultCheckedKeys = listOf("1-1"),
                )
            """.trimIndent(),
        ) {
            NTree(
                data = remember { disabledTreeData() },
                blockLine = true,
                checkable = true,
                cascade = true,
                defaultExpandAll = true,
                defaultCheckedKeys = listOf("1-1"),
            )
        }

        DemoSection(
            title = "Customize key and label field",
            description = "The KMP port uses typed NTreeOption values. Backend records with " +
                "different field names, such as whateverKey and whateverLabel, are mapped into " +
                "options before rendering.",
            code = """
                val options = backendNodes.map { it.toTreeOption() }
                NTree(data = options, blockLine = true)
            """.trimIndent(),
        ) {
            NTree(
                data = remember { customFieldTreeData() },
                blockLine = true,
                defaultExpandedKeys = listOf("40", "41"),
            )
        }

        DemoSection(
            title = "Select multiple nodes",
            description = "Set multiple to toggle several selected nodes. The current keys are " +
                "shown above the tree.",
            code = """
                NTree(
                    data = data,
                    multiple = true,
                    blockLine = true,
                    selectedKeys = selected,
                    onUpdateSelectedKeys = { selected = it },
                )
            """.trimIndent(),
        ) {
            var selected by remember { mutableStateOf(emptyList<String>()) }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NText("Selected keys: ${selected.joinToString().ifEmpty { "none" }}")
                NTree(
                    data = remember { treeData() },
                    multiple = true,
                    blockLine = true,
                    selectedKeys = selected,
                    onUpdateSelectedKeys = { selected = it },
                    defaultExpandedKeys = listOf("40", "41"),
                )
            }
        }

        DemoSection(
            title = "Search",
            description = "Pass pattern to filter nodes by label. Ancestors of matches are " +
                "expanded automatically and matches are underlined. Toggle irrelevant nodes to " +
                "keep or hide the rest of the tree.",
            code = """
                NTree(
                    data = data,
                    pattern = pattern,
                    showIrrelevantNodes = showIrrelevantNodes,
                    blockLine = true,
                )
            """.trimIndent(),
        ) {
            var pattern by remember { mutableStateOf("") }
            var showIrrelevantNodes by remember { mutableStateOf(false) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NInput(
                    value = pattern,
                    onValueChange = { pattern = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "Search",
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    NSwitch(
                        checked = showIrrelevantNodes,
                        onCheckedChange = { showIrrelevantNodes = it },
                    )
                    NText(
                        if (showIrrelevantNodes) "Show irrelevant nodes" else "Hide irrelevant nodes",
                    )
                }
                NTree(
                    data = remember { filterTreeData() },
                    pattern = pattern,
                    showIrrelevantNodes = showIrrelevantNodes,
                    blockLine = true,
                )
            }
        }

        DemoSection(
            title = "Virtual scroll",
            description = "virtualScroll renders only visible rows through a lazy list. Give the " +
                "tree a height and scroll through a large expanded data set.",
            code = """
                NTree(
                    data = data,
                    defaultExpandAll = true,
                    virtualScroll = true,
                    height = 320.dp,
                    virtualScrollState = listState,
                )
            """.trimIndent(),
        ) {
            val listState = rememberLazyListState()
            val scope = rememberCoroutineScope()
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DemoRow {
                    NButton(onClick = { scope.launch { listState.animateScrollToItem(0) } }) {
                        NText("Scroll to top")
                    }
                    NButton(
                        onClick = {
                            val last = (listState.layoutInfo.totalItemsCount - 1).coerceAtLeast(0)
                            scope.launch { listState.animateScrollToItem(last) }
                        },
                    ) {
                        NText("Scroll to bottom")
                    }
                }
                NTree(
                    data = remember { largeTreeData() },
                    blockLine = true,
                    defaultExpandAll = true,
                    virtualScroll = true,
                    height = 320.dp,
                    virtualScrollState = listState,
                )
            }
        }

        DemoSection(
            title = "Prefix and suffix",
            description = "Each option may carry prefix and suffix slots, rendered before and " +
                "after its label. Here every node shows an action on each side.",
            code = """
                NTree(
                    data = data,
                    blockLine = true,
                    selectable = false,
                    defaultExpandedKeys = listOf("40", "41"),
                )
            """.trimIndent(),
        ) {
            NTree(
                data = remember { withActionSlots(treeData()) },
                blockLine = true,
                selectable = false,
                defaultExpandedKeys = listOf("40", "41"),
            )
        }

        DemoSection(
            title = "Batch rendering",
            description = "renderPrefix, renderLabel and renderSuffix apply to every node at " +
                "once, including nodes without their own prefix or suffix.",
            code = """
                NTree(
                    data = data,
                    renderPrefix = { _, _, _ -> NText("Prefix", type = NTextType.Primary) },
                    renderLabel = { option, _, _ -> NText("${'$'}{option.label} :)") },
                    renderSuffix = { _, _, _ -> NText("Suffix", type = NTextType.Primary) },
                )
            """.trimIndent(),
        ) {
            NTree(
                data = remember { treeData() },
                blockLine = true,
                selectable = false,
                defaultExpandedKeys = listOf("40", "41"),
                renderPrefix = { _, _, _ -> NText("Prefix", type = NTextType.Primary) },
                renderLabel = { option, _, _ -> NText("${option.label} :)") },
                renderSuffix = { _, _, _ -> NText("Suffix", type = NTextType.Primary) },
            )
        }

        DemoSection(
            title = "Switcher icon",
            description = "renderSwitcherIcon replaces the expand arrow. It receives the node, " +
                "its expanded state and its selected state.",
            code = """
                NTree(
                    data = data,
                    renderSwitcherIcon = { _, expanded, _ ->
                        NIcon(if (expanded) NIcons.Sun else NIcons.Moon, size = 14.dp)
                    },
                )
            """.trimIndent(),
        ) {
            val data = remember { treeData() }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NTree(
                    data = data,
                    blockLine = true,
                    defaultExpandedKeys = listOf("40", "41"),
                    renderSwitcherIcon = { _, _, _ -> NIcon(NIcons.ChevronRight, size = 14.dp) },
                )
                NTree(
                    data = data,
                    blockLine = true,
                    defaultExpandedKeys = listOf("40", "41"),
                    renderSwitcherIcon = { _, expanded, _ ->
                        NIcon(if (expanded) NIcons.Sun else NIcons.Moon, size = 14.dp)
                    },
                )
            }
        }

        DemoSection(
            title = "File tree",
            description = "Folder icons follow the expanded state. Node clicks are reported " +
                "through onNodeClick; expanding uses controlled expandedKeys.",
            code = """
                NTree(
                    data = data,
                    blockLine = true,
                    expandOnClick = true,
                    expandedKeys = expandedKeys,
                    onUpdateExpandedKeys = { expandedKeys = it },
                    onNodeClick = { option -> lastClick = option.label },
                )
            """.trimIndent(),
        ) {
            val theme = LocalNTheme.current
            var expandedKeys by remember { mutableStateOf(listOf("Folder")) }
            var lastClick by remember { mutableStateOf("Click a file") }
            val data = remember(expandedKeys) {
                fileTreeData(expandedKeys.toSet(), theme.colors.primary, theme.colors.icon)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NText(lastClick, color = theme.colors.text3)
                NTree(
                    data = data,
                    blockLine = true,
                    expandOnClick = true,
                    expandedKeys = expandedKeys,
                    onUpdateExpandedKeys = { expandedKeys = it },
                    onNodeClick = { option ->
                        if (option.children.isNullOrEmpty() && !option.disabled) {
                            lastClick = "[Click] ${option.label}"
                        }
                    },
                )
            }
        }

        DemoSection(
            title = "Node events",
            description = "onNodeClick binds a click handler to every node. Combined with " +
                "overrideClickBehavior it can observe or replace the default selection behavior.",
            code = """
                NTree(
                    data = data,
                    blockLine = true,
                    onNodeClick = { option -> lastClick = option.label },
                )
            """.trimIndent(),
        ) {
            var lastClick by remember { mutableStateOf("Click a node") }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NText(lastClick, color = LocalNTheme.current.colors.text3)
                NTree(
                    data = remember { treeData() },
                    blockLine = true,
                    defaultExpandedKeys = listOf("40", "41"),
                    onNodeClick = { lastClick = "[Click] ${it.label}" },
                )
            }
        }

        DemoSection(
            title = "Show line",
            description = "Toggle showLine to draw connection guides between nested nodes.",
            code = """
                NTree(
                    data = data,
                    showLine = showLine,
                    checkable = true,
                    expandOnClick = true,
                )
            """.trimIndent(),
        ) {
            var showLine by remember { mutableStateOf(false) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    NSwitch(checked = showLine, onCheckedChange = { showLine = it })
                    NText("Show line")
                }
                NTree(
                    data = remember { treeData() },
                    showLine = showLine,
                    checkable = true,
                    expandOnClick = true,
                    defaultExpandedKeys = listOf("40", "4030", "403020"),
                )
            }
        }

        DemoSection(
            title = "Checkbox placement",
            description = "checkboxPlacement places the checkbox on the right side of each row.",
            code = """
                NTree(
                    data = data,
                    checkable = true,
                    checkboxPlacement = NTreeCheckboxPlacement.Right,
                    defaultExpandedKeys = listOf("40", "4030", "403020"),
                    defaultCheckedKeys = listOf("40302010"),
                )
            """.trimIndent(),
        ) {
            NTree(
                data = remember { treeData() },
                checkable = true,
                blockLine = true,
                checkboxPlacement = NTreeCheckboxPlacement.Right,
                defaultExpandedKeys = listOf("40", "4030", "403020"),
                defaultCheckedKeys = listOf("40302010"),
            )
        }

        DemoSection(
            title = "Custom node click behavior",
            description = "overrideClickBehavior can map clicks to expand, select, check or no " +
                "action. Folders expand and only files are selected here.",
            code = """
                NTree(
                    data = data,
                    blockLine = true,
                    selectedKeys = selected,
                    onUpdateSelectedKeys = { selected = it },
                    overrideClickBehavior = { option ->
                        if (!option.children.isNullOrEmpty()) {
                            NTreeClickBehavior.ToggleExpand
                        } else {
                            NTreeClickBehavior.Default
                        }
                    },
                )
            """.trimIndent(),
        ) {
            var selected by remember { mutableStateOf(emptyList<String>()) }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NText("Selected files: ${selected.joinToString().ifEmpty { "none" }}")
                NTree(
                    data = remember { overrideTreeData() },
                    blockLine = true,
                    selectedKeys = selected,
                    onUpdateSelectedKeys = { selected = it },
                    overrideClickBehavior = { option ->
                        if (!option.children.isNullOrEmpty()) {
                            NTreeClickBehavior.ToggleExpand
                        } else {
                            NTreeClickBehavior.Default
                        }
                    },
                )
            }
        }
    }
}

private fun treeData(level: Int = 4, baseKey: String = ""): List<NTreeOption> {
    if (level == 0) return emptyList()
    return (0 until 6 - level).map { index ->
        val key = "$baseKey$level$index"
        NTreeOption(
            label = treeLabel(level),
            key = key,
            children = treeData(level - 1, key),
        )
    }
}

private fun treeLabel(level: Int): String = when (level) {
    4 -> "Out of Tao, One is born"
    3 -> "Out of One, Two"
    2 -> "Out of Two, Three"
    1 -> "Out of Three, the created universe"
    else -> ""
}

private fun compactTreeData(): List<NTreeOption> = listOf(
    NTreeOption(
        label = "Out of Tao, One is born",
        key = "40",
        children = listOf(
            NTreeOption(
                label = "Out of One, Two",
                key = "4030",
                children = listOf(
                    NTreeOption(label = "Out of Two, Three", key = "403020"),
                    NTreeOption(label = "Out of Two, Three", key = "403021"),
                ),
            ),
            NTreeOption(label = "Out of One, Two", key = "4031"),
        ),
    ),
    NTreeOption(
        label = "Out of Tao, One is born",
        key = "41",
        children = listOf(
            NTreeOption(label = "Out of One, Two", key = "4130"),
        ),
    ),
)

private fun asyncTreeData(): List<NTreeOption> = listOf(
    NTreeOption(label = "Out of Tao, One is born", key = "1", isLeaf = false),
    NTreeOption(label = "Out of Tao, One is born", key = "2", isLeaf = false),
)

private fun nextTreeLabel(current: String): String = when (current) {
    "Out of Tao, One is born" -> "Out of One, Two"
    "Out of One, Two" -> "Out of Two, Three"
    "Out of Two, Three" -> "Out of Three, the created universe"
    else -> "Out of Tao, One is born"
}

private fun disabledTreeData(): List<NTreeOption> = listOf(
    NTreeOption(
        label = "0",
        key = "0",
        children = listOf(
            NTreeOption(label = "0-0", key = "0-0"),
            NTreeOption(
                label = "0-1",
                key = "0-1",
                disabled = true,
                children = listOf(
                    NTreeOption(label = "0-1-0", key = "0-1-0"),
                    NTreeOption(label = "0-1-1", key = "0-1-1"),
                ),
            ),
        ),
    ),
    NTreeOption(
        label = "1",
        key = "1",
        children = listOf(
            NTreeOption(label = "1-0", key = "1-0"),
            NTreeOption(
                label = "1-1",
                key = "1-1",
                checkboxDisabled = true,
                children = listOf(
                    NTreeOption(label = "1-1-0", key = "1-1-0"),
                    NTreeOption(label = "1-1-1", key = "1-1-1"),
                ),
            ),
        ),
    ),
)

private fun filterTreeData(): List<NTreeOption> = listOf(
    NTreeOption(
        label = "0",
        key = "0",
        children = listOf(
            NTreeOption(
                label = "0-0",
                key = "0-0",
                children = listOf(
                    NTreeOption(label = "0-0-0", key = "0-0-0"),
                    NTreeOption(label = "0-0-1", key = "0-0-1"),
                ),
            ),
            NTreeOption(
                label = "0-1",
                key = "0-1",
                children = listOf(
                    NTreeOption(label = "0-1-0", key = "0-1-0"),
                    NTreeOption(label = "0-1-1", key = "0-1-1"),
                ),
            ),
        ),
    ),
    NTreeOption(
        label = "1",
        key = "1",
        children = listOf(
            NTreeOption(
                label = "1-0",
                key = "1-0",
                children = listOf(
                    NTreeOption(label = "1-0-0", key = "1-0-0"),
                    NTreeOption(label = "1-0-1", key = "1-0-1"),
                ),
            ),
            NTreeOption(
                label = "1-1",
                key = "1-1",
                children = listOf(
                    NTreeOption(label = "1-1-0", key = "1-1-0"),
                    NTreeOption(label = "1-1-1", key = "1-1-1"),
                ),
            ),
        ),
    ),
)

private fun largeTreeData(level: Int = 4, baseKey: String = ""): List<NTreeOption> {
    if (level == 0) return emptyList()
    return (0 until 9 - level).map { index ->
        val key = "$baseKey$level$index"
        NTreeOption(
            label = treeLabel(level),
            key = key,
            children = largeTreeData(level - 1, key),
        )
    }
}

private fun withActionSlots(nodes: List<NTreeOption>): List<NTreeOption> = nodes.map { node ->
    node.copy(
        children = node.children?.let { withActionSlots(it) },
        prefix = {
            NButton(text = true, type = NButtonType.Primary, size = NSize.Small) {
                NText("Prefix")
            }
        },
        suffix = {
            NButton(text = true, type = NButtonType.Primary, size = NSize.Small) {
                NText("Suffix")
            }
        },
    )
}

private data class CustomFieldNode(
    val whateverLabel: String,
    val whateverKey: String,
    val whateverChildren: List<CustomFieldNode>? = null,
)

private fun customFieldNodes(level: Int = 4, baseKey: String = ""): List<CustomFieldNode> {
    if (level == 0) return emptyList()
    return (0 until 6 - level).map { index ->
        val key = "$baseKey$level$index"
        CustomFieldNode(
            whateverLabel = treeLabel(level),
            whateverKey = key,
            whateverChildren = customFieldNodes(level - 1, key),
        )
    }
}

private fun CustomFieldNode.toTreeOption(): NTreeOption = NTreeOption(
    label = whateverLabel,
    key = whateverKey,
    children = whateverChildren?.map { it.toTreeOption() },
)

private fun customFieldTreeData(): List<NTreeOption> =
    customFieldNodes().map { it.toTreeOption() }

private fun fileTreeData(
    expandedKeys: Set<String>,
    openColor: Color,
    closedColor: Color,
): List<NTreeOption> = listOf(
    NTreeOption(
        label = "Folder",
        key = "Folder",
        prefix = {
            NIcon(
                NIcons.MenuIcon,
                size = 16.dp,
                color = if ("Folder" in expandedKeys) openColor else closedColor,
            )
        },
        children = listOf(
            NTreeOption(
                label = "Empty",
                key = "Empty",
                disabled = true,
                prefix = { NIcon(NIcons.MenuIcon, size = 16.dp, color = closedColor) },
            ),
            NTreeOption(
                label = "MyFiles",
                key = "MyFiles",
                prefix = {
                    NIcon(
                        NIcons.MenuIcon,
                        size = 16.dp,
                        color = if ("MyFiles" in expandedKeys) openColor else closedColor,
                    )
                },
                children = listOf(
                    NTreeOption(
                        label = "template.txt",
                        key = "template.txt",
                        prefix = { NIcon(NIcons.File, size = 16.dp, color = closedColor) },
                    ),
                ),
            ),
        ),
    ),
)

private fun overrideTreeData(): List<NTreeOption> = listOf(
    NTreeOption(
        label = "Folder-1",
        key = "Folder-1",
        children = listOf(
            NTreeOption(label = "File-1-1", key = "File-1-1"),
            NTreeOption(
                label = "Folder-1-2",
                key = "Folder-1-2",
                children = listOf(
                    NTreeOption(label = "File-1-2-1", key = "File-1-2-1"),
                    NTreeOption(label = "File-1-2-2", key = "File-1-2-2"),
                ),
            ),
        ),
    ),
    NTreeOption(
        label = "Folder-2",
        key = "Folder-2",
        children = listOf(
            NTreeOption(label = "File-2-1", key = "File-2-1"),
            NTreeOption(label = "File-2-2", key = "File-2-2"),
        ),
    ),
)
