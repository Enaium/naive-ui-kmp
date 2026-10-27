package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NTransfer
import cn.enaium.naiveui.components.NTransferOption
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize

@Composable
fun TransferDocs() {
    DocsPage(
        title = "Transfer",
        description = "Move entries between a source and a target list. Check entries and use the arrows to move " +
            "them; target entries can also be removed individually.",
    ) {
        BasicTransferDemo()
        FilterableTransferDemo()
        TransferSizeDemo()
        DisabledTransferDemo()
    }
}

private fun transferOptions(count: Int): List<NTransferOption> = List(count) { index ->
    NTransferOption(
        label = "Option $index",
        value = "option-$index",
        disabled = index % 5 == 0,
    )
}

@Composable
private fun BasicTransferDemo() {
    val options = remember { transferOptions(20) }
    var value by remember { mutableStateOf(listOf("option-1", "option-2", "option-6")) }

    DemoSection(
        title = "Basic",
        description = "The target keys are hoisted through `value`. Disabled options cannot be selected or moved.",
        code = """NTransfer(
    options = options,
    value = value,
    onValueChange = { value = it },
)""",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NTransfer(
                options = options,
                value = value,
                onValueChange = { value = it },
                sourceTitle = "Source",
                targetTitle = "Target",
            )
            NText(
                text = "Target contains ${value.size} option(s).",
                color = LocalNTheme.current.colors.text3,
            )
        }
    }
}

@Composable
private fun FilterableTransferDemo() {
    val options = remember { transferOptions(30) }
    var value by remember { mutableStateOf(emptyList<String>()) }

    DemoSection(
        title = "Filterable",
        description = "`filterable` adds a search box to both panels; use `sourceFilterable` or " +
            "`targetFilterable` to enable just one.",
        code = """NTransfer(
    options = options,
    value = value,
    onValueChange = { value = it },
    filterable = true,
)""",
    ) {
        NTransfer(
            options = options,
            value = value,
            onValueChange = { value = it },
            filterable = true,
            heights = 280.dp,
        )
    }
}

@Composable
private fun TransferSizeDemo() {
    val options = remember { transferOptions(12) }
    var smallValue by remember { mutableStateOf(listOf("option-3")) }
    var largeValue by remember { mutableStateOf(listOf("option-4", "option-7")) }

    DemoSection(
        title = "Size",
        description = "Sizes adjust row height, header typography and header buttons.",
        code = "NTransfer(size = NSize.Small, heights = 220.dp, ...)",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            NTransfer(
                options = options,
                value = smallValue,
                onValueChange = { smallValue = it },
                size = NSize.Small,
                heights = 220.dp,
            )
            NTransfer(
                options = options,
                value = largeValue,
                onValueChange = { largeValue = it },
                size = NSize.Large,
                heights = 220.dp,
            )
        }
    }
}

@Composable
private fun DisabledTransferDemo() {
    val options = remember { transferOptions(8) }

    DemoSection(
        title = "Disabled",
        description = "Every interaction is blocked while the component is disabled.",
        code = "NTransfer(options = options, value = value, disabled = true)",
    ) {
        NTransfer(
            options = options,
            value = listOf("option-1", "option-4"),
            onValueChange = {},
            disabled = true,
            heights = 240.dp,
        )
    }
}
