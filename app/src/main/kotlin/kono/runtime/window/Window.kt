package kono.runtime.window

import kono.app.WindowConfig
import kono.runtime.natives.WindowPtr

/**
 * Represents a window that is controlled by an [EventLoop].
 */
class Window internal constructor(internal val ptr: WindowPtr)

/**
 * Creates a [Window] based on the configuration of this [WindowConfig]
 */
fun WindowConfig.toWindow(eventLoop: EventLoop = EventLoop()): Window {
    return buildWindow(eventLoop) {
        title(title)
        closable(closable)
        if (fullScreen)
            fullScreen(true)
        if (maximized)
            maximized(true)
        if (resizable)
            resizable(true)
        size(width, height)
        if (theme != null)
            theme(theme)
    }
}
