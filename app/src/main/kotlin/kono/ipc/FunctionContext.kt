package kono.ipc

import kono.app.KonoApplication
import kono.app.currentRunningApp
import kono.display.WebViewWindow
import kono.runtime.window.EventLoop

/**
 * Represents the context in which a function, such as a listener
 * or an exported function is called. This contains information about
 * the webview as well as the window.
 *
 * This type can be added as a parameter that wish to access any of its
 * information. It is also possible to use any of the types below
 * directly as a function parameter, for example:
 * ```kt
 * @ExportFunction
 * fun ping(ping: String, webView: WebView) {
 *    webView.eval("console.log(${ping.encodeToJson()})")
 * }
 * ```
 * or:
 * ```kt
 * @ExportFunction
 * fun ping(ping: String, context: FunctionContext) {
 *    context.webView.eval("console.log(${ping.encodeToJson()})")
 * }
 * ```
 */
class FunctionContext(
    val window: WebViewWindow,
    val app: KonoApplication = currentRunningApp(),
    val eventLoop: EventLoop,
)