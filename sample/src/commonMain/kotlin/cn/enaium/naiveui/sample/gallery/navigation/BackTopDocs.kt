package cn.enaium.naiveui.sample.gallery.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NBackTop
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

@Composable
private fun BackTopPlayground(
    visibilityHeight: Dp,
    right: Dp = 40.dp,
    bottom: Dp = 40.dp,
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
        )
    }
}

@Composable
fun BackTopDocs() {
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
    NBackTop(scrollState = scrollState)
}
            """.trimIndent(),
        ) {
            BackTopPlayground(visibilityHeight = 180.dp)
        }

        DemoSection(
            title = "Visibility height",
            description = "Raise `visibilityHeight` to require more scrolling before the button appears.",
        ) {
            BackTopPlayground(visibilityHeight = 400.dp)
        }

        DemoSection(
            title = "Position",
            description = "Adjust `right` and `bottom` to move the button inside its container.",
        ) {
            BackTopPlayground(
                visibilityHeight = 180.dp,
                right = 16.dp,
                bottom = 20.dp,
            )
        }

        DemoSection(
            title = "Custom content",
            description = "Pass content to replace the default arrow, for example a wider labeled button.",
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
                        NText("Scroll to the bottom, then use the wide button.", color = theme.colors.text3)
                        Spacer(Modifier.height(700.dp))
                    }
                }
                NBackTop(
                    scrollState = scrollState,
                    visibilityHeight = 120.dp,
                    modifier = Modifier.fillMaxSize(),
                    content = {
                        NText("Top", color = theme.colors.text2)
                    },
                )
            }
        }
    }
}
