package cn.enaium.naiveui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import cn.enaium.naiveui.internal.NCloseButton
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nBottomBorder
import cn.enaium.naiveui.internal.nShadow
import cn.enaium.naiveui.internal.nTopBorder
import cn.enaium.naiveui.theme.LocalNTheme

/** Edge of the window the drawer slides in from. */
enum class NDrawerPlacement { Left, Right, Top, Bottom }

/**
 * A panel that slides in from one window edge above the application.
 *
 * [width] applies to the left/right placements and [height] to the top/bottom
 * ones. Set [resizable] to drag the inner edge, and [embedded] to render the
 * drawer inside the parent layout area instead of the whole window (useful for
 * a custom display area).
 */
@Composable
fun NDrawer(
    show: Boolean,
    onUpdateShow: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    placement: NDrawerPlacement = NDrawerPlacement.Right,
    width: Dp = 400.dp,
    height: Dp = 300.dp,
    title: String? = null,
    header: (@Composable () -> Unit)? = null,
    closable: Boolean = true,
    maskClosable: Boolean = true,
    resizable: Boolean = false,
    minResizeWidth: Dp = 240.dp,
    maxResizeWidth: Dp = 720.dp,
    minResizeHeight: Dp = 160.dp,
    maxResizeHeight: Dp = 640.dp,
    embedded: Boolean = false,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val transitionState = remember { MutableTransitionState(show) }
    transitionState.targetState = show
    if (!transitionState.currentState && !transitionState.targetState) return

    val theme = LocalNTheme.current
    val density = LocalDensity.current
    val close = { onUpdateShow(false) }
    val scrimAlpha by animateFloatAsState(
        targetValue = if (transitionState.targetState) 1f else 0f,
        animationSpec = tween(200),
    )
    val mask = theme.colors.mask.copy(alpha = theme.colors.mask.alpha * scrimAlpha)
    val shape = RoundedCornerShape(theme.borderRadius)
    val horizontal = placement == NDrawerPlacement.Left || placement == NDrawerPlacement.Right
    var currentWidth by remember { mutableStateOf(width) }
    var currentHeight by remember { mutableStateOf(height) }

    val enter = when (placement) {
        NDrawerPlacement.Left -> slideInHorizontally(tween(260)) { -it } + fadeIn(tween(200))
        NDrawerPlacement.Right -> slideInHorizontally(tween(260)) { it } + fadeIn(tween(200))
        NDrawerPlacement.Top -> slideInVertically(tween(260)) { -it } + fadeIn(tween(200))
        NDrawerPlacement.Bottom -> slideInVertically(tween(260)) { it } + fadeIn(tween(200))
    }
    val exit = when (placement) {
        NDrawerPlacement.Left -> slideOutHorizontally(tween(220)) { -it } + fadeOut(tween(180))
        NDrawerPlacement.Right -> slideOutHorizontally(tween(220)) { it } + fadeOut(tween(180))
        NDrawerPlacement.Top -> slideOutVertically(tween(220)) { -it } + fadeOut(tween(180))
        NDrawerPlacement.Bottom -> slideOutVertically(tween(220)) { it } + fadeOut(tween(180))
    }
    val panelAlignment = when (placement) {
        NDrawerPlacement.Left -> Alignment.CenterStart
        NDrawerPlacement.Right -> Alignment.CenterEnd
        NDrawerPlacement.Top -> Alignment.TopCenter
        NDrawerPlacement.Bottom -> Alignment.BottomCenter
    }
    val panelSize = if (horizontal) {
        Modifier.fillMaxHeight().width(currentWidth)
    } else {
        Modifier.fillMaxWidth().height(currentHeight)
    }

    val host: @Composable () -> Unit = {
        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(mask)
                    .nOverlayScrimClick(maskClosable, close),
            )
            AnimatedVisibility(
                visibleState = transitionState,
                enter = enter,
                exit = exit,
                modifier = Modifier.align(panelAlignment),
            ) {
                Box(
                    modifier
                        .then(panelSize)
                        .nShadow(theme, 3, shape)
                        .clip(shape)
                        .background(theme.colors.modal, shape)
                        .nBorder(theme.colors.border, 1.dp, theme.borderRadius)
                        .nConsumeClicks(),
                ) {
                    Column(Modifier.fillMaxSize()) {
                        if (header != null || title != null || closable) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .nBottomBorder(theme.colors.divider)
                                    .padding(start = 20.dp, end = 12.dp, top = 16.dp, bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (header != null) {
                                    Box(Modifier.weight(1f)) { header() }
                                } else if (title != null) {
                                    NText(
                                        text = title,
                                        modifier = Modifier.weight(1f),
                                        size = theme.typography.fontSize.huge,
                                        strong = true,
                                        color = theme.colors.text1,
                                    )
                                } else {
                                    Box(Modifier.weight(1f))
                                }
                                if (closable) {
                                    NCloseButton(onClick = close, size = 20.dp, iconSize = 14.dp)
                                }
                            }
                        }
                        NScrollbar(Modifier.weight(1f)) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                content()
                            }
                        }
                        if (footer != null) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .nTopBorder(theme.colors.divider)
                                    .padding(horizontal = 20.dp, vertical = 14.dp),
                            ) {
                                footer()
                            }
                        }
                    }
                    if (resizable) {
                        val handleModifier = when (placement) {
                            NDrawerPlacement.Right -> Modifier
                                .align(Alignment.CenterStart)
                                .fillMaxHeight()
                                .width(6.dp)
                                .pointerInput(Unit) {
                                    detectDragGestures { change, drag ->
                                        change.consume()
                                        val delta = with(density) { (-drag.x).toDp() }
                                        currentWidth = (currentWidth + delta)
                                            .coerceIn(minResizeWidth, maxResizeWidth)
                                    }
                                }
                            NDrawerPlacement.Left -> Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxHeight()
                                .width(6.dp)
                                .pointerInput(Unit) {
                                    detectDragGestures { change, drag ->
                                        change.consume()
                                        val delta = with(density) { drag.x.toDp() }
                                        currentWidth = (currentWidth + delta)
                                            .coerceIn(minResizeWidth, maxResizeWidth)
                                    }
                                }
                            NDrawerPlacement.Top -> Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(6.dp)
                                .pointerInput(Unit) {
                                    detectDragGestures { change, drag ->
                                        change.consume()
                                        val delta = with(density) { drag.y.toDp() }
                                        currentHeight = (currentHeight + delta)
                                            .coerceIn(minResizeHeight, maxResizeHeight)
                                    }
                                }
                            NDrawerPlacement.Bottom -> Modifier
                                .align(Alignment.TopCenter)
                                .fillMaxWidth()
                                .height(6.dp)
                                .pointerInput(Unit) {
                                    detectDragGestures { change, drag ->
                                        change.consume()
                                        val delta = with(density) { (-drag.y).toDp() }
                                        currentHeight = (currentHeight + delta)
                                            .coerceIn(minResizeHeight, maxResizeHeight)
                                    }
                                }
                        }
                        Box(handleModifier.background(theme.colors.divider.copy(alpha = 0.6f)))
                    }
                }
            }
        }
    }

    if (embedded) {
        Box(Modifier.fillMaxSize().clipToBounds()) { host() }
    } else {
        Popup(
            popupPositionProvider = remember { NWindowPopupPositionProvider() },
            onDismissRequest = close,
            properties = PopupProperties(
                focusable = true,
                dismissOnBackPress = true,
                dismissOnClickOutside = maskClosable,
            ),
        ) {
            host()
        }
    }
}
