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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NBaseLoading
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.theme.LocalNTheme
import coil3.compose.AsyncImagePainter
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImageContent

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

/**
 * Image backed by Coil.
 *
 * Clicking a loaded image opens a fullscreen preview with zoom, rotation and
 * reset controls ([preview], [showToolbar]). [lazy] defers the request until
 * the element has been laid out, which makes lazy lists load as they scroll.
 * [fallbackSrc] is attempted when [src] fails before the [fallback] slot is
 * shown.
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
    fallbackSrc: String? = null,
    showToolbar: Boolean = true,
    placeholder: (@Composable BoxScope.() -> Unit)? = null,
    fallback: (@Composable BoxScope.() -> Unit)? = null,
    onLoad: (() -> Unit)? = null,
    onError: (() -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    var showPreview by remember { mutableStateOf(false) }
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
                    Modifier.nClickable(interaction) { showPreview = true }
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

    if (showPreview && previewable) {
        NImagePreviewDialog(
            src = previewSrc ?: src,
            alt = alt,
            showToolbar = showToolbar,
            onDismiss = { showPreview = false },
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
    src: String?,
    alt: String?,
    showToolbar: Boolean,
    onDismiss: () -> Unit,
) {
    var scale by remember(src) { mutableStateOf(1f) }
    var rotation by remember(src) { mutableStateOf(0f) }
    var offset by remember(src) { mutableStateOf(Offset.Zero) }
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
            if (src != null) {
                AsyncImage(
                    model = src,
                    contentDescription = alt,
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
                    contentScale = ContentScale.Fit,
                )
            }
            if (showToolbar) {
                Row(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 22.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(Color.Black.copy(alpha = 0.55f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NPreviewToolbarButton(NIcons.ZoomOut) {
                        scale = (scale - 0.25f).coerceAtLeast(0.25f)
                    }
                    NPreviewToolbarButton(NIcons.ZoomIn) {
                        scale = (scale + 0.25f).coerceAtMost(8f)
                    }
                    NPreviewToolbarButton(NIcons.RotateCounterclockwise) {
                        rotation -= 90f
                    }
                    NPreviewToolbarButton(NIcons.RotateClockwise) {
                        rotation += 90f
                    }
                    NPreviewToolbarButton(NIcons.ResizeSmall) {
                        scale = 1f
                        rotation = 0f
                        offset = Offset.Zero
                    }
                    NPreviewToolbarButton(NIcons.Close, onClick = onDismiss)
                }
            } else {
                NPreviewToolbarButton(
                    icon = NIcons.Close,
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                    onClick = onDismiss,
                )
            }
        }
    }
}

@Composable
private fun NPreviewToolbarButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Box(
        modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(if (hovered) Color.White.copy(alpha = 0.16f) else Color.Transparent)
            .hoverable(interaction)
            .nClickable(interaction, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        NIcon(icon, size = 18.dp, color = Color.White)
    }
}
