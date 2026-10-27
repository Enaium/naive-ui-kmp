package cn.enaium.naiveui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NBaseLoading
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NPopoverSurface
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nBottomBorder
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NTheme
import cn.enaium.naiveui.theme.nSize
import kotlinx.coroutines.launch

/** Side a column is pinned to when the table scrolls horizontally. */
enum class NDataTableFixed { Left, Right }

/** Sort direction of a data table column. */
enum class NDataTableSortOrder { Ascend, Descend }

/** Pagination behaviour after filters change. */
enum class NDataTablePaginationBehavior { First, Current }

/** One choice in a column filter dropdown. */
@Immutable
data class NDataTableFilterOption(val label: String, val value: String)

/** One entry in the selection dropdown shown next to the header checkbox. */
@Immutable
data class NDataTableSelectionOption(
    val label: String,
    val onSelect: (pageRows: List<NTableRow>, checked: Set<String>) -> Set<String>,
)

/** Visual overrides applied to a row by [NDataTable]'s `rowStyle` callback. */
@Immutable
data class NDataTableRowStyle(
    val textColor: Color? = null,
    val backgroundColor: Color? = null,
)

/**
 * Column definition for [NDataTable].
 *
 * A column with [children] becomes a grouped header and is not rendered as a
 * body cell; the leaves are used for data rendering.
 */
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
    val maxWidth: Dp? = null,
    val sortComparator: ((NTableRow, NTableRow) -> Int)? = null,
    val defaultSortOrder: NDataTableSortOrder? = null,
    val nextSortOrder: ((NDataTableSortOrder?) -> NDataTableSortOrder?)? = null,
    val filterMultiple: Boolean = true,
    val defaultFilterValues: List<String> = emptyList(),
    val filter: ((value: String, row: NTableRow) -> Boolean)? = null,
    val header: (@Composable () -> Unit)? = null,
    val sorterIcon: (@Composable (order: NDataTableSortOrder?) -> Unit)? = null,
    val filterIcon: (@Composable (active: Boolean) -> Unit)? = null,
    val filterMenu: (@Composable (selected: Set<String>, onValuesChange: (Set<String>) -> Unit, dismiss: () -> Unit) -> Unit)? = null,
    val ellipsis: Boolean = false,
    val ellipsisTooltip: Boolean = false,
    val resizable: Boolean = false,
    val textColor: Color? = null,
    val colSpan: ((row: NTableRow, index: Int) -> Int)? = null,
    val rowSpan: ((row: NTableRow, index: Int) -> Int)? = null,
    val titleColSpan: Int = 1,
    val children: List<NDataTableColumn> = emptyList(),
)

/**
 * Pagination options for [NDataTable].
 *
 * [itemCount] is used for remote data. [page] and [pageSize] act as the
 * initial/controlled values.
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
    val selection: Boolean = false,
    val expand: Boolean = false,
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

private data class LeafColumnInfo(
    val column: NDataTableColumn,
    val path: List<NDataTableColumn>,
)

private class DataDisplayRow(
    val row: NTableRow,
    val key: String,
    val depth: Int,
    val expandable: Boolean,
    val expanded: Boolean,
    val loading: Boolean,
    val index: Int,
)

private class MergeStart(val colSpan: Int, val rowSpan: Int)

private fun flattenLeafColumns(
    columns: List<NDataTableColumn>,
    path: List<NDataTableColumn> = emptyList(),
): List<LeafColumnInfo> = columns.flatMap { column ->
    if (column.children.isEmpty()) {
        listOf(LeafColumnInfo(column, path + column))
    } else {
        flattenLeafColumns(column.children, path + column)
    }
}

private fun defaultSorters(leafColumns: List<NDataTableColumn>): Map<String, NDataTableSortOrder> {
    val result = LinkedHashMap<String, NDataTableSortOrder>()
    leafColumns.forEach { column ->
        column.defaultSortOrder?.let { result[column.key] = it }
    }
    return result
}

private fun defaultFilters(leafColumns: List<NDataTableColumn>): Map<String, Set<String>> {
    val result = LinkedHashMap<String, Set<String>>()
    leafColumns.forEach { column ->
        if (column.defaultFilterValues.isNotEmpty()) {
            result[column.key] = column.defaultFilterValues.toSet()
        }
    }
    return result
}

private fun combinedSlotWidth(slots: List<DataColumnSlot>): Pair<Dp?, Float?> {
    val weights = slots.mapNotNull { it.weight }
    return if (weights.size == slots.size) {
        null to weights.sum()
    } else {
        val width = slots.fold(0.dp) { acc, slot -> acc + (slot.width ?: 0.dp) }
        width to null
    }
}

private fun dataTableLayout(
    leafColumns: List<NDataTableColumn>,
    selectionMode: NTableSelectionMode,
    selectionWidth: Dp,
    hasExpandColumn: Boolean,
    metrics: NTableMetrics,
    scrollX: Dp?,
    widthOverrides: Map<String, Dp>,
): DataTableLayout {
    val hasFixed = leafColumns.any { it.fixed != null } || scrollX != null
    val specs = mutableListOf<DataColumnSpec>()
    if (selectionMode != NTableSelectionMode.None) {
        specs += DataColumnSpec(
            column = null,
            selection = true,
            fixed = if (hasFixed) NDataTableFixed.Left else null,
            baseWidth = selectionWidth,
            flexible = false,
        )
    }
    if (hasExpandColumn) {
        specs += DataColumnSpec(
            column = null,
            expand = true,
            fixed = if (hasFixed) NDataTableFixed.Left else null,
            baseWidth = 34.dp,
            flexible = false,
        )
    }
    leafColumns.forEach { column ->
        val override = widthOverrides[column.key]
        val base = override ?: column.width ?: column.minWidth ?: 140.dp
        specs += DataColumnSpec(
            column = column,
            fixed = column.fixed,
            baseWidth = base,
            flexible = override == null && column.width == null &&
                column.fixed == null && !column.resizable,
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
        acc + spec.baseWidth
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
            DataColumnSlot(spec, spec.baseWidth, null)
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

private fun buildDisplayRows(
    topRows: List<NTableRow>,
    tree: Boolean,
    rowKey: (NTableRow) -> String,
    childrenOf: (NTableRow) -> List<NTableRow>,
    isExpandable: (NTableRow) -> Boolean,
    expandedKeys: Set<String>,
    loadingKeys: Set<String>,
): List<DataDisplayRow> {
    if (!tree) {
        return topRows.mapIndexed { index, row ->
            val key = rowKey(row)
            DataDisplayRow(
                row = row,
                key = key,
                depth = 0,
                expandable = isExpandable(row),
                expanded = key in expandedKeys,
                loading = key in loadingKeys,
                index = index,
            )
        }
    }
    val result = ArrayList<DataDisplayRow>()
    fun visit(row: NTableRow, depth: Int) {
        val key = rowKey(row)
        result += DataDisplayRow(
            row = row,
            key = key,
            depth = depth,
            expandable = isExpandable(row),
            expanded = key in expandedKeys,
            loading = key in loadingKeys,
            index = result.size,
        )
        if (key in expandedKeys) {
            childrenOf(row).forEach { visit(it, depth + 1) }
        }
    }
    topRows.forEach { visit(it, 0) }
    return result
}

/** Returns a CSV document for [rows] using the leaf columns of [columns]. */
fun nDataTableCsv(
    columns: List<NDataTableColumn>,
    rows: List<NTableRow>,
    header: ((NDataTableColumn) -> String)? = null,
    cell: ((Any?, NTableRow, NDataTableColumn) -> String)? = null,
): String {
    val leaves = flattenLeafColumns(columns).map { it.column }
    fun escape(value: String): String =
        if (value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    val headerLine = leaves.joinToString(",") { escape(header?.invoke(it) ?: it.title) }
    val body = rows.map { row ->
        leaves.joinToString(",") { column ->
            val formatted = cell?.invoke(row[column.key], row, column)
                ?: row[column.key]?.toString().orEmpty()
            escape(formatted)
        }
    }
    return (listOf(headerLine) + body).joinToString("\n")
}

/**
 * Data-driven table with filtering, sorting, selection, pinned columns,
 * expandable/tree rows, grouped and merged headers, editable cells, pagination
 * and virtualised rendering.
 */
@OptIn(ExperimentalFoundationApi::class)
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
    selectable: ((NTableRow) -> Boolean)? = null,
    selectionOptions: List<NDataTableSelectionOption> = emptyList(),
    sorters: Map<String, NDataTableSortOrder>? = null,
    onSorterChange: ((Map<String, NDataTableSortOrder>) -> Unit)? = null,
    filters: Map<String, Set<String>>? = null,
    onFilterChange: ((Map<String, Set<String>>) -> Unit)? = null,
    multipleSort: Boolean = false,
    pagination: NDataTablePagination? = null,
    onPageChange: ((Int) -> Unit)? = null,
    onPageSizeChange: ((Int) -> Unit)? = null,
    paginationBehaviorOnFilter: NDataTablePaginationBehavior = NDataTablePaginationBehavior.Current,
    remote: Boolean = false,
    expandedRowKeys: Set<String>? = null,
    onExpandedRowKeysChange: ((Set<String>) -> Unit)? = null,
    defaultExpandAll: Boolean = false,
    childrenKey: String = "children",
    expandable: ((NTableRow) -> Boolean)? = null,
    renderExpand: (@Composable (NTableRow, Int) -> Unit)? = null,
    renderExpandIcon: (@Composable (expanded: Boolean, loading: Boolean) -> Unit)? = null,
    renderCell: (@Composable (value: Any?, row: NTableRow, column: NDataTableColumn) -> Unit)? = null,
    onLoadChildren: (suspend (NTableRow) -> List<NTableRow>?)? = null,
    onRowClick: ((NTableRow, Int) -> Unit)? = null,
    rowStyle: ((NTableRow, Int) -> NDataTableRowStyle)? = null,
    rowContextMenu: (@Composable (row: NTableRow, dismiss: () -> Unit) -> Unit)? = null,
    onDisplayedRowsChange: ((List<NTableRow>) -> Unit)? = null,
    striped: Boolean = false,
    bordered: Boolean = true,
    singleColumn: Boolean = false,
    singleLine: Boolean = true,
    bottomBordered: Boolean = true,
    size: NSize? = null,
    loading: Boolean = false,
    scrollX: Dp? = null,
    maxHeight: Dp? = null,
    height: Dp? = null,
    flexHeight: Boolean = false,
    emptyDescription: String? = null,
    summary: ((List<NTableRow>) -> Map<String, String>)? = null,
) {
    val theme = LocalNTheme.current
    val colors = theme.colors
    val effectiveSize = nSize(size)
    val metrics = nTableMetrics(effectiveSize)
    val scope = rememberCoroutineScope()

    val leafInfos = remember(columns) { flattenLeafColumns(columns) }
    val leafColumns = leafInfos.map { it.column }
    val leafInfoByKey = remember(leafInfos) { leafInfos.associateBy { it.column.key } }
    val columnByKey = remember(leafInfos) { leafInfos.associate { it.column.key to it.column } }
    val groupMode = columns.any { it.children.isNotEmpty() }
    val maxHeaderRows = if (groupMode) {
        (leafInfos.maxOfOrNull { it.path.size } ?: 1).coerceAtLeast(1)
    } else {
        1
    }
    val treeMode = onLoadChildren != null ||
        data.any { (it[childrenKey] as? List<*>)?.isNotEmpty() == true }
    val contentExpandMode = !treeMode && renderExpand != null
    val hasExpandColumn = contentExpandMode

    val loadedChildren = remember { mutableStateMapOf<String, List<NTableRow>>() }
    val loadingKeys = remember { mutableStateMapOf<String, Boolean>() }
    var contextMenuKey by remember { mutableStateOf<String?>(null) }
    var selectionMenuOpen by remember { mutableStateOf(false) }
    var widthOverrides by remember { mutableStateOf<Map<String, Dp>>(emptyMap()) }

    var internalSorters by remember { mutableStateOf(defaultSorters(leafColumns)) }
    var internalFilters by remember { mutableStateOf(defaultFilters(leafColumns)) }
    var openFilterKey by remember { mutableStateOf<String?>(null) }
    var page by remember(pagination?.page) {
        mutableStateOf((pagination?.page ?: 1).coerceAtLeast(1))
    }
    var pageSize by remember(pagination?.pageSize) {
        mutableStateOf((pagination?.pageSize ?: 10).coerceAtLeast(1))
    }

    val activeSorters = sorters ?: internalSorters
    val activeFilters = filters ?: internalFilters

    val childrenOf: (NTableRow) -> List<NTableRow> = { row ->
        loadedChildren[rowKey(row)]
            ?: (row[childrenKey] as? List<*>)?.filterIsInstance<NTableRow>()
            ?: emptyList()
    }
    val isRowExpandable: (NTableRow) -> Boolean = { row ->
        if (treeMode) {
            childrenOf(row).isNotEmpty() ||
                (onLoadChildren != null && row["isLeaf"] != true) ||
                expandable?.invoke(row) == true
        } else {
            renderExpand != null && expandable?.invoke(row) != false
        }
    }
    val initialExpanded = remember(data, defaultExpandAll, treeMode, childrenKey) {
        if (!defaultExpandAll) {
            emptySet()
        } else {
            buildSet {
                fun visit(row: NTableRow) {
                    if (isRowExpandable(row)) {
                        add(rowKey(row))
                        childrenOf(row).forEach { visit(it) }
                    }
                }
                data.forEach { visit(it) }
            }
        }
    }
    var internalExpanded by remember(data, defaultExpandAll) {
        mutableStateOf(initialExpanded)
    }
    val expandedKeys = expandedRowKeys ?: internalExpanded

    val filteredTopRows = if (remote) {
        data
    } else {
        data.filter { row ->
            activeFilters.all { (key, values) ->
                if (values.isEmpty()) {
                    true
                } else {
                    val column = columnByKey[key]
                    values.any { value ->
                        column?.filter?.invoke(value, row)
                            ?: (row[key]?.toString()?.contains(value, ignoreCase = true) == true)
                    }
                }
            }
        }
    }
    val sortedTopRows = if (remote || activeSorters.isEmpty()) {
        filteredTopRows
    } else {
        filteredTopRows.sortedWith { a, b ->
            var result = 0
            for ((key, order) in activeSorters) {
                val column = columnByKey[key]
                result = column?.sortComparator?.invoke(a, b)
                    ?: compareTableCellValues(a[key], b[key])
                if (order == NDataTableSortOrder.Descend) result = -result
                if (result != 0) break
            }
            result
        }
    }
    val pageCount = if (pagination == null) {
        1
    } else {
        val count = pagination.itemCount ?: sortedTopRows.size
        ((count + pageSize - 1) / pageSize).coerceAtLeast(1)
    }
    LaunchedEffect(pageCount) {
        if (page > pageCount) {
            page = pageCount
            onPageChange?.invoke(pageCount)
        }
    }
    val pageRows = if (pagination == null || remote) {
        sortedTopRows
    } else {
        sortedTopRows.drop((page - 1) * pageSize).take(pageSize)
    }
    LaunchedEffect(sortedTopRows) {
        onDisplayedRowsChange?.invoke(sortedTopRows)
    }

    var previousFilters by remember { mutableStateOf(activeFilters) }
    LaunchedEffect(activeFilters) {
        if (activeFilters != previousFilters) {
            previousFilters = activeFilters
            if (paginationBehaviorOnFilter == NDataTablePaginationBehavior.First) {
                page = 1
                onPageChange?.invoke(1)
            }
        }
    }

    val selectionWidth = metrics.selectionWidth + if (selectionOptions.isNotEmpty()) 18.dp else 0.dp
    val layout = dataTableLayout(
        leafColumns = leafColumns,
        selectionMode = selectionMode,
        selectionWidth = selectionWidth,
        hasExpandColumn = hasExpandColumn,
        metrics = metrics,
        scrollX = scrollX,
        widthOverrides = widthOverrides,
    )
    val horizontalState = rememberScrollState()
    val dividerColor = if (singleLine) null else colors.divider
    val selectionPageKeys = pageRows
        .filter { selectable?.invoke(it) != false }
        .map(rowKey)
    val allChecked = selectionPageKeys.isNotEmpty() && selectionPageKeys.all { it in checkedRowKeys }
    val someChecked = !allChecked && selectionPageKeys.any { it in checkedRowKeys }

    val displayRows = buildDisplayRows(
        topRows = pageRows,
        tree = treeMode,
        rowKey = rowKey,
        childrenOf = childrenOf,
        isExpandable = if (contentExpandMode) {
            { row -> expandable?.invoke(row) != false }
        } else {
            isRowExpandable
        },
        expandedKeys = expandedKeys,
        loadingKeys = loadingKeys.filterValues { it }.keys.toSet(),
    )
    val mergeMode = leafColumns.any { it.colSpan != null || it.rowSpan != null } &&
        selectionMode == NTableSelectionMode.None &&
        !hasExpandColumn &&
        !groupMode &&
        !layout.scrolled &&
        layout.left.isEmpty() &&
        layout.right.isEmpty()
    val mergeStarts: List<Map<Int, MergeStart>> = if (!mergeMode || displayRows.isEmpty()) {
        emptyList()
    } else {
        val starts = List(displayRows.size) { HashMap<Int, MergeStart>() }
        val covered = Array(displayRows.size) { BooleanArray(leafColumns.size) }
        for (rowIndex in displayRows.indices) {
            val row = displayRows[rowIndex].row
            var columnIndex = 0
            while (columnIndex < leafColumns.size) {
                if (covered[rowIndex][columnIndex]) {
                    columnIndex++
                    continue
                }
                val column = leafColumns[columnIndex]
                val colSpan = (column.colSpan?.invoke(row, rowIndex) ?: 1)
                    .coerceIn(1, leafColumns.size - columnIndex)
                val rowSpan = (column.rowSpan?.invoke(row, rowIndex) ?: 1).coerceAtLeast(1)
                starts[rowIndex][columnIndex] = MergeStart(colSpan, rowSpan)
                for (r in rowIndex until minOf(rowIndex + rowSpan, displayRows.size)) {
                    for (c in columnIndex until minOf(columnIndex + colSpan, leafColumns.size)) {
                        covered[r][c] = true
                    }
                }
                columnIndex += colSpan
            }
        }
        starts
    }

    val footerHeight = if (pagination != null) 52.dp else 0.dp
    val effectiveMaxHeight = maxHeight ?: if (flexHeight && height != null) {
        (height - metrics.headerHeight * maxHeaderRows - footerHeight).coerceAtLeast(80.dp)
    } else {
        null
    }

    fun updateExpanded(next: Set<String>) {
        onExpandedRowKeysChange?.invoke(next)
        if (expandedRowKeys == null) internalExpanded = next
    }

    fun toggleRow(row: NTableRow) {
        val key = rowKey(row)
        val currentlyExpanded = key in expandedKeys
        val next = if (currentlyExpanded) expandedKeys - key else expandedKeys + key
        updateExpanded(next)
        if (!currentlyExpanded && treeMode && onLoadChildren != null &&
            childrenOf(row).isEmpty() && loadingKeys[key] != true
        ) {
            loadingKeys[key] = true
            scope.launch {
                val result = onLoadChildren(row)
                if (result != null) loadedChildren[key] = result
                loadingKeys.remove(key)
            }
        }
    }

    fun applyFilters(next: Map<String, Set<String>>) {
        onFilterChange?.invoke(next)
        if (filters == null) internalFilters = next
        if (paginationBehaviorOnFilter == NDataTablePaginationBehavior.First) {
            page = 1
            onPageChange?.invoke(1)
        }
    }

    fun setFilterValues(column: NDataTableColumn, values: Set<String>) {
        val next = LinkedHashMap<String, Set<String>>()
        next.putAll(activeFilters)
        if (values.isEmpty()) next.remove(column.key) else next[column.key] = values
        applyFilters(next)
    }

    fun handleFilter(column: NDataTableColumn, value: String) {
        val current = activeFilters[column.key].orEmpty()
        val nextValues = when {
            !column.filterMultiple -> if (value in current) emptySet() else setOf(value)
            value in current -> current - value
            else -> current + value
        }
        setFilterValues(column, nextValues)
    }

    fun handleSort(column: NDataTableColumn) {
        if (!column.sortable) return
        val nextOrder = column.nextSortOrder?.invoke(activeSorters[column.key]) ?: when (activeSorters[column.key]) {
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

    fun toggleSelection(row: NTableRow) {
        val callback = onCheckedRowKeysChange ?: return
        if (selectable?.invoke(row) == false) return
        val key = rowKey(row)
        when (selectionMode) {
            NTableSelectionMode.None -> Unit
            NTableSelectionMode.Single -> callback(setOf(key))
            NTableSelectionMode.Multiple -> callback(
                if (key in checkedRowKeys) checkedRowKeys - key else checkedRowKeys + key,
            )
        }
    }

    val renderSelectionHeader: @Composable RowScope.(Dp?, Float?) -> Unit = { width, weight ->
        if (selectionMode == NTableSelectionMode.Multiple) {
            NTableHeaderCell(
                metrics = metrics,
                width = width,
                weight = weight,
                align = NTableAlign.Center,
                dividerColor = dividerColor,
            ) {
                NCheckbox(
                    checked = allChecked,
                    onCheckedChange = { checked ->
                        onCheckedRowKeysChange?.invoke(
                            if (checked) {
                                checkedRowKeys + selectionPageKeys
                            } else {
                                checkedRowKeys - selectionPageKeys.toSet()
                            },
                        )
                    },
                    indeterminate = someChecked,
                    size = effectiveSize,
                )
                if (selectionOptions.isNotEmpty()) {
                    Box {
                        val interaction = remember { MutableInteractionSource() }
                        val hovered by interaction.collectIsHoveredAsState()
                        Box(
                            Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(if (hovered) colors.closeHover else Color.Transparent)
                                .hoverable(interaction)
                                .nClickable(interaction) { selectionMenuOpen = !selectionMenuOpen },
                            contentAlignment = Alignment.Center,
                        ) {
                            NIcon(NIcons.ChevronDown, size = 12.dp, color = colors.icon)
                        }
                        NPopoverSurface(
                            visible = selectionMenuOpen,
                            onDismiss = { selectionMenuOpen = false },
                            placement = NPlacement.BottomStart,
                            padding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
                        ) {
                            Column(Modifier.defaultMinSize(minWidth = 170.dp)) {
                                selectionOptions.forEach { option ->
                                    NButton(
                                        text = true,
                                        size = NSize.Small,
                                        onClick = {
                                            onCheckedRowKeysChange?.invoke(
                                                option.onSelect(pageRows, checkedRowKeys),
                                            )
                                            selectionMenuOpen = false
                                        },
                                    ) {
                                        NText(option.label, size = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            NTableHeaderCell(
                metrics = metrics,
                width = width,
                weight = weight,
                align = NTableAlign.Center,
                dividerColor = dividerColor,
            ) {}
        }
    }

    val renderBlankHeader: @Composable RowScope.(Dp?, Float?) -> Unit = { width, weight ->
        NTableHeaderCell(
            metrics = metrics,
            width = width,
            weight = weight,
            dividerColor = dividerColor,
        ) {}
    }

    val renderLeafHeader: @Composable RowScope.(DataColumnSlot, Dp?, Float?) -> Unit = { slot, width, weight ->
        val column = slot.spec.column!!
        DataTableHeaderCell(
            column = column,
            width = width,
            weight = weight,
            metrics = metrics,
            sortOrder = activeSorters[column.key],
            selectedFilters = activeFilters[column.key].orEmpty(),
            filterOpen = openFilterKey == column.key,
            onFilterOpenChange = { open ->
                openFilterKey = if (open) column.key else null
            },
            onSort = { handleSort(column) },
            onFilterToggle = { value -> handleFilter(column, value) },
            onFilterClear = { setFilterValues(column, emptySet()) },
            onFilterValuesChange = { values -> setFilterValues(column, values) },
            dividerColor = dividerColor,
            onWidthChange = { widthValue ->
                widthOverrides = widthOverrides + (column.key to widthValue)
            },
        )
    }

    val renderGroupHeader: @Composable RowScope.(String, Dp?, Float?) -> Unit = { title, width, weight ->
        NTableHeaderCell(
            metrics = metrics,
            width = width,
            weight = weight,
            align = NTableAlign.Center,
            dividerColor = dividerColor,
        ) {
            NText(
                text = title,
                color = colors.text1,
                size = metrics.fontSize,
                strong = true,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }

    val renderSelectionCell: @Composable RowScope.(DataDisplayRow, Dp?, Float?) -> Unit = { display, width, weight ->
        NTableCell(
            metrics = metrics,
            width = width,
            weight = weight,
            align = NTableAlign.Center,
            dividerColor = dividerColor,
        ) {
            NCheckbox(
                checked = display.key in checkedRowKeys,
                onCheckedChange = { toggleSelection(display.row) },
                disabled = selectable?.invoke(display.row) == false,
                size = effectiveSize,
            )
        }
    }

    val renderExpandCell: @Composable RowScope.(DataDisplayRow, Dp?, Float?) -> Unit = { display, width, weight ->
        NTableCell(
            metrics = metrics,
            width = width,
            weight = weight,
            align = NTableAlign.Center,
            dividerColor = dividerColor,
        ) {
            if (display.expandable) {
                ExpandToggle(
                    expanded = display.expanded,
                    loading = display.loading,
                    onClick = { toggleRow(display.row) },
                    custom = renderExpandIcon,
                )
            }
        }
    }

    val firstLeafKey = leafColumns.firstOrNull()?.key
    val renderDataCell: @Composable RowScope.(DataColumnSlot, DataDisplayRow, Dp?, Float?) -> Unit =
        { slot, display, width, weight ->
            val column = slot.spec.column!!
            val style = rowStyle?.invoke(display.row, display.index)
            val textColor = column.textColor ?: style?.textColor ?: colors.text2
            NTableCell(
                metrics = metrics,
                width = width,
                weight = weight,
                align = column.align,
                dividerColor = dividerColor,
            ) {
                val treePrefix = treeMode && column.key == firstLeafKey
                if (treePrefix) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.width((display.depth * 16).dp))
                        if (display.expandable) {
                            ExpandToggle(
                                expanded = display.expanded,
                                loading = display.loading,
                                onClick = { toggleRow(display.row) },
                                custom = renderExpandIcon,
                            )
                            Spacer(Modifier.width(4.dp))
                        }
                        DataCellContent(
                            column = column,
                            row = display.row,
                            index = display.index,
                            metrics = metrics,
                            color = textColor,
                            fallbackRender = renderCell,
                        )
                    }
                } else {
                    DataCellContent(
                        column = column,
                        row = display.row,
                        index = display.index,
                        metrics = metrics,
                        color = textColor,
                        fallbackRender = renderCell,
                    )
                }
            }
        }

    val renderRow: @Composable (DataDisplayRow, Int) -> Unit = { display, displayIndex ->
        val key = display.key
        val checked = key in checkedRowKeys
        val interaction = remember(key) { MutableInteractionSource() }
        val hovered by interaction.collectIsHoveredAsState()
        val style = rowStyle?.invoke(display.row, display.index)
        val selectedBackground = colors.primarySuppl.copy(
            alpha = if (theme.isDark) 0.16f else 0.08f,
        )
        val styleBackground = style?.backgroundColor
        val baseBackground = when {
            checked -> selectedBackground
            styleBackground != null -> styleBackground
            hovered -> colors.tableHover
            striped && displayIndex % 2 == 1 -> colors.tableStriped
            else -> colors.table
        }
        val canSelect = selectionMode != NTableSelectionMode.None &&
            onCheckedRowKeysChange != null &&
            selectable?.invoke(display.row) != false
        val rowClickable = canSelect || onRowClick != null || rowContextMenu != null
        val showBottomBorder = when {
            singleColumn -> displayIndex == displayRows.lastIndex && !bordered && bottomBordered
            displayIndex < displayRows.lastIndex -> true
            else -> !bordered && bottomBordered
        }
        Box(Modifier.fillMaxWidth()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(baseBackground)
                    .then(
                        if (showBottomBorder) Modifier.nBottomBorder(colors.divider) else Modifier,
                    )
                    .hoverable(interaction)
                    .then(
                        if (rowClickable) {
                            Modifier.combinedClickable(
                                interactionSource = interaction,
                                indication = null,
                                onLongClick = if (rowContextMenu != null) {
                                    { contextMenuKey = key }
                                } else {
                                    null
                                },
                                onClick = {
                                    onRowClick?.invoke(display.row, display.index)
                                    if (canSelect) toggleSelection(display.row)
                                },
                            )
                        } else {
                            Modifier
                        },
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (mergeMode) {
                    MergeBodyRow(
                        leaves = layout.center,
                        starts = mergeStarts.getOrElse(displayIndex) { emptyMap() },
                        metrics = metrics,
                        dividerColor = dividerColor,
                        cell = { column, _, cellWidth, cellWeight ->
                            NTableCell(
                                metrics = metrics,
                                width = cellWidth,
                                weight = cellWeight,
                                align = column.align,
                                dividerColor = dividerColor,
                            ) {
                                DataCellContent(
                                    column = column,
                                    row = display.row,
                                    index = display.index,
                                    metrics = metrics,
                                    color = column.textColor ?: style?.textColor ?: colors.text2,
                                    fallbackRender = renderCell,
                                )
                            }
                        },
                    )
                } else {
                    layout.left.forEach { slot ->
                        when {
                            slot.spec.selection -> renderSelectionCell(display, slot.width, slot.weight)
                            slot.spec.expand -> renderExpandCell(display, slot.width, slot.weight)
                            else -> renderDataCell(slot, display, slot.width, slot.weight)
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
                                when {
                                    slot.spec.selection -> renderSelectionCell(display, slot.width, slot.weight)
                                    slot.spec.expand -> renderExpandCell(display, slot.width, slot.weight)
                                    else -> renderDataCell(slot, display, slot.width, slot.weight)
                                }
                            }
                        }
                    }
                    layout.right.forEach { slot ->
                        when {
                            slot.spec.selection -> renderSelectionCell(display, slot.width, slot.weight)
                            slot.spec.expand -> renderExpandCell(display, slot.width, slot.weight)
                            else -> renderDataCell(slot, display, slot.width, slot.weight)
                        }
                    }
                }
            }
            if (rowContextMenu != null && contextMenuKey == key) {
                NPopoverSurface(
                    visible = true,
                    onDismiss = { contextMenuKey = null },
                    placement = NPlacement.BottomStart,
                    padding = PaddingValues(6.dp),
                ) {
                    rowContextMenu(display.row) { contextMenuKey = null }
                }
            }
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
            if (groupMode) {
                for (headerRow in 0 until maxHeaderRows) {
                    HeaderPaneRow(
                        layout = layout,
                        horizontalState = horizontalState,
                        theme = theme,
                        borderBottom = if (headerRow == maxHeaderRows - 1) colors.border else colors.divider,
                    ) { slots ->
                        var index = 0
                        while (index < slots.size) {
                            val slot = slots[index]
                            if (slot.spec.selection) {
                                if (headerRow == maxHeaderRows - 1) {
                                    renderSelectionHeader(slot.width, slot.weight)
                                } else {
                                    renderBlankHeader(slot.width, slot.weight)
                                }
                                index++
                                continue
                            }
                            if (slot.spec.expand) {
                                renderBlankHeader(slot.width, slot.weight)
                                index++
                                continue
                            }
                            val column = slot.spec.column!!
                            val info = leafInfoByKey[column.key]!!
                            val node = info.path.getOrNull(headerRow)
                            if (node == null) {
                                renderBlankHeader(slot.width, slot.weight)
                                index++
                                continue
                            }
                            var end = index + 1
                            while (end < slots.size) {
                                val nextColumn = slots[end].spec.column ?: break
                                val nextNode = leafInfoByKey[nextColumn.key]?.path?.getOrNull(headerRow)
                                if (nextNode === node) end++ else break
                            }
                            val sub = slots.subList(index, end)
                            val (widthValue, weightValue) = combinedSlotWidth(sub)
                            if (node.children.isEmpty()) {
                                renderLeafHeader(slot, widthValue, weightValue)
                            } else {
                                renderGroupHeader(node.title, widthValue, weightValue)
                            }
                            index = end
                        }
                    }
                }
            } else {
                HeaderPaneRow(
                    layout = layout,
                    horizontalState = horizontalState,
                    theme = theme,
                    borderBottom = colors.border,
                ) { slots ->
                    var index = 0
                    while (index < slots.size) {
                        val slot = slots[index]
                        if (slot.spec.selection) {
                            renderSelectionHeader(slot.width, slot.weight)
                            index++
                            continue
                        }
                        if (slot.spec.expand) {
                            renderBlankHeader(slot.width, slot.weight)
                            index++
                            continue
                        }
                        val column = slot.spec.column!!
                        val titleSpan = column.titleColSpan.coerceAtLeast(1)
                        var take = 1
                        while (take < titleSpan && index + take < slots.size &&
                            slots[index + take].spec.column != null
                        ) {
                            take++
                        }
                        val sub = slots.subList(index, index + take)
                        val (widthValue, weightValue) = combinedSlotWidth(sub)
                        renderLeafHeader(slot, widthValue, weightValue)
                        index += take
                    }
                }
            }

            if (displayRows.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(effectiveMaxHeight ?: 160.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    NEmpty(
                        description = emptyDescription ?: LocalNLocale.current.tableEmpty,
                        size = effectiveSize,
                    )
                }
            } else if (effectiveMaxHeight != null) {
                LazyColumn(Modifier.fillMaxWidth().height(effectiveMaxHeight)) {
                    itemsIndexed(displayRows) { rowIndex, display ->
                        renderRow(display, rowIndex)
                        if (!treeMode && display.expanded && renderExpand != null) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .background(colors.tableHover)
                                    .nBottomBorder(colors.divider),
                            ) {
                                NTableCell(
                                    metrics = metrics,
                                    weight = 1f,
                                    dividerColor = null,
                                ) {
                                    renderExpand(display.row, display.index)
                                }
                            }
                        }
                    }
                }
            } else {
                Column(Modifier.fillMaxWidth()) {
                    displayRows.forEachIndexed { rowIndex, display ->
                        renderRow(display, rowIndex)
                        if (!treeMode && display.expanded && renderExpand != null) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .background(colors.tableHover)
                                    .nBottomBorder(colors.divider),
                            ) {
                                NTableCell(
                                    metrics = metrics,
                                    weight = 1f,
                                    dividerColor = null,
                                ) {
                                    renderExpand(display.row, display.index)
                                }
                            }
                        }
                    }
                }
            }

            if (summary != null) {
                val values = summary(pageRows)
                NTableSummaryRow(theme) {
                    layout.left.forEach { slot ->
                        SummaryCell(
                            slot = slot,
                            values = values,
                            metrics = metrics,
                            dividerColor = dividerColor,
                            colors = colors,
                        )
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
                                SummaryCell(
                                    slot = slot,
                                    values = values,
                                    metrics = metrics,
                                    dividerColor = dividerColor,
                                    colors = colors,
                                )
                            }
                        }
                    }
                    layout.right.forEach { slot ->
                        SummaryCell(
                            slot = slot,
                            values = values,
                            metrics = metrics,
                            dividerColor = dividerColor,
                            colors = colors,
                        )
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
private fun DataCellContent(
    column: NDataTableColumn,
    row: NTableRow,
    index: Int,
    metrics: NTableMetrics,
    color: Color,
    fallbackRender: (@Composable (Any?, NTableRow, NDataTableColumn) -> Unit)?,
) {
    val render = column.render
    if (render != null) {
        render(row, index)
        return
    }
    val value = row[column.key]
    if (fallbackRender != null) {
        fallbackRender(value, row, column)
        return
    }
    val text = value?.toString().orEmpty()
    if (column.ellipsisTooltip) {
        NEllipsis(text = text, lineClamp = 1, tooltip = true)
    } else {
        NText(
            text = text,
            color = color,
            size = metrics.fontSize,
            maxLines = if (column.ellipsis) 1 else Int.MAX_VALUE,
            overflow = if (column.ellipsis) TextOverflow.Ellipsis else TextOverflow.Clip,
        )
    }
}

@Composable
private fun ExpandToggle(
    expanded: Boolean,
    loading: Boolean,
    onClick: () -> Unit,
    custom: (@Composable (expanded: Boolean, loading: Boolean) -> Unit)?,
) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Box(
        Modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(if (hovered) theme.colors.closeHover else Color.Transparent)
            .hoverable(interaction)
            .nClickable(interaction, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (loading) {
            NBaseLoading(size = 12.dp, strokeWidth = 1.5.dp)
        } else if (custom != null) {
            custom(expanded, loading)
        } else {
            NIcon(
                icon = if (expanded) NIcons.ChevronDown else NIcons.ChevronRight,
                size = 14.dp,
                color = theme.colors.icon,
            )
        }
    }
}

@Composable
private fun HeaderPaneRow(
    layout: DataTableLayout,
    horizontalState: ScrollState,
    theme: NTheme,
    borderBottom: Color,
    content: @Composable RowScope.(List<DataColumnSlot>) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(theme.colors.tableHeader)
            .nBottomBorder(borderBottom),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content(layout.left)
        Box(Modifier.weight(1f).clipToBounds()) {
            Row(
                if (layout.scrolled) {
                    Modifier.horizontalScroll(horizontalState).width(layout.centerWidth)
                } else {
                    Modifier.fillMaxWidth()
                },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                content(layout.center)
            }
        }
        content(layout.right)
    }
}

@Composable
private fun RowScope.SummaryCell(
    slot: DataColumnSlot,
    values: Map<String, String>,
    metrics: NTableMetrics,
    dividerColor: Color?,
    colors: cn.enaium.naiveui.theme.NColors,
) {
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

@Composable
private fun RowScope.MergeBodyRow(
    leaves: List<DataColumnSlot>,
    starts: Map<Int, MergeStart>,
    metrics: NTableMetrics,
    dividerColor: Color?,
    cell: @Composable (column: NDataTableColumn, index: Int, width: Dp?, weight: Float?) -> Unit,
) {
    val leafCount = leaves.size
    var index = 0
    while (index < leafCount) {
        val start = starts[index]
        if (start != null) {
            val sub = leaves.subList(index, (index + start.colSpan).coerceAtMost(leafCount))
            val (widthValue, weightValue) = combinedSlotWidth(sub)
            cell(leaves[index].spec.column!!, index, widthValue, weightValue)
            index += start.colSpan
        } else {
            NTableCell(
                metrics = metrics,
                width = leaves[index].width,
                weight = leaves[index].weight,
                dividerColor = dividerColor,
            ) {}
            index++
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
    onFilterValuesChange: (Set<String>) -> Unit,
    dividerColor: Color?,
    onWidthChange: (Dp) -> Unit,
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
        if (column.header != null) {
            column.header.invoke()
        } else {
            NText(
                text = column.title,
                color = colors.text1,
                size = metrics.fontSize,
                strong = true,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (column.sortable) {
            Spacer(Modifier.width(4.dp))
            if (column.sorterIcon != null) {
                column.sorterIcon.invoke(sortOrder)
            } else {
                NIcon(
                    icon = if (sortOrder == NDataTableSortOrder.Descend) {
                        NIcons.ArrowDown
                    } else {
                        NIcons.ArrowUp
                    },
                    size = 12.dp,
                    color = if (sortOrder != null) colors.primary else colors.text3,
                )
            }
        }
        if (column.filterOptions.isNotEmpty() || column.filterMenu != null) {
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
                    if (column.filterIcon != null) {
                        column.filterIcon.invoke(selectedFilters.isNotEmpty())
                    } else {
                        NIcon(
                            icon = NIcons.Filter,
                            size = 13.dp,
                            color = if (selectedFilters.isNotEmpty()) colors.primary else colors.icon,
                        )
                    }
                }
                NPopoverSurface(
                    visible = filterOpen,
                    onDismiss = { onFilterOpenChange(false) },
                    placement = NPlacement.BottomEnd,
                    padding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                ) {
                    if (column.filterMenu != null) {
                        column.filterMenu.invoke(
                            selectedFilters,
                            onFilterValuesChange,
                            { onFilterOpenChange(false) },
                        )
                    } else {
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
                                    NText("Clear", size = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
        if (column.resizable) {
            val latestWidth = rememberUpdatedState(width ?: column.width ?: 140.dp)
            Box(
                Modifier
                    .width(8.dp)
                    .height(22.dp)
                    .pointerInput(column.key) {
                        var start = 0f
                        detectHorizontalDragGestures(
                            onDragStart = { start = latestWidth.value.value },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                val next = (start + dragAmount / density).dp
                                val minWidth = column.minWidth ?: 50.dp
                                val maxWidth = column.maxWidth ?: 800.dp
                                onWidthChange(next.coerceIn(minWidth, maxWidth))
                            },
                        )
                    },
            )
        }
    }
}
