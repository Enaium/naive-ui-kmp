package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
import androidx.compose.ui.platform.LocalFocusManager
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.VisualTransformation
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

/** The rendered input control. */
enum class NInputType { Text, Password, Textarea }

/**
 * Imperative handle for [NInput], mirroring the naive-ui component instance
 * methods that make sense outside the DOM.
 */
class NInputController {
    internal var focusAction: (() -> Unit)? = null
    internal var blurAction: (() -> Unit)? = null
    internal var clearAction: (() -> Unit)? = null

    fun focus() {
        focusAction?.invoke()
    }

    fun blur() {
        blurAction?.invoke()
    }

    fun clear() {
        clearAction?.invoke()
    }
}

/**
 * Text input, ported from naive-ui's `n-input`.
 *
 * Supports the `text`, `password` and `textarea` types, prefix/suffix slots,
 * clearable and word count affordances plus validation status styling.
 */
@Composable
fun NInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    type: NInputType = NInputType.Text,
    size: NSize? = null,
    status: NStatus? = null,
    disabled: Boolean = false,
    readonly: Boolean = false,
    clearable: Boolean = false,
    round: Boolean = false,
    maxlength: Int? = null,
    allowInput: ((String) -> Boolean)? = null,
    countGraphemes: ((String) -> Int)? = null,
    trim: Boolean = false,
    showCount: Boolean = false,
    autofocus: Boolean = false,
    loading: Boolean = false,
    passivelyActivated: Boolean = false,
    keyboardType: KeyboardType? = null,
    controller: NInputController? = null,
    bordered: Boolean = true,
    minLines: Int = 3,
    maxLines: Int = Int.MAX_VALUE,
    onClear: (() -> Unit)? = null,
    onPressEnter: (() -> Unit)? = null,
    onFocus: (() -> Unit)? = null,
    onBlur: (() -> Unit)? = null,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val resolvedSize = nSize(size)
    val height = theme.heightFor(resolvedSize)
    val fontSize = theme.fontSizeFor(resolvedSize)
    val isTextarea = type == NInputType.Textarea
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val hovered by interaction.collectIsHoveredAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var activated by remember(passivelyActivated) { mutableStateOf(!passivelyActivated) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(value, TextRange(value.length)))
    }
    LaunchedEffect(value) {
        if (textFieldValue.text != value) {
            val length = value.length
            val selection = textFieldValue.selection
            textFieldValue = TextFieldValue(
                text = value,
                selection = TextRange(
                    selection.start.coerceIn(0, length),
                    selection.end.coerceIn(0, length),
                ),
            )
        }
    }

    LaunchedEffect(autofocus) {
        if (autofocus && !disabled) focusRequester.requestFocus()
    }

    SideEffect {
        controller?.focusAction = { focusRequester.requestFocus() }
        controller?.blurAction = { focusManager.clearFocus() }
        controller?.clearAction = {
            onClear?.invoke()
            onValueChange("")
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
    val caretColor = statusColors?.color ?: theme.colors.primary
    val radius = if (round) height / 2 else theme.borderRadius
    val shape = RoundedCornerShape(radius)
    val background = if (disabled) theme.colors.inputDisabled else theme.colors.input
    val textColor = if (disabled) theme.colors.textDisabled else theme.colors.text2
    val placeholderColor = if (disabled) theme.colors.placeholderDisabled else theme.colors.placeholder
    val textStyle = theme.typography.style(fontSize, color = textColor)
    val passiveReadOnly = passivelyActivated && !activated
    val effectiveReadOnly = readonly || passiveReadOnly
    val showClear = clearable && !disabled && !readonly && value.isNotEmpty()
    val showPasswordToggle = type == NInputType.Password && !disabled
    val valueCount = countGraphemes?.invoke(value) ?: value.length

    val displayCount = if (maxlength != null) "$valueCount/$maxlength" else valueCount.toString()
    val countStyle = theme.typography.style(fontSize * 0.86f, color = theme.colors.text3)

    val visualTransformation = if (type == NInputType.Password && !passwordVisible) {
        PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }

    fun acceptedValue(next: String): String? {
        val candidate = if (trim) next.trim() else next
        val counter = countGraphemes
        val count = counter?.invoke(candidate) ?: candidate.length
        val limited = when {
            maxlength == null || count <= maxlength -> candidate
            counter != null -> return null
            else -> candidate.take(maxlength)
        }
        val predicate = allowInput
        if (predicate != null && !predicate(limited)) return null
        return limited
    }

    BasicTextField(
        value = textFieldValue,
        onValueChange = { next ->
            textFieldValue = next
            if (next.composition == null) {
                val accepted = acceptedValue(next.text)
                if (accepted != null) {
                    if (accepted != next.text) {
                        textFieldValue = TextFieldValue(
                            text = accepted,
                            selection = TextRange(accepted.length),
                        )
                    }
                    onValueChange(accepted)
                } else {
                    val length = value.length
                    textFieldValue = TextFieldValue(
                        text = value,
                        selection = TextRange(length),
                    )
                }
            } else {
                // Preserve the IME composition range while the user is typing
                // pinyin or another composing script. Committing on every
                // intermediate string resets the cursor and makes the text jump.
                onValueChange(next.text)
            }
        },
        modifier = modifier
            .then(
                if (isTextarea) {
                    Modifier.fillMaxWidth()
                } else {
                    Modifier.fillMaxWidth().height(height)
                },
            )
            .nFocusRing(focused, ringColor, radius)
            .clip(shape)
            .background(if (bordered) background else Color.Transparent, shape)
            .then(if (bordered) Modifier.nBorder(borderColor, 1.dp, radius) else Modifier)
            .hoverable(interaction, enabled = !disabled)
            .focusRequester(focusRequester)
            .onPreviewKeyEvent { event ->
                val isEnter = event.type == KeyEventType.KeyDown &&
                    (event.key == Key.Enter || event.key == Key.NumPadEnter)
                when {
                    isEnter && passivelyActivated && !activated -> {
                        activated = true
                        true
                    }
                    isEnter && !isTextarea -> {
                        val enter = onPressEnter
                        if (enter != null) {
                            enter()
                            true
                        } else {
                            false
                        }
                    }
                    else -> false
                }
            },
        textStyle = textStyle,
        enabled = !disabled,
        readOnly = effectiveReadOnly,
        singleLine = !isTextarea,
        minLines = if (isTextarea) minLines.coerceAtMost(maxLines) else 1,
        maxLines = if (isTextarea) maxLines else 1,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType ?: KeyboardType.Text,
            imeAction = when {
                isTextarea -> ImeAction.Default
                onPressEnter != null -> ImeAction.Done
                else -> ImeAction.Default
            },
        ),
        keyboardActions = KeyboardActions(
            onDone = { onPressEnter?.invoke() },
            onGo = { onPressEnter?.invoke() },
            onSend = { onPressEnter?.invoke() },
        ),
        cursorBrush = SolidColor(caretColor),
        interactionSource = interaction,
        decorationBox = { innerTextField ->
            if (isTextarea) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        if (prefix != null) {
                            Row(Modifier.padding(end = 4.dp, top = 1.dp), verticalAlignment = Alignment.CenterVertically) { prefix() }
                        }
                        Box(Modifier.weight(1f)) {
                            if (value.isEmpty() && placeholder.isNotEmpty()) {
                                NBaseText(
                                    text = placeholder,
                                    style = textStyle.copy(color = placeholderColor),
                                    maxLines = if (maxLines == Int.MAX_VALUE) Int.MAX_VALUE else maxLines,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            innerTextField()
                        }
                        if (suffix != null) {
                            Row(Modifier.padding(start = 4.dp, top = 1.dp), verticalAlignment = Alignment.CenterVertically) { suffix() }
                        }
                    }
                    if (showCount) {
                        Row(
                            Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            NBaseText(displayCount, countStyle)
                        }
                    }
                }
            } else {
                Row(
                    Modifier.fillMaxSize().padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (prefix != null) {
                        Row(Modifier.padding(end = 4.dp), verticalAlignment = Alignment.CenterVertically) { prefix() }
                    }
                    Box(Modifier.weight(1f)) {
                        if (value.isEmpty() && placeholder.isNotEmpty()) {
                            NBaseText(
                                text = placeholder,
                                style = textStyle.copy(color = placeholderColor),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        innerTextField()
                    }
                    if (loading) {
                        Box(Modifier.padding(start = 4.dp)) {
                            NBaseLoading(
                                size = 14.dp,
                                color = statusColors?.color ?: theme.colors.primary,
                            )
                        }
                    }
                    if (suffix != null) {
                        Row(Modifier.padding(start = 4.dp), verticalAlignment = Alignment.CenterVertically) { suffix() }
                    }
                    if (showCount) {
                        Box(Modifier.padding(start = 6.dp)) { NBaseText(displayCount, countStyle) }
                    }
                    if (showPasswordToggle) {
                        val toggleInteraction = remember { MutableInteractionSource() }
                        Box(
                            Modifier
                                .padding(start = 4.dp)
                                .size(18.dp)
                                .clip(CircleShape)
                                .nClickable(toggleInteraction) { passwordVisible = !passwordVisible },
                            contentAlignment = Alignment.Center,
                        ) {
                            NIcon(
                                icon = if (passwordVisible) NIcons.EyeOff else NIcons.Eye,
                                size = 15.dp,
                                color = if (disabled) theme.colors.iconDisabled else theme.colors.icon,
                            )
                        }
                    }
                    if (showClear) {
                        Box(Modifier.padding(start = 4.dp)) {
                            NClearButton(
                                onClick = {
                                    onClear?.invoke()
                                    onValueChange("")
                                },
                                size = 16.dp,
                                iconSize = 11.dp,
                            )
                        }
                    }
                }
            }
        },
    )

    // Focus and blur callbacks are driven from the interaction stream.
    var hasFocused by remember { mutableStateOf(false) }
    LaunchedEffect(focused) {
        if (focused) {
            hasFocused = true
            onFocus?.invoke()
        } else if (hasFocused) {
            if (passivelyActivated) activated = false
            onBlur?.invoke()
        }
    }
}

/** Paints the 2px focus ring outside the input frame, naive-ui's `boxShadowFocus`. */
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

/**
 * Pairwise input, equivalent to naive-ui's `n-input` with the `pair` prop.
 *
 * Renders two text fields sharing one frame, separated by [separator].
 */
@Composable
fun NInput(
    value: Pair<String, String>,
    onValueChange: (Pair<String, String>) -> Unit,
    modifier: Modifier = Modifier,
    placeholders: Pair<String, String> = "" to "",
    separator: String = "-",
    size: NSize? = null,
    status: NStatus? = null,
    disabled: Boolean = false,
    readonly: Boolean = false,
    clearable: Boolean = false,
    round: Boolean = false,
    autofocus: Boolean = false,
    onClear: (() -> Unit)? = null,
    onFocus: (() -> Unit)? = null,
    onBlur: (() -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val resolvedSize = nSize(size)
    val height = theme.heightFor(resolvedSize)
    val fontSize = theme.fontSizeFor(resolvedSize)
    val effectiveStatus = status ?: LocalNFormItemStatus.current
    val statusColors = if (effectiveStatus != null && effectiveStatus != NStatus.Default) {
        theme.colors.status(effectiveStatus)
    } else {
        null
    }
    var startFocused by remember { mutableStateOf(false) }
    var endFocused by remember { mutableStateOf(false) }
    var hadFocus by remember { mutableStateOf(false) }
    val focused = startFocused || endFocused
    val hoverInteraction = remember { MutableInteractionSource() }
    val hovered by hoverInteraction.collectIsHoveredAsState()
    val borderColor = when {
        disabled -> theme.colors.border
        statusColors != null -> if (focused || hovered) statusColors.hover else statusColors.color
        focused || hovered -> theme.colors.primaryHover
        else -> theme.colors.border
    }
    val radius = if (round) height / 2 else theme.borderRadius
    val shape = RoundedCornerShape(radius)
    val showClear = clearable && !disabled && !readonly &&
        (value.first.isNotEmpty() || value.second.isNotEmpty())

    LaunchedEffect(focused) {
        if (focused) {
            hadFocus = true
            onFocus?.invoke()
        } else if (hadFocus) {
            onBlur?.invoke()
        }
    }

    Row(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(if (disabled) theme.colors.inputDisabled else theme.colors.input, shape)
            .nBorder(borderColor, 1.dp, radius)
            .hoverable(hoverInteraction, enabled = !disabled)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NInput(
            value = value.first,
            onValueChange = { onValueChange(it to value.second) },
            modifier = Modifier.weight(1f),
            placeholder = placeholders.first,
            size = size,
            status = status,
            disabled = disabled,
            readonly = readonly,
            autofocus = autofocus,
            bordered = false,
            onFocus = { startFocused = true },
            onBlur = { startFocused = false },
        )
        NBaseText(
            text = separator,
            style = theme.typography.style(fontSize, color = theme.colors.text3),
            modifier = Modifier.padding(horizontal = 2.dp),
        )
        NInput(
            value = value.second,
            onValueChange = { onValueChange(value.first to it) },
            modifier = Modifier.weight(1f),
            placeholder = placeholders.second,
            size = size,
            status = status,
            disabled = disabled,
            readonly = readonly,
            bordered = false,
            onFocus = { endFocused = true },
            onBlur = { endFocused = false },
        )
        if (suffix != null) {
            Row(Modifier.padding(start = 4.dp), verticalAlignment = Alignment.CenterVertically) { suffix() }
        }
        if (showClear) {
            Box(Modifier.padding(start = 4.dp)) {
                NClearButton(
                    onClick = {
                        onClear?.invoke()
                        onValueChange("" to "")
                    },
                    size = 16.dp,
                    iconSize = 11.dp,
                )
            }
        }
    }
}

/** Groups inputs so they read as a single control, mirroring `n-input-group`. */
@Composable
fun NInputGroup(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, content = content)
}

/** Label block for [NInputGroup], mirroring `n-input-group-label`. */
@Composable
fun NInputGroupLabel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val theme = LocalNTheme.current
    val size = nSize()
    Row(
        modifier
            .height(theme.heightFor(size))
            .background(theme.colors.action)
            .nBorder(theme.colors.border, 1.dp, theme.borderRadius)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}
