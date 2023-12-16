package kono.ipc

import kono.json.encodeJson

/**
 * Runs the given code as requested from JavaScript.
 *
 * This will run the given function, and call the success function for
 * JavaScript to indicate that it was successfully invoked, otherwise it
 * will propagate any errors to the error callback function.
 */
fun runRequestFromJS(
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
        // quotation marks are respected and escaped as needed.
        eval("window._${failedId}(${e.message?.encodeJson()})")
    }
}