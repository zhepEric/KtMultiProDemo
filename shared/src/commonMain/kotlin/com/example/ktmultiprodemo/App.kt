package com.example.ktmultiprodemo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ktmultiprodemo.navigation.TabItem
import com.example.ktmultiprodemo.ui.EffectsScreen
import com.example.ktmultiprodemo.ui.ProfileScreen
import com.example.ktmultiprodemo.ui.RecommendScreen
import com.example.ktmultiprodemo.ui.RecommendViewModel
import org.jetbrains.compose.resources.painterResource

import ktmultiprodemo.shared.generated.resources.Res
import ktmultiprodemo.shared.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute by remember {
        derivedStateOf { backStackEntry?.destination?.route }
    }

    // ViewModel 挂到 App 级 ViewModelStoreOwner（Activity 级），
    // 生命周期覆盖整个页面切换过程，页面销毁重组不影响其中的状态
    val recommendViewModel: RecommendViewModel = viewModel { RecommendViewModel() }

    MaterialTheme {
//        var showContent by remember { mutableStateOf(false) }
//        Column(
//            modifier = Modifier
//                .background(MaterialTheme.colorScheme.primaryContainer)
//                .safeContentPadding()
//                .fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//        ) {
//            Button(onClick = { showContent = !showContent }) {
//                Text("Click me!")
//            }
//            AnimatedVisibility(showContent) {
//                val greeting = remember { Greeting().greet() }
//                Column(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                ) {
//                    Image(painterResource(Res.drawable.compose_multiplatform), null)
//                    Text("Compose: $greeting")
//                }
//            }
//        }

        Scaffold(bottomBar = {
            AppBottomBar(currentRoute = currentRoute, onTabSelected = {tabItem ->
                // 底部导航标准写法：
                // popUpTo + saveState —— 切走时保存当前 Tab 的返回栈状态，而不是直接丢弃
                // launchSingleTop —— 避免重复点击同一个 Tab 时叠加多个相同页面
                // restoreState —— 切回时恢复之前保存的状态（如滚动位置）
                navController.navigate(tabItem.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            })
        }) { innerPadding ->

            NavHost(navController = navController,
                startDestination = TabItem.RecommendTab.route,
                modifier = Modifier.padding(innerPadding)) {

                composable(TabItem.RecommendTab.route) {
                    RecommendScreen(recommendViewModel)
                }

                composable(TabItem.EffectsTab.route) {
                    EffectsScreen()
                }

                composable(TabItem.MineTab.route) {
                    ProfileScreen()
                }
            }

        }
    }

}

@Composable
fun AppBottomBar(currentRoute: String?, onTabSelected:(TabItem) -> Unit) {

    NavigationBar {
        TabItem.tabList.forEach { tabItem ->
            NavigationBarItem(selected = currentRoute == tabItem.route,
                onClick = {
                    onTabSelected(tabItem)
                },
                icon = {
                    Icon(imageVector = tabItem.icon, contentDescription = null)
                },
                label = {
                    Text(tabItem.label)
                })
        }
    }
}