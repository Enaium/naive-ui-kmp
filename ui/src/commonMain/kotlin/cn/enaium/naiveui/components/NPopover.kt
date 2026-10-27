package cn.enaium.naiveui.components

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NPopoverSurface
import cn.enaium.naiveui.internal.NTrigger
import cn.enaium.naiveui.internal.nTapTrigger
import kotlinx.coroutines.delay

/**
 * A rich popup anchored to an element.
 *
 * The popup keeps itself open while the pointer is over its content, and the
 * width can be constrained with [width]. Set [showArrow] to render an arrow
 * pointing at the trigger.
 */
@Composable
fun NPopover(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    trigger: NTrigger = NTrigger.Hover,
    placement: NPlacement = NPlacement.Top,
    width: Dp? = null,
    showArrow: Boolean = true,
    disabled: Boolean = false,
    delay: Long = 100,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    children: @Composable () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    val interaction = remember { MutableInteractionSource() }
    val contentInteraction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val contentHovered by contentInteraction.collectIsHoveredAsState()

    LaunchedEffect(hovered, contentHovered, disabled, trigger) {
        if (disabled) {
            visible = false
            return@LaunchedEffect
        }
        if (trigger == NTrigger.Hover) {
            if (hovered || contentHovered) {
                delay(delay)
                visible = true
            } else if (visible) {
                delay(delay)
                visible = false
            }
        }
    }

    val triggerModifier = when (trigger) {
        NTrigger.Click, NTrigger.Focus -> Modifier.nTapTrigger(enabled = !disabled) {
            visible = !visible
        }
        else -> Modifier
    }

    Box(
        modifier
            .then(if (trigger == NTrigger.Hover) Modifier.hoverable(interaction, enabled = !disabled) else Modifier)
            .then(triggerModifier),
    ) {
        children()
        NPopoverSurface(
            visible = visible,
            onDismiss = { visible = false },
            placement = placement,
            focusable = trigger != NTrigger.Hover,
            dismissOnClickOutside = true,
            padding = contentPadding,
            showArrow = showArrow,
        ) {
            Box(
                Modifier
                    .then(if (width != null) Modifier.width(width) else Modifier)
                    .hoverable(contentInteraction, enabled = trigger == NTrigger.Hover),
            ) {
                content()
            }
        }
    }
}
