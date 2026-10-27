package cn.enaium.naiveui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.internal.NScrollbar as InternalNScrollbar

/**
 * Scroll container with a naive-ui style overlay scrollbar. Give the container
 * a bounded size (for example with `Modifier.height`) so it can scroll.
 */
@Composable
fun NScrollbar(
    modifier: Modifier = Modifier,
    horizontal: Boolean = false,
    alwaysShow: Boolean = false,
    scrollState: ScrollState = rememberScrollState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit,
) {
    InternalNScrollbar(
        modifier = modifier,
        horizontal = horizontal,
        alwaysShow = alwaysShow,
        scrollState = scrollState,
        contentPadding = contentPadding,
        content = content,
    )
}
