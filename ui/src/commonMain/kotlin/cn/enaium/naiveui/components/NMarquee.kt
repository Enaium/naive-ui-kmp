package cn.enaium.naiveui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.theme.LocalNTheme

/** Scroll direction of [NMarquee]. */
enum class NMarqueeDirection { Normal, Reverse }

/**
 * Infinite scrolling container.
 *
 * [speed] is expressed in dp per second; pass [duration] (milliseconds per
 * loop) to use a fixed loop time instead. [vertical] switches the scroll axis.
 * The content is duplicated so the loop is seamless.
 */
@Composable
fun NMarquee(
    modifier: Modifier = Modifier,
    vertical: Boolean = false,
    direction: NMarqueeDirection = NMarqueeDirection.Normal,
    speed: Float = 100f,
    duration: Long? = null,
    autoFill: Boolean = false,
    content: @Composable () -> Unit,
) {
    var extentPx by remember { mutableStateOf(0) }
    val density = LocalDensity.current
    val speedPx = with(density) { speed.dp.toPx() }.coerceAtLeast(0.01f)
    val loopMillis = duration?.takeIf { it > 0 }
        ?: ((extentPx / speedPx) * 1000f).toLong().coerceAtLeast(1L)
    val transition = rememberInfiniteTransition()
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = loopMillis.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
    )
    val translation = if (direction == NMarqueeDirection.Normal) {
        -extentPx * progress
    } else {
        -extentPx * (1f - progress)
    }

    BoxWithConstraints(modifier.clipToBounds()) {
        val containerExtent = if (vertical) {
            if (constraints.hasBoundedHeight) constraints.maxHeight else 0
        } else {
            if (constraints.hasBoundedWidth) constraints.maxWidth else 0
        }
        val copyCount = if (autoFill && extentPx > 0 && containerExtent > extentPx) {
            ((containerExtent + extentPx - 1) / extentPx + 1).coerceAtMost(64)
        } else {
            2
        }
        Layout(
            content = {
                repeat(copyCount) { index ->
                    Box(
                        if (index == 0) {
                            Modifier.onSizeChanged {
                                extentPx = if (vertical) it.height else it.width
                            }
                        } else {
                            Modifier
                        },
                    ) {
                        content()
                    }
                }
            },
            modifier = Modifier.graphicsLayer {
                translationX = if (vertical) 0f else translation
                translationY = if (vertical) translation else 0f
            },
        ) { measurables, _ ->
            val placeables = measurables.map { it.measure(Constraints()) }
            if (vertical) {
                val width = placeables.maxOfOrNull { it.width } ?: 0
                val height = placeables.sumOf { it.height }
                layout(width, height) {
                    var y = 0
                    placeables.forEach { placeable ->
                        placeable.placeRelative(0, y)
                        y += placeable.height
                    }
                }
            } else {
                val width = placeables.sumOf { it.width }
                val height = placeables.maxOfOrNull { it.height } ?: 0
                layout(width, height) {
                    var x = 0
                    placeables.forEach { placeable ->
                        placeable.placeRelative(x, 0)
                        x += placeable.width
                    }
                }
            }
        }
    }
}

/** Convenience overload that scrolls a list of plain text entries. */
@Composable
fun NMarquee(
    items: List<String>,
    modifier: Modifier = Modifier,
    vertical: Boolean = false,
    direction: NMarqueeDirection = NMarqueeDirection.Normal,
    speed: Float = 100f,
    duration: Long? = null,
    autoFill: Boolean = false,
    itemSpacing: Dp = 24.dp,
) {
    val theme = LocalNTheme.current
    NMarquee(
        modifier = modifier,
        vertical = vertical,
        direction = direction,
        speed = speed,
        duration = duration,
        autoFill = autoFill,
    ) {
        if (vertical) {
            Column(verticalArrangement = Arrangement.spacedBy(itemSpacing)) {
                items.forEach { item ->
                    NText(text = item, color = theme.colors.text2)
                }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(itemSpacing)) {
                items.forEach { item ->
                    NText(text = item, color = theme.colors.text2, maxLines = 1)
                }
            }
        }
    }
}
