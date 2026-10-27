package cn.enaium.naiveui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.nSize

@Immutable
internal data class NCheckboxGroupContext(
    val value: List<Any>,
    val disabled: Boolean,
    val size: NSize,
    val min: Int?,
    val max: Int?,
    val toggle: (Any, Boolean) -> Unit,
)

internal val LocalNCheckboxGroup = staticCompositionLocalOf<NCheckboxGroupContext?> { null }

private fun NSize.checkboxBox(): Dp = when (this) {
    NSize.Tiny, NSize.Small -> 14.dp
    NSize.Medium -> 16.dp
    NSize.Large -> 18.dp
}

/**
 * Checkbox with optional label or custom slot content.
 *
 * Inside an [NCheckboxGroup] the checked state is derived from the group when
 * [value] is provided; otherwise the [checked] parameter is used.
 */
@Composable
fun NCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    indeterminate: Boolean = false,
    size: NSize? = null,
    label: String? = null,
    content: (@Composable () -> Unit)? = null,
    value: Any? = null,
) {
    val theme = LocalNTheme.current
    val colors = theme.colors
    val group = LocalNCheckboxGroup.current
    val inGroup = group != null && value != null
    val resolvedSize = group?.size ?: nSize(size)
    val checkedState = if (inGroup) group!!.value.contains(value) else checked
    val disabledState = disabled || (group?.disabled ?: false)
    val boxSize = resolvedSize.checkboxBox()
    val shape = RoundedCornerShape(theme.borderRadiusSmall)
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val focused by interaction.collectIsFocusedAsState()

    val active = checkedState || indeterminate
    val boxColor = when {
        disabledState && active -> colors.inputDisabled
        active -> colors.primary
        disabledState -> colors.inputDisabled
        hovered -> colors.base
        else -> colors.base
    }
    val borderColor = when {
        disabledState -> colors.border
        active -> colors.primary
        hovered || focused -> colors.primary
        else -> colors.border
    }
    val markColor = if (disabledState) colors.textDisabled else colors.base
    val focusAlpha = if (focused && !disabledState) 0.3f else 0f

    Row(
        modifier
            .hoverable(interaction, enabled = !disabledState)
            .toggleable(
                value = checkedState,
                enabled = !disabledState,
                role = Role.Checkbox,
                interactionSource = interaction,
                indication = null,
            ) {
                if (inGroup) {
                    group!!.toggle(value as Any, !checkedState)
                } else {
                    onCheckedChange?.invoke(!checkedState)
                }
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(boxSize)
                .drawBehind {
                    if (focusAlpha > 0f) {
                        drawRoundRect(
                            color = colors.primary.copy(alpha = focusAlpha),
                            topLeft = Offset(-2.dp.toPx(), -2.dp.toPx()),
                            size = Size(this.size.width + 4.dp.toPx(), this.size.height + 4.dp.toPx()),
                            cornerRadius = CornerRadius(theme.borderRadiusSmall.toPx() + 2.dp.toPx()),
                            style = Stroke(width = 2.dp.toPx()),
                        )
                    }
                }
                .clip(shape)
                .background(boxColor, shape)
                .nBorder(if (active && !disabledState) colors.primary else borderColor, 1.dp, theme.borderRadiusSmall),
            contentAlignment = Alignment.Center,
        ) {
            if (active) {
                Canvas(Modifier.matchParentSize()) {
                    val w = this.size.width
                    val h = this.size.height
                    val stroke = this.size.minDimension * 0.15f
                    if (indeterminate) {
                        drawLine(
                            color = markColor,
                            start = Offset(w * 0.22f, h * 0.5f),
                            end = Offset(w * 0.78f, h * 0.5f),
                            strokeWidth = stroke,
                            cap = StrokeCap.Round,
                        )
                    } else {
                        val path = Path().apply {
                            moveTo(w * 0.2f, h * 0.52f)
                            lineTo(w * 0.42f, h * 0.73f)
                            lineTo(w * 0.8f, h * 0.29f)
                        }
                        drawPath(
                            path = path,
                            color = markColor,
                            style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round),
                        )
                    }
                }
            }
        }
        val textColor = if (disabledState) colors.textDisabled else colors.text2
        val textStyle = theme.typography.body.copy(
            color = textColor,
            fontSize = theme.fontSizeFor(resolvedSize),
        )
        when {
            content != null -> {
                Spacer(Modifier.width(8.dp))
                CompositionLocalProvider(
                    cn.enaium.naiveui.internal.LocalNContentColor provides textColor,
                    cn.enaium.naiveui.internal.LocalNContentTextStyle provides textStyle,
                ) {
                    content()
                }
            }
            label != null -> {
                Spacer(Modifier.width(8.dp))
                NBaseText(label, textStyle)
            }
        }
    }
}

/**
 * Groups child checkboxes so their selected values are managed together.
 *
 * [min] and [max] clamp the number of selected values. Children must provide a
 * [NCheckbox.value] to participate.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NCheckboxGroup(
    value: List<Any>,
    onValueChange: (List<Any>) -> Unit,
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    size: NSize? = null,
    min: Int? = null,
    max: Int? = null,
    content: @Composable () -> Unit,
) {
    val resolvedSize = nSize(size)
    val context = remember(value, disabled, resolvedSize, min, max, onValueChange) {
        NCheckboxGroupContext(
            value = value,
            disabled = disabled,
            size = resolvedSize,
            min = min,
            max = max,
            toggle = { checkedValue, checked ->
                if (checked) {
                    if (max == null || value.size < max) {
                        if (!value.contains(checkedValue)) onValueChange(value + checkedValue)
                    }
                } else {
                    if (min == null || value.size > min) {
                        onValueChange(value - checkedValue)
                    }
                }
            },
        )
    }
    CompositionLocalProvider(LocalNCheckboxGroup provides context) {
        FlowRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            content()
        }
    }
}
