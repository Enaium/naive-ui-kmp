package cn.enaium.naiveui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NClearButton
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NPopoverSurface
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.NTrigger
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nTapTrigger
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.nSize
import kotlinx.coroutines.delay

/** A selectable entry of [NPopselect]. */
data class NPopselectOption(
    val label: String,
    val value: String,
    val disabled: Boolean = false,
)

/**
 * A select rendered inside a popup.
 *
 * Single selection uses [value]/[onValueChange]; when [multiple] is set,
 * [values]/[onValuesChange] are used instead and long selections are folded
 * into a `+N` tag after [maxTagCount] entries. Pass [children] for a fully
 * custom trigger, and [header]/[empty]/[action] to extend the menu.
 */
@Composable
fun NPopselect(
    options: List<NPopselectOption>,
    value: String? = null,
    onValueChange: (String?) -> Unit = {},
    modifier: Modifier = Modifier,
    multiple: Boolean = false,
    values: List<String> = emptyList(),
    onValuesChange: (List<String>) -> Unit = {},
    trigger: NTrigger = NTrigger.Click,
    placement: NPlacement = NPlacement.BottomStart,
    maxTagCount: Int = Int.MAX_VALUE,
    width: Dp = 180.dp,
    maxHeight: Dp = 240.dp,
    size: NSize? = null,
    placeholder: String? = null,
    disabled: Boolean = false,
    clearable: Boolean = true,
    cancelable: Boolean = false,
    header: (@Composable () -> Unit)? = null,
    empty: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
    children: (@Composable () -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    val resolvedSize = nSize(size)
    val height = theme.heightFor(resolvedSize)
    val shape = RoundedCornerShape(theme.borderRadius)

    val selectedValues = if (multiple) values else listOfNotNull(value)
    val selectedLabels = selectedValues.map { selected ->
        options.firstOrNull { it.value == selected }?.label ?: selected
    }

    var visible by remember { mutableStateOf(false) }
    val interaction = remember { MutableInteractionSource() }
    val menuInteraction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val menuHovered by menuInteraction.collectIsHoveredAsState()
    val borderColor = if (!disabled && (hovered || visible)) {
        theme.colors.primaryHover
    } else {
        theme.colors.border
    }
    val chevronRotation by animateFloatAsState(if (visible) 180f else 0f)

    LaunchedEffect(hovered, menuHovered, disabled, trigger) {
        if (disabled) {
            visible = false
            return@LaunchedEffect
        }
        if (trigger == NTrigger.Hover) {
            if (hovered || menuHovered) {
                visible = true
            } else if (visible) {
                delay(80)
                visible = false
            }
        }
    }

    val triggerModifier = when (trigger) {
        NTrigger.Click, NTrigger.Focus -> Modifier.nTapTrigger(enabled = !disabled) {
            visible = !visible
        }
        NTrigger.Hover -> Modifier.hoverable(interaction, enabled = !disabled)
        NTrigger.Manual -> Modifier
    }

    val pick: (NPopselectOption) -> Unit = { option ->
        if (!option.disabled) {
            if (multiple) {
                val next = if (selectedValues.contains(option.value)) {
                    selectedValues.filterNot { it == option.value }
                } else {
                    selectedValues + option.value
                }
                onValuesChange(next)
            } else {
                if (cancelable && selectedValues.contains(option.value)) {
                    onValueChange(null)
                } else {
                    onValueChange(option.value)
                }
                visible = false
            }
        }
    }
    val clearSelection = {
        if (multiple) onValuesChange(emptyList()) else onValueChange(null)
    }

    Box(modifier) {
        if (children != null) {
            Box(triggerModifier) { children() }
        } else {
            Row(
                Modifier
                    .height(height)
                    .then(if (disabled) Modifier.alpha(theme.opacityDisabled) else Modifier)
                    .clip(shape)
                    .background(theme.colors.input)
                    .nBorder(borderColor, 1.dp, theme.borderRadius)
                    .then(triggerModifier)
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    Modifier.weight(1f).clipToBounds(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    if (selectedLabels.isEmpty()) {
                        NText(
                            text = placeholder ?: locale.placeholder,
                            color = theme.colors.placeholder,
                            size = theme.fontSizeFor(resolvedSize),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    } else if (multiple) {
                        selectedLabels.take(maxTagCount.coerceAtLeast(0)).forEach { label ->
                            NTag(text = label, size = NSize.Small)
                        }
                        if (maxTagCount >= 0 && selectedLabels.size > maxTagCount) {
                            NTag(text = "+${selectedLabels.size - maxTagCount}", size = NSize.Small)
                        }
                    } else {
                        NText(
                            text = selectedLabels.first(),
                            color = theme.colors.text2,
                            size = theme.fontSizeFor(resolvedSize),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                if (clearable && selectedLabels.isNotEmpty() && hovered && !disabled) {
                    NClearButton(onClick = clearSelection, size = 16.dp, iconSize = 10.dp)
                }
                NIcon(
                    icon = NIcons.ChevronDown,
                    modifier = Modifier.rotate(chevronRotation),
                    size = 14.dp,
                    color = theme.colors.icon,
                )
            }
        }

        NPopoverSurface(
            visible = visible,
            onDismiss = { visible = false },
            placement = placement,
            offset = IntOffset(0, 4),
            focusable = true,
            dismissOnClickOutside = true,
            padding = PaddingValues(vertical = 4.dp),
        ) {
            Column(
                Modifier
                    .width(width)
                    .hoverable(menuInteraction, enabled = trigger == NTrigger.Hover),
            ) {
                if (header != null) {
                    Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
                        header()
                    }
                    NPopselectDivider(theme.colors.divider)
                }
                if (options.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp)) {
                        if (empty != null) {
                            empty()
                        } else {
                            NText(
                                text = locale.popselectNoData,
                                color = theme.colors.text3,
                                size = theme.typography.fontSize.medium,
                            )
                        }
                    }
                } else {
                    NScrollbar(Modifier.heightIn(max = maxHeight)) {
                        Column(Modifier.fillMaxWidth()) {
                            options.forEach { option ->
                                key(option.value) {
                                    val optionInteraction = remember { MutableInteractionSource() }
                                    val optionHovered by optionInteraction.collectIsHoveredAsState()
                                    val selected = selectedValues.contains(option.value)
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .height(34.dp)
                                            .background(
                                                if (optionHovered && !option.disabled) {
                                                    theme.colors.hover
                                                } else {
                                                    Color.Transparent
                                                },
                                            )
                                            .hoverable(optionInteraction, enabled = !option.disabled)
                                            .nTapTrigger(enabled = !option.disabled) {
                                                pick(option)
                                            }
                                            .padding(horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        NText(
                                            text = option.label,
                                            modifier = Modifier.weight(1f),
                                            color = when {
                                                option.disabled -> theme.colors.textDisabled
                                                selected -> theme.colors.primary
                                                else -> theme.colors.text2
                                            },
                                            size = theme.typography.fontSize.medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        if (selected) {
                                            NIcon(
                                                icon = NIcons.Checkmark,
                                                size = 16.dp,
                                                color = theme.colors.primary,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (action != null) {
                    NPopselectDivider(theme.colors.divider)
                    Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
                        action()
                    }
                }
            }
        }
    }
}

@Composable
private fun NPopselectDivider(color: Color) {
    Box(Modifier.fillMaxWidth().height(1.dp).background(color))
}
