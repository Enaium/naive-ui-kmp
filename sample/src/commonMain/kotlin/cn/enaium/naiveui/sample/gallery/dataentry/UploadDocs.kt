package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NAvatar
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NCard
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NUpload
import cn.enaium.naiveui.components.NUploadFile
import cn.enaium.naiveui.components.NUploadListType
import cn.enaium.naiveui.components.NUploadStatus
import cn.enaium.naiveui.components.rememberNUploadController
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SampleImageUrl =
    "https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg"

@Composable
fun UploadDocs() {
    DocsPage(
        title = "Upload",
        description = "File upload with naive-ui's trigger and file list styling. Compose Multiplatform has no common " +
            "file picker, so host applications supply files through `onOpenFileDialog` and the upload controller.",
    ) {
        BasicUploadDemo()
        DraggableUploadDemo()
        AbstractUploadDemo()
        BeforeUploadDemo()
        ControlledUploadDemo()
        CustomDownloadDemo()
        CustomRequestDemo()
        DefaultFilesUploadDemo()
        DownloadUploadDemo()
        ImageCardUploadDemo()
        ImageThumbnailUploadDemo()
        OnFinishUploadDemo()
        SubmitManuallyUploadDemo()
    }
}

@Composable
private fun BasicUploadDemo() {
    var nextId by remember { mutableStateOf(1) }
    var files by remember { mutableStateOf(emptyList<NUploadFile>()) }
    val controller = rememberNUploadController()

    fun createFile(): NUploadFile {
        val id = nextId
        nextId += 1
        return NUploadFile(id = "basic-$id", name = "document-$id.txt")
    }

    DemoSection(
        title = "Basic",
        description = "Add a file through the trigger or the controller. Without a custom request, files run " +
            "through a local progress cycle and then finish.",
        code = """NUpload(
    fileList = files,
    onFileListChange = { files = it },
    multiple = true,
    onOpenFileDialog = { controller.addFiles(listOf(createFile())) },
)""",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            NUpload(
                fileList = files,
                onFileListChange = { files = it },
                controller = controller,
                multiple = true,
                onOpenFileDialog = { controller.addFiles(listOf(createFile())) },
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NButton(onClick = { controller.addFiles(listOf(createFile())) }) {
                    NText("Add file")
                }
                NButton(onClick = { controller.clear() }) {
                    NText("Clear list")
                }
            }
        }
    }
}

@Composable
private fun DraggableUploadDemo() {
    var nextId by remember { mutableStateOf(1) }
    var files by remember { mutableStateOf(emptyList<NUploadFile>()) }
    val controller = rememberNUploadController()

    DemoSection(
        title = "Drag to upload",
        description = "Set `draggable` to render the large dashed area. `multiple` and `max` are enforced when files " +
            "are added, and directory selection is delegated to the platform picker.",
        code = """NUpload(
    draggable = true,
    multiple = true,
    max = 5,
    onOpenFileDialog = { controller.addFiles(pickedFiles) },
)""",
    ) {
        NUpload(
            fileList = files,
            onFileListChange = { files = it },
            controller = controller,
            draggable = true,
            multiple = true,
            max = 5,
            onOpenFileDialog = {
                val start = nextId
                nextId += 2
                controller.addFiles(
                    List(2) { offset ->
                        NUploadFile(
                            id = "drag-${start + offset}",
                            name = "archive-${start + offset}.zip",
                        )
                    },
                )
            },
        )
    }
}

@Composable
private fun AbstractUploadDemo() {
    var nextId by remember { mutableStateOf(1) }
    var files by remember {
        mutableStateOf(
            listOf(
                NUploadFile(
                    id = "abstract-default",
                    name = "file.doc",
                    status = NUploadStatus.Finished,
                    percentage = 100f,
                    type = "text/plain",
                ),
            ),
        )
    }
    val controller = rememberNUploadController()

    DemoSection(
        title = "Split trigger and list",
        description = "`abstract = true` hides the built-in trigger so the file list can be driven from anywhere " +
            "through the controller.",
        code = """NUpload(abstract = true, fileList = files, controller = controller)
NButton(onClick = { controller.addFiles(files) }) { NText("Upload") }""",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NButton(onClick = {}) {
                    NText("Useless")
                }
                NButton(
                    onClick = {
                        val id = nextId
                        nextId += 1
                        controller.addFiles(
                            listOf(NUploadFile(id = "abstract-$id", name = "file-$id.doc")),
                        )
                    },
                ) {
                    NText("Upload")
                }
            }
            NCard(title = "File List", size = NSize.Small) {
                NUpload(
                    fileList = files,
                    onFileListChange = { files = it },
                    controller = controller,
                    abstract = true,
                )
            }
        }
    }
}

@Composable
private fun BeforeUploadDemo() {
    var nextId by remember { mutableStateOf(1) }
    var files by remember { mutableStateOf(emptyList<NUploadFile>()) }
    var message by remember { mutableStateOf("Only image/png files pass the beforeUpload hook.") }
    val controller = rememberNUploadController()

    fun addBatch() {
        val id = nextId
        nextId += 1
        message = "Checking image-$id.png and notes-$id.txt..."
        controller.addFiles(
            listOf(
                NUploadFile(id = "png-$id", name = "image-$id.png", type = "image/png"),
                NUploadFile(id = "txt-$id", name = "notes-$id.txt", type = "text/plain"),
            ),
        )
    }

    DemoSection(
        title = "Before upload hook",
        description = "`beforeUpload` runs before a file enters the list. Returning `false` cancels the upload, as " +
            "done here for non-PNG files.",
        code = """NUpload(
    beforeUpload = { file, _ ->
        val accepted = file.type == "image/png"
        if (!accepted) message = "Only PNG files are accepted"
        accepted
    },
)""",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NUpload(
                fileList = files,
                onFileListChange = { files = it },
                controller = controller,
                multiple = true,
                beforeUpload = { file, _ ->
                    val accepted = file.type == "image/png"
                    if (!accepted) {
                        message = "Rejected ${file.name}: only image/png files are allowed."
                    }
                    accepted
                },
                onOpenFileDialog = { addBatch() },
            )
            NText(text = message, color = LocalNTheme.current.colors.text3)
        }
    }
}

@Composable
private fun ControlledUploadDemo() {
    var nextId by remember { mutableStateOf(1) }
    var files by remember {
        mutableStateOf(
            listOf(
                NUploadFile(
                    id = "url-test",
                    name = "URL Test",
                    status = NUploadStatus.Finished,
                    percentage = 100f,
                    url = SampleImageUrl,
                ),
                NUploadFile(id = "text-message", name = "Your text messages", status = NUploadStatus.Failed),
                NUploadFile(
                    id = "notification",
                    name = "Your notifications",
                    status = NUploadStatus.Finished,
                    percentage = 100f,
                ),
                NUploadFile(
                    id = "contact",
                    name = "Your contact info",
                    status = NUploadStatus.Finished,
                    percentage = 100f,
                ),
            ),
        )
    }
    var message by remember { mutableStateOf("Try removing the finished files.") }
    val controller = rememberNUploadController()

    DemoSection(
        title = "Controlled file list",
        description = "The list is hoisted; `onRemove` can veto removal. The server refuses to delete the " +
            "\"notifications\" and \"contact info\" entries.",
        code = """NUpload(
    fileList = files,
    onFileListChange = { files = it },
    onRemove = { file ->
        if (file.id == "notification") false else true
    },
)""",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NUpload(
                fileList = files,
                onFileListChange = { files = it },
                controller = controller,
                onRemove = { file ->
                    when (file.id) {
                        "notification" -> {
                            message = "No, this is useful for us. Removal not allowed."
                            false
                        }
                        "contact" -> {
                            message = "The server said you can't delete this one either."
                            false
                        }
                        else -> {
                            message = "Removed ${file.name}."
                            true
                        }
                    }
                },
                onOpenFileDialog = {
                    val id = nextId
                    nextId += 1
                    controller.addFiles(
                        listOf(NUploadFile(id = "controlled-$id", name = "upload-$id.txt")),
                    )
                },
            )
            NText(text = message, color = LocalNTheme.current.colors.text3)
        }
    }
}

@Composable
private fun CustomDownloadDemo() {
    var nextId by remember { mutableStateOf(1) }
    var message by remember { mutableStateOf("Click the download icon on a finished file.") }
    val controller = rememberNUploadController()

    DemoSection(
        title = "Custom download",
        description = "`showDownloadButton` adds a download action. `customDownload` receives the file so the host " +
            "can perform a cross-origin download.",
        code = """NUpload(
    listType = NUploadListType.Image,
    showDownloadButton = true,
    customDownload = { file -> startCustomDownload(file) },
)""",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NUpload(
                defaultFileList = remember {
                    listOf(
                        NUploadFile(
                            id = "custom-download",
                            name = "custom-download.png",
                            status = NUploadStatus.Finished,
                            percentage = 100f,
                            url = SampleImageUrl,
                            type = "image/png",
                        ),
                    )
                },
                controller = controller,
                listType = NUploadListType.Image,
                showDownloadButton = true,
                onDownload = { message = "Download requested: ${it.name}" },
                customDownload = { message = "Custom download started for ${it.name}" },
                onOpenFileDialog = {
                    val id = nextId
                    nextId += 1
                    controller.addFiles(
                        listOf(
                            NUploadFile(
                                id = "custom-$id",
                                name = "image-$id.png",
                                url = SampleImageUrl,
                                type = "image/png",
                            ),
                        ),
                    )
                },
            )
            NText(text = message, color = LocalNTheme.current.colors.text3)
        }
    }
}

@Composable
private fun CustomRequestDemo() {
    val scope = rememberCoroutineScope()
    var nextId by remember { mutableStateOf(1) }
    var files by remember { mutableStateOf(emptyList<NUploadFile>()) }
    val controller = rememberNUploadController()

    DemoSection(
        title = "Custom request",
        description = "`customRequest` receives the file together with progress, finish and error callbacks. " +
            "Replace the coroutine body with a real network upload.",
        code = """customRequest = { request ->
    scope.launch {
        for (step in 1..10) {
            delay(120)
            request.onProgress(step * 10f)
        }
        request.onFinish(request.file.copy(url = url))
    }
}""",
    ) {
        NUpload(
            fileList = files,
            onFileListChange = { files = it },
            controller = controller,
            onOpenFileDialog = {
                val id = nextId
                nextId += 1
                controller.addFiles(
                    listOf(
                        NUploadFile(
                            id = "custom-request-$id",
                            name = "photo-$id.jpg",
                            type = "image/jpeg",
                        ),
                    ),
                )
            },
            customRequest = { request ->
                scope.launch {
                    for (step in 1..10) {
                        delay(120)
                        request.onProgress(step * 10f)
                    }
                    request.onFinish(request.file.copy(url = SampleImageUrl))
                }
            },
        )
    }
}

@Composable
private fun DefaultFilesUploadDemo() {
    var nextId by remember { mutableStateOf(1) }
    val controller = rememberNUploadController()
    val defaultFiles = remember {
        listOf(
            NUploadFile("the", "The", NUploadStatus.Finished, 100f),
            NUploadFile("razars", "Razars's", NUploadStatus.Finished, 100f),
            NUploadFile("edge", "Blade", NUploadStatus.Finished, 100f),
        )
    }

    DemoSection(
        title = "Default file list",
        description = "`defaultFileList` seeds an uncontrolled list. New files are appended through the controller.",
        code = "NUpload(defaultFileList = defaultFiles, onOpenFileDialog = { ... })",
    ) {
        NUpload(
            defaultFileList = defaultFiles,
            controller = controller,
            onOpenFileDialog = {
                val id = nextId
                nextId += 1
                controller.addFiles(
                    listOf(NUploadFile(id = "default-$id", name = "default-$id.txt")),
                )
            },
        )
    }
}

@Composable
private fun DownloadUploadDemo() {
    var message by remember { mutableStateOf("Only finished files show the download action.") }
    val fileList = remember {
        listOf(
            NUploadFile(id = "a", name = "My Fault.png", status = NUploadStatus.Failed),
            NUploadFile(
                id = "c",
                name = "Finished you can download.png",
                status = NUploadStatus.Finished,
                percentage = 100f,
                url = SampleImageUrl,
                type = "image/png",
            ),
            NUploadFile(
                id = "d",
                name = "Waiting for the finish.doc",
                status = NUploadStatus.Uploading,
                percentage = 50f,
            ),
        )
    }

    DemoSection(
        title = "Download when finished",
        description = "`onDownload` is invoked when the download button is pressed. The button is only rendered for " +
            "finished files.",
        code = "NUpload(defaultFileList = fileList, showDownloadButton = true, onDownload = { ... })",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NUpload(
                defaultFileList = fileList,
                listType = NUploadListType.Image,
                showTrigger = false,
                showDownloadButton = true,
                onDownload = { message = "Download requested: ${it.name}" },
            )
            NText(text = message, color = LocalNTheme.current.colors.text3)
        }
    }
}

@Composable
private fun ImageCardUploadDemo() {
    var nextId by remember { mutableStateOf(1) }
    var preview by remember { mutableStateOf<NUploadFile?>(null) }
    val controller = rememberNUploadController()
    val fileList = remember {
        listOf(
            NUploadFile(id = "a", name = "broken.png", status = NUploadStatus.Failed),
            NUploadFile(
                id = "b",
                name = "regular.doc",
                status = NUploadStatus.Finished,
                percentage = 100f,
                type = "text/plain",
            ),
            NUploadFile(
                id = "c",
                name = "image-with-url.png",
                status = NUploadStatus.Finished,
                percentage = 100f,
                url = SampleImageUrl,
                type = "image/png",
            ),
            NUploadFile(
                id = "d",
                name = "uploading.doc",
                status = NUploadStatus.Uploading,
                percentage = 99f,
            ),
        )
    }
    val previewFiles = remember {
        listOf(
            NUploadFile(
                id = "react",
                name = "react.png",
                status = NUploadStatus.Finished,
                percentage = 100f,
                url = SampleImageUrl,
                type = "image/png",
            ),
            NUploadFile(
                id = "vue",
                name = "vue.png",
                status = NUploadStatus.Finished,
                percentage = 100f,
                url = SampleImageUrl,
                type = "image/png",
            ),
        )
    }
    val theme = LocalNTheme.current

    DemoSection(
        title = "Pictures wall",
        description = "`listType = ImageCard` renders a 96px grid with hover actions. `onPreview` receives the " +
            "clicked file; this demo shows the image beside the wall.",
        code = """NUpload(listType = NUploadListType.ImageCard, onPreview = { preview = it })""",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            NUpload(
                defaultFileList = fileList,
                listType = NUploadListType.ImageCard,
                controller = controller,
                onOpenFileDialog = {
                    val id = nextId
                    nextId += 1
                    controller.addFiles(
                        listOf(
                            NUploadFile(
                                id = "card-$id",
                                name = "card-$id.png",
                                url = SampleImageUrl,
                                type = "image/png",
                            ),
                        ),
                    )
                },
            )
            NUpload(
                defaultFileList = previewFiles,
                listType = NUploadListType.ImageCard,
                showTrigger = false,
                onPreview = { preview = it },
            )
            val current = preview
            if (current != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NAvatar(
                        size = 72.dp,
                        src = current.url,
                        text = current.name.take(1),
                    )
                    Spacer(Modifier.width(10.dp))
                    NText(
                        text = "Previewing ${current.name}",
                        color = theme.colors.text3,
                    )
                }
            } else {
                NText(
                    text = "Click the eye icon on a file with a URL to preview it.",
                    color = theme.colors.text3,
                )
            }
        }
    }
}

@Composable
private fun ImageThumbnailUploadDemo() {
    var nextId by remember { mutableStateOf(1) }
    val controller = rememberNUploadController()
    val fileList = remember {
        listOf(
            NUploadFile(id = "a", name = "My Fault.png", status = NUploadStatus.Failed),
            NUploadFile(
                id = "b",
                name = "regular text.doc",
                status = NUploadStatus.Finished,
                percentage = 100f,
                type = "text/plain",
            ),
            NUploadFile(
                id = "c",
                name = "image.png",
                status = NUploadStatus.Finished,
                percentage = 100f,
                url = SampleImageUrl,
                type = "image/png",
            ),
            NUploadFile(
                id = "d",
                name = "Not Finished Yet.doc",
                status = NUploadStatus.Uploading,
                percentage = 50f,
            ),
        )
    }

    DemoSection(
        title = "Thumbnail file list",
        description = "`createThumbnailUrl` can supply a custom thumbnail for any file, regardless of its type.",
        code = "NUpload(listType = NUploadListType.Image, createThumbnailUrl = { SampleImageUrl })",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NUpload(
                defaultFileList = fileList,
                listType = NUploadListType.Image,
                controller = controller,
                createThumbnailUrl = { SampleImageUrl },
                onOpenFileDialog = {
                    val id = nextId
                    nextId += 1
                    controller.addFiles(
                        listOf(NUploadFile(id = "thumb-$id", name = "text-$id.doc")),
                    )
                },
            )
            NText(
                text = "Every row uses the same custom thumbnail URL.",
                color = LocalNTheme.current.colors.text3,
            )
        }
    }
}

@Composable
private fun OnFinishUploadDemo() {
    var nextId by remember { mutableStateOf(1) }
    val controller = rememberNUploadController()

    DemoSection(
        title = "Change file on finish",
        description = "`onFinish` can rewrite the finished file, for example renaming it and attaching the URL " +
            "returned by the server.",
        code = """NUpload(
    onFinish = { file ->
        val ext = file.name.substringAfterLast('.')
        file.copy(name = "renamed." + ext, url = serverUrl)
    },
)""",
    ) {
        NUpload(
            controller = controller,
            onOpenFileDialog = {
                val id = nextId
                nextId += 1
                controller.addFiles(
                    listOf(NUploadFile(id = "finish-$id", name = "picture-$id.png", type = "image/png")),
                )
            },
            onFinish = { file ->
                val extension = file.name.substringAfterLast('.', "bin")
                file.copy(
                    name = "renamed.$extension",
                    url = SampleImageUrl,
                )
            },
        )
    }
}

@Composable
private fun SubmitManuallyUploadDemo() {
    var nextId by remember { mutableStateOf(1) }
    var files by remember { mutableStateOf(emptyList<NUploadFile>()) }
    val controller = rememberNUploadController()
    val pending = files.count { it.status == NUploadStatus.Pending }

    DemoSection(
        title = "Uncontrolled manually submit",
        description = "With `defaultUpload = false` selected files stay pending until `controller.submit()` is " +
            "called. The file list is still uncontrolled inside the component.",
        code = """NUpload(
    defaultUpload = false,
    controller = controller,
    onOpenFileDialog = { controller.addFiles(files) },
)
NButton(onClick = { controller.submit() }) { NText("Upload File") }""",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            NButton(
                onClick = { controller.submit() },
                disabled = pending == 0,
            ) {
                NText("Upload File")
            }
            NUpload(
                fileList = files,
                onFileListChange = { files = it },
                controller = controller,
                defaultUpload = false,
                multiple = true,
                onOpenFileDialog = {
                    val start = nextId
                    nextId += 2
                    controller.addFiles(
                        List(2) { offset ->
                            NUploadFile(
                                id = "manual-${start + offset}",
                                name = "manual-${start + offset}.txt",
                            )
                        },
                    )
                },
            )
            NText(
                text = "$pending pending file(s). Select files, then press Upload File.",
                color = LocalNTheme.current.colors.text3,
            )
        }
    }
}
