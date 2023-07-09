package kono.display

import kono.runtime.webview.NativeWebView
import kono.runtime.window.WebViewWrapper
import org.intellij.lang.annotations.Language

internal fun WebView(webView: NativeWebView) = WebViewWrapper(webView)

/**
 * Represents a platform WebView
 */
interface WebView {

    /**
     * Evaluate and run JavaScript code.
     *
     * Must be called on the same thread that created the WebView
     */
    fun eval(@Language("JavaScript") js: String)

    /**
     * Evaluate and run javascript code with callback function. The evaluation result
     * will be serialized into a JSON string and passed to the callback function.
     *
     * Must be called on the same thread who created the WebView.
     */
    fun evalWithCallback(@Language("JavaScript") script: String, onFinished: (String) -> Unit)

}