package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.internal.LocalNContentColor
import cn.enaium.naiveui.internal.LocalNContentTextStyle
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NStatus

enum class NTextType { Default, Primary, Info, Success, Warning, Error }

private fun NTextType.status(): NStatus = when (this) {
    NTextType.Default -> NStatus.Default
    NTextType.Primary -> NStatus.Primary
    NTextType.Info -> NStatus.Info
    NTextType.Success -> NStatus.Success
    NTextType.Warning -> NStatus.Warning
    NTextType.Error -> NStatus.Error
}

/**
 * Basic text with naive-ui's type, depth, strong, italic, underline, delete
 * and code options.
 */
@Composable
fun NText(
    text: String,
    modifier: Modifier = Modifier,
    type: NTextType = NTextType.Default,
    depth: Int? = null,
    strong: Boolean = false,
    italic: Boolean = false,
    underline: Boolean = false,
    delete: Boolean = false,
    code: Boolean = false,
    size: TextUnit? = null,
    color: Color? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val theme = LocalNTheme.current
    val inherited = LocalNContentTextStyle.current
    val resolvedColor = color ?: when {
        type != NTextType.Default -> theme.colors.statusColor(type.status())
        LocalNContentColor.current != Color.Unspecified -> LocalNContentColor.current
        depth == 1 -> theme.colors.text1
        depth == 3 -> theme.colors.text3
        else -> theme.colors.text2
    }
    var style = (inherited ?: theme.typography.body).copy(color = resolvedColor)
    if (size != null) style = style.copy(fontSize = size, lineHeight = size * theme.typography.lineHeight)
    if (strong) style = style.copy(fontWeight = theme.typography.fontWeightStrong)
    if (italic) style = style.copy(fontStyle = FontStyle.Italic)
    val decoration = when {
        underline && delete -> TextDecoration.Underline + TextDecoration.LineThrough
        underline -> TextDecoration.Underline
        delete -> TextDecoration.LineThrough
        else -> null
    }
    if (decoration != null) style = style.copy(textDecoration = decoration)
    if (code) {
        style = style.copy(
            fontFamily = theme.typography.monoFamily,
            fontSize = if (size != null) size else style.fontSize * 0.92f,
            background = theme.colors.code,
        )
        Box(
            modifier
                .background(theme.colors.code, RoundedCornerShape(2.dp))
                .padding(horizontal = 4.dp, vertical = 1.dp),
        ) {
            BasicText(text = text, style = style, maxLines = maxLines, overflow = overflow)
        }
    } else {
        BasicText(text = text, modifier = modifier, style = style, maxLines = maxLines, overflow = overflow)
    }
}

@Composable
fun NText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    BasicText(text = text, modifier = modifier, style = style, maxLines = maxLines, overflow = overflow)
}

@Composable
fun NH1(text: String, modifier: Modifier = Modifier) =
    NText(text, modifier, color = LocalNTheme.current.colors.text1, size = LocalNTheme.current.typography.h1.fontSize, strong = true)

@Composable
fun NH2(text: String, modifier: Modifier = Modifier) =
    NText(text, modifier, color = LocalNTheme.current.colors.text1, size = LocalNTheme.current.typography.h2.fontSize, strong = true)

@Composable
fun NH3(text: String, modifier: Modifier = Modifier) =
    NText(text, modifier, color = LocalNTheme.current.colors.text1, size = LocalNTheme.current.typography.h3.fontSize, strong = true)

@Composable
fun NH4(text: String, modifier: Modifier = Modifier) =
    NText(text, modifier, color = LocalNTheme.current.colors.text1, size = LocalNTheme.current.typography.h4.fontSize, strong = true)

@Composable
fun NH5(text: String, modifier: Modifier = Modifier) =
    NText(text, modifier, color = LocalNTheme.current.colors.text1, size = LocalNTheme.current.typography.h5.fontSize, strong = true)

@Composable
fun NH6(text: String, modifier: Modifier = Modifier) =
    NText(text, modifier, color = LocalNTheme.current.colors.text1, size = LocalNTheme.current.typography.h6.fontSize, strong = true)

@Composable
fun NP(text: String, modifier: Modifier = Modifier, depth: Int = 2) =
    NText(text, modifier, depth = depth)

@Composable
fun NBlockquote(text: String, modifier: Modifier = Modifier) {
    val theme = LocalNTheme.current
    Row(modifier.height(IntrinsicSize.Min)) {
        Box(Modifier.width(3.dp).fillMaxHeight().background(theme.colors.divider))
        NText(
            text,
            Modifier.padding(start = 12.dp, top = 4.dp, bottom = 4.dp),
            depth = 2,
        )
    }
}

@Composable
fun NA(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalNTheme.current
    BasicText(
        text = text,
        modifier = modifier.clickable(onClick = onClick),
        style = theme.typography.body.copy(
            color = theme.colors.primary,
            textDecoration = TextDecoration.Underline,
        ),
    )
}

/** Simple ordered/unordered list composables. */
@Composable
fun NUl(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    androidx.compose.foundation.layout.Column(modifier.padding(start = 8.dp)) { content() }
}

@Composable
fun NOl(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    androidx.compose.foundation.layout.Column(modifier.padding(start = 8.dp)) { content() }
}

@Composable
fun NLi(
    text: String,
    modifier: Modifier = Modifier,
    index: Int? = null,
) {
    Row(modifier.padding(vertical = 2.dp)) {
        NText(if (index != null) "$index." else "•", Modifier.padding(end = 8.dp), depth = 2)
        NText(text, depth = 2)
    }
}
