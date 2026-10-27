package cn.enaium.naiveui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class NDataTableDisplayTest {
    private fun people(): List<NTableRow> = listOf(
        NTableRow(mapOf("id" to "1", "name" to "Alice", "age" to 30, "city" to "NY")),
        NTableRow(mapOf("id" to "2", "name" to "Bob", "age" to 20, "city" to "LA")),
    )

    private fun columns(): List<NDataTableColumn> = listOf(
        NDataTableColumn(key = "name", title = "Name"),
        NDataTableColumn(key = "age", title = "Age", sortable = true),
        NDataTableColumn(
            key = "city",
            title = "City",
            filter = { value, row -> row["city"] == value },
        ),
    )

    @Test
    fun rendersHeadersAndCells() = runComposeUiTest {
        setNContent {
            NDataTable(columns = columns(), data = people())
        }
        onNodeWithText("Name").assertExists()
        onNodeWithText("Age").assertExists()
        onNodeWithText("City").assertExists()
        onNodeWithText("Alice").assertExists()
        onNodeWithText("Bob").assertExists()
        onNodeWithText("NY").assertExists()
        onNodeWithText("LA").assertExists()
        onNodeWithText("30").assertExists()
        onNodeWithText("20").assertExists()
    }

    @Test
    fun sortableHeaderCyclesOrderAndReportsSorter() = runComposeUiTest {
        var sorter: Map<String, NDataTableSortOrder>? = null
        var displayed: List<NTableRow> = emptyList()
        setNContent {
            NDataTable(
                columns = columns(),
                data = people(),
                onSorterChange = { sorter = it },
                onDisplayedRowsChange = { displayed = it },
            )
        }
        onNodeWithText("Age").performClick()
        waitForIdle()
        runOnIdle {
            assertEquals(mapOf("age" to NDataTableSortOrder.Ascend), sorter)
            assertEquals(listOf("Bob", "Alice"), displayed.map { it["name"] })
        }

        onNodeWithText("Age").performClick()
        waitForIdle()
        runOnIdle {
            assertEquals(mapOf("age" to NDataTableSortOrder.Descend), sorter)
            assertEquals(listOf("Alice", "Bob"), displayed.map { it["name"] })
        }

        onNodeWithText("Age").performClick()
        waitForIdle()
        runOnIdle {
            assertEquals(emptyMap<String, NDataTableSortOrder>(), sorter)
            assertEquals(listOf("Alice", "Bob"), displayed.map { it["name"] })
        }
    }

    @Test
    fun multipleSelectionTogglesRowKeys() = runComposeUiTest {
        var checked by mutableStateOf(emptySet<String>())
        setNContent {
            NDataTable(
                columns = columns(),
                data = people(),
                selectionMode = NTableSelectionMode.Multiple,
                checkedRowKeys = checked,
                onCheckedRowKeysChange = { checked = it },
            )
        }
        onAllNodes(isToggleable())[1].performClick()
        runOnIdle { assertEquals(setOf("1"), checked) }
        onAllNodes(isToggleable())[2].performClick()
        runOnIdle { assertEquals(setOf("1", "2"), checked) }
        onAllNodes(isToggleable())[1].performClick()
        runOnIdle { assertEquals(setOf("2"), checked) }
    }

    @Test
    fun unselectableRowIsDisabled() = runComposeUiTest {
        var checked by mutableStateOf(emptySet<String>())
        setNContent {
            NDataTable(
                columns = columns(),
                data = people(),
                selectionMode = NTableSelectionMode.Multiple,
                checkedRowKeys = checked,
                onCheckedRowKeysChange = { checked = it },
                selectable = { it["id"] != "2" },
            )
        }
        onAllNodes(isToggleable())[2].assertIsNotEnabled()
        onAllNodes(isToggleable())[2].performClick()
        runOnIdle { assertEquals(emptySet<String>(), checked) }
    }

    @Test
    fun controlledFiltersHideNonMatchingRows() = runComposeUiTest {
        setNContent {
            NDataTable(
                columns = columns(),
                data = people(),
                filters = mapOf("city" to setOf("NY")),
            )
        }
        onNodeWithText("Alice").assertExists()
        onNodeWithText("Bob").assertDoesNotExist()
    }

    @Test
    fun emptyTableShowsDescription() = runComposeUiTest {
        setNContent {
            NDataTable(columns = columns(), data = emptyList())
        }
        onNodeWithText("No Data").assertExists()
    }

    @Test
    fun customEmptyDescriptionIsShown() = runComposeUiTest {
        setNContent {
            NDataTable(
                columns = columns(),
                data = emptyList(),
                emptyDescription = "Nothing here",
            )
        }
        onNodeWithText("Nothing here").assertExists()
        onNodeWithText("No Data").assertDoesNotExist()
    }

    @Test
    fun paginationLimitsRowsAndChangesPage() = runComposeUiTest {
        var page = 0
        setNContent {
            NDataTable(
                columns = columns(),
                data = people(),
                pagination = NDataTablePagination(pageSize = 1),
                onPageChange = { page = it },
            )
        }
        onNodeWithText("Alice").assertExists()
        onNodeWithText("Bob").assertDoesNotExist()
        onNodeWithText("2").performClick()
        waitForIdle()
        onNodeWithText("Bob").assertExists()
        onNodeWithText("Alice").assertDoesNotExist()
        runOnIdle { assertEquals(2, page) }
    }

    @Test
    fun treeRowsExpandWithDefaultExpandAll() = runComposeUiTest {
        val data = listOf(
            NTableRow(
                mapOf(
                    "id" to "1",
                    "name" to "Parent",
                    "children" to listOf(
                        NTableRow(mapOf("id" to "1-1", "name" to "Child One")),
                        NTableRow(mapOf("id" to "1-2", "name" to "Child Two")),
                    ),
                ),
            ),
        )
        setNContent {
            NDataTable(
                columns = listOf(NDataTableColumn(key = "name", title = "Name")),
                data = data,
                defaultExpandAll = true,
            )
        }
        onNodeWithText("Parent").assertExists()
        onNodeWithText("Child One").assertExists()
        onNodeWithText("Child Two").assertExists()
    }
}

@OptIn(ExperimentalTestApi::class)
class NTableDisplayTest {
    private fun people(): List<NTableRow> = listOf(
        nRow("id" to "1", "name" to "Alice", "age" to 30),
        nRow("id" to "2", "name" to "Beta", "age" to 20),
    )

    private fun columns(): List<NTableColumn> = listOf(
        NTableColumn(key = "name", title = "Name"),
        NTableColumn(
            key = "age",
            title = "Age",
            sorter = { a, b -> (a["age"] as Int).compareTo(b["age"] as Int) },
        ),
    )

    @Test
    fun rendersHeadersAndRows() = runComposeUiTest {
        setNContent {
            NTable(columns = columns(), data = people())
        }
        onNodeWithText("Name").assertExists()
        onNodeWithText("Age").assertExists()
        onNodeWithText("Alice").assertExists()
        onNodeWithText("Beta").assertExists()
        onNodeWithText("30").assertExists()
        onNodeWithText("20").assertExists()
    }

    @Test
    fun clickingSortableHeaderReordersRows() = runComposeUiTest {
        setNContent {
            NTable(columns = columns(), data = people())
        }
        onNodeWithText("Age").performClick()
        waitForIdle()
        val betaTop = onNodeWithText("Beta").fetchSemanticsNode().boundsInRoot.top
        val aliceTop = onNodeWithText("Alice").fetchSemanticsNode().boundsInRoot.top
        assertTrue(betaTop < aliceTop)

        onNodeWithText("Age").performClick()
        waitForIdle()
        val betaTopDesc = onNodeWithText("Beta").fetchSemanticsNode().boundsInRoot.top
        val aliceTopDesc = onNodeWithText("Alice").fetchSemanticsNode().boundsInRoot.top
        assertTrue(aliceTopDesc < betaTopDesc)
    }

    @Test
    fun singleSelectionRowClickReportsKey() = runComposeUiTest {
        var checked by mutableStateOf(emptySet<String>())
        setNContent {
            NTable(
                columns = columns(),
                data = people(),
                rowKey = { it["id"].toString() },
                selectionMode = NTableSelectionMode.Single,
                checkedRowKeys = checked,
                onCheckedRowKeysChange = { checked = it },
            )
        }
        onNodeWithText("Alice").performClick()
        runOnIdle { assertEquals(setOf("1"), checked) }
    }

    @Test
    fun multipleSelectionHeaderSelectsAll() = runComposeUiTest {
        var checked by mutableStateOf(emptySet<String>())
        setNContent {
            NTable(
                columns = columns(),
                data = people(),
                rowKey = { it["id"].toString() },
                selectionMode = NTableSelectionMode.Multiple,
                checkedRowKeys = checked,
                onCheckedRowKeysChange = { checked = it },
            )
        }
        onAllNodes(isToggleable())[0].performClick()
        runOnIdle { assertEquals(setOf("1", "2"), checked) }
    }

    @Test
    fun summaryRowRendersComputedValues() = runComposeUiTest {
        setNContent {
            NTable(
                columns = columns(),
                data = people(),
                summary = { rows ->
                    mapOf(
                        "name" to "Total",
                        "age" to rows.sumOf { it["age"] as Int }.toString(),
                    )
                },
            )
        }
        onNodeWithText("Total").assertExists()
        onNodeWithText("50").assertExists()
    }

    @Test
    fun emptyTableShowsNoData() = runComposeUiTest {
        setNContent {
            NTable(columns = columns(), data = emptyList())
        }
        onNodeWithText("No Data").assertExists()
    }

    @Test
    fun columnFilterRemovesRows() = runComposeUiTest {
        val filters = listOf(
            NTableColumn(
                key = "name",
                title = "Name",
                filter = { it["name"] == "Alice" },
            ),
        )
        setNContent {
            NTable(columns = filters, data = people())
        }
        onNodeWithText("Alice").assertExists()
        onNodeWithText("Beta").assertDoesNotExist()
    }

    @Test
    fun paginationShowsNextPage() = runComposeUiTest {
        setNContent {
            NTable(
                columns = columns(),
                data = people(),
                pagination = NTablePagination(pageSize = 1, pageSizes = listOf(1, 2)),
            )
        }
        onNodeWithText("Alice").assertExists()
        onNodeWithText("Beta").assertDoesNotExist()
        onNodeWithText("2").performClick()
        waitForIdle()
        onNodeWithText("Beta").assertExists()
        onNodeWithText("Alice").assertDoesNotExist()
    }
}
