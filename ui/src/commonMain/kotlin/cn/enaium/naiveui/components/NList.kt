package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.internal.LocalNContentColor
import cn.enaium.naiveui.internal.LocalNContentTextStyle
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nBottomBorder
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.internal.rememberNPressInteraction
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.nSize

/** State shared by [NList] with its [NListItem] children. */
@Immutable
internal data class NListContext(
    val size: NSize,
    val bordered: Boolean,
    val hoverable: Boolean,
    val clickable: Boolean,
)

internal val LocalNListContext = staticCompositionLocalOf<NListContext?> { null }

/**
 * List container equivalent to `<n-list>`.
 *
 * Items are separated by a 1dp divider that disappears when [showDivider] is
 * disabled. [bordered] draws an outer rounded border, while [hoverable] and
 * [clickable] apply item-level hover and press feedback.
 */
@Composable
fun NList(
    modifier: Modifier = Modifier,
    size: NSize? = null,
    bordered: Boolean = false,
    showDivider: Boolean = true,
    clickable: Boolean = false,
    hoverable: Boolean = false,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val theme = LocalNTheme.current
    val resolvedSize = nSize(size)
    val context = NListContext(resolvedSize, bordered, hoverable, clickable)
    val shape = RoundedCornerShape(theme.borderRadius)
    val horizontalPadding = when (resolvedSize) {
        NSize.Tiny, NSize.Small -> 16.dp
        NSize.Medium -> 20.dp
        NSize.Large -> 24.dp
    }
    val verticalPadding = when (resolvedSize) {
        NSize.Tiny, NSize.Small -> 8.dp
        NSize.Medium -> 12.dp
        NSize.Large -> 14.dp
    }
    val padded = (bordered || hoverable).let { if (it) horizontalPadding else 0.dp }
    val textStyle = theme.typography.style(theme.fontSizeFor(resolvedSize))

    Column(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(theme.colors.card)
            .then(
                if (bordered) {
                    Modifier.nBorder(theme.colors.divider, 1.dp, theme.borderRadius)
                } else {
                    Modifier
                },
            ),
    ) {
        if (header != null) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(theme.colors.card)
                    .then(if (showDivider) Modifier else Modifier.nBottomBorder(theme.colors.divider))
                    .padding(horizontal = padded, vertical = verticalPadding),
            ) {
                CompositionLocalProvider(
                    LocalNContentColor provides theme.colors.text2,
                    LocalNContentTextStyle provides textStyle,
                ) { header() }
            }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .background(if (showDivider) theme.colors.divider else Color.Transparent),
            verticalArrangement = Arrangement.spacedBy(if (showDivider) 1.dp else 0.dp),
        ) {
            CompositionLocalProvider(
                LocalNListContext provides context,
                LocalNContentColor provides theme.colors.text2,
                LocalNContentTextStyle provides textStyle,
            ) {
                content()
            }
        }
        if (footer != null) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(theme.colors.card)
                    .padding(horizontal = padded, vertical = verticalPadding),
            ) {
                CompositionLocalProvider(
                    LocalNContentColor provides theme.colors.text2,
                    LocalNContentTextStyle provides textStyle,
                ) { footer() }
            }
        }
    }
}

/**
 * Item of [NList] with optional [prefix], [suffix] and [extra] areas.
 *
 * [onClick] makes the item interactive; the surrounding list controls the
 * hoverable and clickable styles.
 */
@Composable
fun NListItem(
    modifier: Modifier = Modifier,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
    extra: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val theme = LocalNTheme.current
    val context = LocalNListContext.current
    val bordered = context?.bordered == true
    val hoverable = context?.hoverable == true
    val clickable = context?.clickable == true
    val size = context?.size ?: NSize.Medium
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val pressed = rememberNPressInteraction(interaction)
    val horizontalPadding: Dp = if (bordered || hoverable) {
        when (size) {
            NSize.Tiny, NSize.Small -> 16.dp
            NSize.Medium -> 20.dp
            NSize.Large -> 24.dp
        }
    } else {
        0.dp
    }
    val verticalPadding: Dp = when (size) {
        NSize.Tiny, NSize.Small -> 8.dp
        NSize.Medium -> 12.dp
        NSize.Large -> 14.dp
    }
    val background = when {
        pressed && onClick != null -> theme.colors.pressed
        hovered && hoverable -> theme.colors.hover
        else -> theme.colors.card
    }

    Row(
        modifier
            .fillMaxWidth()
            .background(background)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .hoverable(interaction, enabled = hoverable || clickable || onClick != null)
            .then(if (clickable) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
            .then(
                if (onClick != null) {
                    Modifier.nClickable(interaction, enabled = true, onClick = onClick)
                } else {
                    Modifier
                },
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (prefix != null) {
            Box(Modifier.padding(end = 20.dp)) { prefix() }
        }
        Box(Modifier.weight(1f)) { content() }
        if (extra != null) {
            Box(Modifier.padding(start = 12.dp)) { extra() }
        }
        if (suffix != null) {
            Box(Modifier.padding(start = 20.dp)) { suffix() }
        }
    }
}
