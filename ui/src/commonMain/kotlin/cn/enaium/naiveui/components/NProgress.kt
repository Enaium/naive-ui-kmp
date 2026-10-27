package cn.enaium.naiveui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.theme.LocalNTheme
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

/** Progress visual shape. */
enum class NProgressType { Line, Circle, MultipleCircle, Dashboard }

/** Semantic color of a progress bar. */
enum class NProgressStatus { Default, Info, Success, Warning, Error }

/** Where the percentage indicator is rendered for line progress. */
enum class NProgressIndicatorPlacement { Inside, Outside }

/**
 * Progress bar with line, circle, dashboard and multiple-circle variants.
 *
 * Percentages are clamped to `0..100`. Pass [percentages] to drive the
 * multiple-circle variant; [percentage] is used for every other type.
 */
@Composable
fun NProgress(
    modifier: Modifier = Modifier,
    type: NProgressType = NProgressType.Line,
    percentage: Float = 0f,
    status: NProgressStatus = NProgressStatus.Default,
    showIndicator: Boolean = true,
    indicatorPlacement: NProgressIndicatorPlacement = NProgressIndicatorPlacement.Outside,
    height: Dp? = null,
    color: Color? = null,
    railColor: Color? = null,
    processing: Boolean = false,
    percentages: List<Float> = emptyList(),
    colors: List<Color> = emptyList(),
    railColors: List<Color> = emptyList(),
    gradient: Pair<Color, Color>? = null,
    gradients: List<Pair<Color, Color>> = emptyList(),
    gapDegree: Float = if (type == NProgressType.Dashboard) 75f else 0f,
    gapOffsetDegree: Float = 0f,
    offsetDegree: Float = 0f,
    strokeWidth: Dp = 7.dp,
    circleGap: Dp = 1.dp,
    unit: String = "%",
    indicatorTextColor: Color? = null,
    circleSize: Dp? = null,
    indicator: (@Composable () -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val fillColor = color ?: statusFillColor(status)
    val resolvedRail = railColor ?: theme.colors.progressRail
    val pct = percentage.coerceIn(0f, 100f)
    when (type) {
        NProgressType.Line -> LineProgress(
            modifier = modifier,
            percentage = pct,
            status = status,
            showIndicator = showIndicator,
            placement = indicatorPlacement,
            height = height,
            fillColor = fillColor,
            railColor = resolvedRail,
            processing = processing,
            gradient = gradient ?: gradients.firstOrNull(),
            unit = unit,
            indicatorTextColor = indicatorTextColor,
            indicator = indicator,
        )
        NProgressType.Circle, NProgressType.Dashboard -> {
            val brush = gradient ?: gradients.firstOrNull()
            Box(
                modifier.size(circleSize ?: 120.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircleProgressCanvas(
                    percentage = pct,
                    fillColor = fillColor,
                    railColor = resolvedRail,
                    gapDegree = gapDegree,
                    gapOffsetDegree = gapOffsetDegree,
                    offsetDegree = offsetDegree,
                    strokeWidth = strokeWidth,
                    gradient = brush,
                )
                if (showIndicator) {
                    if (indicator != null) {
                        indicator()
                    } else if (status != NProgressStatus.Default) {
                        NIcon(
                            statusIcon(status) ?: NIcons.Info,
                            size = 36.dp,
                            color = statusIconColor(status),
                        )
                    } else {
                        NBaseText(
                            text = "${formatPercentage(pct)}$unit",
                            style = TextStyle(
                                fontFamily = theme.typography.fontFamily,
                                fontSize = 28.sp,
                                color = theme.colors.text2,
                            ),
                        )
                    }
                }
            }
        }
        NProgressType.MultipleCircle -> {
            val values = if (percentages.isNotEmpty()) percentages else listOf(percentage)
            val dimension = circleSize ?: 200.dp
            Box(modifier.size(dimension), contentAlignment = Alignment.Center) {
                MultipleCircleCanvas(
                    values = values,
                    fillColors = colors,
                    railColors = railColors,
                    fallbackFill = fillColor,
                    fallbackRail = resolvedRail,
                    gradients = gradients,
                    strokeWidth = strokeWidth,
                    circleGap = circleGap,
                )
                if (showIndicator && indicator != null) indicator()
            }
        }
    }
}

@Composable
private fun LineProgress(
    modifier: Modifier,
    percentage: Float,
    status: NProgressStatus,
    showIndicator: Boolean,
    placement: NProgressIndicatorPlacement,
    height: Dp?,
    fillColor: Color,
    railColor: Color,
    processing: Boolean,
    gradient: Pair<Color, Color>?,
    unit: String,
    indicatorTextColor: Color?,
    indicator: (@Composable () -> Unit)?,
) {
    val theme = LocalNTheme.current
    val railHeight = height ?: 8.dp
    val radius = railHeight / 2
    val shape = RoundedCornerShape(radius)
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .weight(1f)
                .height(railHeight)
                .clip(shape)
                .background(railColor, shape),
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage / 100f)
                    .clip(shape)
                    .then(
                        if (gradient != null) {
                            Modifier.background(
                                Brush.horizontalGradient(listOf(gradient.first, gradient.second)),
                                shape,
                            )
                        } else {
                            Modifier.background(fillColor, shape)
                        },
                    ),
            ) {
                if (processing) ProcessingOverlay(Modifier.matchParentSize())
                if (showIndicator && placement == NProgressIndicatorPlacement.Inside) {
                    Box(
                        Modifier
                            .matchParentSize()
                            .padding(horizontal = 6.dp),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        NBaseText(
                            text = "${formatPercentage(percentage)}$unit",
                            style = TextStyle(
                                fontFamily = theme.typography.fontFamily,
                                fontSize = 12.sp,
                                color = indicatorTextColor ?: Color.White,
                            ),
                            maxLines = 1,
                        )
                    }
                }
            }
        }
        if (showIndicator && placement == NProgressIndicatorPlacement.Outside) {
            Spacer(Modifier.width(14.dp))
            if (indicator != null) {
                indicator()
            } else if (status == NProgressStatus.Default) {
                NBaseText(
                    text = "${formatPercentage(percentage)}$unit",
                    style = TextStyle(
                        fontFamily = theme.typography.fontFamily,
                        fontSize = 14.sp,
                        color = indicatorTextColor ?: theme.colors.text2,
                    ),
                    maxLines = 1,
                )
            } else {
                NIcon(
                    statusIcon(status) ?: NIcons.Info,
                    size = 18.dp,
                    color = indicatorTextColor ?: statusIconColor(status),
                )
            }
        }
    }
}

@Composable
private fun ProcessingOverlay(modifier: Modifier) {
    val transition = rememberInfiniteTransition()
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
        ),
    )
    Canvas(modifier) {
        val x = size.width * (phase * 2f - 1f)
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = 0.35f),
                    Color.Transparent,
                ),
                startX = x,
                endX = x + size.width,
            ),
            size = size,
        )
    }
}

@Composable
private fun CircleProgressCanvas(
    percentage: Float,
    fillColor: Color,
    railColor: Color,
    gapDegree: Float,
    gapOffsetDegree: Float,
    offsetDegree: Float,
    strokeWidth: Dp,
    gradient: Pair<Color, Color>?,
) {
    Canvas(Modifier.fillMaxSize()) {
        val stroke = strokeWidth.toPx()
        val gap = gapDegree.coerceIn(0f, 360f)
        val sweep = (360f - gap) * percentage / 100f
        val arcSize = Size(size.width - stroke, size.height - stroke)
        val topLeft = Offset(stroke / 2f, stroke / 2f)
        val start = 90f + gap / 2f + gapOffsetDegree
        drawArc(
            color = railColor,
            startAngle = start,
            sweepAngle = 360f - gap,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
        if (sweep > 0f) {
            val style = Stroke(width = stroke, cap = StrokeCap.Round)
            if (gradient != null) {
                drawArc(
                    brush = Brush.linearGradient(
                        colors = listOf(gradient.first, gradient.second),
                        start = Offset(0f, size.height),
                        end = Offset(size.width, 0f),
                    ),
                    startAngle = start + offsetDegree,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = style,
                )
            } else {
                drawArc(
                    color = fillColor,
                    startAngle = start + offsetDegree,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = style,
                )
            }
        }
    }
}

@Composable
private fun MultipleCircleCanvas(
    values: List<Float>,
    fillColors: List<Color>,
    railColors: List<Color>,
    fallbackFill: Color,
    fallbackRail: Color,
    gradients: List<Pair<Color, Color>>,
    strokeWidth: Dp,
    circleGap: Dp,
) {
    Canvas(Modifier.fillMaxSize()) {
        val stroke = strokeWidth.toPx()
        val gap = circleGap.toPx()
        val center = Offset(size.width / 2f, size.height / 2f)
        val outer = min(size.width, size.height) / 2f - stroke / 2f
        values.forEachIndexed { index, value ->
            val radius = outer - index * (stroke + gap)
            if (radius <= 0f) return@forEachIndexed
            val topLeft = Offset(center.x - radius, center.y - radius)
            val arcSize = Size(radius * 2f, radius * 2f)
            drawArc(
                color = railColors.getOrNull(index) ?: fallbackRail,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            val sweep = 360f * value.coerceIn(0f, 100f) / 100f
            if (sweep > 0f) {
                val style = Stroke(width = stroke, cap = StrokeCap.Round)
                val gradient = gradients.getOrNull(index)
                if (gradient != null) {
                    drawArc(
                        brush = Brush.linearGradient(
                            colors = listOf(gradient.first, gradient.second),
                            start = Offset(0f, size.height),
                            end = Offset(size.width, 0f),
                        ),
                        startAngle = -90f,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = style,
                    )
                } else {
                    drawArc(
                        color = fillColors.getOrNull(index) ?: fallbackFill,
                        startAngle = -90f,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = style,
                    )
                }
            }
        }
    }
}

@Composable
private fun statusFillColor(status: NProgressStatus): Color {
    val colors = LocalNTheme.current.colors
    return when (status) {
        NProgressStatus.Default, NProgressStatus.Info -> colors.info
        NProgressStatus.Success -> colors.success
        NProgressStatus.Warning -> colors.warning
        NProgressStatus.Error -> colors.error
    }
}

@Composable
private fun statusIconColor(status: NProgressStatus): Color {
    val colors = LocalNTheme.current.colors
    return when (status) {
        NProgressStatus.Default, NProgressStatus.Info -> colors.info
        NProgressStatus.Success -> colors.success
        NProgressStatus.Warning -> colors.warning
        NProgressStatus.Error -> colors.error
    }
}

private fun statusIcon(status: NProgressStatus) = when (status) {
    NProgressStatus.Success -> NIcons.Success
    NProgressStatus.Warning -> NIcons.Warning
    NProgressStatus.Error -> NIcons.Error
    NProgressStatus.Info -> NIcons.Info
    NProgressStatus.Default -> null
}

private fun formatPercentage(value: Float): String {
    val rounded = value.roundToInt()
    return if (abs(value - rounded) < 0.05f) {
        rounded.toString()
    } else {
        val tenths = (value * 10f).roundToInt()
        "${tenths / 10}.${abs(tenths % 10)}"
    }
}
