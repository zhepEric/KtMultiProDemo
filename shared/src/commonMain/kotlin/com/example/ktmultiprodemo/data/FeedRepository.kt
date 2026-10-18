package com.example.ktmultiprodemo.data

import com.example.ktmultiprodemo.ui.FeedItem
import kotlinx.coroutines.delay

/**
 * 数据层：负责"数据从哪来"（现在是假数据，以后换成 Ktor 网络请求时
 * 只需要改这里，ViewModel 和 UI 都不用动）。
 *
 * 这就是依赖注入的收益点：ViewModel 只声明"我要一个 FeedRepository"，
 * 不关心它是假数据实现还是网络实现。
 */
class FeedRepository {

    /** 模拟网络请求：延迟后返回 20 条数据 */
    suspend fun fetchFeeds(): List<FeedItem> {
        delay(1500)
        return (1..20).map {
            FeedItem(id = it, title = "Kotlin 多平台实战 #$it", author = "作者 ${it % 5}")
        }
    }
}
