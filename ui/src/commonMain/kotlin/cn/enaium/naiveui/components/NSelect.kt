package cn.enaium.naiveui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NEmpty
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NBaseLoading
import cn.enaium.naiveui.internal.NClearButton
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NPopoverSurface
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import cn.enaium.naiveui.theme.nSize
import kotlin.math.roundToInt

/** An entry of an [NSelect] menu: either an option or a group of options. */
@Immutable
sealed interface NSelectEntry

/** A single selectable option. */
@Immutable
data class NSelectOption(
    val label: String,
    val value: Any,
    val disabled: Boolean = false,
    val icon: ImageVector? = null,
) : NSelectEntry

/** A labelled group of selectable options. */
@Immutable
data class NSelectGroupOption(
    val label: String,
    val children: List<NSelectOption>,
    val key: Any = label,
) : NSelectEntry

private fun List<NSelectEntry>.flatOptions(): List<NSelectOption> = buildList {
    for (entry in this@flatOptions) {
        when (entry) {
            is NSelectOption -> add(entry)
            is NSelectGroupOption -> addAll(entry.children)
        }
    }
}

/**
 * Select one or multiple values from a popup list, mirroring `<n-select>`.
 *
 * When [multiple] is enabled [value] is expected to be a `List<Any>`, otherwise
 * a single option value. When [show] is provided the open state is controlled;
 * otherwise it is kept internally.
 */
@Composable
fun NSelect(
    options: List<NSelectEntry>,
    value: Any? = null,
    onValueChange: (Any?) -> Unit = {},
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    clearable: Boolean = false,
    disabled: Boolean = false,
    size: NSize? = null,
    multiple: Boolean = false,
    filterable: Boolean = false,
    status: NStatus? = null,
    loading: Boolean = false,
    maxTagCount: Int? = null,
    show: Boolean? = null,
    onUpdateShow: (Boolean) -> Unit = {},
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    val resolvedSize = nSize(size)
    val density = LocalDensity.current
    val interaction = remember { MutableInteractionSource() }
    var internalShow by remember { mutableStateOf(false) }
    val expanded = show ?: internalShow
    var query by remember { mutableStateOf("") }
    var activeIndex by remember { mutableStateOf(-1) }
    var triggerWidth by remember { mutableStateOf(0) }
    val focusRequester = remember { FocusRequester() }
    val inputFocusRequester = remember { FocusRequester() }
    val menuScroll: ScrollState = rememberScrollState()

    fun setShow(next: Boolean) {
        if (show == null) internalShow = next
        onUpdateShow(next)
    }

    val optionByValue = remember(options) {
        options.flatOptions().associateBy { it.value }
    }
    val selectedValues: List<Any> =
        if (multiple) (value as? List<*>)?.mapNotNull { it } ?: emptyList()
        else listOfNotNull(value)

    val filteredEntries = remember(options, query) {
        if (query.isBlank()) {
            options
        } else {
            options.mapNotNull { entry ->
                when (entry) {
                    is NSelectOption ->
                        if (entry.label.contains(query, ignoreCase = true)) entry else null
                    is NSelectGroupOption -> {
                        val children = entry.children.filter {
                            it.label.contains(query, ignoreCase = true)
                        }
                        if (children.isEmpty()) null else entry.copy(children = children)
                    }
                }
            }
        }
    }
    val visibleOptions = remember(filteredEntries) { filteredEntries.flatOptions() }
    val placeholderText = placeholder ?: locale.placeholder

    fun firstEnabledIndex(): Int = visibleOptions.indexOfFirst { !it.disabled }

    LaunchedEffect(expanded, visibleOptions) {
        if (expanded) {
            val selectedIndex = visibleOptions.indexOfFirst {
                !it.disabled && selectedValues.contains(it.value)
            }
            activeIndex = if (selectedIndex >= 0) selectedIndex else firstEnabledIndex()
        } else {
            query = ""
            activeIndex = -1
        }
    }

    fun moveActive(delta: Int) {
        if (visibleOptions.isEmpty()) return
        var index = activeIndex
        repeat(visibleOptions.size) {
            index = when {
                index < 0 -> if (delta > 0) 0 else visibleOptions.lastIndex
                else -> (index + delta).let {
                    if (it < 0) visibleOptions.lastIndex else if (it > visibleOptions.lastIndex) 0 else it
                }
            }
            if (!visibleOptions[index].disabled) {
                activeIndex = index
                return
            }
        }
    }

    fun removeValue(target: Any) {
        val next = selectedValues.filterNot { it == target }
        onValueChange(if (multiple) next else next.firstOrNull())
    }

    fun choose(option: NSelectOption) {
        if (option.disabled || disabled) return
        if (multiple) {
            val next = selectedValues.toMutableList()
            if (next.any { it == option.value }) {
                next.removeAll { it == option.value }
            } else {
                next.add(option.value)
            }
            onValueChange(next.toList())
            query = ""
        } else {
            onValueChange(option.value)
            setShow(false)
        }
    }

    val keyHandler: (KeyEvent) -> Boolean = { event ->
        if (event.type != KeyEventType.KeyDown) {
            false
        } else {
            when (event.key) {
                Key.DirectionDown -> {
                    if (expanded) moveActive(1) else setShow(true)
                    true
                }
                Key.DirectionUp -> {
                    if (expanded) moveActive(-1) else setShow(true)
                    true
                }
                Key.Enter -> {
                    if (expanded) {
                        visibleOptions.getOrNull(activeIndex)?.let { choose(it) }
                    } else {
                        setShow(true)
                    }
                    true
                }
                Key.Spacebar -> {
                    if (filterable) {
                        false
                    } else {
                        if (expanded) {
                            visibleOptions.getOrNull(activeIndex)?.let { choose(it) }
                        } else {
                            setShow(true)
                        }
                        true
                    }
                }
                Key.Escape -> {
                    if (expanded) {
                        setShow(false)
                        true
                    } else {
                        false
                    }
                }
                else -> false
            }
        }
    }

    val menuWidth = if (triggerWidth > 0) with(density) { triggerWidth.toDp() } else 200.dp

    Box(
        modifier
            .defaultMinSize(minWidth = 160.dp)
            .onGloballyPositioned { triggerWidth = it.size.width },
    ) {
        NSelectionField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onKeyEvent(keyHandler),
            size = resolvedSize,
            status = status,
            disabled = disabled,
            active = expanded,
            interaction = interaction,
            loading = loading,
            clearable = clearable && !disabled && selectedValues.isNotEmpty(),
            onClear = {
                onValueChange(if (multiple) emptyList<Any>() else null)
                query = ""
            },
            onClick = {
                if (!disabled) {
                    setShow(!expanded)
                    if (filterable) inputFocusRequester.requestFocus() else focusRequester.requestFocus()
                }
            },
        ) {
            if (multiple) {
                if (selectedValues.isEmpty() && !filterable) {
                    NText(
                        placeholderText,
                        color = theme.colors.placeholder,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                } else {
                    NSelectionTags(
                        labels = selectedValues.map { value ->
                            value to (optionByValue[value]?.label ?: value.toString())
                        },
                        maxTagCount = maxTagCount,
                        size = resolvedSize,
                        disabled = disabled,
                        onRemove = ::removeValue,
                        trailing = if (filterable) {
                            {
                                NSelectionFilterInput(
                                    query = query,
                                    onQueryChange = {
                                        query = it
                                        if (!expanded) setShow(true)
                                    },
                                    placeholder = if (selectedValues.isEmpty()) placeholderText else "",
                                    modifier = Modifier.widthIn(min = 60.dp).width(90.dp),
                                    size = resolvedSize,
                                    enabled = !disabled,
                                    focusRequester = inputFocusRequester,
                                    onFocus = { if (!disabled) setShow(true) },
                                    onKeyEvent = keyHandler,
                                )
                            }
                        } else {
                            null
                        },
                    )
                }
            } else {
                val selected = selectedValues.firstOrNull()?.let { optionByValue[it] }
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    selected?.icon?.let {
                        NIcon(it, size = 16.dp, color = theme.colors.icon)
                        Spacer(Modifier.width(6.dp))
                    }
                    if (filterable) {
                        NSelectionFilterInput(
                            query = query,
                            onQueryChange = {
                                query = it
                                if (!expanded) setShow(true)
                            },
                            placeholder = selected?.label ?: placeholderText,
                            modifier = Modifier.weight(1f),
                            size = resolvedSize,
                            enabled = !disabled,
                            focusRequester = inputFocusRequester,
                            onFocus = { if (!disabled) setShow(true) },
                            onKeyEvent = keyHandler,
                            textColor = if (selected != null) theme.colors.text1 else theme.colors.text2,
                        )
                    } else {
                        NText(
                            selected?.label ?: placeholderText,
                            modifier = Modifier.weight(1f),
                            color = if (selected != null) theme.colors.text1 else theme.colors.placeholder,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }

        NPopoverSurface(
            visible = expanded && !disabled,
            onDismiss = { setShow(false) },
            placement = NPlacement.BottomStart,
            offset = IntOffset(0, 4),
            focusable = false,
            padding = PaddingValues(0.dp),
            modifier = Modifier.width(menuWidth),
        ) {
            if (loading) {
                Box(Modifier.fillMaxWidth().padding(vertical = 20.dp), Alignment.Center) {
                    NBaseLoading()
                }
            } else if (visibleOptions.isEmpty()) {
                NSelectEmptyView(Modifier.padding(vertical = 18.dp))
            } else {
                LaunchedEffect(activeIndex, expanded) {
                    if (expanded && activeIndex >= 0) {
                        var offsetPx = 0f
                        var count = 0
                        var found = false
                        for (entry in filteredEntries) {
                            if (entry is NSelectGroupOption) {
                                offsetPx += with(density) { 26.dp.toPx() }
                                offsetPx += with(density) { 4.dp.toPx() }
                            }
                            val entries = when (entry) {
                                is NSelectOption -> listOf(entry)
                                is NSelectGroupOption -> entry.children
                            }
                            for (option in entries) {
                                if (count == activeIndex) {
                                    found = true
                                    break
                                }
                                offsetPx += with(density) { nSelectionRowHeight(resolvedSize).toPx() }
                                count++
                            }
                            if (found) break
                        }
                        val target = (offsetPx - with(density) { 4.dp.toPx() }).roundToInt()
                        menuScroll.animateScrollTo(target.coerceAtLeast(0))
                    }
                }
                NScrollbar(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 264.dp),
                    scrollState = menuScroll,
                ) {
                    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        var optionIndex = 0
                        filteredEntries.forEach { entry ->
                            when (entry) {
                                is NSelectOption -> {
                                    val index = optionIndex++
                                    NSelectOptionRow(
                                        option = entry,
                                        selected = selectedValues.contains(entry.value),
                                        active = index == activeIndex,
                                        size = resolvedSize,
                                        onClick = { choose(entry) },
                                    )
                                }
                                is NSelectGroupOption -> {
                                    NText(
                                        entry.label,
                                        modifier = Modifier.padding(
                                            start = 12.dp,
                                            end = 12.dp,
                                            top = 8.dp,
                                            bottom = 2.dp,
                                        ),
                                        size = theme.typography.fontSize.mini,
                                        color = theme.colors.text3,
                                    )
                                    entry.children.forEach { option ->
                                        val index = optionIndex++
                                        NSelectOptionRow(
                                            option = option,
                                            selected = selectedValues.contains(option.value),
                                            active = index == activeIndex,
                                            size = resolvedSize,
                                            onClick = { choose(option) },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NSelectOptionRow(
    option: NSelectOption,
    selected: Boolean,
    active: Boolean,
    size: NSize,
    onClick: () -> Unit,
) {
    val theme = LocalNTheme.current
    val enabled = !option.disabled
    NSelectionMenuRow(
        onClick = onClick,
        enabled = enabled,
        active = active,
        height = nSelectionRowHeight(size),
    ) {
        if (option.icon != null) {
            NIcon(
                option.icon,
                size = 16.dp,
                color = if (enabled) theme.colors.icon else theme.colors.iconDisabled,
            )
            Spacer(Modifier.width(8.dp))
        }
        NText(
            option.label,
            modifier = Modifier.weight(1f),
            color = when {
                !enabled -> theme.colors.textDisabled
                selected -> theme.colors.primary
                else -> theme.colors.text2
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (selected) {
            Spacer(Modifier.width(8.dp))
            NIcon(NIcons.Checkmark, size = 16.dp, color = theme.colors.primary)
        }
    }
}

/** Height of a single row in a select-like dropdown menu. */
internal fun nSelectionRowHeight(size: NSize): Dp = when (size) {
    NSize.Tiny -> 26.dp
    NSize.Small -> 30.dp
    NSize.Medium -> 34.dp
    NSize.Large -> 38.dp
}

/**
 * Shared trigger shell for select, tree select and cascader: bordered field
 * with hover/focus border, clear affordance and dropdown arrow.
 */
@Composable
internal fun NSelectionField(
    size: NSize,
    status: NStatus?,
    disabled: Boolean,
    active: Boolean,
    interaction: MutableInteractionSource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    clearable: Boolean = false,
    onClear: () -> Unit = {},
    showArrow: Boolean = true,
    content: @Composable () -> Unit,
) {
    val theme = LocalNTheme.current
    val hovered by interaction.collectIsHoveredAsState()
    val focused by interaction.collectIsFocusedAsState()
    val shape = RoundedCornerShape(theme.borderRadius)
    val borderColor = when {
        disabled -> theme.colors.border
        status != null && status != NStatus.Default -> theme.colors.statusColor(status)
        active || focused -> theme.colors.primary
        hovered -> theme.colors.primaryHover
        else -> theme.colors.border
    }
    val background = if (disabled) theme.colors.inputDisabled else theme.colors.input
    val horizontal = when (size) {
        NSize.Tiny -> 6.dp
        NSize.Small -> 8.dp
        NSize.Medium -> 10.dp
        NSize.Large -> 12.dp
    }
    Box(
        modifier
            .clip(shape)
            .background(background)
            .nBorder(borderColor, 1.dp, theme.borderRadius)
            .hoverable(interaction, enabled = !disabled)
            .nClickable(interaction, enabled = !disabled, onClick = onClick)
            .heightIn(min = theme.heightFor(size))
            .padding(horizontal = horizontal, vertical = 2.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(end = if (showArrow || clearable || loading) 44.dp else 2.dp),
        ) {
            content()
        }
        Row(
            Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (clearable) {
                NClearButton(onClick = onClear)
            }
            when {
                loading -> NBaseLoading(size = 16.dp, color = theme.colors.primary)
                showArrow -> NIcon(
                    NIcons.ChevronDown,
                    modifier = Modifier.rotate(if (active) 180f else 0f),
                    size = 16.dp,
                    color = if (disabled) theme.colors.iconDisabled else theme.colors.icon,
                )
            }
        }
    }
}

/** Tag list shown inside a multiple selection trigger. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun NSelectionTags(
    labels: List<Pair<Any, String>>,
    maxTagCount: Int?,
    size: NSize,
    disabled: Boolean,
    onRemove: (Any) -> Unit,
    trailing: (@Composable () -> Unit)? = null,
) {
    val shown = maxTagCount?.let { labels.take(it.coerceAtLeast(0)) } ?: labels
    val hidden = labels.size - shown.size
    val tagSize = if (size == NSize.Large) NSize.Medium else NSize.Small
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        shown.forEach { (key, label) ->
            NTag(
                text = label,
                size = tagSize,
                closable = !disabled,
                onClose = { onRemove(key) },
            )
        }
        if (hidden > 0) {
            NTag(text = "+$hidden", size = tagSize)
        }
        trailing?.invoke()
    }
}

/** Inline text input used by filterable selection triggers. */
@Composable
internal fun NSelectionFilterInput(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    size: NSize = NSize.Medium,
    enabled: Boolean = true,
    focusRequester: FocusRequester? = null,
    onFocus: () -> Unit = {},
    onKeyEvent: ((KeyEvent) -> Boolean)? = null,
    textColor: Color = LocalNTheme.current.colors.text1,
) {
    val theme = LocalNTheme.current
    val style = TextStyle(
        fontFamily = theme.typography.fontFamily,
        fontSize = theme.fontSizeFor(size),
        color = textColor,
    )
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .onFocusChanged { if (it.isFocused) onFocus() }
            .then(
                if (onKeyEvent != null) Modifier.onKeyEvent { onKeyEvent(it) }
                else Modifier,
            ),
        enabled = enabled,
        singleLine = true,
        textStyle = style,
        cursorBrush = SolidColor(theme.colors.primary),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.CenterStart) {
                if (query.isEmpty() && placeholder.isNotEmpty()) {
                    NText(
                        placeholder,
                        color = theme.colors.placeholder,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                innerTextField()
            }
        },
    )
}

/** Empty popup state shared by select-like components. */
@Composable
internal fun NSelectEmptyView(modifier: Modifier = Modifier, text: String? = null) {
    NEmpty(
        modifier = modifier.fillMaxWidth(),
        description = text ?: LocalNLocale.current.noData,
        size = NSize.Small,
    )
}

/** Hover-aware dropdown row used by select-like menus. */
@Composable
internal fun NSelectionMenuRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    active: Boolean = false,
    height: Dp = 34.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp),
    onHover: () -> Unit = {},
    content: @Composable RowScope.() -> Unit,
) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    LaunchedEffect(hovered) { if (hovered) onHover() }
    Row(
        modifier
            .fillMaxWidth()
            .height(height)
            .background(
                when {
                    active -> theme.colors.hover
                    hovered -> theme.colors.hover
                    else -> Color.Transparent
                },
            )
            .hoverable(interaction)
            .nClickable(interaction, enabled = enabled, onClick = onClick)
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}
