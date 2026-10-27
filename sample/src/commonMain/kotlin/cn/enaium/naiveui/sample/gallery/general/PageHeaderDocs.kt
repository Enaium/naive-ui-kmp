package cn.enaium.naiveui.sample.gallery.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NCard
import cn.enaium.naiveui.components.NPageHeader
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NStatus

@Composable
fun PageHeaderDocs() {
    DocsPage(
        title = "Page Header",
        description = "A header for a page or a detail view, with back navigation, " +
            "subtitle, extra actions, body and footer.",
    ) {
        DemoSection(
            title = "Basic",
            description = "`onBack` renders the back arrow and receives the click.",
            code = """
NPageHeader(
    title = "Anyway.FM",
    subtitle = "A podcast to improve designs",
    onBack = { ... },
) {
    NText("Page content goes here.")
}
""".trimIndent(),
        ) {
            var backPressed by remember { mutableStateOf(0) }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NPageHeader(
                    title = "Anyway.FM",
                    subtitle = "A podcast to improve designs",
                    onBack = { backPressed += 1 },
                ) {
                    NText("Page content goes here.")
                }
                NText("Back pressed $backPressed time(s)")
            }
        }

        DemoSection(
            title = "Extra",
            description = "The `extra` slot renders actions at the end of the title row.",
        ) {
            var refreshed by remember { mutableStateOf(0) }
            NPageHeader(
                title = "Statistics",
                subtitle = "Episodes, guests and topics",
                extra = {
                    NButton(onClick = { refreshed += 1 }) { NText("Refresh") }
                    NTag("Refreshed $refreshed", type = NStatus.Success)
                },
            ) {
                NSpace(size = 12.dp) {
                    NTag("Episodes 125", type = NStatus.Primary)
                    NTag("Guests 22", type = NStatus.Info)
                    NTag("Topics 83", type = NStatus.Warning)
                }
            }
        }

        DemoSection(
            title = "Footer",
            description = "Use `footer` for meta information below the body.",
        ) {
            NPageHeader(
                title = "Best Collection",
                subtitle = "A curated list of episodes",
                footer = { NText("As of April 3, 2021", size = 13.sp) },
            ) {
                NCard(title = "Episode 12") {
                    NText("Designing with gradients and motion.")
                }
            }
        }
    }
}
