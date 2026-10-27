package cn.enaium.naiveui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class NImageDisplayTest {
    @Test
    fun missingSourceShowsAltText() = runComposeUiTest {
        setNContent {
            NImage(alt = "Broken image")
        }
        onNodeWithText("Broken image").assertExists()
    }

    @Test
    fun missingSourceShowsCustomFallbackSlot() = runComposeUiTest {
        setNContent {
            NImage(
                alt = "Ignored",
                fallback = { NText("fallback slot") },
            )
        }
        onNodeWithText("fallback slot").assertExists()
        onNodeWithText("Ignored").assertDoesNotExist()
    }

    @Test
    fun sizingAndFitOptionsRender() = runComposeUiTest {
        setNContent {
            NImage(
                modifier = Modifier.testTag("image-box"),
                width = 120.dp,
                height = 80.dp,
                objectFit = NImageObjectFit.Cover,
                borderRadius = 8.dp,
                preview = false,
            )
        }
        onNodeWithTag("image-box").assertExists()
    }

    @Test
    fun imageGroupRendersEveryChild() = runComposeUiTest {
        setNContent {
            NImageGroup {
                Column {
                    NImage(alt = "First image")
                    NImage(alt = "Second image")
                }
            }
        }
        onNodeWithText("First image").assertExists()
        onNodeWithText("Second image").assertExists()
    }

    @Test
    fun loadAndErrorCallbacksStayIdleWithoutSource() = runComposeUiTest {
        var loads = 0
        var errors = 0
        setNContent {
            NImage(
                onLoad = { loads++ },
                onError = { errors++ },
            )
        }
        runOnIdle {
            assertEquals(0, loads)
            assertEquals(0, errors)
        }
    }
}

@OptIn(ExperimentalTestApi::class)
class NEmptyDisplayTest {
    @Test
    fun showsDefaultDescription() = runComposeUiTest {
        setNContent { NEmpty() }
        onNodeWithText("No Data").assertExists()
    }

    @Test
    fun showsCustomDescription() = runComposeUiTest {
        setNContent { NEmpty(description = "Nothing to show") }
        onNodeWithText("Nothing to show").assertExists()
        onNodeWithText("No Data").assertDoesNotExist()
    }

    @Test
    fun rendersContentSlot() = runComposeUiTest {
        setNContent {
            NEmpty(description = "Empty list") {
                NText("Add item")
            }
        }
        onNodeWithText("Empty list").assertExists()
        onNodeWithText("Add item").assertExists()
    }

    @Test
    fun hideIconStillRendersDescription() = runComposeUiTest {
        setNContent { NEmpty(description = "No results", showIcon = false) }
        onNodeWithText("No results").assertExists()
    }
}

@OptIn(ExperimentalTestApi::class)
class NThingDisplayTest {
    @Test
    fun rendersTitleDescriptionContentAndFooter() = runComposeUiTest {
        setNContent {
            NThing(
                title = "Title",
                titleExtra = "Extra",
                description = "Description",
                content = "Body content",
                footer = { NText("Footer") },
                action = { NText("Action") },
            )
        }
        onNodeWithText("Title").assertExists()
        onNodeWithText("Extra").assertExists()
        onNodeWithText("Description").assertExists()
        onNodeWithText("Body content").assertExists()
        onNodeWithText("Footer").assertExists()
        onNodeWithText("Action").assertExists()
    }

    @Test
    fun rendersAvatarHeaderAndSlots() = runComposeUiTest {
        setNContent {
            NThing(
                avatar = { NText("Avatar") },
                header = { NText("Header") },
                headerExtra = { NText("Header extra") },
                descriptionContent = { NText("Description slot") },
                contentSlot = { NText("Content slot") },
                contentIndented = true,
            )
        }
        onNodeWithText("Avatar").assertExists()
        onNodeWithText("Header").assertExists()
        onNodeWithText("Header extra").assertExists()
        onNodeWithText("Description slot").assertExists()
        onNodeWithText("Content slot").assertExists()
    }

    @Test
    fun descriptionOnlyThingRenders() = runComposeUiTest {
        setNContent {
            NThing(description = "Only description")
        }
        onNodeWithText("Only description").assertExists()
    }
}

@OptIn(ExperimentalTestApi::class)
class NLogDisplayTest {
    @Test
    fun rendersEveryLogLine() = runComposeUiTest {
        setNContent {
            NLog(log = "line one\nline two")
        }
        onNodeWithText("line one").assertExists()
        onNodeWithText("line two").assertExists()
    }

    @Test
    fun countKeepsOnlyLastLines() = runComposeUiTest {
        setNContent {
            NLog(log = "line one\nline two", count = 1)
        }
        onNodeWithText("line two").assertExists()
        onNodeWithText("line one").assertDoesNotExist()
    }

    @Test
    fun trimRemovesLeadingAndTrailingSpaces() = runComposeUiTest {
        setNContent {
            NLog(lines = listOf("  indented  "), trim = true)
        }
        onNodeWithText("indented").assertExists()
    }

    @Test
    fun loadingShowsLoadingLabel() = runComposeUiTest {
        setNContent {
            NLog(lines = listOf("a"), loading = true)
        }
        onNodeWithText("Loading").assertExists()
    }

    @Test
    fun languageHighlightingKeepsPlainText() = runComposeUiTest {
        setNContent {
            NLog(log = "val x = 1", language = "kotlin")
        }
        onNodeWithText("val x = 1").assertExists()
    }
}

@OptIn(ExperimentalTestApi::class)
class NInfiniteScrollDisplayTest {
    @Test
    fun rendersContent() = runComposeUiTest {
        setNContent {
            NInfiniteScroll(
                onLoadMore = {},
                modifier = Modifier.height(120.dp),
            ) {
                NText("Row 0")
                NText("Row 1")
            }
        }
        onNodeWithText("Row 0").assertExists()
        onNodeWithText("Row 1").assertExists()
    }

    @Test
    fun scrollingToBottomLoadsMore() = runComposeUiTest {
        var loads = 0
        setNContent {
            NInfiniteScroll(
                onLoadMore = { loads++ },
                modifier = Modifier.height(120.dp),
            ) {
                Column {
                    repeat(40) { NText("Row $it") }
                }
            }
        }
        onNodeWithText("Row 0").assertExists()
        onNodeWithText("Row 39").performScrollTo()
        waitUntil { loads == 1 }
        runOnIdle { assertEquals(1, loads) }
    }

    @Test
    fun loadingStateSuppressesLoadMore() = runComposeUiTest {
        var loads = 0
        setNContent {
            NInfiniteScroll(
                onLoadMore = { loads++ },
                modifier = Modifier.height(120.dp),
                loading = true,
            ) {
                Column {
                    repeat(40) { NText("Row $it") }
                }
            }
        }
        onNodeWithText("Row 39").performScrollTo()
        waitForIdle()
        runOnIdle { assertEquals(0, loads) }
    }

    @Test
    fun disabledSuppressesLoadMore() = runComposeUiTest {
        var loads = 0
        setNContent {
            NInfiniteScroll(
                onLoadMore = { loads++ },
                modifier = Modifier.height(120.dp),
                disabled = true,
            ) {
                Column {
                    repeat(40) { NText("Row $it") }
                }
            }
        }
        onNodeWithText("Row 39").performScrollTo()
        waitForIdle()
        runOnIdle { assertEquals(0, loads) }
    }
}

@OptIn(ExperimentalTestApi::class)
class NHeatmapDisplayTest {
    private val cells = listOf(
        NHeatmapCell(x = 0, y = 0, value = 1f),
        NHeatmapCell(x = 1, y = 0, value = 4f),
        NHeatmapCell(x = 0, y = 1, value = 2f),
    )

    @Test
    fun rendersFooterAndIndicator() = runComposeUiTest {
        setNContent {
            NHeatmap(
                data = cells,
                xLabels = listOf("Mon", "Tue"),
                yLabels = listOf("Week 1", "Week 2"),
                footer = { NText("heatmap footer") },
                indicator = { NText("heatmap indicator") },
            )
        }
        onNodeWithText("heatmap footer").assertExists()
        onNodeWithText("heatmap indicator").assertExists()
    }

    @Test
    fun showValuesAndColorThemeRender() = runComposeUiTest {
        setNContent {
            NHeatmap(
                data = cells,
                colorTheme = NHeatmapColorTheme.Blue,
                showValues = true,
                valueFormatter = { value -> "v$value" },
                footer = { NText("values footer") },
            )
        }
        onNodeWithText("values footer").assertExists()
    }

    @Test
    fun emptyDataStillRenders() = runComposeUiTest {
        setNContent {
            NHeatmap(
                data = emptyList(),
                footer = { NText("empty heatmap") },
            )
        }
        onNodeWithText("empty heatmap").assertExists()
    }

    @Test
    fun customColorScaleAndSizingRender() = runComposeUiTest {
        setNContent {
            NHeatmap(
                data = cells,
                colorScale = listOf(androidx.compose.ui.graphics.Color.White, androidx.compose.ui.graphics.Color.Black),
                cellSize = 16.dp,
                cellGap = 2.dp,
                indicator = { NText("scale") },
            )
        }
        onNodeWithText("scale").assertExists()
    }
}
