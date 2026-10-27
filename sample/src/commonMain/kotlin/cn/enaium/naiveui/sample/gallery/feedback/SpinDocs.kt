package cn.enaium.naiveui.sample.gallery.feedback

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NSpin
import cn.enaium.naiveui.components.NAlert
import cn.enaium.naiveui.components.NAlertType
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize

@Composable
fun SpinDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Spin",
        description = "Loading indicator with optional description, custom icon and wrapped content.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Here are small, medium and large spins.",
            code = "NSpin(size = NSize.Small)\nNSpin()\nNSpin(size = NSize.Large)",
        ) {
            NSpace(size = 24.dp, vertical = true) {
                NSpace(size = 24.dp) {
                    NSpin(size = NSize.Small)
                    NSpin()
                    NSpin(size = NSize.Large)
                }
            }
        }
        DemoSection(
            title = "Wrap content",
            description = "Wrap a component inside spin and toggle the loading state.",
        ) {
            var show by remember { mutableStateOf(false) }
            NSpace(vertical = true, size = 12.dp) {
                NSpin(show = show) {
                    NAlert(title = "La La La", type = NAlertType.Success) {
                        NText("Leave it till tomorrow to unpack my case. Honey disconnect the phone.")
                    }
                }
                NButton(onClick = { show = !show }) { NText("Click to Spin") }
            }
        }
        DemoSection(
            title = "Description",
            description = "Add a description under the spinner.",
        ) {
            var show by remember { mutableStateOf(false) }
            NSpace(vertical = true, size = 12.dp) {
                NSpin(show = show, description = "You know how lucky you are") {
                    NAlert(title = "La La La", type = NAlertType.Success) {
                        NText("Leave it till tomorrow to unpack my case. Honey disconnect the phone.")
                    }
                }
                NButton(onClick = { show = !show }) { NText("Click to Spin") }
            }
        }
        DemoSection(
            title = "Customize icon",
            description = "Supply an icon through the icon slot; it rotates while spinning.",
        ) {
            var show by remember { mutableStateOf(false) }
            NSpace(vertical = true, size = 12.dp) {
                NSpin(
                    show = show,
                    icon = {
                        NIcon(
                            NIcons.RotateClockwise,
                            size = 30.dp,
                            color = theme.colors.primary,
                        )
                    },
                ) {
                    NAlert(title = "Custom icon", type = NAlertType.Info) {
                        NText("The icon rotates while the spin is active.")
                    }
                }
                NButton(onClick = { show = !show }) { NText("Click to Spin") }
            }
        }
        DemoSection(
            title = "Delay",
            description = "Delay prevents a spinner from flashing for fast operations.",
        ) {
            var show by remember { mutableStateOf(false) }
            NSpace(vertical = true, size = 12.dp) {
                NSpin(show = show, delayMillis = 1000L) {
                    NAlert(title = "La La La", type = NAlertType.Success) {
                        NText("Leave it till tomorrow to unpack my case. Honey disconnect the phone.")
                    }
                }
                NButton(onClick = { show = !show }) {
                    NText(if (show) "Click to stop Spin" else "Click to Spin")
                }
            }
        }
    }
}
