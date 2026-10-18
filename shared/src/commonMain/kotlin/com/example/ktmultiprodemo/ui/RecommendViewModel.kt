package com.example.ktmultiprodemo.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class FeedItem(
    val id: Int,
    val title: String,
    val author: String,
    val liked: Boolean = false,
)

/** 假数据源，模拟网络返回的 20 条数据 */
private val fakeFeeds = (1..20).map {
    FeedItem(id = it, title = "Kotlin 多平台实战 #$it", author = "作者 ${it % 5}")
}

/**
 * ===== 练习点：状态提升（State Hoisting）到 ViewModel =====
 *
 * 问题复盘：之前点赞状态放在 RecommendScreen 的 remember 里，
 * remember 只在"组合存在期间"存活。切换 Tab 时 NavHost 会销毁
 * 推荐页组合，remember 状态随之销毁，切回来等于从零开始。
 *
 * 解决：把状态放进 ViewModel。ViewModel 的生命周期跟随
 * ViewModelStoreOwner（这里是 Activity 级，见 App.kt），
 * 页面销毁重组不影响它，所以点赞、搜索词、加载状态都保留。
 *
 * 分工约定：
 * - ViewModel：业务状态 + 业务逻辑（加载、点赞），不知道 Composable 的存在
 * - Screen：纯 UI，读 ViewModel 的状态，把用户事件回调给 ViewModel
 */
class RecommendViewModel : ViewModel() {

    /** 加载状态：只在 ViewModel 创建时加载一次，切 Tab 不会重新触发 */
    var loading by mutableStateOf(true)
        private set

    /** 搜索词：替代原来的 rememberSaveable，生命周期比页面更长 */
    var query by mutableStateOf("")

    /** 可变状态列表必须用 snapshotStateList 系，直接 MutableList.add() 不触发重组 */
    val feeds = mutableStateListOf<FeedItem>()

    init {
        // 模拟首次请求接口：init 只在 ViewModel 首次创建时执行一次
        viewModelScope.launch {
            delay(1500)
            feeds.addAll(fakeFeeds)
            loading = false
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
