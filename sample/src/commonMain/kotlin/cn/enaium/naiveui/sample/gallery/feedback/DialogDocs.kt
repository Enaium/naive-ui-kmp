package cn.enaium.naiveui.sample.gallery.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NDialog
import cn.enaium.naiveui.components.NDialogHandle
import cn.enaium.naiveui.components.NDialogOptions
import cn.enaium.naiveui.components.NDialogProvider
import cn.enaium.naiveui.components.NDialogType
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.rememberNDialog
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DialogDocs() {
    DocsPage(
        title = "Dialog",
        description = "Dialog is both an imperative confirmation surface and a component. Wrap a subtree with " +
            "`NDialogProvider`, obtain the API with `rememberNDialog()` and open typed dialogs from any callback.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Warning, success and error dialogs with action callbacks.",
            code = "NDialogProvider {\n    val dialog = rememberNDialog()\n    NButton(onClick = { dialog.success(title = \"Success\", content = \"Cool\") }) { NText(\"Open\") }\n}",
        ) {
            NDialogProvider { DialogBasicDemo() }
        }

        DemoSection(
            title = "Customize action",
            description = "`actionText` replaces the standard action row with custom content.",
        ) {
            NDialogProvider { DialogActionDemo() }
        }

        DemoSection(
            title = "Async",
            description = "Set `closeOnPositiveClick = false`, switch the handle to loading and update its content while the work runs.",
        ) {
            NDialogProvider { DialogAsyncDemo() }
        }

        DemoSection(
            title = "Click on mask",
            description = "With `maskClosable = false` a mask click does nothing unless you provide `onMaskClick`. `onEsc` reports Esc.",
        ) {
            NDialogProvider { DialogMaskDemo() }
        }

        DemoSection(
            title = "Use as a component",
            description = "`NDialog` renders the dialog surface inline, without a provider or mask.",
        ) {
            var result by remember { mutableStateOf("No action yet") }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NDialog(
                    title = "Dialog",
                    content = "Are you sure?",
                    negativeText = "Cancel",
                    positiveText = "Submit",
                    onPositiveClick = { result = "Confirm" },
                    onNegativeClick = { result = "Cancel" },
                )
                NText(result, size = 13.sp)
            }
        }

        DemoSection(
            title = "Access all dialog instances",
            description = "`dialog.dialogs` is reactive and exposes every dialog owned by the provider.",
        ) {
            NDialogProvider { DialogReactiveListDemo() }
        }

        DemoSection(
            title = "Dialog types",
            description = "`info`, `success`, `warning` and `error` pick the icon and accent color.",
        ) {
            NDialogProvider { DialogTypesDemo() }
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
        NSpace(size = 8.dp, wrap = true) {
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
        }
        NText(result, size = 13.sp)
    }
}

@Composable
private fun DialogActionDemo() {
    val dialog = rememberNDialog()
    NButton(onClick = {
        dialog.create(
            NDialogOptions(
                title = "Use Render Function",
                content = "Content",
                actionText = "Action",
                type = NDialogType.Warning,
            ),
        )
    }) { NText("Use Render Function") }
}

@Composable
private fun DialogAsyncDemo() {
    val dialog = rememberNDialog()
    val scope = rememberCoroutineScope()
    var active by remember { mutableStateOf<NDialogHandle?>(null) }
    NButton(onClick = {
        val options = NDialogOptions(
            title = "Async",
            content = "Click and count down 3 seconds",
            type = NDialogType.Success,
            positiveText = "Confirm",
            closeOnPositiveClick = false,
            onPositiveClick = {
                active?.loading = true
                scope.launch {
                    delay(1000)
                    active?.content = "Count down 2 seconds"
                    delay(1000)
                    active?.content = "Count down 1 second"
                    delay(1000)
                    active?.content = "Count down 0 second"
                    active?.loading = false
                    active?.close()
                }
            },
        )
        active = dialog.create(options)
    }) { NText("Success") }
}

@Composable
private fun DialogMaskDemo() {
    val dialog = rememberNDialog()
    var result by remember { mutableStateOf("Nothing happened") }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NButton(onClick = {
            dialog.create(
                NDialogOptions(
                    title = "Close",
                    content = "Are you sure?",
                    type = NDialogType.Success,
                    positiveText = "Sure",
                    negativeText = "Not Sure",
                    maskClosable = false,
                    onMaskClick = { result = "cannot close" },
                    onEsc = { result = "close by esc" },
                ),
            )
        }) { NText("Callback on Mask Clicked") }
        NText(result, size = 13.sp)
    }
}

@Composable
private fun DialogReactiveListDemo() {
    val dialog = rememberNDialog()
    val dialogs = dialog.dialogs
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NText("There are ${dialogs.size} dialogs in the current page.", size = 13.sp)
        NSpace(size = 8.dp) {
            NButton(onClick = {
                dialog.info(title = "One", content = "First dialog", positiveText = "OK")
            }) { NText("Open One") }
            NButton(onClick = {
                dialog.info(title = "Two", content = "Second dialog", positiveText = "OK")
                dialog.info(title = "Three", content = "Third dialog", positiveText = "OK")
            }) { NText("Open Two More") }
            NButton(size = NSize.Small, onClick = { dialog.destroyAll() }) { NText("Destroy All") }
        }
    }
}

@Composable
private fun DialogTypesDemo() {
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
