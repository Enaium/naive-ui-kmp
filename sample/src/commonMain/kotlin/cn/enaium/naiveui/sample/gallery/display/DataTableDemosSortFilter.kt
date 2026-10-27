package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NDataTable
import cn.enaium.naiveui.components.NDataTableColumn
import cn.enaium.naiveui.components.NDataTableFilterOption
import cn.enaium.naiveui.components.NDataTablePagination
import cn.enaium.naiveui.components.NDataTablePaginationBehavior
import cn.enaium.naiveui.components.NDataTableSortOrder
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.components.NTableAlign
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.nRow
import cn.enaium.naiveui.sample.gallery.DemoSection
import kotlinx.coroutines.delay

private fun dtSortMap(vararg pairs: Pair<String, NDataTableSortOrder>): Map<String, NDataTableSortOrder> =
    pairs.toMap()

@Composable
fun DtDemoFilterAndSorter() {
    var sorters by remember { mutableStateOf(dtSortMap("name" to NDataTableSortOrder.Ascend)) }
    var filters by remember {
        mutableStateOf<Map<String, Set<String>>>(
            mapOf("address" to setOf("London", "New York")),
        )
    }
    DemoSection(
        title = "Uncontrolled filter and sorter",
        description = "Column defaults and the header controls work together. External buttons change the same controlled state.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NButton(size = NSize.Small, onClick = { sorters = dtSortMap("name" to NDataTableSortOrder.Ascend) }) {
                    NText("Sort By Name (Ascend)")
                }
                NButton(size = NSize.Small, onClick = { filters = mapOf("address" to setOf("London")) }) {
                    NText("Filter Address (London)")
                }
                NButton(size = NSize.Small, onClick = { filters = emptyMap() }) {
                    NText("Clear Filters")
                }
                NButton(size = NSize.Small, onClick = { sorters = emptyMap() }) {
                    NText("Clear Sorter")
                }
            }
            NDataTable(
                columns = listOf(
                    NDataTableColumn("name", "Name", sortable = true),
                    NDataTableColumn(
                        key = "age",
                        title = "Age",
                        sortable = true,
                        sortComparator = { a, b -> (a["age"] as Int) - (b["age"] as Int) },
                    ),
                    NDataTableColumn(
                        key = "address",
                        title = "Address",
                        sortable = true,
                        filterOptions = listOf(
                            NDataTableFilterOption("London", "London"),
                            NDataTableFilterOption("New York", "New York"),
                        ),
                    ),
                ),
                data = dtPeople,
                sorters = sorters,
                onSorterChange = { sorters = it },
                filters = filters,
                onFilterChange = { filters = it },
                pagination = NDataTablePagination(pageSize = 5),
            )
        }
    }
}

@Composable
fun DtDemoPaginationBehavior() {
    val rows = remember {
        List(9) { index ->
            nRow(
                "key" to "pb$index",
                "name" to "Person $index",
                "age" to 20 + index,
                "address" to if (index % 3 == 2) {
                    "New York No. ${index + 1} Lake Park"
                } else {
                    "London No. ${index + 1} Lake Park"
                },
            )
        }
    }
    var behavior by remember { mutableStateOf(NDataTablePaginationBehavior.First) }
    var page by remember { mutableStateOf(2) }
    var filters by remember { mutableStateOf<Map<String, Set<String>>>(emptyMap()) }
    DemoSection(
        title = "Filter page state",
        description = "`paginationBehaviorOnFilter` returns to page 1 or keeps the current page when a filter changes.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NButton(size = NSize.Small, onClick = {
                    behavior = NDataTablePaginationBehavior.First
                    page = 2
                }) {
                    NText("First on filter")
                }
                NButton(size = NSize.Small, onClick = {
                    behavior = NDataTablePaginationBehavior.Current
                    page = 2
                }) {
                    NText("Keep page on filter")
                }
                NText("page = $page")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NButton(size = NSize.Small, onClick = {
                    filters = mapOf("address" to setOf("London"))
                }) {
                    NText("Filter address (London)")
                }
                NButton(size = NSize.Small, onClick = { filters = emptyMap() }) {
                    NText("Clear filter")
                }
            }
            NDataTable(
                columns = listOf(
                    NDataTableColumn("name", "Name"),
                    NDataTableColumn("age", "Age"),
                    NDataTableColumn(
                        key = "address",
                        title = "Address",
                        filterOptions = listOf(
                            NDataTableFilterOption("London", "London"),
                            NDataTableFilterOption("New York", "New York"),
                        ),
                    ),
                ),
                data = rows,
                filters = filters,
                onFilterChange = { filters = it },
                pagination = NDataTablePagination(page = page, pageSize = 3),
                onPageChange = { page = it },
                paginationBehaviorOnFilter = behavior,
            )
        }
    }
}

@Composable
fun DtDemoMultipleSorter() {
    var sorters by remember { mutableStateOf<Map<String, NDataTableSortOrder>>(emptyMap()) }
    val label = if (sorters.isEmpty()) {
        "No sorting applied."
    } else {
        sorters.entries.joinToString(", ") { "${it.key}: ${it.value.name.lowercase()}" }
    }
    DemoSection(
        title = "Multiple column sorting",
        description = "`multipleSort` keeps a chain of sorted columns in click order.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NButton(size = NSize.Small, onClick = {
                    sorters = dtSortMap(
                        "chinese" to NDataTableSortOrder.Ascend,
                        "math" to NDataTableSortOrder.Ascend,
                    )
                }) {
                    NText("Sort Chinese + Math")
                }
                NButton(size = NSize.Small, onClick = { sorters = emptyMap() }) {
                    NText("Clear sorter")
                }
                NText(label)
            }
            NDataTable(
                columns = listOf(
                    NDataTableColumn("name", "Name"),
                    NDataTableColumn(
                        key = "age",
                        title = "Age",
                        sortable = true,
                        sortComparator = { a, b -> (a["age"] as Int) - (b["age"] as Int) },
                    ),
                    NDataTableColumn("chinese", "Chinese Score", sortable = true),
                    NDataTableColumn("math", "Math Score", sortable = true),
                    NDataTableColumn("english", "English Score", sortable = true),
                    NDataTableColumn(
                        key = "address",
                        title = "Address",
                        filterOptions = listOf(
                            NDataTableFilterOption("London", "London"),
                            NDataTableFilterOption("New York", "New York"),
                        ),
                    ),
                ),
                data = dtScores,
                sorters = sorters,
                onSorterChange = { sorters = it },
                multipleSort = true,
                pagination = NDataTablePagination(pageSize = 5),
            )
        }
    }
}

@Composable
fun DtDemoCustomSorter() {
    DemoSection(
        title = "Custom sorter",
        description = "`nextSortOrder` removes the unsorted state from the Age column while Score keeps the default three-state cycle.",
    ) {
        NDataTable(
            columns = listOf(
                NDataTableColumn("name", "Name"),
                NDataTableColumn(
                    key = "age",
                    title = "Age (only asc/desc)",
                    sortable = true,
                    defaultSortOrder = NDataTableSortOrder.Ascend,
                    sortComparator = { a, b -> (a["age"] as Int) - (b["age"] as Int) },
                    nextSortOrder = { order ->
                        if (order == NDataTableSortOrder.Ascend) {
                            NDataTableSortOrder.Descend
                        } else {
                            NDataTableSortOrder.Ascend
                        }
                    },
                ),
                NDataTableColumn(
                    key = "score",
                    title = "Score (default three-state)",
                    sortable = true,
                    sortComparator = { a, b -> (a["score"] as Int) - (b["score"] as Int) },
                ),
                NDataTableColumn("address", "Address"),
            ),
            data = dtScores,
            pagination = NDataTablePagination(pageSize = 10),
        )
    }
}

@Composable
fun DtDemoControlledPage() {
    var page by remember { mutableStateOf(2) }
    var pageSize by remember { mutableStateOf(5) }
    DemoSection(
        title = "Controlled pagination",
        description = "The parent owns `page` and `pageSize`; the table reports changes through callbacks.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NText("page = $page, pageSize = $pageSize")
            NDataTable(
                columns = listOf(
                    NDataTableColumn("name", "Name"),
                    NDataTableColumn("age", "Age"),
                    NDataTableColumn("address", "Address"),
                ),
                data = dtPeople46,
                pagination = NDataTablePagination(
                    page = page,
                    pageSize = pageSize,
                    pageSizes = listOf(3, 5, 7),
                    showSizePicker = true,
                ),
                onPageChange = { page = it },
                onPageSizeChange = {
                    pageSize = it
                    page = 1
                },
            )
        }
    }
}

@Composable
fun DtDemoControlledFilter() {
    var filters by remember { mutableStateOf<Map<String, Set<String>>>(emptyMap()) }
    DemoSection(
        title = "Controlled filter",
        description = "Single-value filters can be driven by buttons or the header dropdown.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NButton(size = NSize.Small, onClick = { filters = mapOf("address" to setOf("London")) }) {
                    NText("Filter Address(London)")
                }
                NButton(size = NSize.Small, onClick = { filters = emptyMap() }) {
                    NText("Clear Address Filters")
                }
            }
            NDataTable(
                columns = listOf(
                    NDataTableColumn(
                        key = "name",
                        title = "Name",
                        sortable = true,
                        sortComparator = { a, b ->
                            (a["name"] as String).length - (b["name"] as String).length
                        },
                    ),
                    NDataTableColumn(
                        key = "age",
                        title = "Age",
                        sortable = true,
                        sortComparator = { a, b -> (a["age"] as Int) - (b["age"] as Int) },
                    ),
                    NDataTableColumn(
                        key = "address",
                        title = "Address",
                        sortable = true,
                        filterMultiple = false,
                        filterOptions = listOf(
                            NDataTableFilterOption("London", "London"),
                            NDataTableFilterOption("New York", "New York"),
                        ),
                    ),
                ),
                data = dtPeople,
                filters = filters,
                onFilterChange = { filters = it },
                pagination = NDataTablePagination(pageSize = 5),
            )
        }
    }
}

@Composable
fun DtDemoControlledSorter() {
    var sorters by remember { mutableStateOf<Map<String, NDataTableSortOrder>>(emptyMap()) }
    DemoSection(
        title = "Controlled sorter",
        description = "Only one column is sorted at a time. Buttons and header clicks update the same state.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NButton(size = NSize.Small, onClick = {
                    sorters = dtSortMap("name" to NDataTableSortOrder.Ascend)
                }) {
                    NText("Sort By Name (Ascend)")
                }
                NButton(size = NSize.Small, onClick = {
                    sorters = dtSortMap("name" to NDataTableSortOrder.Descend)
                }) {
                    NText("Sort By Name (Descend)")
                }
                NButton(size = NSize.Small, onClick = { sorters = emptyMap() }) {
                    NText("Clear Sorter")
                }
            }
            NDataTable(
                columns = listOf(
                    NDataTableColumn("name", "Name", sortable = true),
                    NDataTableColumn(
                        key = "age",
                        title = "Age",
                        sortable = true,
                        sortComparator = { a, b -> (a["age"] as Int) - (b["age"] as Int) },
                    ),
                    NDataTableColumn(
                        key = "address",
                        title = "Address",
                        defaultFilterValues = listOf("London", "New York"),
                        filterOptions = listOf(
                            NDataTableFilterOption("London", "London"),
                            NDataTableFilterOption("New York", "New York"),
                        ),
                    ),
                ),
                data = dtPeople,
                sorters = sorters,
                onSorterChange = { sorters = it },
                pagination = NDataTablePagination(pageSize = 5),
            )
        }
    }
}

@Composable
fun DtDemoControlledMultipleSorter() {
    var sorters by remember { mutableStateOf<Map<String, NDataTableSortOrder>>(emptyMap()) }
    DemoSection(
        title = "Controlled multi-column sorting",
        description = "Multiple sort states can be set externally; the table renders and applies all of them.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NButton(size = NSize.Small, onClick = {
                    sorters = dtSortMap(
                        "english" to NDataTableSortOrder.Descend,
                        "math" to NDataTableSortOrder.Ascend,
                    )
                }) {
                    NText("English desc, Math asc")
                }
                NButton(size = NSize.Small, onClick = { sorters = emptyMap() }) {
                    NText("Clear")
                }
            }
            NDataTable(
                columns = listOf(
                    NDataTableColumn("name", "Name"),
                    NDataTableColumn(
                        key = "age",
                        title = "Age",
                        sortable = true,
                        sortComparator = { a, b -> (a["age"] as Int) - (b["age"] as Int) },
                    ),
                    NDataTableColumn("chinese", "Chinese Score", sortable = true),
                    NDataTableColumn("math", "Math Score", sortable = true),
                    NDataTableColumn("english", "English Score", sortable = true),
                ),
                data = dtScores,
                sorters = sorters,
                onSorterChange = { sorters = it },
                multipleSort = true,
                pagination = NDataTablePagination(pageSize = 5),
            )
        }
    }
}

@Composable
fun DtDemoAjaxUsage() {
    val allRows = remember {
        List(120) { index ->
            nRow(
                "key" to index.toString(),
                "column1" to index,
                "column2" to (index % 2) + 1,
                "column3" to "a$index",
            )
        }
    }
    var page by remember { mutableStateOf(1) }
    var pageSize by remember { mutableStateOf(10) }
    var sorters by remember { mutableStateOf<Map<String, NDataTableSortOrder>>(emptyMap()) }
    var filters by remember { mutableStateOf<Map<String, Set<String>>>(emptyMap()) }
    var loading by remember { mutableStateOf(true) }
    var rows by remember { mutableStateOf<List<cn.enaium.naiveui.components.NTableRow>>(emptyList()) }
    var total by remember { mutableStateOf(0) }

    LaunchedEffect(page, pageSize, sorters, filters) {
        loading = true
        delay(600)
        val filtered = allRows.filter { row ->
            val values = filters["column2"].orEmpty()
            values.isEmpty() || row["column2"].toString() in values
        }
        val order = sorters["column1"]
        val sorted = when (order) {
            NDataTableSortOrder.Ascend -> filtered.sortedBy { it["column1"] as Int }
            NDataTableSortOrder.Descend -> filtered.sortedByDescending { it["column1"] as Int }
            null -> filtered
        }
        total = sorted.size
        rows = sorted.drop((page - 1) * pageSize).take(pageSize)
        loading = false
    }

    DemoSection(
        title = "Async",
        description = "`remote` skips local filtering and paging. The parent refetches when page, sorter or filters change.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NText("Total is $total. Loading = $loading")
            NDataTable(
                columns = listOf(
                    NDataTableColumn("column1", "column1", sortable = true),
                    NDataTableColumn(
                        key = "column2",
                        title = "column2",
                        filterOptions = listOf(
                            NDataTableFilterOption("Value1", "1"),
                            NDataTableFilterOption("Value2", "2"),
                        ),
                    ),
                    NDataTableColumn("column3", "Column3"),
                ),
                data = rows,
                remote = true,
                loading = loading,
                sorters = sorters,
                onSorterChange = { sorters = it },
                filters = filters,
                onFilterChange = { filters = it },
                pagination = NDataTablePagination(
                    page = page,
                    pageSize = pageSize,
                    itemCount = total,
                    pageSizes = listOf(10, 20, 50),
                    showSizePicker = true,
                ),
                onPageChange = { page = it },
                onPageSizeChange = {
                    pageSize = it
                    page = 1
                },
            )
        }
    }
}

@Composable
fun DtDemoCustomFilterMenu() {
    DemoSection(
        title = "Custom filter menu",
        description = "Custom filter icons, filter menus, sorter icons and expand icons are all render slots.",
    ) {
        NDataTable(
            columns = listOf(
                NDataTableColumn(
                    key = "Left",
                    title = "Left",
                    sortable = true,
                    sorterIcon = { order ->
                        NText(
                            when (order) {
                                NDataTableSortOrder.Ascend -> "↓"
                                NDataTableSortOrder.Descend -> "↑"
                                null -> "↕"
                            },
                        )
                    },
                ),
                NDataTableColumn(
                    key = "Right",
                    title = "Right",
                    filterOptions = listOf(
                        NDataTableFilterOption("1", "1"),
                        NDataTableFilterOption("2", "2"),
                    ),
                    filterIcon = { active -> NIcon(NIcons.Search, size = 14.dp) },
                    filterMenu = { _, onValuesChange, dismiss ->
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            NButton(size = NSize.Small, onClick = { onValuesChange(setOf("1")) }) {
                                NText("Filter by 1")
                            }
                            NButton(size = NSize.Small, onClick = { onValuesChange(setOf("2")) }) {
                                NText("Filter by 2")
                            }
                            NButton(size = NSize.Small, text = true, onClick = {
                                onValuesChange(emptySet())
                                dismiss()
                            }) {
                                NText("Clear")
                            }
                        }
                    },
                ),
            ),
            data = listOf(
                nRow("key" to "1", "Left" to "1", "Right" to "1"),
                nRow("key" to "2", "Left" to "2", "Right" to "2"),
            ),
            renderExpand = { _, _ -> NText("Expand content") },
            renderExpandIcon = { _, _ -> NIcon(NIcons.Person, size = 14.dp) },
        )
    }
}
