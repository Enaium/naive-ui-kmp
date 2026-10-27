package cn.enaium.naiveui.sample.gallery.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NLoadingBarProvider
import cn.enaium.naiveui.components.rememberNLoadingBar
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoadingBarDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Loading Bar",
        description = "Loading Bar is a thin progress indicator driven imperatively. Wrap a subtree in NLoadingBarProvider and obtain the controller with rememberNLoadingBar.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Start, finish and error control the bar. It overlays the top edge of the provider.",
            code = """
NLoadingBarProvider {
    val loadingBar = rememberNLoadingBar()
    NButton(onClick = { loadingBar.start() }) { NText("Start") }
    NButton(onClick = { loadingBar.finish() }) { NText("Finish") }
    NButton(onClick = { loadingBar.error() }) { NText("Error") }
}
            """.trimIndent(),
        ) {
            NLoadingBarProvider(
                Modifier
                    .fillMaxWidth()
                    .height(150.dp),
            ) {
                val loadingBar = rememberNLoadingBar()
                Column(
                    Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    NText(
                        "The bar is anchored to this container.",
                        color = theme.colors.text3,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        NButton(onClick = { loadingBar.start() }) { NText("Start") }
                        NButton(onClick = { loadingBar.finish() }) { NText("Finish") }
                        NButton(onClick = { loadingBar.error() }) { NText("Error") }
                    }
                }
            }
        }

        DemoSection(
            title = "Simulated request",
            description = "Start on request begin and finish when it resolves.",
        ) {
            NLoadingBarProvider(
                Modifier
                    .fillMaxWidth()
                    .height(120.dp),
            ) {
                val loadingBar = rememberNLoadingBar()
                val scope = rememberCoroutineScope()
                var busy by remember { mutableStateOf(false) }
                Box(Modifier.fillMaxSize().padding(16.dp)) {
                    NButton(
                        onClick = {
                            if (!busy) {
                                busy = true
                                loadingBar.start()
                                scope.launch {
                                    delay(1400)
                                    loadingBar.finish()
                                    busy = false
                                }
                            }
                        },
                    ) {
                        NText(if (busy) "Loading..." else "Load data")
                    }
                }
            }
        }

        DemoSection(
            title = "Container",
            description = "A provider can be scoped to any box, so its bar does not need to cover the whole window.",
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NLoadingBarProvider(
                    Modifier
                        .weight(1f)
                        .height(100.dp),
                ) {
                    val loadingBar = rememberNLoadingBar()
                    Box(Modifier.fillMaxSize().padding(12.dp)) {
                        NButton(
                            onClick = { loadingBar.start() },
                        ) { NText("Left container") }
                    }
                }
                NLoadingBarProvider(
                    Modifier
                        .weight(1f)
                        .height(100.dp),
                ) {
                    val loadingBar = rememberNLoadingBar()
                    Box(Modifier.fillMaxSize().padding(12.dp)) {
                        NButton(
                            onClick = { loadingBar.error() },
                        ) { NText("Right container") }
                    }
                }
            }
        }
    }
}
