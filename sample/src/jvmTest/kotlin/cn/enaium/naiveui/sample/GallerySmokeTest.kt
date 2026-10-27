package cn.enaium.naiveui.sample

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.runSkikoComposeUiTest
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class GallerySmokeTest {
    @Test
    fun narrowLayoutOpensDrawer() = runSkikoComposeUiTest(size = Size(420f, 900f)) {
        setContent { App() }
        onNodeWithText("Naive UI").assertExists()
        onAllNodesWithText("Search components").assertCountEquals(0)
        onNodeWithContentDescription("Open navigation").performClick()
        waitUntil { onAllNodesWithText("Search components").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Search components").assertExists()
    }

    @Test
    fun githubButtonOpensProjectRepository() = runComposeUiTest {
        var opened: String? = null
        setContent {
            CompositionLocalProvider(
                LocalUriHandler provides object : UriHandler {
                    override fun openUri(uri: String) {
                        opened = uri
                    }
                },
            ) {
                App()
            }
        }
        onNodeWithText("GitHub").performClick()
        runOnIdle {
            kotlin.test.assertEquals("https://github.com/enaium/naive-ui-kmp", opened)
        }
    }

    @Test
    fun docsPageLinksToSampleSource() = runComposeUiTest {
        var opened: String? = null
        setContent {
            CompositionLocalProvider(
                LocalUriHandler provides object : UriHandler {
                    override fun openUri(uri: String) {
                        opened = uri
                    }
                },
            ) {
                App()
            }
        }
        onNodeWithText("View sample source").performClick()
        runOnIdle {
            kotlin.test.assertEquals(
                "https://github.com/enaium/naive-ui-kmp/blob/main/" +
                    "sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery/general/ButtonDocs.kt",
                opened,
            )
        }
    }

    @Test
    fun galleryRendersAndNavigates() = runComposeUiTest {
        setContent { App() }
        onNodeWithText("Naive UI").assertExists()
        onAllNodesWithText("Button").assertCountEquals(2)
        onAllNodesWithText("Button")[0].performClick()
        onAllNodesWithText("Basic")[0].assertExists()
        onAllNodesWithText("Card")[0].performClick()
        onAllNodesWithText("Card")[0].assertExists()
        onNodeWithText("Dark").performClick()
        onNodeWithText("Light").assertExists()
    }
}
