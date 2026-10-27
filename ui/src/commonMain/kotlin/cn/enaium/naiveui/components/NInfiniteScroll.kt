package cn.enaium.naiveui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.internal.NScrollbar

/**
 * Scroll container that calls [onLoadMore] when the content is scrolled within
 * [distance] of the bottom.
 *
 * Loading is skipped while [loading] is true and the whole trigger can be
 * turned off with [disabled]. Give the component a bounded height through
 * [modifier]; content is expected to grow after each load.
 */
@Composable
fun NInfiniteScroll(
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    distance: Dp = 0.dp,
    disabled: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrollState = rememberScrollState()
    val latestOnLoadMore by rememberUpdatedState(onLoadMore)
    val distancePx = with(LocalDensity.current) { distance.toPx() }
    var wasNearBottom by remember { mutableStateOf(false) }
    LaunchedEffect(scrollState, loading, disabled, distancePx) {
        if (disabled) return@LaunchedEffect
        snapshotFlow { scrollState.value to scrollState.maxValue }
            .collect { (value, max) ->
                if (max <= 0) {
                    wasNearBottom = false
                    return@collect
                }
                val nearBottom = value >= max - distancePx
                if (nearBottom && !wasNearBottom) {
                    wasNearBottom = true
                    if (!loading) latestOnLoadMore()
                } else if (!nearBottom) {
                    wasNearBottom = false
                }
            }
    }
    NScrollbar(modifier.fillMaxSize(), scrollState = scrollState) {
        Column(Modifier.fillMaxWidth()) {
            content()
        }
    }
}
