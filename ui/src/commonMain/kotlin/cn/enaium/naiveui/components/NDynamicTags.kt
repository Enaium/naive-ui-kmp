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
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import cn.enaium.naiveui.theme.nSize

private fun dynamicTagsSmallerSize(size: NSize): NSize = when (size) {
    NSize.Tiny -> NSize.Tiny
    NSize.Small -> NSize.Tiny
    NSize.Medium -> NSize.Small
    NSize.Large -> NSize.Medium
}

/**
 * Editable list of tags. Clicking the add button reveals an inline input that
 * commits on Enter or blur; tags can be removed through their close icon.
 */
@Composable
fun NDynamicTags(
    value: List<String>,
    onValueChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    max: Int? = null,
    size: NSize? = null,
    closable: Boolean = true,
    disabled: Boolean = false,
    type: NStatus = NStatus.Default,
    round: Boolean = false,
    placeholder: String? = null,
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    val resolvedSize = nSize(size)
    val inputSize = dynamicTagsSmallerSize(resolvedSize)
    var showInput by remember { mutableStateOf(false) }
    var inputValue by remember { mutableStateOf("") }
    var hadFocus by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val triggerDisabled = disabled || (max != null && value.size >= max)

    fun commit() {
        if (!showInput) return
        val text = inputValue.trim()
        if (text.isNotEmpty() && (max == null || value.size < max)) {
            onValueChange(value + text)
        }
        showInput = false
        inputValue = ""
    }

    LaunchedEffect(showInput) {
        if (showInput) focusRequester.requestFocus()
    }

    NSpace(modifier = modifier, size = 8.dp, wrap = true) {
        value.forEachIndexed { index, tag ->
            composeKey(index) {
                NTag(
                    text = tag,
                    size = resolvedSize,
                    type = type,
                    round = round,
                    closable = closable && !disabled,
                    disabled = disabled,
                    onClose = {
                        onValueChange(value.toMutableList().apply { removeAt(index) })
                    },
                )
            }
        }
        if (showInput) {
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
                        text = placeholder ?: locale.dynamicTags.add,
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
