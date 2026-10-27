package cn.enaium.naiveui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.internal.NBaseLoading
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nShadow
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme

/**
 * Scrollable, monospace log viewer.
 *
 * Pass either a single [log] string or pre-split [lines]. When [language] is
 * set each line is syntax highlighted with the same compact highlighter used
 * by [NCode]. [loading] shows a loader and [loadingLines] skeleton rows, while
 * [count] caps how many trailing lines are rendered.
 *
 * Reaching either edge triggers [onReachTop] / [onReachBottom] and the
 * corresponding [onRequireMore] direction. Use [scrollState] to drive the
 * viewport (for example `scrollState.animateScrollTo(scrollState.maxValue)`).
 */
@Composable
fun NLog(
    modifier: Modifier = Modifier,
    log: String = "",
    lines: List<String> = emptyList(),
    loading: Boolean = false,
    loadingLines: Int = 1,
    count: Int? = null,
    rows: Int = 15,
    fontSize: TextUnit = 14.sp,
    lineHeight: Float = 1.25f,
    language: String? = null,
    trim: Boolean = false,
    autoScrollBottom: Boolean = true,
    offsetTop: Dp = 0.dp,
    offsetBottom: Dp = 0.dp,
    onReachTop: (() -> Unit)? = null,
    onReachBottom: (() -> Unit)? = null,
    onRequireMore: ((from: String) -> Unit)? = null,
    scrollState: ScrollState = rememberScrollState(),
) {
    val theme = LocalNTheme.current
    val density = LocalDensity.current
    val palette = remember(theme) { nCodePalette(theme) }
    val lineHeightDp = with(density) { (fontSize.toPx() * lineHeight).toDp() }
    val containerHeight = lineHeightDp * rows.coerceAtLeast(1)
    val renderedLines = remember(log, lines, trim, count) {
        val source = if (log.isNotEmpty()) log.split('\n') else lines
        val processed = if (trim) source.map { it.trim() } else source
        if (count != null && count >= 0) processed.takeLast(count) else processed
    }
    val annotatedLines = remember(renderedLines, language, palette) {
        renderedLines.map { line ->
            val safe = if (line.isEmpty()) " " else line
            if (language.isNullOrBlank()) AnnotatedString(safe) else nHighlightedCode(safe, language, palette)
        }
    }
    val textStyle = TextStyle(
        fontFamily = theme.typography.monoFamily,
        fontSize = fontSize,
        lineHeight = fontSize * lineHeight,
        color = theme.colors.text2,
    )

    val latestOnReachTop by rememberUpdatedState(onReachTop)
    val latestOnReachBottom by rememberUpdatedState(onReachBottom)
    val latestOnRequireMore by rememberUpdatedState(onRequireMore)
    val topOffsetPx = with(density) { offsetTop.toPx() }
    val bottomOffsetPx = with(density) { offsetBottom.toPx() }
    var atTop by remember { mutableStateOf(false) }
    var atBottom by remember { mutableStateOf(false) }
    LaunchedEffect(scrollState) {
        snapshotFlow {
            Triple(scrollState.value, scrollState.maxValue, scrollState.isScrollInProgress)
        }.collect { (value, max, scrolling) ->
            if (max <= 0) {
                atTop = true
                atBottom = true
                return@collect
            }
            val top = value <= topOffsetPx
            val bottom = max - value <= bottomOffsetPx
            if (scrolling && top && !atTop) {
                latestOnReachTop?.invoke()
                latestOnRequireMore?.invoke("top")
            }
            if (scrolling && bottom && !atBottom) {
                latestOnReachBottom?.invoke()
                latestOnRequireMore?.invoke("bottom")
            }
            atTop = top
            atBottom = bottom
        }
    }

    var stickToBottom by remember { mutableStateOf(true) }
    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.value to scrollState.maxValue }.collect { (value, max) ->
            if (max > 0) stickToBottom = value >= max - 4
        }
    }
    LaunchedEffect(renderedLines, autoScrollBottom, stickToBottom) {
        if (!autoScrollBottom || !stickToBottom) return@LaunchedEffect
        snapshotFlow { scrollState.maxValue }.collect { max ->
            scrollState.scrollTo(max)
        }
    }

    Box(modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(containerHeight)
                .clip(RoundedCornerShape(theme.borderRadius))
                .background(theme.colors.code),
        ) {
            NScrollbar(Modifier.fillMaxSize(), scrollState = scrollState) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    annotatedLines.forEach { line ->
                        BasicText(
                            text = line,
                            style = textStyle,
                            softWrap = false,
                        )
                    }
                    if (loading) {
                        val transition = rememberInfiniteTransition(label = "n-log-loading")
                        val alpha by transition.animateFloat(
                            initialValue = 0.25f,
                            targetValue = 0.6f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(800),
                                repeatMode = RepeatMode.Reverse,
                            ),
                            label = "n-log-loading-alpha",
                        )
                        repeat(loadingLines.coerceAtLeast(1)) { index ->
                            val fraction = 0.55f - (index % 3) * 0.1f
                            Box(
                                Modifier
                                    .fillMaxWidth(fraction)
                                    .height(lineHeightDp * 0.55f)
                                    .padding(vertical = 1.dp)
                                    .clip(RoundedCornerShape(theme.borderRadiusSmall))
                                    .background(theme.colors.text3.copy(alpha = alpha * 0.45f)),
                            )
                        }
                    }
                }
            }
        }
        if (loading) {
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp)
                    .nShadow(theme, 1, RoundedCornerShape(theme.borderRadius))
                    .clip(RoundedCornerShape(theme.borderRadius))
                    .background(theme.colors.modal)
                    .nBorder(theme.colors.border, 1.dp, theme.borderRadius)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    NBaseLoading(size = 14.dp)
                    NText(
                        text = LocalNLocale.current.loading,
                        size = theme.typography.fontSize.small,
                        color = theme.colors.text2,
                    )
                }
            }
        }
    }
}
