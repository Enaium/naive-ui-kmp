package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NAvatar
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NSelect
import cn.enaium.naiveui.components.NSelectEntry
import cn.enaium.naiveui.components.NSelectGroupOption
import cn.enaium.naiveui.components.NSelectOption
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NPopover
import cn.enaium.naiveui.internal.NTrigger
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import kotlinx.coroutines.delay

private val songOptions: List<NSelectEntry> = listOf(
    NSelectOption(
        "Everybody's Got Something to Hide Except Me and My Monkey",
        "song0",
        disabled = true,
    ),
    NSelectOption("Drive My Car", "song1"),
    NSelectOption("Norwegian Wood", "song2"),
    NSelectOption("You Won't See", "song3", disabled = true),
    NSelectOption("Nowhere Man", "song4"),
    NSelectOption("Think For Yourself", "song5"),
    NSelectOption("The Word", "song6"),
    NSelectOption("Michelle", "song7", disabled = true),
    NSelectOption("What goes on", "song8"),
    NSelectOption("Girl", "song9"),
    NSelectOption("I'm looking through you", "song10"),
    NSelectOption("In My Life", "song11"),
    NSelectOption("Wait", "song12"),
)

private val groupedOptions: List<NSelectEntry> = listOf(
    NSelectGroupOption(
        label = "Rubber Soul",
        children = listOf(
            NSelectOption("Drive My Car", "song1"),
            NSelectOption("Norwegian Wood", "song2"),
            NSelectOption("Nowhere Man", "song4"),
            NSelectOption("In My Life", "song11"),
        ),
    ),
    NSelectGroupOption(
        label = "Let It Be",
        children = listOf(
            NSelectOption("Two Of Us", "two-of-us"),
            NSelectOption("Dig A Pony", "dig-a-pony"),
            NSelectOption("Across The Universe", "across-the-universe"),
            NSelectOption("Let It Be", "let-it-be"),
        ),
    ),
    NSelectGroupOption(
        label = "Abbey Road",
        children = listOf(
            NSelectOption("Come Together", "come-together"),
            NSelectOption("Something", "something"),
            NSelectOption("Here Comes the Sun", "here-comes-the-sun"),
        ),
    ),
)

private val manyOptions: List<NSelectEntry> =
    (0 until 10_000).map { NSelectOption(it.toString(), it) }

private data class Song(val title: String, val id: String)

private val customSongs = listOf(
    Song("Drive My Car", "custom-1"),
    Song("Norwegian Wood", "custom-2"),
    Song("Nowhere Man", "custom-3"),
    Song("In My Life", "custom-4"),
)

@Composable
fun SelectDocs() {
    DocsPage(
        title = "Select",
        description = "Select a single value or many values from a popup menu, with " +
            "filtering, grouping, tags, async options and validation states.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Pass options and a value. Disabled options cannot be picked.",
            code = """NSelect(
    options = options,
    value = value,
    onValueChange = { value = it },
    clearable = true,
)""",
        ) {
            var value by remember { mutableStateOf<Any?>("song2") }
            Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NSelect(
                    options = songOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Please select a song",
                    clearable = true,
                )
                NSelect(
                    options = songOptions,
                    value = "Norwegian Wood",
                    onValueChange = {},
                    placeholder = "Disabled",
                    disabled = true,
                )
            }
        }

        DemoSection(
            title = "Size",
            description = "Four control sizes are available.",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            Column(Modifier.width(220.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NSelect(
                    options = songOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Tiny",
                    size = NSize.Tiny,
                )
                NSelect(
                    options = songOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Small",
                    size = NSize.Small,
                )
                NSelect(
                    options = songOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Medium",
                    size = NSize.Medium,
                )
                NSelect(
                    options = songOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Large",
                    size = NSize.Large,
                )
            }
        }

        DemoSection(
            title = "Status",
            description = "Validation status recolors the trigger border outside a form.",
        ) {
            var warning by remember { mutableStateOf<Any?>(null) }
            var error by remember { mutableStateOf<Any?>(null) }
            Column(Modifier.width(220.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NSelect(
                    options = songOptions,
                    value = warning,
                    onValueChange = { warning = it },
                    placeholder = "Warning",
                    status = NStatus.Warning,
                    clearable = true,
                )
                NSelect(
                    options = songOptions,
                    value = error,
                    onValueChange = { error = it },
                    placeholder = "Error",
                    status = NStatus.Error,
                    clearable = true,
                )
                NSelect(
                    options = songOptions,
                    placeholder = "Success",
                    status = NStatus.Success,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Multiple",
            description = "Selected values become closable tags. The menu stays open while " +
                "picking several values.",
        ) {
            var values by remember {
                mutableStateOf<Any?>(listOf<Any>("song1", "song4"))
            }
            Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NSelect(
                    options = songOptions,
                    value = values,
                    onValueChange = { values = it },
                    placeholder = "Please select songs",
                    multiple = true,
                    clearable = true,
                )
                NSelect(
                    options = songOptions,
                    value = listOf<Any>("song1"),
                    onValueChange = {},
                    placeholder = "Disabled",
                    multiple = true,
                    disabled = true,
                )
            }
        }

        DemoSection(
            title = "Events",
            description = "Value, open state, focus and clear events are reported through " +
                "callbacks.",
            code = """NSelect(
    options = options,
    onValueChange = { log("value: ${'$'}it") },
    onUpdateShow = { log("show: ${'$'}it") },
    onFocus = { log("focus") },
    onBlur = { log("blur") },
)""",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            var logs by remember { mutableStateOf(listOf<String>()) }
            fun log(message: String) {
                logs = (logs + message).takeLast(5)
            }
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NSelect(
                    options = songOptions,
                    value = value,
                    onValueChange = {
                        value = it
                        log("update:value -> $it")
                    },
                    onUpdateShow = { log("update:show -> $it") },
                    onFocus = { log("focus") },
                    onBlur = { log("blur") },
                    onClear = { log("clear") },
                    placeholder = "Please select a song",
                    clearable = true,
                )
                NText("Event log", size = 12.sp, depth = 3)
                if (logs.isEmpty()) {
                    NText("Interact with the select above.", size = 12.sp, depth = 3)
                } else {
                    logs.forEach { entry ->
                        NText(entry, size = 12.sp, depth = 3)
                    }
                }
            }
        }

        DemoSection(
            title = "Filterable",
            description = "Type to narrow the options. Arrow keys move the highlight, Enter " +
                "confirms and Escape closes the menu.",
        ) {
            var single by remember { mutableStateOf<Any?>(null) }
            var many by remember { mutableStateOf<Any?>(listOf<Any>()) }
            Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NSelect(
                    options = songOptions,
                    value = single,
                    onValueChange = { single = it },
                    placeholder = "Please select a song",
                    filterable = true,
                    clearable = true,
                )
                NSelect(
                    options = songOptions,
                    value = many,
                    onValueChange = { many = it },
                    placeholder = "Please select songs",
                    multiple = true,
                    filterable = true,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Tag",
            description = "With tag enabled, pressed Enter creates a value from the typed " +
                "text when no option matches.",
        ) {
            var single by remember { mutableStateOf<Any?>(null) }
            var many by remember { mutableStateOf<Any?>(emptyList<Any>()) }
            Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NSelect(
                    options = songOptions,
                    value = many,
                    onValueChange = { many = it },
                    placeholder = "Type and press Enter",
                    multiple = true,
                    filterable = true,
                    tag = true,
                    clearable = true,
                )
                NSelect(
                    options = songOptions,
                    value = single,
                    onValueChange = { single = it },
                    placeholder = "Single tag select",
                    filterable = true,
                    tag = true,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Menu width",
            description = "The menu matches the trigger width by default; pass menuWidth to " +
                "give it a fixed width.",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            Column(Modifier.width(240.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NSelect(
                    options = songOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Match trigger width",
                    clearable = true,
                )
                NSelect(
                    options = songOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Fixed 400dp menu",
                    menuWidth = 400.dp,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Remote",
            description = "Remote mode disables local filtering: options are supplied by the " +
                "caller in response to onSearch, while loading is controlled externally.",
        ) {
            var search by remember { mutableStateOf("") }
            var value by remember { mutableStateOf<Any?>(null) }
            var options by remember { mutableStateOf<List<NSelectEntry>>(emptyList()) }
            var loading by remember { mutableStateOf(false) }
            LaunchedEffect(search) {
                if (search.isBlank()) {
                    options = emptyList()
                    loading = false
                } else {
                    loading = true
                    delay(450)
                    options = songOptions
                        .filterIsInstance<NSelectOption>()
                        .filter { it.label.contains(search, ignoreCase = true) }
                        .map { NSelectOption(it.label, it.label) }
                    loading = false
                }
            }
            Column(Modifier.width(300.dp)) {
                NSelect(
                    options = options,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Search songs",
                    filterable = true,
                    remote = true,
                    loading = loading,
                    onSearch = { search = it },
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Remote multiple",
            description = "Remote selection with multiple values keeps the typed filter after " +
                "each pick.",
        ) {
            var search by remember { mutableStateOf("") }
            var values by remember { mutableStateOf<Any?>(emptyList<Any>()) }
            var options by remember { mutableStateOf<List<NSelectEntry>>(emptyList()) }
            var loading by remember { mutableStateOf(false) }
            LaunchedEffect(search) {
                if (search.isBlank()) {
                    options = emptyList()
                    loading = false
                } else {
                    loading = true
                    delay(450)
                    options = songOptions
                        .filterIsInstance<NSelectOption>()
                        .filter { it.label.contains(search, ignoreCase = true) }
                        .map { NSelectOption(it.label, it.label) }
                    loading = false
                }
            }
            Column(Modifier.width(300.dp)) {
                NSelect(
                    options = options,
                    value = values,
                    onValueChange = { values = it },
                    placeholder = "Search songs",
                    multiple = true,
                    filterable = true,
                    remote = true,
                    loading = loading,
                    onSearch = { search = it },
                    clearFilterAfterSelect = false,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Clearable",
            description = "Single and multiple triggers can be cleared, with or without a " +
                "filter input.",
        ) {
            var single by remember { mutableStateOf<Any?>("song2") }
            var many by remember { mutableStateOf<Any?>(listOf<Any>("song1", "song4")) }
            Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NSelect(
                    options = songOptions,
                    value = single,
                    onValueChange = { single = it },
                    clearable = true,
                )
                NSelect(
                    options = songOptions,
                    value = many,
                    onValueChange = { many = it },
                    multiple = true,
                    clearable = true,
                )
                NSelect(
                    options = songOptions,
                    value = single,
                    onValueChange = { single = it },
                    filterable = true,
                    clearable = true,
                )
                NSelect(
                    options = songOptions,
                    value = many,
                    onValueChange = { many = it },
                    multiple = true,
                    filterable = true,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Scroll event",
            description = "onScroll reports the menu position; here new options are appended " +
                "when the bottom is reached.",
        ) {
            var options by remember { mutableStateOf(songOptions.take(10)) }
            var appended by remember { mutableStateOf(0) }
            var value by remember { mutableStateOf<Any?>(null) }
            Column(Modifier.width(300.dp)) {
                NSelect(
                    options = options,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Scroll to load more",
                    clearable = true,
                    onScroll = { position, maxValue ->
                        if (maxValue > 0 && position >= maxValue - 60 && appended < 3) {
                            appended += 1
                            val next = options.size
                            options = options + listOf(
                                NSelectOption("Loaded v1-$next", "v1-$next"),
                                NSelectOption("Loaded v2-$next", "v2-$next"),
                            )
                        }
                    },
                )
            }
        }

        DemoSection(
            title = "Group",
            description = "Options may be grouped under a label. Groups are not selectable.",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            Column(Modifier.width(300.dp)) {
                NSelect(
                    options = groupedOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Grouped options",
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Many options",
            description = "Ten thousand options rendered with virtual scrolling. Only the " +
                "visible rows are composed, so the menu stays responsive.",
            code = """NSelect(
    options = options,
    value = value,
    onValueChange = { value = it },
    virtualScroll = true,
)""",
        ) {
            var single by remember { mutableStateOf<Any?>(null) }
            var many by remember { mutableStateOf<Any?>(emptyList<Any>()) }
            Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NSelect(
                    options = manyOptions,
                    value = single,
                    onValueChange = { single = it },
                    placeholder = "10000 options",
                    clearable = true,
                )
                NSelect(
                    options = manyOptions,
                    value = many,
                    onValueChange = { many = it },
                    placeholder = "10000 options, multiple",
                    multiple = true,
                    maxTagCount = 3,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Custom option",
            description = "renderOption replaces the content of every option row.",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            val theme = LocalNTheme.current
            Column(Modifier.width(320.dp)) {
                NSelect(
                    options = groupedOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Custom rendered options",
                    clearable = true,
                    renderOption = { option, selected ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            option.disabled -> theme.colors.textDisabled
                                            selected -> theme.colors.primary
                                            else -> theme.colors.info
                                        },
                                    ),
                            )
                            Spacer(Modifier.width(8.dp))
                            NText(
                                option.label,
                                color = when {
                                    option.disabled -> theme.colors.textDisabled
                                    selected -> theme.colors.primary
                                    else -> theme.colors.text2
                                },
                                maxLines = 1,
                            )
                        }
                    },
                )
            }
        }

        DemoSection(
            title = "Custom suffix",
            description = "The suffix slot replaces the dropdown arrow.",
        ) {
            var show1 by remember { mutableStateOf(false) }
            var show2 by remember { mutableStateOf(false) }
            var value by remember { mutableStateOf<Any?>(null) }
            Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NSelect(
                    options = songOptions,
                    value = value,
                    onValueChange = { value = it },
                    show = show1,
                    onUpdateShow = { show1 = it },
                    placeholder = "Please select a song",
                    suffix = {
                        NIcon(
                            if (show1) NIcons.Checkmark else NIcons.Filter,
                            size = 16.dp,
                        )
                    },
                )
                NSelect(
                    options = songOptions,
                    value = value,
                    onValueChange = { value = it },
                    filterable = true,
                    show = show2,
                    onUpdateShow = { show2 = it },
                    placeholder = "Please select a song",
                    suffix = {
                        NIcon(NIcons.Search, size = 16.dp)
                    },
                )
            }
        }

        DemoSection(
            title = "Action",
            description = "Header and action slots surround the options.",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            Column(Modifier.width(300.dp)) {
                NSelect(
                    options = songOptions,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Please select a song",
                    clearable = true,
                    header = {
                        NText("Don't know what to put", size = 12.sp, depth = 3)
                    },
                    action = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            NButton(
                                size = NSize.Small,
                                text = true,
                                onClick = { value = null },
                            ) { NText("Clear") }
                            NButton(
                                size = NSize.Small,
                                text = true,
                                onClick = { value = "song2" },
                            ) { NText("Pick Norwegian Wood") }
                        }
                    },
                )
            }
        }

        DemoSection(
            title = "Fallback option",
            description = "A value without a matching option is displayed as its string form " +
                "unless fallbackOption is disabled.",
        ) {
            var fallbackSingle by remember { mutableStateOf<Any?>("ghost-song") }
            var plainSingle by remember { mutableStateOf<Any?>("ghost-song") }
            var fallbackMany by remember {
                mutableStateOf<Any?>(listOf<Any>("song1", "missing-tag"))
            }
            var plainMany by remember {
                mutableStateOf<Any?>(listOf<Any>("song1", "missing-tag"))
            }
            Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NSelect(
                    options = songOptions,
                    value = fallbackSingle,
                    onValueChange = { fallbackSingle = it },
                    placeholder = "Fallback label",
                    clearable = true,
                )
                NSelect(
                    options = songOptions,
                    value = plainSingle,
                    onValueChange = { plainSingle = it },
                    placeholder = "No fallback",
                    fallbackOption = false,
                    clearable = true,
                )
                NSelect(
                    options = songOptions,
                    value = fallbackMany,
                    onValueChange = { fallbackMany = it },
                    multiple = true,
                    placeholder = "Multiple fallback",
                    clearable = true,
                )
                NSelect(
                    options = songOptions,
                    value = plainMany,
                    onValueChange = { plainMany = it },
                    multiple = true,
                    placeholder = "No fallback",
                    fallbackOption = false,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Max tag count",
            description = "Limit the number of visible tags; the rest are summarized as +N.",
        ) {
            var values by remember {
                mutableStateOf<Any?>(listOf<Any>("song1", "song2", "song4", "song5", "song6"))
            }
            Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NSelect(
                    options = songOptions,
                    value = values,
                    onValueChange = { values = it },
                    placeholder = "maxTagCount = 3",
                    multiple = true,
                    maxTagCount = 3,
                    clearable = true,
                )
                NSelect(
                    options = songOptions,
                    value = values,
                    onValueChange = { values = it },
                    placeholder = "No tag limit",
                    multiple = true,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Add tooltip to option",
            description = "renderOption can wrap the row content in a tooltip.",
        ) {
            Column(Modifier.width(320.dp)) {
                NSelect(
                    options = songOptions,
                    placeholder = "Hover an option",
                    renderOption = { option, _ ->
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            NText(
                                option.label,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                            )
                            NPopover(
                                trigger = NTrigger.Hover,
                                placement = NPlacement.Top,
                                content = {
                                    NText("Rubber Soul - ${option.label}", size = 12.sp)
                                },
                            ) {
                                NIcon(NIcons.Info, size = 14.dp)
                            }
                        }
                    },
                )
            }
        }

        DemoSection(
            title = "Select person",
            description = "renderOption and renderTag turn the select into a people picker.",
        ) {
            val people = listOf(
                NSelectOption("John Lennon", "john"),
                NSelectOption("Paul McCartney", "paul"),
                NSelectOption("George Harrison", "george"),
                NSelectOption("Ringo Starr", "ringo"),
            )
            var single by remember { mutableStateOf<Any?>("john") }
            var many by remember { mutableStateOf<Any?>(listOf<Any>("john", "paul")) }
            val renderOption: @Composable (NSelectOption, Boolean) -> Unit = { option, _ ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NAvatar(text = option.label.take(1), round = true, size = 22.dp)
                    Spacer(Modifier.width(10.dp))
                    NText(option.label)
                }
            }
            val renderTag: @Composable (NSelectOption?, Any, String, () -> Unit) -> Unit =
                { _, _, label, close ->
                    NTag(
                        text = label,
                        round = true,
                        closable = true,
                        size = NSize.Small,
                        onClose = close,
                    )
                }
            Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NSelect(
                    options = people,
                    value = single,
                    onValueChange = { single = it },
                    renderOption = renderOption,
                    renderTag = renderTag,
                    clearable = true,
                )
                NSelect(
                    options = people,
                    value = single,
                    onValueChange = { single = it },
                    filterable = true,
                    renderOption = renderOption,
                    renderTag = renderTag,
                    clearable = true,
                )
                NSelect(
                    options = people,
                    value = many,
                    onValueChange = { many = it },
                    multiple = true,
                    renderOption = renderOption,
                    renderTag = renderTag,
                    clearable = true,
                )
                NSelect(
                    options = people,
                    value = many,
                    onValueChange = { many = it },
                    multiple = true,
                    filterable = true,
                    renderOption = renderOption,
                    renderTag = renderTag,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Customize tag rendering",
            description = "renderTag replaces each selected tag; the demo colors tags by the " +
                "value's meaning.",
        ) {
            val tasks = listOf(
                NSelectOption("Daze today", "value1"),
                NSelectOption("Work is not finished", "value2"),
                NSelectOption("Work overtime in the evening", "value3"),
            )
            var values by remember { mutableStateOf<Any?>(emptyList<Any>()) }
            Column(Modifier.width(320.dp)) {
                NSelect(
                    options = tasks,
                    value = values,
                    onValueChange = { values = it },
                    multiple = true,
                    placeholder = "Pick some tasks",
                    clearable = true,
                    renderTag = { _, value, label, close ->
                        val type = when (value) {
                            "value1" -> NStatus.Success
                            "value2" -> NStatus.Warning
                            else -> NStatus.Error
                        }
                        NTag(
                            text = label,
                            type = type,
                            closable = true,
                            size = NSize.Small,
                            onClose = close,
                        )
                    },
                )
            }
        }

        DemoSection(
            title = "Focus",
            description = "focusRequester lets the caller focus the selection field; the focus " +
                "manager can blur it again.",
        ) {
            val focusRequester = remember { FocusRequester() }
            val focusManager = LocalFocusManager.current
            var focusing by remember { mutableStateOf(false) }
            LaunchedEffect(focusing) {
                if (focusing) {
                    focusRequester.requestFocus()
                    delay(1000)
                    focusManager.clearFocus()
                    focusing = false
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NButton(onClick = { focusing = true }) {
                    NText("Focus then blur in 1 second")
                }
                NSelect(
                    modifier = Modifier.width(200.dp),
                    options = songOptions,
                    placeholder = "Please select a song",
                    focusRequester = focusRequester,
                )
            }
        }

        DemoSection(
            title = "Tag input",
            description = "A permanently closed, filterable, multiple tag select works as a " +
                "tag input box.",
            code = """NSelect(
    value = values,
    onValueChange = { values = it },
    multiple = true,
    filterable = true,
    tag = true,
    show = false,
    showArrow = false,
)""",
        ) {
            var values by remember { mutableStateOf<Any?>(emptyList<Any>()) }
            Column(Modifier.width(320.dp)) {
                NSelect(
                    options = emptyList(),
                    value = values,
                    onValueChange = { values = it },
                    placeholder = "Input, press enter to create tag",
                    multiple = true,
                    filterable = true,
                    tag = true,
                    show = false,
                    showArrow = false,
                    clearable = true,
                )
            }
        }

        DemoSection(
            title = "Custom field",
            description = "Domain objects are adapted into options explicitly; the typed " +
                "options replace label-field/value-field configuration.",
        ) {
            var value by remember { mutableStateOf<Any?>(null) }
            val options = customSongs.map { NSelectOption(it.title, it.id) }
            Column(Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NSelect(
                    options = options,
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Custom data class",
                    clearable = true,
                )
                NText(
                    "value: ${value ?: "none"}",
                    size = 12.sp,
                    depth = 3,
                )
            }
        }
    }
}
