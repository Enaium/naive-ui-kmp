package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NCheckbox
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NDivider
import cn.enaium.naiveui.components.NPagination
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NBaseLoading
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nBottomBorder
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.internal.nRightBorder
import cn.enaium.naiveui.internal.nTopBorder
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NTheme
import cn.enaium.naiveui.theme.nSize

/** Horizontal alignment of table cells. */
enum class NTableAlign { Left, Center, Right }

/** How many rows can be selected at once. */
enum class NTableSelectionMode { None, Single, Multiple }

/**
 * A single table row. Values are looked up by column key.
 *
 * Use [nRow] to build rows compactly:
 * `nRow("name" to "John", "age" to 32)`.
 */
@Immutable
data class NTableRow(val values: Map<String, Any?> = emptyMap()) {
    operator fun get(key: String): Any? = values[key]
}

/** Builds an [NTableRow] from key/value pairs. */
fun nRow(vararg values: Pair<String, Any?>): NTableRow = NTableRow(mapOf(*values))

/** Column definition for [NTable]. */
@Immutable
data class NTableColumn(
    val key: String,
    val title: String,
    val width: Dp? = null,
    val align: NTableAlign = NTableAlign.Left,
    val sorter: ((NTableRow, NTableRow) -> Int)? = null,
    val filter: ((NTableRow) -> Boolean)? = null,
    val render: (@Composable (Any?) -> Unit)? = null,
)

/** Pagination options for [NTable]. Page size is controlled internally. */
@Immutable
data class NTablePagination(
    val pageSize: Int = 10,
    val pageSizes: List<Int> = listOf(10, 20, 50),
    val showSizePicker: Boolean = false,
    val showQuickJumper: Boolean = false,
)

/** Per-row appearance and interaction overrides. */
@Immutable
data class NTableRowProps(
    val disabled: Boolean = false,
    val clickable: Boolean = true,
    val backgroundColor: Color? = null,
)

/** Size tokens shared by the table family. */
internal class NTableMetrics(
    val headerHeight: Dp,
    val rowHeight: Dp,
    val horizontalPadding: Dp,
    val fontSize: TextUnit,
    val selectionWidth: Dp,
)

@Composable
internal fun nTableMetrics(size: NSize): NTableMetrics {
    val theme = LocalNTheme.current
    return remember(size, theme) {
        when (size) {
            NSize.Tiny -> NTableMetrics(26.dp, 28.dp, 8.dp, theme.typography.fontSize.tiny, 32.dp)
            NSize.Small -> NTableMetrics(32.dp, 32.dp, 10.dp, theme.typography.fontSize.small, 36.dp)
            NSize.Medium -> NTableMetrics(38.dp, 38.dp, 12.dp, theme.typography.fontSize.medium, 40.dp)
            NSize.Large -> NTableMetrics(46.dp, 46.dp, 16.dp, theme.typography.fontSize.large, 48.dp)
        }
    }
}

@Composable
internal fun nTableCellContent(value: Any?, metrics: NTableMetrics, color: Color) {
    NText(
        text = value?.toString() ?: "",
        color = color,
        size = metrics.fontSize,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

private fun nTableTextAlignment(align: NTableAlign): Arrangement.Horizontal = when (align) {
    NTableAlign.Left -> Arrangement.Start
    NTableAlign.Center -> Arrangement.Center
    NTableAlign.Right -> Arrangement.End
}

@Composable
internal fun RowScope.NTableHeaderCell(
    metrics: NTableMetrics,
    width: Dp? = null,
    weight: Float? = null,
    align: NTableAlign = NTableAlign.Left,
    dividerColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        (if (weight != null) Modifier.weight(weight) else Modifier.width(width ?: 0.dp))
            .then(if (dividerColor != null) Modifier.nRightBorder(dividerColor) else Modifier)
            .then(if (onClick != null) Modifier.nClickable(interaction, onClick = onClick) else Modifier)
            .defaultMinSize(minHeight = metrics.headerHeight)
            .padding(horizontal = metrics.horizontalPadding, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = nTableTextAlignment(align),
        content = content,
    )
}

@Composable
internal fun RowScope.NTableCell(
    metrics: NTableMetrics,
    width: Dp? = null,
    weight: Float? = null,
    align: NTableAlign = NTableAlign.Left,
    dividerColor: Color? = null,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        (if (weight != null) Modifier.weight(weight) else Modifier.width(width ?: 0.dp))
            .then(if (dividerColor != null) Modifier.nRightBorder(dividerColor) else Modifier)
            .defaultMinSize(minHeight = metrics.rowHeight)
            .padding(horizontal = metrics.horizontalPadding, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = nTableTextAlignment(align),
        content = content,
    )
}

@Composable
private fun NTableSortIcon(ascending: Boolean, color: Color) {
    NIcon(
        icon = if (ascending) NIcons.ArrowUp else NIcons.ArrowDown,
        size = 12.dp,
        color = color,
    )
}

/**
 * A simple table whose columns and rows are described declaratively.
 *
 * Supports sorting, row filtering, single/multiple selection, striped and
 * bordered styles, a sticky header when [maxHeight] is set and optional
 * pagination and summary rows.
 */
@Composable
fun NTable(
    columns: List<NTableColumn>,
    data: List<NTableRow>,
    modifier: Modifier = Modifier,
    rowKey: (NTableRow) -> String = { row ->
        row.values["key"]?.toString()
            ?: row.values["id"]?.toString()
            ?: row.values.toString()
    },
    checkedRowKeys: Set<String> = emptySet(),
    onCheckedRowKeysChange: ((Set<String>) -> Unit)? = null,
    selectionMode: NTableSelectionMode = NTableSelectionMode.None,
    striped: Boolean = false,
    bordered: Boolean = true,
    singleLine: Boolean = true,
    size: NSize? = null,
    loading: Boolean = false,
    pagination: NTablePagination? = null,
    scrollX: Dp? = null,
    maxHeight: Dp? = null,
    summary: ((List<NTableRow>) -> Map<String, String>)? = null,
    emptyDescription: String? = null,
    rowProps: (NTableRow) -> NTableRowProps = { NTableRowProps() },
    onRowClick: ((NTableRow) -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val colors = theme.colors
    val effectiveSize = nSize(size)
    val metrics = nTableMetrics(effectiveSize)
    val showSelection = selectionMode != NTableSelectionMode.None

    var sortKey by remember { mutableStateOf<String?>(null) }
    var sortAscending by remember { mutableStateOf(true) }
    var page by remember { mutableStateOf(1) }
    var pageSize by remember(pagination?.pageSize) {
        mutableStateOf((pagination?.pageSize ?: 10).coerceAtLeast(1))
    }

    val filteredRows = remember(data, columns) {
        data.filter { row -> columns.all { column -> column.filter?.invoke(row) ?: true } }
    }
    val sortedRows = remember(filteredRows, sortKey, sortAscending, columns) {
        val comparator = columns.firstOrNull { it.key == sortKey }?.sorter
        if (comparator == null) {
            filteredRows
        } else {
            filteredRows.sortedWith { a, b ->
                val result = comparator(a, b)
                if (sortAscending) result else -result
            }
        }
    }
    val pageCount = if (pagination == null) {
        1
    } else {
        val count = (sortedRows.size + pageSize - 1) / pageSize
        count.coerceAtLeast(1)
    }
    LaunchedEffect(pageCount) {
        if (page > pageCount) page = pageCount
    }
    val rows = if (pagination == null) {
        sortedRows
    } else {
        sortedRows.drop((page - 1) * pageSize).take(pageSize)
    }

    val selectionWidth = if (showSelection) metrics.selectionWidth else 0.dp
    val scrolled = scrollX != null
    val resolvedWidths = if (scrolled) {
        nTableResolveWidths(columns, selectionWidth, scrollX!!)
    } else {
        emptyList()
    }
    val contentWidth = if (scrolled) {
        selectionWidth + resolvedWidths.fold(0.dp) { acc, width -> acc + width }
    } else {
        0.dp
    }
    val horizontalState = rememberScrollState()

    val pageKeys = rows.map(rowKey)
    val selectableKeys = rows.mapIndexedNotNull { index, row ->
        if (rowProps(row).disabled) null else pageKeys[index]
    }
    val allChecked = selectableKeys.isNotEmpty() && selectableKeys.all { it in checkedRowKeys }
    val someChecked = !allChecked && selectableKeys.any { it in checkedRowKeys }
    val dividerColor = if (singleLine) null else colors.divider

    fun changeSort(column: NTableColumn) {
        if (column.sorter == null) return
        if (sortKey == column.key) {
            if (sortAscending) {
                sortAscending = false
            } else {
                sortKey = null
                sortAscending = true
            }
        } else {
            sortKey = column.key
            sortAscending = true
        }
    }

    fun toggleSelection(row: NTableRow, index: Int) {
        val callback = onCheckedRowKeysChange ?: return
        val props = rowProps(row)
        if (props.disabled) return
        val key = pageKeys[index]
        when (selectionMode) {
            NTableSelectionMode.None -> Unit
            NTableSelectionMode.Single -> callback(setOf(key))
            NTableSelectionMode.Multiple -> callback(
                if (key in checkedRowKeys) checkedRowKeys - key else checkedRowKeys + key,
            )
        }
    }

    Box(modifier) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(theme.borderRadius))
                .background(colors.table)
                .then(
                    if (bordered) {
                        Modifier.nBorder(colors.border, 1.dp, theme.borderRadius)
                    } else {
                        Modifier
                    },
                ),
        ) {
            // Header is rendered outside the vertical scroll container so it
            // stays visible, mirroring the sticky header of naive-ui.
            Box(
                Modifier
                    .fillMaxWidth()
                    .then(if (scrolled) Modifier.horizontalScroll(horizontalState) else Modifier)
                    .background(colors.tableHeader)
                    .nBottomBorder(colors.border),
            ) {
                Row(
                    if (scrolled) Modifier.width(contentWidth) else Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (showSelection) {
                            NTableHeaderCell(
                                metrics = metrics,
                                width = if (scrolled) selectionWidth else metrics.selectionWidth,
                                align = NTableAlign.Center,
                                dividerColor = dividerColor,
                            ) {
                                if (selectionMode == NTableSelectionMode.Multiple) {
                                    NCheckbox(
                                        checked = allChecked,
                                        onCheckedChange = { checked ->
                                            onCheckedRowKeysChange?.invoke(
                                                if (checked) {
                                                    checkedRowKeys + selectableKeys
                                                } else {
                                                    checkedRowKeys - selectableKeys.toSet()
                                                },
                                            )
                                        },
                                        indeterminate = someChecked,
                                        size = effectiveSize,
                                    )
                                }
                            }
                        }
                        columns.forEachIndexed { index, column ->
                            val width = resolvedWidths.getOrNull(index)
                            val active = column.key == sortKey
                            NTableHeaderCell(
                                metrics = metrics,
                                width = if (scrolled) width else column.width,
                                weight = if (!scrolled && column.width == null) 1f else null,
                                align = column.align,
                                dividerColor = if (index < columns.lastIndex) dividerColor else null,
                                onClick = if (column.sorter != null) {
                                    { changeSort(column) }
                                } else {
                                    null
                                },
                            ) {
                                NText(
                                    text = column.title,
                                    color = colors.text1,
                                    size = metrics.fontSize,
                                    strong = true,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                if (column.sorter != null) {
                                    Spacer(Modifier.width(4.dp))
                                    NTableSortIcon(
                                        ascending = if (active) sortAscending else true,
                                        color = if (active) colors.primary else colors.text3,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            val body: @Composable () -> Unit = {
                Column(Modifier.fillMaxWidth()) {
                    rows.forEachIndexed { index, row ->
                        val props = rowProps(row)
                        val key = pageKeys[index]
                        val checked = key in checkedRowKeys
                        val interaction = remember(key) { MutableInteractionSource() }
                        val hovered by interaction.collectIsHoveredAsState()
                        val selectedBackground = colors.primarySuppl.copy(
                            alpha = if (theme.isDark) 0.16f else 0.08f,
                        )
                        val baseBackground = props.backgroundColor
                            ?: if (striped && index % 2 == 1) colors.tableStriped else colors.table
                        val background = when {
                            checked -> selectedBackground
                            else -> if (hovered) colors.tableHover else baseBackground
                        }
                        val clickable = !props.disabled &&
                            props.clickable &&
                            (onRowClick != null ||
                                (selectionMode != NTableSelectionMode.None &&
                                    onCheckedRowKeysChange != null))
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .then(if (scrolled) Modifier.horizontalScroll(horizontalState) else Modifier)
                                .background(background)
                                .then(
                                    if (!(bordered && index == rows.lastIndex)) {
                                        Modifier.nBottomBorder(colors.divider)
                                    } else {
                                        Modifier
                                    },
                                )
                                .hoverable(interaction)
                                .then(
                                    if (clickable) {
                                        Modifier.nClickable(interaction) {
                                            if (props.clickable && onRowClick != null) {
                                                onRowClick(row)
                                            }
                                            toggleSelection(row, index)
                                        }
                                    } else {
                                        Modifier
                                    },
                                ),
                        ) {
                            Row(
                                if (scrolled) {
                                    Modifier.width(contentWidth)
                                } else {
                                    Modifier.fillMaxWidth()
                                },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (showSelection) {
                                    NTableCell(
                                        metrics = metrics,
                                        width = if (scrolled) {
                                            selectionWidth
                                        } else {
                                            metrics.selectionWidth
                                        },
                                        align = NTableAlign.Center,
                                        dividerColor = dividerColor,
                                    ) {
                                        NCheckbox(
                                            checked = checked,
                                            onCheckedChange = { toggleSelection(row, index) },
                                            disabled = props.disabled,
                                            size = effectiveSize,
                                        )
                                    }
                                }
                                columns.forEachIndexed { columnIndex, column ->
                                    NTableCell(
                                        metrics = metrics,
                                        width = if (scrolled) {
                                            resolvedWidths.getOrNull(columnIndex)
                                        } else {
                                            column.width
                                        },
                                        weight = if (!scrolled && column.width == null) 1f else null,
                                        align = column.align,
                                        dividerColor = if (columnIndex < columns.lastIndex) {
                                            dividerColor
                                        } else {
                                            null
                                        },
                                    ) {
                                        val render = column.render
                                        if (render != null) {
                                            render(row[column.key])
                                        } else {
                                            nTableCellContent(row[column.key], metrics, colors.text2)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (rows.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .then(if (scrolled) Modifier.width(contentWidth) else Modifier)
                        .height(if (maxHeight != null) maxHeight else 120.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    NEmpty(description = emptyDescription ?: LocalNLocale.current.tableEmpty, size = effectiveSize)
                }
            } else if (maxHeight != null) {
                NScrollbar(Modifier.fillMaxWidth().height(maxHeight)) {
                    body()
                }
            } else {
                body()
            }

            if (summary != null) {
                val values = summary(rows)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .then(if (scrolled) Modifier.horizontalScroll(horizontalState) else Modifier)
                        .background(colors.tableHeader)
                        .nTopBorder(colors.border),
                ) {
                    Row(
                        if (scrolled) Modifier.width(contentWidth) else Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (showSelection) {
                                NTableCell(
                                    metrics = metrics,
                                    width = if (scrolled) selectionWidth else metrics.selectionWidth,
                                    align = NTableAlign.Center,
                                    dividerColor = dividerColor,
                                ) {}
                            }
                            columns.forEachIndexed { index, column ->
                                NTableCell(
                                    metrics = metrics,
                                    width = if (scrolled) resolvedWidths.getOrNull(index) else column.width,
                                    weight = if (!scrolled && column.width == null) 1f else null,
                                    align = column.align,
                                    dividerColor = if (index < columns.lastIndex) dividerColor else null,
                                ) {
                                    nTableCellContent(
                                        values[column.key],
                                        metrics,
                                        colors.text1,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (pagination != null) {
                NDivider()
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NPagination(
                        page = page,
                        pageCount = pageCount,
                        onPageChange = { page = it },
                        pageSize = pageSize,
                        onPageSizeChange = {
                            pageSize = it.coerceAtLeast(1)
                            page = 1
                        },
                        pageSizes = pagination.pageSizes,
                        showSizePicker = pagination.showSizePicker,
                        showQuickJumper = pagination.showQuickJumper,
                        size = effectiveSize,
                    )
                }
            }
        }

        if (loading) {
            Box(
                Modifier
                    .matchParentSize()
                    .background(colors.table.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center,
            ) {
                NBaseLoading(size = 26.dp)
            }
        }
    }
}

/** Resolves fixed-width columns, distributing remaining space evenly. */
private fun nTableResolveWidths(
    columns: List<NTableColumn>,
    selectionWidth: Dp,
    scrollX: Dp,
): List<Dp> {
    val explicit = selectionWidth + columns.fold(0.dp) { acc, column -> acc + (column.width ?: 0.dp) }
    val flexible = columns.count { it.width == null }
    val minimum = 120.dp
    val target = maxOf(scrollX, explicit + minimum * flexible)
    val width = if (flexible > 0) {
        ((target - explicit) / flexible).coerceAtLeast(80.dp)
    } else {
        0.dp
    }
    val widths = columns.map { it.width ?: width }.toMutableList()
    val total = selectionWidth + widths.fold(0.dp) { acc, value -> acc + value }
    if (widths.isNotEmpty() && total < target) {
        widths[widths.lastIndex] = widths.last() + (target - total)
    }
    return widths
}

/** Shared summary row shell used by [NDataTable]. */
@Composable
internal fun NTableSummaryRow(
    theme: NTheme,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = theme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .background(colors.tableHeader)
            .nTopBorder(colors.border),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}
