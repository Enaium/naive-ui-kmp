package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import cn.enaium.naiveui.internal.LocalNContentColor
import cn.enaium.naiveui.internal.LocalNContentTextStyle
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize

@Immutable
internal data class NAvatarGroupContext(
    val size: Dp?,
    val round: Boolean?,
    val bordered: Boolean,
)

internal val LocalNAvatarGroup = staticCompositionLocalOf<NAvatarGroupContext?> { null }

/**
 * Avatar that renders a remote image via Coil, custom content, or a fallback
 * initial. Call [cn.enaium.naiveui.internal.NImageLoaderSetup] at the app root
 * to enable remote images.
 */
@Composable
fun NAvatar(
    modifier: Modifier = Modifier,
    size: Dp? = null,
    src: String? = null,
    text: String? = null,
    color: Color? = null,
    round: Boolean? = null,
    bordered: Boolean? = null,
    contentScale: ContentScale = ContentScale.Crop,
    fallback: (@Composable BoxScope.() -> Unit)? = null,
    content: (@Composable BoxScope.() -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val group = LocalNAvatarGroup.current
    val resolvedSize = size ?: group?.size ?: 34.dp
    val resolvedRound = round ?: group?.round ?: false
    val resolvedBordered = bordered ?: group?.bordered ?: false
    val shape = if (resolvedRound) CircleShape else RoundedCornerShape(theme.borderRadius)
    val bg = color ?: theme.colors.avatar
    val fg = if (color != null) {
        if (cn.enaium.naiveui.internal.nIsLight(color)) theme.colors.text2 else Color.White
    } else {
        theme.colors.text2
    }
    val fontSize = theme.typography.fontSize.small
    val borderWidth = if (resolvedBordered) 2.dp else 0.dp
    Box(
        modifier
            .size(resolvedSize)
            .clip(shape)
            .background(bg)
            .then(
                if (resolvedBordered) Modifier.nBorder(theme.colors.card, borderWidth, if (resolvedRound) resolvedSize / 2 else theme.borderRadius)
                else Modifier,
            ),
        contentAlignment = Alignment.Center,
    ) {
        val fallbackContent: @Composable BoxScope.() -> Unit = {
            when {
                content != null -> content()
                fallback != null -> fallback()
                text != null -> {
                    CompositionLocalProvider(
                        LocalNContentColor provides fg,
                        LocalNContentTextStyle provides TextStyle(
                            fontFamily = theme.typography.fontFamily,
                            fontSize = fontSize,
                            fontWeight = FontWeight.Medium,
                            color = fg,
                        ),
                    ) {
                        NText(text.take(1).uppercase(), color = fg, size = fontSize, strong = true)
                    }
                }
                else -> NIcon(NIcons.Person, size = resolvedSize * 0.6f, color = fg)
            }
        }
        if (src != null && content == null) {
            SubcomposeAsyncImage(
                model = src,
                contentDescription = text,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale,
                loading = { fallbackContent() },
                error = { fallbackContent() },
            )
        } else {
            fallbackContent()
        }
    }
}

@Immutable
data class NAvatarOption(
    val src: String? = null,
    val text: String? = null,
    val color: Color? = null,
    val round: Boolean? = null,
)

/**
 * Overlapping avatar stack. `max` collapses extra options into a "+N" avatar.
 */
@Composable
fun NAvatarGroup(
    modifier: Modifier = Modifier,
    options: List<NAvatarOption>,
    max: Int = Int.MAX_VALUE,
    size: Dp = 34.dp,
    round: Boolean = false,
    bordered: Boolean = false,
) {
    val theme = LocalNTheme.current
    val limit = max.coerceAtLeast(0)
    val shown = options.take(limit)
    val hidden = options.size - shown.size
    CompositionLocalProvider(LocalNAvatarGroup provides NAvatarGroupContext(size, round, bordered)) {
        androidx.compose.ui.layout.Layout(
            modifier = modifier,
            content = {
                shown.forEach { option ->
                    NAvatar(
                        size = size,
                        src = option.src,
                        text = option.text,
                        color = option.color,
                        round = option.round ?: round,
                        bordered = bordered,
                    )
                }
                if (hidden > 0) {
                    NAvatar(size = size, round = round, bordered = bordered) {
                        NText("+$hidden", color = theme.colors.text2, size = theme.typography.fontSize.small)
                    }
                }
            },
        ) { measurables, constraints ->
            val placeables = measurables.map {
                it.measure(constraints.copy(minWidth = 0, minHeight = 0))
            }
            val overlap = (size.value * 0.25f).dp.roundToPx()
            val width = placeables.sumOf { it.width } - overlap * (placeables.size - 1).coerceAtLeast(0)
            val height = placeables.maxOfOrNull { it.height } ?: 0
            layout(width.coerceAtLeast(0), height) {
                var x = 0
                placeables.forEach { placeable ->
                    placeable.placeRelative(x, 0)
                    x += placeable.width - overlap
                }
            }
        }
    }
}

/** Content-based avatar group without a max limit. */
@Composable
fun NAvatarGroup(
    modifier: Modifier = Modifier,
    size: Dp = 34.dp,
    round: Boolean = false,
    bordered: Boolean = false,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalNAvatarGroup provides NAvatarGroupContext(size, round, bordered)) {
        androidx.compose.ui.layout.Layout(
            modifier = modifier,
            content = content,
        ) { measurables, constraints ->
            val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
            val overlap = (size.value * 0.25f).dp.roundToPx()
            val width = placeables.sumOf { it.width } - overlap * (placeables.size - 1).coerceAtLeast(0)
            val height = placeables.maxOfOrNull { it.height } ?: 0
            layout(width.coerceAtLeast(0), height) {
                var x = 0
                placeables.forEach { placeable ->
                    placeable.placeRelative(x, 0)
                    x += placeable.width - overlap
                }
            }
        }
    }
}
