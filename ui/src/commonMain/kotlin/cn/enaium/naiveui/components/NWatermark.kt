package cn.enaium.naiveui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.theme.LocalNTheme
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter

/**
 * Paints a repeated, optionally rotated watermark over [content].
 *
 * Text supports `\n` separated lines. The watermark never intercepts pointer
 * input.
 */
@Composable
fun NWatermark(
    text: String? = null,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 14.sp,
    fontColor: Color = Color(128, 128, 128).copy(alpha = 0.3f),
    fontWeight: FontWeight = FontWeight.Normal,
    rotate: Float = 0f,
    globalRotate: Float = 0f,
    width: Dp = 192.dp,
    height: Dp = 128.dp,
    horizontalGap: Dp = 0.dp,
    verticalGap: Dp = 0.dp,
    offset: DpOffset = DpOffset(12.dp, 28.dp),
    cross: Boolean = false,
    image: String? = null,
    imageWidth: Dp? = null,
    imageHeight: Dp? = null,
    imageOpacity: Float = 1f,
    textAlign: TextAlign = TextAlign.Left,
    content: @Composable () -> Unit,
) {
    val theme = LocalNTheme.current
    val measurer = rememberTextMeasurer()
    val style = remember(fontSize, fontColor, fontWeight, theme.typography.fontFamily) {
        TextStyle(
            color = fontColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
            fontFamily = theme.typography.fontFamily,
            lineHeight = fontSize * 1.2f,
            textAlign = textAlign,
        )
    }
    val imagePainter = image?.let { rememberAsyncImagePainter(it) }
    val imageReady = if (imagePainter != null) {
        val state by imagePainter.state.collectAsState()
        state is AsyncImagePainter.State.Success
    } else {
        false
    }
    val layouts: List<TextLayoutResult> = remember(measurer, text, style) {
        if (text.isNullOrEmpty()) {
            emptyList()
        } else {
            text.split('\n').map { line -> measurer.measure(AnnotatedString(line), style) }
        }
    }
    val density = LocalDensity.current
    val cellWidth = with(density) { (width + horizontalGap).toPx() }
    val cellHeight = with(density) { (height + verticalGap).toPx() }
    val offsetX = with(density) { offset.x.toPx() }
    val offsetY = with(density) { offset.y.toPx() }

    Box(modifier) {
        content()
        Canvas(Modifier.matchParentSize()) {
            if (layouts.isEmpty() || cellWidth <= 0f || cellHeight <= 0f) return@Canvas
            val columns = (size.width / cellWidth).toInt() + 3
            val rows = (size.height / cellHeight).toInt() + 3
            val lineHeight = layouts.firstOrNull()?.size?.height?.toFloat() ?: 0f
            val painter = imagePainter
            val painterReady = imageReady
            val intrinsic = painter?.intrinsicSize
            val imgWidth = imageWidth?.let { with(density) { it.toPx() } }
                ?: intrinsic?.width?.takeIf { it.isFinite() && it > 0f }
                ?: cellWidth
            val imgHeight = imageHeight?.let { with(density) { it.toPx() } }
                ?: intrinsic?.height?.takeIf { it.isFinite() && it > 0f }
                ?: cellHeight
            withTransform({ if (globalRotate != 0f) rotate(globalRotate, center) }) {
                for (row in 0 until rows) {
                    val rowOffset = if (cross && row % 2 == 1) cellWidth / 2f else 0f
                    for (column in 0 until columns) {
                        val x = column * cellWidth - cellWidth + rowOffset
                        val y = row * cellHeight - cellHeight
                        rotate(degrees = rotate, pivot = Offset(x + cellWidth / 2f, y + cellHeight / 2f)) {
                            if (painterReady && painter != null) {
                                with(painter) {
                                    draw(
                                        size = Size(imgWidth, imgHeight),
                                        alpha = imageOpacity,
                                    )
                                }
                            }
                            layouts.forEachIndexed { index, layout ->
                                drawText(
                                    textLayoutResult = layout,
                                    topLeft = Offset(
                                        x + offsetX,
                                        y + offsetY + lineHeight * index,
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
