package kono.runtime.window

import kono.runtime.natives.NativeRuntime
import kono.runtime.natives.WindowBuilderPtr
import kono.runtime.natives.nativeRuntime

/**
 * Creates a new Window to be run on the event loop
 */
fun buildWindow(eventLoop: EventLoop, block: WindowBuilder.() -> Unit): NativeWindow {
    val builder = WindowBuilder()
    builder.block()
    return builder.build(eventLoop)
}

/**
 * A window builder. This should only be created from [buildWindow]
 * as it properly manages the internal pointers.
 *
 * Implementation note: Calling [WindowBuilder.build] invalidates
 * the [WindowBuilder.builderPtr]. Any further access to it can lead
 * to errors, therefore a WindowBuilder should not be re-used.
 */
class WindowBuilder internal constructor() {

    private var builderPtr: WindowBuilderPtr = nativeRuntime.createWindowBuilder()
    private var consumed = false

    private fun update(action: context(NativeRuntime) WindowBuilderPtr.() -> WindowBuilderPtr): WindowBuilder {
        if (consumed)
            error("This builder has been consumed! Create a new one instead.")
        nativeRuntime { builderPtr = action(builderPtr) }
        return this
    }

    /**
     * Sets the title of the window
     */
    fun title(title: String) = update {
        windowBuilderSetTitle(title)
    }

    /**
     * Sets whether the window is full screen or not. The window
     * will become borderless
     */
    fun fullScreen(fullScreen: Boolean) = update {
        windowBuilderSetFullScreen(fullScreen)
    }

    /**
     * Sets the size of the window in physical pixel units
     */
    fun size(width: Int, height: Int) = update {
        windowBuilderSetSize(width, height)
    }

    /**
     * Sets whether the window should be maximized or not
     */
    fun maximized(maximized: Boolean) = update {
        windowBuilderSetMaximized(maximized)
    }

    /**
     * Sets whether the window can be resized or not
     */
    fun resizable(resizable: Boolean) = update {
        windowBuilderSetResizable(resizable)
    }

    /**
     * Sets whether the window is maximizable or not
     */
    fun maximizable(maximizable: Boolean) = update {
        windowBuilderSetMaximizable(maximizable)
    }

    /**
     * Sets whether the window is closable or not.
     */
    fun closable(closable: Boolean) = update {
        windowBuilderSetClosable(closable)
    }

    /**
     * Sets the webview theme
     */
    fun theme(theme: WindowTheme) = update {
        windowBuilderSetTheme(theme.ordinal.toByte())
    }

    /**
     * Constructs a [NativeWindow] from this builder.
     *
     * Important note: This will invalidate the builder pointer
     */
    fun build(eventLoop: EventLoop): NativeWindow {
        if (consumed)
            error("You cannot re-use the same builder twice!")
        consumed = true
        val windowPtr = with(builderPtr) {
            nativeRuntime {
                windowBuild(eventLoop.ptr)
            }
        }
        return NativeWindow(windowPtr)
    }

}