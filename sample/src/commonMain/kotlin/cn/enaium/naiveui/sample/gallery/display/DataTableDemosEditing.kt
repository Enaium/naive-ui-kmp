package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NDataTable
import cn.enaium.naiveui.components.NDataTableColumn
import cn.enaium.naiveui.components.NDataTableFilterOption
import cn.enaium.naiveui.components.NDataTablePagination
import cn.enaium.naiveui.components.NDataTableSortOrder
import cn.enaium.naiveui.components.NInput
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.components.NTableAlign
import cn.enaium.naiveui.components.NTableRow
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.nDataTableCsv
import cn.enaium.naiveui.components.nRow
import cn.enaium.naiveui.sample.gallery.CodePreview
import cn.enaium.naiveui.sample.gallery.DemoSection

@Composable
fun DtDemoSimpleEditable() {
    var rows by remember {
        mutableStateOf(
            listOf(
                nRow("key" to "0", "name" to "John Brown", "age" to "32", "address" to "New York No. 1 Lake Park"),
                nRow("key" to "1", "name" to "Jim Green", "age" to "42", "address" to "London No. 1 Lake Park"),
                nRow("key" to "2", "name" to "Joe Black", "age" to "32", "address" to "Sidney No. 1 Lake Park"),
            ),
        )
    }
    fun update(index: Int, key: String, value: String) {
        rows = rows.toMutableList().also { list ->
            list[index] = list[index].dtWith(key, value)
        }
    }
    DemoSection(
        title = "Simple editable",
        description = "Inputs stored in column render slots edit the underlying row state directly.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NDataTable(
                columns = listOf(
                    NDataTableColumn(
                        key = "name",
                        title = "Name",
                        render = { row, index ->
                            NInput(
                                value = row["name"]?.toString().orEmpty(),
                                onValueChange = { update(index, "name", it) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        },
                    ),
                    NDataTableColumn(
                        key = "age",
                        title = "Age",
                        width = 120.dp,
                        render = { row, index ->
                            NInput(
                                value = row["age"]?.toString().orEmpty(),
                                onValueChange = { update(index, "age", it) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        },
                    ),
                    NDataTableColumn(
                        key = "address",
                        title = "Address",
                        render = { row, index ->
                            NInput(
                                value = row["address"]?.toString().orEmpty(),
                                onValueChange = { update(index, "address", it) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        },
                    ),
                ),
                data = rows,
                pagination = NDataTablePagination(pageSize = 10),
            )
            CodePreview(
                rows.joinToString("\n") { row ->
                    "${row["name"]} | ${row["age"]} | ${row["address"]}"
                },
            )
        }
    }
}

@Composable
private fun DtEditableCell(
    cellKey: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    var editing by remember(cellKey) { mutableStateOf(false) }
    if (editing) {
        NInput(
            value = value,
            onValueChange = onValueChange,
            autofocus = true,
            onPressEnter = { editing = false },
            onBlur = { editing = false },
        )
    } else {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { editing = true },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NText(if (value.isEmpty()) " " else value)
        }
    }
}

@Composable
fun DtDemoSwitchableEditable() {
    var rows by remember {
        mutableStateOf(
            List(100) { index ->
                nRow(
                    "key" to index.toString(),
                    "name" to "John Brown $index",
                    "age" to index % 40,
                    "address" to "New York No. $index Lake Park",
                )
            },
        )
    }
    var page by remember { mutableStateOf(1) }

    fun update(key: String, field: String, value: Any?) {
        rows = rows.map { row ->
            if (row["key"] == key) row.dtWith(field, value) else row
        }
    }

    DemoSection(
        title = "Switchable editable",
        description = "Click a cell to edit it. Changes are written back by row key across paginated pages.",
    ) {
        NDataTable(
            columns = listOf(
                NDataTableColumn(
                    key = "name",
                    title = "Name",
                    width = 190.dp,
                    render = { row, _ ->
                        DtEditableCell(
                            cellKey = "${row["key"]}-name",
                            value = row["name"]?.toString().orEmpty(),
                            onValueChange = { update(row["key"].toString(), "name", it) },
                        )
                    },
                ),
                NDataTableColumn(
                    key = "age",
                    title = "Age",
                    width = 120.dp,
                    render = { row, _ ->
                        DtEditableCell(
                            cellKey = "${row["key"]}-age",
                            value = row["age"]?.toString().orEmpty(),
                            onValueChange = {
                                update(row["key"].toString(), "age", it.toIntOrNull() ?: 0)
                            },
                        )
                    },
                ),
                NDataTableColumn(
                    key = "address",
                    title = "Address",
                    render = { row, _ ->
                        DtEditableCell(
                            cellKey = "${row["key"]}-address",
                            value = row["address"]?.toString().orEmpty(),
                            onValueChange = { update(row["key"].toString(), "address", it) },
                        )
                    },
                ),
            ),
            data = rows,
            pagination = NDataTablePagination(page = page, pageSize = 10),
            onPageChange = { page = it },
        )
    }
}

@Suppress("DEPRECATION")
@Composable
fun DtDemoExportCsv() {
    val clipboard = LocalClipboardManager.current
    var sorters by remember { mutableStateOf<Map<String, NDataTableSortOrder>>(emptyMap()) }
    var filters by remember { mutableStateOf<Map<String, Set<String>>>(emptyMap()) }
    var displayed by remember { mutableStateOf<List<NTableRow>>(emptyList()) }
    var csv by remember { mutableStateOf("") }
    var info by remember { mutableStateOf("Generate a CSV preview from original or displayed data.") }
    val columns = listOf(
        NDataTableColumn(
            key = "name",
            title = "Name",
            sortable = true,
            render = { row, _ -> NText(row["name"].toString(), color = Color(0xFF2080F0)) },
        ),
        NDataTableColumn(
            key = "age",
            title = "Age",
            width = 100.dp,
            align = NTableAlign.Right,
            sortable = true,
            sortComparator = { a, b -> (a["age"] as Int) - (b["age"] as Int) },
        ),
        NDataTableColumn(
            key = "address",
            title = "Address",
            filterOptions = listOf(
                NDataTableFilterOption("London", "London"),
                NDataTableFilterOption("New York", "New York"),
            ),
        ),
    )
    fun buildCsv(rows: List<NTableRow>) {
        csv = nDataTableCsv(
            columns = columns,
            rows = rows,
            header = { column -> if (column.key == "age") "Age" else column.title },
            cell = { value, _, column ->
                if (column.key == "age") "$value years old" else value?.toString().orEmpty()
            },
        )
        info = "Generated CSV for ${rows.size} rows."
    }
    DemoSection(
        title = "Export CSV",
        description = "`nDataTableCsv` converts the original or currently displayed rows to CSV, " +
            "with custom header and cell formatting. The result can be copied to the clipboard.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NButton(size = NSize.Small, onClick = { buildCsv(dtPeople) }) {
                    NText("Export CSV (original data)")
                }
                NButton(size = NSize.Small, onClick = { buildCsv(displayed) }) {
                    NText("Export CSV (displayed data)")
                }
                NButton(
                    size = NSize.Small,
                    disabled = csv.isEmpty(),
                    onClick = {
                        clipboard.setText(AnnotatedString(csv))
                        info = "Copied ${csv.lines().size - 1} CSV rows to the clipboard."
                    },
                ) {
                    NText("Copy CSV")
                }
            }
            NText(info)
            NDataTable(
                columns = columns,
                data = dtPeople,
                sorters = sorters,
                onSorterChange = { sorters = it },
                filters = filters,
                onFilterChange = { filters = it },
                onDisplayedRowsChange = { displayed = it },
                bordered = false,
            )
            if (csv.isNotEmpty()) {
                CodePreview(csv)
            }
        }
    }
}
