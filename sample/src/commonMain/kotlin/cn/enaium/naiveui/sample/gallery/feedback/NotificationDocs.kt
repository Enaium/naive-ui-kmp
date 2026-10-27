package cn.enaium.naiveui.sample.gallery.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NNotificationHandle
import cn.enaium.naiveui.components.NNotificationOptions
import cn.enaium.naiveui.components.NNotificationPlacement
import cn.enaium.naiveui.components.NNotificationProvider
import cn.enaium.naiveui.components.NNotificationType
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.rememberNNotification
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NotificationDocs() {
    DocsPage(
        title = "Notification",
        description = "Notification shows richer global feedback than Message. Cards stack in a window corner, " +
            "support titles, bodies, meta lines, avatars, actions, reactive updates and scrollable stacks.",
    ) {
        DemoSection(
            title = "Basic",
            description = "`NNotificationOptions` supports title, content, meta, avatar text and an action.",
            code = "NNotificationProvider {\n    val notification = rememberNNotification()\n    NButton(onClick = { notification.success(\"Saved\", \"The document was saved\") }) { NText(\"Notify\") }\n}",
        ) {
            NNotificationProvider { NotificationBasicDemo() }
        }

        DemoSection(
            title = "Change content dynamically",
            description = "The handle exposes reactive `title`, `content` and `type` properties.",
        ) {
            NNotificationProvider { NotificationChangeDemo() }
        }

        DemoSection(
            title = "Unclosable",
            description = "Hide the close button with `closable = false`; `onAfterLeave` chains another notification.",
        ) {
            NNotificationProvider { NotificationChainDemo() }
        }

        DemoSection(
            title = "Duration",
            description = "Notifications close themselves after `duration`. This one counts down and then shows a follow-up.",
        ) {
            NNotificationProvider { NotificationDurationDemo() }
        }

        DemoSection(
            title = "Max",
            description = "The provider keeps at most `max` notifications, dropping the oldest.",
        ) {
            NNotificationProvider(max = 3) { NotificationMaxDemo() }
        }

        DemoSection(
            title = "Placement",
            description = "The provider anchors the stack in six window positions.",
        ) {
            var placement by remember { mutableStateOf(NNotificationPlacement.TopRight) }
            NNotificationProvider(placement = placement) {
                NotificationPlacementDemo(onPlacementChange = { placement = it })
            }
        }

        DemoSection(
            title = "Scrollable",
            description = "`scrollable = true` limits the stack height and makes it scroll when there are many cards.",
        ) {
            NNotificationProvider(scrollable = true) { NotificationScrollableDemo() }
        }

        DemoSection(
            title = "Type",
            description = "Info, success, warning and error shortcuts pick the icon and color.",
        ) {
            NNotificationProvider { NotificationTypeDemo() }
        }

        DemoSection(
            title = "Destroy all",
            description = "`destroyAll()` closes every notification owned by the provider.",
        ) {
            NNotificationProvider { NotificationDestroyDemo() }
        }
    }
}

@Composable
private fun NotificationBasicDemo() {
    val notification = rememberNNotification()
    var result by remember { mutableStateOf("No notification yet") }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NSpace(size = 8.dp, wrap = true) {
            NButton(onClick = {
                result = "Created a notification"
                notification.create(
                    NNotificationOptions(
                        title = "Wouldn't it be Nice",
                        content = "Wouldn't it be nice if we were older, then we wouldn't have to wait so long. " +
                            "And wouldn't it be nice to live together in the kind of world where we belong.",
                        meta = "2019-5-27 15:11",
                        avatarText = "W",
                        actionText = "Mark as Read",
                        duration = 6000,
                        onAction = { result = "Marked as read" },
                        onAfterLeave = { result = "Notification left" },
                    ),
                )
            }) { NText("Wouldn't it be Nice") }
            NButton(onClick = {
                notification.create(
                    NNotificationOptions(
                        title = "Satisfaction",
                        content = "I can't get no satisfaction. I can't get no satisfaction. " +
                            "Cause I try and I try and I try and I try.",
                        meta = "2019-5-27 15:11",
                        actionText = "Mark as Read",
                        duration = 8000,
                    ),
                )
            }) { NText("Satisfaction") }
        }
        NText(result, size = 13.sp)
    }
}

@Composable
private fun NotificationChangeDemo() {
    val notification = rememberNNotification()
    var handle by remember { mutableStateOf<NNotificationHandle?>(null) }
    NSpace(size = 8.dp) {
        NButton(onClick = {
            handle = notification.create(
                NNotificationOptions(
                    title = "Wouldn't it be Nice",
                    content = "From the Beach Boys",
                    meta = "2019-5-27 15:11",
                    duration = 10000,
                    onAfterLeave = { handle = null },
                ),
            )
        }) { NText("Open it") }
        NButton(onClick = {
            handle?.title = "It changed!"
            handle?.content = "You can change any part of notifications that already exist."
            handle?.type = NNotificationType.Success
        }) { NText("Change it") }
    }
}

@Composable
private fun NotificationChainDemo() {
    val notification = rememberNNotification()
    fun chain(index: Int) {
        val title = when (index) {
            0 -> "Close Me if You Can"
            1 -> "Ha Ha Ha Ha!"
            else -> "No, You Can't"
        }
        notification.create(
            NNotificationOptions(
                title = title,
                duration = 2000,
                closable = false,
                onAfterLeave = if (index < 2) {
                    { chain(index + 1) }
                } else {
                    null
                },
            ),
        )
    }
    NButton(onClick = { chain(0) }) { NText("Unclosable") }
}

@Composable
private fun NotificationDurationDemo() {
    val notification = rememberNNotification()
    val scope = rememberCoroutineScope()
    NButton(onClick = {
        val handle = notification.create(
            NNotificationOptions(
                title = "What is Pingshan Road + rain ?",
                content = "You have 10 seconds to answer the question.",
                duration = 10000,
                closable = false,
                onAfterLeave = {
                    notification.create(
                        NNotificationOptions(
                            title = "The Answer is Pingshan River!",
                            content = "Oops, that is not even an anti-humor.",
                            duration = 10000,
                        ),
                    )
                },
            ),
        )
        scope.launch {
            for (count in 9 downTo 0) {
                delay(1000)
                handle.content = "You have $count seconds to answer the question."
            }
        }
    }) { NText("Duration: 10000") }
}

@Composable
private fun NotificationMaxDemo() {
    val notification = rememberNNotification()
    var index by remember { mutableStateOf(0) }
    NButton(onClick = {
        index++
        notification.create(
            NNotificationOptions(
                title = "Notification index: $index",
                content = "You can limit the number of notifications.",
                type = NNotificationType.Info,
            ),
        )
    }) { NText("Max notification count: 3") }
}

@Composable
private fun NotificationPlacementDemo(onPlacementChange: (NNotificationPlacement) -> Unit) {
    val notification = rememberNNotification()
    NSpace(size = 8.dp, wrap = true) {
        NNotificationPlacement.entries.forEach { placement ->
            NButton(onClick = {
                onPlacementChange(placement)
                notification.info(placement.name, "You can change the placement")
            }) { NText(placement.name) }
        }
    }
}

@Composable
private fun NotificationScrollableDemo() {
    val notification = rememberNNotification()
    NButton(onClick = {
        repeat(5) { index ->
            notification.create(
                NNotificationOptions(
                    title = "Many Notifications ${index + 1}",
                    content = "Try to scroll. Try to scroll. Try to scroll. Try to scroll. " +
                        "Try to scroll. Try to scroll. Try to scroll.",
                    duration = 15000,
                ),
            )
        }
    }) { NText("See how it scrolls") }
}

@Composable
private fun NotificationTypeDemo() {
    val notification = rememberNNotification()
    NSpace(size = 8.dp, wrap = true) {
        listOf(
            NNotificationType.Info,
            NNotificationType.Success,
            NNotificationType.Warning,
            NNotificationType.Error,
        ).forEach { type ->
            NButton(onClick = {
                notification.create(
                    NNotificationOptions(
                        content = "What to say?",
                        meta = "I don't know",
                        type = type,
                        duration = 2500,
                        keepAliveOnHover = true,
                    ),
                )
            }) { NText(type.name) }
        }
    }
}

@Composable
private fun NotificationDestroyDemo() {
    val notification = rememberNNotification()
    NSpace(size = 8.dp) {
        NButton(onClick = {
            notification.info("One", "First notification", duration = 0)
            notification.success("Two", "Second notification", duration = 0)
            notification.warning("Three", "Third notification", duration = 0)
        }) { NText("Show Three") }
        NButton(size = NSize.Small, onClick = { notification.destroyAll() }) { NText("Destroy All") }
    }
}
