package com.example.ktmultiprodemo

import kotlin.js.ExperimentalWasmJsInterop

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun(
    """() => {
    const ua = globalThis.navigator ? globalThis.navigator.userAgent : '';
    const match = ua.match(/\(([^)]+)\)/);
    return match ? match[1] : 'Unknown';
}"""
)
private external fun detectDeviceName(): String

actual fun getDeviceName(): String = detectDeviceName()