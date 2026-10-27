package cn.enaium.naiveui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Keeps [content] pinned while its vertical scroll container moves.
 *
 * The content stays in normal flow until [offsetTop] pixels have been
 * scrolled, then it is translated down by the same amount, which holds it at
 * the position it had when sticking began. Place it inside the same
 * [androidx.compose.foundation.verticalScroll] / `NScrollbar` that owns
 * [scrollState].
 *
 * When [scrollState] is null the content is rendered without sticking.
 */
@Composable
fun NAffix(
    scrollState: ScrollState? = null,
    offsetTop: Dp = 0.dp,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    if (scrollState == null) {
        Box(modifier, content = content)
        return
    }
    val density = LocalDensity.current
    val threshold = with(density) { offsetTop.toPx() }
    val stuck = (scrollState.value.toFloat() - threshold).coerceAtLeast(0f)
    val offset = with(density) { stuck.toDp() }
    Box(modifier.offset(y = offset)) {
        content()
    }
}
