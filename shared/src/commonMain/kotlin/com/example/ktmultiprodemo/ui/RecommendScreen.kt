package com.example.ktmultiprodemo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 推荐页：纯 UI 层。
 * 状态（loading / query / feeds）全部在 [RecommendViewModel] 里，
 * 这里只读状态、抛事件，所以页面销毁重组后状态不丢失。
 */
@Composable
fun RecommendScreen(viewModel: RecommendViewModel) {
    val loading = viewModel.loading
    val query = viewModel.query
    val feeds = viewModel.feeds

    // ===== derivedStateOf —— 只有 query 或 feeds 变化才重新过滤 =====
    // 对照实验：写成 val filtered = feeds.filter {...}（无 remember），
    // 在 filter 里加 println，点其他区域触发无关重组，观察日志刷屏
    val filtered by remember {
        derivedStateOf {
            feeds.filter { it.title.contains(query, ignoreCase = true) }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.query = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("搜索推荐内容") },
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items = filtered, key = { it.id }) { feed ->
                    FeedCard(
                        feed,
                        onLike = viewModel::toggleLike,
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}

@Composable
fun FeedCard(item: FeedItem, onLike: (FeedItem) -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleSmall)
                Text(item.author, style = MaterialTheme.typography.bodySmall)
            }

            TextButton(onClick = { onLike(item) }) {
                Text(if (item.liked) "已赞 ❤" else "赞")
            }
        }
    }
}
