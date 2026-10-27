package cn.enaium.naiveui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import cn.enaium.naiveui.theme.nSize
import kotlin.math.roundToInt

/** A node of an [NTreeSelect]. */
@Immutable
data class NTreeSelectOption(
    val label: String,
    val key: Any,
    val children: List<NTreeSelectOption> = emptyList(),
    val disabled: Boolean = false,
    val checkbox: Boolean = true,
)

private data class NTreeRow(val option: NTreeSelectOption, val level: Int)

private fun NTreeSelectOption.descendants(): List<NTreeSelectOption> = buildList {
    children.forEach {
        add(it)
        addAll(it.descendants())
    }
}

private fun List<NTreeSelectOption>.collectRows(
    expanded: Set<Any>,
    forceExpand: Boolean,
    level: Int = 0,
    out: MutableList<NTreeRow> = mutableListOf(),
): List<NTreeRow> {
    forEach { node ->
        out.add(NTreeRow(node, level))
        if (node.children.isNotEmpty() && (forceExpand || node.key in expanded)) {
            node.children.collectRows(expanded, forceExpand, level + 1, out)
        }
    }
    return out
}

private fun List<NTreeSelectOption>.collectExpandableKeys(): Set<Any> = buildSet {
    fun visit(nodes: List<NTreeSelectOption>) {
        nodes.forEach { node ->
            if (node.children.isNotEmpty()) {
                add(node.key)
                visit(node.children)
            }
        }
    }
    visit(this@collectExpandableKeys)
}

private fun List<NTreeSelectOption>.indexPaths(): Map<Any, List<NTreeSelectOption>> = buildMap {
    fun visit(nodes: List<NTreeSelectOption>, prefix: List<NTreeSelectOption>) {
        nodes.forEach { node ->
            val path = prefix + node
            put(node.key, path)
            visit(node.children, path)
        }
    }
    visit(this@indexPaths, emptyList())
}

private fun List<NTreeSelectOption>.filterTree(query: String): List<NTreeSelectOption> =
    if (query.isBlank()) {
        this
    } else {
        mapNotNull { node ->
            val children = node.children.filterTree(query)
            if (node.label.contains(query, ignoreCase = true) || children.isNotEmpty()) {
                node.copy(children = children)
            } else {
                null
            }
        }
    }

/** Expands checked parent keys into all of their descendants. */
private fun List<NTreeSelectOption>.expandChecked(raw: Set<Any>): Set<Any> {
    if (raw.isEmpty()) return raw
    val result = raw.toMutableSet()
    fun markAll(node: NTreeSelectOption) {
        result.add(node.key)
        node.children.forEach(::markAll)
    }
    fun visit(node: NTreeSelectOption) {
        if (raw.contains(node.key)) markAll(node) else node.children.forEach(::visit)
    }
    forEach(::visit)
    return result
}

private fun isVisuallyChecked(
    node: NTreeSelectOption,
    checked: Set<Any>,
    cascade: Boolean,
): Boolean {
    if (!cascade || node.children.isEmpty()) return node.key in checked
    val active = node.children.filter { it.checkbox && !it.disabled }
    if (active.isEmpty()) return node.key in checked
    return active.all { isVisuallyChecked(it, checked, cascade) }
}

private fun hasCheckedDescendant(node: NTreeSelectOption, checked: Set<Any>): Boolean =
    node.children.any { isVisuallyChecked(it, checked, true) || hasCheckedDescendant(it, checked) }

/** Keeps cascade checks canonical: parents are checked only when all their children are. */
private fun List<NTreeSelectOption>.canonicalChecked(
    raw: Set<Any>,
    cascade: Boolean,
): List<Any> {
    if (!cascade) return raw.toList()
    val result = LinkedHashSet<Any>()
    fun visit(node: NTreeSelectOption): Boolean {
        val checked = if (node.children.isEmpty()) {
            node.key in raw
        } else {
            val active = node.children.filter { it.checkbox && !it.disabled }
            if (active.isEmpty()) node.key in raw else active.map { visit(it) }.all { it }
        }
        if (checked && !node.disabled) result.add(node.key)
        return checked
    }
    forEach { visit(it) }
    return result.toList()
}

/**
 * Select one or multiple keys from a tree, mirroring `<n-tree-select>`.
 * With [checkable] the value is always a `List<Any>` of checked keys.
 */
@Composable
fun NTreeSelect(
    options: List<NTreeSelectOption>,
    value: Any? = null,
    onValueChange: (Any?) -> Unit = {},
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    clearable: Boolean = false,
    disabled: Boolean = false,
    size: NSize? = null,
    multiple: Boolean = false,
    filterable: Boolean = false,
    checkable: Boolean = false,
    cascade: Boolean = true,
    defaultExpandAll: Boolean = false,
    defaultExpandedKeys: List<Any> = emptyList(),
    maxTagCount: Int? = null,
    showPath: Boolean = false,
    separator: String = " / ",
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
    var activeIndex by remember { mutableStateOf(-1) }
    var triggerWidth by remember { mutableStateOf(0) }
    val focusRequester = remember { FocusRequester() }
    val inputFocusRequester = remember { FocusRequester() }
    val menuScroll = rememberScrollState()

    val expandableKeys = remember(options) { options.collectExpandableKeys() }
    var expandedKeys by remember {
        mutableStateOf(
            buildSet {
                if (defaultExpandAll) addAll(expandableKeys)
                addAll(defaultExpandedKeys)
            },
        )
    }

    fun setShow(next: Boolean) {
        if (show == null) internalShow = next
        onUpdateShow(next)
    }

    val pathByKey = remember(options) { options.indexPaths() }

    fun labelOf(key: Any, withPath: Boolean = showPath): String {
        val path = pathByKey[key] ?: return key.toString()
        return if (withPath && path.size > 1) {
            path.joinToString(separator) { it.label }
        } else {
            path.last().label
        }
    }

    val rawValues: List<Any> = if (multiple || checkable) {
        (value as? List<*>)?.mapNotNull { it } ?: emptyList()
    } else {
        listOfNotNull(value)
    }
    val effectiveChecked = remember(rawValues, options, cascade) {
        if (cascade && checkable) options.expandChecked(rawValues.toSet()) else rawValues.toSet()
    }

    val filteredOptions = remember(options, query) { options.filterTree(query) }
    val rows = remember(filteredOptions, expandedKeys, query) {
        filteredOptions.collectRows(expandedKeys, forceExpand = query.isNotBlank())
    }
    val placeholderText = placeholder ?: locale.placeholder

    LaunchedEffect(expanded) {
        if (expanded) {
            val selectedIndex = rows.indexOfFirst { row ->
                !row.option.disabled && rawValues.contains(row.option.key)
            }
            activeIndex = if (selectedIndex >= 0) {
                selectedIndex
            } else {
                rows.indexOfFirst { !it.option.disabled }
            }
        } else {
            activeIndex = -1
            query = ""
        }
    }

    fun commitChecked(next: List<Any>) {
        onValueChange(if (multiple || checkable) next else next.firstOrNull())
    }

    fun toggleCheck(node: NTreeSelectOption) {
        if (node.disabled) return
        val next = effectiveChecked.toMutableSet()
        if (isVisuallyChecked(node, next, cascade)) {
            next.remove(node.key)
            node.descendants().forEach { next.remove(it.key) }
        } else {
            next.add(node.key)
            node.descendants().forEach { next.add(it.key) }
        }
        commitChecked(options.canonicalChecked(next, cascade))
    }

    fun toggleSelect(node: NTreeSelectOption) {
        if (node.disabled) return
        if (checkable) {
            toggleCheck(node)
        } else if (multiple) {
            val next = rawValues.toMutableList()
            if (next.contains(node.key)) next.remove(node.key) else next.add(node.key)
            commitChecked(next)
            query = ""
        } else {
            onValueChange(node.key)
            setShow(false)
        }
    }

    fun moveActive(delta: Int) {
        if (rows.isEmpty()) return
        var index = activeIndex
        repeat(rows.size) {
            index = when {
                index < 0 -> if (delta > 0) 0 else rows.lastIndex
                else -> (index + delta).let {
                    if (it < 0) rows.lastIndex else if (it > rows.lastIndex) 0 else it
                }
            }
            if (!rows[index].option.disabled) {
                activeIndex = index
                return
            }
        }
    }

    fun toggleExpand(node: NTreeSelectOption) {
        if (node.children.isEmpty()) return
        expandedKeys = if (node.key in expandedKeys) {
            expandedKeys - node.key
        } else {
            expandedKeys + node.key
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
                Key.DirectionRight -> {
                    rows.getOrNull(activeIndex)?.option?.let(::toggleExpand)
                    true
                }
                Key.DirectionLeft -> {
                    rows.getOrNull(activeIndex)?.option?.let { node ->
                        if (node.key in expandedKeys) toggleExpand(node)
                    }
                    true
                }
                Key.Enter -> {
                    if (expanded) {
                        rows.getOrNull(activeIndex)?.option?.let(::toggleSelect)
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

    val menuWidth = if (triggerWidth > 0) with(density) { triggerWidth.toDp() } else 200.dp
    val multiValue = multiple || checkable

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
            clearable = clearable && !disabled && rawValues.isNotEmpty(),
            onClear = {
                onValueChange(if (multiValue) emptyList<Any>() else null)
                query = ""
            },
            onClick = {
                if (!disabled) {
                    setShow(!expanded)
                    if (filterable) inputFocusRequester.requestFocus() else focusRequester.requestFocus()
                }
            },
        ) {
            if (multiValue) {
                if (rawValues.isEmpty() && !filterable) {
                    NText(
                        placeholderText,
                        color = theme.colors.placeholder,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                } else {
                    NSelectionTags(
                        labels = rawValues.map { it to labelOf(it) },
                        maxTagCount = maxTagCount,
                        size = resolvedSize,
                        disabled = disabled,
                        onRemove = { key ->
                            val removed = pathByKey[key]?.last()?.descendants()?.map { it.key }.orEmpty()
                            val next = rawValues.filterNot { it == key || it in removed }
                            if (checkable) {
                                commitChecked(options.canonicalChecked(next.toSet(), cascade))
                            } else {
                                commitChecked(next)
                            }
                        },
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
                val selectedKey = rawValues.firstOrNull()
                if (filterable) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        NSelectionFilterInput(
                            query = query,
                            onQueryChange = {
                                query = it
                                if (!expanded) setShow(true)
                            },
                            placeholder = selectedKey?.let { labelOf(it) } ?: placeholderText,
                            modifier = Modifier.weight(1f),
                            size = resolvedSize,
                            enabled = !disabled,
                            focusRequester = inputFocusRequester,
                            onFocus = { if (!disabled) setShow(true) },
                            onKeyEvent = keyHandler,
                            textColor = if (selectedKey != null) theme.colors.text1 else theme.colors.text2,
                        )
                    }
                } else {
                    NText(
                        selectedKey?.let { labelOf(it) } ?: placeholderText,
                        color = if (selectedKey != null) theme.colors.text1 else theme.colors.placeholder,
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
            modifier = Modifier.width(menuWidth),
        ) {
            if (loading) {
                Box(Modifier.fillMaxWidth().padding(vertical = 20.dp), Alignment.Center) {
                    NBaseLoading()
                }
            } else if (rows.isEmpty()) {
                NSelectEmptyView(Modifier.padding(vertical = 18.dp))
            } else {
                LaunchedEffect(activeIndex, expanded) {
                    if (expanded && activeIndex >= 0) {
                        val rowHeight = with(density) { nSelectionRowHeight(resolvedSize).toPx() }
                        val target = (activeIndex * rowHeight - with(density) { 4.dp.toPx() }).roundToInt()
                        menuScroll.animateScrollTo(target.coerceAtLeast(0))
                    }
                }
                NScrollbar(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 280.dp),
                    scrollState = menuScroll,
                ) {
                    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        rows.forEachIndexed { index, row ->
                            NTreeSelectRow(
                                row = row,
                                expanded = row.option.key in expandedKeys,
                                selected = rawValues.contains(row.option.key),
                                checked = checkable &&
                                    isVisuallyChecked(row.option, effectiveChecked, cascade),
                                indeterminate = checkable && cascade &&
                                    !isVisuallyChecked(row.option, effectiveChecked, cascade) &&
                                    hasCheckedDescendant(row.option, effectiveChecked),
                                checkable = checkable,
                                active = index == activeIndex,
                                size = resolvedSize,
                                onToggleExpand = { toggleExpand(row.option) },
                                onToggleCheck = { toggleCheck(row.option) },
                                onClick = { toggleSelect(row.option) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NTreeSelectRow(
    row: NTreeRow,
    expanded: Boolean,
    selected: Boolean,
    checked: Boolean,
    indeterminate: Boolean,
    checkable: Boolean,
    active: Boolean,
    size: NSize,
    onToggleExpand: () -> Unit,
    onToggleCheck: () -> Unit,
    onClick: () -> Unit,
) {
    val theme = LocalNTheme.current
    val node = row.option
    NSelectionMenuRow(
        onClick = onClick,
        enabled = !node.disabled,
        active = active,
        height = nSelectionRowHeight(size),
        contentPadding = PaddingValues(start = 8.dp, end = 12.dp),
    ) {
        Spacer(Modifier.width((row.level * 14).dp))
        if (node.children.isNotEmpty()) {
            val chevronInteraction = remember { MutableInteractionSource() }
            Box(
                Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .nClickable(chevronInteraction) { onToggleExpand() },
                contentAlignment = Alignment.Center,
            ) {
                NIcon(
                    NIcons.ChevronRight,
                    modifier = Modifier.rotate(if (expanded) 90f else 0f),
                    size = 14.dp,
                    color = if (node.disabled) theme.colors.iconDisabled else theme.colors.icon,
                )
            }
        } else {
            Spacer(Modifier.width(20.dp))
        }
        if (checkable && node.checkbox) {
            NCheckbox(
                checked = checked,
                indeterminate = indeterminate,
                disabled = node.disabled,
                size = size,
                onCheckedChange = { onToggleCheck() },
            )
            Spacer(Modifier.width(8.dp))
        }
        NText(
            node.label,
            modifier = Modifier.weight(1f),
            color = when {
                node.disabled -> theme.colors.textDisabled
                selected -> theme.colors.primary
                else -> theme.colors.text2
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (selected && !checkable) {
            Spacer(Modifier.width(8.dp))
            NIcon(NIcons.Checkmark, size = 16.dp, color = theme.colors.primary)
        }
    }
}
