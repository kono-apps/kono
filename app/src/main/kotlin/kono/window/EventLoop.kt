package kono.window

import kono.runtime.natives.EventLoopPtr
import kono.runtime.natives.nativeRuntime

/**
 * Represents the context in which windows are running. Multiple
 * windows may exist on the same event loop.
 */
class EventLoop {

    /**
     * The internal pointer to the event loop
     */
    internal val ptr: EventLoopPtr = nativeRuntime { createEventLoop() }

    /**
     * Runs the event loop. This method blocks the thread
     */
    fun run(onInit: () -> Unit = {}) {
        nativeRuntime {
            ptr.eventLoopRun {
                runCatching(onInit).recover { it.printStackTrace() }
            }
        }
    }
}