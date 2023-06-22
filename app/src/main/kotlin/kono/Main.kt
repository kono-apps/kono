package kono

import kono.runtime.EventLoop
import kono.webview.webView
import kono.window.window

fun main() {
    val eventLoop = EventLoop()
    val window = window(eventLoop) {
        title("Hello!")
    }
    val webView = webView(window) {
        html("<p>Hello, Kono!</p>")
    }
    eventLoop.run {
        println("Window created!")
    }
}
