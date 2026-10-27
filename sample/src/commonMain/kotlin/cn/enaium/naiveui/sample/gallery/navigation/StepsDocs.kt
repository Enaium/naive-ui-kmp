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
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NStep
import cn.enaium.naiveui.components.NStepStatus
import cn.enaium.naiveui.components.NSteps
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

private val checkoutSteps = listOf(
    NStep("Cart", "Review the items in the order."),
    NStep("Shipping", "Choose an address and delivery method."),
    NStep("Payment", "Enter a card or choose a saved one."),
    NStep("Done", "The order is confirmed."),
)

@Composable
fun StepsDocs() {
    DocsPage(
        title = "Steps",
        description = "Steps visualize progress through an ordered process, horizontally or vertically, with per-step status overrides.",
    ) {
        DemoSection(
            title = "Basic",
            description = "`current` is 1-based. Use `status` to render the active step as process, wait, finish or error.",
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
                NSteps(
                    steps = checkoutSteps,
                    current = current,
                    status = status,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NButton(
                        onClick = { current = (current - 1).coerceAtLeast(1) },
                    ) { NText("Previous") }
                    NButton(
                        onClick = { current = (current + 1).coerceAtMost(checkoutSteps.size) },
                    ) { NText("Next") }
                }
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
            title = "Vertical",
            description = "Vertical steps stack down the page with a connector between indicators.",
        ) {
            var current by remember { mutableStateOf(3) }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                NSteps(
                    steps = checkoutSteps,
                    current = current,
                    vertical = true,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NButton(onClick = { current = (current - 1).coerceAtLeast(1) }) {
                        NText("Previous")
                    }
                    NButton(onClick = { current = (current + 1).coerceAtMost(checkoutSteps.size) }) {
                        NText("Next")
                    }
                }
            }
        }

        DemoSection(
            title = "Status and disabled",
            description = "Per-step status wins over the global status. Disabled steps are dimmed.",
        ) {
            NSteps(
                steps = listOf(
                    NStep("Finished", "This step is complete.", status = NStepStatus.Finish),
                    NStep("Active", "Currently processing.", status = NStepStatus.Process),
                    NStep("Failed", "Something went wrong.", status = NStepStatus.Error),
                    NStep("Skipped", "Not available yet.", disabled = true),
                ),
                current = 2,
            )
        }

        DemoSection(
            title = "Sizes",
            description = "Small, medium and large steps.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                NSteps(steps = checkoutSteps.take(3), current = 2, size = NSize.Small)
                NSteps(steps = checkoutSteps.take(3), current = 2, size = NSize.Large)
            }
        }
    }
}
