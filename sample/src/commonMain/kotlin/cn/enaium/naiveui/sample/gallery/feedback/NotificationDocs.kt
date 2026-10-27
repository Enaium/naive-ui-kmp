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
import cn.enaium.naiveui.components.NNotificationOptions
import cn.enaium.naiveui.components.NNotificationPlacement
import cn.enaium.naiveui.components.NNotificationProvider
import cn.enaium.naiveui.components.NNotificationType
import cn.enaium.naiveui.components.rememberNNotification
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

@Composable
fun NotificationDocs() {
    DocsPage(
        title = "Notification",
        description = "Notification shows richer global feedback than Message. Notifications stack in a window " +
            "corner and support a title, body, meta line, semantic type and custom duration.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Wrap the subtree with `NNotificationProvider` and call `create` from the API.",
            code = "NNotificationProvider {\n    val notification = rememberNNotification()\n    NButton(onClick = {\n        notification.success(\"Saved\", \"The document was saved\")\n    }) { NText(\"Notify\") }\n}",
        ) {
            NNotificationProvider { NotificationBasicDemo() }
        }

        DemoSection(
            title = "Type",
            description = "`success`, `info`, `warning` and `error` shortcuts pick the icon and color.",
        ) {
            NNotificationProvider { NotificationTypeDemo() }
        }

        DemoSection(
            title = "Duration",
            description = "Use 0 for a persistent notification, or any millisecond value for auto dismissal.",
        ) {
            NNotificationProvider { NotificationDurationDemo() }
        }

        DemoSection(
            title = "Custom options",
            description = "`NNotificationOptions` adds a meta line and controls the close affordance.",
        ) {
            NNotificationProvider { NotificationOptionsDemo() }
        }

        DemoSection(
            title = "Placement",
            description = "The provider places the stack in any of the four window corners.",
        ) {
            NNotificationProvider(placement = NNotificationPlacement.BottomLeft) {
                NotificationPlacementDemo()
            }
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
    var last by remember { mutableStateOf("No notification yet") }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        NSpace(size = 8.dp) {
            NButton(onClick = {
                last = "Created a notification"
                notification.create(
                    title = "Wouldn't it Be Nice",
                    content = "Wouldn't it be nice if we were older, then we wouldn't have to wait so long.",
                )
            }) { NText("Show Notification") }
        }
        NText(last, size = 13.sp)
    }
}

@Composable
private fun NotificationTypeDemo() {
    val notification = rememberNNotification()
    NSpace(size = 8.dp, wrap = true) {
        NNotificationType.entries
            .filter { it != NNotificationType.Default }
            .forEach { type ->
                NButton(onClick = {
                    notification.create(
                        title = type.name,
                        content = "A ${type.name.lowercase()} notification.",
                        type = type,
                    )
                }) { NText(type.name) }
            }
    }
}

@Composable
private fun NotificationDurationDemo() {
    val notification = rememberNNotification()
    NSpace(size = 8.dp, wrap = true) {
        NButton(onClick = {
            notification.info("Short", "Visible for one second.", duration = 1000)
        }) { NText("1000ms") }
        NButton(onClick = {
            notification.info("Persistent", "Close it manually.", duration = 0)
        }) { NText("Persistent") }
    }
}

@Composable
private fun NotificationOptionsDemo() {
    val notification = rememberNNotification()
    NButton(onClick = {
        notification.create(
            NNotificationOptions(
                title = "Satisfaction",
                content = "I can't get no satisfaction, cause I try and I try and I try.",
                meta = "2019-5-27 15:11",
                type = NNotificationType.Success,
                duration = 6000,
            ),
        )
    }) { NText("Show Detailed Notification") }
}

@Composable
private fun NotificationPlacementDemo() {
    val notification = rememberNNotification()
    NButton(onClick = {
        notification.warning("Bottom left", "This stack is anchored to the bottom left corner.")
    }) { NText("Notify Bottom Left") }
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
        NButton(size = NSize.Small, onClick = { notification.destroyAll() }) {
            NText("Destroy All")
        }
    }
}
