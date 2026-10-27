package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.internal.NBaseLoading
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.internal.NClearButton
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import cn.enaium.naiveui.theme.nSize
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt

/** Where the increment/decrement controls are rendered. */
enum class NInputNumberButtonPlacement { Left, Right, Both, None }

private val NNumberPattern = Regex("^-?\\d*\\.?\\d*$")

private fun formatNumber(value: Double?, precision: Int?): String {
    if (value == null) return ""
    if (precision == null) {
        return if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()
    }
    if (precision <= 0) return round(value).toLong().toString()
    val factor = 10.0.pow(precision)
    val rounded = round(value * factor) / factor
    val whole = rounded.toLong()
    val fraction = round(abs(rounded - whole) * factor).toLong()
    return "$whole.${fraction.toString().padStart(precision, '0')}"
}

private fun applyPrecision(value: Double, precision: Int?): Double {
    if (precision == null) return value
    val factor = 10.0.pow(precision)
    return round(value * factor) / factor
}

/**
 * Numeric input with increment/decrement controls, ported from naive-ui's
 * `n-input-number`.
 */
@Composable
fun NInputNumber(
    value: Double?,
    onValueChange: (Double?) -> Unit,
    modifier: Modifier = Modifier,
    min: Double? = null,
    max: Double? = null,
    step: Double = 1.0,
    precision: Int? = null,
    placeholder: String = "",
    disabled: Boolean = false,
    readonly: Boolean = false,
    clearable: Boolean = false,
    size: NSize? = null,
    status: NStatus? = null,
    showButton: Boolean = true,
    buttonPlacement: NInputNumberButtonPlacement = NInputNumberButtonPlacement.Right,
    loading: Boolean = false,
    updateValueOnInput: Boolean = true,
    parse: ((String) -> Double?)? = null,
    format: ((Double?) -> String)? = null,
    validator: ((Double) -> Boolean)? = null,
    keyboardEnabled: Boolean = true,
    minusIcon: ImageVector? = null,
    addIcon: ImageVector? = null,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
    onFocus: (() -> Unit)? = null,
    onBlur: (() -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val resolvedSize = nSize(size)
    val height = theme.heightFor(resolvedSize)
    val fontSize = theme.fontSizeFor(resolvedSize)
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val hovered by interaction.collectIsHoveredAsState()

    fun display(next: Double?): String = format?.invoke(next) ?: formatNumber(next, precision)

    var text by remember { mutableStateOf(display(value)) }

    fun parseText(raw: String): Double? {
        val parser = parse
        if (parser != null) return parser(raw)
        if (raw.isBlank() || raw == "-" || raw == "." || raw == "-.") return null
        return raw.toDoubleOrNull()
    }

    fun normalize(next: Double): Double {
        var result = applyPrecision(next, precision)
        if (min != null) result = maxOf(result, min)
        if (max != null) result = minOf(result, max)
        return applyPrecision(result, precision)
    }

    fun valid(next: Double): Boolean = validator?.invoke(next) ?: true

    fun emitFromText(raw: String) {
        if (!updateValueOnInput || parse != null) return
        val parsed = parseText(raw)
        if (parsed == null) {
            onValueChange(null)
            return
        }
        val normalized = normalize(parsed)
        if (valid(normalized)) onValueChange(normalized)
    }

    fun commit() {
        val parsed = parseText(text)
        if (parsed == null) {
            onValueChange(null)
            text = ""
            return
        }
        val normalized = normalize(parsed)
        if (valid(normalized)) {
            onValueChange(normalized)
            text = display(normalized)
        } else {
            text = display(value)
        }
    }

    fun stepBy(delta: Double) {
        val base = parseText(text) ?: value ?: 0.0
        val next = normalize(base + delta)
        if (!valid(next)) return
        text = display(next)
        onValueChange(next)
    }

    LaunchedEffect(value) {
        if (!focused) text = display(value)
    }

    var hasFocused by remember { mutableStateOf(false) }
    LaunchedEffect(focused) {
        if (focused) {
            hasFocused = true
            onFocus?.invoke()
        } else if (hasFocused) {
            commit()
            onBlur?.invoke()
        }
    }

    val effectiveStatus = status ?: LocalNFormItemStatus.current
    val statusColors = if (effectiveStatus != null && effectiveStatus != NStatus.Default) {
        theme.colors.status(effectiveStatus)
    } else {
        null
    }
    val borderColor = when {
        disabled -> theme.colors.border
        statusColors != null -> if (focused || hovered) statusColors.hover else statusColors.color
        focused || hovered -> theme.colors.primaryHover
        else -> theme.colors.border
    }
    val ringColor = when {
        !focused -> Color.Transparent
        statusColors != null -> statusColors.color.copy(alpha = 0.2f)
        else -> theme.colors.primary.copy(alpha = 0.2f)
    }
    val textColor = if (disabled) theme.colors.textDisabled else theme.colors.text2
    val textStyle = theme.typography.style(fontSize, color = textColor)
    val shape = RoundedCornerShape(theme.borderRadius)
    val background = if (disabled) theme.colors.inputDisabled else theme.colors.input
    val current = parseText(text) ?: value

    val canDecrement = !disabled && !readonly && (min == null || current == null || current > min)
    val canIncrement = !disabled && !readonly && (max == null || current == null || current < max)
    val showControls = showButton && buttonPlacement != NInputNumberButtonPlacement.None
    val showLeftControls = showControls && buttonPlacement == NInputNumberButtonPlacement.Left
    val showRightControls = showControls && buttonPlacement == NInputNumberButtonPlacement.Right
    val showBothControls = showControls && buttonPlacement == NInputNumberButtonPlacement.Both
    val showClear = clearable && !disabled && !readonly && (value != null || text.isNotEmpty())

    Row(
        modifier
            .fillMaxWidth()
            .height(height)
            .nFocusRing(focused, ringColor, theme.borderRadius)
            .clip(shape)
            .background(background, shape)
            .nBorder(borderColor, 1.dp, theme.borderRadius)
            .hoverable(interaction, enabled = !disabled),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showBothControls) {
            NNumberStepButton(
                icon = minusIcon ?: NIcons.Remove,
                enabled = canDecrement,
                height = height,
                onClick = { stepBy(-step) },
            )
        } else if (showLeftControls) {
            NNumberStepper(
                height = height,
                canIncrement = canIncrement,
                canDecrement = canDecrement,
                onIncrement = { stepBy(step) },
                onDecrement = { stepBy(-step) },
                addIcon = addIcon,
                minusIcon = minusIcon,
            )
        }
        Row(
            Modifier.weight(1f).fillMaxHeight().padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (prefix != null) {
                Row(Modifier.padding(end = 4.dp), verticalAlignment = Alignment.CenterVertically) { prefix() }
            }
            Box(Modifier.weight(1f)) {
                if (text.isEmpty() && placeholder.isNotEmpty()) {
                    NBaseText(
                        text = placeholder,
                        style = textStyle.copy(
                            color = if (disabled) theme.colors.placeholderDisabled else theme.colors.placeholder,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                BasicTextField(
                    value = text,
                    onValueChange = { next ->
                        val allowed = parse != null || NNumberPattern.matches(next)
                        if (!readonly && allowed) {
                            text = next
                            emitFromText(next)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onPreviewKeyEvent { event ->
                            if (event.type != KeyEventType.KeyDown || disabled || !keyboardEnabled) {
                                return@onPreviewKeyEvent false
                            }
                            when (event.key) {
                                Key.DirectionUp -> {
                                    if (canIncrement) stepBy(step)
                                    true
                                }
                                Key.DirectionDown -> {
                                    if (canDecrement) stepBy(-step)
                                    true
                                }
                                Key.Enter -> {
                                    commit()
                                    true
                                }
                                else -> false
                            }
                        },
                    textStyle = textStyle,
                    enabled = !disabled,
                    readOnly = readonly,
                    singleLine = true,
                    cursorBrush = SolidColor(statusColors?.color ?: theme.colors.primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { commit() }),
                    interactionSource = interaction,
                )
            }
            if (loading) {
                Box(Modifier.padding(start = 4.dp)) {
                    NBaseLoading(size = 14.dp, color = statusColors?.color ?: theme.colors.primary)
                }
            }
            if (suffix != null) {
                Row(Modifier.padding(start = 4.dp), verticalAlignment = Alignment.CenterVertically) { suffix() }
            }
            if (showClear) {
                Box(Modifier.padding(start = 4.dp)) {
                    NClearButton(
                        onClick = {
                            text = ""
                            onValueChange(null)
                        },
                        size = 16.dp,
                        iconSize = 11.dp,
                    )
                }
            }
        }
        if (showBothControls) {
            NNumberStepButton(
                icon = addIcon ?: NIcons.Add,
                enabled = canIncrement,
                height = height,
                onClick = { stepBy(step) },
            )
        } else if (showRightControls) {
            NNumberStepper(
                height = height,
                canIncrement = canIncrement,
                canDecrement = canDecrement,
                onIncrement = { stepBy(step) },
                onDecrement = { stepBy(-step) },
                addIcon = addIcon,
                minusIcon = minusIcon,
            )
        }
    }
}

/** Integer-typed convenience overload so callers can keep an `Int?` state. */
@Composable
fun NInputNumber(
    value: Int?,
    onValueChange: (Int?) -> Unit,
    modifier: Modifier = Modifier,
    min: Int? = null,
    max: Int? = null,
    step: Int = 1,
    precision: Int? = null,
    placeholder: String = "",
    disabled: Boolean = false,
    readonly: Boolean = false,
    clearable: Boolean = false,
    size: NSize? = null,
    status: NStatus? = null,
    showButton: Boolean = true,
    buttonPlacement: NInputNumberButtonPlacement = NInputNumberButtonPlacement.Right,
    loading: Boolean = false,
    updateValueOnInput: Boolean = true,
    validator: ((Int) -> Boolean)? = null,
    keyboardEnabled: Boolean = true,
    minusIcon: ImageVector? = null,
    addIcon: ImageVector? = null,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
    onFocus: (() -> Unit)? = null,
    onBlur: (() -> Unit)? = null,
) {
    NInputNumber(
        value = value?.toDouble(),
        onValueChange = { next -> onValueChange(next?.roundToInt()) },
        modifier = modifier,
        min = min?.toDouble(),
        max = max?.toDouble(),
        step = step.toDouble(),
        precision = precision,
        placeholder = placeholder,
        disabled = disabled,
        readonly = readonly,
        clearable = clearable,
        size = size,
        status = status,
        showButton = showButton,
        buttonPlacement = buttonPlacement,
        loading = loading,
        updateValueOnInput = updateValueOnInput,
        validator = validator?.let { check -> { next: Double -> check(next.roundToInt()) } },
        keyboardEnabled = keyboardEnabled,
        minusIcon = minusIcon,
        addIcon = addIcon,
        prefix = prefix,
        suffix = suffix,
        onFocus = onFocus,
        onBlur = onBlur,
    )
}

@Composable
private fun NNumberStepper(
    height: Dp,
    canIncrement: Boolean,
    canDecrement: Boolean,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    addIcon: ImageVector?,
    minusIcon: ImageVector?,
) {
    Column {
        NNumberStepButton(
            icon = addIcon ?: NIcons.ArrowUp,
            enabled = canIncrement,
            height = height / 2,
            onClick = onIncrement,
        )
        NNumberStepButton(
            icon = minusIcon ?: NIcons.ChevronDownFilled,
            enabled = canDecrement,
            height = height / 2,
            onClick = onDecrement,
        )
    }
}

@Composable
private fun NNumberStepButton(
    icon: ImageVector,
    enabled: Boolean,
    height: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Box(
        modifier
            .width(22.dp)
            .height(height)
            .background(if (hovered && enabled) theme.colors.pressed else Color.Transparent)
            .hoverable(interaction, enabled = enabled)
            .nClickable(interaction, enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        NIcon(
            icon = icon,
            size = 12.dp,
            color = if (enabled) theme.colors.icon else theme.colors.iconDisabled,
        )
    }
}

private fun Modifier.nFocusRing(visible: Boolean, color: Color, radius: Dp): Modifier {
    if (!visible) return this
    return drawBehind {
        val stroke = 2.dp.toPx()
        drawRoundRect(
            color = color,
            topLeft = Offset(-stroke / 2f, -stroke / 2f),
            size = Size(size.width + stroke, size.height + stroke),
            cornerRadius = CornerRadius(radius.toPx() + stroke / 2f),
            style = Stroke(width = stroke),
        )
    }
}
