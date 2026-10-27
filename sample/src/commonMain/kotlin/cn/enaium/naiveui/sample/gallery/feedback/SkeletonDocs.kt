package cn.enaium.naiveui.sample.gallery.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NSwitch
import cn.enaium.naiveui.components.NSkeleton
import cn.enaium.naiveui.components.NSkeletonType
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

@Composable
fun SkeletonDocs() {
    DocsPage(
        title = "Skeleton",
        description = "A twinkling placeholder shown while content is loading.",
    ) {
        DemoSection(
            title = "Basic usage",
            description = "Use text skeletons to stand in for lines of copy.",
            code = "NSkeleton(type = NSkeletonType.Text, repeat = 2)\nNSkeleton(type = NSkeletonType.Text)",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NSkeleton(type = NSkeletonType.Text, repeat = 2)
                Box(Modifier.fillMaxWidth(0.6f)) {
                    NSkeleton(type = NSkeletonType.Text)
                }
            }
        }
        DemoSection(
            title = "Box",
            description = "Use skeleton as a box with custom dimensions, corners, round or circle shape.",
            code = "NSkeleton(height = 40.dp)\nNSkeleton(height = 40.dp, round = true)\nNSkeleton(height = 40.dp, type = NSkeletonType.Circle)",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NSkeleton(height = 40.dp)
                NSkeleton(height = 40.dp, sharp = false)
                NSkeleton(height = 40.dp, round = true)
                NSkeleton(height = 40.dp, type = NSkeletonType.Circle)
            }
        }
        DemoSection(
            title = "Size",
            description = "Size matches the height of the control the skeleton replaces.",
        ) {
            var loading by remember { mutableStateOf(true) }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NSwitch(checked = loading, onCheckedChange = { loading = it })
                    NText("Loading")
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (loading) {
                        NSkeleton(width = 146.dp, sharp = false, size = NSize.Medium)
                    } else {
                        NText("Won't you fly high")
                    }
                    if (loading) {
                        NSkeleton(width = 132.dp, round = true, size = NSize.Medium)
                    } else {
                        NText("free bird, yeah")
                    }
                    if (loading) {
                        NSkeleton(type = NSkeletonType.Circle, size = NSize.Medium)
                    } else {
                        NText("?")
                    }
                }
            }
        }
        DemoSection(
            title = "Animation",
            description = "The shimmer can be disabled for reduced motion.",
        ) {
            var animated by remember { mutableStateOf(true) }
            NSpace(vertical = true, size = 12.dp) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NSwitch(checked = animated, onCheckedChange = { animated = it })
                    NText("Animated")
                }
                NSkeleton(height = 32.dp, animated = animated)
                NSkeleton(type = NSkeletonType.Text, repeat = 3, animated = animated)
            }
        }
    }
}
