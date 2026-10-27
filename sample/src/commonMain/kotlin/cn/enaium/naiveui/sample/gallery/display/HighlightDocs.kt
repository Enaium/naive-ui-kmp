package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NHighlight
import cn.enaium.naiveui.components.NHighlightPattern
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme

private const val BasicText =
    "Naive UI is a Vue3 component library, it is relatively complete, tunable themes, written in TypeScript, fast."

private const val CaseText =
    "Naive UI is written in full TypeScript and works seamlessly with your TypeScript projects"

@Composable
fun HighlightDocs() {
    val theme = LocalNTheme.current
    DocsPage(
        title = "Highlight",
        description = "Highlight words or phrases inside a text with custom colors and weights.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Every occurrence of the supplied patterns is marked.",
            code = "NHighlight(text, listOf(NHighlightPattern(\"Vue3\")))",
        ) {
            NHighlight(
                text = BasicText,
                patterns = listOf(
                    NHighlightPattern("Naive UI"),
                    NHighlightPattern("Vue3"),
                    NHighlightPattern("TypeScript"),
                    NHighlightPattern("fast"),
                ),
                style = theme.typography.body.copy(fontSize = 15.sp),
            )
        }
        DemoSection(
            title = "Case sensitive",
            description = "By default matching is case-insensitive; enable caseSensitive to change that.",
        ) {
            NHighlight(
                text = CaseText,
                patterns = listOf(
                    NHighlightPattern("Naive UI"),
                    NHighlightPattern("typeScript"),
                ),
                caseSensitive = true,
            )
        }
        DemoSection(
            title = "Style",
            description = "Give each pattern its own color and weight, and tint the mark background.",
        ) {
            NHighlight(
                text = CaseText,
                patterns = listOf(
                    NHighlightPattern("Naive UI", color = theme.colors.success, bold = true),
                    NHighlightPattern("TypeScript", color = theme.colors.info),
                ),
                highlightBackground = theme.colors.primary.copy(alpha = 0.12f),
            )
        }
        DemoSection(
            title = "Component",
            description = "Patterns can combine a text color and a bold weight without any mark background.",
            code = "NHighlight(text, listOf(NHighlightPattern(\"Naive UI\", color = primary, bold = true)))",
        ) {
            NHighlight(
                text = CaseText,
                patterns = listOf(
                    NHighlightPattern("Naive UI", color = theme.colors.primary, bold = true),
                    NHighlightPattern("TypeScript", color = theme.colors.error),
                ),
                highlightBackground = Color.Transparent,
            )
        }
        DemoSection(
            title = "Whole word",
            description = "With wholeWord enabled, Vue does not match inside Vue3.",
        ) {
            NHighlight(
                text = "Vue is a framework. Vue3 is a newer major version of Vue.",
                patterns = listOf(NHighlightPattern("Vue")),
                wholeWord = true,
            )
        }
    }
}
