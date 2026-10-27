package cn.enaium.naiveui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NCloseButton
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** Status of a single file in an upload list. */
enum class NUploadStatus { Pending, Uploading, Finished, Failed }

/** A file shown by [NUpload]. */
@Immutable
data class NUploadFile(
    val id: String,
    val name: String,
    val status: NUploadStatus = NUploadStatus.Pending,
    val percentage: Float = 0f,
    val url: String? = null,
    val type: String? = null,
)

/** Built-in styles for the upload file list. */
enum class NUploadListType { Text, Image, ImageCard }

/**
 * Request handed to [NUpload]'s `customRequest` callback.
 *
 * Implementations call [onProgress] while transferring, then exactly one of
 * [onFinish] or [onError].
 */
class NUploadRequest internal constructor(
    val file: NUploadFile,
    val onProgress: (Float) -> Unit,
    val onFinish: (NUploadFile) -> Unit,
    val onError: () -> Unit,
)

/**
 * Imperative handle mirroring naive-ui's upload methods (`submit`, `clear`).
 * Obtain one with [rememberNUploadController] and pass it to [NUpload].
 */
class NUploadController internal constructor() {
    internal var addFilesAction: ((List<NUploadFile>) -> Unit)? = null
    internal var clearAction: (() -> Unit)? = null
    internal var submitAction: (() -> Unit)? = null

    /** Adds files, honouring `max`, `accept` and `beforeUpload`. */
    fun addFiles(files: List<NUploadFile>) {
        addFilesAction?.invoke(files)
    }

    /** Removes every file from the list. */
    fun clear() {
        clearAction?.invoke()
    }

    /** Starts uploading files that are still pending. */
    fun submit() {
        submitAction?.invoke()
    }
}

@Composable
fun rememberNUploadController(): NUploadController = remember { NUploadController() }

/**
 * Actions exposed to a custom upload trigger. Compose Multiplatform has no
 * common file picker, so the host opens its platform picker and feeds the
 * result back through [addFiles].
 */
class NUploadTriggerScope internal constructor(
    private val open: () -> Unit,
    private val add: (List<NUploadFile>) -> Unit,
    val multiple: Boolean,
    val max: Int?,
    val accept: String?,
    val directory: Boolean,
) {
    /** Notifies the host that the user wants to open a file picker. */
    fun openFileDialog() {
        open()
    }

    /** Adds files to the upload list. */
    fun addFiles(files: List<NUploadFile>) {
        add(files)
    }
}

/**
 * File upload with naive-ui's trigger, dragger and file list styling.
 *
 * Uploading is delegated to [customRequest]. When it is absent files go
 * through a local progress cycle so the upload UI stays usable without a
 * network layer.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NUpload(
    modifier: Modifier = Modifier,
    fileList: List<NUploadFile>? = null,
    defaultFileList: List<NUploadFile> = emptyList(),
    onFileListChange: (List<NUploadFile>) -> Unit = {},
    onSelectFiles: (List<NUploadFile>) -> Unit = {},
    onRemove: (NUploadFile) -> Boolean = { true },
    onPreview: (NUploadFile) -> Unit = {},
    showDownloadButton: Boolean = false,
    onDownload: ((NUploadFile) -> Unit)? = null,
    customDownload: ((NUploadFile) -> Unit)? = null,
    onFinish: ((NUploadFile) -> NUploadFile)? = null,
    createThumbnailUrl: ((NUploadFile) -> String?)? = null,
    multiple: Boolean = false,
    max: Int? = null,
    accept: String? = null,
    disabled: Boolean = false,
    directory: Boolean = false,
    showFileList: Boolean = true,
    abstract: Boolean = false,
    listType: NUploadListType = NUploadListType.Text,
    draggable: Boolean = false,
    defaultUpload: Boolean = true,
    showTrigger: Boolean = true,
    customRequest: ((NUploadRequest) -> Unit)? = null,
    beforeUpload: (NUploadFile, List<NUploadFile>) -> Boolean = { _, _ -> true },
    onOpenFileDialog: (() -> Unit)? = null,
    controller: NUploadController = rememberNUploadController(),
    trigger: (@Composable NUploadTriggerScope.() -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    val scope = rememberCoroutineScope()

    var internalList by remember { mutableStateOf(fileList ?: defaultFileList) }
    LaunchedEffect(fileList) {
        if (fileList != null && fileList != internalList) {
            internalList = fileList
        }
    }

    val displayedList = fileList ?: internalList
    val maxReached = max != null && displayedList.size >= max
    val availableToAdd = !disabled && !maxReached

    fun mutate(newList: List<NUploadFile>) {
        internalList = newList
        onFileListChange(newList)
    }

    fun updateFile(id: String, transform: (NUploadFile) -> NUploadFile) {
        val list = internalList
        val index = list.indexOfFirst { it.id == id }
        if (index < 0) return
        val updated = transform(list[index])
        if (updated == list[index]) return
        mutate(list.toMutableList().also { it[index] = updated })
    }

    fun startUpload(id: String) {
        val source = internalList.firstOrNull { it.id == id } ?: return
        val uploading = source.copy(status = NUploadStatus.Uploading, percentage = 0f)
        updateFile(id) { uploading }
        val finishTransform = onFinish
        val request = NUploadRequest(
            file = uploading,
            onProgress = { percentage ->
                updateFile(id) { file ->
                    if (file.status == NUploadStatus.Uploading) {
                        file.copy(percentage = percentage.coerceIn(0f, 100f))
                    } else {
                        file
                    }
                }
            },
            onFinish = { finished ->
                updateFile(id) { file ->
                    if (file.status == NUploadStatus.Uploading) {
                        val uploaded = file.copy(
                            name = finished.name,
                            percentage = 100f,
                            url = finished.url ?: file.url,
                            type = finished.type ?: file.type,
                        )
                        val transformed = finishTransform?.invoke(uploaded) ?: uploaded
                        transformed.copy(status = NUploadStatus.Finished, percentage = 100f)
                    } else {
                        file
                    }
                }
            },
            onError = {
                updateFile(id) { file ->
                    if (file.status == NUploadStatus.Uploading) {
                        file.copy(status = NUploadStatus.Failed)
                    } else {
                        file
                    }
                }
            },
        )
        val requestHandler = customRequest
        if (requestHandler != null) {
            runCatching { requestHandler(request) }.onFailure { request.onError() }
        } else {
            scope.launch {
                for (step in floatArrayOf(8f, 20f, 36f, 52f, 68f, 84f, 100f)) {
                    delay(110)
                    request.onProgress(step)
                }
                request.onFinish(request.file.copy(percentage = 100f))
            }
        }
    }

    fun addFilesInternal(candidates: List<NUploadFile>) {
        if (!availableToAdd || candidates.isEmpty()) return
        val current = internalList
        val room = max?.let { (it - current.size).coerceAtLeast(0) } ?: Int.MAX_VALUE
        if (room == 0) return
        val used = current.mapTo(mutableSetOf()) { it.id }
        val accepted = ArrayList<NUploadFile>()
        for (candidate in candidates) {
            if (accepted.size >= room) break
            if (!candidate.matchesAccept(accept)) continue
            var id = candidate.id.ifBlank { "upload-${used.size + 1}" }
            if (id in used) {
                val base = id
                var suffix = 1
                while ("$base-$suffix" in used) suffix += 1
                id = "$base-$suffix"
            }
            used += id
            val pending = candidate.copy(
                id = id,
                status = NUploadStatus.Pending,
                percentage = 0f,
            )
            if (!beforeUpload(pending, current + accepted)) continue
            accepted += pending
            if (!multiple) break
        }
        if (accepted.isEmpty()) return
        val added = if (defaultUpload) {
            accepted.map { it.copy(status = NUploadStatus.Uploading) }
        } else {
            accepted
        }
        mutate(current + added)
        onSelectFiles(added)
        if (defaultUpload) added.forEach { startUpload(it.id) }
    }

    fun submitPending() {
        internalList
            .filter { it.status == NUploadStatus.Pending }
            .forEach { startUpload(it.id) }
    }

    fun clearAll() {
        if (internalList.isNotEmpty()) mutate(emptyList())
    }

    fun removeFile(file: NUploadFile) {
        if (disabled) return
        if (!onRemove(file)) return
        mutate(internalList.filterNot { it.id == file.id })
    }

    fun downloadFile(file: NUploadFile) {
        onDownload?.invoke(file)
        customDownload?.invoke(file)
    }

    SideEffect {
        controller.addFilesAction = { files -> addFilesInternal(files) }
        controller.clearAction = { clearAll() }
        controller.submitAction = { submitPending() }
    }

    val triggerScope = NUploadTriggerScope(
        open = { if (availableToAdd) onOpenFileDialog?.invoke() },
        add = { files -> addFilesInternal(files) },
        multiple = multiple,
        max = max,
        accept = accept,
        directory = directory,
    )

    Column(modifier) {
        if (listType == NUploadListType.ImageCard) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (showTrigger && !abstract) {
                    val customTrigger = trigger
                    if (customTrigger != null) {
                        Box(
                            Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(theme.borderRadius))
                                .nBorder(theme.colors.border, 1.dp, theme.borderRadius, dashed = true)
                                .alpha(if (availableToAdd) 1f else theme.opacityDisabled),
                            contentAlignment = Alignment.Center,
                        ) {
                            customTrigger.invoke(triggerScope)
                        }
                    } else {
                        NUploadDefaultTrigger(
                            listType = listType,
                            draggable = false,
                            enabled = availableToAdd,
                            onClick = { onOpenFileDialog?.invoke() },
                        )
                    }
                }
                if (showFileList) {
                    displayedList.forEach { file ->
                        key(file.id) {
                            NUploadImageCard(
                                file = file,
                                thumbnailUrl = createThumbnailUrl?.invoke(file) ?: file.url,
                                disabled = disabled,
                                showDownloadButton = showDownloadButton,
                                onRemove = { removeFile(file) },
                                onRetry = { startUpload(file.id) },
                                onPreview = { onPreview(file) },
                                onDownload = { downloadFile(file) },
                            )
                        }
                    }
                }
            }
        } else {
            if (showTrigger && !abstract) {
                val customTrigger = trigger
                if (customTrigger != null) {
                    Box(Modifier.alpha(if (availableToAdd) 1f else theme.opacityDisabled)) {
                        customTrigger.invoke(triggerScope)
                    }
                } else {
                    NUploadDefaultTrigger(
                        listType = listType,
                        draggable = draggable,
                        enabled = availableToAdd,
                        onClick = { onOpenFileDialog?.invoke() },
                    )
                }
            }
            if (showFileList && displayedList.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    displayedList.forEach { file ->
                        key(file.id) {
                            NUploadTextRow(
                                file = file,
                                thumbnailUrl = createThumbnailUrl?.invoke(file) ?: file.url,
                                listType = listType,
                                disabled = disabled,
                                showDownloadButton = showDownloadButton,
                                onRemove = { removeFile(file) },
                                onRetry = { startUpload(file.id) },
                                onPreview = { onPreview(file) },
                                onDownload = { downloadFile(file) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NUploadDefaultTrigger(
    listType: NUploadListType,
    draggable: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val borderColor = if (enabled && hovered) theme.colors.primaryHover else theme.colors.border
    val shape = RoundedCornerShape(theme.borderRadius)

    if (listType == NUploadListType.ImageCard) {
        Box(
            Modifier
                .size(96.dp)
                .alpha(if (enabled) 1f else theme.opacityDisabled)
                .clip(shape)
                .nBorder(borderColor, 1.dp, theme.borderRadius, dashed = true)
                .hoverable(interaction, enabled = enabled)
                .nClickable(interaction, enabled = enabled, onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            NIcon(NIcons.Add, size = 24.dp, color = if (enabled) theme.colors.icon else theme.colors.iconDisabled)
        }
        return
    }

    val base = Modifier
        .alpha(if (enabled) 1f else theme.opacityDisabled)
        .then(if (draggable) Modifier.fillMaxWidth() else Modifier)
        .clip(shape)
        .nBorder(borderColor, 1.dp, theme.borderRadius, dashed = true)
        .hoverable(interaction, enabled = enabled)
        .nClickable(interaction, enabled = enabled, onClick = onClick)
        .padding(
            horizontal = if (draggable) 24.dp else 14.dp,
            vertical = if (draggable) 24.dp else 10.dp,
        )

    if (draggable) {
        Column(
            modifier = base,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            NIcon(NIcons.Attach, size = 40.dp, color = theme.colors.icon)
            Spacer(Modifier.height(12.dp))
            NText(
                text = locale.uploadDrag,
                size = 16.sp,
                color = theme.colors.text1,
            )
            Spacer(Modifier.height(6.dp))
            NText(
                text = locale.uploadBrowse,
                size = theme.typography.fontSize.medium,
                color = if (enabled) theme.colors.primary else theme.colors.text3,
                underline = true,
                strong = true,
            )
        }
    } else {
        Row(
            modifier = base,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NIcon(NIcons.Attach, size = 20.dp, color = theme.colors.icon)
            Spacer(Modifier.width(8.dp))
            NText(
                text = locale.uploadBrowse,
                size = theme.typography.fontSize.medium,
                color = if (enabled) theme.colors.primary else theme.colors.text3,
                underline = true,
                strong = true,
            )
        }
    }
}

@Composable
private fun NUploadTextRow(
    file: NUploadFile,
    thumbnailUrl: String?,
    listType: NUploadListType,
    disabled: Boolean,
    showDownloadButton: Boolean,
    onRemove: () -> Unit,
    onRetry: () -> Unit,
    onPreview: () -> Unit,
    onDownload: () -> Unit,
) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val shape = RoundedCornerShape(theme.borderRadius)
    val isError = file.status == NUploadStatus.Failed
    val hasUrl = file.url != null && file.status == NUploadStatus.Finished
    val nameColor = when {
        isError -> theme.colors.error
        hasUrl -> theme.colors.success
        else -> theme.colors.text2
    }
    val icon = when {
        file.isImage() -> NIcons.Photo
        else -> NIcons.File
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 34.dp)
            .clip(shape)
            .background(if (hovered && !disabled) theme.colors.hover else Color.Transparent)
            .hoverable(interaction)
            .padding(horizontal = 6.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (listType == NUploadListType.Image) {
            Box(
                Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(theme.borderRadiusSmall))
                    .background(theme.colors.action),
                contentAlignment = Alignment.Center,
            ) {
                if (thumbnailUrl != null) {
                    AsyncImage(
                        model = thumbnailUrl,
                        contentDescription = file.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    NIcon(icon, size = 18.dp, color = theme.colors.icon)
                }
            }
            Spacer(Modifier.width(8.dp))
        } else {
            NIcon(icon, size = 18.dp, color = if (isError) theme.colors.error else theme.colors.icon)
            Spacer(Modifier.width(8.dp))
        }

        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            NText(
                text = file.name,
                size = theme.typography.fontSize.medium,
                color = nameColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                underline = hasUrl,
                modifier = Modifier.fillMaxWidth(),
            )
            if (file.status == NUploadStatus.Uploading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NUploadProgressBar(
                        percentage = file.percentage,
                        status = file.status,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(8.dp))
                    NText(
                        text = "${file.percentage.roundToInt()}%",
                        size = 12.sp,
                        color = theme.colors.text3,
                    )
                }
            }
        }

        if (hasUrl) {
            NUploadIconButton(NIcons.Eye, theme.colors.icon, "Preview", onPreview)
        }
        if (showDownloadButton && file.status == NUploadStatus.Finished) {
            NUploadIconButton(NIcons.Download, theme.colors.icon, "Download", onDownload)
        }
        if (file.status == NUploadStatus.Failed) {
            NUploadIconButton(NIcons.Retry, theme.colors.error, "Retry", onRetry)
        }
        if (!disabled) {
            NCloseButton(onClick = onRemove, size = 22.dp, iconSize = 12.dp)
        }
    }
}

@Composable
private fun NUploadImageCard(
    file: NUploadFile,
    thumbnailUrl: String?,
    disabled: Boolean,
    showDownloadButton: Boolean,
    onRemove: () -> Unit,
    onRetry: () -> Unit,
    onPreview: () -> Unit,
    onDownload: () -> Unit,
) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val shape = RoundedCornerShape(theme.borderRadius)
    val isError = file.status == NUploadStatus.Failed
    val borderColor = when {
        isError -> theme.colors.error
        hovered -> theme.colors.primaryHover
        else -> theme.colors.border
    }
    Box(
        Modifier
            .size(96.dp)
            .clip(shape)
            .nBorder(borderColor, 1.dp, theme.borderRadius)
            .hoverable(interaction),
    ) {
        if (thumbnailUrl != null) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = file.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Box(
                Modifier.fillMaxSize().background(theme.colors.action),
                contentAlignment = Alignment.Center,
            ) {
                NIcon(
                    if (file.isImage()) NIcons.Photo else NIcons.File,
                    size = 32.dp,
                    color = if (isError) theme.colors.error else theme.colors.icon,
                )
            }
        }

        if (file.status == NUploadStatus.Uploading) {
            Column(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.25f))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                NUploadProgressBar(
                    percentage = file.percentage,
                    status = file.status,
                    modifier = Modifier.fillMaxWidth(),
                )
                NText(
                    text = "${file.percentage.roundToInt()}%",
                    size = 11.sp,
                    color = Color.White,
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .clip(RoundedCornerShape(theme.borderRadiusSmall))
                .background(theme.colors.popover.copy(alpha = 0.86f))
                .padding(horizontal = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (file.url != null) {
                NUploadIconButton(NIcons.Eye, theme.colors.icon, "Preview", onPreview)
            }
            if (showDownloadButton && file.status == NUploadStatus.Finished) {
                NUploadIconButton(NIcons.Download, theme.colors.icon, "Download", onDownload)
            }
            if (isError) {
                NUploadIconButton(NIcons.Retry, theme.colors.error, "Retry", onRetry)
            }
            if (!disabled) {
                NUploadIconButton(NIcons.Trash, theme.colors.icon, "Remove", onRemove)
            }
        }
    }
}

@Composable
private fun NUploadIconButton(
    icon: ImageVector,
    tint: Color,
    description: String,
    onClick: () -> Unit,
) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Box(
        Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(if (hovered) theme.colors.closeHover else Color.Transparent)
            .hoverable(interaction)
            .nClickable(interaction, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        NIcon(
            icon = icon,
            size = 14.dp,
            color = if (hovered) theme.colors.text1 else tint,
            contentDescription = description,
        )
    }
}

@Composable
private fun NUploadProgressBar(
    percentage: Float,
    status: NUploadStatus,
    modifier: Modifier = Modifier,
) {
    val theme = LocalNTheme.current
    val color = when (status) {
        NUploadStatus.Failed -> theme.colors.error
        NUploadStatus.Finished -> theme.colors.success
        else -> theme.colors.info
    }
    Canvas(modifier.height(2.dp)) {
        val radius = size.height / 2f
        drawRoundRect(
            color = theme.colors.progressRail,
            cornerRadius = CornerRadius(radius, radius),
        )
        val fraction = (percentage / 100f).coerceIn(0f, 1f)
        if (fraction > 0f) {
            drawRoundRect(
                color = color,
                size = Size(size.width * fraction, size.height),
                cornerRadius = CornerRadius(radius, radius),
            )
        }
    }
}

private fun NUploadFile.isImage(): Boolean {
    if (type?.startsWith("image/", ignoreCase = true) == true) return true
    return name.substringAfterLast('.', "").lowercase() in
        setOf("png", "jpg", "jpeg", "gif", "webp", "bmp", "svg", "avif")
}

private fun NUploadFile.matchesAccept(accept: String?): Boolean {
    if (accept.isNullOrBlank()) return true
    val type = type?.lowercase()
    val name = name.lowercase()
    return accept.split(',').any { raw ->
        val token = raw.trim().lowercase()
        when {
            token.isEmpty() -> true
            token.endsWith("/*") -> type?.startsWith(token.removeSuffix("*")) == true
            token.startsWith(".") -> name.endsWith(token)
            else -> type == token
        }
    }
}
