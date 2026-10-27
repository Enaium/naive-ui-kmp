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
import cn.enaium.naiveui.components.NMessagePlacement
import cn.enaium.naiveui.components.NMessageProvider
import cn.enaium.naiveui.components.rememberNMessage
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

@Composable
fun MessageDocs() {
    DocsPage(
        title = "Message",
        description = "Message shows lightweight global feedback. Wrap a subtree with `NMessageProvider` and " +
            "call `rememberNMessage()` to push info, success, warning, error or loading messages.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Each method shows a message and removes it after 3000ms by default.",
            code = "NMessageProvider {\n    val message = rememberNMessage()\n    NButton(onClick = { message.success(\"Saved\") }) { NText(\"Success\") }\n}",
        ) {
            NMessageProvider { MessageBasicDemo() }
        }

        DemoSection(
            title = "Timing",
            description = "Pass a `duration` in milliseconds. Loading messages stay until closed or destroyed.",
        ) {
            NMessageProvider { MessageTimingDemo() }
        }

        DemoSection(
            title = "Placement",
            description = "The provider chooses where the message host is anchored.",
        ) {
            NMessageProvider(placement = NMessagePlacement.TopRight) { MessagePlacementDemo() }
        }

        DemoSection(
            title = "Destroy all",
            description = "`destroyAll()` immediately clears every message owned by the provider.",
        ) {
            NMessageProvider { MessageDestroyDemo() }
        }
    }
}

@Composable
private fun MessageBasicDemo() {
    val message = rememberNMessage()
    NSpace(size = 8.dp, wrap = true) {
        NButton(onClick = { message.info("I don't know why nobody told you how to unfold your love") }) {
            NText("Info")
        }
        NButton(onClick = { message.success("You walked hand in hand with another man in my place") }) {
            NText("Success")
        }
        NButton(onClick = { message.warning("How many roads must a man walk down") }) {
            NText("Warning")
        }
        NButton(onClick = { message.error("Once upon a time you dressed so fine") }) {
            NText("Error")
        }
    }
}

@Composable
private fun MessageTimingDemo() {
    val message = rememberNMessage()
    var last by remember { mutableStateOf("No message yet") }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        NSpace(size = 8.dp, wrap = true) {
            NButton(onClick = {
                last = "Short message (1000ms)"
                message.success("This disappears after one second.", duration = 1000)
            }) { NText("Short") }
            NButton(onClick = {
                last = "Long message (6000ms)"
                message.info("This stays for six seconds.", duration = 6000)
            }) { NText("Long") }
            NButton(onClick = {
                last = "Loading message"
                message.loading("Loading forever until destroyed.")
            }) { NText("Loading") }
            NButton(size = NSize.Small, onClick = { message.destroyAll() }) { NText("Clear") }
        }
        NText(last, size = 13.sp)
    }
}

@Composable
private fun MessagePlacementDemo() {
    val message = rememberNMessage()
    NSpace(size = 8.dp, wrap = true) {
        NButton(onClick = { message.info("Anchored at the top right corner.") }) {
            NText("Show Message")
        }
    }
}

@Composable
private fun MessageDestroyDemo() {
    val message = rememberNMessage()
    NSpace(size = 8.dp) {
        NButton(onClick = {
            message.info("First message", duration = 0)
            message.success("Second message", duration = 0)
            message.warning("Third message", duration = 0)
        }) { NText("Show Three") }
        NButton(size = NSize.Small, onClick = { message.destroyAll() }) { NText("Destroy All") }
    }
}
