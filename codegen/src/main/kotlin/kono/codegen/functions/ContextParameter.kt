package kono.codegen.functions

import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import kono.app.KonoApplication
import kono.display.WebViewWindow
import kono.ipc.FunctionContext
import kono.runtime.webview.NativeWebView
import kotlin.reflect.KClass

/**
 * Represents a parameter that may be fetched from a [FunctionContext].
 */
enum class ContextParameter(
    type: KClass<*>,
    access: String = "",
    val isNullable: Boolean = true,
) {

    /**
     * The context itself
     */
    CONTEXT(FunctionContext::class),

    /**
     * The current window
     */
    WINDOW(WebViewWindow::class, "window"),

    /**
     * The application instance
     */
    APP(KonoApplication::class, "app", isNullable = false);

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