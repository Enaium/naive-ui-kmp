package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NStatistic
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

@Composable
fun StatisticDocs() {
    DocsPage(
        title = "Statistic",
        description = "Statistic highlights a label and a single numeric or textual value with optional prefix and suffix slots.",
    ) {
        DemoSection(
            title = "Basic",
            description = "value accepts a Number or a preformatted String. prefix and suffix are slots, so icons or units work naturally.",
            code = """
NStatistic(
    label = "Statistic",
    value = 99,
    prefix = { NIcon(NIcons.Success) },
    suffix = { NText("/ 100") },
)
NStatistic(label = "Active Users", value = "1,234,123")
""",
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                NStatistic(
                    modifier = Modifier.weight(1f),
                    label = "Statistic",
                    value = 99,
                    prefix = { NIcon(NIcons.Success) },
                    suffix = { NText("/ 100") },
                )
                NStatistic(
                    modifier = Modifier.weight(1f),
                    label = "Active Users",
                    value = "1,234,123",
                )
            }
        }

        DemoSection(
            title = "Precision and tabular numbers",
            description = "Precision rounds numeric values and pads trailing zeros. tabularNumbers uses tabular figures so digits keep a constant width.",
            code = """
NStatistic(label = "Pi (2 decimals)", value = 3.14159, precision = 2)
NStatistic(label = "Pi (0 decimals)", value = 3.14159, precision = 0, tabularNumbers = true)
""",
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                NStatistic(
                    modifier = Modifier.weight(1f),
                    label = "Pi (2 decimals)",
                    value = 3.14159,
                    precision = 2,
                )
                NStatistic(
                    modifier = Modifier.weight(1f),
                    label = "Pi (0 decimals)",
                    value = 3.14159,
                    precision = 0,
                    tabularNumbers = true,
                )
                NStatistic(
                    modifier = Modifier.weight(1f),
                    label = "Percentage",
                    value = 0.856,
                    precision = 2,
                    suffix = { NText("%") },
                )
            }
        }

        DemoSection(
            title = "Custom value style",
            description = "valueStyle overrides the default 24sp styling of the value.",
            code = """
NStatistic(
    label = "Revenue",
    value = 128450,
    valueStyle = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold),
    suffix = { NText(" USD") },
)
""",
        ) {
            NStatistic(
                label = "Revenue",
                value = 128450,
                valueStyle = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalNTheme.current.colors.primary,
                ),
                suffix = { NText(" USD") },
            )
        }
    }
}
