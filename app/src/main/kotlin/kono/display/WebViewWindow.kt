package kono.display

/**
 * Creates a WebView that is bound to a window
 */
fun createWebViewWindow(window: Window, webView: WebView): WebViewWindow {
    return WebViewWindow(window, webView)
}

/**
 * Represents a webview bound to a window.
 */
class WebViewWindow(
    private val window: Window,
    private val webView: WebView,
) : WebView by webView, Window by window