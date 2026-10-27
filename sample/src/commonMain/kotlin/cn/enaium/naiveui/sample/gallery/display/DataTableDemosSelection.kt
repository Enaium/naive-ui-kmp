package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NDataTable
import cn.enaium.naiveui.components.NDataTableColumn
import cn.enaium.naiveui.components.NDataTablePagination
import cn.enaium.naiveui.components.NDataTableSelectionOption
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.components.NTableAlign
import cn.enaium.naiveui.components.NTableSelectionMode
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.nRow
import cn.enaium.naiveui.sample.gallery.DemoSection
import kotlinx.coroutines.delay

@Composable
fun DtDemoSelect() {
    var checked by remember { mutableStateOf<Set<String>>(emptySet()) }
    DemoSection(
        title = "Selection",
        description = "Rows are selectable when `selectionMode` is set. The header checkbox shows an indeterminate state.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NText(
                if (checked.isEmpty()) {
                    "You have selected 0 rows."
                } else {
                    "You have selected ${checked.size} row${if (checked.size == 1) "" else "s"}."
                },
            )
            NDataTable(
                columns = listOf(
                    NDataTableColumn("name", "Name"),
                    NDataTableColumn("age", "Age", width = 80.dp, align = NTableAlign.Right),
                    NDataTableColumn("address", "Address"),
                ),
                data = dtPeople46,
                selectionMode = NTableSelectionMode.Multiple,
                checkedRowKeys = checked,
                onCheckedRowKeysChange = { checked = it },
                pagination = NDataTablePagination(pageSize = 5),
            )
        }
    }
}

@Composable
fun DtDemoSelectSingle() {
    var checked by remember { mutableStateOf<Set<String>>(setOf("4")) }
    DemoSection(
        title = "Single selection",
        description = "Single selection behaves like a radio group. Rows can be disabled through `selectable`.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NText(
                if (checked.isEmpty()) "Nothing selected." else "Selected: ${checked.first()}",
            )
            NDataTable(
                columns = listOf(
                    NDataTableColumn("name", "Name"),
                    NDataTableColumn("age", "Age"),
                    NDataTableColumn("address", "Address"),
                ),
                data = dtPeople46,
                selectionMode = NTableSelectionMode.Single,
                checkedRowKeys = checked,
                onCheckedRowKeysChange = { checked = it },
                selectable = { row -> row["name"] != "Edward King 3" },
                pagination = NDataTablePagination(pageSize = 6),
            )
        }
    }
}

@Composable
fun DtDemoCustomSelect() {
    var checked by remember { mutableStateOf<Set<String>>(emptySet()) }
    DemoSection(
        title = "Custom selection menu",
        description = "`selectionOptions` adds custom actions to the header checkbox dropdown.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NText("Selected rows: ${checked.size}")
            NDataTable(
                columns = listOf(
                    NDataTableColumn("name", "Name"),
                    NDataTableColumn("age", "Age"),
                    NDataTableColumn("address", "Address"),
                ),
                data = dtPeople46,
                selectionMode = NTableSelectionMode.Multiple,
                checkedRowKeys = checked,
                onCheckedRowKeysChange = { checked = it },
                selectable = { row -> row["name"] != "Edward King 3" },
                selectionOptions = listOf(
                    NDataTableSelectionOption("Select all") { pageRows, current ->
                        current + pageRows
                            .filter { it["name"] != "Edward King 3" }
                            .map { it["key"].toString() }
                    },
                    NDataTableSelectionOption("Select none") { pageRows, current ->
                        current - pageRows.map { it["key"].toString() }.toSet()
                    },
                    NDataTableSelectionOption("Select first 2 rows") { pageRows, _ ->
                        pageRows
                            .filter { it["name"] != "Edward King 3" }
                            .take(2)
                            .map { it["key"].toString() }
                            .toSet()
                    },
                ),
                pagination = NDataTablePagination(pageSize = 6),
            )
        }
    }
}

@Composable
fun DtDemoExpand() {
    var checked by remember { mutableStateOf<Set<String>>(emptySet()) }
    var message by remember { mutableStateOf("Expanded rows are not counted by render's index.") }
    DemoSection(
        title = "Expand rows",
        description = "`renderExpand` + `expandable` add expandable detail rows. `defaultExpandAll` expands them initially.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NText(message)
            NDataTable(
                columns = listOf(
                    NDataTableColumn(
                        key = "index",
                        title = "#",
                        width = 60.dp,
                        render = { _, index -> NText("${index + 1}") },
                    ),
                    NDataTableColumn("name", "Name"),
                    NDataTableColumn("age", "Age"),
                    NDataTableColumn("address", "Address"),
                    NDataTableColumn(
                        key = "tags",
                        title = "Tags",
                        render = { row, _ ->
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                (row["tags"] as? List<*>)?.forEach { tag ->
                                    NTag(
                                        text = tag?.toString(),
                                        type = cn.enaium.naiveui.theme.NStatus.Info,
                                        size = NSize.Small,
                                        bordered = false,
                                    )
                                }
                            }
                        },
                    ),
                    NDataTableColumn(
                        key = "actions",
                        title = "Action",
                        render = { row, _ ->
                            NButton(size = NSize.Small, onClick = {
                                message = "send mail to ${row["name"]}"
                            }) {
                                NText("Send Email")
                            }
                        },
                    ),
                ),
                data = dtTagPeople,
                selectionMode = NTableSelectionMode.Multiple,
                checkedRowKeys = checked,
                onCheckedRowKeysChange = { checked = it },
                expandable = { row -> row["name"] != "Jim Green" },
                renderExpand = { row, _ -> NText("${row["name"]} is a good guy.") },
                defaultExpandAll = true,
                pagination = NDataTablePagination(pageSize = 10),
            )
        }
    }
}

@Composable
fun DtDemoAsyncExpand() {
    var checked by remember { mutableStateOf<Set<String>>(emptySet()) }
    DemoSection(
        title = "Async expand tree data",
        description = "`onLoadChildren` loads child rows when a node is expanded and shows a spinner while loading.",
    ) {
        NDataTable(
            columns = listOf(
                NDataTableColumn("example", "Example"),
            ),
            data = listOf(
                nRow("key" to "p1", "example" to "p1", "isLeaf" to false),
                nRow("key" to "p2", "example" to "p2", "isLeaf" to false),
                nRow("key" to "p3", "example" to "p3", "isLeaf" to false),
            ),
            selectionMode = NTableSelectionMode.Multiple,
            checkedRowKeys = checked,
            onCheckedRowKeysChange = { checked = it },
            onLoadChildren = { row ->
                delay(700)
                listOf(
                    nRow(
                        "key" to "${row["key"]}-1",
                        "example" to "${row["key"]}-1",
                        "isLeaf" to true,
                    ),
                )
            },
        )
    }
}

@Composable
fun DtDemoTree() {
    var checked by remember { mutableStateOf<Set<String>>(emptySet()) }
    var expanded by remember { mutableStateOf(setOf("07", "08")) }
    DemoSection(
        title = "Tree data",
        description = "Rows with a `children` list become tree rows. Expand them individually or with the buttons below.",
    ) {
        val treeData = listOf(
            nRow(
                "key" to "07",
                "index" to "07",
                "name" to "07akioni",
                "children" to listOf(
                    nRow(
                        "key" to "08",
                        "index" to "08",
                        "name" to "08akioni",
                        "children" to listOf(
                            nRow("key" to "09", "index" to "09", "name" to "09akioni"),
                            nRow("key" to "10", "index" to "10", "name" to "10akioni"),
                        ),
                    ),
                ),
            ),
            nRow("key" to "11", "index" to "11", "name" to "11akioni"),
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NButton(size = NSize.Small, onClick = { expanded = setOf("07", "08") }) {
                    NText("Expand all")
                }
                NButton(size = NSize.Small, onClick = { expanded = emptySet() }) {
                    NText("Collapse all")
                }
                NText("expanded = ${expanded.size} rows")
            }
            NDataTable(
                columns = listOf(
                    NDataTableColumn("name", "name"),
                    NDataTableColumn("index", "index"),
                ),
                data = treeData,
                selectionMode = NTableSelectionMode.Multiple,
                checkedRowKeys = checked,
                onCheckedRowKeysChange = { checked = it },
                expandedRowKeys = expanded,
                onExpandedRowKeysChange = { expanded = it },
            )
        }
    }
}
