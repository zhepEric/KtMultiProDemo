package com.example.ktmultiprodemo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * ===== 副作用（Side Effect）练习页 =====
 *
 * 核心问题：Composable 函数应该是"纯"的——同样状态画同样界面。
 * 但计时、网络、订阅这些"不纯"的操作放哪？答案是各种 Effect API，
 * 它们保证：在正确的时机启动、在离开组合时自动清理。
 *
 * 注意：本页故意用 remember 不提升状态，切换 Tab 页面销毁后状态重置，
 * 正好方便观察 DisposableEffect 的 onDispose 触发时机。
 */
@Composable
fun EffectsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("副作用练习", style = MaterialTheme.typography.titleLarge)
        LaunchedEffectDemo()
        DisposableEffectDemo()
        ProduceStateDemo()
        RememberCoroutineScopeDemo()
        LifecycleVsDisposableDemo()
    }
}

// =====================================================================
// 实验 1：LaunchedEffect —— "进入组合 / key 变化时"启动协程
// =====================================================================
@Composable
fun LaunchedEffectDemo() {
    var seconds by remember { mutableStateOf(0) }
    var restartKey by remember { mutableStateOf(0) }
    var running by remember { mutableStateOf(true) }

    // 知识点：
    // - key 变化 → 旧协程【取消】，新协程重新启动（点"重置"观察 seconds 清零重来）
    // - 页面离开组合（切 Tab）→ 协程自动取消，不会泄漏
    // 对照实验：把计时逻辑写到 Button 的 onClick 里用 scope.launch 启动，
    //           连点几下按钮，会跑出多个并发的计时协程（seconds 疯涨）
    LaunchedEffect(restartKey, running) {
        if (running) {
            while (true) {
                delay(1000)
                seconds++
            }
        }
    }

    DemoCard(title = "1️⃣ LaunchedEffect：计时器") {
        Text("已运行 $seconds 秒（${if (running) "计时中" else "已暂停"}）")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = {
                seconds = 0
                restartKey++ // 改 key 触发重启
            }) { Text("重置") }
            TextButton(onClick = { running = !running }) {
                Text(if (running) "暂停" else "继续")
            }
        }
    }
}

// =====================================================================
// 实验 2：DisposableEffect —— "需要清理的订阅"用它
// =====================================================================

/** 假想的埋点 SDK：典型场景——注册后必须反注册，否则泄漏 */
private object FakeTracker {
    val logs = mutableStateListOf<String>()
    // 同时打到控制台：Android 上 Logcat 过滤 package:mine 或搜索 "EffectLab" 可见
    //（println 在 Logcat 里的 tag 是 System.out，所以按 tag:Disposable 过滤是搜不到的）
    fun start(name: String) {
        logs.add(0, "📍 onStart: $name")
        println("EffectLab onStart: $name")
    }
    fun stop(name: String) {
        logs.add(0, "🛑 onStop: $name")
        println("EffectLab onStop: $name")
    }
}

@Composable
fun DisposableEffectDemo() {
    var visible by remember { mutableStateOf(true) }

    DemoCard(title = "2️⃣ DisposableEffect：埋点注册/反注册") {
        TextButton(onClick = { visible = !visible }) {
            Text(if (visible) "隐藏卡片（触发 onDispose）" else "显示卡片（触发注册）")
        }

        if (visible) {
            // 知识点：
            // - 进入组合时执行主体（注册监听）
            // - 离开组合时执行 onDispose（反注册），保证成对出现、不泄漏
            // - 触发时机：visible 变 false、切换 Tab、页面销毁
            // 对照实验：去掉 onDispose 里的 stop，反复显示/隐藏，
            //           观察日志里 onStart 次数多于 onStop —— 这就是泄漏
            DisposableEffect(Unit) {
                FakeTracker.start("练习卡片")
                onDispose { FakeTracker.stop("练习卡片") }
            }
            Text("卡片可见中，已注册埋点 ✅")
        }

        Spacer(Modifier.height(8.dp))
        Text("日志（最新在上）：", style = MaterialTheme.typography.labelMedium)
        FakeTracker.logs.take(6).forEach { log ->
            Text(log, style = MaterialTheme.typography.bodySmall)
        }
    }
}

// =====================================================================
// 实验 3：produceState —— 把"挂起的数据生产"桥接成 Compose State
// =====================================================================

/** 模拟行情推送：一个会持续产出数据的挂起数据源 */
private suspend fun emitPrices(onPrice: (Double) -> Unit) {
    var price = 100.0
    while (true) {
        delay(1000)
        price += Random.nextDouble(-1.5, 1.5)
        onPrice(price)
    }
}

@Composable
fun ProduceStateDemo() {
    // 知识点：
    // - produceState = "自带初始值 + 挂起生产 + 离开组合自动取消"的 State 工厂
    // - 在块里给 value 赋值即触发 UI 更新
    // - 适合把 Flow 之外的挂起数据源（回调、while 轮询）接进 Compose
    // 对照实验：不用 produceState 就得自己写
    //   var price by remember { ... } + LaunchedEffect { emitPrices { price = it } }
    //   功能等价，但初始值、取消逻辑都要自己管
    val priceText: State<String> = produceState(initialValue = "加载中…") {
        emitPrices { p -> value = "¥ ${(p * 100).toInt() / 100.0}" }
    }

    DemoCard(title = "3️⃣ produceState：模拟行情推送") {
        Text("当前价格：${priceText.value}", style = MaterialTheme.typography.titleMedium)
        Text("每秒推送一次；切走页面后推送自动停止", style = MaterialTheme.typography.bodySmall)
    }
}

// =====================================================================
// 加餐：rememberCoroutineScope —— "用户事件触发"的协程
// =====================================================================
@Composable
fun RememberCoroutineScopeDemo() {
    val scope = rememberCoroutineScope()
    var message by remember { mutableStateOf("点按钮，3 秒后收到回调") }

    DemoCard(title = "4️⃣ rememberCoroutineScope：点击触发的延时任务") {
        Text(message)
        Button(onClick = {
            message = "等待中…"
            // 知识点：
            // - LaunchedEffect 是"进入组合时启动"；这里是"用户点击时启动"
            // - scope 同样随组合销毁自动取消，不会在页面销毁后还改状态（避免崩溃）
            scope.launch {
                delay(3000)
                message = "✅ 3 秒到了，回调完成"
            }
        }) { Text("启动 3 秒任务") }
    }
}

// =====================================================================
// 实验 5：DisposableEffect vs LifecycleEventEffect —— 组合生命周期 vs 应用生命周期
// =====================================================================

/** 本实验专用日志（带序号，方便对比触发顺序；同步输出到控制台/Logcat） */
private object EffectCompareLogger {
    val logs = mutableStateListOf<String>()
    private var seq = 0
    fun log(tag: String, event: String) {
        seq++
        val line = "#$seq  [$tag] $event"
        logs.add(0, line)
        println("EffectLab $line") // Logcat 过滤 package:mine 或搜索 "EffectLab"
    }
    fun clear() = logs.clear()
}

@Composable
fun LifecycleVsDisposableDemo() {
    var cardVisible by remember { mutableStateOf(true) }

    // B. LifecycleEventEffect：锚定【LocalLifecycleOwner 的生命周期】
    // 注意作用域：这里处在 NavHost 页面里，LocalLifecycleOwner 是该页面的
    // NavBackStackEntry（Navigation 给每个页面配了独立生命周期），
    // 所以它不仅响应 App 前后台，切 Tab 被覆盖时同样会收到 ON_STOP。
    // 首次进入页面看不到 🟢：ON_START 在页面显示之前早已发生，只响应之后的新事件。
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        EffectCompareLogger.log("Lifecycle", "🟢 ON_START（可见/回前台）")
    }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        EffectCompareLogger.log("Lifecycle", "🔴 ON_STOP（不可见/退后台）")
    }

    DemoCard(title = "5️⃣ DisposableEffect vs Lifecycle 事件") {
        TextButton(onClick = { cardVisible = !cardVisible }) {
            Text(if (cardVisible) "隐藏子卡片（纯组合变化）" else "显示子卡片")
        }

        if (cardVisible) {
            // A. DisposableEffect：锚定【组合】——Composable 在不在组合树里。
            // 放在 if 里：点按钮即可制造"纯组合变化"，与生命周期事件完全隔离
            DisposableEffect(Unit) {
                EffectCompareLogger.log("Disposable", "▶️ 进入组合（注册）")
                onDispose { EffectCompareLogger.log("Disposable", "⏹️ 离开组合（解注册）") }
            }
            Text("子卡片可见中 ✅")
        }

        Text(
            "操作 A：点上方按钮隐藏子卡片 → 只有 ⏹️（组合变了，生命周期无感）\n" +
                "操作 B：按 Home 键退后台再回来 → 只有 🔴🟢（组合没销毁）\n" +
                "操作 C：切 Tab 再切回 → ⏹️▶️ 和 🔴🟢 都会出现：页面出了组合，" +
                "且 Navigation 的页面级生命周期也会 STOP→START\n" +
                "结论：组合生命周期与 Lifecycle 事件是两套维度，按场景选用",
            style = MaterialTheme.typography.bodySmall
        )
        TextButton(onClick = { EffectCompareLogger.clear() }) { Text("清空日志") }
        EffectCompareLogger.logs.take(10).forEach { log ->
            Text(log, style = MaterialTheme.typography.bodySmall)
        }
    }
}

/** 统一的实验卡片容器 */
@Composable
private fun DemoCard(title: String, content: @Composable () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            content()
        }
    }
}
