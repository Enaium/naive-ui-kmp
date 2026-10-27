package cn.enaium.naiveui.sample.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NInput
import cn.enaium.naiveui.components.NDropdown
import cn.enaium.naiveui.components.NDropdownOption
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NButtonType
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.components.NAlign
import cn.enaium.naiveui.icons.NIcon
import cn.enaium.naiveui.icons.NIcons
import cn.enaium.naiveui.internal.NTrigger
import cn.enaium.naiveui.internal.NImageLoaderSetup
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.nBottomBorder
import cn.enaium.naiveui.internal.nRightBorder
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme
import cn.enaium.naiveui.theme.NConfigProvider
import cn.enaium.naiveui.theme.NGlobalStyle
import cn.enaium.naiveui.theme.NLocale
import cn.enaium.naiveui.theme.NLocales
import cn.enaium.naiveui.theme.NSize
import cn.enaium.naiveui.theme.NTheme

@Composable
fun SampleApp() {
    var dark by remember { mutableStateOf(false) }
    var locale by remember { mutableStateOf(NLocale.English) }
    var selectedId by remember { mutableStateOf("button") }
    var drawerOpen by remember { mutableStateOf(false) }
    val components = remember { sampleComponents() }
    NConfigProvider(
        theme = if (dark) NTheme.Dark else NTheme.Light,
        locale = locale,
        rtl = locale.isRtl,
    ) {
        NGlobalStyle {
            NImageLoaderSetup()
            BoxWithConstraints(Modifier.fillMaxSize().safeDrawingPadding()) {
                val wide = maxWidth >= 900.dp
                val selected = components.firstOrNull { it.id == selectedId } ?: components.first()
                Column(Modifier.fillMaxSize()) {
                    GalleryHeader(
                        dark = dark,
                        locale = locale,
                        showMenu = !wide,
                        onOpenMenu = { drawerOpen = true },
                        onToggleDark = { dark = !dark },
                        onLocaleChange = { locale = it },
                    )
                    Row(Modifier.weight(1f).fillMaxWidth()) {
                        if (wide) {
                            GallerySidebar(
                                components = components,
                                selectedId = selectedId,
                                onSelect = { selectedId = it },
                                modifier = Modifier.width(280.dp).fillMaxHeight(),
                            )
                        }
                        Box(Modifier.weight(1f).fillMaxHeight()) {
                            key(selectedId) {
                                selected.content()
                            }
                        }
                    }
                }
                if (!wide && drawerOpen) {
                    Box(Modifier.fillMaxSize()) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.35f))
                                .clickable { drawerOpen = false },
                        )
                        GallerySidebar(
                            components = components,
                            selectedId = selectedId,
                            onSelect = {
                                selectedId = it
                                drawerOpen = false
                            },
                            modifier = Modifier.width(280.dp).fillMaxHeight(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GalleryHeader(
    dark: Boolean,
    locale: NLocale,
    showMenu: Boolean,
    onOpenMenu: () -> Unit,
    onToggleDark: () -> Unit,
    onLocaleChange: (NLocale) -> Unit,
) {
    val theme = LocalNTheme.current
    val colors = theme.colors
    val uriHandler = LocalUriHandler.current
    Row(
        Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(colors.card)
            .nBottomBorder(colors.divider)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showMenu) {
            Box(
                Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = onOpenMenu),
                contentAlignment = Alignment.Center,
            ) {
                NIcon(NIcons.MenuIcon, size = 20.dp, color = colors.text2, contentDescription = if (locale.name.startsWith("zh")) "打开导航" else "Open navigation")
            }
            Spacer(Modifier.width(8.dp))
        }
        NText(
            "Naive UI",
            size = 20.sp,
            strong = true,
            color = colors.primary,
        )
        Spacer(Modifier.width(8.dp))
        NText(
            "Kotlin Multiplatform",
            size = 13.sp,
            color = colors.text3,
        )
        Spacer(Modifier.weight(1f))
        NSpace(size = 8.dp, align = NAlign.Center) {
            NDropdown(
                options = NLocales.all
                    .sortedBy { it.displayName.lowercase() }
                    .map { NDropdownOption.Label(it.name, it.displayName) },
                trigger = NTrigger.Click,
                onSelect = { name -> onLocaleChange(NLocales.of(name)) },
            ) {
                NButton(
                    onClick = {},
                    type = NButtonType.Tertiary,
                    size = NSize.Small,
                ) {
                    NText(locale.displayName, size = 13.sp)
                    Spacer(Modifier.width(4.dp))
                    NIcon(NIcons.ChevronDown, size = 12.dp, color = colors.text3)
                }
            }
            NButton(onClick = onToggleDark, type = NButtonType.Tertiary, size = cn.enaium.naiveui.theme.NSize.Small) {
                NIcon(if (dark) NIcons.Sun else NIcons.Moon, size = 16.dp, color = colors.text2)
                Spacer(Modifier.width(6.dp))
                NText(
                    if (locale.name.startsWith("zh")) {
                        if (dark) "浅色" else "深色"
                    } else {
                        if (dark) "Light" else "Dark"
                    },
                    size = 13.sp,
                )
            }
            NButton(
                onClick = { uriHandler.openUri("https://github.com/enaium/naive-ui-kmp") },
                type = NButtonType.Default,
                size = cn.enaium.naiveui.theme.NSize.Small,
                icon = { NIcon(NIcons.Github, size = 16.dp, color = colors.text2) },
            ) {
                NText("GitHub", size = 13.sp)
            }
        }
    }
}

@Composable
private fun GallerySidebar(
    components: List<SampleComponent>,
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalNTheme.current
    val colors = theme.colors
    val locale = LocalNLocale.current
    var query by remember { mutableStateOf("") }
    val filtered = remember(query, components, locale) {
        if (query.isBlank()) {
            components
        } else {
            components.filter {
                it.title.contains(query, ignoreCase = true) ||
                    it.displayTitle(locale).contains(query, ignoreCase = true) ||
                    it.id.contains(query, ignoreCase = true)
            }
        }
    }
    Column(
        modifier
            .background(colors.card)
            .nRightBorder(colors.divider)
            .padding(vertical = 10.dp),
    ) {
        Box(Modifier.padding(horizontal = 12.dp, vertical = 2.dp)) {
            NInput(
                value = query,
                onValueChange = { query = it },
                placeholder = if (locale.name.startsWith("zh")) "搜索组件" else "Search components",
                size = NSize.Small,
                clearable = true,
                prefix = {
                    NIcon(NIcons.Search, size = 14.dp, color = colors.text3)
                    Spacer(Modifier.width(6.dp))
                },
            )
        }
        Spacer(Modifier.height(4.dp))
        NScrollbar(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 4.dp)) {
                SampleSection.entries.forEach { section ->
                    val items = filtered.filter { it.section == section }
                    if (items.isEmpty()) return@forEach
                    BasicText(
                        text = section.displayTitle(locale).uppercase(),
                        modifier = Modifier.padding(start = 10.dp, top = 14.dp, bottom = 6.dp),
                        style = theme.typography.style(12.sp, FontWeight.Medium, colors.text3),
                    )
                    items.forEach { component ->
                        val selected = component.id == selectedId
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (selected) colors.primary.copy(alpha = 0.1f) else Color.Transparent)
                                .clickable { onSelect(component.id) }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            BasicText(
                                text = component.displayTitle(locale),
                                style = theme.typography.style(
                                    14.sp,
                                    if (selected) FontWeight.Medium else FontWeight.Normal,
                                    if (selected) colors.primary else colors.text2,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}
