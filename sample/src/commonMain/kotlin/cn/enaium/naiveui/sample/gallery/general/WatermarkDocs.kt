package cn.enaium.naiveui.sample.gallery.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NCard
import cn.enaium.naiveui.components.NDivider
import cn.enaium.naiveui.components.NSlider
import cn.enaium.naiveui.components.NSwitch
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NWatermark
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage

private const val SAMPLE_COPY =
    "The underlying logic is to break through information barriers and create a new " +
        "industry ecosystem. The top-level design focuses on user perception tracks, " +
        "reaching a tipping point through differentiation and granularity."

@Composable
private fun WatermarkedCard(
    text: String,
    rotate: Float = 0f,
    horizontalGap: androidx.compose.ui.unit.Dp = 0.dp,
    verticalGap: androidx.compose.ui.unit.Dp = 0.dp,
    fontSize: androidx.compose.ui.unit.TextUnit = 14.sp,
    fontColor: Color = Color(128, 128, 128).copy(alpha = 0.3f),
    cross: Boolean = false,
) {
    NWatermark(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        fontSize = fontSize,
        fontColor = fontColor,
        rotate = rotate,
        horizontalGap = horizontalGap,
        verticalGap = verticalGap,
        cross = cross,
        offset = DpOffset(12.dp, 24.dp),
    ) {
        NCard(title = "Quarterly review") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NText(SAMPLE_COPY)
                NDivider()
                NText("Review · Empower · Collaborate · Connect")
                NText("Align · Breakthrough · Establish · Fulfill")
            }
        }
    }
}

@Composable
fun WatermarkDocs() {
    DocsPage(
        title = "Watermark",
        description = "Repeats a rotated text watermark across any content. The watermark " +
            "is painted above the content and never blocks pointer input.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Set the text, cell size and rotation.",
            code = """
NWatermark(
    text = "Watermark",
    rotate = -15f,
    horizontalGap = 0.dp,
) {
    NCard { ... }
}
""".trimIndent(),
        ) {
            WatermarkedCard(text = "Watermark", rotate = -15f)
        }

        DemoSection(
            title = "Font",
            description = "Customise the font size and colour.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                WatermarkedCard(
                    text = "Naive UI",
                    rotate = -15f,
                    fontSize = 22.sp,
                    fontColor = Color(64, 120, 200).copy(alpha = 0.4f),
                )
            }
        }

        DemoSection(
            title = "Multiline",
            description = "Text separated by `\\n` is drawn as multiple lines inside each tile.",
        ) {
            WatermarkedCard(
                text = "Naive UI\nQuite Interesting...",
                rotate = -15f,
                fontSize = 16.sp,
            )
        }

        DemoSection(
            title = "Rotate & gap",
            description = "Adjust the rotation and the gaps between watermark cells. " +
                "`cross` offsets alternate rows for a denser pattern.",
        ) {
            WatermarkedCard(
                text = "Confidential",
                rotate = -30f,
                horizontalGap = 24.dp,
                verticalGap = 16.dp,
                fontSize = 15.sp,
                cross = true,
            )
        }

        DemoSection(
            title = "Offset",
            description = "The `offset` shifts the text inside each cell so the pattern can " +
                "be aligned with the content.",
        ) {
            NWatermark(
                text = "Internal",
                modifier = Modifier.fillMaxWidth(),
                rotate = -20f,
                horizontalGap = 12.dp,
                verticalGap = 12.dp,
                offset = DpOffset(48.dp, 48.dp),
                cross = true,
            ) {
                NCard(title = "Notes") {
                    NText(
                        "Offsets are applied per tile, which lets you push the mark away " +
                            "from the top-left corner of the content.",
                        Modifier.padding(vertical = 4.dp),
                    )
                }
            }
        }
        DemoSection(
            title = "Image",
            description = "Provide an image URI to tile a logo instead of text. imageWidth, " +
                "imageHeight and imageOpacity control the repeated artwork.",
        ) {
            NWatermark(
                modifier = Modifier.fillMaxWidth(),
                image = "https://picsum.photos/seed/naive/48/48",
                imageWidth = 40.dp,
                imageHeight = 40.dp,
                imageOpacity = 0.35f,
                width = 120.dp,
                height = 96.dp,
                rotate = -18f,
            ) {
                NCard(title = "Image watermark") {
                    NText("The watermark layer accepts remote images loaded through Coil.")
                }
            }
        }

        DemoSection(
            title = "Fullscreen",
            description = "`fullscreen` makes the watermark fill the available area.",
        ) {
            var show by remember { mutableStateOf(false) }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NSwitch(checked = show, onCheckedChange = { show = it })
                if (show) {
                    Box(Modifier.fillMaxWidth().height(220.dp)) {
                        NWatermark(
                            text = "All the roads we have to walk are winding",
                            fullscreen = true,
                            cross = true,
                            rotate = -15f,
                            fontSize = 16.sp,
                            width = 384.dp,
                            height = 384.dp,
                        ) {
                            NCard(title = "Fullscreen watermark") {
                                NText("The watermark layer fills the available area.")
                            }
                        }
                    }
                }
            }
        }

        DemoSection(
            title = "Custom watermark",
            description = "Tune font size, rotation, gaps and global rotation interactively.",
        ) {
            var fontSize by remember { mutableStateOf(16f) }
            var rotate by remember { mutableStateOf(-15f) }
            var gap by remember { mutableStateOf(0f) }
            var globalRotate by remember { mutableStateOf(0f) }
            var cross by remember { mutableStateOf(true) }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NWatermark(
                    text = "Custom Watermark",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = fontSize.sp,
                    rotate = rotate,
                    globalRotate = globalRotate,
                    horizontalGap = gap.dp,
                    verticalGap = gap.dp,
                    cross = cross,
                    width = 192.dp,
                    height = 128.dp,
                ) {
                    NCard(title = "Preview") { NText(SAMPLE_COPY) }
                }
                NText("Font size: ${fontSize.toInt()}")
                NSlider(value = fontSize, onValueChange = { fontSize = it }, min = 8f, max = 32f)
                NText("Rotate: ${rotate.toInt()}")
                NSlider(value = rotate, onValueChange = { rotate = it }, min = -90f, max = 90f)
                NText("Gap: ${gap.toInt()}")
                NSlider(value = gap, onValueChange = { gap = it }, min = 0f, max = 40f)
                NText("Global rotate: ${globalRotate.toInt()}")
                NSlider(
                    value = globalRotate,
                    onValueChange = { globalRotate = it },
                    min = -180f,
                    max = 180f,
                )
                NText("Cross: ${if (cross) "on" else "off"}")
                NSwitch(checked = cross, onCheckedChange = { cross = it })
            }
        }

        DemoSection(
            title = "Global rotation",
            description = "globalRotate rotates the entire tiled pattern on top of each tile's " +
                "own rotate value.",
        ) {
            NWatermark(
                modifier = Modifier.fillMaxWidth(),
                text = "Naive UI",
                rotate = -15f,
                globalRotate = 10f,
                horizontalGap = 16.dp,
                verticalGap = 16.dp,
            ) {
                NCard(title = "Rotated pattern") {
                    NText("Both the pattern grid and the individual watermark are rotated.")
                }
            }
        }
    }
}
