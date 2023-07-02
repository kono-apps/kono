package kono.app

import com.squareup.moshi.Moshi
import kono.asset.AssetHandler
import kono.config.KonoConfig
import kono.window.EventLoop
import kono.webview.webView
import kono.window.window

class KonoApplication(
    private val context: KonoApplicationContext,
    private val config: KonoConfig,
    val moshi: Moshi
) {

    private val assets = AssetHandler(
        landingAsset = config.build.landingAsset
    )

    fun start() {
        val eventLoop = EventLoop()
        val window = window(eventLoop) {
            title(config.window.title)
            fullScreen(config.window.fullScreen)
            resizable(config.window.resizable)
            maximized(config.window.maximized)
            size(width = config.window.width, height = config.window.height)
        }
        val webView = webView(window) {
            url("kono://localhost/")
            addCustomProtocol("kono") { path ->
                assets.loadEmbeddedAsset(path)
            }
            addIpcHandler {
                context.functions.call(moshi, it)
            }
            devTools(config.app.debug)
        }
        eventLoop.run {
            println("Window created!")
        }
    }
}
