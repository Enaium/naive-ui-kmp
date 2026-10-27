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
import cn.enaium.naiveui.components.NCard
import cn.enaium.naiveui.components.NMessageHandle
import cn.enaium.naiveui.components.NMessagePlacement
import cn.enaium.naiveui.components.NMessageProvider
import cn.enaium.naiveui.components.NMessageType
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.rememberNMessage
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

@Composable
fun MessageDocs() {
    DocsPage(
        title = "Message",
        description = "Message shows lightweight global feedback. Wrap a subtree with `NMessageProvider` and call " +
            "`rememberNMessage()` to push typed, closable, reactive or fully custom messages.",
    ) {
        DemoSection(
            title = "Basic",
            description = "All five message types. The info message stays alive while hovered.",
            code = "NMessageProvider {\n    val message = rememberNMessage()\n    NButton(onClick = { message.success(\"Saved\") }) { NText(\"Success\") }\n}",
        ) {
            NMessageProvider { MessageBasicDemo() }
        }

        DemoSection(
            title = "Caveat about theme",
            description = "Messages inherit the theme of their `NMessageProvider`. This message stays for five seconds.",
        ) {
            NMessageProvider { MessageThemeDemo() }
        }

        DemoSection(
            title = "Closable",
            description = "`closable = true` adds a close button to the message.",
        ) {
            NMessageProvider { MessageClosableDemo() }
        }

        DemoSection(
            title = "Customize message",
            description = "`message.custom` renders an arbitrary composable instead of the default bubble.",
        ) {
            NMessageProvider { MessageCustomDemo() }
        }

        DemoSection(
            title = "Icon",
            description = "Pass a custom `icon` vector to replace the type icon.",
        ) {
            NMessageProvider { MessageIconDemo() }
        }

        DemoSection(
            title = "Manually close",
            description = "Create a message with `duration = 0` and keep its handle to close it later.",
        ) {
            NMessageProvider { MessageManualDemo() }
        }

        DemoSection(
            title = "Modify exist message",
            description = "The handle exposes reactive `text` and `type` properties.",
        ) {
            NMessageProvider { MessageModifyDemo() }
        }

        DemoSection(
            title = "Multiple line",
            description = "Long text wraps inside the message body.",
        ) {
            NMessageProvider { MessageMultipleLineDemo() }
        }

        DemoSection(
            title = "No icon",
            description = "`showIcon = false` renders a text-only message.",
        ) {
            NMessageProvider { MessageNoIconDemo() }
        }

        DemoSection(
            title = "Placement",
            description = "The provider anchors the host in one of six window positions.",
        ) {
            var placement by remember { mutableStateOf(NMessagePlacement.Top) }
            NMessageProvider(placement = placement) {
                MessagePlacementDemo(onPlacementChange = { placement = it })
            }
        }

        DemoSection(
            title = "Timing",
            description = "Specify the duration of messages. Loading messages stay until destroyed.",
        ) {
            NMessageProvider { MessageTimingDemo() }
        }
    }
}

@Composable
private fun MessageBasicDemo() {
    val message = rememberNMessage()
    NSpace(size = 8.dp, wrap = true) {
        NButton(onClick = {
            message.info(
                "I don't know why nobody told you how to unfold your love",
                keepAliveOnHover = true,
            )
        }) { NText("Info(Hover will keep alive)") }
        NButton(onClick = { message.error("Once upon a time you dressed so fine") }) { NText("Error") }
        NButton(onClick = { message.warning("How many roads must a man walk down") }) { NText("Warning") }
        NButton(onClick = {
            message.success("'Cause you walked hand in hand With another man in my place")
        }) { NText("Success") }
        NButton(onClick = {
            message.loading("If I were you, I will realize that I love you more than any other guy")
        }) { NText("Loading") }
    }
}

@Composable
private fun MessageThemeDemo() {
    val message = rememberNMessage()
    NButton(onClick = {
        message.info("I don't know why nobody told you how to unfold your love", duration = 5000)
    }) { NText("You can change the theme while the message is active") }
}

@Composable
private fun MessageClosableDemo() {
    val message = rememberNMessage()
    NButton(onClick = {
        message.info(
            "I don't know why nobody told you how to unfold your love",
            closable = true,
            duration = 5000,
        )
    }) { NText("Create a Message") }
}

@Composable
private fun MessageCustomDemo() {
    val message = rememberNMessage()
    NButton(onClick = {
        message.custom(duration = 5000, closable = true) {
            NCard(title = "Lorem ipsum dolor sit amet") {
                NText("Some users said that they want to use an alert as a message.")
            }
        }
    }) { NText("Create a Custom Message") }
}

@Composable
private fun MessageIconDemo() {
    val message = rememberNMessage()
    NButton(onClick = {
        message.warning(
            "I never needed anybody's help in any way",
            icon = NIcons.Time,
        )
    }) { NText("Hourglass Icon") }
}

@Composable
private fun MessageManualDemo() {
    val message = rememberNMessage()
    var handle by remember { mutableStateOf<NMessageHandle?>(null) }
    NSpace(size = 8.dp) {
        NButton(onClick = {
            if (handle == null) {
                handle = message.info("3 * 3 * 4 * 4 * ?", duration = 0)
            }
        }) { NText("Create") }
        NButton(onClick = {
            handle?.close()
            handle = null
        }) { NText("Destroy") }
    }
}

@Composable
private fun MessageModifyDemo() {
    val message = rememberNMessage()
    val types = listOf(
        NMessageType.Success,
        NMessageType.Info,
        NMessageType.Warning,
        NMessageType.Error,
        NMessageType.Loading,
    )
    var handle by remember { mutableStateOf<NMessageHandle?>(null) }
    var count by remember { mutableStateOf(0) }
    var typeIndex by remember { mutableStateOf(0) }
    NSpace(size = 8.dp, wrap = true) {
        NButton(onClick = {
            handle = message.create("$count", duration = 10000)
            handle?.type = types[typeIndex]
        }) { NText("Create a Message Firstly") }
        NButton(onClick = {
            typeIndex = (typeIndex + 1) % types.size
            handle?.type = types[typeIndex]
        }) { NText("Change Type") }
        NButton(onClick = {
            count++
            handle?.text = "$count"
        }) { NText("Plus 1") }
    }
}

@Composable
private fun MessageMultipleLineDemo() {
    val message = rememberNMessage()
    NButton(onClick = {
        message.info(
            "I don't know why nobody told you how to unfold your love. Once upon a time you dressed so fine. " +
                "How many roads must a man walk down. 'Cause you walked hand in hand With another man in my place. " +
                "If I were you, I will realize that I love you more than any other guy.",
        )
    }) { NText("Multiple Line") }
}

@Composable
private fun MessageNoIconDemo() {
    val message = rememberNMessage()
    NButton(onClick = {
        message.warning("I never needed anybody's help in any way", showIcon = false)
    }) { NText("No icon") }
}

@Composable
private fun MessagePlacementDemo(onPlacementChange: (NMessagePlacement) -> Unit) {
    val message = rememberNMessage()
    NSpace(size = 8.dp, wrap = true) {
        NMessagePlacement.entries.forEach { placement ->
            NButton(onClick = {
                onPlacementChange(placement)
                message.info("How many roads must a man walk down")
            }) { NText(placement.name) }
        }
    }
}

@Composable
private fun MessageTimingDemo() {
    val message = rememberNMessage()
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NSpace(size = 8.dp, wrap = true) {
            NButton(onClick = {
                message.info(
                    "I don't know why nobody told you how to unfold your love",
                    duration = 5000,
                )
            }) { NText("Last for 5 second") }
            NButton(onClick = {
                message.loading("Loading until destroyed")
            }) { NText("Loading") }
            NButton(size = NSize.Small, onClick = { message.destroyAll() }) { NText("Clear") }
        }
        NText("Use `duration = 0` to keep a message until it is closed.", size = 13.sp)
    }
}
