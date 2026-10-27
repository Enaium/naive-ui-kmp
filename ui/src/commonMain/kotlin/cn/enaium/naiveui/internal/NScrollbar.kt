package cn.enaium.naiveui.internal

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
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

/**
 * Scroll container with a naive-ui style overlay scrollbar.
 *
 * The thumb is painted in the container's draw phase, so it stays above
 * content and remains visible inside popups. It appears on hover or scrolling,
 * can be dragged, and uses [ScrollState.viewportSize] so its length matches
 * the real scrollable viewport.
 */
@Composable
fun NScrollbar(
    modifier: Modifier = Modifier,
    horizontal: Boolean = false,
    alwaysShow: Boolean = false,
    scrollState: ScrollState = rememberScrollState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit,
) {
    val theme = LocalNTheme.current
    val density = LocalDensity.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    var viewport by remember { mutableStateOf(IntSize.Zero) }
    var active by remember { mutableStateOf(false) }
    var lastScrollValue by remember { mutableStateOf(scrollState.value) }

    LaunchedEffect(scrollState.value, scrollState.isScrollInProgress) {
        if (scrollState.value != lastScrollValue || scrollState.isScrollInProgress) {
            active = true
            lastScrollValue = scrollState.value
        }
        if (!scrollState.isScrollInProgress) {
            delay(900)
            active = false
        }
    }

    val showThumb = alwaysShow || active || hovered
    // ScrollState.viewportSize is the exact viewport used to compute maxValue;
    // the outer layout size can differ (padding, popups, animated sizes).
    val measuredViewport = if (horizontal) viewport.width else viewport.height
    val viewportExtent = scrollState.viewportSize.takeIf { it > 0 } ?: measuredViewport
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
    val thumbColor = if (hovered) theme.colors.scrollbarHover else theme.colors.scrollbar
    val thumbThicknessPx = with(density) { 5.dp.toPx() }
    val edgeInsetPx = with(density) { 2.dp.toPx() }

    Box(
        modifier
            .hoverable(interaction)
            .onGloballyPositioned { viewport = it.size }
            .drawWithContent {
                drawContent()
                if (thumbExtent <= 0f) return@drawWithContent
                if (horizontal) {
                    drawRoundRect(
                        color = thumbColor,
                        topLeft = Offset(thumbOffset + edgeInsetPx, size.height - thumbThicknessPx - edgeInsetPx),
                        size = Size(thumbExtent, thumbThicknessPx),
                        cornerRadius = CornerRadius(thumbThicknessPx / 2f),
                    )
                } else {
                    drawRoundRect(
                        color = thumbColor,
                        topLeft = Offset(size.width - thumbThicknessPx - edgeInsetPx, thumbOffset + edgeInsetPx),
                        size = Size(thumbThicknessPx, thumbExtent),
                        cornerRadius = CornerRadius(thumbThicknessPx / 2f),
                    )
                }
            }
            .then(
                if (maxScroll > 0 && thumbExtent > 0f) {
                    Modifier.pointerInput(horizontal, thumbExtent, thumbOffset, viewportExtent, maxScroll) {
                        var dragging = false
                        var dragValue = 0f
                        detectDragGestures(
                            onDragStart = { position ->
                                dragging = if (horizontal) {
                                    position.x in thumbOffset..(thumbOffset + thumbExtent)
                                } else {
                                    position.y in thumbOffset..(thumbOffset + thumbExtent)
                                }
                                if (dragging) dragValue = scrollState.value.toFloat()
                            },
                            onDrag = { change, amount ->
                                if (!dragging) return@detectDragGestures
                                change.consume()
                                val track = (viewportExtent - thumbExtent).coerceAtLeast(1f)
                                val delta = if (horizontal) amount.x else amount.y
                                dragValue += delta / track * maxScroll
                                scope.launch {
                                    scrollState.scrollTo(dragValue.roundToInt().coerceIn(0, maxScroll))
                                }
                            },
                            onDragEnd = { dragging = false },
                            onDragCancel = { dragging = false },
                        )
                    }
                } else {
                    Modifier
                },
            ),
    ) {
        if (horizontal) {
            Row(Modifier.fillMaxHeight().horizontalScroll(scrollState).padding(contentPadding)) {
                content()
            }
        } else {
            Column(Modifier.fillMaxWidth().verticalScroll(scrollState).padding(contentPadding)) {
                content()
            }
        }
    }
}

/** Fixed-height overflow container used by dropdown-like components. */
@Composable
fun NScrollableMenu(
    modifier: Modifier = Modifier,
    maxHeight: Dp = 240.dp,
    content: @Composable () -> Unit,
) {
    Box(modifier.fillMaxWidth().height(maxHeight)) {
        NScrollbar(Modifier.fillMaxHeight()) {
            content()
        }
    }
}
