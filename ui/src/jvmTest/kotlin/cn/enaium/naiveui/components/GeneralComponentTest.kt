package cn.enaium.naiveui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NTrigger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class GeneralComponentTest {
    @Test
    fun avatarFallsBackToInitial() = runComposeUiTest {
        setNContent {
            NAvatar(text = "john")
        }
        onNodeWithText("J").assertExists()
    }

    @Test
    fun avatarGroupCollapsesExtraOptions() = runComposeUiTest {
        setNContent {
            NAvatarGroup(
                options = listOf(
                    NAvatarOption(text = "alice"),
                    NAvatarOption(text = "bob"),
                    NAvatarOption(text = "carol"),
                ),
                max = 2,
            )
        }
        onNodeWithText("A").assertExists()
        onNodeWithText("B").assertExists()
        onNodeWithText("+1").assertExists()
    }

    @Test
    fun loadingButtonBlocksInteraction() = runComposeUiTest {
        var clicks by mutableStateOf(0)
        setNContent {
            NButton(onClick = { clicks++ }, loading = true) { NText("Save") }
        }
        onNodeWithText("Save").assertIsNotEnabled()
        onNodeWithText("Save").performClick()
        runOnIdle { assertEquals(0, clicks) }
    }

    @Test
    fun cardRendersHeaderBodyFooterAndCloses() = runComposeUiTest {
        var closed by mutableStateOf(false)
        setNContent {
            NCard(
                title = "Card title",
                closable = true,
                onClose = { closed = true },
                footer = { NText("Card footer") },
            ) {
                NText("Card body")
            }
        }
        onNodeWithText("Card title").assertExists()
        onNodeWithText("Card body").assertExists()
        onNodeWithText("Card footer").assertExists()
        onAllNodes(hasClickAction())[0].performClick()
        runOnIdle { assertTrue(closed) }
    }

    @Test
    fun carouselNextArrowChangesIndex() = runComposeUiTest {
        var index by mutableStateOf(0)
        var changes by mutableStateOf(0)
        setNContent {
            NCarousel(
                items = listOf("One", "Two", "Three"),
                modifier = Modifier.width(320.dp).height(200.dp),
                onIndexChange = {
                    index = it
                    changes++
                },
                arrowContent = {
                    NButton(onClick = { next() }) { NText("Next") }
                },
            ) { NText(it) }
        }
        onNodeWithText("One").assertExists()
        onNodeWithText("Next").performClick()
        waitUntil { index == 1 && changes > 0 }
    }

    @Test
    fun collapseExpandsOnTitleClick() = runComposeUiTest {
        var expanded by mutableStateOf<List<String>>(emptyList())
        setNContent {
            NCollapse(
                defaultExpandedNames = listOf("first"),
                onExpandedNamesChange = { expanded = it },
            ) {
                NCollapseItem("first", title = "First") { NText("First content") }
                NCollapseItem("second", title = "Second") { NText("Second content") }
            }
        }
        onNodeWithText("First content").assertExists()
        onNodeWithText("Second content").assertDoesNotExist()
        onNodeWithText("Second").performClick()
        waitUntil { expanded.contains("second") }
        waitUntil { onAllNodesWithText("Second content").fetchSemanticsNodes().isNotEmpty() }
    }

    @Test
    fun collapseAccordionKeepsSingleItemOpen() = runComposeUiTest {
        var expanded by mutableStateOf<List<String>>(emptyList())
        setNContent {
            NCollapse(
                defaultExpandedNames = listOf("first"),
                accordion = true,
                onExpandedNamesChange = { expanded = it },
            ) {
                NCollapseItem("first", title = "First") { NText("First content") }
                NCollapseItem("second", title = "Second") { NText("Second content") }
            }
        }
        onNodeWithText("Second").performClick()
        waitUntil { expanded == listOf("second") }
    }

    @Test
    fun dividerRendersTitle() = runComposeUiTest {
        setNContent {
            NDivider(title = "Section", titlePlacement = NDividerTitlePlacement.Left)
        }
        onNodeWithText("Section").assertExists()
    }

    @Test
    fun dropdownSelectsOption() = runComposeUiTest {
        var selected by mutableStateOf<String?>(null)
        setNContent {
            NDropdown(
                options = listOf(
                    NDropdownOption.Label("edit", "Edit"),
                    NDropdownOption.Label("delete", "Delete"),
                ),
                trigger = NTrigger.Click,
                onSelect = { selected = it },
            ) {
                NButton { NText("Open menu") }
            }
        }
        onNodeWithText("Open menu").performClick()
        waitUntil { onAllNodesWithText("Edit").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("Edit")[0].performClick()
        runOnIdle { assertEquals("edit", selected) }
    }

    @Test
    fun ellipsisRendersText() = runComposeUiTest {
        setNContent {
            NEllipsis(
                text = "A very long piece of text that will be truncated",
                lineClamp = 1,
                modifier = Modifier.width(120.dp),
            )
        }
        onNodeWithText("A very long piece of text that will be truncated").assertExists()
    }

    @Test
    fun gradientTextRenders() = runComposeUiTest {
        setNContent {
            NGradientText(text = "Gradient heading", type = NGradientTextType.Success)
        }
        onNodeWithText("Gradient heading").assertExists()
    }

    @Test
    fun iconRendersWithContentDescription() = runComposeUiTest {
        setNContent {
            NIcon(NIcons.Person, contentDescription = "avatar placeholder")
        }
        onNodeWithContentDescription("avatar placeholder").assertExists()
    }

    @Test
    fun pageHeaderRendersAndInvokesBack() = runComposeUiTest {
        var backed by mutableStateOf(false)
        setNContent {
            NPageHeader(
                title = "Page title",
                subtitle = "Page subtitle",
                onBack = { backed = true },
            ) {
                NText("Page body")
            }
        }
        onNodeWithText("Page title").assertExists()
        onNodeWithText("Page subtitle").assertExists()
        onNodeWithText("Page body").assertExists()
        onAllNodes(hasClickAction())[0].performClick()
        runOnIdle { assertTrue(backed) }
    }

    @Test
    fun closableTagInvokesClose() = runComposeUiTest {
        var closed by mutableStateOf(false)
        setNContent {
            NTag(text = "Closable", closable = true, onClose = { closed = true })
        }
        onNodeWithText("Closable").assertExists()
        onAllNodes(hasClickAction() and isEnabled(), useUnmergedTree = true)[0].performClick()
        runOnIdle { assertTrue(closed) }
    }

    @Test
    fun checkableTagToggles() = runComposeUiTest {
        var checked by mutableStateOf(false)
        setNContent {
            NTag(
                text = "Toggle",
                checkable = true,
                checked = checked,
                onCheckedChange = { checked = it },
            )
        }
        onNodeWithText("Toggle").performClick()
        runOnIdle { assertTrue(checked) }
    }

    @Test
    fun typographyRendersHeadingsAndParagraph() = runComposeUiTest {
        setNContent {
            Column {
                NH1("Heading one")
                NH3("Heading three")
                NP("Paragraph body")
                NBlockquote("Quoted text")
                NText("Inline text", strong = true, underline = true)
            }
        }
        onNodeWithText("Heading one").assertExists()
        onNodeWithText("Heading three").assertExists()
        onNodeWithText("Paragraph body").assertExists()
        onNodeWithText("Quoted text").assertExists()
        onNodeWithText("Inline text").assertExists()
    }

    @Test
    fun watermarkRendersContent() = runComposeUiTest {
        setNContent {
            NWatermark(text = "confidential", rotate = -20f, cross = true) {
                NText("Watermarked content")
            }
        }
        onNodeWithText("Watermarked content").assertExists()
    }

    @Test
    fun floatButtonClickInvokesHandler() = runComposeUiTest {
        var clicks by mutableStateOf(0)
        setNContent {
            NFloatButton(onClick = { clicks++ }) { NText("FAB") }
        }
        onNodeWithText("FAB").performClick()
        runOnIdle { assertEquals(1, clicks) }
    }

    @Test
    fun floatButtonMenuToggles() = runComposeUiTest {
        var menuVisible by mutableStateOf(false)
        setNContent {
            NFloatButton(
                menuTrigger = NFloatButtonMenuTrigger.Click,
                showMenu = menuVisible,
                onShowMenuChange = { menuVisible = it },
                menu = { NText("Create item") },
            ) {
                NText("FAB menu")
            }
        }
        onNodeWithText("Create item").assertDoesNotExist()
        onNodeWithText("FAB menu").performClick()
        waitUntil { menuVisible }
        waitUntil { onAllNodesWithText("Create item").fetchSemanticsNodes().isNotEmpty() }
    }
}
