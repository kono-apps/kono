package kono.runtime

import com.sun.jna.Native
import kono.runtime.natives.NativeRuntime

inline fun <R> nativeRuntime(call: NativeRuntime.() -> R): R {
    return nativeRuntime.call()
}

val nativeRuntime: NativeRuntime by lazy {
    Native.load("kono_runtime_native", NativeRuntime::class.java)
        ?: error("Failed to load 'kono_runtime_native' for the current platform")
}
