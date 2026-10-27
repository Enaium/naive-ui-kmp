package cn.enaium.naiveui.sample.gallery.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NBreadcrumb
import cn.enaium.naiveui.components.NBreadcrumbItem
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

@Composable
fun BreadcrumbDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Breadcrumb",
        description = "Breadcrumb shows the current location in a hierarchy. The last item is emphasized automatically and separators are handled by the parent.",
    ) {
        DemoSection(
            title = "Basic",
            code = """
NBreadcrumb {
    NBreadcrumbItem { NText("Home") }
    NBreadcrumbItem { NText("Account") }
    NBreadcrumbItem { NText("Settings") }
}
            """.trimIndent(),
        ) {
            NBreadcrumb {
                NBreadcrumbItem { NText("Home") }
                NBreadcrumbItem { NText("Account") }
                NBreadcrumbItem { NText("Settings") }
            }
        }

        DemoSection(
            title = "Custom separator",
            description = "Pass `separator` to the breadcrumb to replace the default slash.",
        ) {
            NBreadcrumb(separator = ">") {
                NBreadcrumbItem { NText("Products") }
                NBreadcrumbItem { NText(" electronics ") }
                NBreadcrumbItem { NText("Laptops") }
            }
        }

        DemoSection(
            title = "Separator slot and per-item separator",
            description = "A composable separator can be an icon. Items may override the separator individually.",
        ) {
            NBreadcrumb(
                separatorContent = {
                    NIcon(
                        NIcons.ChevronRight,
                        size = 12.dp,
                        color = theme.colors.text3,
                    )
                },
            ) {
                NBreadcrumbItem { NText("Docs") }
                NBreadcrumbItem(separator = "·") { NText("Navigation") }
                NBreadcrumbItem { NText("Breadcrumb") }
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(end = 2.dp),
                        ) {
                            NIcon(NIcons.MenuIcon, size = 14.dp, color = theme.colors.text3)
                            NText("Home")
                        }
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
