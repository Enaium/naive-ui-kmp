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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import cn.enaium.naiveui.components.NText
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
 * ones. The panel animates both on open and on close.
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
    closable: Boolean = true,
    maskClosable: Boolean = true,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val transitionState = remember { MutableTransitionState(show) }
    transitionState.targetState = show
    if (!transitionState.currentState && !transitionState.targetState) return

    val theme = LocalNTheme.current
    val close = { onUpdateShow(false) }
    val scrimAlpha by animateFloatAsState(
        targetValue = if (transitionState.targetState) 1f else 0f,
        animationSpec = tween(200),
    )
    val mask = theme.colors.mask.copy(alpha = theme.colors.mask.alpha * scrimAlpha)
    val shape = RoundedCornerShape(theme.borderRadius)
    val horizontal = placement == NDrawerPlacement.Left || placement == NDrawerPlacement.Right

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
        Modifier.fillMaxHeight().width(width)
    } else {
        Modifier.fillMaxWidth().height(height)
    }

    Popup(
        popupPositionProvider = remember { NWindowPopupPositionProvider() },
        onDismissRequest = close,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = maskClosable,
        ),
    ) {
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
                Column(
                    modifier
                        .then(panelSize)
                        .clip(shape)
                        .background(theme.colors.modal, shape)
                        .nShadow(theme, 3, shape)
                        .nBorder(theme.colors.border, 1.dp, theme.borderRadius)
                        .nConsumeClicks(),
                ) {
                    if (title != null || closable) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .nBottomBorder(theme.colors.divider)
                                .padding(start = 20.dp, end = 12.dp, top = 16.dp, bottom = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (title != null) {
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
            }
        }
    }
}
