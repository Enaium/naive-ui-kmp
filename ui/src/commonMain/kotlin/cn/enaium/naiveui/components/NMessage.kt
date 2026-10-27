package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.CompositionLocalProvider
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
import cn.enaium.naiveui.internal.NBaseLoading
import cn.enaium.naiveui.internal.NCloseButton
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nShadow
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NStatus
import kotlinx.coroutines.delay

/** Semantic type of a global message. */
enum class NMessageType { Default, Info, Success, Warning, Error, Loading }

/** Where the message host is anchored inside the window. */
enum class NMessagePlacement { Top, TopLeft, TopRight, Bottom, BottomLeft, BottomRight }

/**
 * Returned by [NMessageApi].
 *
 * [text] and [type] are reactive and can be changed while the message is on
 * screen; [close] dismisses it immediately.
 */
class NMessageHandle internal constructor(
    private val textState: MutableState<String>,
    private val typeState: MutableState<NMessageType>,
    private val closeAction: () -> Unit,
) {
    var text: String
        get() = textState.value
        set(value) {
            textState.value = value
        }

    var type: NMessageType
        get() = typeState.value
        set(value) {
            typeState.value = value
        }

    fun close() = closeAction()
}

internal data class NMessageSpec(
    val text: String,
    val type: NMessageType,
    val duration: Long,
    val closable: Boolean = false,
    val showIcon: Boolean = true,
    val icon: ImageVector? = null,
    val keepAliveOnHover: Boolean = false,
    val customContent: (@Composable () -> Unit)? = null,
)

/**
 * Imperative API exposed by [NMessageProvider] through [LocalNMessage].
 *
 * A `duration` of `0` keeps the message on screen until it is closed, which is
 * the default for [loading]. Use [custom] to render arbitrary content.
 */
class NMessageApi internal constructor(
    private val show: (NMessageSpec) -> NMessageHandle,
    private val clear: () -> Unit,
) {
    fun create(
        text: String,
        duration: Long = 3000,
        closable: Boolean = false,
        showIcon: Boolean = true,
        icon: ImageVector? = null,
        keepAliveOnHover: Boolean = false,
    ): NMessageHandle = show(
        NMessageSpec(
            text = text,
            type = NMessageType.Default,
            duration = duration,
            closable = closable,
            showIcon = showIcon,
            icon = icon,
            keepAliveOnHover = keepAliveOnHover,
        ),
    )

    fun info(
        text: String,
        duration: Long = 3000,
        closable: Boolean = false,
        showIcon: Boolean = true,
        icon: ImageVector? = null,
        keepAliveOnHover: Boolean = false,
    ): NMessageHandle = show(
        NMessageSpec(text, NMessageType.Info, duration, closable, showIcon, icon, keepAliveOnHover),
    )

    fun success(
        text: String,
        duration: Long = 3000,
        closable: Boolean = false,
        showIcon: Boolean = true,
        icon: ImageVector? = null,
        keepAliveOnHover: Boolean = false,
    ): NMessageHandle = show(
        NMessageSpec(text, NMessageType.Success, duration, closable, showIcon, icon, keepAliveOnHover),
    )

    fun warning(
        text: String,
        duration: Long = 3000,
        closable: Boolean = false,
        showIcon: Boolean = true,
        icon: ImageVector? = null,
        keepAliveOnHover: Boolean = false,
    ): NMessageHandle = show(
        NMessageSpec(text, NMessageType.Warning, duration, closable, showIcon, icon, keepAliveOnHover),
    )

    fun error(
        text: String,
        duration: Long = 3000,
        closable: Boolean = false,
        showIcon: Boolean = true,
        icon: ImageVector? = null,
        keepAliveOnHover: Boolean = false,
    ): NMessageHandle = show(
        NMessageSpec(text, NMessageType.Error, duration, closable, showIcon, icon, keepAliveOnHover),
    )

    fun loading(
        text: String,
        duration: Long = 0,
        closable: Boolean = false,
        keepAliveOnHover: Boolean = false,
    ): NMessageHandle = show(
        NMessageSpec(
            text = text,
            type = NMessageType.Loading,
            duration = duration,
            closable = closable,
            showIcon = true,
            keepAliveOnHover = keepAliveOnHover,
        ),
    )

    fun custom(
        duration: Long = 3000,
        closable: Boolean = true,
        content: @Composable () -> Unit,
    ): NMessageHandle = show(
        NMessageSpec(
            text = "",
            type = NMessageType.Default,
            duration = duration,
            closable = closable,
            showIcon = false,
            customContent = content,
        ),
    )

    fun destroyAll() = clear()
}

/** Message API of the nearest [NMessageProvider]. */
val LocalNMessage = staticCompositionLocalOf<NMessageApi?> { null }

@Composable
fun rememberNMessage(): NMessageApi =
    LocalNMessage.current ?: error("NMessageProvider is required to use NMessageApi")

private class NMessageEntry(
    val id: Long,
    val spec: NMessageSpec,
    val textState: MutableState<String>,
    val typeState: MutableState<NMessageType>,
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
            show = { spec ->
                val id = nextId++
                if (max > 0 && entries.size >= max) entries.removeAt(0)
                val textState = mutableStateOf(spec.text)
                val typeState = mutableStateOf(spec.type)
                entries.add(NMessageEntry(id, spec, textState, typeState))
                NMessageHandle(textState, typeState) {
                    entries.removeAll { it.id == id }
                }
            },
            clear = { entries.clear() },
        )
    }
    CompositionLocalProvider(LocalNMessage provides api) {
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
    val spec = entry.spec
    val text = entry.textState.value
    val type = entry.typeState.value
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()

    LaunchedEffect(entry.id, spec.duration, hovered) {
        if (spec.duration > 0 && !(spec.keepAliveOnHover && hovered)) {
            delay(spec.duration)
            onClose()
        }
    }

    val theme = LocalNTheme.current
    if (spec.customContent != null) {
        Box(
            Modifier
                .widthIn(max = 460.dp)
                .hoverable(interaction)
                .padding(4.dp),
        ) {
            spec.customContent()
            if (spec.closable) {
                NCloseButton(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.TopEnd),
                    size = 18.dp,
                    iconSize = 12.dp,
                )
            }
        }
        return
    }
    val shape = RoundedCornerShape(theme.borderRadius)
    Row(
        Modifier
            .nShadow(theme, 2, shape)
            .clip(shape)
            .background(theme.colors.popover, shape)
            .nBorder(theme.colors.border, 1.dp, theme.borderRadius)
            .hoverable(interaction)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (spec.showIcon) {
            when (type) {
                NMessageType.Loading -> NBaseLoading(size = 16.dp, strokeWidth = 2.dp)
                NMessageType.Default -> Unit
                else -> NIcon(
                    icon = spec.icon ?: type.icon(),
                    size = 18.dp,
                    color = theme.colors.statusColor(type.status()),
                )
            }
        }
        NText(
            text = text,
            modifier = Modifier.weight(1f, fill = false),
            color = theme.colors.text2,
            size = theme.typography.fontSize.medium,
        )
        if (spec.closable) {
            NCloseButton(onClick = onClose, size = 18.dp, iconSize = 12.dp)
        }
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
