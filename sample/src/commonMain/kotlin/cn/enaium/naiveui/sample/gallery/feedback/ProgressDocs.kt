package cn.enaium.naiveui.sample.gallery.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NProgress
import cn.enaium.naiveui.components.NProgressIndicatorPlacement
import cn.enaium.naiveui.components.NProgressStatus
import cn.enaium.naiveui.components.NProgressType
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NFlex
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

@Composable
fun ProgressDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Progress",
        description = "Line, circle, dashboard and multiple-circle progress indicators with status, custom color and processing animation.",
    ) {
        DemoSection(
            title = "Line",
            description = "Line progress supports default, info, success, warning and error status, an inside or outside indicator, and hidden indicators.",
            code = "NProgress(percentage = 50f)\nNProgress(percentage = 50f, indicatorPlacement = NProgressIndicatorPlacement.Inside)",
        ) {
            ProgressLineDemo()
        }
        DemoSection(
            title = "Circle",
            description = "Circular progress can switch between the same statuses and updates as the percentage changes.",
        ) {
            var percentage by remember { mutableStateOf(20) }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                NFlex(wrap = true, size = 16.dp) {
                    NProgress(type = NProgressType.Circle, percentage = percentage.toFloat())
                    NProgress(type = NProgressType.Circle, status = NProgressStatus.Info, percentage = percentage.toFloat())
                    NProgress(type = NProgressType.Circle, status = NProgressStatus.Success, percentage = percentage.toFloat())
                    NProgress(type = NProgressType.Circle, status = NProgressStatus.Warning, percentage = percentage.toFloat())
                    NProgress(type = NProgressType.Circle, status = NProgressStatus.Error, percentage = percentage.toFloat())
                }
                PercentageButtons(percentage) { percentage = it }
            }
        }
        DemoSection(
            title = "Circle offset",
            description = "Circular progress can be rotated with offsetDegree.",
        ) {
            NProgress(
                type = NProgressType.Circle,
                percentage = 80f,
                offsetDegree = 120f,
            )
        }
        DemoSection(
            title = "Dashboard",
            description = "Dashboard progress exposes gapDegree and gapOffsetDegree.",
        ) {
            NFlex(size = 16.dp, wrap = true) {
                NProgress(type = NProgressType.Dashboard, percentage = 80f)
                NProgress(
                    type = NProgressType.Dashboard,
                    percentage = 80f,
                    gapDegree = 120f,
                    gapOffsetDegree = 120f,
                )
            }
        }
        DemoSection(
            title = "Multiple circle",
            description = "Multiple percentages render concentric rings. Use the indicator slot for center content.",
            code = "NProgress(\n    type = NProgressType.MultipleCircle,\n    percentages = listOf(50f, 25f),\n    colors = listOf(theme.colors.info, theme.colors.success),\n) { NText(\"Circle racing!\") }",
        ) {
            var percentage by remember { mutableStateOf(0) }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                NProgress(
                    type = NProgressType.MultipleCircle,
                    percentages = listOf(
                        percentage.toFloat(),
                        ((percentage + 10) % 100).toFloat(),
                        ((percentage + 20) % 100).toFloat(),
                        ((percentage + 30) % 100).toFloat(),
                    ),
                    colors = listOf(
                        theme.colors.info,
                        theme.colors.success,
                        theme.colors.warning,
                        theme.colors.error,
                    ),
                    railColors = listOf(
                        theme.colors.info.copy(alpha = 0.2f),
                        theme.colors.success.copy(alpha = 0.2f),
                        theme.colors.warning.copy(alpha = 0.2f),
                        theme.colors.error.copy(alpha = 0.2f),
                    ),
                    strokeWidth = 6.dp,
                    circleSize = 180.dp,
                ) {
                    NText("Circle racing!", size = 14.sp)
                }
                PercentageButtons(percentage) { percentage = it }
            }
        }
        DemoSection(
            title = "Custom indicator",
            description = "The indicator slot replaces the default percentage or status icon.",
        ) {
            NFlex(vertical = true, size = 16.dp) {
                NProgress(type = NProgressType.Circle, percentage = 20f) {
                    NText("20% is small", size = 14.sp)
                }
                NProgress(percentage = 20f, status = NProgressStatus.Success) {
                    NText("Don't put too much content here.")
                }
            }
        }
        DemoSection(
            title = "Customize color",
            description = "Set fill, rail and indicator text colors explicitly.",
        ) {
            NFlex(vertical = true, size = 16.dp) {
                NProgress(
                    type = NProgressType.Circle,
                    percentage = 20f,
                    color = theme.colors.success,
                    railColor = theme.colors.success.copy(alpha = 0.2f),
                    indicatorTextColor = theme.colors.success,
                )
                NProgress(
                    percentage = 20f,
                    color = theme.colors.warning,
                    railColor = theme.colors.warning.copy(alpha = 0.2f),
                    indicatorTextColor = theme.colors.warning,
                )
                NProgress(
                    percentage = 20f,
                    status = NProgressStatus.Error,
                    indicatorPlacement = NProgressIndicatorPlacement.Inside,
                    railColor = theme.colors.error.copy(alpha = 0.2f),
                )
            }
        }
        DemoSection(
            title = "Gradient",
            description = "Use a gradient pair instead of a flat fill color.",
        ) {
            NFlex(vertical = true, size = 16.dp) {
                NProgress(
                    percentage = 80f,
                    showIndicator = false,
                    gradient = Color.White to Color(0xFFEE4F9B),
                )
                NFlex(size = 16.dp) {
                    NProgress(
                        type = NProgressType.Circle,
                        percentage = 80f,
                        gradient = Color(0xFFE3F2FD) to Color(0xFF2080F0),
                    )
                    NProgress(
                        type = NProgressType.MultipleCircle,
                        percentages = listOf(80f, 70f),
                        gradients = listOf(
                            Color.White to Color(0xFF18A058),
                            Color(0xFFE3F2FD) to Color(0xFF2080F0),
                        ),
                        railColors = listOf(
                            theme.colors.info.copy(alpha = 0.2f),
                            theme.colors.info.copy(alpha = 0.2f),
                        ),
                        strokeWidth = 6.dp,
                        circleSize = 160.dp,
                    )
                }
            }
        }
        DemoSection(
            title = "No indicator",
            description = "Hide the indicator entirely.",
        ) {
            NFlex(vertical = true, size = 16.dp) {
                NProgress(
                    type = NProgressType.Circle,
                    percentage = 20f,
                    status = NProgressStatus.Success,
                    showIndicator = false,
                )
                NProgress(
                    percentage = 20f,
                    status = NProgressStatus.Success,
                    showIndicator = false,
                )
                NProgress(
                    type = NProgressType.MultipleCircle,
                    percentages = listOf(50f, 25f),
                    showIndicator = false,
                    circleSize = 160.dp,
                )
            }
        }
        DemoSection(
            title = "Height",
            description = "Customize the line height for a bolder rail.",
        ) {
            NFlex(vertical = true, size = 16.dp) {
                NProgress(percentage = 50f, height = 24.dp)
                NProgress(
                    percentage = 50f,
                    status = NProgressStatus.Error,
                    height = 24.dp,
                ) {
                    NText("A Kind of Art")
                }
            }
        }
        DemoSection(
            title = "Processing",
            description = "The processing animation sweeps a highlight across the filled portion.",
        ) {
            NProgress(
                percentage = 60f,
                indicatorPlacement = NProgressIndicatorPlacement.Inside,
                processing = true,
            )
        }
    }
}

@Composable
private fun ProgressLineDemo() {
    var percentage by remember { mutableStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NProgress(percentage = percentage.toFloat(), showIndicator = false)
        NProgress(percentage = percentage.toFloat())
        NProgress(
            percentage = percentage.toFloat(),
            indicatorPlacement = NProgressIndicatorPlacement.Inside,
        )
        NProgressStatus.entries.forEach { status ->
            NProgress(
                percentage = percentage.toFloat(),
                status = status,
                showIndicator = false,
            )
        }
        NProgress(
            percentage = percentage.toFloat(),
            status = NProgressStatus.Info,
            indicatorPlacement = NProgressIndicatorPlacement.Inside,
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PercentageButtons(percentage) { percentage = it }
        }
    }
}

@Composable
private fun PercentageButtons(percentage: Int, onChange: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        NButton(onClick = { onChange(if (percentage - 10 < 0) 100 else percentage - 10) }) {
            NText("Minus 10%")
        }
        NButton(onClick = { onChange(if (percentage + 10 > 100) 0 else percentage + 10) }) {
            NText("Add 10%")
        }
    }
}
