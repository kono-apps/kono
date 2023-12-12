package kono.ipc

import kono.json.encodeJson

/**
 * Runs the given code as requested from JavaScript. This
 * will propagate any errors to the error callback specified
 */
fun runJS(
    eval: (String) -> Unit,
    successId: Long,
    failedId: Long,
    action: () -> String,
) {
    try {
        val result = action()
        eval("window._${successId}(${result})")
    } catch (e: Exception) {
        // We pass the output once again to a String adapter, so that any
        // quotation marks as respected and escaped as needed.
        eval("window._${failedId}(${e.message?.encodeJson()})")
    }
}