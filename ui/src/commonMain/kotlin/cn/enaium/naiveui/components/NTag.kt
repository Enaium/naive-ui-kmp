package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.internal.LocalNContentColor
import cn.enaium.naiveui.internal.LocalNContentTextStyle
import cn.enaium.naiveui.internal.NCloseButton
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus

/**
 * Tag for categorization. Supports checkable and closable variants and all
 * semantic types.
 */
@Composable
fun NTag(
    text: String? = null,
    modifier: Modifier = Modifier,
    type: NStatus = NStatus.Default,
    size: NSize = NSize.Medium,
    round: Boolean = false,
    bordered: Boolean = true,
    closable: Boolean = false,
    onClose: () -> Unit = {},
    checkable: Boolean = false,
    checked: Boolean? = null,
    onCheckedChange: (Boolean) -> Unit = {},
    disabled: Boolean = false,
    strong: Boolean = false,
    color: Color? = null,
    textColor: Color? = null,
    content: (@Composable RowScope.() -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val isDefault = type == NStatus.Default
    val base = color ?: theme.colors.statusColor(type)
    var internalChecked by remember { mutableStateOf(false) }
    val isChecked = checked ?: internalChecked
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()

    val height = when (size) {
        NSize.Tiny -> 16.dp
        NSize.Small -> 22.dp
        NSize.Medium -> 28.dp
        NSize.Large -> 34.dp
    }
    val fontSize = when (size) {
        NSize.Tiny, NSize.Small -> theme.typography.fontSize.mini
        NSize.Medium -> theme.typography.fontSize.small
        NSize.Large -> theme.typography.fontSize.medium
    }
    val closeSize = when (size) {
        NSize.Tiny, NSize.Small -> 16.dp
        else -> 18.dp
    }
    val shape = if (round) RoundedCornerShape(percent = 50) else RoundedCornerShape(theme.borderRadiusSmall)
    val checkedBg = base
    val normalBg = when {
        strong -> base
        !isDefault -> base.copy(alpha = 0.12f)
        bordered -> theme.colors.action
        else -> theme.colors.tag
    }
    val bg = if (checkable && isChecked) checkedBg else normalBg
    val fg = when {
        textColor != null -> textColor
        checkable && isChecked -> theme.colors.base
        strong -> theme.colors.base
        !isDefault -> base
        else -> theme.colors.text2
    }.let { if (disabled) it.copy(alpha = theme.opacityDisabled) else it }
    val border = when {
        !bordered -> Color.Transparent
        checkable && isChecked -> base
        !isDefault -> base.copy(alpha = 0.3f)
        else -> theme.colors.border
    }
    val hoverBg = when {
        checkable && !isChecked -> theme.colors.buttonColor2Hover
        !isDefault && !strong -> base.copy(alpha = if (hovered) 0.2f else 0.12f)
        else -> bg
    }
    val textStyle = TextStyle(
        fontFamily = theme.typography.fontFamily,
        fontSize = fontSize,
        lineHeight = fontSize,
        fontWeight = if (strong) theme.typography.fontWeightStrong else theme.typography.fontWeight,
        color = fg,
    )

    Box(
        modifier
            .height(height)
            .clip(shape)
            .background(if (hovered && checkable) hoverBg else bg)
            .then(if (bordered) Modifier.nBorder(border, 1.dp, if (round) height / 2 else theme.borderRadiusSmall) else Modifier)
            .hoverable(interaction, enabled = !disabled)
            .nClickable(
                interaction,
                enabled = checkable && !disabled,
            ) {
                internalChecked = !isChecked
                onCheckedChange(!isChecked)
            }
            .padding(horizontal = 7.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        CompositionLocalProvider(
            LocalNContentColor provides fg,
            LocalNContentTextStyle provides textStyle,
        ) {
            Row(
                Modifier.padding(vertical = 0.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (content != null) {
                    content()
                } else if (text != null) {
                    NText(text, style = textStyle)
                }
                if (closable) {
                    Box(Modifier.size(4.dp))
                    NCloseButton(
                        onClick = onClose,
                        size = closeSize,
                        iconSize = if (size == NSize.Tiny || size == NSize.Small) 10.dp else 12.dp,
                    )
                }
            }
        }
    }
}
