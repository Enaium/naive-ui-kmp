package cn.enaium.naiveui.sample

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.runComposeUiTest
import cn.enaium.naiveui.sample.gallery.SampleSection
import cn.enaium.naiveui.sample.gallery.sampleComponents
import cn.enaium.naiveui.theme.NConfigProvider
import cn.enaium.naiveui.theme.NGlobalStyle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CatalogTest {
    @Test
    fun catalogContainsEveryComponentOnce() {
        val components = sampleComponents()
        assertEquals(94, components.size)
        assertEquals(94, components.map { it.id }.toSet().size)
        assertEquals(94, components.map { it.title }.toSet().size)
        val expected = mapOf(
            SampleSection.General to 15,
            SampleSection.DataEntry to 21,
            SampleSection.Display to 21,
            SampleSection.Navigation to 9,
            SampleSection.Feedback to 16,
            SampleSection.Layout to 5,
            SampleSection.Utility to 4,
            SampleSection.Config to 3,
        )
        expected.forEach { (section, count) ->
            assertEquals(count, components.count { it.section == section }, section.title)
        }
        assertTrue(components.all { it.id.isNotBlank() && it.title.isNotBlank() })
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun everyDocsPageRenders() {
        val components = sampleComponents()
        components.forEach { component ->
            println("RENDER_DOCS ${component.id}")
            runComposeUiTest {
                setContent {
                    NConfigProvider {
                        NGlobalStyle { component.content() }
                    }
                }
                onAllNodesWithText(component.title)[0].assertExists()
            }
        }
    }
}
