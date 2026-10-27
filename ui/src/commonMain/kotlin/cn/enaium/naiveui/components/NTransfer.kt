package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NEmpty
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NCloseButton
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize

/** One selectable entry of an [NTransfer]. */
@Immutable
data class NTransferOption(
    val label: String,
    val value: String,
    val disabled: Boolean = false,
)

/**
 * Moves entries between a source and a target list.
 *
 * Source rows expose checkboxes; the arrow buttons move all checked entries to
 * the other panel. Target rows can be unchecked and moved back or removed one
 * by one.
 */
@Composable
fun NTransfer(
    options: List<NTransferOption>,
    modifier: Modifier = Modifier,
    value: List<String> = emptyList(),
    onValueChange: (List<String>) -> Unit = {},
    sourceTitle: String? = null,
    targetTitle: String? = null,
    filterable: Boolean = false,
    sourceFilterable: Boolean? = null,
    targetFilterable: Boolean? = null,
    showSelected: Boolean = true,
    selectAll: Boolean = true,
    disabled: Boolean = false,
    size: NSize = NSize.Medium,
    heights: Dp = 300.dp,
) {
    val locale = LocalNLocale.current

    val targetSet = value.toSet()
    val sourceOptions = options.filter { it.value !in targetSet }
    val targetOptions = options.filter { it.value in targetSet }

    var sourceChecked by remember { mutableStateOf(emptySet<String>()) }
    var targetChecked by remember { mutableStateOf(emptySet<String>()) }
    var sourcePattern by remember { mutableStateOf("") }
    var targetPattern by remember { mutableStateOf("") }

    val sourceValues = sourceOptions.mapTo(mutableSetOf()) { it.value }
    val targetValues = targetOptions.mapTo(mutableSetOf()) { it.value }
    val effectiveSourceChecked = sourceChecked.intersect(sourceValues)
    val effectiveTargetChecked = targetChecked.intersect(targetValues)

    val srcFilterable = sourceFilterable ?: filterable
    val tgtFilterable = targetFilterable ?: filterable

    fun matches(pattern: String, option: NTransferOption): Boolean =
        pattern.isBlank() || option.label.contains(pattern, ignoreCase = true)

    val visibleSource = sourceOptions.filter { option ->
        matches(sourcePattern, option) && (showSelected || option.value !in effectiveSourceChecked)
    }
    val visibleTarget = targetOptions.filter { option -> matches(targetPattern, option) }

    fun moveCheckedToTarget() {
        if (disabled) return
        val moving = sourceOptions
            .filter { it.value in effectiveSourceChecked && !it.disabled }
            .map { it.value }
        if (moving.isEmpty()) return
        onValueChange((value + moving).distinct())
        sourceChecked = sourceChecked - moving.toSet()
    }

    fun moveCheckedToSource() {
        if (disabled) return
        val moving = targetOptions
            .filter { it.value in effectiveTargetChecked && !it.disabled }
            .map { it.value }
        if (moving.isEmpty()) return
        onValueChange(value.filterNot { it in moving })
        targetChecked = targetChecked - moving.toSet()
    }

    fun removeOption(option: NTransferOption) {
        if (disabled) return
        onValueChange(value.filterNot { it == option.value })
        targetChecked = targetChecked - option.value
    }

    fun selectAllSource() {
        if (disabled) return
        val candidates = visibleSource.filterNot { it.disabled }.mapTo(mutableSetOf()) { it.value }
        if (candidates.isEmpty()) return
        val allChecked = candidates.all { it in effectiveSourceChecked }
        sourceChecked = if (allChecked) sourceChecked - candidates else sourceChecked + candidates
    }

    fun clearTarget() {
        if (disabled) return
        onValueChange(value.filterNot { it in targetValues })
        targetChecked = emptySet()
    }

    val language = locale
    val selectableSource = visibleSource.filterNot { it.disabled }
    val sourceActionLabel = if (
        selectableSource.isNotEmpty() && selectableSource.all { it.value in effectiveSourceChecked }
    ) {
        language.unselectAll
    } else {
        language.selectAll
    }

    Row(
        modifier = modifier.fillMaxWidth().height(heights),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NTransferPanel(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            title = sourceTitle ?: language.sourceTitle,
            extra = "${language.total} ${options.size}",
            options = visibleSource,
            checked = effectiveSourceChecked,
            onToggle = { option, checked ->
                if (!showSelected) {
                    if (!disabled) onValueChange((value + option.value).distinct())
                } else {
                    if (!disabled) {
                        sourceChecked = if (checked) sourceChecked + option.value else sourceChecked - option.value
                    }
                }
            },
            showCheckboxes = showSelected,
            actionText = if (selectAll) sourceActionLabel else null,
            onAction = { selectAllSource() },
            filterable = srcFilterable,
            pattern = sourcePattern,
            onPatternChange = { sourcePattern = it },
            disabled = disabled,
            size = size,
        )

        Column(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            NButton(
                onClick = { moveCheckedToTarget() },
                size = NSize.Small,
                circle = true,
                secondary = true,
                disabled = disabled || effectiveSourceChecked.isEmpty(),
            ) {
                NIcon(NIcons.ChevronRight, size = 14.dp)
            }
            NButton(
                onClick = { moveCheckedToSource() },
                size = NSize.Small,
                circle = true,
                secondary = true,
                disabled = disabled || effectiveTargetChecked.isEmpty(),
            ) {
                NIcon(NIcons.ChevronLeft, size = 14.dp)
            }
        }

        NTransferPanel(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            title = targetTitle ?: language.targetTitle,
            extra = "${language.selected} ${targetOptions.size}",
            options = visibleTarget,
            checked = effectiveTargetChecked,
            onToggle = { option, checked ->
                if (!disabled) {
                    targetChecked = if (checked) targetChecked + option.value else targetChecked - option.value
                }
            },
            showCheckboxes = true,
            onRemove = { option -> removeOption(option) },
            actionText = if (targetOptions.isNotEmpty()) language.clear else null,
            onAction = { clearTarget() },
            filterable = tgtFilterable,
            pattern = targetPattern,
            onPatternChange = { targetPattern = it },
            disabled = disabled,
            size = size,
        )
    }
}

@Composable
private fun NTransferPanel(
    title: String,
    extra: String,
    options: List<NTransferOption>,
    checked: Set<String>,
    onToggle: (NTransferOption, Boolean) -> Unit,
    showCheckboxes: Boolean,
    modifier: Modifier = Modifier,
    onRemove: ((NTransferOption) -> Unit)? = null,
    actionText: String? = null,
    onAction: () -> Unit = {},
    filterable: Boolean = false,
    pattern: String = "",
    onPatternChange: (String) -> Unit = {},
    disabled: Boolean = false,
    size: NSize = NSize.Medium,
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    val shape = RoundedCornerShape(theme.borderRadius)
    val titleSize = when (size) {
        NSize.Tiny, NSize.Small -> theme.typography.fontSize.medium
        NSize.Medium -> theme.typography.fontSize.large
        NSize.Large -> theme.typography.fontSize.huge
    }
    val actionSize = if (size == NSize.Large) NSize.Small else NSize.Tiny

    Column(
        modifier
            .alpha(if (disabled) 0.9f else 1f)
            .clip(shape)
            .background(theme.colors.card)
            .nBorder(theme.colors.border, 1.dp, theme.borderRadius),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NText(
                text = title,
                size = titleSize,
                strong = true,
                color = if (disabled) theme.colors.textDisabled else theme.colors.text1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (actionText != null) {
                NButton(
                    onClick = onAction,
                    size = actionSize,
                    tertiary = true,
                    disabled = disabled,
                ) {
                    NText(text = actionText)
                }
                Spacer(Modifier.width(8.dp))
            }
            NText(
                text = extra,
                size = theme.typography.fontSize.small,
                color = if (disabled) theme.colors.textDisabled else theme.colors.text3,
                maxLines = 1,
            )
        }

        Box(Modifier.fillMaxWidth().height(1.dp).background(theme.colors.divider))

        if (filterable) {
            Box(Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 4.dp)) {
                NTransferSearchField(
                    value = pattern,
                    onValueChange = onPatternChange,
                    placeholder = locale.search,
                    disabled = disabled,
                    size = size,
                )
            }
        }

        Box(Modifier.weight(1f).fillMaxWidth()) {
            if (options.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    NEmpty(
                        modifier = Modifier.fillMaxWidth(),
                        description = locale.noData,
                        size = NSize.Small,
                    )
                }
            } else {
                NScrollbar(Modifier.fillMaxSize()) {
                    Column(Modifier.padding(vertical = 4.dp)) {
                        options.forEach { option ->
                            NTransferItem(
                                option = option,
                                checked = option.value in checked,
                                showCheckbox = showCheckboxes,
                                removable = onRemove != null,
                                disabled = disabled,
                                size = size,
                                onToggle = { onToggle(option, it) },
                                onRemove = { onRemove?.invoke(option) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NTransferItem(
    option: NTransferOption,
    checked: Boolean,
    showCheckbox: Boolean,
    removable: Boolean,
    disabled: Boolean,
    size: NSize,
    onToggle: (Boolean) -> Unit,
    onRemove: () -> Unit,
) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val itemDisabled = disabled || option.disabled
    val shape = RoundedCornerShape(theme.borderRadius)

    Box(
        Modifier.fillMaxWidth().padding(horizontal = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = theme.heightFor(size))
                .clip(shape)
                .background(if (hovered && !itemDisabled) theme.colors.hover else Color.Transparent)
                .hoverable(interaction)
                .nClickable(interaction, enabled = !itemDisabled, onClick = { onToggle(!checked) })
                .padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showCheckbox) {
                NCheckbox(
                    checked = checked,
                    onCheckedChange = { onToggle(it) },
                    disabled = itemDisabled,
                    size = size,
                )
                Spacer(Modifier.width(8.dp))
            }
            NText(
                text = option.label,
                modifier = Modifier.weight(1f),
                size = theme.typography.fontSize.of(size),
                color = if (itemDisabled) theme.colors.textDisabled else theme.colors.text2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (removable && !itemDisabled) {
                NCloseButton(
                    onClick = onRemove,
                    size = 18.dp,
                    iconSize = 12.dp,
                )
            }
        }
    }
}

@Composable
private fun NTransferSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    disabled: Boolean,
    size: NSize,
) {
    val theme = LocalNTheme.current
    val inputSize = if (size == NSize.Large) NSize.Medium else NSize.Small
    NInput(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = placeholder,
        size = inputSize,
        disabled = disabled,
        clearable = true,
        prefix = {
            NIcon(NIcons.Search, size = 14.dp, color = theme.colors.icon)
        },
    )
}
