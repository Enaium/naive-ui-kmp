package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NNumberAnimation
import cn.enaium.naiveui.components.rememberNNumberAnimationState
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

@Composable
fun NumberAnimationDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Number Animation",
        description = "Animate a numeric value with easing, formatting and locale-aware separators.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Call play on the state to replay the animation between from and to.",
        ) {
            val state = rememberNNumberAnimationState()
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NNumberAnimation(
                    from = 9.96,
                    to = 0.07,
                    precision = 2,
                    active = false,
                    state = state,
                    modifier = Modifier.width(140.dp),
                )
                NButton(onClick = { state.play() }) { NText("Play") }
            }
        }
        DemoSection(
            title = "Precision",
            description = "precision fixes the number of decimal places.",
            code = "NNumberAnimation(to = 24.0, precision = 2)",
        ) {
            val state = rememberNNumberAnimationState()
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NNumberAnimation(to = 24.0, precision = 2, active = false, state = state)
                NButton(onClick = { state.play() }) { NText("Play") }
            }
        }
        DemoSection(
            title = "Separator",
            description = "showSeparator groups thousands to keep big numbers readable.",
        ) {
            val state = rememberNNumberAnimationState()
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NNumberAnimation(to = 100000000.0, showSeparator = true, active = false, state = state)
                NButton(onClick = { state.play() }) { NText("Play") }
            }
        }
        DemoSection(
            title = "Locale",
            description = "locale picks the grouping and decimal separators, for example de-DE.",
        ) {
            val state = rememberNNumberAnimationState()
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NNumberAnimation(
                    to = 699700.699,
                    precision = 3,
                    showSeparator = true,
                    locale = "de-DE",
                    active = false,
                    state = state,
                )
                NButton(onClick = { state.play() }) { NText("Play") }
            }
        }
        DemoSection(
            title = "Finish callback",
            description = "onFinish runs once the animation reaches the target value.",
        ) {
            var finished by remember { mutableStateOf(false) }
            val state = rememberNNumberAnimationState()
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NNumberAnimation(
                        to = 100000000.0,
                        showSeparator = true,
                        active = false,
                        state = state,
                        onFinish = { finished = true },
                    )
                    NButton(onClick = {
                        finished = false
                        state.play()
                    }) {
                        NText("Play")
                    }
                }
                NText(
                    text = if (finished) "Finished" else "Waiting",
                    size = 13.sp,
                    color = if (finished) theme.colors.success else theme.colors.text3,
                )
            }
        }
    }
}
