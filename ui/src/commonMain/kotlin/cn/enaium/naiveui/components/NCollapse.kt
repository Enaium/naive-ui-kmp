package cn.enaium.naiveui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.internal.NCollapseTransition
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.internal.nTopBorder
import cn.enaium.naiveui.theme.LocalNTheme

/** Arrow position of a collapse item header. */
enum class NCollapseArrowPlacement { Left, Right }

/** Payload of [NCollapse.onItemHeaderClick]. */
@Immutable
data class NCollapseHeaderClickInfo(val name: String, val expanded: Boolean)

internal class NCollapseContext(
    val expandedNames: List<String>,
    val arrowPlacement: NCollapseArrowPlacement,
    val toggle: (String) -> Unit,
)

internal val LocalNCollapse = compositionLocalOf<NCollapseContext?> { null }

/**
 * Vertically stacked collapsible panels.
 *
 * [expandedNames] turns the component into a controlled component; leave it
 * `null` and use [defaultExpandedNames] for uncontrolled usage.
 */
@Composable
fun NCollapse(
    modifier: Modifier = Modifier,
    expandedNames: List<String>? = null,
    defaultExpandedNames: List<String> = emptyList(),
    accordion: Boolean = false,
    arrowPlacement: NCollapseArrowPlacement = NCollapseArrowPlacement.Left,
    onExpandedNamesChange: ((List<String>) -> Unit)? = null,
    onItemHeaderClick: ((NCollapseHeaderClickInfo) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    var internalExpanded by remember { mutableStateOf(defaultExpandedNames) }
    val mergedExpandedNames = expandedNames ?: internalExpanded
    val toggle: (String) -> Unit = { name ->
        val currentlyExpanded = name in mergedExpandedNames
        val next = if (accordion) {
            if (currentlyExpanded) emptyList() else listOf(name)
        } else {
            if (currentlyExpanded) mergedExpandedNames - name else mergedExpandedNames + name
        }
        if (expandedNames == null) internalExpanded = next
        onExpandedNamesChange?.invoke(next)
        onItemHeaderClick?.invoke(NCollapseHeaderClickInfo(name, !currentlyExpanded))
    }
    androidx.compose.runtime.CompositionLocalProvider(
        LocalNCollapse provides NCollapseContext(mergedExpandedNames, arrowPlacement, toggle),
    ) {
        Column(modifier) { content() }
    }
}

/**
 * A panel inside [NCollapse]. Content is animated when the panel is expanded
 * or collapsed.
 */
@Composable
fun NCollapseItem(
    name: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    disabled: Boolean = false,
    showArrow: Boolean = true,
    extra: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val collapse = LocalNCollapse.current ?: return
    val theme = LocalNTheme.current
    val expanded = name in collapse.expandedNames
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        label = "n-collapse-arrow",
    )
    val interaction = remember { MutableInteractionSource() }
    val titleColor = if (disabled) theme.colors.textDisabled else theme.colors.text1
    val arrowColor = if (disabled) theme.colors.iconDisabled else theme.colors.icon

    Column(
        modifier
            .fillMaxWidth()
            .nTopBorder(theme.colors.divider),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .hoverable(interaction, enabled = !disabled)
                .nClickable(interaction, enabled = !disabled) { collapse.toggle(name) },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showArrow && collapse.arrowPlacement == NCollapseArrowPlacement.Left) {
                NIcon(
                    NIcons.ChevronRight,
                    Modifier.rotate(arrowRotation),
                    size = 18.dp,
                    color = arrowColor,
                )
                Spacer(Modifier.width(4.dp))
            }
            if (title != null) {
                NText(
                    text = title,
                    modifier = Modifier.weight(1f),
                    color = titleColor,
                    size = theme.typography.fontSize.medium,
                )
            } else {
                Spacer(Modifier.weight(1f))
            }
            if (showArrow && collapse.arrowPlacement == NCollapseArrowPlacement.Right) {
                Spacer(Modifier.width(4.dp))
                NIcon(
                    NIcons.ChevronRight,
                    Modifier.rotate(arrowRotation),
                    size = 18.dp,
                    color = arrowColor,
                )
            }
            if (extra != null) {
                Spacer(Modifier.width(8.dp))
                extra()
            }
        }
        NCollapseTransition(show = expanded) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 4.dp),
            ) {
                content()
            }
        }
    }
}
