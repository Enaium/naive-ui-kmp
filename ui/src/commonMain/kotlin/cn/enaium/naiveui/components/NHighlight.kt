package cn.enaium.naiveui.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import cn.enaium.naiveui.internal.LocalNContentColor
import cn.enaium.naiveui.internal.LocalNContentTextStyle
import cn.enaium.naiveui.theme.LocalNTheme

/** A word or phrase to highlight, optionally with its own color and weight. */
@Immutable
data class NHighlightPattern(
    val text: String,
    val color: Color? = null,
    val bold: Boolean = false,
)

private fun nEscapeRegex(value: String): String = buildString(value.length) {
    for (c in value) {
        if (c in ".^$*+?()[]{}|\\") append('\\')
        append(c)
    }
}

private fun nHighlightRegex(
    patterns: List<NHighlightPattern>,
    caseSensitive: Boolean,
    wholeWord: Boolean,
): Regex? {
    val alternation = patterns
        .filter { it.text.isNotEmpty() }
        .map { pattern ->
            val escaped = nEscapeRegex(pattern.text)
            if (wholeWord) "\\b$escaped\\b" else escaped
        }
        .takeIf { it.isNotEmpty() }
        ?.joinToString("|")
        ?: return null
    return Regex(alternation, if (caseSensitive) emptySet() else setOf(RegexOption.IGNORE_CASE))
}

/**
 * Highlights every occurrence of the supplied [patterns] inside [text].
 *
 * Naive UI's `highlight-tag` maps onto per-pattern styling: a pattern can
 * carry its own text [NHighlightPattern.color] and [NHighlightPattern.bold].
 * The mark background comes from [highlightBackground] (a soft warning tint by
 * default); pass [Color.Transparent] to keep the text color only.
 */
@Composable
fun NHighlight(
    text: String,
    patterns: List<NHighlightPattern>,
    modifier: Modifier = Modifier,
    caseSensitive: Boolean = false,
    wholeWord: Boolean = false,
    highlightColor: Color? = null,
    highlightBackground: Color? = null,
    style: TextStyle? = null,
) {
    val theme = LocalNTheme.current
    val inherited = LocalNContentTextStyle.current ?: theme.typography.body
    val inheritedColor = LocalNContentColor.current
    val resolvedStyle = style ?: inherited
    val baseStyle = resolvedStyle.copy(
        color = resolvedStyle.color.takeIf { it != Color.Unspecified }
            ?: inheritedColor.takeIf { it != Color.Unspecified }
            ?: theme.colors.text2,
    )
    val markBackground = highlightBackground
        ?: if (theme.isDark) {
            theme.colors.warning.copy(alpha = 0.22f)
        } else {
            theme.colors.warning.copy(alpha = 0.26f)
        }
    val defaultMarkColor = highlightColor ?: if (theme.isDark) theme.colors.warningHover else theme.colors.text1
    val regex = remember(patterns, caseSensitive, wholeWord) {
        nHighlightRegex(patterns, caseSensitive, wholeWord)
    }
    val annotated = remember(regex, patterns, text, markBackground, defaultMarkColor) {
        if (regex == null || text.isEmpty()) {
            AnnotatedString(text)
        } else {
            buildAnnotatedString {
                var last = 0
                for (match in regex.findAll(text)) {
                    val start = match.range.first
                    val end = match.range.last + 1
                    if (start > last) append(text.substring(last, start))
                    val pattern = patterns.firstOrNull {
                        it.text.isNotEmpty() && it.text.equals(match.value, ignoreCase = !caseSensitive)
                    }
                    withStyle(
                        SpanStyle(
                            color = pattern?.color ?: defaultMarkColor,
                            background = markBackground,
                            fontWeight = if (pattern?.bold == true) FontWeight.SemiBold else null,
                        ),
                    ) {
                        append(match.value)
                    }
                    last = end
                }
                if (last < text.length) append(text.substring(last))
            }
        }
    }
    BasicText(text = annotated, modifier = modifier, style = baseStyle)
}
