package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.runtime.Composable
import cn.enaium.naiveui.sample.gallery.DocsPage

/**
 * Mirrors the 40 upstream `naive-ui` data-table demos, in the same order:
 * basic, empty, border, size, row-props, merge-cell, filter-and-sorter,
 * pagination-behavior-on-filter, multiple-sorter, custom-sorter,
 * column-draggable, select, select-single, custom-select, group-header,
 * controlled-page, controlled-filter, controlled-sorter,
 * controlled-multiple-sorter, fixed-header, fixed-header-column, summary,
 * ellipsis, ellipsis-tooltip, expand, render-header, custom-style, ajax-usage,
 * virtual, virtual-x, custom-filter-menu, tree, flex-height, striped,
 * simple-editable, switchable-editable, context-menu, async-expand,
 * render-cell, export-csv.
 */
@Composable
fun DataTableDocs() {
    DocsPage(
        title = "Data Table",
        description = "A data-driven table with sorting, filtering, selection, expandable and tree rows, " +
            "grouped and merged headers, pinned columns, editing, pagination and virtual rendering.",
    ) {
        DtDemoBasic() // basic
        DtDemoEmpty() // empty
        DtDemoBorder() // border
        DtDemoSize() // size
        DtDemoRowProps() // row-props
        DtDemoMergeCell() // merge-cell
        DtDemoFilterAndSorter() // filter-and-sorter
        DtDemoPaginationBehavior() // pagination-behavior-on-filter
        DtDemoMultipleSorter() // multiple-sorter
        DtDemoCustomSorter() // custom-sorter
        DtDemoColumnDraggable() // column-draggable
        DtDemoSelect() // select
        DtDemoSelectSingle() // select-single
        DtDemoCustomSelect() // custom-select
        DtDemoGroupHeader() // group-header
        DtDemoControlledPage() // controlled-page
        DtDemoControlledFilter() // controlled-filter
        DtDemoControlledSorter() // controlled-sorter
        DtDemoControlledMultipleSorter() // controlled-multiple-sorter
        DtDemoFixedHeader() // fixed-header
        DtDemoFixedHeaderColumn() // fixed-header-column
        DtDemoSummary() // summary
        DtDemoEllipsis() // ellipsis
        DtDemoEllipsisTooltip() // ellipsis-tooltip
        DtDemoExpand() // expand
        DtDemoRenderHeader() // render-header
        DtDemoCustomStyle() // custom-style
        DtDemoAjaxUsage() // ajax-usage
        DtDemoVirtual() // virtual
        DtDemoVirtualX() // virtual-x
        DtDemoCustomFilterMenu() // custom-filter-menu
        DtDemoTree() // tree
        DtDemoFlexHeight() // flex-height
        DtDemoStriped() // striped
        DtDemoSimpleEditable() // simple-editable
        DtDemoSwitchableEditable() // switchable-editable
        DtDemoContextMenu() // context-menu
        DtDemoAsyncExpand() // async-expand
        DtDemoRenderCell() // render-cell
        DtDemoExportCsv() // export-csv
    }
}
