package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NTimePicker
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/** Value granularity supported by [NCalendar]. */
enum class NCalendarType { Date, Datetime }

private enum class NCalendarView { Day, Month, Year }

private fun nCalendarMonthDays(year: Int, month: Int): Int {
    val first = LocalDate(year, month, 1)
    return first.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY).day
}

private fun nCalendarGridStart(year: Int, month: Int, firstDayOfWeek: Int): LocalDate {
    val first = LocalDate(year, month, 1)
    val offset = (first.dayOfWeek.isoDayNumber - firstDayOfWeek.coerceIn(1, 7) + 7) % 7
    return first.minus(offset, DateTimeUnit.DAY)
}

private fun nCalendarGridCellCount(year: Int, month: Int, firstDayOfWeek: Int): Int {
    val first = LocalDate(year, month, 1)
    val offset = (first.dayOfWeek.isoDayNumber - firstDayOfWeek.coerceIn(1, 7) + 7) % 7
    return ((offset + nCalendarMonthDays(year, month) + 6) / 7) * 7
}

@Composable
private fun NCalendarNavButton(
    previous: Boolean,
    description: String,
    onClick: () -> Unit,
) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Box(
        Modifier
            .height(28.dp)
            .width(28.dp)
            .clip(RoundedCornerShape(theme.borderRadiusSmall))
            .background(if (hovered) theme.colors.hover else Color.Transparent)
            .hoverable(interaction)
            .nClickable(interaction, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        NIcon(
            if (previous) NIcons.ChevronLeft else NIcons.ChevronRight,
            size = 16.dp,
            color = theme.colors.icon,
            contentDescription = description,
        )
    }
}

@Composable
private fun NCalendarHeaderLabel(text: String, onClick: () -> Unit) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Box(
        Modifier
            .clip(RoundedCornerShape(theme.borderRadiusSmall))
            .background(if (hovered) theme.colors.hover else Color.Transparent)
            .hoverable(interaction)
            .nClickable(interaction, onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        NText(text, size = 15.sp, strong = true, color = theme.colors.text1)
    }
}

@Composable
private fun NCalendarChoiceCell(
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
            .padding(3.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(theme.borderRadius))
            .background(
                when {
                    selected -> theme.colors.primary.copy(alpha = if (theme.isDark) 0.3f else 0.12f)
                    hovered && !disabled -> theme.colors.hover
                    else -> Color.Transparent
                },
            )
            .then(
                if (selected) Modifier.nBorder(theme.colors.primary, 1.dp, theme.borderRadius) else Modifier,
            )
            .hoverable(interaction, enabled = !disabled)
            .nClickable(interaction, enabled = !disabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        NText(
            label,
            size = 13.sp,
            color = when {
                disabled -> theme.colors.textDisabled
                selected -> theme.colors.primary
                else -> theme.colors.text2
            },
            maxLines = 1,
        )
    }
}

@Composable
private fun NCalendarDayCell(
    date: LocalDate,
    inMonth: Boolean,
    selected: Boolean,
    isToday: Boolean,
    disabled: Boolean,
    cellHeight: Dp,
    onClick: () -> Unit,
    daySlot: (@Composable (LocalDate) -> Unit)?,
) {
    val theme = LocalNTheme.current
    val interaction = remember(date) { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val shape = RoundedCornerShape(theme.borderRadius)
    val border = when {
        selected -> Modifier.nBorder(theme.colors.primary, 1.dp, theme.borderRadius)
        isToday -> Modifier.nBorder(theme.colors.primary.copy(alpha = 0.55f), 1.dp, theme.borderRadius)
        else -> Modifier
    }
    Box(
        Modifier
            .height(cellHeight)
            .padding(2.dp)
            .clip(shape)
            .background(
                when {
                    selected -> theme.colors.primary.copy(alpha = if (theme.isDark) 0.3f else 0.12f)
                    hovered && !disabled -> theme.colors.hover
                    else -> Color.Transparent
                },
            )
            .then(border)
            .hoverable(interaction, enabled = !disabled)
            .nClickable(interaction, enabled = !disabled, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
    ) {
        Column(Modifier.fillMaxSize()) {
            NText(
                date.day.toString(),
                size = 13.sp,
                color = when {
                    disabled -> theme.colors.textDisabled
                    selected -> theme.colors.primary
                    !inMonth -> theme.colors.text3
                    else -> theme.colors.text2
                },
                maxLines = 1,
            )
            if (daySlot != null) {
                Box(Modifier.fillMaxWidth().weight(1f).padding(top = 2.dp)) { daySlot(date) }
            }
        }
    }
}

/**
 * Calendar with a navigable month panel.
 *
 * The grid always contains whole Monday-to-Sunday rows (or the locale's
 * [firstDayOfWeek]), and days outside the panel month are still rendered but
 * dimmed. [time] is only used by [NCalendarType.Datetime], where a time picker
 * is appended below the grid.
 */
@Composable
fun NCalendar(
    value: LocalDate,
    onValueChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    type: NCalendarType = NCalendarType.Date,
    defaultPanelDate: LocalDate? = null,
    showMonth: Boolean = true,
    showYear: Boolean = true,
    firstDayOfWeek: Int? = null,
    isDateDisabled: ((LocalDate) -> Boolean)? = null,
    time: LocalTime? = null,
    onTimeChange: ((LocalTime) -> Unit)? = null,
    onPanelChange: (year: Int, month: Int) -> Unit = { _, _ -> },
    header: (@Composable (year: Int, month: Int) -> Unit)? = null,
    day: (@Composable (LocalDate) -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    val effectiveFirstDayOfWeek = firstDayOfWeek ?: locale.date.firstDayOfWeek
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val initialPanel = defaultPanelDate ?: value
    var panelYear by remember { mutableStateOf(initialPanel.year) }
    var panelMonth by remember { mutableStateOf(initialPanel.month.number) }
    var view by remember { mutableStateOf(NCalendarView.Day) }
    var lastSyncedValue by remember { mutableStateOf(value) }

    fun goTo(year: Int, month: Int) {
        val safeMonth = month.coerceIn(1, 12)
        panelYear = year
        panelMonth = safeMonth
        onPanelChange(year, safeMonth)
    }

    LaunchedEffect(value) {
        if (value != lastSyncedValue) {
            lastSyncedValue = value
            if (value.year != panelYear || value.month.number != panelMonth) {
                panelYear = value.year
                panelMonth = value.month.number
                onPanelChange(value.year, value.month.number)
            }
        }
    }

    fun previous(): Unit = when (view) {
        NCalendarView.Day -> if (panelMonth == 1) goTo(panelYear - 1, 12) else goTo(panelYear, panelMonth - 1)
        NCalendarView.Month -> goTo(panelYear - 1, panelMonth)
        NCalendarView.Year -> goTo(panelYear - 12, panelMonth)
    }

    fun next(): Unit = when (view) {
        NCalendarView.Day -> if (panelMonth == 12) goTo(panelYear + 1, 1) else goTo(panelYear, panelMonth + 1)
        NCalendarView.Month -> goTo(panelYear + 1, panelMonth)
        NCalendarView.Year -> goTo(panelYear + 12, panelMonth)
    }

    Column(
        modifier
            .clip(RoundedCornerShape(theme.borderRadius))
            .background(theme.colors.card)
            .nBorder(theme.colors.border, 1.dp, theme.borderRadius)
            .padding(12.dp),
    ) {
        Row(
            Modifier.fillMaxWidth().height(40.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NCalendarNavButton(true, locale.prev) { previous() }
            Row(
                Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                when (view) {
                    NCalendarView.Day -> {
                        if (header != null) {
                            header(panelYear, panelMonth)
                        } else {
                            if (showMonth) {
                                NCalendarHeaderLabel(locale.date.months[panelMonth - 1]) {
                                    view = NCalendarView.Month
                                }
                            }
                            if (showMonth && showYear) Spacer(Modifier.width(4.dp))
                            if (showYear) {
                                NCalendarHeaderLabel(panelYear.toString()) {
                                    view = NCalendarView.Year
                                }
                            }
                            if (!showMonth && !showYear) {
                                NText("$panelYear-${panelMonth.toString().padStart(2, '0')}", size = 15.sp, strong = true, color = theme.colors.text1)
                            }
                        }
                    }
                    NCalendarView.Month -> {
                        if (showYear) {
                            NCalendarHeaderLabel(panelYear.toString()) {
                                view = NCalendarView.Year
                            }
                        } else {
                            NText(panelYear.toString(), size = 15.sp, strong = true, color = theme.colors.text1)
                        }
                    }
                    NCalendarView.Year -> {
                        val decadeStart = panelYear - ((panelYear % 10 + 10) % 10) - 1
                        NText(
                            "$decadeStart - ${decadeStart + 11}",
                            size = 15.sp,
                            strong = true,
                            color = theme.colors.text1,
                        )
                    }
                }
            }
            NCalendarNavButton(false, locale.next) { next() }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(theme.colors.divider))
        Spacer(Modifier.height(6.dp))

        when (view) {
            NCalendarView.Day -> {
                Row(Modifier.fillMaxWidth()) {
                    locale.date.weekdayHeaders(effectiveFirstDayOfWeek).forEach { label ->
                        Box(Modifier.weight(1f).height(28.dp), contentAlignment = Alignment.Center) {
                            NText(label, size = 12.sp, color = theme.colors.text3, maxLines = 1)
                        }
                    }
                }
                val cellHeight = if (day != null) 76.dp else 56.dp
                val cellCount = nCalendarGridCellCount(panelYear, panelMonth, effectiveFirstDayOfWeek)
                val gridStart = nCalendarGridStart(panelYear, panelMonth, effectiveFirstDayOfWeek)
                for (row in 0 until cellCount / 7) {
                    Row(Modifier.fillMaxWidth()) {
                        for (column in 0 until 7) {
                            val date = gridStart.plus(row * 7 + column, DateTimeUnit.DAY)
                            Box(Modifier.weight(1f)) {
                                NCalendarDayCell(
                                    date = date,
                                    inMonth = date.year == panelYear && date.month.number == panelMonth,
                                    selected = value == date,
                                    isToday = date == today,
                                    disabled = isDateDisabled?.invoke(date) == true,
                                    cellHeight = cellHeight,
                                    onClick = { onValueChange(date) },
                                    daySlot = day,
                                )
                            }
                        }
                    }
                }
            }
            NCalendarView.Month -> {
                for (row in 0 until 4) {
                    Row(Modifier.fillMaxWidth()) {
                        for (column in 0 until 3) {
                            val month = row * 3 + column + 1
                            NCalendarChoiceCell(
                                label = locale.date.months[month - 1],
                                selected = value.year == panelYear && value.month.number == month,
                                disabled = false,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    goTo(panelYear, month)
                                    view = NCalendarView.Day
                                },
                            )
                        }
                    }
                }
            }
            NCalendarView.Year -> {
                val decadeStart = panelYear - ((panelYear % 10 + 10) % 10) - 1
                for (row in 0 until 4) {
                    Row(Modifier.fillMaxWidth()) {
                        for (column in 0 until 3) {
                            val year = decadeStart + row * 3 + column
                            NCalendarChoiceCell(
                                label = year.toString(),
                                selected = value.year == year,
                                disabled = false,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    goTo(year, panelMonth)
                                    view = NCalendarView.Month
                                },
                            )
                        }
                    }
                }
            }
        }

        if (type == NCalendarType.Datetime && onTimeChange != null) {
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(theme.colors.divider))
            Row(
                Modifier.fillMaxWidth().padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                NText(locale.date.selectTime, size = 13.sp, color = theme.colors.text3)
                NTimePicker(
                    value = time,
                    onValueChange = { onTimeChange(it ?: LocalTime(0, 0)) },
                    size = NSize.Small,
                    clearable = false,
                    modifier = Modifier.width(168.dp),
                )
            }
        }
    }
}
