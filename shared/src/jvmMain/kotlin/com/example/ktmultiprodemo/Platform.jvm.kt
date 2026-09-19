package com.example.ktmultiprodemo

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

actual fun getDeviceName(): String =
    "${System.getProperty("os.name")} ${System.getProperty("os.version")} (${System.getProperty("os.arch")})"