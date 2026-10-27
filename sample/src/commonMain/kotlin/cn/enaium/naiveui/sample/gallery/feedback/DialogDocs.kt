package cn.enaium.naiveui.sample.gallery.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NDialogOptions
import cn.enaium.naiveui.components.NDialogProvider
import cn.enaium.naiveui.components.NDialogType
import cn.enaium.naiveui.components.rememberNDialog
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

@Composable
fun DialogDocs() {
    DocsPage(
        title = "Dialog",
        description = "Dialog is an imperative confirmation surface. Wrap a subtree with `NDialogProvider`, " +
            "obtain the API with `rememberNDialog()` and open typed dialogs from any callback.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Provider dialogs render in the window center. Actions close the dialog automatically.",
            code = "NDialogProvider {\n    val dialog = rememberNDialog()\n    NButton(onClick = {\n        dialog.success(title = \"Success\", content = \"Cool\")\n    }) { NText(\"Open\") }\n}",
        ) {
            NDialogProvider { DialogBasicDemo() }
        }

        DemoSection(
            title = "Type",
            description = "`info`, `success`, `warning` and `error` pick the icon and accent color.",
        ) {
            NDialogProvider { DialogTypeDemo() }
        }

        DemoSection(
            title = "Custom options",
            description = "`create(NDialogOptions(...))` exposes mask closing, closable, icons and callbacks.",
        ) {
            NDialogProvider { DialogOptionsDemo() }
        }

        DemoSection(
            title = "Multiple dialogs",
            description = "Dialogs stack in the center; the newest one is on top and owns the scrim and back button.",
        ) {
            NDialogProvider { DialogStackDemo() }
        }

        DemoSection(
            title = "Destroy all",
            description = "`destroyAll()` closes every dialog owned by the provider.",
        ) {
            NDialogProvider { DialogDestroyDemo() }
        }
    }
}

@Composable
private fun DialogBasicDemo() {
    val dialog = rememberNDialog()
    var result by remember { mutableStateOf("No action yet") }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        NSpace(size = 8.dp) {
            NButton(onClick = {
                dialog.warning(
                    title = "Confirm",
                    content = "Are you sure?",
                    positiveText = "Sure",
                    negativeText = "Not Sure",
                    onPositiveClick = { result = "Sure" },
                    onNegativeClick = { result = "Not Sure" },
                )
            }) { NText("Confirm") }
            NButton(onClick = {
                dialog.success(
                    title = "Success",
                    content = "Cool",
                    positiveText = "Wow!",
                    onPositiveClick = { result = "Great!" },
                )
            }) { NText("Success") }
            NButton(onClick = {
                dialog.error(
                    title = "Error",
                    content = "A mistake.",
                    positiveText = "Ahhh!",
                    onPositiveClick = { result = "I knew it..." },
                )
            }) { NText("Error") }
            NButton(onClick = {
                dialog.info(title = "Info", content = "Something happened.")
            }) { NText("Info") }
        }
        NText(result, size = 13.sp)
    }
}

@Composable
private fun DialogTypeDemo() {
    val dialog = rememberNDialog()
    NSpace(size = 8.dp, wrap = true) {
        NDialogType.entries.forEach { type ->
            NButton(onClick = {
                dialog.create(
                    title = type.name,
                    content = "A ${type.name.lowercase()} dialog.",
                    type = type,
                    positiveText = "OK",
                )
            }) { NText(type.name) }
        }
    }
}

@Composable
private fun DialogOptionsDemo() {
    val dialog = rememberNDialog()
    NButton(onClick = {
        dialog.create(
            NDialogOptions(
                title = "Locked dialog",
                content = "The mask is not closable and there is no close icon.",
                type = NDialogType.Warning,
                showIcon = true,
                closable = false,
                maskClosable = false,
                positiveText = "Understood",
            ),
        )
    }) { NText("Open Custom Dialog") }
}

@Composable
private fun DialogStackDemo() {
    val dialog = rememberNDialog()
    NButton(onClick = {
        dialog.info(title = "First", content = "This is the bottom dialog.", positiveText = "OK")
        dialog.warning(title = "Second", content = "This one stacks above.", positiveText = "OK")
    }) { NText("Open Two Dialogs") }
}

@Composable
private fun DialogDestroyDemo() {
    val dialog = rememberNDialog()
    NSpace(size = 8.dp) {
        NButton(onClick = {
            dialog.info(title = "One", content = "First dialog", positiveText = "OK")
            dialog.info(title = "Two", content = "Second dialog", positiveText = "OK")
            dialog.info(title = "Three", content = "Third dialog", positiveText = "OK")
        }) { NText("Open Three") }
        NButton(size = NSize.Small, onClick = { dialog.destroyAll() }) { NText("Destroy All") }
    }
}
