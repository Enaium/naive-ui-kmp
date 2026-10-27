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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.theme.LocalNComponentSize
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import cn.enaium.naiveui.theme.nSize

/** Label placement used by [NForm] and [NFormItem]. */
enum class NFormLabelPlacement { Top, Left }

/** Value types understood by [NFormRule]. */
enum class NFormRuleType { String, Number, Array, Boolean, Email }

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
) {
    internal fun check(value: Any?): String? {
        if (isEmptyValue(value)) {
            return if (required) message ?: "This field is required" else null
        }
        if (type != null && !type.accepts(value)) {
            return message ?: "Invalid value"
        }
        val size = measure(value)
        if (min != null && size != null && size < min) {
            return message ?: "Value should be at least $min"
        }
        if (max != null && size != null && size > max) {
            return message ?: "Value should be at most $max"
        }
        if (pattern != null && (value !is String || !pattern.matches(value))) {
            return message ?: "Invalid format"
        }
        if (validator != null && !validator.invoke(value)) {
            return message ?: "Validation failed"
        }
        return null
    }
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

/**
 * Validation state shared by a form and its items. Create it with
 * [rememberNFormState] and pass it to [NForm], then call [validate] from a
 * button or any other event handler.
 */
@Stable
class NFormState {
    var errors by mutableStateOf<Map<String, List<String>>>(emptyMap())
        private set

    /** Whether [validate] has run at least once since the last restore. */
    var validated by mutableStateOf(false)
        private set

    /** Fallback callback used when [validate] is called without one. */
    var validateCallback: ((Boolean) -> Unit)? = null

    private var model: Map<String, Any?> = emptyMap()
    private var formRules: Map<String, List<NFormRule>> = emptyMap()
    private val itemRules = LinkedHashMap<String, List<NFormRule>>()

    internal fun attach(
        model: Map<String, Any?>,
        rules: Map<String, List<NFormRule>>,
        callback: ((Boolean) -> Unit)?,
    ) {
        this.model = model
        this.formRules = rules
        this.validateCallback = callback
    }

    internal fun registerItemRules(path: String, rules: List<NFormRule>) {
        itemRules[path] = rules
    }

    internal fun unregisterItemRules(path: String) {
        itemRules.remove(path)
    }

    /**
     * Validates every registered path and updates [errors]. Returns whether
     * the form is valid.
     */
    fun validate(callback: ((Boolean) -> Unit)? = null): Boolean {
        val next = evaluate()
        update(next)
        validated = true
        val valid = next.isEmpty()
        (callback ?: validateCallback)?.invoke(valid)
        return valid
    }

    /** Validates a single path, leaving other errors untouched. */
    fun validatePath(path: String, callback: ((Boolean) -> Unit)? = null): Boolean {
        val messages = effectiveRules(path).mapNotNull { it.check(valueAt(path)) }
        val next = errors.toMutableMap()
        if (messages.isEmpty()) next.remove(path) else next[path] = messages
        update(next)
        validated = true
        val valid = messages.isEmpty()
        (callback ?: validateCallback)?.invoke(valid)
        return valid
    }

    fun restoreValidation() {
        validated = false
        update(emptyMap())
    }

    fun errorOf(path: String): String? = errors[path]?.firstOrNull()

    fun errorsOf(path: String): List<String> = errors[path].orEmpty()

    fun statusOf(path: String): NStatus =
        if (errors.containsKey(path)) NStatus.Error else NStatus.Default

    internal fun revalidate() {
        if (validated) update(evaluate())
    }

    private fun update(next: Map<String, List<String>>) {
        if (next != errors) errors = next
    }

    private fun evaluate(): Map<String, List<String>> {
        val paths = LinkedHashSet<String>()
        paths.addAll(formRules.keys)
        paths.addAll(itemRules.keys)
        val result = LinkedHashMap<String, List<String>>()
        paths.forEach { path ->
            val messages = effectiveRules(path).mapNotNull { it.check(valueAt(path)) }
            if (messages.isNotEmpty()) result[path] = messages
        }
        return result
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
    val rules: Map<String, List<NFormRule>>,
) {
    fun validate(callback: ((Boolean) -> Unit)? = null): Boolean = state.validate(callback)

    fun restoreValidation() = state.restoreValidation()
}

val LocalNForm = staticCompositionLocalOf<NFormContext?> { null }

/** Validation status of the enclosing [NFormItem], consumed by inputs. */
val LocalNFormItemStatus = staticCompositionLocalOf<NStatus?> { null }

/**
 * Collects and validates data. Rules are keyed by model path and can be
 * overridden per item through [NFormItem]'s `rule`/`rules` parameters.
 *
 * The [model] map is read when [NFormState.validate] runs, so demos should
 * rebuild it from their state on every composition.
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
    validateCallback: ((Boolean) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val resolvedSize = nSize(size)
    SideEffect { state.attach(model, rules, validateCallback) }
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
        rules = rules,
    )
    CompositionLocalProvider(
        LocalNForm provides context,
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
 * A labeled form field. Errors produced by the enclosing form are rendered as
 * feedback below the content; [feedback] provides a static hint when no error
 * is present.
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
    labelPlacement: NFormLabelPlacement? = null,
    labelWidth: Dp? = null,
    size: NSize? = null,
    first: Boolean = false,
    showLabel: Boolean? = null,
    extra: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val theme = LocalNTheme.current
    val form = LocalNForm.current
    val resolvedSize = nSize(size ?: form?.size)
    val placement = labelPlacement ?: form?.labelPlacement ?: NFormLabelPlacement.Top
    val resolvedShowLabel = showLabel ?: form?.showLabel ?: true
    val resolvedShowFeedback = showFeedback ?: form?.showFeedback ?: true
    val showRequireMark = form?.showRequireMark ?: true

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

    val messages = if (form != null && path != null) form.state.errorsOf(path) else emptyList()
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
    val rootModifier = if (form?.inline == true) modifier else modifier.fillMaxWidth()

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
            FormItemContent(resolvedSize, status, form?.inline == true, content)
            extra?.let { Box(Modifier.fillMaxWidth().padding(top = 2.dp)) { it() } }
            if (resolvedShowFeedback) {
                FormItemFeedback(feedbackLines, status, resolvedSize)
            }
        } else {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                if (resolvedShowLabel && label != null) {
                    Box(
                        Modifier
                            .width(labelWidth ?: form?.labelWidth ?: 96.dp)
                            .padding(end = 12.dp, top = 6.dp),
                    ) { labelContent() }
                }
                Column(Modifier.weight(1f)) {
                    FormItemContent(resolvedSize, status, form?.inline == true, content)
                    extra?.let { Box(Modifier.fillMaxWidth().padding(top = 2.dp)) { it() } }
                    if (resolvedShowFeedback) {
                        FormItemFeedback(feedbackLines, status, resolvedSize)
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
) {
    val theme = LocalNTheme.current
    val color = when (status) {
        NStatus.Error -> theme.colors.error
        NStatus.Warning -> theme.colors.warning
        NStatus.Success -> theme.colors.success
        else -> theme.colors.text3
    }
    val fontSize = if (size == NSize.Small) 13.sp else theme.typography.fontSize.of(size)
    Column(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 24.dp)
            .padding(top = 4.dp, start = 2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        lines.forEach { line ->
            NText(text = line, color = color, size = fontSize)
        }
    }
}
