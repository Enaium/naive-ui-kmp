@file:OptIn(kotlin.time.ExperimentalTime::class)

package cn.enaium.naiveui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import cn.enaium.naiveui.internal.LocalNContentColor
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NDateLocale
import cn.enaium.naiveui.theme.NLocale
import kotlinx.coroutines.delay
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.Padding
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.Instant

/** How [NTime] renders its value. */
enum class NTimeType { Relative, Date, DateTime }

/**
 * Formatted time equivalent to `<n-time>`.
 *
 * [time] is an epoch millisecond value, or a Unix timestamp when [unix] is
 * enabled. An ISO-8601 string can be supplied through [isoTime] instead.
 *
 * `Date` and `DateTime` formats are built from date-fns style tokens
 * (`yyyy`, `MM`, `dd`, `HH`, `mm`, `ss`, `SSS`, `a`, `MMM`, `EEEE`, ...).
 * `Relative` values update once per second while [liveUpdate] is enabled and
 * no fixed [to] target is given.
 */
@Composable
fun NTime(
    time: Long? = null,
    modifier: Modifier = Modifier,
    type: NTimeType = NTimeType.DateTime,
    format: String? = null,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    unix: Boolean = false,
    to: Long? = null,
    isoTime: String? = null,
    isoTo: String? = null,
    liveUpdate: Boolean = true,
    updateIntervalMillis: Long = 1000L,
    color: Color = Color.Unspecified,
    size: TextUnit? = null,
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    var now by remember { mutableLongStateOf(nNowMillis()) }

    LaunchedEffect(type, liveUpdate, updateIntervalMillis, to, isoTo) {
        if (type != NTimeType.Relative || !liveUpdate || to != null || isoTo != null) return@LaunchedEffect
        while (true) {
            now = nNowMillis()
            delay(updateIntervalMillis)
        }
    }

    val resolvedTime = remember(time, isoTime, unix, timeZone) {
        resolveNTimeMillis(time, isoTime, unix, timeZone)
    }
    val resolvedTo = remember(to, isoTo, unix, timeZone) {
        resolveNTimeMillis(to, isoTo, unix, timeZone)
    }
    val from = resolvedTime ?: now
    val target = resolvedTo ?: now

    val rendered = when (type) {
        NTimeType.Relative -> formatNRelativeTime(from, target, locale)
        NTimeType.Date -> formatNDateTime(
            millis = from,
            pattern = format ?: "yyyy-MM-dd",
            timeZone = timeZone,
            dateLocale = locale.date,
        )
        NTimeType.DateTime -> formatNDateTime(
            millis = from,
            pattern = format ?: "yyyy-MM-dd HH:mm:ss",
            timeZone = timeZone,
            dateLocale = locale.date,
        )
    }

    val baseStyle = theme.typography.body
    val style = if (size != null) {
        baseStyle.copy(fontSize = size, lineHeight = size * theme.typography.lineHeight)
    } else {
        baseStyle
    }
    val resolvedColor = when {
        color != Color.Unspecified -> color
        LocalNContentColor.current != Color.Unspecified -> LocalNContentColor.current
        else -> theme.colors.text2
    }
    NBaseText(
        text = rendered,
        style = style,
        modifier = modifier,
        color = resolvedColor,
    )
}

internal fun nNowMillis(): Long = Clock.System.now().toEpochMilliseconds()

internal fun resolveNTimeMillis(
    value: Long?,
    iso: String?,
    unix: Boolean,
    timeZone: TimeZone,
): Long? = when {
    iso != null -> nParseIsoMillis(iso, timeZone)
    value != null -> if (unix) value * 1000L else value
    else -> null
}

internal fun nParseIsoMillis(text: String, timeZone: TimeZone): Long? {
    val value = text.trim()
    if (value.isEmpty()) return null
    runCatching { Instant.parse(value) }.getOrNull()?.let { return it.toEpochMilliseconds() }
    runCatching { LocalDateTime.parse(value).toInstant(timeZone) }.getOrNull()
        ?.let { return it.toEpochMilliseconds() }
    runCatching { LocalDate.parse(value).atStartOfDayIn(timeZone) }.getOrNull()
        ?.let { return it.toEpochMilliseconds() }
    return null
}

private fun formatNDateTime(
    millis: Long,
    pattern: String,
    timeZone: TimeZone,
    dateLocale: NDateLocale,
): String {
    val dateTime = Instant.fromEpochMilliseconds(millis).toLocalDateTime(timeZone)
    return dateTime.format(nBuildDateTimeFormat(pattern, dateLocale))
}

private fun nBuildDateTimeFormat(
    pattern: String,
    dateLocale: NDateLocale,
): DateTimeFormat<LocalDateTime> = LocalDateTime.Format {
    var index = 0
    while (index < pattern.length) {
        val current = pattern[index]
        when (current) {
            'y' -> {
                val count = countPatternRun(pattern, index)
                when {
                    count >= 4 -> year()
                    count == 2 -> yearTwoDigits(2000)
                    else -> year(Padding.NONE)
                }
                index += count
            }
            'M' -> {
                val count = countPatternRun(pattern, index)
                when {
                    count >= 4 -> monthName(nMonthNames(dateLocale.months))
                    count == 3 -> monthName(nMonthNames(dateLocale.monthsShort))
                    count == 2 -> monthNumber(Padding.ZERO)
                    else -> monthNumber(Padding.NONE)
                }
                index += count
            }
            'd' -> {
                val count = countPatternRun(pattern, index)
                day(if (count >= 2) Padding.ZERO else Padding.NONE)
                index += count
            }
            'H' -> {
                val count = countPatternRun(pattern, index)
                hour(if (count >= 2) Padding.ZERO else Padding.NONE)
                index += count
            }
            'h' -> {
                val count = countPatternRun(pattern, index)
                amPmHour(if (count >= 2) Padding.ZERO else Padding.NONE)
                index += count
            }
            'm' -> {
                val count = countPatternRun(pattern, index)
                minute(if (count >= 2) Padding.ZERO else Padding.NONE)
                index += count
            }
            's' -> {
                val count = countPatternRun(pattern, index)
                second(if (count >= 2) Padding.ZERO else Padding.NONE)
                index += count
            }
            'S' -> {
                val count = countPatternRun(pattern, index)
                secondFraction(fixedLength = count.coerceIn(1, 9))
                index += count
            }
            'a' -> {
                amPmMarker(dateLocale.am, dateLocale.pm)
                index++
            }
            'E' -> {
                val count = countPatternRun(pattern, index)
                if (count >= 4) {
                    dayOfWeek(nDayOfWeekNames(dateLocale.weekdays))
                } else {
                    dayOfWeek(nDayOfWeekNames(dateLocale.weekdaysShort))
                }
                index += count
            }
            '\'' -> {
                val end = pattern.indexOf('\'', index + 1)
                if (end == -1) {
                    chars("'")
                    index++
                } else {
                    chars(pattern.substring(index + 1, end))
                    index = end + 1
                }
            }
            else -> {
                chars(current.toString())
                index++
            }
        }
    }
}

private fun countPatternRun(pattern: String, start: Int): Int {
    var index = start
    while (index < pattern.length && pattern[index] == pattern[start]) index++
    return index - start
}

private fun nMonthNames(names: List<String>): MonthNames =
    runCatching { MonthNames(names) }.getOrElse { MonthNames.ENGLISH_FULL }

private fun nDayOfWeekNames(names: List<String>): DayOfWeekNames =
    runCatching { DayOfWeekNames(names) }.getOrElse { DayOfWeekNames.ENGLISH_FULL }

private fun formatNRelativeTime(from: Long, to: Long, locale: NLocale): String {
    val difference = to - from
    val past = difference >= 0L
    val millis = abs(difference)
    val year = 365L * 24 * 60 * 60 * 1000
    val month = 30L * 24 * 60 * 60 * 1000
    val day = 24L * 60 * 60 * 1000
    val hour = 60L * 60 * 1000
    val minute = 60L * 1000

    val amount: Long
    val englishUnit: String
    val localizedUnit: String
    when {
        millis >= year -> {
            amount = millis / year
            englishUnit = "year"
            localizedUnit = "年"
        }
        millis >= month -> {
            amount = millis / month
            englishUnit = "month"
            localizedUnit = "个月"
        }
        millis >= day -> {
            amount = millis / day
            englishUnit = "day"
            localizedUnit = locale.daysSuffix
        }
        millis >= hour -> {
            amount = millis / hour
            englishUnit = "hour"
            localizedUnit = locale.hoursSuffix
        }
        millis >= minute -> {
            amount = millis / minute
            englishUnit = "minute"
            localizedUnit = "分钟"
        }
        else -> {
            amount = millis / 1000
            englishUnit = "second"
            localizedUnit = locale.secondsSuffix
        }
    }
    return if (locale.name.startsWith("zh")) {
        "$amount$localizedUnit${if (past) "前" else "后"}"
    } else {
        val unit = if (amount == 1L) englishUnit else "${englishUnit}s"
        if (past) "$amount $unit ago" else "in $amount $unit"
    }
}
