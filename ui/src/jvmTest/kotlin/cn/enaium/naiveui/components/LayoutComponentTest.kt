package cn.enaium.naiveui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.swipe
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class LayoutComponentTest {
    @Test
    fun spaceRendersChildren() = runComposeUiTest {
        setNContent {
            NSpace(size = 8.dp) {
                NText("Space A")
                NText("Space B")
            }
        }
        onNodeWithText("Space A").assertExists()
        onNodeWithText("Space B").assertExists()
    }

    @Test
    fun flexRendersChildrenVertically() = runComposeUiTest {
        setNContent {
            NFlex(vertical = true, size = 4.dp) {
                NText("Flex A")
                NText("Flex B")
            }
        }
        onNodeWithText("Flex A").assertExists()
        onNodeWithText("Flex B").assertExists()
    }

    @Test
    fun gridPlacesItems() = runComposeUiTest {
        setNContent {
            NGrid(cols = 12, xGap = 8.dp) {
                NGridItem(span = 6) { NText("Grid left") }
                NGridItem(span = 6) { NText("Grid right") }
                NGridItem(span = 12) { NText("Grid full") }
            }
        }
        onNodeWithText("Grid left").assertExists()
        onNodeWithText("Grid right").assertExists()
        onNodeWithText("Grid full").assertExists()
    }

    @Test
    fun layoutRendersHeaderSiderAndContent() = runComposeUiTest {
        setNContent {
            NLayout(hasSider = true, modifier = Modifier.size(400.dp, 300.dp)) {
                NLayoutSider {
                    NText("Sider text")
                }
                NLayoutContent {
                    NLayoutHeader { NText("Header text") }
                    NText("Content text")
                    NLayoutFooter { NText("Footer text") }
                }
            }
        }
        onNodeWithText("Sider text").assertExists()
        onNodeWithText("Header text").assertExists()
        onNodeWithText("Content text").assertExists()
        onNodeWithText("Footer text").assertExists()
    }

    @Test
    fun layoutSiderTriggerInvokesCollapseChange() = runComposeUiTest {
        var collapsed by mutableStateOf(false)
        setNContent {
            NLayout(hasSider = true, modifier = Modifier.size(400.dp, 300.dp)) {
                NLayoutSider(
                    collapsed = collapsed,
                    onCollapsedChange = { collapsed = it },
                    showTrigger = true,
                    triggerStyle = NLayoutSiderTrigger.Bar,
                ) {
                    NText("Sider body")
                }
                NLayoutContent { NText("Layout body") }
            }
        }
        onNodeWithText("Sider body").assertExists()
        onAllNodes(hasClickAction())[0].performClick()
        runOnIdle { assertTrue(collapsed) }
    }

    @Test
    fun splitRendersBothPanes() = runComposeUiTest {
        setNContent {
            NSplit(
                modifier = Modifier.size(320.dp, 200.dp),
                pane1 = { NText("Pane one") },
                pane2 = { NText("Pane two") },
            )
        }
        onNodeWithText("Pane one").assertExists()
        onNodeWithText("Pane two").assertExists()
    }

    @Test
    fun splitDragUpdatesSizes() = runComposeUiTest {
        var sizes by mutableStateOf(listOf(0.5f, 0.5f))
        setNContent {
            NSplit(
                modifier = Modifier.size(320.dp, 200.dp),
                sizes = sizes,
                onSizesChange = { sizes = it },
                trigger = {
                    Box(Modifier.fillMaxSize().testTag("split-trigger"))
                },
                pane1 = { NText("Start pane") },
                pane2 = { NText("End pane") },
            )
        }
        onNodeWithTag("split-trigger").performTouchInput {
            swipe(center, center + Offset(120f, 0f), durationMillis = 200)
        }
        runOnIdle {
            assertTrue(sizes.first() > 0.5f)
            assertEquals(1f, sizes[0] + sizes[1], 0.001f)
        }
    }
}
