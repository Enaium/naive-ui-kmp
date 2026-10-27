@file:OptIn(kotlin.time.ExperimentalTime::class)

package cn.enaium.naiveui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.theme.LocalNTheme
import kotlinx.coroutines.delay
import kotlinx.datetime.TimeZone
import kotlin.time.Instant
import kotlin.time.TimeSource

/** Time parts exposed to [NCountdown] format and render callbacks. */
@Immutable
data class NCountdownTimeInfo(
    val days: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val milliseconds: Int,
)

/** Imperative handle for [NCountdown]. */
class NCountdownState internal constructor() {
    internal var version by mutableIntStateOf(0)
        private set

    /** Restarts the countdown from its configured duration or target. */
    fun reset() {
        version += 1
    }
}

/** Creates a [NCountdownState] remembered across recompositions. */
@Composable
fun rememberNCountdownState(): NCountdownState = remember { NCountdownState() }

/**
 * Countdown equivalent to `<n-countdown>`.
 *
 * The duration can be given directly in milliseconds via [duration] or through
 * an absolute [target] epoch millisecond value, a [targetInstant] or a
 * [targetIso] ISO-8601 string.
 * [precision] from 0 to 3 controls the displayed fraction of a second.
 */
@Composable
fun NCountdown(
    duration: Long = 0L,
    modifier: Modifier = Modifier,
    active: Boolean = true,
    precision: Int = 0,
    target: Long? = null,
    targetInstant: Instant? = null,
    targetIso: String? = null,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    onFinish: (() -> Unit)? = null,
    state: NCountdownState = rememberNCountdownState(),
    format: ((NCountdownTimeInfo) -> String)? = null,
    render: (@Composable (NCountdownTimeInfo) -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val resolvedPrecision = precision.coerceIn(0, 3)
    val totalMillis = remember(target, targetInstant, targetIso, duration, timeZone) {
        val targetMillis = when {
            targetInstant != null -> targetInstant.toEpochMilliseconds()
            targetIso != null -> nParseIsoMillis(targetIso, timeZone)
            target != null -> target
            else -> null
        }
        if (targetMillis != null) {
            (targetMillis - nNowMillis()).coerceAtLeast(0L)
        } else {
            duration.coerceAtLeast(0L)
        }
    }

    var remaining by remember { mutableLongStateOf(totalMillis) }
    var finished by remember { mutableStateOf(false) }
    var trackedTotal by remember { mutableLongStateOf(totalMillis) }
    var consumedVersion by remember { mutableIntStateOf(-1) }
    val currentOnFinish by rememberUpdatedState(onFinish)
    val version = state.version

    LaunchedEffect(active, totalMillis, version, resolvedPrecision) {
        if (trackedTotal != totalMillis || consumedVersion != version) {
            trackedTotal = totalMillis
            consumedVersion = version
            remaining = totalMillis
            finished = false
        }
        if (!active) return@LaunchedEffect
        if (remaining <= 0L) {
            if (!finished) {
                finished = true
                currentOnFinish?.invoke()
            }
            return@LaunchedEffect
        }
        val mark = TimeSource.Monotonic.markNow()
        val startedAt = remaining
        var announced = false
        while (true) {
            val left = startedAt - mark.elapsedNow().inWholeMilliseconds
            if (left <= 0L) {
                remaining = 0L
                if (!announced) {
                    announced = true
                    finished = true
                    currentOnFinish?.invoke()
                }
                break
            }
            remaining = left
            delay(tickDelay(resolvedPrecision))
        }
    }

    val info = countdownTimeInfo(remaining, resolvedPrecision)
    val baseStyle = theme.typography.body
    if (render != null) {
        Box(modifier) { render(info) }
    } else {
        val text = format?.invoke(info) ?: defaultCountdownText(info, resolvedPrecision)
        NBaseText(
            text = text,
            style = baseStyle.copy(fontFeatureSettings = "tnum"),
            modifier = modifier,
            color = theme.colors.text2,
        )
    }
}

private fun tickDelay(precision: Int): Long = when (precision) {
    0 -> 1000L
    1 -> 100L
    else -> 16L
}

private fun countdownTimeInfo(remaining: Long, precision: Int): NCountdownTimeInfo {
    val adjusted = when (precision) {
        0 -> remaining + 999L
        1 -> remaining + 99L
        2 -> remaining + 9L
        else -> remaining
    }.coerceAtLeast(0L)
    val milliseconds = (adjusted % 1000L).toInt()
    return NCountdownTimeInfo(
        days = (adjusted / 86_400_000L).toInt(),
        hours = (adjusted / 3_600_000L).toInt(),
        minutes = ((adjusted % 3_600_000L) / 60_000L).toInt(),
        seconds = ((adjusted % 60_000L) / 1000L).toInt(),
        milliseconds = when (precision) {
            0 -> 0
            1 -> (milliseconds / 100) * 100
            2 -> (milliseconds / 10) * 10
            else -> milliseconds
        },
    )
}

private fun defaultCountdownText(info: NCountdownTimeInfo, precision: Int): String {
    val hours = info.hours.toString().padStart(2, '0')
    val minutes = info.minutes.toString().padStart(2, '0')
    val seconds = info.seconds.toString().padStart(2, '0')
    if (precision == 0) return "$hours:$minutes:$seconds"
    val fraction = (info.milliseconds / when (precision) {
        1 -> 100
        2 -> 10
        else -> 1
    }).toString().padStart(precision, '0')
    return "$hours:$minutes:$seconds.$fraction"
}
