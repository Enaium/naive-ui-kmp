package cn.enaium.naiveui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import cn.enaium.naiveui.theme.NStatus
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class DataEntryFormTest {
    @Test
    fun formReportsRequiredError() = runComposeUiTest {
        val state = NFormState()
        var model by mutableStateOf(mapOf<String, Any?>("name" to ""))
        setNContent {
            NForm(
                model = model,
                rules = mapOf(
                    "name" to listOf(NFormRule(required = true, message = "Name is required")),
                ),
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
        runOnIdle {
            assertTrue(state.validated)
            assertFalse(state.validate())
            assertEquals("Name is required", state.errorOf("name"))
        }
    }

    @Test
    fun formPassesWhenModelIsValid() = runComposeUiTest {
        val state = NFormState()
        var model by mutableStateOf(mapOf<String, Any?>("name" to "Alice"))
        var valid: Boolean? = null
        setNContent {
            NForm(
                model = model,
                rules = mapOf("name" to listOf(NFormRule(required = true, message = "Required"))),
                state = state,
            ) {
                NFormItem(path = "name", label = "Name") {
                    NInput(value = model["name"] as String, onValueChange = { model = model + ("name" to it) })
                }
                NButton(onClick = { state.validate(callback = { valid = it }) }) { NText("Validate") }
            }
        }
        onNodeWithText("Validate").performClick()
        runOnIdle {
            assertEquals(true, valid)
            assertTrue(state.errors.isEmpty())
        }
    }

    @Test
    fun formValidatesEmailTypeRule() = runComposeUiTest {
        val state = NFormState()
        var model by mutableStateOf(mapOf<String, Any?>("email" to "not-an-email"))
        setNContent {
            NForm(
                model = model,
                rules = mapOf(
                    "email" to listOf(
                        NFormRule(type = NFormRuleType.Email, message = "Invalid email"),
                    ),
                ),
                state = state,
            ) {
                NFormItem(path = "email", label = "Email") {
                    NInput(value = model["email"] as String, onValueChange = { model = model + ("email" to it) })
                }
                NButton(onClick = { state.validate() }) { NText("Validate") }
            }
        }
        onNodeWithText("Validate").performClick()
        waitUntil { onAllNodesWithText("Invalid email").fetchSemanticsNodes().isNotEmpty() }
        runOnIdle { assertEquals("Invalid email", state.errorOf("email")) }
    }

    @Test
    fun formValidatesMinLength() = runComposeUiTest {
        val state = NFormState()
        var model by mutableStateOf(mapOf<String, Any?>("name" to "ab"))
        setNContent {
            NForm(
                model = model,
                rules = mapOf(
                    "name" to listOf(NFormRule(min = 3, message = "Too short")),
                ),
                state = state,
            ) {
                NFormItem(path = "name", label = "Name") {
                    NInput(value = model["name"] as String, onValueChange = { model = model + ("name" to it) })
                }
                NButton(onClick = { state.validate() }) { NText("Validate") }
            }
        }
        onNodeWithText("Validate").performClick()
        waitUntil { onAllNodesWithText("Too short").fetchSemanticsNodes().isNotEmpty() }
        runOnIdle { assertEquals("Too short", state.errorOf("name")) }
    }

    @Test
    fun formWarningRuleDoesNotRaiseError() = runComposeUiTest {
        val state = NFormState()
        var model by mutableStateOf(mapOf<String, Any?>("name" to ""))
        setNContent {
            NForm(
                model = model,
                rules = mapOf(
                    "name" to listOf(
                        NFormRule(
                            required = true,
                            message = "Missing name",
                            level = NFormRuleLevel.Warning,
                        ),
                    ),
                ),
                state = state,
            ) {
                NFormItem(path = "name", label = "Name") {
                    NInput(value = model["name"] as String, onValueChange = { model = model + ("name" to it) })
                }
                NButton(onClick = { state.validate() }) { NText("Validate") }
            }
        }
        onNodeWithText("Validate").performClick()
        waitUntil { onAllNodesWithText("Missing name").fetchSemanticsNodes().isNotEmpty() }
        runOnIdle {
            assertTrue(state.errors.isEmpty())
            assertEquals("Missing name", state.warningOf("name"))
            assertEquals(NStatus.Warning, state.statusOf("name"))
        }
    }

    @Test
    fun formValidatePathOnlyTouchesSelectedPath() = runComposeUiTest {
        val state = NFormState()
        var model by mutableStateOf(mapOf<String, Any?>("a" to "", "b" to ""))
        setNContent {
            NForm(
                model = model,
                rules = mapOf(
                    "a" to listOf(NFormRule(required = true, message = "A required")),
                    "b" to listOf(NFormRule(required = true, message = "B required")),
                ),
                state = state,
            ) {
                NFormItem(path = "a", label = "A") {
                    NInput(value = model["a"] as String, onValueChange = { model = model + ("a" to it) })
                }
                NFormItem(path = "b", label = "B") {
                    NInput(value = model["b"] as String, onValueChange = { model = model + ("b" to it) })
                }
            }
        }
        runOnIdle {
            assertFalse(state.validatePath("a"))
            assertEquals("A required", state.errorOf("a"))
            assertEquals(null, state.errorOf("b"))
        }
    }

    @Test
    fun formItemRuleOverridesFormRules() = runComposeUiTest {
        val state = NFormState()
        var model by mutableStateOf(mapOf<String, Any?>("age" to ""))
        setNContent {
            NForm(model = model, state = state) {
                NFormItem(
                    path = "age",
                    label = "Age",
                    rule = NFormRule(required = true, message = "Age required"),
                ) {
                    NInput(value = model["age"] as String, onValueChange = { model = model + ("age" to it) })
                }
                NButton(onClick = { state.validate() }) { NText("Validate") }
            }
        }
        onNodeWithText("Validate").performClick()
        waitUntil { onAllNodesWithText("Age required").fetchSemanticsNodes().isNotEmpty() }
        runOnIdle { assertEquals("Age required", state.errorOf("age")) }
    }

    @Test
    fun formRestoreValidationClearsErrors() = runComposeUiTest {
        val state = NFormState()
        var model by mutableStateOf(mapOf<String, Any?>("name" to ""))
        setNContent {
            NForm(
                model = model,
                rules = mapOf("name" to listOf(NFormRule(required = true, message = "Name is required"))),
                state = state,
            ) {
                NFormItem(path = "name", label = "Name") {
                    NInput(value = model["name"] as String, onValueChange = { model = model + ("name" to it) })
                }
                NButton(onClick = { state.validate() }) { NText("Validate") }
            }
        }
        onNodeWithText("Validate").performClick()
        waitUntil { onAllNodesWithText("Name is required").fetchSemanticsNodes().isNotEmpty() }
        runOnIdle { state.restoreValidation() }
        waitUntil { onAllNodesWithText("Name is required").fetchSemanticsNodes().isEmpty() }
        runOnIdle {
            assertTrue(state.errors.isEmpty())
            assertFalse(state.validated)
        }
    }

    @Test
    fun formAsyncValidatorRuns() = runComposeUiTest {
        val state = NFormState()
        var model by mutableStateOf(mapOf<String, Any?>("name" to "bad"))
        setNContent {
            NForm(
                model = model,
                rules = mapOf(
                    "name" to listOf(
                        NFormRule(
                            asyncValidator = { it == "ok" },
                            message = "Async failed",
                        ),
                    ),
                ),
                state = state,
            ) {
                NFormItem(path = "name", label = "Name") {
                    NInput(value = model["name"] as String, onValueChange = { model = model + ("name" to it) })
                }
            }
        }
        runOnIdle { runBlocking { assertFalse(state.validateAsync()) } }
        runOnIdle { model = model + ("name" to "ok") }
        waitForIdle()
        runOnIdle { runBlocking { assertTrue(state.validateAsync()) } }
        runOnIdle { assertTrue(state.errors.isEmpty()) }
    }
}
