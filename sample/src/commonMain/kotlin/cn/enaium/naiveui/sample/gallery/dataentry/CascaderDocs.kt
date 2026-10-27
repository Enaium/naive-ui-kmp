package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NCascader
import cn.enaium.naiveui.components.NCascaderExpandTrigger
import cn.enaium.naiveui.components.NCascaderOption
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus

private val regionOptions: List<NCascaderOption> = listOf(
    NCascaderOption(
        label = "Zhejiang",
        value = "zhejiang",
        children = listOf(
            NCascaderOption(
                label = "Hangzhou",
                value = "hangzhou",
                children = listOf(
                    NCascaderOption("West Lake", "west-lake"),
                    NCascaderOption("Lingyin Temple", "lingyin"),
                    NCascaderOption("Leifeng Pagoda", "leifeng", disabled = true),
                ),
            ),
            NCascaderOption(
                label = "Ningbo",
                value = "ningbo",
                children = listOf(
                    NCascaderOption("Tianyi Pavilion", "tianyi"),
                    NCascaderOption("Dongqian Lake", "dongqian"),
                ),
            ),
        ),
    ),
    NCascaderOption(
        label = "Jiangsu",
        value = "jiangsu",
        children = listOf(
            NCascaderOption(
                label = "Nanjing",
                value = "nanjing",
                children = listOf(
                    NCascaderOption("Sun Yat-sen Mausoleum", "sun-yat-sen"),
                    NCascaderOption("Confucius Temple", "confucius"),
                ),
            ),
            NCascaderOption(
                label = "Suzhou",
                value = "suzhou",
                children = listOf(
                    NCascaderOption("Humble Administrator's Garden", "humble-garden"),
                    NCascaderOption("Tiger Hill", "tiger-hill"),
                ),
            ),
        ),
    ),
    NCascaderOption(
        label = "Beijing",
        value = "beijing",
        disabled = true,
        children = listOf(
            NCascaderOption("Forbidden City", "forbidden-city"),
        ),
    ),
)

@Composable
fun CascaderDocs() {
    DocsPage(
        title = "Cascader",
        description = "Select values from tree-structured data by walking through columns. " +
            "Supports single, multiple and checkbox selection, filtering and path display.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Click an option to open the next column and pick a leaf. The " +
                "trigger shows the full path; hover trigger opens columns on hover.",
            code = """NCascader(
    options = options,
    value = value,
    onValueChange = { value = it },
    clearable = true,
    showPath = true,
)""",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            var hoverValue by remember { mutableStateOf<Any?>("tianyi") }
            Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NCascader(
                    options = regionOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Please select a place",
                    clearable = true,
                    showPath = true,
                )
                NCascader(
                    options = regionOptions,
                    value = hoverValue,
                    onValueChange = { hoverValue = it },
                    placeholder = "Hover to expand",
                    clearable = true,
                    showPath = true,
                    expandTrigger = NCascaderExpandTrigger.Hover,
                )
            }
        }

        DemoSection(
            title = "Multiple",
            description = "Multiple mode selects several leaves with cascading checkboxes. " +
                "The menu stays open and selected leaves are shown as tags.",
            code = """NCascader(
    options = options,
    value = values,
    onValueChange = { values = it },
    multiple = true,
    clearable = true,
)""",
        ) {
            var values by remember {
                mutableStateOf<Any?>(listOf<Any>("west-lake", "tianyi"))
            }
            Column(Modifier.width(300.dp)) {
                NCascader(
                    options = regionOptions,
                    value = values,
                    onValueChange = { values = it },
                    placeholder = "Select several places",
                    multiple = true,
                    maxTagCount = 3,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Checkable and cascade",
            description = "Cascade links parents and children. Disable cascade to check each " +
                "column independently.",
        ) {
            var cascaded by remember { mutableStateOf<Any?>(listOf<Any>("hangzhou")) }
            var independent by remember { mutableStateOf<Any?>(listOf<Any>("hangzhou")) }
            Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NCascader(
                    options = regionOptions,
                    value = cascaded,
                    onValueChange = { cascaded = it },
                    placeholder = "Cascade checks",
                    multiple = true,
                    checkable = true,
                    cascade = true,
                    clearable = true,
                )
                NCascader(
                    options = regionOptions,
                    value = independent,
                    onValueChange = { independent = it },
                    placeholder = "Independent checks",
                    multiple = true,
                    checkable = true,
                    cascade = false,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Change on select",
            description = "When changeOnSelect is enabled a branch can be selected without " +
                "walking to a leaf.",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            Column(Modifier.width(300.dp)) {
                NCascader(
                    options = regionOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Any level is selectable",
                    changeOnSelect = true,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Filterable",
            description = "Search every level; matches show their parent path.",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            Column(Modifier.width(300.dp)) {
                NCascader(
                    options = regionOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Search places",
                    filterable = true,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Size and status",
            description = "Cascader uses the shared control heights and validation colors.",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            var warningValue by remember { mutableStateOf<Any?>(null) }
            var errorValue by remember { mutableStateOf<Any?>(null) }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.width(200.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    NCascader(
                        options = regionOptions,
                        value = value,
                        onValueChange = { value = it },
                        placeholder = "Small",
                        size = NSize.Small,
                    )
                    NCascader(
                        options = regionOptions,
                        value = value,
                        onValueChange = { value = it },
                        placeholder = "Medium",
                        size = NSize.Medium,
                    )
                    NCascader(
                        options = regionOptions,
                        value = value,
                        onValueChange = { value = it },
                        placeholder = "Large",
                        size = NSize.Large,
                    )
                }
                Column(Modifier.width(200.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    NCascader(
                        options = regionOptions,
                        value = warningValue,
                        onValueChange = { warningValue = it },
                        placeholder = "Warning",
                        status = NStatus.Warning,
                    )
                    NCascader(
                        options = regionOptions,
                        value = errorValue,
                        onValueChange = { errorValue = it },
                        placeholder = "Error",
                        status = NStatus.Error,
                    )
                    NCascader(
                        options = emptyList(),
                        placeholder = "No data",
                    )
                }
            }
        }
    }
}
