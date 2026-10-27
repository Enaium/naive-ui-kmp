package cn.enaium.naiveui.sample.gallery.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NDrawer
import cn.enaium.naiveui.components.NDrawerPlacement
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

@Composable
fun DrawerDocs() {
    DocsPage(
        title = "Drawer",
        description = "Drawer slides a panel in from any window edge. The panel animates on open and close, " +
            "supports a title, close affordance, footer and scrollable body.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Choose one of the four placements; `width` applies to side drawers and `height` to top/bottom ones.",
            code = "var show by remember { mutableStateOf(false) }\nNDrawer(\n    show = show,\n    onUpdateShow = { show = it },\n    placement = NDrawerPlacement.Right,\n    width = 400.dp,\n    title = \"Stoner\",\n) {\n    NText(\"Drawer content\")\n}",
        ) {
            var show by remember { mutableStateOf(false) }
            var placement by remember { mutableStateOf(NDrawerPlacement.Right) }
            NSpace(size = 8.dp, wrap = true) {
                NDrawerPlacement.entries.forEach { value ->
                    NButton(onClick = {
                        placement = value
                        show = true
                    }) { NText(value.name) }
                }
            }
            NDrawer(
                show = show,
                onUpdateShow = { show = it },
                placement = placement,
                width = 420.dp,
                height = 260.dp,
                title = "Stoner",
            ) {
                NText(
                    "Stoner is a 1965 novel by the American writer John Williams. " +
                        "It was reissued in 2003 and again in 2006.",
                )
            }
        }

        DemoSection(
            title = "Closable & footer",
            description = "`closable = false` hides the close icon; use the footer slot for actions.",
        ) {
            var show by remember { mutableStateOf(false) }
            NButton(onClick = { show = true }) { NText("Open Drawer") }
            NDrawer(
                show = show,
                onUpdateShow = { show = it },
                title = "Closable",
                closable = false,
                width = 380.dp,
                footer = {
                    NButton(size = NSize.Small, onClick = { show = false }) { NText("Close") }
                },
            ) {
                NText("This drawer has no close icon, so the footer button dismisses it.")
            }
        }

        DemoSection(
            title = "Scrollable",
            description = "Long content scrolls inside the drawer while the header and footer stay fixed.",
        ) {
            var show by remember { mutableStateOf(false) }
            NButton(onClick = { show = true }) { NText("Open Long Drawer") }
            NDrawer(
                show = show,
                onUpdateShow = { show = it },
                title = "Scrollable",
                width = 420.dp,
                footer = {
                    NButton(size = NSize.Small, onClick = { show = false }) { NText("Done") }
                },
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..40).forEach { line ->
                        NText("Drawer line $line")
                    }
                }
            }
        }

        DemoSection(
            title = "Multiple drawers",
            description = "A drawer can open another drawer from its own content.",
        ) {
            var showOuter by remember { mutableStateOf(false) }
            var showInner by remember { mutableStateOf(false) }
            NButton(onClick = { showOuter = true }) { NText("Come on!") }
            NDrawer(
                show = showOuter,
                onUpdateShow = { showOuter = it },
                title = "Outer Drawer",
                width = 460.dp,
            ) {
                NText("The outer drawer hosts a second drawer.")
                NButton(onClick = { showInner = true }) { NText("Come on Again!") }
                NDrawer(
                    show = showInner,
                    onUpdateShow = { showInner = it },
                    title = "Inner Drawer",
                    width = 300.dp,
                ) {
                    NText("Nested drawers stack above the outer one.")
                }
            }
        }

        DemoSection(
            title = "Bottom placement",
            description = "Top and bottom drawers use `height` and span the full window width.",
        ) {
            var show by remember { mutableStateOf(false) }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NButton(onClick = { show = true }) { NText("Open Bottom Drawer") }
                NText("A bottom drawer is useful for compact sheets.", size = 13.sp)
            }
            NDrawer(
                show = show,
                onUpdateShow = { show = it },
                placement = NDrawerPlacement.Bottom,
                height = 220.dp,
                title = "Bottom",
            ) {
                NText("The panel slides up from the bottom edge of the window.")
            }
        }
    }
}
