package com.example.ktmultiprodemo.di

import com.example.ktmultiprodemo.data.FeedRepository
import com.example.ktmultiprodemo.ui.RecommendViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin 模块：声明"谁可以被注入、怎么创建"。
 *
 * - single { }：全局单例，全应用共享同一个实例（适合 Repository、网络客户端）
 * - viewModel { }：声明 ViewModel 的构造方式，配合 koinViewModel() 取用
 * - get()：从容器里自动解析构造参数需要的依赖，
 *   比如 RecommendViewModel(get()) 里的 get() 会自动拿到上面的 FeedRepository
 *
 * 新增依赖时只需要在这里登记，调用处完全不用改。
 */
val appModule = module {
    single { FeedRepository() }
    viewModel { RecommendViewModel(get()) }
}
