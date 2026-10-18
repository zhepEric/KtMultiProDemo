package com.example.ktmultiprodemo.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class TabItem(
    val route: String,
    val label: String,
    val icon: ImageVector
){
    data object RecommendTab: TabItem(route = "recommend", label = "推荐", icon = Icons.Outlined.Home)
    data object EffectsTab: TabItem(route = "effects", label = "练习", icon = Icons.Outlined.Build)
    data object MineTab: TabItem(route = "mine", label = "我的", icon = Icons.Outlined.Person)


    companion object{
        val tabList = listOf(RecommendTab, EffectsTab, MineTab)
    }
}


