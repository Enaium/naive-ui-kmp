package cn.enaium.naiveui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cn.enaium.naiveui.internal.NCollapseTransition as InternalCollapseTransition

/**
 * Collapse transition that expands or shrinks its content on the vertical axis.
 * Set [horizontal] to animate the width instead, mirroring
 * `<n-collapse-transition>`.
 */
@Composable
fun NCollapseTransition(
    show: Boolean,
    modifier: Modifier = Modifier,
    horizontal: Boolean = false,
    content: @Composable () -> Unit,
) {
    InternalCollapseTransition(
        show = show,
        modifier = modifier,
        horizontal = horizontal,
        content = content,
    )
}
