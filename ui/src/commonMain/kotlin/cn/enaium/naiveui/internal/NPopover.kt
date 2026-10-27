package cn.enaium.naiveui.internal

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import cn.enaium.naiveui.theme.LocalNTheme
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.roundToInt

/** Placement of a popup relative to its anchor. */
enum class NPlacement {
    Top,
    TopStart,
    TopEnd,
    Bottom,
    BottomStart,
    BottomEnd,
    Left,
    LeftStart,
    LeftEnd,
    Right,
    RightStart,
    RightEnd,
}

/** How a popover is opened. */
enum class NTrigger { Hover, Click, Focus, Manual }

private class NPopupPositionProvider(
    private val placement: NPlacement,
    private val offset: IntOffset,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val anchorWidth = anchorBounds.right - anchorBounds.left
        val anchorHeight = anchorBounds.bottom - anchorBounds.top
        val popupWidth = popupContentSize.width
        val popupHeight = popupContentSize.height
        val rtl = layoutDirection == LayoutDirection.Rtl
        val start = if (rtl) anchorBounds.right - popupWidth else anchorBounds.left
        val end = if (rtl) anchorBounds.left else anchorBounds.right - popupWidth
        val centerX = anchorBounds.left + (anchorWidth - popupWidth) / 2
        val centerY = anchorBounds.top + (anchorHeight - popupHeight) / 2
        var x = centerX
        var y = anchorBounds.bottom
        when (placement) {
            NPlacement.Bottom -> {
                x = centerX
                y = anchorBounds.bottom
            }
            NPlacement.BottomStart -> {
                x = start
                y = anchorBounds.bottom
            }
            NPlacement.BottomEnd -> {
                x = end
                y = anchorBounds.bottom
            }
            NPlacement.Top -> {
                x = centerX
                y = anchorBounds.top - popupHeight
            }
            NPlacement.TopStart -> {
                x = start
                y = anchorBounds.top - popupHeight
            }
            NPlacement.TopEnd -> {
                x = end
                y = anchorBounds.top - popupHeight
            }
            NPlacement.Left -> {
                x = anchorBounds.left - popupWidth
                y = centerY
            }
            NPlacement.LeftStart -> {
                x = anchorBounds.left - popupWidth
                y = anchorBounds.top
            }
            NPlacement.LeftEnd -> {
                x = anchorBounds.left - popupWidth
                y = anchorBounds.bottom - popupHeight
            }
            NPlacement.Right -> {
                x = anchorBounds.right
                y = centerY
            }
            NPlacement.RightStart -> {
                x = anchorBounds.right
                y = anchorBounds.top
            }
            NPlacement.RightEnd -> {
                x = anchorBounds.right
                y = anchorBounds.bottom - popupHeight
            }
        }
        x += offset.x
        y += offset.y
        val maxX = max(0, windowSize.width - popupWidth)
        val maxY = max(0, windowSize.height - popupHeight)
        return IntOffset(x.coerceIn(0, maxX), y.coerceIn(0, maxY))
    }
}

/**
 * The bare popup surface used by tooltip, dropdown, select, date picker and
 * friends. Must be composed inside the anchor `Box` so the popup can position
 * itself relative to that anchor.
 */
@Composable
fun NPopoverSurface(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    placement: NPlacement = NPlacement.Bottom,
    offset: IntOffset = IntOffset(0, 6),
    focusable: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    dismissOnBackPress: Boolean = true,
    padding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    color: Color? = null,
    borderColor: Color? = null,
    showArrow: Boolean = false,
    content: @Composable () -> Unit,
) {
    if (!visible) return
    val theme = LocalNTheme.current
    val shape = RoundedCornerShape(theme.borderRadius)
    Popup(
        popupPositionProvider = remember(placement, offset) { NPopupPositionProvider(placement, offset) },
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            focusable = focusable,
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside,
        ),
    ) {
        val arrowSpace = 6.dp
        val arrowPadding = if (!showArrow) {
            Modifier
        } else {
            when (placement) {
                NPlacement.Bottom, NPlacement.BottomStart, NPlacement.BottomEnd ->
                    Modifier.padding(top = arrowSpace)
                NPlacement.Top, NPlacement.TopStart, NPlacement.TopEnd ->
                    Modifier.padding(bottom = arrowSpace)
                NPlacement.Left, NPlacement.LeftStart, NPlacement.LeftEnd ->
                    Modifier.padding(end = arrowSpace)
                NPlacement.Right, NPlacement.RightStart, NPlacement.RightEnd ->
                    Modifier.padding(start = arrowSpace)
            }
        }
        Box(modifier) {
            Box(
                Modifier
                    .then(arrowPadding)
                    .nShadow(theme, 2, shape)
                    .clip(shape)
                    .background(color ?: theme.colors.popover, shape)
                    .nBorder(borderColor ?: theme.colors.border, 1.dp, theme.borderRadius)
                    .padding(padding),
            ) {
                content()
            }
            if (showArrow) {
                val arrowColor = color ?: theme.colors.popover
                val arrowModifier = when (placement) {
                    NPlacement.Bottom, NPlacement.BottomStart, NPlacement.BottomEnd ->
                        Modifier.align(Alignment.TopCenter)
                    NPlacement.Top, NPlacement.TopStart, NPlacement.TopEnd ->
                        Modifier.align(Alignment.BottomCenter)
                    NPlacement.Left, NPlacement.LeftStart, NPlacement.LeftEnd ->
                        Modifier.align(Alignment.CenterEnd)
                    NPlacement.Right, NPlacement.RightStart, NPlacement.RightEnd ->
                        Modifier.align(Alignment.CenterStart)
                }
                val arrowWidth = 10.dp
                val arrowHeight = 8.dp
                val vertical = placement in setOf(
                    NPlacement.Bottom, NPlacement.BottomStart, NPlacement.BottomEnd,
                    NPlacement.Top, NPlacement.TopStart, NPlacement.TopEnd,
                )
                Box(
                    arrowModifier.size(
                        width = if (vertical) arrowWidth else arrowHeight,
                        height = if (vertical) arrowHeight else arrowWidth,
                    ),
                ) {
                    Canvas(Modifier.fillMaxSize()) {
                        val path = Path()
                        when (placement) {
                            NPlacement.Bottom, NPlacement.BottomStart, NPlacement.BottomEnd -> {
                                path.moveTo(size.width / 2f, 0f)
                                path.lineTo(size.width, size.height)
                                path.lineTo(0f, size.height)
                            }
                            NPlacement.Top, NPlacement.TopStart, NPlacement.TopEnd -> {
                                path.moveTo(size.width / 2f, size.height)
                                path.lineTo(size.width, 0f)
                                path.lineTo(0f, 0f)
                            }
                            NPlacement.Left, NPlacement.LeftStart, NPlacement.LeftEnd -> {
                                path.moveTo(size.width, size.height / 2f)
                                path.lineTo(0f, 0f)
                                path.lineTo(0f, size.height)
                            }
                            NPlacement.Right, NPlacement.RightStart, NPlacement.RightEnd -> {
                                path.moveTo(0f, size.height / 2f)
                                path.lineTo(size.width, 0f)
                                path.lineTo(size.width, size.height)
                            }
                        }
                        path.close()
                        drawPath(path, color = arrowColor)
                        drawPath(
                            path,
                            color = borderColor ?: theme.colors.text3.copy(alpha = 0.35f),
                            style = Stroke(width = 1.dp.toPx()),
                        )
                    }
                }
            }
        }
    }
}

/**
 * Popover with built-in trigger handling. For fully controlled menus and
 * pickers use [NPopoverSurface] with an explicit `visible` state.
 */
@Composable
fun NPopover(
    modifier: Modifier = Modifier,
    trigger: NTrigger = NTrigger.Hover,
    placement: NPlacement = NPlacement.Bottom,
    disabled: Boolean = false,
    showArrow: Boolean = true,
    delayMillis: Long = 100,
    focusable: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    content: @Composable () -> Unit,
    children: @Composable () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()

    LaunchedEffect(hovered, disabled) {
        if (disabled) {
            visible = false
            return@LaunchedEffect
        }
        if (trigger == NTrigger.Hover) {
            if (hovered) {
                delay(delayMillis)
                visible = true
            } else if (visible) {
                delay(delayMillis)
                visible = false
            }
        }
    }

    Box(
        modifier
            .hoverable(interaction, enabled = !disabled)
            .nTapTrigger(enabled = !disabled) {
                if (trigger != NTrigger.Manual) visible = !visible
            },
    ) {
        children()
        NPopoverSurface(
            visible = visible,
            onDismiss = { visible = false },
            placement = placement,
            focusable = focusable,
            dismissOnClickOutside = true,
            padding = contentPadding,
            showArrow = showArrow,
        ) {
            content()
        }
    }
}

/** Utility for consumers that need the computed offset for custom popups. */
@Composable
fun rememberNPopupOffset(placement: NPlacement, x: Int = 0, y: Int = 0): IntOffset =
    remember(placement, x, y) { IntOffset(x, y) }

internal fun dpToPxOffset(value: Float): Int = value.roundToInt()

/** Simple anchored dropdown composition used by select-like components. */
@Composable
fun NAnchoredPopup(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    placement: NPlacement = NPlacement.BottomStart,
    offset: IntOffset = IntOffset(0, 4),
    focusable: Boolean = true,
    padding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit,
) {
    NPopoverSurface(
        visible = visible,
        onDismiss = onDismiss,
        modifier = modifier,
        placement = placement,
        offset = offset,
        focusable = focusable,
        padding = padding,
        content = content,
    )
}

/** Unused geometry helper retained for popup alignment math in components. */
internal fun IntRect.centerOffset(size: IntSize): Offset =
    Offset(
        x = left + ((right - left) - size.width) / 2f,
        y = top + ((bottom - top) - size.height) / 2f,
    )
