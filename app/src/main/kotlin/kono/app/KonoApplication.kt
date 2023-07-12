package kono.app

import com.squareup.moshi.Moshi
import kono.asset.AssetHandler
import kono.display.WindowHandler
import kono.ipc.IpcHandler

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
    val context: KonoApplicationContext,
    val config: KonoConfig,
    val moshi: Moshi,
) {

    init {
        if (::app.isInitialized)
            error("App has been initialized twice!")
        app = this
    }

    val events = context.createEventHandler(this)
    val functions = context.createFunctionHandler(this)
    val windows = WindowHandler(this)

    fun start() {
        windows.spawnWebView()
        windows.show()
    }
}
