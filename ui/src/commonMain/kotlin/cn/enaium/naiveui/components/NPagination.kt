package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NInput
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NPopoverSurface
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.nSize
import kotlin.math.ceil

private sealed interface NPageItem {
    data class Page(val value: Int) : NPageItem
    data class Jump(val forward: Boolean, val target: Int) : NPageItem
}

/**
 * Pagination with page buttons, ellipsis jumping, optional size picker and
 * quick jumper.
 *
 * Either [pageCount] or [itemCount] determines the number of pages; [pageSize]
 * is used for [itemCount] and for the size picker.
 */
@Composable
fun NPagination(
    page: Int,
    pageCount: Int? = null,
    onPageChange: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    pageSize: Int = 10,
    onPageSizeChange: ((Int) -> Unit)? = null,
    pageSizes: List<Int> = listOf(10),
    showSizePicker: Boolean = false,
    showQuickJumper: Boolean = false,
    size: NSize? = null,
    disabled: Boolean = false,
    simple: Boolean = false,
    itemCount: Int? = null,
    pageSlot: Int = 9,
) {
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    val resolvedSize = nSize(size)
    val itemHeight: Dp = when (resolvedSize) {
        NSize.Tiny, NSize.Small -> 22.dp
        NSize.Medium -> 28.dp
        NSize.Large -> 34.dp
    }
    val iconSize = when (resolvedSize) {
        NSize.Tiny, NSize.Small -> 14.dp
        NSize.Medium -> 16.dp
        NSize.Large -> 18.dp
    }
    val fontSize = theme.fontSizeFor(resolvedSize)
    val totalPages = (
        pageCount
            ?: itemCount?.let { ceil(it.toFloat() / pageSize.coerceAtLeast(1)).toInt() }
            ?: 1
        ).coerceAtLeast(1)
    val current = page.coerceIn(1, totalPages)

    fun goTo(target: Int) {
        if (disabled) return
        val next = target.coerceIn(1, totalPages)
        if (next != page) onPageChange(next)
    }

    if (simple) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PageArrow(
                forward = false,
                enabled = !disabled && current > 1,
                itemHeight = itemHeight,
                iconSize = iconSize,
            ) { goTo(current - 1) }
            SimplePageInput(
                page = current,
                pageCount = totalPages,
                size = resolvedSize,
                disabled = disabled,
                onGo = ::goTo,
            )
            PageArrow(
                forward = true,
                enabled = !disabled && current < totalPages,
                itemHeight = itemHeight,
                iconSize = iconSize,
            ) { goTo(current + 1) }
        }
        return
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PageArrow(
            forward = false,
            enabled = !disabled && current > 1,
            itemHeight = itemHeight,
            iconSize = iconSize,
        ) { goTo(current - 1) }

        buildPageItems(current, totalPages, pageSlot).forEach { item ->
            when (item) {
                is NPageItem.Page -> PageButton(
                    page = item.value,
                    active = item.value == current,
                    disabled = disabled,
                    itemHeight = itemHeight,
                    fontSize = fontSize,
                    onClick = { goTo(item.value) },
                )
                is NPageItem.Jump -> PageJumpButton(
                    forward = item.forward,
                    disabled = disabled,
                    itemHeight = itemHeight,
                    iconSize = iconSize,
                    onClick = { goTo(item.target) },
                )
            }
        }

        PageArrow(
            forward = true,
            enabled = !disabled && current < totalPages,
            itemHeight = itemHeight,
            iconSize = iconSize,
        ) { goTo(current + 1) }

        if (showSizePicker && onPageSizeChange != null) {
            val sizes = (pageSizes + pageSize).distinct().sorted()
            var open by remember { mutableStateOf(false) }
            Box {
                Row(
                    modifier = Modifier
                        .height(itemHeight)
                        .clip(RoundedCornerShape(theme.borderRadius))
                        .nBorder(theme.colors.border, 1.dp, theme.borderRadius)
                        .nClickable(remember { MutableInteractionSource() }, enabled = !disabled) {
                            open = !open
                        }
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    NBaseText(
                        text = "$pageSize ${locale.itemsPerPage}",
                        style = theme.typography.style(fontSize, color = if (disabled) theme.colors.textDisabled else theme.colors.text2),
                    )
                    NIcon(
                        NIcons.ChevronDown,
                        size = 14.dp,
                        color = if (disabled) theme.colors.iconDisabled else theme.colors.icon,
                    )
                }
                NPopoverSurface(
                    visible = open && !disabled,
                    onDismiss = { open = false },
                    placement = NPlacement.BottomStart,
                    offset = androidx.compose.ui.unit.IntOffset(0, 4),
                    padding = PaddingValues(4.dp),
                ) {
                    Column(Modifier.widthIn(min = 120.dp)) {
                        sizes.forEach { option ->
                            val selected = option == pageSize
                            val interaction = remember(option) { MutableInteractionSource() }
                            val hovered by interaction.collectIsHoveredAsState()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(30.dp)
                                    .clip(RoundedCornerShape(theme.borderRadiusSmall))
                                    .background(if (hovered) theme.colors.hover else Color.Transparent)
                                    .hoverable(interaction)
                                    .nClickable(interaction) {
                                        open = false
                                        if (!selected) onPageSizeChange(option)
                                    }
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                NBaseText(
                                    text = "$option ${locale.itemsPerPage}",
                                    style = theme.typography.style(
                                        fontSize,
                                        if (selected) FontWeight.Medium else theme.typography.fontWeight,
                                        if (selected) theme.colors.primary else theme.colors.text2,
                                    ),
                                )
                                Spacer(Modifier.weight(1f))
                                if (selected) {
                                    NIcon(NIcons.Checkmark, size = 14.dp, color = theme.colors.primary)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showQuickJumper) {
            var quick by remember { mutableStateOf(current.toString()) }
            LaunchedEffect(current) { quick = current.toString() }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                NText(locale.jumpTo, size = fontSize, color = theme.colors.text2)
                NInput(
                    value = quick,
                    onValueChange = { value -> quick = value.filter { it.isDigit() }.take(6) },
                    modifier = Modifier.width(56.dp),
                    size = resolvedSize,
                    disabled = disabled,
                )
                NButton(
                    onClick = {
                        quick.toIntOrNull()?.let { goTo(it) }
                        quick = current.toString()
                    },
                    size = resolvedSize,
                    text = true,
                    disabled = disabled,
                ) {
                    NText(locale.go, size = fontSize)
                }
            }
        }
    }
}

@Composable
private fun PageButton(
    page: Int,
    active: Boolean,
    disabled: Boolean,
    itemHeight: Dp,
    fontSize: androidx.compose.ui.unit.TextUnit,
    onClick: () -> Unit,
) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val textColor = when {
        disabled -> theme.colors.textDisabled
        active -> theme.colors.primary
        hovered -> theme.colors.primaryHover
        else -> theme.colors.text2
    }
    val border = when {
        active && disabled -> theme.colors.primary.copy(alpha = 0.4f)
        active -> theme.colors.primary
        disabled -> theme.colors.border
        else -> Color.Transparent
    }
    Box(
        modifier = Modifier
            .height(itemHeight)
            .widthIn(min = itemHeight)
            .clip(RoundedCornerShape(theme.borderRadius))
            .nBorder(border, 1.dp, theme.borderRadius)
            .hoverable(interaction)
            .nClickable(interaction, enabled = !disabled && !active, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        NBaseText(
            text = page.toString(),
            style = theme.typography.style(
                fontSize,
                if (active) FontWeight.Medium else theme.typography.fontWeight,
                textColor,
            ),
            modifier = Modifier.padding(horizontal = 6.dp),
        )
    }
}

@Composable
private fun PageJumpButton(
    forward: Boolean,
    disabled: Boolean,
    itemHeight: Dp,
    iconSize: Dp,
    onClick: () -> Unit,
) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Box(
        modifier = Modifier
            .height(itemHeight)
            .width(itemHeight)
            .clip(RoundedCornerShape(theme.borderRadius))
            .hoverable(interaction)
            .nClickable(interaction, enabled = !disabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        NIcon(
            if (forward) NIcons.FastForward else NIcons.FastBackward,
            size = iconSize,
            color = when {
                disabled -> theme.colors.textDisabled
                hovered -> theme.colors.primaryHover
                else -> theme.colors.text2
            },
        )
    }
}

@Composable
private fun PageArrow(
    forward: Boolean,
    enabled: Boolean,
    itemHeight: Dp,
    iconSize: Dp,
    onClick: () -> Unit,
) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Box(
        modifier = Modifier
            .height(itemHeight)
            .width(itemHeight)
            .clip(RoundedCornerShape(theme.borderRadius))
            .nBorder(
                if (enabled && hovered) theme.colors.primaryHover else theme.colors.border,
                1.dp,
                theme.borderRadius,
            )
            .hoverable(interaction)
            .nClickable(interaction, enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        NIcon(
            if (forward) NIcons.Forward else NIcons.Backward,
            size = iconSize,
            color = when {
                !enabled -> theme.colors.textDisabled
                hovered -> theme.colors.primaryHover
                else -> theme.colors.text2
            },
        )
    }
}

@Composable
private fun SimplePageInput(
    page: Int,
    pageCount: Int,
    size: NSize,
    disabled: Boolean,
    onGo: (Int) -> Unit,
) {
    val theme = LocalNTheme.current
    var text by remember { mutableStateOf(page.toString()) }
    LaunchedEffect(page) { text = page.toString() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        NInput(
            value = text,
            onValueChange = { value -> text = value.filter { it.isDigit() }.take(6) },
            modifier = Modifier.width(48.dp),
            size = size,
            disabled = disabled,
        )
        NBaseText(
            text = "/ $pageCount",
            style = theme.typography.style(theme.typography.fontSize.medium, color = theme.colors.text2),
        )
        NButton(
            onClick = {
                text.toIntOrNull()?.let(onGo)
                text = page.toString()
            },
            size = size,
            text = true,
            disabled = disabled,
        ) {
            NText(LocalNLocale.current.go, size = theme.typography.fontSize.medium)
        }
    }
}

private fun buildPageItems(current: Int, pageCount: Int, pageSlot: Int): List<NPageItem> {
    val slot = pageSlot.coerceAtLeast(5)
    if (pageCount <= slot) {
        return (1..pageCount).map { NPageItem.Page(it) }
    }
    val items = mutableListOf<NPageItem>()
    if (current <= slot - 2) {
        for (i in 1..slot - 2) items += NPageItem.Page(i)
        items += NPageItem.Jump(forward = true, target = (current + 5).coerceAtMost(pageCount))
        items += NPageItem.Page(pageCount)
    } else if (current >= pageCount - (slot - 3)) {
        items += NPageItem.Page(1)
        items += NPageItem.Jump(forward = false, target = (current - 5).coerceAtLeast(1))
        for (i in pageCount - (slot - 3)..pageCount) items += NPageItem.Page(i)
    } else {
        val window = slot - 4
        val start = (current - window / 2).coerceIn(2, pageCount - window)
        items += NPageItem.Page(1)
        items += NPageItem.Jump(forward = false, target = (current - 5).coerceAtLeast(1))
        for (i in start until start + window) items += NPageItem.Page(i)
        items += NPageItem.Jump(forward = true, target = (current + 5).coerceAtMost(pageCount))
        items += NPageItem.Page(pageCount)
    }
    return items
}
