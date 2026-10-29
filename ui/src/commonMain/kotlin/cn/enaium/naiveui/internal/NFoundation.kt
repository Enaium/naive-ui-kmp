package cn.enaium.naiveui.internal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NTheme
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Content color inherited by text and icons inside a component. */
val LocalNContentColor = staticCompositionLocalOf { Color.Unspecified }

/** Base text style inherited by text inside a component. */
val LocalNContentTextStyle = staticCompositionLocalOf<TextStyle?> { null }

@Immutable
data class NInteractionState(
    val hovered: Boolean,
    val pressed: Boolean,
    val focused: Boolean,
)

@Composable
fun rememberNInteraction(source: MutableInteractionSource): NInteractionState {
    val hovered by source.collectIsHoveredAsState()
    val pressed = rememberNPressInteraction(source)
    val focused by source.collectIsFocusedAsState()
    return NInteractionState(hovered, pressed, focused)
}

/**
 * Press state with a minimum visible duration.
 *
 * A fast tap can emit Press and Release before the next frame, so
 * `collectIsPressedAsState` never renders the pressed styling. Keeping the
 * visual state for a short tail makes every tap visibly react.
 */
@Composable
fun rememberNPressInteraction(
    source: MutableInteractionSource,
    minimumMillis: Long = 200L,
): Boolean {
    var pressed by mutableStateOf(false)
    LaunchedEffect(source, minimumMillis) {
        var hideJob: Job? = null
        source.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    hideJob?.cancel()
                    hideJob = null
                    pressed = true
                }
                is PressInteraction.Release, is PressInteraction.Cancel -> {
                    hideJob?.cancel()
                    hideJob = launch {
                        delay(minimumMillis)
                        pressed = false
                    }
                }
            }
        }
    }
    return pressed
}

/** Rounded border that also supports dashed styling. */
fun Modifier.nBorder(
    color: Color,
    width: Dp = 1.dp,
    radius: Dp = 3.dp,
    dashed: Boolean = false,
): Modifier = drawBehind {
    val stroke = width.toPx()
    val half = stroke / 2f
    drawRoundRect(
        color = color,
        topLeft = Offset(half, half),
        size = Size(size.width - stroke, size.height - stroke),
        cornerRadius = CornerRadius(radius.toPx(), radius.toPx()),
        style = Stroke(
            width = stroke,
            pathEffect = if (dashed) {
                androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                    floatArrayOf(6.dp.toPx(), 4.dp.toPx()),
                )
            } else {
                null
            },
        ),
    )
}

fun Modifier.nBottomBorder(color: Color, width: Dp = 1.dp): Modifier = drawBehind {
    drawRect(
        color = color,
        topLeft = Offset(0f, size.height - width.toPx()),
        size = Size(size.width, width.toPx()),
    )
}

fun Modifier.nTopBorder(color: Color, width: Dp = 1.dp): Modifier = drawBehind {
    drawRect(color = color, size = Size(size.width, width.toPx()))
}

fun Modifier.nLeftBorder(color: Color, width: Dp = 1.dp): Modifier = drawBehind {
    drawRect(color = color, size = Size(width.toPx(), size.height))
}

fun Modifier.nRightBorder(color: Color, width: Dp = 1.dp): Modifier = drawBehind {
    drawRect(
        color = color,
        topLeft = Offset(size.width - width.toPx(), 0f),
        size = Size(width.toPx(), size.height),
    )
}

fun Modifier.nSoftShadow(color: Color = Color.Black, blur: Dp = 12.dp, alpha: Float = 0.12f): Modifier =
    drawBehind {
        val steps = 24
        val blurPx = blur.toPx()
        val layerAlpha = alpha / steps
        for (i in 1..steps) {
            val spread = blurPx * i / steps
            drawRoundRect(
                color = color.copy(alpha = layerAlpha),
                topLeft = Offset(-spread, -spread),
                size = Size(size.width + spread * 2, size.height + spread * 2),
                cornerRadius = CornerRadius(spread * 0.8f, spread * 0.8f),
            )
        }
    }

/** Applies one of naive-ui's three box-shadow tokens using Compose elevation. */
fun Modifier.nShadow(
    theme: NTheme,
    level: Int = 1,
    shape: Shape = RoundedCornerShape(theme.borderRadius),
): Modifier {
    val elevation = when (level.coerceIn(1, 3)) {
        1 -> 6.dp
        2 -> 10.dp
        else -> 18.dp
    }
    val alpha = if (theme.isDark) 0.5f else 0.22f
    return dropShadow(shape) {
        radius = elevation.toPx()
        spread = 0f
        color = Color.Black
        this.alpha = alpha
        offset = androidx.compose.ui.geometry.Offset(0f, 2.dp.toPx())
    }
}

@Composable
fun NBaseText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = if (color == Color.Unspecified) style else style.copy(color = color),
        maxLines = maxLines,
        overflow = overflow,
    )
}

/** The spinner used by loading states across naive-ui components. */
@Composable
fun NBaseLoading(
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    color: Color = LocalNTheme.current.colors.primary,
    strokeWidth: Dp = 2.dp,
) {
    val transition = androidx.compose.animation.core.rememberInfiniteTransition()
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(900, easing = androidx.compose.animation.core.LinearEasing),
        ),
    )
    Canvas(modifier.size(size).rotate(angle)) {
        val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        val inset = strokeWidth.toPx() / 2
        drawArc(
            color = color,
            startAngle = 15f,
            sweepAngle = 300f,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = Size(this.size.width - inset * 2, this.size.height - inset * 2),
            style = stroke,
        )
    }
}

/** Fade + expand transition used by naive-ui for inline expansion. */
@Composable
fun NFadeInExpandTransition(
    show: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = show,
        modifier = modifier,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        content()
    }
}

/** Height (or width) collapse transition, equivalent to `<n-collapse-transition>`. */
@Composable
fun NCollapseTransition(
    show: Boolean,
    modifier: Modifier = Modifier,
    horizontal: Boolean = false,
    content: @Composable () -> Unit,
) {
    val enter = if (horizontal) {
        androidx.compose.animation.expandHorizontally() + fadeIn()
    } else {
        expandVertically() + fadeIn()
    }
    val exit = if (horizontal) {
        androidx.compose.animation.shrinkHorizontally() + fadeOut()
    } else {
        shrinkVertically() + fadeOut()
    }
    AnimatedVisibility(visible = show, modifier = modifier, enter = enter, exit = exit) {
        content()
    }
}

/** Small circular close affordance used by tags, drawers, modals, etc. */
@Composable
fun NCloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    iconSize: Dp = 12.dp,
    absolute: Boolean = false,
) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    @Suppress("UNUSED_VARIABLE")
    val absoluteFlag = absolute
    Box(
        modifier
            .size(size)
            .clip(CircleShape)
            .background(if (hovered) theme.colors.closeHover else Color.Transparent)
            .hoverable(interaction)
            .nClickable(interaction, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        NIcon(NIcons.Close, size = iconSize, color = theme.colors.closeIcon)
    }
}

/** Circular clear affordance used by inputs and selects. */
@Composable
fun NClearButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    iconSize: Dp = 12.dp,
) {
    val theme = LocalNTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Box(
        modifier
            .size(size)
            .clip(CircleShape)
            .background(if (hovered) theme.colors.closeHover else Color.Transparent)
            .hoverable(interaction)
            .nClickable(interaction, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        NIcon(
            NIcons.Clear,
            size = iconSize,
            color = if (hovered) theme.colors.clearHover else theme.colors.clear,
        )
    }
}

/** Borderless clickable with hover/press interaction tracking and no ripple. */
fun Modifier.nClickable(
    interactionSource: MutableInteractionSource,
    enabled: Boolean = true,
    onClick: () -> Unit,
): Modifier = this.then(
    this.clickable(
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled,
        onClick = onClick,
    ),
)

/**
 * Observes taps on a trigger without being blocked by a child that consumes
 * the click (for example a button used as a popover trigger).
 */
fun Modifier.nTapTrigger(enabled: Boolean = true, onTap: () -> Unit): Modifier =
    if (!enabled) {
        this
    } else {
        this.pointerInput(onTap) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                var up: androidx.compose.ui.input.pointer.PointerInputChange? = null
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    if (!change.pressed) {
                        up = change
                        break
                    }
                }
                if (up != null) {
                    val distance = (up.position - down.position).getDistance()
                    if (distance <= viewConfiguration.touchSlop) onTap()
                }
            }
        }
    }

/** Adds a subtle press-down scale used by several naive-ui controls. */
fun Modifier.nPressScale(pressed: Boolean): Modifier =
    graphicsLayer {
        val scale = if (pressed) 0.97f else 1f
        scaleX = scale
        scaleY = scale
    }

/** A hover-aware background helper. */
fun Modifier.nHoverBackground(
    interactionSource: MutableInteractionSource,
    normal: Color,
    hovered: Color,
    shape: Shape = RoundedCornerShape(3.dp),
): Modifier = this
    .background(normal, shape)
    .hoverable(interactionSource)
    .drawBehind {
        // The hover tint is painted by the caller through [nHoverBackgroundState].
    }

/** Waits [millis] then hides, used by tooltip-like components. */
@Composable
fun rememberDelayedVisible(visible: Boolean, millis: Long): Boolean {
    var result by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(visible) {
        if (visible) {
            delay(millis)
            result = true
        } else {
            result = false
        }
    }
    return result
}
