package kono.runtime.webview

import kono.runtime.window.WindowTheme

enum class WebViewTheme {

    DARK,
    LIGHT,
    AUTO;

}

fun WindowTheme?.toWebViewTheme(): WebViewTheme {
    return when (this) {
        WindowTheme.DARK -> WebViewTheme.DARK
        WindowTheme.LIGHT -> WebViewTheme.LIGHT
        null -> WebViewTheme.AUTO
    }
}