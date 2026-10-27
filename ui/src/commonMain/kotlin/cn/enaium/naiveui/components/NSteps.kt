package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NBaseText
import cn.enaium.naiveui.internal.nClickable
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NTheme

/** Status of a step or of the currently active step. */
enum class NStepStatus { Wait, Process, Finish, Error }

/** Placement of step text relative to the indicator. */
enum class NStepContentPlacement { Right, Bottom }

/**
 * A single step description for [NSteps].
 *
 * [status] overrides the status derived from [NSteps.current] when set.
 * [icon] replaces the indicator for wait/process states, while [content]
 * replaces the description with custom composable content.
 */
class NStep(
    val title: String,
    val description: String? = null,
    val status: NStepStatus? = null,
    val disabled: Boolean = false,
    val icon: (@Composable () -> Unit)? = null,
    val content: (@Composable () -> Unit)? = null,
)

/**
 * Displays progress through a sequence of steps.
 *
 * [current] is 1-based; steps before it are finished and the step at it uses
 * [status]. Individual [NStep.status] values take precedence. When
 * [onCurrentChange] is provided steps become clickable.
 */
@Composable
fun NSteps(
    steps: List<NStep>,
    modifier: Modifier = Modifier,
    current: Int = 1,
    status: NStepStatus = NStepStatus.Process,
    vertical: Boolean = false,
    size: NSize = NSize.Medium,
    contentPlacement: NStepContentPlacement = NStepContentPlacement.Right,
    onCurrentChange: ((Int) -> Unit)? = null,
    finishIcon: (@Composable () -> Unit)? = null,
    errorIcon: (@Composable () -> Unit)? = null,
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
                val interaction = remember(index, step.title) { MutableInteractionSource() }
                val clickable = onCurrentChange != null && !step.disabled
                Row(
                    Modifier
                        .height(IntrinsicSize.Min)
                        .then(
                            if (clickable) {
                                Modifier
                                    .hoverable(interaction)
                                    .nClickable(interaction) { onCurrentChange?.invoke(index + 1) }
                            } else {
                                Modifier
                            },
                        ),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        StepIndicator(
                            index = index + 1,
                            status = stepStatus,
                            size = indicatorSize,
                            disabled = step.disabled,
                            icon = step.icon,
                            finishIcon = finishIcon,
                            errorIcon = errorIcon,
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
                        StepText(step, stepStatus, titleSize, descriptionSize, horizontal = false)
                    }
                }
            }
        }
        return
    }

    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        steps.forEachIndexed { index, step ->
            val stepStatus = resolveStepStatus(index, current, status, step.status)
            val interaction = remember(index, step.title) { MutableInteractionSource() }
            val clickable = onCurrentChange != null && !step.disabled
            Column(
                Modifier
                    .weight(1f)
                    .alpha(if (step.disabled) theme.opacityDisabled else 1f)
                    .then(
                        if (clickable) {
                            Modifier
                                .hoverable(interaction)
                                .nClickable(interaction) { onCurrentChange?.invoke(index + 1) }
                        } else {
                            Modifier
                        },
                    ),
            ) {
                if (contentPlacement == NStepContentPlacement.Bottom) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        StepIndicator(
                            index = index + 1,
                            status = stepStatus,
                            size = indicatorSize,
                            disabled = step.disabled,
                            icon = step.icon,
                            finishIcon = finishIcon,
                            errorIcon = errorIcon,
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
                    StepText(step, stepStatus, titleSize, descriptionSize, horizontal = false)
                } else {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        StepIndicator(
                            index = index + 1,
                            status = stepStatus,
                            size = indicatorSize,
                            disabled = step.disabled,
                            icon = step.icon,
                            finishIcon = finishIcon,
                            errorIcon = errorIcon,
                        )
                        Column(Modifier.weight(1f).padding(start = 9.dp)) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                StepTitle(step, stepStatus, titleSize)
                                if (index != steps.lastIndex) {
                                    Spacer(Modifier.width(12.dp))
                                    Box(
                                        Modifier
                                            .weight(1f)
                                            .height(1.dp)
                                            .background(connectorColor(stepStatus, theme)),
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            StepDescription(step, stepStatus, descriptionSize)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepText(
    step: NStep,
    status: NStepStatus,
    titleSize: TextUnit,
    descriptionSize: TextUnit,
    horizontal: Boolean,
) {
    StepTitle(step, status, titleSize)
    Spacer(Modifier.height(if (horizontal) 2.dp else 4.dp))
    StepDescription(step, status, descriptionSize)
}

@Composable
private fun StepTitle(step: NStep, status: NStepStatus, titleSize: TextUnit) {
    val theme = LocalNTheme.current
    NBaseText(
        text = step.title,
        style = theme.typography.style(
            titleSize,
            if (status == NStepStatus.Process) FontWeight.Medium else theme.typography.fontWeight,
            titleColor(status, theme),
        ),
        maxLines = 1,
    )
}

@Composable
private fun StepDescription(step: NStep, status: NStepStatus, descriptionSize: TextUnit) {
    val theme = LocalNTheme.current
    val custom = step.content
    if (custom != null) {
        custom()
    } else {
        val description = step.description
        if (!description.isNullOrEmpty()) {
            NBaseText(
                text = description,
                style = theme.typography.style(descriptionSize, color = descriptionColor(status, theme)),
            )
        }
    }
}

private fun titleColor(status: NStepStatus, theme: NTheme): Color = when (status) {
    NStepStatus.Process -> theme.colors.text1
    NStepStatus.Error -> theme.colors.error
    NStepStatus.Wait, NStepStatus.Finish -> theme.colors.textDisabled
}

private fun descriptionColor(status: NStepStatus, theme: NTheme): Color = when (status) {
    NStepStatus.Process -> theme.colors.text2
    NStepStatus.Error -> theme.colors.error
    NStepStatus.Wait, NStepStatus.Finish -> theme.colors.textDisabled
}

@Composable
private fun StepIndicator(
    index: Int,
    status: NStepStatus,
    size: Dp,
    disabled: Boolean,
    icon: (@Composable () -> Unit)?,
    finishIcon: (@Composable () -> Unit)?,
    errorIcon: (@Composable () -> Unit)?,
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
        when {
            status == NStepStatus.Finish -> {
                if (finishIcon != null) {
                    finishIcon()
                } else {
                    NIcon(NIcons.Checkmark, size = iconSize, color = contentColor)
                }
            }
            status == NStepStatus.Error -> {
                if (errorIcon != null) {
                    errorIcon()
                } else {
                    NIcon(NIcons.Close, size = iconSize, color = contentColor)
                }
            }
            icon != null -> icon()
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

private fun connectorColor(status: NStepStatus, theme: NTheme): Color =
    if (status == NStepStatus.Finish) theme.colors.primary else theme.colors.textDisabled
