package cn.enaium.naiveui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.nSize
import kotlin.math.abs
import kotlin.math.roundToInt

private fun NSize.sliderHandle(): Dp = when (this) {
    NSize.Tiny -> 14.dp
    NSize.Small -> 16.dp
    NSize.Medium -> 18.dp
    NSize.Large -> 22.dp
}

private fun defaultSliderFormat(value: Float): String {
    val rounded = (value * 100f).roundToInt() / 100f
    return if (rounded == rounded.toInt().toFloat()) rounded.toInt().toString() else rounded.toString()
}

/**
 * Slider with custom drawn rail, handle and marks.
 *
 * Range mode is enabled by supplying [secondValue] together with
 * [onSecondValueChange]. Values snap to [step], or to the nearest mark when
 * [snapToMarks] is set.
 */
@Composable
fun NSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    min: Float = 0f,
    max: Float = 100f,
    step: Float = 1f,
    vertical: Boolean = false,
    disabled: Boolean = false,
    showTooltip: Boolean = false,
    tooltipFormatter: ((Float) -> String)? = null,
    marks: Map<Float, String>? = null,
    size: NSize? = null,
    secondValue: Float? = null,
    onSecondValueChange: ((Float) -> Unit)? = null,
    reverse: Boolean = false,
    snapToMarks: Boolean = false,
    keyboard: Boolean = true,
    onDragStart: (() -> Unit)? = null,
    onDragEnd: (() -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val colors = theme.colors
    val density = LocalDensity.current
    val measurer = rememberTextMeasurer()
    val handle = nSize(size).sliderHandle()
    val handlePx = with(density) { handle.toPx() }
    val railThickness = 4.dp
    val railPx = with(density) { railThickness.toPx() }
    val labelStyle = theme.typography.body.copy(
        color = colors.text3,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
    )
    val markActiveStyle = labelStyle.copy(color = colors.text2)

    val markLabels = marks ?: emptyMap()
    val maxLabelWidthPx = remember(markLabels, theme) {
        markLabels.values.maxOfOrNull { measurer.measure(it, labelStyle).size.width } ?: 0
    }
    val labelArea = with(density) { maxLabelWidthPx.toDp() }
    val marksArea = 22.dp
    val canvasWidth = if (vertical && markLabels.isNotEmpty()) handle + labelArea + 14.dp else handle
    val horizontalHeight = handle + if (markLabels.isNotEmpty()) marksArea else 0.dp

    val enabled = !disabled
    val range = secondValue != null
    val low = if (range) minOf(value, secondValue!!) else value
    val high = if (range) maxOf(value, secondValue!!) else value

    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val focused by interaction.collectIsFocusedAsState()
    var dragging by remember { mutableStateOf(false) }
    var draggedHandle by remember { mutableStateOf(0) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    val currentValue by rememberUpdatedState(value)
    val currentSecond by rememberUpdatedState(secondValue)
    val latestOnValueChange by rememberUpdatedState(onValueChange)
    val latestOnSecondValueChange by rememberUpdatedState(onSecondValueChange)
    val latestOnDragStart by rememberUpdatedState(onDragStart)
    val latestOnDragEnd by rememberUpdatedState(onDragEnd)
    val latestFormatter by rememberUpdatedState(tooltipFormatter)

    fun fractionOf(v: Float): Float {
        val span = max - min
        val raw = if (span > 0f) ((v - min) / span).coerceIn(0f, 1f) else 0f
        return if (reverse) 1f - raw else raw
    }

    fun valueAt(position: Offset): Float {
        val width = canvasSize.width.toFloat()
        val height = canvasSize.height.toFloat()
        val raw = if (vertical) {
            val available = (height - handlePx).coerceAtLeast(1f)
            1f - ((position.y - handlePx / 2f) / available).coerceIn(0f, 1f)
        } else {
            val available = (width - handlePx).coerceAtLeast(1f)
            ((position.x - handlePx / 2f) / available).coerceIn(0f, 1f)
        }
        val fraction = if (reverse) 1f - raw else raw
        return min + fraction * (max - min)
    }

    fun snap(v: Float): Float {
        val lowerBound = minOf(min, max)
        val upperBound = maxOf(min, max)
        val clamped = v.coerceIn(lowerBound, upperBound)
        if (snapToMarks && markLabels.isNotEmpty()) {
            return markLabels.keys.minByOrNull { abs(it - clamped) } ?: clamped
        }
        if (step <= 0f) return clamped
        val steps = ((clamped - min) / step).roundToInt()
        return (min + steps * step).coerceIn(lowerBound, upperBound)
    }

    fun commit(target: Int, raw: Float) {
        val snapped = snap(raw)
        if (target == 0) {
            if (snapped != currentValue) latestOnValueChange(snapped)
        } else {
            val upper = currentSecond ?: return
            if (snapped != upper) latestOnSecondValueChange?.invoke(snapped)
        }
    }

    fun pickHandle(v: Float): Int {
        val second = currentSecond ?: return 0
        return if (abs(v - currentValue) <= abs(v - second)) 0 else 1
    }

    val gestureKeys = listOf(min, max, step, vertical, reverse, snapToMarks, marks, disabled)

    val pointerModifiers = if (!enabled) {
        Modifier
    } else {
        Modifier
            .pointerInput(gestureKeys) {
                detectTapGestures { position ->
                    val target = pickHandle(valueAt(position))
                    draggedHandle = target
                    commit(target, valueAt(position))
                }
            }
            .pointerInput(gestureKeys) {
                var target = 0
                detectDragGestures(
                    onDragStart = { position ->
                        target = pickHandle(valueAt(position))
                        draggedHandle = target
                        dragging = true
                        commit(target, valueAt(position))
                        latestOnDragStart?.invoke()
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        commit(target, valueAt(change.position))
                    },
                    onDragEnd = {
                        dragging = false
                        latestOnDragEnd?.invoke()
                    },
                    onDragCancel = { dragging = false },
                )
            }
    }

    val keyboardModifier = if (enabled && keyboard) {
        Modifier.onKeyEvent { event ->
            if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
            val delta = when (event.key) {
                Key.DirectionRight, Key.DirectionUp -> if (step > 0f) step else (max - min) / 100f
                Key.DirectionLeft, Key.DirectionDown -> if (step > 0f) -step else -(max - min) / 100f
                Key.PageUp -> (max - min) / 10f
                Key.PageDown -> -(max - min) / 10f
                else -> null
            }
            if (delta != null) {
                if (draggedHandle == 0 || currentSecond == null) {
                    commit(0, currentValue + delta)
                } else {
                    commit(1, currentSecond!! + delta)
                }
                true
            } else {
                when (event.key) {
                    Key.MoveHome -> {
                        commit(draggedHandle, min)
                        true
                    }
                    Key.MoveEnd -> {
                        commit(draggedHandle, max)
                        true
                    }
                    else -> false
                }
            }
        }
    } else {
        Modifier
    }

    val canvasModifier = (if (vertical) {
        Modifier.fillMaxHeight().heightIn(min = 120.dp).width(canvasWidth)
    } else {
        Modifier.fillMaxWidth().height(horizontalHeight)
    })
        .onSizeChanged { canvasSize = it }
        .semantics {
            if (!range) {
                progressBarRangeInfo = ProgressBarRangeInfo(value, min..max)
            }
        }
        .then(keyboardModifier)
        .focusable(interactionSource = interaction, enabled = enabled)
        .hoverable(interaction)
        .then(pointerModifiers)

    Canvas(modifier.then(canvasModifier)) {
        val width = this.size.width
        val height = this.size.height
        val alpha = if (disabled) theme.opacityDisabled else 1f
        val railColor = colors.rail.copy(alpha = colors.rail.alpha * alpha)
        val fillColor = colors.primary.copy(alpha = alpha)
        val trackCenter = handlePx / 2f
        val trackLength = (if (vertical) height - handlePx else width - handlePx).coerceAtLeast(0f)
        val centerY = if (vertical) height / 2f else trackCenter

        fun positionFor(v: Float): Float {
            val fraction = fractionOf(v)
            return if (vertical) {
                height - trackCenter - fraction * trackLength
            } else {
                trackCenter + fraction * trackLength
            }
        }

        // Rail.
        if (vertical) {
            drawRoundRect(
                color = railColor,
                topLeft = Offset(trackCenter - railPx / 2f, trackCenter),
                size = Size(railPx, trackLength),
                cornerRadius = CornerRadius(railPx / 2f),
            )
        } else {
            drawRoundRect(
                color = railColor,
                topLeft = Offset(trackCenter, centerY - railPx / 2f),
                size = Size(trackLength, railPx),
                cornerRadius = CornerRadius(railPx / 2f),
            )
        }

        // Fill.
        val lowPos = positionFor(low)
        val highPos = positionFor(high)
        val fillStart = minOf(lowPos, highPos)
        val fillEnd = maxOf(lowPos, highPos)
        if (fillEnd > fillStart) {
            if (vertical) {
                drawRoundRect(
                    color = fillColor,
                    topLeft = Offset(trackCenter - railPx / 2f, fillStart),
                    size = Size(railPx, fillEnd - fillStart),
                    cornerRadius = CornerRadius(railPx / 2f),
                )
            } else {
                drawRoundRect(
                    color = fillColor,
                    topLeft = Offset(fillStart, centerY - railPx / 2f),
                    size = Size(fillEnd - fillStart, railPx),
                    cornerRadius = CornerRadius(railPx / 2f),
                )
            }
        }

        // Marks.
        if (markLabels.isNotEmpty()) {
            markLabels.forEach { (markValue, label) ->
                val active = markValue in low..high
                val pos = positionFor(markValue)
                val dotColor = if (active) fillColor else colors.card
                val dotBorder = if (active) fillColor else railColor
                val center = if (vertical) Offset(trackCenter, pos) else Offset(pos, centerY)
                drawCircle(dotColor, radius = 4.dp.toPx(), center = center)
                drawCircle(dotBorder, radius = 4.dp.toPx(), center = center, style = Stroke(2.dp.toPx()))
                val layout = measurer.measure(label, if (active) markActiveStyle else labelStyle)
                if (vertical) {
                    drawText(
                        layout,
                        topLeft = Offset(
                            trackCenter + handlePx / 2f + 6.dp.toPx(),
                            pos - layout.size.height / 2f,
                        ),
                    )
                } else {
                    drawText(
                        layout,
                        topLeft = Offset(
                            pos - layout.size.width / 2f,
                            handlePx + 2.dp.toPx(),
                        ),
                    )
                }
            }
        }

        // Handles.
        fun drawHandle(v: Float, isActiveHandle: Boolean) {
            val center = if (vertical) {
                Offset(trackCenter, positionFor(v))
            } else {
                Offset(positionFor(v), centerY)
            }
            if (isActiveHandle && focused && enabled) {
                drawCircle(
                    color = colors.primary.copy(alpha = 0.2f),
                    radius = handlePx / 2f + 4.dp.toPx(),
                    center = center,
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
            drawCircle(
                color = Color.Black.copy(alpha = 0.2f * alpha),
                radius = handlePx / 2f + 1.dp.toPx(),
                center = center,
            )
            drawCircle(
                color = if (disabled) colors.base.copy(alpha = 0.8f) else Color.White,
                radius = handlePx / 2f,
                center = center,
            )
        }
        drawHandle(value, draggedHandle == 0 || !range)
        secondValue?.let { drawHandle(it, draggedHandle == 1) }

        // Tooltip.
        val tooltipValue: Float? = when {
            range -> if (dragging) (if (draggedHandle == 0) value else secondValue) else null
            showTooltip || dragging || (hovered && enabled) -> value
            else -> null
        }
        if (tooltipValue != null) {
            val text = latestFormatter?.invoke(tooltipValue) ?: defaultSliderFormat(tooltipValue)
            val layout = measurer.measure(
                text,
                TextStyle(color = Color.White, fontSize = 12.sp, fontFamily = theme.typography.fontFamily),
            )
            val paddingX = 8.dp.toPx()
            val paddingY = 4.dp.toPx()
            val boxWidth = layout.size.width + paddingX * 2
            val boxHeight = layout.size.height + paddingY * 2
            val pos = positionFor(tooltipValue)
            val anchorX = if (vertical) trackCenter else pos
            val anchorY = if (vertical) pos else centerY
            val left = (anchorX - boxWidth / 2f).coerceIn(0f, (width - boxWidth).coerceAtLeast(0f))
            val top = (anchorY - handlePx / 2f - 8.dp.toPx() - boxHeight).coerceAtLeast(0f)
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.85f),
                topLeft = Offset(left, top),
                size = Size(boxWidth, boxHeight),
                cornerRadius = CornerRadius(theme.borderRadius.toPx()),
            )
            drawText(
                layout,
                topLeft = Offset(left + paddingX, top + paddingY),
            )
        }
    }
}
