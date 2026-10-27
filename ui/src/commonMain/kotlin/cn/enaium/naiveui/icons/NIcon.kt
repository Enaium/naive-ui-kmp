package cn.enaium.naiveui.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.theme.LocalNTheme

/** Renders an [ImageVector] with a size and tint, equivalent to `<n-icon>`. */
@Composable
fun NIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    color: Color = LocalNTheme.current.colors.icon,
    contentDescription: String? = null,
) {
    val painter = rememberVectorPainter(icon)
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        colorFilter = if (color == Color.Unspecified) null else ColorFilter.tint(color),
    )
}

/** Wraps arbitrary icon content, matching `<n-icon>`'s slot API. */
@Composable
fun NIcon(
    modifier: Modifier = Modifier,
    size: Dp? = null,
    color: Color = LocalNTheme.current.colors.icon,
    content: @Composable BoxScope.() -> Unit,
) {
    val sizedModifier = if (size != null) modifier.size(size) else modifier
    Box(sizedModifier, contentAlignment = Alignment.Center) {
        androidx.compose.runtime.CompositionLocalProvider(
            LocalNIconColor provides color,
        ) {
            content()
        }
    }
}

/** Color used by slot-based icons. */
val LocalNIconColor = androidx.compose.runtime.staticCompositionLocalOf { Color.Unspecified }
