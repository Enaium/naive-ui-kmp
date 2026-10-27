package cn.enaium.naiveui.sample.gallery.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NSwitch
import cn.enaium.naiveui.components.NAlert
import cn.enaium.naiveui.components.NAlertType
import cn.enaium.naiveui.components.NMarquee
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage

@Composable
fun AlertDocs() {
    DocsPage(
        title = "Alert",
        description = "Inline messages for info, success, warning and error states.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Alerts support default, info, success, warning and error types.",
            code = "NAlert(title = \"Info Text\", type = NAlertType.Info) {\n    NText(\"Gee it's good to be back home\")\n}",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NAlert(
                    title = "Default Text",
                    type = NAlertType.Default,
                    icon = { NIcon(NIcons.Info, size = 24.dp) },
                ) {
                    NText("Gee it's good to be back home")
                }
                NAlert(title = "Info Text", type = NAlertType.Info) {
                    NText("Gee it's good to be back home")
                }
                NAlert(title = "Success Text", type = NAlertType.Success) {
                    NText("Leave it till tomorrow to unpack my case")
                }
                NAlert(title = "Warning Text", type = NAlertType.Warning) {
                    NText("Honey disconnect the phone")
                }
                NAlert(title = "Error Text", type = NAlertType.Error) {
                    NText("I'm back in the U.S.S.R.")
                }
            }
        }
        DemoSection(
            title = "Bordered",
            description = "The border can be turned off.",
        ) {
            var bordered by remember { mutableStateOf(true) }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NSwitch(checked = bordered, onCheckedChange = { bordered = it })
                    NText("Bordered")
                }
                NAlert(title = "Could be no border", type = NAlertType.Info, bordered = bordered) {
                    NText("Gee it's good to be back home")
                }
            }
        }
        DemoSection(
            title = "Closable",
            description = "Closable alerts can be dismissed; use onClose for cleanup.",
        ) {
            var reset by remember { mutableStateOf(0) }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                key(reset) {
                    NAlert(title = "Default Text", type = NAlertType.Default, closable = true) {
                        NText("Gee it's good to be back home")
                    }
                    NAlert(title = "Info Text", type = NAlertType.Info, closable = true) {
                        NText("Gee it's good to be back home")
                    }
                    NAlert(title = "Success Text", type = NAlertType.Success, closable = true) {
                        NText("Leave it till tomorrow to unpack my case")
                    }
                    NAlert(title = "Warning Text", type = NAlertType.Warning, closable = true) {
                        NText("Honey disconnect the phone")
                    }
                    NAlert(title = "Error Text", type = NAlertType.Error, closable = true) {
                        NText("I'm back in the U.S.S.R.")
                    }
                }
                NButton(onClick = { reset += 1 }) { NText("Restore alerts") }
            }
        }
        DemoSection(
            title = "Icon",
            description = "The icon slot replaces the built-in status icon.",
        ) {
            NAlert(
                title = "Back in the U.S.S.R.",
                icon = { NIcon(NIcons.Info, size = 24.dp) },
            ) {
                NText("Well the Ukraine girls really knock me out. They leave the West behind.")
            }
        }
        DemoSection(
            title = "No icon",
            description = "Hide the icon for a more compact message.",
        ) {
            NAlert(showIcon = false) {
                NText("Yeah I'm back in the U.S.S.R. You don't know how lucky you are boys.")
            }
        }
        DemoSection(
            title = "Banner with action",
            description = "Banner alerts have no rounded corners and no border; the action slot sits on the trailing edge.",
        ) {
            NAlert(
                type = NAlertType.Info,
                title = "New version available",
                banner = true,
                action = {
                    NButton(size = cn.enaium.naiveui.theme.NSize.Small) { NText("Update") }
                },
            ) {
                NText("Refresh the page to load the latest release.")
            }
        }
        DemoSection(
            title = "Marquee",
            description = "Combine the alert with a marquee for long, continuously scrolling messages.",
        ) {
            NAlert(type = NAlertType.Error, title = "Warning") {
                NMarquee(
                    items = listOf("Test environment is offline again."),
                    itemSpacing = 64.dp,
                )
            }
        }
        DemoSection(
            title = "Long content",
            description = "Content wraps inside the alert body.",
        ) {
            NAlert(title = "Long message", type = NAlertType.Warning) {
                NText(
                    "Alerts keep their icon aligned with the title while longer content wraps " +
                        "underneath it. The tinted background, border and close button stay anchored " +
                        "to the container edges regardless of the text length.",
                )
            }
        }
    }
}
