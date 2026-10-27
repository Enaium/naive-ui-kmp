package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NButtonType
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NCloseButton
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nShadow
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus

/** Semantic type of a dialog opened through [NDialogApi]. */
enum class NDialogType { Default, Info, Success, Warning, Error }

/** Options accepted by [NDialogApi.create]. */
data class NDialogOptions(
    val title: String? = null,
    val content: String? = null,
    val type: NDialogType = NDialogType.Default,
    val positiveText: String? = null,
    val negativeText: String? = null,
    val showIcon: Boolean = true,
    val closable: Boolean = true,
    val maskClosable: Boolean = true,
    val closeOnPositiveClick: Boolean = true,
    val closeOnNegativeClick: Boolean = true,
    val onPositiveClick: (() -> Unit)? = null,
    val onNegativeClick: (() -> Unit)? = null,
    val onClose: (() -> Unit)? = null,
)

/** Returned by [NDialogApi]; call [close] to dismiss the dialog. */
class NDialogHandle internal constructor(private val closeAction: () -> Unit) {
    fun close() = closeAction()
}

/**
 * Imperative dialog API exposed by [NDialogProvider] through [LocalNDialog].
 *
 * Dialogs stack in the window center; the last created dialog is the topmost
 * one and the only one affected by scrim clicks and the back button.
 */
class NDialogApi internal constructor(
    private val show: (NDialogOptions) -> NDialogHandle,
    private val clear: () -> Unit,
) {
    fun create(options: NDialogOptions): NDialogHandle = show(options)

    fun create(
        title: String? = null,
        content: String? = null,
        positiveText: String? = null,
        negativeText: String? = null,
        type: NDialogType = NDialogType.Default,
        onPositiveClick: (() -> Unit)? = null,
        onNegativeClick: (() -> Unit)? = null,
    ): NDialogHandle = show(
        NDialogOptions(
            title = title,
            content = content,
            type = type,
            positiveText = positiveText,
            negativeText = negativeText,
            onPositiveClick = onPositiveClick,
            onNegativeClick = onNegativeClick,
        ),
    )

    fun info(
        title: String? = null,
        content: String? = null,
        positiveText: String? = null,
        negativeText: String? = null,
        onPositiveClick: (() -> Unit)? = null,
        onNegativeClick: (() -> Unit)? = null,
    ): NDialogHandle = create(
        title = title,
        content = content,
        positiveText = positiveText,
        negativeText = negativeText,
        type = NDialogType.Info,
        onPositiveClick = onPositiveClick,
        onNegativeClick = onNegativeClick,
    )

    fun success(
        title: String? = null,
        content: String? = null,
        positiveText: String? = null,
        negativeText: String? = null,
        onPositiveClick: (() -> Unit)? = null,
        onNegativeClick: (() -> Unit)? = null,
    ): NDialogHandle = create(
        title = title,
        content = content,
        positiveText = positiveText,
        negativeText = negativeText,
        type = NDialogType.Success,
        onPositiveClick = onPositiveClick,
        onNegativeClick = onNegativeClick,
    )

    fun warning(
        title: String? = null,
        content: String? = null,
        positiveText: String? = null,
        negativeText: String? = null,
        onPositiveClick: (() -> Unit)? = null,
        onNegativeClick: (() -> Unit)? = null,
    ): NDialogHandle = create(
        title = title,
        content = content,
        positiveText = positiveText,
        negativeText = negativeText,
        type = NDialogType.Warning,
        onPositiveClick = onPositiveClick,
        onNegativeClick = onNegativeClick,
    )

    fun error(
        title: String? = null,
        content: String? = null,
        positiveText: String? = null,
        negativeText: String? = null,
        onPositiveClick: (() -> Unit)? = null,
        onNegativeClick: (() -> Unit)? = null,
    ): NDialogHandle = create(
        title = title,
        content = content,
        positiveText = positiveText,
        negativeText = negativeText,
        type = NDialogType.Error,
        onPositiveClick = onPositiveClick,
        onNegativeClick = onNegativeClick,
    )

    fun destroyAll() = clear()
}

/** Dialog API of the nearest [NDialogProvider]. */
val LocalNDialog = staticCompositionLocalOf<NDialogApi?> { null }

@Composable
fun rememberNDialog(): NDialogApi =
    LocalNDialog.current ?: error("NDialogProvider is required to use NDialogApi")

private data class NDialogEntry(val id: Long, val options: NDialogOptions)

/**
 * Hosts the imperative dialog API. Wrap the application content with it and
 * call [rememberNDialog] inside the subtree.
 */
@Composable
fun NDialogProvider(content: @Composable () -> Unit) {
    val entries = remember { mutableStateListOf<NDialogEntry>() }
    var nextId by remember { mutableStateOf(0L) }
    val api = remember {
        NDialogApi(
            show = { options ->
                val id = nextId++
                entries.add(NDialogEntry(id, options))
                NDialogHandle { entries.removeAll { it.id == id } }
            },
            clear = { entries.clear() },
        )
    }
    CompositionLocalProvider(LocalNDialog provides api) {
        content()
        NDialogHost(entries) { id -> entries.removeAll { it.id == id } }
    }
}

@Composable
private fun NDialogHost(entries: List<NDialogEntry>, onClose: (Long) -> Unit) {
    if (entries.isEmpty()) return
    val theme = LocalNTheme.current
    val top = entries.last()
    val closeTop = {
        top.options.onClose?.invoke()
        onClose(top.id)
    }
    Popup(
        popupPositionProvider = remember { NWindowPopupPositionProvider() },
        onDismissRequest = closeTop,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = top.options.maskClosable,
        ),
    ) {
        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .background(theme.colors.mask),
        ) {
            val dialogWidth = minOf(400.dp, (maxWidth - 48.dp).coerceAtLeast(240.dp))
            val stackMaxHeight = (maxHeight - 48.dp).coerceAtLeast(160.dp)
            Box(
                Modifier
                    .fillMaxSize()
                    .nOverlayScrimClick(top.options.maskClosable, closeTop),
                contentAlignment = Alignment.Center,
            ) {
                NScrollbar(Modifier.heightIn(max = stackMaxHeight)) {
                    Column(
                        Modifier.padding(vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        entries.forEach { entry ->
                            key(entry.id) {
                                NDialogCard(entry, dialogWidth) { onClose(entry.id) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NDialogCard(entry: NDialogEntry, width: Dp, onClose: () -> Unit) {
    val options = entry.options
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    val shape = RoundedCornerShape(theme.borderRadius)
    val status = options.type.status()
    val showPositive = options.positiveText != null ||
        options.onPositiveClick != null ||
        options.type != NDialogType.Default
    val showNegative = options.negativeText != null || options.onNegativeClick != null
    val dismiss = {
        options.onClose?.invoke()
        onClose()
    }

    Column(
        Modifier
            .width(width)
            .clip(shape)
            .background(theme.colors.modal, shape)
            .nShadow(theme, 3, shape)
            .nBorder(theme.colors.border, 1.dp, theme.borderRadius)
            .nConsumeClicks()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (options.title != null || options.closable) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (options.showIcon) {
                    NIcon(
                        icon = options.type.icon(),
                        size = 22.dp,
                        color = if (options.type == NDialogType.Default) {
                            theme.colors.primary
                        } else {
                            theme.colors.statusColor(status)
                        },
                    )
                }
                if (options.title != null) {
                    NText(
                        text = options.title,
                        modifier = Modifier.weight(1f),
                        size = theme.typography.fontSize.huge,
                        strong = true,
                        color = theme.colors.text1,
                    )
                } else {
                    Spacer(Modifier.weight(1f))
                }
                if (options.closable) {
                    NCloseButton(onClick = dismiss, size = 20.dp, iconSize = 14.dp)
                }
            }
        }
        if (options.content != null) {
            NText(
                text = options.content,
                color = theme.colors.text3,
                size = theme.typography.fontSize.medium,
            )
        }
        if (showPositive || showNegative) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (showNegative) {
                    NButton(
                        size = NSize.Small,
                        onClick = {
                            options.onNegativeClick?.invoke()
                            if (options.closeOnNegativeClick) onClose()
                        },
                    ) {
                        NText(options.negativeText ?: locale.cancel)
                    }
                }
                if (showPositive) {
                    NButton(
                        size = NSize.Small,
                        type = NButtonType.Primary,
                        onClick = {
                            options.onPositiveClick?.invoke()
                            if (options.closeOnPositiveClick) onClose()
                        },
                    ) {
                        NText(options.positiveText ?: locale.ok)
                    }
                }
            }
        }
    }
}

private fun NDialogType.status(): NStatus = when (this) {
    NDialogType.Default -> NStatus.Default
    NDialogType.Info -> NStatus.Info
    NDialogType.Success -> NStatus.Success
    NDialogType.Warning -> NStatus.Warning
    NDialogType.Error -> NStatus.Error
}

private fun NDialogType.icon(): ImageVector = when (this) {
    NDialogType.Default, NDialogType.Info -> NIcons.Info
    NDialogType.Success -> NIcons.Success
    NDialogType.Warning -> NIcons.Warning
    NDialogType.Error -> NIcons.Error
}
