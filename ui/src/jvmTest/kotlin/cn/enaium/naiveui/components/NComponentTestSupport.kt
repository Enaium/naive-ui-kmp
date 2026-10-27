package cn.enaium.naiveui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import cn.enaium.naiveui.theme.NConfigProvider
import cn.enaium.naiveui.theme.NGlobalStyle

@OptIn(ExperimentalTestApi::class)
fun ComposeUiTest.setNContent(content: @Composable () -> Unit) {
    setContent {
        NConfigProvider {
            NGlobalStyle { content() }
        }
    }
}
