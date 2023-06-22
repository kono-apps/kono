package kono.runtime.natives

import com.sun.jna.Callback
import com.sun.jna.Library
import com.sun.jna.Pointer

// For semantics
typealias WindowBuilderPtr = Pointer
typealias WindowPtr = Pointer
typealias WebViewPtr = Pointer
typealias WebViewBuilderPtr = Pointer
typealias EventLoopPtr = Pointer
typealias AssetPtr = Pointer

interface NativeRuntime : Library {

    fun createEventLoop(): EventLoopPtr

    fun createWindowBuilder(): WindowBuilderPtr

    fun WindowBuilderPtr.windowBuilderSetTitle(title: String): WindowBuilderPtr

    fun WindowBuilderPtr.windowBuilderSetFullScreen(fullScreen: Boolean): WindowBuilderPtr

    fun WindowBuilderPtr.windowBuilderSetSize(width: Int, height: Int): WindowBuilderPtr

    fun WindowBuilderPtr.windowBuilderSetMaximized(maximized: Boolean): WindowBuilderPtr

    fun WindowBuilderPtr.windowBuilderSetMaximizable(maximizable: Boolean): WindowBuilderPtr

    fun WindowBuilderPtr.windowBuilderSetClosable(closable: Boolean): WindowBuilderPtr

    fun WindowBuilderPtr.windowBuilderSetResizable(closable: Boolean): WindowBuilderPtr

    fun WindowBuilderPtr.windowBuild(eventLoop: EventLoopPtr): WindowPtr

    /**
     * Creates a new wry::WebViewBuilder
     */
    fun WindowPtr.createWebViewBuilder(): WebViewBuilderPtr

    /**
     * Sets the webview's URL.
     *
     * Note: Any previous pointer to the builder will be invalid,
     * as Rust will drop it. Variables must use the return value of this
     * method
     */
    fun WebViewBuilderPtr.webViewSetURL(
        url: String
    ): WebViewBuilderPtr

    fun WebViewBuilderPtr.webViewAddCustomProtocol(
        name: String,
        handler: CustomProtocolCallback
    ): WebViewBuilderPtr

    fun WebViewBuilderPtr.webViewSetDevTools(
        devTools: Boolean
    ): WebViewBuilderPtr

    fun WebViewBuilderPtr.webViewAddInitScript(
        script: String
    ): WebViewBuilderPtr

    fun WebViewBuilderPtr.webViewSetHTML(
        html: String
    ): WebViewBuilderPtr

    fun WebViewBuilderPtr.webViewAddIPCHandler(
        handler: IPCHandler
    ): WebViewBuilderPtr

    fun WebViewBuilderPtr.webViewBuild(): WebViewPtr

    fun EventLoopPtr.eventLoopRun(onInit: InitCallback)

    fun AssetPtr.assetSetContent(content: ByteArray, length: Int): AssetPtr

    fun AssetPtr.assetSetMimeType(mimeType: String): AssetPtr

    fun AssetPtr.getAssetPath(): String

}

@Suppress("unused") // actually used by JNA
fun interface CustomProtocolCallback : Callback {
    fun apply(assetPtr: AssetPtr): AssetPtr
}

@Suppress("unused") // actually used by JNA
fun interface InitCallback : Callback {
    fun apply()
}

@Suppress("unused") // actually used by JNA
fun interface IPCHandler : Callback {
    fun apply(request: String)
}