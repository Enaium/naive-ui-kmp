package cn.enaium.naiveui.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.MouseButton
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NCascader
import cn.enaium.naiveui.components.NCascaderOption
import cn.enaium.naiveui.components.NDatePicker
import cn.enaium.naiveui.components.NDateTimeRangePicker
import cn.enaium.naiveui.components.rememberNDatePickerController
import cn.enaium.naiveui.components.NDrawer
import cn.enaium.naiveui.components.NTimePicker
import cn.enaium.naiveui.components.NTreeSelect
import cn.enaium.naiveui.components.NTreeSelectOption
import cn.enaium.naiveui.components.NSelect
import cn.enaium.naiveui.components.NSelectOption
import cn.enaium.naiveui.components.NModal
import cn.enaium.naiveui.components.NLayout
import cn.enaium.naiveui.components.NLayoutSider
import cn.enaium.naiveui.components.NLayoutCollapseMode
import cn.enaium.naiveui.components.NLayoutSiderTrigger
import cn.enaium.naiveui.components.NMenu
import cn.enaium.naiveui.components.NMenuOption
import cn.enaium.naiveui.components.NTooltip
import cn.enaium.naiveui.components.NDropdown
import cn.enaium.naiveui.components.NDropdownOption
import cn.enaium.naiveui.components.NModalPreset
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NButtonGroup
import cn.enaium.naiveui.components.NButtonType
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NFloatButton
import cn.enaium.naiveui.components.NBadge
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.components.NSlider
import cn.enaium.naiveui.sample.gallery.feedback.DrawerDocs
import cn.enaium.naiveui.sample.gallery.display.DtDemoBorder
import cn.enaium.naiveui.sample.gallery.display.DtDemoColumnDraggable
import cn.enaium.naiveui.sample.gallery.display.DtDemoFixedHeaderColumn
import cn.enaium.naiveui.sample.gallery.display.DtDemoMergeCell
import cn.enaium.naiveui.internal.NTrigger
import cn.enaium.naiveui.theme.NConfigProvider
import cn.enaium.naiveui.theme.NLocales
import cn.enaium.naiveui.theme.NGlobalStyle
import java.io.File
import kotlin.test.Test
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
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
    fun dateTimePopup() = runComposeUiTest {
        var value by mutableStateOf<LocalDateTime?>(LocalDateTime(2007, 6, 30, 0, 41, 0))
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(40.dp)) {
                        val controller = rememberNDatePickerController()
                        androidx.compose.runtime.LaunchedEffect(Unit) { controller.focus() }
                        NDatePicker(
                            value = value,
                            onValueChange = { value = it },
                            controller = controller,
                        )
                    }
                }
            }
        }
        waitForIdle()
        capture("overlay-date-time.png")
        onNodeWithText("00:41:00").performClick()
        waitUntil { onAllNodesWithText("Now").fetchSemanticsNodes().isNotEmpty() }
        capture("overlay-date-time-time-picker.png")
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
    fun dateTimeRangePopup() = runComposeUiTest {
        var value by mutableStateOf(
            LocalDateTime(2007, 6, 30, 0, 41) to LocalDateTime(2026, 10, 27, 23, 47, 50),
        )
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(20.dp)) {
                        val controller = rememberNDatePickerController()
                        androidx.compose.runtime.LaunchedEffect(Unit) { controller.focus() }
                        NDateTimeRangePicker(
                            value = value,
                            onValueChange = { value = it ?: value },
                            controller = controller,
                        )
                    }
                }
            }
        }
        waitForIdle()
        capture("date-time-range.png")
        onNodeWithText("00:41:00").performClick()
        waitForIdle()
        capture("date-time-range-after-time-click.png")
        waitUntil { onAllNodesWithText("Now").fetchSemanticsNodes().isNotEmpty() }
        onNodeWithText("Now").assertExists()
        capture("date-time-range-time-picker.png")
    }

    @Test
    fun dataTableMergeCells() = runComposeUiTest {
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(20.dp)) { DtDemoMergeCell() }
                }
            }
        }
        waitForIdle()
        capture("data-table-merge.png")
    }

    @Test
    fun floatButtonBadgeOverflow() = runComposeUiTest {
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(40.dp)) {
                        NBadge(value = 9, offset = androidx.compose.ui.unit.DpOffset(6.dp, -8.dp)) {
                            NFloatButton(size = 64.dp) {
                                NIcon(NIcons.Add, size = 24.dp)
                            }
                        }
                    }
                }
            }
        }
        waitForIdle()
        capture("float-button-badge.png")
    }

    @Test
    fun badgeDotCorner() = runComposeUiTest {
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(40.dp)) {
                        NBadge(dot = true) {
                            Box(
                                Modifier
                                    .size(48.dp)
                                    .background(Color(0xFFCCCCCC)),
                            )
                        }
                    }
                }
            }
        }
        waitForIdle()
        capture("badge-dot-corner.png")
    }

    @Test
    fun selectKeepsFocusBorderAfterSelection() = runComposeUiTest {
        var value by mutableStateOf<Any?>(null)
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(40.dp)) {
                        NSelect(
                            options = listOf(
                                NSelectOption("Option A", "a"),
                                NSelectOption("Option B", "b"),
                            ),
                            value = value,
                            onValueChange = { value = it },
                            placeholder = "Pick one",
                        )
                    }
                }
            }
        }
        onNodeWithText("Pick one").performClick()
        waitUntil { onAllNodesWithText("Option A").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("Option A")[0].performClick()
        waitForIdle()
        capture("select-focus-border.png")
        onRoot().performMouseInput { moveTo(Offset(500f, 500f)); press(MouseButton.Primary); release(MouseButton.Primary) }
        waitForIdle()
        capture("select-blur-border.png")
    }

    @Test
    fun secondaryPressed() = runComposeUiTest {
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(40.dp)) {
                        NButton(
                            secondary = true,
                            modifier = Modifier.testTag("secondary"),
                        ) { NText("Secondary") }
                        NButton(
                            tertiary = true,
                            modifier = Modifier.testTag("tertiary"),
                        ) { NText("Tertiary") }
                        NButton(
                            quaternary = true,
                            modifier = Modifier.testTag("quaternary"),
                        ) { NText("Quaternary") }
                    }
                }
            }
        }
        onNodeWithTag("secondary").performTouchInput { down(center) }
        mainClock.advanceTimeBy(200)
        waitForIdle()
        capture("button-secondary-pressed.png")
    }

    @Test
    fun buttonGroups() = runComposeUiTest {
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(40.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        NButtonGroup(vertical = true, roundStart = true) {
                            NButton(icon = { NIcon(NIcons.ArrowUp) }) { NText("Live a") }
                            NButton(ghost = true, icon = { NIcon(NIcons.ArrowUp) }) { NText("Sufficient") }
                            NButton(icon = { NIcon(NIcons.ArrowUp) }) { NText("Life") }
                        }
                        NButtonGroup(roundEnd = true) {
                            NButton { NText("Eat") }
                            NButton { NText("One More") }
                            NButton(modifier = Modifier.testTag("apple")) { NText("Apple") }
                        }
                    }
                }
            }
        }
        waitForIdle()
        capture("button-groups.png")
        mainClock.autoAdvance = false
        onNodeWithTag("apple").performTouchInput { down(center) }
        mainClock.advanceTimeByFrame()
        mainClock.advanceTimeBy(180)
        capture("button-group-wave.png")
        onNodeWithTag("apple").performTouchInput { up() }
        mainClock.advanceTimeBy(800)
        capture("button-group-focused.png")
    }

    @Test
    fun dashedButtons() = runComposeUiTest {
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(40.dp)) {
                        NButton(dashed = true) { NText("Default") }
                        NButton(type = NButtonType.Primary, dashed = true) { NText("Primary") }
                        NButton(type = NButtonType.Info, dashed = true) { NText("Info") }
                    }
                }
            }
        }
        waitForIdle()
        capture("button-dashed.png")
    }

    @Test
    fun menuCollapsedSider() = runComposeUiTest {
        var collapsed by mutableStateOf(true)
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    NLayout(
                        modifier = Modifier.height(320.dp),
                        hasSider = true,
                    ) {
                        NLayoutSider(
                            bordered = true,
                            collapseMode = NLayoutCollapseMode.Width,
                            collapsedWidth = 64.dp,
                            width = 240.dp,
                            collapsed = collapsed,
                            onCollapsedChange = { collapsed = it },
                            showTrigger = true,
                            triggerStyle = NLayoutSiderTrigger.ArrowCircle,
                        ) {
                            NMenu(
                                options = listOf(
                                    NMenuOption("a", "Hear the Wind Sing", icon = cn.enaium.naiveui.icons.NIcons.File),
                                    NMenuOption(
                                        key = "b",
                                        label = "Dance Dance Dance",
                                        icon = cn.enaium.naiveui.icons.NIcons.File,
                                        children = listOf(NMenuOption("c", "Child")),
                                    ),
                                ),
                                value = null,
                                onValueChange = {},
                                collapsed = collapsed,
                                collapsedWidth = 64.dp,
                                width = 240.dp,
                            )
                        }
                        Column(Modifier.padding(20.dp)) { NText("Content") }
                    }
                }
            }
        }
        waitForIdle()
        capture("menu-collapsed-sider.png")
    }

    @Test
    fun menuChevron() = runComposeUiTest {
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(40.dp)) {
                        NMenu(
                            options = listOf(
                                NMenuOption(
                                    key = "parent",
                                    label = "Parent",
                                    children = listOf(NMenuOption("child", "Child")),
                                ),
                            ),
                            value = null,
                            onValueChange = {},
                        )
                    }
                }
            }
        }
        waitForIdle()
        capture("menu-chevron.png")
    }

    @Test
    fun primaryButtonFocusBorder() = runComposeUiTest {
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(40.dp)) {
                        NButton(type = NButtonType.Primary, onClick = {}) {
                            NText("Primary")
                        }
                    }
                }
            }
        }
        onNodeWithText("Primary").performClick()
        onNodeWithText("Primary").assertIsFocused()
        mainClock.advanceTimeBy(400)
        waitForIdle()
        capture("button-primary-focus.png")
    }

    @Test
    fun floatButtonHover() = runComposeUiTest {
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(40.dp)) {
                        NFloatButton(
                            modifier = Modifier.testTag("float-button"),
                            onClick = {},
                        ) { NText("+") }
                    }
                }
            }
        }
        onNodeWithTag("float-button").performMouseInput { moveTo(center) }
        waitForIdle()
        capture("float-button-hover.png")
    }

    @Test
    fun sliderHoverTooltip() = runComposeUiTest {
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(40.dp)) {
                        NSlider(
                            value = 50f,
                            onValueChange = {},
                            modifier = Modifier.testTag("hover-slider"),
                        )
                    }
                }
            }
        }
        onNodeWithTag("hover-slider").performMouseInput {
            moveTo(center)
        }
        waitForIdle()
        capture("slider-hover-tooltip.png")
    }

    @Test
    fun sliderAlwaysShowTooltip() = runComposeUiTest {
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(40.dp)) {
                        NSlider(value = 50f, onValueChange = {}, showTooltip = true)
                    }
                }
            }
        }
        waitForIdle()
        capture("slider-always-tooltip.png")
    }

    @Test
    fun dataTableBorders() = runComposeUiTest {
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(20.dp)) { DtDemoBorder() }
                }
            }
        }
        waitForIdle()
        capture("data-table-borders.png")
    }

    @Test
    fun dataTableColumnDraggable() = runComposeUiTest {
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(20.dp)) { DtDemoColumnDraggable() }
                }
            }
        }
        waitForIdle()
        capture("data-table-column-draggable.png")
    }

    @Test
    fun dataTableFixedColumnShadow() = runComposeUiTest {
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    Column(Modifier.padding(20.dp)) { DtDemoFixedHeaderColumn() }
                }
            }
        }
        onNodeWithText("Row").performTouchInput {
            swipeLeft()
        }
        waitForIdle()
        capture("data-table-fixed-column-shadow.png")
    }

    @Test
    fun drawerSurface() = runComposeUiTest {
        setContent {
            NConfigProvider {
                NGlobalStyle {
                    NDrawer(
                        show = true,
                        onUpdateShow = {},
                        title = "Stoner",
                        width = 502.dp,
                    ) {
                        NText(
                            "Stoner is a 1965 novel by the American writer John Williams. " +
                                "It was reissued in 2003 and again in 2006.",
                        )
                    }
                }
            }
        }
        waitForIdle()
        capture("drawer-surface.png")
    }

    @Test
    fun drawerDocsBasic() = runComposeUiTest {
        setContent {
            NConfigProvider {
                NGlobalStyle { DrawerDocs() }
            }
        }
        onAllNodesWithText("Right")[0].performClick()
        waitForIdle()
        capture("drawer-docs-basic.png")
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
