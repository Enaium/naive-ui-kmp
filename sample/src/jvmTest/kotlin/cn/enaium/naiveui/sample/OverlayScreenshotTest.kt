package cn.enaium.naiveui.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NCascader
import cn.enaium.naiveui.components.NCascaderOption
import cn.enaium.naiveui.components.NDatePicker
import cn.enaium.naiveui.components.NTimePicker
import cn.enaium.naiveui.components.NTreeSelect
import cn.enaium.naiveui.components.NTreeSelectOption
import cn.enaium.naiveui.components.NSelect
import cn.enaium.naiveui.components.NSelectOption
import cn.enaium.naiveui.components.NModal
import cn.enaium.naiveui.components.NTooltip
import cn.enaium.naiveui.components.NDropdown
import cn.enaium.naiveui.components.NDropdownOption
import cn.enaium.naiveui.components.NModalPreset
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.internal.NTrigger
import cn.enaium.naiveui.theme.NConfigProvider
import cn.enaium.naiveui.theme.NLocales
import cn.enaium.naiveui.theme.NGlobalStyle
import java.io.File
import kotlin.test.Test
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image

@OptIn(ExperimentalTestApi::class)
class OverlayScreenshotTest {
    private fun ComposeUiTest.capture(name: String) {
        val roots = onAllNodes(isRoot()).fetchSemanticsNodes()
        roots.forEachIndexed { index, node ->
            val bitmap = onAllNodes(isRoot())[index].captureToImage()
            val image = Image.makeFromBitmap(bitmap.asSkiaBitmap())
            val data = image.encodeToData(EncodedImageFormat.PNG) ?: error("encode failed")
            val dir = File("build/screenshots").apply { mkdirs() }
            val fileName = if (roots.size == 1) name else name.replace(".png", "-root$index.png")
            File(dir, fileName).writeBytes(data.bytes)
        }
    }

    @Test
    fun datePickerPopup() = runComposeUiTest {
        var date by mutableStateOf<LocalDate?>(LocalDate(2024, 5, 20))
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(40.dp)) {
                        NDatePicker(value = date, onValueChange = { date = it })
                    }
                }
            }
        }
        onNodeWithText("2024-05-20").performClick()
        waitForIdle()
        capture("overlay-date-picker.png")
    }

    @Test
    fun timePickerPopup() = runComposeUiTest {
        var time by mutableStateOf<LocalTime?>(LocalTime(14, 30, 0))
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(40.dp)) {
                        NTimePicker(value = time, onValueChange = { time = it })
                    }
                }
            }
        }
        onNodeWithText("14:30:00").performClick()
        waitForIdle()
        capture("overlay-time-picker.png")
    }

    @Test
    fun cascaderPopup() = runComposeUiTest {
        var value by mutableStateOf<Any?>(null)
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(40.dp)) {
                        NCascader(
                            options = listOf(
                                NCascaderOption(
                                    "Zhejiang",
                                    "zhejiang",
                                    children = listOf(
                                        NCascaderOption(
                                            "Hangzhou",
                                            "hangzhou",
                                            children = listOf(
                                                NCascaderOption("West Lake", "west-lake"),
                                                NCascaderOption("Xixi", "xixi"),
                                            ),
                                        ),
                                    ),
                                ),
                                NCascaderOption("Jiangsu", "jiangsu"),
                            ),
                            value = value,
                            onValueChange = { value = it },
                            show = true,
                        )
                    }
                }
            }
        }
        waitForIdle()
        capture("overlay-cascader.png")
    }

    @Test
    fun treeSelectPopup() = runComposeUiTest {
        var value by mutableStateOf<Any?>(null)
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(40.dp)) {
                        NTreeSelect(
                            options = listOf(
                                NTreeSelectOption(
                                    "Parent",
                                    "parent",
                                    children = listOf(
                                        NTreeSelectOption("Child A", "a"),
                                        NTreeSelectOption("Child B", "b"),
                                    ),
                                ),
                            ),
                            value = value,
                            onValueChange = { value = it },
                            defaultExpandAll = true,
                            show = true,
                        )
                    }
                }
            }
        }
        waitForIdle()
        capture("overlay-tree-select.png")
    }

    @Test
    fun dropdownPopup() = runComposeUiTest {
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(40.dp)) {
                        NDropdown(
                            options = listOf(
                                NDropdownOption.Label("edit", "Edit"),
                                NDropdownOption.Label("duplicate", "Duplicate"),
                                NDropdownOption.Divider(),
                                NDropdownOption.Label("delete", "Delete"),
                            ),
                            trigger = NTrigger.Click,
                        ) {
                            NButton { NText("Actions") }
                        }
                    }
                }
            }
        }
        onNodeWithText("Actions").performClick()
        waitForIdle()
        capture("overlay-dropdown.png")
    }

    @Test
    fun languageDropdownScrollbar() = runComposeUiTest {
        setContent { App() }
        onAllNodesWithText("English")[0].performClick()
        val last = NLocales.all.sortedBy { it.displayName.lowercase() }.last()
        waitUntil { onAllNodesWithText(last.displayName).fetchSemanticsNodes().isNotEmpty() }
        capture("language-dropdown-open.png")
        onNodeWithText(last.displayName).performScrollTo()
        onNodeWithText(last.displayName).performMouseInput { moveTo(Offset(10f, 10f)) }
        waitForIdle()
        capture("language-dropdown-end.png")
        onNodeWithText(last.displayName).assertExists()
    }

    @Test
    fun tooltipPopup() = runComposeUiTest {
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(80.dp)) {
                        NTooltip(text = "A helpful hint", trigger = NTrigger.Click) {
                            NButton { NText("Hover me") }
                        }
                    }
                }
            }
        }
        onNodeWithText("Hover me").performClick()
        waitForIdle()
        capture("overlay-tooltip.png")
    }

    @Test
    fun selectPopup() = runComposeUiTest {
        var value by mutableStateOf<Any?>("a")
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(40.dp)) {
                        NSelect(
                            options = listOf(
                                NSelectOption("Option A", "a"),
                                NSelectOption("Option B", "b"),
                                NSelectOption("Option C", "c"),
                            ),
                            value = value,
                            onValueChange = { value = it },
                        )
                    }
                }
            }
        }
        onNodeWithText("Option A").performClick()
        waitForIdle()
        capture("overlay-select.png")
    }

    @Test
    fun dialogModal() = runComposeUiTest {
        var show by mutableStateOf(true)
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    NModal(
                        show = show,
                        onUpdateShow = { show = it },
                        preset = NModalPreset.Dialog,
                        title = "Dialog title",
                        contentText = "This is a dialog modal rendered on a scrim.",
                        positiveText = "Confirm",
                        negativeText = "Cancel",
                        onPositiveClick = { show = false },
                    ) {
                        NButton(onClick = {}) { NText("Custom content") }
                    }
                }
            }
        }
        onNodeWithText("Dialog title").assertExists()
        capture("overlay-modal.png")
    }
}
