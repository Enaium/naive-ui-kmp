package cn.enaium.naiveui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NPopoverSurface
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.internal.nHsv
import cn.enaium.naiveui.internal.nIsLight
import cn.enaium.naiveui.internal.nTopBorder
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.roundToInt

/** Value formats offered by [NColorPicker]. */
enum class NColorPickerMode(val label: String) {
    Rgb("RGB"),
    Hex("HEX"),
    Hsl("HSL"),
    Hsv("HSV"),
}

/** Optional buttons rendered at the bottom of a color picker panel. */
enum class NColorPickerAction { Confirm, Clear }

/**
 * Color picker trigger with a popup panel containing a saturation/value
 * palette, hue and alpha sliders, mode tabs, an editable value field and
 * optional swatches.
 *
 * [show] makes the panel controlled; leave it `null` for uncontrolled
 * behaviour and observe changes through [onShowChange].
 */
@Composable
fun NColorPicker(
    value: Color,
    onValueChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
    show: Boolean? = null,
    onShowChange: (Boolean) -> Unit = {},
    modes: List<NColorPickerMode> = listOf(NColorPickerMode.Rgb, NColorPickerMode.Hex, NColorPickerMode.Hsl),
    showAlpha: Boolean = true,
    swatches: List<Color> = emptyList(),
    disabled: Boolean = false,
    size: NSize = NSize.Medium,
    placement: NPlacement = NPlacement.BottomStart,
    showPreview: Boolean = false,
    actions: List<NColorPickerAction>? = null,
    onConfirm: ((Color) -> Unit)? = null,
    onClear: (() -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    var internalShow by remember { mutableStateOf(false) }
    val mergedShow = show ?: internalShow

    fun updateShow(next: Boolean) {
        if (show == null) internalShow = next
        onShowChange(next)
    }

    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val height = theme.heightFor(size)
    val fontSize = theme.fontSizeFor(size)
    val shape = RoundedCornerShape(theme.borderRadius)
    val labelMode = modes.firstOrNull() ?: NColorPickerMode.Hex

    Box(modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(height)
                .alpha(if (disabled) theme.opacityDisabled else 1f)
                .clip(shape)
                .nBorder(
                    color = if (!disabled && hovered) theme.colors.primaryHover else theme.colors.border,
                    width = 1.dp,
                    radius = theme.borderRadius,
                )
                .hoverable(interaction, enabled = !disabled)
                .nClickable(interaction, enabled = !disabled) { updateShow(!mergedShow) },
        ) {
            Box(Modifier.matchParentSize().nCheckerboard())
            Box(Modifier.matchParentSize().background(value))
            Row(
                Modifier.matchParentSize().padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NText(
                    text = formatColor(value, labelMode, showAlpha),
                    modifier = Modifier.fillMaxWidth(),
                    size = fontSize,
                    color = readableOn(value),
                    maxLines = 1,
                )
            }
        }

        NPopoverSurface(
            visible = mergedShow,
            onDismiss = { updateShow(false) },
            placement = placement,
            focusable = true,
            padding = PaddingValues(0.dp),
        ) {
            NColorPickerPanel(
                value = value,
                onValueChange = onValueChange,
                modes = modes,
                showAlpha = showAlpha,
                swatches = swatches,
                showPreview = showPreview,
                actions = actions,
                disabled = disabled,
                onConfirm = onConfirm,
                onClear = onClear,
                onRequestClose = { updateShow(false) },
            )
        }
    }
}

/**
 * The panel used inside [NColorPicker]. Use it directly to embed a color
 * picker without a trigger.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NColorPickerPanel(
    value: Color,
    onValueChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
    modes: List<NColorPickerMode> = listOf(NColorPickerMode.Rgb, NColorPickerMode.Hex, NColorPickerMode.Hsl),
    showAlpha: Boolean = true,
    swatches: List<Color> = emptyList(),
    showPreview: Boolean = false,
    actions: List<NColorPickerAction>? = null,
    disabled: Boolean = false,
    onConfirm: ((Color) -> Unit)? = null,
    onClear: (() -> Unit)? = null,
    onRequestClose: (() -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    val availableModes = remember(modes) {
        if (modes.isEmpty()) listOf(NColorPickerMode.Hex) else modes
    }
    var mode by remember { mutableStateOf(availableModes.first()) }
    LaunchedEffect(availableModes) {
        if (mode !in availableModes) mode = availableModes.first()
    }
    var hsva by remember { mutableStateOf(value.toHsva().withAlpha(showAlpha)) }
    var text by remember(mode, showAlpha) { mutableStateOf(formatColor(value, mode, showAlpha)) }

    LaunchedEffect(value, mode, showAlpha) {
        if (!colorClose(hsva.toColor(), value)) {
            hsva = value.toHsva().withAlpha(showAlpha)
        }
        val parsed = parseColor(text, showAlpha)
        if (parsed == null || !colorClose(parsed, value)) {
            text = formatColor(value, mode, showAlpha)
        }
    }

    fun emit(color: Color) {
        if (disabled) return
        onValueChange(if (showAlpha) color else color.copy(alpha = 1f))
    }

    Column(
        modifier
            .width(248.dp)
            .clip(RoundedCornerShape(theme.borderRadius))
            .background(theme.colors.popover),
    ) {
        Column(
            Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            NColorPalette(
                hsva = hsva,
                enabled = !disabled,
                onUpdate = { s, v ->
                    if (!disabled) {
                        hsva = hsva.copy(
                            s = s.coerceIn(0f, 1f),
                            v = v.coerceIn(0f, 1f),
                            a = if (showAlpha) hsva.a else 1f,
                        )
                        emit(hsva.toColor())
                    }
                },
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    NColorHueSlider(
                        hue = hsva.h,
                        enabled = !disabled,
                        onHueChange = { hue ->
                            if (!disabled) {
                                hsva = hsva.copy(h = normalizeHue(hue))
                                emit(hsva.toColor())
                            }
                        },
                    )
                    if (showAlpha) {
                        NColorAlphaSlider(
                            hsva = hsva,
                            enabled = !disabled,
                            onAlphaChange = { alpha ->
                                if (!disabled) {
                                    hsva = hsva.copy(a = alpha.coerceIn(0f, 1f))
                                    emit(hsva.toColor())
                                }
                            },
                        )
                    }
                }
                if (showPreview) {
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .nBorder(theme.colors.border, 1.dp, 15.dp),
                    ) {
                        Box(Modifier.matchParentSize().nCheckerboard())
                        Box(Modifier.matchParentSize().background(hsva.toColor()))
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                availableModes.forEach { candidate ->
                    val selected = candidate == mode
                    val tabInteraction = remember(candidate) { MutableInteractionSource() }
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(theme.borderRadiusSmall))
                            .background(if (selected) theme.colors.buttonColor2 else Color.Transparent)
                            .nClickable(tabInteraction, enabled = !disabled) { mode = candidate }
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        NText(
                            text = candidate.label + if (showAlpha) "A" else "",
                            size = 12.sp,
                            strong = selected,
                            color = when {
                                disabled -> theme.colors.textDisabled
                                selected -> theme.colors.primary
                                else -> theme.colors.text3
                            },
                        )
                    }
                }
            }

            BasicTextField(
                value = text,
                onValueChange = { input ->
                    text = input
                    val parsed = parseColor(input, showAlpha)
                    if (parsed != null) {
                        hsva = parsed.toHsva()
                        emit(parsed)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .clip(RoundedCornerShape(theme.borderRadius))
                    .background(theme.colors.input)
                    .nBorder(theme.colors.border, 1.dp, theme.borderRadius)
                    .padding(horizontal = 8.dp),
                enabled = !disabled,
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = theme.typography.fontFamily,
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                    color = if (disabled) theme.colors.textDisabled else theme.colors.text1,
                    textAlign = TextAlign.Center,
                ),
                cursorBrush = SolidColor(theme.colors.primary),
                decorationBox = { innerTextField ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        innerTextField()
                    }
                },
            )

            if (swatches.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    swatches.forEach { swatch ->
                        val swatchInteraction = remember { MutableInteractionSource() }
                        Box(
                            Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(theme.borderRadiusSmall))
                                .nBorder(theme.colors.border, 1.dp, theme.borderRadiusSmall)
                                .hoverable(swatchInteraction)
                                .nClickable(swatchInteraction, enabled = !disabled) {
                                    if (!disabled) {
                                        val target = if (showAlpha) swatch else swatch.copy(alpha = 1f)
                                        hsva = target.toHsva()
                                        emit(target)
                                    }
                                },
                        ) {
                            Box(Modifier.matchParentSize().nCheckerboard())
                            Box(Modifier.matchParentSize().background(swatch))
                        }
                    }
                }
            }
        }

        if (!actions.isNullOrEmpty()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .nTopBorder(theme.colors.divider)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (actions?.contains(NColorPickerAction.Confirm) == true) {
                    NButton(
                        onClick = {
                            onConfirm?.invoke(hsva.toColor())
                            onRequestClose?.invoke()
                        },
                        size = NSize.Small,
                        disabled = disabled,
                    ) {
                        NText(locale.confirm)
                    }
                }
                if (actions?.contains(NColorPickerAction.Clear) == true) {
                    Spacer(Modifier.width(8.dp))
                    NButton(
                        onClick = {
                            onClear?.invoke()
                            onRequestClose?.invoke()
                        },
                        size = NSize.Small,
                        disabled = disabled,
                    ) {
                        NText(locale.clear)
                    }
                }
            }
        }
    }
}

@Composable
private fun NColorPalette(
    hsva: ColorHsva,
    enabled: Boolean,
    onUpdate: (Float, Float) -> Unit,
) {
    val theme = LocalNTheme.current
    val shape = RoundedCornerShape(theme.borderRadiusSmall)
    val update: (Offset, Size) -> Unit = { position, canvasSize ->
        if (canvasSize.width > 0f && canvasSize.height > 0f) {
            onUpdate(
                position.x / canvasSize.width,
                1f - position.y / canvasSize.height,
            )
        }
    }
    val currentOnUpdate = rememberUpdatedState(update)
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(shape)
            .nDragTrack(enabled, currentOnUpdate),
    ) {
        drawRect(
            brush = Brush.horizontalGradient(
                listOf(Color.White, nHsv(hsva.h, 1f, 1f)),
            ),
        )
        drawRect(
            brush = Brush.verticalGradient(
                listOf(Color.Transparent, Color.Black),
            ),
        )
        val inset = 8.dp.toPx()
        val maxX = (size.width - inset).coerceAtLeast(inset)
        val maxY = (size.height - inset).coerceAtLeast(inset)
        val center = Offset(
            x = (hsva.s * size.width).coerceIn(inset, maxX),
            y = ((1f - hsva.v) * size.height).coerceIn(inset, maxY),
        )
        drawCircle(
            color = Color.White,
            radius = 8.dp.toPx(),
            center = center,
        )
        drawCircle(
            color = hsva.copy(a = 1f).toColor(),
            radius = 6.dp.toPx(),
            center = center,
        )
        drawCircle(
            color = Color.White,
            radius = 7.dp.toPx(),
            center = center,
            style = Stroke(2.dp.toPx()),
        )
    }
}

@Composable
private fun NColorHueSlider(
    hue: Float,
    enabled: Boolean,
    onHueChange: (Float) -> Unit,
) {
    val theme = LocalNTheme.current
    val update: (Offset, Size) -> Unit = { position, canvasSize ->
        if (canvasSize.width > 0f) {
            onHueChange(
                ((position.x / canvasSize.width) * 360f).coerceIn(0f, 359f),
            )
        }
    }
    val currentOnHueChange = rememberUpdatedState(update)
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(CircleShape)
            .nDragTrack(enabled, currentOnHueChange),
    ) {
        drawRect(
            brush = Brush.horizontalGradient(
                listOf(
                    Color.Red,
                    Color.Yellow,
                    Color.Green,
                    Color.Cyan,
                    Color.Blue,
                    Color.Magenta,
                    Color.Red,
                ),
            ),
        )
        val inset = 7.dp.toPx()
        val maxX = (size.width - inset).coerceAtLeast(inset)
        val x = (hue / 360f * size.width).coerceIn(inset, maxX)
        val center = Offset(x, size.height / 2f)
        drawCircle(Color.White, radius = 7.dp.toPx(), center = center)
        drawCircle(
            color = nHsv(normalizeHue(hue), 1f, 1f),
            radius = 5.dp.toPx(),
            center = center,
        )
        drawCircle(
            color = theme.colors.popover.copy(alpha = 0.35f),
            radius = 7.dp.toPx(),
            center = center,
            style = Stroke(1.dp.toPx()),
        )
    }
}

@Composable
private fun NColorAlphaSlider(
    hsva: ColorHsva,
    enabled: Boolean,
    onAlphaChange: (Float) -> Unit,
) {
    val theme = LocalNTheme.current
    val update: (Offset, Size) -> Unit = { position, canvasSize ->
        if (canvasSize.width > 0f) {
            onAlphaChange(
                (position.x / canvasSize.width).coerceIn(0f, 1f),
            )
        }
    }
    val currentOnAlphaChange = rememberUpdatedState(update)
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(CircleShape)
            .nDragTrack(enabled, currentOnAlphaChange),
    ) {
        drawCheckerboard(4.dp.toPx())
        val opaque = hsva.copy(a = 1f).toColor()
        drawRect(
            brush = Brush.horizontalGradient(
                listOf(opaque.copy(alpha = 0f), opaque),
            ),
        )
        val inset = 7.dp.toPx()
        val maxX = (size.width - inset).coerceAtLeast(inset)
        val x = (hsva.a * size.width).coerceIn(inset, maxX)
        val center = Offset(x, size.height / 2f)
        drawCircle(Color.White, radius = 7.dp.toPx(), center = center)
        drawCircle(hsva.toColor(), radius = 5.dp.toPx(), center = center)
        drawCircle(
            color = theme.colors.popover.copy(alpha = 0.35f),
            radius = 7.dp.toPx(),
            center = center,
            style = Stroke(1.dp.toPx()),
        )
    }
}

private fun Modifier.nDragTrack(
    enabled: Boolean,
    onPosition: State<(Offset, Size) -> Unit>,
): Modifier {
    if (!enabled) return this
    return pointerInput(Unit) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            onPosition.value(down.position, Size(size.width.toFloat(), size.height.toFloat()))
            down.consume()
            drag(down.id) { change ->
                onPosition.value(change.position, Size(size.width.toFloat(), size.height.toFloat()))
                change.consume()
            }
        }
    }
}

private data class ColorHsva(
    val h: Float,
    val s: Float,
    val v: Float,
    val a: Float,
) {
    fun toColor(): Color = nHsv(
        h = normalizeHue(h),
        s = s.coerceIn(0f, 1f),
        v = v.coerceIn(0f, 1f),
    ).copy(alpha = a.coerceIn(0f, 1f))

    fun withAlpha(showAlpha: Boolean): ColorHsva = if (showAlpha) this else copy(a = 1f)
}

private data class ColorHsl(val h: Float, val s: Float, val l: Float)

private fun Color.toHsva(): ColorHsva {
    val r = red
    val g = green
    val b = blue
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = max - min
    val hue = when {
        delta == 0f -> 0f
        max == r -> 60f * (((g - b) / delta) % 6f)
        max == g -> 60f * (((b - r) / delta) + 2f)
        else -> 60f * (((r - g) / delta) + 4f)
    }
    return ColorHsva(
        h = normalizeHue(hue),
        s = if (max == 0f) 0f else delta / max,
        v = max,
        a = alpha,
    )
}

private fun Color.toHsl(): ColorHsl {
    val r = red
    val g = green
    val b = blue
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val lightness = (max + min) / 2f
    val delta = max - min
    val saturation = if (delta == 0f) {
        0f
    } else {
        delta / (1f - abs(2f * lightness - 1f)).coerceAtLeast(0.0001f)
    }
    val hue = when {
        delta == 0f -> 0f
        max == r -> 60f * (((g - b) / delta) % 6f)
        max == g -> 60f * (((b - r) / delta) + 2f)
        else -> 60f * (((r - g) / delta) + 4f)
    }
    return ColorHsl(
        h = normalizeHue(hue),
        s = saturation.coerceIn(0f, 1f),
        l = lightness.coerceIn(0f, 1f),
    )
}

private fun hslToColor(hue: Float, saturation: Float, lightness: Float): Color {
    val h = normalizeHue(hue)
    val c = (1f - abs(2f * lightness - 1f)) * saturation
    val x = c * (1f - abs((h / 60f) % 2f - 1f))
    val m = lightness - c / 2f
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

private fun normalizeHue(hue: Float): Float {
    val normalized = hue % 360f
    return if (normalized < 0f) normalized + 360f else normalized
}

private fun colorClose(a: Color, b: Color): Boolean =
    abs(a.red - b.red) < 0.004f &&
        abs(a.green - b.green) < 0.004f &&
        abs(a.blue - b.blue) < 0.004f &&
        abs(a.alpha - b.alpha) < 0.004f

private val numberPattern = Regex("[-+]?(?:\\d+\\.?\\d*|\\.\\d+)")

private fun parseNumbers(text: String): List<Float> =
    numberPattern.findAll(text).map { it.value.toFloat() }.toList()

private fun parseColor(input: String, showAlpha: Boolean): Color? {
    val text = input.trim()
    if (text.isEmpty()) return null
    val lower = text.lowercase()
    val color = when {
        lower.startsWith("rgb") -> {
            val values = parseNumbers(lower)
            if (values.size < 3) {
                null
            } else {
                Color(
                    red = values[0].coerceIn(0f, 255f) / 255f,
                    green = values[1].coerceIn(0f, 255f) / 255f,
                    blue = values[2].coerceIn(0f, 255f) / 255f,
                    alpha = if (showAlpha && values.size > 3) values[3].coerceIn(0f, 1f) else 1f,
                )
            }
        }
        lower.startsWith("hsl") -> {
            val values = parseNumbers(lower)
            if (values.size < 3) {
                null
            } else {
                hslToColor(
                    hue = values[0],
                    saturation = (values[1] / 100f).coerceIn(0f, 1f),
                    lightness = (values[2] / 100f).coerceIn(0f, 1f),
                ).copy(
                    alpha = if (showAlpha && values.size > 3) values[3].coerceIn(0f, 1f) else 1f,
                )
            }
        }
        lower.startsWith("hsv") -> {
            val values = parseNumbers(lower)
            if (values.size < 3) {
                null
            } else {
                nHsv(
                    h = normalizeHue(values[0]),
                    s = (values[1] / 100f).coerceIn(0f, 1f),
                    v = (values[2] / 100f).coerceIn(0f, 1f),
                ).copy(
                    alpha = if (showAlpha && values.size > 3) values[3].coerceIn(0f, 1f) else 1f,
                )
            }
        }
        else -> parseHex(text)
    } ?: return null
    return if (showAlpha) color else color.copy(alpha = 1f)
}

private fun parseHex(input: String): Color? {
    val hex = input.removePrefix("#").trim()
    if (hex.isEmpty() || hex.any { it !in "0123456789abcdefABCDEF" }) return null
    val expanded = when (hex.length) {
        3, 4 -> hex.map { "$it$it" }.joinToString("")
        6, 8 -> hex
        else -> return null
    }
    fun channel(index: Int): Float =
        expanded.substring(index, index + 2).toInt(16) / 255f
    return when (expanded.length) {
        6 -> Color(channel(0), channel(2), channel(4), 1f)
        8 -> Color(channel(0), channel(2), channel(4), channel(6))
        else -> null
    }
}

private fun formatColor(color: Color, mode: NColorPickerMode, showAlpha: Boolean): String =
    when (mode) {
        NColorPickerMode.Hex -> hexString(color, showAlpha)
        NColorPickerMode.Rgb -> {
            val red = (color.red * 255f).roundToInt()
            val green = (color.green * 255f).roundToInt()
            val blue = (color.blue * 255f).roundToInt()
            if (showAlpha) {
                "rgba($red, $green, $blue, ${formatAlpha(color.alpha)})"
            } else {
                "rgb($red, $green, $blue)"
            }
        }
        NColorPickerMode.Hsl -> {
            val hsl = color.toHsl()
            val hue = hsl.h.roundToInt()
            val saturation = (hsl.s * 100f).roundToInt()
            val lightness = (hsl.l * 100f).roundToInt()
            if (showAlpha) {
                "hsla($hue, $saturation%, $lightness%, ${formatAlpha(color.alpha)})"
            } else {
                "hsl($hue, $saturation%, $lightness%)"
            }
        }
        NColorPickerMode.Hsv -> {
            val hsv = color.toHsva()
            val hue = hsv.h.roundToInt()
            val saturation = (hsv.s * 100f).roundToInt()
            val value = (hsv.v * 100f).roundToInt()
            if (showAlpha) {
                "hsva($hue, $saturation%, $value%, ${formatAlpha(color.alpha)})"
            } else {
                "hsv($hue, $saturation%, $value%)"
            }
        }
    }

private fun hexString(color: Color, showAlpha: Boolean): String {
    fun part(value: Float): String =
        (value * 255f).roundToInt().coerceIn(0, 255).toString(16).padStart(2, '0')
    val rgb = "#${part(color.red)}${part(color.green)}${part(color.blue)}"
    return if (showAlpha) rgb + part(color.alpha) else rgb
}

private fun formatAlpha(alpha: Float): String {
    val rounded = (alpha * 100f).roundToInt() / 100f
    return if (rounded == rounded.toInt().toFloat()) {
        rounded.toInt().toString()
    } else {
        rounded.toString()
    }
}

private fun Color.blendedOverWhite(): Color = Color(
    red = red * alpha + (1f - alpha),
    green = green * alpha + (1f - alpha),
    blue = blue * alpha + (1f - alpha),
    alpha = 1f,
)

private fun readableOn(color: Color): Color =
    if (nIsLight(color.blendedOverWhite())) Color(0xFF333639) else Color.White

private fun Modifier.nCheckerboard(cell: Dp = 4.dp): Modifier = drawBehind {
    drawCheckerboard(cell.toPx())
}

private fun DrawScope.drawCheckerboard(cell: Float) {
    drawRect(Color.White)
    if (cell <= 0f) return
    val columns = ceil(size.width / cell).toInt()
    val rows = ceil(size.height / cell).toInt()
    val cellColor = Color(0xFFDDDDDD)
    for (row in 0 until rows) {
        for (column in 0 until columns) {
            if ((row + column) % 2 != 0) continue
            val left = column * cell
            val top = row * cell
            drawRect(
                color = cellColor,
                topLeft = Offset(left, top),
                size = Size(
                    width = minOf(cell, size.width - left),
                    height = minOf(cell, size.height - top),
                ),
            )
        }
    }
}
