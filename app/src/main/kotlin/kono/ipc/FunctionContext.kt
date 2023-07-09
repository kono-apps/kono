package kono.ipc

import kono.app.KonoApplication
import kono.app.currentRunningApp
import kono.display.WebViewWindow
import kono.runtime.webview.NativeWebView
import kono.runtime.window.EventLoop
import kono.runtime.window.NativeWindow

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
 *    // Note: It's not recommended to actually pass strings like that
 *    // directly, but for the sake of the example!
 *    webView.eval("console.log('$ping')")
 * }
 * ```
 * or:
 * ```kt
 * @ExportFunction
 * fun ping(ping: String, context: FunctionContext) {
 *    context.webView.eval("console.log('$ping')")
 * }
 * ```
 */
class FunctionContext(
    val window: WebViewWindow,
    val app: KonoApplication = currentRunningApp(),
    val eventLoop: EventLoop? = null,
)