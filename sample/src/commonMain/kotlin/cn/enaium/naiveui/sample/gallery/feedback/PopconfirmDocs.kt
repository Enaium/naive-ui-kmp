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
import cn.enaium.naiveui.components.NPopconfirm
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NButtonType
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NTrigger
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus

@Composable
fun PopconfirmDocs() {
    DocsPage(
        title = "Popconfirm",
        description = "Popconfirm asks for confirmation in a small popup. It supports positive and negative " +
            "actions, semantic types, a custom body and configurable trigger and placement.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Provide the question through `text`; the two actions call the matching callbacks.",
        ) {
            var result by remember { mutableStateOf("Wait for the action") }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NPopconfirm(
                    text = "Are you sure you want to delete this file?",
                    positiveText = "Delete",
                    negativeText = "Cancel",
                    onPositiveClick = { result = "Deleted" },
                    onNegativeClick = { result = "Cancelled" },
                ) {
                    NButton { NText("Delete") }
                }
                NText(result, size = 13.sp)
            }
        }

        DemoSection(
            title = "Type",
            description = "`type` tints the icon and the positive button.",
        ) {
            NSpace(size = 8.dp, wrap = true) {
                listOf(
                    NStatus.Default,
                    NStatus.Info,
                    NStatus.Success,
                    NStatus.Warning,
                    NStatus.Error,
                ).forEach { status ->
                    NPopconfirm(
                        text = "Confirm ${status.name.lowercase()} action?",
                        type = status,
                        positiveText = "Yes",
                        negativeText = "No",
                    ) {
                        NButton(size = NSize.Small) { NText(status.name) }
                    }
                }
            }
        }

        DemoSection(
            title = "No icon",
            description = "Hide the leading icon with `showIcon = false`.",
        ) {
            NPopconfirm(
                text = "Show icon or not?",
                showIcon = false,
                positiveText = "Ok",
                negativeText = "No",
            ) {
                NButton { NText("No Icon") }
            }
        }

        DemoSection(
            title = "Custom content",
            description = "The `content` slot extends the body with arbitrary composables.",
        ) {
            NPopconfirm(
                text = "This action cannot be undone.",
                positiveText = "Continue",
                negativeText = "Back",
                content = {
                    NText("All related records will also be removed.", size = 13.sp)
                },
            ) {
                NButton(type = NButtonType.Error) {
                    NText("Remove")
                }
            }
        }

        DemoSection(
            title = "Trigger & placement",
            description = "Popconfirm opens on click by default. Hover triggers and top/bottom placements are supported.",
        ) {
            NSpace(size = 8.dp, wrap = true) {
                NPopconfirm(
                    text = "Click confirm",
                    trigger = NTrigger.Click,
                    placement = NPlacement.Top,
                ) {
                    NButton(size = NSize.Small) { NText("Click Top") }
                }
                NPopconfirm(
                    text = "Hover confirm",
                    trigger = NTrigger.Hover,
                    placement = NPlacement.Bottom,
                ) {
                    NButton(size = NSize.Small) { NText("Hover Bottom") }
                }
            }
        }
    }
}
