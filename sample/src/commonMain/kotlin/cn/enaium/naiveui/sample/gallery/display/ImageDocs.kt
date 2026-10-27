package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NButtonType
import cn.enaium.naiveui.components.NFlex
import cn.enaium.naiveui.components.NImage
import cn.enaium.naiveui.components.NImageGroup
import cn.enaium.naiveui.components.NImageObjectFit
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.rememberNImageState
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

private const val Photo = "https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg"
private const val Artwork = "https://gw.alipayobjects.com/zos/antfincdn/aPkFc8Sj7n/method-draw-image.svg"
private const val Landscape = "https://picsum.photos/id/10/600/400"

@Composable
fun ImageDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Image",
        description = "Image with Coil loading, placeholders, fallbacks, groups and a fullscreen preview.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Click the image to preview it with zoom, rotation and reset controls.",
            code = "NImage(src = url, width = 100.dp, height = 100.dp)",
        ) {
            NImage(src = Photo, alt = "Basic", width = 100.dp, height = 100.dp)
        }
        DemoSection(
            title = "Image group",
            description = "Images composed inside NImageGroup share one viewer with previous/next navigation.",
            code = "NImageGroup { NImage(...); NImage(...) }",
        ) {
            NImageGroup {
                NFlex(size = 12.dp) {
                    NImage(src = Photo, alt = "Group one", width = 100.dp, height = 100.dp)
                    NImage(src = Artwork, alt = "Group two", width = 100.dp, height = 100.dp)
                }
            }
        }
        DemoSection(
            title = "Fail to load",
            description = "fallbackSrc replaces a broken source until the image is fixed.",
            code = "NImage(src = broken, fallbackSrc = url)",
        ) {
            NImage(
                src = "https://invalid.example/naive-ui/broken.png",
                fallbackSrc = Photo,
                alt = "Fallback",
                width = 100.dp,
                height = 100.dp,
            )
        }
        DemoSection(
            title = "Error slot",
            description = "The fallback slot replaces the built-in error display; toggle the source to retry.",
            code = "NImage(src = url, fallback = { NText(\"Failed to load\") })",
        ) {
            var src by remember { mutableStateOf("https://invalid.example/naive-ui/broken.png") }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NFlex(size = 8.dp) {
                    NButton(type = NButtonType.Error, onClick = {
                        src = "https://invalid.example/naive-ui/broken.png"
                    }) {
                        NText("Load fail")
                    }
                    NButton(type = NButtonType.Primary, onClick = { src = Photo }) {
                        NText("Refresh")
                    }
                }
                NImage(
                    src = src,
                    alt = "Custom error",
                    width = 100.dp,
                    height = 100.dp,
                    fallback = {
                        NText("Image unavailable", size = 12.sp, color = theme.colors.text3)
                    },
                )
            }
        }
        DemoSection(
            title = "Disable preview",
            description = "previewDisabled keeps the image static.",
        ) {
            NImage(
                src = Photo,
                alt = "Preview disabled",
                width = 100.dp,
                height = 100.dp,
                previewDisabled = true,
            )
        }
        DemoSection(
            title = "Custom toolbar",
            description = "renderToolbar receives a scope with prev/next/zoom/rotate/reset/close actions.",
            code = "NImage(renderToolbar = { NButton(onClick = { close() }) { ... } })",
        ) {
            NImage(
                src = Photo,
                alt = "Custom toolbar",
                width = 100.dp,
                height = 100.dp,
                renderToolbar = {
                    NFlex(size = 4.dp, align = cn.enaium.naiveui.components.NAlign.Center) {
                        NButton(size = cn.enaium.naiveui.theme.NSize.Tiny, onClick = { prev() }) {
                            NText("Prev")
                        }
                        NButton(size = cn.enaium.naiveui.theme.NSize.Tiny, onClick = { next() }) {
                            NText("Next")
                        }
                        NButton(size = cn.enaium.naiveui.theme.NSize.Tiny, onClick = { zoomIn() }) {
                            NText("Zoom in")
                        }
                        NButton(size = cn.enaium.naiveui.theme.NSize.Tiny, onClick = { close() }) {
                            NText("Close")
                        }
                    }
                },
            )
        }
        DemoSection(
            title = "Custom toolbar style",
            description = "Custom toolbars can use any components and colors, for example primary buttons.",
        ) {
            NImage(
                src = Landscape,
                alt = "Styled toolbar",
                width = 100.dp,
                height = 100.dp,
                renderToolbar = {
                    NFlex(size = 4.dp, align = cn.enaium.naiveui.components.NAlign.Center) {
                        NButton(
                            size = cn.enaium.naiveui.theme.NSize.Tiny,
                            type = NButtonType.Primary,
                            onClick = { zoomOut() },
                        ) {
                            NText("−")
                        }
                        NButton(
                            size = cn.enaium.naiveui.theme.NSize.Tiny,
                            type = NButtonType.Primary,
                            onClick = { zoomIn() },
                        ) {
                            NText("+")
                        }
                        NButton(
                            size = cn.enaium.naiveui.theme.NSize.Tiny,
                            type = NButtonType.Info,
                            onClick = { resizeToOriginalSize() },
                        ) {
                            NText("1:1")
                        }
                        NButton(
                            size = cn.enaium.naiveui.theme.NSize.Tiny,
                            type = NButtonType.Error,
                            onClick = { close() },
                        ) {
                            NText("Close")
                        }
                    }
                },
            )
        }
        DemoSection(
            title = "Toolbar tooltip",
            description = "showToolbarTooltip labels every default toolbar button on hover.",
            code = "NImageGroup(showToolbarTooltip = true) { ... }",
        ) {
            NImageGroup(showToolbarTooltip = true) {
                NFlex(size = 12.dp) {
                    NImage(src = Photo, alt = "Tooltip one", width = 100.dp, height = 100.dp)
                    NImage(src = Artwork, alt = "Tooltip two", width = 100.dp, height = 100.dp)
                }
            }
        }
        DemoSection(
            title = "Lazy load",
            description = "lazy defers the request until the image is laid out, so lazy lists load as they scroll.",
        ) {
            NImage(
                src = "https://picsum.photos/id/1/600/400",
                alt = "Lazy",
                width = 100.dp,
                height = 100.dp,
                lazy = true,
            )
        }
        DemoSection(
            title = "Preview image props",
            description = "previewSrc and previewObjectFit control the image shown in the viewer.",
            code = "NImage(src = thumb, previewSrc = large, previewObjectFit = NImageObjectFit.Cover)",
        ) {
            NImage(
                src = "https://picsum.photos/id/2/200/120",
                previewSrc = "https://picsum.photos/id/2/1200/750",
                previewObjectFit = NImageObjectFit.Cover,
                alt = "Preview props",
                width = 120.dp,
                height = 80.dp,
            )
        }
        DemoSection(
            title = "Manually open preview",
            description = "rememberNImageState exposes showPreview for buttons and other controls.",
            code = "val imageState = rememberNImageState(); NButton(onClick = { imageState.showPreview() })",
        ) {
            val imageState = rememberNImageState()
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NButton(onClick = { imageState.showPreview() }) { NText("Open preview") }
                NImage(
                    src = Photo,
                    alt = "Manual preview",
                    width = 100.dp,
                    height = 100.dp,
                    state = imageState,
                )
            }
        }
        DemoSection(
            title = "Component preview",
            description = "A standalone image opens its own preview modal without a group.",
        ) {
            NImage(
                src = Landscape,
                previewSrc = "https://picsum.photos/id/10/1400/900",
                alt = "Component preview",
                width = 140.dp,
                height = 90.dp,
            )
        }
        DemoSection(
            title = "Component preview group",
            description = "A group with three images: the toolbar arrows move through the gallery.",
        ) {
            NImageGroup(showToolbar = true) {
                NFlex(size = 12.dp, wrap = true) {
                    NImage(src = "https://picsum.photos/id/3/200/150", alt = "One", width = 100.dp, height = 75.dp)
                    NImage(src = "https://picsum.photos/id/4/200/150", alt = "Two", width = 100.dp, height = 75.dp)
                    NImage(src = "https://picsum.photos/id/5/200/150", alt = "Three", width = 100.dp, height = 75.dp)
                }
            }
        }
    }
}
