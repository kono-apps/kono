package kono.codegen.functions

import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import kono.app.KonoApplication
import kono.display.WebViewWindow
import kono.ipc.FunctionContext
import kono.runtime.webview.WebView
import kono.runtime.window.Window
import kotlin.reflect.KClass

/**
 * Represents a parameter that may be fetched from a [FunctionContext].
 */
enum class ContextParameter(
    type: KClass<*>,
    access: String = "",
    val isNullable: Boolean = false,
) {

    /**
     * The context itself
     */
    CONTEXT(FunctionContext::class),

    /**
     * The current webview window
     */
    WEBVIEW_WINDOW(WebViewWindow::class, "window"),

    /**
     * The current window
     */
    WINDOW(Window::class, "window.window"),

    /**
     * The current window
     */
    WEBVIEW(WebView::class, "window.webView"),

    /**
     * The application instance
     */
    APP(KonoApplication::class, "app");

    /**
     * The type representing this context parameter
     */
    val type = type.asTypeName()

    /**
     * The expression used to access this parameter
     */
    val access = if (access.isBlank()) "" else ".$access"

    companion object {
        private val byType = values().associateBy { it.type }

        /**
         * Fetches the context parameter according to the given type
         */
        fun fromType(type: TypeName): ContextParameter? = byType[type]
    }
}