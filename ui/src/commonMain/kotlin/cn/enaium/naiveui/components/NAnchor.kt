package cn.enaium.naiveui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.theme.LocalNTheme

/** Visual style of an anchor's navigation list. */
enum class NAnchorType { Rail, Block }

/** A link in an [NAnchor]. [children] render one indentation level deeper. */
class NAnchorItem(
    val id: String,
    val title: String,
    val children: List<NAnchorItem> = emptyList(),
)

@Immutable
private class NAnchorContext(
    val activeId: String?,
    val type: NAnchorType,
    val showBackground: Boolean,
    val indent: Dp,
    val onSelect: (String) -> Unit,
    val onPosition: (String, Rect) -> Unit,
)

private val LocalNAnchor = staticCompositionLocalOf<NAnchorContext?> { null }

/**
 * Anchor navigation with a rail, nested links and optional affix behavior.
 *
 * [activeId] makes selection controlled; otherwise clicking a link updates the
 * internal active id. [affix] pins the anchor inside the vertical container
 * owning [scrollState].
 */
@Composable
fun NAnchor(
    items: List<NAnchorItem> = emptyList(),
    modifier: Modifier = Modifier,
    activeId: String? = null,
    onSelect: (String) -> Unit = {},
    offsetTop: Dp = 0.dp,
    type: NAnchorType = NAnchorType.Rail,
    showRail: Boolean = true,
    showBackground: Boolean = true,
    ignoreGap: Boolean = false,
    bound: Dp = 12.dp,
    affix: Boolean = false,
    scrollState: ScrollState? = null,
    content: @Composable () -> Unit = {},
) {
    val theme = LocalNTheme.current
    var internalActive by remember { mutableStateOf<String?>(null) }
    val current = activeId ?: internalActive
    val positions = remember { mutableStateMapOf<String, Rect>() }
    var anchorBounds by remember { mutableStateOf<Rect?>(null) }

    fun select(id: String) {
        if (activeId == null) internalActive = id
        onSelect(id)
    }

    val context = NAnchorContext(
        activeId = current,
        type = type,
        showBackground = showBackground,
        indent = 18.dp,
        onSelect = ::select,
        onPosition = { id, rect -> positions[id] = rect },
    )

    val body: @Composable () -> Unit = {
        Box(
            modifier
                .onGloballyPositioned { coordinates ->
                    anchorBounds = coordinates.boundsInWindow()
                }
                .then(
                    if (showRail && type == NAnchorType.Rail) {
                        Modifier.drawBehind {
                            drawRect(
                                color = theme.colors.rail,
                                topLeft = Offset.Zero,
                                size = Size(2.dp.toPx(), size.height),
                            )
                        }
                    } else {
                        Modifier
                    },
                ),
        ) {
            CompositionLocalProvider(LocalNAnchor provides context) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = if (showRail && type == NAnchorType.Rail) 16.dp else 0.dp),
                ) {
                    if (items.isNotEmpty()) {
                        items.forEach { item -> AnchorItem(item, 0) }
                    } else {
                        content()
                    }
                }
            }
            if (showRail && type == NAnchorType.Rail) {
                val rect = current?.let { positions[it] }
                val density = LocalDensity.current
                val activeBounds = anchorBounds?.let { anchor ->
                    rect?.let { link ->
                        val gap = if (ignoreGap) 0f else with(density) { bound.toPx() }
                        val topPx = (link.top - anchor.top - gap).coerceAtLeast(0f)
                        val heightPx = link.height + gap * 2
                        with(density) { topPx.toDp() } to with(density) { heightPx.toDp() }
                    }
                }
                val top by animateDpAsState(
                    targetValue = activeBounds?.first ?: 0.dp,
                    animationSpec = tween(220),
                )
                val height by animateDpAsState(
                    targetValue = activeBounds?.second ?: 0.dp,
                    animationSpec = tween(220),
                )
                if (activeBounds != null) {
                    Box(
                        Modifier
                            .offset(y = top)
                            .width(2.dp)
                            .height(height)
                            .background(theme.colors.primary),
                    )
                }
            }
        }
    }

    if (affix) {
        NAffix(scrollState = scrollState, offsetTop = offsetTop) { body() }
    } else {
        body()
    }
}

@Composable
private fun AnchorItem(item: NAnchorItem, depth: Int) {
    NAnchorLink(
        title = item.title,
        href = item.id,
        depth = depth,
    )
    if (item.children.isNotEmpty()) {
        Column(Modifier.fillMaxWidth()) {
            item.children.forEach { child -> AnchorItem(child, depth + 1) }
        }
    }
}

/**
 * A single anchor link. Used by [NAnchor] and usable directly in the anchor's
 * `content` slot.
 */
@Composable
fun NAnchorLink(
    title: String,
    href: String,
    modifier: Modifier = Modifier,
    active: Boolean? = null,
    depth: Int = 0,
    onClick: (() -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val context = LocalNAnchor.current
    val isActive = active ?: (context?.activeId == href)
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val showBackground = context?.showBackground ?: true
    val block = context?.type == NAnchorType.Block
    val textColor = when {
        isActive -> theme.colors.primary
        hovered -> theme.colors.primaryHover
        else -> theme.colors.text2
    }
    val background = when {
        isActive && (showBackground || block) -> theme.colors.primary.copy(alpha = 0.1f)
        hovered && block -> theme.colors.hover
        else -> Color.Transparent
    }
    val shape = RoundedCornerShape(theme.borderRadiusSmall)
    val indent = context?.indent ?: 18.dp
    val fontSize = theme.typography.fontSize.medium
    Box(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                context?.onPosition?.invoke(href, coordinates.boundsInWindow())
            },
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(start = indent * depth, top = 1.dp, bottom = 1.dp)
                .clip(shape)
                .background(background)
                .hoverable(interaction)
                .nClickable(interaction) {
                    context?.onSelect?.invoke(href)
                    onClick?.invoke()
                }
                .padding(horizontal = 6.dp, vertical = 6.dp),
        ) {
            NBaseText(
                text = title,
                style = theme.typography.style(
                    fontSize,
                    if (isActive) FontWeight.Medium else theme.typography.fontWeight,
                    textColor,
                ),
            )
        }
    }
}
