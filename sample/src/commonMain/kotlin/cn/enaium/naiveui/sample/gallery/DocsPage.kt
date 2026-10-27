package cn.enaium.naiveui.sample.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NDivider
import cn.enaium.naiveui.internal.NScrollbar
import cn.enaium.naiveui.internal.nBorder
import cn.enaium.naiveui.theme.LocalNLocale
import cn.enaium.naiveui.theme.LocalNTheme

/** Scrollable documentation page used by every gallery entry. */
@Composable
fun DocsPage(
    title: String,
    description: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    val theme = LocalNTheme.current
    val colors = theme.colors
    NScrollbar(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            val locale = LocalNLocale.current
            val sourcePath = componentSourcePath(title)
            val uriHandler = LocalUriHandler.current
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NText(
                    localizedDocsTitle(title, locale),
                    modifier = Modifier.weight(1f),
                    size = 30.sp,
                    strong = true,
                    color = colors.text1,
                )
                if (sourcePath != null) {
                    val sourceLabel = if (locale.name.startsWith("zh")) "查看示例源码" else "View sample source"
                    BasicText(
                        text = sourceLabel,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .clip(RoundedCornerShape(theme.borderRadiusSmall))
                            .background(colors.buttonColor2)
                            .clickable {
                                uriHandler.openUri(
                                    "https://github.com/enaium/naive-ui-kmp/blob/main/$sourcePath",
                                )
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        style = theme.typography.style(
                            13.sp,
                            FontWeight.Medium,
                            colors.primary,
                        ),
                    )
                }
            }
            NText(description, color = colors.text3, size = theme.typography.fontSize.medium)
            NDivider()
            content()
            Spacer(Modifier.height(40.dp))
        }
    }
}

/**
 * A single documented demo. Pass [code] to expose a Kotlin snippet behind the
 * "Code" toggle.
 */
@Composable
fun DemoSection(
    title: String,
    description: String? = null,
    code: String? = null,
    content: @Composable () -> Unit,
) {
    val theme = LocalNTheme.current
    val colors = theme.colors
    var showCode by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        NText(title, size = 18.sp, strong = true, color = colors.text1)
        if (description != null) {
            NText(description, size = theme.typography.fontSize.medium, color = colors.text3)
        }
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(theme.borderRadius))
                .background(colors.card)
                .nBorder(colors.divider, 1.dp, theme.borderRadius)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(Modifier.fillMaxWidth()) { content() }
            if (code != null) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    val chinese = LocalNLocale.current.name.startsWith("zh")
                    val label = when {
                        showCode && chinese -> "隐藏代码"
                        showCode -> "Hide code"
                        chinese -> "显示代码"
                        else -> "Show code"
                    }
                    Box {
                        BasicText(
                            text = label,
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(colors.buttonColor2)
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            style = TextStyle(
                                color = colors.primary,
                                fontSize = 13.sp,
                                fontFamily = theme.typography.fontFamily,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                        Box(
                            Modifier
                                .matchParentSize()
                                .clickable { showCode = !showCode },
                        )
                    }
                }
                if (showCode) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(theme.borderRadiusSmall))
                            .background(colors.code)
                            .padding(14.dp),
                    ) {
                        BasicText(
                            text = code,
                            style = TextStyle(
                                color = colors.text2, fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace, lineHeight = 20.sp,
                            ),
                        )
                    }
                }
            }
        }
    }
}

/** Width-constrained container used for responsive demo rows. */
@Composable
fun DemoRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

/** Simple monospaced snippet preview. */
@Composable
fun CodePreview(code: String, modifier: Modifier = Modifier) {
    val theme = LocalNTheme.current
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(theme.borderRadiusSmall))
            .background(theme.colors.code)
            .padding(14.dp),
    ) {
        BasicText(
            text = code,
            style = TextStyle(
                color = theme.colors.text2,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 20.sp,
            ),
        )
    }
}
