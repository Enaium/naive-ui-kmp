package cn.enaium.naiveui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.theme.LocalNTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

/** When the scrollbar thumb is displayed. */
enum class NScrollbarTrigger { Hover, None }

/** Which edge hosts the scrollbar thumb. */
enum class NScrollbarPlacement { Top, Bottom, Left, Right }

/**
 * Scroll container with a naive-ui style overlay scrollbar. Give the container
 * a bounded size (for example with `Modifier.height`) so it can scroll.
 *
 * [trigger] controls visibility, [xPlacement]/[yPlacement] move the thumb, and
 * [color], [colorHover] and [thickness] customize its appearance.
 */
@Composable
fun NScrollbar(
    modifier: Modifier = Modifier,
    horizontal: Boolean = false,
    alwaysShow: Boolean = false,
    trigger: NScrollbarTrigger = NScrollbarTrigger.Hover,
    yPlacement: NScrollbarPlacement = NScrollbarPlacement.Right,
    xPlacement: NScrollbarPlacement = NScrollbarPlacement.Bottom,
    scrollState: ScrollState = rememberScrollState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    color: Color? = null,
    colorHover: Color? = null,
    thickness: Dp = 5.dp,
    content: @Composable () -> Unit,
) {
    val theme = LocalNTheme.current
    val density = LocalDensity.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    var viewport by remember { mutableStateOf(IntSize.Zero) }
    var active by remember { mutableStateOf(false) }
    var thumbHovered by remember { mutableStateOf(false) }

    LaunchedEffect(scrollState.value, scrollState.isScrollInProgress) {
        if (scrollState.isScrollInProgress) {
            active = true
        } else if (active) {
            delay(700)
            active = false
        }
    }

    val showThumb = alwaysShow || trigger == NScrollbarTrigger.None || active || hovered || thumbHovered
    val viewportExtent = if (horizontal) viewport.width else viewport.height
    val maxScroll = scrollState.maxValue
    val total = viewportExtent + maxScroll
    val fraction = if (total <= 0) 1f else viewportExtent.toFloat() / total
    val minThumbPx = with(density) { 20.dp.toPx() }
    val thumbExtent = if (showThumb && maxScroll > 0) {
        max(minThumbPx, viewportExtent * fraction)
    } else {
        0f
    }
    val thumbOffset = if (maxScroll > 0 && viewportExtent > thumbExtent) {
        (scrollState.value.toFloat() / maxScroll) * (viewportExtent - thumbExtent)
    } else {
        0f
    }
    val scope = rememberCoroutineScope()
    val thumbColor = if (thumbHovered) {
        colorHover ?: theme.colors.scrollbarHover
    } else {
        color ?: theme.colors.scrollbar
    }
    val thumbShape = RoundedCornerShape(thickness / 2f)

    Box(
        modifier
            .hoverable(interaction)
            .onGloballyPositioned { viewport = it.size },
    ) {
        if (horizontal) {
            Row(Modifier.horizontalScroll(scrollState).padding(contentPadding)) {
                content()
            }
        } else {
            Column(Modifier.fillMaxWidth().verticalScroll(scrollState).padding(contentPadding)) {
                content()
            }
        }
        if (thumbExtent > 0f) {
            val thumbInteraction = remember { MutableInteractionSource() }
            val thumbHoveredState by thumbInteraction.collectIsHoveredAsState()
            LaunchedEffect(thumbHoveredState) { thumbHovered = thumbHoveredState }
            val thumbExtentDp = with(density) { thumbExtent.toDp() }
            val thumbOffsetDp = with(density) { thumbOffset.toDp() }
            val thumbModifier = if (horizontal) {
                Modifier
                    .align(
                        if (xPlacement == NScrollbarPlacement.Top) {
                            Alignment.TopCenter
                        } else {
                            Alignment.BottomCenter
                        },
                    )
                    .offset(x = thumbOffsetDp)
                    .size(width = thumbExtentDp, height = thickness)
            } else {
                Modifier
                    .align(
                        if (yPlacement == NScrollbarPlacement.Left) {
                            Alignment.CenterStart
                        } else {
                            Alignment.CenterEnd
                        },
                    )
                    .offset(y = thumbOffsetDp)
                    .size(width = thickness, height = thumbExtentDp)
            }
            Box(
                thumbModifier
                    .clip(thumbShape)
                    .background(thumbColor)
                    .hoverable(thumbInteraction)
                    .draggable(
                        orientation = if (horizontal) Orientation.Horizontal else Orientation.Vertical,
                        state = rememberDraggableState { delta ->
                            val track = (viewportExtent - thumbExtent).coerceAtLeast(1f)
                            val target = scrollState.value + (delta / track) * maxScroll
                            scope.launch {
                                scrollState.scrollTo(target.roundToInt().coerceIn(0, maxScroll))
                            }
                        },
                    ),
            )
        }
    }
}
