package kono.webview.test

import kono.runtime.EventLoop
import kono.webview.Asset
import kono.webview.webView
import kono.window.window
import org.intellij.lang.annotations.Language

@Language("HTML")
val HTML = """
    <html lang="en">
    <head>
        <title>Hello!!!</title>
    </head>
    <body>
    <button onclick='window.ipc.postMessage("HELLO!!!")'>Click me!!</button>
    </html>
""".trimIndent()

val Assets = mapOf(
    "/" to Asset(
        "ztext/html",
        HTML
    )
)

fun main() {
    val eventLoop = EventLoop()
    val window = window(eventLoop) {
        title("Hello")
        size(width = 800, height = 800)
    }
    webView(window) {
        url("kono://localhost")
        addCustomProtocol("kono") {
            Assets["/"]!!
        }
        addIpcHandler { message ->
            println(message)
        }
        devTools(true)
        addInitializationScript("console.log('HHello!!')")
    }
    eventLoop.run {
        println("WebView opened!")
    }
    println("A")
}