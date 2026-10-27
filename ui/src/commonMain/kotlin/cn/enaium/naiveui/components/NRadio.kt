package cn.enaium.naiveui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.internal.LocalNContentColor
import cn.enaium.naiveui.internal.LocalNContentTextStyle
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.nSize

@Immutable
internal data class NRadioGroupContext(
    val value: Any?,
    val disabled: Boolean,
    val size: NSize,
    val onValueChange: (Any?) -> Unit,
)

internal val LocalNRadioGroup = staticCompositionLocalOf<NRadioGroupContext?> { null }

private fun NSize.radioDot(): Dp = when (this) {
    NSize.Tiny, NSize.Small -> 14.dp
    NSize.Medium -> 16.dp
    NSize.Large -> 18.dp
}

/**
 * Radio button.
 *
 * When composed inside an [NRadioGroup] the selection is derived from the
 * group's [value] and a click selects this radio's [value].
 */
@Composable
fun NRadio(
    value: Any? = null,
    label: String? = null,
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    size: NSize? = null,
    content: (@Composable () -> Unit)? = null,
    checked: Boolean? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val colors = theme.colors
    val group = LocalNRadioGroup.current
    val resolvedSize = group?.size ?: nSize(size)
    val checkedState = group?.let { it.value == value } ?: (checked ?: false)
    val disabledState = disabled || (group?.disabled ?: false)
    val sizeDp = resolvedSize.radioDot()
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val focused by interaction.collectIsFocusedAsState()

    val borderColor = when {
        disabledState -> colors.border
        checkedState || hovered || focused -> colors.primary
        else -> colors.border
    }
    val dotColor = if (disabledState) colors.border else colors.primary
    val textColor = if (disabledState) colors.textDisabled else colors.text2
    val textStyle = theme.typography.body.copy(
        color = textColor,
        fontSize = theme.fontSizeFor(resolvedSize),
    )
    val focusAlpha = if (focused && !disabledState) 0.2f else 0f

    Row(
        modifier
            .hoverable(interaction, enabled = !disabledState)
            .selectable(
                selected = checkedState,
                enabled = !disabledState,
                role = Role.RadioButton,
                interactionSource = interaction,
                indication = null,
            ) {
                if (group != null) {
                    group.onValueChange(value)
                } else if (!checkedState) {
                    onCheckedChange?.invoke(true)
                }
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(sizeDp)
                .drawBehind {
                    if (focusAlpha > 0f) {
                        drawCircle(
                            color = colors.primary.copy(alpha = focusAlpha),
                            radius = this.size.minDimension / 2 + 2.dp.toPx(),
                            style = Stroke(width = 2.dp.toPx()),
                        )
                    }
                }
                .clip(CircleShape)
                .background(if (disabledState) colors.inputDisabled else colors.base)
                .nBorder(borderColor, 1.dp, sizeDp / 2),
            contentAlignment = Alignment.Center,
        ) {
            if (checkedState) {
                Canvas(Modifier.size(sizeDp - 8.dp)) {
                    drawCircle(dotColor)
                }
            }
        }
        when {
            content != null -> {
                Spacer(Modifier.width(8.dp))
                CompositionLocalProvider(
                    LocalNContentColor provides textColor,
                    LocalNContentTextStyle provides textStyle,
                ) {
                    content()
                }
                Spacer(Modifier.width(8.dp))
            }
            label != null -> {
                Spacer(Modifier.width(8.dp))
                NBaseText(label, textStyle)
                Spacer(Modifier.width(8.dp))
            }
        }
    }
}

/**
 * Button styled radio. Usually placed inside an [NRadioGroup].
 */
@Composable
fun NRadioButton(
    value: Any? = null,
    label: String? = null,
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    size: NSize? = null,
    content: (@Composable () -> Unit)? = null,
    checked: Boolean? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val colors = theme.colors
    val group = LocalNRadioGroup.current
    val resolvedSize = group?.size ?: nSize(size)
    val checkedState = group?.let { it.value == value } ?: (checked ?: false)
    val disabledState = disabled || (group?.disabled ?: false)
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val focused by interaction.collectIsFocusedAsState()

    val borderColor = when {
        disabledState -> colors.border.copy(alpha = theme.opacityDisabled)
        checkedState -> colors.primary
        else -> colors.border
    }
    val textColor = when {
        disabledState -> colors.textDisabled
        checkedState -> colors.primary
        hovered -> colors.primaryHover
        else -> colors.text2
    }
    val height = theme.heightFor(resolvedSize)
    val shape = if (group == null) RoundedCornerShape(theme.borderRadius) else RoundedCornerShape(0.dp)
    val focusAlpha = if (focused && !disabledState) 0.3f else 0f

    Box(
        modifier
            .defaultMinSize(minHeight = height)
            .hoverable(interaction, enabled = !disabledState)
            .selectable(
                selected = checkedState,
                enabled = !disabledState,
                role = Role.RadioButton,
                interactionSource = interaction,
                indication = null,
            ) {
                if (group != null) {
                    group.onValueChange(value)
                } else if (!checkedState) {
                    onCheckedChange?.invoke(true)
                }
            }
            .background(colors.base)
            .nBorder(borderColor, 1.dp, if (group == null) theme.borderRadius else 0.dp)
            .drawBehind {
                if (focusAlpha > 0f) {
                    drawRoundRect(
                        color = colors.primary.copy(alpha = focusAlpha),
                        topLeft = Offset(-2.dp.toPx(), -2.dp.toPx()),
                        size = Size(this.size.width + 4.dp.toPx(), this.size.height + 4.dp.toPx()),
                        cornerRadius = CornerRadius(theme.borderRadius.toPx() + 2.dp.toPx()),
                        style = Stroke(width = 2.dp.toPx()),
                    )
                }
            }
            .padding(horizontal = 14.dp)
            .height(height),
        contentAlignment = Alignment.Center,
    ) {
        val textStyle = theme.typography.body.copy(
            color = textColor,
            fontSize = theme.fontSizeFor(resolvedSize),
        )
        if (content != null) {
            CompositionLocalProvider(
                LocalNContentColor provides textColor,
                LocalNContentTextStyle provides textStyle,
            ) {
                content()
            }
        } else if (label != null) {
            NBaseText(label, textStyle)
        }
    }
}

/**
 * Radio group managing a single selected [value].
 */
@Composable
fun NRadioGroup(
    value: Any?,
    onValueChange: (Any?) -> Unit,
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    size: NSize? = null,
    content: @Composable () -> Unit,
) {
    val resolvedSize = nSize(size)
    val context = remember(value, disabled, resolvedSize, onValueChange) {
        NRadioGroupContext(
            value = value,
            disabled = disabled,
            size = resolvedSize,
            onValueChange = onValueChange,
        )
    }
    CompositionLocalProvider(LocalNRadioGroup provides context) {
        Row(
            modifier,
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
    }
}
