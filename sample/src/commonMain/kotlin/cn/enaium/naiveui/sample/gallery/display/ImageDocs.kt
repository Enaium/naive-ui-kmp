package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NImage
import cn.enaium.naiveui.components.NImageObjectFit
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NFlex
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

private const val Photo = "https://picsum.photos/id/10/600/400"

@Composable
fun ImageDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Image",
        description = "Image with Coil loading, placeholders, fallbacks and a fullscreen preview.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Click the image to preview it; the toolbar supports zoom and rotation.",
            code = "NImage(src = url, width = 240.dp, height = 150.dp)",
        ) {
            NImage(
                src = Photo,
                alt = "Mountains",
                width = 240.dp,
                height = 150.dp,
            )
        }
        DemoSection(
            title = "Object fit",
            description = "Fill stretches, Cover crops and Contain letterboxes.",
        ) {
            NFlex(
                modifier = Modifier.fillMaxWidth(),
                size = 12.dp,
                wrap = true,
            ) {
                ImageWithLabel("Fill", NImageObjectFit.Fill)
                ImageWithLabel("Cover", NImageObjectFit.Cover)
                ImageWithLabel("Contain", NImageObjectFit.Contain)
            }
        }
        DemoSection(
            title = "Lazy",
            description = "lazy defers the request until the element is laid out, which is what lazy lists want.",
        ) {
            NImage(
                src = "https://picsum.photos/id/1/600/400",
                alt = "Lazy image",
                width = 240.dp,
                height = 150.dp,
                lazy = true,
                placeholder = {
                    NText("Loading…", size = 13.sp, color = theme.colors.text3)
                },
            )
        }
        DemoSection(
            title = "Preview source",
            description = "previewSrc loads a higher resolution image inside the preview.",
        ) {
            NImage(
                src = "https://picsum.photos/id/2/240/150",
                previewSrc = "https://picsum.photos/id/2/1200/750",
                alt = "Preview",
                width = 240.dp,
                height = 150.dp,
            )
        }
        DemoSection(
            title = "Error and fallback",
            description = "When loading fails the fallback slot (or alt text) is shown.",
            code = "NImage(src = brokenUrl, fallback = { NText(\"Failed to load\") })",
        ) {
            NImage(
                src = "https://invalid.example/naive-ui/missing.png",
                alt = "Missing image",
                width = 240.dp,
                height = 150.dp,
                fallback = {
                    NText("Failed to load", size = 13.sp, color = theme.colors.error)
                },
            )
        }
    }
}

@Composable
private fun ImageWithLabel(label: String, fit: NImageObjectFit) {
    val theme = LocalNTheme.current
    Column(
        Modifier.width(150.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NImage(
            src = "https://picsum.photos/id/3/300/200",
            alt = label,
            objectFit = fit,
            width = 150.dp,
            height = 100.dp,
        )
        NText(label, size = 12.sp, color = theme.colors.text3)
    }
}
