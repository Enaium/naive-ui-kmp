package cn.enaium.naiveui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.internal.LocalNContentColor
import cn.enaium.naiveui.internal.LocalNContentTextStyle
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.nSize

/**
 * Generic content block equivalent to `<n-thing>`.
 *
 * Combines an optional avatar, header with extra area, description, content,
 * footer and action slots. Set [contentIndented] to place all following
 * content beside the avatar instead of below the header.
 */
@Composable
fun NThing(
    modifier: Modifier = Modifier,
    title: String? = null,
    titleExtra: String? = null,
    description: String? = null,
    content: String? = null,
    size: NSize? = null,
    contentIndented: Boolean = false,
    avatar: (@Composable () -> Unit)? = null,
    header: (@Composable () -> Unit)? = null,
    headerExtra: (@Composable RowScope.() -> Unit)? = null,
    descriptionContent: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
    contentSlot: (@Composable () -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val resolvedSize = nSize(size)
    val baseStyle = theme.typography.style(theme.fontSizeFor(resolvedSize))
    val titleSize = when (resolvedSize) {
        NSize.Tiny -> 14.sp
        NSize.Small -> 15.sp
        NSize.Medium -> 16.sp
        NSize.Large -> 18.sp
    }
    val titleStyle = baseStyle.copy(
        fontSize = titleSize,
        lineHeight = titleSize * theme.typography.lineHeight,
        fontWeight = theme.typography.fontWeightStrong,
        color = theme.colors.text1,
    )
    val descriptionStyle = baseStyle.copy(color = theme.colors.text3)
    val hasHeader = header != null || title != null || headerExtra != null || titleExtra != null
    val hasDescription = descriptionContent != null || description != null
    val hasHeaderBlock = hasHeader || hasDescription
    val hasContent = contentSlot != null || content != null

    CompositionLocalProvider(
        LocalNContentColor provides theme.colors.text2,
        LocalNContentTextStyle provides baseStyle,
    ) {
        if (contentIndented && avatar != null) {
            Row(modifier.fillMaxWidth()) {
                Box(Modifier.padding(end = 12.dp, top = 2.dp)) { avatar() }
                Column(Modifier.weight(1f)) {
                    if (hasHeaderBlock) {
                        ThingHeaderBlock(
                            title = title,
                            titleExtra = titleExtra,
                            header = header,
                            headerExtra = headerExtra,
                            description = description,
                            descriptionContent = descriptionContent,
                            titleStyle = titleStyle,
                            textStyle = baseStyle,
                            descriptionStyle = descriptionStyle,
                        )
                    }
                    ThingBodyBlock(
                        content = content,
                        contentSlot = contentSlot,
                        footer = footer,
                        action = action,
                        textStyle = baseStyle,
                        topPadding = if (hasHeaderBlock && hasContent) 12.dp else 0.dp,
                    )
                }
            }
        } else {
            Column(modifier.fillMaxWidth()) {
                if (avatar != null) {
                    Row(Modifier.fillMaxWidth()) {
                        Box(Modifier.padding(end = 12.dp, top = 2.dp)) { avatar() }
                        Column(Modifier.weight(1f)) {
                            if (hasHeaderBlock) {
                                ThingHeaderBlock(
                                    title = title,
                                    titleExtra = titleExtra,
                                    header = header,
                                    headerExtra = headerExtra,
                                    description = description,
                                    descriptionContent = descriptionContent,
                                    titleStyle = titleStyle,
                                    textStyle = baseStyle,
                                    descriptionStyle = descriptionStyle,
                                )
                            }
                        }
                    }
                    ThingBodyBlock(
                        content = content,
                        contentSlot = contentSlot,
                        footer = footer,
                        action = action,
                        textStyle = baseStyle,
                        topPadding = if (hasHeaderBlock && hasContent) 12.dp else 0.dp,
                    )
                } else {
                    if (hasHeaderBlock) {
                        ThingHeaderBlock(
                            title = title,
                            titleExtra = titleExtra,
                            header = header,
                            headerExtra = headerExtra,
                            description = description,
                            descriptionContent = descriptionContent,
                            titleStyle = titleStyle,
                            textStyle = baseStyle,
                            descriptionStyle = descriptionStyle,
                        )
                    }
                    ThingBodyBlock(
                        content = content,
                        contentSlot = contentSlot,
                        footer = footer,
                        action = action,
                        textStyle = baseStyle,
                        topPadding = if (hasHeaderBlock && hasContent) 12.dp else 0.dp,
                    )
                }
            }
        }
    }
}

@Composable
private fun ThingHeaderBlock(
    title: String?,
    titleExtra: String?,
    header: (@Composable () -> Unit)?,
    headerExtra: (@Composable RowScope.() -> Unit)?,
    description: String?,
    descriptionContent: (@Composable () -> Unit)?,
    titleStyle: TextStyle,
    textStyle: TextStyle,
    descriptionStyle: TextStyle,
) {
    if (header != null || title != null || headerExtra != null || titleExtra != null) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.weight(1f)) {
                if (header != null) header() else if (title != null) NBaseText(title, titleStyle)
            }
            if (headerExtra != null || titleExtra != null) {
                Spacer(Modifier.width(8.dp))
                if (headerExtra != null) {
                    Row(content = headerExtra)
                } else {
                    NBaseText(titleExtra.orEmpty(), textStyle.copy(color = LocalNTheme.current.colors.text3))
                }
            }
        }
    }
    if (descriptionContent != null || description != null) {
        Box(Modifier.fillMaxWidth()) {
            if (descriptionContent != null) {
                CompositionLocalProvider(
                    LocalNContentColor provides descriptionStyle.color,
                    LocalNContentTextStyle provides descriptionStyle,
                ) { descriptionContent() }
            } else {
                NBaseText(description.orEmpty(), descriptionStyle)
            }
        }
    }
}

@Composable
private fun ThingBodyBlock(
    content: String?,
    contentSlot: (@Composable () -> Unit)?,
    footer: (@Composable () -> Unit)?,
    action: (@Composable () -> Unit)?,
    textStyle: TextStyle,
    topPadding: Dp,
) {
    var hasAbove = false
    if (contentSlot != null || content != null) {
        Box(Modifier.fillMaxWidth().padding(top = topPadding)) {
            if (contentSlot != null) contentSlot() else NBaseText(content.orEmpty(), textStyle)
        }
        hasAbove = true
    }
    if (footer != null) {
        Box(Modifier.fillMaxWidth().padding(top = if (hasAbove) 12.dp else 0.dp)) { footer() }
        hasAbove = true
    }
    if (action != null) {
        Box(Modifier.fillMaxWidth().padding(top = if (hasAbove) 12.dp else 0.dp)) { action() }
    }
}
