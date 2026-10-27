package cn.enaium.naiveui.sample.gallery.utility

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NDiscreteApiHost
import cn.enaium.naiveui.components.NModal
import cn.enaium.naiveui.components.NModalPreset
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.rememberNDiscreteApi
import cn.enaium.naiveui.sample.gallery.DemoRow
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NConfigProvider
import cn.enaium.naiveui.theme.NTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DiscreteApiDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Discrete API",
        description = "Message, notification, dialog and loading bar can be driven from anywhere through " +
            "an API object instead of a provider scope. rememberNDiscreteApi creates the APIs and " +
            "NDiscreteApiHost renders their overlays.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Trigger each API from a button; the theme switch also updates the overlays.",
            code = """
                val api = rememberNDiscreteApi()
                NDiscreteApiHost(api) { /* app content */ }

                api.message.info("Message")
                api.notification.create(title = "Notification")
                api.dialog.info(title = "Dialog", content = "Hello")
                api.loadingBar.start()
            """.trimIndent(),
        ) {
            var dark by remember { mutableStateOf(false) }
            var modalShow by remember { mutableStateOf(false) }
            val api = rememberNDiscreteApi()
            val scope = rememberCoroutineScope()
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DemoRow {
                    NButton(onClick = { dark = !dark }) {
                        NText("theme: ${if (dark) "dark" else "light"}")
                    }
                }
                NConfigProvider(theme = if (dark) NTheme.Dark else NTheme.Light) {
                    NDiscreteApiHost(
                        api = api,
                        modifier = Modifier.fillMaxWidth().height(320.dp),
                    ) {
                        Column(
                            Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            NText(
                                "The host renders overlays above this area.",
                                color = LocalNTheme.current.colors.text3,
                            )
                            DemoRow {
                                NButton(onClick = { api.message.info("Message") }) { NText("message") }
                                NButton(onClick = { api.message.success("Success") }) { NText("success") }
                                NButton(onClick = { api.message.error("Error") }) { NText("error") }
                            }
                            DemoRow {
                                NButton(
                                    onClick = {
                                        api.notification.create(
                                            title = "Notification",
                                            content = "This notification is rendered by the discrete host.",
                                        )
                                    },
                                ) { NText("notification") }
                                NButton(
                                    onClick = {
                                        api.dialog.create(
                                            title = "Dialog",
                                            content = "Dialog content rendered by the discrete host.",
                                            negativeText = "Cancel",
                                            positiveText = "Confirm",
                                        )
                                    },
                                ) { NText("dialog") }
                            }
                            DemoRow {
                                NButton(
                                    onClick = {
                                        api.loadingBar.start()
                                        scope.launch {
                                            delay(1200)
                                            api.loadingBar.finish()
                                        }
                                    },
                                ) { NText("loading bar") }
                                NButton(onClick = { modalShow = true }) { NText("modal") }
                            }
                        }
                    }
                    NModal(
                        show = modalShow,
                        onUpdateShow = { modalShow = it },
                        preset = NModalPreset.Card,
                        title = "Modal",
                    ) {
                        NText("Modal content")
                    }
                }
            }
        }

        DemoSection(
            title = "Dialog confirmation",
            description = "Positive and negative callbacks run after the dialog closes.",
        ) {
            val api = rememberNDiscreteApi()
            NDiscreteApiHost(api, Modifier.fillMaxWidth().height(200.dp)) {
                Column(Modifier.padding(16.dp)) {
                    NButton(
                        onClick = {
                            api.dialog.create(
                                title = "Delete item",
                                content = "This action cannot be undone.",
                                positiveText = "Delete",
                                negativeText = "Cancel",
                                onPositive = {
                                    api.message.success("Deleted")
                                },
                            )
                        },
                    ) { NText("Delete…") }
                }
            }
        }
    }
}
