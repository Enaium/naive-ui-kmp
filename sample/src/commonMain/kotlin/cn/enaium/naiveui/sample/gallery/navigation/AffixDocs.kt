package cn.enaium.naiveui.sample.gallery.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import cn.enaium.naiveui.components.NAffix
import cn.enaium.naiveui.components.NTag
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NStatus

@Composable
private fun AffixContainer(
    triggerTop: Dp,
    top: Dp,
    triggerBottom: Dp,
    bottom: Dp,
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
                    "Scroll inside this container to trigger the affixes.",
                    color = theme.colors.text3,
                )
                Spacer(Modifier.height(120.dp))
                Row(Modifier.fillMaxWidth()) {
                    Box(Modifier.weight(1f)) {
                        NAffix(
                            scrollState = scrollState,
                            triggerTop = triggerTop,
                            top = top,
                        ) {
                            NTag(
                                text = "Trigger top ${triggerTop.value.toInt()}px",
                                type = NStatus.Primary,
                            )
                        }
                    }
                    Box(Modifier.weight(1f)) {
                        NAffix(
                            scrollState = scrollState,
                            triggerBottom = triggerBottom,
                            bottom = bottom,
                        ) {
                            NTag(
                                text = "Trigger bottom ${triggerBottom.value.toInt()}px",
                                type = NStatus.Info,
                            )
                        }
                    }
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
        description = "Affix keeps content in place once its scroll container moves. In Compose, pass the ScrollState that owns the scrolling and choose a top or bottom trigger.",
    ) {
        DemoSection(
            title = "Basic",
            description = "`triggerTop`/`triggerBottom` decide when sticking starts. `top`/`bottom` shift the pinned position once triggered.",
            code = """
val scrollState = rememberScrollState()
NScrollbar(scrollState = scrollState) {
    NAffix(
        scrollState = scrollState,
        triggerTop = 60.dp,
        top = 120.dp,
    ) { NTag("Pinned") }
}
            """.trimIndent(),
        ) {
            AffixContainer(
                triggerTop = 60.dp,
                top = 120.dp,
                triggerBottom = 60.dp,
                bottom = 120.dp,
            )
        }

        DemoSection(
            title = "Position",
            description = "Use different trigger offsets for top- and bottom-anchored content. Compose affixes translate inside their scroll container rather than using CSS fixed positioning.",
        ) {
            AffixContainer(
                triggerTop = 50.dp,
                top = 50.dp,
                triggerBottom = 60.dp,
                bottom = 60.dp,
            )
        }
    }
}
