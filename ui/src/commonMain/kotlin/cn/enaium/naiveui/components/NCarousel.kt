package cn.enaium.naiveui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.internal.nClickable
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** Visual style of carousel pagination indicators. */
enum class NCarouselDotType { Dot, Line }

/** Placement of the carousel pagination indicators. */
enum class NCarouselDotPlacement { Top, Bottom, Left, Right }

/**
 * A slideshow built on the foundation pager.
 *
 * The caller is responsible for sizing the carousel through [modifier]
 * (for example `Modifier.fillMaxWidth().height(240.dp)`).
 */
@Composable
fun <T> NCarousel(
    items: List<T>,
    modifier: Modifier = Modifier,
    autoplay: Boolean = false,
    interval: Long = 5000L,
    loop: Boolean = true,
    showArrow: Boolean = false,
    showDot: Boolean = true,
    dotType: NCarouselDotType = NCarouselDotType.Dot,
    dotPlacement: NCarouselDotPlacement = NCarouselDotPlacement.Bottom,
    draggable: Boolean = false,
    initialIndex: Int = 0,
    currentIndex: Int? = null,
    onIndexChange: ((Int) -> Unit)? = null,
    content: @Composable (T) -> Unit,
) {
    if (items.isEmpty()) {
        Box(modifier) {}
        return
    }
    val lastIndex = items.lastIndex
    val pagerState = rememberPagerState(
        initialPage = initialIndex.coerceIn(0, lastIndex),
        pageCount = { items.size },
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(autoplay, interval, loop, items.size) {
        if (!autoplay || items.size <= 1) return@LaunchedEffect
        while (isActive) {
            delay(interval)
            if (pagerState.isScrollInProgress) continue
            val current = pagerState.currentPage
            val target = when {
                current < lastIndex -> current + 1
                loop -> 0
                else -> lastIndex
            }
            if (target != current) pagerState.animateScrollToPage(target)
        }
    }

    val latestOnIndexChange by rememberUpdatedState(onIndexChange)
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { latestOnIndexChange?.invoke(it) }
    }

    LaunchedEffect(currentIndex, items.size) {
        if (currentIndex == null) return@LaunchedEffect
        val target = currentIndex.coerceIn(0, lastIndex)
        if (target != pagerState.currentPage) pagerState.animateScrollToPage(target)
    }

    val step: (Int) -> Unit = { delta ->
        val current = pagerState.currentPage
        val target = when {
            current + delta < 0 -> if (loop) lastIndex else 0
            current + delta > lastIndex -> if (loop) 0 else lastIndex
            else -> current + delta
        }
        if (target != current) scope.launch { pagerState.animateScrollToPage(target) }
    }

    Box(modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = draggable,
        ) { page ->
            Box(Modifier.fillMaxSize()) { content(items[page]) }
        }

        if (showArrow) {
            val prevEnabled = loop || pagerState.currentPage > 0
            val nextEnabled = loop || pagerState.currentPage < lastIndex
            Box(Modifier.align(Alignment.CenterStart).padding(start = 12.dp)) {
                CarouselArrow(left = true, enabled = prevEnabled) { step(-1) }
            }
            Box(Modifier.align(Alignment.CenterEnd).padding(end = 12.dp)) {
                CarouselArrow(left = false, enabled = nextEnabled) { step(1) }
            }
        }

        if (showDot) {
            val vertical = dotPlacement == NCarouselDotPlacement.Left ||
                dotPlacement == NCarouselDotPlacement.Right
            val placementModifier = when (dotPlacement) {
                NCarouselDotPlacement.Top -> Modifier.align(Alignment.TopCenter).padding(top = 12.dp)
                NCarouselDotPlacement.Bottom -> Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
                NCarouselDotPlacement.Left -> Modifier.align(Alignment.CenterStart).padding(start = 12.dp)
                NCarouselDotPlacement.Right -> Modifier.align(Alignment.CenterEnd).padding(end = 12.dp)
            }
            if (vertical) {
                Column(
                    placementModifier,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    CarouselDots(items.size, pagerState.currentPage, dotType) { index ->
                        scope.launch { pagerState.animateScrollToPage(index) }
                    }
                }
            } else {
                Row(
                    placementModifier,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    CarouselDots(items.size, pagerState.currentPage, dotType) { index ->
                        scope.launch { pagerState.animateScrollToPage(index) }
                    }
                }
            }
        }
    }
}

@Composable
private fun CarouselArrow(left: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Box(
        Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = if (hovered && enabled) 0.4f else 0.22f))
            .hoverable(interaction, enabled = enabled)
            .nClickable(interaction, enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        NIcon(
            if (left) NIcons.ChevronLeft else NIcons.ChevronRight,
            size = 20.dp,
            color = Color.White.copy(alpha = if (enabled) 1f else 0.45f),
        )
    }
}

@Composable
private fun CarouselDots(
    total: Int,
    current: Int,
    type: NCarouselDotType,
    onSelect: (Int) -> Unit,
) {
    repeat(total) { index ->
        val active = index == current
        val interaction = remember { MutableInteractionSource() }
        when (type) {
            NCarouselDotType.Dot -> {
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = if (active) 1f else 0.3f))
                        .hoverable(interaction)
                        .nClickable(interaction) { onSelect(index) },
                )
            }
            NCarouselDotType.Line -> {
                val width: Dp by animateDpAsState(
                    targetValue = if (active) 24.dp else 16.dp,
                    label = "n-carousel-dot-line",
                )
                Box(
                    Modifier
                        .size(width = width, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = if (active) 1f else 0.3f))
                        .hoverable(interaction)
                        .nClickable(interaction) { onSelect(index) },
                )
            }
        }
    }
}
