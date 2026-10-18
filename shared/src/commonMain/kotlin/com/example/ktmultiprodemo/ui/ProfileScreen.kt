package com.example.ktmultiprodemo.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ktmultiprodemo.Greeting
import ktmultiprodemo.shared.generated.resources.Res
import ktmultiprodemo.shared.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProfileScreen() {
    // 练习点 1 对照实验 A：remember —— 旋转屏幕后数字归零
    var normalCount by remember { mutableStateOf(0) }
    // 练习点 1 对照实验 B：rememberSaveable —— 旋转屏幕后数字保留
    var savedCount by rememberSaveable { mutableStateOf(0) }

    // 原来的 Greeting 演示挪到这里（showContent 状态也随导航自然提升到了本页）
    var showContent by remember { mutableStateOf(false) }
    val greeting = remember { Greeting().greet() }

    Column(
        modifier = Modifier.fillMaxSize().safeContentPadding().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(greeting, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { normalCount++ }) {
                Text("normalCount $normalCount")
            }
            Button(onClick = { savedCount++ }) {
                Text("savedCount:$savedCount")
            }

        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = { showContent = !showContent }) {
            Text("Click me!")
        }
        AnimatedVisibility(showContent) {
            val greeting = remember { Greeting().greet() }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(painterResource(Res.drawable.compose_multiplatform), null)
                Text("Compose: $greeting")
            }
        }


    }
}