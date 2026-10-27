package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NCheckbox
import cn.enaium.naiveui.components.NRadio
import cn.enaium.naiveui.components.NRadioButton
import cn.enaium.naiveui.components.NRadioGroup
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

private val songs = listOf(
    "Rock'n'Roll Star",
    "Shakermaker",
    "Live Forever",
    "Up in the Sky",
    "...",
)

@Composable
fun RadioDocs() {
    DocsPage(
        title = "Radio",
        description = "Radios let the user pick exactly one option from a set. Use a radio group to coordinate the selection.",
    ) {
        DemoSection(
            title = "Basic",
            description = "A standalone radio can be controlled through the checked parameter.",
            code = """
var checkedValue by remember { mutableStateOf<String?>(null) }

NRadio(
    checked = checkedValue == "Definitely Maybe",
    onCheckedChange = { checkedValue = "Definitely Maybe" },
    label = "Definitely Maybe",
)
""",
        ) {
            var checkedValue by remember { mutableStateOf<String?>(null) }
            var disabled by remember { mutableStateOf(true) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NRadio(
                        checked = checkedValue == "Definitely Maybe",
                        onCheckedChange = { checkedValue = "Definitely Maybe" },
                        label = "Definitely Maybe",
                    )
                    NRadio(
                        checked = checkedValue == "Be Here Now",
                        onCheckedChange = { checkedValue = "Be Here Now" },
                        label = "Be Here Now",
                    )
                    NRadio(
                        checked = checkedValue == "Be Here Now",
                        onCheckedChange = { checkedValue = "Be Here Now" },
                        disabled = disabled,
                        label = "Disabled",
                    )
                }
                NCheckbox(
                    checked = disabled,
                    onCheckedChange = { disabled = it },
                    label = "Disable the last radio",
                )
            }
        }

        DemoSection(
            title = "Group",
            description = "NRadioGroup keeps the selected value of all nested radios.",
            code = """
var value by remember { mutableStateOf<Any?>(null) }

NRadioGroup(value, { value = it }) {
    NRadio(value = "rock", label = "Rock'n'Roll Star")
    NRadio(value = "live", label = "Live Forever")
}
""",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NRadioGroup(value, { value = it }) {
                    songs.forEach { song ->
                        NRadio(value = song, label = song)
                    }
                }
                NText(if (value == null) "Nothing selected" else "Selected: $value")
            }
        }

        DemoSection(
            title = "Options",
            description = "Build a group from a data list; each radio distinguishes itself with its value.",
        ) {
            var value by remember { mutableStateOf<Any?>("Apple") }
            val options = listOf("Apple", "Banana", "Orange")
            NRadioGroup(value, { value = it }, size = NSize.Small) {
                options.forEach { option ->
                    NRadio(value = option, label = option)
                }
            }
        }

        DemoSection(
            title = "Button group",
            description = "Use NRadioButton for a segmented button style group. Individual buttons can be disabled.",
            code = """
NRadioGroup(value, { value = it }) {
    NRadioButton(value = "a", label = "First")
    NRadioButton(value = "b", label = "Second")
}
""",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            var disableShakermaker by remember { mutableStateOf(false) }
            var disableLiveForever by remember { mutableStateOf(false) }
            NSpace(vertical = true, size = 12.dp) {
                NRadioGroup(value, { value = it }) {
                    songs.forEach { song ->
                        NRadioButton(
                            value = song,
                            label = song,
                            disabled = (song == "Shakermaker" && disableShakermaker) ||
                                (song == "Live Forever" && disableLiveForever),
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    NCheckbox(
                        checked = disableShakermaker,
                        onCheckedChange = { disableShakermaker = it },
                        label = "Disable Shakermaker",
                    )
                    NCheckbox(
                        checked = disableLiveForever,
                        onCheckedChange = { disableLiveForever = it },
                        label = "Disable Live Forever",
                    )
                }
            }
        }

        DemoSection(
            title = "Size",
            description = "Radio groups support small, medium and large sizes.",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            NSpace(vertical = true, size = 12.dp) {
                NRadioGroup(value, { value = it }, size = NSize.Small) {
                    songs.forEach { song -> NRadioButton(value = song, label = song) }
                }
                NRadioGroup(value, { value = it }, size = NSize.Medium) {
                    songs.forEach { song -> NRadioButton(value = song, label = song) }
                }
                NRadioGroup(value, { value = it }, size = NSize.Large) {
                    songs.forEach { song -> NRadioButton(value = song, label = song) }
                }
            }
        }
    }
}
