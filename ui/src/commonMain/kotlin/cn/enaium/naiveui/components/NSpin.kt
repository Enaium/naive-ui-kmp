package cn.enaium.naiveui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.internal.NBaseLoading
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.nSize
import kotlinx.coroutines.delay

/**
 * Loading indicator that either stands alone or wraps content.
 *
 * When [content] is supplied it is dimmed while [show] is active and the
 * spinner is overlaid in the center. Like naive-ui, [show] has no effect on a
 * bare spin.
 */
@Composable
fun NSpin(
    modifier: Modifier = Modifier,
    show: Boolean = true,
    description: String? = null,
    size: NSize? = null,
    delayMillis: Long = 0L,
    icon: (@Composable () -> Unit)? = null,
    rotate: Boolean = true,
    strokeWidth: Dp? = null,
    content: (@Composable () -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val resolvedSize = nSize(size)
    val spinnerSize = theme.heightFor(resolvedSize)
    val stroke = strokeWidth ?: when (resolvedSize) {
        NSize.Tiny -> 2.dp
        NSize.Small -> 2.dp
        NSize.Medium -> 2.5.dp
        NSize.Large -> 3.dp
    }
    var active by remember { mutableStateOf(show && delayMillis <= 0L) }
    LaunchedEffect(show, delayMillis) {
        if (show && delayMillis > 0L) {
            delay(delayMillis)
            active = true
        } else {
            active = show
        }
    }

    val spinner: @Composable () -> Unit = {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (icon != null) {
                RotatingSpinIcon(active = active, rotate = rotate, icon = icon)
            } else {
                NBaseLoading(
                    size = spinnerSize,
                    color = theme.colors.primary,
                    strokeWidth = stroke,
                )
            }
            if (description != null) {
                NBaseText(
                    text = description,
                    style = theme.typography.body.copy(color = theme.colors.primary),
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }

    if (content == null) {
        Box(modifier, contentAlignment = Alignment.Center) { spinner() }
    } else {
        Box(modifier) {
            Box(Modifier.alpha(if (active) theme.opacityDisabled else 1f)) {
                content()
            }
            if (active) {
                Box(Modifier.matchParentSize(), contentAlignment = Alignment.Center) {
                    spinner()
                }
            }
        }
    }
}

@Composable
private fun RotatingSpinIcon(
    active: Boolean,
    rotate: Boolean,
    icon: @Composable () -> Unit,
) {
    if (active && rotate) {
        val transition = rememberInfiniteTransition()
        val angle by transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        )
        Box(Modifier.rotate(angle)) { icon() }
    } else {
        Box { icon() }
    }
}
