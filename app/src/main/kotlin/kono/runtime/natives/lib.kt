package kono.runtime.natives

import com.sun.jna.Native

inline fun <R> nativeRuntime(call: NativeRuntime.() -> R): R {
    return nativeRuntime.call()
}

val nativeRuntime: NativeRuntime by lazy {
    Native.load("kono_runtime_native", NativeRuntime::class.java)
        ?: error("Failed to load 'kono_runtime_native' for the current platform")
}
