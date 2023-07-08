package kono.app

import com.squareup.moshi.Moshi
import kono.asset.AssetHandler
import kono.ipc.FunctionContext
import kono.ipc.IpcHandler
import kono.webview.WebView
import kono.webview.webView
import kono.window.EventLoop
import kono.window.Window
import kono.window.window

private lateinit var app: KonoApplication

fun currentRunningApp() = app

class KonoApplication(
    context: KonoApplicationContext,
    private val config: KonoConfig,
    val moshi: Moshi,
) {

    init {
        if (::app.isInitialized)
            error("App has been initialized twice!")
        app = this
    }

    val events = context.createEventHandler(this)
    val functions = context.createFunctionHandler(this)
    private val assets = AssetHandler(landingAsset = config.build.landingAsset)
    private val ipcHandler = IpcHandler(this)

    fun start() {
        var webView: WebView? = null
        val eventLoop: EventLoop?
        val window: Window?

        eventLoop = EventLoop()
        window = window(eventLoop) {
            title(config.window.title)
            fullScreen(config.window.fullScreen)
            resizable(config.window.resizable)
            maximized(config.window.maximized)
            closable(config.window.closable)
            size(width = config.window.width, height = config.window.height)
        }
        webView = webView(window) {
            url("kono://localhost/")
            addCustomProtocol("kono") { path ->
                assets.loadEmbeddedAsset(path)
            }
            addIpcHandler { request ->
                val context = FunctionContext(
                    webView = webView!!,
                    window = window,
                    app = this@KonoApplication,
                    eventLoop = eventLoop,
                )
                ipcHandler.handle(request, context)
            }
            devTools(config.app.debug)
        }
        eventLoop.run {
            println("Window created!")
        }
    }
}
