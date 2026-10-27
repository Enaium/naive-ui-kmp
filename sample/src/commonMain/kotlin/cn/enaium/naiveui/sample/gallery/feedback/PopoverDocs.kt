package cn.enaium.naiveui.sample.gallery.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NButtonType
import cn.enaium.naiveui.components.NPopover
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.internal.NPlacement
import cn.enaium.naiveui.internal.NTrigger
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NSize
import kotlin.math.roundToInt

@Composable
fun PopoverDocs() {
    DocsPage(
        title = "Popover",
        description = "Popover is a generic floating surface. It supports hover, click, focus and manual triggers, " +
            "delays, arrow toggling, custom width, trigger width, header/footer slots, scrollable bodies and raw content.",
    ) {
        DemoSection(
            title = "Basic",
            description = "The `content` slot holds arbitrary composables.",
        ) {
            NPopover(
                trigger = NTrigger.Hover,
                content = { NText("Maybe I don't really want to know how your garden grows") },
            ) {
                NButton { NText("Hover") }
            }
        }

        DemoSection(
            title = "Delay",
            description = "`delay` is the time before showing and `duration` the time before hiding.",
        ) {
            NPopover(
                trigger = NTrigger.Hover,
                delay = 500,
                duration = 500,
                content = {
                    NText("Lately did you ever feel the pain in the morning rain as it soaks it to the bone")
                },
            ) {
                NButton { NText("Delay 500, Duration 500") }
            }
        }

        DemoSection(
            title = "Event",
            description = "`onUpdateShow` reports both opening and closing.",
        ) {
            var event by remember { mutableStateOf("No event yet") }
            var manualShow by remember { mutableStateOf(false) }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NSpace(size = 8.dp) {
                    NPopover(
                        placement = NPlacement.Bottom,
                        trigger = NTrigger.Hover,
                        onUpdateShow = { event = if (it) "show" else "hide" },
                        content = { NText("I wish they all could be California girls") },
                    ) {
                        NButton { NText("Hover") }
                    }
                    NPopover(
                        placement = NPlacement.Bottom,
                        trigger = NTrigger.Click,
                        onUpdateShow = { event = if (it) "show" else "hide" },
                        content = { NText("I wish they all could be California girls") },
                    ) {
                        NButton { NText("Click") }
                    }
                    NPopover(
                        placement = NPlacement.Bottom,
                        trigger = NTrigger.Manual,
                        show = manualShow,
                        onUpdateShow = { manualShow = it },
                        content = { NText("I wish they all could be California girls") },
                    ) {
                        NButton(onClick = {
                            manualShow = !manualShow
                            event = if (manualShow) "show" else "hide"
                        }) { NText("Manual") }
                    }
                }
                NText(event, size = 13.sp)
            }
        }

        DemoSection(
            title = "Adjust the popover",
            description = "Popups are clamped inside the window. The trigger below sits at the right edge so the " +
                "surface is shifted back into view instead of overflowing.",
        ) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                NPopover(
                    trigger = NTrigger.Click,
                    placement = NPlacement.BottomStart,
                    width = 320.dp,
                    content = {
                        NText(
                            "The default is to adjust within the existing position. If the existing position is " +
                                "not satisfied the surface is shifted at the same latitude so it stays visible.",
                        )
                    },
                ) {
                    NButton { NText("Offset") }
                }
            }
        }

        DemoSection(
            title = "Manually positioned",
            description = "Click inside the green area. A manual popover is anchored at the click position.",
        ) {
            val theme = LocalNTheme.current
            var show by remember { mutableStateOf(false) }
            var click by remember { mutableStateOf(Offset.Zero) }
            Box(
                Modifier
                    .size(200.dp)
                    .clipToBounds()
                    .background(theme.colors.success.copy(alpha = 0.5f))
                    .pointerInput(Unit) {
                        detectTapGestures { position ->
                            click = position
                            show = !show
                        }
                    },
            ) {
                Box(Modifier.offset { IntOffset(click.x.roundToInt(), click.y.roundToInt()) }) {
                    NPopover(
                        trigger = NTrigger.Manual,
                        placement = NPlacement.BottomStart,
                        show = show,
                        onUpdateShow = { show = it },
                        content = { NText("Cool!") },
                    ) {
                        Box(Modifier.size(1.dp))
                    }
                }
            }
        }

        DemoSection(
            title = "No arrow",
            description = "`showArrow = false` removes the arrow and its reserved space.",
        ) {
            NSpace(size = 8.dp) {
                NPopover(trigger = NTrigger.Hover, showArrow = true, content = { NText("Like a rect with an arrow") }) {
                    NButton { NText("Arrow") }
                }
                NPopover(trigger = NTrigger.Hover, showArrow = false, content = { NText("Like a rect") }) {
                    NButton { NText("No arrow") }
                }
            }
        }

        DemoSection(
            title = "Placement",
            description = "All twelve placements. Click a trigger to open its popover.",
        ) {
            NSpace(size = 8.dp, wrap = true) {
                NPlacement.entries.forEach { placement ->
                    NPopover(
                        trigger = NTrigger.Click,
                        placement = placement,
                        content = { NText("Oops!", size = 28.sp) },
                    ) {
                        NButton(size = NSize.Small) { NText(placement.name) }
                    }
                }
            }
        }

        DemoSection(
            title = "Raw content",
            description = "`raw = true` removes the popover background, border and padding so the content is the surface.",
        ) {
            NPopover(
                trigger = NTrigger.Hover,
                raw = true,
                showArrow = false,
                content = {
                    Box(
                        Modifier
                            .size(100.dp)
                            .background(Color(0xFFE53935)),
                        contentAlignment = Alignment.Center,
                    ) {
                        NText(
                            "Who kicks a hole in the sky so the heaven cry over me.",
                            color = Color.White,
                            size = 13.sp,
                        )
                    }
                },
            ) {
                NButton { NText("Hover") }
            }
        }

        DemoSection(
            title = "Slots",
            description = "`header` and `footer` are separated from the body by dividers.",
        ) {
            NPopover(
                trigger = NTrigger.Click,
                header = { NText("Divider is on the bottom.", strong = true) },
                footer = { NText("Divider is on the top.") },
                content = { NText("Content.") },
            ) {
                NButton { NText("There's a divider") }
            }
        }

        DemoSection(
            title = "Style",
            description = "Fixed width, max width, scrollable height, padding and header/footer combinations.",
        ) {
            NSpace(size = 8.dp, wrap = true) {
                NPopover(
                    trigger = NTrigger.Click,
                    width = 500.dp,
                    content = { NText("Looks like a bar") },
                ) {
                    NButton { NText("Width 500px") }
                }
                NPopover(
                    trigger = NTrigger.Click,
                    maxWidth = 100.dp,
                    content = { NText("Who kicks a hole in the sky so the heaven cry over me.") },
                ) {
                    NButton { NText("Max width 100px") }
                }
                NPopover(
                    trigger = NTrigger.Click,
                    scrollable = true,
                    maxHeight = 240.dp,
                    content = {
                        Column {
                            repeat(12) { NText("Put whatever you want.") }
                        }
                    },
                ) {
                    NButton { NText("Max height 240px") }
                }
                NPopover(
                    trigger = NTrigger.Click,
                    contentPadding = PaddingValues(0.dp),
                    content = { NText("Put whatever you want.") },
                ) {
                    NButton { NText("No padding") }
                }
                NPopover(
                    trigger = NTrigger.Click,
                    scrollable = true,
                    maxHeight = 240.dp,
                    contentPadding = PaddingValues(0.dp),
                    content = {
                        Column {
                            repeat(12) { NText("Put whatever you want.") }
                        }
                    },
                ) {
                    NButton { NText("Scrollable & no padding") }
                }
                NPopover(
                    trigger = NTrigger.Click,
                    contentPadding = PaddingValues(0.dp),
                    header = { NText("Divider is on the bottom.", strong = true) },
                    content = { NText("Divider is on the top.") },
                ) {
                    NButton { NText("With header & no padding") }
                }
            }
        }

        DemoSection(
            title = "Use trigger width",
            description = "`triggerWidth = true` makes the surface as wide as its anchor.",
        ) {
            NPopover(
                trigger = NTrigger.Hover,
                triggerWidth = true,
                content = {
                    NText("This old town don't smell too pretty and I can feel the warning signs running around my mind")
                },
            ) {
                NButton { NText("I would like to leave this city") }
            }
        }

        DemoSection(
            title = "Trigger",
            description = "Hover, hover without content keep-alive, click, focus and manual.",
        ) {
            var manualShow by remember { mutableStateOf(false) }
            NSpace(size = 8.dp, wrap = true) {
                NPopover(
                    trigger = NTrigger.Hover,
                    content = { NText("I wish they all could be California girls") },
                ) {
                    NButton { NText("Hover") }
                }
                NPopover(
                    trigger = NTrigger.Hover,
                    keepAliveOnHover = false,
                    content = { NText("I wish they all could be California girls") },
                ) {
                    NButton { NText("Hover (ignore popup)") }
                }
                NPopover(
                    trigger = NTrigger.Click,
                    content = { NText("I wish they all could be California girls") },
                ) {
                    NButton { NText("Click") }
                }
                NPopover(
                    trigger = NTrigger.Focus,
                    content = { NText("I wish they all could be California girls") },
                ) {
                    NButton { NText("Focus") }
                }
                NPopover(
                    trigger = NTrigger.Manual,
                    show = manualShow,
                    onUpdateShow = { manualShow = it },
                    content = { NText("I wish they all could be California girls") },
                ) {
                    NButton(onClick = { manualShow = !manualShow }) { NText("Manual") }
                }
            }
        }

        DemoSection(
            title = "Interactive content",
            description = "Popover content is a real composition, so it can hold state and actions.",
        ) {
            var count by remember { mutableStateOf(0) }
            NPopover(
                trigger = NTrigger.Click,
                content = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        NText("Count: $count")
                        NButton(
                            size = NSize.Small,
                            type = NButtonType.Primary,
                            onClick = { count++ },
                        ) {
                            NText("Increment")
                        }
                    }
                },
            ) {
                NButton { NText("Click me") }
            }
            NText("Count: $count", size = 13.sp)
        }
    }
}
