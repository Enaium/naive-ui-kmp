package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NBaseLoading
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.NVirtualList
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nBottomBorder
import cn.enaium.naiveui.internal.nTopBorder
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.theme.LocalNTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * A node of [NTree].
 *
 * [prefix] and [suffix] are composable slots rendered around the label.
 * A node whose [isLeaf] is `false` and whose [children] is `null` is treated as
 * an unloaded node and is filled through [NTree]'s `onLoad` callback.
 */
data class NTreeOption(
    val label: String,
    val key: String,
    val children: List<NTreeOption>? = null,
    val disabled: Boolean = false,
    val isLeaf: Boolean? = null,
    val prefix: (@Composable () -> Unit)? = null,
    val suffix: (@Composable () -> Unit)? = null,
)

/** Where a dragged tree node is dropped relative to the hovered node. */
enum class NTreeDropPosition { Before, Inside, After }

/** Information reported by [NTree.onDrop] after a drag-and-drop gesture. */
data class NTreeDropInfo(
    val dragNode: NTreeOption,
    val node: NTreeOption,
    val dropPosition: NTreeDropPosition,
)

/**
 * Tree with expandable nodes, selection, cascade checking and async loading.
 *
 * Mirrors `<n-tree>` from naive-ui. Selection, expansion and checking may be
 * controlled by passing the matching `keys` parameter; otherwise the component
 * keeps the state internally and starts from the `default*` values.
 */
@Composable
fun NTree(
    data: List<NTreeOption>,
    modifier: Modifier = Modifier,
    height: Dp? = null,
    selectedKeys: List<String>? = null,
    defaultSelectedKeys: List<String> = emptyList(),
    expandedKeys: List<String>? = null,
    defaultExpandedKeys: List<String> = emptyList(),
    checkedKeys: List<String>? = null,
    defaultCheckedKeys: List<String> = emptyList(),
    onUpdateSelectedKeys: ((List<String>) -> Unit)? = null,
    onUpdateExpandedKeys: ((List<String>) -> Unit)? = null,
    onUpdateCheckedKeys: ((List<String>) -> Unit)? = null,
    selectable: Boolean = true,
    checkable: Boolean = false,
    cascade: Boolean = false,
    multiple: Boolean = false,
    blockLine: Boolean = false,
    showLine: Boolean = false,
    indent: Dp = 24.dp,
    expandOnClick: Boolean = false,
    defaultExpandAll: Boolean = false,
    virtualScroll: Boolean = false,
    draggable: Boolean = false,
    disabled: Boolean = false,
    onLoad: (suspend (NTreeOption) -> List<NTreeOption>?)? = null,
    onDrop: ((NTreeDropInfo) -> Unit)? = null,
    renderLabel: (@Composable (NTreeOption, Boolean, Boolean) -> Unit)? = null,
    renderSuffix: (@Composable (NTreeOption, Boolean, Boolean) -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    val theme = LocalNTheme.current
    val density = LocalDensity.current
    var draggingKey by remember(data) { mutableStateOf<String?>(null) }
    var dropTargetKey by remember(data) { mutableStateOf<String?>(null) }
    var dropPosition by remember(data) { mutableStateOf(NTreeDropPosition.After) }
    val loadedChildren = remember(data) { mutableStateMapOf<String, List<NTreeOption>>() }
    val loadingKeys = remember(data) { mutableStateMapOf<String, Boolean>() }

    var internalSelected by remember(data) { mutableStateOf(defaultSelectedKeys) }
    var internalExpanded by remember(data, defaultExpandAll) {
        mutableStateOf(
            if (defaultExpandAll) collectExpandableKeys(data).toSet() else defaultExpandedKeys.toSet(),
        )
    }
    var internalChecked by remember(data) { mutableStateOf(defaultCheckedKeys) }

    val mergedSelected = selectedKeys ?: internalSelected
    val mergedExpanded = expandedKeys?.toSet() ?: internalExpanded
    val mergedChecked = checkedKeys ?: internalChecked
    val checkedSet = mergedChecked.toSet()
    val selectedSet = mergedSelected.toSet()

    fun updateSelected(keys: List<String>) {
        if (selectedKeys == null) internalSelected = keys
        onUpdateSelectedKeys?.invoke(keys)
    }

    fun updateExpanded(keys: Set<String>) {
        if (expandedKeys == null) internalExpanded = keys
        onUpdateExpandedKeys?.invoke(keys.toList())
    }

    fun updateChecked(keys: List<String>) {
        if (checkedKeys == null) internalChecked = keys
        onUpdateCheckedKeys?.invoke(keys)
    }

    val childMap = HashMap<String, List<NTreeOption>>()
    val parentMap = HashMap<String, String?>()
    fun collect(nodes: List<NTreeOption>, parent: String?) {
        nodes.forEach { node ->
            parentMap[node.key] = parent
            val children = loadedChildren[node.key] ?: node.children.orEmpty()
            childMap[node.key] = children
            collect(children, node.key)
        }
    }
    collect(data, null)

    fun subtreeKeys(node: NTreeOption): List<String> =
        listOf(node.key) + childMap[node.key].orEmpty().flatMap { subtreeKeys(it) }

    fun selectNode(option: NTreeOption) {
        if (!selectable || disabled || option.disabled) return
        val next = if (multiple) {
            if (option.key in selectedSet) mergedSelected - option.key else mergedSelected + option.key
        } else {
            if (option.key in selectedSet) mergedSelected else listOf(option.key)
        }
        updateSelected(next)
    }

    fun toggleCheck(option: NTreeOption) {
        if (disabled || option.disabled) return
        val target = option.key !in checkedSet
        val next = checkedSet.toMutableSet()
        if (cascade) {
            val subtree = subtreeKeys(option)
            if (target) next.addAll(subtree) else subtree.forEach { next.remove(it) }
            var parent = parentMap[option.key]
            while (parent != null) {
                val siblings = childMap[parent].orEmpty()
                if (siblings.isNotEmpty() && siblings.all { it.key in next }) {
                    next.add(parent)
                } else {
                    next.remove(parent)
                }
                parent = parentMap[parent]
            }
        } else if (target) {
            next.add(option.key)
        } else {
            next.remove(option.key)
        }
        updateChecked(next.toList())
    }

    fun toggleExpand(option: NTreeOption) {
        if (disabled || option.disabled) return
        val current = mergedExpanded
        if (option.key in current) {
            updateExpanded(current - option.key)
            return
        }
        val children = childMap[option.key].orEmpty()
        val unloaded = option.isLeaf == false && children.isEmpty() && !loadedChildren.containsKey(option.key)
        if (unloaded) {
            val loader = onLoad ?: return
            if (loadingKeys.containsKey(option.key)) return
            loadingKeys[option.key] = true
            scope.launch {
                try {
                    val loaded = loader(option)
                    if (loaded != null) {
                        loadedChildren[option.key] = loaded
                        if (loaded.isNotEmpty()) updateExpanded((expandedKeys?.toSet() ?: internalExpanded) + option.key)
                    }
                } finally {
                    loadingKeys.remove(option.key)
                }
            }
            return
        }
        updateExpanded(current + option.key)
    }

    val indeterminateKeys = if (checkable && cascade) {
        val result = HashSet<String>()
        fun visit(node: NTreeOption): Boolean {
            val children = childMap[node.key].orEmpty()
            var anyChecked = false
            children.forEach { child ->
                val childChecked = child.key in checkedSet
                val descendantChecked = visit(child)
                if (childChecked || descendantChecked) anyChecked = true
            }
            if (anyChecked && node.key !in checkedSet) result.add(node.key)
            return anyChecked
        }
        data.forEach { visit(it) }
        result
    } else {
        emptySet()
    }

    fun flatten(
        nodes: List<NTreeOption>,
        pathLast: List<Boolean>,
    ): List<NTreeRowData> {
        val result = ArrayList<NTreeRowData>()
        nodes.forEachIndexed { index, node ->
            val children = childMap[node.key].orEmpty()
            val isLast = index == nodes.lastIndex
            val loaded = loadedChildren.containsKey(node.key)
            val expandable = node.isLeaf != true &&
                (children.isNotEmpty() || (node.isLeaf == false && !loaded))
            val expanded = node.key in mergedExpanded && children.isNotEmpty()
            result.add(
                NTreeRowData(
                    option = node,
                    pathLast = pathLast,
                    isLeaf = !expandable,
                    expandable = expandable,
                    expanded = expanded,
                ),
            )
            if (expanded) {
                result.addAll(flatten(children, pathLast + isLast))
            }
        }
        return result
    }

    val rows = flatten(data, emptyList())
    val rowHeightPx = with(density) { 34.dp.toPx() }

    fun clearDrag() {
        draggingKey = null
        dropTargetKey = null
    }

    val renderRow: @Composable (NTreeRowData) -> Unit = { row ->
        val option = row.option
        val nodeDisabled = disabled || option.disabled
        val rowIndex = rows.indexOf(row)
        val dragging = draggingKey == option.key
        val dropTarget = dropTargetKey == option.key
        val dragModifier = if (draggable && !nodeDisabled) {
            Modifier.pointerInput(option.key, rows.size) {
                var accumulated = 0f
                detectDragGestures(
                    onDragStart = {
                        if (onDrop != null) {
                            draggingKey = option.key
                            dropTargetKey = option.key
                            dropPosition = NTreeDropPosition.After
                            accumulated = 0f
                        }
                    },
                    onDrag = { change, amount ->
                        if (onDrop == null || draggingKey == null) return@detectDragGestures
                        change.consume()
                        accumulated += amount.y
                        val delta = (accumulated / rowHeightPx).roundToInt()
                        val targetIndex = (rowIndex + delta).coerceIn(0, rows.lastIndex)
                        val target = rows[targetIndex].option
                        if (target.key == draggingKey) {
                            dropTargetKey = null
                        } else {
                            dropTargetKey = target.key
                            dropPosition = when {
                                rows[targetIndex].expandable && targetIndex != rowIndex -> NTreeDropPosition.Inside
                                targetIndex > rowIndex -> NTreeDropPosition.After
                                else -> NTreeDropPosition.Before
                            }
                        }
                    },
                    onDragEnd = {
                        val dragNode = rows.firstOrNull { it.option.key == draggingKey }?.option
                        val target = rows.firstOrNull { it.option.key == dropTargetKey }?.option
                        if (dragNode != null && target != null && target.key != dragNode.key &&
                            target.key !in subtreeKeys(dragNode) && onDrop != null
                        ) {
                            onDrop.invoke(NTreeDropInfo(dragNode, target, dropPosition))
                        }
                        clearDrag()
                    },
                    onDragCancel = { clearDrag() },
                )
            }
        } else {
            Modifier
        }
        Box(
            dragModifier
                .then(if (dragging) Modifier.nBorder(theme.colors.primary, 1.dp, theme.borderRadiusSmall) else Modifier)
                .then(
                    if (dropTarget && !dragging) {
                        when (dropPosition) {
                            NTreeDropPosition.Before -> Modifier.nTopBorder(theme.colors.primary, 2.dp)
                            NTreeDropPosition.After -> Modifier.nBottomBorder(theme.colors.primary, 2.dp)
                            NTreeDropPosition.Inside -> Modifier.nBorder(theme.colors.primary, 1.dp, theme.borderRadiusSmall)
                        }
                    } else {
                        Modifier
                    },
                ),
        ) {
            NTreeRow(
                option = option,
                pathLast = row.pathLast,
                isLeaf = row.isLeaf,
                expandable = row.expandable,
                expanded = row.expanded,
                loading = loadingKeys.containsKey(option.key),
                selected = option.key in selectedSet,
                checked = option.key in checkedSet,
                indeterminate = option.key in indeterminateKeys,
                nodeDisabled = nodeDisabled,
                blockLine = blockLine,
                showLine = showLine,
                indent = indent,
                checkable = checkable,
                onToggleExpand = { toggleExpand(option) },
                onToggleCheck = { toggleCheck(option) },
                onSelect = { selectNode(option) },
                expandOnClick = expandOnClick,
                renderLabel = renderLabel,
                renderSuffix = renderSuffix,
            )
        }
    }

    Box(modifier) {
        when {
            virtualScroll -> {
                NVirtualList(
                    rows,
                    Modifier.fillMaxWidth().height(height ?: 300.dp),
                    itemHeight = 34.dp,
                ) { row -> renderRow(row) }
            }
            height != null -> {
                NScrollbar(Modifier.fillMaxWidth().height(height)) {
                    Column(Modifier.fillMaxWidth()) {
                        rows.forEach { row -> renderRow(row) }
                    }
                }
            }
            else -> {
                Column(Modifier.fillMaxWidth()) {
                    rows.forEach { row -> renderRow(row) }
                }
            }
        }
    }
}

private data class NTreeRowData(
    val option: NTreeOption,
    val pathLast: List<Boolean>,
    val isLeaf: Boolean,
    val expandable: Boolean,
    val expanded: Boolean,
)

@Composable
private fun NTreeRow(
    option: NTreeOption,
    pathLast: List<Boolean>,
    isLeaf: Boolean,
    expandable: Boolean,
    expanded: Boolean,
    loading: Boolean,
    selected: Boolean,
    checked: Boolean,
    indeterminate: Boolean,
    nodeDisabled: Boolean,
    blockLine: Boolean,
    showLine: Boolean,
    indent: Dp,
    checkable: Boolean,
    onToggleExpand: () -> Unit,
    onToggleCheck: () -> Unit,
    onSelect: () -> Unit,
    expandOnClick: Boolean,
    renderLabel: (@Composable (NTreeOption, Boolean, Boolean) -> Unit)?,
    renderSuffix: (@Composable (NTreeOption, Boolean, Boolean) -> Unit)?,
) {
    val theme = LocalNTheme.current
    val rowInteraction = remember { MutableInteractionSource() }
    val rowHovered by rowInteraction.collectIsHoveredAsState()
    val contentInteraction = remember { MutableInteractionSource() }
    val contentHovered by contentInteraction.collectIsHoveredAsState()
    val activeColor = theme.colors.primary.copy(alpha = if (theme.isDark) 0.15f else 0.1f)
    val hoverColor = theme.colors.hover
    val showHover = !nodeDisabled && ((blockLine && rowHovered) || (!blockLine && contentHovered))
    val rowBackground = when {
        selected -> activeColor
        showHover -> hoverColor
        else -> Color.Transparent
    }
    val shape = RoundedCornerShape(theme.borderRadiusSmall)
    val textColor = when {
        nodeDisabled -> theme.colors.textDisabled
        selected -> theme.colors.primary
        else -> theme.colors.text2
    }
    val textStyle = theme.typography.body.copy(color = textColor)
    val onClick = {
        if (!nodeDisabled) {
            onSelect()
            if (expandOnClick && expandable) onToggleExpand()
        }
    }

    Row(
        Modifier
            .fillMaxWidth()
            .height(34.dp)
            .clip(shape)
            .then(if (blockLine) Modifier.background(rowBackground) else Modifier)
            .hoverable(rowInteraction)
            .then(
                if (blockLine) {
                    Modifier.nClickable(rowInteraction, enabled = !nodeDisabled, onClick = onClick)
                } else {
                    Modifier
                },
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        pathLast.forEachIndexed { index, ancestorLast ->
            NTreeIndent(
                indent = indent,
                color = theme.colors.divider,
                ancestorLast = ancestorLast,
                leafBranch = showLine && isLeaf && index == pathLast.lastIndex,
            )
        }
        Box(Modifier.width(20.dp).fillMaxHeight(), contentAlignment = Alignment.Center) {
            when {
                loading -> NBaseLoading(size = 14.dp, strokeWidth = 1.5.dp)
                expandable -> Box(
                    Modifier
                        .size(18.dp)
                        .clip(RoundedCornerShape(theme.borderRadiusSmall))
                        .nClickable(rowInteraction, enabled = !nodeDisabled, onClick = onToggleExpand),
                    contentAlignment = Alignment.Center,
                ) {
                    NIcon(
                        NIcons.ChevronRight,
                        Modifier.rotate(if (expanded) 90f else 0f),
                        size = 14.dp,
                        color = if (selected) theme.colors.primary else theme.colors.icon,
                    )
                }
            }
        }
        if (checkable) {
            NTreeCheckbox(
                checked = checked,
                indeterminate = indeterminate,
                disabled = nodeDisabled,
                onCheckedChange = onToggleCheck,
            )
            Spacer(Modifier.width(8.dp))
        }
        Box(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(vertical = 2.dp)
                .clip(shape)
                .then(if (!blockLine) Modifier.background(rowBackground) else Modifier)
                .hoverable(contentInteraction)
                .then(
                    if (!blockLine) {
                        Modifier.nClickable(contentInteraction, enabled = !nodeDisabled, onClick = onClick)
                    } else {
                        Modifier
                    },
                )
                .padding(horizontal = 6.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val prefix = option.prefix
                if (prefix != null) {
                    Box(Modifier.padding(end = 8.dp)) { prefix() }
                }
                Box(Modifier.weight(1f, fill = false)) {
                    if (renderLabel != null) {
                        renderLabel(option, selected, checked)
                    } else {
                        BasicText(
                            text = option.label,
                            style = textStyle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                val suffix = option.suffix
                if (renderSuffix != null) {
                    Spacer(Modifier.width(8.dp))
                    renderSuffix(option, selected, checked)
                } else if (suffix != null) {
                    Spacer(Modifier.width(8.dp))
                    suffix()
                }
            }
        }
    }
}

@Composable
private fun NTreeIndent(
    indent: Dp,
    color: Color,
    ancestorLast: Boolean,
    leafBranch: Boolean,
) {
    if (indent <= 0.dp) return
    Box(
        Modifier
            .width(indent)
            .fillMaxHeight()
            .drawBehind {
                val x = size.width / 2f
                val stroke = 1.dp.toPx()
                drawLine(
                    color = color,
                    start = Offset(x, 0f),
                    end = Offset(x, if (ancestorLast) size.height / 2f else size.height),
                    strokeWidth = stroke,
                )
                if (leafBranch) {
                    drawLine(
                        color = color,
                        start = Offset(x, size.height / 2f),
                        end = Offset(size.width, size.height / 2f),
                        strokeWidth = stroke,
                    )
                }
            },
    )
}

@Composable
private fun NTreeCheckbox(
    checked: Boolean,
    indeterminate: Boolean,
    disabled: Boolean,
    onCheckedChange: () -> Unit,
) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val active = checked || indeterminate
    val borderColor = when {
        active -> theme.colors.primary
        disabled -> theme.colors.border
        hovered -> theme.colors.primaryHover
        else -> theme.colors.border
    }
    val shape = RoundedCornerShape(2.dp)
    Box(
        Modifier
            .size(16.dp)
            .clip(shape)
            .background(if (active) theme.colors.primary else Color.Transparent)
            .nBorder(borderColor, 1.dp, 2.dp)
            .hoverable(interaction)
            .nClickable(interaction, enabled = !disabled, onClick = onCheckedChange),
        contentAlignment = Alignment.Center,
    ) {
        when {
            indeterminate -> Box(Modifier.width(8.dp).height(2.dp).background(Color.White))
            checked -> NIcon(NIcons.Checkmark, size = 12.dp, color = Color.White)
        }
    }
}

private fun collectExpandableKeys(nodes: List<NTreeOption>): List<String> =
    nodes.flatMap { node ->
        if (!node.children.isNullOrEmpty()) {
            listOf(node.key) + collectExpandableKeys(node.children.orEmpty())
        } else {
            emptyList()
        }
    }
