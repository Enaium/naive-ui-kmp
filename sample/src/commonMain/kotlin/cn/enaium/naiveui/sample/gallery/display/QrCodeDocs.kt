package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NButtonType
import cn.enaium.naiveui.components.NInput
import cn.enaium.naiveui.components.NQrCode
import cn.enaium.naiveui.components.NQrCodeBitmap
import cn.enaium.naiveui.components.NQrCodeType
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

@Composable
fun QrCodeDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "QR Code",
        description = "Encode text into a scannable QR code drawn with Compose.",
    ) {
        DemoSection(
            title = "Basic",
            description = "The value reacts to the input below; the encoded matrix is generated on change.",
            code = "NQrCode(value = text)",
        ) {
            var text by remember { mutableStateOf("The rain dampened the sky") }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NQrCode(value = text)
                NInput(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.width(280.dp),
                    placeholder = "QR value",
                )
            }
        }
        DemoSection(
            title = "Icon",
            description = "An icon is drawn in a rounded badge; use errorCorrectionLevel H to keep scanning reliable.",
            code = "NQrCode(value = url, iconSrc = logo, errorCorrectionLevel = \"H\")",
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NQrCode(
                    value = "https://www.naiveui.com/",
                    iconSrc = "https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg",
                    errorCorrectionLevel = "H",
                )
                NQrCode(
                    value = "https://www.naiveui.com/",
                    iconSrc = "https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg",
                    iconBackgroundColor = Color(0xFF333333),
                    errorCorrectionLevel = "H",
                )
                NQrCode(
                    value = "https://www.naiveui.com/",
                    iconSrc = "https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg",
                    iconSize = 32.dp,
                    errorCorrectionLevel = "H",
                )
            }
        }
        DemoSection(
            title = "Size",
            description = "The size includes the quiet-zone padding.",
        ) {
            var size by remember { mutableStateOf(110) }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NButton(onClick = { size = (size - 10).coerceAtLeast(50) }) {
                        NText("Minus 10px")
                    }
                    NButton(onClick = { size = (size + 10).coerceAtMost(220) }) {
                        NText("Add 10px")
                    }
                }
                NQrCode(value = "The rain dampened the sky", size = size.dp)
            }
        }
        DemoSection(
            title = "Color",
            description = "Customize the modules and the background.",
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NQrCode(value = "https://www.naiveui.com/", color = Color(0xFF18A058))
                NQrCode(
                    value = "https://www.naiveui.com/",
                    color = Color(0xFF409EFF),
                    backgroundColor = Color(0xFFF5F5F5),
                )
                NQrCode(value = "https://www.naiveui.com/", color = Color(0xFF225A95))
            }
        }
        DemoSection(
            title = "Error correction",
            description = "Higher levels survive more damage at the cost of density.",
            code = "NQrCode(value = text, errorCorrectionLevel = level)",
        ) {
            var level by remember { mutableStateOf("M") }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NQrCode(
                    value = "Like a humorous magician, he skillfully turns tedious information into a mysterious QR code",
                    errorCorrectionLevel = level,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("L", "M", "Q", "H").forEach { option ->
                        NButton(
                            onClick = { level = option },
                            type = if (level == option) NButtonType.Primary else NButtonType.Default,
                        ) {
                            NText(option)
                        }
                    }
                }
            }
        }
        DemoSection(
            title = "Download",
            description = "NQrCodeBitmap renders the encoding to an ImageBitmap that host platforms can save.",
            code = "val bitmap = NQrCodeBitmap(text, pixels = 320)",
        ) {
            var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NQrCode(value = "https://www.naiveui.com/")
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        NButton(onClick = {
                            bitmap = NQrCodeBitmap(
                                value = "https://www.naiveui.com/",
                                pixels = 320,
                            )
                        }) {
                            NText("Render image")
                        }
                        NText(
                            text = if (bitmap == null) "No bitmap captured yet" else "Captured 320 × 320 image",
                            size = theme.typography.fontSize.small,
                            color = theme.colors.text3,
                        )
                    }
                }
                val captured = bitmap
                if (captured != null) {
                    Image(
                        bitmap = captured,
                        contentDescription = "Rendered QR code",
                        modifier = Modifier.size(160.dp),
                    )
                }
            }
        }
        DemoSection(
            title = "Customize render type",
            description = "Canvas draws every module; Svg merges horizontal runs into one path.",
            code = "NQrCode(value = text, type = NQrCodeType.Svg)",
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                NQrCode(value = "The rain dampened the sky", type = NQrCodeType.Canvas)
                NQrCode(value = "The rain dampened the sky", type = NQrCodeType.Svg)
            }
        }
    }
}
