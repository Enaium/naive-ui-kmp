package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key as composeKey
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import cn.enaium.naiveui.theme.nSize

/** Object formatted tag value, created through `onCreate`. */
data class NDynamicTagsOption(val label: String, val value: String)

@Suppress("UNCHECKED_CAST")
private fun <T> castTag(value: Any?): T = value as T

private fun dynamicTagsSmallerSize(size: NSize): NSize = when (size) {
    NSize.Tiny -> NSize.Tiny
    NSize.Small -> NSize.Tiny
    NSize.Medium -> NSize.Small
    NSize.Large -> NSize.Medium
}

/**
 * Editable list of tags. Clicking the trigger reveals an inline input that
 * commits on Enter or blur; tags can be removed through their close icon.
 */
@Composable
fun <T> NDynamicTags(
    value: List<T>,
    onValueChange: (List<T>) -> Unit,
    modifier: Modifier = Modifier,
    max: Int? = null,
    size: NSize? = null,
    closable: Boolean = true,
    disabled: Boolean = false,
    type: NStatus = NStatus.Default,
    round: Boolean = false,
    placeholder: String = "Add",
    label: (T) -> String = { it.toString() },
    onCreate: ((String) -> T)? = null,
    renderTag: (@Composable (tag: T, index: Int, onClose: () -> Unit) -> Unit)? = null,
    inputSlot: (@Composable (submit: (String) -> Unit, deactivate: () -> Unit) -> Unit)? = null,
    triggerSlot: (@Composable (activate: () -> Unit, disabled: Boolean) -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val resolvedSize = nSize(size)
    val inputSize = dynamicTagsSmallerSize(resolvedSize)
    var showInput by remember { mutableStateOf(false) }
    var inputValue by remember { mutableStateOf("") }
    var hadFocus by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val triggerDisabled = disabled || (max != null && value.size >= max)

    fun submitTag(text: String) {
        val trimmed = text.trim()
        if (trimmed.isNotEmpty() && (max == null || value.size < max)) {
            onValueChange(value + (onCreate?.invoke(trimmed) ?: castTag<T>(trimmed)))
        }
        showInput = false
        inputValue = ""
    }

    fun commit() {
        if (showInput) submitTag(inputValue)
    }

    fun deactivate() {
        showInput = false
        inputValue = ""
    }

    fun remove(index: Int) {
        onValueChange(value.toMutableList().apply { removeAt(index) })
    }

    LaunchedEffect(showInput) {
        if (showInput && inputSlot == null) focusRequester.requestFocus()
    }

    NSpace(modifier = modifier, size = 8.dp, wrap = true) {
        value.forEachIndexed { index, tag ->
            composeKey(index) {
                if (renderTag != null) {
                    renderTag(tag, index) { remove(index) }
                } else {
                    NTag(
                        text = label(tag),
                        size = resolvedSize,
                        type = type,
                        round = round,
                        closable = closable && !disabled,
                        disabled = disabled,
                        onClose = { remove(index) },
                    )
                }
            }
        }
        if (showInput) {
            if (inputSlot != null) {
                inputSlot({ text -> submitTag(text) }, { deactivate() })
            } else {
                val textStyle = TextStyle(
                    color = theme.colors.text1,
                    fontSize = theme.fontSizeFor(inputSize),
                    fontFamily = theme.typography.fontFamily,
                )
                Box(
                    Modifier
                        .width(96.dp)
                        .height(theme.heightFor(inputSize))
                        .clip(RoundedCornerShape(theme.borderRadius))
                        .background(theme.colors.input)
                        .nBorder(theme.colors.primary, 1.dp, theme.borderRadius)
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (inputValue.isEmpty()) {
                        NText(
                            text = placeholder,
                            color = theme.colors.placeholder,
                            size = theme.fontSizeFor(inputSize),
                        )
                    }
                    BasicTextField(
                        value = inputValue,
                        onValueChange = { inputValue = it },
                        textStyle = textStyle,
                        singleLine = true,
                        cursorBrush = SolidColor(theme.colors.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onFocusChanged { state ->
                                if (hadFocus && !state.isFocused) commit()
                                hadFocus = state.isFocused
                            }
                            .onPreviewKeyEvent { event ->
                                if (
                                    event.type == KeyEventType.KeyDown &&
                                    (event.key == Key.Enter || event.key == Key.NumPadEnter)
                                ) {
                                    commit()
                                    true
                                } else {
                                    false
                                }
                            },
                    )
                }
            }
        } else if (triggerSlot != null) {
            triggerSlot({ if (!triggerDisabled) showInput = true }, triggerDisabled)
        } else {
            NButton(
                onClick = { if (!triggerDisabled) showInput = true },
                size = inputSize,
                dashed = true,
                disabled = triggerDisabled,
                icon = { NIcon(NIcons.Add, size = 14.dp) },
                content = {},
            )
        }
    }
}
