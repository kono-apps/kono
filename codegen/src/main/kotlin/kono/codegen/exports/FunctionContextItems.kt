package kono.codegen.exports

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName

val WindowContextItem = ContextItem(
    ClassName("kono.window", "Window"),
    ".window!!"
)

val WebViewContextItem = ContextItem(
    ClassName("kono.webview", "WebView"),
    ".webView!!"
)

val AppContextItem = ContextItem(
    ClassName("kono.app", "KonoApplication"),
    ".app"
)

val EventLoopContextItem = ContextItem(
    ClassName("kono.window", "EventLoop"),
    ".eventLoop!!"
)

val FunctionContextItem = ContextItem(
    ClassName("kono.ipc", "FunctionContext"),
    ""
)

val ContextItems = listOf(
    WindowContextItem,
    EventLoopContextItem,
    WebViewContextItem,
    AppContextItem,
    FunctionContextItem
).associateBy { it.type }

class ContextItem(
    val type: TypeName,
    val access: String,
)