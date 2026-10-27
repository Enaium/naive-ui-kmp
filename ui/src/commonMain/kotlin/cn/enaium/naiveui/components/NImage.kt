package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NBaseLoading
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NPopover
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.theme.LocalNTheme
import coil3.compose.AsyncImagePainter
import coil3.compose.AsyncImage

/** How an image fills its box, mirroring naive-ui's `object-fit`. */
enum class NImageObjectFit { Fill, Contain, Cover, None, ScaleDown }

private val NImageObjectFit.contentScale: ContentScale
    get() = when (this) {
        NImageObjectFit.Fill -> ContentScale.FillBounds
        NImageObjectFit.Contain -> ContentScale.Fit
        NImageObjectFit.Cover -> ContentScale.Crop
        NImageObjectFit.None -> ContentScale.None
        NImageObjectFit.ScaleDown -> ContentScale.Inside
    }

/** Imperative handle for opening an [NImage] preview. */
@Stable
class NImageState internal constructor() {
    internal var visible by mutableStateOf(false)

    /** Opens the preview modal (or the parent group's preview). */
    fun showPreview() {
        visible = true
    }
}

@Composable
fun rememberNImageState(): NImageState = remember { NImageState() }

/**
 * Actions available to a custom preview toolbar. The default toolbar is
 * replaced when [NImage.renderToolbar] (or [NImageGroup.renderToolbar]) is set.
 */
@Stable
class NImageToolbarScope internal constructor(
    private val onPrev: () -> Unit,
    private val onNext: () -> Unit,
    private val onZoomIn: () -> Unit,
    private val onZoomOut: () -> Unit,
    private val onRotateCounterclockwise: () -> Unit,
    private val onRotateClockwise: () -> Unit,
    private val onResizeToOriginalSize: () -> Unit,
    private val onClose: () -> Unit,
) {
    fun prev() = onPrev()
    fun next() = onNext()
    fun zoomIn() = onZoomIn()
    fun zoomOut() = onZoomOut()
    fun rotateCounterclockwise() = onRotateCounterclockwise()
    fun rotateClockwise() = onRotateClockwise()
    fun resizeToOriginalSize() = onResizeToOriginalSize()
    fun close() = onClose()
}

@Stable
class NImageGroupState internal constructor() {
    internal val sources = mutableStateListOf<String>()
    internal var visible by mutableStateOf(false)
    internal var index by mutableStateOf(0)
}

@Composable
fun rememberNImageGroupState(): NImageGroupState = remember { NImageGroupState() }

internal val LocalNImageGroup = staticCompositionLocalOf<NImageGroupState?> { null }

/**
 * Groups images so their previews share one viewer with previous/next
 * navigation. Images register automatically when composed inside the group.
 */
@Composable
fun NImageGroup(
    modifier: Modifier = Modifier,
    state: NImageGroupState = rememberNImageGroupState(),
    showToolbar: Boolean = true,
    showToolbarTooltip: Boolean = false,
    renderToolbar: (@Composable NImageToolbarScope.() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalNImageGroup provides state) {
        Box(modifier) {
            content()
            if (state.visible) {
                NImagePreviewDialog(
                    sources = state.sources.toList(),
                    initialIndex = state.index,
                    objectFit = NImageObjectFit.Contain,
                    showToolbar = showToolbar,
                    showToolbarTooltip = showToolbarTooltip,
                    renderToolbar = renderToolbar,
                    onDismiss = { state.visible = false },
                )
            }
        }
    }
}

/**
 * Image backed by Coil.
 *
 * Clicking a loaded image opens a fullscreen preview with zoom, rotation and
 * reset controls ([preview], [showToolbar]); [renderToolbar] replaces the
 * default controls, and [showToolbarTooltip] adds hover labels. [lazy] defers
 * the request until the element has been laid out. [fallbackSrc] is attempted
 * when [src] fails before the [fallback] slot is shown. Use [state] (or
 * [NImageGroup]) to open the preview imperatively.
 *
 * Remote images require `NImageLoaderSetup()` at the application root.
 */
@Composable
fun NImage(
    modifier: Modifier = Modifier,
    src: String? = null,
    alt: String? = null,
    width: Dp? = null,
    height: Dp? = null,
    objectFit: NImageObjectFit = NImageObjectFit.Fill,
    borderRadius: Dp? = null,
    lazy: Boolean = false,
    preview: Boolean = true,
    previewDisabled: Boolean = false,
    previewSrc: String? = null,
    previewObjectFit: NImageObjectFit = NImageObjectFit.Contain,
    fallbackSrc: String? = null,
    showToolbar: Boolean = true,
    showToolbarTooltip: Boolean = false,
    renderToolbar: (@Composable NImageToolbarScope.() -> Unit)? = null,
    state: NImageState = rememberNImageState(),
    placeholder: (@Composable BoxScope.() -> Unit)? = null,
    fallback: (@Composable BoxScope.() -> Unit)? = null,
    onLoad: (() -> Unit)? = null,
    onError: (() -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val group = LocalNImageGroup.current
    val interaction = remember { MutableInteractionSource() }
    var localPreview by remember { mutableStateOf(false) }
    var laidOut by remember(src, lazy) { mutableStateOf(!lazy) }
    val shape = RoundedCornerShape(borderRadius ?: theme.borderRadius)
    val sizeModifier = when {
        width != null && height != null -> Modifier.size(width, height)
        width != null -> Modifier.width(width).defaultMinSize(minHeight = 80.dp)
        height != null -> Modifier.height(height).defaultMinSize(minWidth = 80.dp)
        else -> Modifier.defaultMinSize(minWidth = 80.dp, minHeight = 80.dp)
    }
    val contentScale = objectFit.contentScale
    val previewable = preview && !previewDisabled && src != null
    val placeholderContent: @Composable BoxScope.() -> Unit = {
        if (placeholder != null) placeholder() else NImagePlaceholder()
    }
    val errorContent: @Composable BoxScope.() -> Unit = {
        if (fallback != null) fallback() else NImageError(alt)
    }

    DisposableEffect(group, src) {
        val imageSrc = src
        if (group != null && imageSrc != null) group.sources.add(imageSrc)
        onDispose {
            if (group != null && imageSrc != null) group.sources.remove(imageSrc)
        }
    }

    LaunchedEffect(state.visible) {
        if (!state.visible) return@LaunchedEffect
        state.visible = false
        if (group != null && src != null) {
            group.index = group.sources.indexOf(src).coerceAtLeast(0)
            group.visible = true
        } else {
            localPreview = true
        }
    }

    Box(
        modifier
            .then(sizeModifier)
            .clip(shape)
            .background(theme.colors.action)
            .onSizeChanged { size ->
                if (size.width > 0 && size.height > 0) laidOut = true
            }
            .then(
                if (previewable) {
                    Modifier.nClickable(interaction) { state.showPreview() }
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        when {
            src == null -> errorContent()
            !laidOut -> placeholderContent()
            else -> NImageContent(
                src = src,
                alt = alt,
                contentScale = contentScale,
                fallbackSrc = fallbackSrc,
                onLoad = onLoad,
                onError = onError,
                errorContent = errorContent,
                placeholderContent = placeholderContent,
            )
        }
    }

    if (localPreview && group == null && src != null) {
        NImagePreviewDialog(
            sources = listOf(previewSrc ?: src),
            initialIndex = 0,
            objectFit = previewObjectFit,
            showToolbar = showToolbar,
            showToolbarTooltip = showToolbarTooltip,
            renderToolbar = renderToolbar,
            onDismiss = { localPreview = false },
        )
    }
}

@Composable
private fun NImageContent(
    src: String,
    alt: String?,
    contentScale: ContentScale,
    fallbackSrc: String?,
    onLoad: (() -> Unit)?,
    onError: (() -> Unit)?,
    errorContent: @Composable BoxScope.() -> Unit,
    placeholderContent: @Composable BoxScope.() -> Unit,
) {
    val latestOnLoad by rememberUpdatedState(onLoad)
    val latestOnError by rememberUpdatedState(onError)
    var state by remember(src) { mutableStateOf<AsyncImagePainter.State?>(null) }
    var loadedOnce by remember(src) { mutableStateOf(false) }
    Box(Modifier.fillMaxSize()) {
        AsyncImage(
            model = src,
            contentDescription = alt,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale,
            onState = { newState ->
                state = newState
                when (newState) {
                    is AsyncImagePainter.State.Success -> {
                        if (!loadedOnce) {
                            loadedOnce = true
                            latestOnLoad?.invoke()
                        }
                    }
                    is AsyncImagePainter.State.Error -> latestOnError?.invoke()
                    else -> Unit
                }
            },
        )
        when (state) {
            is AsyncImagePainter.State.Success -> Unit
            is AsyncImagePainter.State.Error -> {
                if (fallbackSrc != null && fallbackSrc != src) {
                    NImageContent(
                        src = fallbackSrc,
                        alt = alt,
                        contentScale = contentScale,
                        fallbackSrc = null,
                        onLoad = null,
                        onError = null,
                        errorContent = errorContent,
                        placeholderContent = placeholderContent,
                    )
                } else {
                    errorContent()
                }
            }
            else -> placeholderContent()
        }
    }
}

@Composable
private fun NImagePlaceholder() {
    val theme = LocalNTheme.current
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        NBaseLoading(size = 18.dp, color = theme.colors.primary)
    }
}

@Composable
private fun NImageError(alt: String?) {
    val theme = LocalNTheme.current
    Column(
        Modifier.fillMaxSize().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        NIcon(NIcons.Error, size = 22.dp, color = theme.colors.icon)
        if (!alt.isNullOrEmpty()) {
            NText(
                text = alt,
                modifier = Modifier.padding(top = 4.dp),
                size = theme.typography.fontSize.small,
                color = theme.colors.text3,
                maxLines = 2,
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun NImagePreviewDialog(
    sources: List<String>,
    initialIndex: Int,
    objectFit: NImageObjectFit,
    showToolbar: Boolean,
    showToolbarTooltip: Boolean,
    renderToolbar: (@Composable NImageToolbarScope.() -> Unit)?,
    onDismiss: () -> Unit,
) {
    val safeSources = sources.filter { it.isNotBlank() }
    if (safeSources.isEmpty()) return
    var index by remember(safeSources) {
        mutableStateOf(initialIndex.coerceIn(0, safeSources.lastIndex))
    }
    var scale by remember(safeSources, index) { mutableStateOf(1f) }
    var rotation by remember(safeSources, index) { mutableStateOf(0f) }
    var offset by remember(safeSources, index) { mutableStateOf(Offset.Zero) }
    val currentSrc = safeSources[index]
    val actions = NImageToolbarScope(
        onPrev = { if (index > 0) index-- },
        onNext = { if (index < safeSources.lastIndex) index++ },
        onZoomIn = { scale = (scale + 0.25f).coerceAtMost(8f) },
        onZoomOut = { scale = (scale - 0.25f).coerceAtLeast(0.25f) },
        onRotateCounterclockwise = { rotation -= 90f },
        onRotateClockwise = { rotation += 90f },
        onResizeToOriginalSize = {
            scale = 1f
            rotation = 0f
            offset = Offset.Zero
        },
        onClose = onDismiss,
    )
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.86f)),
        ) {
            AsyncImage(
                model = currentSrc,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = if (showToolbar) 88.dp else 0.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        rotationZ = rotation
                        translationX = offset.x
                        translationY = offset.y
                    }
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(0.25f, 8f)
                            if (scale > 1f) offset += pan
                        }
                    },
                contentScale = objectFit.contentScale,
            )
            if (showToolbar) {
                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 22.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(Color.Black.copy(alpha = 0.55f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    if (renderToolbar != null) {
                        renderToolbar(actions)
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (safeSources.size > 1) {
                                NPreviewToolbarButton(
                                    icon = NIcons.ChevronLeft,
                                    tooltip = if (showToolbarTooltip) "Previous" else null,
                                    enabled = index > 0,
                                    onClick = actions::prev,
                                )
                                NPreviewToolbarButton(
                                    icon = NIcons.ChevronRight,
                                    tooltip = if (showToolbarTooltip) "Next" else null,
                                    enabled = index < safeSources.lastIndex,
                                    onClick = actions::next,
                                )
                            }
                            NPreviewToolbarButton(
                                icon = NIcons.ZoomOut,
                                tooltip = if (showToolbarTooltip) "Zoom out" else null,
                                onClick = actions::zoomOut,
                            )
                            NPreviewToolbarButton(
                                icon = NIcons.ZoomIn,
                                tooltip = if (showToolbarTooltip) "Zoom in" else null,
                                onClick = actions::zoomIn,
                            )
                            NPreviewToolbarButton(
                                icon = NIcons.RotateCounterclockwise,
                                tooltip = if (showToolbarTooltip) "Rotate counterclockwise" else null,
                                onClick = actions::rotateCounterclockwise,
                            )
                            NPreviewToolbarButton(
                                icon = NIcons.RotateClockwise,
                                tooltip = if (showToolbarTooltip) "Rotate clockwise" else null,
                                onClick = actions::rotateClockwise,
                            )
                            NPreviewToolbarButton(
                                icon = NIcons.ResizeSmall,
                                tooltip = if (showToolbarTooltip) "Original size" else null,
                                onClick = actions::resizeToOriginalSize,
                            )
                            NPreviewToolbarButton(
                                icon = NIcons.Close,
                                tooltip = if (showToolbarTooltip) "Close" else null,
                                onClick = onDismiss,
                            )
                        }
                    }
                }
            } else {
                NPreviewToolbarButton(
                    icon = NIcons.Close,
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                    onClick = onDismiss,
                )
            }
            if (safeSources.size > 1) {
                NText(
                    text = "${index + 1} / ${safeSources.size}",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 18.dp),
                    size = 13.sp,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun NPreviewToolbarButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tooltip: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val button: @Composable () -> Unit = {
        Box(
            modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (hovered && enabled) Color.White.copy(alpha = 0.16f) else Color.Transparent)
                .hoverable(interaction)
                .nClickable(interaction, enabled = enabled, onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            NIcon(
                icon,
                size = 18.dp,
                color = if (enabled) Color.White else Color.White.copy(alpha = 0.35f),
            )
        }
    }
    if (tooltip != null) {
        NPopover(
            placement = NPlacement.Top,
            delayMillis = 0,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            content = {
                NText(tooltip, size = theme.typography.fontSize.small, color = theme.colors.text2)
            },
        ) {
            button()
        }
    } else {
        button()
    }
}
