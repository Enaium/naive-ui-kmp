package cn.enaium.naiveui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.internal.nShadow
import cn.enaium.naiveui.theme.LocalNTheme
import kotlinx.coroutines.launch

/**
 * Floating back-to-top button.
 *
 * Compose this inside a `Box` that also contains the vertical scroll
 * container; it overlays the box and appears after [visibilityHeight] pixels
 * of scrolling. Clicking animates [scrollState] back to zero.
 */
@Composable
fun NBackTop(
    scrollState: ScrollState,
    visibilityHeight: Dp = 180.dp,
    modifier: Modifier = Modifier,
    right: Dp = 40.dp,
    bottom: Dp = 40.dp,
    show: Boolean? = null,
    onClick: () -> Unit = {},
    content: (@Composable () -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val density = androidx.compose.ui.platform.LocalDensity.current
    val visible = show ?: (scrollState.value > with(density) { visibilityHeight.toPx() })
    val scope = rememberCoroutineScope()
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val shape = RoundedCornerShape(theme.borderRadius)

    Box(modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = right, bottom = bottom),
            enter = fadeIn() + scaleIn(initialScale = 0.8f),
            exit = fadeOut() + scaleOut(targetScale = 0.8f),
        ) {
            val customContent = content
            Box(
                Modifier
                    .heightIn(min = 40.dp)
                    .then(if (customContent == null) Modifier.widthIn(min = 40.dp) else Modifier)
                    .nShadow(theme, 1, shape)
                    .clip(shape)
                    .background(theme.colors.popover)
                    .hoverable(interaction)
                    .nClickable(interaction) {
                        scope.launch { scrollState.animateScrollTo(0) }
                        onClick()
                    }
                    .then(
                        if (customContent == null) {
                            Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                        } else {
                            Modifier
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (customContent != null) {
                    customContent()
                } else {
                    NIcon(
                        NIcons.ArrowUp,
                        size = 20.dp,
                        color = if (hovered) theme.colors.primaryHover else theme.colors.text2,
                    )
                }
            }
        }
    }
}
