package cn.enaium.naiveui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cn.enaium.naiveui.internal.LocalNContentColor

/**
 * Generic styled container, the Compose counterpart of `<n-el>`.
 *
 * It hosts arbitrary content and can override the inherited content color,
 * which is how naive-ui exposes its theme variables to descendants. [tag] and
 * [abstract] are accepted for API parity; Compose has no host element.
 */
@Composable
fun NElement(
    modifier: Modifier = Modifier,
    tag: String = "div",
    abstract: Boolean = false,
    contentColor: Color? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    @Suppress("UNUSED_VARIABLE")
    val parity = listOf(tag, abstract)
    if (contentColor == null) {
        Box(modifier, content = content)
    } else {
        CompositionLocalProvider(LocalNContentColor provides contentColor) {
            Box(modifier, content = content)
        }
    }
}
