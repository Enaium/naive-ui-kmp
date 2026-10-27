package cn.enaium.naiveui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.internal.nTapTrigger
import cn.enaium.naiveui.theme.LocalNTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.ceil

/** Scroll axis of a carousel. */
enum class NCarouselDirection { Horizontal, Vertical }

/** Transition style used while switching slides. */
enum class NCarouselEffect { Slide, Fade, Card, Custom }

/** Whether pagination indicators react to clicks or hovers. */
enum class NCarouselTrigger { Click, Hover }

/** Visual style of carousel pagination indicators. */
enum class NCarouselDotType { Dot, Line }

/** Placement of the carousel pagination indicators. */
enum class NCarouselDotPlacement { Top, Bottom, Left, Right }

/**
 * Per-page visual transform used by [NCarouselEffect.Fade] style effects.
 *
 * [translationX] and [translationY] are fractions of the page size.
 */
@Immutable
data class NCarouselPageTransform(
    val alpha: Float = 1f,
    val scale: Float = 1f,
    val translationX: Float = 0f,
    val translationY: Float = 0f,
)

/** State offered to a custom arrow slot. */
@Immutable
class NCarouselArrowScope(
    val total: Int,
    val currentIndex: Int,
    val to: (Int) -> Unit,
    val prev: () -> Unit,
    val next: () -> Unit,
)

/** State offered to a custom dots slot. */
@Immutable
class NCarouselDotsScope(
    val total: Int,
    val currentIndex: Int,
    val to: (Int) -> Unit,
    val trigger: NCarouselTrigger,
)

private fun NCarouselEffect.defaultTransform(offset: Float): NCarouselPageTransform {
    val distance = abs(offset).coerceIn(0f, 1f)
    return NCarouselPageTransform(alpha = 1f - distance)
}

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
    direction: NCarouselDirection = NCarouselDirection.Horizontal,
    autoplay: Boolean = false,
    interval: Long = 5000L,
    loop: Boolean = true,
    showArrow: Boolean = false,
    showDot: Boolean = true,
    dotType: NCarouselDotType = NCarouselDotType.Dot,
    dotPlacement: NCarouselDotPlacement = NCarouselDotPlacement.Bottom,
    draggable: Boolean = false,
    mousewheel: Boolean = false,
    keyboard: Boolean = false,
    initialIndex: Int = 0,
    currentIndex: Int? = null,
    onIndexChange: ((Int) -> Unit)? = null,
    trigger: NCarouselTrigger = NCarouselTrigger.Click,
    slidesPerView: Float = 1f,
    autoSlideSize: Boolean = false,
    spaceBetween: Dp = 0.dp,
    centeredSlides: Boolean = false,
    effect: NCarouselEffect = NCarouselEffect.Slide,
    transitionDurationMillis: Int = 300,
    customTransform: ((offset: Float) -> NCarouselPageTransform)? = null,
    arrowContent: (@Composable NCarouselArrowScope.() -> Unit)? = null,
    dotsContent: (@Composable NCarouselDotsScope.() -> Unit)? = null,
    content: @Composable (T) -> Unit,
) {
    if (items.isEmpty()) {
        Box(modifier) {}
        return
    }
    val vertical = direction == NCarouselDirection.Vertical
    val lastIndex = items.lastIndex
    val pagerState = rememberPagerState(
        initialPage = initialIndex.coerceIn(0, lastIndex),
        pageCount = { items.size },
    )
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    val to: (Int) -> Unit = { index ->
        val target = if (loop) {
            ((index % items.size) + items.size) % items.size
        } else {
            index.coerceIn(0, lastIndex)
        }
        scope.launch {
            pagerState.animateScrollToPage(target, animationSpec = tween(transitionDurationMillis))
        }
    }
    val prev: () -> Unit = {
        val current = pagerState.currentPage
        val target = if (current <= 0) {
            if (loop) lastIndex else 0
        } else {
            current - 1
        }
        if (target != current) {
            scope.launch {
                pagerState.animateScrollToPage(target, animationSpec = tween(transitionDurationMillis))
            }
        }
    }
    val next: () -> Unit = {
        val current = pagerState.currentPage
        val target = if (current >= lastIndex) {
            if (loop) 0 else lastIndex
        } else {
            current + 1
        }
        if (target != current) {
            scope.launch {
                pagerState.animateScrollToPage(target, animationSpec = tween(transitionDurationMillis))
            }
        }
    }

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
            if (target != current) {
                pagerState.animateScrollToPage(target, animationSpec = tween(transitionDurationMillis))
            }
        }
    }

    val latestOnIndexChange by rememberUpdatedState(onIndexChange)
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { latestOnIndexChange?.invoke(it) }
    }

    LaunchedEffect(currentIndex, items.size) {
        if (currentIndex == null) return@LaunchedEffect
        val target = currentIndex.coerceIn(0, lastIndex)
        if (target != pagerState.currentPage) {
            pagerState.animateScrollToPage(target, animationSpec = tween(transitionDurationMillis))
        }
    }

    val centered = centeredSlides && slidesPerView <= 1f

    BoxWithConstraints(
        modifier
            .then(
                if (keyboard) {
                    Modifier
                        .focusRequester(focusRequester)
                        .focusable()
                        .nTapTrigger { focusRequester.requestFocus() }
                        .onPreviewKeyEvent { event ->
                            if (event.type != KeyEventType.KeyDown) {
                                false
                            } else {
                                when (event.key) {
                                    Key.DirectionLeft, Key.DirectionUp -> {
                                        prev()
                                        true
                                    }
                                    Key.DirectionRight, Key.DirectionDown -> {
                                        next()
                                        true
                                    }
                                    else -> false
                                }
                            }
                        }
                } else {
                    Modifier
                },
            ),
    ) {
        val mainExtent = if (vertical) maxHeight else maxWidth
        val spacingCount = (ceil(slidesPerView.toDouble()).toInt() - 1).coerceAtLeast(0)
        val pageExtent: Dp = when {
            effect == NCarouselEffect.Card -> mainExtent
            autoSlideSize -> mainExtent * 0.6f
            slidesPerView > 1f -> ((mainExtent - spaceBetween * spacingCount.toFloat()) / slidesPerView)
                .coerceIn(1.dp, mainExtent.coerceAtLeast(1.dp))
            centered -> mainExtent * 0.7f
            else -> mainExtent
        }
        val centerPadding = if (centered) ((mainExtent - pageExtent) / 2f).coerceAtLeast(0.dp) else 0.dp
        val pageSize = PageSize.Fixed(pageExtent)
        val pageSpacing = spaceBetween
        val userScroll = draggable || mousewheel
        val pagePadding = if (vertical) {
            PaddingValues(vertical = centerPadding)
        } else {
            PaddingValues(horizontal = centerPadding)
        }

        if (effect == NCarouselEffect.Card) {
            val current = pagerState.currentPage
            val ghostExtent = mainExtent * 0.6f
            if (current > 0) {
                CardGhost(
                    item = items[current - 1],
                    direction = -1,
                    pageExtent = ghostExtent,
                    vertical = vertical,
                    content = content,
                )
            }
            if (current < lastIndex) {
                CardGhost(
                    item = items[current + 1],
                    direction = 1,
                    pageExtent = ghostExtent,
                    vertical = vertical,
                    content = content,
                )
            }
        }

        if (vertical) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = pagePadding,
                pageSize = pageSize,
                pageSpacing = pageSpacing,
                userScrollEnabled = userScroll,
            ) { page ->
                CarouselPage(
                    page = page,
                    currentPage = pagerState.currentPage,
                    offsetFraction = pagerState.currentPageOffsetFraction,
                    effect = effect,
                    customTransform = customTransform,
                    vertical = true,
                ) {
                    content(items[page])
                }
            }
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = pagePadding,
                pageSize = pageSize,
                pageSpacing = pageSpacing,
                userScrollEnabled = userScroll,
            ) { page ->
                CarouselPage(
                    page = page,
                    currentPage = pagerState.currentPage,
                    offsetFraction = pagerState.currentPageOffsetFraction,
                    effect = effect,
                    customTransform = customTransform,
                    vertical = false,
                ) {
                    content(items[page])
                }
            }
        }

        if (showArrow || arrowContent != null) {
            val arrowScope = NCarouselArrowScope(
                total = items.size,
                currentIndex = pagerState.currentPage,
                to = to,
                prev = prev,
                next = next,
            )
            if (arrowContent != null) {
                Box(Modifier.fillMaxSize()) {
                    with(arrowScope) { arrowContent() }
                }
            } else {
                val prevEnabled = loop || pagerState.currentPage > 0
                val nextEnabled = loop || pagerState.currentPage < lastIndex
                if (vertical) {
                    Box(Modifier.align(Alignment.TopCenter).padding(top = 12.dp)) {
                        CarouselArrow(up = true, enabled = prevEnabled, vertical = true, onPrevious = prev, onNext = next)
                    }
                    Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)) {
                        CarouselArrow(up = false, enabled = nextEnabled, vertical = true, onPrevious = prev, onNext = next)
                    }
                } else {
                    Box(Modifier.align(Alignment.CenterStart).padding(start = 12.dp)) {
                        CarouselArrow(up = true, enabled = prevEnabled, vertical = false, onPrevious = prev, onNext = next)
                    }
                    Box(Modifier.align(Alignment.CenterEnd).padding(end = 12.dp)) {
                        CarouselArrow(up = false, enabled = nextEnabled, vertical = false, onPrevious = prev, onNext = next)
                    }
                }
            }
        }

        if (dotsContent != null) {
            val dotsScope = NCarouselDotsScope(
                total = items.size,
                currentIndex = pagerState.currentPage,
                to = to,
                trigger = trigger,
            )
            Box(Modifier.fillMaxSize()) {
                with(dotsScope) { dotsContent() }
            }
        } else if (showDot) {
            val verticalDots = dotPlacement == NCarouselDotPlacement.Left ||
                dotPlacement == NCarouselDotPlacement.Right
            val dotModifier = when (dotPlacement) {
                NCarouselDotPlacement.Top -> Modifier.align(Alignment.TopCenter).padding(top = 12.dp)
                NCarouselDotPlacement.Bottom -> Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
                NCarouselDotPlacement.Left -> Modifier.align(Alignment.CenterStart).padding(start = 12.dp)
                NCarouselDotPlacement.Right -> Modifier.align(Alignment.CenterEnd).padding(end = 12.dp)
            }
            if (verticalDots) {
                Column(dotModifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    CarouselDots(items.size, pagerState.currentPage, dotType, trigger, to)
                }
            } else {
                Row(dotModifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CarouselDots(items.size, pagerState.currentPage, dotType, trigger, to)
                }
            }
        }
    }
}

@Composable
private fun CarouselPage(
    page: Int,
    currentPage: Int,
    offsetFraction: Float,
    effect: NCarouselEffect,
    customTransform: ((offset: Float) -> NCarouselPageTransform)?,
    vertical: Boolean,
    content: @Composable () -> Unit,
) {
    val offset = (page - currentPage) - offsetFraction
    val transform = when (effect) {
        NCarouselEffect.Slide, NCarouselEffect.Card -> NCarouselPageTransform()
        NCarouselEffect.Fade -> effect.defaultTransform(offset)
        NCarouselEffect.Custom -> customTransform?.invoke(offset) ?: effect.defaultTransform(offset)
    }
    Box(
        Modifier
            .fillMaxSize()
            .graphicsLayer {
                alpha = transform.alpha
                scaleX = transform.scale
                scaleY = transform.scale
                translationX = transform.translationX * size.width
                translationY = transform.translationY * size.height
            },
    ) {
        if (effect == NCarouselEffect.Card) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .then(
                            if (vertical) {
                                Modifier.fillMaxWidth().fillMaxHeight(0.6f)
                            } else {
                                Modifier.fillMaxHeight().fillMaxWidth(0.6f)
                            },
                        ),
                ) {
                    content()
                }
            }
        } else {
            content()
        }
    }
}

@Composable
private fun <T> BoxScope.CardGhost(
    item: T,
    direction: Int,
    pageExtent: Dp,
    vertical: Boolean,
    content: @Composable (T) -> Unit,
) {
    val theme = LocalNTheme.current
    val density = LocalDensity.current
    val shift = with(density) { pageExtent.toPx() } * 0.55f
    Box(
        Modifier
            .then(
                if (vertical) {
                    Modifier
                        .fillMaxWidth(0.85f)
                        .height(pageExtent)
                } else {
                    Modifier
                        .fillMaxHeight(0.85f)
                        .width(pageExtent)
                },
            )
            .align(Alignment.Center)
            .graphicsLayer {
                if (vertical) translationY = direction * shift else translationX = direction * shift
                scaleX = 0.85f
                scaleY = 0.85f
                alpha = 0.55f
            }
            .clip(RoundedCornerShape(theme.borderRadius)),
    ) {
        content(item)
    }
}

@Composable
private fun CarouselArrow(
    up: Boolean,
    enabled: Boolean,
    vertical: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val isPrevious = up
    Box(
        Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = if (hovered && enabled) 0.4f else 0.22f))
            .hoverable(interaction, enabled = enabled)
            .nClickable(interaction, enabled = enabled) {
                if (isPrevious) onPrevious() else onNext()
            },
        contentAlignment = Alignment.Center,
    ) {
        val icon = when {
            vertical && up -> NIcons.ArrowUp
            vertical -> NIcons.ArrowDown
            up -> NIcons.ChevronLeft
            else -> NIcons.ChevronRight
        }
        NIcon(
            icon,
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
    trigger: NCarouselTrigger,
    onSelect: (Int) -> Unit,
) {
    repeat(total) { index ->
        val active = index == current
        val interaction = remember { MutableInteractionSource() }
        val hovered by interaction.collectIsHoveredAsState()
        if (trigger == NCarouselTrigger.Hover) {
            LaunchedEffect(hovered) {
                if (hovered) onSelect(index)
            }
        }
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
