package com.example.ktmultiprodemo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KtMultiProDemo",
    ) {
        App()
    }
}