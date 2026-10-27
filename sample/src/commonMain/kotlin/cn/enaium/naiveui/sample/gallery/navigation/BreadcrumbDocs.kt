package cn.enaium.naiveui.sample.gallery.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NBreadcrumb
import cn.enaium.naiveui.components.NBreadcrumbItem
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NPopoverSurface
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

@Composable
private fun DropdownCrumb(
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    Box {
        NText(
            value,
            modifier = Modifier
                .clip(RoundedCornerShape(3.dp))
                .clickable { open = !open }
                .padding(horizontal = 2.dp, vertical = 1.dp),
        )
        NPopoverSurface(
            visible = open,
            onDismiss = { open = false },
            placement = NPlacement.BottomStart,
            padding = PaddingValues(4.dp),
        ) {
            Column {
                options.forEach { option ->
                    NButton(
                        onClick = {
                            onSelect(option)
                            open = false
                        },
                        text = true,
                    ) { NText(option) }
                }
            }
        }
    }
}

@Composable
fun BreadcrumbDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Breadcrumb",
        description = "Breadcrumb shows the current location in a hierarchy, with custom separators and arbitrary item content.",
    ) {
        DemoSection(
            title = "Basic",
            code = """
NBreadcrumb {
    NBreadcrumbItem { NText("Home") }
    NBreadcrumbItem { NText("Account") }
    NBreadcrumbItem { NText("Category") }
}
            """.trimIndent(),
        ) {
            NBreadcrumb {
                NBreadcrumbItem {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        NIcon(NIcons.MenuIcon, size = 14.dp, color = theme.colors.text3)
                        NText("Home")
                    }
                }
                NBreadcrumbItem {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        NIcon(NIcons.Person, size = 14.dp, color = theme.colors.text3)
                        NText("Account")
                    }
                }
                NBreadcrumbItem {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        NIcon(NIcons.File, size = 14.dp, color = theme.colors.text3)
                        NText("Category")
                    }
                }
            }
        }

        DemoSection(
            title = "Custom item content",
            description = "Items accept arbitrary content. Here each dropdown changes the visible breadcrumb label.",
        ) {
            var first by remember { mutableStateOf("I'm ok") }
            var second by remember { mutableStateOf("I'm ok") }
            NBreadcrumb {
                NBreadcrumbItem {
                    DropdownCrumb(
                        value = first,
                        options = listOf("David Tao", "Black Tangerine"),
                        onSelect = { first = it },
                    )
                }
                NBreadcrumbItem {
                    DropdownCrumb(
                        value = second,
                        options = listOf("Small Town Girl", "Ordinary Friend"),
                        onSelect = { second = it },
                    )
                }
                NBreadcrumbItem { NText("Category") }
            }
        }

        DemoSection(
            title = "Customize separator",
            description = "Pass `separator` to replace the default slash.",
        ) {
            NBreadcrumb(separator = ">") {
                NBreadcrumbItem {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        NIcon(NIcons.MenuIcon, size = 14.dp, color = theme.colors.text3)
                        NText("Home")
                    }
                }
                NBreadcrumbItem { NText("Account") }
                NBreadcrumbItem { NText("Category") }
            }
        }

        DemoSection(
            title = "Separator per item",
            description = "Each item can set its own separator string or separator slot.",
        ) {
            NBreadcrumb {
                NBreadcrumbItem(separator = ">") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        NIcon(NIcons.MenuIcon, size = 14.dp, color = theme.colors.text3)
                        NText("Home")
                    }
                }
                NBreadcrumbItem(separatorContent = { NText("~", color = theme.colors.text3) }) {
                    NText("Account")
                }
                NBreadcrumbItem { NText("Category") }
            }
        }

        DemoSection(
            title = "Clickable items",
            description = "Provide `onClick` (or `href`) on the items that should navigate.",
        ) {
            var clicked by remember { mutableStateOf("nothing") }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NBreadcrumb {
                    NBreadcrumbItem(onClick = { clicked = "Home" }) {
                        NText("Home")
                    }
                    NBreadcrumbItem(onClick = { clicked = "Projects" }) {
                        NText("Projects")
                    }
                    NBreadcrumbItem { NText("Current project") }
                }
                NText("Last clicked: $clicked", color = theme.colors.text3)
            }
        }

        DemoSection(
            title = "Hide a separator",
            description = "Set `showSeparator = false` when a custom layout should not add one.",
        ) {
            NBreadcrumb {
                NBreadcrumbItem(showSeparator = false) { NText("No separator") }
                NBreadcrumbItem { NText("After it") }
            }
        }
    }
}
