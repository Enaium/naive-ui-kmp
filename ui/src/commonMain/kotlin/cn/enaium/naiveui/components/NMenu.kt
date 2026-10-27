package cn.enaium.naiveui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.internal.NFadeInExpandTransition
import cn.enaium.naiveui.internal.NPopoverSurface
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.nSize

/** Layout direction of a menu. */
enum class NMenuMode { Vertical, Horizontal }

/**
 * A node of a menu tree.
 *
 * `children` makes the node a submenu, otherwise it selects [key] when clicked.
 */
@Immutable
data class NMenuOption(
    val key: String,
    val label: String,
    val icon: ImageVector? = null,
    val disabled: Boolean = false,
    val children: List<NMenuOption> = emptyList(),
    val extra: String? = null,
)

@Immutable
private class NMenuContext(
    val selectedKey: String?,
    val expandedKeys: Set<String>,
    val collapsed: Boolean,
    val mode: NMenuMode,
    val indent: Dp,
    val inverted: Boolean,
    val disabled: Boolean,
    val onSelect: (String) -> Unit,
    val onToggle: (String) -> Unit,
)

private val LocalNMenuContext = staticCompositionLocalOf<NMenuContext?> { null }
private val LocalNMenuDepth = staticCompositionLocalOf { 0 }

private val NMenuItemHeight = 42.dp

private fun NMenuOption.contains(key: String?): Boolean =
    key != null && (this.key == key || children.any { it.contains(key) })

/**
 * Menu with vertical and horizontal modes, nested options, collapse support
 * and an optional slot API built from [NMenuItem] / [NSubMenu].
 *
 * Pass [options] for the declarative tree, or leave it empty and provide
 * [content] with slot components.
 */
@Composable
fun NMenu(
    modifier: Modifier = Modifier,
    options: List<NMenuOption> = emptyList(),
    value: String? = null,
    onValueChange: (String) -> Unit = {},
    mode: NMenuMode = NMenuMode.Vertical,
    collapsed: Boolean = false,
    collapsedWidth: Dp = 48.dp,
    width: Dp = 240.dp,
    height: Dp? = null,
    indent: Dp = 32.dp,
    defaultExpandedKeys: Set<String> = emptySet(),
    accordion: Boolean = false,
    inverted: Boolean = false,
    size: NSize? = null,
    disabled: Boolean = false,
    content: @Composable () -> Unit = {},
) {
    val theme = LocalNTheme.current
    val resolvedSize = nSize(size)
    val fontSize = theme.fontSizeFor(resolvedSize)
    var expandedKeys by remember(defaultExpandedKeys) { mutableStateOf(defaultExpandedKeys) }

    fun toggle(key: String) {
        expandedKeys = when {
            key in expandedKeys -> expandedKeys - key
            accordion -> setOf(key)
            else -> expandedKeys + key
        }
    }

    fun select(key: String) {
        if (!disabled) onValueChange(key)
    }

    val context = NMenuContext(
        selectedKey = value,
        expandedKeys = expandedKeys,
        collapsed = collapsed,
        mode = mode,
        indent = indent,
        inverted = inverted,
        disabled = disabled,
        onSelect = ::select,
        onToggle = ::toggle,
    )

    CompositionLocalProvider(
        LocalNMenuContext provides context,
        LocalNMenuDepth provides 0,
    ) {
        if (mode == NMenuMode.Horizontal) {
            Row(
                modifier = modifier
                    .background(if (inverted) theme.colors.inverted else Color.Transparent)
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (options.isNotEmpty()) {
                    options.forEach { option ->
                        HorizontalMenuOption(option, context, fontSize)
                    }
                } else {
                    content()
                }
            }
        } else {
            val menuWidth = if (collapsed) collapsedWidth else width
            val menuModifier = modifier
                .width(menuWidth)
                .background(if (inverted) theme.colors.inverted else Color.Transparent)
            val menuContent: @Composable () -> Unit = {
                Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    if (options.isNotEmpty()) {
                        options.forEach { option ->
                            VerticalMenuOption(option, 0, context, fontSize)
                        }
                    } else {
                        content()
                    }
                }
            }
            if (height != null) {
                NScrollbar(menuModifier.height(height), content = menuContent)
            } else {
                Box(menuModifier) { menuContent() }
            }
        }
    }
}

@Composable
private fun VerticalMenuOption(
    option: NMenuOption,
    depth: Int,
    context: NMenuContext,
    fontSize: androidx.compose.ui.unit.TextUnit,
) {
    val hasChildren = option.children.isNotEmpty()
    val expanded = option.key in context.expandedKeys
    val selected = context.selectedKey == option.key
    val childActive = context.selectedKey != null && option.children.any { it.contains(context.selectedKey) }
    val itemDisabled = option.disabled || context.disabled

    if (context.collapsed && hasChildren) {
        var open by remember(option.key) { mutableStateOf(false) }
        Box {
            MenuRow(
                label = option.label,
                icon = option.icon,
                paddingStart = 0.dp,
                fontSize = fontSize,
                selected = selected,
                childActive = childActive,
                expanded = open,
                hasChildren = hasChildren,
                disabled = itemDisabled,
                extra = option.extra,
                collapsed = true,
                inverted = context.inverted,
                horizontal = false,
                onClick = { if (!itemDisabled) open = !open },
            )
            NPopoverSurface(
                visible = open,
                onDismiss = { open = false },
                placement = cn.enaium.naiveui.internal.NPlacement.RightStart,
                padding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            ) {
                Column(Modifier.width(200.dp).padding(vertical = 4.dp)) {
                    option.children.forEach { child ->
                        HorizontalPopupOption(
                            option = child,
                            context = context,
                            fontSize = fontSize,
                            depth = 0,
                            onSelected = { open = false },
                        )
                    }
                }
            }
        }
        return
    }

    MenuRow(
        label = option.label,
        icon = option.icon,
        paddingStart = context.indent * (depth + 1),
        fontSize = fontSize,
        selected = selected,
        childActive = childActive,
        expanded = expanded,
        hasChildren = hasChildren,
        disabled = itemDisabled,
        extra = option.extra,
        collapsed = context.collapsed,
        inverted = context.inverted,
        horizontal = false,
        onClick = {
            when {
                itemDisabled -> Unit
                hasChildren -> context.onToggle(option.key)
                else -> context.onSelect(option.key)
            }
        },
    )
    if (hasChildren) {
        NFadeInExpandTransition(show = expanded) {
            Column(Modifier.fillMaxWidth()) {
                CompositionLocalProvider(LocalNMenuDepth provides depth + 1) {
                    option.children.forEach { child ->
                        VerticalMenuOption(child, depth + 1, context, fontSize)
                    }
                }
            }
        }
    }
}

@Composable
private fun HorizontalMenuOption(
    option: NMenuOption,
    context: NMenuContext,
    fontSize: androidx.compose.ui.unit.TextUnit,
) {
    val hasChildren = option.children.isNotEmpty()
    val selected = context.selectedKey == option.key
    val childActive = context.selectedKey != null && option.children.any { it.contains(context.selectedKey) }
    val itemDisabled = option.disabled || context.disabled
    var open by remember(option.key) { mutableStateOf(false) }

    Box {
        MenuRow(
            label = option.label,
            icon = option.icon,
            paddingStart = 20.dp,
            fontSize = fontSize,
            selected = selected,
            childActive = childActive,
            expanded = open,
            hasChildren = hasChildren,
            disabled = itemDisabled,
            extra = option.extra,
            collapsed = false,
            inverted = context.inverted,
            horizontal = true,
            onClick = {
                when {
                    itemDisabled -> Unit
                    hasChildren -> open = !open
                    else -> context.onSelect(option.key)
                }
            },
        )
        if (hasChildren) {
            NPopoverSurface(
                visible = open,
                onDismiss = { open = false },
                placement = cn.enaium.naiveui.internal.NPlacement.BottomStart,
                padding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            ) {
                Column(Modifier.width(200.dp).padding(vertical = 4.dp)) {
                    option.children.forEach { child ->
                        HorizontalPopupOption(
                            option = child,
                            context = context,
                            fontSize = fontSize,
                            depth = 0,
                            onSelected = { open = false },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HorizontalPopupOption(
    option: NMenuOption,
    context: NMenuContext,
    fontSize: androidx.compose.ui.unit.TextUnit,
    depth: Int,
    onSelected: () -> Unit,
) {
    val hasChildren = option.children.isNotEmpty()
    val expanded = option.key in context.expandedKeys
    val itemDisabled = option.disabled || context.disabled
    MenuRow(
        label = option.label,
        icon = option.icon,
        paddingStart = 12.dp + 16.dp * depth,
        fontSize = fontSize,
        selected = context.selectedKey == option.key,
        childActive = context.selectedKey != null && option.children.any { it.contains(context.selectedKey) },
        expanded = expanded,
        hasChildren = hasChildren,
        disabled = itemDisabled,
        extra = option.extra,
        collapsed = false,
        inverted = context.inverted,
        horizontal = true,
        onClick = {
            when {
                itemDisabled -> Unit
                hasChildren -> context.onToggle(option.key)
                else -> {
                    context.onSelect(option.key)
                    onSelected()
                }
            }
        },
    )
    if (hasChildren && expanded) {
        Column(Modifier.fillMaxWidth()) {
            option.children.forEach { child ->
                HorizontalPopupOption(child, context, fontSize, depth + 1, onSelected)
            }
        }
    }
}

@Composable
private fun MenuRow(
    label: String,
    icon: ImageVector?,
    paddingStart: Dp,
    fontSize: androidx.compose.ui.unit.TextUnit,
    selected: Boolean,
    childActive: Boolean,
    expanded: Boolean,
    hasChildren: Boolean,
    disabled: Boolean,
    extra: String?,
    collapsed: Boolean,
    inverted: Boolean,
    horizontal: Boolean,
    onClick: () -> Unit,
) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val contentColor = when {
        disabled -> theme.colors.textDisabled
        inverted -> Color.White
        selected || childActive -> theme.colors.primary
        else -> theme.colors.text2
    }
    val background = when {
        disabled -> Color.Transparent
        selected -> theme.colors.primary.copy(alpha = if (inverted) 0.24f else 0.1f)
        hovered && inverted -> Color.White.copy(alpha = 0.08f)
        hovered -> theme.colors.hover
        else -> Color.Transparent
    }
    val weight = if (selected || childActive) FontWeight.Medium else theme.typography.fontWeight

    if (collapsed && !horizontal) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(NMenuItemHeight)
                .clip(RoundedCornerShape(theme.borderRadius))
                .background(background)
                .hoverable(interaction)
                .nClickable(interaction, enabled = !disabled, onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            if (icon != null) {
                NIcon(icon, size = 24.dp, color = contentColor)
            } else {
                NBaseText(
                    text = label.take(1),
                    style = theme.typography.style(fontSize, weight, contentColor),
                )
            }
        }
        return
    }

    Row(
        modifier = Modifier
            .then(if (horizontal) Modifier else Modifier.fillMaxWidth())
            .height(NMenuItemHeight)
            .clip(RoundedCornerShape(theme.borderRadius))
            .background(background)
            .hoverable(interaction)
            .nClickable(interaction, enabled = !disabled, onClick = onClick)
            .padding(start = paddingStart, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            NIcon(icon, size = 20.dp, color = contentColor)
            Spacer(Modifier.width(8.dp))
        }
        Box(if (horizontal) Modifier else Modifier.weight(1f)) {
            NBaseText(
                text = label,
                style = theme.typography.style(fontSize, weight, contentColor),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        val extraText = extra
        if (!extraText.isNullOrEmpty()) {
            NBaseText(
                text = extraText,
                style = theme.typography.style(fontSize * 0.86f, color = theme.colors.text3),
                maxLines = 1,
            )
            Spacer(Modifier.width(6.dp))
        }
        if (hasChildren) {
            val rotation by animateFloatAsState(
                targetValue = if (expanded || horizontal) 0f else -90f,
                animationSpec = tween(200),
            )
            NIcon(
                NIcons.ChevronDownFilled,
                modifier = Modifier.rotate(rotation),
                size = 14.dp,
                color = contentColor,
            )
        }
    }
}

/**
 * A leaf entry for [NMenu]'s slot API. Selecting it calls the menu's
 * `onValueChange` with [key].
 */
@Composable
fun NMenuItem(
    label: String,
    key: String = label,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    disabled: Boolean = false,
    extra: String? = null,
    selected: Boolean? = null,
    onClick: (() -> Unit)? = null,
) {
    val context = LocalNMenuContext.current
    val depth = LocalNMenuDepth.current
    val theme = LocalNTheme.current
    val fontSize = theme.fontSizeFor(nSize())
    val isSelected = selected ?: (context?.selectedKey == key)
    val itemDisabled = disabled || context?.disabled == true
    Box(modifier) {
        MenuRow(
            label = label,
            icon = icon,
            paddingStart = if (context?.collapsed == true) 0.dp else (context?.indent ?: 32.dp) * (depth + 1),
            fontSize = fontSize,
            selected = isSelected,
            childActive = false,
            expanded = false,
            hasChildren = false,
            disabled = itemDisabled,
            extra = extra,
            collapsed = context?.collapsed == true,
            inverted = context?.inverted == true,
            horizontal = context?.mode == NMenuMode.Horizontal,
            onClick = {
                if (!itemDisabled) {
                    context?.onSelect?.invoke(key)
                    onClick?.invoke()
                }
            },
        )
    }
}

/**
 * Expandable entry for [NMenu]'s slot API. Its [content] should contain
 * [NMenuItem] or nested [NSubMenu] entries.
 */
@Composable
fun NSubMenu(
    label: String,
    key: String = label,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    disabled: Boolean = false,
    extra: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val context = checkNotNull(LocalNMenuContext.current) {
        "NSubMenu must be placed inside NMenu"
    }
    val depth = LocalNMenuDepth.current
    val theme = LocalNTheme.current
    val fontSize = theme.fontSizeFor(nSize())
    val expanded = key in context.expandedKeys
    val itemDisabled = disabled || context.disabled
    var open by remember(key) { mutableStateOf(false) }

    if (context.mode == NMenuMode.Horizontal) {
        Box(modifier) {
            MenuRow(
                label = label,
                icon = icon,
                paddingStart = 20.dp,
                fontSize = fontSize,
                selected = false,
                childActive = context.selectedKey == key,
                expanded = open,
                hasChildren = true,
                disabled = itemDisabled,
                extra = extra,
                collapsed = false,
                inverted = context.inverted,
                horizontal = true,
                onClick = { if (!itemDisabled) open = !open },
            )
            NPopoverSurface(
                visible = open,
                onDismiss = { open = false },
                placement = cn.enaium.naiveui.internal.NPlacement.BottomStart,
                padding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            ) {
                Column(Modifier.width(200.dp).padding(vertical = 4.dp)) {
                    CompositionLocalProvider(LocalNMenuDepth provides depth + 1) {
                        content()
                    }
                }
            }
        }
        return
    }

    Column(modifier.fillMaxWidth()) {
        MenuRow(
            label = label,
            icon = icon,
            paddingStart = if (context.collapsed) 0.dp else context.indent * (depth + 1),
            fontSize = fontSize,
            selected = false,
            childActive = false,
            expanded = expanded,
            hasChildren = true,
            disabled = itemDisabled,
            extra = extra,
            collapsed = context.collapsed,
            inverted = context.inverted,
            horizontal = false,
            onClick = { if (!itemDisabled && !context.collapsed) context.onToggle(key) },
        )
        if (!context.collapsed) {
            NFadeInExpandTransition(show = expanded) {
                Column(Modifier.fillMaxWidth()) {
                    CompositionLocalProvider(LocalNMenuDepth provides depth + 1) {
                        content()
                    }
                }
            }
        }
    }
}

/** A divider between menu entries. */
@Composable
fun NMenuDivider(modifier: Modifier = Modifier) {
    val theme = LocalNTheme.current
    Box(
        modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 12.dp)
            .height(1.dp)
            .background(theme.colors.divider),
    )
}
