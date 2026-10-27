package cn.enaium.naiveui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.nSize

/** Built-in item presets. */
enum class NDynamicInputPreset { Input, Pair }

/** Value used by the pair preset. */
data class NDynamicInputPair(val key: String, val value: String)

@Suppress("UNCHECKED_CAST")
private fun <T> castTo(value: Any?): T = value as T

@Suppress("UNCHECKED_CAST")
private fun <T> defaultPresetValue(preset: NDynamicInputPreset): T = when (preset) {
    NDynamicInputPreset.Input -> "" as T
    NDynamicInputPreset.Pair -> NDynamicInputPair("", "") as T
}

private fun smallerSize(size: NSize): NSize = when (size) {
    NSize.Tiny -> NSize.Tiny
    NSize.Small -> NSize.Tiny
    NSize.Medium -> NSize.Small
    NSize.Large -> NSize.Medium
}

/**
 * Editable list of values. Each row renders the preset content (an input or a
 * key/value pair) or a custom [item], plus add/remove actions. The create
 * button is shown when the list is empty.
 */
@Composable
fun <T> NDynamicInput(
    values: List<T>,
    onValueChange: (List<T>) -> Unit,
    modifier: Modifier = Modifier,
    min: Int = 0,
    max: Int? = null,
    placeholder: String = "",
    disabled: Boolean = false,
    showSortButton: Boolean = false,
    size: NSize? = null,
    preset: NDynamicInputPreset = NDynamicInputPreset.Input,
    keyPlaceholder: String = "",
    valuePlaceholder: String = "",
    onCreate: ((Int) -> T)? = null,
    onRemove: ((Int) -> Unit)? = null,
    item: (@Composable (value: T, index: Int, onValueChange: (T) -> Unit) -> Unit)? = null,
    action: (@Composable (
        value: T,
        index: Int,
        create: (Int) -> Unit,
        remove: (Int) -> Unit,
        move: (Int, Int) -> Unit,
    ) -> Unit)? = null,
    createButtonContent: (@Composable () -> Unit)? = null,
) {
    val resolvedSize = nSize(size)
    val buttonSize = smallerSize(resolvedSize)
    val insertionDisabled = max != null && values.size >= max
    val removeDisabled = values.size <= min

    fun update(index: Int, value: T) {
        if (disabled) return
        onValueChange(values.toMutableList().apply { this[index] = value })
    }

    fun create(index: Int) {
        if (disabled || insertionDisabled) return
        val next = values.toMutableList()
        val target = (index + 1).coerceIn(0, next.size)
        next.add(target, onCreate?.invoke(target) ?: defaultPresetValue<T>(preset))
        onValueChange(next)
    }

    fun remove(index: Int) {
        if (disabled || removeDisabled) return
        onRemove?.invoke(index)
        onValueChange(values.toMutableList().apply { removeAt(index) })
    }

    fun move(index: Int, offset: Int) {
        if (disabled) return
        val target = index + offset
        if (target !in values.indices) return
        val next = values.toMutableList()
        val current = next.removeAt(index)
        next.add(target, current)
        onValueChange(next)
    }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (values.isEmpty()) {
            NButton(
                onClick = { create(-1) },
                size = resolvedSize,
                block = true,
                dashed = true,
                ghost = true,
                disabled = disabled || insertionDisabled,
                icon = { NIcon(NIcons.Add, size = 16.dp) },
            ) {
                if (createButtonContent != null) {
                    createButtonContent()
                } else {
                    NText(LocalNLocale.current.dynamicInput.create)
                }
            }
        } else {
            values.forEachIndexed { index, value ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.weight(1f)) {
                        when {
                            item != null -> item(value, index, { update(index, it) })
                            preset == NDynamicInputPreset.Pair -> {
                                val pair = castTo<NDynamicInputPair>(value)
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    NInput(
                                        value = pair.key,
                                        onValueChange = {
                                            update(index, castTo<T>(NDynamicInputPair(it, pair.value)))
                                        },
                                        placeholder = keyPlaceholder,
                                        disabled = disabled,
                                        size = resolvedSize,
                                        modifier = Modifier.weight(1f),
                                    )
                                    NInput(
                                        value = pair.value,
                                        onValueChange = {
                                            update(index, castTo<T>(NDynamicInputPair(pair.key, it)))
                                        },
                                        placeholder = valuePlaceholder,
                                        disabled = disabled,
                                        size = resolvedSize,
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                            }
                            else -> NInput(
                                value = (value as? String) ?: value.toString(),
                                onValueChange = { update(index, castTo<T>(it)) },
                                placeholder = placeholder,
                                disabled = disabled,
                                size = resolvedSize,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                    if (action != null) {
                        action(
                            value,
                            index,
                            { create(it) },
                            { remove(it) },
                            { i, offset -> move(i, offset) },
                        )
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            NButton(
                                onClick = { remove(index) },
                                size = buttonSize,
                                circle = true,
                                disabled = disabled || removeDisabled,
                                icon = { NIcon(NIcons.Remove, size = 15.dp) },
                            )
                            NButton(
                                onClick = { create(index) },
                                size = buttonSize,
                                circle = true,
                                disabled = disabled || insertionDisabled,
                                icon = { NIcon(NIcons.Add, size = 15.dp) },
                            )
                            if (showSortButton) {
                                NButton(
                                    onClick = { move(index, -1) },
                                    size = buttonSize,
                                    circle = true,
                                    disabled = disabled || index == 0,
                                    icon = { NIcon(NIcons.ArrowUp, size = 14.dp) },
                                )
                                NButton(
                                    onClick = { move(index, 1) },
                                    size = buttonSize,
                                    circle = true,
                                    disabled = disabled || index == values.lastIndex,
                                    icon = { NIcon(NIcons.ArrowDown, size = 14.dp) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
