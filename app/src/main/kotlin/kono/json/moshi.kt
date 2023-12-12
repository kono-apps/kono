package kono.json

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Encodes this string as a JSON string. This is useful
 * when we need to pass a string to a JS function
 * using [kono.runtime.webview.WebView.eval] and similar
 * functions.
 *
 * For example, passing `Hello` would return `"Hello"`. Similarly,
 * quotes, backslashes, and any other characters will be escaped
 * if necessary.
 */
fun String?.encodeJson(): String {
    return Json.encodeToString(this)
}
