package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NClearButton
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NPopoverSurface
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NDateLocale
import cn.enaium.naiveui.theme.NLocale
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import cn.enaium.naiveui.theme.nSize
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/** Value granularity of [NDatePicker]. */
enum class NDatePickerType {
    Date,
    Datetime,
    Month,
    Year,
    Week,
    Quarter,
    MonthRange,
    YearRange,
    QuarterRange,
    DatetimeRange,
}

/** Footer actions supported by the date pickers. */
enum class NDatePickerAction { Now, Clear, Cancel, Confirm }

private enum class NDatePanelView { Day, Month, Quarter, Year }

/** Imperative handle for focusing and blurring a date picker. */
class NDatePickerController internal constructor() {
    internal var request by mutableStateOf<Boolean?>(null)

    fun focus() {
        request = true
    }

    fun blur() {
        request = false
    }
}

@Composable
fun rememberNDatePickerController(): NDatePickerController = remember { NDatePickerController() }

private fun nDatePad(value: Int, width: Int = 2): String =
    value.toString().padStart(width, '0')

internal fun nDateMonthDays(year: Int, month: Int): Int {
    val first = LocalDate(year, month, 1)
    return first.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY).day
}


private fun nDateResolveFirstDayOfWeek(locale: NDateLocale, value: Int?): Int {
    val raw = value ?: locale.firstDayOfWeek
    return if (raw == 0) 7 else raw.coerceIn(1, 7)
}

internal fun nDateGridStart(year: Int, month: Int, firstDayOfWeek: Int): LocalDate {
    val first = LocalDate(year, month, 1)
    val offset = (first.dayOfWeek.isoDayNumber - firstDayOfWeek.coerceIn(1, 7) + 7) % 7
    return first.minus(offset, DateTimeUnit.DAY)
}

internal fun nDateGridCellCount(year: Int, month: Int, firstDayOfWeek: Int): Int = 42

private fun nDateWeekStart(date: LocalDate, firstDayOfWeek: Int): LocalDate {
    val offset = (date.dayOfWeek.isoDayNumber - firstDayOfWeek.coerceIn(1, 7) + 7) % 7
    return date.minus(offset, DateTimeUnit.DAY)
}

private fun nDateQuarter(date: LocalDate): Int = (date.month.number - 1) / 3 + 1

private fun nDateNormalize(
    date: LocalDate,
    type: NDatePickerType,
    firstDayOfWeek: Int,
): LocalDate = when (type) {
    NDatePickerType.Month, NDatePickerType.MonthRange -> LocalDate(date.year, date.month.number, 1)
    NDatePickerType.Year, NDatePickerType.YearRange -> LocalDate(date.year, 1, 1)
    NDatePickerType.Quarter, NDatePickerType.QuarterRange -> {
        val quarter = nDateQuarter(date)
        LocalDate(date.year, (quarter - 1) * 3 + 1, 1)
    }
    NDatePickerType.Week -> nDateWeekStart(date, firstDayOfWeek)
    else -> date
}

private fun nDateDefaultFormat(type: NDatePickerType): String = when (type) {
    NDatePickerType.Month, NDatePickerType.MonthRange -> "yyyy-MM"
    NDatePickerType.Year, NDatePickerType.YearRange -> "yyyy"
    NDatePickerType.Quarter, NDatePickerType.QuarterRange -> "yyyy-Q"
    NDatePickerType.Datetime, NDatePickerType.DatetimeRange -> "yyyy-MM-dd HH:mm:ss"
    else -> "yyyy-MM-dd"
}

private fun nDateDefaultPlaceholder(type: NDatePickerType, locale: NLocale): String = when (type) {
    NDatePickerType.Month, NDatePickerType.MonthRange -> locale.date.selectMonth
    NDatePickerType.Year, NDatePickerType.YearRange -> locale.date.selectYear
    NDatePickerType.Datetime, NDatePickerType.DatetimeRange ->
        "${locale.date.selectDate} & ${locale.date.selectTime}"
    else -> locale.date.selectDate
}

private fun nDateDefaultActions(type: NDatePickerType): Set<NDatePickerAction> = when (type) {
    NDatePickerType.Datetime ->
        setOf(NDatePickerAction.Clear, NDatePickerAction.Now, NDatePickerAction.Confirm)
    NDatePickerType.DatetimeRange ->
        setOf(NDatePickerAction.Clear, NDatePickerAction.Confirm)
    NDatePickerType.MonthRange, NDatePickerType.YearRange, NDatePickerType.QuarterRange ->
        setOf(NDatePickerAction.Cancel, NDatePickerAction.Confirm)
    else -> setOf(NDatePickerAction.Now)
}

private fun nDatePanelInitialView(type: NDatePickerType): NDatePanelView = when (type) {
    NDatePickerType.Month, NDatePickerType.MonthRange -> NDatePanelView.Month
    NDatePickerType.Year, NDatePickerType.YearRange -> NDatePanelView.Year
    NDatePickerType.Quarter, NDatePickerType.QuarterRange -> NDatePanelView.Quarter
    else -> NDatePanelView.Day
}

/**
 * Formats [date] and/or [time] with the small subset of date-fns tokens used by
 * naive-ui's pickers: `yyyy`, `yy`, `MMMM`, `MMM`, `MM`, `M`, `dd`, `d`, `EEEE`,
 * `EEE`, `HH`, `H`, `hh`, `h`, `mm`, `m`, `ss`, `s`, `a` and `Q`.
 */
internal fun nDateFormat(
    date: LocalDate?,
    time: LocalTime?,
    pattern: String,
    locale: NDateLocale,
): String {
    val out = StringBuilder()
    var index = 0
    while (index < pattern.length) {
        val char = pattern[index]
        var length = 1
        while (index + length < pattern.length && pattern[index + length] == char) length++
        when (char) {
            'y' -> if (date != null) {
                val year = date.year
                when {
                    length >= 4 -> out.append(year.toString().padStart(4, '0'))
                    length == 2 -> out.append(nDatePad(((year % 100) + 100) % 100))
                    else -> out.append(year)
                }
            }
            'M' -> if (date != null) {
                val month = date.month.number
                when {
                    length >= 4 -> out.append(locale.months[month - 1])
                    length == 3 -> out.append(locale.monthsShort[month - 1])
                    length == 2 -> out.append(nDatePad(month))
                    else -> out.append(month)
                }
            }
            'd' -> if (date != null) {
                out.append(if (length >= 2) nDatePad(date.day) else date.day.toString())
            }
            'E' -> if (date != null) {
                val weekday = date.dayOfWeek.ordinal
                out.append(if (length >= 4) locale.weekdays[weekday] else locale.weekdaysShort[weekday])
            }
            'Q' -> if (date != null) {
                val quarter = nDateQuarter(date)
                out.append(if (length >= 2) nDatePad(quarter) else quarter)
            }
            'H' -> if (time != null) {
                out.append(if (length >= 2) nDatePad(time.hour) else time.hour.toString())
            }
            'h' -> if (time != null) {
                val hour = time.hour % 12
                val display = if (hour == 0) 12 else hour
                out.append(if (length >= 2) nDatePad(display) else display.toString())
            }
            'm' -> if (time != null) {
                out.append(if (length >= 2) nDatePad(time.minute) else time.minute.toString())
            }
            's' -> if (time != null) {
                out.append(if (length >= 2) nDatePad(time.second) else time.second.toString())
            }
            'a' -> if (time != null) {
                val marker = if (time.hour >= 12) locale.pm else locale.am
                out.append(if (length >= 3) marker.uppercase() else marker.lowercase())
            }
            else -> out.append(char)
        }
        index += length
    }
    return out.toString()
}

private fun nDateFormatValue(
    date: LocalDate,
    pattern: String,
    locale: NDateLocale,
): String = nDateFormat(date, null, pattern, locale)

/** Input-shaped trigger shared by the date and time pickers. */
@Composable
internal fun NDateInputTrigger(
    text: String?,
    placeholder: String,
    disabled: Boolean,
    size: NSize,
    clearable: Boolean,
    onClick: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    status: NStatus? = null,
    prefix: (@Composable () -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val resolvedSize = nSize(size)
    val height = theme.heightFor(resolvedSize)
    val fontSize = theme.fontSizeFor(resolvedSize)
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val statusColor = status?.let { theme.colors.statusColor(it) }
    val borderColor = when {
        disabled -> theme.colors.border
        statusColor != null -> statusColor
        hovered -> theme.colors.primaryHover
        else -> theme.colors.border
    }
    Row(
        modifier
            .height(height)
            .clip(RoundedCornerShape(theme.borderRadius))
            .background(if (disabled) theme.colors.inputDisabled else theme.colors.input)
            .nBorder(borderColor, 1.dp, theme.borderRadius)
            .hoverable(interaction, enabled = !disabled)
            .nClickable(interaction, enabled = !disabled, onClick = onClick)
            .padding(start = 12.dp, end = if (clearable) 6.dp else 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (prefix != null) {
            Box(Modifier.size(18.dp), contentAlignment = Alignment.Center) { prefix() }
            Spacer(Modifier.width(6.dp))
        }
        NText(
            text = text ?: placeholder,
            size = fontSize,
            color = when {
                disabled -> theme.colors.textDisabled
                text == null -> theme.colors.placeholder
                else -> theme.colors.text2
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (clearable) {
            if (text != null && !disabled) {
                NClearButton(onClick = onClear, size = 18.dp, iconSize = 12.dp)
            } else {
                Spacer(Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun NDateRangeTrigger(
    startText: String?,
    endText: String?,
    startPlaceholder: String,
    endPlaceholder: String,
    disabled: Boolean,
    size: NSize,
    clearable: Boolean,
    onClick: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    status: NStatus? = null,
    prefix: (@Composable () -> Unit)? = null,
    separator: (@Composable () -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val resolvedSize = nSize(size)
    val height = theme.heightFor(resolvedSize)
    val fontSize = theme.fontSizeFor(resolvedSize)
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val statusColor = status?.let { theme.colors.statusColor(it) }
    Row(
        modifier
            .height(height)
            .clip(RoundedCornerShape(theme.borderRadius))
            .background(if (disabled) theme.colors.inputDisabled else theme.colors.input)
            .nBorder(
                color = when {
                    disabled -> theme.colors.border
                    statusColor != null -> statusColor
                    hovered -> theme.colors.primaryHover
                    else -> theme.colors.border
                },
                width = 1.dp,
                radius = theme.borderRadius,
            )
            .hoverable(interaction, enabled = !disabled)
            .nClickable(interaction, enabled = !disabled, onClick = onClick)
            .padding(start = 12.dp, end = if (clearable) 6.dp else 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (prefix != null) {
            Box(Modifier.size(18.dp), contentAlignment = Alignment.Center) { prefix() }
            Spacer(Modifier.width(6.dp))
        }
        NText(
            text = startText ?: startPlaceholder,
            modifier = Modifier.widthIn(min = 88.dp),
            size = fontSize,
            color = when {
                disabled -> theme.colors.textDisabled
                startText == null -> theme.colors.placeholder
                else -> theme.colors.text2
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (separator != null) {
            Box(Modifier.padding(horizontal = 6.dp), contentAlignment = Alignment.Center) { separator() }
        } else {
            NIcon(
                NIcons.To,
                modifier = Modifier.padding(horizontal = 6.dp),
                size = 14.dp,
                color = theme.colors.text3,
            )
        }
        NText(
            text = endText ?: endPlaceholder,
            modifier = Modifier.widthIn(min = 88.dp),
            size = fontSize,
            color = when {
                disabled -> theme.colors.textDisabled
                endText == null -> theme.colors.placeholder
                else -> theme.colors.text2
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (clearable) {
            if ((startText != null || endText != null) && !disabled) {
                NClearButton(onClick = onClear, size = 18.dp, iconSize = 12.dp)
            } else {
                Spacer(Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun NPanelNavButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
) {
    NPanelNavButton(icon, description, onClick, null)
}

@Composable
private fun NPanelNavButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    slot: (@Composable () -> Unit)?,
) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Box(
        Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(theme.borderRadiusSmall))
            .background(if (hovered) theme.colors.hover else Color.Transparent)
            .hoverable(interaction)
            .nClickable(interaction, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (slot != null) slot() else NIcon(icon, size = 14.dp, color = theme.colors.icon, contentDescription = description)
    }
}

@Composable
private fun NPanelLabel(text: String, onClick: () -> Unit) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Box(
        Modifier
            .clip(RoundedCornerShape(theme.borderRadiusSmall))
            .background(if (hovered) theme.colors.hover else Color.Transparent)
            .hoverable(interaction)
            .nClickable(interaction, onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp),
    ) {
        NText(text, size = 14.sp, strong = true, color = theme.colors.text2)
    }
}

@Composable
private fun NDayCell(
    date: LocalDate,
    inMonth: Boolean,
    endpoint: Boolean,
    inRange: Boolean,
    isToday: Boolean,
    disabled: Boolean,
    onClick: () -> Unit,
) {
    val theme = LocalNTheme.current
    val interaction = remember(date) { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val shape = RoundedCornerShape(theme.borderRadius)
    val background = when {
        endpoint -> theme.colors.primary
        inRange -> theme.colors.primary.copy(alpha = if (theme.isDark) 0.28f else 0.12f)
        hovered && !disabled -> theme.colors.hover
        else -> Color.Transparent
    }
    val textColor = when {
        endpoint -> theme.colors.base
        disabled -> theme.colors.textDisabled
        !inMonth -> theme.colors.text3
        else -> theme.colors.text2
    }
    Box(
        Modifier
            .padding(1.dp)
            .fillMaxWidth()
            .height(32.dp)
            .clip(shape)
            .background(background)
            .then(
                if (isToday && !endpoint) {
                    Modifier.nBorder(theme.colors.primary, 1.dp, theme.borderRadius)
                } else {
                    Modifier
                },
            )
            .hoverable(interaction, enabled = !disabled)
            .nClickable(interaction, enabled = !disabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        NText(date.day.toString(), size = 13.sp, color = textColor, maxLines = 1)
    }
}

@Composable
private fun NChoiceCell(
    label: String,
    selected: Boolean,
    disabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalNTheme.current
    val interaction = remember(label) { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Box(
        modifier
            .padding(2.dp)
            .height(42.dp)
            .clip(RoundedCornerShape(theme.borderRadius))
            .background(
                when {
                    selected -> theme.colors.primary
                    hovered && !disabled -> theme.colors.hover
                    else -> Color.Transparent
                },
            )
            .hoverable(interaction, enabled = !disabled)
            .nClickable(interaction, enabled = !disabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        NText(
            label,
            size = 13.sp,
            color = when {
                selected -> theme.colors.base
                disabled -> theme.colors.textDisabled
                else -> theme.colors.text2
            },
            maxLines = 1,
        )
    }
}

/**
 * One month/quarter/year panel used by the date pickers. Navigation between
 * day, month, quarter and year views is internal so each range panel can
 * navigate independently.
 */
@Composable
private fun NDateCalendarPanel(
    year: Int,
    month: Int,
    type: NDatePickerType,
    selected: LocalDate?,
    rangeStart: LocalDate?,
    rangeEnd: LocalDate?,
    firstDayOfWeek: Int,
    today: LocalDate,
    isDateDisabled: (LocalDate) -> Boolean,
    isMonthDisabled: (Int, Int) -> Boolean,
    isYearDisabled: (Int) -> Boolean,
    isQuarterDisabled: (Int, Int) -> Boolean,
    onYearChange: (Int) -> Unit,
    onMonthChange: (Int) -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onSelectMonth: (Int) -> Unit,
    onSelectYear: (Int) -> Unit,
    onSelectQuarter: (Int) -> Unit,
    monthFormat: String = "MMM",
    yearFormat: String = "yyyy",
    prevYear: (@Composable () -> Unit)? = null,
    prevMonth: (@Composable () -> Unit)? = null,
    nextMonth: (@Composable () -> Unit)? = null,
    nextYear: (@Composable () -> Unit)? = null,
    fastYearSelect: Boolean = false,
    fastMonthSelect: Boolean = false,
    onFastSelectClose: () -> Unit = {},
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    var view by remember(type) { mutableStateOf(nDatePanelInitialView(type)) }
    val disabledPredicate = remember(isDateDisabled) { isDateDisabled }

    fun previousMonth() {
        if (month == 1) {
            onMonthChange(12)
            onYearChange(year - 1)
        } else {
            onMonthChange(month - 1)
        }
    }

    fun nextMonth() {
        if (month == 12) {
            onMonthChange(1)
            onYearChange(year + 1)
        } else {
            onMonthChange(month + 1)
        }
    }

    Column(Modifier.width(280.dp).padding(horizontal = 2.dp)) {
        Row(
            Modifier.fillMaxWidth().height(34.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when (view) {
                NDatePanelView.Day -> {
                    NPanelNavButton(NIcons.ChevronLeft, locale.date.prevYear, { onYearChange(year - 1) }, prevYear)
                    NPanelNavButton(NIcons.ChevronLeft, locale.date.prevMonth, { previousMonth() }, prevMonth)
                    Row(
                        Modifier.weight(1f),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        NPanelLabel(locale.date.months[month - 1]) { view = NDatePanelView.Month }
                        Spacer(Modifier.width(2.dp))
                        NPanelLabel(year.toString()) { view = NDatePanelView.Year }
                    }
                    NPanelNavButton(NIcons.ChevronRight, locale.date.nextMonth, { nextMonth() }, nextMonth)
                    NPanelNavButton(NIcons.ChevronRight, locale.date.nextYear, { onYearChange(year + 1) }, nextYear)
                }
                NDatePanelView.Month -> {
                    NPanelNavButton(NIcons.ChevronLeft, locale.date.prevYear) { onYearChange(year - 1) }
                    Row(Modifier.weight(1f), horizontalArrangement = Arrangement.Center) {
                        NPanelLabel(year.toString()) { view = NDatePanelView.Year }
                    }
                    NPanelNavButton(NIcons.ChevronRight, locale.date.nextYear) { onYearChange(year + 1) }
                }
                NDatePanelView.Quarter -> {
                    NPanelNavButton(NIcons.ChevronLeft, locale.date.prevYear) { onYearChange(year - 1) }
                    Row(Modifier.weight(1f), horizontalArrangement = Arrangement.Center) {
                        NPanelLabel(year.toString()) { view = NDatePanelView.Year }
                    }
                    NPanelNavButton(NIcons.ChevronRight, locale.date.nextYear) { onYearChange(year + 1) }
                }
                NDatePanelView.Year -> {
                    val decadeStart = year - ((year % 10 + 10) % 10) - 1
                    NPanelNavButton(NIcons.ChevronLeft, locale.date.prevYear) { onYearChange(year - 12) }
                    Row(Modifier.weight(1f), horizontalArrangement = Arrangement.Center) {
                        NText(
                            "$decadeStart - ${decadeStart + 11}",
                            size = 14.sp,
                            strong = true,
                            color = theme.colors.text2,
                        )
                    }
                    NPanelNavButton(NIcons.ChevronRight, locale.date.nextYear) { onYearChange(year + 12) }
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        when (view) {
            NDatePanelView.Day -> {
                Row(Modifier.fillMaxWidth()) {
                    locale.date.weekdayHeaders(firstDayOfWeek).forEach { label ->
                        Box(Modifier.weight(1f).height(26.dp), contentAlignment = Alignment.Center) {
                            NText(label, size = 12.sp, color = theme.colors.text3, maxLines = 1)
                        }
                    }
                }
                val cellCount = nDateGridCellCount(year, month, firstDayOfWeek)
                val gridStart = nDateGridStart(year, month, firstDayOfWeek)
                for (row in 0 until cellCount / 7) {
                    Row(Modifier.fillMaxWidth()) {
                        for (column in 0 until 7) {
                            val date = gridStart.plus(row * 7 + column, DateTimeUnit.DAY)
                            val isEndpoint = selected == date || rangeStart == date || rangeEnd == date
                            val inRange = rangeStart != null && rangeEnd != null &&
                                date > rangeStart && date < rangeEnd
                            Box(Modifier.weight(1f)) {
                                NDayCell(
                                    date = date,
                                    inMonth = date.year == year && date.month.number == month,
                                    endpoint = isEndpoint,
                                    inRange = inRange,
                                    isToday = date == today,
                                    disabled = disabledPredicate(date),
                                    onClick = { onSelectDate(date) },
                                )
                            }
                        }
                    }
                }
            }
            NDatePanelView.Month -> {
                val selectedMonth = selected?.takeIf { it.year == year }?.month?.number
                val rangeStartMonth = rangeStart?.takeIf { it.year == year }?.month?.number
                val rangeEndMonth = rangeEnd?.takeIf { it.year == year }?.month?.number
                for (row in 0 until 4) {
                    Row(Modifier.fillMaxWidth()) {
                        for (column in 0 until 3) {
                            val value = row * 3 + column + 1
                            NChoiceCell(
                                label = nDateFormatValue(LocalDate(year, value, 1), monthFormat, locale.date),
                                selected = selectedMonth == value || rangeStartMonth == value || rangeEndMonth == value,
                                disabled = isMonthDisabled(year, value),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    when (type) {
                                        NDatePickerType.Month, NDatePickerType.MonthRange -> onSelectMonth(value)
                                        NDatePickerType.Quarter, NDatePickerType.QuarterRange -> {
                                            onMonthChange(value)
                                            view = NDatePanelView.Quarter
                                        }
                                        else -> {
                                            onMonthChange(value)
                                            if (fastMonthSelect) onFastSelectClose() else view = NDatePanelView.Day
                                        }
                                    }
                                },
                            )
                        }
                    }
                }
            }
            NDatePanelView.Quarter -> {
                val selectedQuarter = selected?.takeIf { it.year == year }?.let { nDateQuarter(it) }
                val startQuarter = rangeStart?.takeIf { it.year == year }?.let { nDateQuarter(it) }
                val endQuarter = rangeEnd?.takeIf { it.year == year }?.let { nDateQuarter(it) }
                for (row in 0 until 2) {
                    Row(Modifier.fillMaxWidth()) {
                        for (column in 0 until 2) {
                            val quarter = row * 2 + column + 1
                            NChoiceCell(
                                label = "Q$quarter",
                                selected = selectedQuarter == quarter || startQuarter == quarter || endQuarter == quarter,
                                disabled = isQuarterDisabled(year, quarter),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    when (type) {
                                        NDatePickerType.Quarter, NDatePickerType.QuarterRange ->
                                            onSelectQuarter(quarter)
                                        else -> {
                                            onMonthChange((quarter - 1) * 3 + 1)
                                            view = NDatePanelView.Day
                                        }
                                    }
                                },
                            )
                        }
                    }
                }
            }
            NDatePanelView.Year -> {
                val decadeStart = year - ((year % 10 + 10) % 10) - 1
                for (row in 0 until 4) {
                    Row(Modifier.fillMaxWidth()) {
                        for (column in 0 until 3) {
                            val value = decadeStart + row * 3 + column
                            NChoiceCell(
                                label = nDateFormatValue(LocalDate(value, 1, 1), yearFormat, locale.date),
                                selected = selected?.year == value ||
                                    rangeStart?.year == value ||
                                    rangeEnd?.year == value ||
                                    (type == NDatePickerType.Year && year == value),
                                disabled = isYearDisabled(value),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    when (type) {
                                        NDatePickerType.Year, NDatePickerType.YearRange -> onSelectYear(value)
                                        NDatePickerType.Month, NDatePickerType.MonthRange,
                                        NDatePickerType.Quarter, NDatePickerType.QuarterRange,
                                        -> {
                                            onYearChange(value)
                                            view = nDatePanelInitialView(type)
                                        }
                                        else -> {
                                            onYearChange(value)
                                            if (fastYearSelect) onFastSelectClose() else view = NDatePanelView.Month
                                        }
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun NPanelFooterButton(
    text: String,
    enabled: Boolean = true,
    primary: Boolean = false,
    onClick: () -> Unit,
) {
    NButton(
        onClick = onClick,
        size = NSize.Tiny,
        type = if (primary) NButtonType.Primary else NButtonType.Default,
        disabled = !enabled,
    ) {
        NText(text, size = 12.sp)
    }
}

@Composable
internal fun NPanelFooter(content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit) {
    val theme = LocalNTheme.current
    Box(Modifier.fillMaxWidth().height(1.dp).background(theme.colors.divider))
    Row(
        Modifier.fillMaxWidth().padding(top = 6.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
private fun NDateActionFooter(
    actions: Set<NDatePickerAction>,
    confirmEnabled: Boolean,
    onClear: () -> Unit,
    onNow: () -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (actions.isEmpty()) return
    val locale = LocalNLocale.current
    NPanelFooter {
        if (NDatePickerAction.Clear in actions) {
            NPanelFooterButton(locale.date.clear, onClick = onClear)
        }
        if (NDatePickerAction.Now in actions) {
            NPanelFooterButton(locale.date.now, onClick = onNow)
        }
        if (NDatePickerAction.Cancel in actions) {
            NPanelFooterButton(locale.cancel, onClick = onCancel)
        }
        if (NDatePickerAction.Confirm in actions) {
            NPanelFooterButton(
                locale.confirm,
                enabled = confirmEnabled,
                primary = true,
                onClick = onConfirm,
            )
        }
    }
}

@Composable
private fun NDateExtraFooter(footer: (@Composable () -> Unit)?) {
    if (footer == null) return
    val theme = LocalNTheme.current
    Box(Modifier.fillMaxWidth().height(1.dp).background(theme.colors.divider))
    Row(Modifier.fillMaxWidth().padding(top = 6.dp)) { footer() }
}

/**
 * Date picker with date, month, year, week and quarter precision.
 *
 * The value is a [LocalDate]; ranges resolve to the first day of the selected
 * month/year/quarter and weeks resolve to the week start. Use the
 * [LocalDateTime] overload for date-time values.
 */
@Composable
fun NDatePicker(
    value: LocalDate?,
    onValueChange: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
    type: NDatePickerType = NDatePickerType.Date,
    placeholder: String? = null,
    clearable: Boolean = true,
    disabled: Boolean = false,
    size: NSize? = null,
    format: String? = null,
    min: LocalDate? = null,
    max: LocalDate? = null,
    firstDayOfWeek: Int? = null,
    isDateDisabled: ((LocalDate) -> Boolean)? = null,
    shortcuts: (@Composable ColumnScope.(select: (LocalDate) -> Unit) -> Unit)? = null,
    onClear: (() -> Unit)? = null,
    onFocus: (() -> Unit)? = null,
    onBlur: (() -> Unit)? = null,
    onConfirm: ((LocalDate?) -> Unit)? = null,
    actions: Set<NDatePickerAction>? = null,
    status: NStatus? = null,
    monthFormat: String = "MMM",
    yearFormat: String = "yyyy",
    fastYearSelect: Boolean = false,
    fastMonthSelect: Boolean = false,
    panel: Boolean = false,
    updateValueOnClose: Boolean = false,
    footer: (@Composable () -> Unit)? = null,
    controller: NDatePickerController? = null,
    dateIcon: (@Composable () -> Unit)? = null,
    prevYearIcon: (@Composable () -> Unit)? = null,
    prevMonthIcon: (@Composable () -> Unit)? = null,
    nextMonthIcon: (@Composable () -> Unit)? = null,
    nextYearIcon: (@Composable () -> Unit)? = null,
    formattedValue: String? = null,
    onFormattedValueChange: ((String?) -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    val resolvedFirstDayOfWeek = nDateResolveFirstDayOfWeek(locale.date, firstDayOfWeek)
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val effectiveType = when (type) {
        NDatePickerType.Datetime,
        NDatePickerType.MonthRange,
        NDatePickerType.YearRange,
        NDatePickerType.QuarterRange,
        NDatePickerType.DatetimeRange,
        -> NDatePickerType.Date
        else -> type
    }
    val pattern = format ?: nDateDefaultFormat(effectiveType)
    val resolvedSize = nSize(size)
    val resolvedPlaceholder = placeholder ?: nDateDefaultPlaceholder(effectiveType, locale)
    val resolvedActions = actions ?: nDateDefaultActions(effectiveType)
    var visible by remember { mutableStateOf(false) }
    val seed = value ?: today
    var panelYear by remember(visible) { mutableStateOf(seed.year) }
    var panelMonth by remember(visible) { mutableStateOf(seed.month.number) }
    var pendingDate by remember(visible) { mutableStateOf(value) }

    val dateDisabled: (LocalDate) -> Boolean = { date ->
        (min != null && date < min) || (max != null && date > max) ||
            (isDateDisabled?.invoke(date) == true)
    }
    val monthDisabled: (Int, Int) -> Boolean = { year, month ->
        (min != null && LocalDate(year, month, nDateMonthDays(year, month)) < min) ||
            (max != null && LocalDate(year, month, 1) > max)
    }
    val yearDisabled: (Int) -> Boolean = { year ->
        (min != null && LocalDate(year, 12, 31) < min) || (max != null && LocalDate(year, 1, 1) > max)
    }
    val quarterDisabled: (Int, Int) -> Boolean = { year, quarter ->
        val start = LocalDate(year, (quarter - 1) * 3 + 1, 1)
        val end = LocalDate(year, quarter * 3, nDateMonthDays(year, quarter * 3))
        (min != null && end < min) || (max != null && start > max)
    }

    fun emitValue(date: LocalDate?) {
        onValueChange(date)
        onFormattedValueChange?.invoke(date?.let { nDateFormatValue(it, pattern, locale.date) })
    }

    fun closePopup(commit: Boolean = true) {
        if (commit && updateValueOnClose && pendingDate != value) {
            pendingDate?.let { emitValue(it) }
        }
        val wasVisible = visible
        visible = false
        if (wasVisible) onBlur?.invoke()
    }

    fun applySelection(date: LocalDate) {
        pendingDate = date
        if (!updateValueOnClose) {
            emitValue(date)
            visible = false
            onBlur?.invoke()
        }
    }

    if (controller != null) {
        LaunchedEffect(controller.request) {
            val request = controller.request
            if (request != null) {
                controller.request = null
                if (request) {
                    if (!visible) {
                        visible = true
                        onFocus?.invoke()
                    }
                } else if (visible) {
                    closePopup(commit = true)
                }
            }
        }
    }

    if (panel) {
        Column(
            modifier
                .clip(RoundedCornerShape(theme.borderRadius))
                .background(theme.colors.card)
                .nBorder(
                    status?.let { theme.colors.statusColor(it) } ?: theme.colors.border,
                    1.dp,
                    theme.borderRadius,
                )
                .padding(12.dp),
        ) {
            NDateCalendarPanel(
                year = panelYear,
                month = panelMonth,
                type = effectiveType,
                selected = value,
                rangeStart = if (effectiveType == NDatePickerType.Week) {
                    value?.let { nDateWeekStart(it, resolvedFirstDayOfWeek) }
                } else {
                    null
                },
                rangeEnd = if (effectiveType == NDatePickerType.Week) {
                    value?.let { nDateWeekStart(it, resolvedFirstDayOfWeek).plus(6, DateTimeUnit.DAY) }
                } else {
                    null
                },
                firstDayOfWeek = resolvedFirstDayOfWeek,
                today = today,
                isDateDisabled = dateDisabled,
                isMonthDisabled = monthDisabled,
                isYearDisabled = yearDisabled,
                isQuarterDisabled = quarterDisabled,
                onYearChange = { panelYear = it },
                onMonthChange = { panelMonth = it },
                onSelectDate = { emitValue(nDateNormalize(it, effectiveType, resolvedFirstDayOfWeek)) },
                onSelectMonth = { emitValue(LocalDate(panelYear, it, 1)) },
                onSelectYear = { emitValue(LocalDate(it, 1, 1)) },
                onSelectQuarter = {
                    emitValue(LocalDate(panelYear, (it - 1) * 3 + 1, 1))
                },
                monthFormat = monthFormat,
                yearFormat = yearFormat,
            )
            NDateExtraFooter(footer)
        }
        return
    }

    Box(modifier) {
        NDateInputTrigger(
            text = formattedValue ?: value?.let { nDateFormatValue(it, pattern, locale.date) },
            placeholder = resolvedPlaceholder,
            disabled = disabled,
            size = resolvedSize,
            clearable = clearable,
            status = status,
            prefix = dateIcon,
            onClick = {
                if (visible) closePopup(commit = true) else {
                    visible = true
                    onFocus?.invoke()
                }
            },
            onClear = {
                emitValue(null)
                onClear?.invoke()
                closePopup(commit = false)
            },
        )
        NPopoverSurface(
            visible = visible && !disabled,
            onDismiss = { closePopup(commit = true) },
            placement = NPlacement.BottomStart,
            padding = PaddingValues(0.dp),
        ) {
            Row(Modifier.padding(12.dp)) {
                if (shortcuts != null) {
                    Box(Modifier.width(148.dp).height(264.dp).padding(end = 8.dp)) {
                        NScrollbar(Modifier.fillMaxSize()) {
                            Column(Modifier.fillMaxWidth()) {
                                shortcuts { selected ->
                                    emitValue(selected)
                                    closePopup(commit = false)
                                }
                            }
                        }
                    }
                    Box(Modifier.width(1.dp).height(264.dp).background(theme.colors.divider))
                    Spacer(Modifier.width(12.dp))
                }
                Column(Modifier.width(280.dp)) {
                    NDateCalendarPanel(
                        year = panelYear,
                        month = panelMonth,
                        type = effectiveType,
                        selected = pendingDate,
                        rangeStart = if (effectiveType == NDatePickerType.Week) {
                            pendingDate?.let { nDateWeekStart(it, resolvedFirstDayOfWeek) }
                        } else {
                            null
                        },
                        rangeEnd = if (effectiveType == NDatePickerType.Week) {
                            pendingDate?.let { nDateWeekStart(it, resolvedFirstDayOfWeek).plus(6, DateTimeUnit.DAY) }
                        } else {
                            null
                        },
                        firstDayOfWeek = resolvedFirstDayOfWeek,
                        today = today,
                        isDateDisabled = dateDisabled,
                        isMonthDisabled = monthDisabled,
                        isYearDisabled = yearDisabled,
                        isQuarterDisabled = quarterDisabled,
                        onYearChange = { panelYear = it },
                        onMonthChange = { panelMonth = it },
                        onSelectDate = { applySelection(nDateNormalize(it, effectiveType, resolvedFirstDayOfWeek)) },
                        onSelectMonth = { applySelection(LocalDate(panelYear, it, 1)) },
                        onSelectYear = { applySelection(LocalDate(it, 1, 1)) },
                        onSelectQuarter = {
                            applySelection(LocalDate(panelYear, (it - 1) * 3 + 1, 1))
                        },
                        monthFormat = monthFormat,
                        yearFormat = yearFormat,
                        prevYear = prevYearIcon,
                        prevMonth = prevMonthIcon,
                        nextMonth = nextMonthIcon,
                        nextYear = nextYearIcon,
                        fastYearSelect = fastYearSelect,
                        fastMonthSelect = fastMonthSelect,
                        onFastSelectClose = { closePopup(commit = true) },
                    )
                    NDateActionFooter(
                        actions = resolvedActions,
                        confirmEnabled = pendingDate != null,
                        onClear = {
                            emitValue(null)
                            onClear?.invoke()
                            closePopup(commit = false)
                        },
                        onNow = {
                            val nowValue = nDateNormalize(today, effectiveType, resolvedFirstDayOfWeek)
                            emitValue(nowValue)
                            closePopup(commit = false)
                        },
                        onCancel = { closePopup(commit = false) },
                        onConfirm = {
                            if (updateValueOnClose && pendingDate != value) {
                                pendingDate?.let { emitValue(it) }
                            }
                            onConfirm?.invoke(pendingDate)
                            closePopup(commit = false)
                        },
                    )
                    NDateExtraFooter(footer)
                }
            }
        }
    }
}

/**
 * Date-time picker. The panel edits a draft date and time and commits on
 * confirm (or on close when [updateValueOnClose] is set).
 */
@Composable
fun NDatePicker(
    value: LocalDateTime?,
    onValueChange: (LocalDateTime?) -> Unit,
    modifier: Modifier = Modifier,
    type: NDatePickerType = NDatePickerType.Datetime,
    placeholder: String? = null,
    clearable: Boolean = true,
    disabled: Boolean = false,
    size: NSize? = null,
    format: String? = null,
    use12Hours: Boolean = false,
    defaultTime: LocalTime? = null,
    min: LocalDateTime? = null,
    max: LocalDateTime? = null,
    firstDayOfWeek: Int? = null,
    isDateDisabled: ((LocalDate) -> Boolean)? = null,
    isHourDisabled: ((Int) -> Boolean)? = null,
    isMinuteDisabled: ((Int) -> Boolean)? = null,
    isSecondDisabled: ((Int) -> Boolean)? = null,
    shortcuts: (@Composable ColumnScope.(select: (LocalDateTime) -> Unit) -> Unit)? = null,
    onClear: (() -> Unit)? = null,
    onFocus: (() -> Unit)? = null,
    onBlur: (() -> Unit)? = null,
    onConfirm: ((LocalDateTime?) -> Unit)? = null,
    actions: Set<NDatePickerAction>? = null,
    status: NStatus? = null,
    fastYearSelect: Boolean = false,
    fastMonthSelect: Boolean = false,
    updateValueOnClose: Boolean = false,
    footer: (@Composable () -> Unit)? = null,
    controller: NDatePickerController? = null,
    dateIcon: (@Composable () -> Unit)? = null,
    prevYearIcon: (@Composable () -> Unit)? = null,
    prevMonthIcon: (@Composable () -> Unit)? = null,
    nextMonthIcon: (@Composable () -> Unit)? = null,
    nextYearIcon: (@Composable () -> Unit)? = null,
    formattedValue: String? = null,
    onFormattedValueChange: ((String?) -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    val resolvedFirstDayOfWeek = nDateResolveFirstDayOfWeek(locale.date, firstDayOfWeek)
    val zone = remember { TimeZone.currentSystemDefault() }
    val now = remember { Clock.System.now().toLocalDateTime(zone) }
    val today = remember { Clock.System.todayIn(zone) }
    val pattern = format ?: nDateDefaultFormat(NDatePickerType.Datetime)
    val resolvedSize = nSize(size)
    val resolvedPlaceholder = placeholder ?: nDateDefaultPlaceholder(NDatePickerType.Datetime, locale)
    val resolvedActions = actions ?: nDateDefaultActions(NDatePickerType.Datetime)
    var visible by remember { mutableStateOf(false) }

    val initialTime = value?.time ?: defaultTime ?: now.time
    var panelYear by remember(visible) { mutableStateOf((value?.date ?: today).year) }
    var panelMonth by remember(visible) { mutableStateOf((value?.date ?: today).month.number) }
    var draftDate by remember(visible) { mutableStateOf(value?.date ?: today) }
    var dateText by remember(visible) {
        mutableStateOf(nDateFormat(draftDate, null, "yyyy-MM-dd", locale.date))
    }
    LaunchedEffect(draftDate) {
        dateText = nDateFormat(draftDate, null, "yyyy-MM-dd", locale.date)
        panelYear = draftDate.year
        panelMonth = draftDate.month.number
    }
    var hour by remember(visible) { mutableStateOf(initialTime.hour) }
    var minute by remember(visible) { mutableStateOf(initialTime.minute) }
    var second by remember(visible) { mutableStateOf(initialTime.second) }

    val draft = LocalDateTime(draftDate, LocalTime(hour, minute, second))
    val valid = (min == null || draft >= min) && (max == null || draft <= max)

    fun emitValue(dateTime: LocalDateTime?) {
        onValueChange(dateTime)
        onFormattedValueChange?.invoke(
            dateTime?.let { nDateFormat(it.date, it.time, pattern, locale.date) },
        )
    }

    fun closePopup(commitDraft: Boolean) {
        if (commitDraft && updateValueOnClose && valid && draft != value) emitValue(draft)
        val wasVisible = visible
        visible = false
        if (wasVisible) onBlur?.invoke()
    }

    val dateDisabled: (LocalDate) -> Boolean = { date ->
        (min != null && date < min.date) || (max != null && date > max.date) ||
            (isDateDisabled?.invoke(date) == true)
    }
    val monthDisabled: (Int, Int) -> Boolean = { year, month ->
        (min != null && LocalDate(year, month, nDateMonthDays(year, month)) < min.date) ||
            (max != null && LocalDate(year, month, 1) > max.date)
    }
    val yearDisabled: (Int) -> Boolean = { year ->
        (min != null && LocalDate(year, 12, 31) < min.date) ||
            (max != null && LocalDate(year, 1, 1) > max.date)
    }
    val quarterDisabled: (Int, Int) -> Boolean = { year, quarter ->
        val start = LocalDate(year, (quarter - 1) * 3 + 1, 1)
        val end = LocalDate(year, quarter * 3, nDateMonthDays(year, quarter * 3))
        (min != null && end < min.date) || (max != null && start > max.date)
    }

    if (controller != null) {
        LaunchedEffect(controller.request) {
            val request = controller.request
            if (request != null) {
                controller.request = null
                if (request) {
                    if (!visible) {
                        visible = true
                        onFocus?.invoke()
                    }
                } else if (visible) {
                    closePopup(commitDraft = true)
                }
            }
        }
    }

    Box(modifier) {
        NDateInputTrigger(
            text = formattedValue
                ?: value?.let { nDateFormat(it.date, it.time, pattern, locale.date) },
            placeholder = resolvedPlaceholder,
            disabled = disabled,
            size = resolvedSize,
            clearable = clearable,
            status = status,
            prefix = dateIcon,
            onClick = {
                if (visible) closePopup(commitDraft = true) else {
                    visible = true
                    onFocus?.invoke()
                }
            },
            onClear = {
                emitValue(null)
                onClear?.invoke()
                closePopup(commitDraft = false)
            },
        )
        NPopoverSurface(
            visible = visible && !disabled,
            onDismiss = { closePopup(commitDraft = true) },
            placement = NPlacement.BottomStart,
            padding = PaddingValues(0.dp),
        ) {
            Row(Modifier.padding(12.dp)) {
                if (shortcuts != null) {
                    Box(Modifier.width(160.dp).height(250.dp).padding(end = 8.dp)) {
                        NScrollbar(Modifier.fillMaxSize()) {
                            Column(Modifier.fillMaxWidth()) {
                                shortcuts { selected ->
                                    emitValue(selected)
                                    closePopup(commitDraft = false)
                                }
                            }
                        }
                    }
                    Box(Modifier.width(1.dp).height(250.dp).background(theme.colors.divider))
                    Spacer(Modifier.width(12.dp))
                }
                Column(Modifier.width(280.dp)) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        NInput(
                            value = dateText,
                            onValueChange = { text ->
                                dateText = text
                                runCatching { LocalDate.parse(text.trim()) }.getOrNull()?.let { date ->
                                    draftDate = date
                                }
                            },
                            modifier = Modifier.weight(1f),
                            placeholder = locale.date.selectDate,
                            size = NSize.Small,
                        )
                        NTimePicker(
                            value = LocalTime(hour, minute, second),
                            onValueChange = {
                                if (it != null) {
                                    hour = it.hour
                                    minute = it.minute
                                    second = it.second
                                }
                            },
                            modifier = Modifier.width(118.dp),
                            size = NSize.Small,
                            format = "HH:mm:ss",
                            clearable = false,
                            showSeconds = true,
                            use12Hours = use12Hours,
                        )
                    }
                    NDateCalendarPanel(
                        year = panelYear,
                        month = panelMonth,
                        type = NDatePickerType.Date,
                        selected = draftDate,
                        rangeStart = null,
                        rangeEnd = null,
                        firstDayOfWeek = resolvedFirstDayOfWeek,
                        today = today,
                        isDateDisabled = dateDisabled,
                        isMonthDisabled = monthDisabled,
                        isYearDisabled = yearDisabled,
                        isQuarterDisabled = quarterDisabled,
                        onYearChange = { panelYear = it },
                        onMonthChange = { panelMonth = it },
                        onSelectDate = { draftDate = it },
                        onSelectMonth = { panelMonth = it },
                        onSelectYear = { panelYear = it },
                        onSelectQuarter = {},
                        prevYear = prevYearIcon,
                        prevMonth = prevMonthIcon,
                        nextMonth = nextMonthIcon,
                        nextYear = nextYearIcon,
                        fastYearSelect = fastYearSelect,
                        fastMonthSelect = fastMonthSelect,
                        onFastSelectClose = { closePopup(commitDraft = true) },
                    )
                    NDateActionFooter(
                        actions = resolvedActions,
                        confirmEnabled = valid,
                        onClear = {
                            emitValue(null)
                            onClear?.invoke()
                            closePopup(commitDraft = false)
                        },
                        onNow = {
                            val current = Clock.System.now().toLocalDateTime(zone)
                            emitValue(current)
                            closePopup(commitDraft = false)
                        },
                        onCancel = { closePopup(commitDraft = false) },
                        onConfirm = {
                            if (valid) {
                                emitValue(draft)
                                onConfirm?.invoke(draft)
                            }
                            closePopup(commitDraft = false)
                        },
                    )
                    NDateExtraFooter(footer)
                }
            }
        }
    }
}

/**
 * Range date picker for date, month, year and quarter ranges. The two panels
 * navigate independently and the pair is committed with confirm (or on close
 * when [updateValueOnClose] is set).
 */
@Composable
fun NDatePicker(
    value: Pair<LocalDate, LocalDate>?,
    onValueChange: (Pair<LocalDate, LocalDate>?) -> Unit,
    modifier: Modifier = Modifier,
    type: NDatePickerType = NDatePickerType.Date,
    startPlaceholder: String? = null,
    endPlaceholder: String? = null,
    clearable: Boolean = true,
    disabled: Boolean = false,
    size: NSize? = null,
    format: String? = null,
    min: LocalDate? = null,
    max: LocalDate? = null,
    firstDayOfWeek: Int? = null,
    isDateDisabled: ((LocalDate) -> Boolean)? = null,
    shortcuts: (@Composable ColumnScope.(select: (Pair<LocalDate, LocalDate>) -> Unit) -> Unit)? = null,
    onClear: (() -> Unit)? = null,
    onFocus: (() -> Unit)? = null,
    onBlur: (() -> Unit)? = null,
    onConfirm: ((Pair<LocalDate, LocalDate>?) -> Unit)? = null,
    actions: Set<NDatePickerAction>? = null,
    status: NStatus? = null,
    fastYearSelect: Boolean = false,
    fastMonthSelect: Boolean = false,
    updateValueOnClose: Boolean = false,
    footer: (@Composable () -> Unit)? = null,
    controller: NDatePickerController? = null,
    dateIcon: (@Composable () -> Unit)? = null,
    separator: (@Composable () -> Unit)? = null,
    prevYearIcon: (@Composable () -> Unit)? = null,
    prevMonthIcon: (@Composable () -> Unit)? = null,
    nextMonthIcon: (@Composable () -> Unit)? = null,
    nextYearIcon: (@Composable () -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    val resolvedFirstDayOfWeek = nDateResolveFirstDayOfWeek(locale.date, firstDayOfWeek)
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val effectiveType = when (type) {
        NDatePickerType.MonthRange,
        NDatePickerType.YearRange,
        NDatePickerType.QuarterRange,
        -> type
        else -> NDatePickerType.Date
    }
    val pattern = format ?: nDateDefaultFormat(effectiveType)
    val resolvedSize = nSize(size)
    val resolvedStart = startPlaceholder ?: locale.date.startDate
    val resolvedEnd = endPlaceholder ?: locale.date.endDate
    val resolvedActions = actions ?: nDateDefaultActions(effectiveType)
    var visible by remember { mutableStateOf(false) }

    val startSeed = value?.first ?: today
    val endSeed = when (effectiveType) {
        NDatePickerType.YearRange -> LocalDate((value?.second ?: startSeed).year + if (value?.second == null) 1 else 0, 1, 1)
        NDatePickerType.QuarterRange -> LocalDate((value?.second ?: startSeed).year + if (value?.second == null) 1 else 0, 1, 1)
        else -> value?.second ?: startSeed.plus(1, DateTimeUnit.MONTH)
    }
    var startYear by remember(visible) { mutableStateOf(startSeed.year) }
    var startMonth by remember(visible) { mutableStateOf(if (effectiveType == NDatePickerType.YearRange) 1 else startSeed.month.number) }
    var endYear by remember(visible) { mutableStateOf(endSeed.year) }
    var endMonth by remember(visible) { mutableStateOf(if (effectiveType == NDatePickerType.YearRange) 1 else endSeed.month.number) }
    var draftStart by remember(visible) { mutableStateOf(value?.first) }
    var draftEnd by remember(visible) { mutableStateOf(value?.second) }

    val dateDisabled: (LocalDate) -> Boolean = { date ->
        (min != null && date < min) || (max != null && date > max) ||
            (isDateDisabled?.invoke(date) == true)
    }
    val monthDisabled: (Int, Int) -> Boolean = { year, month ->
        (min != null && LocalDate(year, month, nDateMonthDays(year, month)) < min) ||
            (max != null && LocalDate(year, month, 1) > max)
    }
    val yearDisabled: (Int) -> Boolean = { year ->
        (min != null && LocalDate(year, 12, 31) < min) || (max != null && LocalDate(year, 1, 1) > max)
    }
    val quarterDisabled: (Int, Int) -> Boolean = { year, quarter ->
        val start = LocalDate(year, (quarter - 1) * 3 + 1, 1)
        val end = LocalDate(year, quarter * 3, nDateMonthDays(year, quarter * 3))
        (min != null && end < min) || (max != null && start > max)
    }

    fun emitValue(range: Pair<LocalDate, LocalDate>?) {
        onValueChange(range)
    }

    fun closePopup(commit: Boolean) {
        if (commit && updateValueOnClose) {
            val start = draftStart
            val end = draftEnd
            if (start != null && end != null && (start to end) != value) emitValue(start to end)
        }
        val wasVisible = visible
        visible = false
        if (wasVisible) onBlur?.invoke()
    }

    fun selectRange(date: LocalDate) {
        val start = draftStart
        if (start == null || draftEnd != null) {
            draftStart = date
            draftEnd = null
        } else if (date < start) {
            draftStart = date
            draftEnd = start
        } else {
            draftEnd = date
        }
    }

    if (controller != null) {
        LaunchedEffect(controller.request) {
            val request = controller.request
            if (request != null) {
                controller.request = null
                if (request) {
                    if (!visible) {
                        visible = true
                        onFocus?.invoke()
                    }
                } else if (visible) {
                    closePopup(commit = true)
                }
            }
        }
    }

    val completeRange = draftStart != null && draftEnd != null

    Box(modifier) {
        NDateRangeTrigger(
            startText = value?.first?.let { nDateFormatValue(it, pattern, locale.date) },
            endText = value?.second?.let { nDateFormatValue(it, pattern, locale.date) },
            startPlaceholder = resolvedStart,
            endPlaceholder = resolvedEnd,
            disabled = disabled,
            size = resolvedSize,
            clearable = clearable,
            status = status,
            prefix = dateIcon,
            separator = separator,
            onClick = {
                if (visible) closePopup(commit = true) else {
                    visible = true
                    onFocus?.invoke()
                }
            },
            onClear = {
                emitValue(null)
                onClear?.invoke()
                closePopup(commit = false)
            },
        )
        NPopoverSurface(
            visible = visible && !disabled,
            onDismiss = { closePopup(commit = true) },
            placement = NPlacement.BottomStart,
            padding = PaddingValues(0.dp),
        ) {
            Row(Modifier.padding(12.dp)) {
                if (shortcuts != null) {
                    Box(Modifier.width(150.dp).height(264.dp).padding(end = 8.dp)) {
                        NScrollbar(Modifier.fillMaxSize()) {
                            Column(Modifier.fillMaxWidth()) {
                                shortcuts { pair ->
                                    emitValue(pair)
                                    closePopup(commit = false)
                                }
                            }
                        }
                    }
                    Box(Modifier.width(1.dp).height(264.dp).background(theme.colors.divider))
                    Spacer(Modifier.width(10.dp))
                }
                Column(Modifier.width(577.dp)) {
                    Row {
                        NDateCalendarPanel(
                            year = startYear,
                            month = startMonth,
                            type = effectiveType,
                            selected = null,
                            rangeStart = draftStart,
                            rangeEnd = draftEnd,
                            firstDayOfWeek = resolvedFirstDayOfWeek,
                            today = today,
                            isDateDisabled = dateDisabled,
                            isMonthDisabled = monthDisabled,
                            isYearDisabled = yearDisabled,
                            isQuarterDisabled = quarterDisabled,
                            onYearChange = { startYear = it },
                            onMonthChange = { startMonth = it },
                            onSelectDate = { selectRange(it) },
                            onSelectMonth = { selectRange(LocalDate(startYear, it, 1)) },
                            onSelectYear = { selectRange(LocalDate(it, 1, 1)) },
                            onSelectQuarter = {
                                selectRange(LocalDate(startYear, (it - 1) * 3 + 1, 1))
                            },
                            prevYear = prevYearIcon,
                            prevMonth = prevMonthIcon,
                            nextMonth = nextMonthIcon,
                            nextYear = nextYearIcon,
                            fastYearSelect = fastYearSelect,
                            fastMonthSelect = fastMonthSelect,
                            onFastSelectClose = { closePopup(commit = true) },
                        )
                        Spacer(Modifier.width(8.dp))
                        Box(Modifier.width(1.dp).height(264.dp).background(theme.colors.divider))
                        Spacer(Modifier.width(8.dp))
                        NDateCalendarPanel(
                            year = endYear,
                            month = endMonth,
                            type = effectiveType,
                            selected = null,
                            rangeStart = draftStart,
                            rangeEnd = draftEnd,
                            firstDayOfWeek = resolvedFirstDayOfWeek,
                            today = today,
                            isDateDisabled = dateDisabled,
                            isMonthDisabled = monthDisabled,
                            isYearDisabled = yearDisabled,
                            isQuarterDisabled = quarterDisabled,
                            onYearChange = { endYear = it },
                            onMonthChange = { endMonth = it },
                            onSelectDate = { selectRange(it) },
                            onSelectMonth = { selectRange(LocalDate(endYear, it, 1)) },
                            onSelectYear = { selectRange(LocalDate(it, 1, 1)) },
                            onSelectQuarter = {
                                selectRange(LocalDate(endYear, (it - 1) * 3 + 1, 1))
                            },
                            prevYear = prevYearIcon,
                            prevMonth = prevMonthIcon,
                            nextMonth = nextMonthIcon,
                            nextYear = nextYearIcon,
                            fastYearSelect = fastYearSelect,
                            fastMonthSelect = fastMonthSelect,
                            onFastSelectClose = { closePopup(commit = true) },
                        )
                    }
                    NDateActionFooter(
                        actions = resolvedActions,
                        confirmEnabled = completeRange,
                        onClear = {
                            emitValue(null)
                            onClear?.invoke()
                            closePopup(commit = false)
                        },
                        onNow = {
                            val nowValue = nDateNormalize(today, effectiveType, resolvedFirstDayOfWeek)
                            emitValue(nowValue to nowValue)
                            closePopup(commit = false)
                        },
                        onCancel = { closePopup(commit = false) },
                        onConfirm = {
                            val start = draftStart
                            val end = draftEnd
                            if (start != null && end != null) {
                                emitValue(start to end)
                                onConfirm?.invoke(start to end)
                            }
                            closePopup(commit = false)
                        },
                    )
                    NDateExtraFooter(footer)
                }
            }
        }
    }
}

/**
 * Date-time range picker. Each calendar has its own time columns and the pair
 * is committed with confirm.
 */
@Composable
fun NDateTimeRangePicker(
    value: Pair<LocalDateTime, LocalDateTime>?,
    onValueChange: (Pair<LocalDateTime, LocalDateTime>?) -> Unit,
    modifier: Modifier = Modifier,
    startPlaceholder: String? = null,
    endPlaceholder: String? = null,
    clearable: Boolean = true,
    disabled: Boolean = false,
    size: NSize? = null,
    format: String? = null,
    use12Hours: Boolean = false,
    defaultStartTime: LocalTime? = null,
    defaultEndTime: LocalTime? = null,
    min: LocalDateTime? = null,
    max: LocalDateTime? = null,
    firstDayOfWeek: Int? = null,
    isDateDisabled: ((LocalDate) -> Boolean)? = null,
    isHourDisabled: ((Int) -> Boolean)? = null,
    isMinuteDisabled: ((Int) -> Boolean)? = null,
    isSecondDisabled: ((Int) -> Boolean)? = null,
    shortcuts: (@Composable ColumnScope.(select: (Pair<LocalDateTime, LocalDateTime>) -> Unit) -> Unit)? = null,
    onClear: (() -> Unit)? = null,
    onFocus: (() -> Unit)? = null,
    onBlur: (() -> Unit)? = null,
    onConfirm: ((Pair<LocalDateTime, LocalDateTime>?) -> Unit)? = null,
    actions: Set<NDatePickerAction>? = null,
    status: NStatus? = null,
    fastYearSelect: Boolean = false,
    fastMonthSelect: Boolean = false,
    updateValueOnClose: Boolean = false,
    footer: (@Composable () -> Unit)? = null,
    controller: NDatePickerController? = null,
    separator: (@Composable () -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    val resolvedFirstDayOfWeek = nDateResolveFirstDayOfWeek(locale.date, firstDayOfWeek)
    val zone = remember { TimeZone.currentSystemDefault() }
    val now = remember { Clock.System.now().toLocalDateTime(zone) }
    val today = remember { Clock.System.todayIn(zone) }
    val pattern = format ?: "yyyy-MM-dd HH:mm:ss"
    val resolvedSize = nSize(size)
    val resolvedStart = startPlaceholder ?: locale.date.startDate
    val resolvedEnd = endPlaceholder ?: locale.date.endDate
    val resolvedActions = actions ?: nDateDefaultActions(NDatePickerType.DatetimeRange)
    var visible by remember { mutableStateOf(false) }

    val startSeedDate = value?.first?.date ?: today
    val endSeedDate = value?.second?.date ?: today
    var startYear by remember(visible) { mutableStateOf(startSeedDate.year) }
    var startMonth by remember(visible) { mutableStateOf(startSeedDate.month.number) }
    var endYear by remember(visible) { mutableStateOf(endSeedDate.year) }
    var endMonth by remember(visible) { mutableStateOf(endSeedDate.month.number) }
    var startDate by remember(visible) { mutableStateOf(startSeedDate) }
    var endDate by remember(visible) { mutableStateOf(endSeedDate) }
    var startTime by remember(visible) {
        mutableStateOf(value?.first?.time ?: defaultStartTime ?: LocalTime(0, 0))
    }
    var endTime by remember(visible) {
        mutableStateOf(value?.second?.time ?: defaultEndTime ?: LocalTime(23, 59, 59))
    }
    var startSet by remember(visible) { mutableStateOf(value?.first != null) }
    var endSet by remember(visible) { mutableStateOf(value?.second != null) }
    var selectingEnd by remember(visible) { mutableStateOf(value?.first != null && value?.second == null) }
    var startDateText by remember(visible) {
        mutableStateOf(nDateFormat(startDate, null, "yyyy-MM-dd", locale.date))
    }
    var endDateText by remember(visible) {
        mutableStateOf(nDateFormat(endDate, null, "yyyy-MM-dd", locale.date))
    }
    LaunchedEffect(startDate) {
        startDateText = nDateFormat(startDate, null, "yyyy-MM-dd", locale.date)
        startYear = startDate.year
        startMonth = startDate.month.number
    }
    LaunchedEffect(endDate) {
        endDateText = nDateFormat(endDate, null, "yyyy-MM-dd", locale.date)
        endYear = endDate.year
        endMonth = endDate.month.number
    }

    val startDateTime = LocalDateTime(startDate, startTime)
    val endDateTime = LocalDateTime(endDate, endTime)
    val validRange = startSet && endSet &&
        (min == null || startDateTime >= min) &&
        (max == null || endDateTime <= max) &&
        startDateTime <= endDateTime

    val dateDisabled: (LocalDate) -> Boolean = { date ->
        (min != null && date < min.date) || (max != null && date > max.date) ||
            (isDateDisabled?.invoke(date) == true)
    }
    val monthDisabled: (Int, Int) -> Boolean = { year, month ->
        (min != null && LocalDate(year, month, nDateMonthDays(year, month)) < min.date) ||
            (max != null && LocalDate(year, month, 1) > max.date)
    }
    val yearDisabled: (Int) -> Boolean = { year ->
        (min != null && LocalDate(year, 12, 31) < min.date) ||
            (max != null && LocalDate(year, 1, 1) > max.date)
    }
    val quarterDisabled: (Int, Int) -> Boolean = { year, quarter ->
        val start = LocalDate(year, (quarter - 1) * 3 + 1, 1)
        val end = LocalDate(year, quarter * 3, nDateMonthDays(year, quarter * 3))
        (min != null && end < min.date) || (max != null && start > max.date)
    }

    fun emitValue(range: Pair<LocalDateTime, LocalDateTime>?) {
        onValueChange(range)
    }

    fun closePopup(commit: Boolean) {
        if (commit && updateValueOnClose && validRange &&
            (startDateTime to endDateTime) != value
        ) {
            emitValue(startDateTime to endDateTime)
        }
        val wasVisible = visible
        visible = false
        if (wasVisible) onBlur?.invoke()
    }

    fun selectDate(date: LocalDate) {
        if (!selectingEnd || (startSet && endSet)) {
            startDate = date
            startSet = true
            endSet = false
            selectingEnd = true
        } else {
            endDate = date
            endSet = true
            selectingEnd = false
            if (LocalDateTime(endDate, endTime) < LocalDateTime(startDate, startTime)) {
                val oldDate = startDate
                val oldTime = startTime
                startDate = endDate
                startTime = endTime
                endDate = oldDate
                endTime = oldTime
            }
        }
    }

    if (controller != null) {
        LaunchedEffect(controller.request) {
            val request = controller.request
            if (request != null) {
                controller.request = null
                if (request) {
                    if (!visible) {
                        visible = true
                        onFocus?.invoke()
                    }
                } else if (visible) {
                    closePopup(commit = true)
                }
            }
        }
    }

    Box(modifier) {
        NDateRangeTrigger(
            startText = value?.first?.let { nDateFormat(it.date, it.time, pattern, locale.date) },
            endText = value?.second?.let { nDateFormat(it.date, it.time, pattern, locale.date) },
            startPlaceholder = resolvedStart,
            endPlaceholder = resolvedEnd,
            disabled = disabled,
            size = resolvedSize,
            clearable = clearable,
            status = status,
            separator = separator,
            onClick = {
                if (visible) closePopup(commit = true) else {
                    visible = true
                    onFocus?.invoke()
                }
            },
            onClear = {
                emitValue(null)
                onClear?.invoke()
                closePopup(commit = false)
            },
        )
        NPopoverSurface(
            visible = visible && !disabled,
            onDismiss = { closePopup(commit = true) },
            placement = NPlacement.BottomStart,
            padding = PaddingValues(0.dp),
        ) {
            Row(Modifier.padding(12.dp)) {
                if (shortcuts != null) {
                    Box(Modifier.width(150.dp).height(380.dp).padding(end = 8.dp)) {
                        NScrollbar(Modifier.fillMaxSize()) {
                            Column(Modifier.fillMaxWidth()) {
                                shortcuts { pair ->
                                    emitValue(pair)
                                    closePopup(commit = false)
                                }
                            }
                        }
                    }
                    Box(Modifier.width(1.dp).height(380.dp).background(theme.colors.divider))
                    Spacer(Modifier.width(10.dp))
                }
                Column(Modifier.width(577.dp)) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        NInput(
                            value = startDateText,
                            onValueChange = { text ->
                                startDateText = text
                                runCatching { LocalDate.parse(text.trim()) }.getOrNull()?.let { date ->
                                    startDate = date
                                    startYear = date.year
                                    startMonth = date.month.number
                                    startSet = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                            placeholder = locale.date.startDate,
                            size = NSize.Small,
                        )
                        NTimePicker(
                            value = startTime,
                            onValueChange = {
                                if (it != null) {
                                    startTime = it
                                    startSet = true
                                }
                            },
                            modifier = Modifier.width(118.dp),
                            size = NSize.Small,
                            format = "HH:mm:ss",
                            clearable = false,
                            showSeconds = true,
                            use12Hours = use12Hours,
                        )
                        NInput(
                            value = endDateText,
                            onValueChange = { text ->
                                endDateText = text
                                runCatching { LocalDate.parse(text.trim()) }.getOrNull()?.let { date ->
                                    endDate = date
                                    endYear = date.year
                                    endMonth = date.month.number
                                    endSet = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                            placeholder = locale.date.endDate,
                            size = NSize.Small,
                        )
                        NTimePicker(
                            value = endTime,
                            onValueChange = {
                                if (it != null) {
                                    endTime = it
                                    endSet = true
                                }
                            },
                            modifier = Modifier.width(118.dp),
                            size = NSize.Small,
                            format = "HH:mm:ss",
                            clearable = false,
                            showSeconds = true,
                            use12Hours = use12Hours,
                        )
                    }
                    Row(Modifier.padding(horizontal = 12.dp)) {
                        Column(Modifier.width(280.dp)) {
                            NDateCalendarPanel(
                                year = startYear,
                                month = startMonth,
                                type = NDatePickerType.Date,
                                selected = if (startSet) startDate else null,
                                rangeStart = if (startSet) startDate else null,
                                rangeEnd = if (endSet) endDate else null,
                                firstDayOfWeek = resolvedFirstDayOfWeek,
                                today = today,
                                isDateDisabled = dateDisabled,
                                isMonthDisabled = monthDisabled,
                                isYearDisabled = yearDisabled,
                                isQuarterDisabled = quarterDisabled,
                                onYearChange = { startYear = it },
                                onMonthChange = { startMonth = it },
                                onSelectDate = { selectDate(it) },
                                onSelectMonth = { startMonth = it },
                                onSelectYear = { startYear = it },
                                onSelectQuarter = {},
                                fastYearSelect = fastYearSelect,
                                fastMonthSelect = fastMonthSelect,
                                onFastSelectClose = { closePopup(commit = true) },
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Box(Modifier.width(1.dp).height(300.dp).background(theme.colors.divider))
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.width(280.dp)) {
                            NDateCalendarPanel(
                                year = endYear,
                                month = endMonth,
                                type = NDatePickerType.Date,
                                selected = if (endSet) endDate else null,
                                rangeStart = if (startSet) startDate else null,
                                rangeEnd = if (endSet) endDate else null,
                                firstDayOfWeek = resolvedFirstDayOfWeek,
                                today = today,
                                isDateDisabled = dateDisabled,
                                isMonthDisabled = monthDisabled,
                                isYearDisabled = yearDisabled,
                                isQuarterDisabled = quarterDisabled,
                                onYearChange = { endYear = it },
                                onMonthChange = { endMonth = it },
                                onSelectDate = { selectDate(it) },
                                onSelectMonth = { endMonth = it },
                                onSelectYear = { endYear = it },
                                onSelectQuarter = {},
                                fastYearSelect = fastYearSelect,
                                fastMonthSelect = fastMonthSelect,
                                onFastSelectClose = { closePopup(commit = true) },
                            )
                        }
                    }
                    NDateActionFooter(
                        actions = resolvedActions,
                        confirmEnabled = validRange,
                        onClear = {
                            emitValue(null)
                            onClear?.invoke()
                            closePopup(commit = false)
                        },
                        onNow = {
                            val current = Clock.System.now().toLocalDateTime(zone)
                            emitValue(current to current)
                            closePopup(commit = false)
                        },
                        onCancel = { closePopup(commit = false) },
                        onConfirm = {
                            if (validRange) {
                                val pair = startDateTime to endDateTime
                                emitValue(pair)
                                onConfirm?.invoke(pair)
                            }
                            closePopup(commit = false)
                        },
                    )
                    NDateExtraFooter(footer)
                }
            }
        }
    }
}
