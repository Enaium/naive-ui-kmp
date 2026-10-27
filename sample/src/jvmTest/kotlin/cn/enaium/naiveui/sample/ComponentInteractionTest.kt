package cn.enaium.naiveui.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.MouseButton
import cn.enaium.naiveui.components.NDatePicker
import cn.enaium.naiveui.components.NForm
import cn.enaium.naiveui.components.NFormItem
import cn.enaium.naiveui.components.NFormRule
import cn.enaium.naiveui.components.NInput
import cn.enaium.naiveui.components.NSelect
import cn.enaium.naiveui.components.NSelectOption
import cn.enaium.naiveui.components.NFormState
import cn.enaium.naiveui.components.NDataTable
import cn.enaium.naiveui.components.NDataTableColumn
import cn.enaium.naiveui.components.NTree
import cn.enaium.naiveui.components.NTreeDropInfo
import cn.enaium.naiveui.components.NTreeOption
import cn.enaium.naiveui.components.nRow
import cn.enaium.naiveui.components.NMessageProvider
import cn.enaium.naiveui.components.NModal
import cn.enaium.naiveui.components.NModalPreset
import cn.enaium.naiveui.components.rememberNMessage
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NTabPane
import cn.enaium.naiveui.components.NTabs
import cn.enaium.naiveui.theme.NConfigProvider
import cn.enaium.naiveui.theme.NGlobalStyle
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalTestApi::class)
class ComponentInteractionTest {
    private fun ComposeUiTest.setTestContent(content: @Composable () -> Unit) {
        setContent {
            NConfigProvider {
                NGlobalStyle { content() }
            }
        }
    }

    @Test
    fun inputAcceptsText() = runComposeUiTest {
        var value by mutableStateOf("")
        setTestContent {
            NInput(value = value, onValueChange = { value = it }, placeholder = "Name")
        }
        onNode(hasSetTextAction()).performTextInput("Hello")
        runOnIdle { assertEquals("Hello", value) }
    }

    @Test
    fun selectOpensAndSelects() = runComposeUiTest {
        var value by mutableStateOf<Any?>(null)
        setTestContent {
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
    fun datePickerSelectsDay() = runComposeUiTest {
        var value by mutableStateOf<LocalDate?>(LocalDate(2024, 1, 1))
        setTestContent {
            NDatePicker(
                value = value,
                onValueChange = { value = it },
                format = "yyyy-MM-dd",
            )
        }
        onNodeWithText("2024-01-01").performClick()
        waitUntil { onAllNodesWithText("15").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("15")[0].performClick()
        runOnIdle { assertEquals(LocalDate(2024, 1, 15), value) }
    }

    @Test
    fun tabsSwitchContent() = runComposeUiTest {
        setTestContent {
            NTabs(
                panes = listOf(
                    NTabPane("one", "One", content = { NText("Content one") }),
                    NTabPane("two", "Two", content = { NText("Content two") }),
                ),
            )
        }
        onNodeWithText("Two").performClick()
        onNodeWithText("Content two").assertExists()
    }

    @Test
    fun treeExpands() = runComposeUiTest {
        setTestContent {
            NTree(
                data = listOf(
                    NTreeOption(
                        label = "Parent",
                        key = "parent",
                        children = listOf(NTreeOption(label = "Child", key = "child")),
                    ),
                ),
                expandOnClick = true,
            )
        }
        onNodeWithText("Parent").performClick()
        waitUntil { onAllNodesWithText("Child").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Child").assertExists()
    }

    @Test
    fun treeDragReportsDrop() = runComposeUiTest {
        var drop: NTreeDropInfo? = null
        setTestContent {
            NTree(
                data = listOf(
                    NTreeOption(label = "Alpha", key = "alpha"),
                    NTreeOption(label = "Beta", key = "beta"),
                ),
                draggable = true,
                onDrop = { drop = it },
            )
        }
        onNodeWithText("Alpha").performMouseInput {
            press(MouseButton.Primary)
            moveBy(Offset(0f, 40f))
            moveBy(Offset(0f, 40f))
            release(MouseButton.Primary)
        }
        waitForIdle()
        runOnIdle { assertNotNull(drop) }
    }

    @Test
    fun dataTableRendersRows() = runComposeUiTest {
        setTestContent {
            NDataTable(
                columns = listOf(
                    NDataTableColumn("name", "Name"),
                    NDataTableColumn("age", "Age"),
                ),
                data = listOf(
                    nRow("key" to "1", "name" to "Alice", "age" to 20),
                    nRow("key" to "2", "name" to "Bob", "age" to 31),
                ),
            )
        }
        onNodeWithText("Name").assertExists()
        onNodeWithText("Alice").assertExists()
        onNodeWithText("Bob").assertExists()
    }

    @Test
    fun formValidatesRequiredField() = runComposeUiTest {
        val state = NFormState()
        var model by mutableStateOf(mapOf<String, Any?>("name" to ""))
        setTestContent {
            NForm(
                model = model,
                rules = mapOf("name" to listOf(NFormRule(required = true, message = "Name is required"))),
                state = state,
            ) {
                NFormItem(path = "name", label = "Name") {
                    NInput(
                        value = model["name"] as String,
                        onValueChange = { model = model + ("name" to it) },
                    )
                }
                NButton(onClick = { state.validate() }) { NText("Validate") }
            }
        }
        onNodeWithText("Validate").performClick()
        waitUntil { onAllNodesWithText("Name is required").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Name is required").assertExists()
    }

    @Test
    fun modalOpensAndCloses() = runComposeUiTest {
        var show by mutableStateOf(false)
        setTestContent {
            Column {
                NButton(onClick = { show = true }) { NText("Open modal") }
                NModal(
                    show = show,
                    onUpdateShow = { show = it },
                    preset = NModalPreset.Dialog,
                    title = "Modal title",
                    positiveText = "OK",
                    onPositiveClick = { show = false },
                ) {
                    NText("Modal body")
                }
            }
        }
        onNodeWithText("Open modal").performClick()
        waitUntil { onAllNodesWithText("Modal title").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Modal body").assertExists()
        onNodeWithText("OK").performClick()
        waitUntil { onAllNodesWithText("Modal title").fetchSemanticsNodes().isEmpty() }
        onAllNodesWithText("Modal title").assertCountEquals(0)
    }

    @Test
    fun messageProviderShowsMessage() = runComposeUiTest {
        setTestContent {
            NMessageProvider {
                val message = rememberNMessage()
                NButton(onClick = { message.success("Saved successfully") }) { NText("Show message") }
            }
        }
        onNodeWithText("Show message").performClick()
        waitUntil { onAllNodesWithText("Saved successfully").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Saved successfully").assertExists()
    }
}
