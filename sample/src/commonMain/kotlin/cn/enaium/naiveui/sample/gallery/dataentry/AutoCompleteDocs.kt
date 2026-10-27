package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NAutoComplete
import cn.enaium.naiveui.components.NAutoCompleteOption
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AutoCompleteDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Auto Complete",
        description = "An input that suggests matching options below it. Navigate with the arrow keys " +
            "and confirm with Enter.",
    ) {
        var basic by remember { mutableStateOf("") }
        DemoSection(
            title = "Basic",
            description = "Start typing to see email domain suggestions.",
            code = "NAutoComplete(value = value, onValueChange = { value = it }, options = options)",
        ) {
            val prefix = basic.substringBefore('@')
            val options = listOf("@gmail.com", "@163.com", "@qq.com").map {
                val candidate = prefix + it
                NAutoCompleteOption(label = candidate, value = candidate)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NAutoComplete(
                    value = basic,
                    onValueChange = { basic = it },
                    modifier = Modifier.width(320.dp),
                    options = options,
                    placeholder = "Email",
                    clearable = true,
                )
                NText(
                    text = if (basic.isEmpty()) "Value: (empty)" else "Value: $basic",
                    color = theme.colors.text3,
                    size = theme.typography.fontSize.small,
                )
            }
        }

        DemoSection(
            title = "Size",
            description = "Small, medium and large sizes.",
        ) {
            var sized by remember { mutableStateOf("") }
            val prefix = sized.substringBefore('@')
            val options = listOf("@gmail.com", "@163.com", "@qq.com").map {
                val candidate = prefix + it
                NAutoCompleteOption(label = candidate, value = candidate)
            }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NAutoComplete(
                    value = sized,
                    onValueChange = { sized = it },
                    options = options,
                    size = NSize.Small,
                    placeholder = "Email",
                )
                NAutoComplete(
                    value = sized,
                    onValueChange = { sized = it },
                    options = options,
                    size = NSize.Medium,
                    placeholder = "Email",
                )
                NAutoComplete(
                    value = sized,
                    onValueChange = { sized = it },
                    options = options,
                    size = NSize.Large,
                    placeholder = "Email",
                )
            }
        }

        DemoSection(
            title = "Load remote options",
            description = "Update options asynchronously and use loading to indicate a pending request.",
        ) {
            var remoteValue by remember { mutableStateOf("") }
            var remoteOptions by remember { mutableStateOf(emptyList<NAutoCompleteOption>()) }
            var remoteLoading by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()
            NAutoComplete(
                value = remoteValue,
                onValueChange = { next ->
                    remoteValue = next
                    remoteLoading = true
                    scope.launch {
                        delay(500)
                        val prefix = next.substringBefore('@')
                        remoteOptions = listOf("gmail.com", "163.com", "qq.com")
                            .map { domain ->
                                val candidate = prefix + "@" + domain
                                NAutoCompleteOption(label = candidate, value = candidate)
                            }
                        remoteLoading = false
                    }
                },
                modifier = Modifier.width(320.dp),
                options = remoteOptions,
                loading = remoteLoading,
                placeholder = "Email",
                clearable = true,
            )
        }

        DemoSection(
            title = "Manual options",
            description = "With filterable = false the options are shown exactly as supplied, which suits " +
                "server-side filtered results.",
        ) {
            var manual by remember { mutableStateOf("") }
            val manualOptions = listOf("gmail.com", "163.com", "qq.com")
                .map { NAutoCompleteOption(label = it, value = it) }
            NAutoComplete(
                value = manual,
                onValueChange = { manual = it },
                modifier = Modifier.width(320.dp),
                options = manualOptions,
                filterable = false,
                placeholder = "Pick a domain",
            )
        }

        DemoSection(
            title = "Loading",
            description = "loading shows a spinner in the input and at the top of the menu.",
        ) {
            NAutoComplete(
                value = "",
                onValueChange = {},
                modifier = Modifier.width(320.dp),
                options = listOf(NAutoCompleteOption(label = "Loading options…")),
                loading = true,
                placeholder = "Loading",
            )
        }

        DemoSection(
            title = "Validation status",
            description = "Validation status can be applied outside a form.",
        ) {
            var warning by remember { mutableStateOf("") }
            var error by remember { mutableStateOf("") }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NAutoComplete(
                    value = warning,
                    onValueChange = { warning = it },
                    options = emptyList(),
                    status = NStatus.Warning,
                    placeholder = "Warning",
                )
                NAutoComplete(
                    value = error,
                    onValueChange = { error = it },
                    options = emptyList(),
                    status = NStatus.Error,
                    placeholder = "Error",
                )
            }
        }

        DemoSection(
            title = "Disabled",
            description = "A disabled autocomplete cannot be focused.",
        ) {
            NAutoComplete(
                value = "07akioni@example.com",
                onValueChange = {},
                modifier = Modifier.width(320.dp),
                options = emptyList(),
                disabled = true,
            )
        }
    }
}
