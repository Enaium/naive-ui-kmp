package cn.enaium.naiveui.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import cn.enaium.naiveui.theme.NStatus
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class NListDisplayTest {
    @Test
    fun rendersItemsHeaderAndFooter() = runComposeUiTest {
        setNContent {
            NList(
                header = { NText("Header") },
                footer = { NText("Footer") },
            ) {
                NListItem { NText("First") }
                NListItem { NText("Second") }
            }
        }
        onNodeWithText("Header").assertExists()
        onNodeWithText("Footer").assertExists()
        onNodeWithText("First").assertExists()
        onNodeWithText("Second").assertExists()
    }

    @Test
    fun rendersPrefixSuffixAndExtraSlots() = runComposeUiTest {
        setNContent {
            NList {
                NListItem(
                    prefix = { NText("P") },
                    suffix = { NText("S") },
                    extra = { NText("E") },
                ) { NText("Body") }
            }
        }
        onNodeWithText("P").assertExists()
        onNodeWithText("S").assertExists()
        onNodeWithText("E").assertExists()
        onNodeWithText("Body").assertExists()
    }

    @Test
    fun clickableItemInvokesHandler() = runComposeUiTest {
        var clicks = 0
        setNContent {
            NList {
                NListItem(onClick = { clicks++ }) { NText("Clickable") }
            }
        }
        onNodeWithText("Clickable").performClick()
        runOnIdle { assertEquals(1, clicks) }
    }

    @Test
    fun borderedAndDividerlessListRenders() = runComposeUiTest {
        setNContent {
            NList(bordered = true, hoverable = true, showDivider = false) {
                NListItem { NText("Alpha") }
                NListItem { NText("Beta") }
            }
        }
        onNodeWithText("Alpha").assertExists()
        onNodeWithText("Beta").assertExists()
    }
}

@OptIn(ExperimentalTestApi::class)
class NTimelineDisplayTest {
    @Test
    fun rendersVerticalItems() = runComposeUiTest {
        setNContent {
            NTimeline(
                items = listOf(
                    NTimelineItem(title = "Created", content = "Step one", time = "09:00"),
                    NTimelineItem(
                        title = "Shipped",
                        content = "Step two",
                        time = "10:00",
                        type = NStatus.Success,
                    ),
                ),
            )
        }
        onNodeWithText("Created").assertExists()
        onNodeWithText("Step one").assertExists()
        onNodeWithText("09:00").assertExists()
        onNodeWithText("Shipped").assertExists()
        onNodeWithText("Step two").assertExists()
        onNodeWithText("10:00").assertExists()
    }

    @Test
    fun rendersHorizontalItems() = runComposeUiTest {
        setNContent {
            NTimeline(
                items = listOf(
                    NTimelineItem(title = "One", content = "First"),
                    NTimelineItem(title = "Two", content = "Second"),
                ),
                horizontal = true,
            )
        }
        onNodeWithText("One").assertExists()
        onNodeWithText("Two").assertExists()
        onNodeWithText("First").assertExists()
        onNodeWithText("Second").assertExists()
    }

    @Test
    fun globalIconIsUsedForEveryItem() = runComposeUiTest {
        setNContent {
            NTimeline(
                items = listOf(
                    NTimelineItem(title = "One"),
                    NTimelineItem(title = "Two"),
                    NTimelineItem(title = "Three"),
                ),
                icon = { NText("dot") },
            )
        }
        onAllNodesWithText("dot").assertCountEquals(3)
    }

    @Test
    fun itemSpecificIconReplacesDefaultMarker() = runComposeUiTest {
        setNContent {
            NTimeline(
                items = listOf(
                    NTimelineItem(title = "One", icon = { NText("star") }),
                    NTimelineItem(title = "Two"),
                ),
            )
        }
        onNodeWithText("star").assertExists()
        onNodeWithText("One").assertExists()
        onNodeWithText("Two").assertExists()
    }

    @Test
    fun dashedLineAndPlacementRenderContent() = runComposeUiTest {
        setNContent {
            NTimeline(
                items = listOf(
                    NTimelineItem(title = "Left one", lineType = NTimelineLineType.Dashed),
                    NTimelineItem(title = "Left two", content = "Details"),
                ),
                itemPlacement = NTimelinePlacement.Right,
            )
        }
        onNodeWithText("Left one").assertExists()
        onNodeWithText("Left two").assertExists()
        onNodeWithText("Details").assertExists()
    }
}

@OptIn(ExperimentalTestApi::class)
class NDescriptionsDisplayTest {
    @Test
    fun rendersTitleLabelsAndValues() = runComposeUiTest {
        setNContent {
            NDescriptions(
                title = "User",
                column = 2,
                labelPlacement = NDescriptionsLabelPlacement.Left,
            ) {
                NDescriptionsItem(label = "Name") { NText("Alice") }
                NDescriptionsItem(label = "Age") { NText("30") }
            }
        }
        onNodeWithText("User").assertExists()
        onNodeWithText("Name").assertExists()
        onNodeWithText("Alice").assertExists()
        onNodeWithText("Age").assertExists()
        onNodeWithText("30").assertExists()
        onAllNodesWithText(":").assertCountEquals(2)
    }

    @Test
    fun topPlacementOmitsSeparator() = runComposeUiTest {
        setNContent {
            NDescriptions(column = 2, labelPlacement = NDescriptionsLabelPlacement.Top) {
                NDescriptionsItem(label = "Name") { NText("Alice") }
            }
        }
        onNodeWithText("Name").assertExists()
        onNodeWithText("Alice").assertExists()
        onNodeWithText(":").assertDoesNotExist()
    }

    @Test
    fun borderedDescriptionsRenderAllCells() = runComposeUiTest {
        setNContent {
            NDescriptions(bordered = true, column = 2) {
                NDescriptionsItem(label = "Name") { NText("Alice") }
                NDescriptionsItem(label = "City") { NText("Paris") }
                NDescriptionsItem(label = "Age") { NText("30") }
            }
        }
        onNodeWithText("Name").assertExists()
        onNodeWithText("Alice").assertExists()
        onNodeWithText("City").assertExists()
        onNodeWithText("Paris").assertExists()
        onNodeWithText("Age").assertExists()
        onNodeWithText("30").assertExists()
    }

    @Test
    fun spanAllowsItemToOccupyColumns() = runComposeUiTest {
        setNContent {
            NDescriptions(column = 3, labelPlacement = NDescriptionsLabelPlacement.Left) {
                NDescriptionsItem(label = "Full", span = 3) { NText("Spanning content") }
                NDescriptionsItem(label = "Short") { NText("Small") }
            }
        }
        onNodeWithText("Full").assertExists()
        onNodeWithText("Spanning content").assertExists()
        onNodeWithText("Short").assertExists()
        onNodeWithText("Small").assertExists()
    }

    @Test
    fun customHeaderAndLabelContentRender() = runComposeUiTest {
        setNContent {
            NDescriptions(
                header = { NText("Custom header") },
                labelPlacement = NDescriptionsLabelPlacement.Left,
            ) {
                NDescriptionsItem(labelContent = { NText("Custom label") }) {
                    NText("Custom value")
                }
            }
        }
        onNodeWithText("Custom header").assertExists()
        onNodeWithText("Custom label").assertExists()
        onNodeWithText("Custom value").assertExists()
    }
}
