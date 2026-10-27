package cn.enaium.naiveui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Behaviour tests for the remaining navigation components.
 */
@OptIn(ExperimentalTestApi::class)
class NavigationCoreTest {
    @Test
    fun affixRendersWithAndWithoutScrollState() = runComposeUiTest {
        setNContent {
            Column {
                NAffix { NText("Static affix") }
                NAffix(scrollState = ScrollState(240)) { NText("Scrolled affix") }
            }
        }
        onNodeWithText("Static affix").assertExists()
        onNodeWithText("Scrolled affix").assertExists()
    }

    @Test
    fun backTopInvokesClickWhenShown() = runComposeUiTest {
        var clicks by mutableStateOf(0)
        setNContent {
            NBackTop(
                scrollState = ScrollState(0),
                show = true,
                onClick = { clicks++ },
            ) {
                NText("Back to top")
            }
        }
        onNodeWithText("Back to top").performClick()
        runOnIdle { assertEquals(1, clicks) }
    }

    @Test
    fun backTopVisibilityFollowsScrollAndShowFlag() = runComposeUiTest {
        setNContent {
            Column {
                NBackTop(
                    scrollState = ScrollState(1),
                    visibilityHeight = 0.dp,
                ) {
                    NText("Auto back")
                }
                NBackTop(
                    scrollState = ScrollState(500),
                    show = false,
                ) {
                    NText("Hidden back")
                }
            }
        }
        onNodeWithText("Auto back").assertExists()
        onNodeWithText("Hidden back").assertDoesNotExist()
    }

    @Test
    fun breadcrumbClicksItemsExceptCurrent() = runComposeUiTest {
        var clicks by mutableStateOf(0)
        setNContent {
            NBreadcrumb(separator = ">") {
                NBreadcrumbItem(onClick = { clicks++ }) { NText("Home") }
                NBreadcrumbItem { NText("Library") }
                NBreadcrumbItem { NText("Current") }
            }
        }
        onAllNodesWithText(">").assertCountEquals(2)
        onNodeWithText("Home").performClick()
        runOnIdle { assertEquals(1, clicks) }
        onNodeWithText("Library").performClick()
        onNodeWithText("Current").performClick()
        runOnIdle { assertEquals(1, clicks) }
    }

    @Test
    fun breadcrumbCustomHrefItemIsClickable() = runComposeUiTest {
        var clicks by mutableStateOf(0)
        setNContent {
            NBreadcrumb(separatorContent = { NText("|") }) {
                NBreadcrumbItem(clickable = true, onClick = { clicks++ }) { NText("Docs") }
                NBreadcrumbItem { NText("Page") }
            }
        }
        onNodeWithText("|").assertExists()
        onNodeWithText("Docs").performClick()
        runOnIdle { assertEquals(1, clicks) }
    }

    @Test
    fun anchorSelectsTopLevelItem() = runComposeUiTest {
        var selected by mutableStateOf<String?>(null)
        setNContent {
            NAnchor(
                items = listOf(
                    NAnchorItem("intro", "Introduction"),
                    NAnchorItem("api", "API"),
                ),
                onSelect = { selected = it },
            )
        }
        onNodeWithText("API").performClick()
        runOnIdle { assertEquals("api", selected) }
    }

    @Test
    fun anchorRendersAndSelectsNestedChild() = runComposeUiTest {
        var selected by mutableStateOf<String?>(null)
        setNContent {
            NAnchor(
                items = listOf(
                    NAnchorItem(
                        id = "guide",
                        title = "Guide",
                        children = listOf(NAnchorItem("install", "Install")),
                    ),
                ),
                onSelect = { selected = it },
            )
        }
        onNodeWithText("Guide").assertExists()
        onNodeWithText("Install").performClick()
        runOnIdle { assertEquals("install", selected) }
    }

    @Test
    fun anchorLinkInvokesCustomClick() = runComposeUiTest {
        var custom by mutableStateOf(0)
        setNContent {
            NAnchor {
                NAnchorLink(title = "Custom link", href = "custom", onClick = { custom++ })
            }
        }
        onNodeWithText("Custom link").performClick()
        runOnIdle { assertEquals(1, custom) }
    }

    @Test
    fun loadingBarControllerDrivesProvider() = runComposeUiTest {
        var controller: NLoadingBarController? = null
        setNContent {
            NLoadingBarProvider {
                controller = rememberNLoadingBar()
                NText("Page content")
            }
        }
        onNodeWithText("Page content").assertExists()
        runOnIdle { controller!!.start() }
        waitForIdle()
        onNodeWithText("Page content").assertExists()
        runOnIdle { controller!!.finish() }
        waitForIdle()
        onNodeWithText("Page content").assertExists()
        runOnIdle { controller!!.error() }
        waitForIdle()
        onNodeWithText("Page content").assertExists()
    }

    @Test
    fun paginationReportsPageChange() = runComposeUiTest {
        var page by mutableStateOf(1)
        setNContent {
            NPagination(
                page = page,
                pageCount = 5,
                onPageChange = { page = it },
            )
        }
        onNodeWithText("3").performClick()
        runOnIdle { assertEquals(3, page) }
    }

    @Test
    fun paginationPrevAndNextSlotsNavigate() = runComposeUiTest {
        var page by mutableStateOf(2)
        setNContent {
            NPagination(
                page = page,
                pageCount = 5,
                onPageChange = { page = it },
                prev = { NText("Prev") },
                next = { NText("Next") },
            )
        }
        onNodeWithText("Next").performClick()
        runOnIdle { assertEquals(3, page) }
        onNodeWithText("Prev").performClick()
        runOnIdle { assertEquals(2, page) }
    }

    @Test
    fun paginationIgnoresClicksWhenDisabled() = runComposeUiTest {
        var page by mutableStateOf(1)
        setNContent {
            NPagination(
                page = page,
                pageCount = 5,
                disabled = true,
                onPageChange = { page = it },
            )
        }
        onNodeWithText("2").performClick()
        runOnIdle { assertEquals(1, page) }
    }

    @Test
    fun paginationQuickJumperGoesToPage() = runComposeUiTest {
        var page by mutableStateOf(1)
        setNContent {
            NPagination(
                page = page,
                pageCount = 10,
                showQuickJumper = true,
                onPageChange = { page = it },
            )
        }
        onNode(hasSetTextAction()).performTextReplacement("7")
        onNodeWithText("Confirm").performClick()
        runOnIdle { assertEquals(7, page) }
    }

    @Test
    fun paginationSizePickerChangesPageSize() = runComposeUiTest {
        var pageSize by mutableStateOf(10)
        setNContent {
            NPagination(
                page = 1,
                pageCount = 10,
                pageSize = pageSize,
                pageSizes = listOf(10, 20),
                showSizePicker = true,
                onPageSizeChange = { pageSize = it },
            )
        }
        onNodeWithText("10 page").performClick()
        waitUntil { onAllNodesWithText("20 page").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("20 page")[0].performClick()
        runOnIdle { assertEquals(20, pageSize) }
    }

    @Test
    fun stepsReportCurrentChange() = runComposeUiTest {
        var current by mutableStateOf(1)
        setNContent {
            NSteps(
                steps = listOf(
                    NStep("First", "First description"),
                    NStep("Second", "Second description"),
                    NStep("Third", "Third description"),
                ),
                current = current,
                onCurrentChange = { current = it },
            )
        }
        onNodeWithText("Second").performClick()
        runOnIdle { assertEquals(2, current) }
    }

    @Test
    fun stepsIgnoreDisabledStep() = runComposeUiTest {
        var current by mutableStateOf(1)
        setNContent {
            NSteps(
                steps = listOf(
                    NStep("First"),
                    NStep("Second"),
                    NStep("Locked", disabled = true),
                ),
                current = current,
                onCurrentChange = { current = it },
            )
        }
        onNodeWithText("Locked").performClick()
        runOnIdle { assertEquals(1, current) }
    }

    @Test
    fun stepsRenderTitlesAndDescriptionsVertically() = runComposeUiTest {
        setNContent {
            NSteps(
                steps = listOf(
                    NStep("Account", "Create the account"),
                    NStep("Profile", "Fill the profile"),
                ),
                current = 1,
                vertical = true,
                contentPlacement = NStepContentPlacement.Bottom,
            )
        }
        onNodeWithText("Account").assertExists()
        onNodeWithText("Create the account").assertExists()
        onNodeWithText("Profile").assertExists()
        onNodeWithText("Fill the profile").assertExists()
    }

    @Test
    fun stepsUseExplicitStatusWithoutAffectingClick() = runComposeUiTest {
        var current by mutableStateOf(1)
        setNContent {
            NSteps(
                steps = listOf(
                    NStep("Start", status = NStepStatus.Finish),
                    NStep("Failure", status = NStepStatus.Error),
                ),
                current = current,
                status = NStepStatus.Wait,
                onCurrentChange = { current = it },
            )
        }
        onNodeWithText("Failure").performClick()
        runOnIdle { assertEquals(2, current) }
    }
}
