package cn.enaium.naiveui.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
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
import kotlinx.coroutines.launch

/** Visual style of a tab header. */
enum class NTabsType { Line, Bar, Card, Segment }

/** Position of the tab header relative to its panes. */
enum class NTabsPlacement { Top, Bottom, Left, Right }

/** How a tab is activated. */
enum class NTabsTrigger { Click, Hover }

/** How inactive panes are kept in the composition. */
enum class NTabsDisplayDirective { If, Show, ShowLazy }

/** Main-axis distribution for line and bar tabs. */
enum class NTabsJustify { Start, Center, End, SpaceBetween, SpaceAround, SpaceEvenly }

/** Imperative handle for [NTabs]. */
@Stable
class NTabsState internal constructor() {
    internal var barVersion by mutableStateOf(0)
    internal var scrollHandler: (() -> Unit)? = null

    /** Recomputes the animated bar position from the measured tabs. */
    fun syncBarPosition() {
        barVersion += 1
    }

    /** Scrolls the header so the current tab is centered when possible. */
    fun scrollToCurrentTab() {
        scrollHandler?.invoke()
    }
}

/** Creates a remembered [NTabsState]. */
@Composable
fun rememberNTabsState(): NTabsState = remember { NTabsState() }

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
    val displayDirective: NTabsDisplayDirective = NTabsDisplayDirective.If,
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
    tabMinWidth: Dp = 0.dp,
    animated: Boolean = true,
    disabled: Boolean = false,
    trigger: NTabsTrigger = NTabsTrigger.Click,
    justifyContent: NTabsJustify = NTabsJustify.Start,
    barWidth: Dp? = null,
    showScrollButton: Boolean = false,
    centerActiveTab: Boolean = false,
    displayDirective: NTabsDisplayDirective = NTabsDisplayDirective.If,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
    state: NTabsState? = null,
    onBeforeLeave: ((String, String?) -> Boolean)? = null,
    panes: List<NTabPane> = emptyList(),
    paneContentPadding: Dp = 12.dp,
) {
    var internalValue by remember { mutableStateOf<String?>(null) }
    val current = value
        ?: internalValue
        ?: panes.firstOrNull { !it.disabled }?.name

    fun select(name: String) {
        if (disabled) return
        if (onBeforeLeave?.invoke(name, current) == false) return
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
            tabMinWidth = tabMinWidth,
            closable = closable,
            addable = addable,
            animated = animated,
            disabled = disabled,
            trigger = trigger,
            justifyContent = justifyContent,
            barWidth = barWidth,
            showScrollButton = showScrollButton,
            centerActiveTab = centerActiveTab,
            prefix = prefix,
            suffix = suffix,
            state = state,
            onSelect = ::select,
            onClose = onClose,
            onAdd = onAdd,
        )
    }
    val pane: @Composable (Modifier) -> Unit = { paneModifier ->
        TabPanes(
            panes = panes,
            current = current,
            defaultDirective = displayDirective,
            modifier = paneModifier,
        )
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
private fun TabPanes(
    panes: List<NTabPane>,
    current: String?,
    defaultDirective: NTabsDisplayDirective,
    modifier: Modifier,
) {
    val visited = remember { mutableStateMapOf<String, Boolean>() }
    LaunchedEffect(current) {
        if (current != null) visited[current] = true
    }
    val composeAll = panes.any {
        val directive = if (it.displayDirective == NTabsDisplayDirective.If) defaultDirective else it.displayDirective
        directive != NTabsDisplayDirective.If
    }
    Box(modifier) {
        if (!composeAll) {
            key(current) {
                panes.firstOrNull { it.name == current }?.content?.invoke()
            }
        } else {
            panes.forEach { pane ->
                key(pane.name) {
                    val directive = if (pane.displayDirective == NTabsDisplayDirective.If) {
                        defaultDirective
                    } else {
                        pane.displayDirective
                    }
                    when {
                        pane.name == current -> pane.content?.invoke()
                        directive == NTabsDisplayDirective.Show -> HiddenPane(pane)
                        directive == NTabsDisplayDirective.ShowLazy && visited[pane.name] == true -> HiddenPane(pane)
                    }
                }
            }
        }
    }
}

/** Composes a pane for state retention without giving it layout space. */
@Composable
private fun HiddenPane(pane: NTabPane) {
    Box(
        Modifier.layout { measurable, constraints ->
            measurable.measure(constraints)
            layout(0, 0) {}
        },
    ) {
        pane.content?.invoke()
    }
}

@Composable
private fun TabHeader(
    panes: List<NTabPane>,
    current: String?,
    type: NTabsType,
    placement: NTabsPlacement,
    fontSize: TextUnit,
    tabPadding: Dp,
    tabMinWidth: Dp,
    closable: Boolean,
    addable: Boolean,
    animated: Boolean,
    disabled: Boolean,
    trigger: NTabsTrigger,
    justifyContent: NTabsJustify,
    barWidth: Dp?,
    showScrollButton: Boolean,
    centerActiveTab: Boolean,
    prefix: (@Composable () -> Unit)?,
    suffix: (@Composable () -> Unit)?,
    state: NTabsState?,
    onSelect: (String) -> Unit,
    onClose: ((String) -> Unit)?,
    onAdd: (() -> Unit)?,
) {
    val theme = LocalNTheme.current
    val density = LocalDensity.current
    val prefixContent = prefix
    val suffixContent = suffix
    val bounds = remember { mutableStateMapOf<String, Rect>() }
    val vertical = placement == NTabsPlacement.Left || placement == NTabsPlacement.Right
    @Suppress("UNUSED_VARIABLE")
    val barVersion = state?.barVersion ?: 0
    val activeRect = current?.let { bounds[it] }
    val animation: AnimationSpec<Dp> = if (animated) tween(250) else snap()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    val targetLeft = activeRect?.let { with(density) { it.left.toDp() } } ?: 0.dp
    val targetTop = activeRect?.let { with(density) { it.top.toDp() } } ?: 0.dp
    val targetWidth = activeRect?.let { with(density) { it.width.toDp() } } ?: 0.dp
    val targetHeight = activeRect?.let { with(density) { it.height.toDp() } } ?: 0.dp
    val indicatorWidth = if (barWidth != null) minOf(targetWidth, barWidth) else targetWidth
    val indicatorHeight = if (barWidth != null) minOf(targetHeight, barWidth) else targetHeight
    val barLeft by animateDpAsState(targetLeft + (targetWidth - indicatorWidth) * 0.5f, animation)
    val barTop by animateDpAsState(targetTop + (targetHeight - indicatorHeight) * 0.5f, animation)
    val barWidthAnim by animateDpAsState(indicatorWidth, animation)
    val barHeightAnim by animateDpAsState(indicatorHeight, animation)
    val showBar = activeRect != null && (type == NTabsType.Line || type == NTabsType.Bar)
    val lineBorder = type == NTabsType.Line
    val headerShape = RoundedCornerShape(theme.borderRadius)

    fun selectPane(name: String) = onSelect(name)

    val tabContent: @Composable (NTabPane, Modifier) -> Unit = { pane, itemModifier ->
        TabItem(
            pane = pane,
            active = pane.name == current,
            type = type,
            fontSize = fontSize,
            tabPadding = tabPadding,
            tabMinWidth = tabMinWidth,
            closable = pane.closable ?: closable,
            disabled = disabled || pane.disabled,
            trigger = trigger,
            onSelect = { selectPane(pane.name) },
            onClose = { onClose?.invoke(pane.name) },
            modifier = itemModifier.onGloballyPositioned { coordinates ->
                bounds[pane.name] = coordinates.boundsInParent()
            },
        )
    }

    fun scrollToCurrent() {
        val rect = activeRect ?: return
        val viewport = scrollState.viewportSize
        if (viewport <= 0) return
        val target = (rect.left + rect.width / 2 - viewport / 2).toInt()
        scope.launch {
            scrollState.animateScrollTo(target.coerceIn(0, scrollState.maxValue))
        }
    }

    LaunchedEffect(centerActiveTab, current, activeRect, scrollState.viewportSize) {
        if (centerActiveTab && activeRect != null && !vertical) {
            val viewport = scrollState.viewportSize
            if (viewport > 0) {
                val target = (activeRect.left + activeRect.width / 2 - viewport / 2).toInt()
                scrollState.animateScrollTo(target.coerceIn(0, scrollState.maxValue))
            }
        }
    }
    LaunchedEffect(state, activeRect) {
        state?.scrollHandler = { scrollToCurrent() }
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
                if (prefixContent != null) prefixContent()
                panes.forEach { pane -> tabContent(pane, Modifier.fillMaxWidth()) }
                if (addable) {
                    AddTab(modifier = Modifier.fillMaxWidth(), onAdd = onAdd)
                }
                if (suffixContent != null) suffixContent()
            }
            if (showBar) {
                val align = if (placement == NTabsPlacement.Left) Alignment.CenterEnd else Alignment.CenterStart
                Box(
                    Modifier
                        .align(align)
                        .offset(y = barTop)
                        .alpha(if (activeRect == null) 0f else 1f)
                        .width(2.dp)
                        .height(barHeightAnim)
                        .background(theme.colors.primary),
                )
            }
        }
        return
    }

    val scrollableHeader = showScrollButton || centerActiveTab
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
        if (scrollableHeader) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                if (showScrollButton) {
                    ScrollArrow(false) {
                        scope.launch {
                            scrollState.animateScrollTo(
                                (scrollState.value - 160).coerceIn(0, scrollState.maxValue),
                            )
                        }
                    }
                }
                Box(
                    Modifier
                        .weight(1f)
                        .horizontalScroll(scrollState),
                ) {
                    Box {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
                            if (prefixContent != null) prefixContent()
                            panes.forEach { pane -> tabContent(pane, Modifier) }
                            if (addable) AddTab(onAdd = onAdd)
                            if (suffixContent != null) suffixContent()
                        }
                        if (showBar) {
                            val align = if (placement == NTabsPlacement.Top) Alignment.BottomStart else Alignment.TopStart
                            Box(
                                Modifier
                                    .align(align)
                                    .offset(x = barLeft)
                                    .alpha(if (activeRect == null) 0f else 1f)
                                    .width(barWidthAnim)
                                    .height(2.dp)
                                    .background(theme.colors.primary),
                            )
                        }
                    }
                }
                if (showScrollButton) {
                    ScrollArrow(true) {
                        scope.launch {
                            scrollState.animateScrollTo(
                                (scrollState.value + 160).coerceIn(0, scrollState.maxValue),
                            )
                        }
                    }
                }
            }
        } else {
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
                horizontalArrangement = if (type == NTabsType.Card) {
                    Arrangement.spacedBy(4.dp)
                } else {
                    justifyContent.arrangement()
                },
            ) {
                if (prefixContent != null) prefixContent()
                panes.forEach { pane ->
                    val itemModifier = if (type == NTabsType.Segment) Modifier.weight(1f) else Modifier
                    TabItem(
                        pane = pane,
                        active = pane.name == current,
                        type = type,
                        fontSize = fontSize,
                        tabPadding = tabPadding,
                        tabMinWidth = tabMinWidth,
                        closable = pane.closable ?: closable,
                        disabled = disabled || pane.disabled,
                        trigger = trigger,
                        onSelect = { selectPane(pane.name) },
                        onClose = { onClose?.invoke(pane.name) },
                        modifier = itemModifier.onGloballyPositioned { coordinates ->
                            bounds[pane.name] = coordinates.boundsInParent()
                        },
                    )
                }
                if (addable) AddTab(onAdd = onAdd)
                if (suffixContent != null) suffixContent()
            }
            if (showBar) {
                val align = if (placement == NTabsPlacement.Top) Alignment.BottomStart else Alignment.TopStart
                Box(
                    Modifier
                        .align(align)
                        .offset(x = barLeft)
                        .alpha(if (activeRect == null) 0f else 1f)
                        .width(barWidthAnim)
                        .height(2.dp)
                        .background(theme.colors.primary),
                )
            }
        }
    }
}

private fun NTabsJustify.arrangement(): Arrangement.Horizontal = when (this) {
    NTabsJustify.Start -> Arrangement.Start
    NTabsJustify.Center -> Arrangement.Center
    NTabsJustify.End -> Arrangement.End
    NTabsJustify.SpaceBetween -> Arrangement.SpaceBetween
    NTabsJustify.SpaceAround -> Arrangement.SpaceAround
    NTabsJustify.SpaceEvenly -> Arrangement.SpaceEvenly
}

@Composable
private fun ScrollArrow(forward: Boolean, onClick: () -> Unit) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Box(
        Modifier
            .width(24.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(theme.borderRadius))
            .hoverable(interaction)
            .nClickable(interaction, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        NIcon(
            if (forward) NIcons.ChevronRight else NIcons.ChevronLeft,
            size = 14.dp,
            color = if (hovered) theme.colors.primaryHover else theme.colors.icon,
        )
    }
}

@Composable
private fun TabItem(
    pane: NTabPane,
    active: Boolean,
    type: NTabsType,
    fontSize: TextUnit,
    tabPadding: Dp,
    tabMinWidth: Dp,
    closable: Boolean,
    disabled: Boolean,
    trigger: NTabsTrigger,
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
    val borderModifier = if (type == NTabsType.Card) {
        Modifier.nBorder(theme.colors.divider, 1.dp, theme.borderRadius)
    } else {
        Modifier
    }
    LaunchedEffect(hovered, trigger, disabled) {
        if (hovered && trigger == NTabsTrigger.Hover && !disabled) onSelect()
    }
    val base = modifier
        .clip(shape)
        .background(background)
        .then(borderModifier)
        .hoverable(interaction)
        .nClickable(interaction, enabled = !disabled, onClick = onSelect)
    Row(
        modifier = base
            .then(if (tabMinWidth > 0.dp) Modifier.widthIn(min = tabMinWidth) else Modifier)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
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
