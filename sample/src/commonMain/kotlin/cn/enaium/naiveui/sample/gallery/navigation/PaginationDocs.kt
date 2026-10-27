package cn.enaium.naiveui.sample.gallery.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import cn.enaium.naiveui.components.NButton
import cn.enaium.naiveui.components.NPagination
import cn.enaium.naiveui.components.NPaginationSizeOption
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

@Composable
fun PaginationDocs() {
    DocsPage(
        title = "Pagination",
        description = "Pagination splits long result sets into pages. It supports custom page slots, item counts, size pickers, jumpers, slots and every control size.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Use `pageCount` for a known number of pages and control `page` with state.",
            code = """
var page by remember { mutableStateOf(2) }
NPagination(
    page = page,
    pageCount = 100,
    onPageChange = { page = it },
)
            """.trimIndent(),
        ) {
            var page by remember { mutableStateOf(2) }
            NPagination(
                page = page,
                pageCount = 100,
                onPageChange = { page = it },
            )
        }

        DemoSection(
            title = "Simple mode",
            description = "Simple mode replaces the page list with a compact input and total.",
        ) {
            var page by remember { mutableStateOf(2) }
            NPagination(
                page = page,
                pageCount = 100,
                onPageChange = { page = it },
                simple = true,
            )
        }

        DemoSection(
            title = "Page slot",
            description = "`pageSlot` changes how many page items are displayed, keeping the control's length more stable.",
        ) {
            var page by remember { mutableStateOf(2) }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NPagination(page = page, pageCount = 100, onPageChange = { page = it })
                NPagination(
                    page = page,
                    pageCount = 100,
                    onPageChange = { page = it },
                    pageSlot = 8,
                )
                NPagination(
                    page = page,
                    pageCount = 100,
                    onPageChange = { page = it },
                    pageSlot = 7,
                )
            }
        }

        DemoSection(
            title = "Quick jumper",
            description = "The `goto` slot replaces the default jump label. Enter a page and press Go.",
        ) {
            var page by remember { mutableStateOf(2) }
            var secondPage by remember { mutableStateOf(2) }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NPagination(
                    page = page,
                    pageCount = 100,
                    onPageChange = { page = it },
                    showQuickJumper = true,
                    goto = { NText("Go!") },
                )
                NPagination(
                    page = secondPage,
                    pageCount = 100,
                    onPageChange = { secondPage = it },
                    showQuickJumper = true,
                    goto = { NText("Jump") },
                )
            }
        }

        DemoSection(
            title = "Size",
            description = "Small, medium and large controls, each with a size picker and quick jumper.",
        ) {
            var page by remember { mutableStateOf(2) }
            var pageSize by remember { mutableStateOf(10) }
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                listOf(NSize.Small, NSize.Medium, NSize.Large).forEach { size ->
                    NPagination(
                        page = page,
                        pageCount = 100,
                        onPageChange = { page = it },
                        pageSize = pageSize,
                        onPageSizeChange = { pageSize = it },
                        pageSizes = listOf(10, 20, 30, 40),
                        showSizePicker = true,
                        showQuickJumper = true,
                        size = size,
                    )
                }
            }
        }

        DemoSection(
            title = "Size picker",
            description = "Choose how many rows each page shows.",
        ) {
            var page by remember { mutableStateOf(2) }
            var pageSize by remember { mutableStateOf(20) }
            NPagination(
                page = page,
                pageCount = 100,
                onPageChange = { page = it },
                pageSize = pageSize,
                onPageSizeChange = { pageSize = it },
                pageSizes = listOf(10, 20, 30, 40),
                showSizePicker = true,
            )
        }

        DemoSection(
            title = "Disabled",
            description = "A disabled pagination blocks every control.",
        ) {
            var page by remember { mutableStateOf(2) }
            var pageSize by remember { mutableStateOf(20) }
            var disabled by remember { mutableStateOf(true) }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NPagination(
                    page = page,
                    pageCount = 100,
                    onPageChange = { page = it },
                    pageSize = pageSize,
                    onPageSizeChange = { pageSize = it },
                    pageSizes = listOf(10, 20, 30, 40),
                    showSizePicker = true,
                    showQuickJumper = true,
                    disabled = disabled,
                )
                NButton(onClick = { disabled = !disabled }) {
                    NText(if (disabled) "Enable" else "Disable")
                }
            }
        }

        DemoSection(
            title = "Use item count",
            description = "Pass `itemCount` instead of `pageCount`; the page count is derived from `pageSize`.",
        ) {
            var page by remember { mutableStateOf(1) }
            var pageSize by remember { mutableStateOf(10) }
            NPagination(
                page = page,
                itemCount = 201,
                pageSize = pageSize,
                onPageChange = { page = it },
                onPageSizeChange = { pageSize = it },
                pageSizes = listOf(10, 20, 30, 40),
                showSizePicker = true,
            )
        }

        DemoSection(
            title = "Custom previous and next",
            description = "Use the `prev` and `next` slots to replace the arrow content.",
        ) {
            var page by remember { mutableStateOf(2) }
            NPagination(
                page = page,
                pageCount = 101,
                onPageChange = { page = it },
                prev = { _ -> NText("Go Prev") },
                next = { _ -> NText("Go Next") },
            )
        }

        DemoSection(
            title = "Prefix and suffix",
            description = "Prefix and suffix slots receive the current page range information.",
        ) {
            var page by remember { mutableStateOf(1) }
            NPagination(
                page = page,
                itemCount = 101,
                onPageChange = { page = it },
                prefix = { info ->
                    NText("From item ${info.startIndex}, total ${info.itemCount}")
                },
                suffix = { info ->
                    NText("To item ${info.endIndex}")
                },
            )
        }

        DemoSection(
            title = "Custom page size options",
            description = "`pageSizeOptions` supplies labels for the size picker.",
        ) {
            var page by remember { mutableStateOf(2) }
            var pageSize by remember { mutableStateOf(20) }
            NPagination(
                page = page,
                pageCount = 100,
                onPageChange = { page = it },
                pageSize = pageSize,
                onPageSizeChange = { pageSize = it },
                pageSizeOptions = listOf(
                    NPaginationSizeOption(10, "10 per page"),
                    NPaginationSizeOption(20, "20 per page"),
                    NPaginationSizeOption(30, "30 per page"),
                    NPaginationSizeOption(40, "40 per page"),
                ),
                showSizePicker = true,
            )
        }
    }
}
