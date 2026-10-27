package cn.enaium.naiveui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus

/** Line style of a timeline item. */
enum class NTimelineLineType { Default, Dashed }

/**
 * A single timeline entry.
 *
 * [icon] replaces the default circle for this item, while [color] overrides
 * the accent color derived from [type].
 */
data class NTimelineItem(
    val title: String? = null,
    val content: String? = null,
    val time: String? = null,
    val type: NStatus = NStatus.Default,
    val color: Color? = null,
    val icon: (@Composable () -> Unit)? = null,
    val lineType: NTimelineLineType = NTimelineLineType.Default,
)

/**
 * Displays a list of events, vertically by default or in a row when
 * [horizontal] is enabled. Mirrors `<n-timeline>` / `<n-timeline-item>`.
 *
 * When [icon] is supplied it is used for every item without its own icon.
 */
@Composable
fun NTimeline(
    items: List<NTimelineItem>,
    modifier: Modifier = Modifier,
    horizontal: Boolean = false,
    size: NSize = NSize.Medium,
    iconSize: Dp = 14.dp,
    icon: (@Composable (NTimelineItem) -> Unit)? = null,
) {
    if (horizontal) {
        NScrollbar(modifier, horizontal = true) {
            items.forEachIndexed { index, item ->
                NTimelineHorizontalItem(
                    item = item,
                    isLast = index == items.lastIndex,
                    size = size,
                    iconSize = iconSize,
                    globalIcon = icon,
                )
            }
        }
    } else {
        Column(modifier.fillMaxWidth()) {
            items.forEachIndexed { index, item ->
                NTimelineVerticalItem(
                    item = item,
                    isLast = index == items.lastIndex,
                    size = size,
                    iconSize = iconSize,
                    globalIcon = icon,
                )
            }
        }
    }
}

@Composable
private fun NTimelineVerticalItem(
    item: NTimelineItem,
    isLast: Boolean,
    size: NSize,
    iconSize: Dp,
    globalIcon: (@Composable (NTimelineItem) -> Unit)?,
) {
    val theme = LocalNTheme.current
    val titleSize = if (size == NSize.Large) 16.sp else 14.sp
    val titleStyle = theme.typography.style(titleSize, theme.typography.fontWeightStrong, theme.colors.text1)
    val contentStyle = theme.typography.style(theme.typography.fontSize.medium, color = theme.colors.text2)
    val timeStyle = theme.typography.style(12.sp, color = theme.colors.text3)
    val accent = item.color
        ?: if (item.type == NStatus.Default) theme.colors.text3 else theme.colors.statusColor(item.type)
    val dashed = item.lineType == NTimelineLineType.Dashed
    val density = LocalDensity.current
    val circleTop = with(density) {
        ((titleSize.toDp() * 1.25f - iconSize) / 2f).coerceAtLeast(0.dp)
    }
    val iconSlot: (@Composable () -> Unit)? = when {
        item.icon != null -> item.icon
        globalIcon != null -> { { globalIcon(item) } }
        else -> null
    }

    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Box(Modifier.width(iconSize + 12.dp).fillMaxHeight()) {
            if (!isLast) {
                Canvas(Modifier.fillMaxSize()) {
                    val x = iconSize.toPx() / 2f
                    val startY = circleTop.toPx() + iconSize.toPx()
                    drawLine(
                        color = theme.colors.rail,
                        start = Offset(x, startY),
                        end = Offset(x, this.size.height),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = if (dashed) {
                            PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()))
                        } else {
                            null
                        },
                    )
                }
            }
            Box(
                Modifier
                    .offset(y = circleTop)
                    .size(iconSize),
                contentAlignment = Alignment.Center,
            ) {
                if (iconSlot != null) {
                    NIcon(size = iconSize, color = accent) {
                        iconSlot()
                    }
                } else {
                    Box(
                        Modifier
                            .matchParentSize()
                            .clip(CircleShape)
                            .nBorder(accent, 2.dp, iconSize / 2),
                    )
                }
            }
        }
        Column(
            Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 20.dp),
        ) {
            if (item.title != null) {
                BasicText(
                    text = item.title,
                    style = titleStyle,
                    modifier = Modifier.padding(bottom = 6.dp),
                )
            }
            if (item.content != null) {
                BasicText(text = item.content, style = contentStyle)
            }
            if (item.time != null) {
                BasicText(
                    text = item.time,
                    style = timeStyle,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
    }
}

@Composable
private fun NTimelineHorizontalItem(
    item: NTimelineItem,
    isLast: Boolean,
    size: NSize,
    iconSize: Dp,
    globalIcon: (@Composable (NTimelineItem) -> Unit)?,
) {
    val theme = LocalNTheme.current
    val titleSize = if (size == NSize.Large) 16.sp else 14.sp
    val titleStyle = theme.typography.style(titleSize, theme.typography.fontWeightStrong, theme.colors.text1)
    val contentStyle = theme.typography.style(theme.typography.fontSize.medium, color = theme.colors.text2)
    val timeStyle = theme.typography.style(12.sp, color = theme.colors.text3)
    val accent = item.color
        ?: if (item.type == NStatus.Default) theme.colors.text3 else theme.colors.statusColor(item.type)
    val dashed = item.lineType == NTimelineLineType.Dashed
    val iconSlot: (@Composable () -> Unit)? = when {
        item.icon != null -> item.icon
        globalIcon != null -> { { globalIcon(item) } }
        else -> null
    }

    Column(Modifier.widthIn(min = 180.dp).padding(end = 40.dp)) {
        Box(Modifier.fillMaxWidth().height(iconSize + 12.dp)) {
            if (!isLast) {
                Canvas(Modifier.fillMaxSize()) {
                    val y = this.size.height / 2f
                    drawLine(
                        color = theme.colors.rail,
                        start = Offset(iconSize.toPx() / 2f, y),
                        end = Offset(this.size.width, y),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = if (dashed) {
                            PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()))
                        } else {
                            null
                        },
                    )
                }
            }
            Box(
                Modifier.align(Alignment.CenterStart).size(iconSize),
                contentAlignment = Alignment.Center,
            ) {
                if (iconSlot != null) {
                    NIcon(size = iconSize, color = accent) {
                        iconSlot()
                    }
                } else {
                    Box(
                        Modifier
                            .matchParentSize()
                            .clip(CircleShape)
                            .nBorder(accent, 2.dp, iconSize / 2),
                    )
                }
            }
        }
        Column(Modifier.padding(top = 2.dp)) {
            if (item.title != null) {
                BasicText(
                    text = item.title,
                    style = titleStyle,
                    modifier = Modifier.padding(bottom = 6.dp),
                )
            }
            if (item.content != null) {
                BasicText(text = item.content, style = contentStyle)
            }
            if (item.time != null) {
                BasicText(
                    text = item.time,
                    style = timeStyle,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
    }
}
