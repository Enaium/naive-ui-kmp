package cn.enaium.naiveui.sample.gallery.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.components.NStep
import cn.enaium.naiveui.components.NStepContentPlacement
import cn.enaium.naiveui.components.NStepStatus
import cn.enaium.naiveui.components.NSteps
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize

private val songSteps = listOf(
    NStep("I Me Mine", "All through the day, I me mine I me mine, I me mine"),
    NStep("Let It Be", "When I find myself in times of trouble Mother Mary comes to me"),
    NStep("Come Together", "Here come old flat top He come grooving up slowly"),
    NStep("Something", "Something in the way she moves Attracts me like no other lover"),
)

@Composable
private fun StepControls(
    current: Int,
    count: Int,
    status: NStepStatus,
    onCurrentChange: (Int) -> Unit,
    onStatusChange: (NStepStatus) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NButton(onClick = { onCurrentChange((current - 1).coerceAtLeast(1)) }) {
                NText("Previous")
            }
            NButton(onClick = { onCurrentChange((current + 1).coerceAtMost(count)) }) {
                NText("Next")
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NStepStatus.entries.forEach { option ->
                NButton(onClick = { onStatusChange(option) }) {
                    NText(
                        when (option) {
                            NStepStatus.Wait -> "Wait"
                            NStepStatus.Process -> "Process"
                            NStepStatus.Finish -> "Finish"
                            NStepStatus.Error -> "Error"
                        },
                    )
                }
            }
        }
        NText("Current status: $status", color = LocalNTheme.current.colors.text3)
    }
}

@Composable
fun StepsDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Steps",
        description = "Steps visualize progress through an ordered process, horizontally or vertically, with per-step statuses, custom icons and clickable navigation.",
    ) {
        DemoSection(
            title = "Basic",
            description = "`current` is 1-based. `status` controls the active step's state.",
            code = """
NSteps(
    steps = steps,
    current = current,
    status = NStepStatus.Process,
)
            """.trimIndent(),
        ) {
            var current by remember { mutableStateOf(2) }
            var status by remember { mutableStateOf(NStepStatus.Process) }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                NSteps(steps = songSteps, current = current, status = status)
                StepControls(
                    current = current,
                    count = songSteps.size,
                    status = status,
                    onCurrentChange = { current = it },
                    onStatusChange = { status = it },
                )
            }
        }

        DemoSection(
            title = "Size",
            description = "Steps support small and medium sizes.",
        ) {
            var current by remember { mutableStateOf(2) }
            var status by remember { mutableStateOf(NStepStatus.Process) }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                NSteps(
                    steps = songSteps,
                    current = current,
                    status = status,
                    size = NSize.Small,
                )
                StepControls(
                    current = current,
                    count = songSteps.size,
                    status = status,
                    onCurrentChange = { current = it },
                    onStatusChange = { status = it },
                )
            }
        }

        DemoSection(
            title = "Vertical",
            description = "Vertical steps stack downward with a connector between indicators.",
        ) {
            val steps = songSteps.toMutableList().apply {
                add(2, NStep("Break"))
            }
            var current by remember { mutableStateOf(3) }
            var status by remember { mutableStateOf(NStepStatus.Process) }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                NSteps(
                    steps = steps,
                    current = current,
                    status = status,
                    vertical = true,
                )
                StepControls(
                    current = current,
                    count = steps.size,
                    status = status,
                    onCurrentChange = { current = it },
                    onStatusChange = { status = it },
                )
            }
        }

        DemoSection(
            title = "Content placement",
            description = "Place the text to the right of the indicator or below it.",
        ) {
            var current by remember { mutableStateOf(2) }
            var status by remember { mutableStateOf(NStepStatus.Process) }
            var placement by remember { mutableStateOf(NStepContentPlacement.Bottom) }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NButton(onClick = { placement = NStepContentPlacement.Right }) {
                        NText("Right")
                    }
                    NButton(onClick = { placement = NStepContentPlacement.Bottom }) {
                        NText("Bottom")
                    }
                }
                NSteps(
                    steps = songSteps,
                    current = current,
                    status = status,
                    contentPlacement = placement,
                )
                StepControls(
                    current = current,
                    count = songSteps.size,
                    status = status,
                    onCurrentChange = { current = it },
                    onStatusChange = { status = it },
                )
            }
        }

        DemoSection(
            title = "Content slot",
            description = "Each step can render custom content below its title, such as a Next button.",
        ) {
            var current by remember { mutableStateOf(1) }
            var status by remember { mutableStateOf(NStepStatus.Process) }
            val steps = listOf(
                NStep(
                    title = "I Me Mine",
                    content = {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            NText("All through the day, I me mine I me mine, I me mine")
                            if (current == 1) {
                                NButton(
                                    onClick = { current = 2 },
                                    size = NSize.Small,
                                ) { NText("Next") }
                            }
                        }
                    },
                ),
                NStep(
                    title = "Let It Be",
                    content = {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            NText("When I find myself in times of trouble Mother Mary comes to me")
                            if (current == 2) {
                                NButton(onClick = { current = 3 }, size = NSize.Small) {
                                    NText("Next")
                                }
                            }
                        }
                    },
                ),
                NStep(
                    title = "Come Together",
                    content = {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            NText("Here come old flat top He come grooving up slowly")
                            if (current == 3) {
                                NButton(onClick = { current = 4 }, size = NSize.Small) {
                                    NText("Next")
                                }
                            }
                        }
                    },
                ),
                NStep(
                    title = "Something",
                    content = {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            NText("Something in the way she moves Attracts me like no other lover")
                            if (current == 4) {
                                NButton(onClick = { current = 1 }, size = NSize.Small) {
                                    NText("Restart")
                                }
                            }
                        }
                    },
                ),
            )
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                NSteps(steps = steps, current = current, status = status)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NStepStatus.entries.forEach { option ->
                        NButton(onClick = { status = option }) {
                            NText(
                                when (option) {
                                    NStepStatus.Wait -> "Wait"
                                    NStepStatus.Process -> "Process"
                                    NStepStatus.Finish -> "Finish"
                                    NStepStatus.Error -> "Error"
                                },
                            )
                        }
                    }
                }
            }
        }

        DemoSection(
            title = "Custom icon",
            description = "Override the finish and error icons globally, and each step's icon individually.",
        ) {
            var current by remember { mutableStateOf(1) }
            var status by remember { mutableStateOf(NStepStatus.Finish) }
            val steps = listOf(
                songSteps[0],
                songSteps[1],
                songSteps[2],
                NStep(
                    title = "Something",
                    description = "Something in the way she moves Attracts me like no other lover",
                    icon = {
                        NIcon(NIcons.Info, size = 14.dp, color = theme.colors.primary)
                    },
                ),
            )
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                NSteps(
                    steps = steps,
                    current = current,
                    status = status,
                    finishIcon = {
                        NIcon(NIcons.Success, size = 16.dp, color = theme.colors.primary)
                    },
                    errorIcon = {
                        NIcon(NIcons.Close, size = 14.dp, color = theme.colors.error)
                    },
                )
                StepControls(
                    current = current,
                    count = steps.size,
                    status = status,
                    onCurrentChange = { current = it },
                    onStatusChange = { status = it },
                )
            }
        }

        DemoSection(
            title = "Click to switch",
            description = "Providing `onCurrentChange` makes steps clickable. Disabled steps are ignored.",
            code = """
NSteps(
    steps = steps,
    current = current,
    onCurrentChange = { current = it },
)
            """.trimIndent(),
        ) {
            var current by remember { mutableStateOf(1) }
            val steps = listOf(
                songSteps[0],
                songSteps[1],
                songSteps[2],
                NStep(
                    title = "Something",
                    description = "Something in the way she moves Attracts me like no other lover",
                    disabled = true,
                ),
            )
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NSteps(
                    steps = steps,
                    current = current,
                    onCurrentChange = { current = it },
                )
                NText("Selected step: $current", color = theme.colors.text3)
            }
        }
    }
}
