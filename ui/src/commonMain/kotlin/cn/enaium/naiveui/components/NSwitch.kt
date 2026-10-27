package cn.enaium.naiveui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.internal.NBaseLoading
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.nSize

private data class NSwitchMetrics(
    val railWidth: Dp,
    val railHeight: Dp,
    val buttonWidth: Dp,
    val buttonWidthPressed: Dp,
)

private fun NSize.switchMetrics(): NSwitchMetrics = when (this) {
    NSize.Tiny, NSize.Small -> NSwitchMetrics(32.dp, 18.dp, 14.dp, 20.dp)
    NSize.Medium -> NSwitchMetrics(40.dp, 22.dp, 18.dp, 24.dp)
    NSize.Large -> NSwitchMetrics(48.dp, 26.dp, 22.dp, 28.dp)
}

/**
 * Switch toggle with animated thumb, optional inline text and loading state.
 */
@Composable
fun NSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    size: NSize? = null,
    activeText: String? = null,
    inactiveText: String? = null,
    inlinePrompt: Boolean = false,
    loading: Boolean = false,
    round: Boolean = true,
    railColor: ((checked: Boolean) -> Color)? = null,
    icon: (@Composable () -> Unit)? = null,
    checkedIcon: (@Composable () -> Unit)? = null,
    uncheckedIcon: (@Composable () -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val colors = theme.colors
    val resolvedSize = nSize(size)
    val metrics = resolvedSize.switchMetrics()
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val focused by interaction.collectIsFocusedAsState()
    val enabled = !disabled && !loading

    val railShape = if (round) CircleShape else RoundedCornerShape(theme.borderRadiusSmall)
    val buttonShape = if (round) CircleShape else RoundedCornerShape(theme.borderRadiusSmall)
    val resolvedRailColor by animateColorAsState(
        targetValue = railColor?.invoke(checked)
            ?: if (checked) colors.primary else Color.Black.copy(alpha = 0.14f),
        animationSpec = tween(200),
        label = "switchRail",
    )
    val buttonWidth by animateDpAsState(
        targetValue = if (pressed && enabled) metrics.buttonWidthPressed else metrics.buttonWidth,
        animationSpec = tween(150),
        label = "switchButtonWidth",
    )
    val textColor = Color.White
    val inlineContent = inlinePrompt && (if (checked) activeText else inactiveText) != null

    Row(
        modifier
            .hoverable(interaction, enabled = enabled)
            .toggleable(
                value = checked,
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Switch,
            ) {
                onCheckedChange?.invoke(!checked)
            }
            .then(if (disabled) Modifier.alpha(theme.opacityDisabled) else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .then(if (inlineContent) Modifier.widthIn(min = metrics.railWidth) else Modifier.width(metrics.railWidth))
                .height(metrics.railHeight)
                .drawBehind {
                    if (focused && enabled) {
                        drawRoundRect(
                            color = colors.primary.copy(alpha = 0.2f),
                            topLeft = Offset(-2.dp.toPx(), -2.dp.toPx()),
                            size = Size(this.size.width + 4.dp.toPx(), this.size.height + 4.dp.toPx()),
                            cornerRadius = CornerRadius(this.size.height / 2 + 2.dp.toPx()),
                            style = Stroke(width = 2.dp.toPx()),
                        )
                    }
                }
                .clip(railShape)
                .background(resolvedRailColor, railShape),
        ) {
            val edge = 2.dp
            val targetOffset = if (checked) edge else (maxWidth - buttonWidth - edge).coerceAtLeast(0.dp)
            val thumbOffset by animateDpAsState(
                targetValue = targetOffset,
                animationSpec = tween(200),
                label = "switchThumbOffset",
            )
            if (inlineContent) {
                val prompt = if (checked) activeText else inactiveText
                if (prompt != null) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(metrics.railHeight)
                            .padding(
                                start = if (checked) buttonWidth + 4.dp else 7.dp,
                                end = if (checked) 7.dp else buttonWidth + 4.dp,
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = if (checked) Arrangement.End else Arrangement.Start,
                    ) {
                        NBaseText(
                            prompt,
                            theme.typography.body.copy(color = textColor, fontSize = 12.sp),
                            maxLines = 1,
                        )
                    }
                }
            }
            Box(
                Modifier
                    .offset(x = thumbOffset)
                    .align(Alignment.CenterStart)
                    .width(buttonWidth)
                    .height(metrics.buttonWidth)
                    .shadow(2.dp, buttonShape, clip = false, ambientColor = Color.Black.copy(alpha = 0.3f))
                    .background(Color.White, buttonShape),
                contentAlignment = Alignment.Center,
            ) {
                if (loading) {
                    NBaseLoading(
                        size = metrics.buttonWidth * 0.72f,
                        color = colors.primary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    val iconSlot = if (checked) checkedIcon ?: icon else uncheckedIcon ?: icon
                    if (iconSlot != null) {
                        CompositionLocalProvider(
                            cn.enaium.naiveui.icons.LocalNIconColor provides colors.text3,
                        ) {
                            iconSlot()
                        }
                    }
                }
            }
        }
        if (!inlinePrompt) {
            val outsideText = if (checked) activeText else inactiveText
            if (outsideText != null) {
                NBaseText(
                    outsideText,
                    theme.typography.body.copy(
                        color = if (disabled) colors.textDisabled else colors.text2,
                        fontSize = theme.fontSizeFor(resolvedSize),
                    ),
                )
            }
        }
    }
}
