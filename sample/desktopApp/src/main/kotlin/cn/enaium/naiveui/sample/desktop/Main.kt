package cn.enaium.naiveui.sample.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cn.enaium.naiveui.sample.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "naive-ui-kmp",
    ) {
        App()
    }
}
