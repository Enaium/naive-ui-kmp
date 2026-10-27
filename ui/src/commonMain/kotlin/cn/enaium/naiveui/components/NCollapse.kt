package cn.enaium.naiveui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.internal.NCollapseTransition
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.internal.nTopBorder
import cn.enaium.naiveui.theme.LocalNTheme
import kotlin.math.roundToInt

/** Arrow position of a collapse item header. */
enum class NCollapseArrowPlacement { Left, Right }

/** Whether inactive content is unmounted or kept mounted. */
enum class NCollapseDisplayDirective { If, Show }

/** Header sub-areas that can trigger expansion. */
enum class NCollapseTriggerArea { Main, Extra, Arrow }

/** Payload of [NCollapse.onItemHeaderClick]. */
@Immutable
data class NCollapseHeaderClickInfo(val name: String, val expanded: Boolean)

internal class NCollapseContext(
    val expandedNames: List<String>,
    val arrowPlacement: NCollapseArrowPlacement,
    val displayDirective: NCollapseDisplayDirective,
    val triggerAreas: Set<NCollapseTriggerArea>,
    val arrow: (@Composable () -> Unit)?,
    val headerExtra: (@Composable () -> Unit)?,
    val toggle: (String) -> Unit,
)

internal val LocalNCollapse = compositionLocalOf<NCollapseContext?> { null }

/**
 * Vertically stacked collapsible panels.
 *
 * [expandedNames] turns the component into a controlled component; leave it
 * `null` and use [defaultExpandedNames] for uncontrolled usage. Global
 * [arrow] and [headerExtra] slots apply to every item that does not override
 * them.
 */
@Composable
fun NCollapse(
    modifier: Modifier = Modifier,
    expandedNames: List<String>? = null,
    defaultExpandedNames: List<String> = emptyList(),
    accordion: Boolean = false,
    arrowPlacement: NCollapseArrowPlacement = NCollapseArrowPlacement.Left,
    displayDirective: NCollapseDisplayDirective = NCollapseDisplayDirective.If,
    triggerAreas: Set<NCollapseTriggerArea> = setOf(
        NCollapseTriggerArea.Main,
        NCollapseTriggerArea.Extra,
        NCollapseTriggerArea.Arrow,
    ),
    arrow: (@Composable () -> Unit)? = null,
    headerExtra: (@Composable () -> Unit)? = null,
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
    CompositionLocalProvider(
        LocalNCollapse provides NCollapseContext(
            expandedNames = mergedExpandedNames,
            arrowPlacement = arrowPlacement,
            displayDirective = displayDirective,
            triggerAreas = triggerAreas,
            arrow = arrow,
            headerExtra = headerExtra,
            toggle = toggle,
        ),
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
    header: (@Composable () -> Unit)? = null,
    arrow: (@Composable () -> Unit)? = null,
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
    val resolvedArrow = arrow ?: collapse.arrow
    val mainInteraction = remember { MutableInteractionSource() }
    val extraInteraction = remember { MutableInteractionSource() }

    Column(
        modifier
            .fillMaxWidth()
            .nTopBorder(theme.colors.divider),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showArrow && collapse.arrowPlacement == NCollapseArrowPlacement.Left) {
                CollapseArrow(
                    rotation = arrowRotation,
                    color = arrowColor,
                    custom = resolvedArrow,
                    modifier = Modifier.padding(end = 4.dp),
                    clickable = NCollapseTriggerArea.Arrow in collapse.triggerAreas && !disabled,
                    interaction = interaction,
                    onClick = { collapse.toggle(name) },
                )
            }
            Box(
                Modifier
                    .weight(1f)
                    .then(
                        if (NCollapseTriggerArea.Main in collapse.triggerAreas) {
                            Modifier.nClickable(interaction, enabled = !disabled) { collapse.toggle(name) }
                        } else {
                            Modifier
                        },
                    ),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (header != null) {
                        header()
                    } else if (title != null) {
                        NText(
                            text = title,
                            color = titleColor,
                            size = theme.typography.fontSize.medium,
                        )
                    }
                }
            }
            if (showArrow && collapse.arrowPlacement == NCollapseArrowPlacement.Right) {
                CollapseArrow(
                    rotation = arrowRotation,
                    color = arrowColor,
                    custom = resolvedArrow,
                    modifier = Modifier.padding(start = 4.dp),
                    clickable = NCollapseTriggerArea.Arrow in collapse.triggerAreas && !disabled,
                    interaction = remember { MutableInteractionSource() },
                    onClick = { collapse.toggle(name) },
                )
            }
            val resolvedExtra = extra ?: collapse.headerExtra
            if (resolvedExtra != null) {
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier.then(
                        if (NCollapseTriggerArea.Extra in collapse.triggerAreas) {
                            Modifier.nClickable(extraInteraction, enabled = !disabled) { collapse.toggle(name) }
                        } else {
                            Modifier
                        },
                    ),
                ) {
                    resolvedExtra()
                }
            }
        }
        when (collapse.displayDirective) {
            NCollapseDisplayDirective.If -> NCollapseTransition(show = expanded) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 4.dp),
                ) {
                    content()
                }
            }
            NCollapseDisplayDirective.Show -> {
                val progress by animateFloatAsState(
                    targetValue = if (expanded) 1f else 0f,
                    animationSpec = tween(150),
                    label = "n-collapse-content",
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clipToBounds()
                        .layout { measurable, constraints ->
                            val full = measurable.measure(constraints)
                            val target = (full.height * progress).roundToInt()
                            val placeable = measurable.measure(
                                constraints.copy(minHeight = target, maxHeight = target),
                            )
                            layout(placeable.width, target) {
                                placeable.place(0, 0)
                            }
                        },
                ) {
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
    }
}

@Composable
private fun CollapseArrow(
    rotation: Float,
    color: androidx.compose.ui.graphics.Color,
    custom: (@Composable () -> Unit)?,
    modifier: Modifier,
    clickable: Boolean,
    interaction: MutableInteractionSource,
    onClick: () -> Unit,
) {
    Box(
        modifier.then(
            if (clickable) Modifier.nClickable(interaction, onClick = onClick) else Modifier,
        ),
    ) {
        if (custom != null) {
            Box(Modifier.rotate(rotation)) { custom() }
        } else {
            NIcon(
                NIcons.ChevronRight,
                Modifier.rotate(rotation),
                size = 18.dp,
                color = color,
            )
        }
    }
}
