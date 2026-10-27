package cn.enaium.naiveui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NButtonType
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.internal.NCloseButton
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.internal.nShadow
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import kotlin.math.roundToInt

/**
 * Positions a popup in window coordinates instead of relative to its anchor.
 *
 * [horizontal] and [vertical] are fractions of the free space: `0f` = start,
 * `0.5f` = center, `1f` = end. Margins are applied to the start/end edges and
 * ignored for centered axes.
 */
internal class NWindowPopupPositionProvider(
    private val horizontal: Float = 0f,
    private val vertical: Float = 0f,
    private val marginX: Int = 0,
    private val marginY: Int = 0,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val availableX = (windowSize.width - popupContentSize.width).coerceAtLeast(0)
        val availableY = (windowSize.height - popupContentSize.height).coerceAtLeast(0)
        val x = when {
            horizontal >= 1f -> availableX - marginX
            horizontal <= 0f -> marginX
            else -> (availableX * horizontal).roundToInt()
        }
        val y = when {
            vertical >= 1f -> availableY - marginY
            vertical <= 0f -> marginY
            else -> (availableY * vertical).roundToInt()
        }
        return IntOffset(x, y)
    }
}

@Composable
internal fun Modifier.nOverlayScrimClick(enabled: Boolean, onClick: () -> Unit): Modifier {
    val interaction = remember { MutableInteractionSource() }
    return clickable(
        interactionSource = interaction,
        indication = null,
    ) { if (enabled) onClick() }
}

/** Swallows pointer clicks so a surface above a scrim stays open. */
@Composable
internal fun Modifier.nConsumeClicks(): Modifier {
    val interaction = remember { MutableInteractionSource() }
    return clickable(
        interactionSource = interaction,
        indication = null,
    ) {}
}

/** Whether the modal renders the card or the dialog preset. */
enum class NModalPreset { Card, Dialog }

/** Vertical placement of the modal inside the window. */
enum class NModalPlacement { Center, Top }

/**
 * A modal window rendered above the whole application window.
 *
 * The scrim covers the window, `maskClosable` decides whether a click on it
 * dismisses the modal, and the content area scrolls when it gets too tall.
 */
@Composable
fun NModal(
    show: Boolean,
    onUpdateShow: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    preset: NModalPreset = NModalPreset.Card,
    title: String? = null,
    contentText: String? = null,
    closable: Boolean = true,
    maskClosable: Boolean = true,
    width: Dp = 440.dp,
    placement: NModalPlacement = NModalPlacement.Center,
    positiveText: String? = null,
    negativeText: String? = null,
    onPositiveClick: (() -> Unit)? = null,
    onNegativeClick: (() -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    if (!show) return
    val theme = LocalNTheme.current
    val locale = LocalNLocale.current
    val close = { onUpdateShow(false) }
    val hasHeader = title != null || closable
    val shape = RoundedCornerShape(theme.borderRadius)

    Popup(
        popupPositionProvider = remember { NWindowPopupPositionProvider() },
        onDismissRequest = close,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = maskClosable,
        ),
    ) {
        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .background(theme.colors.mask),
        ) {
            val modalWidth = minOf(width, (maxWidth - 48.dp).coerceAtLeast(240.dp))
            val contentMaxHeight = maxHeight * 0.66f
            Box(
                Modifier
                    .fillMaxSize()
                    .nOverlayScrimClick(maskClosable, close)
                    .padding(top = if (placement == NModalPlacement.Top) 80.dp else 0.dp),
                contentAlignment = if (placement == NModalPlacement.Center) {
                    Alignment.Center
                } else {
                    Alignment.TopCenter
                },
            ) {
                Column(
                    Modifier
                        .then(modifier)
                        .width(modalWidth)
                        .clip(shape)
                        .background(theme.colors.modal, shape)
                        .nShadow(theme, 3, shape)
                        .nBorder(theme.colors.border, 1.dp, theme.borderRadius)
                        .nConsumeClicks(),
                ) {
                    if (hasHeader) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 12.dp, top = 16.dp, bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (title != null) {
                                NText(
                                    text = title,
                                    modifier = Modifier.weight(1f),
                                    size = theme.typography.fontSize.huge,
                                    strong = true,
                                    color = theme.colors.text1,
                                )
                            } else {
                                Spacer(Modifier.weight(1f))
                            }
                            if (closable) {
                                NCloseButton(onClick = close, size = 20.dp, iconSize = 14.dp)
                            }
                        }
                    }
                    NScrollbar(Modifier.heightIn(max = contentMaxHeight)) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 20.dp,
                                    end = 20.dp,
                                    top = if (hasHeader) 0.dp else 20.dp,
                                    bottom = 20.dp,
                                ),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            if (contentText != null) {
                                NText(
                                    text = contentText,
                                    color = theme.colors.text3,
                                    size = theme.typography.fontSize.medium,
                                )
                            }
                            content()
                        }
                    }
                    if (actions != null) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            actions()
                        }
                    } else if (preset == NModalPreset.Dialog) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (negativeText != null || onNegativeClick != null) {
                                NButton(
                                    size = NSize.Small,
                                    onClick = {
                                        onNegativeClick?.invoke()
                                        close()
                                    },
                                ) {
                                    NText(negativeText ?: locale.popconfirmNo)
                                }
                            }
                            NButton(
                                size = NSize.Small,
                                type = NButtonType.Primary,
                                onClick = {
                                    onPositiveClick?.invoke()
                                    close()
                                },
                            ) {
                                NText(positiveText ?: locale.ok)
                            }
                        }
                    }
                }
            }
        }
    }
}

