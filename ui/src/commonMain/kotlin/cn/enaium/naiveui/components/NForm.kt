package cn.enaium.naiveui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.theme.LocalNComponentSize
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import cn.enaium.naiveui.theme.nSize

/** Label placement used by [NForm] and [NFormItem]. */
enum class NFormLabelPlacement { Top, Left }

/** Value types understood by [NFormRule]. */
enum class NFormRuleType { String, Number, Array, Boolean, Email }

/** Severity of a failed rule. */
enum class NFormRuleLevel { Error, Warning }

/**
 * A single validation rule, mirroring the subset of `async-validator` used by
 * naive-ui's form documentation.
 */
data class NFormRule(
    val required: Boolean = false,
    val message: String? = null,
    val validator: ((Any?) -> Boolean)? = null,
    val min: Int? = null,
    val max: Int? = null,
    val pattern: Regex? = null,
    val type: NFormRuleType? = null,
    val key: String? = null,
    val asyncValidator: (suspend (Any?) -> Boolean)? = null,
    val level: NFormRuleLevel = NFormRuleLevel.Error,
    val renderMessage: (() -> String)? = null,
) {
    internal fun check(value: Any?, path: String, messages: Map<String, String>): String? {
        if (isEmptyValue(value)) {
            return if (required) messageFor("required", path, messages, "This field is required") else null
        }
        if (type != null && !type.accepts(value)) {
            return messageFor("type", path, messages, "Invalid value")
        }
        val size = measure(value)
        if (min != null && size != null && size < min) {
            return messageFor("min", path, messages, "Value should be at least $min")
        }
        if (max != null && size != null && size > max) {
            return messageFor("max", path, messages, "Value should be at most $max")
        }
        if (pattern != null && (value !is String || !pattern.matches(value))) {
            return messageFor("pattern", path, messages, "Invalid format")
        }
        if (validator != null && !validator.invoke(value)) {
            return messageFor("validator", path, messages, "Validation failed")
        }
        return null
    }

    internal suspend fun asyncCheck(
        value: Any?,
        path: String,
        messages: Map<String, String>,
    ): String? {
        val async = asyncValidator ?: return null
        if (isEmptyValue(value)) return null
        return if (async(value)) null else messageFor("validator", path, messages, "Validation failed")
    }

    internal fun messageFor(
        kind: String,
        path: String,
        messages: Map<String, String>,
        fallback: String,
    ): String = renderMessage?.invoke()
        ?: message
        ?: messages[kind]?.replace("%s", path)
        ?: fallback
}

private fun isEmptyValue(value: Any?): Boolean = when (value) {
    null -> true
    is String -> value.isBlank()
    is Collection<*> -> value.isEmpty()
    is Map<*, *> -> value.isEmpty()
    else -> false
}

private fun measure(value: Any?): Int? = when (value) {
    is String -> value.length
    is Number -> value.toInt()
    is Collection<*> -> value.size
    is Map<*, *> -> value.size
    else -> null
}

private fun NFormRuleType.accepts(value: Any?): Boolean = when (this) {
    NFormRuleType.String -> value is String
    NFormRuleType.Number -> value is Number
    NFormRuleType.Array -> value is Collection<*> || value is Array<*>
    NFormRuleType.Boolean -> value is Boolean
    NFormRuleType.Email -> value is String && EMAIL.matches(value)
}

private val EMAIL = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

private fun MutableMap<String, List<String>>.add(path: String, message: String) {
    this[path] = (this[path].orEmpty()) + message
}

private data class Evaluation(
    val errors: Map<String, List<String>>,
    val warnings: Map<String, List<String>>,
)

/**
 * Validation state shared by a form and its items. Create it with
 * [rememberNFormState] and pass it to [NForm], then call [validate] or
 * [validateAsync] from an event handler.
 */
@Stable
class NFormState {
    var errors by mutableStateOf<Map<String, List<String>>>(emptyMap())
        private set

    /** Warnings produced by rules with [NFormRuleLevel.Warning]. */
    var warnings by mutableStateOf<Map<String, List<String>>>(emptyMap())
        private set

    /** Whether validation has run at least once since the last restore. */
    var validated by mutableStateOf(false)
        private set

    /** Whether an async validation is currently running. */
    var validating by mutableStateOf(false)
        private set

    /** Fallback callback used when validation is called without one. */
    var validateCallback: ((Boolean) -> Unit)? = null

    private var model: Map<String, Any?> = emptyMap()
    private var formRules: Map<String, List<NFormRule>> = emptyMap()
    private var validateMessages: Map<String, String> = emptyMap()
    private val itemRules = LinkedHashMap<String, List<NFormRule>>()

    internal fun attach(
        model: Map<String, Any?>,
        rules: Map<String, List<NFormRule>>,
        callback: ((Boolean) -> Unit)?,
        messages: Map<String, String>,
    ) {
        this.model = model
        this.formRules = rules
        this.validateCallback = callback
        this.validateMessages = messages
    }

    internal fun registerItemRules(path: String, rules: List<NFormRule>) {
        itemRules[path] = rules
    }

    internal fun unregisterItemRules(path: String) {
        itemRules.remove(path)
    }

    /**
     * Validates synchronously. Async validators are ignored; use
     * [validateAsync] to run them.
     *
     * When [paths] or [shouldRuleBeApplied] is set, only the selected paths and
     * rules are validated and their previous results are replaced, leaving
     * other form items untouched.
     */
    fun validate(
        callback: ((Boolean) -> Unit)? = null,
        paths: Set<String>? = null,
        shouldRuleBeApplied: ((NFormRule) -> Boolean)? = null,
    ): Boolean {
        val full = paths == null && shouldRuleBeApplied == null
        val result = evaluateSync(paths, shouldRuleBeApplied)
        if (full) {
            update(result.errors, result.warnings)
        } else {
            val base = paths ?: allRulePaths()
            val selected = if (shouldRuleBeApplied == null) {
                base
            } else {
                base.filter { path -> effectiveRules(path).any(shouldRuleBeApplied) }.toSet()
            }
            updatePartial(selected, result)
        }
        validated = true
        val valid = result.errors.isEmpty()
        (callback ?: validateCallback)?.invoke(valid)
        return valid
    }

    /**
     * Validates every path, awaiting all [NFormRule.asyncValidator] callbacks.
     */
    suspend fun validateAsync(callback: ((Boolean) -> Unit)? = null): Boolean {
        validating = true
        try {
            val result = evaluateAsync()
            update(result.errors, result.warnings)
            validated = true
            val valid = result.errors.isEmpty()
            (callback ?: validateCallback)?.invoke(valid)
            return valid
        } finally {
            validating = false
        }
    }

    /** Validates a single path, leaving other errors untouched. */
    fun validatePath(path: String, callback: ((Boolean) -> Unit)? = null): Boolean {
        val result = evaluateSync(setOf(path), null)
        updatePartial(setOf(path), result)
        validated = true
        val valid = result.errors.isEmpty()
        callback?.invoke(valid)
        return valid
    }

    fun restoreValidation() {
        validated = false
        update(emptyMap(), emptyMap())
    }

    fun errorOf(path: String): String? = errors[path]?.firstOrNull()

    fun errorsOf(path: String): List<String> = errors[path].orEmpty()

    fun warningOf(path: String): String? = warnings[path]?.firstOrNull()

    fun warningsOf(path: String): List<String> = warnings[path].orEmpty()

    fun statusOf(path: String): NStatus = when {
        errors.containsKey(path) -> NStatus.Error
        warnings.containsKey(path) -> NStatus.Warning
        else -> NStatus.Default
    }

    internal fun revalidate() {
        if (!validated) return
        val result = evaluateSync(null, null)
        update(result.errors, result.warnings)
    }

    private fun update(
        nextErrors: Map<String, List<String>>,
        nextWarnings: Map<String, List<String>>,
    ) {
        if (nextErrors != errors) errors = nextErrors
        if (nextWarnings != warnings) warnings = nextWarnings
    }

    private fun updatePartial(paths: Set<String>, result: Evaluation) {
        val nextErrors = errors.toMutableMap()
        val nextWarnings = warnings.toMutableMap()
        paths.forEach { path ->
            nextErrors.remove(path)
            nextWarnings.remove(path)
        }
        nextErrors.putAll(result.errors)
        nextWarnings.putAll(result.warnings)
        update(nextErrors, nextWarnings)
    }

    private fun allRulePaths(): Set<String> {
        val paths = LinkedHashSet<String>()
        paths.addAll(formRules.keys)
        paths.addAll(itemRules.keys)
        return paths
    }

    private fun evaluateSync(
        paths: Set<String>?,
        shouldRuleBeApplied: ((NFormRule) -> Boolean)?,
    ): Evaluation {
        val errors = LinkedHashMap<String, List<String>>()
        val warnings = LinkedHashMap<String, List<String>>()
        (paths ?: allRulePaths()).forEach { path ->
            val value = valueAt(path)
            effectiveRules(path)
                .filter { shouldRuleBeApplied == null || shouldRuleBeApplied(it) }
                .forEach { rule ->
                    val message = rule.check(value, path, validateMessages)
                    if (message != null) {
                        if (rule.level == NFormRuleLevel.Warning) {
                            warnings.add(path, message)
                        } else {
                            errors.add(path, message)
                        }
                    }
                }
        }
        return Evaluation(errors, warnings)
    }

    private suspend fun evaluateAsync(): Evaluation {
        val errors = LinkedHashMap<String, List<String>>()
        val warnings = LinkedHashMap<String, List<String>>()
        allRulePaths().forEach { path ->
            val value = valueAt(path)
            effectiveRules(path).forEach { rule ->
                val message = rule.check(value, path, validateMessages)
                    ?: rule.asyncCheck(value, path, validateMessages)
                if (message != null) {
                    if (rule.level == NFormRuleLevel.Warning) {
                        warnings.add(path, message)
                    } else {
                        errors.add(path, message)
                    }
                }
            }
        }
        return Evaluation(errors, warnings)
    }

    private fun effectiveRules(path: String): List<NFormRule> =
        itemRules[path] ?: formRules[path].orEmpty()

    private fun valueAt(path: String): Any? {
        var current: Any? = model
        path.split('.').forEach { segment ->
            current = (current as? Map<*, *>)?.get(segment.substringBefore('['))
            INDEX.find(segment)?.let { match ->
                current = (current as? List<*>)?.getOrNull(match.groupValues[1].toInt())
            }
        }
        return current
    }

    private companion object {
        val INDEX = Regex("\\[(\\d+)]")
    }
}

@Composable
fun rememberNFormState(): NFormState = remember { NFormState() }

/** Builds a rule map for [NForm] with one rule per path. */
fun nFormRules(vararg rules: Pair<String, NFormRule>): Map<String, List<NFormRule>> =
    rules.associate { (path, rule) -> path to listOf(rule) }

/** Render context shared by the form and its items. */
@Immutable
data class NFormContext(
    val state: NFormState,
    val size: NSize,
    val labelPlacement: NFormLabelPlacement,
    val labelWidth: Dp?,
    val showFeedback: Boolean,
    val showLabel: Boolean,
    val showRequireMark: Boolean,
    val inline: Boolean,
    val disabled: Boolean,
    val rules: Map<String, List<NFormRule>>,
    val validateMessages: Map<String, String>,
) {
    val validateCallback: ((Boolean) -> Unit)? get() = state.validateCallback

    fun validate(callback: ((Boolean) -> Unit)? = null): Boolean = state.validate(callback)

    fun restoreValidation() = state.restoreValidation()
}

val LocalNForm = staticCompositionLocalOf<NFormContext?> { null }

/** Whether the enclosing form is disabled. */
val LocalNFormDisabled = staticCompositionLocalOf { false }

/** Validation status of the enclosing [NFormItem], consumed by inputs. */
val LocalNFormItemStatus = staticCompositionLocalOf<NStatus?> { null }

/**
 * Collects and validates data. Rules are keyed by model path and can be
 * overridden per item through [NFormItem]'s `rule`/`rules` parameters.
 *
 * The [model] map is read when validation runs, so demos should rebuild it
 * from their state on every composition.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NForm(
    model: Map<String, Any?>,
    modifier: Modifier = Modifier,
    rules: Map<String, List<NFormRule>> = emptyMap(),
    state: NFormState = rememberNFormState(),
    size: NSize? = null,
    labelPlacement: NFormLabelPlacement = NFormLabelPlacement.Top,
    labelWidth: Dp? = null,
    showLabel: Boolean = true,
    showFeedback: Boolean = true,
    showRequireMark: Boolean = true,
    inline: Boolean = false,
    disabled: Boolean = false,
    validateMessages: Map<String, String> = emptyMap(),
    validateCallback: ((Boolean) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val resolvedSize = nSize(size)
    SideEffect { state.attach(model, rules, validateCallback, validateMessages) }
    LaunchedEffect(state, model) { state.revalidate() }

    val context = NFormContext(
        state = state,
        size = resolvedSize,
        labelPlacement = labelPlacement,
        labelWidth = labelWidth,
        showFeedback = showFeedback,
        showLabel = showLabel,
        showRequireMark = showRequireMark,
        inline = inline,
        disabled = disabled,
        rules = rules,
        validateMessages = validateMessages,
    )
    CompositionLocalProvider(
        LocalNForm provides context,
        LocalNFormDisabled provides disabled,
        LocalNComponentSize provides resolvedSize,
    ) {
        if (inline) {
            FlowRow(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                content()
            }
        } else {
            Column(modifier) { content() }
        }
    }
}

/**
 * A labeled form field. Errors (and warnings) produced by the enclosing form
 * are rendered as feedback below the content; [feedback] provides a static
 * hint when neither is present.
 */
@Composable
fun NFormItem(
    path: String? = null,
    label: String? = null,
    modifier: Modifier = Modifier,
    rule: NFormRule? = null,
    rules: List<NFormRule> = emptyList(),
    required: Boolean = false,
    showFeedback: Boolean? = null,
    feedback: String? = null,
    validationMessage: String? = null,
    validationStatus: NStatus? = null,
    feedbackAlignment: Alignment.Horizontal? = null,
    feedbackColor: Color? = null,
    labelPlacement: NFormLabelPlacement? = null,
    labelWidth: Dp? = null,
    size: NSize? = null,
    first: Boolean = false,
    showLabel: Boolean? = null,
    extra: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val form = LocalNForm.current
    val resolvedSize = nSize(size ?: form?.size)
    val placement = labelPlacement ?: form?.labelPlacement ?: NFormLabelPlacement.Top
    val resolvedShowLabel = showLabel ?: form?.showLabel ?: true
    val resolvedShowFeedback = showFeedback ?: form?.showFeedback ?: true
    val showRequireMark = form?.showRequireMark ?: true
    val inline = form?.inline == true

    val localRules = when {
        rules.isNotEmpty() -> rules
        rule != null -> listOf(rule)
        else -> null
    }
    val effectiveRules = localRules ?: (path?.let { form?.rules?.get(it) } ?: emptyList())
    val isRequired = required || effectiveRules.any { it.required }

    if (form != null && path != null && localRules != null) {
        SideEffect { form.state.registerItemRules(path, localRules) }
        DisposableEffect(form, path) {
            onDispose { form.state.unregisterItemRules(path) }
        }
    }

    val errors = if (form != null && path != null) form.state.errorsOf(path) else emptyList()
    val warnings = if (form != null && path != null) form.state.warningsOf(path) else emptyList()
    val messages = if (errors.isNotEmpty()) errors else warnings
    val stateStatus = if (form != null && path != null) {
        form.state.statusOf(path)
    } else {
        NStatus.Default
    }
    val explicitValidation = validationMessage?.let { listOf(it) }
    val status = validationStatus ?: when {
        explicitValidation != null -> NStatus.Error
        else -> stateStatus.takeIf { it != NStatus.Default }
    }
    val feedbackLines = when {
        messages.isNotEmpty() -> messages
        explicitValidation != null -> explicitValidation
        else -> listOfNotNull(feedback)
    }
    val rootModifier = if (inline) modifier else modifier.fillMaxWidth()

    Column(rootModifier.padding(top = if (first) 0.dp else 12.dp)) {
        val labelContent: @Composable () -> Unit = {
            if (resolvedShowLabel && label != null) {
                FormItemLabel(label, isRequired && showRequireMark, resolvedSize)
            }
        }
        if (placement == NFormLabelPlacement.Top) {
            if (resolvedShowLabel && label != null) {
                Box(Modifier.padding(bottom = 6.dp, start = 2.dp)) { labelContent() }
            }
            FormItemContent(resolvedSize, status, inline, content)
            extra?.let { Box(Modifier.fillMaxWidth().padding(top = 2.dp)) { it() } }
            if (resolvedShowFeedback) {
                FormItemFeedback(
                    feedbackLines,
                    status,
                    resolvedSize,
                    inline,
                    feedbackAlignment,
                    feedbackColor,
                )
            }
        } else {
            Row(
                if (inline) Modifier else Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                if (resolvedShowLabel && label != null) {
                    Box(
                        Modifier
                            .width(labelWidth ?: form?.labelWidth ?: 96.dp)
                            .padding(end = 12.dp, top = 6.dp),
                    ) { labelContent() }
                }
                Column(Modifier.weight(1f)) {
                    FormItemContent(resolvedSize, status, inline, content)
                    extra?.let { Box(Modifier.fillMaxWidth().padding(top = 2.dp)) { it() } }
                    if (resolvedShowFeedback) {
                        FormItemFeedback(
                            feedbackLines,
                            status,
                            resolvedSize,
                            inline,
                            feedbackAlignment,
                            feedbackColor,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FormItemLabel(text: String, required: Boolean, size: NSize) {
    val theme = LocalNTheme.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        NText(
            text = text,
            color = theme.colors.text1,
            size = theme.typography.fontSize.of(size),
        )
        if (required) {
            NText(
                text = "*",
                color = theme.colors.error,
                size = theme.typography.fontSize.of(size),
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }
}

@Composable
private fun FormItemContent(
    size: NSize,
    status: NStatus?,
    inline: Boolean,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalNComponentSize provides size,
        LocalNFormItemStatus provides status,
    ) {
        Box(if (inline) Modifier else Modifier.fillMaxWidth()) { content() }
    }
}

@Composable
private fun FormItemFeedback(
    lines: List<String>,
    status: NStatus?,
    size: NSize,
    inline: Boolean,
    alignment: Alignment.Horizontal?,
    colorOverride: Color?,
) {
    val theme = LocalNTheme.current
    val color = colorOverride ?: when (status) {
        NStatus.Error -> theme.colors.error
        NStatus.Warning -> theme.colors.warning
        NStatus.Success -> theme.colors.success
        else -> theme.colors.text3
    }
    val fontSize = if (size == NSize.Small) 13.sp else theme.typography.fontSize.of(size)
    Column(
        Modifier
            .then(if (inline) Modifier else Modifier.fillMaxWidth())
            .heightIn(min = 24.dp)
            .padding(top = 4.dp, start = 2.dp),
        horizontalAlignment = alignment ?: Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        lines.forEach { line ->
            NText(text = line, color = color, size = fontSize)
        }
    }
}
