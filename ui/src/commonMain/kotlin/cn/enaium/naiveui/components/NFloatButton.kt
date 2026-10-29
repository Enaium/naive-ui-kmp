package cn.enaium.naiveui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.internal.LocalNContentColor
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.internal.rememberNPressInteraction
import cn.enaium.naiveui.internal.nShadow
import cn.enaium.naiveui.theme.LocalNTheme

/** Shape of a float button and of the buttons inside a group. */
enum class NFloatButtonShape { Circle, Square }

/** Visual variant of a float button. */
enum class NFloatButtonType { Default, Primary }

/** How a float button opens its menu. */
enum class NFloatButtonMenuTrigger { Hover, Click }

private class NFloatButtonGroupContext(val shape: NFloatButtonShape)

private val LocalNFloatButtonGroup = staticCompositionLocalOf<NFloatButtonGroupContext?> { null }

/**
 * A circular or square floating action button.
 *
 * The caller decides where it lives; no fixed screen positioning is applied.
 * When [menuTrigger] is set, [menu] is revealed above the button and the body
 * turns into a close affordance while open.
 */
@Composable
fun NFloatButton(
    modifier: Modifier = Modifier,
    type: NFloatButtonType = NFloatButtonType.Default,
    shape: NFloatButtonShape = NFloatButtonShape.Circle,
    description: String? = null,
    width: Dp = 40.dp,
    height: Dp = 40.dp,
    size: Dp? = null,
    menuTrigger: NFloatButtonMenuTrigger? = null,
    showMenu: Boolean? = null,
    onShowMenuChange: ((Boolean) -> Unit)? = null,
    menu: (@Composable () -> Unit)? = null,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val resolvedWidth = size ?: width
    val resolvedHeight = size ?: height
    val theme = LocalNTheme.current
    val group = LocalNFloatButtonGroup.current
    val resolvedShape = group?.shape ?: shape
    val inSquareGroup = group != null && group.shape == NFloatButtonShape.Square
    val interaction = remember { MutableInteractionSource() }
    val hoverInteraction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val pressed = rememberNPressInteraction(interaction)
    val areaHovered by hoverInteraction.collectIsHoveredAsState()
    val primary = type == NFloatButtonType.Primary
    var internalMenuVisible by remember { mutableStateOf(false) }
    val menuVisible = showMenu ?: internalMenuVisible
    val updateMenu: (Boolean) -> Unit = { next ->
        if (showMenu == null) internalMenuVisible = next
        onShowMenuChange?.invoke(next)
    }

    if (menuTrigger == NFloatButtonMenuTrigger.Hover) {
        androidx.compose.runtime.LaunchedEffect(areaHovered) {
            if (areaHovered) updateMenu(true) else updateMenu(false)
        }
    }

    val menuProgress by animateFloatAsState(
        targetValue = if (menuVisible) 1f else 0f,
        animationSpec = tween(200),
        label = "n-float-button-menu",
    )

    val container = when {
        inSquareGroup -> androidx.compose.ui.graphics.Color.Transparent
        primary -> theme.colors.primary
        else -> theme.colors.popover
    }
    val currentContainer = when {
        inSquareGroup -> container
        pressed -> if (primary) theme.colors.primaryPressed else container
        hovered -> if (primary) theme.colors.primaryHover else container
        else -> container
    }
    // Keep the popover surface opaque and paint hover/pressed as an overlay.
    // Replacing the background with a translucent colour exposes the shadow
    // underneath and makes the whole button look grey.
    val stateOverlay = when {
        primary -> androidx.compose.ui.graphics.Color.Transparent
        inSquareGroup -> if (hovered) theme.colors.buttonColor2Hover else androidx.compose.ui.graphics.Color.Transparent
        pressed -> theme.colors.buttonColor2Pressed
        hovered -> theme.colors.buttonColor2Hover
        else -> androidx.compose.ui.graphics.Color.Transparent
    }
    val foreground = when {
        inSquareGroup -> if (primary) theme.colors.primary else theme.colors.text2
        primary -> theme.colors.base
        else -> theme.colors.text2
    }
    val resolved: Shape = when (resolvedShape) {
        NFloatButtonShape.Circle -> CircleShape
        NFloatButtonShape.Square -> RoundedCornerShape(theme.borderRadius)
    }

    Column(
        modifier.hoverable(hoverInteraction),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (menu != null && menuTrigger != null) {
            AnimatedVisibility(
                visible = menuVisible,
                enter = fadeIn(tween(200)) + expandVertically(expandFrom = Alignment.Bottom),
                exit = fadeOut(tween(150)) + shrinkVertically(shrinkTowards = Alignment.Bottom),
            ) {
                Column(
                    Modifier.padding(bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    menu()
                }
            }
        }
        Box(
            Modifier
                .widthIn(min = resolvedWidth)
                .heightIn(min = resolvedHeight)
                .then(
                    if (inSquareGroup) {
                        Modifier
                    } else {
                        Modifier.nShadow(theme, if (pressed) 3 else if (hovered) 2 else 1, resolved)
                    },
                )
                .hoverable(interaction)
                .then(
                    when (menuTrigger) {
                        NFloatButtonMenuTrigger.Click -> Modifier.nClickable(interaction) {
                            updateMenu(!menuVisible)
                        }
                        else -> Modifier.nClickable(interaction, onClick = onClick)
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.matchParentSize().clip(resolved).background(currentContainer)) {
                if (stateOverlay != androidx.compose.ui.graphics.Color.Transparent) {
                    Box(Modifier.matchParentSize().background(stateOverlay))
                }
            }
            Box(Modifier.padding(horizontal = 4.dp, vertical = 2.dp)) {
                Column(
                    Modifier.graphicsLayer {
                        alpha = 1f - menuProgress
                        scaleX = 1f - 0.25f * menuProgress
                        scaleY = 1f - 0.25f * menuProgress
                    },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    CompositionLocalProvider(LocalNContentColor provides foreground) {
                        content()
                    }
                    if (description != null) {
                        NText(
                            text = description,
                            color = foreground,
                            size = 12.sp,
                            maxLines = 1,
                        )
                    }
                }
                if (menuTrigger != null) {
                    Box(
                        Modifier
                            .matchParentSize()
                            .graphicsLayer {
                                alpha = menuProgress
                                scaleX = 0.75f + 0.25f * menuProgress
                                scaleY = 0.75f + 0.25f * menuProgress
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        NIcon(NIcons.Close, size = 18.dp, color = foreground)
                    }
                }
            }
        }
    }
}

/**
 * A vertical stack of [NFloatButton]s.
 *
 * Square groups share one surface with a single shadow; circular groups keep
 * the individual buttons.
 */
@Composable
fun NFloatButtonGroup(
    modifier: Modifier = Modifier,
    shape: NFloatButtonShape = NFloatButtonShape.Circle,
    content: @Composable () -> Unit,
) {
    val theme = LocalNTheme.current
    CompositionLocalProvider(LocalNFloatButtonGroup provides NFloatButtonGroupContext(shape)) {
        if (shape == NFloatButtonShape.Square) {
            Column(
                modifier
                    .nShadow(theme, 1, RoundedCornerShape(theme.borderRadius))
                    .clip(RoundedCornerShape(theme.borderRadius))
                    .background(theme.colors.popover),
            ) {
                content()
            }
        } else {
            Column(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                content()
            }
        }
    }
}
