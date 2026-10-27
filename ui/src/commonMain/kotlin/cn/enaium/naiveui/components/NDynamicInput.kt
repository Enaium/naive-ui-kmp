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
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.nSize

private fun smallerSize(size: NSize): NSize = when (size) {
    NSize.Tiny -> NSize.Tiny
    NSize.Small -> NSize.Tiny
    NSize.Medium -> NSize.Small
    NSize.Large -> NSize.Medium
}

/**
 * Editable list of string values. Each row renders an input (or a custom
 * [item]) plus add/remove actions; the create button is shown when the list
 * is empty.
 */
@Composable
fun NDynamicInput(
    values: List<String>,
    onValueChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    min: Int = 0,
    max: Int? = null,
    placeholder: String = "",
    disabled: Boolean = false,
    showSortButton: Boolean = false,
    size: NSize? = null,
    item: (@Composable (value: String, index: Int, onValueChange: (String) -> Unit) -> Unit)? = null,
    createButtonContent: (@Composable () -> Unit)? = null,
) {
    val resolvedSize = nSize(size)
    val buttonSize = smallerSize(resolvedSize)
    val insertionDisabled = max != null && values.size >= max
    val removeDisabled = values.size <= min

    fun update(index: Int, value: String) {
        if (disabled) return
        onValueChange(values.toMutableList().apply { this[index] = value })
    }

    fun create(index: Int) {
        if (disabled || insertionDisabled) return
        val next = values.toMutableList()
        next.add((index + 1).coerceIn(0, next.size), "")
        onValueChange(next)
    }

    fun remove(index: Int) {
        if (disabled || removeDisabled) return
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
                if (createButtonContent != null) createButtonContent() else NText("Create")
            }
        } else {
            values.forEachIndexed { index, value ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.weight(1f)) {
                        if (item != null) {
                            item(value, index, { update(index, it) })
                        } else {
                            NInput(
                                value = value,
                                onValueChange = { update(index, it) },
                                placeholder = placeholder,
                                disabled = disabled,
                                size = resolvedSize,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
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
