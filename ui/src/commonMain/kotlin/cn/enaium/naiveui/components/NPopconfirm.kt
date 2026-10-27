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
 * Runs [onPositiveClick] or [onNegativeClick] and closes the popup. The action
 * row can be replaced with [actions] and the icon with [icon].
 */
@Composable
fun NPopconfirm(
    text: String? = null,
    modifier: Modifier = Modifier,
    positiveText: String? = null,
    negativeText: String? = null,
    showIcon: Boolean = true,
    showPositive: Boolean = true,
    showNegative: Boolean = true,
    type: NStatus = NStatus.Default,
    trigger: NTrigger = NTrigger.Click,
    placement: NPlacement = NPlacement.Top,
    disabled: Boolean = false,
    delay: Long = 100,
    show: Boolean? = null,
    onUpdateShow: ((Boolean) -> Unit)? = null,
    onPositiveClick: () -> Unit = {},
    onNegativeClick: () -> Unit = {},
    actions: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
    children: @Composable () -> Unit,
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
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
        if (hovered || contentHovered) {
            delay(delay)
            requestVisible(true)
        } else if (visible) {
            delay(delay)
            requestVisible(false)
        }
    }

    val triggerModifier = when (trigger) {
        NTrigger.Click, NTrigger.Focus -> Modifier.nTapTrigger(enabled = !disabled) {
            requestVisible(!visible)
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
            onDismiss = { requestVisible(false) },
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
                    if (icon != null) {
                        icon()
                    } else {
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
                    if (actions != null) {
                        actions()
                    } else if (showPositive || showNegative) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                        ) {
                            if (showNegative) {
                                NButton(
                                    size = NSize.Small,
                                    onClick = {
                                        requestVisible(false)
                                        onNegativeClick()
                                    },
                                ) {
                                    NText(negativeText ?: locale.popconfirmNo)
                                }
                            }
                            if (showPositive) {
                                NButton(
                                    size = NSize.Small,
                                    type = type.buttonType(),
                                    onClick = {
                                        requestVisible(false)
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
