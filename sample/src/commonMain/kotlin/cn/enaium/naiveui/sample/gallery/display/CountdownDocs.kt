package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NSwitch
import cn.enaium.naiveui.components.NCountdown
import cn.enaium.naiveui.components.NStatistic
import cn.enaium.naiveui.components.rememberNCountdownState
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

@Composable
fun CountdownDocs() {
    DocsPage(
        title = "Countdown",
        description = "Countdown renders a live HH:mm:ss timer from a duration or an absolute target, with optional sub-second precision and custom rendering.",
    ) {
        DemoSection(
            title = "Basic",
            description = "duration is in milliseconds. Toggle active to start and pause the countdown.",
            code = """
var active by remember { mutableStateOf(false) }
Row {
    NCountdown(duration = 5000, active = active)
    NSwitch(checked = active, onCheckedChange = { active = it })
}
""",
        ) {
            var active by remember { mutableStateOf(false) }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NCountdown(duration = 5000L, active = active)
                NSwitch(checked = active, onCheckedChange = { active = it })
            }
        }

        DemoSection(
            title = "Precision",
            description = "precision 0 shows seconds, while 1 to 3 shows tenths, hundredths and milliseconds.",
            code = """
NCountdown(duration = 3_600_000, precision = 0)
NCountdown(duration = 3_600_000, precision = 1)
NCountdown(duration = 3_600_000, precision = 2)
NCountdown(duration = 3_600_000, precision = 3)
""",
        ) {
            var active by remember { mutableStateOf(false) }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    NCountdown(duration = 3_600_000L, active = active, precision = 0)
                    NCountdown(duration = 3_600_000L, active = active, precision = 1)
                    NCountdown(duration = 3_600_000L, active = active, precision = 2)
                    NCountdown(duration = 3_600_000L, active = active, precision = 3)
                }
                NSwitch(checked = active, onCheckedChange = { active = it })
            }
        }

        DemoSection(
            title = "Render",
            description = "format maps the time parts to a string; render replaces the text entirely with composable content.",
            code = """
NCountdown(
    duration = 996_000,
    active = active,
    format = { info -> info.minutes.toString() + "m " + info.seconds + "s" },
)
NCountdown(
    duration = 996_000,
    active = active,
    render = { info ->
        NText(countdownPart(info.hours) + ":" + countdownPart(info.minutes))
    },
)
""",
        ) {
            var active by remember { mutableStateOf(false) }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NStatistic(
                    label = "I'm free to be whatever I",
                    tabularNumbers = true,
                    content = {
                        NCountdown(
                            duration = 996_000L,
                            active = active,
                            render = { info ->
                                NText(
                                    countdownPart(info.hours) + ":" +
                                        countdownPart(info.minutes) + ":" +
                                        countdownPart(info.seconds),
                                )
                            },
                        )
                    },
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NCountdown(
                        duration = 996_000L,
                        active = active,
                        format = { info -> "${info.minutes}m ${info.seconds}s" },
                    )
                    NSwitch(checked = active, onCheckedChange = { active = it })
                }
            }
        }

        DemoSection(
            title = "Reset",
            description = "Keep a countdown state and call reset() to start the timer again from its duration.",
            code = """
val state = rememberNCountdownState()
var active by remember { mutableStateOf(true) }

Row {
    NCountdown(duration = 86_400_000, active = active, state = state)
    NButton(size = NSize.Tiny, onClick = { state.reset() }) { NText("Reset") }
    NSwitch(checked = active, onCheckedChange = { active = it })
}
""",
        ) {
            val state = rememberNCountdownState()
            var active by remember { mutableStateOf(true) }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NCountdown(duration = 86_400_000L, active = active, state = state)
                NButton(size = NSize.Tiny, onClick = { state.reset() }) { NText("Reset") }
                NSwitch(checked = active, onCheckedChange = { active = it })
            }
        }

        DemoSection(
            title = "Finish",
            description = "onFinish runs once when the timer reaches zero. Reset the state to run it again.",
            code = """
val state = rememberNCountdownState()
var active by remember { mutableStateOf(true) }
var finished by remember { mutableStateOf(false) }

NCountdown(
    duration = 3000,
    active = active,
    state = state,
    onFinish = { finished = true },
)
NButton(onClick = {
    state.reset()
    finished = false
}) { NText("Restart") }
""",
        ) {
            val state = rememberNCountdownState()
            var active by remember { mutableStateOf(true) }
            var finished by remember { mutableStateOf(false) }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NCountdown(
                    duration = 3000L,
                    active = active,
                    state = state,
                    onFinish = { finished = true },
                )
                NText(if (finished) "Finished" else "Running")
                NButton(
                    size = NSize.Small,
                    onClick = {
                        state.reset()
                        finished = false
                    },
                ) { NText("Restart") }
                NSwitch(checked = active, onCheckedChange = { active = it })
            }
        }
    }
}

private fun countdownPart(value: Int): String = value.toString().padStart(2, '0')
