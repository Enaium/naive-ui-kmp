package cn.enaium.naiveui.sample.gallery.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NAvatar
import cn.enaium.naiveui.components.NAvatarGroup
import cn.enaium.naiveui.components.NAvatarOption
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NAlign
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage

private const val AVATAR_URL = "https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg"

@Composable
fun AvatarDocs() {
    DocsPage(
        title = "Avatar",
        description = "On the Internet, nobody knows you're a dog. Avatars show a person or thing with an image, text or icon.",
    ) {
        DemoSection(
            title = "Basic",
            description = "An avatar shows a remote image, text initials or fallback content.",
        ) {
            NAvatar(text = "NA")
        }

        DemoSection(
            title = "Size",
            description = "Avatars support small, medium and large sizes, plus arbitrary custom sizes in dp.",
        ) {
            NSpace(size = 12.dp, align = NAlign.End) {
                NAvatar(size = 24.dp, text = "S")
                NAvatar(size = 34.dp, text = "M")
                NAvatar(size = 40.dp, text = "L")
                NAvatar(size = 48.dp, text = "XL")
            }
        }

        DemoSection(
            title = "Shape",
            description = "Set `round` for a circular avatar; the default shape follows the theme radius.",
        ) {
            NSpace(size = 12.dp, align = NAlign.End) {
                NAvatar(size = 40.dp, text = "R", round = false)
                NAvatar(size = 40.dp, text = "R", round = true)
            }
        }

        DemoSection(
            title = "Text",
            description = "Text is resized to fit the avatar. Click the button to change the name shown.",
        ) {
            var name by remember { mutableStateOf("Oasis") }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NSpace(size = 12.dp, align = NAlign.Center) {
                    NAvatar(size = 40.dp, text = name)
                    NAvatar(size = 40.dp, text = name, round = true)
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
            title = "Icon",
            description = "Use the content slot for an icon avatar.",
        ) {
            NAvatar(size = 40.dp) {
                NIcon(NIcons.Person, size = 24.dp)
            }
            NAvatar(size = 40.dp, round = true) {
                NIcon(NIcons.Person, size = 24.dp, color = Color(0xFF8A2BE2))
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
            title = "Remote image",
            description = "Pass `src` to load an image. Text and fallback are used while loading or on error.",
        ) {
            NSpace(size = 12.dp, align = NAlign.Center) {
                NAvatar(size = 44.dp, src = AVATAR_URL, text = "07", round = true)
                NAvatar(
                    size = 44.dp,
                    src = AVATAR_URL,
                    text = "07",
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}
