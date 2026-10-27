package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NForm
import cn.enaium.naiveui.components.NFormItem
import cn.enaium.naiveui.components.NFormLabelPlacement
import cn.enaium.naiveui.components.NFormRule
import cn.enaium.naiveui.components.NFormRuleType
import cn.enaium.naiveui.components.NFormState
import cn.enaium.naiveui.components.NInput
import cn.enaium.naiveui.components.nFormRules
import cn.enaium.naiveui.components.rememberNFormState
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NButtonType
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NStatus

@Composable
fun FormDocs() {
    DocsPage(
        title = "Form",
        description = "Collect and validate data with declarative rules.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Create a form state, attach rules by model path and validate on demand.",
            code = """
val form = rememberNFormState()
NForm(
    model = mapOf("name" to name),
    rules = nFormRules("name" to NFormRule(required = true, message = "Name is required")),
    state = form,
) {
    NFormItem(path = "name", label = "Name") {
        NInput(value = name, onValueChange = { name = it })
    }
}
NButton(onClick = { form.validate() }) { NText("Validate") }
""".trimIndent(),
        ) {
            BasicFormDemo()
        }
        DemoSection(
            title = "Validation rules",
            description = "Rules support required, min/max length, patterns, value types and custom validators.",
            code = """
rules = mapOf(
    "username" to listOf(
        NFormRule(required = true, min = 3, max = 12),
    ),
    "email" to listOf(NFormRule(type = NFormRuleType.Email)),
    "age" to listOf(NFormRule(type = NFormRuleType.Number, min = 1, max = 150)),
    "code" to listOf(
        NFormRule(validator = { it is String && it.endsWith("42") }, message = "End with 42"),
    ),
)
""".trimIndent(),
        ) {
            RulesFormDemo()
        }
        DemoSection(
            title = "Required mark",
            description = "Asterisks are shown for items with a required rule or an explicit required flag.",
            code = """
NFormItem(label = "Name", required = true) { ... }
NFormItem(label = "Nickname", feedback = "Optional") { ... }
""".trimIndent(),
        ) {
            RequiredMarkDemo()
        }
        DemoSection(
            title = "Label placement",
            description = "Labels can sit above the content or on the left with a shared width.",
            code = """
NForm(labelPlacement = NFormLabelPlacement.Left, labelWidth = 96.dp) { ... }
NForm(labelPlacement = NFormLabelPlacement.Top) { ... }
""".trimIndent(),
        ) {
            LabelPlacementDemo()
        }
        DemoSection(
            title = "Feedback",
            description = "Feedback is reserved below every item. Validation messages replace custom feedback, and validationStatus recolors it.",
            code = """
NFormItem(label = "Email", feedback = "We will never share your email.") { ... }
NFormItem(label = "Password", validationStatus = NStatus.Warning, feedback = "Weak password") { ... }
NFormItem(label = "Notes", showFeedback = false) { ... }
""".trimIndent(),
        ) {
            FeedbackDemo()
        }
    }
}

private fun inputStatus(form: NFormState, path: String): NStatus? =
    if (form.errorOf(path) != null) NStatus.Error else null

@Composable
private fun BasicFormDemo() {
    val form = rememberNFormState()
    val theme = LocalNTheme.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<Boolean?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        NForm(
            model = mapOf("name" to name, "email" to email),
            rules = nFormRules(
                "name" to NFormRule(required = true, message = "Name is required"),
                "email" to NFormRule(
                    required = true,
                    pattern = Regex("^\\S+@\\S+\\.\\S+$"),
                    message = "Enter a valid email",
                ),
            ),
            state = form,
            labelPlacement = NFormLabelPlacement.Left,
            labelWidth = 96.dp,
            validateCallback = { result = it },
        ) {
            NFormItem(path = "name", label = "Name", first = true) {
                NInput(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Input",
                    status = inputStatus(form, "name"),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            NFormItem(path = "email", label = "Email") {
                NInput(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "name@example.com",
                    status = inputStatus(form, "email"),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            NButton(
                type = NButtonType.Primary,
                onClick = { form.validate() },
            ) { NText("Validate") }
            NButton(
                type = NButtonType.Default,
                secondary = true,
                onClick = { form.restoreValidation(); result = null },
            ) { NText("Restore validation") }
            if (result != null) {
                NText(
                    text = if (result == true) "Valid" else "Invalid",
                    color = if (result == true) theme.colors.success else theme.colors.error,
                )
            }
        }
    }
}

@Composable
private fun RulesFormDemo() {
    val form = rememberNFormState()
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        NForm(
            model = mapOf(
                "username" to username,
                "email" to email,
                "age" to age.toIntOrNull(),
                "code" to code,
            ),
            rules = mapOf(
                "username" to listOf(
                    NFormRule(required = true, message = "Username is required"),
                    NFormRule(min = 3, max = 12, message = "Username must be 3 to 12 characters"),
                ),
                "email" to listOf(
                    NFormRule(type = NFormRuleType.Email, required = true, message = "Enter a valid email"),
                ),
                "age" to listOf(
                    NFormRule(
                        type = NFormRuleType.Number,
                        required = true,
                        min = 1,
                        max = 150,
                        message = "Age must be 1 to 150",
                    ),
                ),
                "code" to listOf(
                    NFormRule(
                        required = true,
                        validator = { it is String && it.endsWith("42") },
                        message = "Code must end with 42",
                    ),
                ),
            ),
            state = form,
            labelPlacement = NFormLabelPlacement.Left,
            labelWidth = 96.dp,
        ) {
            NFormItem(path = "username", label = "Username", first = true) {
                NInput(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = "3 to 12 characters",
                    status = inputStatus(form, "username"),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            NFormItem(path = "email", label = "Email") {
                NInput(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "name@example.com",
                    status = inputStatus(form, "email"),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            NFormItem(path = "age", label = "Age") {
                NInput(
                    value = age,
                    onValueChange = { age = it.filter(Char::isDigit).take(3) },
                    placeholder = "Number",
                    status = inputStatus(form, "age"),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            NFormItem(path = "code", label = "Code") {
                NInput(
                    value = code,
                    onValueChange = { code = it },
                    placeholder = "Try 42",
                    status = inputStatus(form, "code"),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        NButton(type = NButtonType.Primary, onClick = { form.validate() }) { NText("Validate") }
    }
}

@Composable
private fun RequiredMarkDemo() {
    val form = rememberNFormState()
    var name by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var showRequireMark by remember { mutableStateOf(true) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        NForm(
            model = mapOf("name" to name, "nickname" to nickname),
            state = form,
            showRequireMark = showRequireMark,
            labelPlacement = NFormLabelPlacement.Left,
            labelWidth = 96.dp,
        ) {
            NFormItem(
                path = "name",
                label = "Name",
                required = true,
                rule = NFormRule(required = true, message = "Name is required"),
                first = true,
            ) {
                NInput(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Required",
                    status = inputStatus(form, "name"),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            NFormItem(path = "nickname", label = "Nickname", feedback = "Optional") {
                NInput(
                    value = nickname,
                    onValueChange = { nickname = it },
                    placeholder = "Optional",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        NButton(onClick = { showRequireMark = !showRequireMark }) {
            NText(if (showRequireMark) "Hide require mark" else "Show require mark")
        }
    }
}

@Composable
private fun LabelPlacementDemo() {
    var leftPlacement by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        NForm(
            model = mapOf("name" to name, "city" to city),
            labelPlacement = if (leftPlacement) NFormLabelPlacement.Left else NFormLabelPlacement.Top,
            labelWidth = 96.dp,
            modifier = Modifier.width(420.dp),
        ) {
            NFormItem(label = "Name", first = true) {
                NInput(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Input",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            NFormItem(label = "City") {
                NInput(
                    value = city,
                    onValueChange = { city = it },
                    placeholder = "Input",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        NButton(onClick = { leftPlacement = !leftPlacement }) {
            NText(if (leftPlacement) "Use top placement" else "Use left placement")
        }
    }
}

@Composable
private fun FeedbackDemo() {
    val form = rememberNFormState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        NForm(
            model = mapOf("email" to email, "password" to password, "notes" to notes),
            rules = mapOf(
                "password" to listOf(
                    NFormRule(min = 8, message = "Password must be at least 8 characters"),
                ),
            ),
            state = form,
            labelPlacement = NFormLabelPlacement.Top,
            modifier = Modifier.width(420.dp),
        ) {
            NFormItem(
                path = "email",
                label = "Email",
                feedback = "We will never share your email.",
                first = true,
            ) {
                NInput(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Input",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            NFormItem(
                path = "password",
                label = "Password",
                validationStatus = if (form.errorOf("password") != null) null else NStatus.Warning,
                feedback = "Weak password",
            ) {
                NInput(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Input",
                    status = inputStatus(form, "password"),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            NFormItem(path = "notes", label = "Notes", showFeedback = false) {
                NInput(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = "No feedback area",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        NButton(type = NButtonType.Primary, onClick = { form.validate() }) { NText("Validate") }
    }
}
