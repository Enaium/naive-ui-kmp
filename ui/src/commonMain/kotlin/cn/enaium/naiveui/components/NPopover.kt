package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NPopoverSurface
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.NTrigger
import cn.enaium.naiveui.internal.nTapTrigger
import cn.enaium.naiveui.theme.LocalNTheme
import kotlinx.coroutines.delay

/**
 * A rich popup anchored to an element.
 *
 * The popup keeps itself open while the pointer is over its content. Use
 * [show]/[onUpdateShow] for two-way visibility control (required by
 * [NTrigger.Manual]), [triggerWidth] to mirror the anchor width, and
 * [header]/[footer] for divided sections.
 */
@Composable
fun NPopover(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    trigger: NTrigger = NTrigger.Hover,
    placement: NPlacement = NPlacement.Top,
    width: Dp? = null,
    maxWidth: Dp? = null,
    triggerWidth: Boolean = false,
    showArrow: Boolean = true,
    disabled: Boolean = false,
    delay: Long = 100,
    duration: Long = 0,
    keepAliveOnHover: Boolean = true,
    show: Boolean? = null,
    onUpdateShow: ((Boolean) -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    scrollable: Boolean = false,
    maxHeight: Dp? = null,
    raw: Boolean = false,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    children: @Composable () -> Unit,
) {
    val theme = LocalNTheme.current
    var innerVisible by remember { mutableStateOf(false) }
    val visible = show ?: innerVisible
    val interaction = remember { MutableInteractionSource() }
    val contentInteraction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val contentHovered by contentInteraction.collectIsHoveredAsState()

    fun requestVisible(next: Boolean) {
        if (next == visible) return
        if (show == null) innerVisible = next
        onUpdateShow?.invoke(next)
    }

    LaunchedEffect(hovered, contentHovered, disabled, trigger, show) {
        if (disabled) {
            if (show == null && innerVisible) innerVisible = false
            return@LaunchedEffect
        }
        if (trigger != NTrigger.Hover) return@LaunchedEffect
        val active = hovered || (keepAliveOnHover && contentHovered)
        if (active) {
            delay(delay)
            requestVisible(true)
        } else if (visible) {
            delay(duration)
            requestVisible(false)
        }
    }

    val density = LocalDensity.current
    var anchorWidth by remember { mutableStateOf(0.dp) }
    val triggerModifier = when (trigger) {
        NTrigger.Click, NTrigger.Focus -> Modifier.nTapTrigger(enabled = !disabled) {
            requestVisible(!visible)
        }
        else -> Modifier
    }
    val resolvedWidth = width ?: if (triggerWidth && anchorWidth > 0.dp) anchorWidth else null
    val widthModifier = when {
        resolvedWidth != null -> Modifier.width(resolvedWidth)
        maxWidth != null -> Modifier.widthIn(max = maxWidth)
        else -> Modifier
    }

    Box(
        modifier
            .onGloballyPositioned { coordinates ->
                anchorWidth = with(density) { coordinates.size.width.toDp() }
            }
            .then(if (trigger == NTrigger.Hover) Modifier.hoverable(interaction, enabled = !disabled) else Modifier)
            .then(triggerModifier),
    ) {
        children()
        NPopoverSurface(
            visible = visible,
            onDismiss = { requestVisible(false) },
            placement = placement,
            focusable = trigger != NTrigger.Hover,
            dismissOnClickOutside = true,
            padding = if (raw) PaddingValues(0.dp) else contentPadding,
            color = if (raw) Color.Transparent else null,
            borderColor = if (raw) Color.Transparent else null,
            showArrow = showArrow,
        ) {
            Column(widthModifier.hoverable(contentInteraction, enabled = trigger == NTrigger.Hover)) {
                if (header != null) {
                    header()
                    NPopoverDivider(theme.colors.divider)
                }
                if (scrollable) {
                    NScrollbar(Modifier.heightIn(max = maxHeight ?: 240.dp)) {
                        Box(Modifier.fillMaxWidth()) { content() }
                    }
                } else {
                    content()
                }
                if (footer != null) {
                    NPopoverDivider(theme.colors.divider)
                    footer()
                }
            }
        }
    }
}

@Composable
private fun NPopoverDivider(color: Color) {
    Box(Modifier.fillMaxWidth().height(1.dp).background(color))
}
