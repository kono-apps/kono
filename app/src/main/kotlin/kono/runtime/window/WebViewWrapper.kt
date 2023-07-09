package kono.runtime.window

import kono.display.WebView
import kono.runtime.webview.NativeWebView

class WebViewWrapper(private val webView: NativeWebView) : WebView {

    override fun eval(js: String) {
        webView.eval(js)
    }

    override fun evalWithCallback(script: String, onFinished: (String) -> Unit) {
        webView.evalWithCallback(script, onFinished)
    }
}