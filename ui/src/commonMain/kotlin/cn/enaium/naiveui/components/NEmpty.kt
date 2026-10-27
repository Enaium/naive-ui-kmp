package cn.enaium.naiveui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.internal.LocalNContentColor
import cn.enaium.naiveui.internal.LocalNContentTextStyle
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.nSize

/**
 * Placeholder shown when there is no data.
 *
 * Mirrors `<n-empty>`: optional icon, description and an extra slot for
 * actions.
 */
@Composable
fun NEmpty(
    modifier: Modifier = Modifier,
    description: String? = null,
    size: NSize? = null,
    showIcon: Boolean = true,
    icon: ImageVector? = null,
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val resolvedSize = nSize(size)
    val iconSize = when (resolvedSize) {
        NSize.Tiny -> 28.dp
        NSize.Small -> 34.dp
        NSize.Medium -> 40.dp
        NSize.Large -> 46.dp
    }
    val fontSize = theme.fontSizeFor(resolvedSize)
    val descriptionColor = theme.colors.textDisabled
    val extraStyle = theme.typography.body.copy(
        fontSize = fontSize,
        color = theme.colors.text2,
    )
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showIcon) {
            Box(Modifier.size(iconSize), contentAlignment = Alignment.Center) {
                NIcon(icon ?: NIcons.Empty, size = iconSize, color = theme.colors.icon)
            }
            Spacer(Modifier.height(8.dp))
        }
        NBaseText(
            text = description ?: LocalNLocale.current.emptyDescription,
            style = theme.typography.body.copy(fontSize = fontSize, color = descriptionColor),
        )
        if (content != null) {
            Spacer(Modifier.height(12.dp))
            CompositionLocalProvider(
                LocalNContentColor provides extraStyle.color,
                LocalNContentTextStyle provides extraStyle,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    content()
                }
            }
        }
    }
}
