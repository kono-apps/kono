package kono

import kono.export.ExportFunction
import kono.generated.GeneratedAppContext
import kono.runtime.EventLoop
import kono.webview.webView
import kono.window.window

@ExportFunction(name = "HELLO")
fun reverse(value: String): String {
    return value.reversed()
}

fun main() {
    val eventLoop = EventLoop()
    val window = window(eventLoop) {
        title("Hello!")
    }
    webView(window) {
        url("kono://localhost/")
        addCustomProtocol("kono") { path ->
            if (path == "/")
                GeneratedAppContext.assets.getAsset("/index.html")
            else
                GeneratedAppContext.assets.getAsset(path)
        }
        devTools(true)
    }
    eventLoop.run {
        println("Window created!")
    }
}
