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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.theme.LocalNTheme
import qrcode.QRCode
import qrcode.raw.ErrorCorrectionLevel

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

/**
 * Renders a QR code for [value] using `qrcode-kotlin` for the encoding and
 * Compose for the drawing.
 *
 * [errorCorrectionLevel] accepts `L`, `M`, `Q` and `H` (upper or lower case)
 * and defaults to `M`. The quiet zone is controlled by [padding].
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
                for (row in 0 until count) {
                    val cells = modules[row]
                    for (column in 0 until count) {
                        if (cells[column]) {
                            drawRect(
                                color = color,
                                topLeft = Offset(column * cell, row * cell),
                                // Slight overlap avoids hairline seams between modules.
                                size = Size(cell + 0.5f, cell + 0.5f),
                            )
                        }
                    }
                }
            }
        }
    }
}
