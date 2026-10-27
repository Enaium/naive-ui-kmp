package cn.enaium.naiveui.sample.gallery.general

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NCarousel
import cn.enaium.naiveui.components.NCarouselDirection
import cn.enaium.naiveui.components.NCarouselDotPlacement
import cn.enaium.naiveui.components.NCarouselDotType
import cn.enaium.naiveui.components.NCarouselEffect
import cn.enaium.naiveui.components.NCarouselPageTransform
import cn.enaium.naiveui.components.NCarouselTrigger
import cn.enaium.naiveui.components.NFlex
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NStatus
import kotlin.math.abs

private data class CarouselSlide(val title: String, val colors: List<Color>)

private val demosSlides = listOf(
    CarouselSlide("Morning Light", listOf(Color(0xFF667EEA), Color(0xFF764BA2))),
    CarouselSlide("Deep Ocean", listOf(Color(0xFF0F2027), Color(0xFF2C5364))),
    CarouselSlide("Warm Sunset", listOf(Color(0xFFF7971E), Color(0xFFFFD200))),
    CarouselSlide("Fresh Mint", listOf(Color(0xFF11998E), Color(0xFF38EF7D))),
)

@Composable
private fun Slide(slide: CarouselSlide) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(slide.colors))
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        NText(slide.title, color = Color.White, size = 24.sp, strong = true)
    }
}

@Composable
private fun CustomArrowButton(previous: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        NIcon(
            if (previous) NIcons.ChevronLeft else NIcons.ChevronRight,
            size = 16.dp,
            color = Color.White,
        )
    }
}

@Composable
fun CarouselDocs() {
    DocsPage(
        title = "Carousel",
        description = "A slideshow for cycling through content, with vertical mode, " +
            "slides per view, effects, autoplay, custom arrows and dots.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Give the carousel a height; each entry of `items` becomes a slide.",
            code = """
NCarousel(
    items = slides,
    modifier = Modifier.fillMaxWidth().height(220.dp),
) { slide -> Slide(slide) }
""".trimIndent(),
        ) {
            NCarousel(
                items = demosSlides,
                modifier = Modifier.fillMaxWidth().height(220.dp),
            ) { slide -> Slide(slide) }
        }

        DemoSection(
            title = "Arrow",
            description = "Set `showArrow` to display previous and next controls.",
        ) {
            var loop by remember { mutableStateOf(true) }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NFlex(size = 8.dp) {
                    NButton(onClick = { loop = !loop }) { NText(if (loop) "Loop: on" else "Loop: off") }
                }
                NCarousel(
                    items = demosSlides,
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    showArrow = true,
                    loop = loop,
                ) { slide -> Slide(slide) }
            }
        }

        DemoSection(
            title = "Autoplay",
            description = "Autoplay advances every `interval` milliseconds; `draggable` " +
                "enables swipe and mouse-drag navigation.",
        ) {
            NCarousel(
                items = demosSlides,
                modifier = Modifier.fillMaxWidth().height(220.dp),
                autoplay = true,
                interval = 2000L,
                draggable = true,
                showArrow = true,
            ) { slide -> Slide(slide) }
        }

        DemoSection(
            title = "Dots",
            description = "Change the dot style and placement. Line dots expand for the " +
                "active slide.",
        ) {
            var dotType by remember { mutableStateOf(NCarouselDotType.Dot) }
            var placement by remember { mutableStateOf(NCarouselDotPlacement.Bottom) }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NFlex(size = 8.dp) {
                    NButton(onClick = {
                        dotType = if (dotType == NCarouselDotType.Dot) {
                            NCarouselDotType.Line
                        } else {
                            NCarouselDotType.Dot
                        }
                    }) { NText("Type: " + dotType.name.lowercase()) }
                    NButton(onClick = {
                        placement = when (placement) {
                            NCarouselDotPlacement.Bottom -> NCarouselDotPlacement.Top
                            NCarouselDotPlacement.Top -> NCarouselDotPlacement.Left
                            NCarouselDotPlacement.Left -> NCarouselDotPlacement.Right
                            NCarouselDotPlacement.Right -> NCarouselDotPlacement.Bottom
                        }
                    }) { NText("Placement: " + placement.name.lowercase()) }
                }
                NCarousel(
                    items = demosSlides,
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    dotType = dotType,
                    dotPlacement = placement,
                    showArrow = placement == NCarouselDotPlacement.Bottom ||
                        placement == NCarouselDotPlacement.Top,
                ) { slide -> Slide(slide) }
            }
        }

        DemoSection(
            title = "Vertical",
            description = "Set `direction` to vertical; the dots move to the right side.",
        ) {
            NCarousel(
                items = demosSlides,
                modifier = Modifier.fillMaxWidth().height(240.dp),
                direction = NCarouselDirection.Vertical,
                dotPlacement = NCarouselDotPlacement.Right,
                showArrow = true,
            ) { slide -> Slide(slide) }
        }

        DemoSection(
            title = "Space between",
            description = "`spaceBetween` adds a gap between slides.",
        ) {
            NCarousel(
                items = demosSlides,
                modifier = Modifier.fillMaxWidth().height(200.dp),
                spaceBetween = 20.dp,
                draggable = true,
            ) { slide -> Slide(slide) }
        }

        DemoSection(
            title = "Slides per view",
            description = "`slidesPerView = 3` shows three slides at a time. Use `loop = false` " +
                "when showing multiple slides.",
        ) {
            NCarousel(
                items = demosSlides,
                modifier = Modifier.fillMaxWidth().height(180.dp),
                slidesPerView = 3f,
                spaceBetween = 16.dp,
                loop = false,
                draggable = true,
            ) { slide -> Slide(slide) }
        }

        DemoSection(
            title = "Auto slides per view",
            description = "`autoSlideSize` sizes slides independently from the viewport, so " +
                "neighbouring slides can peek in.",
        ) {
            NCarousel(
                items = demosSlides,
                modifier = Modifier.fillMaxWidth().height(180.dp),
                autoSlideSize = true,
                spaceBetween = 16.dp,
                loop = false,
                draggable = true,
            ) { slide -> Slide(slide) }
        }

        DemoSection(
            title = "Centered",
            description = "`centeredSlides` keeps the active slide in the middle with " +
                "auto-sized slides.",
        ) {
            NCarousel(
                items = demosSlides,
                modifier = Modifier.fillMaxWidth().height(200.dp),
                autoSlideSize = true,
                centeredSlides = true,
                spaceBetween = 16.dp,
                loop = false,
                draggable = true,
            ) { slide -> Slide(slide) }
        }

        DemoSection(
            title = "Effect",
            description = "Switch between `Slide`, `Fade` and `Card` transitions.",
        ) {
            var effect by remember { mutableStateOf(NCarouselEffect.Slide) }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NFlex(size = 8.dp) {
                    NCarouselEffect.entries.forEach { option ->
                        NButton(onClick = { effect = option }) {
                            NText(option.name)
                        }
                    }
                }
                key(effect) {
                    NCarousel(
                        items = demosSlides,
                        modifier = Modifier.fillMaxWidth().height(230.dp),
                        effect = effect,
                        centeredSlides = effect == NCarouselEffect.Card,
                        draggable = true,
                        showDot = effect != NCarouselEffect.Card,
                        transitionDurationMillis = 400,
                    ) { slide -> Slide(slide) }
                }
            }
        }

        DemoSection(
            title = "Customizing transition",
            description = "`effect = Custom` accepts a `customTransform` that maps the page " +
                "offset to alpha, scale and translation.",
        ) {
            NCarousel(
                items = demosSlides,
                modifier = Modifier.fillMaxWidth().height(230.dp),
                effect = NCarouselEffect.Custom,
                showArrow = true,
                customTransform = { offset ->
                    val distance = abs(offset).coerceIn(0f, 1f)
                    NCarouselPageTransform(
                        alpha = 1f - distance,
                        scale = 0.8f + 0.2f * (1f - distance),
                    )
                },
            ) { slide -> Slide(slide) }
        }

        DemoSection(
            title = "Hover",
            description = "Set `trigger = Hover` to switch slides when the pointer moves over " +
                "the dots.",
        ) {
            NCarousel(
                items = demosSlides,
                modifier = Modifier.fillMaxWidth().height(220.dp),
                trigger = NCarouselTrigger.Hover,
            ) { slide -> Slide(slide) }
        }

        DemoSection(
            title = "Keyboard",
            description = "With `keyboard = true`, click the carousel and use the arrow keys. " +
                "Vertical mode uses the up/down keys.",
        ) {
            var direction by remember { mutableStateOf(NCarouselDirection.Horizontal) }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NButton(onClick = {
                    direction = if (direction == NCarouselDirection.Horizontal) {
                        NCarouselDirection.Vertical
                    } else {
                        NCarouselDirection.Horizontal
                    }
                }) { NText("Direction: " + direction.name.lowercase()) }
                NCarousel(
                    items = demosSlides,
                    modifier = Modifier.fillMaxWidth().height(240.dp),
                    keyboard = true,
                    direction = direction,
                    dotPlacement = if (direction == NCarouselDirection.Vertical) {
                        NCarouselDotPlacement.Right
                    } else {
                        NCarouselDotPlacement.Bottom
                    },
                ) { slide -> Slide(slide) }
            }
        }

        DemoSection(
            title = "Mousewheel",
            description = "`mousewheel` makes the carousel respond to scroll wheel input.",
        ) {
            NCarousel(
                items = demosSlides,
                modifier = Modifier.fillMaxWidth().height(240.dp),
                direction = NCarouselDirection.Vertical,
                dotPlacement = NCarouselDotPlacement.Right,
                mousewheel = true,
            ) { slide -> Slide(slide) }
        }

        DemoSection(
            title = "Simulate drag",
            description = "Set `draggable` to switch slides by dragging with the mouse.",
        ) {
            NCarousel(
                items = demosSlides,
                modifier = Modifier.fillMaxWidth().height(220.dp),
                draggable = true,
            ) { slide -> Slide(slide) }
        }

        DemoSection(
            title = "Customizing arrow and dots",
            description = "Provide `arrowContent` and `dotsContent` to replace the default " +
                "indicators.",
        ) {
            NCarousel(
                items = demosSlides,
                modifier = Modifier.fillMaxWidth().height(220.dp),
                autoplay = true,
                arrowContent = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                        Row(
                            Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            CustomArrowButton(previous = true, onClick = prev)
                            CustomArrowButton(previous = false, onClick = next)
                        }
                    }
                },
                dotsContent = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) {
                        Row(
                            Modifier.padding(20.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            repeat(total) { index ->
                                Box(
                                    Modifier
                                        .size(
                                            width = if (index == currentIndex) 40.dp else 12.dp,
                                            height = 4.dp,
                                        )
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            Color.White.copy(alpha = if (index == currentIndex) 1f else 0.4f),
                                        )
                                        .clickable { to(index) },
                                )
                            }
                        }
                    }
                },
            ) { slide -> Slide(slide) }
        }

        DemoSection(
            title = "Customize card",
            description = "The card effect stacks the neighbouring slides behind the active one.",
        ) {
            NCarousel(
                items = demosSlides,
                modifier = Modifier.fillMaxWidth().height(240.dp),
                effect = NCarouselEffect.Card,
                showDot = false,
                draggable = true,
            ) { slide -> Slide(slide) }
        }

        DemoSection(
            title = "Custom Dots",
            description = "A custom dots slot can render a counter instead of indicators.",
        ) {
            NCarousel(
                items = demosSlides,
                modifier = Modifier.fillMaxWidth().height(220.dp),
                autoplay = true,
                dotsContent = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                        NText(
                            "${currentIndex + 1} / $total",
                            modifier = Modifier.padding(bottom = 12.dp),
                            color = Color.White,
                            size = 12.sp,
                        )
                    }
                },
            ) { slide -> Slide(slide) }
        }

        DemoSection(
            title = "Controlled index",
            description = "Pass `currentIndex` to control the active slide and observe " +
                "changes through `onIndexChange`.",
        ) {
            var index by remember { mutableStateOf(0) }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NFlex(size = 8.dp, wrap = true) {
                    NButton(onClick = { index = (index - 1 + demosSlides.size) % demosSlides.size }) {
                        NText("Previous")
                    }
                    NButton(onClick = { index = (index + 1) % demosSlides.size }) {
                        NText("Next")
                    }
                    NTag("Current index: $index", type = NStatus.Primary)
                }
                NCarousel(
                    items = demosSlides,
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    currentIndex = index,
                    onIndexChange = { index = it },
                    showArrow = true,
                ) { slide -> Slide(slide) }
            }
        }
    }
}
