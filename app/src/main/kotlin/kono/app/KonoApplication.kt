package kono.app

import kono.display.WindowHandler

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
