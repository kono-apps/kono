package kono.fns

import com.squareup.moshi.*
import kono.json.KonoMoshi
import kono.json.adapterOf
import kono.webview.WebView
import okio.Buffer

typealias JsFunction = (Moshi, JsonReader, JsonWriter) -> Unit

fun functionHandler(functions: MutableMap<String, JsFunction>.() -> Unit): FunctionHandler {
    val fns = mutableMapOf<String, JsFunction>().also(functions)
    return FunctionHandler(fns)
}

class FunctionHandler(private val functions: MutableMap<String, JsFunction>) {

    fun call(moshi: Moshi, json: String, webView: WebView): String {
        val buffer = Buffer()
        buffer.writeUtf8(json)

        val outputString = Buffer()
        val output = JsonWriter.of(outputString)

        val dataReader = JsonReader.of(buffer)

        val nameReader = dataReader.peekJson()
        val callbackInfo = KonoMoshi.adapterOf<CallbackInfo>().fromJson(nameReader)!!

        val runFunction = functions[callbackInfo.function] ?: error("No such function: ${callbackInfo.function}")

        try {
            // We pass the output once again to a String adapter, so that any
            // quotation marks as respected and escaped as needed.
            runFunction(moshi, dataReader, output)
            val evalAsJs = moshi.adapter(String::class.java).toJson(outputString.readString(Charsets.UTF_8))

            webView.eval("window._${callbackInfo.callbackId}(JSON.parse($evalAsJs))")
        } catch (e: Exception) {
            val errorAsJson = moshi.adapter(String::class.java).toJson(e.message)
            webView.eval("window._${callbackInfo.errorId}($errorAsJson)")
        }
        return outputString.readString(Charsets.UTF_8)
    }
}

@JsonClass(generateAdapter = true)
internal data class CallbackInfo(
    val callbackId: Long,
    val errorId: Long,
    val function: String,
)
