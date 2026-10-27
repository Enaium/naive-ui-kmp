package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NTree
import cn.enaium.naiveui.components.NTreeOption
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import kotlinx.coroutines.delay

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
            description = "Disabled nodes cannot be selected, checked or expanded. Disabling " +
                "the tree itself has the same effect for every node.",
            code = """
                NTree(data = data, blockLine = true, defaultExpandedKeys = listOf("0", "1"))
            """.trimIndent(),
        ) {
            NTree(
                data = remember { disabledTreeData() },
                blockLine = true,
                defaultExpandedKeys = listOf("0", "1"),
            )
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
            NTreeOption(label = "1-1", key = "1-1"),
        ),
    ),
)
