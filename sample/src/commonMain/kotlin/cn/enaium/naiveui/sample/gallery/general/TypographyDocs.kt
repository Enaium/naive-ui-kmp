package cn.enaium.naiveui.sample.gallery.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NA
import cn.enaium.naiveui.components.NBlockquote
import cn.enaium.naiveui.components.NDivider
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
            title = "Header",
            description = "Six heading levels, from h1 to h6. Combine a heading with a colored NText for emphasis.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                NH1("H1 Heading")
                NText("Colored heading", type = NTextType.Primary, size = 30.sp, strong = true)
                NH2("H2 Heading")
                NH3("H3 Heading")
                NH4("H4 Heading")
                NH5("H5 Heading")
                NH6("H6 Heading")
            }
        }

        DemoSection(
            title = "Text",
            description = "Types, depth, strong, italic, underline, delete and code can be combined on the same text.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                NText("Default text")
                NText("Info text", type = NTextType.Info)
                NText("Success text", type = NTextType.Success)
                NText("Warning text", type = NTextType.Warning)
                NText("Error text", type = NTextType.Error)
                NText("Depth 1 — stronger", depth = 1)
                NText("Depth 2 — regular", depth = 2)
                NText("Depth 3 — weaker", depth = 3)
                NText("Strong text", strong = true)
                NText("Italic text", italic = true)
                NText("Underlined text", underline = true)
                NText("Deleted text", delete = true)
                NText("Inline code: let x = 1", code = true)
                NText("Code with delete", code = true, delete = true)
                NText("Strong and underlined", strong = true, underline = true)
                NText("Custom size", size = 20.sp)
            }
        }

        DemoSection(
            title = "Tags",
            description = "Headings, paragraphs, lists and blockquotes are available as standalone composables.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NH1("h1 tag")
                NP(PARAGRAPH)
                NH2("h2 tag")
                NP("Paragraphs use the theme body text and a configurable depth.", depth = 2)
                NH3("h3 tag")
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
                NBlockquote("Blockquotes show a vertical bar beside indented text.")
                NDivider()
                NText("Typography elements compose with dividers and each other.")
            }
        }

        DemoSection(
            title = "Router link",
            description = "NA renders a themed link. Click it to update the text below, the same way a router link " +
                "would navigate.",
        ) {
            var clicked by remember { mutableStateOf(0) }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                NA("Back Home", onClick = { clicked += 1 })
                NText(if (clicked == 0) "Not clicked yet" else "Clicked $clicked time(s)")
            }
        }
    }
}
