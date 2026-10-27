package cn.enaium.naiveui.sample.gallery.feedback

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NResult
import cn.enaium.naiveui.components.NResultSize
import cn.enaium.naiveui.components.NResultStatus
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NFlex
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage

@Composable
fun ResultDocs() {
    DocsPage(
        title = "Result",
        description = "Feedback page for the outcome of an operation, with built-in and custom illustrations.",
    ) {
        DemoSection(
            title = "Basic statuses",
            description = "Result supports info, success, warning and error statuses.",
        ) {
            NFlex(wrap = true, size = 32.dp) {
                ResultCard(
                    status = NResultStatus.Info,
                    title = "Information",
                    description = "In this era, info is about money and money is about info.",
                    action = "I Want Information",
                )
                ResultCard(
                    status = NResultStatus.Success,
                    title = "Success",
                    description = "Child of failure.",
                    action = "I Like It",
                )
                ResultCard(
                    status = NResultStatus.Warning,
                    title = "Warning",
                    description = "People seldom see it until it comes true.",
                    action = "Hmm, Truly Sad",
                )
                ResultCard(
                    status = NResultStatus.Error,
                    title = "Error",
                    description = "It's red.",
                    action = "I Don't Like It",
                )
            }
        }
        DemoSection(
            title = "HTTP statuses",
            description = "403, 404, 418 and 500 render illustrated status icons.",
        ) {
            NFlex(wrap = true, size = 32.dp) {
                ResultCard(
                    status = NResultStatus.Forbidden403,
                    title = "403 Forbidden",
                    description = "Some of the doors are always close to you.",
                    action = "Take It Easy",
                )
                ResultCard(
                    status = NResultStatus.NotFound404,
                    title = "404 Not Found",
                    description = "You know life is always ridiculous.",
                    action = "Find Something Funny",
                )
                ResultCard(
                    status = NResultStatus.Teapot418,
                    title = "418 I'm a Teapot",
                    description = "In Chinese, teapot is a kind of 'Beiju', which means 'tragedy'.",
                    action = "Take the Truth",
                )
                ResultCard(
                    status = NResultStatus.ServerError500,
                    title = "500 Server Error",
                    description = "Server error may prove that you need hiring more developers.",
                    action = "Spread Money Out",
                )
            }
        }
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
            title = "Custom illustration",
            description = "The icon slot accepts any composable.",
        ) {
            NResult(
                title = "Lpsum",
                description = "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet.",
                size = NResultSize.Small,
                icon = {
                    NText(
                        "Custom icon content",
                        Modifier.width(240.dp),
                    )
                },
            )
        }
        DemoSection(
            title = "Content and footer",
            description = "Put additional information in the body and actions in the footer.",
        ) {
            NResult(
                status = NResultStatus.Success,
                title = "Upload complete",
                description = "Your files are ready to share.",
                size = NResultSize.Small,
                content = {
                    NText("3 files · 12.4 MB · shared with your team")
                },
                extra = {
                    NFlex(size = 12.dp) {
                        NButton(type = cn.enaium.naiveui.components.NButtonType.Primary) {
                            NText("Open")
                        }
                        NButton { NText("Share") }
                    }
                },
            )
        }
    }
}

@Composable
private fun ResultCard(
    status: NResultStatus,
    title: String,
    description: String,
    action: String,
) {
    NResult(
        modifier = Modifier.width(280.dp),
        status = status,
        title = title,
        description = description,
        size = NResultSize.Small,
        extra = {
            NButton { NText(action) }
        },
    )
}
