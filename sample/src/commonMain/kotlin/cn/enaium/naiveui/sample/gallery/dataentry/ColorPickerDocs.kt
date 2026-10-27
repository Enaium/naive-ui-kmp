package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NColorPicker
import cn.enaium.naiveui.components.NColorPickerAction
import cn.enaium.naiveui.components.NColorPickerMode
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import kotlin.math.roundToInt

@Composable
fun ColorPickerDocs() {
    DocsPage(
        title = "Color Picker",
        description = "Pick a color with the saturation/value palette, hue and alpha sliders, editable value field " +
            "and preset swatches.",
    ) {
        BasicColorPickerDemo()
        SwatchesColorPickerDemo()
        AlphaColorPickerDemo()
        ModesColorPickerDemo()
        SizeColorPickerDemo()
        DisabledColorPickerDemo()
    }
}

private fun colorHex(color: Color): String {
    fun part(value: Float): String =
        (value * 255f).roundToInt().coerceIn(0, 255).toString(16).padStart(2, '0')
    return "#${part(color.red)}${part(color.green)}${part(color.blue)}"
}

@Composable
private fun ColorPreview(color: Color) {
    val theme = LocalNTheme.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(theme.borderRadiusSmall))
                .background(color),
        )
        Spacer(Modifier.width(8.dp))
        NText(colorHex(color), color = theme.colors.text3)
    }
}

@Composable
private fun BasicColorPickerDemo() {
    var color by remember { mutableStateOf(Color(0xFF18A058)) }

    DemoSection(
        title = "Basic",
        description = "The trigger shows the current value over the color fill. The panel supports the palette, " +
            "hue slider and an editable value field.",
        code = "NColorPicker(value = color, onValueChange = { color = it })",
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(180.dp)) {
                NColorPicker(value = color, onValueChange = { color = it })
            }
            ColorPreview(color)
        }
    }
}

@Composable
private fun SwatchesColorPickerDemo() {
    var color by remember { mutableStateOf(Color(0xFF2080F0)) }
    val swatches = remember {
        listOf(
            Color(0xFFFFFFFF),
            Color(0xFF18A058),
            Color(0xFF2080F0),
            Color(0xFFF0A020),
            Color(0xFFD03050),
        )
    }

    DemoSection(
        title = "Swatches",
        description = "Preset colors are rendered below the input field and apply their value directly.",
        code = "NColorPicker(value = color, onValueChange = { color = it }, swatches = swatches)",
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(180.dp)) {
                NColorPicker(
                    value = color,
                    onValueChange = { color = it },
                    swatches = swatches,
                )
            }
            ColorPreview(color)
        }
    }
}

@Composable
private fun AlphaColorPickerDemo() {
    var color by remember { mutableStateOf(Color(0xFF18A058)) }
    var confirmed by remember { mutableStateOf<String?>(null) }

    DemoSection(
        title = "Alpha",
        description = "`showAlpha = false` hides the alpha slider and drops the alpha channel from values and input " +
            "formats. Add confirm and clear buttons with `actions`.",
        code = """NColorPicker(
    value = color,
    onValueChange = { color = it },
    showAlpha = false,
    actions = listOf(NColorPickerAction.Confirm),
    onConfirm = { confirmed = colorHex(it) },
)""",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.width(180.dp)) {
                    NColorPicker(
                        value = color,
                        onValueChange = { color = it },
                        showAlpha = false,
                        actions = listOf(NColorPickerAction.Confirm, NColorPickerAction.Clear),
                        onConfirm = { confirmed = colorHex(it) },
                        onClear = { confirmed = null },
                    )
                }
                ColorPreview(color)
            }
            NText(
                text = confirmed?.let { "Confirmed $it" } ?: "Open the panel and press Confirm.",
                color = LocalNTheme.current.colors.text3,
            )
        }
    }
}

@Composable
private fun ModesColorPickerDemo() {
    var hexColor by remember { mutableStateOf(Color(0xFF2080F0)) }
    var allColor by remember { mutableStateOf(Color(0xFFD03050)) }

    DemoSection(
        title = "Modes",
        description = "Restrict the value formats with `modes`. The active tab controls the editable field and the " +
            "trigger label.",
        code = """NColorPicker(modes = listOf(NColorPickerMode.Hex))
NColorPicker(modes = listOf(Rgb, Hex, Hsl, Hsv))""",
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NText("Hex only", color = LocalNTheme.current.colors.text3)
                Box(Modifier.width(180.dp)) {
                    NColorPicker(
                        value = hexColor,
                        onValueChange = { hexColor = it },
                        modes = listOf(NColorPickerMode.Hex),
                    )
                }
                ColorPreview(hexColor)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NText("All modes", color = LocalNTheme.current.colors.text3)
                Box(Modifier.width(180.dp)) {
                    NColorPicker(
                        value = allColor,
                        onValueChange = { allColor = it },
                        modes = listOf(
                            NColorPickerMode.Rgb,
                            NColorPickerMode.Hex,
                            NColorPickerMode.Hsl,
                            NColorPickerMode.Hsv,
                        ),
                    )
                }
                ColorPreview(allColor)
            }
        }
    }
}

@Composable
private fun SizeColorPickerDemo() {
    var small by remember { mutableStateOf(Color(0xFF18A058)) }
    var medium by remember { mutableStateOf(Color(0xFF2080F0)) }
    var large by remember { mutableStateOf(Color(0xFFF0A020)) }

    DemoSection(
        title = "Size",
        description = "The trigger height follows the picker size; the panel keeps its fixed width.",
        code = "NColorPicker(size = NSize.Small)",
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(150.dp)) {
                NColorPicker(
                    value = small,
                    onValueChange = { small = it },
                    size = NSize.Small,
                )
            }
            Box(Modifier.width(150.dp)) {
                NColorPicker(
                    value = medium,
                    onValueChange = { medium = it },
                    size = NSize.Medium,
                )
            }
            Box(Modifier.width(150.dp)) {
                NColorPicker(
                    value = large,
                    onValueChange = { large = it },
                    size = NSize.Large,
                )
            }
        }
    }
}

@Composable
private fun DisabledColorPickerDemo() {
    DemoSection(
        title = "Disabled",
        description = "A disabled picker cannot open the panel or change its value.",
        code = "NColorPicker(value = color, onValueChange = { }, disabled = true)",
    ) {
        Box(Modifier.width(180.dp)) {
            NColorPicker(
                value = Color(0xFF18A058),
                onValueChange = {},
                disabled = true,
            )
        }
    }
}
