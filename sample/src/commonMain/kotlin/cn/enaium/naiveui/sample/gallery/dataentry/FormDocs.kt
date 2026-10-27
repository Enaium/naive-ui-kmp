package cn.enaium.naiveui.sample.gallery.dataentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NDynamicTags
import cn.enaium.naiveui.components.NForm
import cn.enaium.naiveui.components.NFormItem
import cn.enaium.naiveui.components.NFormLabelPlacement
import cn.enaium.naiveui.components.NFormRule
import cn.enaium.naiveui.components.NFormRuleLevel
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
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FormDocs() {
    DocsPage(
        title = "Form",
        description = "Collect and validate data with declarative rules.",
    ) {
        DemoSection(
            title = "Inline form",
            description = "Set inline to lay fields out in a flowing row.",
            code = """
NForm(model = model, rules = rules, inline = true, size = size) {
    NFormItem(path = "user.name", label = "Name") {
        NInput(value = name, onValueChange = { name = it })
    }
}
""".trimIndent(),
        ) {
            InlineFormDemo()
        }
        DemoSection(
            title = "Customizing rules",
            description = "Custom validators can compare fields and be triggered manually with validatePath.",
            code = """
NFormRule(validator = { it == password }, message = "Passwords do not match")
form.validatePath("reenteredPassword")
""".trimIndent(),
        ) {
            CustomRuleDemo()
        }
        DemoSection(
            title = "Abnormal warning",
            description = "Rules with level Warning report feedback without failing validation.",
            code = """
NFormRule(
    level = NFormRuleLevel.Warning,
    validator = { (it as? Number)?.toInt() == 3 },
    message = "How about three humps?",
)
""".trimIndent(),
        ) {
            AbnormalWarningDemo()
        }
        DemoSection(
            title = "Custom validation",
            description = "Drive feedback yourself with validationStatus and feedback instead of rules.",
            code = """
NFormItem(
    label = "Airport",
    validationStatus = if (airport == "10:30") null else NStatus.Warning,
    feedback = "Please set the time to 10:30",
) { NInput(value = airport, onValueChange = { airport = it }) }
""".trimIndent(),
        ) {
            CustomValidationDemo()
        }
        DemoSection(
            title = "I18n",
            description = "renderMessage is evaluated when validation runs, so messages can follow the active locale.",
            code = """
NFormRule(
    required = true,
    renderMessage = { if (english) "English error" else "中文错误" },
)
""".trimIndent(),
        ) {
            I18nDemo()
        }
        DemoSection(
            title = "Label placement top",
            description = "Labels sit above the content. The form size is inherited by every field.",
            code = """
NForm(labelPlacement = NFormLabelPlacement.Top, size = size) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        NFormItem(path = "name", label = "Name", modifier = Modifier.weight(1f)) { ... }
    }
}
""".trimIndent(),
        ) {
            TopPlacementDemo()
        }
        DemoSection(
            title = "Label placement left",
            description = "Labels sit on the left; labelWidth keeps them aligned across items.",
            code = """
NForm(labelPlacement = NFormLabelPlacement.Left, labelWidth = 120.dp) {
    NFormItem(path = "name", label = "Name") {
        NInput(value = name, onValueChange = { name = it })
    }
}
""".trimIndent(),
        ) {
            LeftPlacementDemo()
        }
        DemoSection(
            title = "Use form item alone",
            description = "A form item works outside a form; pass validationMessage to show feedback directly.",
            code = """
NFormItem(
    label = "This is a FormItem",
    validationMessage = if (value == message) null else message,
) { NInput(value = value, onValueChange = { value = it }) }
""".trimIndent(),
        ) {
            ItemOnlyDemo()
        }
        DemoSection(
            title = "Async validation",
            description = "asyncValidator suspends during validateAsync; the button reflects the running state.",
            code = """
NFormRule(
    required = true,
    asyncValidator = { value -> delay(800); value == "testName" },
    message = "error name",
)
scope.launch { form.validateAsync { valid -> result = valid } }
""".trimIndent(),
        ) {
            AsyncDemo()
        }
        DemoSection(
            title = "Disabled form",
            description = "A disabled form exposes LocalNFormDisabled and demos pass disabled down to their controls.",
            code = """
NForm(disabled = disabled, model = model) {
    NFormItem(path = "name", label = "Input") {
        NInput(value = name, onValueChange = { name = it }, disabled = disabled)
    }
}
""".trimIndent(),
        ) {
            DisabledFormDemo()
        }
        DemoSection(
            title = "Show / hide label",
            description = "Labels are controlled by the form and can be overridden per item.",
            code = """
NForm(showLabel = formShowLabel) {
    NFormItem(label = "Name", showLabel = itemShowLabel) { ... }
}
""".trimIndent(),
        ) {
            ShowLabelDemo()
        }
        DemoSection(
            title = "Apply partial validation",
            description = "validate accepts paths or a rule predicate, leaving other fields untouched.",
            code = """
form.validate(shouldRuleBeApplied = { it.key == "a" })
form.validate(paths = setOf("fieldB"))
form.validate()
form.restoreValidation()
""".trimIndent(),
        ) {
            PartialRulesDemo()
        }
        DemoSection(
            title = "Custom messages",
            description = "validateMessages templates replace default messages; %s is the model path.",
            code = """
NForm(validateMessages = mapOf("required" to "%s is really really required")) {
    NFormItem(path = "user.name", label = "Name") { ... }
}
""".trimIndent(),
        ) {
            CustomMessagesDemo()
        }
        DemoSection(
            title = "Dynamic form",
            description = "Add and remove form items; each hobby is validated through its indexed path.",
            code = """
NFormItem(
    path = "hobbies[${'$'}index].hobby",
    label = "hobby${'$'}{index + 1}",
    rule = NFormRule(required = true, message = "Please input hobby"),
) { ... }
""".trimIndent(),
        ) {
            DynamicFormDemo()
        }
        DemoSection(
            title = "Custom feedback style",
            description = "feedbackAlignment and feedbackColor style the feedback area.",
            code = """
NFormItem(
    path = "input",
    label = "Centered feedback",
    feedbackAlignment = Alignment.CenterHorizontally,
    rule = NFormRule(required = true, message = "Centered feedback"),
) { NInput(value = value, onValueChange = { value = it }) }
""".trimIndent(),
        ) {
            FeedbackStyleDemo()
        }
    }
}

private fun fieldStatus(form: NFormState, path: String): NStatus? =
    form.statusOf(path).takeIf { it != NStatus.Default }

@Composable
private fun ResultText(result: Boolean?) {
    if (result == null) return
    val theme = LocalNTheme.current
    NText(
        text = if (result) "Valid" else "Invalid",
        color = if (result) theme.colors.success else theme.colors.error,
    )
}

@Composable
private fun InlineFormDemo() {
    val form = rememberNFormState()
    var size by remember { mutableStateOf(NSize.Medium) }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<Boolean?>(null) }
    val rules = nFormRules("user.name" to NFormRule(required = true, message = "Please input your name"))

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NButton(size = NSize.Small, type = NButtonType.Primary, onClick = { size = NSize.Small }) {
                NText("Small")
            }
            NButton(size = NSize.Small, type = NButtonType.Primary, onClick = { size = NSize.Medium }) {
                NText("Medium")
            }
            NButton(size = NSize.Small, type = NButtonType.Primary, onClick = { size = NSize.Large }) {
                NText("Large")
            }
        }
        NForm(
            model = mapOf(
                "user" to mapOf("name" to name, "age" to age),
                "phone" to phone,
            ),
            rules = rules + mapOf(
                "user.age" to listOf(NFormRule(required = true, message = "Please input your age")),
                "phone" to listOf(NFormRule(required = true, message = "Please input your number")),
            ),
            state = form,
            inline = true,
            size = size,
            validateCallback = { result = it },
        ) {
            NFormItem(path = "user.name", label = "Name", first = true) {
                NInput(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Input Name",
                    status = fieldStatus(form, "user.name"),
                    modifier = Modifier.width(160.dp),
                )
            }
            NFormItem(path = "user.age", label = "Age", first = true) {
                NInput(
                    value = age,
                    onValueChange = { age = it.filter(Char::isDigit) },
                    placeholder = "Input Age",
                    status = fieldStatus(form, "user.age"),
                    modifier = Modifier.width(130.dp),
                )
            }
            NFormItem(path = "phone", label = "Phone", first = true) {
                NInput(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = "Phone Number",
                    status = fieldStatus(form, "phone"),
                    modifier = Modifier.width(170.dp),
                )
            }
            NFormItem(first = true, showFeedback = false) {
                NButton(onClick = { form.validate() }) { NText("Validate") }
            }
        }
        ResultText(result)
    }
}

@Composable
private fun CustomRuleDemo() {
    val form = rememberNFormState()
    var age by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var reentered by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<Boolean?>(null) }

    NForm(
        model = mapOf("age" to age, "password" to password, "reenteredPassword" to reentered),
        rules = mapOf(
            "age" to listOf(
                NFormRule(
                    required = true,
                    validator = {
                        it is String && it.isNotEmpty() && it.all(Char::isDigit) && it.toInt() >= 18
                    },
                    message = "Age should be an integer above 18",
                ),
            ),
            "password" to listOf(NFormRule(required = true, message = "Password is required")),
            "reenteredPassword" to listOf(
                NFormRule(required = true, message = "Re-entered password is required"),
                NFormRule(validator = { it == password }, message = "Passwords do not match"),
            ),
        ),
        state = form,
        labelPlacement = NFormLabelPlacement.Left,
        labelWidth = 150.dp,
        validateCallback = { result = it },
    ) {
        NFormItem(path = "age", label = "Age", first = true) {
            NInput(
                value = age,
                onValueChange = { age = it.filter(Char::isDigit).take(9) },
                placeholder = "At least 18",
                status = fieldStatus(form, "age"),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        NFormItem(path = "password", label = "Password") {
            NInput(
                value = password,
                onValueChange = {
                    password = it
                    if (reentered.isNotEmpty()) form.validatePath("reenteredPassword")
                },
                placeholder = "Input",
                status = fieldStatus(form, "password"),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        NFormItem(path = "reenteredPassword", label = "Re-enter Password") {
            NInput(
                value = reentered,
                onValueChange = { reentered = it },
                placeholder = "Input",
                disabled = password.isEmpty(),
                status = fieldStatus(form, "reenteredPassword"),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NButton(type = NButtonType.Primary, onClick = { form.validate() }) { NText("Validate") }
        ResultText(result)
    }
}

@Composable
private fun AbnormalWarningDemo() {
    val form = rememberNFormState()
    var count by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        NForm(
            model = mapOf("count" to count.toIntOrNull()),
            rules = mapOf(
                "count" to listOf(
                    NFormRule(
                        required = true,
                        type = NFormRuleType.Number,
                        message = "Try to answer?",
                    ),
                    NFormRule(
                        level = NFormRuleLevel.Warning,
                        validator = { (it as? Number)?.toInt() == 3 },
                        message = "How about three-humped camel? when it's pregnant.",
                    ),
                ),
            ),
            state = form,
            labelPlacement = NFormLabelPlacement.Top,
            modifier = Modifier.width(460.dp),
        ) {
            NFormItem(
                path = "count",
                label = "How many humps can a camel have at most?",
                first = true,
            ) {
                NInput(
                    value = count,
                    onValueChange = { count = it.filter(Char::isDigit) },
                    placeholder = "Input a number",
                    status = fieldStatus(form, "count"),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            NButton(type = NButtonType.Primary, onClick = { form.validate() }) {
                NText("Submit Answer")
            }
            val theme = LocalNTheme.current
            val summary = when {
                form.errors.isNotEmpty() -> "Invalid." to theme.colors.error
                form.warnings.isNotEmpty() -> "Valid but be aware of warnings." to theme.colors.warning
                form.validated -> "Perfect." to theme.colors.success
                else -> null
            }
            if (summary != null) NText(summary.first, color = summary.second)
        }
    }
}

@Composable
private fun CustomValidationDemo() {
    var airport by remember { mutableStateOf("10:29") }
    var gate by remember { mutableStateOf("B") }

    val airportStatus = when (airport) {
        "10:30" -> null
        "10:29" -> NStatus.Warning
        else -> NStatus.Error
    }
    val airportFeedback = when (airport) {
        "10:30" -> "The plane of 10:30 has arrived."
        "10:29" -> "Almost there, please set the time to 10:30"
        else -> "Please set the time to 10:30"
    }
    val gateStatus = if (gate == "B12") null else NStatus.Error
    val gateFeedback = if (gate == "B12") "Gate confirmed." else "Please enter B12"

    NForm(
        model = emptyMap<String, Any?>(),
        labelPlacement = NFormLabelPlacement.Left,
        labelWidth = 90.dp,
        modifier = Modifier.width(460.dp),
    ) {
        NFormItem(
            label = "Airport",
            validationStatus = airportStatus,
            feedback = airportFeedback,
            first = true,
        ) {
            NInput(
                value = airport,
                onValueChange = { airport = it },
                clearable = true,
                status = airportStatus,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        NFormItem(
            label = "Gate",
            validationStatus = gateStatus,
            feedback = gateFeedback,
        ) {
            NInput(
                value = gate,
                onValueChange = { gate = it },
                clearable = true,
                status = gateStatus,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun I18nDemo() {
    val form = rememberNFormState()
    var english by remember { mutableStateOf(true) }
    var value by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NButton(
                type = if (english) NButtonType.Primary else NButtonType.Default,
                onClick = {
                    english = true
                    if (form.validated) form.validate()
                },
            ) { NText("English") }
            NButton(
                type = if (english) NButtonType.Default else NButtonType.Primary,
                onClick = {
                    english = false
                    if (form.validated) form.validate()
                },
            ) { NText("Chinese") }
            NButton(onClick = { form.validate() }) { NText("Validate") }
        }
        NForm(
            model = mapOf("input" to value),
            rules = mapOf(
                "input" to listOf(
                    NFormRule(
                        required = true,
                        renderMessage = { if (english) "English error" else "中文错误" },
                    ),
                ),
            ),
            state = form,
            modifier = Modifier.width(420.dp),
        ) {
            NFormItem(
                path = "input",
                label = "Input something to remove error",
                first = true,
            ) {
                NInput(
                    value = value,
                    onValueChange = { value = it },
                    status = fieldStatus(form, "input"),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun TopPlacementDemo() {
    val form = rememberNFormState()
    var size by remember { mutableStateOf(NSize.Medium) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<Boolean?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(NSize.Small to "Small", NSize.Medium to "Medium", NSize.Large to "Large")
                .forEach { (value, label) ->
                    NButton(
                        size = NSize.Small,
                        type = if (size == value) NButtonType.Primary else NButtonType.Default,
                        onClick = { size = value },
                    ) { NText(label) }
                }
        }
        NForm(
            model = mapOf("name" to name, "email" to email),
            rules = nFormRules(
                "name" to NFormRule(required = true, message = "Please input name"),
                "email" to NFormRule(required = true, message = "Please input email"),
            ),
            state = form,
            size = size,
            labelPlacement = NFormLabelPlacement.Top,
            validateCallback = { result = it },
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                NFormItem(
                    path = "name",
                    label = "Name",
                    modifier = Modifier.weight(1f),
                    first = true,
                ) {
                    NInput(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = "Input",
                        status = fieldStatus(form, "name"),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                NFormItem(
                    path = "email",
                    label = "Email",
                    modifier = Modifier.weight(1f),
                    first = true,
                ) {
                    NInput(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Input",
                        status = fieldStatus(form, "email"),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            NButton(type = NButtonType.Primary, round = true, onClick = { form.validate() }) {
                NText("Validate")
            }
            ResultText(result)
        }
    }
}

@Composable
private fun LeftPlacementDemo() {
    val form = rememberNFormState()
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
            labelWidth = 120.dp,
            validateCallback = { result = it },
            modifier = Modifier.width(460.dp),
        ) {
            NFormItem(path = "name", label = "Name", first = true) {
                NInput(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Input",
                    status = fieldStatus(form, "name"),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            NFormItem(path = "email", label = "Email") {
                NInput(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "name@example.com",
                    status = fieldStatus(form, "email"),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            NButton(type = NButtonType.Primary, onClick = { form.validate() }) { NText("Validate") }
            NButton(
                secondary = true,
                onClick = { form.restoreValidation(); result = null },
            ) { NText("Restore validation") }
            ResultText(result)
        }
    }
}

@Composable
private fun ItemOnlyDemo() {
    val expected = "It is not in a Form"
    var value by remember { mutableStateOf(expected) }
    val invalid = value != expected

    NFormItem(
        label = "This is a FormItem",
        validationMessage = if (invalid) expected else null,
        validationStatus = if (invalid) NStatus.Error else null,
        labelPlacement = NFormLabelPlacement.Left,
        labelWidth = 150.dp,
        modifier = Modifier.width(460.dp),
    ) {
        NInput(
            value = value,
            onValueChange = { value = it },
            status = if (invalid) NStatus.Error else null,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun AsyncDemo() {
    val form = rememberNFormState()
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("name") }
    var age by remember { mutableStateOf("15") }
    var phone by remember { mutableStateOf("1251550092") }
    var result by remember { mutableStateOf<Boolean?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        NForm(
            model = mapOf("name" to name, "age" to age, "phone" to phone),
            rules = mapOf(
                "name" to listOf(
                    NFormRule(
                        required = true,
                        message = "error name",
                        asyncValidator = { value -> delay(400); value == "testName" },
                    ),
                ),
                "age" to listOf(
                    NFormRule(
                        required = true,
                        message = "error age",
                        asyncValidator = { value ->
                            delay(800)
                            (value as? String)?.toIntOrNull()?.let { it > 16 } == true
                        },
                    ),
                ),
                "phone" to listOf(
                    NFormRule(
                        required = true,
                        validator = { value -> value is String && Regex("^1[38]\\d{9}$").matches(value) },
                        message = "Invalid phone number",
                    ),
                ),
            ),
            state = form,
            labelPlacement = NFormLabelPlacement.Left,
            labelWidth = 90.dp,
            modifier = Modifier.width(460.dp),
        ) {
            NFormItem(path = "name", label = "Name", first = true) {
                NInput(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Try testName",
                    status = fieldStatus(form, "name"),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            NFormItem(path = "age", label = "Age") {
                NInput(
                    value = age,
                    onValueChange = { age = it.filter(Char::isDigit) },
                    placeholder = "Older than 16",
                    status = fieldStatus(form, "age"),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            NFormItem(path = "phone", label = "Phone") {
                NInput(
                    value = phone,
                    onValueChange = { phone = it.filter(Char::isDigit).take(11) },
                    placeholder = "Phone Number",
                    status = fieldStatus(form, "phone"),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            NButton(
                type = NButtonType.Primary,
                loading = form.validating,
                onClick = {
                    scope.launch {
                        form.validateAsync { valid -> result = valid }
                    }
                },
            ) { NText(if (form.validating) "Verifying" else "Validate") }
            ResultText(result)
        }
    }
}

@Composable
private fun DisabledFormDemo() {
    var disabled by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf(listOf("teacher", "frontend")) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        NButton(onClick = { disabled = !disabled }) {
            NText(if (disabled) "Enable form" else "Disable form")
        }
        NForm(
            model = mapOf("name" to name, "tags" to tags),
            disabled = disabled,
            labelPlacement = NFormLabelPlacement.Left,
            labelWidth = 120.dp,
            modifier = Modifier.width(480.dp),
        ) {
            NFormItem(path = "name", label = "Input", first = true) {
                NInput(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Input",
                    disabled = disabled,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            NFormItem(path = "tags", label = "Dynamic Tags") {
                NDynamicTags(
                    value = tags,
                    onValueChange = { tags = it },
                    disabled = disabled,
                )
            }
        }
    }
}

@Composable
private fun ShowLabelDemo() {
    var formShowLabel by remember { mutableStateOf(true) }
    var itemShowLabel by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NButton(onClick = { formShowLabel = !formShowLabel }) {
                NText("Form labels: ${if (formShowLabel) "on" else "off"}")
            }
            NButton(onClick = { itemShowLabel = !itemShowLabel }) {
                NText("First item: ${if (itemShowLabel) "on" else "off"}")
            }
        }
        NForm(
            model = mapOf("user" to mapOf("name" to name), "age" to age),
            showLabel = formShowLabel,
            labelPlacement = NFormLabelPlacement.Left,
            labelWidth = 90.dp,
            modifier = Modifier.width(440.dp),
        ) {
            NFormItem(path = "user.name", label = "Name", showLabel = itemShowLabel, first = true) {
                NInput(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Input Name",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            NFormItem(path = "age", label = "Age") {
                NInput(
                    value = age,
                    onValueChange = { age = it },
                    placeholder = "Input Age",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun PartialRulesDemo() {
    val form = rememberNFormState()
    var fieldA by remember { mutableStateOf("") }
    var fieldB by remember { mutableStateOf("") }
    var action by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NButton(
                size = NSize.Small,
                onClick = {
                    form.validate(shouldRuleBeApplied = { it.key == "a" })
                    action = "Validated field A by rule key"
                },
            ) { NText("Check field A by rule key") }
            NButton(
                size = NSize.Small,
                onClick = {
                    form.validate(paths = setOf("fieldB"))
                    action = "Validated field B by path"
                },
            ) { NText("Check field B by path") }
            NButton(
                size = NSize.Small,
                onClick = {
                    form.validate()
                    action = "Validated all fields"
                },
            ) { NText("Check all fields") }
            NButton(
                size = NSize.Small,
                onClick = {
                    form.restoreValidation()
                    action = "Validation restored"
                },
            ) { NText("Restore validation") }
        }
        NForm(
            model = mapOf("fieldA" to fieldA, "fieldB" to fieldB),
            rules = mapOf(
                "fieldA" to listOf(
                    NFormRule(key = "a", required = true, min = 3, message = "Min length is 3"),
                ),
                "fieldB" to listOf(
                    NFormRule(required = true, min = 2, message = "Min length is 2"),
                ),
            ),
            state = form,
            labelPlacement = NFormLabelPlacement.Left,
            labelWidth = 110.dp,
            modifier = Modifier.width(440.dp),
        ) {
            NFormItem(path = "fieldA", label = "Min length 3", first = true) {
                NInput(
                    value = fieldA,
                    onValueChange = { fieldA = it },
                    status = fieldStatus(form, "fieldA"),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            NFormItem(path = "fieldB", label = "Min length 2") {
                NInput(
                    value = fieldB,
                    onValueChange = { fieldB = it },
                    status = fieldStatus(form, "fieldB"),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        if (action.isNotEmpty()) NText(action, color = LocalNTheme.current.colors.text3)
    }
}

@Composable
private fun CustomMessagesDemo() {
    val form = rememberNFormState()
    var name by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<Boolean?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        NForm(
            model = mapOf("user" to mapOf("name" to name)),
            rules = mapOf(
                "user.name" to listOf(NFormRule(required = true)),
            ),
            state = form,
            validateMessages = mapOf("required" to "%s is really really required"),
            labelPlacement = NFormLabelPlacement.Top,
            validateCallback = { result = it },
            modifier = Modifier.width(440.dp),
        ) {
            NFormItem(path = "user.name", label = "Name", first = true) {
                NInput(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Input Name",
                    status = fieldStatus(form, "user.name"),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            NButton(onClick = { form.validate() }) { NText("Validate") }
            ResultText(result)
        }
    }
}

@Composable
private fun DynamicFormDemo() {
    val form = rememberNFormState()
    var name by remember { mutableStateOf("") }
    var hobbies by remember { mutableStateOf(listOf("")) }
    var result by remember { mutableStateOf<Boolean?>(null) }

    NForm(
        model = mapOf(
            "name" to name,
            "hobbies" to hobbies.map { mapOf("hobby" to it) },
        ),
        state = form,
        labelPlacement = NFormLabelPlacement.Left,
        labelWidth = 90.dp,
        validateCallback = { result = it },
        modifier = Modifier.width(520.dp),
    ) {
        NFormItem(
            path = "name",
            label = "name",
            rule = NFormRule(required = true, message = "Please input Name"),
            first = true,
        ) {
            NInput(
                value = name,
                onValueChange = { name = it },
                status = fieldStatus(form, "name"),
                clearable = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        hobbies.forEachIndexed { index, hobby ->
            NFormItem(
                path = "hobbies[$index].hobby",
                label = "hobby${index + 1}",
                rule = NFormRule(required = true, message = "Please input hobby${index + 1}"),
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NInput(
                        value = hobby,
                        onValueChange = { value ->
                            hobbies = hobbies.toMutableList().also { it[index] = value }
                        },
                        status = fieldStatus(form, "hobbies[$index].hobby"),
                        clearable = true,
                        modifier = Modifier.weight(1f),
                    )
                    NButton(
                        size = NSize.Small,
                        onClick = {
                            hobbies = hobbies.toMutableList().also { it.removeAt(index) }
                        },
                    ) { NText("Remove") }
                }
            }
        }
        NFormItem(showFeedback = false) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                NButton(type = NButtonType.Primary, onClick = { form.validate() }) { NText("Validate") }
                NButton(onClick = { hobbies = hobbies + "" }) { NText("New hobby") }
                ResultText(result)
            }
        }
    }
}

@Composable
private fun FeedbackStyleDemo() {
    val form = rememberNFormState()
    var value by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        NForm(
            model = mapOf("input" to value),
            rules = nFormRules(
                "input" to NFormRule(required = true, message = "Centered feedback"),
            ),
            state = form,
            modifier = Modifier.width(440.dp),
        ) {
            NFormItem(
                path = "input",
                label = "Centered feedback",
                feedbackAlignment = Alignment.CenterHorizontally,
                first = true,
            ) {
                NInput(
                    value = value,
                    onValueChange = { value = it },
                    status = fieldStatus(form, "input"),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        NButton(onClick = { form.validate() }) { NText("Validate") }
    }
}
