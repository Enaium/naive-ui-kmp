package cn.enaium.naiveui.sample.gallery.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NAvatar
import cn.enaium.naiveui.components.NAvatarGroup
import cn.enaium.naiveui.components.NAvatarOption
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.components.NBadge
import cn.enaium.naiveui.components.NBadgeType
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NAlign
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage

private const val AVATAR_URL = "https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg"

private val lazySources = (1..10).map { "https://picsum.photos/id/$it/100/100" }

@Composable
fun AvatarDocs() {
    DocsPage(
        title = "Avatar",
        description = "On the Internet, nobody knows you're a dog. Avatars show a person or thing with an image, text or icon.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Pass an image URL through `src`; text and the fallback slot are shown while loading or on error.",
        ) {
            NAvatar(size = 40.dp, src = AVATAR_URL, text = "07")
        }

        DemoSection(
            title = "Size",
            description = "Avatars support small, medium and large sizes, plus arbitrary custom sizes in dp.",
        ) {
            NSpace(size = 12.dp, align = NAlign.End) {
                NAvatar(size = 24.dp, src = AVATAR_URL, text = "S")
                NAvatar(size = 34.dp, src = AVATAR_URL, text = "M")
                NAvatar(size = 40.dp, src = AVATAR_URL, text = "L")
                NAvatar(size = 48.dp, src = AVATAR_URL, text = "XL")
            }
        }

        DemoSection(
            title = "Shape",
            description = "Set `round` for a circular avatar; the default shape follows the theme radius.",
        ) {
            NSpace(size = 12.dp, align = NAlign.End) {
                NAvatar(size = 40.dp, src = AVATAR_URL, text = "R", round = false)
                NAvatar(size = 40.dp, src = AVATAR_URL, text = "R", round = true)
            }
        }

        DemoSection(
            title = "Color",
            description = "Set a background color to distinguish people or things.",
        ) {
            NSpace(size = 12.dp) {
                NAvatar(size = 40.dp, text = "M", color = Color(0xFFE74C3C))
                NAvatar(size = 40.dp, text = "A", color = Color(0xFF3498DB), round = true)
                NAvatar(size = 40.dp, text = "T", color = Color(0xFF2ECC71))
            }
        }

        DemoSection(
            title = "Badge",
            description = "Wrap an avatar with `NBadge` to show a count or status dot.",
        ) {
            NSpace(size = 20.dp, align = NAlign.Center) {
                NBadge(value = "999+") { NAvatar(text = "App") }
                NBadge(dot = true, type = NBadgeType.Success) {
                    NAvatar(text = "App")
                }
            }
        }

        DemoSection(
            title = "Icon",
            description = "Use the content slot for an icon avatar.",
        ) {
            NSpace(size = 12.dp) {
                NAvatar(size = 40.dp) {
                    NIcon(NIcons.Person, size = 24.dp)
                }
                NAvatar(size = 40.dp, round = true) {
                    NIcon(NIcons.Person, size = 24.dp, color = Color(0xFF8A2BE2))
                }
            }
        }

        DemoSection(
            title = "Name size",
            description = "Text is scaled to fit the avatar. Change the name and compare both shapes.",
        ) {
            var name by remember { mutableStateOf("Oasis") }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NSpace(size = 12.dp, align = NAlign.Center) {
                    NAvatar(size = 34.dp, text = name)
                    NAvatar(size = 34.dp, text = name, round = true)
                    NAvatar(size = 48.dp, text = name)
                }
                NButton(
                    onClick = {
                        name = when (name) {
                            "Oasis" -> "Beatles"
                            "Beatles" -> "Blur"
                            else -> "Oasis"
                        }
                    },
                ) {
                    NText("Change name")
                }
            }
        }

        DemoSection(
            title = "Fallback",
            description = "When an image fails to load, the fallback slot is shown.",
        ) {
            NSpace(size = 12.dp, align = NAlign.Center) {
                NAvatar(size = 40.dp, src = "empty.png", round = true, text = "07")
                NAvatar(
                    size = 40.dp,
                    src = "empty.png",
                    round = true,
                    fallback = { NText("07") },
                )
            }
        }

        DemoSection(
            title = "Group",
            description = "Avatars can overlap in a group. `max` collapses the extra people into a \"+N\" avatar.",
        ) {
            val options = remember {
                listOf(
                    NAvatarOption(text = "Leonardo"),
                    NAvatarOption(text = "Jennifer"),
                    NAvatarOption(text = "Audrey"),
                    NAvatarOption(text = "Anne"),
                    NAvatarOption(text = "Taylor"),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NAvatarGroup(options = options, max = 3, size = 40.dp, round = true)
                NAvatarGroup(size = 40.dp, round = true) {
                    options.forEach { option -> NAvatar(text = option.text) }
                }
            }
        }

        DemoSection(
            title = "Lazy load",
            description = "A `LazyColumn` only composes the avatars currently on screen, so their images load as they " +
                "enter the viewport. Scroll the list to load more.",
        ) {
            LazyColumn(
                Modifier.fillMaxWidth().height(140.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(lazySources) { source ->
                    NAvatar(size = 40.dp, src = source, text = "P", round = true)
                }
            }
        }

        DemoSection(
            title = "v-show debug",
            description = "Toggle both avatars. Compose has no `v-show`; conditional composition is the equivalent of " +
                "`v-if`, while `Modifier.alpha(0f)` matches a hidden but composed `v-show` node.",
        ) {
            var visible by remember { mutableStateOf(false) }
            var composed by remember { mutableStateOf(true) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NSpace(size = 12.dp, align = NAlign.Center) {
                    NButton(onClick = {
                        visible = !visible
                        composed = !composed
                    }) {
                        NText("Toggle")
                    }
                    if (visible) {
                        NAvatar(size = 40.dp, text = "Oasis111")
                    }
                    if (composed) {
                        NAvatar(size = 40.dp, text = "Oasis111", round = true)
                    } else {
                        NAvatar(size = 40.dp, text = "Oasis111", round = true, modifier = Modifier.alpha(0f))
                    }
                }
                NText("Composed opacity variant: ${if (composed) "visible" else "hidden"}")
            }
        }
    }
}
