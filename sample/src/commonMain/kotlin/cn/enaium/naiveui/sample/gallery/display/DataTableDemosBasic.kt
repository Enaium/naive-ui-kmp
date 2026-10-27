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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NDataTable
import cn.enaium.naiveui.components.NDataTableColumn
import cn.enaium.naiveui.components.NDataTablePagination
import cn.enaium.naiveui.components.NGradientText
import cn.enaium.naiveui.components.NGradientTextType
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.components.NTableAlign
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NTooltip
import cn.enaium.naiveui.components.nRow
import cn.enaium.naiveui.sample.gallery.DemoSection

private val dtDemoGreen = Color(0xFF18A058)
private val dtDemoRed = Color(0xFFD03050)

@Composable
fun DtDemoBasic() {
    var played by remember { mutableStateOf<String?>(null) }
    DemoSection(
        title = "Basic usage",
        description = "Columns describe their key and title. A `render` column can place buttons or other content in a cell.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NText(played?.let { "Now playing: $it" } ?: "Press Play on a song.")
            NDataTable(
                columns = listOf(
                    NDataTableColumn("no", "No"),
                    NDataTableColumn("title", "Title"),
                    NDataTableColumn("length", "Length"),
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
                data = dtSongs,
                bordered = false,
            )
        }
    }
}

@Composable
fun DtDemoEmpty() {
    var showData by remember { mutableStateOf(false) }
    DemoSection(
        title = "Empty",
        description = "When there is no data the table shows a placeholder below the header.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NButton(
                size = NSize.Small,
                onClick = { showData = !showData },
            ) {
                NText(if (showData) "Clear data" else "Load data")
            }
            NDataTable(
                columns = listOf(
                    NDataTableColumn("name", "Name"),
                    NDataTableColumn("age", "Age"),
                    NDataTableColumn("address", "Address"),
                ),
                data = if (showData) dtPeople else emptyList(),
                emptyDescription = "No Data",
            )
        }
    }
}

@Composable
fun DtDemoBorder() {
    DemoSection(
        title = "Border",
        description = "Combine `bordered`, `singleLine` and `singleColumn` to control outer, column and row dividers.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val columns = listOf(
                NDataTableColumn("name", "Name"),
                NDataTableColumn("age", "Age"),
                NDataTableColumn("address", "Address"),
            )
            NDataTable(
                columns = columns,
                data = dtTagPeople,
                bordered = false,
                singleLine = false,
            )
            NDataTable(
                columns = columns,
                data = dtTagPeople.take(2),
                bordered = false,
            )
            NDataTable(
                columns = columns,
                data = dtTagPeople.take(2),
                bordered = false,
                singleLine = false,
                singleColumn = true,
            )
        }
    }
}

@Composable
fun DtDemoSize() {
    DemoSection(
        title = "Size",
        description = "The table follows the provider size or an explicit `size`.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val columns = listOf(
                NDataTableColumn("name", "Name"),
                NDataTableColumn("age", "Age"),
                NDataTableColumn("address", "Address"),
            )
            NDataTable(columns = columns, data = dtPeople.take(2), size = NSize.Small, bordered = false)
            NDataTable(columns = columns, data = dtPeople.take(2), size = NSize.Large, bordered = false)
        }
    }
}

@Composable
fun DtDemoRowProps() {
    var clicked by remember { mutableStateOf("Click a row to see its name.") }
    var activeKey by remember { mutableStateOf<String?>(null) }
    val highlight = cn.enaium.naiveui.theme.LocalNTheme.current.colors.primarySuppl.copy(alpha = 0.12f)
    DemoSection(
        title = "Row props",
        description = "Use `onRowClick` and `rowStyle` to attach behaviour and styling to a row.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NText(clicked)
            NDataTable(
                columns = listOf(
                    NDataTableColumn("name", "Name"),
                    NDataTableColumn("age", "Age"),
                    NDataTableColumn("address", "Address"),
                ),
                data = listOf(
                    nRow("key" to "0", "name" to "07akioni", "age" to "18", "address" to "Yiheyuan Road"),
                    nRow("key" to "1", "name" to "08akioni", "age" to "14", "address" to "Pingshan Road"),
                    nRow("key" to "2", "name" to "09akioni", "age" to "22", "address" to "Haidian Bridge"),
                ),
                onRowClick = { row, _ ->
                    clicked = "Clicked: ${row["name"]}"
                    activeKey = row["key"]?.toString()
                },
                rowStyle = { row, _ ->
                    if (row["key"]?.toString() == activeKey) {
                        cn.enaium.naiveui.components.NDataTableRowStyle(backgroundColor = highlight)
                    } else {
                        cn.enaium.naiveui.components.NDataTableRowStyle()
                    }
                },
            )
        }
    }
}

@Composable
fun DtDemoRenderCell() {
    DemoSection(
        title = "Render cell",
        description = "`renderCell` customizes the fallback for empty values across all columns.",
    ) {
        NDataTable(
            columns = listOf(
                NDataTableColumn(
                    key = "no",
                    title = "Date",
                    width = 120.dp,
                    render = { _, index -> NText("${index + 1}") },
                ),
                NDataTableColumn("note", "Note"),
            ),
            data = listOf(
                nRow("key" to "0", "no" to 3, "note" to ""),
                nRow("key" to "1", "no" to 4, "note" to ""),
                nRow("key" to "2", "no" to 12, "note" to ""),
                nRow("key" to "3", "no" to 10, "note" to ""),
                nRow("key" to "4", "no" to 19, "note" to ""),
            ),
            renderCell = { value, _, _ ->
                if (value == null || value.toString().isEmpty()) {
                    NText("Empty", depth = 3)
                } else {
                    NText(value.toString())
                }
            },
        )
    }
}

@Composable
fun DtDemoCustomStyle() {
    var highlight by remember { mutableStateOf(true) }
    DemoSection(
        title = "Custom style",
        description = "Column text colors and `rowStyle` combine like naive-ui row and column classes.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NButton(size = NSize.Small, onClick = { highlight = !highlight }) {
                    NText(if (highlight) "Disable row style" else "Enable row style")
                }
                NText("Rows older than 32 turn red; the age column stays green.")
            }
            NDataTable(
                columns = listOf(
                    NDataTableColumn("name", "Name"),
                    NDataTableColumn("age", "Age", textColor = dtDemoGreen),
                    NDataTableColumn("address", "Address"),
                ),
                data = dtPeople,
                rowStyle = { row, _ ->
                    val age = row["age"] as? Int ?: 0
                    if (highlight && age > 32) {
                        cn.enaium.naiveui.components.NDataTableRowStyle(textColor = dtDemoRed)
                    } else {
                        cn.enaium.naiveui.components.NDataTableRowStyle()
                    }
                },
            )
        }
    }
}

@Composable
fun DtDemoRenderHeader() {
    DemoSection(
        title = "Customized header",
        description = "`NDataTableColumn.header` renders arbitrary content, including gradient text and tooltips.",
    ) {
        NDataTable(
            columns = listOf(
                NDataTableColumn(
                    key = "name",
                    title = "Name",
                    header = {
                        NTooltip(text = "Tooltip Content") {
                            NGradientText("Name", type = NGradientTextType.Error, size = 22.sp)
                        }
                    },
                ),
                NDataTableColumn(
                    key = "age",
                    title = "Age",
                    header = {
                        NGradientText("Age", type = NGradientTextType.Info, size = 18.sp)
                    },
                ),
                NDataTableColumn(
                    key = "address",
                    title = "Address",
                    header = {
                        NGradientText("Address", type = NGradientTextType.Warning, size = 15.sp)
                    },
                ),
            ),
            data = dtPeople,
        )
    }
}

@Composable
fun DtDemoEllipsis() {
    DemoSection(
        title = "Ellipsis",
        description = "Set `ellipsis` on a column to truncate overflowing cell text.",
    ) {
        NDataTable(
            columns = listOf(
                NDataTableColumn("name", "Name"),
                NDataTableColumn("age", "Age"),
                NDataTableColumn("address", "Address", width = 110.dp, ellipsis = true),
                NDataTableColumn("anotherAddress", "Another Address", width = 110.dp, ellipsis = true),
                NDataTableColumn("more", "Details", width = 120.dp, ellipsis = true),
            ),
            data = dtPeople.mapIndexed { index, row ->
                row.dtWith("anotherAddress", row["address"])
                    .dtWith(
                        "more",
                        "Additional details for row $index that will be truncated.",
                    )
            },
            pagination = NDataTablePagination(pageSize = 10),
        )
    }
}

@Composable
fun DtDemoEllipsisTooltip() {
    DemoSection(
        title = "Ellipsis with tooltip",
        description = "`ellipsisTooltip` reveals the full value in a tooltip when the cell overflows.",
    ) {
        NDataTable(
            columns = listOf(
                NDataTableColumn("name", "Name"),
                NDataTableColumn("age", "Age"),
                NDataTableColumn("address", "Address", width = 110.dp, ellipsisTooltip = true),
                NDataTableColumn("anotherAddress", "Another Address", width = 110.dp, ellipsisTooltip = true),
            ),
            data = dtPeople.map { row -> row.dtWith("anotherAddress", row["address"]) },
            pagination = NDataTablePagination(pageSize = 10),
        )
    }
}

@Composable
fun DtDemoStriped() {
    DemoSection(
        title = "Striped",
        description = "Alternating row backgrounds make wide tables easier to scan.",
    ) {
        NDataTable(
            columns = listOf(
                NDataTableColumn("name", "Name"),
                NDataTableColumn("age", "Age", width = 80.dp, align = NTableAlign.Right),
                NDataTableColumn("address", "Address"),
            ),
            data = dtPeople46,
            striped = true,
            pagination = NDataTablePagination(pageSize = 10),
        )
    }
}
