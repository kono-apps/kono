package kono.json

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import okio.Buffer

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
    val buffer = Buffer()
    JsonWriter.of(buffer).value(this)
    return buffer.readString(Charsets.UTF_8)
}

// A hacky way to get an adapter quickly, without kotlin-reflect
inline fun <reified T> Moshi.adapterOf(): JsonAdapter<T> {
    return adapter(typeOf<T>())
}
