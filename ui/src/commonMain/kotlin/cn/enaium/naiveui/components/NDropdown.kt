package cn.enaium.naiveui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NPopoverSurface
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.NTrigger
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.internal.nTapTrigger
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import kotlinx.coroutines.delay

/** An entry in an [NDropdown] menu. */
sealed interface NDropdownOption {
    val key: String

    /** A regular selectable option. [children] turns it into a submenu. */
    data class Label(
        override val key: String,
        val label: String,
        val disabled: Boolean = false,
        val icon: ImageVector? = null,
        val extra: String? = null,
        val children: List<NDropdownOption> = emptyList(),
        val onClick: (() -> Unit)? = null,
    ) : NDropdownOption

    /** A horizontal separator. */
    data class Divider(override val key: String = "divider") : NDropdownOption

    /** A labelled group of options. */
    data class Group(
        override val key: String,
        val label: String,
        val children: List<NDropdownOption>,
    ) : NDropdownOption

    /** A non-selectable header. */
    data class Header(override val key: String, val label: String) : NDropdownOption

    /** Arbitrary content rendered inline in the menu. */
    data class Content(
        override val key: String,
        val content: @Composable () -> Unit,
    ) : NDropdownOption
}

private class DropdownRenderers(
    val size: NSize,
    val renderLabel: (@Composable (NDropdownOption) -> Unit)?,
    val renderIcon: (@Composable (NDropdownOption.Label) -> Unit)?,
    val optionWrapper: (@Composable (NDropdownOption.Label, @Composable () -> Unit) -> Unit)?,
    val onSelect: (String) -> Unit,
)

/**
 * A menu anchored to [content].
 *
 * Supports hover, click and manual triggering, grouped options, dividers,
 * custom labels/icons, nested submenus and manual popup offsets.
 */
@Composable
fun NDropdown(
    options: List<NDropdownOption>,
    modifier: Modifier = Modifier,
    trigger: NTrigger = NTrigger.Hover,
    placement: NPlacement = NPlacement.Bottom,
    showArrow: Boolean = false,
    disabled: Boolean = false,
    size: NSize = NSize.Medium,
    show: Boolean? = null,
    onShowChange: ((Boolean) -> Unit)? = null,
    offset: IntOffset = IntOffset(0, 6),
    renderLabel: (@Composable (NDropdownOption) -> Unit)? = null,
    renderIcon: (@Composable (NDropdownOption.Label) -> Unit)? = null,
    optionWrapper: (@Composable (NDropdownOption.Label, @Composable () -> Unit) -> Unit)? = null,
    onSelect: (String) -> Unit = {},
    content: @Composable () -> Unit,
) {
    var internalShow by remember { mutableStateOf(false) }
    val visible = show ?: internalShow
    val updateShow: (Boolean) -> Unit = { next ->
        if (show == null) internalShow = next
        onShowChange?.invoke(next)
    }
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()

    LaunchedEffect(trigger, hovered, disabled) {
        if (trigger != NTrigger.Hover) return@LaunchedEffect
        if (disabled) {
            if (visible) updateShow(false)
            return@LaunchedEffect
        }
        if (hovered) {
            delay(80)
            updateShow(true)
        } else if (visible) {
            delay(120)
            updateShow(false)
        }
    }

    val renderers = DropdownRenderers(
        size = size,
        renderLabel = renderLabel,
        renderIcon = renderIcon,
        optionWrapper = optionWrapper,
        onSelect = { key ->
            updateShow(false)
            onSelect(key)
        },
    )

    Box(
        modifier
            .hoverable(interaction, enabled = !disabled)
            .nTapTrigger(enabled = !disabled) {
                if (trigger == NTrigger.Click) updateShow(!visible)
            },
    ) {
        content()
        NPopoverSurface(
            visible = visible,
            onDismiss = { updateShow(false) },
            placement = placement,
            offset = offset,
            focusable = trigger != NTrigger.Hover,
            dismissOnClickOutside = true,
            padding = PaddingValues(0.dp),
        ) {
            DropdownMenuPanel(
                options = options,
                renderers = renderers,
                showArrow = showArrow,
                placement = placement,
            )
        }
    }
}

@Composable
private fun DropdownMenuPanel(
    options: List<NDropdownOption>,
    renderers: DropdownRenderers,
    showArrow: Boolean,
    placement: NPlacement,
) {
    val theme = LocalNTheme.current
    val arrowAtTop = placement == NPlacement.Bottom ||
        placement == NPlacement.BottomStart ||
        placement == NPlacement.BottomEnd
    val arrowAtBottom = placement == NPlacement.Top ||
        placement == NPlacement.TopStart ||
        placement == NPlacement.TopEnd
    Box(
        Modifier
            .widthIn(min = 160.dp, max = 280.dp)
            .clip(RoundedCornerShape(theme.borderRadius))
            .background(theme.colors.popover),
    ) {
        Column {
            if (showArrow && arrowAtTop) {
                DropdownArrow(pointsUp = true)
            }
            Column(Modifier.padding(vertical = 4.dp)) {
                DropdownMenuContent(options, renderers)
            }
            if (showArrow && arrowAtBottom) {
                DropdownArrow(pointsUp = false)
            }
        }
    }
}

@Composable
private fun DropdownMenuContent(
    options: List<NDropdownOption>,
    renderers: DropdownRenderers,
) {
    val theme = LocalNTheme.current
    NScrollbar(Modifier.heightIn(max = 360.dp), alwaysShow = true) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
        ) {
            options.forEach { option ->
                when (option) {
                    is NDropdownOption.Divider -> MenuDivider(theme)
                    is NDropdownOption.Content -> option.content()
                    is NDropdownOption.Header -> MenuGroupLabel(option.label, renderers, option)
                    is NDropdownOption.Group -> {
                        MenuGroupLabel(option.label, renderers, option)
                        option.children.forEach { child ->
                            DropdownOptionRow(child, renderers)
                        }
                    }
                    is NDropdownOption.Label -> DropdownOptionRow(option, renderers)
                }
            }
        }
    }
}

@Composable
private fun MenuGroupLabel(
    label: String,
    renderers: DropdownRenderers,
    option: NDropdownOption,
) {
    val theme = LocalNTheme.current
    val custom = renderers.renderLabel
    if (custom != null) {
        Box(Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            custom(option)
        }
    } else {
        NText(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            color = theme.colors.text3,
            size = 12.sp,
        )
    }
}

@Composable
private fun MenuDivider(theme: cn.enaium.naiveui.theme.NTheme) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .height(1.dp)
            .background(theme.colors.divider),
    )
}

@Composable
private fun DropdownOptionRow(
    option: NDropdownOption,
    renderers: DropdownRenderers,
) {
    val theme = LocalNTheme.current
    when (option) {
        is NDropdownOption.Divider -> MenuDivider(theme)
        is NDropdownOption.Content -> option.content()
        is NDropdownOption.Header -> MenuGroupLabel(option.label, renderers, option)
        is NDropdownOption.Group -> {
            MenuGroupLabel(option.label, renderers, option)
            option.children.forEach { child -> DropdownOptionRow(child, renderers) }
        }
        is NDropdownOption.Label -> DropdownLabelRow(option, renderers)
    }
}

@Composable
private fun DropdownLabelRow(
    option: NDropdownOption.Label,
    renderers: DropdownRenderers,
) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    var submenuVisible by remember { mutableStateOf(false) }
    var submenuHovered by remember { mutableStateOf(false) }
    val hasChildren = option.children.isNotEmpty()
    val enabled = !option.disabled

    LaunchedEffect(hovered, submenuHovered, enabled) {
        if (!enabled) {
            submenuVisible = false
            return@LaunchedEffect
        }
        if (hovered || submenuHovered) {
            delay(60)
            submenuVisible = true
        } else {
            delay(180)
            submenuVisible = false
        }
    }

    val row: @Composable () -> Unit = {
        Row(
            Modifier
                .fillMaxWidth()
                .height(theme.heightFor(renderers.size))
                .clip(RoundedCornerShape(theme.borderRadiusSmall))
                .background(if (hovered && enabled) theme.colors.hover else Color.Transparent)
                .hoverable(interaction, enabled = enabled)
                .nClickable(interaction, enabled = enabled) {
                    option.onClick?.invoke()
                    if (hasChildren) submenuVisible = true else renderers.onSelect(option.key)
                }
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val customIcon = renderers.renderIcon
            val icon = option.icon
            if (customIcon != null) {
                Box(Modifier.padding(end = 8.dp)) { customIcon(option) }
            } else if (icon != null) {
                NIcon(
                    icon,
                    Modifier.padding(end = 8.dp),
                    size = 16.dp,
                    color = if (enabled) theme.colors.icon else theme.colors.iconDisabled,
                )
            }
            val customLabel = renderers.renderLabel
            if (customLabel != null) {
                Box(Modifier.weight(1f)) { customLabel(option) }
            } else {
                NText(
                    text = option.label,
                    modifier = Modifier.weight(1f),
                    color = if (enabled) theme.colors.text2 else theme.colors.textDisabled,
                    size = theme.fontSizeFor(renderers.size),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (hasChildren) {
                NIcon(
                    NIcons.ChevronRight,
                    Modifier.padding(start = 8.dp),
                    size = 14.dp,
                    color = if (enabled) theme.colors.icon else theme.colors.iconDisabled,
                )
            }
            val extraText = option.extra
            if (extraText != null) {
                Spacer(Modifier.width(12.dp))
                NText(text = extraText, color = theme.colors.text3, size = 12.sp)
            }
        }
    }

    Box {
        val wrapper = renderers.optionWrapper
        if (wrapper != null) wrapper(option, row) else row()
        if (hasChildren) {
            NPopoverSurface(
                visible = submenuVisible,
                onDismiss = { submenuVisible = false },
                placement = NPlacement.RightStart,
                offset = IntOffset(-2, 0),
                focusable = false,
                dismissOnClickOutside = false,
                padding = PaddingValues(0.dp),
            ) {
                DropdownSubmenu(
                    options = option.children,
                    renderers = renderers,
                    submenuHovered = { submenuHovered = it },
                )
            }
        }
    }
}

@Composable
private fun DropdownSubmenu(
    options: List<NDropdownOption>,
    renderers: DropdownRenderers,
    submenuHovered: (Boolean) -> Unit,
) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    LaunchedEffect(hovered) { submenuHovered(hovered) }
    Box(
        Modifier
            .widthIn(min = 160.dp, max = 280.dp)
            .clip(RoundedCornerShape(theme.borderRadius))
            .background(theme.colors.popover)
            .hoverable(interaction),
    ) {
        Column(Modifier.padding(vertical = 4.dp, horizontal = 4.dp)) {
            DropdownMenuContent(options, renderers)
        }
    }
}

@Composable
private fun DropdownArrow(pointsUp: Boolean) {
    val theme = LocalNTheme.current
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(width = 14.dp, height = 6.dp)) {
            val path = Path().apply {
                if (pointsUp) {
                    moveTo(size.width / 2f, 0f)
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                } else {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width / 2f, size.height)
                }
                close()
            }
            drawPath(path, color = theme.colors.popover)
            drawPath(
                path = path,
                color = theme.colors.border,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx()),
            )
        }
    }
}
