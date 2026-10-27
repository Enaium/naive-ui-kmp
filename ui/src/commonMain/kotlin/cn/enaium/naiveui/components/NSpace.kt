package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.theme.LocalNTheme

/** Cross-axis alignment used by `NSpace` and `NFlex`. */
enum class NAlign { Start, Center, End, Baseline, Stretch }

/** Main-axis distribution used by `NSpace` and `NFlex`. */
enum class NJustify { Start, Center, End, SpaceBetween, SpaceAround, SpaceEvenly }

private fun NJustify.arrangement(): Arrangement.Horizontal = when (this) {
    NJustify.Start -> Arrangement.Start
    NJustify.Center -> Arrangement.Center
    NJustify.End -> Arrangement.End
    NJustify.SpaceBetween -> Arrangement.SpaceBetween
    NJustify.SpaceAround -> Arrangement.SpaceAround
    NJustify.SpaceEvenly -> Arrangement.SpaceEvenly
}

private fun NAlign.alignment(): Alignment.Vertical = when (this) {
    NAlign.Start, NAlign.Baseline -> Alignment.Top
    NAlign.Center -> Alignment.CenterVertically
    NAlign.End -> Alignment.Bottom
    NAlign.Stretch -> Alignment.CenterVertically
}

private fun NAlign.horizontalAlignment(): Alignment.Horizontal = when (this) {
    NAlign.Start -> Alignment.Start
    NAlign.Center, NAlign.Baseline -> Alignment.CenterHorizontally
    NAlign.End -> Alignment.End
    NAlign.Stretch -> Alignment.CenterHorizontally
}

/**
 * Spaced group. With [wrap] enabled items flow onto multiple lines.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NSpace(
    modifier: Modifier = Modifier,
    vertical: Boolean = false,
    size: Dp = 8.dp,
    verticalSize: Dp = size,
    align: NAlign = NAlign.Start,
    justify: NJustify = NJustify.Start,
    wrap: Boolean = false,
    content: @Composable () -> Unit,
) {
    if (wrap) {
        FlowRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(size, Alignment.Start),
            verticalArrangement = Arrangement.spacedBy(verticalSize),
            itemVerticalAlignment = when (align) {
                NAlign.Center, NAlign.Baseline -> Alignment.CenterVertically
                NAlign.End -> Alignment.Bottom
                else -> Alignment.Top
            },
        ) {
            content()
        }
        return
    }
    if (vertical) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(size),
            horizontalAlignment = align.horizontalAlignment(),
        ) {
            content()
        }
    } else {
        Row(
            modifier = modifier,
            horizontalArrangement = justify.arrangement().let { arrangement ->
                if (arrangement == Arrangement.Start) Arrangement.spacedBy(size, Alignment.Start) else arrangement
            },
            verticalAlignment = align.alignment(),
        ) {
            content()
        }
    }
}

/**
 * Flex container. This is the lower-level counterpart of `NSpace` and maps
 * directly onto Compose's `Row`, `Column` and `FlowRow`.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NFlex(
    modifier: Modifier = Modifier,
    vertical: Boolean = false,
    size: Dp = 0.dp,
    wrap: Boolean = false,
    align: NAlign = NAlign.Start,
    justify: NJustify = NJustify.Start,
    content: @Composable () -> Unit,
) {
    if (wrap) {
        FlowRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(size, Alignment.Start),
            verticalArrangement = Arrangement.spacedBy(size),
            itemVerticalAlignment = align.alignment(),
        ) {
            content()
        }
        return
    }
    if (vertical) {
        Column(
            modifier = modifier,
            verticalArrangement = if (justify == NJustify.Start) Arrangement.spacedBy(size) else Arrangement.spacedBy(size),
            horizontalAlignment = align.horizontalAlignment(),
        ) {
            content()
        }
    } else {
        Row(
            modifier = modifier,
            horizontalArrangement = if (justify == NJustify.Start) Arrangement.spacedBy(size) else justify.arrangement(),
            verticalAlignment = align.alignment(),
        ) {
            content()
        }
    }
}

enum class NDividerTitlePlacement { Left, Center, Right }

/**
 * Divider with optional title and dashed style.
 */
@Composable
fun NDivider(
    modifier: Modifier = Modifier,
    vertical: Boolean = false,
    dashed: Boolean = false,
    title: String? = null,
    titlePlacement: NDividerTitlePlacement = NDividerTitlePlacement.Center,
) {
    val theme = LocalNTheme.current
    val color = theme.colors.divider
    if (vertical) {
        val height = with(LocalDensity.current) { 14.sp.toDp() }
        Box(
            modifier
                .width(1.dp)
                .height(height)
                .background(color),
        )
        return
    }
    if (title == null) {
        DividerLine(modifier.fillMaxWidth(), color, dashed)
        return
    }
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val titleWeight = when (titlePlacement) {
            NDividerTitlePlacement.Left -> 0.08f
            NDividerTitlePlacement.Center -> 1f
            NDividerTitlePlacement.Right -> 1f
        }
        if (titlePlacement != NDividerTitlePlacement.Left) {
            DividerLine(Modifier.weight(titleWeight), color, dashed)
        }
        androidx.compose.foundation.text.BasicText(
            text = title,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = theme.typography.body.copy(color = theme.colors.text2),
        )
        if (titlePlacement != NDividerTitlePlacement.Right) {
            DividerLine(Modifier.weight(if (titlePlacement == NDividerTitlePlacement.Center) 1f else 12f), color, dashed)
        }
    }
}

@Composable
private fun DividerLine(modifier: Modifier, color: Color, dashed: Boolean) {
    if (dashed) {
        Box(
            modifier.height(1.dp).nDashedLine(color),
        )
    } else {
        Box(modifier.height(1.dp).background(color))
    }
}

private fun Modifier.nDashedLine(color: Color): Modifier = drawBehind {
    drawLine(
        color = color,
        start = Offset(0f, size.height / 2f),
        end = Offset(size.width, size.height / 2f),
        strokeWidth = size.height,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx())),
    )
}
