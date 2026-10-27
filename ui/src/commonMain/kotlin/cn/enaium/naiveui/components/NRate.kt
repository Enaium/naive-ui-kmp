package cn.enaium.naiveui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.nSize
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private fun NSize.rateStar(): Dp = when (this) {
    NSize.Tiny, NSize.Small -> 16.dp
    NSize.Medium -> 20.dp
    NSize.Large -> 24.dp
}

private fun starPath(outer: Float, inner: Float): Path {
    val path = Path()
    val center = Offset(outer, outer)
    for (i in 0 until 10) {
        val radius = if (i % 2 == 0) outer else inner
        val angle = -PI / 2 + i * PI / 5
        val x = center.x + (radius * cos(angle)).toFloat()
        val y = center.y + (radius * sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

private fun DrawScope.drawStar(path: Path, color: Color) {
    drawPath(path, color)
}

/**
 * Rating control backed by star icons.
 *
 * The [value] is expressed in whole stars. For half-star precision use
 * [NRatePrecise]. Clicking the current value clears the rating when
 * [clearable] is enabled.
 */
@Composable
fun NRate(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    count: Int = 5,
    size: NSize? = null,
    color: Color? = null,
    allowHalf: Boolean = false,
    readonly: Boolean = false,
    disabled: Boolean = false,
    clearable: Boolean = false,
    onHoverValueChange: ((Float?) -> Unit)? = null,
    icon: (@Composable (index: Int, active: Boolean) -> Unit)? = null,
) {
    NRateCore(
        value = value.toFloat(),
        onValueChange = { onValueChange(it.toInt()) },
        modifier = modifier,
        count = count,
        size = size,
        color = color,
        allowHalf = allowHalf,
        readonly = readonly,
        disabled = disabled,
        clearable = clearable,
        onHoverValueChange = onHoverValueChange,
        icon = icon,
    )
}

/**
 * Rating control with fractional values, enabling real half-star selection
 * when [allowHalf] is set.
 */
@Composable
fun NRatePrecise(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    count: Int = 5,
    size: NSize? = null,
    color: Color? = null,
    allowHalf: Boolean = true,
    readonly: Boolean = false,
    disabled: Boolean = false,
    clearable: Boolean = false,
    onHoverValueChange: ((Float?) -> Unit)? = null,
    icon: (@Composable (index: Int, active: Boolean) -> Unit)? = null,
) {
    NRateCore(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        count = count,
        size = size,
        color = color,
        allowHalf = allowHalf,
        readonly = readonly,
        disabled = disabled,
        clearable = clearable,
        onHoverValueChange = onHoverValueChange,
        icon = icon,
    )
}

@Composable
private fun NRateCore(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier,
    count: Int,
    size: NSize?,
    color: Color?,
    allowHalf: Boolean,
    readonly: Boolean,
    disabled: Boolean,
    clearable: Boolean,
    onHoverValueChange: ((Float?) -> Unit)?,
    icon: (@Composable (index: Int, active: Boolean) -> Unit)?,
) {
    val theme = LocalNTheme.current
    val colors = theme.colors
    val star = nSize(size).rateStar()
    val gap = 6.dp
    val starPx = with(androidx.compose.ui.platform.LocalDensity.current) { star.toPx() }
    val gapPx = with(androidx.compose.ui.platform.LocalDensity.current) { gap.toPx() }

    val interactive = !readonly && !disabled
    val activeColor = color ?: Color(0xFFFFCC33)
    val inactiveColor = colors.rail
    val starPathCache = remember(star, starPx) { starPath(starPx / 2f, starPx / 2f * 0.48f) }

    var hoverValue by remember { mutableStateOf<Float?>(null) }
    val latestValue by rememberUpdatedState(value)
    val latestOnValueChange by rememberUpdatedState(onValueChange)
    val latestOnHoverValueChange by rememberUpdatedState(onHoverValueChange)

    fun valueAt(x: Float): Float {
        if (count <= 0) return 0f
        val cell = starPx + gapPx
        val index = (x / cell).toInt().coerceIn(0, count - 1)
        val within = x - index * cell
        val base = index + 1f
        return if (allowHalf && within < starPx / 2f) (base - 0.5f).coerceAtLeast(0.5f) else base
    }

    fun commit(next: Float) {
        val clamped = next.coerceIn(0f, count.toFloat())
        latestOnValueChange(clamped)
    }

    Row(
        modifier
            .then(
                if (interactive) {
                    Modifier
                        .pointerInput(interactive, count, allowHalf, starPx, gapPx, clearable) {
                            detectTapGestures { position ->
                                val next = valueAt(position.x)
                                if (clearable && next == latestValue) {
                                    commit(0f)
                                } else {
                                    commit(next)
                                }
                            }
                        }
                        .pointerInput(interactive, count, allowHalf, starPx, gapPx) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    if (event.type == PointerEventType.Exit) {
                                        if (hoverValue != null) {
                                            hoverValue = null
                                            latestOnHoverValueChange?.invoke(null)
                                        }
                                    } else {
                                        val position = event.changes.firstOrNull()?.position
                                        val next = position?.let { valueAt(it.x) }
                                        if (next != hoverValue) {
                                            hoverValue = next
                                            latestOnHoverValueChange?.invoke(next)
                                        }
                                    }
                                }
                            }
                        }
                } else {
                    Modifier
                },
            )
            .then(
                if (interactive) {
                    Modifier
                        .onKeyEvent { event ->
                            if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
                            val delta = when (event.key) {
                                Key.DirectionRight, Key.DirectionUp -> if (allowHalf) 0.5f else 1f
                                Key.DirectionLeft, Key.DirectionDown -> if (allowHalf) -0.5f else -1f
                                else -> 0f
                            }
                            if (delta != 0f) {
                                commit(latestValue + delta)
                                true
                            } else {
                                false
                            }
                        }
                        .focusable()
                } else {
                    Modifier
                },
            )
            .then(if (disabled) Modifier.alpha(theme.opacityDisabled) else Modifier),
        horizontalArrangement = Arrangement.spacedBy(gap),
    ) {
        val displayValue = hoverValue ?: value
        val hoveredIndex = if (count <= 0) {
            null
        } else {
            hoverValue?.let { (kotlin.math.ceil(it).toInt() - 1).coerceIn(0, count - 1) }
        }
        for (index in 0 until count) {
            val hoveredStar = hoveredIndex == index
            Box(
                Modifier
                    .size(star)
                    .clip(CircleShape)
                    .graphicsLayer {
                        val scale = if (hoveredStar && interactive) 1.05f else 1f
                        scaleX = scale
                        scaleY = scale
                    },
                contentAlignment = Alignment.Center,
            ) {
                val amount = (displayValue - index).coerceIn(0f, 1f)
                if (icon != null) {
                    icon(index, amount >= 0.5f)
                } else {
                    Canvas(Modifier.size(star)) {
                        val path = starPathCache
                        drawStar(path, inactiveColor)
                        when {
                            amount >= 0.999f -> drawStar(path, activeColor)
                            amount >= 0.5f -> clipRect(0f, 0f, this.size.width / 2f, this.size.height) {
                                drawStar(path, activeColor)
                            }
                        }
                    }
                }
            }
        }
    }
}
