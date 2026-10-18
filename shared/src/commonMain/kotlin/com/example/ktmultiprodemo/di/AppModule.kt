package com.example.ktmultiprodemo.di

import com.example.ktmultiprodemo.data.FeedApi
import com.example.ktmultiprodemo.data.FeedRepository
import com.example.ktmultiprodemo.data.RemoteFeedRepository
import com.example.ktmultiprodemo.data.createHttpClient
import com.example.ktmultiprodemo.ui.RecommendViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin 进阶用法 1：按层拆分 module。
 * 网络层和业务层分开声明，App 启动时 modules(appModule, networkModule) 一起装载；
 * 层与层之间的依赖（Repository → FeedApi → HttpClient）靠 get() 自动串起来。
 */
val networkModule = module {
    single { createHttpClient() }   // HttpClient 全局单例（底层连接池复用）
    single { FeedApi(get()) }
}

/**
 * Koin 进阶用法 2：接口绑定 single<接口> { 实现 }。
 * 想切回离线假数据，把下行改成 single<FeedRepository> { FakeFeedRepository() } 即可，
 * ViewModel、UI 一行都不用动——这就是"面向接口编程 + DI"的组合拳。
 */
val appModule = module {
    single<FeedRepository> { RemoteFeedRepository(get()) }
    viewModel { RecommendViewModel(get()) }
}
