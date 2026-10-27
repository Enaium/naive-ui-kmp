package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import cn.enaium.naiveui.theme.nSize

private fun otpCellWidth(size: NSize): Dp = when (size) {
    NSize.Tiny -> 22.dp
    NSize.Small -> 24.dp
    NSize.Medium -> 30.dp
    NSize.Large -> 36.dp
}

/**
 * One-time-password input with one text field per character, ported from
 * naive-ui's `n-input-otp`.
 *
 * Typing advances the focus to the next cell, backspace on an empty cell
 * clears the previous one, and pasting a full code fills all cells at once.
 */
@Composable
fun NInputOtp(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    separator: String? = null,
    size: NSize? = null,
    status: NStatus? = null,
    disabled: Boolean = false,
    readonly: Boolean = false,
    mask: Boolean = false,
    autofocus: Boolean = false,
    block: Boolean = false,
    onFinish: ((String) -> Unit)? = null,
    onFocus: (() -> Unit)? = null,
    onBlur: (() -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val resolvedSize = nSize(size)
    val height = theme.heightFor(resolvedSize)
    val cellWidth = otpCellWidth(resolvedSize)
    val fontSize = theme.fontSizeFor(resolvedSize)
    val statusColors = if (status != null && status != NStatus.Default) theme.colors.status(status) else null
    val cells = List(length.coerceAtLeast(1)) { index ->
        value.getOrNull(index)?.takeIf { it != ' ' }
    }
    val focusRequesters = remember(length) { List(length.coerceAtLeast(1)) { FocusRequester() } }
    val interactions = remember(length) { List(length.coerceAtLeast(1)) { MutableInteractionSource() } }
    val textStyle = theme.typography.style(fontSize, color = if (disabled) theme.colors.textDisabled else theme.colors.text2)
        .copy(textAlign = TextAlign.Center)

    LaunchedEffect(Unit) {
        if (autofocus && !disabled && cells.isNotEmpty()) focusRequesters[0].requestFocus()
    }

    fun emit(next: List<Char?>) {
        val normalized = List(cells.size) { next.getOrNull(it) }
        val text = buildString {
            normalized.forEach { append(it ?: ' ') }
        }.trimEnd()
        onValueChange(text)
        if (normalized.all { it != null }) onFinish?.invoke(text)
    }

    fun setChar(index: Int, char: Char?) {
        val next = cells.toMutableList()
        next[index] = char
        emit(next)
    }

    fun focusCell(index: Int) {
        val target = index.coerceIn(0, focusRequesters.lastIndex)
        focusRequesters[target].requestFocus()
    }

    Row(
        modifier = modifier.then(if (block) Modifier.fillMaxWidth() else Modifier),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(cells.size) { index ->
            if (separator != null && cells.size > 1 && index == cells.size / 2) {
                NBaseText(
                    text = separator,
                    style = theme.typography.style(fontSize, color = theme.colors.text3),
                    modifier = Modifier.padding(horizontal = 2.dp),
                )
            }
            val interaction = interactions[index]
            val focused by interaction.collectIsFocusedAsState()
            var hasFocused by remember { mutableStateOf(false) }
            val borderColor = when {
                disabled -> theme.colors.border
                statusColors != null -> if (focused) statusColors.hover else statusColors.color
                focused -> theme.colors.primaryHover
                else -> theme.colors.border
            }
            val ringColor = when {
                !focused -> Color.Transparent
                statusColors != null -> statusColors.color.copy(alpha = 0.2f)
                else -> theme.colors.primary.copy(alpha = 0.2f)
            }
            val cellModifier = if (block) {
                Modifier.weight(1f).height(height)
            } else {
                Modifier.width(cellWidth).height(height)
            }
            Box(
                modifier = cellModifier
                    .nOtpRing(focused, ringColor, theme.borderRadius)
                    .clip(RoundedCornerShape(theme.borderRadius))
                    .background(if (disabled) theme.colors.inputDisabled else theme.colors.input)
                    .nBorder(borderColor, 1.dp, theme.borderRadius),
                contentAlignment = Alignment.Center,
            ) {
                BasicTextField(
                    value = cells[index]?.toString() ?: "",
                    onValueChange = { raw ->
                        val chars = raw.filter { !it.isWhitespace() }
                        val current = cells[index]?.toString() ?: ""
                        if (chars.isEmpty()) {
                            setChar(index, null)
                            if (index > 0) focusCell(index - 1)
                        } else {
                            val appended = current.isNotEmpty() &&
                                chars.length == current.length + 1 &&
                                chars.startsWith(current)
                            if (chars.length > 1 && !appended) {
                                val next = cells.toMutableList()
                                var cursor = index
                                for (char in chars) {
                                    if (cursor >= next.size) break
                                    next[cursor] = char
                                    cursor++
                                }
                                emit(next)
                                focusCell(cursor)
                            } else {
                                setChar(index, chars.last())
                                if (index < cells.lastIndex) focusCell(index + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .focusRequester(focusRequesters[index])
                        .onFocusChanged { state ->
                            if (state.isFocused) {
                                hasFocused = true
                                onFocus?.invoke()
                            } else if (hasFocused) {
                                onBlur?.invoke()
                            }
                        }
                        .onPreviewKeyEvent { event ->
                            if (event.type != KeyEventType.KeyDown || disabled || readonly) {
                                return@onPreviewKeyEvent false
                            }
                            when {
                                event.key == Key.Backspace && cells[index] == null && index > 0 -> {
                                    setChar(index - 1, null)
                                    focusCell(index - 1)
                                    true
                                }
                                else -> false
                            }
                        },
                    textStyle = textStyle,
                    enabled = !disabled,
                    readOnly = readonly,
                    singleLine = true,
                    visualTransformation = if (mask) PasswordVisualTransformation() else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (mask) KeyboardType.NumberPassword else KeyboardType.Number,
                    ),
                    cursorBrush = SolidColor(statusColors?.color ?: theme.colors.primary),
                    interactionSource = interaction,
                    decorationBox = { innerTextField ->
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            innerTextField()
                        }
                    },
                )
            }
        }
    }
}

private fun Modifier.nOtpRing(visible: Boolean, color: Color, radius: Dp): Modifier {
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
