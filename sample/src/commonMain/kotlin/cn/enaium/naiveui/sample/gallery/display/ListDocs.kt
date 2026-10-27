package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NList
import cn.enaium.naiveui.components.NListItem
import cn.enaium.naiveui.components.NThing
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus

@Composable
fun ListDocs() {
    DocsPage(
        title = "List",
        description = "List displays a series of items with optional header, footer, dividers, hover and click feedback.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Items can define prefix, suffix and arbitrary content. Header and footer are optional slots.",
            code = """
NList(
    header = { NText("Header") },
    footer = { NText("Footer") },
) {
    NListItem(
        prefix = { NButton(onClick = {}) { NText("Prefix") } },
        suffix = { NButton(onClick = {}) { NText("Suffix") } },
    ) {
        NThing(title = "Thing", description = "description")
    }
}
""",
        ) {
            NList(
                header = { NText("Header") },
                footer = { NText("Footer") },
            ) {
                NListItem(
                    prefix = { NButton(onClick = {}) { NText("Prefix") } },
                    suffix = { NButton(onClick = {}) { NText("Suffix") } },
                ) {
                    NThing(
                        title = "Thing",
                        titleExtra = "extra",
                        description = "description",
                        content = "Biu\nBiu\nBiu",
                    )
                }
                NListItem {
                    NThing(
                        title = "Another thing",
                        titleExtra = "extra",
                        description = "description",
                        content = "A list item can hold any content.",
                    )
                }
            }
        }

        DemoSection(
            title = "Hoverable",
            description = "Hoverable applies a hover background. Pass onClick to an item to make it interactive; clickable shows a hand cursor and enables press feedback.",
            code = """
NList(hoverable = true, clickable = true) {
    NListItem(onClick = { count++ }) {
        NThing(title = "Better Late Than Never", content = "...")
    }
}
""",
        ) {
            var clicked by remember { mutableStateOf(0) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NList(hoverable = true, clickable = true) {
                    NListItem(onClick = { clicked++ }) {
                        NThing(
                            title = "Better Late Than Never",
                            descriptionContent = {
                                NTag(
                                    text = "Tag A",
                                    type = NStatus.Info,
                                    bordered = false,
                                    size = NSize.Small,
                                )
                            },
                            content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                        )
                    }
                    NListItem(onClick = { clicked++ }) {
                        NThing(
                            title = "Lorem Ipsum",
                            descriptionContent = {
                                NTag(
                                    text = "Tag B",
                                    type = NStatus.Info,
                                    bordered = false,
                                    size = NSize.Small,
                                )
                            },
                            content = "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                        )
                    }
                }
                NText("Clicked $clicked time(s)")
            }
        }

        DemoSection(
            title = "Border",
            description = "The bordered variant adds an outer border and inner padding. Disable showDivider to remove lines between items.",
            code = """
NList(bordered = true) { ... }
NList(bordered = true, showDivider = false) { ... }
""",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                NList(
                    bordered = true,
                    header = { NText("Bordered header") },
                    footer = { NText("Bordered footer") },
                ) {
                    NListItem {
                        NThing(title = "With divider", description = "Items are separated by a 1dp line.")
                    }
                    NListItem(suffix = { NButton(onClick = {}) { NText("Action") } }) {
                        NThing(title = "Second item", description = "The suffix slot stays at the end of the row.")
                    }
                }
                NList(bordered = true, showDivider = false) {
                    NListItem {
                        NThing(title = "No divider", description = "showDivider = false removes the item dividers.")
                    }
                    NListItem {
                        NThing(title = "Compact list", description = "Useful when items already have their own separators.")
                    }
                }
            }
        }

        DemoSection(
            title = "Size",
            description = "Size adjusts the item paddings and text size. It combines with bordered and hoverable.",
            code = """
NList(size = NSize.Small, bordered = true) { ... }
NList(size = NSize.Large, bordered = true) { ... }
""",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                NList(size = NSize.Small, bordered = true) {
                    NListItem {
                        NThing(title = "Small", description = "Compact padding and text.")
                    }
                    NListItem {
                        NThing(title = "Second item", description = "List size is inherited by items.")
                    }
                }
                NList(size = NSize.Large, bordered = true) {
                    NListItem {
                        NThing(title = "Large", description = "Roomier padding and larger text.")
                    }
                    NListItem {
                        NThing(title = "Second item", description = "List size is inherited by items.")
                    }
                }
            }
        }
    }
}
