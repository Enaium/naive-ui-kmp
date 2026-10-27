package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NSlider
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize
import kotlin.math.roundToInt

@Composable
fun SliderDocs() {
    DocsPage(
        title = "Slider",
        description = "Sliders select a numeric value by dragging a handle along a track. They support ranges, marks, tooltips and vertical orientation.",
    ) {
        DemoSection(
            title = "Basic",
            description = "The slider snaps to step increments and reports the new value through onValueChange.",
            code = """
var value by remember { mutableStateOf(50f) }

NSlider(value, { value = it }, step = 10f)
""",
        ) {
            var value by remember { mutableStateOf(50f) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NSlider(value, { value = it }, step = 10f)
                NText("Value: ${value.roundToInt()}")
            }
        }

        DemoSection(
            title = "Range",
            description = "Provide secondValue and onSecondValueChange to select an interval.",
            code = """
var start by remember { mutableStateOf(50f) }
var end by remember { mutableStateOf(70f) }

NSlider(start, { start = it }, secondValue = end, onSecondValueChange = { end = it })
""",
        ) {
            var start by remember { mutableStateOf(50f) }
            var end by remember { mutableStateOf(70f) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NSlider(
                    start,
                    { start = it },
                    secondValue = end,
                    onSecondValueChange = { end = it },
                )
                NText("Range: ${start.roundToInt()} – ${end.roundToInt()}")
            }
        }

        DemoSection(
            title = "Marks",
            description = "Marks add labels and dots to the track. The dots between the selected handles are highlighted.",
            code = """
val marks = mapOf(34f to "Amazing", 75f to "Good")

NSlider(value, { value = it }, secondValue = end, onSecondValueChange = { end = it }, marks = marks)
""",
        ) {
            var start by remember { mutableStateOf(20f) }
            var end by remember { mutableStateOf(60f) }
            val marks = mapOf(34f to "Amazing", 75f to "Good")
            NSlider(
                start,
                { start = it },
                secondValue = end,
                onSecondValueChange = { end = it },
                marks = marks,
            )
        }

        DemoSection(
            title = "Restrict selectable values",
            description = "Set snapToMarks = true to snap the handle to the provided marks only.",
        ) {
            var temperature by remember { mutableStateOf(20f) }
            val marks = mapOf(
                0f to "0°C",
                20f to "20°C",
                37f to "37°C",
                100f to "100°C",
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NSlider(
                    temperature,
                    { temperature = it },
                    marks = marks,
                    snapToMarks = true,
                    step = 0f,
                )
                NText("Temperature: ${temperature.roundToInt()}°C")
            }
        }

        DemoSection(
            title = "Tooltip",
            description = "showTooltip keeps the indicator visible; tooltipFormatter customizes its text. The indicator also follows hover and drag by default.",
            code = """
NSlider(
    value, { value = it },
    showTooltip = true,
    tooltipFormatter = { "${'$'}it%" },
)
""",
        ) {
            var value by remember { mutableStateOf(50f) }
            NSpace(vertical = true, size = 18.dp) {
                NSlider(
                    value,
                    { value = it },
                    step = 10f,
                    tooltipFormatter = { "${it.roundToInt()}%" },
                )
                NSlider(
                    value,
                    { value = it },
                    step = 10f,
                    showTooltip = true,
                    tooltipFormatter = { "${it.roundToInt()}%" },
                )
            }
        }

        DemoSection(
            title = "Vertical",
            description = "Vertical sliders fill the height of their container, so constrain the height when needed.",
        ) {
            var single by remember { mutableStateOf(77f) }
            var reversed by remember { mutableStateOf(20f) }
            var rangeStart by remember { mutableStateOf(20f) }
            var rangeEnd by remember { mutableStateOf(70f) }
            val marks = mapOf(
                0f to "0°C",
                20f to "20°C",
                37f to "37°C",
                100f to "100°C",
            )
            Box(Modifier.height(220.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NSlider(single, { single = it }, vertical = true, showTooltip = true)
                    NSlider(
                        rangeStart,
                        { rangeStart = it },
                        secondValue = rangeEnd,
                        onSecondValueChange = { rangeEnd = it },
                        marks = marks,
                        vertical = true,
                    )
                    NSlider(reversed, { reversed = it }, vertical = true, reverse = true)
                    NSlider(30f, {}, vertical = true, disabled = true)
                }
            }
        }

        DemoSection(
            title = "Disabled and reverse",
            description = "Disabled sliders are dimmed and ignore input. reverse flips the direction of the track.",
        ) {
            var value by remember { mutableStateOf(40f) }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                NSlider(value, { value = it }, disabled = true)
                var reversedValue by remember { mutableStateOf(40f) }
                NSlider(reversedValue, { reversedValue = it }, reverse = true)
            }
        }

        DemoSection(
            title = "Size",
            description = "The handle size follows the slider size.",
        ) {
            var small by remember { mutableStateOf(30f) }
            var medium by remember { mutableStateOf(50f) }
            var large by remember { mutableStateOf(70f) }
            Column(
                modifier = Modifier.width(320.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                NSlider(small, { small = it }, size = NSize.Small)
                NSlider(medium, { medium = it }, size = NSize.Medium)
                NSlider(large, { large = it }, size = NSize.Large)
            }
        }
    }
}
