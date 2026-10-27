package cn.enaium.naiveui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NPopoverSurface
import cn.enaium.naiveui.theme.LocalNTheme

/** What interaction expands a truncated [NEllipsis]. */
enum class NEllipsisExpandTrigger { Click, Hover }

/**
 * Text that is truncated after [lineClamp] lines, with an optional tooltip
 * showing the full content.
 */
@Composable
fun NEllipsis(
    text: String,
    modifier: Modifier = Modifier,
    lineClamp: Int? = null,
    tooltip: Boolean = true,
    expandTrigger: NEllipsisExpandTrigger = NEllipsisExpandTrigger.Click,
    tooltipMaxWidth: Dp = 360.dp,
    content: (@Composable () -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    var clickedExpanded by remember { mutableStateOf(false) }
    var hasOverflow by remember { mutableStateOf(false) }

    val hoverExpands = expandTrigger == NEllipsisExpandTrigger.Hover && hovered
    val expanded = clickedExpanded || hoverExpands
    val maxLines = if (expanded) Int.MAX_VALUE else (lineClamp ?: 1)
    val overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis
    val showTooltip = tooltip && (content != null || hasOverflow) && !expanded && hovered

    Box(
        modifier
            .hoverable(interaction)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = expandTrigger == NEllipsisExpandTrigger.Click,
            ) {
                if (expandTrigger == NEllipsisExpandTrigger.Click) {
                    clickedExpanded = !clickedExpanded
                }
            },
    ) {
        val textStyle = theme.typography.body.copy(color = theme.colors.text2)
        if (content != null) {
            val density = androidx.compose.ui.platform.LocalDensity.current
            val lineHeight = with(density) {
                (theme.typography.fontSize.medium * theme.typography.lineHeight).toDp()
            }
            val limitModifier = if (expanded) {
                Modifier
            } else {
                Modifier.heightIn(max = lineHeight * maxLines).clipToBounds()
            }
            Box(limitModifier) { content() }
        } else {
            BasicText(
                text = text,
                style = textStyle,
                maxLines = maxLines,
                overflow = overflow,
                onTextLayout = { layout: TextLayoutResult -> hasOverflow = layout.hasVisualOverflow },
            )
        }
        NPopoverSurface(
            visible = showTooltip,
            onDismiss = {},
            placement = NPlacement.Top,
            focusable = false,
            dismissOnClickOutside = false,
            dismissOnBackPress = false,
            padding = PaddingValues(8.dp),
        ) {
            Box(Modifier.widthIn(max = tooltipMaxWidth)) {
                BasicText(
                    text = text,
                    style = TextStyle(
                        color = theme.colors.text2,
                        fontSize = theme.typography.fontSize.medium,
                        lineHeight = theme.typography.fontSize.medium * theme.typography.lineHeight,
                        fontFamily = theme.typography.fontFamily,
                    ),
                )
            }
        }
    }
}
