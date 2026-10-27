package cn.enaium.naiveui.sample

import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import java.io.File
import kotlin.test.Test
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image

@OptIn(ExperimentalTestApi::class)
class GalleryScreenshotTest {
    @Test
    fun captureGalleryScreens() = runComposeUiTest {
        setContent { App() }
        onNodeWithText("Naive UI").assertExists()
        capture("gallery-button.png")
        onAllNodesWithText("Date Picker")[0].performClick()
        waitForIdle()
        capture("gallery-date-picker.png")
        onAllNodesWithText("Data Table")[0].performScrollTo().performClick()
        waitForIdle()
        capture("gallery-data-table.png")
        onNodeWithText("Dark").performClick()
        waitForIdle()
        capture("gallery-dark.png")
        onAllNodesWithText("English")[0].performClick()
        waitUntil { onAllNodesWithText("简体中文").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("简体中文").performScrollTo().performClick()
        waitForIdle()
        capture("gallery-chinese.png")
    }

    private fun ComposeUiTest.capture(name: String) {
        val bitmap = onRoot().captureToImage()
        val image = Image.makeFromBitmap(bitmap.asSkiaBitmap())
        val data = image.encodeToData(EncodedImageFormat.PNG) ?: error("encode failed")
        val dir = File("build/screenshots").apply { mkdirs() }
        File(dir, name).writeBytes(data.bytes)
    }
}
