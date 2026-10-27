package cn.enaium.naiveui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.theme.LocalNTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Imperative handle returned by [rememberNLoadingBar]. */
@Stable
class NLoadingBarController internal constructor(
    private val scope: CoroutineScope,
) {
    internal val progress = Animatable(0f)
    internal var visible by mutableStateOf(false)
    internal var isError by mutableStateOf(false)
    private var job: Job? = null

    /** Starts the loading bar and eases it towards completion. */
    fun start() {
        job?.cancel()
        isError = false
        visible = true
        job = scope.launch {
            progress.snapTo(0f)
            progress.animateTo(0.8f, tween(durationMillis = 1600, easing = FastOutSlowInEasing))
        }
    }

    /** Completes the bar and hides it. */
    fun finish() {
        if (!visible && progress.value == 0f) return
        job?.cancel()
        job = scope.launch {
            progress.animateTo(1f, tween(220))
            delay(180)
            visible = false
            progress.snapTo(0f)
            isError = false
        }
    }

    /** Turns the bar red, completes it and hides it. */
    fun error() {
        job?.cancel()
        isError = true
        visible = true
        job = scope.launch {
            progress.animateTo(1f, tween(220))
            delay(450)
            visible = false
            progress.snapTo(0f)
            isError = false
        }
    }
}

/** The loading bar provided by the closest [NLoadingBarProvider]. */
val LocalNLoadingBar = staticCompositionLocalOf<NLoadingBarController?> { null }

/**
 * Provides an imperative loading bar to descendants, mirroring naive-ui's
 * `<n-loading-bar-provider>` plus `useLoadingBar`.
 *
 * The thin bar overlays the top edge of this provider's box.
 */
@Composable
fun NLoadingBarProvider(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val theme = LocalNTheme.current
    val scope = rememberCoroutineScope()
    val controller = remember { NLoadingBarController(scope) }
    CompositionLocalProvider(LocalNLoadingBar provides controller) {
        Box(modifier) {
            content()
            val fraction = controller.progress.value.coerceIn(0f, 1f)
            if (controller.visible && fraction > 0f) {
                Box(
                    Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth(fraction)
                        .height(2.dp)
                        .background(if (controller.isError) theme.colors.error else theme.colors.primary),
                )
            }
        }
    }
}

/**
 * Returns the loading bar controller. Must be called inside
 * [NLoadingBarProvider].
 */
@Composable
fun rememberNLoadingBar(): NLoadingBarController =
    LocalNLoadingBar.current
        ?: error("rememberNLoadingBar must be used inside NLoadingBarProvider")
