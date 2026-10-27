package cn.enaium.naiveui.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class NTreeDisplayTest {
    private fun data(): List<NTreeOption> = listOf(
        NTreeOption(
            label = "Root",
            key = "root",
            children = listOf(
                NTreeOption(label = "Child A", key = "a"),
                NTreeOption(label = "Child B", key = "b"),
            ),
        ),
    )

    @Test
    fun rendersRootAndHidesCollapsedChildren() = runComposeUiTest {
        setNContent { NTree(data = data()) }
        onNodeWithText("Root").assertExists()
        onNodeWithText("Child A").assertDoesNotExist()
        onNodeWithText("Child B").assertDoesNotExist()
    }

    @Test
    fun defaultExpandedKeysShowChildren() = runComposeUiTest {
        setNContent {
            NTree(data = data(), defaultExpandedKeys = listOf("root"))
        }
        onNodeWithText("Root").assertExists()
        onNodeWithText("Child A").assertExists()
        onNodeWithText("Child B").assertExists()
    }

    @Test
    fun expandOnClickRevealsChildren() = runComposeUiTest {
        setNContent {
            NTree(data = data(), expandOnClick = true)
        }
        onNodeWithText("Root").performClick()
        waitForIdle()
        onNodeWithText("Child A").assertExists()
        onNodeWithText("Child B").assertExists()
    }

    @Test
    fun clickingNodeSelectsIt() = runComposeUiTest {
        var selected: List<String> = emptyList()
        setNContent {
            NTree(
                data = data(),
                onUpdateSelectedKeys = { selected = it },
                onNodeClick = {},
            )
        }
        onNodeWithText("Root").performClick()
        runOnIdle { assertEquals(listOf("root"), selected) }
    }

    @Test
    fun multipleSelectionTogglesKeys() = runComposeUiTest {
        var selected: List<String> = emptyList()
        setNContent {
            NTree(
                data = data(),
                multiple = true,
                defaultExpandedKeys = listOf("root"),
                onUpdateSelectedKeys = { selected = it },
            )
        }
        onNodeWithText("Root").performClick()
        runOnIdle { assertEquals(listOf("root"), selected) }
        onNodeWithText("Child A").performClick()
        runOnIdle { assertEquals(listOf("root", "a"), selected) }
        onNodeWithText("Root").performClick()
        runOnIdle { assertEquals(listOf("a"), selected) }
    }

    @Test
    fun cascadeCheckSelectsWholeSubtree() = runComposeUiTest {
        var checked: Set<String> = emptySet()
        setNContent {
            NTree(
                data = data(),
                checkable = true,
                cascade = true,
                defaultExpandedKeys = listOf("root"),
                overrideClickBehavior = { NTreeClickBehavior.ToggleCheck },
                onUpdateCheckedKeys = { checked = it.toSet() },
            )
        }
        onNodeWithText("Root").performClick()
        runOnIdle { assertEquals(setOf("root", "a", "b"), checked) }
    }

    @Test
    fun checkingAllChildrenChecksParent() = runComposeUiTest {
        var checked: Set<String> = emptySet()
        setNContent {
            NTree(
                data = data(),
                checkable = true,
                cascade = true,
                defaultExpandedKeys = listOf("root"),
                overrideClickBehavior = { NTreeClickBehavior.ToggleCheck },
                onUpdateCheckedKeys = { checked = it.toSet() },
            )
        }
        onNodeWithText("Child A").performClick()
        runOnIdle { assertEquals(setOf("a"), checked) }
        onNodeWithText("Child B").performClick()
        runOnIdle { assertEquals(setOf("a", "b", "root"), checked) }
    }

    @Test
    fun disabledNodeCannotBeSelected() = runComposeUiTest {
        var selected: List<String> = emptyList()
        setNContent {
            NTree(
                data = listOf(NTreeOption(label = "Root", key = "root", disabled = true)),
                onUpdateSelectedKeys = { selected = it },
            )
        }
        onNodeWithText("Root").performClick()
        runOnIdle { assertEquals(emptyList(), selected) }
    }

    @Test
    fun patternFilterKeepsMatchesAndAncestors() = runComposeUiTest {
        val filteredData = listOf(
            NTreeOption(
                label = "Root",
                key = "root",
                children = listOf(
                    NTreeOption(label = "Child A", key = "a"),
                    NTreeOption(label = "Child B", key = "b"),
                ),
            ),
            NTreeOption(label = "Other", key = "other"),
        )
        setNContent {
            NTree(
                data = filteredData,
                pattern = "Child B",
                showIrrelevantNodes = false,
            )
        }
        onNodeWithText("Root").assertExists()
        onNodeWithText("Child B").assertExists()
        onNodeWithText("Other").assertDoesNotExist()
    }

    @Test
    fun selectableFalseIgnoresClicks() = runComposeUiTest {
        var selected: List<String> = emptyList()
        setNContent {
            NTree(
                data = data(),
                selectable = false,
                onUpdateSelectedKeys = { selected = it },
            )
        }
        onNodeWithText("Root").performClick()
        runOnIdle { assertEquals(emptyList(), selected) }
    }
}
