package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.theme.LocalNTheme
import kotlin.math.max
import kotlin.math.roundToInt

/** Split axis. */
enum class NSplitDirection { Horizontal, Vertical }

/**
 * Two-pane resizable split. [sizes] holds the fraction of the first and second
 * pane; dragging the divider updates the first fraction within [min] and
 * [max]. The panes fill the size given by [modifier].
 */
@Composable
fun NSplit(
    modifier: Modifier = Modifier,
    direction: NSplitDirection = NSplitDirection.Horizontal,
    sizes: List<Float> = listOf(0.5f, 0.5f),
    min: Float = 0f,
    max: Float = 1f,
    disabled: Boolean = false,
    resizeTriggerSize: Dp = 3.dp,
    onSizesChange: ((List<Float>) -> Unit)? = null,
    onDragEnd: ((List<Float>) -> Unit)? = null,
    pane1: @Composable () -> Unit,
    pane2: @Composable () -> Unit,
) {
    val theme = LocalNTheme.current
    val horizontal = direction == NSplitDirection.Horizontal
    var fraction by remember {
        mutableStateOf((sizes.firstOrNull() ?: 0.5f).coerceIn(min, max))
    }
    LaunchedEffect(sizes, min, max) {
        sizes.firstOrNull()?.let { fraction = it.coerceIn(min, max) }
    }
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val triggerColor = if (hovered && !disabled) theme.colors.primaryHover else theme.colors.border

    BoxWithConstraints(modifier) {
        val density = LocalDensity.current
        val usablePx = with(density) {
            if (horizontal) (maxWidth - resizeTriggerSize).toPx() else (maxHeight - resizeTriggerSize).toPx()
        }.coerceAtLeast(1f)
        val currentSizes = { listOf(fraction, 1f - fraction) }

        Layout(
            modifier = Modifier.fillMaxSize(),
            content = {
                Box { pane1() }
                Box { pane2() }
                Box(
                    Modifier
                        .background(triggerColor)
                        .hoverable(interaction, enabled = !disabled)
                        .pointerHoverIcon(PointerIcon.Hand)
                        .pointerInput(direction, disabled, usablePx, min, max) {
                            if (disabled) return@pointerInput
                            detectDragGestures(
                                onDragEnd = { onDragEnd?.invoke(currentSizes()) },
                            ) { change, dragAmount ->
                                change.consume()
                                val delta = if (horizontal) dragAmount.x else dragAmount.y
                                val next = (fraction + delta / usablePx).coerceIn(min, max)
                                fraction = next
                                onSizesChange?.invoke(currentSizes())
                            }
                        },
                )
            },
        ) { measurables, constraints ->
            val trigger = resizeTriggerSize.roundToPx()
            val mainMax = if (horizontal) constraints.maxWidth else constraints.maxHeight
            val crossBounded = if (horizontal) constraints.hasBoundedHeight else constraints.hasBoundedWidth
            val crossMax = if (horizontal) constraints.maxHeight else constraints.maxWidth
            val usable = (mainMax - trigger).coerceAtLeast(0)
            val first = (usable * fraction).roundToInt().coerceIn(0, usable)
            val second = usable - first
            fun paneConstraints(main: Int): Constraints = if (horizontal) {
                Constraints(
                    minWidth = main,
                    maxWidth = main,
                    minHeight = if (crossBounded) crossMax else 0,
                    maxHeight = crossMax,
                )
            } else {
                Constraints(
                    minWidth = if (crossBounded) crossMax else 0,
                    maxWidth = crossMax,
                    minHeight = main,
                    maxHeight = main,
                )
            }
            val firstPlaceable = measurables[0].measure(paneConstraints(first))
            val secondPlaceable = measurables[1].measure(paneConstraints(second))
            val crossSize = if (crossBounded) {
                if (horizontal) constraints.maxHeight else constraints.maxWidth
            } else {
                if (horizontal) {
                    max(firstPlaceable.height, secondPlaceable.height)
                } else {
                    max(firstPlaceable.width, secondPlaceable.width)
                }
            }
            val divider = measurables[2].measure(
                if (horizontal) {
                    Constraints.fixed(trigger, crossSize)
                } else {
                    Constraints.fixed(crossSize, trigger)
                },
            )
            val width = if (horizontal) {
                if (constraints.hasBoundedWidth) constraints.maxWidth else first + trigger + second
            } else {
                crossSize
            }
            val height = if (horizontal) {
                crossSize
            } else {
                if (constraints.hasBoundedHeight) constraints.maxHeight else first + trigger + second
            }
            layout(width, height) {
                if (horizontal) {
                    firstPlaceable.placeRelative(0, 0)
                    divider.placeRelative(first, 0)
                    secondPlaceable.placeRelative(first + trigger, 0)
                } else {
                    firstPlaceable.placeRelative(0, 0)
                    divider.placeRelative(0, first)
                    secondPlaceable.placeRelative(0, first + trigger)
                }
            }
        }
    }
}
