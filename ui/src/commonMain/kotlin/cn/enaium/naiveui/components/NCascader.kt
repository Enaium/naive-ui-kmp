package cn.enaium.naiveui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NBaseLoading
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NPopoverSurface
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import cn.enaium.naiveui.theme.nSize

/** A node of an [NCascader]. */
@Immutable
data class NCascaderOption(
    val label: String,
    val value: Any,
    val children: List<NCascaderOption> = emptyList(),
    val disabled: Boolean = false,
)

/** Whether a column opens on hover or on click. */
enum class NCascaderExpandTrigger { Click, Hover }

private data class NCascaderMatch(
    val option: NCascaderOption,
    val path: List<NCascaderOption>,
)

private fun NCascaderOption.descendants(): List<NCascaderOption> = buildList {
    children.forEach {
        add(it)
        addAll(it.descendants())
    }
}

private fun List<NCascaderOption>.indexPaths(): Map<Any, List<NCascaderOption>> = buildMap {
    fun visit(nodes: List<NCascaderOption>, prefix: List<NCascaderOption>) {
        nodes.forEach { node ->
            val path = prefix + node
            put(node.value, path)
            visit(node.children, path)
        }
    }
    visit(this@indexPaths, emptyList())
}

private fun List<NCascaderOption>.expandCascaderChecked(raw: Set<Any>): Set<Any> {
    if (raw.isEmpty()) return raw
    val result = raw.toMutableSet()
    fun markAll(node: NCascaderOption) {
        result.add(node.value)
        node.children.forEach(::markAll)
    }
    fun visit(node: NCascaderOption) {
        if (raw.contains(node.value)) markAll(node) else node.children.forEach(::visit)
    }
    forEach(::visit)
    return result
}

private fun isCascaderChecked(
    node: NCascaderOption,
    checked: Set<Any>,
    cascade: Boolean,
): Boolean {
    if (!cascade || node.children.isEmpty()) return node.value in checked
    val active = node.children.filter { !it.disabled }
    if (active.isEmpty()) return node.value in checked
    return active.all { isCascaderChecked(it, checked, cascade) }
}

private fun hasCascaderCheckedDescendant(node: NCascaderOption, checked: Set<Any>): Boolean =
    node.children.any {
        isCascaderChecked(it, checked, true) || hasCascaderCheckedDescendant(it, checked)
    }

private fun List<NCascaderOption>.canonicalCascaderChecked(raw: Set<Any>): List<Any> {
    val result = LinkedHashSet<Any>()
    fun visit(node: NCascaderOption): Boolean {
        val checked = if (node.children.isEmpty()) {
            node.value in raw
        } else {
            val active = node.children.filter { !it.disabled }
            if (active.isEmpty()) node.value in raw else active.map { visit(it) }.all { it }
        }
        if (checked && !node.disabled) result.add(node.value)
        return checked
    }
    forEach { visit(it) }
    return result.toList()
}

private fun List<NCascaderOption>.matchingPaths(
    query: String,
    prefix: List<NCascaderOption> = emptyList(),
): List<NCascaderMatch> = buildList {
    this@matchingPaths.forEach { node ->
        val path = prefix + node
        if (node.label.contains(query, ignoreCase = true)) add(NCascaderMatch(node, path))
        addAll(node.children.matchingPaths(query, path))
    }
}

/**
 * Select a value by walking through columns of a tree, mirroring
 * `<n-cascader>`. Single mode reports the leaf value; multiple/checkable mode
 * reports a `List<Any>` of checked values.
 */
@Composable
fun NCascader(
    options: List<NCascaderOption>,
    value: Any? = null,
    onValueChange: (Any?) -> Unit = {},
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    clearable: Boolean = false,
    disabled: Boolean = false,
    size: NSize? = null,
    multiple: Boolean = false,
    checkable: Boolean = false,
    filterable: Boolean = false,
    separator: String = " / ",
    expandTrigger: NCascaderExpandTrigger = NCascaderExpandTrigger.Click,
    cascade: Boolean = true,
    showPath: Boolean = true,
    changeOnSelect: Boolean = false,
    maxTagCount: Int? = null,
    status: NStatus? = null,
    loading: Boolean = false,
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
    var activePath by remember { mutableStateOf<List<NCascaderOption>>(emptyList()) }
    var activeIndex by remember { mutableStateOf(-1) }
    var triggerWidth by remember { mutableStateOf(0) }
    val focusRequester = remember { FocusRequester() }
    val inputFocusRequester = remember { FocusRequester() }
    val columnsScroll = rememberScrollState()

    fun setShow(next: Boolean) {
        if (show == null) internalShow = next
        onUpdateShow(next)
    }

    val multiMode = multiple || checkable
    val pathByValue = remember(options) { options.indexPaths() }
    val rawValues: List<Any> = if (multiMode) {
        (value as? List<*>)?.mapNotNull { it } ?: emptyList()
    } else {
        listOfNotNull((value as? List<*>)?.lastOrNull() ?: value)
    }
    val singleValue: Any? = rawValues.firstOrNull()
    val effectiveChecked = remember(rawValues, options, cascade) {
        if (multiMode && cascade) {
            options.expandCascaderChecked(rawValues.toSet())
        } else {
            rawValues.toSet()
        }
    }

    fun labelFor(key: Any): String {
        val path = pathByValue[key] ?: return key.toString()
        return if (showPath && path.size > 1) {
            path.joinToString(separator) { it.label }
        } else {
            path.last().label
        }
    }

    val columns = remember(options, activePath) {
        buildList {
            add(options)
            activePath.forEach { add(it.children) }
        }
    }
    val activeColumn = columns.lastOrNull().orEmpty()
    val placeholderText = placeholder ?: locale.placeholder

    LaunchedEffect(expanded) {
        if (expanded) {
            val initialPath = when {
                multiMode -> rawValues.firstOrNull()?.let { pathByValue[it] }
                else -> singleValue?.let { pathByValue[it] }
            }
            val parentPath = initialPath?.dropLast(1)?.filter { it.children.isNotEmpty() }.orEmpty()
            activePath = parentPath
            val siblings = parentPath.lastOrNull()?.children ?: options
            val last = initialPath?.lastOrNull()
            val selectedIndex = if (last != null) {
                siblings.indexOfFirst { it.value == last.value }
            } else {
                -1
            }
            activeIndex = if (selectedIndex >= 0) {
                selectedIndex
            } else {
                siblings.indexOfFirst { !it.disabled }
            }
        } else {
            query = ""
            activeIndex = -1
        }
    }

    fun emitChecked(next: Set<Any>) {
        onValueChange(
            if (cascade) options.canonicalCascaderChecked(next)
            else next.toList(),
        )
    }

    fun toggleCheck(option: NCascaderOption) {
        if (option.disabled) return
        val next = (if (cascade) effectiveChecked else rawValues.toSet()).toMutableSet()
        if (isCascaderChecked(option, next, cascade)) {
            next.remove(option.value)
            option.descendants().forEach { next.remove(it.value) }
        } else {
            next.add(option.value)
            option.descendants().forEach { if (!it.disabled) next.add(it.value) }
        }
        emitChecked(next)
        if (multiMode) query = ""
    }

    fun selectLeaf(option: NCascaderOption) {
        if (option.disabled) return
        if (multiMode) {
            toggleCheck(option)
        } else {
            onValueChange(option.value)
            setShow(false)
        }
    }

    fun activate(option: NCascaderOption, level: Int) {
        if (option.disabled) return
        if (option.children.isNotEmpty()) {
            activePath = activePath.take(level) + option
            activeIndex = 0
            if (!multiMode && changeOnSelect) onValueChange(option.value)
        } else {
            selectLeaf(option)
        }
    }

    fun moveColumnActive(delta: Int) {
        if (activeColumn.isEmpty()) return
        var index = activeIndex
        repeat(activeColumn.size) {
            index = when {
                index < 0 -> if (delta > 0) 0 else activeColumn.lastIndex
                else -> (index + delta).let {
                    if (it < 0) activeColumn.lastIndex else if (it > activeColumn.lastIndex) 0 else it
                }
            }
            if (!activeColumn[index].disabled) {
                activeIndex = index
                return
            }
        }
    }

    fun removeValue(key: Any) {
        if (!multiMode) {
            onValueChange(null)
            return
        }
        val removed = pathByValue[key]?.last()?.descendants()?.map { it.value }.orEmpty()
        val next = rawValues.filterNot { it == key || it in removed }
        if (cascade) {
            onValueChange(options.canonicalCascaderChecked(next.toSet()))
        } else {
            onValueChange(next)
        }
    }

    val keyHandler: (KeyEvent) -> Boolean = { event ->
        if (event.type != KeyEventType.KeyDown) {
            false
        } else {
            when (event.key) {
                Key.DirectionDown -> {
                    if (expanded) moveColumnActive(1) else setShow(true)
                    true
                }
                Key.DirectionUp -> {
                    if (expanded) moveColumnActive(-1) else setShow(true)
                    true
                }
                Key.DirectionRight -> {
                    activeColumn.getOrNull(activeIndex)?.let { activate(it, columns.lastIndex) }
                    true
                }
                Key.DirectionLeft -> {
                    if (activePath.isNotEmpty()) {
                        activePath = activePath.dropLast(1)
                        activeIndex = 0
                    }
                    true
                }
                Key.Enter -> {
                    if (expanded) {
                        activeColumn.getOrNull(activeIndex)?.let { activate(it, columns.lastIndex) }
                    } else {
                        setShow(true)
                    }
                    true
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

    val matches = remember(options, query) {
        if (query.isBlank()) emptyList() else options.matchingPaths(query)
    }
    val hasValue = if (multiMode) rawValues.isNotEmpty() else singleValue != null
    val menuWidth = if (triggerWidth > 0) with(density) { triggerWidth.toDp() } else 200.dp

    Box(
        modifier
            .defaultMinSize(minWidth = 180.dp)
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
            clearable = clearable && !disabled && hasValue,
            onClear = {
                onValueChange(if (multiMode) emptyList<Any>() else null)
                query = ""
            },
            onClick = {
                if (!disabled) {
                    setShow(!expanded)
                    if (filterable) inputFocusRequester.requestFocus() else focusRequester.requestFocus()
                }
            },
        ) {
            if (multiMode) {
                if (rawValues.isEmpty() && !filterable) {
                    NText(
                        placeholderText,
                        color = theme.colors.placeholder,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                } else {
                    NSelectionTags(
                        labels = rawValues.map { it to labelFor(it) },
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
                                    placeholder = if (rawValues.isEmpty()) placeholderText else "",
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
                if (filterable) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        NSelectionFilterInput(
                            query = query,
                            onQueryChange = {
                                query = it
                                if (!expanded) setShow(true)
                            },
                            placeholder = singleValue?.let { labelFor(it) } ?: placeholderText,
                            modifier = Modifier.weight(1f),
                            size = resolvedSize,
                            enabled = !disabled,
                            focusRequester = inputFocusRequester,
                            onFocus = { if (!disabled) setShow(true) },
                            onKeyEvent = keyHandler,
                            textColor = if (singleValue != null) theme.colors.text1 else theme.colors.text2,
                        )
                    }
                } else {
                    NText(
                        singleValue?.let { labelFor(it) } ?: placeholderText,
                        color = if (singleValue != null) theme.colors.text1 else theme.colors.placeholder,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
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
            modifier = Modifier.widthIn(min = menuWidth),
        ) {
            if (loading) {
                Box(Modifier.fillMaxWidth().padding(vertical = 20.dp), Alignment.Center) {
                    NBaseLoading()
                }
            } else if (options.isEmpty()) {
                NSelectEmptyView(Modifier.padding(vertical = 18.dp))
            } else if (query.isNotBlank()) {
                if (matches.isEmpty()) {
                    NSelectEmptyView(Modifier.padding(vertical = 18.dp))
                } else {
                    NScrollbar(
                        modifier = Modifier.width(menuWidth).heightIn(max = 280.dp),
                    ) {
                        Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            matches.forEach { match ->
                                NCascaderMatchRow(
                                    match = match,
                                    checked = multiMode && isCascaderChecked(
                                        match.option,
                                        effectiveChecked,
                                        cascade,
                                    ),
                                    selected = !multiMode && match.option.value == singleValue,
                                    size = resolvedSize,
                                    onSelect = {
                                        query = ""
                                        if (multiMode) {
                                            toggleCheck(match.option)
                                        } else if (match.option.children.isNotEmpty() && !changeOnSelect) {
                                            activePath = match.path
                                        } else {
                                            onValueChange(match.option.value)
                                            setShow(false)
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            } else {
                Row(
                    Modifier
                        .widthIn(max = 520.dp)
                        .horizontalScroll(columnsScroll),
                ) {
                    columns.forEachIndexed { level, columnOptions ->
                        NScrollbar(
                            modifier = Modifier
                                .width(150.dp)
                                .heightIn(max = 280.dp),
                        ) {
                            Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                columnOptions.forEachIndexed { index, option ->
                                    val inActivePath = activePath.getOrNull(level) == option
                                    NCascaderColumnRow(
                                        option = option,
                                        active = inActivePath || (level == columns.lastIndex && index == activeIndex),
                                        checked = multiMode && isCascaderChecked(
                                            option,
                                            effectiveChecked,
                                            cascade,
                                        ),
                                        indeterminate = multiMode && cascade &&
                                            !isCascaderChecked(option, effectiveChecked, cascade) &&
                                            hasCascaderCheckedDescendant(option, effectiveChecked),
                                        selected = !multiMode && option.value == singleValue,
                                        multiMode = multiMode,
                                        expandOnHover = expandTrigger == NCascaderExpandTrigger.Hover,
                                        size = resolvedSize,
                                        onHover = {
                                            if (option.children.isNotEmpty() && option != activePath.getOrNull(level)) {
                                                activePath = activePath.take(level) + option
                                                activeIndex = 0
                                            }
                                        },
                                        onCheck = { toggleCheck(option) },
                                        onClick = { activate(option, level) },
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

@Composable
private fun NCascaderColumnRow(
    option: NCascaderOption,
    active: Boolean,
    checked: Boolean,
    indeterminate: Boolean,
    selected: Boolean,
    multiMode: Boolean,
    expandOnHover: Boolean,
    size: NSize,
    onHover: () -> Unit,
    onCheck: () -> Unit,
    onClick: () -> Unit,
) {
    val theme = LocalNTheme.current
    NSelectionMenuRow(
        onClick = onClick,
        enabled = !option.disabled,
        active = active,
        height = nSelectionRowHeight(size),
        contentPadding = PaddingValues(horizontal = 8.dp),
        onHover = { if (expandOnHover) onHover() },
    ) {
        if (multiMode) {
            NCheckbox(
                checked = checked,
                indeterminate = indeterminate,
                disabled = option.disabled,
                size = size,
                onCheckedChange = { onCheck() },
            )
            Spacer(Modifier.width(6.dp))
        }
        NText(
            option.label,
            modifier = Modifier.weight(1f),
            color = when {
                option.disabled -> theme.colors.textDisabled
                checked || selected -> theme.colors.primary
                else -> theme.colors.text2
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        when {
            option.children.isNotEmpty() -> NIcon(
                NIcons.ChevronRight,
                size = 14.dp,
                color = if (option.disabled) theme.colors.iconDisabled else theme.colors.icon,
            )
            checked || selected -> NIcon(NIcons.Checkmark, size = 14.dp, color = theme.colors.primary)
        }
    }
}

@Composable
private fun NCascaderMatchRow(
    match: NCascaderMatch,
    checked: Boolean,
    selected: Boolean,
    size: NSize,
    onSelect: () -> Unit,
) {
    val theme = LocalNTheme.current
    NSelectionMenuRow(
        onClick = onSelect,
        enabled = !match.option.disabled,
        height = nSelectionRowHeight(size) + 8.dp,
        contentPadding = PaddingValues(horizontal = 10.dp),
    ) {
        if (checked) {
            NIcon(NIcons.Checkmark, size = 14.dp, color = theme.colors.primary)
            Spacer(Modifier.width(6.dp))
        }
        Column(Modifier.weight(1f)) {
            if (match.path.size > 1) {
                NText(
                    match.path.dropLast(1).joinToString(" / ") { it.label },
                    size = theme.typography.fontSize.mini,
                    color = theme.colors.text3,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            NText(
                match.option.label,
                color = when {
                    match.option.disabled -> theme.colors.textDisabled
                    checked || selected -> theme.colors.primary
                    else -> theme.colors.text2
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (match.option.children.isNotEmpty()) {
            NIcon(NIcons.ChevronRight, size = 14.dp, color = theme.colors.icon)
        }
    }
}
