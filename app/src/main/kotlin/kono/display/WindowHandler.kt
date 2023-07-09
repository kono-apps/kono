package kono.display

import java.util.concurrent.atomic.AtomicInteger

class WindowHandler {

    private val counter = AtomicInteger()
    private val windows = mutableMapOf<Int, WebViewWindow>()

    fun createWindow(window: WebViewWindow) {
        windows[counter.getAndIncrement()] = window
    }

}