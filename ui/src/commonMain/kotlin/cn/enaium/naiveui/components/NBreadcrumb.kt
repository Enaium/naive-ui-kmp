package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.internal.LocalNContentColor
import cn.enaium.naiveui.internal.LocalNContentTextStyle
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.theme.LocalNTheme

private class NBreadcrumbContext(
    val separator: String,
    val separatorContent: (@Composable () -> Unit)?,
    val items: MutableList<Any>,
)

private val LocalNBreadcrumb = staticCompositionLocalOf<NBreadcrumbContext?> { null }

/**
 * Breadcrumb trail. [separator] is used by default, or [separatorContent] for
 * an icon or other custom separator.
 */
@Composable
fun NBreadcrumb(
    modifier: Modifier = Modifier,
    separator: String = "/",
    separatorContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val items = remember { mutableStateListOf<Any>() }
    val context = remember(separator, separatorContent) {
        NBreadcrumbContext(separator, separatorContent, items)
    }
    CompositionLocalProvider(LocalNBreadcrumb provides context) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            content()
        }
    }
}

/**
 * One entry of an [NBreadcrumb].
 *
 * [href] is opened through the platform URI handler when supplied and no
 * [onClick] callback is given. The last item is emphasized and never renders a
 * trailing separator.
 */
@Composable
fun NBreadcrumbItem(
    modifier: Modifier = Modifier,
    href: String? = null,
    clickable: Boolean = true,
    showSeparator: Boolean = true,
    separator: String? = null,
    separatorContent: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val theme = LocalNTheme.current
    val context = LocalNBreadcrumb.current
    val token = remember { Any() }
    DisposableEffect(context, token) {
        context?.items?.add(token)
        onDispose { context?.items?.remove(token) }
    }
    val isLast = context != null && context.items.lastOrNull() === token
    val isClickable = clickable && (href != null || onClick != null)
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val uriHandler = LocalUriHandler.current
    val textColor = when {
        isLast -> theme.colors.text1
        hovered && isClickable -> theme.colors.text2
        else -> theme.colors.text3
    }
    val shape = RoundedCornerShape(theme.borderRadiusSmall)
    val fontSize = theme.typography.fontSize.medium

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .clip(shape)
                .background(if (hovered && isClickable) theme.colors.hover else Color.Transparent)
                .hoverable(interaction)
                .nClickable(interaction, enabled = isClickable) {
                    onClick?.invoke() ?: href?.let { uriHandler.openUri(it) }
                }
                .padding(horizontal = 2.dp, vertical = 1.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CompositionLocalProvider(
                LocalNContentColor provides textColor,
                LocalNContentTextStyle provides theme.typography.style(
                    fontSize,
                    if (isLast) FontWeight.Medium else theme.typography.fontWeight,
                    textColor,
                ),
            ) {
                content()
            }
        }
        if (!isLast && showSeparator) {
            val customSeparator = separatorContent ?: context?.separatorContent
            if (customSeparator != null) {
                Row(
                    Modifier.padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    customSeparator()
                }
            } else {
                NBaseText(
                    text = separator ?: context?.separator ?: "/",
                    style = theme.typography.style(
                        fontSize,
                        color = theme.colors.text3,
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }
        }
    }
}
