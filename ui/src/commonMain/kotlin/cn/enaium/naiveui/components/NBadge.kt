package cn.enaium.naiveui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.theme.LocalNTheme

/** Semantic color of a badge. */
enum class NBadgeType { Default, Info, Success, Warning, Error }

/**
 * Small count or dot marker attached to the top end of its content.
 *
 * Set [inline] to render the marker without a wrapped child, matching
 * naive-ui's "as is" mode.
 */
@Composable
fun NBadge(
    modifier: Modifier = Modifier,
    value: Any? = null,
    max: Int? = null,
    dot: Boolean = false,
    showZero: Boolean = false,
    color: Color? = null,
    type: NBadgeType = NBadgeType.Default,
    processing: Boolean = false,
    offset: DpOffset = DpOffset.Zero,
    show: Boolean = true,
    inline: Boolean = false,
    valueContent: (@Composable () -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val theme = LocalNTheme.current
    val badgeColor = color ?: when (type) {
        NBadgeType.Default, NBadgeType.Error -> theme.colors.error
        NBadgeType.Info -> theme.colors.info
        NBadgeType.Success -> theme.colors.success
        NBadgeType.Warning -> theme.colors.warning
    }
    val numericValue = when (value) {
        is Number -> value.toDouble()
        is String -> value.toDoubleOrNull()
        else -> null
    }
    val hasValue = value != null && value.toString().isNotEmpty()
    val visible = show && (
        dot ||
            valueContent != null ||
            (hasValue && !(!showZero && numericValue != null && numericValue <= 0.0))
        )

    val transition = rememberInfiniteTransition()
    val wave by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, delayMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
    )

    val badge: @Composable () -> Unit = {
        if (dot) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(badgeColor))
        } else {
            Box(
                Modifier
                    .height(18.dp)
                    .defaultMinSize(minWidth = 18.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(badgeColor)
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (valueContent != null) {
                    valueContent()
                } else {
                    NBaseText(
                        text = formatBadgeValue(value, max),
                        style = TextStyle(
                            fontFamily = theme.typography.fontFamily,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = Color.White,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                    )
                }
            }
        }
    }

    val waveModifier = if (processing) {
        Modifier.drawBehind {
            val spread = 5.dp.toPx() * wave
            val baseAlpha = if (theme.isDark) 0.35f else 0.5f
            drawRoundRect(
                color = badgeColor.copy(alpha = baseAlpha * (1f - wave)),
                topLeft = Offset(-spread, -spread),
                size = Size(size.width + spread * 2, size.height + spread * 2),
                cornerRadius = CornerRadius(size.height / 2 + spread),
            )
        }
    } else {
        Modifier
    }

    if (inline) {
        Box(modifier.then(waveModifier)) { badge() }
    } else {
        Box(modifier) {
            content()
            if (visible) {
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .graphicsLayer {
                            translationX = size.width * (if (dot) 1f else 0.5f) + offset.x.toPx()
                            translationY = -size.height / 2f + offset.y.toPx()
                        }
                        .then(waveModifier),
                ) {
                    badge()
                }
            }
        }
    }
}

private fun formatBadgeValue(value: Any?, max: Int?): String {
    if (value is Number) {
        val number = value.toDouble()
        if (max != null && number > max) return "$max+"
        return if (number % 1.0 == 0.0) number.toLong().toString() else number.toString()
    }
    return value?.toString() ?: ""
}
