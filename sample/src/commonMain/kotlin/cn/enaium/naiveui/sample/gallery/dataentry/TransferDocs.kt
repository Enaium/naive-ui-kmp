package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NAvatar
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NTransfer
import cn.enaium.naiveui.components.NTransferOption
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
        LargeDataTransferDemo()
        RenderLabelTransferDemo()
        RenderSourceListTransferDemo()
        SizeTransferDemo()
    }
}

private fun transferOptions(count: Int): List<NTransferOption> = List(count) { index ->
    NTransferOption(
        label = "Option $index",
        value = "option-$index",
        disabled = index % 5 == 0,
    )
}

private fun transferValues(count: Int): List<String> = List(count) { "option-$it" }

@Composable
private fun BasicTransferDemo() {
    val options = remember { transferOptions(100) }
    var value by remember { mutableStateOf(emptyList<String>()) }

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
    val options = remember { transferOptions(100) }
    var sourceValue by remember { mutableStateOf(transferValues(50)) }
    var targetValue by remember { mutableStateOf(transferValues(50)) }

    DemoSection(
        title = "Filterable",
        description = "`sourceFilterable` and `targetFilterable` add a search box to individual panels.",
        code = """NTransfer(options = options, value = value, onValueChange = { value = it }, sourceFilterable = true)
NTransfer(options = options, value = value, onValueChange = { value = it }, targetFilterable = true)""",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            NTransfer(
                options = options,
                value = sourceValue,
                onValueChange = { sourceValue = it },
                sourceFilterable = true,
                heights = 260.dp,
            )
            NTransfer(
                options = options,
                value = targetValue,
                onValueChange = { targetValue = it },
                targetFilterable = true,
                heights = 260.dp,
            )
        }
    }
}

@Composable
private fun LargeDataTransferDemo() {
    val options = remember { transferOptions(42000) }
    var value by remember { mutableStateOf(transferValues(50)) }

    DemoSection(
        title = "Large data",
        description = "`virtualScroll` composes only visible rows, so tens of thousands of options stay responsive.",
        code = "NTransfer(options = options, value = value, onValueChange = { value = it }, virtualScroll = true)",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NTransfer(
                options = options,
                value = value,
                onValueChange = { value = it },
                virtualScroll = true,
                heights = 360.dp,
            )
            NText(
                text = "42,000 options, ${value.size} in the target.",
                color = LocalNTheme.current.colors.text3,
            )
        }
    }
}

private fun avatarOptions(): List<NTransferOption> = listOf(
    NTransferOption("07akioni", "https://avatars.githubusercontent.com/u/18677354?s=60&v=4"),
    NTransferOption("amadeus711", "https://avatars.githubusercontent.com/u/46394163?s=60&v=4"),
    NTransferOption("Talljack", "https://avatars.githubusercontent.com/u/34439652?s=60&v=4"),
    NTransferOption("JiwenBai", "https://avatars.githubusercontent.com/u/43430022?s=60&v=4"),
    NTransferOption("songjianet", "https://avatars.githubusercontent.com/u/19239641?s=60&v=4"),
)

@Composable
private fun RenderLabelTransferDemo() {
    val options = remember { avatarOptions() }
    var value by remember { mutableStateOf(listOf(options.first().value)) }

    DemoSection(
        title = "Custom label",
        description = "`renderTargetLabel` replaces the default target label with arbitrary content, here an avatar " +
            "loaded from the option value.",
        code = """NTransfer(
    options = options,
    value = value,
    onValueChange = { value = it },
    renderTargetLabel = { option ->
        Row { NAvatar(size = 24.dp, round = true, src = option.value); NText(option.label) }
    },
)""",
    ) {
        NTransfer(
            options = options,
            value = value,
            onValueChange = { value = it },
            renderTargetLabel = { option ->
                Row(
                    Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NAvatar(
                        size = 24.dp,
                        src = option.value,
                        round = true,
                        text = option.label.take(1),
                    )
                    Spacer(Modifier.width(6.dp))
                    NText(option.label)
                }
            },
            heights = 280.dp,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RenderSourceListTransferDemo() {
    val options = remember { transferOptions(24) }
    var value by remember { mutableStateOf(emptyList<String>()) }
    val theme = LocalNTheme.current

    DemoSection(
        title = "Custom source list",
        description = "`renderSourceList` replaces the whole source body. This custom chip list moves entries through " +
            "the scope's `onCheck`.",
        code = """NTransfer(
    options = options,
    value = value,
    onValueChange = { value = it },
    renderSourceList = {
        FlowRow {
            options.forEach { option ->
                NTag(option.label, checkable = true, onCheckedChange = {
                    onCheck(checkedValues + option.value)
                })
            }
        }
    },
)""",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NTransfer(
                options = options,
                value = value,
                onValueChange = { value = it },
                sourceFilterable = true,
                heights = 300.dp,
                renderSourceList = {
                    Column(
                        Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        NText(
                            text = "Custom list · ${options.size} available" +
                                if (pattern.isBlank()) "" else " · filter: \"$pattern\"",
                            size = theme.typography.fontSize.small,
                            color = theme.colors.text3,
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            options.forEach { option ->
                                NTag(
                                    text = option.label,
                                    size = NSize.Small,
                                    disabled = option.disabled,
                                    checkable = true,
                                    onCheckedChange = {
                                        onCheck((checkedValues + option.value).distinct())
                                    },
                                )
                            }
                        }
                    }
                },
            )
            NText(
                text = "Target contains ${value.size} option(s).",
                color = theme.colors.text3,
            )
        }
    }
}

@Composable
private fun SizeTransferDemo() {
    val options = remember { transferOptions(40) }
    var smallValue by remember { mutableStateOf(transferValues(10)) }
    var largeValue by remember { mutableStateOf(transferValues(10)) }

    DemoSection(
        title = "Size",
        description = "Sizes adjust row height, header typography and header buttons.",
        code = """NTransfer(size = NSize.Small)
NTransfer(size = NSize.Large)""",
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
