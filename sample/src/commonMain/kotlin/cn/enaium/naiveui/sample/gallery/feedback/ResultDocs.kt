package cn.enaium.naiveui.sample.gallery.feedback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NResult
import cn.enaium.naiveui.components.NResultSize
import cn.enaium.naiveui.components.NResultStatus
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage

@Composable
fun ResultDocs() {
    DocsPage(
        title = "Result",
        description = "Feedback page for the outcome of an operation, with built-in and custom illustrations.",
    ) {
        ResultStatusDemo(
            sectionTitle = "404",
            status = NResultStatus.NotFound404,
            title = "404 Not Found",
            description = "You know life is always ridiculous.",
            action = "Find Something Funny",
        )
        ResultStatusDemo(
            sectionTitle = "403",
            status = NResultStatus.Forbidden403,
            title = "403 Forbidden",
            description = "Some of the doors are always close to you.",
            action = "Take It Easy",
        )
        ResultStatusDemo(
            sectionTitle = "500",
            status = NResultStatus.ServerError500,
            title = "500 Server Error",
            description = "Server error may prove that you need hiring more developers.",
            action = "Spread Money Out",
        )
        ResultStatusDemo(
            sectionTitle = "418",
            status = NResultStatus.Teapot418,
            title = "418 I'm a Teapot",
            description = "In Chinese, teapot is a kind of 'Beiju', which means 'tragedy'.",
            action = "It Is Easy to Take the Truth",
        )
        ResultStatusDemo(
            sectionTitle = "Info",
            status = NResultStatus.Info,
            title = "Information",
            description = "In this era, info is about money and money is about info.",
            action = "I Want Information",
        )
        ResultStatusDemo(
            sectionTitle = "Success",
            status = NResultStatus.Success,
            title = "Success",
            description = "Child of failure.",
            action = "I Like It",
        )
        ResultStatusDemo(
            sectionTitle = "Warning",
            status = NResultStatus.Warning,
            title = "Warning",
            description = "People seldom see it until it comes true.",
            action = "Hmm, Truly Sad, Isn't It?",
        )
        ResultStatusDemo(
            sectionTitle = "Error",
            status = NResultStatus.Error,
            title = "Error",
            description = "It's red.",
            action = "I Don't Like It",
        )
        DemoSection(
            title = "Size",
            description = "Result can be small, medium, large or huge.",
        ) {
            NResult(
                status = NResultStatus.NotFound404,
                title = "Huge! Huge!",
                description = "Like this",
                size = NResultSize.Huge,
                extra = {
                    NButton { NText("Just So So") }
                },
            )
        }
        DemoSection(
            title = "Customizing icon",
            description = "The icon slot accepts any composable; content and footer slots are also available.",
            code = "NResult(title = \"Lpsum\", icon = { NIcon(NIcons.Photo, size = 80.dp) })",
        ) {
            NResult(
                status = NResultStatus.Info,
                title = "Lpsum",
                description = "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet.",
                size = NResultSize.Small,
                icon = { NIcon(NIcons.Photo, size = 80.dp) },
                content = {
                    NText("Custom body content rendered below the description.")
                },
                extra = {
                    NButton { NText("Custom footer action") }
                },
            )
        }
    }
}

@Composable
private fun ResultStatusDemo(
    sectionTitle: String,
    status: NResultStatus,
    title: String,
    description: String,
    action: String,
) {
    var clicked by remember { mutableStateOf(false) }
    DemoSection(title = sectionTitle) {
        NResult(
            status = status,
            title = title,
            description = description,
            extra = {
                NButton(onClick = { clicked = true }) {
                    NText(if (clicked) "Done" else action)
                }
            },
        )
    }
}
