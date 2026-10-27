package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
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
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NBaseLoading
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nShadow
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NStatus
import kotlinx.coroutines.delay

/** Semantic type of a global message. */
enum class NMessageType { Default, Info, Success, Warning, Error, Loading }

/** Where the message host is anchored inside the window. */
enum class NMessagePlacement { Top, TopLeft, TopRight, Bottom, BottomLeft, BottomRight }

/** Returned by [NMessageApi]; call [close] to dismiss the message immediately. */
class NMessageHandle internal constructor(private val closeAction: () -> Unit) {
    fun close() = closeAction()
}

/**
 * Imperative API exposed by [NMessageProvider] through [LocalNMessage].
 *
 * A `duration` of `0` keeps the message on screen until it is closed, which is
 * the default for [loading].
 */
class NMessageApi internal constructor(
    private val show: (String, NMessageType, Long) -> NMessageHandle,
    private val clear: () -> Unit,
) {
    fun create(text: String, duration: Long = 3000): NMessageHandle =
        show(text, NMessageType.Default, duration)

    fun info(text: String, duration: Long = 3000): NMessageHandle =
        show(text, NMessageType.Info, duration)

    fun success(text: String, duration: Long = 3000): NMessageHandle =
        show(text, NMessageType.Success, duration)

    fun warning(text: String, duration: Long = 3000): NMessageHandle =
        show(text, NMessageType.Warning, duration)

    fun error(text: String, duration: Long = 3000): NMessageHandle =
        show(text, NMessageType.Error, duration)

    fun loading(text: String, duration: Long = 0): NMessageHandle =
        show(text, NMessageType.Loading, duration)

    fun destroyAll() = clear()
}

/** Message API of the nearest [NMessageProvider]. */
val LocalNMessage = staticCompositionLocalOf<NMessageApi?> { null }

@Composable
fun rememberNMessage(): NMessageApi =
    LocalNMessage.current ?: error("NMessageProvider is required to use NMessageApi")

private data class NMessageEntry(
    val id: Long,
    val text: String,
    val type: NMessageType,
    val duration: Long,
)

/**
 * Hosts the imperative message API. Wrap the application content with it and
 * call [rememberNMessage] inside the subtree.
 */
@Composable
fun NMessageProvider(
    placement: NMessagePlacement = NMessagePlacement.Top,
    max: Int = 8,
    content: @Composable () -> Unit,
) {
    val entries = remember { mutableStateListOf<NMessageEntry>() }
    var nextId by remember { mutableStateOf(0L) }
    val api = remember(max) {
        NMessageApi(
            show = { text, type, duration ->
                val id = nextId++
                if (max > 0 && entries.size >= max) entries.removeAt(0)
                entries.add(NMessageEntry(id, text, type, duration))
                NMessageHandle { entries.removeAll { it.id == id } }
            },
            clear = { entries.clear() },
        )
    }
    androidx.compose.runtime.CompositionLocalProvider(LocalNMessage provides api) {
        content()
        NMessageHost(entries, placement) { id -> entries.removeAll { it.id == id } }
    }
}

@Composable
private fun NMessageHost(
    entries: List<NMessageEntry>,
    placement: NMessagePlacement,
    onClose: (Long) -> Unit,
) {
    if (entries.isEmpty()) return
    val margin = with(LocalDensity.current) { 16.dp.roundToPx() }
    val horizontal = when (placement) {
        NMessagePlacement.Top, NMessagePlacement.Bottom -> 0.5f
        NMessagePlacement.TopLeft, NMessagePlacement.BottomLeft -> 0f
        NMessagePlacement.TopRight, NMessagePlacement.BottomRight -> 1f
    }
    val vertical = when (placement) {
        NMessagePlacement.Top, NMessagePlacement.TopLeft, NMessagePlacement.TopRight -> 0f
        NMessagePlacement.Bottom, NMessagePlacement.BottomLeft, NMessagePlacement.BottomRight -> 1f
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
        Column(
            Modifier.widthIn(max = 460.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            entries.forEach { entry ->
                key(entry.id) { NMessageItem(entry) { onClose(entry.id) } }
            }
        }
    }
}

@Composable
private fun NMessageItem(entry: NMessageEntry, onClose: () -> Unit) {
    // The containing provider removes the entry after its duration elapses;
    // this effect only schedules the removal for its own item.
    LaunchedEffect(entry.id, entry.duration) {
        if (entry.duration > 0) {
            delay(entry.duration)
            onClose()
        }
    }
    val theme = LocalNTheme.current
    val shape = RoundedCornerShape(theme.borderRadius)
    Row(
        Modifier
            .nShadow(theme, 2, shape)
            .clip(shape)
            .background(theme.colors.popover, shape)
            .nBorder(theme.colors.border, 1.dp, theme.borderRadius)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        when (entry.type) {
            NMessageType.Loading -> NBaseLoading(size = 16.dp, strokeWidth = 2.dp)
            NMessageType.Default -> Unit
            else -> NIcon(
                icon = entry.type.icon(),
                size = 18.dp,
                color = theme.colors.statusColor(entry.type.status()),
            )
        }
        NText(
            text = entry.text,
            color = theme.colors.text2,
            size = theme.typography.fontSize.medium,
        )
    }
}

private fun NMessageType.status(): NStatus = when (this) {
    NMessageType.Default -> NStatus.Default
    NMessageType.Info -> NStatus.Info
    NMessageType.Success -> NStatus.Success
    NMessageType.Warning -> NStatus.Warning
    NMessageType.Error -> NStatus.Error
    NMessageType.Loading -> NStatus.Default
}

private fun NMessageType.icon(): ImageVector = when (this) {
    NMessageType.Info -> NIcons.Info
    NMessageType.Success -> NIcons.Success
    NMessageType.Warning -> NIcons.Warning
    NMessageType.Error -> NIcons.Error
    NMessageType.Default, NMessageType.Loading -> NIcons.Info
}
