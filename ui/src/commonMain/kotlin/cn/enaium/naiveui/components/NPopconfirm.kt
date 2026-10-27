package cn.enaium.naiveui.components

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NButtonType
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NPopoverSurface
import cn.enaium.naiveui.internal.NTrigger
import cn.enaium.naiveui.internal.nTapTrigger
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import kotlinx.coroutines.delay

/**
 * Small confirmation popup anchored to an action trigger.
 *
 * Runs [onPositiveClick] or [onNegativeClick] and closes the popup. Use
 * [content] for custom body content.
 */
@Composable
fun NPopconfirm(
    text: String? = null,
    modifier: Modifier = Modifier,
    positiveText: String? = null,
    negativeText: String? = null,
    showIcon: Boolean = true,
    type: NStatus = NStatus.Default,
    trigger: NTrigger = NTrigger.Click,
    placement: NPlacement = NPlacement.Top,
    disabled: Boolean = false,
    delay: Long = 100,
    onPositiveClick: () -> Unit = {},
    onNegativeClick: () -> Unit = {},
    content: (@Composable () -> Unit)? = null,
    children: @Composable () -> Unit,
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
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
            padding = PaddingValues(12.dp),
        ) {
            Row(
                Modifier
                    .widthIn(min = 200.dp, max = 300.dp)
                    .hoverable(contentInteraction, enabled = trigger == NTrigger.Hover),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (showIcon) {
                    NIcon(
                        icon = type.icon(),
                        size = 20.dp,
                        color = if (type == NStatus.Default) {
                            theme.colors.warning
                        } else {
                            theme.colors.statusColor(type)
                        },
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (text != null) {
                        NText(
                            text = text,
                            color = theme.colors.text2,
                            size = theme.typography.fontSize.medium,
                        )
                    }
                    if (content != null) content()
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    ) {
                        NButton(
                            size = NSize.Small,
                            onClick = {
                                visible = false
                                onNegativeClick()
                            },
                        ) {
                            NText(negativeText ?: locale.popconfirmNo)
                        }
                        NButton(
                            size = NSize.Small,
                            type = type.buttonType(),
                            onClick = {
                                visible = false
                                onPositiveClick()
                            },
                        ) {
                            NText(positiveText ?: locale.popconfirmYes)
                        }
                    }
                }
            }
        }
    }
}

private fun NStatus.icon(): ImageVector = when (this) {
    NStatus.Default, NStatus.Warning -> NIcons.Warning
    NStatus.Primary, NStatus.Info -> NIcons.Info
    NStatus.Success -> NIcons.Success
    NStatus.Error -> NIcons.Error
}

private fun NStatus.buttonType(): NButtonType = when (this) {
    NStatus.Default, NStatus.Primary -> NButtonType.Primary
    NStatus.Info -> NButtonType.Info
    NStatus.Success -> NButtonType.Success
    NStatus.Warning -> NButtonType.Warning
    NStatus.Error -> NButtonType.Error
}
