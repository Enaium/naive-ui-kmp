package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NDataTable
import cn.enaium.naiveui.components.NDataTableColumn
import cn.enaium.naiveui.components.NDataTableFilterOption
import cn.enaium.naiveui.components.NDataTableFixed
import cn.enaium.naiveui.components.NDataTablePagination
import cn.enaium.naiveui.components.NDataTableSortOrder
import cn.enaium.naiveui.components.NTableAlign
import cn.enaium.naiveui.components.NTableSelectionMode
import cn.enaium.naiveui.components.nRow
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

private val songRows = listOf(
    nRow("key" to "1", "no" to 3, "title" to "Wonderwall", "length" to "4:18"),
    nRow("key" to "2", "no" to 4, "title" to "Don't Look Back in Anger", "length" to "4:48"),
    nRow("key" to "3", "no" to 12, "title" to "Champagne Supernova", "length" to "7:27"),
    nRow("key" to "4", "no" to 7, "title" to "Supersonic", "length" to "4:43"),
    nRow("key" to "5", "no" to 9, "title" to "Live Forever", "length" to "4:36"),
)

private val selectionRows = List(12) { index ->
    nRow(
        "key" to index.toString(),
        "name" to "Edward King $index",
        "age" to 30 + index % 8,
        "address" to "London, Park Lane no. $index",
    )
}

private val scoreRows = listOf(
    nRow(
        "key" to "0",
        "name" to "John Brown",
        "age" to 32,
        "address" to "New York No. 1 Lake Park",
        "chinese" to 98,
        "math" to 60,
        "english" to 70,
    ),
    nRow(
        "key" to "1",
        "name" to "Jim Green",
        "age" to 42,
        "address" to "London No. 1 Lake Park",
        "chinese" to 98,
        "math" to 66,
        "english" to 89,
    ),
    nRow(
        "key" to "2",
        "name" to "Joe Black",
        "age" to 32,
        "address" to "Sidney No. 1 Lake Park",
        "chinese" to 98,
        "math" to 66,
        "english" to 89,
    ),
    nRow(
        "key" to "3",
        "name" to "Jim Red",
        "age" to 32,
        "address" to "London No. 2 Lake Park",
        "chinese" to 88,
        "math" to 99,
        "english" to 89,
    ),
    nRow(
        "key" to "4",
        "name" to "Anna White",
        "age" to 27,
        "address" to "New York No. 2 Lake Park",
        "chinese" to 76,
        "math" to 88,
        "english" to 92,
    ),
)

private val fixedRows = List(10) { index ->
    nRow(
        "key" to index.toString(),
        "name" to "Edward King $index",
        "age" to 32,
        "email" to "edward$index@example.com",
        "notes" to "Row $index with a longer note for horizontal scrolling.",
        "address" to "London, Park Lane no. $index",
    )
}

private val tablePaginationRows = List(12) { index ->
    nRow(
        "key" to index.toString(),
        "name" to "Item ${index + 1}",
        "age" to 20 + index,
        "address" to "City ${index + 1}, Sample Street no. ${index + 1}",
    )
}

@Composable
fun DataTableDocs() {
    DocsPage(
        title = "Data Table",
        description = "A data-driven table with built-in sorting, filtering, selection, " +
            "pagination and pinned columns.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Columns describe their key and title. A `render` column can place " +
                "arbitrary content such as buttons inside a cell.",
            code = """
NDataTable(
    columns = listOf(
        NDataTableColumn("name", "Name"),
        NDataTableColumn("age", "Age", width = 80.dp, align = NTableAlign.Right),
    ),
    data = listOf(nRow("name" to "John Brown", "age" to 32)),
)
""".trimIndent(),
        ) {
            var played by remember { mutableStateOf<String?>(null) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NText(
                    played?.let { "Now playing: $it" } ?: "Press Play on a song.",
                )
                NDataTable(
                    columns = listOf(
                        NDataTableColumn(
                            key = "no",
                            title = "No",
                            width = 70.dp,
                            align = NTableAlign.Right,
                        ),
                        NDataTableColumn("title", "Title"),
                        NDataTableColumn(
                            key = "length",
                            title = "Length",
                            width = 100.dp,
                            align = NTableAlign.Right,
                        ),
                        NDataTableColumn(
                            key = "actions",
                            title = "Action",
                            width = 110.dp,
                            render = { row, _ ->
                                NButton(
                                    size = NSize.Small,
                                    tertiary = true,
                                    onClick = { played = row["title"]?.toString() },
                                ) {
                                    NText("Play")
                                }
                            },
                        ),
                    ),
                    data = songRows,
                    bordered = false,
                )
            }
        }

        DemoSection(
            title = "Selection",
            description = "Selection is enabled per table. The header checkbox shows an " +
                "indeterminate state when only some rows on the page are selected.",
        ) {
            var checked by remember { mutableStateOf<Set<String>>(emptySet()) }
            var singleChecked by remember { mutableStateOf<Set<String>>(emptySet()) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NText(
                    if (checked.isEmpty()) {
                        "Multiple selection: no rows selected."
                    } else {
                        "Multiple selection: ${checked.size} row(s) selected."
                    },
                )
                NDataTable(
                    columns = listOf(
                        NDataTableColumn("name", "Name"),
                        NDataTableColumn(
                            key = "age",
                            title = "Age",
                            width = 80.dp,
                            align = NTableAlign.Right,
                        ),
                        NDataTableColumn("address", "Address"),
                    ),
                    data = selectionRows,
                    selectionMode = NTableSelectionMode.Multiple,
                    checkedRowKeys = checked,
                    onCheckedRowKeysChange = { checked = it },
                    striped = true,
                    pagination = NDataTablePagination(pageSize = 5),
                )
                NText(
                    if (singleChecked.isEmpty()) {
                        "Single selection: no row selected."
                    } else {
                        "Single selection: ${singleChecked.first()} selected."
                    },
                )
                NDataTable(
                    columns = listOf(
                        NDataTableColumn("name", "Name"),
                        NDataTableColumn("address", "Address"),
                    ),
                    data = selectionRows.take(4),
                    selectionMode = NTableSelectionMode.Single,
                    checkedRowKeys = singleChecked,
                    onCheckedRowKeysChange = { singleChecked = it },
                    bordered = false,
                )
            }
        }

        DemoSection(
            title = "Sorting",
            description = "Set `sortable` on one or more columns. With `multipleSort`, each " +
                "clicked column is added to the sort chain in click order.",
        ) {
            var sorters by remember {
                mutableStateOf<Map<String, NDataTableSortOrder>>(emptyMap())
            }
            val label = if (sorters.isEmpty()) {
                "No sorting applied."
            } else {
                sorters.entries.joinToString(", ") { entry ->
                    "${entry.key}: ${entry.value.name.lowercase()}"
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NText("Sorting: $label")
                NDataTable(
                    columns = listOf(
                        NDataTableColumn("name", "Name", sortable = true),
                        NDataTableColumn(
                            key = "age",
                            title = "Age",
                            width = 90.dp,
                            align = NTableAlign.Right,
                            sortable = true,
                        ),
                        NDataTableColumn(
                            key = "chinese",
                            title = "Chinese",
                            width = 100.dp,
                            align = NTableAlign.Right,
                            sortable = true,
                        ),
                        NDataTableColumn(
                            key = "math",
                            title = "Math",
                            width = 90.dp,
                            align = NTableAlign.Right,
                            sortable = true,
                        ),
                        NDataTableColumn(
                            key = "english",
                            title = "English",
                            width = 100.dp,
                            align = NTableAlign.Right,
                            sortable = true,
                        ),
                        NDataTableColumn("address", "Address"),
                    ),
                    data = scoreRows,
                    sorters = sorters,
                    onSorterChange = { sorters = it },
                    multipleSort = true,
                )
            }
        }

        DemoSection(
            title = "Filter",
            description = "Columns with `filterOptions` show a dropdown in the header. " +
                "Selected options are matched against the cell text.",
        ) {
            var filters by remember { mutableStateOf<Map<String, Set<String>>>(emptyMap()) }
            val label = if (filters.isEmpty()) {
                "No filters applied."
            } else {
                filters.entries.joinToString(", ") { entry ->
                    "${entry.key}: ${entry.value.joinToString()}"
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NText("Filters: $label")
                NDataTable(
                    columns = listOf(
                        NDataTableColumn("name", "Name"),
                        NDataTableColumn(
                            key = "age",
                            title = "Age",
                            width = 90.dp,
                            align = NTableAlign.Right,
                        ),
                        NDataTableColumn(
                            key = "address",
                            title = "Address",
                            filterOptions = listOf(
                                NDataTableFilterOption("London", "London"),
                                NDataTableFilterOption("New York", "New York"),
                                NDataTableFilterOption("Sidney", "Sidney"),
                            ),
                        ),
                    ),
                    data = scoreRows,
                    filters = filters,
                    onFilterChange = { filters = it },
                    pagination = NDataTablePagination(pageSize = 5),
                )
            }
        }

        DemoSection(
            title = "Pagination",
            description = "Paginate large data sets and optionally let users change the page size.",
        ) {
            var page by remember { mutableStateOf(1) }
            var pageSize by remember { mutableStateOf(3) }
            NDataTable(
                columns = listOf(
                    NDataTableColumn("name", "Name"),
                    NDataTableColumn(
                        key = "age",
                        title = "Age",
                        width = 80.dp,
                        align = NTableAlign.Right,
                    ),
                    NDataTableColumn("address", "Address"),
                ),
                data = tablePaginationRows,
                pagination = NDataTablePagination(
                    page = page,
                    pageSize = pageSize,
                    pageSizes = listOf(3, 5, 10),
                    showSizePicker = true,
                    showQuickJumper = true,
                ),
                onPageChange = { page = it },
                onPageSizeChange = {
                    pageSize = it
                    page = 1
                },
            )
        }

        DemoSection(
            title = "Fixed columns",
            description = "Pinned columns stay visible while the middle section scrolls " +
                "horizontally. A fixed table also keeps its header visible with `maxHeight`.",
        ) {
            NDataTable(
                columns = listOf(
                    NDataTableColumn("name", "Name", width = 160.dp, fixed = NDataTableFixed.Left),
                    NDataTableColumn(
                        key = "age",
                        title = "Age",
                        width = 100.dp,
                        fixed = NDataTableFixed.Left,
                        align = NTableAlign.Right,
                    ),
                    NDataTableColumn("email", "Email", width = 220.dp),
                    NDataTableColumn("notes", "Notes", width = 260.dp),
                    NDataTableColumn(
                        key = "address",
                        title = "Address",
                        width = 220.dp,
                        fixed = NDataTableFixed.Right,
                    ),
                ),
                data = fixedRows,
                scrollX = 900.dp,
                maxHeight = 220.dp,
                pagination = NDataTablePagination(pageSize = 10),
            )
        }

        DemoSection(
            title = "Loading",
            description = "The loading overlay covers the table while data is being fetched.",
        ) {
            var loading by remember { mutableStateOf(true) }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NButton(
                        size = NSize.Small,
                        onClick = { loading = !loading },
                    ) {
                        NText(if (loading) "Stop loading" else "Start loading")
                    }
                    NText(if (loading) "Loading..." else "Loaded.")
                }
                NDataTable(
                    columns = listOf(
                        NDataTableColumn("name", "Name"),
                        NDataTableColumn("age", "Age", width = 80.dp),
                        NDataTableColumn("address", "Address"),
                    ),
                    data = selectionRows.take(3),
                    loading = loading,
                    pagination = NDataTablePagination(pageSize = 5),
                )
            }
        }

        DemoSection(
            title = "Empty",
            description = "An empty data list shows the empty placeholder below the header.",
        ) {
            NDataTable(
                columns = listOf(
                    NDataTableColumn("name", "Name"),
                    NDataTableColumn("age", "Age", width = 80.dp),
                    NDataTableColumn("address", "Address"),
                ),
                data = emptyList(),
                emptyDescription = "No matching records",
            )
        }
    }
}
