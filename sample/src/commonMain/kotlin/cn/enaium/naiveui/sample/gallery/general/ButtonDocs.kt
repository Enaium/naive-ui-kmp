package cn.enaium.naiveui.sample.gallery.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NPopover
import cn.enaium.naiveui.components.NButtonGroup
import cn.enaium.naiveui.components.NButtonType
import cn.enaium.naiveui.components.NIconPlacement
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NAlign
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val nButtonTypes = listOf(
    NButtonType.Default,
    NButtonType.Tertiary,
    NButtonType.Primary,
    NButtonType.Info,
    NButtonType.Success,
    NButtonType.Warning,
    NButtonType.Error,
)

private fun NButtonType.label(): String = when (this) {
    NButtonType.Default -> "Default"
    NButtonType.Tertiary -> "Tertiary"
    NButtonType.Primary -> "Primary"
    NButtonType.Info -> "Info"
    NButtonType.Success -> "Success"
    NButtonType.Warning -> "Warning"
    NButtonType.Error -> "Error"
}

@Composable
fun ButtonDocs() {
    DocsPage(
        title = "Button",
        description = "Button is used to trigger some actions. It comes in many types, variants, sizes and shapes.",
    ) {
        DemoSection(
            title = "Basic",
            description = "There are `default`, `primary`, `tertiary`, `info`, `success`, `warning` and `error` button types.",
        ) {
            NSpace(size = 8.dp, wrap = true) {
                nButtonTypes.forEach { type ->
                    NButton(type = type) { NText(type.label()) }
                }
            }
        }

        DemoSection(
            title = "Secondary",
            description = "Secondary buttons use a translucent background. Add `round` or `circle` for pill and icon-only shapes.",
        ) {
            NSpace(size = 8.dp, wrap = true) {
                nButtonTypes.forEach { type ->
                    NButton(type = type, secondary = true, strong = true) { NText(type.label()) }
                }
                nButtonTypes.filter { it != NButtonType.Tertiary }.forEach { type ->
                    NButton(type = type, secondary = true, strong = true, round = true) {
                        NText(type.label())
                    }
                }
            }
        }

        DemoSection(
            title = "Tertiary",
            description = "Tertiary buttons use a filled, low-contrast background.",
        ) {
            NSpace(size = 8.dp, wrap = true) {
                nButtonTypes.filter { it != NButtonType.Tertiary }.forEach { type ->
                    NButton(type = type, tertiary = true) { NText(type.label()) }
                    NButton(type = type, tertiary = true, round = true) { NText(type.label()) }
                }
            }
        }

        DemoSection(
            title = "Quaternary",
            description = "Quaternary buttons are transparent until hovered.",
        ) {
            NSpace(size = 8.dp, wrap = true) {
                nButtonTypes.filter { it != NButtonType.Tertiary }.forEach { type ->
                    NButton(type = type, quaternary = true) { NText(type.label()) }
                    NButton(type = type, quaternary = true, round = true) { NText(type.label()) }
                }
            }
        }

        DemoSection(
            title = "Dashed",
            description = "Use `dashed` to show a dashed button border.",
        ) {
            NSpace(size = 8.dp, wrap = true) {
                nButtonTypes.filter { it != NButtonType.Tertiary }.forEach { type ->
                    NButton(type = type, dashed = true) { NText(type.label()) }
                }
            }
        }

        DemoSection(
            title = "Size",
            description = "Buttons can be `tiny`, `small`, `medium` and `large`.",
        ) {
            NSpace(size = 8.dp, align = NAlign.Center, wrap = true) {
                NButton(size = NSize.Tiny) { NText("Tiny") }
                NButton(size = NSize.Small) { NText("Small") }
                NButton(size = NSize.Medium) { NText("Medium") }
                NButton(size = NSize.Large) { NText("Large") }
            }
        }

        DemoSection(
            title = "Text",
            description = "Text buttons look like plain text until hovered.",
        ) {
            NButton(text = true, icon = { NIcon(NIcons.Forward) }) {
                NText("The Engine is Still Spitting Smoke")
            }
        }

        DemoSection(
            title = "Tag",
            description = "Render the button as a different element with the `tag` prop. A text button is the natural stand-in for a link.",
        ) {
            NButton(text = true, type = NButtonType.Primary) { NText("Anyway.News") }
        }

        DemoSection(
            title = "Disabled",
            description = "Disabled buttons ignore clicks; pass `tag` when a disabled button must still receive pointer events.",
        ) {
            NSpace(size = 8.dp, align = NAlign.Center, wrap = true) {
                NButton(disabled = true) { NText("Disabled") }
                NButton(disabled = true, type = NButtonType.Primary) { NText("Primary Disabled") }
            }
        }

        DemoSection(
            title = "Icon",
            description = "Icons can be placed on either side using the `icon` slot and `iconPlacement`.",
        ) {
            NSpace(size = 8.dp, wrap = true) {
                NButton(icon = { NIcon(NIcons.Add) }) { NText("+100$") }
                NButton(iconPlacement = NIconPlacement.Right, icon = { NIcon(NIcons.Add) }) {
                    NText("+100$")
                }
            }
        }

        DemoSection(
            title = "Events",
            description = "Handle clicks with `onClick`. The last action is reported below.",
        ) {
            var message by remember { mutableStateOf("No clicks yet") }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NButton(onClick = { message = "Button Clicked" }) { NText("Click Me") }
                NText(message, size = 13.sp)
            }
        }

        DemoSection(
            title = "Shape",
            description = "Buttons can be `circle`, `round` or rectangular.",
        ) {
            NSpace(size = 8.dp, align = NAlign.Center) {
                NButton(circle = true, icon = { NIcon(NIcons.Add) })
                NButton(round = true) { NText("Round") }
                NButton { NText("Rect") }
            }
        }

        DemoSection(
            title = "Ghost",
            description = "Ghost buttons have transparent backgrounds.",
        ) {
            NSpace(size = 8.dp, wrap = true) {
                nButtonTypes.filter { it != NButtonType.Tertiary }.forEach { type ->
                    NButton(type = type, ghost = true) { NText(type.label()) }
                }
            }
        }

        DemoSection(
            title = "Loading",
            description = "A loading button shows a spinner and ignores clicks until it finishes.",
        ) {
            val scope = rememberCoroutineScope()
            var loading by remember { mutableStateOf(false) }
            NButton(
                loading = loading,
                icon = { NIcon(NIcons.Time) },
                onClick = {
                    loading = true
                    scope.launch {
                        delay(1500)
                        loading = false
                    }
                },
            ) {
                NText("Click Me")
            }
        }

        DemoSection(
            title = "Custom color",
            description = "Pass `color` to use a custom background and `textColor` for a custom foreground.",
        ) {
            NSpace(size = 8.dp, align = NAlign.Center, wrap = true) {
                NButton(color = Color(0xFF8A2BE2), textColor = Color.White) { NText("#8a2be2") }
                NButton(color = Color(0xFFFF69B4), textColor = Color.White) { NText("#ff69b4") }
                NButton(color = Color(0xFF8A2BE2), ghost = true) { NText("#8a2be2") }
                NButton(color = Color(0xFFFF69B4), text = true) { NText("#ff69b4") }
            }
        }

        DemoSection(
            title = "Button group",
            description = "Group related actions with `NButtonGroup`. A group can be vertical and share a size.",
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                NButtonGroup(vertical = true) {
                    NButton(icon = { NIcon(NIcons.ArrowUp) }) { NText("Live a") }
                    NButton(ghost = true, icon = { NIcon(NIcons.ArrowUp) }) { NText("Sufficient") }
                    NButton(icon = { NIcon(NIcons.ArrowUp) }) { NText("Life") }
                }
                NButtonGroup(vertical = true, size = NSize.Large) {
                    NButton { NText("With") }
                    NButton { NText("Enough") }
                    NButton(ghost = true, round = true) { NText("Happiness") }
                }
            }
        }

        DemoSection(
            title = "Icon-only button",
            description = "Use a text button with an icon and a larger font size when you only need an icon.",
        ) {
            NButton(text = true) { NIcon(NIcons.More, size = 24.dp) }
        }

        DemoSection(
            title = "Popover",
            description = "A disabled native button does not emit hover events, so the popover listens on the wrapper. " +
                "Hover the disabled button to show the popover.",
        ) {
            NPopover(content = { NText("Lpsum") }) {
                NButton(disabled = true) { NText("Disabled button as div") }
            }
        }
    }
}
