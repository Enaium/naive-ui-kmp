package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.internal.NBaseLoading
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.internal.NPopoverSurface
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import cn.enaium.naiveui.theme.nSize

/** A single mention suggestion. [prefix] restricts it to one trigger character. */
@Immutable
data class NMentionOption(
    val label: String,
    val value: String = label,
    val prefix: String? = null,
    val disabled: Boolean = false,
)

/**
 * Mention input, ported from naive-ui's `n-mention`.
 *
 * Typing the [prefix] trigger opens a suggestion popup anchored to the input.
 * Selecting an option (click or keyboard) replaces the trigger and search text
 * with `prefix + label`. Use [onSearch] to load options remotely.
 */
@Composable
fun NMention(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    options: List<NMentionOption> = emptyList(),
    prefix: String = "@",
    placeholder: String = "",
    disabled: Boolean = false,
    size: NSize? = null,
    status: NStatus? = null,
    loading: Boolean = false,
    onSelect: ((NMentionOption) -> Unit)? = null,
    onSearch: ((pattern: String, prefix: String) -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val resolvedSize = nSize(size)
    val density = LocalDensity.current
    var focused by remember { mutableStateOf(false) }
    var dismissed by remember { mutableStateOf(false) }
    var suppressed by remember { mutableStateOf(false) }
    var highlighted by remember { mutableStateOf(0) }
    var anchorWidth by remember { mutableStateOf(0) }

    val triggerIndex = value.lastIndexOf(prefix)
    val search = if (triggerIndex >= 0) value.substring(triggerIndex + prefix.length) else ""
    val hasTrigger = triggerIndex >= 0 &&
        (triggerIndex == 0 || value[triggerIndex - 1].isWhitespace()) &&
        search.none { it.isWhitespace() }
    val filtered = if (hasTrigger) {
        options.filter { option ->
            (option.prefix == null || option.prefix == prefix) &&
                (search.isEmpty() ||
                    option.label.contains(search, ignoreCase = true) ||
                    option.value.contains(search, ignoreCase = true))
        }
    } else {
        emptyList()
    }
    val visible = hasTrigger && focused && !dismissed && !suppressed && !disabled &&
        (filtered.isNotEmpty() || loading)

    fun choose(option: NMentionOption) {
        if (option.disabled) return
        val inserted = prefix + option.label
        val next = value.substring(0, triggerIndex) + inserted
        dismissed = true
        suppressed = true
        onValueChange(next)
        onSelect?.invoke(option)
    }

    fun notifySearch(next: String) {
        val index = next.lastIndexOf(prefix)
        if (index < 0) return
        if (index > 0 && !next[index - 1].isWhitespace()) return
        val pattern = next.substring(index + prefix.length)
        if (pattern.any { it.isWhitespace() }) return
        onSearch?.invoke(pattern, prefix)
    }

    Box(
        modifier
            .onGloballyPositioned { anchorWidth = it.size.width }
            .onPreviewKeyEvent { event ->
                if (!visible || event.type != KeyEventType.KeyDown) {
                    return@onPreviewKeyEvent false
                }
                when (event.key) {
                    Key.DirectionDown -> {
                        if (filtered.isNotEmpty()) highlighted = (highlighted + 1) % filtered.size
                        true
                    }
                    Key.DirectionUp -> {
                        if (filtered.isNotEmpty()) highlighted = (highlighted - 1 + filtered.size) % filtered.size
                        true
                    }
                    Key.Enter, Key.Tab -> {
                        filtered.getOrNull(highlighted)?.let { choose(it) }
                        true
                    }
                    Key.Escape -> {
                        dismissed = true
                        true
                    }
                    else -> false
                }
            },
    ) {
        NInput(
            value = value,
            onValueChange = { next ->
                dismissed = false
                suppressed = false
                highlighted = 0
                onValueChange(next)
                notifySearch(next)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = placeholder,
            disabled = disabled,
            size = size,
            status = status,
            onFocus = {
                focused = true
                dismissed = false
                if (hasTrigger) onSearch?.invoke(search, prefix)
            },
            onBlur = { focused = false },
        )
        NPopoverSurface(
            visible = visible,
            onDismiss = { dismissed = true },
            placement = cn.enaium.naiveui.internal.NPlacement.BottomStart,
            offset = IntOffset(0, 4),
            focusable = false,
            padding = PaddingValues(4.dp),
        ) {
            val menuWidth = with(density) { anchorWidth.toDp() }
            NScrollbar(Modifier.width(if (menuWidth > 0.dp) menuWidth else 200.dp).heightIn(max = 220.dp)) {
                Column {
                    if (loading) {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            NBaseLoading(size = 14.dp)
                            Box(Modifier.width(8.dp))
                            NBaseText(
                                text = LocalNLocale.current.loading,
                                style = theme.typography.style(theme.fontSizeFor(resolvedSize), color = theme.colors.text3),
                            )
                        }
                    } else {
                        filtered.forEachIndexed { index, option ->
                            val interaction = remember(option) { MutableInteractionSource() }
                            val hovered by interaction.collectIsHoveredAsState()
                            val selected = index == highlighted
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(theme.borderRadiusSmall))
                                    .background(
                                        when {
                                            selected -> theme.colors.hover
                                            hovered -> theme.colors.hover
                                            else -> theme.colors.popover
                                        },
                                    )
                                    .hoverable(interaction, enabled = !option.disabled)
                                    .nClickable(interaction, enabled = !option.disabled) { choose(option) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                            ) {
                                NBaseText(
                                    text = option.label,
                                    style = theme.typography.style(
                                        theme.fontSizeFor(resolvedSize),
                                        color = if (option.disabled) theme.colors.textDisabled else theme.colors.text2,
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

