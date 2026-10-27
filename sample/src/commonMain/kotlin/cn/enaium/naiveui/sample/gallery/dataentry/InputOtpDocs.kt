package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NForm
import cn.enaium.naiveui.components.NFormItem
import cn.enaium.naiveui.components.NFormRule
import cn.enaium.naiveui.components.NInputOtp
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.nFormRules
import cn.enaium.naiveui.components.rememberNFormState
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus

@Composable
fun InputOtpDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Input OTP",
        description = "One-time-password input with a text field per character, automatic focus " +
            "advance, paste support, masking and form validation.",
    ) {
        var otp by remember { mutableStateOf("654321") }
        var finished by remember { mutableStateOf("") }
        DemoSection(
            title = "Basic",
            description = "length defines the number of characters, six by default. Typing advances focus " +
                "to the next cell; onFinish fires when every cell is filled.",
            code = "NInputOtp(value = value, onValueChange = { value = it })",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NInputOtp(
                    value = otp,
                    onValueChange = { otp = it },
                    onFinish = { finished = "Finished: $it" },
                )
                NText(
                    if (finished.isEmpty()) "Value: $otp" else finished,
                    color = theme.colors.text3,
                    size = theme.typography.fontSize.small,
                )
            }
        }

        DemoSection(
            title = "Status",
            description = "Readonly, disabled, warning and error states.",
        ) {
            var warning by remember { mutableStateOf("123456") }
            var error by remember { mutableStateOf("123456") }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInputOtp(value = "123456", onValueChange = {}, readonly = true)
                NInputOtp(value = "123456", onValueChange = {}, disabled = true)
                NInputOtp(
                    value = warning,
                    onValueChange = { warning = it },
                    status = NStatus.Warning,
                )
                NInputOtp(
                    value = error,
                    onValueChange = { error = it },
                    status = NStatus.Error,
                )
            }
        }

        DemoSection(
            title = "Mask",
            description = "Set mask to hide the entered characters.",
        ) {
            var masked by remember { mutableStateOf("654321") }
            NInputOtp(value = masked, onValueChange = { masked = it }, mask = true)
        }

        DemoSection(
            title = "Custom rendering",
            description = "gap and separatorAt customize the cell spacing and where separators appear.",
        ) {
            var templateCode by remember { mutableStateOf("654321") }
            NInputOtp(
                value = templateCode,
                onValueChange = { templateCode = it },
                gap = 6.dp,
                separator = "-",
                separatorAt = { it == 1 || it == 3 },
            )
        }

        DemoSection(
            title = "Block",
            description = "block makes the cells share the full width of the parent.",
        ) {
            var blocked by remember { mutableStateOf("654321") }
            NInputOtp(
                value = blocked,
                onValueChange = { blocked = it },
                modifier = Modifier.width(360.dp),
                block = true,
            )
        }

        DemoSection(
            title = "Size",
            description = "Small, medium and large sizes change the cell width and height.",
        ) {
            var sized by remember { mutableStateOf("654321") }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInputOtp(value = sized, onValueChange = { sized = it }, size = NSize.Small)
                NInputOtp(value = sized, onValueChange = { sized = it }, size = NSize.Medium)
                NInputOtp(value = sized, onValueChange = { sized = it }, size = NSize.Large)
            }
        }

        DemoSection(
            title = "Use with form",
            description = "Combine the OTP input with n-form and a validator that requires six characters.",
        ) {
            val form = rememberNFormState()
            var code by remember { mutableStateOf("") }
            var result by remember { mutableStateOf("") }
            Column(Modifier.width(360.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NForm(
                    model = mapOf("passcode" to code),
                    rules = nFormRules(
                        "passcode" to NFormRule(
                            required = true,
                            message = "Passcode must be 6 characters",
                            validator = { (it as? String)?.length == 6 },
                        ),
                    ),
                    state = form,
                ) {
                    NFormItem(path = "passcode", label = "Pass Code") {
                        NInputOtp(value = code, onValueChange = { code = it })
                    }
                }
                NButton(onClick = { result = if (form.validate()) "Valid" else "Invalid" }) {
                    NText("Validate")
                }
                if (result.isNotEmpty()) {
                    NText(
                        result,
                        color = if (result == "Valid") theme.colors.success else theme.colors.error,
                        size = theme.typography.fontSize.small,
                    )
                }
            }
        }

        DemoSection(
            title = "Limit input format",
            description = "allowInput receives the typed character and its index, and can reject it.",
        ) {
            var numeric by remember { mutableStateOf("") }
            var english by remember { mutableStateOf("") }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NText("Only allow number input", color = theme.colors.text2)
                NInputOtp(
                    value = numeric,
                    onValueChange = { numeric = it },
                    allowInput = { char, _, _ -> char.all(Char::isDigit) },
                )
                NText("Only allow character input", color = theme.colors.text2)
                NInputOtp(
                    value = english,
                    onValueChange = { english = it },
                    allowInput = { char, _, _ -> char.all(Char::isLetter) },
                )
            }
        }

        DemoSection(
            title = "Length and separator",
            description = "Customize the code length and insert a separator between the two halves.",
        ) {
            var shortCode by remember { mutableStateOf("1234") }
            var longCode by remember { mutableStateOf("654321") }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NInputOtp(
                    value = shortCode,
                    onValueChange = { shortCode = it },
                    length = 4,
                )
                NInputOtp(
                    value = longCode,
                    onValueChange = { longCode = it },
                    separator = "–",
                )
            }
        }
    }
}
