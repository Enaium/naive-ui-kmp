package cn.enaium.naiveui.sample.gallery.general

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NAlign
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

private val galleryIcons: List<Pair<String, ImageVector>> = listOf(
    "Add" to NIcons.Add,
    "ArrowBack" to NIcons.ArrowBack,
    "ArrowDown" to NIcons.ArrowDown,
    "ArrowUp" to NIcons.ArrowUp,
    "Attach" to NIcons.Attach,
    "Backward" to NIcons.Backward,
    "Cancel" to NIcons.Cancel,
    "Checkmark" to NIcons.Checkmark,
    "ChevronDown" to NIcons.ChevronDown,
    "ChevronDownFilled" to NIcons.ChevronDownFilled,
    "ChevronLeft" to NIcons.ChevronLeft,
    "ChevronRight" to NIcons.ChevronRight,
    "Clear" to NIcons.Clear,
    "Close" to NIcons.Close,
    "Date" to NIcons.Date,
    "Download" to NIcons.Download,
    "Empty" to NIcons.Empty,
    "Error" to NIcons.Error,
    "Eye" to NIcons.Eye,
    "EyeOff" to NIcons.EyeOff,
    "FastBackward" to NIcons.FastBackward,
    "FastForward" to NIcons.FastForward,
    "File" to NIcons.File,
    "Filter" to NIcons.Filter,
    "Forward" to NIcons.Forward,
    "Info" to NIcons.Info,
    "More" to NIcons.More,
    "Photo" to NIcons.Photo,
    "Remove" to NIcons.Remove,
    "ResizeSmall" to NIcons.ResizeSmall,
    "Retry" to NIcons.Retry,
    "RotateClockwise" to NIcons.RotateClockwise,
    "RotateCounterclockwise" to NIcons.RotateCounterclockwise,
    "Search" to NIcons.Search,
    "Success" to NIcons.Success,
    "Switcher" to NIcons.Switcher,
    "Time" to NIcons.Time,
    "To" to NIcons.To,
    "Trash" to NIcons.Trash,
    "Warning" to NIcons.Warning,
    "ZoomIn" to NIcons.ZoomIn,
    "ZoomOut" to NIcons.ZoomOut,
    "Person" to NIcons.Person,
    "MenuIcon" to NIcons.MenuIcon,
    "Moon" to NIcons.Moon,
    "Sun" to NIcons.Sun,
    "Github" to NIcons.Github,
)

@Composable
fun IconDocs() {
    DocsPage(
        title = "Icon",
        description = "Icons render an ImageVector at a given size and tint, and can also wrap arbitrary custom content.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Icons accept a `size` and a `color`. The default color follows the theme's icon color.",
        ) {
            NSpace(size = 16.dp, align = NAlign.Center) {
                NIcon(NIcons.Success, size = 40.dp)
                NIcon(NIcons.Warning, size = 40.dp, color = Color(0xFF0E7A0D))
                NIcon(NIcons.Person, size = 40.dp, color = Color(0xFF8A2BE2))
            }
        }

        DemoSection(
            title = "Custom content",
            description = "The content overload accepts any composable, so custom drawings and text glyphs work.",
        ) {
            NSpace(size = 16.dp, align = NAlign.Center) {
                NIcon(size = 40.dp) {
                    NText("★", size = 34.sp, color = Color(0xFFF0A020))
                }
                NIcon(size = 40.dp) {
                    Box(
                        Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(LocalNTheme.current.colors.success),
                    )
                }
            }
        }

        DemoSection(
            title = "Depth",
            description = "Use text colors to match the depth of surrounding typography.",
        ) {
            val colors = LocalNTheme.current.colors
            NSpace(size = 16.dp, align = NAlign.Center) {
                NIcon(NIcons.More, size = 32.dp, color = colors.text1)
                NIcon(NIcons.More, size = 32.dp, color = colors.text2)
                NIcon(NIcons.More, size = 32.dp, color = colors.text3)
            }
        }

        DemoSection(
            title = "Icon with background",
            description = "A rounded box behind an icon makes a compact icon button or status badge.",
        ) {
            val colors = LocalNTheme.current.colors
            NSpace(size = 12.dp, align = NAlign.Center) {
                Box(
                    Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.success.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center,
                ) {
                    NIcon(NIcons.Checkmark, size = 18.dp, color = colors.success)
                }
                Box(
                    Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.primary.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center,
                ) {
                    NIcon(NIcons.Add, size = 18.dp, color = colors.primary)
                }
            }
        }

        DemoSection(
            title = "All icons",
            description = "Every icon shipped with the library. The name is shown below each icon.",
        ) {
            NSpace(size = 16.dp, verticalSize = 16.dp, wrap = true) {
                galleryIcons.forEach { (name, icon) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        NIcon(icon, size = 24.dp)
                        NText(name, size = 11.sp, color = LocalNTheme.current.colors.text3)
                    }
                }
            }
        }
    }
}
