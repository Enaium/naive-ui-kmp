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
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NAffix
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NStatus

@Composable
private fun AffixPlayground(offsetTop: Dp) {
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
                    "Scroll inside this container. The tag sticks after ${offsetTop.value.toInt()}dp of scrolling.",
                    color = theme.colors.text3,
                )
                Spacer(Modifier.height(90.dp))
                NAffix(scrollState = scrollState, offsetTop = offsetTop) {
                    NTag(
                        text = "Pinned after ${offsetTop.value.toInt()}dp",
                        type = NStatus.Primary,
                    )
                }
                Spacer(Modifier.height(520.dp))
            }
        }
    }
}

@Composable
fun AffixDocs() {
    DocsPage(
        title = "Affix",
        description = "Affix keeps content in place once its scroll container moves. In Compose, pass the ScrollState that owns the scrolling.",
    ) {
        DemoSection(
            title = "Basic",
            description = "`NAffix` stays in normal flow until the trigger offset is reached, then translates with the scroll.",
            code = """
val scrollState = rememberScrollState()
NScrollbar(scrollState = scrollState) {
    NAffix(scrollState = scrollState, offsetTop = 40.dp) {
        NTag("Pinned")
    }
}
            """.trimIndent(),
        ) {
            AffixPlayground(offsetTop = 40.dp)
        }

        DemoSection(
            title = "Trigger offset",
            description = "A larger offset lets the affixed content travel farther before it sticks.",
        ) {
            AffixPlayground(offsetTop = 120.dp)
        }
    }
}
