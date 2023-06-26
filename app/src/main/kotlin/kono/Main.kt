package kono

import kono.export.ExportFunction
import kono.fns.FunctionHandler
import kono.runtime.EventLoop
import kono.webview.webView
import kono.window.window
import org.intellij.lang.annotations.Language

@ExportFunction(name = "HELLO")
fun reverse(value: String): String {
    return value.reversed()
}

@Language("JSON")
const val json = """{
  "fn": "reverse",
  "p": [
  ],
  "d": {
  }
}"""

fun main() {
    val eventLoop = EventLoop()
    val window = window(eventLoop) {
        title("HELLO Hello!")
    }
    val webView = webView(window) {
        html("<p>Hello, Kono!</p>")
    }
    eventLoop.run {
        println("Window created!")
    }
}
