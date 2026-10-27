package cn.enaium.naiveui.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.internal.NCloseButton
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nBottomBorder
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.internal.nLeftBorder
import cn.enaium.naiveui.internal.nRightBorder
import cn.enaium.naiveui.internal.nTopBorder
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.nSize

/** Visual style of a tab header. */
enum class NTabsType { Line, Bar, Card, Segment }

/** Position of the tab header relative to its panes. */
enum class NTabsPlacement { Top, Bottom, Left, Right }

/**
 * A single tab and its pane content.
 *
 * Instantiate panes as values, then pass the list to [NTabs]:
 * ```
 * val panes = listOf(
 *     NTabPane("basic", "Basic") { Text("Basic pane") },
 *     NTabPane("api", "API") { Text("API pane") },
 * )
 * ```
 */
class NTabPane(
    val name: String,
    val tab: String = name,
    val disabled: Boolean = false,
    val closable: Boolean? = null,
    val icon: ImageVector? = null,
    val content: (@Composable () -> Unit)? = null,
)

/**
 * Tabs with line, bar, card and segment styles and top, bottom, left and right
 * placements.
 *
 * Pass [value] to control the active tab or leave it null for internal state.
 * Closing and adding tabs is reported through [onClose] and [onAdd].
 */
@Composable
fun NTabs(
    value: String? = null,
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    type: NTabsType = NTabsType.Bar,
    placement: NTabsPlacement = NTabsPlacement.Top,
    size: NSize? = null,
    closable: Boolean = false,
    addable: Boolean = false,
    onClose: ((String) -> Unit)? = null,
    onAdd: (() -> Unit)? = null,
    tabPadding: Dp = 16.dp,
    animated: Boolean = true,
    disabled: Boolean = false,
    panes: List<NTabPane> = emptyList(),
    paneContentPadding: Dp = 12.dp,
) {
    var internalValue by remember { mutableStateOf<String?>(null) }
    val current = value
        ?: internalValue
        ?: panes.firstOrNull { !it.disabled }?.name

    fun select(name: String) {
        if (disabled) return
        if (value == null) internalValue = name
        onValueChange(name)
    }

    val resolvedSize = nSize(size)
    val theme = LocalNTheme.current
    val fontSize = when (resolvedSize) {
        NSize.Small -> 14.sp
        NSize.Large -> 16.sp
        else -> theme.typography.fontSize.medium
    }
    val header: @Composable () -> Unit = {
        TabHeader(
            panes = panes,
            current = current,
            type = type,
            placement = placement,
            fontSize = fontSize,
            tabPadding = tabPadding,
            closable = closable,
            addable = addable,
            animated = animated,
            disabled = disabled,
            onSelect = ::select,
            onClose = onClose,
            onAdd = onAdd,
        )
    }
    val pane: @Composable (Modifier) -> Unit = { paneModifier ->
        val selected = panes.firstOrNull { it.name == current }
        Box(paneModifier) {
            selected?.content?.invoke()
        }
    }

    when (placement) {
        NTabsPlacement.Top -> Column(modifier.fillMaxWidth()) {
            header()
            pane(Modifier.fillMaxWidth().padding(top = paneContentPadding))
        }
        NTabsPlacement.Bottom -> Column(modifier.fillMaxWidth()) {
            pane(Modifier.fillMaxWidth().padding(bottom = paneContentPadding))
            header()
        }
        NTabsPlacement.Left -> Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            header()
            pane(Modifier.weight(1f).padding(start = paneContentPadding))
        }
        NTabsPlacement.Right -> Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            pane(Modifier.weight(1f).padding(end = paneContentPadding))
            header()
        }
    }
}

@Composable
private fun TabHeader(
    panes: List<NTabPane>,
    current: String?,
    type: NTabsType,
    placement: NTabsPlacement,
    fontSize: androidx.compose.ui.unit.TextUnit,
    tabPadding: Dp,
    closable: Boolean,
    addable: Boolean,
    animated: Boolean,
    disabled: Boolean,
    onSelect: (String) -> Unit,
    onClose: ((String) -> Unit)?,
    onAdd: (() -> Unit)?,
) {
    val theme = LocalNTheme.current
    val density = LocalDensity.current
    val bounds = remember { mutableStateMapOf<String, Rect>() }
    val vertical = placement == NTabsPlacement.Left || placement == NTabsPlacement.Right
    val activeRect = current?.let { bounds[it] }
    val animation: AnimationSpec<Dp> = if (animated) tween(250) else snap()

    val targetLeft = activeRect?.let { with(density) { it.left.toDp() } } ?: 0.dp
    val targetTop = activeRect?.let { with(density) { it.top.toDp() } } ?: 0.dp
    val targetWidth = activeRect?.let { with(density) { it.width.toDp() } } ?: 0.dp
    val targetHeight = activeRect?.let { with(density) { it.height.toDp() } } ?: 0.dp
    val barLeft by animateDpAsState(targetLeft, animation)
    val barTop by animateDpAsState(targetTop, animation)
    val barWidth by animateDpAsState(targetWidth, animation)
    val barHeight by animateDpAsState(targetHeight, animation)
    val showBar = activeRect != null && (type == NTabsType.Line || type == NTabsType.Bar)

    val lineBorder = type == NTabsType.Line
    val headerShape = RoundedCornerShape(theme.borderRadius)

    val tabContent: @Composable (NTabPane) -> Unit = { pane ->
        TabItem(
            pane = pane,
            active = pane.name == current,
            type = type,
            fontSize = fontSize,
            tabPadding = tabPadding,
            closable = pane.closable ?: closable,
            disabled = disabled || pane.disabled,
            onSelect = { onSelect(pane.name) },
            onClose = { onClose?.invoke(pane.name) },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    bounds[pane.name] = coordinates.boundsInParent()
                },
        )
    }

    if (vertical) {
        Box(
            Modifier
                .widthIn(min = 100.dp)
                .then(
                    when {
                        !lineBorder -> Modifier
                        placement == NTabsPlacement.Left -> Modifier.nRightBorder(theme.colors.divider)
                        else -> Modifier.nLeftBorder(theme.colors.divider)
                    },
                ),
        ) {
            Column(Modifier.padding(vertical = 4.dp)) {
                panes.forEach { pane -> tabContent(pane) }
                if (addable) {
                    AddTab(modifier = Modifier.fillMaxWidth(), onAdd = onAdd)
                }
            }
            if (showBar) {
                val align = if (placement == NTabsPlacement.Left) Alignment.CenterEnd else Alignment.CenterStart
                Box(
                    Modifier
                        .align(align)
                        .offset(y = barTop)
                        .alpha(if (activeRect == null) 0f else 1f)
                        .width(2.dp)
                        .height(barHeight)
                        .background(theme.colors.primary),
                )
            }
        }
        return
    }

    Box(
        Modifier
            .fillMaxWidth()
            .then(
                when {
                    !lineBorder -> Modifier
                    placement == NTabsPlacement.Top -> Modifier.nBottomBorder(theme.colors.divider)
                    else -> Modifier.nTopBorder(theme.colors.divider)
                },
            ),
    ) {
        val railModifier = if (type == NTabsType.Segment) {
            Modifier
                .fillMaxWidth()
                .clip(headerShape)
                .background(theme.colors.tab, headerShape)
                .padding(3.dp)
        } else {
            Modifier.fillMaxWidth()
        }
        Row(
            modifier = railModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (type == NTabsType.Card) Arrangement.spacedBy(4.dp) else Arrangement.Start,
        ) {
            panes.forEach { pane ->
                val itemModifier = if (type == NTabsType.Segment) Modifier.weight(1f) else Modifier
                TabItem(
                    pane = pane,
                    active = pane.name == current,
                    type = type,
                    fontSize = fontSize,
                    tabPadding = tabPadding,
                    closable = pane.closable ?: closable,
                    disabled = disabled || pane.disabled,
                    onSelect = { onSelect(pane.name) },
                    onClose = { onClose?.invoke(pane.name) },
                    modifier = itemModifier.onGloballyPositioned { coordinates ->
                        bounds[pane.name] = coordinates.boundsInParent()
                    },
                )
            }
            if (addable) {
                AddTab(onAdd = onAdd)
            }
        }
        if (showBar) {
            val align = if (placement == NTabsPlacement.Top) Alignment.BottomStart else Alignment.TopStart
            Box(
                Modifier
                    .align(align)
                    .offset(x = barLeft)
                    .alpha(if (activeRect == null) 0f else 1f)
                    .width(barWidth)
                    .height(2.dp)
                    .background(theme.colors.primary),
            )
        }
    }
}

@Composable
private fun TabItem(
    pane: NTabPane,
    active: Boolean,
    type: NTabsType,
    fontSize: androidx.compose.ui.unit.TextUnit,
    tabPadding: Dp,
    closable: Boolean,
    disabled: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalNTheme.current
    val interaction = remember(pane.name) { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val verticalPadding = when (type) {
        NTabsType.Card -> 10.dp
        NTabsType.Segment -> 6.dp
        NTabsType.Line -> 10.dp
        NTabsType.Bar -> 6.dp
    }
    val horizontalPadding = when (type) {
        NTabsType.Card -> 16.dp
        NTabsType.Segment -> 12.dp
        NTabsType.Line, NTabsType.Bar -> tabPadding
    }
    val textColor = when {
        disabled -> theme.colors.textDisabled
        active && type == NTabsType.Segment -> theme.colors.text1
        active -> theme.colors.primary
        hovered && type != NTabsType.Segment -> theme.colors.primaryHover
        else -> theme.colors.text1
    }
    val background = when {
        disabled -> Color.Transparent
        type == NTabsType.Card && active -> theme.colors.card
        type == NTabsType.Card -> theme.colors.tab
        type == NTabsType.Segment && active -> theme.colors.card
        else -> Color.Transparent
    }
    val shape = RoundedCornerShape(theme.borderRadius)
    val borderModifier = when {
        type == NTabsType.Card -> Modifier.nBorder(theme.colors.divider, 1.dp, theme.borderRadius)
        type == NTabsType.Segment && active -> Modifier
        else -> Modifier
    }
    val base = modifier
        .clip(shape)
        .background(background)
        .then(borderModifier)
        .hoverable(interaction)
        .nClickable(interaction, enabled = !disabled, onClick = onSelect)
    Row(
        modifier = base.padding(horizontal = horizontalPadding, vertical = verticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (pane.icon != null) {
            NIcon(pane.icon, size = 16.dp, color = textColor)
            Spacer(Modifier.width(6.dp))
        }
        NBaseText(
            text = pane.tab,
            style = theme.typography.style(
                fontSize,
                if (active) FontWeight.Medium else theme.typography.fontWeight,
                textColor,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (closable) {
            Spacer(Modifier.width(4.dp))
            NCloseButton(onClick = onClose, size = 16.dp, iconSize = 10.dp)
        }
    }
}

@Composable
private fun AddTab(modifier: Modifier = Modifier, onAdd: (() -> Unit)?) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(theme.borderRadius))
            .hoverable(interaction)
            .nClickable(interaction, onClick = { onAdd?.invoke() })
            .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        NIcon(
            NIcons.Add,
            size = 16.dp,
            color = if (hovered) theme.colors.primaryHover else theme.colors.icon,
        )
    }
}
