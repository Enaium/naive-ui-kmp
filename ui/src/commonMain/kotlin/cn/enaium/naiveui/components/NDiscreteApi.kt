package cn.enaium.naiveui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NButtonType
import cn.enaium.naiveui.components.NCard
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.internal.NCloseButton
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.internal.nShadow
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NStatus
import kotlinx.coroutines.delay

/** A message rendered by [NDiscreteApiHost]. */
@Stable
class NDiscreteMessage internal constructor(
    val id: Long,
    val content: String,
    val type: NStatus,
    val durationMillis: Long,
)

/** A notification rendered by [NDiscreteApiHost]. */
@Stable
class NDiscreteNotification internal constructor(
    val id: Long,
    val title: String,
    val content: String,
    val type: NStatus,
    val durationMillis: Long,
)

/** A dialog rendered by [NDiscreteApiHost]. */
@Stable
class NDiscreteDialog internal constructor(
    val id: Long,
    val title: String,
    val content: String,
    val type: NStatus,
    val positiveText: String,
    val negativeText: String?,
    val onPositive: (() -> Unit)?,
    val onNegative: (() -> Unit)?,
)

/** Imperative message API, mirroring `message` from naive-ui's discrete API. */
@Stable
class NDiscreteMessageApi internal constructor() {
    internal val messages = mutableStateListOf<NDiscreteMessage>()
    private var nextId = 0L

    fun create(content: String, type: NStatus = NStatus.Default, durationMillis: Long = 3000) {
        messages += NDiscreteMessage(++nextId, content, type, durationMillis)
    }

    fun info(content: String) = create(content, NStatus.Info)
    fun success(content: String) = create(content, NStatus.Success)
    fun warning(content: String) = create(content, NStatus.Warning)
    fun error(content: String) = create(content, NStatus.Error)
    fun loading(content: String) = create(content, NStatus.Default, 2000)

    fun remove(id: Long) {
        messages.removeAll { it.id == id }
    }

    fun clear() = messages.clear()
}

/** Imperative notification API. */
@Stable
class NDiscreteNotificationApi internal constructor() {
    internal val notifications = mutableStateListOf<NDiscreteNotification>()
    private var nextId = 0L

    fun create(
        title: String,
        content: String = "",
        type: NStatus = NStatus.Default,
        durationMillis: Long = 4500,
    ) {
        notifications += NDiscreteNotification(++nextId, title, content, type, durationMillis)
    }

    fun info(title: String, content: String = "") = create(title, content, NStatus.Info)
    fun success(title: String, content: String = "") = create(title, content, NStatus.Success)
    fun warning(title: String, content: String = "") = create(title, content, NStatus.Warning)
    fun error(title: String, content: String = "") = create(title, content, NStatus.Error)

    fun remove(id: Long) {
        notifications.removeAll { it.id == id }
    }

    fun clear() = notifications.clear()
}

/** Imperative dialog API. Only one dialog is shown at a time. */
@Stable
class NDiscreteDialogApi internal constructor() {
    internal var current by mutableStateOf<NDiscreteDialog?>(null)
        private set
    private var nextId = 0L

    fun create(
        title: String,
        content: String = "",
        type: NStatus = NStatus.Default,
        positiveText: String = "OK",
        negativeText: String? = null,
        onPositive: (() -> Unit)? = null,
        onNegative: (() -> Unit)? = null,
    ) {
        current = NDiscreteDialog(
            ++nextId,
            title,
            content,
            type,
            positiveText,
            negativeText,
            onPositive,
            onNegative,
        )
    }

    fun info(title: String, content: String = "") = create(title, content, NStatus.Info)
    fun success(title: String, content: String = "") = create(title, content, NStatus.Success)
    fun warning(title: String, content: String = "") = create(title, content, NStatus.Warning)
    fun error(title: String, content: String = "") = create(title, content, NStatus.Error)

    fun close() {
        current = null
    }
}

/** Imperative loading bar API. */
@Stable
class NDiscreteLoadingBarApi internal constructor() {
    internal var visible by mutableStateOf(false)
        private set
    internal var finishing by mutableStateOf(false)
        private set
    internal var progress by mutableStateOf(0f)
        private set
    internal var status by mutableStateOf(NStatus.Primary)
        private set

    fun start() {
        status = NStatus.Primary
        progress = 0f
        finishing = false
        visible = true
    }

    fun loading(value: Float) {
        status = NStatus.Primary
        progress = value.coerceIn(0f, 1f)
        finishing = false
        visible = true
    }

    fun finish() {
        progress = 1f
        finishing = true
        visible = true
    }

    fun error() {
        status = NStatus.Error
        finish()
    }

    internal fun hide() {
        visible = false
        finishing = false
    }
}

/** Bundle returned by [rememberNDiscreteApi]. */
data class NDiscreteApi internal constructor(
    val message: NDiscreteMessageApi,
    val notification: NDiscreteNotificationApi,
    val dialog: NDiscreteDialogApi,
    val loadingBar: NDiscreteLoadingBarApi,
)

/**
 * Creates the discrete APIs and remembers them for the composition. Render
 * [NDiscreteApiHost] with the result to display the overlays.
 */
@Composable
fun rememberNDiscreteApi(): NDiscreteApi = remember {
    NDiscreteApi(NDiscreteMessageApi(), NDiscreteNotificationApi(), NDiscreteDialogApi(), NDiscreteLoadingBarApi())
}

/** Hosts the overlays produced by the discrete APIs above [content]. */
@Composable
fun NDiscreteApiHost(
    api: NDiscreteApi,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val theme = LocalNTheme.current
    val bar = api.loadingBar
    LaunchedEffect(bar.finishing) {
        if (bar.finishing) {
            delay(350)
            bar.hide()
        }
    }
    Box(modifier.fillMaxSize()) {
        content()

        if (bar.visible) {
            val width by animateFloatAsState(bar.progress, label = "n-discrete-loading-bar")
            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(theme.colors.rail),
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(width)
                        .height(2.dp)
                        .background(theme.colors.statusColor(bar.status)),
                )
            }
        }

        Column(
            Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            api.message.messages.forEach { message ->
                key(message.id) {
                    NDiscreteMessageView(message) { api.message.remove(message.id) }
                }
            }
        }

        Column(
            Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            api.notification.notifications.forEach { notification ->
                key(notification.id) {
                    NDiscreteNotificationView(notification) {
                        api.notification.remove(notification.id)
                    }
                }
            }
        }

        val dialog = api.dialog.current
        if (dialog != null) {
            val maskInteraction = remember { MutableInteractionSource() }
            val cardInteraction = remember { MutableInteractionSource() }
            Box(
                Modifier
                    .matchParentSize()
                    .background(theme.colors.mask)
                    .nClickable(maskInteraction) { api.dialog.close() },
                contentAlignment = Alignment.Center,
            ) {
                NCard(
                    modifier = Modifier
                        .width(400.dp)
                        .nShadow(theme, 2, RoundedCornerShape(theme.borderRadius))
                        .nClickable(cardInteraction) {},
                    title = dialog.title,
                    closable = true,
                    onClose = { api.dialog.close() },
                    footer = {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                        ) {
                            if (dialog.negativeText != null) {
                                NButton(
                                    onClick = {
                                        api.dialog.close()
                                        dialog.onNegative?.invoke()
                                    },
                                ) { NText(dialog.negativeText) }
                            }
                            NButton(
                                type = NButtonType.Primary,
                                onClick = {
                                    api.dialog.close()
                                    dialog.onPositive?.invoke()
                                },
                            ) { NText(dialog.positiveText) }
                        }
                    },
                ) {
                    NText(dialog.content, color = theme.colors.text2)
                }
            }
        }
    }
}

@Composable
private fun NDiscreteMessageView(message: NDiscreteMessage, onDismiss: () -> Unit) {
    val theme = LocalNTheme.current
    LaunchedEffect(message.id) {
        delay(message.durationMillis)
        onDismiss()
    }
    val color = if (message.type == NStatus.Default) theme.colors.text1 else theme.colors.statusColor(message.type)
    Box(
        Modifier
            .nShadow(theme, 2, RoundedCornerShape(theme.borderRadius))
            .clip(RoundedCornerShape(theme.borderRadius))
            .background(theme.colors.popover)
            .nBorder(theme.colors.border, 1.dp, theme.borderRadius)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            NText(message.content, color = color)
            NCloseButton(
                onClick = onDismiss,
                modifier = Modifier.padding(start = 10.dp),
                size = 16.dp,
                iconSize = 10.dp,
            )
        }
    }
}

@Composable
private fun NDiscreteNotificationView(notification: NDiscreteNotification, onDismiss: () -> Unit) {
    val theme = LocalNTheme.current
    LaunchedEffect(notification.id) {
        delay(notification.durationMillis)
        onDismiss()
    }
    Box(Modifier.width(320.dp)) {
        NCard(
            title = notification.title,
            closable = true,
            onClose = onDismiss,
        ) {
            if (notification.content.isNotEmpty()) {
                NText(
                    notification.content,
                    color = if (notification.type == NStatus.Default) {
                        theme.colors.text2
                    } else {
                        theme.colors.statusColor(notification.type)
                    },
                )
            }
        }
    }
}
