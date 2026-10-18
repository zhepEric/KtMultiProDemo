package com.example.ktmultiprodemo.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * JSONPlaceholder 返回的文章结构。
 * @Serializable 由 kotlinx.serialization 编译插件生成解析代码，全平台可用。
 */
@Serializable
data class PostDto(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String,
)

/**
 * HttpClient 工厂：引擎不用手写——Ktor 会自动使用各平台 classpath 上
 * 唯一的引擎实现（Android/Desktop=OkHttp，iOS=Darwin，Web=JS）。
 */
fun createHttpClient(): HttpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true // 服务端多返回字段时不炸
        })
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 15_000
    }
}

/**
 * API 层：只负责"怎么调接口、拿到什么 DTO"，
 * 不知道 DTO 会被用来做什么（那是 Repository 的事）。
 */
class FeedApi(private val client: HttpClient) {

    /** 真实免费 API：https://jsonplaceholder.typicode.com */
    suspend fun fetchPosts(): List<PostDto> =
        client.get("https://jsonplaceholder.typicode.com/posts").body()
}
