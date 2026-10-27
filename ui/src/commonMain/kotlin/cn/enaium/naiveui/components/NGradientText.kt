package cn.enaium.naiveui.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import cn.enaium.naiveui.theme.LocalNTheme
import kotlin.math.cos
import kotlin.math.sin

/** Semantic presets available to [NGradientText]. */
enum class NGradientTextType { Primary, Info, Success, Warning, Error }

/**
 * A linear gradient expressed as CSS-like colour stops.
 *
 * [angle] follows the CSS convention: `0` points up, `90` points right and
 * `252` (the naive-ui default) points down-left.
 */
@Immutable
data class NGradient(
    val colors: List<Color>,
    val angle: Float = 252f,
)

private fun gradientBrush(colors: List<Color>, angle: Float): Brush {
    if (colors.isEmpty()) return Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
    val resolved = if (colors.size == 1) listOf(colors[0], colors[0]) else colors
    val radians = angle * (kotlin.math.PI / 180.0).toFloat()
    val dx = sin(radians)
    val dy = -cos(radians)
    val length = 400f
    return Brush.linearGradient(
        colors = resolved,
        start = Offset.Zero,
        end = Offset(dx * length, dy * length),
    )
}

/**
 * Text painted with a gradient shader.
 *
 * Either use one of the semantic [type] presets or pass custom [gradient] /
 * [brush] values.
 */
@Composable
fun NGradientText(
    text: String,
    modifier: Modifier = Modifier,
    type: NGradientTextType = NGradientTextType.Primary,
    gradient: NGradient? = null,
    brush: Brush? = null,
    size: TextUnit? = null,
    strong: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
) {
    val theme = LocalNTheme.current
    val typeColor = when (type) {
        NGradientTextType.Primary -> theme.colors.primary
        NGradientTextType.Info -> theme.colors.info
        NGradientTextType.Success -> theme.colors.success
        NGradientTextType.Warning -> theme.colors.warning
        NGradientTextType.Error -> theme.colors.error
    }
    val resolvedBrush = brush ?: gradient?.let { gradientBrush(it.colors, it.angle) }
        ?: gradientBrush(listOf(typeColor.copy(alpha = 0.6f), typeColor), 252f)
    val style = TextStyle(
        brush = resolvedBrush,
        fontFamily = theme.typography.fontFamily,
        fontSize = size ?: theme.typography.fontSize.large,
        lineHeight = (size ?: theme.typography.fontSize.large) * theme.typography.lineHeight,
        fontWeight = if (strong) theme.typography.fontWeightStrong else theme.typography.fontWeight,
    )
    BasicText(
        text = text,
        modifier = modifier,
        style = style,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}
