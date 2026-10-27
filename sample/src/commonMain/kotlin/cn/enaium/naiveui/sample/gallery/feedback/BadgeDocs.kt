package cn.enaium.naiveui.sample.gallery.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NSwitch
import cn.enaium.naiveui.components.NBadge
import cn.enaium.naiveui.components.NBadgeType
import cn.enaium.naiveui.components.NAvatar
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NFlex
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage

@Composable
fun BadgeDocs() {
    DocsPage(
        title = "Badge",
        description = "A small count or status marker attached to the top end of its content.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Attach a value or dot badge to content and cap the displayed value with max.",
            code = "NBadge(value = value, max = 15) {\n    NAvatar()\n}",
        ) {
            var value by remember { mutableStateOf(5) }
            NFlex(size = 24.dp, wrap = true, align = cn.enaium.naiveui.components.NAlign.Center) {
                NBadge(value = value, max = 15) { NAvatar() }
                NBadge(value = value, dot = true) { NAvatar() }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NButton(onClick = { value = (value + 1).coerceAtMost(16) }) { NText("+") }
                    NButton(onClick = { value = (value - 1).coerceAtLeast(0) }) { NText("-") }
                }
            }
        }
        DemoSection(
            title = "Type",
            description = "Badges support default, error, info, success and warning types.",
        ) {
            NFlex(size = 24.dp, wrap = true, align = cn.enaium.naiveui.components.NAlign.Center) {
                NBadge(dot = true) { NAvatar() }
                NBadge(dot = true, type = NBadgeType.Error) { NAvatar() }
                NBadge(dot = true, type = NBadgeType.Info) { NAvatar() }
                NBadge(dot = true, type = NBadgeType.Success) { NAvatar() }
                NBadge(dot = true, type = NBadgeType.Warning) { NAvatar() }
            }
        }
        DemoSection(
            title = "Processing",
            description = "A processing ripple indicates background work.",
        ) {
            NFlex(size = 24.dp, wrap = true, align = cn.enaium.naiveui.components.NAlign.Center) {
                NBadge(dot = true, processing = true) { NAvatar() }
                NBadge(value = 20, processing = true) { NAvatar() }
                NBadge(dot = true, type = NBadgeType.Info, processing = true) { NAvatar() }
            }
        }
        DemoSection(
            title = "Show zero",
            description = "Zero is hidden by default; enable showZero to display it.",
        ) {
            var value by remember { mutableStateOf(0) }
            NFlex(size = 24.dp, wrap = true, align = cn.enaium.naiveui.components.NAlign.Center) {
                NBadge(value = value) { NAvatar() }
                NBadge(value = value, showZero = true) { NAvatar() }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NButton(onClick = { value += 1 }) { NText("+") }
                    NButton(onClick = { value = (value - 1).coerceAtLeast(0) }) { NText("-") }
                }
            }
        }
        DemoSection(
            title = "Overflow",
            description = "Set max to render values above the limit as \"max+\".",
        ) {
            var value by remember { mutableStateOf(101) }
            NFlex(size = 24.dp, wrap = true, align = cn.enaium.naiveui.components.NAlign.Center) {
                NBadge(value = value, showZero = true) { NAvatar() }
                NBadge(value = value, max = 99) { NAvatar() }
                NBadge(value = value, max = 10, showZero = true) { NAvatar() }
                NButton(onClick = { value = (value + 1).coerceAtMost(120) }) { NText("Add") }
            }
        }
        DemoSection(
            title = "Controlled visibility",
            description = "Toggle the badge without removing its content.",
        ) {
            var value by remember { mutableStateOf(5) }
            var show by remember { mutableStateOf(true) }
            NFlex(size = 24.dp, wrap = true, align = cn.enaium.naiveui.components.NAlign.Center) {
                NBadge(value = value, max = 15, show = show) { NAvatar() }
                NBadge(value = value, dot = true, show = show) { NAvatar() }
                NSwitch(checked = show, onCheckedChange = { show = it })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NButton(onClick = { value = (value + 1).coerceAtMost(16) }) { NText("+") }
                    NButton(onClick = { value = (value - 1).coerceAtLeast(0) }) { NText("-") }
                }
            }
        }
        DemoSection(
            title = "Custom content",
            description = "String values allow words like \"new\" or \"hot\".",
        ) {
            NFlex(size = 24.dp, wrap = true, align = cn.enaium.naiveui.components.NAlign.Center) {
                NBadge(value = "new") { NAvatar() }
                NBadge(value = "hot") { NAvatar() }
            }
        }
        DemoSection(
            title = "Customize color",
            description = "Override the badge color directly.",
        ) {
            NBadge(value = "15", color = Color(0xFF888888)) { NAvatar() }
        }
        DemoSection(
            title = "Offset",
            description = "Shift the badge from its default position.",
        ) {
            NBadge(value = 5, max = 15, offset = DpOffset((-17).dp, 17.dp)) { NAvatar() }
        }
        DemoSection(
            title = "Raw",
            description = "Use inline mode when the badge has no wrapped content.",
        ) {
            NFlex(size = 24.dp, align = cn.enaium.naiveui.components.NAlign.Center) {
                NBadge(value = 5, max = 15, inline = true)
                NBadge(dot = true, inline = true)
                NBadge(value = 5, dot = true, type = NBadgeType.Success, inline = true)
            }
        }
    }
}
