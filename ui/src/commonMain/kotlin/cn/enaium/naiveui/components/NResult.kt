package cn.enaium.naiveui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.theme.LocalNTheme
import kotlin.math.min

/** Outcome illustrated by [NResult]. */
enum class NResultStatus {
    Info,
    Success,
    Warning,
    Error,
    Forbidden403,
    NotFound404,
    Teapot418,
    ServerError500,
}

/** Size of the result illustration and typography. */
enum class NResultSize { Small, Medium, Large, Huge }

/**
 * Page-level feedback for an operation outcome.
 *
 * [content] is the body below the description and [extra] is the footer used
 * for actions.
 */
@Composable
fun NResult(
    modifier: Modifier = Modifier,
    status: NResultStatus = NResultStatus.Info,
    title: String? = null,
    description: String? = null,
    size: NResultSize = NResultSize.Medium,
    icon: (@Composable () -> Unit)? = null,
    extra: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) {
    val theme = LocalNTheme.current
    val iconSize = when (size) {
        NResultSize.Small -> 64.dp
        NResultSize.Medium -> 80.dp
        NResultSize.Large -> 100.dp
        NResultSize.Huge -> 125.dp
    }
    val titleSize = when (size) {
        NResultSize.Small -> 26.sp
        NResultSize.Medium -> 32.sp
        NResultSize.Large -> 40.sp
        NResultSize.Huge -> 48.sp
    }
    val fontSize = when (size) {
        NResultSize.Small, NResultSize.Medium -> 14.sp
        NResultSize.Large -> 15.sp
        NResultSize.Huge -> 16.sp
    }
    val accent = when (status) {
        NResultStatus.Info -> theme.colors.info
        NResultStatus.Success -> theme.colors.success
        NResultStatus.Warning -> theme.colors.warning
        NResultStatus.Error -> theme.colors.error
        else -> theme.colors.text2
    }
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (icon != null) {
            icon()
        } else {
            when (status) {
                NResultStatus.Info, NResultStatus.Success,
                NResultStatus.Warning, NResultStatus.Error
                -> NIcon(
                    icon = when (status) {
                        NResultStatus.Success -> NIcons.Success
                        NResultStatus.Warning -> NIcons.Warning
                        NResultStatus.Error -> NIcons.Error
                        else -> NIcons.Info
                    },
                    size = iconSize,
                    color = accent,
                )
                else -> ResultIllustration(status, iconSize)
            }
        }
        if (title != null) {
            Spacer(Modifier.height(16.dp))
            NBaseText(
                text = title,
                style = theme.typography.style(
                    size = titleSize,
                    weight = theme.typography.fontWeightStrong,
                    color = theme.colors.text1,
                ).copy(textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (description != null) {
            Spacer(Modifier.height(4.dp))
            NBaseText(
                text = description,
                style = theme.typography.style(size = fontSize, color = theme.colors.text2)
                    .copy(textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (content != null) {
            Spacer(Modifier.height(24.dp))
            Box(Modifier.fillMaxWidth()) { content() }
        }
        if (extra != null) {
            Spacer(Modifier.height(24.dp))
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { extra() }
        }
    }
}

@Composable
private fun ResultIllustration(status: NResultStatus, size: Dp) {
    Canvas(Modifier.size(size)) {
        when (status) {
            NResultStatus.Forbidden403 -> drawForbidden()
            NResultStatus.NotFound404 -> drawNotFound()
            NResultStatus.Teapot418 -> drawTeapot()
            NResultStatus.ServerError500 -> drawServerError()
            else -> Unit
        }
    }
}

private val FaceYellow = Color(0xFFFFCB4C)
private val FaceBrown = Color(0xFF65471B)
private val Orange = Color(0xFFEF9645)
private val SkinYellow = Color(0xFFFFDC5D)
private val TeapotGray = Color(0xFF66757F)
private val TeapotDark = Color(0xFF292F33)
private val TeapotLight = Color(0xFFE1E8ED)
private val TeaGreen = Color(0xFF77B255)
private val TeaGreenLight = Color(0xFFA6D388)

private fun DrawScope.drawNotFound() {
    val d = min(size.width, size.height)
    val c = center
    drawCircle(FaceYellow, radius = d * 0.47f, center = c)
    drawOval(
        color = FaceBrown,
        topLeft = Offset(c.x - d * 0.21f, c.y - d * 0.20f),
        size = Size(d * 0.09f, d * 0.14f),
    )
    drawOval(
        color = FaceBrown,
        topLeft = Offset(c.x + d * 0.12f, c.y - d * 0.20f),
        size = Size(d * 0.09f, d * 0.14f),
    )
    drawArc(
        color = FaceBrown,
        startAngle = 20f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(c.x - d * 0.24f, c.y - d * 0.14f),
        size = Size(d * 0.48f, d * 0.36f),
        style = Stroke(width = d * 0.05f, cap = StrokeCap.Round),
    )
}

private fun DrawScope.drawServerError() {
    val d = min(size.width, size.height)
    val c = center
    drawCircle(FaceYellow, radius = d * 0.47f, center = c)
    val eyeStroke = Stroke(width = d * 0.055f, cap = StrokeCap.Round)
    val eyeY = c.y - d * 0.16f
    val eyeSize = d * 0.11f
    listOf(-1f, 1f).forEach { side ->
        val x = c.x + side * d * 0.21f
        drawLine(
            FaceBrown,
            Offset(x - eyeSize, eyeY - eyeSize),
            Offset(x + eyeSize, eyeY + eyeSize),
            strokeWidth = eyeStroke.width,
            cap = StrokeCap.Round,
        )
        drawLine(
            FaceBrown,
            Offset(x - eyeSize, eyeY + eyeSize),
            Offset(x + eyeSize, eyeY - eyeSize),
            strokeWidth = eyeStroke.width,
            cap = StrokeCap.Round,
        )
    }
    drawArc(
        color = FaceBrown,
        startAngle = 200f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(c.x - d * 0.23f, c.y + d * 0.02f),
        size = Size(d * 0.46f, d * 0.34f),
        style = Stroke(width = d * 0.055f, cap = StrokeCap.Round),
    )
}

private fun DrawScope.drawForbidden() {
    val d = min(size.width, size.height)
    val c = center
    // Sleeve.
    drawRoundRect(
        color = Orange,
        topLeft = Offset(c.x - d * 0.17f, c.y + d * 0.20f),
        size = Size(d * 0.34f, d * 0.28f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(d * 0.06f),
    )
    // Palm.
    drawRoundRect(
        color = SkinYellow,
        topLeft = Offset(c.x - d * 0.21f, c.y - d * 0.12f),
        size = Size(d * 0.42f, d * 0.42f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(d * 0.12f),
    )
    // Fingers.
    val fingerWidth = d * 0.085f
    val fingers = listOf(
        -0.155f to -0.34f,
        -0.05f to -0.40f,
        0.055f to -0.38f,
        0.16f to -0.31f,
    )
    fingers.forEach { (x, top) ->
        drawLine(
            color = SkinYellow,
            start = Offset(c.x + d * x, c.y + d * top),
            end = Offset(c.x + d * x, c.y + d * 0.04f),
            strokeWidth = fingerWidth,
            cap = StrokeCap.Round,
        )
    }
    // Thumb.
    drawLine(
        color = SkinYellow,
        start = Offset(c.x - d * 0.20f, c.y + d * 0.16f),
        end = Offset(c.x - d * 0.33f, c.y + d * 0.01f),
        strokeWidth = fingerWidth * 1.1f,
        cap = StrokeCap.Round,
    )
}

private fun DrawScope.drawTeapot() {
    val d = min(size.width, size.height)
    val c = center
    drawOval(
        color = TeapotDark,
        topLeft = Offset(c.x - d * 0.46f, c.y + d * 0.10f),
        size = Size(d * 0.92f, d * 0.36f),
    )
    drawOval(
        color = TeapotGray,
        topLeft = Offset(c.x - d * 0.44f, c.y - d * 0.06f),
        size = Size(d * 0.88f, d * 0.34f),
    )
    drawOval(
        color = TeapotLight,
        topLeft = Offset(c.x - d * 0.42f, c.y - d * 0.14f),
        size = Size(d * 0.84f, d * 0.36f),
    )
    drawOval(
        color = TeaGreen,
        topLeft = Offset(c.x - d * 0.40f, c.y - d * 0.30f),
        size = Size(d * 0.80f, d * 0.28f),
    )
    drawOval(
        color = TeaGreenLight,
        topLeft = Offset(c.x - d * 0.34f, c.y - d * 0.27f),
        size = Size(d * 0.68f, d * 0.18f),
    )
    // Spout.
    drawLine(
        color = TeapotLight,
        start = Offset(c.x + d * 0.38f, c.y - d * 0.08f),
        end = Offset(c.x + d * 0.48f, c.y + d * 0.06f),
        strokeWidth = d * 0.07f,
        cap = StrokeCap.Round,
    )
    // Handle.
    drawArc(
        color = TeapotLight,
        startAngle = 250f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(c.x - d * 0.52f, c.y - d * 0.12f),
        size = Size(d * 0.24f, d * 0.24f),
        style = Stroke(width = d * 0.06f, cap = StrokeCap.Round),
    )
    // Steam.
    listOf(-0.08f, 0.05f).forEach { x ->
        drawArc(
            color = TeaGreen,
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(c.x + d * x, c.y - d * 0.46f),
            size = Size(d * 0.12f, d * 0.16f),
            style = Stroke(width = d * 0.035f, cap = StrokeCap.Round),
        )
    }
}
