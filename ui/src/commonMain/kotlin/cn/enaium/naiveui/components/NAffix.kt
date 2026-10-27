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
 * The content stays in normal flow until [triggerTop] pixels have been
 * scrolled, then it is translated down by the same amount plus the difference
 * between [top] and [triggerTop]. Place it inside the same
 * [androidx.compose.foundation.verticalScroll] / `NScrollbar` that owns
 * [scrollState].
 *
 * When [triggerBottom] is provided the affix triggers while approaching the
 * end of the scroll range instead.
 *
 * When [scrollState] is null the content is rendered without sticking.
 */
@Composable
fun NAffix(
    scrollState: ScrollState? = null,
    offsetTop: Dp = 0.dp,
    modifier: Modifier = Modifier,
    triggerTop: Dp = offsetTop,
    top: Dp = offsetTop,
    triggerBottom: Dp? = null,
    bottom: Dp? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    if (scrollState == null) {
        Box(modifier, content = content)
        return
    }
    val density = LocalDensity.current
    val offsetPx = if (triggerBottom != null) {
        val trigger = (scrollState.maxValue - with(density) { triggerBottom.toPx() }).coerceAtLeast(0f)
        val stuck = (scrollState.value.toFloat() - trigger).coerceAtLeast(0f)
        val extra = with(density) { ((bottom ?: triggerBottom) - triggerBottom).toPx() }.coerceAtLeast(0f)
        stuck + extra
    } else {
        val trigger = with(density) { triggerTop.toPx() }
        val stuck = (scrollState.value.toFloat() - trigger).coerceAtLeast(0f)
        val extra = with(density) { (top - triggerTop).toPx() }.coerceAtLeast(0f)
        stuck + extra
    }
    val offset = with(density) { offsetPx.toDp() }
    Box(modifier.offset(y = offset)) {
        content()
    }
}
