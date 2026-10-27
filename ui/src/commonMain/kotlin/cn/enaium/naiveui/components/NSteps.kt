package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize

/** Status of a step or of the currently active step. */
enum class NStepStatus { Wait, Process, Finish, Error }

/**
 * A single step description for [NSteps].
 *
 * [status] overrides the status derived from [NSteps.current] when set.
 */
class NStep(
    val title: String,
    val description: String? = null,
    val status: NStepStatus? = null,
    val disabled: Boolean = false,
)

/**
 * Displays progress through a sequence of steps.
 *
 * [current] is 1-based; steps before it are finished and the step at it uses
 * [status]. Individual [NStep.status] values take precedence.
 */
@Composable
fun NSteps(
    steps: List<NStep>,
    modifier: Modifier = Modifier,
    current: Int = 1,
    status: NStepStatus = NStepStatus.Process,
    vertical: Boolean = false,
    size: NSize = NSize.Medium,
) {
    val theme = LocalNTheme.current
    val indicatorSize: Dp = when (size) {
        NSize.Tiny, NSize.Small -> 22.dp
        NSize.Medium -> 28.dp
        NSize.Large -> 32.dp
    }
    val titleSize = when (size) {
        NSize.Tiny, NSize.Small -> theme.typography.fontSize.small
        NSize.Large -> theme.typography.fontSize.large
        else -> theme.typography.fontSize.medium
    }
    val descriptionSize = when (size) {
        NSize.Tiny, NSize.Small -> theme.typography.fontSize.tiny
        NSize.Large -> theme.typography.fontSize.medium
        else -> theme.typography.fontSize.small
    }

    if (vertical) {
        Column(modifier) {
            steps.forEachIndexed { index, step ->
                val stepStatus = resolveStepStatus(index, current, status, step.status)
                Row(Modifier.height(IntrinsicSize.Min)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        StepIndicator(
                            index = index + 1,
                            status = stepStatus,
                            size = indicatorSize,
                            disabled = step.disabled,
                        )
                        if (index != steps.lastIndex) {
                            Box(
                                Modifier
                                    .width(2.dp)
                                    .weight(1f)
                                    .padding(vertical = 4.dp)
                                    .background(connectorColor(stepStatus, theme)),
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(
                        Modifier
                            .weight(1f)
                            .alpha(if (step.disabled) theme.opacityDisabled else 1f)
                            .padding(bottom = if (index == steps.lastIndex) 0.dp else 20.dp),
                    ) {
                        StepText(step, stepStatus, titleSize, descriptionSize)
                    }
                }
            }
        }
        return
    }

    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        steps.forEachIndexed { index, step ->
            Column(Modifier.weight(1f).alpha(if (step.disabled) theme.opacityDisabled else 1f)) {
                val stepStatus = resolveStepStatus(index, current, status, step.status)
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StepIndicator(
                        index = index + 1,
                        status = stepStatus,
                        size = indicatorSize,
                        disabled = step.disabled,
                    )
                    if (index != steps.lastIndex) {
                        Box(
                            Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                                .height(2.dp)
                                .background(connectorColor(stepStatus, theme)),
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                StepText(step, stepStatus, titleSize, descriptionSize)
            }
        }
    }
}

@Composable
private fun StepText(
    step: NStep,
    status: NStepStatus,
    titleSize: androidx.compose.ui.unit.TextUnit,
    descriptionSize: androidx.compose.ui.unit.TextUnit,
) {
    val theme = LocalNTheme.current
    val titleColor = when (status) {
        NStepStatus.Process -> theme.colors.text1
        NStepStatus.Error -> theme.colors.error
        NStepStatus.Wait, NStepStatus.Finish -> theme.colors.textDisabled
    }
    val descriptionColor = when (status) {
        NStepStatus.Process -> theme.colors.text2
        NStepStatus.Error -> theme.colors.error
        NStepStatus.Wait, NStepStatus.Finish -> theme.colors.textDisabled
    }
    NBaseText(
        text = step.title,
        style = theme.typography.style(
            titleSize,
            if (status == NStepStatus.Process) FontWeight.Medium else theme.typography.fontWeight,
            titleColor,
        ),
        maxLines = 1,
    )
    val description = step.description
    if (!description.isNullOrEmpty()) {
        Spacer(Modifier.height(4.dp))
        NBaseText(
            text = description,
            style = theme.typography.style(descriptionSize, color = descriptionColor),
        )
    }
}

@Composable
private fun StepIndicator(
    index: Int,
    status: NStepStatus,
    size: Dp,
    disabled: Boolean,
) {
    val theme = LocalNTheme.current
    val background = when (status) {
        NStepStatus.Process -> theme.colors.primary
        NStepStatus.Wait, NStepStatus.Finish, NStepStatus.Error -> Color.Transparent
    }
    val borderColor = when (status) {
        NStepStatus.Process -> theme.colors.primary
        NStepStatus.Wait -> theme.colors.textDisabled
        NStepStatus.Finish -> theme.colors.primary
        NStepStatus.Error -> theme.colors.error
    }
    val contentColor = when (status) {
        NStepStatus.Process -> theme.colors.base
        NStepStatus.Wait -> theme.colors.textDisabled
        NStepStatus.Finish -> theme.colors.primary
        NStepStatus.Error -> theme.colors.error
    }
    val iconSize = size * 0.5f
    Box(
        modifier = Modifier
            .size(size)
            .alpha(if (disabled) theme.opacityDisabled else 1f)
            .clip(CircleShape)
            .background(background, CircleShape)
            .border(1.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        when (status) {
            NStepStatus.Finish -> NIcon(NIcons.Checkmark, size = iconSize, color = contentColor)
            NStepStatus.Error -> NIcon(NIcons.Close, size = iconSize, color = contentColor)
            else -> NBaseText(
                text = index.toString(),
                style = theme.typography.style(
                    theme.typography.fontSize.small * (size.value / 28f),
                    FontWeight.Medium,
                    contentColor,
                ),
            )
        }
    }
}

private fun resolveStepStatus(
    index: Int,
    current: Int,
    currentStatus: NStepStatus,
    stepStatus: NStepStatus?,
): NStepStatus {
    if (stepStatus != null) return stepStatus
    return when {
        index < current - 1 -> NStepStatus.Finish
        index > current - 1 -> NStepStatus.Wait
        else -> currentStatus
    }
}

private fun connectorColor(status: NStepStatus, theme: cn.enaium.naiveui.theme.NTheme): Color =
    if (status == NStepStatus.Finish) theme.colors.primary else theme.colors.textDisabled
