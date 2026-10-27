package cn.enaium.naiveui.sample.gallery.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NBackTop
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

@Composable
private fun WideBackTopLabel(text: String) {
    val theme = LocalNTheme.current
    Box(
        Modifier.width(200.dp).height(40.dp),
        contentAlignment = Alignment.Center,
    ) {
        NText(text, color = theme.colors.text2)
    }
}

@Composable
private fun BackTopPlayground(
    visibilityHeight: Dp,
    right: Dp = 40.dp,
    bottom: Dp = 40.dp,
    content: (@Composable () -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val scrollState = rememberScrollState()
    Box(
        Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(theme.borderRadius))
            .background(theme.colors.body)
            .nBorder(theme.colors.border, 1.dp, theme.borderRadius),
    ) {
        NScrollbar(Modifier.fillMaxSize(), scrollState = scrollState) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                NText(
                    "Scroll down more than ${visibilityHeight.value.toInt()}dp to reveal the button.",
                    color = theme.colors.text3,
                )
                Spacer(Modifier.height(700.dp))
                NText("Bottom of the container.", color = theme.colors.text3)
            }
        }
        NBackTop(
            scrollState = scrollState,
            visibilityHeight = visibilityHeight,
            right = right,
            bottom = bottom,
            content = content,
        )
    }
}

@Composable
fun BackTopDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Back Top",
        description = "Back Top floats above a scroll container and returns it to the top with an animated scroll.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Pass the ScrollState of the container. The button appears after 180dp of scrolling.",
            code = """
val scrollState = rememberScrollState()
Box {
    NScrollbar(scrollState = scrollState) { /* long content */ }
    NBackTop(scrollState = scrollState, right = 100.dp)
}
            """.trimIndent(),
        ) {
            BackTopPlayground(visibilityHeight = 180.dp, right = 100.dp)
        }

        DemoSection(
            title = "Visibility height",
            description = "Raise `visibilityHeight` and position the button with a custom wide label.",
        ) {
            BackTopPlayground(
                visibilityHeight = 300.dp,
                bottom = 100.dp,
                content = { WideBackTopLabel("Visibility Height: 300px") },
            )
        }

        DemoSection(
            title = "Change position",
            description = "Adjust `right` and `bottom` to place the button elsewhere in its container.",
        ) {
            BackTopPlayground(
                visibilityHeight = 180.dp,
                right = 40.dp,
                bottom = 160.dp,
                content = { WideBackTopLabel("Change Position") },
            )
        }

        DemoSection(
            title = "Target container",
            description = "Back Top listens to the explicitly passed ScrollState, which can belong to a small inner container.",
        ) {
            val scrollState = rememberScrollState()
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(theme.borderRadius))
                    .background(theme.colors.body)
                    .nBorder(theme.colors.border, 1.dp, theme.borderRadius),
            ) {
                Column(Modifier.fillMaxSize().padding(12.dp)) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .clip(RoundedCornerShape(theme.borderRadiusSmall))
                            .nBorder(theme.colors.border, 1.dp, theme.borderRadiusSmall),
                    ) {
                        NScrollbar(Modifier.fillMaxSize(), scrollState = scrollState) {
                            Column(Modifier.padding(8.dp)) {
                                repeat(8) {
                                    NText(
                                        "A funny joke is need to be wrote here.",
                                        color = theme.colors.text3,
                                    )
                                }
                            }
                        }
                    }
                }
                NBackTop(
                    scrollState = scrollState,
                    visibilityHeight = 10.dp,
                    right = 20.dp,
                    bottom = 20.dp,
                    modifier = Modifier.fillMaxSize(),
                    content = { WideBackTopLabel("Specify Target") },
                )
            }
        }
    }
}
