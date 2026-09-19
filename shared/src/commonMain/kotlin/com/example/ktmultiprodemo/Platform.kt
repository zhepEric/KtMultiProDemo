package com.example.ktmultiprodemo

interface Platform {
    val name: String
}

/**
 * 定义一个期望（expect）声明，告诉编译器“所有平台都必须提供这个功能”
 */
expect fun getPlatform(): Platform

expect fun getDeviceName(): String