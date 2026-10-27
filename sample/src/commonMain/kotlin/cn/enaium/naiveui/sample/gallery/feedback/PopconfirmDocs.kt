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
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NPopconfirm
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NTrigger
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize

private const val quote = "Things pass us by. Nobody can catch them. That's the way we live our lives."

@Composable
fun PopconfirmDocs() {
    DocsPage(
        title = "Popconfirm",
        description = "Popconfirm asks for confirmation in a small popup. It supports positive and negative " +
            "actions, action/icon slots, semantic types, controlled visibility and custom triggers.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Provide the question through `text`; the two actions call the matching callbacks.",
        ) {
            var result by remember { mutableStateOf("Wait for the action") }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NPopconfirm(
                    text = quote,
                    onPositiveClick = { result = "positive click" },
                    onNegativeClick = { result = "negative click" },
                ) {
                    NButton { NText("Quote") }
                }
                NText(result, size = 13.sp)
            }
        }

        DemoSection(
            title = "Actions",
            description = "Toggle the default actions with `showPositive`/`showNegative`, or replace the row with `actions`.",
        ) {
            NSpace(size = 8.dp, wrap = true) {
                NPopconfirm(
                    text = quote,
                    onPositiveClick = { },
                    onNegativeClick = { },
                ) {
                    NButton { NText("Quote") }
                }
                NPopconfirm(
                    text = quote,
                    showNegative = false,
                ) {
                    NButton { NText("Only Confirm") }
                }
                NPopconfirm(
                    text = quote,
                    showPositive = false,
                ) {
                    NButton { NText("Only Cancel") }
                }
                NPopconfirm(
                    text = quote,
                    showPositive = false,
                    showNegative = false,
                ) {
                    NButton { NText("Nothing") }
                }
                NPopconfirm(
                    text = quote,
                    actions = { NText("Custom Action") },
                ) {
                    NButton { NText("Custom action") }
                }
            }
        }

        DemoSection(
            title = "Custom action",
            description = "A controlled popconfirm with a custom action button, plus a custom positive text.",
        ) {
            var show by remember { mutableStateOf(false) }
            NSpace(size = 8.dp) {
                NPopconfirm(
                    text = "For example, if I were to write about elephants, I'd have had no idea what words to use.",
                    show = show,
                    onUpdateShow = { show = it },
                    actions = {
                        NButton(size = NSize.Small, onClick = { show = false }) { NText("Maybe") }
                    },
                ) {
                    NButton { NText("Quote") }
                }
                NPopconfirm(
                    text = "For example, if I were to write about elephants, I'd have had no idea what words to use.",
                    positiveText = "Oops!",
                ) {
                    NButton { NText("Quote") }
                }
            }
        }

        DemoSection(
            title = "Custom icon",
            description = "The `icon` slot replaces the default status icon.",
        ) {
            val theme = LocalNTheme.current
            NPopconfirm(
                positiveText = "ok",
                negativeText = "not ok",
                icon = { NIcon(NIcons.Person, size = 20.dp, color = theme.colors.error) },
                text = "How can you be strong without purchasing?",
            ) {
                NButton { NText("Quit Game") }
            }
        }

        DemoSection(
            title = "Event",
            description = "`positive-click` and `negative-click` are reported below.",
        ) {
            var event by remember { mutableStateOf("No event yet") }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NPopconfirm(
                    text = "I heared that players will still be abused after purchasing in some games when I watch Bilibili.",
                    onPositiveClick = { event = "positive click" },
                    onNegativeClick = { event = "negative click" },
                ) {
                    NButton { NText("Quit Game") }
                }
                NText(event, size = 13.sp)
            }
        }

        DemoSection(
            title = "No icon",
            description = "Set `showIcon = false` for a text-only confirmation.",
        ) {
            NPopconfirm(
                text = "As is",
                showIcon = false,
            ) {
                NButton { NText("No icon") }
            }
        }

        DemoSection(
            title = "Triggers",
            description = "Click is the default; hover triggers also keep the popup alive while the pointer is inside.",
        ) {
            NSpace(size = 8.dp) {
                NPopconfirm(text = "Click confirm", trigger = NTrigger.Click) {
                    NButton(size = NSize.Small) { NText("Click") }
                }
                NPopconfirm(text = "Hover confirm", trigger = NTrigger.Hover) {
                    NButton(size = NSize.Small) { NText("Hover") }
                }
            }
        }
    }
}
