package cn.enaium.naiveui.sample.gallery.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NA
import cn.enaium.naiveui.components.NBlockquote
import cn.enaium.naiveui.components.NH1
import cn.enaium.naiveui.components.NH2
import cn.enaium.naiveui.components.NH3
import cn.enaium.naiveui.components.NH4
import cn.enaium.naiveui.components.NH5
import cn.enaium.naiveui.components.NH6
import cn.enaium.naiveui.components.NLi
import cn.enaium.naiveui.components.NOl
import cn.enaium.naiveui.components.NP
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.components.NTextType
import cn.enaium.naiveui.components.NUl
import cn.enaium.naiveui.components.NDivider
import cn.enaium.naiveui.components.NSpace
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage

private const val PARAGRAPH =
    "Hear the Wind Sing is the first novel by Japanese writer Haruki Murakami. " +
        "It first appeared in the June 1979 issue of Gunzo, and in book form the next month."

@Composable
fun TypographyDocs() {
    DocsPage(
        title = "Typography",
        description = "Naive UI provides HTML-like text styling: headings, paragraphs, quotes, lists, links and inline text styles.",
    ) {
        DemoSection(
            title = "Headings",
            description = "Six heading levels, from h1 to h6.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                NH1("H1 Heading")
                NH2("H2 Heading")
                NH3("H3 Heading")
                NH4("H4 Heading")
                NH5("H5 Heading")
                NH6("H6 Heading")
            }
        }

        DemoSection(
            title = "Text types",
            description = "Text supports the semantic types used across the library.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                NText("Default text")
                NText("Primary text", type = NTextType.Primary)
                NText("Info text", type = NTextType.Info)
                NText("Success text", type = NTextType.Success)
                NText("Warning text", type = NTextType.Warning)
                NText("Error text", type = NTextType.Error)
            }
        }

        DemoSection(
            title = "Depth",
            description = "Depth selects a shade of the theme's text colors, from strongest to weakest.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                NText("Depth 1 — stronger", depth = 1)
                NText("Depth 2 — regular", depth = 2)
                NText("Depth 3 — weaker", depth = 3)
            }
        }

        DemoSection(
            title = "Inline styles",
            description = "Strong, italic, underline, delete and code can be combined on the same text.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                NText("Strong text", strong = true)
                NText("Italic text", italic = true)
                NText("Underlined text", underline = true)
                NText("Deleted text", delete = true)
                NText("Inline code: let x = 1", code = true)
                NText("Strong and underlined", strong = true, underline = true)
                NText("Custom size", size = 20.sp)
            }
        }

        DemoSection(
            title = "Paragraph",
            description = "NP renders a paragraph with the default depth.",
        ) {
            NP(PARAGRAPH)
            NP("This paragraph uses depth 3.", depth = 3)
        }

        DemoSection(
            title = "Blockquote",
            description = "Blockquotes show a vertical bar beside indented text.",
        ) {
            NBlockquote(PARAGRAPH)
        }

        DemoSection(
            title = "Lists",
            description = "Untitled lists use bullets; ordered lists render an index for each item.",
        ) {
            NSpace(size = 24.dp, wrap = true) {
                NUl {
                    NLi("Unordered item one")
                    NLi("Unordered item two")
                    NLi("Unordered item three")
                }
                NOl {
                    NLi("Ordered item one", index = 1)
                    NLi("Ordered item two", index = 2)
                    NLi("Ordered item three", index = 3)
                }
            }
        }

        DemoSection(
            title = "Links",
            description = "NA renders a themed link. Click the link to update the text below.",
        ) {
            var clicked by remember { mutableStateOf(0) }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                NA("Hear the Wind Sing", onClick = { clicked += 1 })
                NText(if (clicked == 0) "Not clicked yet" else "Clicked $clicked time(s)")
            }
        }

        DemoSection(
            title = "Mixed content",
            description = "Typography elements compose naturally with dividers and each other.",
        ) {
            Column(Modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NH2("sin(x)")
                NP("The derivative of sin(x) is cos(x).", depth = 2)
                NDivider()
                NH3("cos(x)")
                NBlockquote("The derivative of cos(x) is -sin(x).")
            }
        }
    }
}
