package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NCloseButton
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nShadow
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import kotlinx.coroutines.delay

/** Semantic type of a notification card. */
enum class NNotificationType { Default, Info, Success, Warning, Error }

/** Where the notification host is anchored inside the window. */
enum class NNotificationPlacement { Top, TopLeft, TopRight, Bottom, BottomLeft, BottomRight }

/** Options accepted by [NNotificationApi.create]. */
data class NNotificationOptions(
    val title: String? = null,
    val content: String? = null,
    val meta: String? = null,
    val type: NNotificationType = NNotificationType.Default,
    val duration: Long = 4500,
    val closable: Boolean = true,
    val avatarText: String? = null,
    val actionText: String? = null,
    val keepAliveOnHover: Boolean = false,
    val onAction: (() -> Unit)? = null,
    val onAfterLeave: (() -> Unit)? = null,
)

/**
 * Returned by [NNotificationApi].
 *
 * [title], [content] and [type] are reactive and can be changed while the
 * notification is visible; [close] dismisses it immediately.
 */
class NNotificationHandle internal constructor(
    private val titleState: MutableState<String?>,
    private val contentState: MutableState<String?>,
    private val typeState: MutableState<NNotificationType>,
    private val closeAction: () -> Unit,
) {
    var title: String?
        get() = titleState.value
        set(value) {
            titleState.value = value
        }

    var content: String?
        get() = contentState.value
        set(value) {
            contentState.value = value
        }

    var type: NNotificationType
        get() = typeState.value
        set(value) {
            typeState.value = value
        }

    fun close() = closeAction()
}

/**
 * Imperative API exposed by [NNotificationProvider] through
 * [LocalNNotification].
 */
class NNotificationApi internal constructor(
    private val show: (NNotificationOptions) -> NNotificationHandle,
    private val clear: () -> Unit,
) {
    fun create(options: NNotificationOptions): NNotificationHandle = show(options)

    fun create(
        title: String? = null,
        content: String? = null,
        type: NNotificationType = NNotificationType.Default,
        duration: Long = 4500,
    ): NNotificationHandle = show(
        NNotificationOptions(
            title = title,
            content = content,
            type = type,
            duration = duration,
        ),
    )

    fun info(title: String? = null, content: String? = null, duration: Long = 4500): NNotificationHandle =
        create(title, content, NNotificationType.Info, duration)

    fun success(title: String? = null, content: String? = null, duration: Long = 4500): NNotificationHandle =
        create(title, content, NNotificationType.Success, duration)

    fun warning(title: String? = null, content: String? = null, duration: Long = 4500): NNotificationHandle =
        create(title, content, NNotificationType.Warning, duration)

    fun error(title: String? = null, content: String? = null, duration: Long = 4500): NNotificationHandle =
        create(title, content, NNotificationType.Error, duration)

    fun destroyAll() = clear()
}

/** Notification API of the nearest [NNotificationProvider]. */
val LocalNNotification = staticCompositionLocalOf<NNotificationApi?> { null }

@Composable
fun rememberNNotification(): NNotificationApi =
    LocalNNotification.current
        ?: error("NNotificationProvider is required to use NNotificationApi")

private class NNotificationEntry(
    val id: Long,
    val options: NNotificationOptions,
    val titleState: MutableState<String?>,
    val contentState: MutableState<String?>,
    val typeState: MutableState<NNotificationType>,
)

/**
 * Hosts the imperative notification API. Notifications stack in the chosen
 * window corner and dismiss themselves after their duration.
 */
@Composable
fun NNotificationProvider(
    placement: NNotificationPlacement = NNotificationPlacement.TopRight,
    max: Int = 5,
    scrollable: Boolean = false,
    content: @Composable () -> Unit,
) {
    val entries = remember { mutableStateListOf<NNotificationEntry>() }
    var nextId by remember { mutableStateOf(0L) }
    val api = remember(max) {
        NNotificationApi(
            show = { options ->
                val id = nextId++
                if (max > 0 && entries.size >= max) entries.removeAt(0)
                val titleState = mutableStateOf(options.title)
                val contentState = mutableStateOf(options.content)
                val typeState = mutableStateOf(options.type)
                entries.add(NNotificationEntry(id, options, titleState, contentState, typeState))
                NNotificationHandle(titleState, contentState, typeState) {
                    entries.removeAll { it.id == id }
                }
            },
            clear = { entries.clear() },
        )
    }
    CompositionLocalProvider(LocalNNotification provides api) {
        content()
        NNotificationHost(entries, placement, scrollable) { id -> entries.removeAll { it.id == id } }
    }
}

@Composable
private fun NNotificationHost(
    entries: List<NNotificationEntry>,
    placement: NNotificationPlacement,
    scrollable: Boolean,
    onClose: (Long) -> Unit,
) {
    if (entries.isEmpty()) return
    val margin = with(LocalDensity.current) { 20.dp.roundToPx() }
    val horizontal = when (placement) {
        NNotificationPlacement.Top, NNotificationPlacement.Bottom -> 0.5f
        NNotificationPlacement.TopLeft, NNotificationPlacement.BottomLeft -> 0f
        NNotificationPlacement.TopRight, NNotificationPlacement.BottomRight -> 1f
    }
    val vertical = when (placement) {
        NNotificationPlacement.Top, NNotificationPlacement.TopLeft, NNotificationPlacement.TopRight -> 0f
        NNotificationPlacement.Bottom, NNotificationPlacement.BottomLeft, NNotificationPlacement.BottomRight -> 1f
    }
    Popup(
        popupPositionProvider = remember(horizontal, vertical, margin) {
            NWindowPopupPositionProvider(
                horizontal = horizontal,
                vertical = vertical,
                marginX = margin,
                marginY = margin,
            )
        },
        onDismissRequest = {},
        properties = PopupProperties(
            focusable = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
    ) {
        BoxWithConstraints {
            val stack: @Composable () -> Unit = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    entries.forEach { entry ->
                        key(entry.id) {
                            NNotificationCard(entry) { onClose(entry.id) }
                        }
                    }
                }
            }
            if (scrollable) {
                NScrollbar(Modifier.heightIn(max = maxHeight * 0.8f)) { stack() }
            } else {
                stack()
            }
        }
    }
}

@Composable
private fun NNotificationCard(
    entry: NNotificationEntry,
    onClose: () -> Unit,
) {
    val options = entry.options
    val title = entry.titleState.value
    val content = entry.contentState.value
    val type = entry.typeState.value
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()

    LaunchedEffect(entry.id, options.duration, hovered) {
        if (options.duration > 0 && !(options.keepAliveOnHover && hovered)) {
            delay(options.duration)
            options.onAfterLeave?.invoke()
            onClose()
        }
    }

    val theme = LocalNTheme.current
    val shape = RoundedCornerShape(theme.borderRadius)
    Column(
        Modifier
            .width(340.dp)
            .nShadow(theme, 2, shape)
            .clip(shape)
            .background(theme.colors.popover, shape)
            .nBorder(theme.colors.border, 1.dp, theme.borderRadius)
            .hoverable(interaction)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (options.avatarText != null) {
                Box(
                    Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(theme.colors.primarySuppl),
                    contentAlignment = Alignment.Center,
                ) {
                    NText(
                        text = options.avatarText.take(1).uppercase(),
                        color = theme.colors.base,
                        size = theme.typography.fontSize.medium,
                        strong = true,
                    )
                }
            } else {
                val icon = type.icon()
                if (icon != null) {
                    NIcon(
                        icon = icon,
                        size = 20.dp,
                        color = theme.colors.statusColor(type.status()),
                    )
                }
            }
            if (title != null) {
                NText(
                    text = title,
                    modifier = Modifier.weight(1f),
                    size = theme.typography.fontSize.large,
                    strong = true,
                    color = theme.colors.text1,
                )
            } else {
                Spacer(Modifier.weight(1f))
            }
            if (options.closable) {
                NCloseButton(onClick = onClose, size = 18.dp, iconSize = 12.dp)
            }
        }
        if (content != null) {
            NText(
                text = content,
                color = theme.colors.text3,
                size = theme.typography.fontSize.medium,
            )
        }
        if (options.meta != null) {
            NText(
                text = options.meta,
                color = theme.colors.text3,
                size = theme.typography.fontSize.small,
            )
        }
        if (options.actionText != null) {
            NButton(
                size = NSize.Small,
                text = true,
                type = NButtonType.Primary,
                onClick = {
                    options.onAction?.invoke()
                    onClose()
                },
            ) {
                NText(options.actionText)
            }
        }
    }
}

private fun NNotificationType.status(): NStatus = when (this) {
    NNotificationType.Default -> NStatus.Default
    NNotificationType.Info -> NStatus.Info
    NNotificationType.Success -> NStatus.Success
    NNotificationType.Warning -> NStatus.Warning
    NNotificationType.Error -> NStatus.Error
}

private fun NNotificationType.icon(): ImageVector? = when (this) {
    NNotificationType.Default -> null
    NNotificationType.Info -> NIcons.Info
    NNotificationType.Success -> NIcons.Success
    NNotificationType.Warning -> NIcons.Warning
    NNotificationType.Error -> NIcons.Error
}
