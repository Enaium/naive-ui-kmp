package cn.enaium.naiveui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class NHighlightDisplayTest {
    private fun ComposeUiTest.spans(text: String): List<AnnotatedString.Range<SpanStyle>> {
        val node = onNodeWithText(text).fetchSemanticsNode()
        return node.config[SemanticsProperties.Text].single().spanStyles
    }

    @Test
    fun marksEveryMatchingPattern() = runComposeUiTest {
        setNContent {
            NHighlight(
                text = "The quick brown fox",
                patterns = listOf(NHighlightPattern("quick"), NHighlightPattern("fox")),
            )
        }
        onNodeWithText("The quick brown fox").assertExists()
        val spans = spans("The quick brown fox")
        assertEquals(2, spans.size)
        assertEquals(4, spans[0].start)
        assertEquals(9, spans[0].end)
        assertEquals(16, spans[1].start)
        assertEquals(19, spans[1].end)
    }

    @Test
    fun caseInsensitiveByDefault() = runComposeUiTest {
        setNContent {
            NHighlight(text = "Hello hello", patterns = listOf(NHighlightPattern("hello")))
        }
        assertEquals(2, spans("Hello hello").size)
    }

    @Test
    fun caseSensitiveHonorsCase() = runComposeUiTest {
        setNContent {
            NHighlight(
                text = "Hello hello",
                patterns = listOf(NHighlightPattern("hello")),
                caseSensitive = true,
            )
        }
        val spans = spans("Hello hello")
        assertEquals(1, spans.size)
        assertEquals(6, spans.single().start)
    }

    @Test
    fun wholeWordSkipsPartialMatches() = runComposeUiTest {
        setNContent {
            NHighlight(
                text = "cat category cat",
                patterns = listOf(NHighlightPattern("cat")),
                wholeWord = true,
            )
        }
        val spans = spans("cat category cat")
        assertEquals(2, spans.size)
        assertEquals(0, spans[0].start)
        assertEquals(13, spans[1].start)
    }

    @Test
    fun patternColorAndBoldAreApplied() = runComposeUiTest {
        setNContent {
            NHighlight(
                text = "hello world",
                patterns = listOf(NHighlightPattern("hello", color = Color.Red, bold = true)),
            )
        }
        val span = spans("hello world").single().item
        assertEquals(Color.Red, span.color)
        assertEquals(FontWeight.SemiBold, span.fontWeight)
    }

    @Test
    fun noMatchingPatternKeepsPlainText() = runComposeUiTest {
        setNContent {
            NHighlight(text = "plain text", patterns = listOf(NHighlightPattern("missing")))
        }
        onNodeWithText("plain text").assertExists()
        assertTrue(spans("plain text").isEmpty())
    }

    @Test
    fun emptyPatternListRendersPlainText() = runComposeUiTest {
        setNContent {
            NHighlight(text = "nothing to mark", patterns = emptyList())
        }
        onNodeWithText("nothing to mark").assertExists()
        assertTrue(spans("nothing to mark").isEmpty())
    }
}

@OptIn(ExperimentalTestApi::class)
class NCodeDisplayTest {
    @Test
    fun rendersPlainCode() = runComposeUiTest {
        setNContent {
            NCode(code = "val x = 1")
        }
        onNodeWithText("val x = 1").assertExists()
    }

    @Test
    fun rendersHighlightedLanguageWithoutChangingText() = runComposeUiTest {
        setNContent {
            NCode(code = "fun main() {}", language = "kotlin")
        }
        onNodeWithText("fun main() {}").assertExists()
    }

    @Test
    fun customHighlighterTransformsText() = runComposeUiTest {
        setNContent {
            NCode(
                code = "hello",
                hljs = { code, _ -> AnnotatedString(code.uppercase()) },
            )
        }
        onNodeWithText("HELLO").assertExists()
    }

    @Test
    fun showLineNumbersAddsNumberColumn() = runComposeUiTest {
        setNContent {
            NCode(code = "a\nb", showLineNumbers = true)
        }
        onNodeWithText("a\nb").assertExists()
        onNodeWithText("1\n2").assertExists()
    }

    @Test
    fun inlineCodeStillRendersText() = runComposeUiTest {
        setNContent {
            NCode(code = "x = 1", inline = true)
        }
        onNodeWithText("x = 1").assertExists()
    }

    @Test
    fun wordWrapKeepsAllLines() = runComposeUiTest {
        setNContent {
            NCode(code = "first\nsecond", wordWrap = true)
        }
        onNodeWithText("first\nsecond").assertExists()
    }
}

@OptIn(ExperimentalTestApi::class)
class NEquationDisplayTest {
    @Test
    fun rendersSimpleFormulaTokens() = runComposeUiTest {
        setNContent {
            NEquation(value = "E = mc^2")
        }
        onNodeWithText("E").assertExists()
        onNodeWithText("=").assertExists()
        onNodeWithText("mc").assertExists()
        onNodeWithText("2").assertExists()
    }

    @Test
    fun rendersFractionParts() = runComposeUiTest {
        setNContent {
            NEquation(value = "\\frac{1}{2}")
        }
        onNodeWithText("1").assertExists()
        onNodeWithText("2").assertExists()
    }

    @Test
    fun rendersSquareRootRadicand() = runComposeUiTest {
        setNContent {
            NEquation(value = "\\sqrt{9}")
        }
        onNodeWithText("9").assertExists()
    }

    @Test
    fun unknownCommandFallsBackToPlainText() = runComposeUiTest {
        setNContent {
            NEquation(value = "\\unknowncmd x")
        }
        assertTrue(
            onAllNodesWithText("unknowncmd", substring = true).fetchSemanticsNodes().isNotEmpty(),
        )
        onNodeWithText("x").assertExists()
    }

    @Test
    fun displayModeRendersCenteredFormula() = runComposeUiTest {
        setNContent {
            NEquation(value = "a + b", displayMode = true)
        }
        onNodeWithText("a").assertExists()
        onNodeWithText("b").assertExists()
        onNodeWithText("+").assertExists()
    }

    @Test
    fun lineBreakSplitsFormulaRows() = runComposeUiTest {
        setNContent {
            NEquation(value = "a \\\\ b")
        }
        onNodeWithText("a").assertExists()
        onNodeWithText("b").assertExists()
    }
}
