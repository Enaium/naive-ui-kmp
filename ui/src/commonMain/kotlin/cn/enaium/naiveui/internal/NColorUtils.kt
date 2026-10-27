package cn.enaium.naiveui.internal

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import kotlin.math.abs

/** Color helpers mirroring naive-ui's `createHoverColor` / `createPressedColor`. */
fun nHoverColor(base: Color, dark: Boolean = false): Color =
    if (dark) lerp(base, Color.White, 0.1f) else lerp(base, Color.White, 0.12f)

fun nPressedColor(base: Color, dark: Boolean = false): Color =
    if (dark) lerp(base, Color.Black, 0.1f) else lerp(base, Color.Black, 0.12f)

fun nSupplColor(base: Color, dark: Boolean = false): Color =
    if (dark) lerp(base, Color.Black, 0.35f) else base

fun nWithAlpha(color: Color, alpha: Float): Color = color.copy(alpha = alpha)

fun nMix(a: Color, b: Color, t: Float): Color = lerp(a, b, t.coerceIn(0f, 1f))

/** Perceived luminance, used to pick readable foreground colors. */
fun nLuminance(color: Color): Float =
    0.2126f * color.red + 0.7152f * color.green + 0.0722f * color.blue

fun nIsLight(color: Color): Boolean = nLuminance(color) > 0.6f

fun nReadableTextColor(background: Color, dark: Boolean = false): Color =
    if (nIsLight(background)) Color(0xFF333639) else if (dark) Color.Black else Color.White

/** Approximate alpha of a color. */
fun nAlphaOf(color: Color): Float = color.alpha

/** Returns a hue-rotated variant; used by the color picker. */
fun nHsv(h: Float, s: Float, v: Float): Color {
    val c = v * s
    val x = c * (1 - abs((h / 60f) % 2 - 1))
    val m = v - c
    val (r, g, b) = when {
        h < 60f -> Triple(c, x, 0f)
        h < 120f -> Triple(x, c, 0f)
        h < 180f -> Triple(0f, c, x)
        h < 240f -> Triple(0f, x, c)
        h < 300f -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }
    return Color(r + m, g + m, b + m)
}
