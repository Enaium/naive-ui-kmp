package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NDynamicTags
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NStatus

@Composable
fun DynamicTagsDocs() {
    DocsPage(
        title = "Dynamic Tags",
        description = "Maintain a dynamic list of tags with an inline add input.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Tags are closable by default and the add button reveals an inline input.",
            code = """
var tags by remember { mutableStateOf(listOf("teacher", "programmer")) }
NDynamicTags(value = tags, onValueChange = { tags = it })
""".trimIndent(),
        ) {
            BasicDemo()
        }
        DemoSection(
            title = "Max tag count",
            description = "When the maximum is reached the add button is disabled.",
            code = """
NDynamicTags(
    value = tags,
    onValueChange = { tags = it },
    max = 3,
)
""".trimIndent(),
        ) {
            MaxDemo()
        }
        DemoSection(
            title = "Add input",
            description = "Type a value and press Enter or move focus away to commit it. The input can be tailored with a placeholder.",
            code = """
NDynamicTags(
    value = tags,
    onValueChange = { tags = it },
    placeholder = "Type and press Enter",
)
""".trimIndent(),
        ) {
            InputDemo()
        }
        DemoSection(
            title = "Appearance",
            description = "Tags accept a status type, round shape and can be made non-closable.",
            code = """
NDynamicTags(
    value = tags,
    onValueChange = { tags = it },
    type = NStatus.Success,
    round = true,
    closable = false,
)
""".trimIndent(),
        ) {
            AppearanceDemo()
        }
    }
}

@Composable
private fun BasicDemo() {
    var tags by remember { mutableStateOf(listOf("teacher", "programmer")) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NDynamicTags(value = tags, onValueChange = { tags = it })
        NText("Tags: $tags", color = LocalNTheme.current.colors.text3)
    }
}

@Composable
private fun MaxDemo() {
    var tags by remember { mutableStateOf(listOf("teacher", "programmer")) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NDynamicTags(value = tags, onValueChange = { tags = it }, max = 3)
        NText("${tags.size} of 3 tags", color = LocalNTheme.current.colors.text3)
    }
}

@Composable
private fun InputDemo() {
    var tags by remember { mutableStateOf(listOf("naive-ui")) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NDynamicTags(
            value = tags,
            onValueChange = { tags = it },
            placeholder = "Type and press Enter",
        )
        NText("Tags: $tags", color = LocalNTheme.current.colors.text3)
    }
}

@Composable
private fun AppearanceDemo() {
    var tags by remember { mutableStateOf(listOf("tag", "round", "success")) }
    NDynamicTags(
        value = tags,
        onValueChange = { tags = it },
        type = NStatus.Success,
        round = true,
        closable = false,
    )
}
