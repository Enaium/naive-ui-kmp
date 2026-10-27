package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NThing
import cn.enaium.naiveui.components.NAvatar
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus

@Composable
fun ThingDocs() {
    DocsPage(
        title = "Thing",
        description = "Thing composes a title, description, avatar, content, footer and action area into one consistent block.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Every region is optional. Avatar, header, headerExtra, description, footer and action are independent slots.",
            code = """
NThing(
    title = "Money",
    description = "Description",
    content = "Money is any item or record accepted as payment...",
    avatar = { NAvatar { NIcon(NIcons.Person) } },
    headerExtra = { NButton(size = NSize.Small) { NText("Edit") } },
    footer = { NText("Footer") },
)
""",
        ) {
            NThing(
                title = "Money",
                titleExtra = "extra",
                description = "Description",
                content = "Money is any item or verifiable record that is generally accepted as payment for goods and services and repayment of debts.",
                avatar = {
                    NAvatar(content = { NIcon(NIcons.Person, size = 20.dp) })
                },
                descriptionContent = {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        NTag(text = "cash", type = NStatus.Info, bordered = false, size = NSize.Small)
                        NTag(text = "record", type = NStatus.Success, bordered = false, size = NSize.Small)
                    }
                },
                headerExtra = {
                    NButton(size = NSize.Small, onClick = {}) { NText("Edit") }
                },
                footer = { NText("Footer") },
                action = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        NButton(size = NSize.Small, onClick = {}) { NText("\$1") }
                        NButton(size = NSize.Small, onClick = {}) { NText("\$10") }
                        NButton(size = NSize.Small, onClick = {}) { NText("\$100") }
                    }
                },
            )
        }

        DemoSection(
            title = "Content indented",
            description = "contentIndented moves the header, description, content, footer and action beside the avatar.",
            code = """
NThing(contentIndented = true, avatar = { NAvatar(text = "M") }, title = "Money") {
    ...
}
""",
        ) {
            NThing(
                contentIndented = true,
                title = "Money",
                description = "Content indented under the avatar",
                content = "The avatar occupies its own column and everything else flows to its right.",
                avatar = {
                    NAvatar(content = { NIcon(NIcons.Person, size = 20.dp) })
                },
                headerExtra = {
                    NButton(size = NSize.Small, onClick = {}) { NText("Action") }
                },
                footer = { NText("Footer") },
            )
        }

        DemoSection(
            title = "Size",
            description = "The size prop scales the base font and title.",
            code = """
NThing(size = NSize.Small, title = "Small")
NThing(size = NSize.Large, title = "Large")
""",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                NThing(
                    size = NSize.Small,
                    title = "Small",
                    description = "A compact thing.",
                    content = "Content at the small size.",
                )
                NThing(
                    size = NSize.Medium,
                    title = "Medium",
                    description = "The default size.",
                    content = "Content at the medium size.",
                )
                NThing(
                    size = NSize.Large,
                    title = "Large",
                    description = "A more prominent thing.",
                    content = "Content at the large size.",
                )
            }
        }
    }
}
