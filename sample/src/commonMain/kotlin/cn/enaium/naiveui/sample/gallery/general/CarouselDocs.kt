package cn.enaium.naiveui.sample.gallery.general

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NCarousel
import cn.enaium.naiveui.components.NCarouselDotPlacement
import cn.enaium.naiveui.components.NCarouselDotType
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NFlex
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NStatus

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
fun CarouselDocs() {
    DocsPage(
        title = "Carousel",
        description = "A slideshow for cycling through a series of content, with " +
            "autoplay, arrows, configurable dots and drag support.",
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
            title = "Autoplay & draggable",
            description = "Autoplay advances every `interval` milliseconds; `draggable` " +
                "enables swipe/drag navigation.",
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
