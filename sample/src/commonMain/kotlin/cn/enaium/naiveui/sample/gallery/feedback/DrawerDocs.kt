package cn.enaium.naiveui.sample.gallery.feedback

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NDrawer
import cn.enaium.naiveui.components.NDrawerPlacement
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize

@Composable
fun DrawerDocs() {
    DocsPage(
        title = "Drawer",
        description = "Drawer slides a panel in from any window edge. It supports resizing, header/footer slots, " +
            "nested drawers, long scrollable bodies and an embedded mode for a custom display area.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Choose one of the four placements; `width` applies to side drawers and `height` to top/bottom ones.",
            code = "var show by remember { mutableStateOf(false) }\nNDrawer(show, onUpdateShow = { show = it }, title = \"Stoner\", width = 502.dp) {\n    NText(\"Drawer content\")\n}",
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
                width = 502.dp,
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
            title = "Closable",
            description = "The header close affordance is controlled by `closable`.",
        ) {
            var show by remember { mutableStateOf(false) }
            NButton(onClick = { show = true }) { NText("Open") }
            NDrawer(
                show = show,
                onUpdateShow = { show = it },
                title = "Stoner",
                closable = true,
                width = 502.dp,
            ) {
                NText("Click the close icon in the header to dismiss this drawer.")
            }
        }

        DemoSection(
            title = "Multiple drawers",
            description = "A drawer can open another drawer from its own footer.",
        ) {
            var showOuter by remember { mutableStateOf(false) }
            var showInner by remember { mutableStateOf(false) }
            NButton(onClick = { showOuter = true }) { NText("Come on!") }
            NDrawer(
                show = showOuter,
                onUpdateShow = { showOuter = it },
                title = "Outer Drawer",
                width = 502.dp,
                footer = {
                    NButton(size = NSize.Small, onClick = { showInner = true }) { NText("Come on Again!") }
                },
            ) {
                NText("Stoner is a 1965 novel by the American writer John Williams.")
            }
            NDrawer(
                show = showInner,
                onUpdateShow = { showInner = it },
                title = "Inner Drawer",
                width = 251.dp,
            ) {
                NText("Stoner is a 1965 novel by the American writer John Williams.")
            }
        }

        DemoSection(
            title = "Resizable drawer",
            description = "Set `resizable = true` and drag the inner edge. Width/height start from `width`/`height`.",
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
                width = 502.dp,
                height = 320.dp,
                resizable = true,
                title = "Resizable",
            ) {
                NText("Drag the thin inner edge of this drawer to resize it.")
            }
        }

        DemoSection(
            title = "Scroll content",
            description = "Long content scrolls while the header and footer stay in place.",
        ) {
            var show by remember { mutableStateOf(false) }
            NButton(onClick = { show = !show }) { NText("Come on!") }
            NDrawer(
                show = show,
                onUpdateShow = { show = it },
                title = "Stoner",
                width = 480.dp,
                footer = {
                    NButton(size = NSize.Small, onClick = { show = false }) { NText("Done") }
                },
            ) {
                (1..18).forEach { chapter ->
                    NText(
                        "Chapter $chapter. William Stoner is born on a small farm in 1891. One day his father " +
                            "suggests he should attend the University of Missouri to study agriculture. Stoner agrees " +
                            "but, while studying a compulsory literature course, he quickly falls in love with " +
                            "literary studies.",
                    )
                }
            }
        }

        DemoSection(
            title = "Custom header and bottom content",
            description = "`header` and `footer` replace the default title row and action area.",
        ) {
            var show by remember { mutableStateOf(false) }
            NButton(onClick = { show = true }) { NText("Open") }
            NDrawer(
                show = show,
                onUpdateShow = { show = it },
                header = { NText("Header", size = 16.sp, strong = true) },
                closable = false,
                width = 502.dp,
                footer = {
                    NButton(size = NSize.Small, onClick = { show = false }) { NText("Footer") }
                },
            ) {
                NText("The body is between the custom header and footer.")
            }
        }

        DemoSection(
            title = "Customize display area",
            description = "`embedded = true` renders the drawer inside its parent area instead of the whole window.",
        ) {
            val theme = LocalNTheme.current
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
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clipToBounds()
                    .border(1.dp, theme.colors.border, RoundedCornerShape(theme.borderRadius)),
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    NText("Target Area")
                }
                NDrawer(
                    show = show,
                    onUpdateShow = { show = it },
                    embedded = true,
                    placement = placement,
                    width = 200.dp,
                    height = 200.dp,
                    title = "Stoner",
                ) {
                    NText("Stoner is a 1965 novel by the American writer John Williams.")
                }
            }
        }
    }
}
