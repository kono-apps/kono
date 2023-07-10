package kono.display

import kono.app.KonoApplication
import kono.asset.AssetHandler
import kono.ipc.FunctionContext
import kono.ipc.IpcHandler
import kono.runtime.webview.WebViewBuilder
import kono.runtime.webview.buildWebView
import kono.runtime.webview.toWebViewTheme
import kono.runtime.window.EventLoop
import kono.runtime.window.toWindow
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

class WindowHandler(private val app: KonoApplication) {

    private val loop = EventLoop()
    private val counter = AtomicInteger()
    private val ipcHandler = IpcHandler(app)
    private val assets = AssetHandler(landingAsset = app.config.build.landingAsset)
    private val windows = mutableMapOf<Int, WebViewWindow>()

    init {
        spawnWebView()
    }

    /**
     * Creates a new webview on the given starting location
     */
    fun spawnWebView(location: String = "", buildFun: WebViewBuilder.() -> Unit = {}): WebViewWindow {
        val window = app.config.window.toWindow(eventLoop = loop)
        var context: FunctionContext? = null
        val webView = buildWebView(window) {
            url("kono://localhost/${location}")
            addCustomProtocol("kono") { path ->
                assets.loadEmbeddedAsset(path)
            }
            addIpcHandler { request ->
                ipcHandler.handle(request, context!!)
            }
            theme(app.config.window.theme.toWebViewTheme())
            devTools(app.config.app.debug)
            buildFun()
        }
        val webViewWindow = WebViewWindow(
            window = window,
            webView = webView
        )
        context = FunctionContext(webViewWindow, app, loop)
        windows[counter.getAndIncrement()] = webViewWindow
        return webViewWindow
    }

    fun show() {
        loop.run()
    }

}