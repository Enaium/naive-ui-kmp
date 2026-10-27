package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NSlider
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import kotlin.math.roundToInt

@Composable
fun SliderDocs() {
    DocsPage(
        title = "Slider",
        description = "Sliders select a numeric value by dragging a handle along a track. They support ranges, marks, tooltips, custom handles and vertical orientation.",
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
            title = "Mark",
            description = "Marks add labels and dots to the track. The dots between the selected handles are highlighted.",
            code = """
val marks = mapOf(34f to "Amazing", 75f to "Good")

NSlider(start, { start = it }, secondValue = end, onSecondValueChange = { end = it }, marks = marks)
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
            title = "Disabled",
            description = "A disabled slider is dimmed and ignores mouse and keyboard input.",
        ) {
            var value by remember { mutableStateOf(40f) }
            NSlider(value, { value = it }, disabled = true)
        }

        DemoSection(
            title = "Disable tooltip",
            description = "Set tooltip = false to suppress the indicator shown while hovering or dragging.",
            code = """
NSlider(value, { value = it }, step = 10f, tooltip = false)
""",
        ) {
            var value by remember { mutableStateOf(50f) }
            NSlider(value, { value = it }, step = 10f, tooltip = false)
        }

        DemoSection(
            title = "Format tooltip",
            description = "tooltipFormatter receives the current value and returns the indicator text.",
            code = """
NSlider(
    value, { value = it },
    tooltipFormatter = { "${'$'}{it.roundToInt()}%" },
)
""",
        ) {
            var value by remember { mutableStateOf(50f) }
            NSlider(
                value,
                { value = it },
                step = 10f,
                tooltipFormatter = { "${it.roundToInt()}%" },
            )
        }

        DemoSection(
            title = "Reverse",
            description = "reverse flips the direction of the track.",
        ) {
            var value by remember { mutableStateOf(40f) }
            NSlider(value, { value = it }, reverse = true)
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
            title = "Always show tooltip",
            description = "showTooltip keeps the indicator visible even when the slider is not hovered or dragged.",
        ) {
            var value by remember { mutableStateOf(50f) }
            NSlider(value, { value = it }, showTooltip = true)
        }

        DemoSection(
            title = "More than 2 handles",
            description = "extraValues adds further draggable handles; onExtraValuesChange reports their new positions.",
            code = """
var handles by remember { mutableStateOf(listOf(50f, 70f, 80f, 90f)) }

NSlider(
    value = handles[0],
    onValueChange = { handles = handles.toMutableList().also { it[0] = ${'$'}it } },
    secondValue = handles[1],
    onSecondValueChange = { v -> handles = handles.toMutableList().also { it[1] = v } },
    extraValues = handles.drop(2),
    onExtraValuesChange = { extras -> handles = handles.take(2) + extras },
)
""",
        ) {
            var handles by remember { mutableStateOf(listOf(50f, 70f, 80f, 90f)) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NSlider(
                    value = handles[0],
                    onValueChange = { v -> handles = handles.toMutableList().also { it[0] = v } },
                    secondValue = handles[1],
                    onSecondValueChange = { v -> handles = handles.toMutableList().also { it[1] = v } },
                    extraValues = handles.drop(2),
                    onExtraValuesChange = { extras -> handles = handles.take(2) + extras },
                )
                NText("Handles: " + handles.joinToString(", ") { it.roundToInt().toString() })
            }
        }

        DemoSection(
            title = "Custom thumb",
            description = "The thumb slot replaces the default handle with arbitrary content.",
            code = """
NSlider(
    value, { value = it },
    thumb = { v ->
        Box(Modifier.size(24.dp).clip(CircleShape).background(primary)) {
            NText(v.roundToInt().toString(), color = Color.White, size = 10.sp)
        }
    },
)
""",
        ) {
            var value by remember { mutableStateOf(50f) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NSlider(
                    value,
                    { value = it },
                    step = 10f,
                    thumb = { v ->
                        Box(
                            Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(LocalNTheme.current.colors.primary),
                            contentAlignment = Alignment.Center,
                        ) {
                            NText(v.roundToInt().toString(), color = Color.White, size = 10.sp)
                        }
                    },
                )
                NText("Value: ${value.roundToInt()}")
            }
        }

        DemoSection(
            title = "Custom marks",
            description = "markContent replaces mark labels with custom composables that receive the value and its active state.",
            code = """
NSlider(
    start, { start = it },
    secondValue = end,
    onSecondValueChange = { end = it },
    vertical = true,
    marks = marks,
    markContent = { value, active ->
        NText("${'$'}{value.roundToInt()}°C", color = if (active) primary else gray)
    },
)
""",
        ) {
            var start by remember { mutableStateOf(20f) }
            var end by remember { mutableStateOf(70f) }
            val marks = mapOf(
                0f to "0°C",
                20f to "20°C",
                37f to "37°C",
                100f to "100°C",
            )
            Box(Modifier.height(240.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NSlider(
                        start,
                        { start = it },
                        secondValue = end,
                        onSecondValueChange = { end = it },
                        vertical = true,
                        marks = marks,
                        markContent = { value, active ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (active) Color(0xFF2080F0) else Color(0xFFC0C0C0),
                                        ),
                                )
                                NText(
                                    " ${value.roundToInt()}°C",
                                    size = 12.sp,
                                    color = if (active) Color(0xFF2080F0) else Color(0xFF909090),
                                )
                            }
                        },
                    )
                }
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
