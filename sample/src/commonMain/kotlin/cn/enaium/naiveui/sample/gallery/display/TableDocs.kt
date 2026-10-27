package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NTable
import cn.enaium.naiveui.components.NTableAlign
import cn.enaium.naiveui.components.NTableColumn
import cn.enaium.naiveui.components.NTablePagination
import cn.enaium.naiveui.components.NTableSelectionMode
import cn.enaium.naiveui.components.nRow
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus

private val wordRows = listOf(
    nRow(
        "word" to "Abandon",
        "meaning" to "放弃",
        "example" to "He abandoned the plan.",
        "status" to "Learned",
    ),
    nRow(
        "word" to "Abnormal",
        "meaning" to "反常的",
        "example" to "The result was abnormal.",
        "status" to "Review",
    ),
    nRow(
        "word" to "Abolish",
        "meaning" to "彻底废除",
        "example" to "The law was abolished.",
        "status" to "Learned",
    ),
    nRow(
        "word" to "Absorb",
        "meaning" to "吸收",
        "example" to "Plants absorb sunlight.",
        "status" to "New",
    ),
)

private val peopleRows = listOf(
    nRow("key" to "1", "name" to "John Brown", "age" to 32, "address" to "New York No. 1 Lake Park"),
    nRow("key" to "2", "name" to "Jim Green", "age" to 42, "address" to "London No. 1 Lake Park"),
    nRow("key" to "3", "name" to "Joe Black", "age" to 32, "address" to "Sidney No. 1 Lake Park"),
    nRow("key" to "4", "name" to "Jim Red", "age" to 28, "address" to "London No. 2 Lake Park"),
    nRow("key" to "5", "name" to "Anna White", "age" to 36, "address" to "New York No. 2 Lake Park"),
    nRow("key" to "6", "name" to "Peter Black", "age" to 45, "address" to "Sidney No. 2 Lake Park"),
)

private val pagedRows = List(11) { index ->
    nRow(
        "key" to index.toString(),
        "name" to "Edward King $index",
        "age" to 24 + index % 12,
        "address" to "London, Park Lane no. $index",
    )
}

@Composable
fun TableDocs() {
    DocsPage(
        title = "Table",
        description = "A compact table for presenting structured data. It supports " +
            "selection, sorting, pagination and summary rows.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Columns are declared with a key and a title, and every row is a map of values.",
            code = """
NTable(
    columns = listOf(
        NTableColumn("name", "Name"),
        NTableColumn("age", "Age"),
    ),
    data = listOf(
        nRow("name" to "John Brown", "age" to 32),
    ),
)
""".trimIndent(),
        ) {
            val columns = listOf(
                NTableColumn("word", "Word"),
                NTableColumn("meaning", "Meaning"),
                NTableColumn("example", "Example"),
                NTableColumn(
                    key = "status",
                    title = "Status",
                    render = { value ->
                        NTag(
                            text = value?.toString().orEmpty(),
                            type = when (value) {
                                "Learned" -> NStatus.Success
                                "Review" -> NStatus.Warning
                                else -> NStatus.Info
                            },
                            size = NSize.Small,
                            bordered = value != "Learned",
                        )
                    },
                ),
            )
            NTable(
                columns = columns,
                data = wordRows,
                bordered = false,
                singleLine = false,
            )
        }

        DemoSection(
            title = "Bordered",
            description = "Set `bordered` and `singleLine` to control the outer border and column dividers.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val columns = listOf(
                    NTableColumn("word", "Word", width = 140.dp),
                    NTableColumn("meaning", "Meaning"),
                    NTableColumn("example", "Example"),
                )
                NTable(
                    columns = columns,
                    data = wordRows.take(2),
                    bordered = true,
                    singleLine = false,
                )
                NTable(
                    columns = columns,
                    data = wordRows.take(2),
                    bordered = false,
                    singleLine = true,
                )
            }
        }

        DemoSection(
            title = "Striped",
            description = "Alternating row backgrounds make wide tables easier to scan.",
        ) {
            NTable(
                columns = listOf(
                    NTableColumn("name", "Name"),
                    NTableColumn("age", "Age", width = 90.dp, align = NTableAlign.Right),
                    NTableColumn("address", "Address"),
                ),
                data = peopleRows,
                striped = true,
            )
        }

        DemoSection(
            title = "Size",
            description = "Tables inherit the provider size but can also set it directly.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val columns = listOf(
                    NTableColumn("name", "Name"),
                    NTableColumn("age", "Age"),
                    NTableColumn("address", "Address"),
                )
                NTable(columns, peopleRows.take(2), size = NSize.Small, bordered = false)
                NTable(columns, peopleRows.take(2), size = NSize.Large, bordered = false)
            }
        }

        DemoSection(
            title = "Selection",
            description = "Use `selectionMode` and `checkedRowKeys` to control selected rows. " +
                "The header checkbox selects every row on the current page.",
            code = """
var checked by remember { mutableStateOf(emptySet<String>()) }
NTable(
    columns = columns,
    data = data,
    selectionMode = NTableSelectionMode.Multiple,
    checkedRowKeys = checked,
    onCheckedRowKeysChange = { checked = it },
)
""".trimIndent(),
        ) {
            var checked by remember { mutableStateOf<Set<String>>(emptySet()) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NText(
                    if (checked.isEmpty()) {
                        "You have not selected any rows."
                    } else {
                        "You have selected ${checked.size} row${if (checked.size == 1) "" else "s"}."
                    },
                )
                NTable(
                    columns = listOf(
                        NTableColumn("name", "Name"),
                        NTableColumn("age", "Age", width = 80.dp, align = NTableAlign.Right),
                        NTableColumn("address", "Address"),
                    ),
                    data = peopleRows,
                    selectionMode = NTableSelectionMode.Multiple,
                    checkedRowKeys = checked,
                    onCheckedRowKeysChange = { checked = it },
                    striped = true,
                )
            }
        }

        DemoSection(
            title = "Pagination",
            description = "Passing `pagination` adds a paginator below the table. Page size and page " +
                "switching are handled by the table.",
        ) {
            NTable(
                columns = listOf(
                    NTableColumn("name", "Name"),
                    NTableColumn("age", "Age", width = 80.dp, align = NTableAlign.Right),
                    NTableColumn("address", "Address"),
                ),
                data = pagedRows,
                pagination = NTablePagination(
                    pageSize = 3,
                    pageSizes = listOf(3, 5, 10),
                    showSizePicker = true,
                ),
            )
        }

        DemoSection(
            title = "Summary",
            description = "The summary callback receives the rows of the current page and returns " +
                "a value for each column key.",
        ) {
            NTable(
                columns = listOf(
                    NTableColumn("name", "Name"),
                    NTableColumn("age", "Age", width = 90.dp, align = NTableAlign.Right),
                    NTableColumn("address", "Address"),
                ),
                data = peopleRows,
                summary = { rows ->
                    mapOf(
                        "name" to "Total of ${rows.size} rows",
                        "age" to rows.sumOf { it["age"] as? Int ?: 0 }.toString(),
                    )
                },
            )
        }
    }
}
