package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.internal.NCloseButton
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nShadow
import cn.enaium.naiveui.internal.nTopBorder
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize

/**
 * Card container with header, footer and closable support.
 */
@Composable
fun NCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    size: NSize = NSize.Medium,
    bordered: Boolean = true,
    hoverable: Boolean = false,
    embedded: Boolean = false,
    segmented: Boolean = false,
    closable: Boolean = false,
    onClose: () -> Unit = {},
    headerExtra: (@Composable RowScope.() -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val shape = RoundedCornerShape(theme.borderRadius)
    val padding = when (size) {
        NSize.Tiny, NSize.Small -> 14.dp
        NSize.Medium -> 18.dp
        NSize.Large -> 22.dp
    }
    val titleSize = when (size) {
        NSize.Tiny, NSize.Small -> theme.typography.fontSize.medium
        NSize.Medium -> theme.typography.fontSize.large
        NSize.Large -> theme.typography.fontSize.huge
    }
    val background = if (embedded) theme.colors.action else theme.colors.card

    Column(
        modifier
            .then(if (hoverable) Modifier.nShadow(theme, if (hovered) 2 else 1, shape) else Modifier)
            .clip(shape)
            .background(background)
            .then(if (bordered) Modifier.nBorder(theme.colors.border, 1.dp, theme.borderRadius) else Modifier)
            .then(if (hoverable) Modifier.hoverable(interaction) else Modifier),
    ) {
        if (title != null || headerExtra != null || closable) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = padding, vertical = padding * 0.75f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (title != null) {
                    NText(
                        title,
                        color = theme.colors.text1,
                        size = titleSize,
                        strong = true,
                        modifier = Modifier.weight(1f),
                    )
                } else {
                    Spacer(Modifier.weight(1f))
                }
                headerExtra?.invoke(this)
                if (headerExtra != null || closable) Spacer(Modifier.width(8.dp))
                if (closable) {
                    NCloseButton(onClick = onClose)
                }
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .padding(padding)
                .then(
                    if (segmented) {
                        Modifier
                    } else {
                        Modifier
                    },
                ),
        ) {
            Column { content() }
        }
        if (footer != null) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .nTopBorder(theme.colors.divider)
                    .padding(padding),
            ) {
                footer()
            }
        }
    }
}

/** Card content with no padding, useful for tables and lists. */
@Composable
fun NCardContent(
    modifier: Modifier = Modifier,
    color: Color? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val theme = LocalNTheme.current
    Column(modifier.fillMaxWidth().background(color ?: theme.colors.card)) { content() }
}
