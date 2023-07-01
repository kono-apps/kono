package kono.webview.test

import kono.asset.MimeType
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
    <button onclick='window.ipc.postMessage("Hello!")'>Click me!!</button>
    </html>
""".trimIndent().encodeToByteArray()

val Assets = mapOf(
    "/" to Asset(MimeType.HTML) { HTML }
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
        addInitializationScript("console.log('Hello!!')")
    }
    eventLoop.run {
        println("WebView opened!")
    }
}