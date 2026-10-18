package com.example.ktmultiprodemo.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktmultiprodemo.data.FeedRepository
import kotlinx.coroutines.launch

data class FeedItem(
    val id: Int,
    val title: String,
    val author: String,
    val liked: Boolean = false,
)

/**
 * ===== 状态提升 + 依赖注入 =====
 *
 * 状态在 ViewModel 里：生命周期跟随 ViewModelStoreOwner（见 App.kt），
 * 切换 Tab 页面销毁重组不影响它，点赞、搜索词、加载状态都保留。
 *
 * 依赖由 Koin 注入：ViewModel 只声明"我要一个 FeedRepository"，
 * 不关心它怎么 new 出来的（见 di/AppModule.kt）。以后把假数据
 * 换成网络实现时，这里一行都不用改。
 *
 * 分工约定：
 * - Repository：数据从哪来（假数据/网络/数据库）
 * - ViewModel：业务状态 + 业务逻辑（加载、点赞）
 * - Screen：纯 UI，读状态、抛事件
 */
class RecommendViewModel(
    private val repository: FeedRepository,
) : ViewModel() {

    /** 加载状态 */
    var loading by mutableStateOf(true)
        private set

    /** 网络错误信息：null 表示正常；真实网络请求必须有错误态 */
    var error by mutableStateOf<String?>(null)
        private set

    /** 搜索词：替代原来的 rememberSaveable，生命周期比页面更长 */
    var query by mutableStateOf("")

    /** 可变状态列表必须用 snapshotStateList 系，直接 MutableList.add() 不触发重组 */
    val feeds = mutableStateListOf<FeedItem>()

    init {
        loadFeeds()
    }

    /** 拉取数据：首次进入和点"重试"都会走这里 */
    fun loadFeeds() {
        viewModelScope.launch {
            loading = true
            error = null
            try {
                val result = repository.fetchFeeds()
                feeds.clear()
                feeds.addAll(result)
            } catch (e: Exception) {
                // 真实网络场景：超时、无网、JSON 解析失败都会到这里
                error = "加载失败：${e.message ?: e::class.simpleName}"
            } finally {
                loading = false
            }
        }
    }

    fun toggleLike(item: FeedItem) {
        val index = feeds.indexOfFirst { it.id == item.id }
        // 顺手修了一个 bug：原来是 index > 0，导致第 1 条永远点不了赞
        if (index >= 0) {
            feeds[index] = feeds[index].copy(liked = !feeds[index].liked)
        }
    }
}

