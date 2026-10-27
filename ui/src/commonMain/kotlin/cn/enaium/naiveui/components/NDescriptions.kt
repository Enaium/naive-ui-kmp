package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.internal.LocalNContentColor
import cn.enaium.naiveui.internal.LocalNContentTextStyle
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nBottomBorder
import cn.enaium.naiveui.internal.nRightBorder
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.nSize

/** Label position of [NDescriptions]. */
enum class NDescriptionsLabelPlacement { Top, Left }

/** Horizontal alignment of description labels. */
enum class NDescriptionsLabelAlign { Left, Center, Right }

@Immutable
internal data class NDescriptionsItemData(
    val label: String?,
    val span: Int,
    val labelContent: (@Composable () -> Unit)?,
    val content: @Composable () -> Unit,
)

/**
 * Builder scope used by [NDescriptions]. Declare items with
 * [NDescriptionsItem][NDescriptionsItem] or [item].
 */
class NDescriptionsScope internal constructor() {
    internal val items = mutableListOf<NDescriptionsItemData>()

    /** Adds an item with a text [label], a [span] and its [content]. */
    fun item(
        label: String? = null,
        span: Int = 1,
        labelContent: (@Composable () -> Unit)? = null,
        content: @Composable () -> Unit = {},
    ) {
        items += NDescriptionsItemData(label, span, labelContent, content)
    }
}

/**
 * Declares an item inside [NDescriptions].
 *
 * Mirrors `<n-descriptions-item>`: the label can be a string or a slot, and
 * [span] controls how many columns the item occupies.
 */
fun NDescriptionsScope.NDescriptionsItem(
    label: String? = null,
    span: Int = 1,
    labelContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit = {},
) {
    item(label = label, span = span, labelContent = labelContent, content = content)
}

private data class NDescriptionsCell(val item: NDescriptionsItemData, val span: Int)

/**
 * Descriptions table equivalent to `<n-descriptions>`.
 *
 * Supports a fixed [column] count, per-item [NDescriptionsItem] spans, top or
 * left label placement and the bordered table variant.
 */
@Composable
fun NDescriptions(
    modifier: Modifier = Modifier,
    title: String? = null,
    column: Int = 3,
    labelPlacement: NDescriptionsLabelPlacement = NDescriptionsLabelPlacement.Top,
    labelAlign: NDescriptionsLabelAlign = NDescriptionsLabelAlign.Left,
    size: NSize? = null,
    bordered: Boolean = false,
    separator: String = ":",
    header: (@Composable () -> Unit)? = null,
    content: @Composable NDescriptionsScope.() -> Unit,
) {
    val theme = LocalNTheme.current
    val resolvedSize = nSize(size)
    val columns = column.coerceAtLeast(1)
    val scope = remember { NDescriptionsScope() }
    scope.items.clear()
    scope.content()
    val rows = buildRows(scope.items, columns)

    val fontSize = when (resolvedSize) {
        NSize.Tiny, NSize.Small -> theme.typography.fontSize.small
        NSize.Medium -> theme.typography.fontSize.medium
        NSize.Large -> theme.typography.fontSize.large
    }
    val padding: Dp = when (resolvedSize) {
        NSize.Tiny, NSize.Small -> 8.dp
        NSize.Medium -> 12.dp
        NSize.Large -> 16.dp
    }
    val rowGap: Dp = when (resolvedSize) {
        NSize.Tiny, NSize.Small -> 8.dp
        NSize.Medium -> 12.dp
        NSize.Large -> 14.dp
    }
    val textAlign = when (labelAlign) {
        NDescriptionsLabelAlign.Left -> TextAlign.Left
        NDescriptionsLabelAlign.Center -> TextAlign.Center
        NDescriptionsLabelAlign.Right -> TextAlign.Right
    }
    val borderColor = theme.colors.divider
    val tdColor = theme.colors.card
    val thColor = theme.colors.tableHeader
    val baseStyle = theme.typography.style(fontSize)

    Column(modifier.fillMaxWidth()) {
        if (title != null || header != null) {
            Box(Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                if (header != null) {
                    header()
                } else {
                    NBaseText(
                        text = title.orEmpty(),
                        style = theme.typography.style(18.sp, theme.typography.fontWeightStrong, theme.colors.text1),
                    )
                }
            }
        }
        if (bordered) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(theme.borderRadius))
                    .background(tdColor)
                    .nBorder(borderColor, 1.dp, theme.borderRadius),
            ) {
                rows.forEach { row ->
                    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                        row.forEach { cell ->
                            Box(
                                Modifier
                                    .weight(cell.span.toFloat())
                                    .fillMaxHeight()
                                    .nRightBorder(borderColor)
                                    .nBottomBorder(borderColor),
                            ) {
                                if (labelPlacement == NDescriptionsLabelPlacement.Left) {
                                    BorderedLeftCell(
                                        cell = cell,
                                        labelStyle = baseStyle,
                                        textAlign = textAlign,
                                        contentColor = theme.colors.text2,
                                        thColor = thColor,
                                        borderColor = borderColor,
                                        padding = padding,
                                    )
                                } else {
                                    BorderedTopCell(
                                        cell = cell,
                                        labelStyle = baseStyle,
                                        textAlign = textAlign,
                                        contentColor = theme.colors.text2,
                                        thColor = thColor,
                                        borderColor = borderColor,
                                        padding = padding,
                                    )
                                }
                            }
                        }
                        val filler = columns - row.sumOf { it.span }
                        if (filler > 0) {
                            Box(
                                Modifier
                                    .weight(filler.toFloat())
                                    .fillMaxHeight()
                                    .nRightBorder(borderColor)
                                    .nBottomBorder(borderColor),
                            )
                        }
                    }
                }
            }
        } else {
            Column(Modifier.fillMaxWidth()) {
                rows.forEachIndexed { index, row ->
                    val isLast = index == rows.lastIndex
                    Row(Modifier.fillMaxWidth()) {
                        row.forEach { cell ->
                            val cellModifier = Modifier
                                .weight(cell.span.toFloat())
                                .padding(end = 16.dp, bottom = if (isLast) 0.dp else rowGap)
                            if (labelPlacement == NDescriptionsLabelPlacement.Left) {
                                Row(cellModifier) {
                                    LabelBox(
                                        cell = cell,
                                        style = labelStyle(baseStyle, textAlign),
                                        color = theme.colors.text1,
                                        modifier = Modifier.padding(end = 2.dp),
                                    )
                                    if (separator.isNotEmpty()) {
                                        NBaseText(
                                            text = separator,
                                            style = baseStyle.copy(color = theme.colors.text3),
                                            modifier = Modifier.padding(start = 2.dp, end = 8.dp),
                                        )
                                    } else {
                                        Box(Modifier.padding(end = 8.dp))
                                    }
                                    ContentBox(
                                        cell = cell,
                                        style = baseStyle,
                                        color = theme.colors.text2,
                                    )
                                }
                            } else {
                                Column(cellModifier) {
                                    LabelBox(
                                        cell = cell,
                                        style = labelStyle(baseStyle, textAlign),
                                        color = theme.colors.text1,
                                        modifier = Modifier.padding(bottom = 4.dp),
                                    )
                                    ContentBox(
                                        cell = cell,
                                        style = baseStyle,
                                        color = theme.colors.text2,
                                    )
                                }
                            }
                        }
                        val filler = columns - row.sumOf { it.span }
                        if (filler > 0) {
                            Box(Modifier.weight(filler.toFloat()))
                        }
                    }
                }
            }
        }
        if (rows.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(vertical = padding))
        }
    }
}

private fun labelStyle(base: TextStyle, align: TextAlign) = base.copy(
    fontWeight = FontWeight.Medium,
    textAlign = align,
)

@Composable
private fun LabelBox(
    cell: NDescriptionsCell,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        CompositionLocalProvider(
            LocalNContentColor provides color,
            LocalNContentTextStyle provides style,
        ) {
            if (cell.item.labelContent != null) {
                cell.item.labelContent()
            } else if (cell.item.label != null) {
                NBaseText(cell.item.label, style.copy(color = color))
            }
        }
    }
}

@Composable
private fun ContentBox(
    cell: NDescriptionsCell,
    style: TextStyle,
    color: Color,
) {
    CompositionLocalProvider(
        LocalNContentColor provides color,
        LocalNContentTextStyle provides style.copy(color = color),
    ) {
        cell.item.content()
    }
}

@Composable
private fun BorderedTopCell(
    cell: NDescriptionsCell,
    labelStyle: TextStyle,
    textAlign: TextAlign,
    contentColor: Color,
    thColor: Color,
    borderColor: Color,
    padding: Dp,
) {
    val resolvedLabelStyle = labelStyle.copy(
        fontWeight = FontWeight.Medium,
        textAlign = textAlign,
    )
    Column(Modifier.fillMaxHeight()) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(thColor)
                .nBottomBorder(borderColor)
                .padding(horizontal = padding, vertical = padding * 0.75f),
        ) {
            LabelBox(
                cell = cell,
                style = resolvedLabelStyle,
                color = LocalNTheme.current.colors.text1,
            )
        }
        Box(Modifier.fillMaxWidth().padding(padding)) {
            ContentBox(cell = cell, style = labelStyle, color = contentColor)
        }
    }
}

@Composable
private fun BorderedLeftCell(
    cell: NDescriptionsCell,
    labelStyle: TextStyle,
    textAlign: TextAlign,
    contentColor: Color,
    thColor: Color,
    borderColor: Color,
    padding: Dp,
) {
    val resolvedLabelStyle = labelStyle.copy(
        fontWeight = FontWeight.Medium,
        textAlign = textAlign,
    )
    Row(Modifier.fillMaxHeight()) {
        Box(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(thColor)
                .nRightBorder(borderColor)
                .padding(padding),
        ) {
            LabelBox(
                cell = cell,
                style = resolvedLabelStyle,
                color = LocalNTheme.current.colors.text1,
            )
        }
        Box(
            Modifier
                .weight((cell.span * 2 - 1).toFloat())
                .fillMaxHeight()
                .padding(padding),
        ) {
            ContentBox(cell = cell, style = labelStyle, color = contentColor)
        }
    }
}

private fun buildRows(
    items: List<NDescriptionsItemData>,
    columns: Int,
): List<List<NDescriptionsCell>> {
    val rows = mutableListOf<MutableList<NDescriptionsCell>>()
    var used = 0
    items.forEach { item ->
        val span = item.span.coerceIn(1, columns)
        if (used == 0 || used + span > columns) {
            rows += mutableListOf<NDescriptionsCell>()
            used = 0
        }
        rows.last() += NDescriptionsCell(item, span)
        used += span
        if (used >= columns) used = 0
    }
    return rows
}
