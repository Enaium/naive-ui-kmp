package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.internal.nShadow
import cn.enaium.naiveui.theme.LocalNTheme

/** Shape of a float button and of the buttons inside a group. */
enum class NFloatButtonShape { Circle, Square }

/** Visual variant of a float button. */
enum class NFloatButtonType { Default, Primary }

private class NFloatButtonGroupContext(val shape: NFloatButtonShape)

private val LocalNFloatButtonGroup = staticCompositionLocalOf<NFloatButtonGroupContext?> { null }

/**
 * A circular or square floating action button.
 *
 * The caller decides where it lives; no fixed screen positioning is applied.
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
    val hovered by interaction.collectIsHoveredAsState()
    val pressed by interaction.collectIsPressedAsState()
    val primary = type == NFloatButtonType.Primary

    val container = when {
        inSquareGroup -> androidx.compose.ui.graphics.Color.Transparent
        primary -> theme.colors.primary
        else -> theme.colors.popover
    }
    val currentContainer = when {
        inSquareGroup -> if (hovered) theme.colors.buttonColor2Hover else container
        pressed -> if (primary) theme.colors.primaryPressed else theme.colors.buttonColor2Pressed
        hovered -> if (primary) theme.colors.primaryHover else theme.colors.buttonColor2Hover
        else -> container
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

    Box(
        modifier
            .widthIn(min = resolvedWidth)
            .heightIn(min = resolvedHeight)
            .then(
                if (inSquareGroup) {
                    Modifier
                } else {
                    Modifier.nShadow(theme, if (pressed) 3 else if (hovered) 2 else 1, resolved)
                },
            )
            .clip(resolved)
            .background(currentContainer)
            .hoverable(interaction)
            .nClickable(interaction, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            androidx.compose.runtime.CompositionLocalProvider(
                cn.enaium.naiveui.internal.LocalNContentColor provides foreground,
            ) {
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
                    .clip(RoundedCornerShape(theme.borderRadius))
                    .background(theme.colors.popover)
                    .nShadow(theme, 1, RoundedCornerShape(theme.borderRadius)),
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
