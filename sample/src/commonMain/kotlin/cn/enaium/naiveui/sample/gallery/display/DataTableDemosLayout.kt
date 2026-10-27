package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NDataTable
import cn.enaium.naiveui.components.NDataTableColumn
import cn.enaium.naiveui.components.NDataTableFixed
import cn.enaium.naiveui.components.NDataTablePagination
import cn.enaium.naiveui.components.NButtonType
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.components.NSlider
import cn.enaium.naiveui.components.NTableAlign
import cn.enaium.naiveui.components.NTableSelectionMode
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.nRow
import cn.enaium.naiveui.sample.gallery.DemoSection

@Composable
fun DtDemoMergeCell() {
    var message by remember { mutableStateOf("Click Send Email to show row data.") }
    DemoSection(
        title = "Merge cell",
        description = "`colSpan`, `rowSpan` and `titleColSpan` merge body and header cells.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NText(message)
            NDataTable(
                columns = listOf(
                    NDataTableColumn(
                        key = "name",
                        title = "Name",
                        titleColSpan = 2,
                        colSpan = { _, index -> if (index == 0) 2 else 1 },
                        rowSpan = { _, index -> if (index == 0) 2 else 1 },
                    ),
                    NDataTableColumn("age", "Age"),
                    NDataTableColumn(
                        key = "address",
                        title = "Address",
                        colSpan = { _, index -> if (index == 2) 2 else 1 },
                    ),
                    NDataTableColumn(
                        key = "tags",
                        title = "Tags",
                        titleColSpan = 2,
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
                        rowSpan = { _, index -> if (index == 0) 2 else 1 },
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
                singleLine = false,
                pagination = NDataTablePagination(pageSize = 10),
            )
        }
    }
}

@Composable
fun DtDemoColumnDraggable() {
    var played by remember { mutableStateOf("Drag the right edge of Title or Length to resize.") }
    DemoSection(
        title = "Column draggable",
        description = "Columns with `resizable` can be widened by dragging their right edge.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NText(played)
            NDataTable(
                columns = listOf(
                    NDataTableColumn("no", "No", width = 60.dp),
                    NDataTableColumn("title", "Title", resizable = true),
                    NDataTableColumn(
                        key = "length",
                        title = "Length",
                        width = 80.dp,
                        minWidth = 50.dp,
                        maxWidth = 120.dp,
                        resizable = true,
                    ),
                    NDataTableColumn(
                        key = "actions",
                        title = "Action",
                        width = 110.dp,
                        render = { row, _ ->
                            NButton(size = NSize.Small, tertiary = true, onClick = {
                                played = "Play ${row["title"]}"
                            }) {
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
fun DtDemoGroupHeader() {
    val groupData = remember {
        List(50) { index ->
            nRow(
                "key" to index.toString(),
                "name" to "name_$index",
                "physicsAttack" to "physicsAttack_$index",
                "magicAttack" to "magicAttack_$index",
                "defend" to "defend_$index",
                "speed" to "speed_$index",
            )
        }
    }
    DemoSection(
        title = "Grouped header",
        description = "Columns with `children` render a grouped, multi-row header.",
    ) {
        NDataTable(
            columns = listOf(
                NDataTableColumn("name", "Name"),
                NDataTableColumn(
                    key = "attrs",
                    title = "Attrs",
                    children = listOf(
                        NDataTableColumn(
                            key = "attack",
                            title = "Attack",
                            children = listOf(
                                NDataTableColumn("physicsAttack", "Physics Attack"),
                                NDataTableColumn("magicAttack", "Magic Attack"),
                            ),
                        ),
                        NDataTableColumn("defend", "Defend"),
                        NDataTableColumn("speed", "Speed"),
                    ),
                ),
            ),
            data = groupData,
            singleLine = false,
            pagination = NDataTablePagination(pageSize = 10),
        )
    }
}

@Composable
fun DtDemoFixedHeader() {
    DemoSection(
        title = "Fixed header",
        description = "`maxHeight` bounds the body and keeps the header visible while scrolling.",
    ) {
        NDataTable(
            columns = listOf(
                NDataTableColumn("name", "Name"),
                NDataTableColumn("age", "Age"),
                NDataTableColumn("address", "Address"),
            ),
            data = dtPeople46,
            maxHeight = 250.dp,
            pagination = NDataTablePagination(pageSize = 15),
        )
    }
}

@Composable
fun DtDemoFixedHeaderColumn() {
    var checked by remember { mutableStateOf<Set<String>>(emptySet()) }
    DemoSection(
        title = "Fixed header and column",
        description = "Pinned left and right columns stay visible while the middle scrolls horizontally.",
    ) {
        NDataTable(
            columns = listOf(
                NDataTableColumn("name", "Name", width = 200.dp, fixed = NDataTableFixed.Left),
                NDataTableColumn(
                    key = "age",
                    title = "Age",
                    width = 100.dp,
                    fixed = NDataTableFixed.Left,
                    align = NTableAlign.Right,
                ),
                NDataTableColumn(
                    key = "row",
                    title = "Row",
                    render = { _, index -> NText("row $index") },
                ),
                NDataTableColumn(
                    key = "row1",
                    title = "Row1",
                    render = { _, index -> NText("row1 $index") },
                ),
                NDataTableColumn(
                    key = "row2",
                    title = "Row2",
                    width = 100.dp,
                    fixed = NDataTableFixed.Right,
                ),
                NDataTableColumn(
                    key = "address",
                    title = "Address",
                    width = 200.dp,
                    fixed = NDataTableFixed.Right,
                ),
            ),
            data = dtFixedRows,
            selectionMode = NTableSelectionMode.Multiple,
            checkedRowKeys = checked,
            onCheckedRowKeysChange = { checked = it },
            maxHeight = 250.dp,
            scrollX = 1800.dp,
            pagination = NDataTablePagination(pageSize = 10),
        )
    }
}

@Composable
fun DtDemoSummary() {
    DemoSection(
        title = "Summary",
        description = "`summary` returns a value for each column key and receives the current page data.",
    ) {
        NDataTable(
            columns = listOf(
                NDataTableColumn("name", "Name"),
                NDataTableColumn("age", "Age", width = 90.dp, align = NTableAlign.Right),
                NDataTableColumn("address", "Address"),
            ),
            data = dtPeople.take(3),
            summary = { rows ->
                mapOf(
                    "name" to "Total of ${rows.size} rows",
                    "age" to rows.sumOf { it["age"] as? Int ?: 0 }.toString(),
                )
            },
            pagination = NDataTablePagination(pageSize = 10),
        )
    }
}

@Composable
fun DtDemoVirtual() {
    val largeData = remember {
        List(5000) { index ->
            nRow(
                "key" to index.toString(),
                "name" to "Edward King $index",
                "age" to 32,
                "address" to "London, Park Lane no. $index",
            )
        }
    }
    DemoSection(
        title = "Virtual scroll",
        description = "Only visible rows are composed. 5000 rows render with a 250dp body.",
    ) {
        NDataTable(
            columns = listOf(
                NDataTableColumn("name", "Name", width = 200.dp, fixed = NDataTableFixed.Left),
                NDataTableColumn(
                    key = "age",
                    title = "Age",
                    width = 100.dp,
                    fixed = NDataTableFixed.Left,
                    align = NTableAlign.Right,
                ),
                NDataTableColumn("address", "Address", width = 300.dp),
                NDataTableColumn(
                    key = "score",
                    title = "Score",
                    width = 120.dp,
                    render = { _, index -> NText("${index % 100}") },
                ),
            ),
            data = largeData,
            selectionMode = NTableSelectionMode.Multiple,
            maxHeight = 250.dp,
            scrollX = 900.dp,
        )
    }
}

@Composable
fun DtDemoVirtualX() {
    val rowCount = 1000
    val columnCount = 100
    val virtualColumns = List(columnCount) { index ->
        NDataTableColumn(
            key = "col$index",
            title = "Col $index",
            width = 100.dp,
            fixed = when {
                index <= 1 -> NDataTableFixed.Left
                index > columnCount - 3 -> NDataTableFixed.Right
                else -> null
            },
            render = { _, rowIndex -> NText("$index-$rowIndex") },
        )
    }
    val virtualData = remember(rowCount) {
        List(rowCount) { index -> nRow("key" to index.toString()) }
    }
    DemoSection(
        title = "Large data (rows & columns)",
        description = "1000 rows and 100 columns: rows are virtualized inside the 250dp body while the " +
            "columns scroll horizontally with pinned edges.",
    ) {
        NDataTable(
            columns = virtualColumns,
            data = virtualData,
            maxHeight = 250.dp,
            scrollX = (columnCount * 100).dp,
        )
    }
}

@Composable
fun DtDemoFlexHeight() {
    var tableHeight by remember { mutableStateOf(260f) }
    DemoSection(
        title = "Flex height",
        description = "`flexHeight` makes the body fit the requested table height.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NSlider(
                value = tableHeight,
                onValueChange = { tableHeight = it },
                min = 200f,
                max = 500f,
                step = 50f,
                modifier = Modifier.widthIn(max = 220.dp),
            )
            NText("Table height: ${tableHeight.toInt()}dp")
            NDataTable(
                columns = listOf(
                    NDataTableColumn("name", "Name", width = 200.dp, fixed = NDataTableFixed.Left),
                    NDataTableColumn(
                        key = "age",
                        title = "Age",
                        width = 100.dp,
                        fixed = NDataTableFixed.Left,
                    ),
                    NDataTableColumn(
                        key = "row",
                        title = "Row",
                        render = { _, index -> NText("row $index") },
                    ),
                    NDataTableColumn(
                        key = "row1",
                        title = "Row1",
                        render = { _, index -> NText("row1 $index") },
                    ),
                    NDataTableColumn(
                        key = "address",
                        title = "Address",
                        width = 200.dp,
                        fixed = NDataTableFixed.Right,
                    ),
                ),
                data = dtFixedRows,
                scrollX = 1800.dp,
                height = tableHeight.dp,
                flexHeight = true,
                pagination = NDataTablePagination(pageSize = 10),
            )
        }
    }
}

@Composable
fun DtDemoContextMenu() {
    var lastAction by remember { mutableStateOf("Long-press a row to open its context menu.") }
    DemoSection(
        title = "Context menu",
        description = "Long-press a row to open a popover menu anchored to that row.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NText(lastAction)
            NDataTable(
                columns = listOf(
                    NDataTableColumn("no", "No."),
                    NDataTableColumn("title", "Title"),
                    NDataTableColumn("length", "Length"),
                ),
                data = dtSongs,
                rowContextMenu = { row, dismiss ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        NButton(
                            text = true,
                            size = NSize.Small,
                            onClick = {
                                lastAction = "Edit ${row["title"]}"
                                dismiss()
                            },
                        ) {
                            NText("Edit")
                        }
                        NButton(
                            text = true,
                            type = NButtonType.Error,
                            size = NSize.Small,
                            onClick = {
                                lastAction = "Delete ${row["title"]}"
                                dismiss()
                            },
                        ) {
                            NText("Delete")
                        }
                    }
                },
            )
        }
    }
}
