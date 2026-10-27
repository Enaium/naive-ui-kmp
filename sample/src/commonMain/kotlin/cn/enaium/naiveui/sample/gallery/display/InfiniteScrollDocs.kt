package cn.enaium.naiveui.sample.gallery.display

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.naiveui.components.NInfiniteScroll
import cn.enaium.naiveui.components.NAvatar
import cn.enaium.naiveui.components.NText
import cn.enaium.naiveui.sample.gallery.DemoSection
import cn.enaium.naiveui.sample.gallery.DocsPage
import cn.enaium.naiveui.theme.LocalNTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun InfiniteScrollDocs() {
    DocsPage(
        title = "Infinite Scroll",
        description = "Load more content when the viewport nears the bottom.",
    ) {
        DemoSection(
            title = "Basic",
            description = "onLoadMore fires when the content scrolls within distance of the bottom.",
            code = "NInfiniteScroll(onLoadMore = { count += 1 }, distance = 10.dp) { ... }",
        ) {
            var count by remember { mutableStateOf(6) }
            NInfiniteScroll(
                onLoadMore = { count += 1 },
                modifier = Modifier.height(240.dp),
                distance = 10.dp,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    repeat(count) { index -> ScrollItem(index + 1) }
                }
            }
        }
        DemoSection(
            title = "Loading state",
            description = "Gate onLoadMore with loading while new data is fetched.",
        ) {
            var items by remember { mutableStateOf((1..10).toList()) }
            var loading by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()
            val noMore = items.size > 22
            NInfiniteScroll(
                onLoadMore = {
                    if (!loading && !noMore) {
                        loading = true
                        scope.launch {
                            delay(800)
                            items = items + (items.size + 1..items.size + 2)
                            loading = false
                        }
                    }
                },
                modifier = Modifier.height(240.dp),
                loading = loading,
                distance = 20.dp,
                disabled = noMore,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items.forEach { index -> ScrollItem(index) }
                    Box(Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        NText(
                            text = when {
                                loading -> "Loading…"
                                noMore -> "No more"
                                else -> "Scroll down"
                            },
                            size = 13.sp,
                            color = LocalNTheme.current.colors.text3,
                        )
                    }
                }
            }
        }
        DemoSection(
            title = "Chat",
            description = "A chat-like list that appends messages while loading.",
        ) {
            var messages by remember {
                mutableStateOf(
                    listOf(
                        "Monday" to 0,
                        "Tuesday" to 1,
                        "Wednesday" to 2,
                        "Thursday" to 3,
                        "Friday" to 4,
                    ),
                )
            }
            var loading by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()
            NInfiniteScroll(
                onLoadMore = {
                    if (!loading) {
                        loading = true
                        scope.launch {
                            delay(700)
                            val weekdays = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
                            val next = messages.size
                            messages = messages + (weekdays[next % weekdays.size] to next)
                            loading = false
                        }
                    }
                },
                modifier = Modifier.height(240.dp),
                loading = loading,
                distance = 12.dp,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    messages.forEachIndexed { index, (message, avatarIndex) ->
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            NAvatar(
                                src = avatarUrl(avatarIndex),
                                size = 28.dp,
                                round = true,
                            )
                            NText("$message ${index + 1}", size = 14.sp)
                        }
                    }
                    if (loading) {
                        NText(
                            text = "Loading…",
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            size = 13.sp,
                            color = LocalNTheme.current.colors.text3,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScrollItem(index: Int) {
    val theme = LocalNTheme.current
    Box(
        Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(theme.borderRadius))
            .background(theme.colors.primary.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center,
    ) {
        NText("Item $index", color = theme.colors.primary)
    }
}

private fun avatarUrl(index: Int): String = when (index % 3) {
    0 -> "https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg"
    1 -> "https://avatars.githubusercontent.com/u/20943608?s=60&v=4"
    else -> "https://avatars.githubusercontent.com/u/39197136?s=60&v=4"
}
