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
import cn.enaium.naiveui.components.NText
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
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.nSize
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/** Value granularity of [NDatePicker]. */
enum class NDatePickerType { Date, Datetime, Month, Year }

private enum class NDatePanelView { Day, Month, Year }

private fun nDatePad(value: Int, width: Int = 2): String =
    value.toString().padStart(width, '0')

internal fun nDateMonthDays(year: Int, month: Int): Int {
    val first = LocalDate(year, month, 1)
    return first.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY).day
}

internal fun nDateGridStart(year: Int, month: Int, firstDayOfWeek: Int): LocalDate {
    val first = LocalDate(year, month, 1)
    val offset = (first.dayOfWeek.isoDayNumber - firstDayOfWeek.coerceIn(1, 7) + 7) % 7
    return first.minus(offset, DateTimeUnit.DAY)
}

internal fun nDateGridCellCount(year: Int, month: Int, firstDayOfWeek: Int): Int {
    val first = LocalDate(year, month, 1)
    val offset = (first.dayOfWeek.isoDayNumber - firstDayOfWeek.coerceIn(1, 7) + 7) % 7
    return ((offset + nDateMonthDays(year, month) + 6) / 7) * 7
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
            'Q' -> if (date != null) out.append((date.month.number + 2) / 3)
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

private fun nDateDefaultFormat(type: NDatePickerType): String = when (type) {
    NDatePickerType.Month -> "yyyy-MM"
    NDatePickerType.Year -> "yyyy"
    NDatePickerType.Datetime -> "yyyy-MM-dd HH:mm:ss"
    NDatePickerType.Date -> "yyyy-MM-dd"
}

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
) {
    val theme = LocalNTheme.current
    val resolvedSize = nSize(size)
    val height = theme.heightFor(resolvedSize)
    val fontSize = theme.fontSizeFor(resolvedSize)
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val borderColor = when {
        disabled -> theme.colors.border
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
) {
    val theme = LocalNTheme.current
    val resolvedSize = nSize(size)
    val height = theme.heightFor(resolvedSize)
    val fontSize = theme.fontSizeFor(resolvedSize)
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Row(
        modifier
            .height(height)
            .clip(RoundedCornerShape(theme.borderRadius))
            .background(if (disabled) theme.colors.inputDisabled else theme.colors.input)
            .nBorder(if (hovered) theme.colors.primaryHover else theme.colors.border, 1.dp, theme.borderRadius)
            .hoverable(interaction, enabled = !disabled)
            .nClickable(interaction, enabled = !disabled, onClick = onClick)
            .padding(start = 12.dp, end = if (clearable) 6.dp else 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
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
        NIcon(
            NIcons.To,
            modifier = Modifier.padding(horizontal = 6.dp),
            size = 14.dp,
            color = theme.colors.text3,
        )
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
        NIcon(icon, size = 14.dp, color = theme.colors.icon, contentDescription = description)
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
 * One month/year panel used by the date picker. Month and year views are
 * internal to the panel so each range panel can navigate independently.
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
    onYearChange: (Int) -> Unit,
    onMonthChange: (Int) -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onSelectMonth: (Int) -> Unit,
    onSelectYear: (Int) -> Unit,
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    val effectiveFirstDayOfWeek = firstDayOfWeek ?: locale.date.firstDayOfWeek
    var view by remember(type) {
        mutableStateOf(
            when (type) {
                NDatePickerType.Month -> NDatePanelView.Month
                NDatePickerType.Year -> NDatePanelView.Year
                else -> NDatePanelView.Day
            },
        )
    }
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
                    NPanelNavButton(NIcons.ChevronLeft, locale.date.prevYear) { onYearChange(year - 1) }
                    NPanelNavButton(NIcons.ChevronLeft, locale.date.prevMonth) { previousMonth() }
                    Row(
                        Modifier.weight(1f),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        NPanelLabel(locale.date.months[month - 1]) { view = NDatePanelView.Month }
                        Spacer(Modifier.width(2.dp))
                        NPanelLabel(year.toString()) { view = NDatePanelView.Year }
                    }
                    NPanelNavButton(NIcons.ChevronRight, locale.date.nextMonth) { nextMonth() }
                    NPanelNavButton(NIcons.ChevronRight, locale.date.nextYear) { onYearChange(year + 1) }
                }
                NDatePanelView.Month -> {
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
                for (row in 0 until 4) {
                    Row(Modifier.fillMaxWidth()) {
                        for (column in 0 until 3) {
                            val value = row * 3 + column + 1
                            NChoiceCell(
                                label = locale.date.monthsShort[value - 1],
                                selected = selectedMonth == value,
                                disabled = isMonthDisabled(year, value),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    if (type == NDatePickerType.Month) {
                                        onSelectMonth(value)
                                    } else {
                                        onMonthChange(value)
                                        view = NDatePanelView.Day
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
                                label = value.toString(),
                                selected = selected?.year == value || (type == NDatePickerType.Year && year == value),
                                disabled = isYearDisabled(value),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    if (type == NDatePickerType.Year) {
                                        onSelectYear(value)
                                    } else {
                                        onYearChange(value)
                                        view = if (type == NDatePickerType.Month) {
                                            NDatePanelView.Month
                                        } else {
                                            NDatePanelView.Month
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
internal fun NPanelFooterButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Box(
        Modifier
            .clip(RoundedCornerShape(theme.borderRadiusSmall))
            .background(if (hovered && enabled) theme.colors.hover else Color.Transparent)
            .hoverable(interaction, enabled = enabled)
            .nClickable(interaction, enabled = enabled, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        NText(
            text,
            size = 13.sp,
            color = if (enabled) theme.colors.primary else theme.colors.textDisabled,
        )
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

/**
 * Date picker with date, month and year precision.
 *
 * The value is a [LocalDate]; [NDatePickerType.Month] resolves to the first day
 * of the chosen month and [NDatePickerType.Year] to January 1st. Use the
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
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    val effectiveFirstDayOfWeek = firstDayOfWeek ?: locale.date.firstDayOfWeek
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val effectiveType = if (type == NDatePickerType.Datetime) NDatePickerType.Date else type
    val pattern = format ?: nDateDefaultFormat(effectiveType)
    val resolvedSize = nSize(size)
    val resolvedPlaceholder = placeholder ?: when (effectiveType) {
        NDatePickerType.Month -> locale.date.selectMonth
        NDatePickerType.Year -> locale.date.selectYear
        else -> locale.date.selectDate
    }
    var visible by remember { mutableStateOf(false) }
    val seed = value ?: today
    var panelYear by remember(visible) { mutableStateOf(seed.year) }
    var panelMonth by remember(visible) { mutableStateOf(seed.month.number) }

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

    Box(modifier) {
        NDateInputTrigger(
            text = value?.let { nDateFormat(it, null, pattern, locale.date) },
            placeholder = resolvedPlaceholder,
            disabled = disabled,
            size = resolvedSize,
            clearable = clearable,
            onClick = { visible = !visible },
            onClear = {
                visible = false
                onValueChange(null)
                onClear?.invoke()
            },
        )
        NPopoverSurface(
            visible = visible && !disabled,
            onDismiss = { visible = false },
            placement = NPlacement.BottomStart,
            padding = PaddingValues(0.dp),
        ) {
            Row(Modifier.padding(12.dp)) {
                if (shortcuts != null) {
                    Box(Modifier.width(148.dp).height(264.dp).padding(end = 8.dp)) {
                        NScrollbar(Modifier.fillMaxSize()) {
                            Column(Modifier.fillMaxWidth()) {
                                shortcuts { selected ->
                                    onValueChange(selected)
                                    visible = false
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
                        selected = value,
                        rangeStart = null,
                        rangeEnd = null,
                        firstDayOfWeek = effectiveFirstDayOfWeek,
                        today = today,
                        isDateDisabled = dateDisabled,
                        isMonthDisabled = monthDisabled,
                        isYearDisabled = yearDisabled,
                        onYearChange = { panelYear = it },
                        onMonthChange = { panelMonth = it },
                        onSelectDate = { selected ->
                            onValueChange(selected)
                            visible = false
                        },
                        onSelectMonth = { selectedMonth ->
                            onValueChange(LocalDate(panelYear, selectedMonth, 1))
                            visible = false
                        },
                        onSelectYear = { selectedYear ->
                            onValueChange(LocalDate(selectedYear, 1, 1))
                            visible = false
                        },
                    )
                    if (effectiveType == NDatePickerType.Date) {
                        NPanelFooter {
                            NPanelFooterButton(locale.date.now, enabled = !dateDisabled(today)) {
                                onValueChange(today)
                                visible = false
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Date-time picker. The panel edits a draft date and time and commits on
 * confirm, matching the `datetime` type of naive-ui.
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
    min: LocalDateTime? = null,
    max: LocalDateTime? = null,
    firstDayOfWeek: Int? = null,
    isDateDisabled: ((LocalDate) -> Boolean)? = null,
    shortcuts: (@Composable ColumnScope.(select: (LocalDateTime) -> Unit) -> Unit)? = null,
    onClear: (() -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    val effectiveFirstDayOfWeek = firstDayOfWeek ?: locale.date.firstDayOfWeek
    val zone = remember { TimeZone.currentSystemDefault() }
    val now = remember { Clock.System.now().toLocalDateTime(zone) }
    val today = remember { Clock.System.todayIn(zone) }
    val pattern = format ?: nDateDefaultFormat(NDatePickerType.Datetime)
    val resolvedSize = nSize(size)
    val resolvedPlaceholder = placeholder ?: "${locale.date.selectDate} & ${locale.date.selectTime}"
    var visible by remember { mutableStateOf(false) }

    Box(modifier) {
        NDateInputTrigger(
            text = value?.let { nDateFormat(it.date, it.time, pattern, locale.date) },
            placeholder = resolvedPlaceholder,
            disabled = disabled,
            size = resolvedSize,
            clearable = clearable,
            onClick = { visible = !visible },
            onClear = {
                visible = false
                onValueChange(null)
                onClear?.invoke()
            },
        )
        NPopoverSurface(
            visible = visible && !disabled,
            onDismiss = { visible = false },
            placement = NPlacement.BottomStart,
            padding = PaddingValues(0.dp),
        ) {
            var panelYear by remember { mutableStateOf((value?.date ?: today).year) }
            var panelMonth by remember { mutableStateOf((value?.date ?: today).month.number) }
            var draftDate by remember { mutableStateOf(value?.date ?: today) }
            var hour by remember { mutableStateOf(value?.time?.hour ?: now.hour) }
            var minute by remember { mutableStateOf(value?.time?.minute ?: now.minute) }
            var second by remember { mutableStateOf(value?.time?.second ?: now.second) }

            val draftTime = LocalTime(hour, minute, second)
            val draft = LocalDateTime(draftDate, draftTime)
            val valid = (min == null || draft >= min) && (max == null || draft <= max)
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

            Row(Modifier.padding(12.dp)) {
                if (shortcuts != null) {
                    Box(Modifier.width(160.dp).height(250.dp).padding(end = 8.dp)) {
                        NScrollbar(Modifier.fillMaxSize()) {
                            Column(Modifier.fillMaxWidth()) {
                                shortcuts { selected ->
                                    onValueChange(selected)
                                    visible = false
                                }
                            }
                        }
                    }
                    Box(Modifier.width(1.dp).height(250.dp).background(theme.colors.divider))
                    Spacer(Modifier.width(12.dp))
                }
                Column(Modifier.width(443.dp)) {
                    Row {
                        NDateCalendarPanel(
                            year = panelYear,
                            month = panelMonth,
                            type = NDatePickerType.Date,
                            selected = draftDate,
                            rangeStart = null,
                            rangeEnd = null,
                            firstDayOfWeek = effectiveFirstDayOfWeek,
                            today = today,
                            isDateDisabled = dateDisabled,
                            isMonthDisabled = monthDisabled,
                            isYearDisabled = yearDisabled,
                            onYearChange = { panelYear = it },
                            onMonthChange = { panelMonth = it },
                            onSelectDate = { draftDate = it },
                            onSelectMonth = { panelMonth = it },
                            onSelectYear = { panelYear = it },
                        )
                        Spacer(Modifier.width(8.dp))
                        Box(Modifier.width(1.dp).height(224.dp).background(theme.colors.divider))
                        Spacer(Modifier.width(8.dp))
                        NTimeColumns(
                            hour = hour,
                            minute = minute,
                            second = second,
                            hourStep = 1,
                            minuteStep = 1,
                            secondStep = 1,
                            showSeconds = true,
                            use12Hours = use12Hours,
                            columnHeight = 224.dp,
                            onHourChange = { hour = it },
                            onMinuteChange = { minute = it },
                            onSecondChange = { second = it },
                        )
                    }
                    NPanelFooter {
                        NPanelFooterButton(locale.date.now) {
                            draftDate = today
                            hour = now.hour
                            minute = now.minute
                            second = now.second
                        }
                        NPanelFooterButton(locale.cancel) { visible = false }
                        NPanelFooterButton(locale.confirm, enabled = valid) {
                            onValueChange(draft)
                            visible = false
                        }
                    }
                }
            }
        }
    }
}

/**
 * Range date picker. The two panels navigate independently; the value is
 * committed when confirm is pressed.
 */
@Composable
fun NDatePicker(
    value: Pair<LocalDate, LocalDate>?,
    onValueChange: (Pair<LocalDate, LocalDate>?) -> Unit,
    modifier: Modifier = Modifier,
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
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    val effectiveFirstDayOfWeek = firstDayOfWeek ?: locale.date.firstDayOfWeek
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val pattern = format ?: "yyyy-MM-dd"
    val resolvedSize = nSize(size)
    val resolvedStart = startPlaceholder ?: locale.date.startDate
    val resolvedEnd = endPlaceholder ?: locale.date.endDate
    var visible by remember { mutableStateOf(false) }

    val startSeed = value?.first ?: today
    val endSeed = value?.second ?: startSeed.plus(1, DateTimeUnit.MONTH)
    var startYear by remember(visible) { mutableStateOf(startSeed.year) }
    var startMonth by remember(visible) { mutableStateOf(startSeed.month.number) }
    var endYear by remember(visible) { mutableStateOf(endSeed.year) }
    var endMonth by remember(visible) { mutableStateOf(endSeed.month.number) }

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

    Box(modifier) {
        NDateRangeTrigger(
            startText = value?.first?.let { nDateFormat(it, null, pattern, locale.date) },
            endText = value?.second?.let { nDateFormat(it, null, pattern, locale.date) },
            startPlaceholder = resolvedStart,
            endPlaceholder = resolvedEnd,
            disabled = disabled,
            size = resolvedSize,
            clearable = clearable,
            onClick = { visible = !visible },
            onClear = {
                visible = false
                onValueChange(null)
                onClear?.invoke()
            },
        )
        NPopoverSurface(
            visible = visible && !disabled,
            onDismiss = { visible = false },
            placement = NPlacement.BottomStart,
            padding = PaddingValues(0.dp),
        ) {
            var draftStart by remember { mutableStateOf(value?.first) }
            var draftEnd by remember { mutableStateOf(value?.second) }

            fun select(date: LocalDate) {
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

            Row(Modifier.padding(12.dp)) {
                if (shortcuts != null) {
                    Box(Modifier.width(150.dp).height(264.dp).padding(end = 8.dp)) {
                        NScrollbar(Modifier.fillMaxSize()) {
                            Column(Modifier.fillMaxWidth()) {
                                shortcuts { pair ->
                                    onValueChange(pair)
                                    visible = false
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
                            type = NDatePickerType.Date,
                            selected = null,
                            rangeStart = draftStart,
                            rangeEnd = draftEnd,
                            firstDayOfWeek = effectiveFirstDayOfWeek,
                            today = today,
                            isDateDisabled = dateDisabled,
                            isMonthDisabled = monthDisabled,
                            isYearDisabled = yearDisabled,
                            onYearChange = { startYear = it },
                            onMonthChange = { startMonth = it },
                            onSelectDate = { select(it) },
                            onSelectMonth = { startMonth = it },
                            onSelectYear = { startYear = it },
                        )
                        Spacer(Modifier.width(8.dp))
                        Box(Modifier.width(1.dp).height(264.dp).background(theme.colors.divider))
                        Spacer(Modifier.width(8.dp))
                        NDateCalendarPanel(
                            year = endYear,
                            month = endMonth,
                            type = NDatePickerType.Date,
                            selected = null,
                            rangeStart = draftStart,
                            rangeEnd = draftEnd,
                            firstDayOfWeek = effectiveFirstDayOfWeek,
                            today = today,
                            isDateDisabled = dateDisabled,
                            isMonthDisabled = monthDisabled,
                            isYearDisabled = yearDisabled,
                            onYearChange = { endYear = it },
                            onMonthChange = { endMonth = it },
                            onSelectDate = { select(it) },
                            onSelectMonth = { endMonth = it },
                            onSelectYear = { endYear = it },
                        )
                    }
                    NPanelFooter {
                        NPanelFooterButton(locale.cancel) { visible = false }
                        val rangeStart = draftStart
                        val rangeEnd = draftEnd
                        NPanelFooterButton(
                            locale.confirm,
                            enabled = rangeStart != null && rangeEnd != null,
                        ) {
                            if (rangeStart != null && rangeEnd != null) {
                                onValueChange(rangeStart to rangeEnd)
                                visible = false
                            }
                        }
                    }
                }
            }
        }
    }
}
