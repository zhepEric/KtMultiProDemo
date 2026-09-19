package com.example.ktmultiprodemo

import web.navigator.navigator

private val userAgent = navigator.userAgent

class JsPlatform: Platform {
    private val browserList = listOf("Chrome", "Firefox", "Safari", "Edge")

    override val name: String = userAgent.findAnyOf(browserList, ignoreCase = true)
            ?.let { (startIndex) -> userAgent.substring(startIndex).substringBefore(" ") }
            ?: "Unknown"
}

actual fun getPlatform(): Platform = JsPlatform()

private val osList = listOf("Windows NT", "Mac OS X", "Android", "iPhone", "Linux")

actual fun getDeviceName(): String =
    userAgent.findAnyOf(osList, ignoreCase = true)
        ?.let { (startIndex) -> userAgent.substring(startIndex).substringBefore(";").substringBefore(" ") }
        ?: "Unknown"

