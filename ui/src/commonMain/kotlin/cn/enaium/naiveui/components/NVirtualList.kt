package cn.enaium.naiveui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.internal.NVirtualList as InternalNVirtualList

/**
 * Virtualized list that only composes the visible rows. Set a bounded height
 * on [modifier]; every row is [itemHeight] tall unless [itemResizable] is set.
 */
@Composable
fun <T> NVirtualList(
    items: List<T>,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 34.dp,
    itemResizable: Boolean = false,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    item: @Composable (T) -> Unit,
) {
    InternalNVirtualList(
        items = items,
        modifier = modifier,
        itemHeight = itemHeight,
        itemResizable = itemResizable,
        state = state,
        contentPadding = contentPadding,
        item = item,
    )
}
