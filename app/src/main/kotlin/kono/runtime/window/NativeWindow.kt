package kono.runtime.window

import kono.runtime.natives.WindowPtr

/**
 * Represents a window that is controlled by an [EventLoop].
 */
class NativeWindow internal constructor(internal val ptr: WindowPtr)