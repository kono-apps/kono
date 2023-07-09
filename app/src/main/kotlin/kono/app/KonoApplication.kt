package kono.app

import com.squareup.moshi.Moshi
import kono.asset.AssetHandler
import kono.ipc.FunctionContext
import kono.ipc.IpcHandler
import kono.runtime.webview.NativeWebView
import kono.runtime.webview.webView
import kono.runtime.window.EventLoop
import kono.runtime.window.NativeWindow
import kono.runtime.window.window

/**
 * Represents the currently running app
 */
private lateinit var app: KonoApplication

/**
 * Returns the currently running app. This will throw an error
 * if no app has been created yet
 */
fun currentRunningApp() = app

/**
 * Represents a Kono application.
 */
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
        var nativeWebView: NativeWebView? = null
        val eventLoop: EventLoop?
        val nativeWindow: NativeWindow?

        eventLoop = EventLoop()
        nativeWindow = window(eventLoop) {
            title(config.window.title)
            fullScreen(config.window.fullScreen)
            resizable(config.window.resizable)
            maximized(config.window.maximized)
            closable(config.window.closable)
            size(width = config.window.width, height = config.window.height)
        }
        nativeWebView = webView(nativeWindow) {
            url("kono://localhost/")
            addCustomProtocol("kono") { path ->
                assets.loadEmbeddedAsset(path)
            }
            addIpcHandler { request ->
                val context = FunctionContext(
                    nativeWebView = nativeWebView!!,
                    nativeWindow = nativeWindow,
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
