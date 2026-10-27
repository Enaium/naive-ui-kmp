package cn.enaium.naiveui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class NButtonTest {
    @Test
    fun clickInvokesHandler() = runComposeUiTest {
        var clicks by mutableStateOf(0)
        setNContent {
            NButton(onClick = { clicks++ }) { NText("Save") }
        }
        onNodeWithText("Save").performClick()
        runOnIdle { assertEquals(1, clicks) }
    }

    @Test
    fun disabledButtonDoesNotFire() = runComposeUiTest {
        var clicks by mutableStateOf(0)
        setNContent {
            NButton(onClick = { clicks++ }, disabled = true) { NText("Save") }
        }
        onNodeWithText("Save").assertIsNotEnabled()
        onNodeWithText("Save").performClick()
        runOnIdle { assertEquals(0, clicks) }
    }
}
