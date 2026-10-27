package cn.enaium.naiveui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
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
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.LocalNContentColor
import cn.enaium.naiveui.internal.NScrollbar as InternalNScrollbar
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nBottomBorder
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.internal.nLeftBorder
import cn.enaium.naiveui.internal.nRightBorder
import cn.enaium.naiveui.internal.nTopBorder
import cn.enaium.naiveui.theme.LocalNTheme
import kotlin.math.max

/** Positioning mode shared by layout containers, mirroring naive-ui's `position`. */
enum class NLayoutPosition { Static, Absolute }

/** Which side of a layout hosts its sider. */
enum class NLayoutSiderPlacement { Left, Right }

/** How a sider collapses: shrinking its width or translating its content. */
enum class NLayoutCollapseMode { Width, Transform }

/** Built-in sider trigger shapes. */
enum class NLayoutSiderTrigger { ArrowCircle, Bar }

private data class NLayoutSiderMarker(val sider: Boolean = true) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@NLayoutSiderMarker
}

private val LocalNLayoutSiderPlacement = staticCompositionLocalOf { NLayoutSiderPlacement.Left }

/**
 * Page layout container. When [hasSider] is set children are laid out in a
 * row, otherwise they stack in a column. Set [position] to
 * [NLayoutPosition.Absolute] to fill the enclosing container.
 *
 * When [scrollable] is enabled the content scrolls inside the layout using the
 * naive-ui style scrollbar and [scrollState] can drive it programmatically.
 */
@Composable
fun NLayout(
    modifier: Modifier = Modifier,
    hasSider: Boolean = false,
    embedded: Boolean = false,
    position: NLayoutPosition = NLayoutPosition.Static,
    siderPlacement: NLayoutSiderPlacement = NLayoutSiderPlacement.Left,
    scrollable: Boolean = false,
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable () -> Unit,
) {
    val theme = LocalNTheme.current
    val background = if (embedded) theme.colors.action else theme.colors.body
    val areaModifier = modifier
        .then(if (position == NLayoutPosition.Absolute) Modifier.fillMaxSize() else Modifier.fillMaxWidth())
        .background(background)
    CompositionLocalProvider(
        LocalNContentColor provides theme.colors.text2,
        LocalNLayoutSiderPlacement provides siderPlacement,
    ) {
        if (scrollable) {
            InternalNScrollbar(modifier = areaModifier, scrollState = scrollState) {
                NLayoutArea(
                    hasSider = hasSider,
                    placement = siderPlacement,
                    modifier = Modifier.fillMaxWidth(),
                    content = content,
                )
            }
        } else {
            NLayoutArea(
                hasSider = hasSider,
                placement = siderPlacement,
                modifier = areaModifier,
                content = content,
            )
        }
    }
}

@Composable
private fun NLayoutArea(
    hasSider: Boolean,
    placement: NLayoutSiderPlacement,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    if (!hasSider) {
        Column(modifier) { content() }
        return
    }
    Layout(modifier = modifier, content = content) { measurables, constraints ->
        val siderIndex = measurables.indexOfFirst { it.parentData is NLayoutSiderMarker }
        val boundedWidth = constraints.hasBoundedWidth
        if (siderIndex < 0) {
            // Fallback row behavior for layouts without a marked sider.
            var used = 0
            val placeables = measurables.map { measurable ->
                val remaining = if (boundedWidth) {
                    (constraints.maxWidth - used).coerceAtLeast(0)
                } else {
                    Constraints.Infinity
                }
                measurable.measure(
                    Constraints(
                        minWidth = 0,
                        maxWidth = remaining,
                        minHeight = 0,
                        maxHeight = constraints.maxHeight,
                    ),
                ).also { used += it.width }
            }
            val height = placeables.maxOfOrNull { it.height } ?: 0
            val width = if (boundedWidth) constraints.maxWidth else used
            layout(width, height) {
                var x = 0
                placeables.forEach { placeable ->
                    placeable.placeRelative(x, 0)
                    x += placeable.width
                }
            }
        } else {
            val sider = measurables[siderIndex].measure(
                Constraints(
                    minWidth = 0,
                    maxWidth = constraints.maxWidth,
                    minHeight = 0,
                    maxHeight = constraints.maxHeight,
                ),
            )
            val remaining = if (boundedWidth) {
                (constraints.maxWidth - sider.width).coerceAtLeast(0)
            } else {
                Constraints.Infinity
            }
            val others = measurables.filterIndexed { index, _ -> index != siderIndex }.map { measurable ->
                measurable.measure(
                    Constraints(
                        minWidth = if (boundedWidth) remaining else 0,
                        maxWidth = remaining,
                        minHeight = 0,
                        maxHeight = constraints.maxHeight,
                    ),
                )
            }
            val othersWidth = others.sumOf { it.width }
            val width = if (boundedWidth) constraints.maxWidth else sider.width + othersWidth
            val height = max(sider.height, others.maxOfOrNull { it.height } ?: 0)
            // A Measurable may only be measured once per layout pass.
            val finalSider = sider
            layout(width, height) {
                var x = 0
                if (placement == NLayoutSiderPlacement.Left) {
                    finalSider.placeRelative(0, 0)
                    x = finalSider.width
                }
                others.forEach { placeable ->
                    placeable.placeRelative(x, 0)
                    x += placeable.width
                }
                if (placement == NLayoutSiderPlacement.Right) {
                    finalSider.placeRelative(width - finalSider.width, 0)
                }
            }
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
 * built-in trigger and [collapseMode] chooses between shrinking the content
 * width and translating it.
 */
@Composable
fun NLayoutSider(
    modifier: Modifier = Modifier,
    width: Dp = 240.dp,
    collapsedWidth: Dp = 48.dp,
    collapsed: Boolean = false,
    onCollapsedChange: (Boolean) -> Unit = {},
    collapseMode: NLayoutCollapseMode = NLayoutCollapseMode.Width,
    showCollapsedContent: Boolean = true,
    bordered: Boolean = true,
    inverted: Boolean = false,
    showTrigger: Boolean = false,
    triggerStyle: NLayoutSiderTrigger = NLayoutSiderTrigger.ArrowCircle,
    scrollable: Boolean = false,
    scrollState: ScrollState = rememberScrollState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val theme = LocalNTheme.current
    val placement = LocalNLayoutSiderPlacement.current
    val targetWidth = if (collapsed) collapsedWidth else width
    val animatedWidth by animateDpAsState(targetWidth, label = "n-layout-sider-width")
    val background = if (inverted) theme.colors.inverted else theme.colors.card
    val foreground = if (inverted) Color.White else theme.colors.text2
    val interaction = remember { MutableInteractionSource() }
    val collapseRange = (width - collapsedWidth).coerceAtLeast(0.dp)
    val collapseProgress = if (collapseRange > 0.dp) {
        ((width - animatedWidth) / collapseRange).coerceIn(0f, 1f)
    } else {
        0f
    }
    val transformOffset = -collapseRange * collapseProgress

    val contentColumn: @Composable () -> Unit = {
        if (showCollapsedContent || !collapsed) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .then(
                        if (collapseMode == NLayoutCollapseMode.Transform) {
                            Modifier.requiredWidth(width).offset(x = transformOffset)
                        } else {
                            Modifier.fillMaxWidth()
                        },
                    )
                    .padding(contentPadding),
            ) {
                content()
            }
        }
    }

    CompositionLocalProvider(LocalNContentColor provides foreground) {
        Box(
            modifier
                .then(NLayoutSiderMarker())
                .width(animatedWidth)
                .fillMaxHeight()
                .background(background)
                .then(
                    if (bordered) {
                        if (placement == NLayoutSiderPlacement.Left) {
                            Modifier.nRightBorder(theme.colors.divider)
                        } else {
                            Modifier.nLeftBorder(theme.colors.divider)
                        }
                    } else {
                        Modifier
                    },
                ),
        ) {
            Box(Modifier.fillMaxWidth().clipToBounds()) {
                if (scrollable) {
                    InternalNScrollbar(
                        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                        scrollState = scrollState,
                    ) {
                        contentColumn()
                    }
                } else {
                    contentColumn()
                }
            }
            if (showTrigger) {
                val triggerAlignment = if (placement == NLayoutSiderPlacement.Left) {
                    Alignment.CenterEnd
                } else {
                    Alignment.CenterStart
                }
                val triggerOffset = if (placement == NLayoutSiderPlacement.Left) 9.dp else (-9).dp
                if (triggerStyle == NLayoutSiderTrigger.Bar) {
                    val barInteraction = remember { MutableInteractionSource() }
                    val barHovered by barInteraction.collectIsHoveredAsState()
                    Box(
                        Modifier
                            .align(triggerAlignment)
                            .width(8.dp)
                            .fillMaxHeight()
                            .background(if (barHovered) theme.colors.primary.copy(alpha = 0.4f) else theme.colors.divider)
                            .hoverable(barInteraction)
                            .nClickable(barInteraction) { onCollapsedChange(!collapsed) },
                    )
                } else {
                    Box(
                        Modifier
                            .align(triggerAlignment)
                            .offset(x = triggerOffset)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(theme.colors.base)
                            .nBorder(theme.colors.divider, 1.dp, 9.dp)
                            .hoverable(interaction)
                            .nClickable(interaction) { onCollapsedChange(!collapsed) },
                        contentAlignment = Alignment.Center,
                    ) {
                        val icon = when {
                            placement == NLayoutSiderPlacement.Left && collapsed -> NIcons.ChevronRight
                            placement == NLayoutSiderPlacement.Left -> NIcons.ChevronLeft
                            collapsed -> NIcons.ChevronLeft
                            else -> NIcons.ChevronRight
                        }
                        NIcon(icon, size = 12.dp, color = theme.colors.text2)
                    }
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
