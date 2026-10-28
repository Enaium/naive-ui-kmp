package cn.enaium.naiveui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.MouseButton
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.math.abs

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

    @Test
    fun rowSpanCoversFollowingRowsAndCentersContent() = runComposeUiTest {
        val rows = listOf(
            NTableRow(mapOf("id" to "1", "name" to "Merged", "city" to "NY")),
            NTableRow(mapOf("id" to "2", "name" to "Hidden", "city" to "LA")),
            NTableRow(mapOf("id" to "3", "name" to "Last", "city" to "SF")),
        )
        setNContent {
            NDataTable(
                columns = listOf(
                    NDataTableColumn(
                        key = "name",
                        title = "Name",
                        rowSpan = { _, index -> if (index == 0) 2 else 1 },
                    ),
                    NDataTableColumn(key = "city", title = "City"),
                ),
                data = rows,
            )
        }
        onAllNodesWithText("Merged")[0].assertExists()
        onNodeWithText("Hidden").assertDoesNotExist()
        onNodeWithText("Last").assertExists()
        val merged = onNodeWithText("Merged").fetchSemanticsNode().boundsInRoot.center.y
        val first = onNodeWithText("NY").fetchSemanticsNode().boundsInRoot.center.y
        val second = onNodeWithText("LA").fetchSemanticsNode().boundsInRoot.center.y
        assertTrue(merged > first && merged < second, "merged content should be vertically centered")
    }

    @Test
    fun contextMenuOpensAtLongPressPosition() = runComposeUiTest {
        setNContent {
            NDataTable(
                columns = listOf(
                    NDataTableColumn("name", "Name"),
                    NDataTableColumn("city", "City"),
                ),
                data = people(),
                rowContextMenu = { _, dismiss ->
                    NButton(onClick = dismiss) { NText("Row action") }
                },
            )
        }
        val alice = onNodeWithText("Alice").fetchSemanticsNode().boundsInRoot
        val target = Offset(alice.left + 160f, alice.center.y)
        onRoot().performTouchInput { longClick(target) }
        waitUntil { onAllNodesWithText("Row action").fetchSemanticsNodes().isNotEmpty() }
        val menu = onNodeWithText("Row action").fetchSemanticsNode().boundsInRoot
        assertTrue(
            menu.left > target.x - 24f,
            "context menu should open near the long-press x, was ${menu.left} for $target",
        )
        assertTrue(
            abs(menu.top - target.y) < 60f,
            "context menu should open near the long-press y, was ${menu.top} for $target",
        )
    }

    @Test
    fun fixedColumnsStayPinnedWhenCenterScrolls() = runComposeUiTest {
        setNContent {
            NDataTable(
                columns = listOf(
                    NDataTableColumn("name", "Name", width = 100.dp, fixed = NDataTableFixed.Left),
                    NDataTableColumn(
                        "center",
                        "Center",
                        width = 200.dp,
                        render = { _, index -> NText("center $index") },
                    ),
                    NDataTableColumn("address", "Address", width = 120.dp, fixed = NDataTableFixed.Right),
                ),
                data = people(),
                scrollX = 1400.dp,
            )
        }
        val headerTop = onNodeWithText("Name").fetchSemanticsNode().boundsInRoot.top
        assertTrue(headerTop < 100f, "fixed-column shadows must not expand the header")
        val before = onNodeWithText("center 0").fetchSemanticsNode().boundsInRoot.left
        onNodeWithText("Center").performTouchInput { swipeLeft() }
        waitForIdle()
        val after = onNodeWithText("center 0").fetchSemanticsNode().boundsInRoot.left
        assertTrue(after < before, "center column should scroll horizontally")
        onNodeWithText("Name").assertExists()
        onNodeWithText("Address").assertExists()
    }

    @Test
    fun resizableColumnHandleChangesColumnWidth() = runComposeUiTest {
        setNContent {
            NDataTable(
                columns = listOf(
                    NDataTableColumn("name", "Name", width = 100.dp, resizable = true),
                    NDataTableColumn("city", "City"),
                ),
                data = people(),
            )
        }
        val handle = onNodeWithContentDescription(
            "Resize column name",
            useUnmergedTree = true,
        )
        val handleBounds = handle.fetchSemanticsNode().boundsInRoot
        assertEquals(100f, handleBounds.center.x, 1.5f)
        val before = handleBounds.left
        handle.performMouseInput {
            moveTo(center)
            press(MouseButton.Primary)
            moveBy(Offset(80f, 0f))
            moveBy(Offset(40f, 0f))
            release(MouseButton.Primary)
        }
        waitForIdle()
        val after = handle.fetchSemanticsNode().boundsInRoot.left
        assertTrue(after > before + 40f, "resize handle should move with the column edge: before=$before after=$after")
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
