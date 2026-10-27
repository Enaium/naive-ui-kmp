package cn.enaium.naiveui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class DataEntrySelectionTest {
    @Test
    fun checkboxTogglesAndReportsValue() = runComposeUiTest {
        var checked by mutableStateOf(false)
        var reported: Any? = null
        setNContent {
            NCheckbox(
                checked = checked,
                onCheckedChange = { checked = it },
                label = "Agree",
                checkedValue = "yes",
                uncheckedValue = "no",
                onValueChange = { reported = it },
            )
        }
        onNodeWithText("Agree").performClick()
        runOnIdle {
            assertEquals(true, checked)
            assertEquals("yes", reported)
        }
        onNodeWithText("Agree").performClick()
        runOnIdle {
            assertEquals(false, checked)
            assertEquals("no", reported)
        }
    }

    @Test
    fun checkboxDisabledDoesNotToggle() = runComposeUiTest {
        var checked by mutableStateOf(false)
        setNContent {
            NCheckbox(
                checked = checked,
                onCheckedChange = { checked = it },
                disabled = true,
                label = "Agree",
            )
        }
        onNodeWithText("Agree").assertIsNotEnabled()
        onNodeWithText("Agree").performClick()
        runOnIdle { assertEquals(false, checked) }
    }

    @Test
    fun checkboxGroupRespectsMinAndMax() = runComposeUiTest {
        var value by mutableStateOf(listOf<Any>("a", "b"))
        setNContent {
            NCheckboxGroup(value = value, onValueChange = { value = it }, min = 2, max = 2) {
                NCheckbox(checked = false, value = "a", label = "A")
                NCheckbox(checked = false, value = "b", label = "B")
                NCheckbox(checked = false, value = "c", label = "C")
            }
        }
        onNodeWithText("C").performClick()
        runOnIdle { assertEquals(listOf("a", "b"), value) }
        onNodeWithText("A").performClick()
        runOnIdle { assertEquals(listOf("a", "b"), value) }
    }

    @Test
    fun checkboxGroupTogglesValue() = runComposeUiTest {
        var value by mutableStateOf(listOf<Any>("a"))
        setNContent {
            NCheckboxGroup(value = value, onValueChange = { value = it }) {
                NCheckbox(checked = false, value = "a", label = "A")
                NCheckbox(checked = false, value = "b", label = "B")
            }
        }
        onNodeWithText("B").performClick()
        runOnIdle { assertEquals(listOf("a", "b"), value) }
    }

    @Test
    fun radioGroupSelectsValue() = runComposeUiTest {
        var value by mutableStateOf<Any?>("a")
        setNContent {
            NRadioGroup(value = value, onValueChange = { value = it }) {
                NRadio(value = "a", label = "A")
                NRadio(value = "b", label = "B")
            }
        }
        onNodeWithText("B").performClick()
        runOnIdle { assertEquals("b", value) }
    }

    @Test
    fun radioGroupDisabledBlocksSelection() = runComposeUiTest {
        var value by mutableStateOf<Any?>("a")
        setNContent {
            NRadioGroup(value = value, onValueChange = { value = it }, disabled = true) {
                NRadio(value = "a", label = "A")
                NRadio(value = "b", label = "B")
            }
        }
        onNodeWithText("B").assertIsNotEnabled()
        onNodeWithText("B").performClick()
        runOnIdle { assertEquals("a", value) }
    }

    @Test
    fun standaloneRadioReportsCheckedChange() = runComposeUiTest {
        var checked by mutableStateOf(false)
        setNContent {
            NRadio(checked = checked, onCheckedChange = { checked = it }, label = "Only")
        }
        onNodeWithText("Only").performClick()
        runOnIdle { assertEquals(true, checked) }
    }

    @Test
    fun switchTogglesValue() = runComposeUiTest {
        var checked by mutableStateOf(false)
        setNContent {
            NSwitch(
                checked = checked,
                onCheckedChange = { checked = it },
                activeText = "On",
                inactiveText = "Off",
            )
        }
        onNodeWithText("Off").performClick()
        runOnIdle { assertEquals(true, checked) }
        onNodeWithText("On").performClick()
        runOnIdle { assertEquals(false, checked) }
    }

    @Test
    fun switchLoadingBlocksToggle() = runComposeUiTest {
        var checked by mutableStateOf(false)
        setNContent {
            NSwitch(checked = checked, onCheckedChange = { checked = it }, loading = true, inactiveText = "Off")
        }
        onNodeWithText("Off").performClick()
        runOnIdle { assertEquals(false, checked) }
    }

    @Test
    fun switchDisabledBlocksToggle() = runComposeUiTest {
        var checked by mutableStateOf(false)
        setNContent {
            NSwitch(checked = checked, onCheckedChange = { checked = it }, disabled = true, inactiveText = "Off")
        }
        onNodeWithText("Off").assertIsNotEnabled()
        onNodeWithText("Off").performClick()
        runOnIdle { assertEquals(false, checked) }
    }

    @Test
    fun selectSelectsSingleOption() = runComposeUiTest {
        var value by mutableStateOf<Any?>(null)
        setNContent {
            NSelect(
                options = listOf(NSelectOption("Option A", "a"), NSelectOption("Option B", "b")),
                value = value,
                onValueChange = { value = it },
                placeholder = "Pick one",
            )
        }
        onNodeWithText("Pick one").performClick()
        waitUntil { onAllNodesWithText("Option A").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("Option A")[0].performClick()
        runOnIdle { assertEquals("a", value) }
    }

    @Test
    fun selectMultipleAccumulatesValues() = runComposeUiTest {
        var value by mutableStateOf<Any?>(emptyList<Any>())
        setNContent {
            NSelect(
                options = listOf(NSelectOption("Option A", "a"), NSelectOption("Option B", "b")),
                value = value,
                onValueChange = { value = it },
                multiple = true,
                placeholder = "Pick many",
            )
        }
        onNodeWithText("Pick many").performClick()
        waitUntil { onAllNodesWithText("Option A").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("Option A")[0].performClick()
        waitForIdle()
        onAllNodesWithText("Option B")[0].performClick()
        runOnIdle { assertEquals(listOf("a", "b"), value) }
    }

    @Test
    fun selectFilterableFiltersOptions() = runComposeUiTest {
        var value by mutableStateOf<Any?>(null)
        setNContent {
            NSelect(
                options = listOf(NSelectOption("Alpha", "a"), NSelectOption("Beta", "b")),
                value = value,
                onValueChange = { value = it },
                filterable = true,
                placeholder = "Pick one",
            )
        }
        onNode(hasSetTextAction()).performTextInput("Bet")
        waitForIdle()
        waitUntil { onAllNodesWithText("Alpha").fetchSemanticsNodes().isEmpty() }
        onNodeWithText("Beta").assertExists()
        onAllNodesWithText("Beta")[0].performClick()
        runOnIdle { assertEquals("b", value) }
    }

    @Test
    fun selectDisabledDoesNotOpen() = runComposeUiTest {
        var value by mutableStateOf<Any?>(null)
        setNContent {
            NSelect(
                options = listOf(NSelectOption("Option A", "a")),
                value = value,
                onValueChange = { value = it },
                placeholder = "Pick one",
                disabled = true,
            )
        }
        onNodeWithText("Pick one").performClick()
        waitForIdle()
        assertTrue(onAllNodesWithText("Option A").fetchSemanticsNodes().isEmpty())
        runOnIdle { assertEquals(null, value) }
    }

    @Test
    fun selectRendersGroupOptions() = runComposeUiTest {
        var value by mutableStateOf<Any?>(null)
        setNContent {
            NSelect(
                options = listOf(
                    NSelectGroupOption(
                        label = "Fruits",
                        children = listOf(
                            NSelectOption("Apple", "apple"),
                            NSelectOption("Banana", "banana"),
                        ),
                    ),
                ),
                value = value,
                onValueChange = { value = it },
                placeholder = "Pick one",
            )
        }
        onNodeWithText("Pick one").performClick()
        waitUntil { onAllNodesWithText("Fruits").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("Banana")[0].performClick()
        runOnIdle { assertEquals("banana", value) }
    }

    @Test
    fun selectControlledShowOpensMenu() = runComposeUiTest {
        var value by mutableStateOf<Any?>(null)
        setNContent {
            NSelect(
                options = listOf(NSelectOption("Option A", "a")),
                value = value,
                onValueChange = { value = it },
                placeholder = "Pick one",
                show = true,
            )
        }
        waitUntil { onAllNodesWithText("Option A").fetchSemanticsNodes().isNotEmpty() }
    }

    @Test
    fun selectMaxTagCountCollapsesTags() = runComposeUiTest {
        var value by mutableStateOf<Any?>(listOf("a", "b"))
        setNContent {
            NSelect(
                options = listOf(NSelectOption("Alpha", "a"), NSelectOption("Beta", "b")),
                value = value,
                onValueChange = { value = it },
                multiple = true,
                maxTagCount = 1,
                placeholder = "Pick many",
            )
        }
        onNodeWithText("Alpha").assertExists()
        onNodeWithText("+1").assertExists()
        assertTrue(onAllNodesWithText("Beta").fetchSemanticsNodes().isEmpty())
    }

    @Test
    fun cascaderSelectsLeafThroughColumns() = runComposeUiTest {
        var value by mutableStateOf<Any?>(null)
        setNContent {
            NCascader(
                options = sampleRegions(),
                value = value,
                onValueChange = { value = it },
                placeholder = "Region",
            )
        }
        onNodeWithText("Region").performClick()
        waitUntil { onAllNodesWithText("Zhejiang").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("Zhejiang")[0].performClick()
        waitUntil { onAllNodesWithText("Hangzhou").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("Hangzhou")[0].performClick()
        runOnIdle { assertEquals("hangzhou", value) }
    }

    @Test
    fun cascaderChangeOnSelectEmitsParent() = runComposeUiTest {
        var value by mutableStateOf<Any?>(null)
        setNContent {
            NCascader(
                options = sampleRegions(),
                value = value,
                onValueChange = { value = it },
                placeholder = "Region",
                changeOnSelect = true,
            )
        }
        onNodeWithText("Region").performClick()
        waitUntil { onAllNodesWithText("Zhejiang").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("Zhejiang")[0].performClick()
        runOnIdle { assertEquals("zhejiang", value) }
    }

    @Test
    fun cascaderCheckableCascadesToDescendants() = runComposeUiTest {
        var value by mutableStateOf<Any?>(emptyList<Any>())
        setNContent {
            NCascader(
                options = sampleRegions(),
                value = value,
                onValueChange = { value = it },
                multiple = true,
                checkable = true,
                maxTagCount = 0,
                placeholder = "Region",
            )
        }
        onNodeWithText("Region").performClick()
        waitUntil { onAllNodesWithText("Zhejiang").fetchSemanticsNodes().isNotEmpty() }
        onAllNodes(isToggleable())[0].performClick()
        runOnIdle {
            assertEquals(setOf("zhejiang", "hangzhou", "ningbo"), (value as List<*>).toSet())
        }
        onAllNodes(isToggleable())[0].performClick()
        runOnIdle { assertEquals(emptySet<String>(), (value as List<*>).toSet()) }
    }

    @Test
    fun cascaderCheckStrategyChildReportsLeaves() = runComposeUiTest {
        var value by mutableStateOf<Any?>(emptyList<Any>())
        setNContent {
            NCascader(
                options = sampleRegions(),
                value = value,
                onValueChange = { value = it },
                multiple = true,
                checkable = true,
                checkStrategy = NCascaderCheckStrategy.Child,
                placeholder = "Region",
            )
        }
        onNodeWithText("Region").performClick()
        waitUntil { onAllNodesWithText("Zhejiang").fetchSemanticsNodes().isNotEmpty() }
        onAllNodes(isToggleable())[0].performClick()
        runOnIdle {
            assertEquals(setOf("hangzhou", "ningbo"), (value as List<*>).toSet())
        }
    }

    @Test
    fun cascaderFilterableSearchesLeaves() = runComposeUiTest {
        var value by mutableStateOf<Any?>(null)
        setNContent {
            NCascader(
                options = sampleRegions(),
                value = value,
                onValueChange = { value = it },
                filterable = true,
                placeholder = "Region",
            )
        }
        onNode(hasSetTextAction()).performTextInput("Ning")
        waitUntil { onAllNodesWithText("Ningbo").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("Ningbo")[0].performClick()
        runOnIdle { assertEquals("ningbo", value) }
    }

    @Test
    fun cascaderDisabledDoesNotOpen() = runComposeUiTest {
        var value by mutableStateOf<Any?>(null)
        setNContent {
            NCascader(
                options = sampleRegions(),
                value = value,
                onValueChange = { value = it },
                placeholder = "Region",
                disabled = true,
            )
        }
        onNodeWithText("Region").performClick()
        waitForIdle()
        assertTrue(onAllNodesWithText("Zhejiang").fetchSemanticsNodes().isEmpty())
    }

    @Test
    fun treeSelectSelectsNode() = runComposeUiTest {
        var value by mutableStateOf<Any?>(null)
        setNContent {
            NTreeSelect(
                options = sampleNodes(),
                value = value,
                onValueChange = { value = it },
                placeholder = "Node",
                defaultExpandAll = true,
            )
        }
        onNodeWithText("Node").performClick()
        waitUntil { onAllNodesWithText("Child").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("Child")[0].performClick()
        runOnIdle { assertEquals("child", value) }
    }

    @Test
    fun treeSelectMultipleSelectsParent() = runComposeUiTest {
        var value by mutableStateOf<Any?>(emptyList<Any>())
        setNContent {
            NTreeSelect(
                options = sampleNodes(),
                value = value,
                onValueChange = { value = it },
                placeholder = "Node",
                multiple = true,
                defaultExpandAll = true,
            )
        }
        onNodeWithText("Node").performClick()
        waitUntil { onAllNodesWithText("Parent").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("Parent")[0].performClick()
        runOnIdle { assertEquals(listOf<Any>("parent"), value) }
    }

    @Test
    fun treeSelectCheckableCascadesChecks() = runComposeUiTest {
        var value by mutableStateOf<Any?>(emptyList<Any>())
        setNContent {
            NTreeSelect(
                options = sampleNodes(),
                value = value,
                onValueChange = { value = it },
                placeholder = "Node",
                checkable = true,
                defaultExpandAll = true,
            )
        }
        onNodeWithText("Node").performClick()
        waitUntil { onAllNodesWithText("Parent").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("Parent")[0].performClick()
        runOnIdle {
            assertEquals(setOf("parent", "child", "leaf"), (value as List<*>).toSet())
        }
    }

    @Test
    fun treeSelectCheckStrategyChildReportsLeaves() = runComposeUiTest {
        var value by mutableStateOf<Any?>(emptyList<Any>())
        setNContent {
            NTreeSelect(
                options = sampleNodes(),
                value = value,
                onValueChange = { value = it },
                placeholder = "Node",
                checkable = true,
                checkStrategy = NTreeCheckStrategy.Child,
                defaultExpandAll = true,
            )
        }
        onNodeWithText("Node").performClick()
        waitUntil { onAllNodesWithText("Parent").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("Parent")[0].performClick()
        runOnIdle {
            assertEquals(setOf("leaf"), (value as List<*>).toSet())
        }
    }

    @Test
    fun treeSelectFilterableFindsNode() = runComposeUiTest {
        var value by mutableStateOf<Any?>(null)
        setNContent {
            NTreeSelect(
                options = sampleNodes(),
                value = value,
                onValueChange = { value = it },
                placeholder = "Node",
                filterable = true,
            )
        }
        onNode(hasSetTextAction()).performTextInput("Leaf")
        waitUntil { onAllNodesWithText("Leaf").fetchSemanticsNodes().size >= 2 }
        onAllNodesWithText("Leaf")[1].performClick()
        runOnIdle { assertEquals("leaf", value) }
    }

    @Test
    fun treeSelectShowPathRendersAncestors() = runComposeUiTest {
        var value by mutableStateOf<Any?>("child")
        setNContent {
            NTreeSelect(
                options = sampleNodes(),
                value = value,
                onValueChange = { value = it },
                showPath = true,
                defaultExpandAll = true,
            )
        }
        onNodeWithText("Parent / Child").assertExists()
    }

    @Test
    fun treeSelectDisabledDoesNotOpen() = runComposeUiTest {
        var value by mutableStateOf<Any?>(null)
        setNContent {
            NTreeSelect(
                options = sampleNodes(),
                value = value,
                onValueChange = { value = it },
                placeholder = "Node",
                disabled = true,
            )
        }
        onNodeWithText("Node").performClick()
        waitForIdle()
        assertTrue(onAllNodesWithText("Parent").fetchSemanticsNodes().isEmpty())
    }

    @Test
    fun transferRendersPanelsAndTitles() = runComposeUiTest {
        var value by mutableStateOf(listOf("b"))
        setNContent {
            NTransfer(
                options = sampleTransferOptions(),
                value = value,
                onValueChange = { value = it },
                sourceTitle = "Available",
                targetTitle = "Selected",
            )
        }
        onNodeWithText("Available").assertExists()
        onNodeWithText("Selected").assertExists()
        onNodeWithText("Alpha").assertExists()
        onNodeWithText("Beta").assertExists()
        onNodeWithText("Total 2").assertExists()
        onNodeWithText("Selected 1").assertExists()
    }

    @Test
    fun transferMovesItemWhenShowSelectedDisabled() = runComposeUiTest {
        var value by mutableStateOf(emptyList<String>())
        setNContent {
            NTransfer(
                options = sampleTransferOptions(),
                value = value,
                onValueChange = { value = it },
                showSelected = false,
            )
        }
        onNodeWithText("Alpha").performClick()
        runOnIdle { assertEquals(listOf("a"), value) }
    }

    @Test
    fun transferSourceFilterHidesItems() = runComposeUiTest {
        var value by mutableStateOf(emptyList<String>())
        setNContent {
            NTransfer(
                options = sampleTransferOptions(),
                value = value,
                onValueChange = { value = it },
                sourceFilterable = true,
                targetFilterable = false,
            )
        }
        onNode(hasSetTextAction()).performTextInput("Bet")
        waitUntil { onAllNodesWithText("Alpha").fetchSemanticsNodes().isEmpty() }
        onNodeWithText("Beta").assertExists()
    }

    @Test
    fun transferClearEmptiesTarget() = runComposeUiTest {
        var value by mutableStateOf(listOf("a"))
        setNContent {
            NTransfer(
                options = sampleTransferOptions(),
                value = value,
                onValueChange = { value = it },
            )
        }
        onNodeWithText("Clear").performClick()
        runOnIdle { assertEquals(emptyList<String>(), value) }
    }

    @Test
    fun transferDisabledIgnoresClicks() = runComposeUiTest {
        var value by mutableStateOf(emptyList<String>())
        setNContent {
            NTransfer(
                options = sampleTransferOptions(),
                value = value,
                onValueChange = { value = it },
                showSelected = false,
                disabled = true,
            )
        }
        onNodeWithText("Alpha").performClick()
        runOnIdle { assertEquals(emptyList<String>(), value) }
    }

    private fun sampleRegions(): List<NCascaderOption> = listOf(
        NCascaderOption(
            label = "Zhejiang",
            value = "zhejiang",
            children = listOf(
                NCascaderOption("Hangzhou", "hangzhou"),
                NCascaderOption("Ningbo", "ningbo"),
            ),
        ),
        NCascaderOption(
            label = "Jiangsu",
            value = "jiangsu",
            children = listOf(
                NCascaderOption("Nanjing", "nanjing"),
                NCascaderOption("Suzhou", "suzhou"),
            ),
        ),
    )

    private fun sampleNodes(): List<NTreeSelectOption> = listOf(
        NTreeSelectOption(
            label = "Parent",
            key = "parent",
            children = listOf(
                NTreeSelectOption(
                    label = "Child",
                    key = "child",
                    children = listOf(
                        NTreeSelectOption(label = "Leaf", key = "leaf"),
                    ),
                ),
            ),
        ),
    )

    private fun sampleTransferOptions(): List<NTransferOption> = listOf(
        NTransferOption("Alpha", "a"),
        NTransferOption("Beta", "b"),
    )
}
