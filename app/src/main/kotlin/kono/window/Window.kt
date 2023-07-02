package kono.window

import kono.runtime.natives.WindowPtr

/**
 * Represents a window that is controlled by an [EventLoop].
 */
class Window internal constructor(internal val ptr: WindowPtr)