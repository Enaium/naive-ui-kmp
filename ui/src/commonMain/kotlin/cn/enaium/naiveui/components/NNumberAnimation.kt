package cn.enaium.naiveui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cn.enaium.naiveui.internal.LocalNContentColor
import cn.enaium.naiveui.internal.LocalNContentTextStyle
import cn.enaium.naiveui.theme.LocalNTheme
import kotlin.math.abs
import kotlin.math.round

/** Imperative handle for replaying [NNumberAnimation]. */
@Stable
class NNumberAnimationState internal constructor() {
    internal var playToken by mutableIntStateOf(0)
        private set

    /** Restarts the animation from `from` to `to`. */
    fun play() {
        playToken++
    }
}

@Composable
fun rememberNNumberAnimationState(): NNumberAnimationState = remember { NNumberAnimationState() }

private val NNumberEaseOut = Easing { t ->
    val inverted = 1f - t
    1f - inverted * inverted * inverted * inverted * inverted
}

private fun nPow10(exponent: Int): Long {
    var value = 1L
    repeat(exponent) { value *= 10L }
    return value
}

private fun nGroupDigits(digits: String, separator: Char): String {
    if (digits.length <= 3) return digits
    val builder = StringBuilder(digits.length + digits.length / 3)
    val firstGroup = digits.length % 3
    var index = 0
    if (firstGroup > 0) {
        builder.append(digits, 0, firstGroup)
        index = firstGroup
    }
    while (index < digits.length) {
        if (builder.isNotEmpty()) builder.append(separator)
        builder.append(digits, index, index + 3)
        index += 3
    }
    return builder.toString()
}

private fun nFormatAnimatedNumber(
    value: Float,
    precision: Int,
    showSeparator: Boolean,
    groupingSeparator: Char,
    decimalSeparator: Char,
): String {
    val places = precision.coerceIn(0, 10)
    val factor = nPow10(places)
    val magnitude = abs(value.toDouble())
    val scaled = round(magnitude * factor).toLong()
    val integer = (scaled / factor).toString()
    val fraction = if (places > 0) (scaled % factor).toString().padStart(places, '0') else ""
    val sign = if (value < 0f && scaled != 0L) "-" else ""
    return buildString {
        append(sign)
        append(if (showSeparator) nGroupDigits(integer, groupingSeparator) else integer)
        if (places > 0) {
            append(decimalSeparator)
            append(fraction)
        }
    }
}

private fun nLocaleSeparators(locale: String?): Pair<Char, Char> {
    if (locale == null) return ',' to '.'
    val normalized = locale.replace('_', '-').lowercase()
    return when {
        normalized.startsWith("de") ||
            normalized.startsWith("nl") ||
            normalized.startsWith("tr") ||
            normalized.startsWith("id") ||
            normalized.startsWith("vi") ||
            normalized.startsWith("pt") ||
            normalized.startsWith("es") ||
            normalized.startsWith("it") ||
            normalized.startsWith("da") ||
            normalized.startsWith("ro") -> '.' to ','
        normalized.startsWith("fr") ||
            normalized.startsWith("ru") ||
            normalized.startsWith("pl") ||
            normalized.startsWith("cs") ||
            normalized.startsWith("sk") ||
            normalized.startsWith("sv") ||
            normalized.startsWith("nb") ||
            normalized.startsWith("nn") ||
            normalized.startsWith("no") ||
            normalized.startsWith("fi") ||
            normalized.startsWith("uk") -> ' ' to ','
        else -> ',' to '.'
    }
}

/**
 * Animates a number between [from] and [to] with an ease-out curve.
 *
 * The value is formatted with [precision] decimals, optional thousands
 * separators, a [prefix] and a [suffix]. Use [locale] to pick locale-specific
 * separators (for example `de-DE` groups with `.` and uses `,` for decimals)
 * or override them explicitly. Call [NNumberAnimationState.play] to replay the
 * animation, or use [active] for declarative control.
 */
@Composable
fun NNumberAnimation(
    to: Double,
    modifier: Modifier = Modifier,
    from: Double = 0.0,
    precision: Int = 0,
    duration: Int = 2000,
    active: Boolean = true,
    showSeparator: Boolean = false,
    prefix: String? = null,
    suffix: String? = null,
    locale: String? = null,
    separator: Char? = null,
    decimalSeparator: Char? = null,
    state: NNumberAnimationState = rememberNNumberAnimationState(),
    onFinish: (() -> Unit)? = null,
) {
    val animatable = remember { Animatable(from.toFloat()) }
    val latestOnFinish by rememberUpdatedState(onFinish)
    LaunchedEffect(from, to, active, duration, state.playToken) {
        val shouldAnimate = active || state.playToken > 0
        if (!shouldAnimate || from == to) {
            animatable.snapTo(from.toFloat())
            return@LaunchedEffect
        }
        animatable.snapTo(from.toFloat())
        animatable.animateTo(
            targetValue = to.toFloat(),
            animationSpec = tween(
                durationMillis = duration.coerceAtLeast(0),
                easing = NNumberEaseOut,
            ),
        )
        latestOnFinish?.invoke()
    }
    val theme = LocalNTheme.current
    val inherited = LocalNContentTextStyle.current ?: theme.typography.body
    val inheritedColor = LocalNContentColor.current
    val resolvedColor = inheritedColor.takeIf { it != Color.Unspecified }
        ?: inherited.color.takeIf { it != Color.Unspecified }
        ?: theme.colors.text2
    val localeSeparators = remember(locale) { nLocaleSeparators(locale) }
    val formatted = nFormatAnimatedNumber(
        value = animatable.value,
        precision = precision,
        showSeparator = showSeparator,
        groupingSeparator = separator ?: localeSeparators.first,
        decimalSeparator = decimalSeparator ?: localeSeparators.second,
    )
    BasicText(
        text = buildString {
            if (!prefix.isNullOrEmpty()) append(prefix)
            append(formatted)
            if (!suffix.isNullOrEmpty()) append(suffix)
        },
        modifier = modifier,
        style = inherited.copy(color = resolvedColor),
    )
}
