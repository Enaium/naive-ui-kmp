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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.components.NRate
import cn.enaium.naiveui.components.NRatePrecise
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

@Composable
fun RateDocs() {
    DocsPage(
        title = "Rate",
        description = "Rate renders a row of stars and reports the selected rating. Hovering previews the value before it is committed.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Click a star to set the rating.",
            code = """
var value by remember { mutableStateOf(0) }

NRate(value, { value = it })
""",
        ) {
            var value by remember { mutableStateOf(0) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NRate(value, { value = it })
                NText(if (value == 0) "Not rated" else "Rating: $value")
            }
        }

        DemoSection(
            title = "Size",
            description = "Stars come in small, medium and large sizes.",
        ) {
            var small by remember { mutableStateOf(2) }
            var medium by remember { mutableStateOf(3) }
            var large by remember { mutableStateOf(4) }
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NRate(small, { small = it }, size = NSize.Small)
                NRate(medium, { medium = it }, size = NSize.Medium)
                NRate(large, { large = it }, size = NSize.Large)
            }
        }

        DemoSection(
            title = "Color",
            description = "Customize the active star color.",
            code = """
NRate(value, { value = it }, color = Color(0xFF4FB233))
""",
        ) {
            var value by remember { mutableStateOf(3) }
            NRate(value, { value = it }, color = Color(0xFF4FB233))
        }

        DemoSection(
            title = "Icon",
            description = "Provide a custom icon slot; it receives the star index and whether it is active.",
            code = """
NRate(
    value, { value = it },
    icon = { _, active ->
        NIcon(NIcons.Success, size = 16.dp, color = if (active) Color(0xFF18A058) else Color(0xFFBDBDBD))
    },
)
""",
        ) {
            var value by remember { mutableStateOf(3) }
            NRate(
                value,
                { value = it },
                size = NSize.Large,
                icon = { _, active ->
                    NIcon(
                        NIcons.Success,
                        size = 18.dp,
                        color = if (active) Color(0xFF18A058) else Color(0xFFBDBDBD),
                    )
                },
            )
        }

        DemoSection(
            title = "Allow half star",
            description = "Half stars need fractional values, so use NRatePrecise with allowHalf enabled.",
            code = """
var value by remember { mutableStateOf(2.5f) }

NRatePrecise(value, { value = it }, allowHalf = true)
""",
        ) {
            var value by remember { mutableStateOf(2.5f) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NRatePrecise(value, { value = it }, allowHalf = true)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NRatePrecise(value, { value = it }, allowHalf = true, count = 10, size = NSize.Small)
                    NText("Score: $value")
                }
            }
        }

        DemoSection(
            title = "Readonly and disabled",
            description = "Readonly rates display a value without reacting to clicks; disabled rates are also dimmed.",
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NRate(3, {}, readonly = true)
                NRate(4, {}, disabled = true)
            }
        }

        DemoSection(
            title = "Clearable",
            description = "With clearable enabled, clicking the current value clears the rating.",
            code = """
NRate(value, { value = it }, clearable = true)
""",
        ) {
            var value by remember { mutableStateOf(3) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NRate(value, { value = it }, clearable = true)
                NText(if (value == 0) "Cleared" else "Rating: $value (click the same star again to clear)")
            }
        }

        DemoSection(
            title = "Hover value",
            description = "onHoverValueChange reports the value under the pointer, which is useful for live previews.",
        ) {
            var value by remember { mutableStateOf(0) }
            var hover by remember { mutableStateOf<Float?>(null) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NRate(
                    value,
                    { value = it },
                    onHoverValueChange = { hover = it },
                )
                NText(if (hover == null) "Move the pointer over the stars" else "Hover value: $hover")
            }
        }
    }
}
