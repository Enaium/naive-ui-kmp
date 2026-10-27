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
import cn.enaium.naiveui.components.NPagination
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.NSize

@Composable
fun PaginationDocs() {
    DocsPage(
        title = "Pagination",
        description = "Pagination splits long result sets into pages. It supports item counts, a page-size picker, a quick jumper and a compact simple mode.",
    ) {
        DemoSection(
            title = "Basic",
            description = "Use `pageCount` for a known number of pages and control `page` with state.",
            code = """
var page by remember { mutableStateOf(1) }
NPagination(
    page = page,
    pageCount = 100,
    onPageChange = { page = it },
)
            """.trimIndent(),
        ) {
            var page by remember { mutableStateOf(1) }
            NPagination(
                page = page,
                pageCount = 100,
                onPageChange = { page = it },
            )
        }

        DemoSection(
            title = "Item count",
            description = "Pass `itemCount` and `pageSize` instead of `pageCount`; the page count is derived.",
        ) {
            var page by remember { mutableStateOf(1) }
            NPagination(
                page = page,
                itemCount = 1000,
                pageSize = 20,
                onPageChange = { page = it },
            )
        }

        DemoSection(
            title = "Size picker",
            description = "Choose how many rows each page shows.",
        ) {
            var page by remember { mutableStateOf(1) }
            var pageSize by remember { mutableStateOf(20) }
            NPagination(
                page = page,
                itemCount = 1000,
                pageSize = pageSize,
                onPageChange = { page = it },
                onPageSizeChange = { pageSize = it },
                pageSizes = listOf(10, 20, 30, 40),
                showSizePicker = true,
            )
        }

        DemoSection(
            title = "Quick jumper",
            description = "Lets users type a page number and confirm it.",
        ) {
            var page by remember { mutableStateOf(1) }
            NPagination(
                page = page,
                pageCount = 50,
                onPageChange = { page = it },
                showQuickJumper = true,
            )
        }

        DemoSection(
            title = "Simple and disabled",
            description = "Simple mode replaces the page list with a compact input; disabled blocks all interaction.",
        ) {
            var page by remember { mutableStateOf(1) }
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                NPagination(
                    page = page,
                    pageCount = 100,
                    onPageChange = { page = it },
                    simple = true,
                )
                NPagination(
                    page = 3,
                    pageCount = 100,
                    disabled = true,
                )
            }
        }

        DemoSection(
            title = "Sizes",
            description = "Pagination follows the naive-ui small, medium and large control heights.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(NSize.Small, NSize.Medium, NSize.Large).forEach { size ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        var page by remember(size) { mutableStateOf(2) }
                        NPagination(
                            page = page,
                            pageCount = 50,
                            onPageChange = { page = it },
                            size = size,
                        )
                    }
                }
            }
        }
    }
}
