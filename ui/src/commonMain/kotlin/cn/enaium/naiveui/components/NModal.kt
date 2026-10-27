package cn.enaium.naiveui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import cn.enaium.naiveui.internal.NCloseButton
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nShadow
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import kotlin.math.roundToInt

/**
 * Positions a popup in window coordinates instead of relative to its anchor.
 *
 * [horizontal] and [vertical] are fractions of the free space: `0f` = start,
 * `0.5f` = center, `1f` = end. Margins are applied to the start/end edges and
 * ignored for centered axes.
 */
internal class NWindowPopupPositionProvider(
    private val horizontal: Float = 0f,
    private val vertical: Float = 0f,
    private val marginX: Int = 0,
    private val marginY: Int = 0,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val availableX = (windowSize.width - popupContentSize.width).coerceAtLeast(0)
        val availableY = (windowSize.height - popupContentSize.height).coerceAtLeast(0)
        val x = when {
            horizontal >= 1f -> availableX - marginX
            horizontal <= 0f -> marginX
            else -> (availableX * horizontal).roundToInt()
        }
        val y = when {
            vertical >= 1f -> availableY - marginY
            vertical <= 0f -> marginY
            else -> (availableY * vertical).roundToInt()
        }
        return IntOffset(x, y)
    }
}

@Composable
internal fun Modifier.nOverlayScrimClick(enabled: Boolean, onClick: () -> Unit): Modifier {
    val interaction = remember { MutableInteractionSource() }
    return clickable(
        interactionSource = interaction,
        indication = null,
    ) { if (enabled) onClick() }
}

/** Swallows pointer clicks so a surface above a scrim stays open. */
@Composable
internal fun Modifier.nConsumeClicks(): Modifier {
    val interaction = remember { MutableInteractionSource() }
    return clickable(
        interactionSource = interaction,
        indication = null,
    ) {}
}

/** Whether the modal renders the card or the dialog preset. */
enum class NModalPreset { Card, Dialog }

/** Where the modal surface is anchored inside the window. */
enum class NModalPlacement { Center, Top, TopLeft, TopRight, BottomLeft, BottomRight }

/**
 * A modal window rendered above the whole application window.
 *
 * The scrim covers the window, `maskClosable` decides whether a click on it
 * dismisses the modal, and the content area scrolls when it gets too tall.
 * `draggable` lets the header move the surface, and `transformOrigin` sets the
 * pivot of the entrance animation.
 */
@Composable
fun NModal(
    show: Boolean,
    onUpdateShow: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    preset: NModalPreset = NModalPreset.Card,
    title: String? = null,
    contentText: String? = null,
    closable: Boolean = true,
    maskClosable: Boolean = true,
    showMask: Boolean = true,
    width: Dp = 440.dp,
    height: Dp? = null,
    placement: NModalPlacement = NModalPlacement.Center,
    draggable: Boolean = false,
    transformOrigin: TransformOrigin = TransformOrigin.Center,
    positiveText: String? = null,
    negativeText: String? = null,
    onPositiveClick: (() -> Unit)? = null,
    onNegativeClick: (() -> Unit)? = null,
    header: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(show) {
        if (show) {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(200))
        } else {
            progress.snapTo(0f)
        }
    }
    if (!show) return

    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    val close = { onUpdateShow(false) }
    val hasHeader = header != null || title != null || closable
    val shape = RoundedCornerShape(theme.borderRadius)
    val alignment = when (placement) {
        NModalPlacement.Center -> Alignment.Center
        NModalPlacement.Top -> Alignment.TopCenter
        NModalPlacement.TopLeft -> Alignment.TopStart
        NModalPlacement.TopRight -> Alignment.TopEnd
        NModalPlacement.BottomLeft -> Alignment.BottomStart
        NModalPlacement.BottomRight -> Alignment.BottomEnd
    }
    val outerPadding = when (placement) {
        NModalPlacement.Center -> PaddingValues(0.dp)
        NModalPlacement.Top -> PaddingValues(top = 80.dp)
        else -> PaddingValues(40.dp)
    }
    val scrimAlpha = theme.colors.mask.alpha * progress.value

    Popup(
        popupPositionProvider = remember { NWindowPopupPositionProvider() },
        onDismissRequest = close,
        properties = PopupProperties(
            focusable = showMask,
            dismissOnBackPress = showMask,
            dismissOnClickOutside = showMask && maskClosable,
        ),
    ) {
        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .then(
                    if (showMask) {
                        Modifier.background(theme.colors.mask.copy(alpha = scrimAlpha))
                    } else {
                        Modifier
                    },
                ),
        ) {
            val modalWidth = minOf(width, (maxWidth - 48.dp).coerceAtLeast(240.dp))
            val contentMaxHeight = maxHeight * 0.66f
            val density = LocalDensity.current
            val windowWidthPx = with(density) { maxWidth.toPx() }
            val windowHeightPx = with(density) { maxHeight.toPx() }
            var cardSize by remember { mutableStateOf(IntSize.Zero) }
            var dragOffset by remember(show) { mutableStateOf(IntOffset.Zero) }

            val dragModifier = if (draggable) {
                Modifier.pointerInput(Unit) {
                    detectDragGestures { change, drag ->
                        change.consume()
                        val limitX = ((windowWidthPx - cardSize.width) / 2f).coerceAtLeast(0f)
                        val limitY = ((windowHeightPx - cardSize.height) / 2f).coerceAtLeast(0f)
                        dragOffset = IntOffset(
                            (dragOffset.x + drag.x).coerceIn(-limitX, limitX).roundToInt(),
                            (dragOffset.y + drag.y).coerceIn(-limitY, limitY).roundToInt(),
                        )
                    }
                }
            } else {
                Modifier
            }

            Box(
                Modifier
                    .fillMaxSize()
                    .then(
                        if (showMask) {
                            Modifier.nOverlayScrimClick(maskClosable, close)
                        } else {
                            Modifier
                        },
                    )
                    .padding(outerPadding),
                contentAlignment = alignment,
            ) {
                val headerContent: @Composable () -> Unit = {
                    if (header != null) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .then(dragModifier)
                                .padding(start = 20.dp, end = 12.dp, top = 16.dp, bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(Modifier.weight(1f)) { header() }
                            if (closable) {
                                NCloseButton(onClick = close, size = 20.dp, iconSize = 14.dp)
                            }
                        }
                    } else if (title != null || closable) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .then(dragModifier)
                                .padding(start = 20.dp, end = 12.dp, top = 16.dp, bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
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
                                NCloseButton(onClick = close, size = 20.dp, iconSize = 14.dp)
                            }
                        }
                    }
                }
                val bodyContent: @Composable () -> Unit = {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 20.dp,
                                end = 20.dp,
                                top = if (hasHeader) 0.dp else 20.dp,
                                bottom = 20.dp,
                            ),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        if (contentText != null) {
                            NText(
                                text = contentText,
                                color = theme.colors.text3,
                                size = theme.typography.fontSize.medium,
                            )
                        }
                        content()
                    }
                }

                val entranceOrigin = transformOrigin
                Column(
                    Modifier
                        .then(modifier)
                        .width(modalWidth)
                        .then(if (height != null) Modifier.height(height) else Modifier)
                        .nShadow(theme, 3, shape)
                        .clip(shape)
                        .background(theme.colors.modal, shape)
                        .nBorder(theme.colors.border, 1.dp, theme.borderRadius)
                        .nConsumeClicks()
                        .onSizeChanged { cardSize = it }
                        .graphicsLayer {
                            translationX = dragOffset.x.toFloat()
                            translationY = dragOffset.y.toFloat()
                            val scale = 0.85f + 0.15f * progress.value
                            scaleX = scale
                            scaleY = scale
                            alpha = progress.value
                            this.transformOrigin = entranceOrigin
                        },
                ) {
                    headerContent()
                    if (height != null) {
                        NScrollbar(Modifier.weight(1f)) { bodyContent() }
                    } else {
                        NScrollbar(Modifier.heightIn(max = contentMaxHeight)) { bodyContent() }
                    }
                    if (actions != null) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            actions()
                        }
                    } else if (preset == NModalPreset.Dialog) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (negativeText != null || onNegativeClick != null) {
                                NButton(
                                    size = NSize.Small,
                                    onClick = {
                                        onNegativeClick?.invoke()
                                        close()
                                    },
                                ) {
                                    NText(negativeText ?: locale.popconfirmNo)
                                }
                            }
                            NButton(
                                size = NSize.Small,
                                type = NButtonType.Primary,
                                onClick = {
                                    onPositiveClick?.invoke()
                                    close()
                                },
                            ) {
                                NText(positiveText ?: locale.ok)
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Options accepted by [NModalApi.create]. */
data class NModalOptions(
    val title: String? = null,
    val contentText: String? = null,
    val preset: NModalPreset = NModalPreset.Card,
    val width: Dp = 440.dp,
    val height: Dp? = null,
    val closable: Boolean = true,
    val maskClosable: Boolean = true,
    val showMask: Boolean = true,
    val transformOrigin: TransformOrigin = TransformOrigin.Center,
    val positiveText: String? = null,
    val negativeText: String? = null,
    val onPositiveClick: (() -> Unit)? = null,
    val onNegativeClick: (() -> Unit)? = null,
)

/** Returned by [NModalApi]; call [close] to dismiss the modal. */
class NModalHandle internal constructor(private val closeAction: () -> Unit) {
    fun close() = closeAction()
}

/** Imperative modal API exposed by [NModalProvider] through [LocalNModal]. */
class NModalApi internal constructor(
    private val show: (NModalOptions) -> NModalHandle,
    private val clear: () -> Unit,
) {
    fun create(options: NModalOptions): NModalHandle = show(options)

    fun create(
        title: String? = null,
        contentText: String? = null,
        preset: NModalPreset = NModalPreset.Card,
        width: Dp = 440.dp,
        positiveText: String? = null,
        onPositiveClick: (() -> Unit)? = null,
    ): NModalHandle = show(
        NModalOptions(
            title = title,
            contentText = contentText,
            preset = preset,
            width = width,
            positiveText = positiveText,
            onPositiveClick = onPositiveClick,
        ),
    )

    fun destroyAll() = clear()
}

/** Modal API of the nearest [NModalProvider]. */
val LocalNModal = staticCompositionLocalOf<NModalApi?> { null }

@Composable
fun rememberNModal(): NModalApi =
    LocalNModal.current ?: error("NModalProvider is required to use NModalApi")

private data class NModalEntry(val id: Long, val options: NModalOptions)

/**
 * Hosts the imperative modal API. Wrap the application content with it and
 * call [rememberNModal] inside the subtree.
 */
@Composable
fun NModalProvider(content: @Composable () -> Unit) {
    val entries = remember { mutableStateListOf<NModalEntry>() }
    var nextId by remember { mutableStateOf(0L) }
    val api = remember {
        NModalApi(
            show = { options ->
                val id = nextId++
                entries.add(NModalEntry(id, options))
                NModalHandle { entries.removeAll { it.id == id } }
            },
            clear = { entries.clear() },
        )
    }
    CompositionLocalProvider(LocalNModal provides api) {
        content()
        entries.forEach { entry ->
            key(entry.id) {
                val options = entry.options
                val closeEntry = { entries.removeAll { it.id == entry.id }; Unit }
                val modalActions: (@Composable RowScope.() -> Unit)? =
                    if (options.preset == NModalPreset.Card && options.positiveText != null) {
                        {
                            NButton(
                                size = NSize.Small,
                                type = NButtonType.Primary,
                                onClick = {
                                    options.onPositiveClick?.invoke()
                                    closeEntry()
                                },
                            ) {
                                NText(options.positiveText)
                            }
                        }
                    } else {
                        null
                    }
                NModal(
                    show = true,
                    onUpdateShow = { visible -> if (!visible) closeEntry() },
                    preset = options.preset,
                    title = options.title,
                    contentText = options.contentText,
                    closable = options.closable,
                    maskClosable = options.maskClosable,
                    showMask = options.showMask,
                    width = options.width,
                    height = options.height,
                    transformOrigin = options.transformOrigin,
                    positiveText = options.positiveText,
                    negativeText = options.negativeText,
                    onPositiveClick = options.onPositiveClick,
                    onNegativeClick = options.onNegativeClick,
                    actions = modalActions,
                )
            }
        }
    }
}
