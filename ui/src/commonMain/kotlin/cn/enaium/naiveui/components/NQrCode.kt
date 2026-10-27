package cn.enaium.naiveui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.theme.LocalNTheme
import coil3.compose.SubcomposeAsyncImage
import qrcode.QRCode
import qrcode.raw.ErrorCorrectionLevel

/** Render mode for [NQrCode]. `Canvas` draws each module, `Svg` merges runs. */
enum class NQrCodeType { Canvas, Svg }

private fun nQrErrorCorrectionLevel(level: String): ErrorCorrectionLevel =
    when (level.trim().uppercase().firstOrNull()) {
        'L' -> ErrorCorrectionLevel.LOW
        'Q' -> ErrorCorrectionLevel.HIGH
        'H' -> ErrorCorrectionLevel.VERY_HIGH
        else -> ErrorCorrectionLevel.MEDIUM
    }

private fun nQrMatrix(value: String, level: String): List<BooleanArray> {
    val code = QRCode
        .ofSquares()
        .withErrorCorrectionLevel(nQrErrorCorrectionLevel(level))
        .build(value)
    val raw = code.rawData
    val count = raw.size
    return List(count) { row ->
        BooleanArray(count) { column -> raw[row].getOrNull(column)?.dark == true }
    }
}

private fun DrawScope.nDrawQrModules(
    modules: List<BooleanArray>,
    color: Color,
    origin: Offset,
    cell: Float,
    merged: Boolean,
) {
    if (!merged) {
        for (row in modules.indices) {
            val cells = modules[row]
            for (column in cells.indices) {
                if (cells[column]) {
                    drawRect(
                        color = color,
                        topLeft = Offset(origin.x + column * cell, origin.y + row * cell),
                        // Slight overlap avoids hairline seams between modules.
                        size = Size(cell + 0.5f, cell + 0.5f),
                    )
                }
            }
        }
        return
    }
    val path = Path()
    for (row in modules.indices) {
        val cells = modules[row]
        var runStart = -1
        for (column in cells.indices) {
            val dark = cells[column]
            if (dark && runStart < 0) runStart = column
            if ((!dark || column == cells.lastIndex) && runStart >= 0) {
                val runEnd = if (dark) column + 1 else column
                path.addRect(
                    Rect(
                        left = origin.x + runStart * cell,
                        top = origin.y + row * cell,
                        right = origin.x + runEnd * cell,
                        bottom = origin.y + (row + 1) * cell,
                    ),
                )
                runStart = -1
            }
        }
    }
    drawPath(path, color)
}

/**
 * Renders the QR modules of [value] into an [ImageBitmap].
 *
 * This is the portable half of downloading a QR code: Compose Multiplatform
 * has no common file picker, so hosts can save or share the returned bitmap
 * with their own platform API.
 */
fun NQrCodeBitmap(
    value: String,
    pixels: Int = 400,
    color: Color = Color.Black,
    backgroundColor: Color = Color.White,
    errorCorrectionLevel: String = "M",
    padding: Int = 24,
): ImageBitmap? {
    val modules = runCatching { nQrMatrix(value.ifEmpty { "-" }, errorCorrectionLevel) }.getOrNull()
        ?: return null
    if (modules.isEmpty()) return null
    val size = pixels.coerceAtLeast(42)
    val bitmap = ImageBitmap(size, size)
    val canvas = Canvas(bitmap)
    CanvasDrawScope().draw(
        density = Density(1f),
        layoutDirection = LayoutDirection.Ltr,
        canvas = canvas,
        size = Size(size.toFloat(), size.toFloat()),
    ) {
        drawRect(backgroundColor)
        val area = (size - padding * 2).toFloat()
        val cell = area / modules.size
        nDrawQrModules(
            modules = modules,
            color = color,
            origin = Offset(padding.toFloat(), padding.toFloat()),
            cell = cell,
            merged = false,
        )
    }
    return bitmap
}

/**
 * Renders a QR code for [value] using `qrcode-kotlin` for the encoding and
 * Compose for the drawing.
 *
 * [type] switches between per-module rectangles and merged path runs.
 * [iconSrc] is drawn in a rounded badge in the centre; use error correction
 * level `H` when covering modules. [errorCorrectionLevel] accepts `L`, `M`,
 * `Q` and `H`, and [padding] controls the quiet zone.
 */
@Composable
fun NQrCode(
    value: String,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    color: Color = Color.Black,
    backgroundColor: Color = Color.White,
    errorCorrectionLevel: String = "M",
    padding: Dp = 12.dp,
    type: NQrCodeType = NQrCodeType.Canvas,
    iconSrc: String? = null,
    iconSize: Dp = 40.dp,
    iconBackgroundColor: Color = Color.White,
    iconBorderRadius: Dp = 4.dp,
) {
    val theme = LocalNTheme.current
    val matrix = remember(value, errorCorrectionLevel) {
        runCatching { nQrMatrix(value.ifEmpty { "-" }, errorCorrectionLevel) }.getOrNull()
    }
    Box(
        modifier
            .size(size)
            .clip(RoundedCornerShape(theme.borderRadius))
            .background(backgroundColor)
            .padding(padding),
        contentAlignment = Alignment.Center,
    ) {
        val modules = matrix
        if (modules == null || modules.isEmpty()) {
            NIcon(NIcons.Error, size = 20.dp, color = theme.colors.error)
        } else {
            Canvas(Modifier.fillMaxSize()) {
                val count = modules.size
                val cell = this.size.minDimension / count
                nDrawQrModules(
                    modules = modules,
                    color = color,
                    origin = Offset.Zero,
                    cell = cell,
                    merged = type == NQrCodeType.Svg,
                )
            }
            if (iconSrc != null) {
                Box(
                    Modifier
                        .size(iconSize)
                        .clip(RoundedCornerShape(iconBorderRadius))
                        .background(iconBackgroundColor)
                        .padding(iconSize * 0.06f),
                    contentAlignment = Alignment.Center,
                ) {
                    SubcomposeAsyncImage(
                        model = iconSrc,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        loading = {},
                        error = {},
                    )
                }
            }
        }
    }
}
