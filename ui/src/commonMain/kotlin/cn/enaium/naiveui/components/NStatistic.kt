package cn.enaium.naiveui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.internal.LocalNContentColor
import cn.enaium.naiveui.internal.LocalNContentTextStyle
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.theme.LocalNTheme
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.round

/**
 * Statistic used to display a single prominent number.
 *
 * [value] accepts either a [Number] or a preformatted [String]. When a
 * [Number] is supplied together with [precision], the value is rounded to that
 * many decimal places and padded with trailing zeros.
 */
@Composable
fun NStatistic(
    modifier: Modifier = Modifier,
    label: String? = null,
    value: Any? = null,
    precision: Int? = null,
    valueStyle: TextStyle? = null,
    tabularNumbers: Boolean = false,
    labelContent: (@Composable () -> Unit)? = null,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val baseValueStyle = theme.typography.style(24.sp, color = theme.colors.text2)
    val mergedValueStyle = valueStyle ?: baseValueStyle
    val resolvedValueStyle = if (tabularNumbers) {
        mergedValueStyle.copy(fontFeatureSettings = "tnum")
    } else {
        mergedValueStyle
    }
    val labelTextStyle = theme.typography.style(theme.typography.fontSize.medium, color = theme.colors.text3)

    Column(modifier.fillMaxWidth()) {
        if (label != null || labelContent != null) {
            Box(Modifier.fillMaxWidth()) {
                if (labelContent != null) {
                    CompositionLocalProvider(
                        LocalNContentColor provides theme.colors.text3,
                        LocalNContentTextStyle provides labelTextStyle,
                    ) { labelContent() }
                } else {
                    NBaseText(label.orEmpty(), labelTextStyle)
                }
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (prefix != null) {
                Box(Modifier.padding(end = 4.dp)) {
                    CompositionLocalProvider(
                        LocalNContentColor provides theme.colors.text2,
                        LocalNContentTextStyle provides resolvedValueStyle,
                    ) { prefix() }
                }
            }
            CompositionLocalProvider(
                LocalNContentColor provides theme.colors.text2,
                LocalNContentTextStyle provides resolvedValueStyle,
            ) {
                when {
                    value != null -> NBaseText(
                        text = formatStatisticValue(value, precision),
                        style = resolvedValueStyle,
                        color = resolvedValueStyle.color,
                    )
                    content != null -> content()
                    else -> Unit
                }
            }
            if (suffix != null) {
                Box(Modifier.padding(start = 4.dp)) {
                    CompositionLocalProvider(
                        LocalNContentColor provides theme.colors.text2,
                        LocalNContentTextStyle provides resolvedValueStyle,
                    ) { suffix() }
                }
            }
        }
    }
}

private fun formatStatisticValue(value: Any, precision: Int?): String = when (value) {
    is Number -> formatNumber(value, precision)
    else -> value.toString()
}

private fun formatNumber(value: Number, precision: Int?): String {
    if (precision == null) return plainNumber(value)
    val digits = precision.coerceIn(0, 9)
    val factor = 10.0.pow(digits)
    val number = value.toDouble()
    if (!number.isFinite()) return value.toString()
    val scaledDouble = abs(number) * factor
    if (scaledDouble >= Long.MAX_VALUE.toDouble()) return plainNumber(value)
    val scaled = round(scaledDouble).toLong()
    val factorLong = factor.toLong()
    val whole = scaled / factorLong
    val fraction = scaled % factorLong
    val sign = if (number < 0 && scaled != 0L) "-" else ""
    return if (digits == 0) {
        "$sign$whole"
    } else {
        "$sign$whole.${fraction.toString().padStart(digits, '0')}"
    }
}

private fun plainNumber(value: Number): String = when (value) {
    is Byte, is Short, is Int, is Long -> value.toString()
    is Float, is Double -> {
        val number = value.toDouble()
        if (number.isFinite() && number == floor(number) && abs(number) < 1e15) {
            number.toLong().toString()
        } else {
            value.toString()
        }
    }
    else -> value.toString()
}
