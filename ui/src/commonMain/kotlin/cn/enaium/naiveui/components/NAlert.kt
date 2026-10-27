package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.internal.NCloseButton
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NStatus

/** Semantic type of an alert. */
enum class NAlertType { Default, Info, Success, Warning, Error }

/**
 * Inline message with an optional icon, title, action and close button.
 *
 * The tinted background and border use the type color, matching naive-ui's
 * low-contrast alert surface.
 */
@Composable
fun NAlert(
    modifier: Modifier = Modifier,
    type: NAlertType = NAlertType.Default,
    title: String? = null,
    closable: Boolean = false,
    onClose: (() -> Unit)? = null,
    showIcon: Boolean = true,
    bordered: Boolean = true,
    banner: Boolean = false,
    icon: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    var visible by remember { mutableStateOf(true) }
    if (!visible) return

    val status = when (type) {
        NAlertType.Default -> NStatus.Default
        NAlertType.Info -> NStatus.Info
        NAlertType.Success -> NStatus.Success
        NAlertType.Warning -> NStatus.Warning
        NAlertType.Error -> NStatus.Error
    }
    val accent = theme.colors.statusColor(status)
    val isDefault = type == NAlertType.Default
    val background = if (isDefault) theme.colors.action else accent.copy(alpha = 0.1f)
    val borderColor = if (isDefault) theme.colors.divider else accent.copy(alpha = 0.25f)
    val titleColor = theme.colors.text1
    val contentColor = theme.colors.text2
    val iconColor = if (isDefault) theme.colors.text2 else accent
    val iconSize = 24.dp
    val shape = if (banner) RoundedCornerShape(0.dp) else RoundedCornerShape(theme.borderRadius)
    val padding = if (banner) {
        Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    } else {
        Modifier.padding(13.dp)
    }
    val vector = when (type) {
        NAlertType.Info -> NIcons.Info
        NAlertType.Success -> NIcons.Success
        NAlertType.Warning -> NIcons.Warning
        NAlertType.Error -> NIcons.Error
        NAlertType.Default -> null
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(background, shape)
            .then(
                if (bordered && !banner) {
                    Modifier.nBorder(borderColor, 1.dp, theme.borderRadius)
                } else {
                    Modifier
                },
            )
            .then(padding),
        verticalAlignment = Alignment.Top,
    ) {
        if (showIcon && (icon != null || vector != null)) {
            Box(
                Modifier.size(iconSize).padding(top = 1.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (icon != null) {
                    icon()
                } else if (vector != null) {
                    NIcon(vector, size = iconSize, color = iconColor)
                }
            }
            Spacer(Modifier.width(8.dp))
        }
        Column(Modifier.weight(1f)) {
            if (title != null) {
                NBaseText(
                    text = title,
                    style = theme.typography.style(16.sp, theme.typography.fontWeightStrong, titleColor)
                        .copy(lineHeight = 19.sp),
                )
                if (content != null) Spacer(Modifier.height(9.dp))
            }
            if (content != null) {
                androidx.compose.runtime.CompositionLocalProvider(
                    cn.enaium.naiveui.internal.LocalNContentColor provides contentColor,
                    cn.enaium.naiveui.internal.LocalNContentTextStyle provides
                        theme.typography.body.copy(color = contentColor),
                ) {
                    content()
                }
            }
        }
        if (action != null) {
            Spacer(Modifier.width(12.dp))
            Box(Modifier.padding(top = 1.dp)) { action() }
        }
        if (closable) {
            Spacer(Modifier.width(8.dp))
            NCloseButton(
                onClick = {
                    onClose?.invoke()
                    visible = false
                },
                modifier = Modifier.padding(top = 1.dp),
                size = 18.dp,
                iconSize = 12.dp,
            )
        }
    }
}
