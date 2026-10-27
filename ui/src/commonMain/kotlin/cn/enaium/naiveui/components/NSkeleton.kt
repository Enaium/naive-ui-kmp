package cn.enaium.naiveui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize

/** Shape presets supported by the skeleton placeholder. */
enum class NSkeletonType { Rect, Text, Circle }

/**
 * A twinkling placeholder. When [repeat] is greater than one the placeholder
 * is stacked vertically, which matches the behaviour of text skeletons in
 * naive-ui.
 */
@Composable
fun NSkeleton(
    modifier: Modifier = Modifier,
    type: NSkeletonType = NSkeletonType.Rect,
    width: Dp? = null,
    height: Dp? = null,
    size: NSize? = null,
    round: Boolean = false,
    sharp: Boolean = true,
    repeat: Int = 1,
    animated: Boolean = true,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(if (repeat > 1) 8.dp else 0.dp),
    ) {
        val count = repeat.coerceAtLeast(1)
        for (i in 0 until count) {
            NSkeletonItem(
                modifier = Modifier.fillMaxWidth(),
                type = type,
                width = width,
                height = height,
                size = size,
                round = round,
                sharp = sharp,
                animated = animated,
            )
        }
    }
}

/** A single skeleton bar, the building block used by [NSkeleton]. */
@Composable
fun NSkeletonItem(
    modifier: Modifier = Modifier,
    type: NSkeletonType = NSkeletonType.Rect,
    width: Dp? = null,
    height: Dp? = null,
    size: NSize? = null,
    round: Boolean = false,
    sharp: Boolean = true,
    animated: Boolean = true,
) {
    val theme = LocalNTheme.current
    val sizeHeight = size?.let { theme.heightFor(it) }
    val resolvedWidth: Dp?
    val resolvedHeight: Dp
    if (type == NSkeletonType.Circle) {
        val diameter = width ?: height ?: sizeHeight ?: 16.dp
        resolvedWidth = diameter
        resolvedHeight = diameter
    } else {
        resolvedWidth = width
        resolvedHeight = height ?: sizeHeight ?: 16.dp
    }
    val shape = when {
        type == NSkeletonType.Circle -> CircleShape
        round -> RoundedCornerShape(percent = 50)
        sharp -> RectangleShape
        else -> RoundedCornerShape(theme.borderRadius)
    }
    val colorStart = if (theme.isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFEEEEEE)
    val colorEnd = if (theme.isDark) Color.White.copy(alpha = 0.18f) else Color(0xFFDDDDDD)
    val color = if (animated) {
        val transition = rememberInfiniteTransition()
        val phase by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        )
        lerp(colorStart, colorEnd, phase)
    } else {
        colorStart
    }
    val sized = modifier.fillMaxWidth().height(resolvedHeight)
    val withWidth = if (resolvedWidth != null) sized.width(resolvedWidth) else sized
    androidx.compose.foundation.layout.Box(
        withWidth.clip(shape).background(color, shape),
    )
}
