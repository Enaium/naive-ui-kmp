package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NPopoverSurface
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.nSize
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

private fun nTimeValues(step: Int, count: Int): List<Int> {
    val safeStep = step.coerceAtLeast(1)
    return (0 until count step safeStep).toList()
}

private fun nTimePad(value: Int): String = value.toString().padStart(2, '0')

@Composable
private fun NTimeColumn(
    values: List<Int>,
    selected: Int,
    label: (Int) -> String,
    enabled: Boolean,
    isDisabled: (Int) -> Boolean,
    height: Dp,
    width: Dp,
    onSelect: (Int) -> Unit,
) {
    val theme = LocalNTheme.current
    val scrollState = androidx.compose.foundation.rememberScrollState()
    val density = LocalDensity.current
    val itemHeight = 30.dp
    LaunchedEffect(selected, values) {
        val index = values.indexOf(selected)
        if (index >= 0) {
            val viewport = with(density) { height.toPx() }
            val itemPx = with(density) { itemHeight.toPx() }
            val topSpacer = with(density) { 72.dp.toPx() }
            val target = topSpacer + itemPx * index - viewport / 2f + itemPx / 2f
            scrollState.scrollTo(target.toInt().coerceAtLeast(0))
        }
    }
    Box(Modifier.width(width).height(height)) {
        NScrollbar(Modifier.fillMaxSize(), scrollState = scrollState) {
            Column(Modifier.fillMaxWidth()) {
                Spacer(Modifier.height(72.dp))
                values.forEach { value ->
                    val active = value == selected
                    val itemDisabled = !enabled || isDisabled(value)
                    val interaction = remember(value, itemDisabled) { MutableInteractionSource() }
                    val hovered by interaction.collectIsHoveredAsState()
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                            .clip(RoundedCornerShape(theme.borderRadius))
                            .background(
                                when {
                                    active -> theme.colors.primary.copy(alpha = if (theme.isDark) 0.3f else 0.12f)
                                    hovered && !itemDisabled -> theme.colors.hover
                                    else -> Color.Transparent
                                },
                            )
                            .hoverable(interaction, enabled = !itemDisabled)
                            .nClickable(interaction, enabled = !itemDisabled) { onSelect(value) },
                        contentAlignment = Alignment.Center,
                    ) {
                        NText(
                            label(value),
                            size = 13.sp,
                            color = when {
                                itemDisabled -> theme.colors.textDisabled
                                active -> theme.colors.primary
                                else -> theme.colors.text2
                            },
                            maxLines = 1,
                        )
                    }
                }
                Spacer(Modifier.height(72.dp))
            }
        }
    }
}

/**
 * The hour/minute/second columns shared by [NTimePicker] and the date-time
 * picker. Values outside [hourStep]/[minuteStep]/[secondStep] are omitted.
 */
@Composable
internal fun NTimeColumns(
    hour: Int,
    minute: Int,
    second: Int,
    hourStep: Int,
    minuteStep: Int,
    secondStep: Int,
    showSeconds: Boolean,
    use12Hours: Boolean,
    modifier: Modifier = Modifier,
    columnHeight: Dp = 200.dp,
    enabled: Boolean = true,
    isHourDisabled: (Int) -> Boolean = { false },
    isMinuteDisabled: (Int) -> Boolean = { false },
    isSecondDisabled: (Int) -> Boolean = { false },
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    onSecondChange: (Int) -> Unit,
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    val hours24 = nTimeValues(hourStep, 24)
    val hours12 = hours24.map { ((it + 11) % 12) + 1 }.distinct()
    val minutes = nTimeValues(minuteStep, 60)
    val seconds = nTimeValues(secondStep, 60)
    val displayHour = if (hour % 12 == 0) 12 else hour % 12
    val isPm = hour >= 12

    Row(modifier, verticalAlignment = Alignment.Top) {
        NTimeColumn(
            values = if (use12Hours) hours12 else hours24,
            selected = if (use12Hours) displayHour else hour,
            label = { if (use12Hours) it.toString() else nTimePad(it) },
            enabled = enabled,
            isDisabled = { value ->
                val actual = if (use12Hours) (value % 12) + if (isPm) 12 else 0 else value
                isHourDisabled(actual)
            },
            height = columnHeight,
            width = 48.dp,
            onSelect = { value ->
                if (use12Hours) {
                    onHourChange((value % 12) + if (isPm) 12 else 0)
                } else {
                    onHourChange(value)
                }
            },
        )
        NTimeDivider(columnHeight)
        NTimeColumn(
            values = minutes,
            selected = minute,
            label = { nTimePad(it) },
            enabled = enabled,
            isDisabled = isMinuteDisabled,
            height = columnHeight,
            width = 48.dp,
            onSelect = onMinuteChange,
        )
        if (showSeconds) {
            NTimeDivider(columnHeight)
            NTimeColumn(
                values = seconds,
                selected = second,
                label = { nTimePad(it) },
                enabled = enabled,
                isDisabled = isSecondDisabled,
                height = columnHeight,
                width = 48.dp,
                onSelect = onSecondChange,
            )
        }
        if (use12Hours) {
            NTimeDivider(columnHeight)
            NTimeColumn(
                values = listOf(0, 1),
                selected = if (isPm) 1 else 0,
                label = { if (it == 0) locale.date.am else locale.date.pm },
                enabled = enabled,
                isDisabled = { false },
                height = columnHeight,
                width = 50.dp,
                onSelect = { marker ->
                    val base = hour % 12
                    onHourChange(base + if (marker == 1) 12 else 0)
                },
            )
        }
    }
}

@Composable
private fun NTimeDivider(height: Dp) {
    val theme = LocalNTheme.current
    Box(Modifier.width(1.dp).height(height).background(theme.colors.divider))
}

/**
 * Time picker with scrollable hour, minute and second columns.
 *
 * Column clicks update the value immediately, like naive-ui. `Now` resets the
 * draft to the current wall-clock time and `Confirm` closes the panel after
 * invoking [onConfirm].
 */
@Composable
fun NTimePicker(
    value: LocalTime?,
    onValueChange: (LocalTime?) -> Unit,
    modifier: Modifier = Modifier,
    format: String? = null,
    hourStep: Int = 1,
    minuteStep: Int = 1,
    secondStep: Int = 1,
    disabled: Boolean = false,
    clearable: Boolean = true,
    size: NSize? = null,
    placeholder: String? = null,
    use12Hours: Boolean = false,
    showSeconds: Boolean = true,
    isHourDisabled: ((Int) -> Boolean)? = null,
    isMinuteDisabled: ((Int) -> Boolean)? = null,
    isSecondDisabled: ((Int) -> Boolean)? = null,
    onClear: (() -> Unit)? = null,
    onConfirm: ((LocalTime) -> Unit)? = null,
) {
    val locale = LocalNLocale.current
    val zone = remember { TimeZone.currentSystemDefault() }
    val pattern = format ?: if (showSeconds) "HH:mm:ss" else "HH:mm"
    val secondVisible = format?.contains('s') ?: showSeconds
    val resolvedSize = nSize(size)
    var visible by remember { mutableStateOf(false) }

    Box(modifier) {
        NDateInputTrigger(
            text = value?.let { nDateFormat(null, it, pattern, locale.date) },
            placeholder = placeholder ?: locale.date.selectTime,
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
            val now = remember { Clock.System.now().toLocalDateTime(zone).time }
            var hour by remember { mutableStateOf(value?.hour ?: now.hour) }
            var minute by remember { mutableStateOf(value?.minute ?: now.minute) }
            var second by remember { mutableStateOf(value?.second ?: now.second) }

            fun emit(newHour: Int = hour, newMinute: Int = minute, newSecond: Int = second) {
                hour = newHour
                minute = newMinute
                second = newSecond
                onValueChange(LocalTime(newHour, newMinute, newSecond))
            }

            val columnCount = if (secondVisible) 3 else 2
            val panelWidth = 48.dp * columnCount + 1.dp * (columnCount - 1) +
                if (use12Hours) 51.dp else 0.dp
            Column(Modifier.padding(12.dp).width(panelWidth)) {
                NTimeColumns(
                    hour = hour,
                    minute = minute,
                    second = second,
                    hourStep = hourStep,
                    minuteStep = minuteStep,
                    secondStep = secondStep,
                    showSeconds = secondVisible,
                    use12Hours = use12Hours,
                    columnHeight = 200.dp,
                    isHourDisabled = isHourDisabled ?: { false },
                    isMinuteDisabled = isMinuteDisabled ?: { false },
                    isSecondDisabled = isSecondDisabled ?: { false },
                    onHourChange = { emit(newHour = it) },
                    onMinuteChange = { emit(newMinute = it) },
                    onSecondChange = { emit(newSecond = it) },
                )
                NPanelFooter {
                    NPanelFooterButton(locale.date.now) {
                        emit(now.hour, now.minute, now.second)
                        visible = false
                    }
                    NPanelFooterButton(locale.confirm) {
                        val time = LocalTime(hour, minute, second)
                        onValueChange(time)
                        onConfirm?.invoke(time)
                        visible = false
                    }
                }
            }
        }
    }
}
