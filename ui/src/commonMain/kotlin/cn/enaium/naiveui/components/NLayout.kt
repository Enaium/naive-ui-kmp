package cn.enaium.naiveui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.LocalNContentColor
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nBottomBorder
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.internal.nRightBorder
import cn.enaium.naiveui.internal.nTopBorder
import cn.enaium.naiveui.theme.LocalNTheme

/** Positioning mode shared by layout containers, mirroring naive-ui's `position`. */
enum class NLayoutPosition { Static, Absolute }

/**
 * Page layout container. When [hasSider] is set children are laid out in a
 * row, otherwise they stack in a column. Set [position] to
 * [NLayoutPosition.Absolute] to fill the enclosing container.
 */
@Composable
fun NLayout(
    modifier: Modifier = Modifier,
    hasSider: Boolean = false,
    embedded: Boolean = false,
    position: NLayoutPosition = NLayoutPosition.Static,
    content: @Composable () -> Unit,
) {
    val theme = LocalNTheme.current
    val background = if (embedded) theme.colors.action else theme.colors.body
    val layoutModifier = modifier
        .then(if (position == NLayoutPosition.Absolute) Modifier.fillMaxSize() else Modifier.fillMaxWidth())
        .background(background)
    CompositionLocalProvider(LocalNContentColor provides theme.colors.text2) {
        if (hasSider) {
            Row(layoutModifier) { content() }
        } else {
            Column(layoutModifier) { content() }
        }
    }
}

/**
 * Top bar of a layout. Fixed at [height] with the header background token and
 * an optional bottom border.
 */
@Composable
fun NLayoutHeader(
    modifier: Modifier = Modifier,
    bordered: Boolean = false,
    inverted: Boolean = false,
    height: Dp = 60.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp),
    content: @Composable RowScope.() -> Unit = {},
) {
    val theme = LocalNTheme.current
    val background = if (inverted) theme.colors.inverted else theme.colors.card
    val foreground = if (inverted) Color.White else theme.colors.text2
    CompositionLocalProvider(LocalNContentColor provides foreground) {
        Row(
            modifier
                .fillMaxWidth()
                .height(height)
                .background(background)
                .then(if (bordered) Modifier.nBottomBorder(theme.colors.divider) else Modifier)
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

/** Bottom bar of a layout with the footer background token. */
@Composable
fun NLayoutFooter(
    modifier: Modifier = Modifier,
    bordered: Boolean = false,
    inverted: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
    content: @Composable RowScope.() -> Unit = {},
) {
    val theme = LocalNTheme.current
    val background = if (inverted) theme.colors.inverted else theme.colors.action
    val foreground = if (inverted) Color.White else theme.colors.text2
    CompositionLocalProvider(LocalNContentColor provides foreground) {
        Row(
            modifier
                .fillMaxWidth()
                .background(background)
                .then(if (bordered) Modifier.nTopBorder(theme.colors.divider) else Modifier)
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

/**
 * Sidebar of a layout. Must be placed inside a [NLayout] with `hasSider`.
 * [collapsed] shrinks the sider to [collapsedWidth]; [showTrigger] renders the
 * built-in chevron trigger.
 */
@Composable
fun NLayoutSider(
    modifier: Modifier = Modifier,
    width: Dp = 240.dp,
    collapsedWidth: Dp = 48.dp,
    collapsed: Boolean = false,
    onCollapsedChange: (Boolean) -> Unit = {},
    bordered: Boolean = true,
    inverted: Boolean = false,
    showTrigger: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val theme = LocalNTheme.current
    val targetWidth = if (collapsed) collapsedWidth else width
    val animatedWidth by animateDpAsState(targetWidth, label = "n-layout-sider-width")
    val background = if (inverted) theme.colors.inverted else theme.colors.card
    val foreground = if (inverted) Color.White else theme.colors.text2
    val interaction = remember { MutableInteractionSource() }
    CompositionLocalProvider(LocalNContentColor provides foreground) {
        Box(
            modifier
                .width(animatedWidth)
                .fillMaxHeight()
                .background(background)
                .then(if (bordered) Modifier.nRightBorder(theme.colors.divider) else Modifier),
        ) {
            Column(Modifier.fillMaxHeight().clipToBounds().padding(contentPadding)) { content() }
            if (showTrigger) {
                Box(
                    Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 9.dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(theme.colors.base)
                        .nBorder(theme.colors.divider, 1.dp, 9.dp)
                        .hoverable(interaction)
                        .nClickable(interaction) { onCollapsedChange(!collapsed) },
                    contentAlignment = Alignment.Center,
                ) {
                    NIcon(
                        if (collapsed) NIcons.ChevronRight else NIcons.ChevronLeft,
                        size = 12.dp,
                        color = theme.colors.text2,
                    )
                }
            }
        }
    }
}

/**
 * Main content area of a layout. Fills the remaining space of a sider row and
 * grows with the enclosing container.
 */
@Composable
fun NLayoutContent(
    modifier: Modifier = Modifier,
    position: NLayoutPosition = NLayoutPosition.Static,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val themedModifier = modifier
        .then(if (position == NLayoutPosition.Absolute) Modifier.fillMaxSize() else Modifier.fillMaxWidth())
        .padding(contentPadding)
    Column(themedModifier, verticalArrangement = Arrangement.Top, content = content)
}
