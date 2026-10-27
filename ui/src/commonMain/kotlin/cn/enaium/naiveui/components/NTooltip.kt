package cn.enaium.naiveui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NTrigger
import cn.enaium.naiveui.theme.LocalNTheme

/**
 * Textual hint shown next to an element.
 *
 * Pass [text] for the common case or [content] for arbitrary popup content.
 * The tooltip opens on hover by default; [trigger] switches to click.
 */
@Composable
fun NTooltip(
    text: String? = null,
    modifier: Modifier = Modifier,
    placement: NPlacement = NPlacement.Top,
    trigger: NTrigger = NTrigger.Hover,
    disabled: Boolean = false,
    delay: Long = 100,
    width: Dp? = null,
    showArrow: Boolean = true,
    content: (@Composable () -> Unit)? = null,
    children: @Composable () -> Unit,
) {
    val theme = LocalNTheme.current
    NPopover(
        modifier = modifier,
        trigger = trigger,
        placement = placement,
        width = width,
        showArrow = showArrow,
        disabled = disabled,
        delay = delay,
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        content = {
            Box(
                if (width == null) Modifier.widthIn(max = 280.dp) else Modifier,
            ) {
                if (content != null) {
                    content()
                } else {
                    NText(
                        text = text ?: "",
                        color = theme.colors.text2,
                        size = theme.typography.fontSize.mini,
                    )
                }
            }
        },
        children = children,
    )
}
