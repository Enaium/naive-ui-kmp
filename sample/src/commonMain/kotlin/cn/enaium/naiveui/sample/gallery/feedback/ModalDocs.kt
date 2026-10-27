package cn.enaium.naiveui.sample.gallery.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NModal
import cn.enaium.naiveui.components.NModalPlacement
import cn.enaium.naiveui.components.NModalPreset
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NButtonType
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

@Composable
fun ModalDocs() {
    DocsPage(
        title = "Modal",
        description = "Modal displays content above the whole window with a mask. It ships `card` and `dialog` " +
            "presets, controls mask closing, uses centered or top placement and scrolls long content.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Control visibility with `show`/`onUpdateShow` and put any content in the slot.",
            code = "var show by remember { mutableStateOf(false) }\nNModal(\n    show = show,\n    onUpdateShow = { show = it },\n    title = \"Modal\",\n    actions = { NButton(size = NSize.Small) { NText(\"Close\") } },\n) {\n    NText(\"Modal content\")\n}",
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
                contentText = "This modal uses the card preset.",
                actions = {
                    NButton(size = NSize.Small, onClick = { show = false }) { NText("Close") }
                    NButton(
                        size = NSize.Small,
                        type = NButtonType.Primary,
                        onClick = { show = false },
                    ) {
                        NText("Confirm")
                    }
                },
            ) {
                NText("You can put anything in the modal, a card for example.")
            }
        }

        DemoSection(
            title = "Preset dialog",
            description = "`preset = Dialog` renders title, content text and a standard action row.",
        ) {
            var show by remember { mutableStateOf(false) }
            var result by remember { mutableStateOf("No action yet") }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NButton(onClick = { show = true }) { NText("Open Dialog") }
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
                width = 400.dp,
                onPositiveClick = { result = "Submitted" },
                onNegativeClick = { result = "Cancelled" },
            )
        }

        DemoSection(
            title = "Mask not closable",
            description = "With `maskClosable = false` only the close button or actions dismiss the modal.",
        ) {
            var show by remember { mutableStateOf(false) }
            NButton(onClick = { show = true }) { NText("Open Locked Modal") }
            NModal(
                show = show,
                onUpdateShow = { show = it },
                title = "Mask not closable",
                maskClosable = false,
                width = 420.dp,
            ) {
                NText("Clicking the mask does nothing. Use the close button instead.")
            }
        }

        DemoSection(
            title = "Placement",
            description = "`placement = Top` anchors the modal below the top edge instead of centering it.",
        ) {
            var show by remember { mutableStateOf(false) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NButton(onClick = { show = true }) { NText("Open at Top") }
            }
            NModal(
                show = show,
                onUpdateShow = { show = it },
                title = "Top Placement",
                placement = NModalPlacement.Top,
                width = 500.dp,
            ) {
                NText("This modal is pinned near the top of the window.")
            }
        }

        DemoSection(
            title = "Scrollable content",
            description = "Content taller than the window scrolls inside the modal body.",
        ) {
            var show by remember { mutableStateOf(false) }
            NButton(onClick = { show = true }) { NText("Open Long Modal") }
            NModal(
                show = show,
                onUpdateShow = { show = it },
                title = "Scrollable",
                width = 520.dp,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..40).forEach { line ->
                        NText("Line $line of a long modal body.")
                    }
                }
            }
        }

        DemoSection(
            title = "Width & closable",
            description = "`width` fixes the surface width and `closable = false` removes the close affordance.",
        ) {
            var show by remember { mutableStateOf(false) }
            NSpace(size = 8.dp) {
                NButton(onClick = { show = true }) { NText("Open Wide Modal") }
            }
            NModal(
                show = show,
                onUpdateShow = { show = it },
                title = "Wide Modal",
                closable = false,
                width = 640.dp,
                actions = {
                    NButton(size = NSize.Small, onClick = { show = false }) { NText("Done") }
                },
            ) {
                NText("This modal is 640dp wide and has no close icon.")
            }
        }
    }
}
