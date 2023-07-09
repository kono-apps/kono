package kono.runtime.webview

import kono.asset.Asset
import kono.runtime.natives.nativeRuntime
import kono.runtime.natives.NativeRuntime
import kono.runtime.natives.WebViewBuilderPtr
import kono.runtime.window.NativeWindow
import org.intellij.lang.annotations.Language
import java.io.File

private val RunningDirectory by lazy {
    File(System.getProperty("user.dir"))
}

/**
 * Creates a new WebView that is bound to the given window
 */
fun webView(
    nativeWindow: NativeWindow,
    runningDir: File = RunningDirectory,
    block: WebViewBuilder.() -> Unit
): NativeWebView {
    val builder = WebViewBuilder(nativeWindow)
    builder.block()
    return builder.build(runningDir)
}

/**
 * A webview builder. This should only be created from [webView]
 * as it properly manages the internal pointers.
 *
 * Implementation note: Calling [WebViewBuilder.build] invalidates
 * the [WebViewBuilder.builderPtr]. Any further access to it can lead
 * to errors, therefore a WindowBuilder should not be re-used.
 */
class WebViewBuilder internal constructor(private val nativeWindow: NativeWindow) {

    /**
     * This pointer must be updated every time the value change, in
     * which case, you should use [update] instead of manually updating it.
     */
    private var builderPtr = nativeRuntime { nativeWindow.ptr.createWebViewBuilder() }

    /**
     * Whether this builder has been consumed yet or not. This becomes true
     * after calling [build]
     */
    private var consumed = false

    /**
     * Updates the pointer after invoking the given action
     */
    private fun update(action: context(NativeRuntime) WebViewBuilderPtr.() -> WebViewBuilderPtr): WebViewBuilder {
        if (consumed)
            error("This builder has been consumed! Create a new one instead.")
        nativeRuntime { builderPtr = action(builderPtr) }
        return this
    }

    /**
     * Sets the URL this WebView starts on
     */
    fun url(url: String) = update {
        webViewSetURL(url)
    }

    /**
     * Sets the webview theme
     */
    fun theme(theme: WebViewTheme) = update {
        webViewBuilderSetTheme(theme.ordinal.toByte())
    }

    /**
     * Register custom file loading protocols with pairs of scheme uri string and
     * a handling function.
     */
    fun addCustomProtocol(name: String, handler: (path: String) -> Asset) = update {
        webViewAddCustomProtocol(name) { path ->
            val asset = handler(path)
            asset.assetPtr
        }
    }

    /**
     * Set the IPC handler to receive the message from Javascript on
     * webview to host code. The message sent from webview should call
     * `window.ipc.postMessage("insert_message_here");`.
     */
    fun addIpcHandler(handler: (message: String) -> Unit) = update {
        webViewAddIPCHandler { message ->
            runCatching {
                handler(message)
            }.recover {
                it.printStackTrace()
            }
        }
    }

    /**
     * Sets whether are dev tools enabled or not.
     */
    fun devTools(enabled: Boolean) = update {
        webViewSetDevTools(enabled)
    }

    /**
     * Initialize JavaScript code when loading new pages. When the webview
     * loads a new page, this initialization code will be executed. It is guaranteed
     * that code is executed before `window.onload`.
     */
    fun addInitializationScript(@Language("JavaScript") script: String) = update {
        webViewAddInitScript(script)
    }

    /**
     * Load the provided HTML string when the builder calling [WebViewBuilder.build] to create
     * the WebView. This will be ignored if url is provided.
     */
    fun html(@Language("HTML") html: String) = update {
        webViewSetHTML(html)
    }

    /**
     * Builds the WebView
     */
    fun build(runningDir: File): NativeWebView {
        if (consumed)
            error("You cannot re-use the same builder twice!")
        consumed = true
        val webViewPtr = with(builderPtr) {
            nativeRuntime {
                webViewBuild(runningDir.absolutePath)
            }
        }
        return NativeWebView(webViewPtr)
    }
}