package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NLog
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import kotlinx.coroutines.launch

private fun sampleLog(start: Int, count: Int): String =
    (start until start + count).joinToString("\n") { index ->
        "2026-10-27 10:${(index % 60).toString().padStart(2, '0')} service=api level=INFO request=$index completed in ${index % 40 + 1}ms"
    }

private val kotlinLog = """
fun main() {
    val message = "Hello Naive UI"
    println(message)
}
""".trim()

@Composable
fun LogDocs() {
    DocsPage(
        title = "Log",
        description = "A scrollable, monospace log viewer with loading, events and syntax highlighting.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Pass a single log string or pre-split lines.",
            code = "NLog(log = log, rows = 8)",
        ) {
            NLog(log = sampleLog(1, 20), rows = 8)
        }
        DemoSection(
            title = "Loading",
            description = "The loader and skeleton rows are controlled by loading and loadingLines.",
            code = "NLog(log = log, loading = loading, loadingLines = 3)",
        ) {
            var loading by remember { mutableStateOf(true) }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NButton(onClick = { loading = !loading }) {
                    NText(if (loading) "Stop loading" else "Start loading")
                }
                NLog(log = sampleLog(1, 6), loading = loading, loadingLines = 3, rows = 8)
            }
        }
        DemoSection(
            title = "Auto bottom",
            description = "autoScrollBottom keeps the newest line visible when the log grows.",
        ) {
            var lines by remember { mutableStateOf(sampleLog(1, 10)) }
            var next by remember { mutableStateOf(11) }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NButton(onClick = {
                    lines = lines + "\n" + sampleLog(next, 3)
                    next += 3
                }) {
                    NText("Add data")
                }
                NLog(log = lines, rows = 8, autoScrollBottom = true)
            }
        }
        DemoSection(
            title = "Highlight",
            description = "Set language to highlight each line with the same tokenizer as NCode.",
            code = "NLog(lines = lines, language = \"kotlin\")",
        ) {
            NLog(log = kotlinLog, language = "kotlin", rows = 8)
        }
        DemoSection(
            title = "Rows",
            description = "rows controls the viewport height.",
        ) {
            NLog(log = "1\n2\n3\n4\n5\n6", rows = 5)
        }
        DemoSection(
            title = "Scroll",
            description = "Drive the viewport through the exposed scrollState.",
        ) {
            val scrollState = rememberScrollState()
            val scope = rememberCoroutineScope()
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    NButton(onClick = {
                        scope.launch { scrollState.scrollTo(0) }
                    }) {
                        NText("Scroll to top")
                    }
                    NButton(onClick = {
                        scope.launch { scrollState.animateScrollTo(scrollState.maxValue) }
                    }) {
                        NText("Scroll to bottom")
                    }
                }
                NLog(log = sampleLog(1, 30), rows = 6, scrollState = scrollState)
            }
        }
        DemoSection(
            title = "Events",
            description = "onReachTop and onReachBottom fire when the user scrolls to an edge.",
        ) {
            var lastEvent by remember { mutableStateOf("Scroll the log to an edge") }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NText(lastEvent, size = 13.sp, color = LocalNTheme.current.colors.text3)
                NLog(
                    log = sampleLog(1, 30),
                    rows = 6,
                    onReachTop = { lastEvent = "Reached top" },
                    onReachBottom = { lastEvent = "Reached bottom" },
                    autoScrollBottom = false,
                )
            }
        }
    }
}
