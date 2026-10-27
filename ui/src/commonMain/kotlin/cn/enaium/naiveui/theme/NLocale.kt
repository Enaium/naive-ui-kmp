package cn.enaium.naiveui.theme

import androidx.compose.runtime.Immutable

@Immutable
data class NDateLocale(
    val months: List<String> = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December",
    ),
    val monthsShort: List<String> = months.map { it.take(3) },
    val weekdays: List<String> = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"),
    val weekdaysShort: List<String> = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"),
    val am: String = "AM",
    val pm: String = "PM",
    val year: String = "Year",
    val month: String = "Month",
    val day: String = "Day",
    val hour: String = "Hour",
    val minute: String = "Minute",
    val second: String = "Second",
    val now: String = "Now",
    val clear: String = "Clear",
    val confirm: String = "Confirm",
    val selectDate: String = "Select Date",
    val selectTime: String = "Select Time",
    val selectMonth: String = "Select Month",
    val selectYear: String = "Select Year",
    val prevMonth: String = "Previous month",
    val nextMonth: String = "Next month",
    val prevYear: String = "Previous year",
    val nextYear: String = "Next year",
    val startDate: String = "Start Date",
    val endDate: String = "End Date",
    val startTime: String = "Start Time",
    val endTime: String = "End Time",
    /** ISO weekday: 1 = Monday, 7 = Sunday. */
    val firstDayOfWeek: Int = 1,
) {
    fun weekdayHeaders(firstDayOfWeek: Int = this.firstDayOfWeek): List<String> {
        val zeroBased = (firstDayOfWeek - 1).coerceIn(0, 6)
        return List(7) { weekdaysShort[(zeroBased + it) % 7] }
    }
}

@Immutable
data class NGlobalLocale(
    val undo: String = "Undo",
    val redo: String = "Redo",
    val confirm: String = "Confirm",
    val clear: String = "Clear",
)

@Immutable
data class NPopconfirmLocale(
    val positiveText: String = "Confirm",
    val negativeText: String = "Cancel",
)

@Immutable
data class NCascaderLocale(
    val placeholder: String = "Please Select",
    val loading: String = "Loading",
    val loadingRequiredMessage: (String) -> String = { label ->
        "Please load all $label's descendants before checking it."
    },
)

@Immutable
data class NTimeLocale(
    val dateFormat: String = "yyyy-MM-dd",
    val dateTimeFormat: String = "yyyy-MM-dd HH:mm:ss",
)

@Immutable
data class NDatePickerLocale(
    val yearFormat: String = "yyyy",
    val monthFormat: String = "MMM",
    val dayFormat: String = "eeeeee",
    val yearTypeFormat: String = "yyyy",
    val monthTypeFormat: String = "yyyy-MM",
    val dateFormat: String = "yyyy-MM-dd",
    val dateTimeFormat: String = "yyyy-MM-dd HH:mm:ss",
    val quarterFormat: String = "yyyy-qqq",
    val weekFormat: String = "YYYY-w",
    val clear: String = "Clear",
    val now: String = "Now",
    val confirm: String = "Confirm",
    val selectTime: String = "Select Time",
    val selectDate: String = "Select Date",
    val datePlaceholder: String = "Select Date",
    val datetimePlaceholder: String = "Select Date and Time",
    val monthPlaceholder: String = "Select Month",
    val yearPlaceholder: String = "Select Year",
    val quarterPlaceholder: String = "Select Quarter",
    val weekPlaceholder: String = "Select Week",
    val startDatePlaceholder: String = "Start Date",
    val endDatePlaceholder: String = "End Date",
    val startDatetimePlaceholder: String = "Start Date and Time",
    val endDatetimePlaceholder: String = "End Date and Time",
    val startMonthPlaceholder: String = "Start Month",
    val endMonthPlaceholder: String = "End Month",
    val monthBeforeYear: Boolean = true,
    val firstDayOfWeek: Int = 6,
    val today: String = "Today",
)

@Immutable
data class NDataTableLocale(
    val checkTableAll: String = "Select all in the table",
    val uncheckTableAll: String = "Unselect all in the table",
    val confirm: String = "Confirm",
    val clear: String = "Clear",
)

@Immutable
data class NLegacyTransferLocale(
    val sourceTitle: String = "Source",
    val targetTitle: String = "Target",
)

@Immutable
data class NTransferLocale(
    val selectAll: String = "Select all",
    val unselectAll: String = "Unselect all",
    val clearAll: String = "Clear",
    val total: (Int) -> String = { num -> "Total $num items" },
    val selected: (Int) -> String = { num -> "$num items selected" },
)

@Immutable
data class NEmptyLocale(val description: String = "No Data")

@Immutable
data class NSelectLocale(val placeholder: String = "Please Select")

@Immutable
data class NTimePickerLocale(
    val placeholder: String = "Select Time",
    val positiveText: String = "OK",
    val negativeText: String = "Cancel",
    val now: String = "Now",
    val clear: String = "Clear",
)

@Immutable
data class NPaginationLocale(
    val goto: String = "Goto",
    val selectionSuffix: String = "page",
)

@Immutable
data class NDynamicTagsLocale(val add: String = "Add")

@Immutable
data class NLogLocale(val loading: String = "Loading")

@Immutable
data class NInputLocale(val placeholder: String = "Please Input")

@Immutable
data class NInputNumberLocale(val placeholder: String = "Please Input")

@Immutable
data class NDynamicInputLocale(val create: String = "Create")

@Immutable
data class NThemeEditorLocale(
    val title: String = "Theme Editor",
    val clearAllVars: String = "Clear All Variables",
    val clearSearch: String = "Clear Search",
    val filterCompName: String = "Filter Component Name",
    val filterVarName: String = "Filter Variable Name",
    val import: String = "Import",
    val export: String = "Export",
    val restore: String = "Reset to Default",
)

@Immutable
data class NImageLocale(
    val tipPrevious: String = "Previous picture (←)",
    val tipNext: String = "Next picture (→)",
    val tipCounterclockwise: String = "Counterclockwise",
    val tipClockwise: String = "Clockwise",
    val tipZoomOut: String = "Zoom out",
    val tipZoomIn: String = "Zoom in",
    val tipDownload: String = "Download",
    val tipClose: String = "Close (Esc)",
    val tipOriginalSize: String = "Zoom to original size",
)

@Immutable
data class NHeatmapLocale(
    val less: String = "less",
    val more: String = "more",
    val monthFormat: String = "MMM",
    val weekdayFormat: String = "eee",
)

/** Strings used by the KMP port that do not exist in naive-ui's locale files. */
@Immutable
data class NExtraLocale(
    val search: String = "Search",
    val select: String = "Select",
    val selected: String = "Selected",
    val previous: String = "Previous",
    val next: String = "Next",
    val totalLabel: String = "Total",
    val selectedItems: String = "item(s) selected",
    val optional: String = "Optional",
    val required: String = "Required",
    val imageError: String = "Load failed",
    val imageLoading: String = "Loading",
    val close: String = "Close",
    val daysSuffix: String = "d",
    val hoursSuffix: String = "h",
    val minutesSuffix: String = "m",
    val secondsSuffix: String = "s",
    val startTime: String = "Start Time",
    val endTime: String = "End Time",
    val year: String = "Year",
    val month: String = "Month",
    val day: String = "Day",
    val hour: String = "Hour",
    val minute: String = "Minute",
    val second: String = "Second",
    val prevMonth: String = "Previous month",
    val nextMonth: String = "Next month",
    val prevYear: String = "Previous year",
    val nextYear: String = "Next year",
)

/** Upload strings; naive-ui keeps these in component code rather than locales. */
@Immutable
data class NUploadLocale(
    val browse: String = "Select File",
    val drag: String = "Click or Drag file to this area to upload",
    val pending: String = "Pending",
    val uploading: String = "Uploading",
    val finished: String = "Finished",
    val failed: String = "Failed",
    val remove: String = "Remove file",
    val retry: String = "Retry",
    val countLimit: String = "Maximum number of files exceeded",
)

/**
 * Complete naive-ui locale surface plus the extra strings used by this port.
 * Every locale is available from [NLocales].
 */
@Immutable
data class NLocale(
    val name: String = "en-US",
    val displayName: String = "English",
    val global: NGlobalLocale = NGlobalLocale(),
    val popconfirm: NPopconfirmLocale = NPopconfirmLocale(),
    val cascader: NCascaderLocale = NCascaderLocale(),
    val time: NTimeLocale = NTimeLocale(),
    val datePicker: NDatePickerLocale = NDatePickerLocale(),
    val dataTable: NDataTableLocale = NDataTableLocale(),
    val legacyTransfer: NLegacyTransferLocale = NLegacyTransferLocale(),
    val transfer: NTransferLocale = NTransferLocale(),
    val empty: NEmptyLocale = NEmptyLocale(),
    val select: NSelectLocale = NSelectLocale(),
    val timePicker: NTimePickerLocale = NTimePickerLocale(),
    val pagination: NPaginationLocale = NPaginationLocale(),
    val dynamicTags: NDynamicTagsLocale = NDynamicTagsLocale(),
    val log: NLogLocale = NLogLocale(),
    val input: NInputLocale = NInputLocale(),
    val inputNumber: NInputNumberLocale = NInputNumberLocale(),
    val dynamicInput: NDynamicInputLocale = NDynamicInputLocale(),
    val themeEditor: NThemeEditorLocale = NThemeEditorLocale(),
    val image: NImageLocale = NImageLocale(),
    val heatmap: NHeatmapLocale = NHeatmapLocale(),
    val upload: NUploadLocale = NUploadLocale(),
    val extra: NExtraLocale = NExtraLocale(),
    val date: NDateLocale = NDateLocale(),
) {
    val isRtl: Boolean
        get() = name.substringBefore('-').lowercase() in setOf("ar", "he", "fa", "ur", "ug", "yi")

    // Flat compatibility aliases used across the component implementations.
    val ok: String get() = timePicker.positiveText
    val cancel: String get() = timePicker.negativeText
    val confirm: String get() = global.confirm
    val clear: String get() = global.clear
    val close: String get() = extra.close
    val loading: String get() = cascader.loading
    val noData: String get() = empty.description
    val placeholder: String get() = select.placeholder
    val search: String get() = extra.search
    val selectLabel: String get() = extra.select
    val selected: String get() = extra.selected
    val itemsPerPage: String get() = pagination.selectionSuffix
    val jumpTo: String get() = pagination.goto
    val page: String get() = pagination.selectionSuffix
    val go: String get() = global.confirm
    val prev: String get() = extra.previous
    val next: String get() = extra.next
    val total: String get() = extra.totalLabel
    val emptyDescription: String get() = empty.description
    val sourceTitle: String get() = legacyTransfer.sourceTitle
    val targetTitle: String get() = legacyTransfer.targetTitle
    val selectAll: String get() = transfer.selectAll
    val unselectAll: String get() = transfer.unselectAll
    val selectedItems: String get() = extra.selectedItems
    val uploadBrowse: String get() = upload.browse
    val uploadDrag: String get() = upload.drag
    val uploadPending: String get() = upload.pending
    val uploadUploading: String get() = upload.uploading
    val uploadFinished: String get() = upload.finished
    val uploadFailed: String get() = upload.failed
    val uploadRemove: String get() = upload.remove
    val uploadRetry: String get() = upload.retry
    val uploadCountLimit: String get() = upload.countLimit
    val tableEmpty: String get() = empty.description
    val checkAll: String get() = dataTable.checkTableAll
    val uncheckAll: String get() = dataTable.uncheckTableAll
    val formOptional: String get() = extra.optional
    val formRequired: String get() = extra.required
    val imageError: String get() = extra.imageError
    val imageLoading: String get() = extra.imageLoading
    val cascadePlaceholder: String get() = cascader.placeholder
    val cascadeNoData: String get() = empty.description
    val mentionNoData: String get() = empty.description
    val popconfirmYes: String get() = popconfirm.positiveText
    val popconfirmNo: String get() = popconfirm.negativeText
    val popselectNoData: String get() = empty.description
    val treeEmpty: String get() = empty.description
    val daysSuffix: String get() = extra.daysSuffix
    val hoursSuffix: String get() = extra.hoursSuffix
    val minutesSuffix: String get() = extra.minutesSuffix
    val secondsSuffix: String get() = extra.secondsSuffix

    companion object {
        val English: NLocale get() = NLocales.EnUS
        val Chinese: NLocale get() = NLocales.ZhCN
    }
}
