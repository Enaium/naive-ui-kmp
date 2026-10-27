package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NCheckbox
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NDivider
import cn.enaium.naiveui.components.NPagination
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NBaseLoading
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NPopoverSurface
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nBottomBorder
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.internal.nRightBorder
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.nSize

/** Side a column is pinned to when the table scrolls horizontally. */
enum class NDataTableFixed { Left, Right }

/** Sort direction of a data table column. */
enum class NDataTableSortOrder { Ascend, Descend }

/** One choice in a column filter dropdown. */
@Immutable
data class NDataTableFilterOption(val label: String, val value: String)

/** Column definition for [NDataTable]. */
@Immutable
data class NDataTableColumn(
    val key: String,
    val title: String,
    val width: Dp? = null,
    val minWidth: Dp? = null,
    val align: NTableAlign = NTableAlign.Left,
    val fixed: NDataTableFixed? = null,
    val sortable: Boolean = false,
    val filterOptions: List<NDataTableFilterOption> = emptyList(),
    val render: (@Composable (NTableRow, Int) -> Unit)? = null,
)

/**
 * Pagination options for [NDataTable].
 *
 * [itemCount] can be used for remote data; when null the filtered row count is
 * used. [page] and [pageSize] act as the initial/controlled values.
 */
@Immutable
data class NDataTablePagination(
    val page: Int = 1,
    val pageSize: Int = 10,
    val itemCount: Int? = null,
    val pageSizes: List<Int> = listOf(10, 20, 50),
    val showSizePicker: Boolean = false,
    val showQuickJumper: Boolean = false,
)

private class DataColumnSpec(
    val column: NDataTableColumn?,
    val fixed: NDataTableFixed?,
    val baseWidth: Dp,
    val flexible: Boolean,
)

private class DataColumnSlot(
    val spec: DataColumnSpec,
    val width: Dp?,
    val weight: Float?,
)

private class DataTableLayout(
    val left: List<DataColumnSlot>,
    val center: List<DataColumnSlot>,
    val right: List<DataColumnSlot>,
    val centerWidth: Dp,
    val scrolled: Boolean,
)

private fun dataTableLayout(
    columns: List<NDataTableColumn>,
    selectionMode: NTableSelectionMode,
    metrics: NTableMetrics,
    scrollX: Dp?,
): DataTableLayout {
    val hasFixed = columns.any { it.fixed != null } || scrollX != null
    val specs = mutableListOf<DataColumnSpec>()
    if (selectionMode != NTableSelectionMode.None) {
        specs += DataColumnSpec(
            column = null,
            fixed = if (hasFixed) NDataTableFixed.Left else null,
            baseWidth = metrics.selectionWidth,
            flexible = false,
        )
    }
    columns.forEach { column ->
        specs += DataColumnSpec(
            column = column,
            fixed = column.fixed,
            baseWidth = column.width ?: column.minWidth ?: 140.dp,
            flexible = column.width == null && column.fixed == null,
        )
    }
    val left = specs.filter { it.fixed == NDataTableFixed.Left }
    val right = specs.filter { it.fixed == NDataTableFixed.Right }
    val center = specs.filter { it.fixed == null }

    if (scrollX == null) {
        fun slot(spec: DataColumnSpec): DataColumnSlot {
            val weight = if (spec.flexible) 1f else null
            return DataColumnSlot(spec, if (weight == null) spec.baseWidth else null, weight)
        }
        return DataTableLayout(
            left = left.map(::slot),
            center = center.map(::slot),
            right = right.map(::slot),
            centerWidth = 0.dp,
            scrolled = false,
        )
    }

    fun fixedSlot(spec: DataColumnSpec) = DataColumnSlot(spec, spec.baseWidth, null)
    val leftSlots = left.map(::fixedSlot)
    val rightSlots = right.map(::fixedSlot)
    val leftWidth = leftSlots.fold(0.dp) { acc, slot -> acc + (slot.width ?: 0.dp) }
    val rightWidth = rightSlots.fold(0.dp) { acc, slot -> acc + (slot.width ?: 0.dp) }
    val fixedCenterWidth = center.filterNot { it.flexible }.fold(0.dp) { acc, spec ->
        acc + (spec.column?.width ?: spec.baseWidth)
    }
    val flexibleSpecs = center.filter { it.flexible }
    val minimumFlexWidth = flexibleSpecs.fold(0.dp) { acc, spec ->
        acc + (spec.column?.minWidth ?: 120.dp)
    }
    val budget = (scrollX - leftWidth - rightWidth).coerceAtLeast(fixedCenterWidth + minimumFlexWidth)
    val perFlexible = if (flexibleSpecs.isNotEmpty()) {
        ((budget - fixedCenterWidth) / flexibleSpecs.size).coerceAtLeast(0.dp)
    } else {
        0.dp
    }
    val resolved = center.map { spec ->
        if (spec.flexible) {
            DataColumnSlot(
                spec = spec,
                width = maxOf(spec.column?.minWidth ?: 120.dp, perFlexible),
                weight = null,
            )
        } else {
            DataColumnSlot(spec, spec.column?.width ?: spec.baseWidth, null)
        }
    }.toMutableList()
    val centerSum = resolved.fold(0.dp) { acc, slot -> acc + (slot.width ?: 0.dp) }
    if (resolved.isNotEmpty() && centerSum < budget) {
        val last = resolved.last()
        resolved[resolved.lastIndex] = DataColumnSlot(
            spec = last.spec,
            width = (last.width ?: 0.dp) + (budget - centerSum),
            weight = null,
        )
    }
    val centerWidth = resolved.fold(0.dp) { acc, slot -> acc + (slot.width ?: 0.dp) }
    return DataTableLayout(
        left = leftSlots,
        center = resolved,
        right = rightSlots,
        centerWidth = centerWidth,
        scrolled = true,
    )
}

private fun compareTableCellValues(a: Any?, b: Any?): Int = when {
    a === b -> 0
    a == null -> -1
    b == null -> 1
    a is Number && b is Number -> a.toDouble().compareTo(b.toDouble())
    a is Boolean && b is Boolean -> a.compareTo(b)
    else -> a.toString().compareTo(b.toString())
}

/**
 * Feature rich data table with column sorting, filtering, pinned columns,
 * selection, pagination, summary rows and virtualised bodies.
 */
@Composable
fun NDataTable(
    columns: List<NDataTableColumn>,
    data: List<NTableRow>,
    modifier: Modifier = Modifier,
    rowKey: (NTableRow) -> String = { row ->
        row.values["key"]?.toString()
            ?: row.values["id"]?.toString()
            ?: row.values.toString()
    },
    selectionMode: NTableSelectionMode = NTableSelectionMode.None,
    checkedRowKeys: Set<String> = emptySet(),
    onCheckedRowKeysChange: ((Set<String>) -> Unit)? = null,
    sorters: Map<String, NDataTableSortOrder>? = null,
    onSorterChange: ((Map<String, NDataTableSortOrder>) -> Unit)? = null,
    filters: Map<String, Set<String>>? = null,
    onFilterChange: ((Map<String, Set<String>>) -> Unit)? = null,
    multipleSort: Boolean = false,
    pagination: NDataTablePagination? = null,
    onPageChange: ((Int) -> Unit)? = null,
    onPageSizeChange: ((Int) -> Unit)? = null,
    striped: Boolean = false,
    bordered: Boolean = true,
    singleLine: Boolean = true,
    size: NSize? = null,
    loading: Boolean = false,
    scrollX: Dp? = null,
    maxHeight: Dp? = null,
    emptyDescription: String? = null,
    summary: ((List<NTableRow>) -> Map<String, String>)? = null,
) {
    val theme = LocalNTheme.current
    val colors = theme.colors
    val effectiveSize = nSize(size)
    val metrics = nTableMetrics(effectiveSize)

    var internalSorters by remember { mutableStateOf<Map<String, NDataTableSortOrder>>(emptyMap()) }
    var internalFilters by remember { mutableStateOf<Map<String, Set<String>>>(emptyMap()) }
    var openFilterKey by remember { mutableStateOf<String?>(null) }
    var page by remember(pagination?.page) {
        mutableStateOf((pagination?.page ?: 1).coerceAtLeast(1))
    }
    var pageSize by remember(pagination?.pageSize) {
        mutableStateOf((pagination?.pageSize ?: 10).coerceAtLeast(1))
    }

    val activeSorters = sorters ?: internalSorters
    val activeFilters = filters ?: internalFilters

    val filteredRows = data.filter { row ->
        activeFilters.all { (key, values) ->
            values.isEmpty() || values.any { value ->
                row[key]?.toString()?.contains(value, ignoreCase = true) == true
            }
        }
    }
    val sortedRows = if (activeSorters.isEmpty()) {
        filteredRows
    } else {
        filteredRows.sortedWith { a, b ->
            var result = 0
            for ((key, order) in activeSorters) {
                result = compareTableCellValues(a[key], b[key])
                if (order == NDataTableSortOrder.Descend) result = -result
                if (result != 0) break
            }
            result
        }
    }

    val pageCount = if (pagination == null) {
        1
    } else {
        val count = pagination.itemCount ?: sortedRows.size
        ((count + pageSize - 1) / pageSize).coerceAtLeast(1)
    }
    LaunchedEffect(pageCount) {
        if (page > pageCount) page = pageCount
    }
    val rows = if (pagination == null) {
        sortedRows
    } else {
        sortedRows.drop((page - 1) * pageSize).take(pageSize)
    }

    val layout = dataTableLayout(columns, selectionMode, metrics, scrollX)
    val horizontalState = rememberScrollState()
    val dividerColor = if (singleLine) null else colors.divider

    val pageKeys = rows.map(rowKey)
    val allChecked = pageKeys.isNotEmpty() && pageKeys.all { it in checkedRowKeys }
    val someChecked = !allChecked && pageKeys.any { it in checkedRowKeys }

    fun handleSort(column: NDataTableColumn) {
        if (!column.sortable) return
        val nextOrder = when (activeSorters[column.key]) {
            null -> NDataTableSortOrder.Ascend
            NDataTableSortOrder.Ascend -> NDataTableSortOrder.Descend
            NDataTableSortOrder.Descend -> null
        }
        val next = LinkedHashMap<String, NDataTableSortOrder>()
        next.putAll(activeSorters)
        if (nextOrder == null) {
            next.remove(column.key)
        } else {
            if (!multipleSort) next.clear()
            next[column.key] = nextOrder
        }
        onSorterChange?.invoke(next)
        if (sorters == null) internalSorters = next
    }

    fun handleFilter(column: NDataTableColumn, value: String) {
        val current = activeFilters[column.key].orEmpty()
        val nextValues = if (value in current) current - value else current + value
        val next = LinkedHashMap<String, Set<String>>()
        next.putAll(activeFilters)
        if (nextValues.isEmpty()) {
            next.remove(column.key)
        } else {
            next[column.key] = nextValues
        }
        onFilterChange?.invoke(next)
        if (filters == null) internalFilters = next
    }

    fun clearFilter(column: NDataTableColumn) {
        val next = LinkedHashMap<String, Set<String>>()
        next.putAll(activeFilters)
        next.remove(column.key)
        onFilterChange?.invoke(next)
        if (filters == null) internalFilters = next
    }

    fun toggleSelection(row: NTableRow, index: Int) {
        val callback = onCheckedRowKeysChange ?: return
        val key = pageKeys[index]
        when (selectionMode) {
            NTableSelectionMode.None -> Unit
            NTableSelectionMode.Single -> callback(setOf(key))
            NTableSelectionMode.Multiple -> callback(
                if (key in checkedRowKeys) checkedRowKeys - key else checkedRowKeys + key,
            )
        }
    }

    val headerCell: @Composable RowScope.(DataColumnSlot) -> Unit = { slot ->
        val column = slot.spec.column
        if (column == null) {
            NTableHeaderCell(
                metrics = metrics,
                width = slot.width,
                weight = slot.weight,
                align = NTableAlign.Center,
                dividerColor = dividerColor,
            ) {
                if (selectionMode == NTableSelectionMode.Multiple) {
                    NCheckbox(
                        checked = allChecked,
                        onCheckedChange = { checked ->
                            onCheckedRowKeysChange?.invoke(
                                if (checked) {
                                    checkedRowKeys + pageKeys
                                } else {
                                    checkedRowKeys - pageKeys.toSet()
                                },
                            )
                        },
                        indeterminate = someChecked,
                        size = effectiveSize,
                    )
                }
            }
        } else {
            DataTableHeaderCell(
                column = column,
                width = slot.width,
                weight = slot.weight,
                metrics = metrics,
                sortOrder = activeSorters[column.key],
                selectedFilters = activeFilters[column.key].orEmpty(),
                filterOpen = openFilterKey == column.key,
                onFilterOpenChange = { open ->
                    openFilterKey = if (open) column.key else null
                },
                onSort = { handleSort(column) },
                onFilterToggle = { value -> handleFilter(column, value) },
                onFilterClear = { clearFilter(column) },
                dividerColor = dividerColor,
            )
        }
    }

    val bodyCell: @Composable RowScope.(DataColumnSlot, NTableRow, Int) -> Unit =
        { slot, row, index ->
            val column = slot.spec.column
            if (column == null) {
                NTableCell(
                    metrics = metrics,
                    width = slot.width,
                    weight = slot.weight,
                    align = NTableAlign.Center,
                    dividerColor = dividerColor,
                ) {
                    NCheckbox(
                        checked = pageKeys[index] in checkedRowKeys,
                        onCheckedChange = { toggleSelection(row, index) },
                        size = effectiveSize,
                    )
                }
            } else {
                NTableCell(
                    metrics = metrics,
                    width = slot.width,
                    weight = slot.weight,
                    align = column.align,
                    dividerColor = dividerColor,
                ) {
                    val render = column.render
                    if (render != null) {
                        render(row, index)
                    } else {
                        nTableCellContent(row[column.key], metrics, colors.text2)
                    }
                }
            }
        }

    val renderRow: @Composable (NTableRow, Int) -> Unit = { row, index ->
        val key = pageKeys[index]
        val checked = key in checkedRowKeys
        val interaction = remember(key) { MutableInteractionSource() }
        val hovered by interaction.collectIsHoveredAsState()
        val selectedBackground = colors.primarySuppl.copy(
            alpha = if (theme.isDark) 0.16f else 0.08f,
        )
        val baseBackground = if (striped && index % 2 == 1) colors.tableStriped else colors.table
        val background = if (checked) {
            selectedBackground
        } else if (hovered) {
            colors.tableHover
        } else {
            baseBackground
        }
        Row(
            Modifier
                .fillMaxWidth()
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
                    if (selectionMode != NTableSelectionMode.None && onCheckedRowKeysChange != null) {
                        Modifier.nClickable(interaction) { toggleSelection(row, index) }
                    } else {
                        Modifier
                    },
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            layout.left.forEach { slot -> bodyCell(slot, row, index) }
            Box(Modifier.weight(1f).clipToBounds()) {
                Row(
                    if (layout.scrolled) {
                        Modifier.horizontalScroll(horizontalState).width(layout.centerWidth)
                    } else {
                        Modifier.fillMaxWidth()
                    },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    layout.center.forEach { slot -> bodyCell(slot, row, index) }
                }
            }
            layout.right.forEach { slot -> bodyCell(slot, row, index) }
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
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(colors.tableHeader)
                    .nBottomBorder(colors.border),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                layout.left.forEach { slot -> headerCell(slot) }
                Box(Modifier.weight(1f).clipToBounds()) {
                    Row(
                        if (layout.scrolled) {
                            Modifier.horizontalScroll(horizontalState).width(layout.centerWidth)
                        } else {
                            Modifier.fillMaxWidth()
                        },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        layout.center.forEach { slot -> headerCell(slot) }
                    }
                }
                layout.right.forEach { slot -> headerCell(slot) }
            }

            if (rows.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(maxHeight ?: 160.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    NEmpty(description = emptyDescription ?: LocalNLocale.current.tableEmpty, size = effectiveSize)
                }
            } else if (maxHeight != null) {
                LazyColumn(Modifier.fillMaxWidth().height(maxHeight)) {
                    itemsIndexed(rows) { index, row -> renderRow(row, index) }
                }
            } else {
                Column(Modifier.fillMaxWidth()) {
                    rows.forEachIndexed { index, row -> renderRow(row, index) }
                }
            }

            if (summary != null) {
                val values = summary(rows)
                NTableSummaryRow(theme) {
                    layout.left.forEach { slot ->
                        NTableCell(
                            metrics = metrics,
                            width = slot.width,
                            weight = slot.weight,
                            align = slot.spec.column?.align ?: NTableAlign.Center,
                            dividerColor = dividerColor,
                        ) {
                            val column = slot.spec.column
                            if (column != null) {
                                nTableCellContent(values[column.key], metrics, colors.text1)
                            }
                        }
                    }
                    Box(Modifier.weight(1f).clipToBounds()) {
                        Row(
                            if (layout.scrolled) {
                                Modifier.horizontalScroll(horizontalState).width(layout.centerWidth)
                            } else {
                                Modifier.fillMaxWidth()
                            },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            layout.center.forEach { slot ->
                                NTableCell(
                                    metrics = metrics,
                                    width = slot.width,
                                    weight = slot.weight,
                                    align = slot.spec.column?.align ?: NTableAlign.Left,
                                    dividerColor = dividerColor,
                                ) {
                                    val column = slot.spec.column
                                    if (column != null) {
                                        nTableCellContent(values[column.key], metrics, colors.text1)
                                    }
                                }
                            }
                        }
                    }
                    layout.right.forEach { slot ->
                        NTableCell(
                            metrics = metrics,
                            width = slot.width,
                            weight = slot.weight,
                            align = slot.spec.column?.align ?: NTableAlign.Left,
                            dividerColor = dividerColor,
                        ) {
                            val column = slot.spec.column
                            if (column != null) {
                                nTableCellContent(values[column.key], metrics, colors.text1)
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
                        onPageChange = {
                            page = it
                            onPageChange?.invoke(it)
                        },
                        pageSize = pageSize,
                        onPageSizeChange = {
                            pageSize = it.coerceAtLeast(1)
                            page = 1
                            onPageSizeChange?.invoke(it)
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

@Composable
private fun RowScope.DataTableHeaderCell(
    column: NDataTableColumn,
    width: Dp?,
    weight: Float?,
    metrics: NTableMetrics,
    sortOrder: NDataTableSortOrder?,
    selectedFilters: Set<String>,
    filterOpen: Boolean,
    onFilterOpenChange: (Boolean) -> Unit,
    onSort: () -> Unit,
    onFilterToggle: (String) -> Unit,
    onFilterClear: () -> Unit,
    dividerColor: Color?,
) {
    val theme = LocalNTheme.current
    val colors = theme.colors
    NTableHeaderCell(
        metrics = metrics,
        width = width,
        weight = weight,
        align = column.align,
        dividerColor = dividerColor,
        onClick = if (column.sortable) onSort else null,
    ) {
        NText(
            text = column.title,
            color = colors.text1,
            size = metrics.fontSize,
            strong = true,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (column.sortable) {
            Spacer(Modifier.width(4.dp))
            NIcon(
                icon = if (sortOrder == NDataTableSortOrder.Descend) NIcons.ArrowDown else NIcons.ArrowUp,
                size = 12.dp,
                color = if (sortOrder != null) colors.primary else colors.text3,
            )
        }
        if (column.filterOptions.isNotEmpty()) {
            Spacer(Modifier.width(4.dp))
            Box {
                val interaction = remember { MutableInteractionSource() }
                val hovered by interaction.collectIsHoveredAsState()
                Box(
                    Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(if (hovered) colors.closeHover else Color.Transparent)
                        .hoverable(interaction)
                        .nClickable(interaction) { onFilterOpenChange(!filterOpen) },
                    contentAlignment = Alignment.Center,
                ) {
                    NIcon(
                        icon = NIcons.Filter,
                        size = 13.dp,
                        color = if (selectedFilters.isNotEmpty()) colors.primary else colors.icon,
                    )
                }
                NPopoverSurface(
                    visible = filterOpen,
                    onDismiss = { onFilterOpenChange(false) },
                    placement = NPlacement.BottomEnd,
                    padding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                ) {
                    Column(Modifier.defaultMinSize(minWidth = 150.dp)) {
                        column.filterOptions.forEach { option ->
                            NCheckbox(
                                checked = option.value in selectedFilters,
                                onCheckedChange = { onFilterToggle(option.value) },
                                label = option.label,
                                size = NSize.Small,
                            )
                        }
                        if (selectedFilters.isNotEmpty()) {
                            NButton(
                                text = true,
                                size = NSize.Small,
                                onClick = onFilterClear,
                            ) {
                                NText(LocalNLocale.current.clear, size = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
