package cn.enaium.naiveui.internal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * List that composes only visible items, mirroring `<n-virtual-list>`.
 *
 * When [itemResizable] is false each row is forced to [itemHeight]; otherwise
 * rows are measured normally.
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
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = state,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.Top,
    ) {
        items(items) { value ->
            Box(
                if (itemResizable) Modifier.fillMaxWidth() else Modifier.fillMaxWidth().height(itemHeight),
            ) {
                item(value)
            }
        }
    }
}
