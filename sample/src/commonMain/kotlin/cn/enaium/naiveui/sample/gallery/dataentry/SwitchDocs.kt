package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.components.NSwitch
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize
import kotlinx.coroutines.delay

@Composable
fun SwitchDocs() {
    DocsPage(
        title = "Switch",
        description = "Switches toggle a single boolean value and animate their thumb between the off and on states.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Use checked and onCheckedChange to control the switch.",
            code = """
var checked by remember { mutableStateOf(false) }

NSwitch(checked, { checked = it })
NSwitch(checked, { checked = it }, disabled = true)
""",
        ) {
            var checked by remember { mutableStateOf(false) }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NSwitch(checked, { checked = it })
                NSwitch(checked, { checked = it }, disabled = true)
                NSwitch(checked, { checked = it }, loading = true)
            }
        }

        DemoSection(
            title = "Size",
            description = "Small, medium and large switches match the rest of the form controls.",
        ) {
            var checked by remember { mutableStateOf(true) }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NSwitch(checked, { checked = it }, size = NSize.Small)
                NSwitch(checked, { checked = it }, size = NSize.Medium)
                NSwitch(checked, { checked = it }, size = NSize.Large)
            }
        }

        DemoSection(
            title = "Content",
            description = "Set inlinePrompt together with activeText and inactiveText to place labels inside the rail. Without inlinePrompt the label is rendered next to the switch.",
            code = """
NSwitch(
    checked = checked,
    onCheckedChange = { checked = it },
    activeText = "Checked",
    inactiveText = "Unchecked",
    inlinePrompt = true,
)
""",
        ) {
            var checked by remember { mutableStateOf(false) }
            NSpace(vertical = true, size = 14.dp) {
                NSwitch(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    activeText = "Checked",
                    inactiveText = "Unchecked",
                    inlinePrompt = true,
                )
                NSwitch(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    activeText = "Big wheels keep on turnin'",
                    inactiveText = "Carry me home to see my kin",
                )
            }
        }

        DemoSection(
            title = "Loading",
            description = "While loading, the switch shows a spinner and ignores interaction until the async update finishes.",
            code = """
var pending by remember { mutableStateOf<Boolean?>(null) }
LaunchedEffect(pending) {
    val next = pending ?: return@LaunchedEffect
    delay(1200)
    active = next
    pending = null
    loading = false
}
NSwitch(checked = active, loading = loading, onCheckedChange = { pending = it; loading = true })
""",
        ) {
            var active by remember { mutableStateOf(false) }
            var loading by remember { mutableStateOf(false) }
            var pending by remember { mutableStateOf<Boolean?>(null) }
            LaunchedEffect(pending) {
                val next = pending ?: return@LaunchedEffect
                delay(1200)
                active = next
                pending = null
                loading = false
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NSwitch(checked = active, loading = true)
                NSwitch(
                    checked = active,
                    onCheckedChange = {
                        pending = it
                        loading = true
                    },
                    loading = loading,
                )
                NText(if (loading) "Saving…" else if (active) "On" else "Off")
            }
        }

        DemoSection(
            title = "Shape",
            description = "Set round = false for a square switch.",
        ) {
            var checked by remember { mutableStateOf(true) }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                NSwitch(checked, { checked = it }, round = false)
                NSwitch(checked, { checked = it })
            }
        }

        DemoSection(
            title = "Colors",
            description = "railColor receives the checked state and returns the rail color, matching naive-ui's railStyle.",
            code = """
NSwitch(
    checked, { checked = it },
    railColor = { checked -> if (checked) Color(0xFFD03050) else Color(0xFF2080F0) },
)
""",
        ) {
            var checked by remember { mutableStateOf(false) }
            NSwitch(
                checked,
                { checked = it },
                activeText = "Checked",
                inactiveText = "Unchecked",
                inlinePrompt = true,
                railColor = { on ->
                    if (on) androidx.compose.ui.graphics.Color(0xFFD03050)
                    else androidx.compose.ui.graphics.Color(0xFF2080F0)
                },
            )
        }

        DemoSection(
            title = "Icon",
            description = "Provide icon, checkedIcon or uncheckedIcon to customize the thumb content.",
        ) {
            var active by remember { mutableStateOf(false) }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(32.dp),
            ) {
                NSwitch(
                    checked = active,
                    onCheckedChange = { active = it },
                    icon = { NIcon(NIcons.Sun, size = 14.dp) },
                )
                NSwitch(
                    checked = active,
                    onCheckedChange = { active = it },
                    checkedIcon = { NIcon(NIcons.Checkmark, size = 14.dp) },
                    uncheckedIcon = { NIcon(NIcons.Close, size = 14.dp) },
                    size = NSize.Large,
                )
            }
        }

        DemoSection(
            title = "Customize checked value",
            description = "checkedValue and uncheckedValue change the value emitted by onValueChange.",
            code = """
NSwitch(
    checked, { checked = it },
    checkedValue = "Foo",
    uncheckedValue = "Bar",
    onValueChange = { emitted = it },
)
""",
        ) {
            var checked by remember { mutableStateOf(false) }
            var emitted by remember { mutableStateOf<Any?>(null) }
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NSwitch(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    checkedValue = "Foo",
                    uncheckedValue = "Bar",
                    onValueChange = { emitted = it },
                )
                NText("onValueChange: ${emitted ?: "—"}")
            }
        }

        DemoSection(
            title = "Event",
            description = "onCheckedChange reports every user driven change.",
        ) {
            var active by remember { mutableStateOf(false) }
            var events by remember { mutableStateOf(listOf<String>()) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NSwitch(
                    checked = active,
                    onCheckedChange = {
                        active = it
                        events = (events + "onCheckedChange: $it").takeLast(3)
                    },
                )
                NText(if (events.isEmpty()) "No events yet" else events.joinToString("  •  "))
            }
        }
    }
}
