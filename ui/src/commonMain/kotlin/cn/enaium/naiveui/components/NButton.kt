package cn.enaium.naiveui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import cn.enaium.naiveui.internal.LocalNContentColor
import cn.enaium.naiveui.internal.LocalNContentTextStyle
import cn.enaium.naiveui.internal.NBaseLoading
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.internal.rememberNPressInteraction
import cn.enaium.naiveui.internal.nHoverColor
import cn.enaium.naiveui.internal.nPressedColor
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NStatus
import cn.enaium.naiveui.theme.nSize

/** Naive UI button types. */
enum class NButtonType { Default, Tertiary, Primary, Info, Success, Warning, Error }

val NButtonType.status: NStatus
    get() = when (this) {
        NButtonType.Default, NButtonType.Tertiary -> NStatus.Default
        NButtonType.Primary -> NStatus.Primary
        NButtonType.Info -> NStatus.Info
        NButtonType.Success -> NStatus.Success
        NButtonType.Warning -> NStatus.Warning
        NButtonType.Error -> NStatus.Error
    }

enum class NIconPlacement { Left, Right }

internal class NButtonGroupState {
    var count by mutableStateOf(0)
    var nextSlot = 0
}

internal class NButtonGroupContext(
    val size: NSize?,
    val vertical: Boolean,
    val roundStart: Boolean,
    val roundEnd: Boolean,
    val state: NButtonGroupState,
)

internal val LocalNButtonGroup = staticCompositionLocalOf<NButtonGroupContext?> { null }

/**
 * Button used to trigger actions.
 *
 * Visual variants mirror naive-ui: `type`, `secondary`, `tertiary`,
 * `quaternary`, `text`, `ghost`, `dashed`, `round`, `circle`, `block`.
 */
@Composable
fun NButton(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    type: NButtonType = NButtonType.Default,
    size: NSize? = null,
    strong: Boolean = false,
    secondary: Boolean = false,
    tertiary: Boolean = false,
    quaternary: Boolean = false,
    text: Boolean = false,
    ghost: Boolean = false,
    dashed: Boolean = false,
    bordered: Boolean = true,
    disabled: Boolean = false,
    loading: Boolean = false,
    round: Boolean = false,
    circle: Boolean = false,
    block: Boolean = false,
    color: Color? = null,
    textColor: Color? = null,
    iconPlacement: NIconPlacement = NIconPlacement.Left,
    icon: (@Composable () -> Unit)? = null,
    content: @Composable RowScope.() -> Unit = {},
) {
    val theme = LocalNTheme.current
    val group = LocalNButtonGroup.current
    val groupSlot = remember(group) { group?.state?.let { it.nextSlot++ } ?: 0 }
    val groupCount = group?.state?.count ?: 0
    val resolvedSize = group?.size ?: nSize(size)
    val height = theme.heightFor(resolvedSize)
    val fontSize = theme.fontSizeFor(resolvedSize)
    val status = type.status
    val isDefault = status == NStatus.Default
    val base = color ?: theme.colors.statusColor(status)
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val focused by interaction.collectIsFocusedAsState()
    val pressed = rememberNPressInteraction(interaction)
    val enabled = !disabled && !loading
    var clickToken by remember { mutableIntStateOf(0) }
    var clickFlash by remember { mutableStateOf(false) }
    LaunchedEffect(clickToken) {
        if (clickToken > 0) {
            clickFlash = true
            delay(180)
            clickFlash = false
        }
    }

    val solid = !secondary && !tertiary && !quaternary && !text && !ghost && !dashed
    var background = Color.Transparent
    var foreground = if (isDefault) theme.colors.text2 else base
    var borderColor = Color.Transparent
    var hoveredBackground = Color.Transparent
    var pressedBackground = Color.Transparent
    var hoveredBorder = borderColor
    var hoveredForeground = foreground

    when {
        solid -> {
            if (isDefault) {
                background = Color.Transparent
                foreground = theme.colors.text2
                borderColor = if (bordered) theme.colors.border else Color.Transparent
                hoveredForeground = theme.colors.primaryHover
                hoveredBorder = theme.colors.primaryHover
                hoveredBackground = Color.Transparent
                pressedBackground = Color.Transparent
            } else {
                background = base
                foreground = theme.colors.base
                borderColor = if (bordered) base else Color.Transparent
                hoveredBackground = theme.colors.status(status).hover
                pressedBackground = theme.colors.status(status).pressed
                hoveredBorder = hoveredBackground
                hoveredForeground = theme.colors.base
            }
        }
        secondary -> {
            background = if (isDefault) theme.colors.buttonColor2 else base.copy(alpha = 0.16f)
            foreground = if (isDefault) theme.colors.text2 else base
            borderColor = Color.Transparent
            hoveredBackground = if (isDefault) theme.colors.buttonColor2Hover else base.copy(alpha = 0.22f)
            pressedBackground = if (isDefault) theme.colors.buttonColor2Pressed else base.copy(alpha = 0.28f)
            hoveredForeground = if (isDefault) theme.colors.primaryHover else base
        }
        tertiary -> {
            background = theme.colors.buttonColor2
            foreground = if (isDefault) theme.colors.text2 else theme.colors.text3.let { if (status == NStatus.Default) it else base }
            if (!isDefault) foreground = base
            borderColor = Color.Transparent
            hoveredBackground = theme.colors.buttonColor2Hover
            pressedBackground = theme.colors.buttonColor2Pressed
            hoveredForeground = if (isDefault) theme.colors.primaryHover else base
        }
        quaternary -> {
            background = Color.Transparent
            foreground = if (isDefault) theme.colors.text2 else base
            hoveredBackground = theme.colors.buttonColor2Hover
            pressedBackground = theme.colors.buttonColor2Pressed
            hoveredForeground = if (isDefault) theme.colors.primaryHover else base
        }
        ghost || dashed -> {
            background = Color.Transparent
            foreground = if (isDefault) theme.colors.text2 else base
            borderColor = if (bordered) (if (isDefault) theme.colors.border else base) else Color.Transparent
            hoveredForeground = if (isDefault) theme.colors.primaryHover else base
            hoveredBorder = hoveredForeground
        }
        text -> {
            background = Color.Transparent
            foreground = if (isDefault) theme.colors.text2 else base
            borderColor = Color.Transparent
            hoveredForeground = if (isDefault) theme.colors.primaryHover else base
            hoveredBackground = theme.colors.buttonColor2Hover
            pressedBackground = theme.colors.buttonColor2Pressed
        }
    }
    if (textColor != null) {
        foreground = textColor
        hoveredForeground = nHoverColor(textColor, theme.isDark)
    }
    if (disabled) {
        foreground = foreground.copy(alpha = theme.opacityDisabled)
        borderColor = borderColor.copy(alpha = theme.opacityDisabled)
        background = background.copy(alpha = background.alpha * theme.opacityDisabled)
    }

    val cornerRadius = if (round || circle) height / 2 else theme.borderRadius
    val first = group != null && groupSlot == 0
    val last = group != null && groupCount > 0 && groupSlot == groupCount - 1
    val startRadius = if (
        group != null && first && (group.roundStart || round)
    ) height / 2 else if (group != null) theme.borderRadius else cornerRadius
    val endRadius = if (
        group != null && last && (group.roundEnd || round)
    ) height / 2 else if (group != null) theme.borderRadius else cornerRadius
    val topStartRadius: Dp
    val topEndRadius: Dp
    val bottomEndRadius: Dp
    val bottomStartRadius: Dp
    if (group == null) {
        topStartRadius = cornerRadius
        topEndRadius = cornerRadius
        bottomEndRadius = cornerRadius
        bottomStartRadius = cornerRadius
    } else if (group.vertical) {
        topStartRadius = if (first) startRadius else 0.dp
        topEndRadius = if (first) startRadius else 0.dp
        bottomEndRadius = if (last) endRadius else 0.dp
        bottomStartRadius = if (last) endRadius else 0.dp
    } else {
        topStartRadius = if (first) startRadius else 0.dp
        topEndRadius = if (last) endRadius else 0.dp
        bottomEndRadius = if (last) endRadius else 0.dp
        bottomStartRadius = if (first) startRadius else 0.dp
    }
    val shape: Shape = if (circle) {
        CircleShape
    } else {
        RoundedCornerShape(
            topStart = topStartRadius,
            topEnd = topEndRadius,
            bottomEnd = bottomEndRadius,
            bottomStart = bottomStartRadius,
        )
    }
    val horizontalPadding = when {
        circle -> 0.dp
        round -> when (resolvedSize) {
            NSize.Tiny -> 10.dp
            NSize.Small -> 14.dp
            NSize.Medium -> 18.dp
            NSize.Large -> 22.dp
        }
        else -> when (resolvedSize) {
            NSize.Tiny -> 6.dp
            NSize.Small -> 10.dp
            NSize.Medium -> 14.dp
            NSize.Large -> 18.dp
        }
    }
    val iconSize = when (resolvedSize) {
        NSize.Tiny -> 14.dp
        NSize.Small -> 18.dp
        NSize.Medium -> 18.dp
        NSize.Large -> 20.dp
    }
    val focusKeepsBase = secondary || tertiary || quaternary || text || (solid && isDefault)
    val pressedVisual = pressed || clickFlash
    val currentBackgroundTarget = when {
        pressedVisual -> pressedBackground
        hovered -> hoveredBackground
        focused -> if (focusKeepsBase) background else hoveredBackground
        else -> background
    }
    val currentForegroundTarget = if (hovered || pressedVisual || focused) hoveredForeground else foreground
    val currentBorderTarget = when {
        pressedVisual -> if (isDefault) theme.colors.primaryPressed else hoveredBorder
        hovered || focused -> hoveredBorder
        else -> borderColor
    }
    val pressOverlay = if (
        pressedVisual && (secondary || tertiary || quaternary)
    ) {
        Color.Black.copy(alpha = if (theme.isDark) 0.03f else 0.02f)
    } else {
        Color.Transparent
    }
    val colorAnimation = tween<androidx.compose.ui.graphics.Color>(
        durationMillis = if (pressedVisual) 0 else 300,
    )
    val borderAnimation = tween<androidx.compose.ui.graphics.Color>(
        durationMillis = 600,
        easing = EaseOut,
    )
    val currentBackground by animateColorAsState(
        currentBackgroundTarget,
        animationSpec = colorAnimation,
        label = "n-button-background",
    )
    val currentForeground by animateColorAsState(
        currentForegroundTarget,
        animationSpec = colorAnimation,
        label = "n-button-foreground",
    )
    val currentBorder by animateColorAsState(
        currentBorderTarget,
        animationSpec = borderAnimation,
        label = "n-button-border",
    )
    val borderVisible = bordered && currentBorder.alpha > 0f &&
        !secondary && !tertiary && !quaternary && !text
    val showBorder = borderVisible
    val rippleColor = when {
        text || secondary || tertiary || quaternary -> Color.Transparent
        color != null -> color
        isDefault -> theme.colors.primary
        else -> base
    }
    val waveEnabled = !text && !secondary && !tertiary && !quaternary
    var waveToken by remember { mutableIntStateOf(0) }
    val wave = remember { Animatable(0f) }
    LaunchedEffect(waveToken) {
        if (waveEnabled && waveToken > 0 && rippleColor.alpha > 0f) {
            wave.snapTo(0f)
            wave.animateTo(1f, tween(durationMillis = 600, easing = EaseOut))
        }
    }
    val waveModifier = Modifier.drawBehind {
        val progress = wave.value
        if (progress > 0f && progress < 1f) {
            val spread = (0.5f + 4f * progress).dp.toPx()
            val canvasSize = this.size
            val path = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(
                            -spread / 2f,
                            -spread / 2f,
                            canvasSize.width + spread / 2f,
                            canvasSize.height + spread / 2f,
                        ),
                        topLeft = CornerRadius(
                            topStartRadius.toPx() + spread / 2f,
                            topStartRadius.toPx() + spread / 2f,
                        ),
                        topRight = CornerRadius(
                            topEndRadius.toPx() + spread / 2f,
                            topEndRadius.toPx() + spread / 2f,
                        ),
                        bottomRight = CornerRadius(
                            bottomEndRadius.toPx() + spread / 2f,
                            bottomEndRadius.toPx() + spread / 2f,
                        ),
                        bottomLeft = CornerRadius(
                            bottomStartRadius.toPx() + spread / 2f,
                            bottomStartRadius.toPx() + spread / 2f,
                        ),
                    ),
                )
            }
            drawPath(
                path = path,
                color = rippleColor.copy(
                    alpha = (if (theme.isDark) 0.8f else 0.6f) * (1f - progress),
                ),
                style = Stroke(width = spread),
            )
        }
    }

    val textStyle = TextStyle(
        fontFamily = theme.typography.fontFamily,
        fontSize = fontSize,
        lineHeight = fontSize * 1.15f,
        fontWeight = if (strong) theme.typography.fontWeightStrong else theme.typography.fontWeight,
        color = currentForeground,
    )
    val fillsWidth = (block && !circle) || circle || group?.vertical == true
    Box(
        modifier = modifier
            .then(if (group?.vertical == true) Modifier.fillMaxWidth() else Modifier)
            .then(if (block && !circle) Modifier.fillMaxWidth() else Modifier)
            .then(if (circle) Modifier.size(height) else Modifier.height(height))
            .zIndex(
                when {
                    pressed || focused || (wave.value > 0f && wave.value < 1f) -> 1000f
                    group != null -> (groupCount - groupSlot).toFloat()
                    else -> 0f
                },
            )
            .then(waveModifier),
    ) {
        CompositionLocalProvider(
            LocalNContentColor provides currentForeground,
            LocalNContentTextStyle provides textStyle,
        ) {
            Row(
                modifier = Modifier
                    .then(if (fillsWidth) Modifier.fillMaxWidth() else Modifier)
                    .fillMaxHeight()
                    .clip(shape)
                    .background(currentBackground)
                    .then(
                        if (pressOverlay.alpha > 0f) {
                            Modifier.drawBehind { drawRect(pressOverlay) }
                        } else {
                            Modifier
                        },
                    )
                    .then(
                        if (showBorder) {
                            if (group != null) {
                                Modifier.border(1.dp, currentBorder, shape)
                            } else {
                                Modifier.nBorder(
                                    currentBorder,
                                    1.dp,
                                    if (circle || round) height / 2 else theme.borderRadius,
                                    dashed,
                                )
                            }
                        } else {
                            Modifier
                        },
                    )
                    .hoverable(interaction, enabled = enabled)
                    .nClickable(
                        interaction,
                        enabled = enabled,
                        onClick = {
                            if (waveEnabled) waveToken += 1
                            if (secondary || tertiary || quaternary) clickToken += 1
                            onClick()
                        },
                    )
                    .padding(horizontal = horizontalPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
            val iconModifier = Modifier.size(iconSize)
            if (loading) {
                NBaseLoading(size = iconSize, color = currentForeground, strokeWidth = if (fontSize.value >= 14) 2.dp else 1.5.dp)
                if (!circle) Box(Modifier.width(6.dp))
            } else if (icon != null && iconPlacement == NIconPlacement.Left) {
                Box(iconModifier) { icon() }
                if (!circle) Box(Modifier.width(6.dp))
            }
            content()
            if (!loading && icon != null && iconPlacement == NIconPlacement.Right) {
                if (!circle) Box(Modifier.width(6.dp))
                Box(iconModifier) { icon() }
            }
            }
        }
    }
}

/**
 * Groups buttons. A group size wins over the size of individual buttons, and
 * connected edges are clipped by the group shape.
 */
@Composable
fun NButtonGroup(
    modifier: Modifier = Modifier,
    size: NSize? = null,
    vertical: Boolean = false,
    roundStart: Boolean = false,
    roundEnd: Boolean = false,
    content: @Composable () -> Unit,
) {
    val state = remember { NButtonGroupState() }
    val context = remember(size, vertical, roundStart, roundEnd, state) {
        NButtonGroupContext(size, vertical, roundStart, roundEnd, state)
    }
    CompositionLocalProvider(LocalNButtonGroup provides context) {
        SideEffect { state.count = state.nextSlot }
        if (vertical) {
            Column(
                modifier.width(IntrinsicSize.Max),
                verticalArrangement = Arrangement.spacedBy((-1).dp),
            ) { content() }
        } else {
            Row(
                modifier,
                horizontalArrangement = Arrangement.spacedBy((-1).dp),
            ) { content() }
        }
    }
}
