package cn.enaium.naiveui.sample.gallery.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NButtonType
import cn.enaium.naiveui.components.NModal
import cn.enaium.naiveui.components.NModalPlacement
import cn.enaium.naiveui.components.NModalPreset
import cn.enaium.naiveui.components.NModalProvider
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.rememberNModal
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ModalDocs() {
    DocsPage(
        title = "Modal",
        description = "Modal displays content above the whole window with a mask. It ships card and dialog presets, " +
            "supports mask/position/dragging/animation origin options, an imperative API and scrollable content.",
    ) {
        DemoSection(
            title = "Basic",
            description = "You can put anything in the modal, a card for example.",
        ) {
            var show by remember { mutableStateOf(false) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NButton(onClick = { show = true }) { NText("Start me up") }
                NText(if (show) "Modal is open" else "Modal is closed", size = 13.sp)
            }
            NModal(
                show = show,
                onUpdateShow = { show = it },
                title = "Modal",
                contentText = "Content",
                width = 600.dp,
                actions = {
                    NButton(size = NSize.Small, onClick = { show = false }) { NText("Close") }
                    NButton(
                        size = NSize.Small,
                        type = NButtonType.Primary,
                        onClick = { show = false },
                    ) { NText("Confirm") }
                },
            )
        }

        DemoSection(
            title = "Content scrollable",
            description = "Give the card a fixed `height` to keep the header and footer fixed while only the body scrolls.",
        ) {
            var show by remember { mutableStateOf(false) }
            NButton(onClick = { show = true }) { NText("Open") }
            NModal(
                show = show,
                onUpdateShow = { show = it },
                preset = NModalPreset.Card,
                title = "Content Scrollable",
                height = 400.dp,
                width = 600.dp,
                actions = {
                    NButton(size = NSize.Small, onClick = { show = false }) { NText("Fixed footer area") }
                },
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..30).forEach { line ->
                        NText("Line $line — only this area scrolls.")
                    }
                }
            }
        }

        DemoSection(
            title = "Controlled",
            description = "The modal is fully controlled by `show`. This one counts down and closes itself.",
        ) {
            var show by remember { mutableStateOf(false) }
            var timeout by remember { mutableStateOf(6) }
            LaunchedEffect(show) {
                if (show) {
                    timeout = 6
                    while (timeout > 0) {
                        delay(1000)
                        timeout--
                    }
                    show = false
                }
            }
            NButton(onClick = { show = true }) { NText("Start me up") }
            NModal(
                show = show,
                onUpdateShow = { show = it },
                title = "Modal",
                width = 600.dp,
            ) {
                NText("Countdown ${timeout}s")
            }
        }

        DemoSection(
            title = "Customizing position",
            description = "Use the corner placements to pin the modal near a window edge.",
        ) {
            var show by remember { mutableStateOf(false) }
            NButton(onClick = { show = true }) { NText("Start me up") }
            NModal(
                show = show,
                onUpdateShow = { show = it },
                title = "Modal",
                placement = NModalPlacement.BottomRight,
                width = 480.dp,
                contentText = "Content",
                actions = {
                    NButton(size = NSize.Small, onClick = { show = false }) { NText("Close") }
                },
            )
        }

        DemoSection(
            title = "Draggable",
            description = "Set `draggable = true` and drag the header to move the surface.",
        ) {
            var show by remember { mutableStateOf(false) }
            NButton(onClick = { show = true }) { NText("Card preset") }
            NModal(
                show = show,
                onUpdateShow = { show = it },
                preset = NModalPreset.Card,
                title = "Card preset draggable",
                draggable = true,
                width = 640.dp,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(8) { NText("Placeholder...") }
                }
            }
        }

        DemoSection(
            title = "Mask closable",
            description = "With `maskClosable = false` only the close button or the dialog actions dismiss the modal.",
        ) {
            var show by remember { mutableStateOf(false) }
            var result by remember { mutableStateOf("No action yet") }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NButton(onClick = { show = true }) { NText("Start me up") }
                NText(result, size = 13.sp)
            }
            NModal(
                show = show,
                onUpdateShow = { show = it },
                preset = NModalPreset.Dialog,
                title = "Dialog",
                contentText = "Are you sure?",
                positiveText = "Confirm",
                negativeText = "Cancel",
                maskClosable = false,
                onPositiveClick = { result = "Submit" },
                onNegativeClick = { result = "Cancel" },
            )
        }

        DemoSection(
            title = "Mask visible",
            description = "`showMask = false` turns the modal into a floating panel without a mask.",
        ) {
            var show by remember { mutableStateOf(false) }
            NButton(onClick = { show = true }) { NText("Floating Panel") }
            NModal(
                show = show,
                onUpdateShow = { show = it },
                preset = NModalPreset.Card,
                title = "Floating Panel",
                draggable = true,
                showMask = false,
                width = 500.dp,
                actions = {
                    NButton(size = NSize.Small, onClick = { show = false }) { NText("Close") }
                },
            ) {
                NText("Floating Panel")
            }
        }

        DemoSection(
            title = "Use preset card",
            description = "The card preset exposes the title, close affordance and action footer.",
        ) {
            var show by remember { mutableStateOf(false) }
            NButton(onClick = { show = true }) { NText("Start me up") }
            NModal(
                show = show,
                onUpdateShow = { show = it },
                preset = NModalPreset.Card,
                title = "Modal",
                width = 600.dp,
                contentText = "Content",
                actions = {
                    NButton(size = NSize.Small, onClick = { show = false }) { NText("Footer") }
                },
            )
        }

        DemoSection(
            title = "Use preset dialog",
            description = "The dialog preset renders title, content text and the positive/negative actions.",
        ) {
            var show by remember { mutableStateOf(false) }
            var result by remember { mutableStateOf("No action yet") }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NButton(onClick = { show = true }) { NText("Start me up") }
                NText(result, size = 13.sp)
            }
            NModal(
                show = show,
                onUpdateShow = { show = it },
                preset = NModalPreset.Dialog,
                title = "Dialog",
                contentText = "Are you sure?",
                positiveText = "Submit",
                negativeText = "Cancel",
                onPositiveClick = { result = "Submit" },
                onNegativeClick = { result = "Cancel" },
            )
        }

        DemoSection(
            title = "Use preset dialog (slot)",
            description = "`header`, the content slot and `actions` replace the preset slots.",
        ) {
            var show by remember { mutableStateOf(false) }
            NButton(onClick = { show = true }) { NText("Start me up") }
            NModal(
                show = show,
                onUpdateShow = { show = it },
                preset = NModalPreset.Dialog,
                header = { NText("title", size = 16.sp, strong = true) },
                actions = { NText("action") },
            ) {
                NText("content")
            }
        }

        DemoSection(
            title = "Imperative API",
            description = "`NModalProvider` exposes `rememberNModal()`. Imperative modals can be closed by handle or timeout.",
        ) {
            NModalProvider { ModalImperativeDemo() }
        }

        DemoSection(
            title = "Transform origin",
            description = "The entrance animation scales from `transformOrigin`. Center and top-left pivots behave differently.",
        ) {
            var show by remember { mutableStateOf(false) }
            var origin by remember { mutableStateOf(TransformOrigin.Center) }
            var originLabel by remember { mutableStateOf("center") }
            NSpace(size = 8.dp) {
                NButton(onClick = {
                    origin = TransformOrigin.Center
                    originLabel = "center"
                    show = true
                }) { NText("Center origin") }
                NButton(onClick = {
                    origin = TransformOrigin(0f, 0f)
                    originLabel = "top left"
                    show = true
                }) { NText("Top left origin") }
            }
            NModal(
                show = show,
                onUpdateShow = { show = it },
                title = "Okay",
                width = 600.dp,
                transformOrigin = origin,
            ) {
                NText("Animation origin: $originLabel")
            }
        }
    }
}

@Composable
private fun ModalImperativeDemo() {
    val modal = rememberNModal()
    val scope = rememberCoroutineScope()
    NSpace(size = 8.dp, wrap = true) {
        NButton(onClick = {
            val handle = modal.create(
                title = "Dialog preset",
                contentText = "Content",
                preset = NModalPreset.Dialog,
                onPositiveClick = { },
            )
            scope.launch {
                delay(3000)
                handle.close()
            }
        }) { NText("Dialog (auto close 3s)") }
        NButton(onClick = {
            modal.create(
                title = "Card preset",
                contentText = "Content",
                preset = NModalPreset.Card,
                width = 400.dp,
                positiveText = "Close",
            )
        }) { NText("Card preset") }
        NButton(onClick = {
            modal.create(
                title = "Everything",
                contentText = "Content",
                preset = NModalPreset.Card,
                width = 400.dp,
                positiveText = "Close",
            )
        }) { NText("Any content") }
        NButton(size = NSize.Small, onClick = { modal.destroyAll() }) { NText("Destroy all") }
    }
}
