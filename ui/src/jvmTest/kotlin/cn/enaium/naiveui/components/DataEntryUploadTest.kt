package cn.enaium.naiveui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class DataEntryUploadTest {
    @Test
    fun uploadSingleModeKeepsFirstFile() = runComposeUiTest {
        lateinit var controller: NUploadController
        var files by mutableStateOf(emptyList<NUploadFile>())
        setNContent {
            controller = rememberNUploadController()
            NUpload(
                controller = controller,
                defaultUpload = false,
                onFileListChange = { files = it },
            )
        }
        runOnIdle {
            controller.addFiles(
                listOf(
                    NUploadFile(id = "1", name = "a.txt"),
                    NUploadFile(id = "2", name = "b.txt"),
                ),
            )
        }
        waitForIdle()
        runOnIdle {
            assertEquals(1, files.size)
            assertEquals("a.txt", files.single().name)
        }
    }

    @Test
    fun uploadAcceptFiltersFiles() = runComposeUiTest {
        lateinit var controller: NUploadController
        var files by mutableStateOf(emptyList<NUploadFile>())
        setNContent {
            controller = rememberNUploadController()
            NUpload(
                controller = controller,
                defaultUpload = false,
                multiple = true,
                accept = "image/*",
                onFileListChange = { files = it },
            )
        }
        runOnIdle {
            controller.addFiles(
                listOf(
                    NUploadFile(id = "1", name = "a.txt", type = "text/plain"),
                    NUploadFile(id = "2", name = "b.png", type = "image/png"),
                ),
            )
        }
        waitForIdle()
        runOnIdle {
            assertEquals(listOf("b.png"), files.map { it.name })
        }
    }

    @Test
    fun uploadMaxLimitsFileCount() = runComposeUiTest {
        lateinit var controller: NUploadController
        var files by mutableStateOf(emptyList<NUploadFile>())
        setNContent {
            controller = rememberNUploadController()
            NUpload(
                controller = controller,
                defaultUpload = false,
                multiple = true,
                max = 1,
                onFileListChange = { files = it },
            )
        }
        runOnIdle {
            controller.addFiles(
                listOf(
                    NUploadFile(id = "1", name = "a.txt"),
                    NUploadFile(id = "2", name = "b.txt"),
                ),
            )
        }
        waitForIdle()
        runOnIdle { assertEquals(1, files.size) }
    }

    @Test
    fun uploadBeforeUploadRejectsFiles() = runComposeUiTest {
        lateinit var controller: NUploadController
        var files by mutableStateOf(emptyList<NUploadFile>())
        setNContent {
            controller = rememberNUploadController()
            NUpload(
                controller = controller,
                defaultUpload = false,
                multiple = true,
                beforeUpload = { file, _ -> file.name.endsWith(".png") },
                onFileListChange = { files = it },
            )
        }
        runOnIdle {
            controller.addFiles(
                listOf(
                    NUploadFile(id = "1", name = "a.txt"),
                    NUploadFile(id = "2", name = "b.png"),
                ),
            )
        }
        waitForIdle()
        runOnIdle { assertEquals(listOf("b.png"), files.map { it.name }) }
    }

    @Test
    fun uploadCustomRequestFinishesFile() = runComposeUiTest {
        lateinit var controller: NUploadController
        var files by mutableStateOf(emptyList<NUploadFile>())
        setNContent {
            controller = rememberNUploadController()
            NUpload(
                controller = controller,
                multiple = true,
                onFileListChange = { files = it },
                customRequest = { request ->
                    request.onProgress(50f)
                    request.onFinish(request.file.copy(url = "https://example.com/a.png"))
                },
            )
        }
        runOnIdle {
            controller.addFiles(listOf(NUploadFile(id = "1", name = "a.png")))
        }
        waitForIdle()
        runOnIdle {
            val file = files.single()
            assertEquals(NUploadStatus.Finished, file.status)
            assertEquals(100f, file.percentage)
            assertEquals("https://example.com/a.png", file.url)
        }
    }

    @Test
    fun uploadCustomRequestErrorMarksFailed() = runComposeUiTest {
        lateinit var controller: NUploadController
        var files by mutableStateOf(emptyList<NUploadFile>())
        setNContent {
            controller = rememberNUploadController()
            NUpload(
                controller = controller,
                onFileListChange = { files = it },
                customRequest = { request -> request.onError() },
            )
        }
        runOnIdle { controller.addFiles(listOf(NUploadFile(id = "1", name = "a.png"))) }
        waitForIdle()
        runOnIdle { assertEquals(NUploadStatus.Failed, files.single().status) }
    }

    @Test
    fun uploadSubmitStartsPendingFiles() = runComposeUiTest {
        lateinit var controller: NUploadController
        var files by mutableStateOf(emptyList<NUploadFile>())
        var requests = 0
        setNContent {
            controller = rememberNUploadController()
            NUpload(
                controller = controller,
                defaultUpload = false,
                onFileListChange = { files = it },
                customRequest = { request ->
                    requests++
                    request.onFinish(request.file)
                },
            )
        }
        runOnIdle { controller.addFiles(listOf(NUploadFile(id = "1", name = "a.png"))) }
        waitForIdle()
        runOnIdle {
            assertEquals(NUploadStatus.Pending, files.single().status)
            assertEquals(0, requests)
            controller.submit()
        }
        waitForIdle()
        runOnIdle {
            assertEquals(1, requests)
            assertEquals(NUploadStatus.Finished, files.single().status)
        }
    }

    @Test
    fun uploadDisabledIgnoresAdd() = runComposeUiTest {
        lateinit var controller: NUploadController
        var files by mutableStateOf(emptyList<NUploadFile>())
        setNContent {
            controller = rememberNUploadController()
            NUpload(
                controller = controller,
                defaultUpload = false,
                disabled = true,
                onFileListChange = { files = it },
            )
        }
        runOnIdle { controller.addFiles(listOf(NUploadFile(id = "1", name = "a.png"))) }
        waitForIdle()
        runOnIdle { assertTrue(files.isEmpty()) }
    }

    @Test
    fun uploadControlledListRendersProgress() = runComposeUiTest {
        setNContent {
            NUpload(
                fileList = listOf(
                    NUploadFile(
                        id = "1",
                        name = "report.pdf",
                        status = NUploadStatus.Uploading,
                        percentage = 42f,
                    ),
                ),
            )
        }
        onNodeWithText("report.pdf").assertExists()
        onNodeWithText("42%").assertExists()
    }

    @Test
    fun uploadShowFileListFalseHidesFiles() = runComposeUiTest {
        setNContent {
            NUpload(
                fileList = listOf(NUploadFile(id = "1", name = "report.pdf")),
                showFileList = false,
            )
        }
        assertTrue(onAllNodesWithText("report.pdf").fetchSemanticsNodes().isEmpty())
    }
}
