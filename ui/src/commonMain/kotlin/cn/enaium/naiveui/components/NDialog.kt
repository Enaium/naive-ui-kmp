package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
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
import kotlin.math.roundToInt

/** Semantic type of a dialog opened through [NDialogApi]. */
enum class NDialogType { Default, Info, Success, Warning, Error }

/** Options accepted by [NDialogApi.create]. */
data class NDialogOptions(
    val title: String? = null,
    val content: String? = null,
    val type: NDialogType = NDialogType.Default,
    val positiveText: String? = null,
    val negativeText: String? = null,
    val actionText: String? = null,
    val showIcon: Boolean = true,
    val closable: Boolean = true,
    val maskClosable: Boolean = true,
    val closeOnPositiveClick: Boolean = true,
    val closeOnNegativeClick: Boolean = true,
    val closeOnEsc: Boolean = true,
    val draggable: Boolean = false,
    val onPositiveClick: (() -> Unit)? = null,
    val onNegativeClick: (() -> Unit)? = null,
    val onClose: (() -> Unit)? = null,
    val onMaskClick: (() -> Unit)? = null,
    val onEsc: (() -> Unit)? = null,
)

/**
 * Returned by [NDialogApi]; call [close] to dismiss the dialog.
 *
 * [content] and [loading] are reactive: change them while the dialog is open
 * to update the surface (for example during an async confirmation).
 */
class NDialogHandle internal constructor(
    private val contentState: MutableState<String?>,
    private val loadingState: MutableState<Boolean>,
    private val closeAction: () -> Unit,
) {
    var content: String?
        get() = contentState.value
        set(value) {
            contentState.value = value
        }

    var loading: Boolean
        get() = loadingState.value
        set(value) {
            loadingState.value = value
        }

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
    private val list: () -> List<NDialogOptions>,
    private val clear: () -> Unit,
) {
    /** All dialogs currently owned by the provider, oldest first. */
    val dialogs: List<NDialogOptions>
        get() = list()

    fun create(options: NDialogOptions): NDialogHandle = show(options)

    fun create(
        title: String? = null,
        content: String? = null,
        positiveText: String? = null,
        negativeText: String? = null,
        type: NDialogType = NDialogType.Default,
        actionText: String? = null,
        onPositiveClick: (() -> Unit)? = null,
        onNegativeClick: (() -> Unit)? = null,
    ): NDialogHandle = show(
        NDialogOptions(
            title = title,
            content = content,
            type = type,
            positiveText = positiveText,
            negativeText = negativeText,
            actionText = actionText,
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

private class NDialogEntry(
    val id: Long,
    val options: NDialogOptions,
    val contentState: MutableState<String?>,
    val loadingState: MutableState<Boolean>,
)

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
                val contentState = mutableStateOf(options.content)
                val loadingState = mutableStateOf(false)
                entries.add(NDialogEntry(id, options, contentState, loadingState))
                NDialogHandle(contentState, loadingState) {
                    entries.removeAll { it.id == id }
                }
            },
            list = { entries.map { it.options } },
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
    val onScrimClick = {
        top.options.onMaskClick?.invoke()
        if (top.options.maskClosable) closeTop()
    }
    Popup(
        popupPositionProvider = remember { NWindowPopupPositionProvider() },
        onDismissRequest = {
            top.options.onEsc?.invoke()
            if (top.options.closeOnEsc) closeTop()
        },
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
                    .nOverlayScrimClick(true, onScrimClick),
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
                                NDialogSurface(
                                    width = dialogWidth,
                                    title = entry.options.title,
                                    content = entry.contentState.value,
                                    type = entry.options.type,
                                    showIcon = entry.options.showIcon,
                                    closable = entry.options.closable,
                                    positiveText = entry.options.positiveText,
                                    negativeText = entry.options.negativeText,
                                    actionText = entry.options.actionText,
                                    loading = entry.loadingState.value,
                                    showPositive = entry.options.positiveText != null ||
                                        entry.options.onPositiveClick != null ||
                                        entry.options.type != NDialogType.Default,
                                    showNegative = entry.options.negativeText != null ||
                                        entry.options.onNegativeClick != null,
                                    draggable = entry.options.draggable,
                                    onPositiveClick = {
                                        entry.options.onPositiveClick?.invoke()
                                        if (entry.options.closeOnPositiveClick && !entry.loadingState.value) {
                                            onClose(entry.id)
                                        }
                                    },
                                    onNegativeClick = {
                                        entry.options.onNegativeClick?.invoke()
                                        if (entry.options.closeOnNegativeClick) onClose(entry.id)
                                    },
                                    onClose = {
                                        entry.options.onClose?.invoke()
                                        onClose(entry.id)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Inline dialog surface, equivalent to using `<n-dialog>` as a component.
 */
@Composable
fun NDialog(
    title: String? = null,
    content: String? = null,
    positiveText: String? = null,
    negativeText: String? = null,
    type: NDialogType = NDialogType.Default,
    showIcon: Boolean = true,
    closable: Boolean = true,
    actionText: String? = null,
    modifier: Modifier = Modifier,
    onPositiveClick: (() -> Unit)? = null,
    onNegativeClick: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
) {
    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        NDialogSurface(
            width = 400.dp,
            title = title,
            content = content,
            type = type,
            showIcon = showIcon,
            closable = closable,
            positiveText = positiveText,
            negativeText = negativeText,
            actionText = actionText,
            loading = false,
            showPositive = positiveText != null,
            showNegative = negativeText != null,
            onPositiveClick = { onPositiveClick?.invoke() },
            onNegativeClick = { onNegativeClick?.invoke() },
            onClose = { onClose?.invoke() },
        )
    }
}

@Composable
private fun NDialogSurface(
    width: Dp,
    title: String?,
    content: String?,
    type: NDialogType,
    showIcon: Boolean,
    closable: Boolean,
    positiveText: String?,
    negativeText: String?,
    actionText: String?,
    loading: Boolean,
    showPositive: Boolean,
    showNegative: Boolean,
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    draggable: Boolean = false,
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    val shape = RoundedCornerShape(theme.borderRadius)
    val status = type.status()
    var dragOffset by remember { mutableStateOf(IntOffset.Zero) }
    val dragModifier = if (draggable) {
        Modifier.pointerInput(Unit) {
            detectDragGestures { change, drag ->
                change.consume()
                dragOffset = IntOffset(
                    dragOffset.x + drag.x.roundToInt(),
                    dragOffset.y + drag.y.roundToInt(),
                )
            }
        }
    } else {
        Modifier
    }

    Column(
        modifier
            .width(width)
            .nShadow(theme, 3, shape)
            .clip(shape)
            .background(theme.colors.modal, shape)
            .nBorder(theme.colors.border, 1.dp, theme.borderRadius)
            .nConsumeClicks()
            .padding(20.dp)
            .graphicsLayer {
                translationX = dragOffset.x.toFloat()
                translationY = dragOffset.y.toFloat()
            },
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (title != null || closable) {
            Row(
                Modifier.fillMaxWidth().then(dragModifier),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (showIcon) {
                    NIcon(
                        icon = type.icon(),
                        size = 22.dp,
                        color = if (type == NDialogType.Default) {
                            theme.colors.primary
                        } else {
                            theme.colors.statusColor(status)
                        },
                    )
                }
                if (title != null) {
                    NText(
                        text = title,
                        modifier = Modifier.weight(1f),
                        size = theme.typography.fontSize.huge,
                        strong = true,
                        color = theme.colors.text1,
                    )
                } else {
                    Spacer(Modifier.weight(1f))
                }
                if (closable) {
                    NCloseButton(onClick = onClose, size = 20.dp, iconSize = 14.dp)
                }
            }
        }
        if (content != null) {
            NText(
                text = content,
                color = theme.colors.text3,
                size = theme.typography.fontSize.medium,
            )
        }
        if (actionText != null) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NText(
                    text = actionText,
                    color = theme.colors.text2,
                    size = theme.typography.fontSize.medium,
                )
            }
        } else if (showPositive || showNegative) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (showNegative) {
                    NButton(size = NSize.Small, onClick = onNegativeClick) {
                        NText(negativeText ?: locale.cancel)
                    }
                }
                if (showPositive) {
                    NButton(
                        size = NSize.Small,
                        type = NButtonType.Primary,
                        loading = loading,
                        onClick = onPositiveClick,
                    ) {
                        NText(positiveText ?: locale.ok)
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
