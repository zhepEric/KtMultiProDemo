package com.example.ktmultiprodemo

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "${sayHello(platform.name)}\nDevice: ${getDeviceName()}"
    }
}