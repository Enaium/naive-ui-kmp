package cn.enaium.naiveui.sample.gallery.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NGradient
import cn.enaium.naiveui.components.NGradientText
import cn.enaium.naiveui.components.NGradientTextType
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage

@Composable
fun GradientTextDocs() {
    DocsPage(
        title = "Gradient Text",
        description = "Text painted with a gradient brush, either from semantic type " +
            "presets or from custom colours and angles.",
    ) {
        DemoSection(
            title = "Type",
            description = "Each semantic type maps to the corresponding theme colour.",
            code = """
NGradientText("Boom!", type = NGradientTextType.Error)
NGradientText("Emmm", type = NGradientTextType.Info)
""".trimIndent(),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                NGradientText("Boom!", type = NGradientTextType.Error)
                NGradientText("Emmm", type = NGradientTextType.Info)
                NGradientText("Oops!", type = NGradientTextType.Warning)
                NGradientText("Haha!", type = NGradientTextType.Success)
                NGradientText("Primary", type = NGradientTextType.Primary)
            }
        }

        DemoSection(
            title = "Size & weight",
            description = "Set `size` to override the font size; `strong = false` uses the " +
                "regular weight.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                NGradientText("Live Forever", type = NGradientTextType.Info, size = 24.sp)
                NGradientText(
                    "Live Forever",
                    type = NGradientTextType.Error,
                    size = 24.sp,
                    strong = false,
                )
                NGradientText(
                    "Married with Children",
                    type = NGradientTextType.Warning,
                    size = 32.sp,
                )
                NGradientText(
                    "Back in the USSR",
                    type = NGradientTextType.Success,
                    size = 32.sp,
                )
            }
        }

        DemoSection(
            title = "Custom gradient",
            description = "Pass an `NGradient` with any number of colour stops. The angle " +
                "uses the CSS convention (0 points up, 90 points right).",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                NGradientText(
                    "Custom Color",
                    gradient = NGradient(
                        colors = listOf(Color(0xFF555555), Color(0xFFAAAAAA)),
                        angle = 0f,
                    ),
                    size = 24.sp,
                )
                NGradientText(
                    "Custom Color",
                    gradient = NGradient(
                        colors = listOf(Color(0xFF555555), Color(0xFFAAAAAA)),
                        angle = 180f,
                    ),
                    size = 24.sp,
                )
                NGradientText(
                    "A Scribbled Color",
                    gradient = NGradient(
                        colors = listOf(Color.Red, Color.Green, Color.Blue),
                        angle = 90f,
                    ),
                    size = 24.sp,
                )
            }
        }

        DemoSection(
            title = "Custom brush",
            description = "For full control, pass any Compose `Brush`.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                NGradientText(
                    "Sweeping rainbow",
                    brush = Brush.linearGradient(
                        listOf(
                            Color(0xFFFF0080),
                            Color(0xFFFF8C00),
                            Color(0xFF00C853),
                            Color(0xFF2979FF),
                        ),
                    ),
                    size = 26.sp,
                )
                NText(
                    "The brush is applied to the glyphs, so the background stays transparent.",
                )
            }
        }
    }
}
