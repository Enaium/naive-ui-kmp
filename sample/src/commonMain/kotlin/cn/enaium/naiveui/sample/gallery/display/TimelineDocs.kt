package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NTimeline
import cn.enaium.naiveui.components.NTimelineItem
import cn.enaium.naiveui.components.NTimelineLineType
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus

@Composable
fun TimelineDocs() {
    DocsPage(
        title = "Timeline",
        description = "Display a sequence of events. Items support semantic types, custom " +
            "accent colors, icons, dashed connectors and a horizontal layout.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Each item accepts a title, content and time. The marker color comes " +
                "from the item type.",
            code = """
                NTimeline(
                    items = listOf(
                        NTimelineItem(content = "Oops"),
                        NTimelineItem(
                            type = NStatus.Success,
                            title = "Success",
                            content = "success content",
                            time = "2018-04-03 20:46",
                        ),
                        NTimelineItem(
                            type = NStatus.Error,
                            title = "Error",
                            content = "error content",
                            time = "2018-04-03 20:46",
                        ),
                    ),
                )
            """.trimIndent(),
        ) {
            NTimeline(items = basicItems())
        }

        DemoSection(
            title = "Types",
            description = "Default, success, error, warning, info and primary types map to the " +
                "theme status colors. A dashed line type can be set per item.",
            code = """
                NTimeline(
                    items = listOf(
                        NTimelineItem(type = NStatus.Info, content = "info"),
                        NTimelineItem(type = NStatus.Success, content = "success"),
                        NTimelineItem(type = NStatus.Warning, content = "warning"),
                        NTimelineItem(
                            type = NStatus.Error,
                            content = "error",
                            lineType = NTimelineLineType.Dashed,
                        ),
                    ),
                )
            """.trimIndent(),
        ) {
            NTimeline(items = typeItems())
        }

        DemoSection(
            title = "Customize icon",
            description = "Provide an icon for a single item or for the whole timeline. Icons " +
                "replace the default circle marker.",
            code = """
                NTimeline(
                    items = listOf(
                        NTimelineItem(
                            title = "Success",
                            content = "success content",
                            icon = { NIcon(NIcons.Time, size = 14.dp) },
                        ),
                    ),
                )
            """.trimIndent(),
        ) {
            NTimeline(items = iconItems())
        }

        DemoSection(
            title = "Horizontal",
            description = "Set horizontal to lay items out in a scrollable row. Connectors run " +
                "from each marker to the next item.",
            code = """
                NTimeline(
                    horizontal = true,
                    items = listOf(
                        NTimelineItem(content = "Oops"),
                        NTimelineItem(type = NStatus.Success, content = "Success content"),
                        NTimelineItem(type = NStatus.Error, content = "Error content"),
                    ),
                )
            """.trimIndent(),
        ) {
            NTimeline(horizontal = true, items = basicItems())
        }

        DemoSection(
            title = "Time",
            description = "The time is rendered below the content in a smaller, muted style.",
            code = """
                NTimeline(
                    items = listOf(
                        NTimelineItem(
                            title = "Version 1.0.0",
                            content = "First public release.",
                            time = "2018-04-03 20:46",
                        ),
                    ),
                )
            """.trimIndent(),
        ) {
            NTimeline(items = timeItems())
        }

        DemoSection(
            title = "Size",
            description = "Use the large size to increase the title font size.",
            code = """
                NTimeline(size = NSize.Large, items = items)
            """.trimIndent(),
        ) {
            NTimeline(size = NSize.Large, items = basicItems())
        }
    }
}

@Composable
private fun basicItems(): List<NTimelineItem> = listOf(
    NTimelineItem(content = "Oops"),
    NTimelineItem(
        type = NStatus.Success,
        title = "Success",
        content = "success content",
        time = "2018-04-03 20:46",
    ),
    NTimelineItem(
        type = NStatus.Error,
        content = "Error content",
        time = "2018-04-03 20:46",
    ),
    NTimelineItem(
        type = NStatus.Warning,
        title = "Warning",
        content = "warning content",
        time = "2018-04-03 20:46",
    ),
    NTimelineItem(
        type = NStatus.Info,
        title = "Info",
        content = "info content",
        time = "2018-04-03 20:46",
        lineType = NTimelineLineType.Dashed,
    ),
    NTimelineItem(content = "Oops"),
)

@Composable
private fun typeItems(): List<NTimelineItem> = listOf(
    NTimelineItem(type = NStatus.Info, title = "Info", content = "info content"),
    NTimelineItem(type = NStatus.Success, title = "Success", content = "success content"),
    NTimelineItem(type = NStatus.Warning, title = "Warning", content = "warning content"),
    NTimelineItem(
        type = NStatus.Error,
        title = "Error",
        content = "error content",
        lineType = NTimelineLineType.Dashed,
    ),
    NTimelineItem(type = NStatus.Primary, title = "Primary", content = "primary content"),
)

@Composable
private fun iconItems(): List<NTimelineItem> = listOf(
    NTimelineItem(
        content = "Oops",
        icon = { NIcon(NIcons.Time, size = 14.dp) },
    ),
    NTimelineItem(
        type = NStatus.Success,
        title = "Success",
        content = "success content",
        time = "2018-04-03 20:46",
        icon = { NIcon(NIcons.Success, size = 14.dp) },
    ),
    NTimelineItem(
        type = NStatus.Error,
        content = "error content",
        time = "2018-04-03 20:46",
        icon = { NIcon(NIcons.Error, size = 14.dp) },
    ),
    NTimelineItem(
        type = NStatus.Warning,
        title = "Warning",
        content = "warning content",
        time = "2018-04-03 20:46",
        icon = { NIcon(NIcons.Warning, size = 14.dp) },
    ),
    NTimelineItem(
        type = NStatus.Info,
        title = "Info",
        content = "info content",
        time = "2018-04-03 20:46",
        icon = { NIcon(NIcons.Info, size = 14.dp) },
    ),
)

@Composable
private fun timeItems(): List<NTimelineItem> = listOf(
    NTimelineItem(
        title = "Version 1.0.0",
        content = "First public release.",
        time = "2018-04-03 20:46",
    ),
    NTimelineItem(
        title = "Version 1.1.0",
        content = "Added tree and timeline components.",
        time = "2018-06-12 09:15",
    ),
    NTimelineItem(
        title = "Version 1.2.0",
        content = "Improved dark theme colors.",
        time = "2018-09-30 18:02",
    ),
)
