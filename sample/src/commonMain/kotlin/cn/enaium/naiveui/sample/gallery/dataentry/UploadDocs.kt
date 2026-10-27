package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NUpload
import cn.enaium.naiveui.components.NUploadFile
import cn.enaium.naiveui.components.NUploadListType
import cn.enaium.naiveui.components.NUploadStatus
import cn.enaium.naiveui.components.rememberNUploadController
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
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
        FileListTypesDemo()
        UploadLimitDemo()
        CustomRequestDemo()
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
private fun FileListTypesDemo() {
    val textFiles = remember {
        listOf(
            NUploadFile(id = "text-1", name = "pending-report.pdf"),
            NUploadFile(
                id = "text-2",
                name = "design-spec.doc",
                status = NUploadStatus.Uploading,
                percentage = 62f,
            ),
            NUploadFile(
                id = "text-3",
                name = "notes.txt",
                status = NUploadStatus.Finished,
                percentage = 100f,
                type = "text/plain",
            ),
            NUploadFile(id = "text-4", name = "broken.zip", status = NUploadStatus.Failed),
        )
    }
    val imageFiles = remember {
        listOf(
            NUploadFile(
                id = "image-1",
                name = "preview.png",
                status = NUploadStatus.Finished,
                percentage = 100f,
                url = SampleImageUrl,
                type = "image/png",
            ),
            NUploadFile(
                id = "image-2",
                name = "photo.jpg",
                status = NUploadStatus.Failed,
                url = SampleImageUrl,
                type = "image/jpeg",
            ),
        )
    }
    val cardFiles = remember {
        listOf(
            NUploadFile(
                id = "card-1",
                name = "wallpaper.png",
                status = NUploadStatus.Finished,
                percentage = 100f,
                url = SampleImageUrl,
                type = "image/png",
            ),
        )
    }
    val controller = rememberNUploadController()
    var extraId by remember { mutableStateOf(1) }

    DemoSection(
        title = "File list types",
        description = "Text rows, image thumbnails and the pictures-wall grid. Images render through Coil when a URL " +
            "is available.",
        code = """NUpload(listType = NUploadListType.Text)
NUpload(listType = NUploadListType.Image)
NUpload(listType = NUploadListType.ImageCard)""",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            NUpload(defaultFileList = textFiles, showTrigger = false)
            NUpload(
                defaultFileList = imageFiles,
                listType = NUploadListType.Image,
                showTrigger = false,
            )
            NUpload(
                defaultFileList = cardFiles,
                listType = NUploadListType.ImageCard,
                controller = controller,
                onOpenFileDialog = {
                    val id = extraId
                    extraId += 1
                    controller.addFiles(
                        listOf(
                            NUploadFile(
                                id = "card-extra-$id",
                                name = "added-$id.png",
                                url = SampleImageUrl,
                                type = "image/png",
                            ),
                        ),
                    )
                },
            )
        }
    }
}

@Composable
private fun UploadLimitDemo() {
    var nextId by remember { mutableStateOf(1) }
    var files by remember { mutableStateOf(emptyList<NUploadFile>()) }
    val controller = rememberNUploadController()

    DemoSection(
        title = "Limit",
        description = "`max` caps the total number of files. Adding three files at once only keeps the free slots, " +
            "and the trigger becomes disabled when the limit is reached.",
        code = "NUpload(max = 2, multiple = true, ...)",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NUpload(
                fileList = files,
                onFileListChange = { files = it },
                controller = controller,
                max = 2,
                multiple = true,
                onOpenFileDialog = {
                    val start = nextId
                    nextId += 3
                    controller.addFiles(
                        List(3) { offset ->
                            NUploadFile(
                                id = "limit-${start + offset}",
                                name = "limit-${start + offset}.txt",
                            )
                        },
                    )
                },
            )
            NText(
                text = "Click the trigger to add three files; only the remaining slots are accepted.",
                color = LocalNTheme.current.colors.text3,
            )
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
                            id = "custom-$id",
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
