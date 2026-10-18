package com.example.ktmultiprodemo.data

import com.example.ktmultiprodemo.ui.FeedItem

/**
 * 仓库层接口：上层（ViewModel）只面向接口编程。
 *
 * 这就是 Koin 接口绑定的价值点——AppModule 里一行切换实现：
 *   single<FeedRepository> { RemoteFeedRepository(get()) }  // 真实网络
 *   single<FeedRepository> { FakeFeedRepository() }         // 离线调试/测试
 * ViewModel 和 UI 完全不用改。
 */
interface FeedRepository {
    suspend fun fetchFeeds(): List<FeedItem>
}

/** 远程实现：Ktor 调真实 API，DTO → 业务模型 FeedItem 的转换在这里完成 */
class RemoteFeedRepository(private val api: FeedApi) : FeedRepository {
    override suspend fun fetchFeeds(): List<FeedItem> =
        api.fetchPosts().take(20).map { post ->
            FeedItem(
                id = post.id,
                // 接口返回的是英文假文，取前 40 字符让列表好看一点
                title = post.title.take(40),
                author = "用户 ${post.userId}",
            )
        }
}

/** 离线实现：没网/写测试时用，保留原来的假数据行为 */
class FakeFeedRepository : FeedRepository {
    override suspend fun fetchFeeds(): List<FeedItem> {
        kotlinx.coroutines.delay(1500)
        return (1..20).map {
            FeedItem(id = it, title = "Kotlin 多平台实战 #$it", author = "作者 ${it % 5}")
        }
    }
}
