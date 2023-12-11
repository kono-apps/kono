package kono.runtime.window

import kono.runtime.natives.EventReceiverPtr
import kono.runtime.natives.nativeRuntime

/**
 * A utility class that receives native events from the [EventLoop]
 */
class EventReceiver {

    /**
     * The internal pointer to the event receiver
     */
    internal val ptr: EventReceiverPtr = nativeRuntime { createEventReceiver() }

    fun onInit(init: () -> Unit) {
        nativeRuntime {
            ptr.setInit(init)
        }
    }

}