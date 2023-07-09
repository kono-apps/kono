package kono.runtime.webview

import kono.runtime.natives.WebViewPtr
import kono.runtime.natives.nativeRuntime
import org.intellij.lang.annotations.Language

class NativeWebView internal constructor(private val ptr: WebViewPtr) {

    /**
     * Evaluate and run JavaScript code.
     *
     * Must be called on the same thread that created the WebView
     */
    fun eval(@Language("JavaScript") js: String) {
        nativeRuntime { ptr.webViewEval(js) }
    }

    /**
     * Evaluate and run javascript code with callback function. The evaluation result
     * will be serialized into a JSON string and passed to the callback function.
     *
     * Must be called on the same thread who created the WebView.
     */
    fun evalWithCallback(@Language("JavaScript") script: String, onFinished: (String) -> Unit) {
        nativeRuntime {
            ptr.webViewEvalWithCallback(script) {
                onFinished(it)
            }
        }
    }
}