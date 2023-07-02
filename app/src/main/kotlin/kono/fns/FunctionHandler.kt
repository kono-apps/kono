package kono.fns

import com.squareup.moshi.*
import kono.json.KonoMoshi
import kono.json.adapterOf
import okio.Buffer

typealias JsFunction = (Moshi, JsonReader, JsonWriter) -> Unit

fun functionHandler(functions: MutableMap<String, JsFunction>.() -> Unit): FunctionHandler {
    val fns = mutableMapOf<String, JsFunction>().also(functions)
    return FunctionHandler(fns)
}

class CallResult(
    val success: Boolean,
    val exception: String? = null,
)

class FunctionHandler(private val functions: MutableMap<String, JsFunction>) {

    fun call(moshi: Moshi, json: String): String {
        val buffer = Buffer()
        buffer.writeUtf8(json)

        val outputString = Buffer()
        val output = JsonWriter.of(outputString)

        val dataReader = JsonReader.of(buffer)

        val nameReader = dataReader.peekJson()
        val name = KonoMoshi.adapterOf<CallbackInfo>().fromJson(nameReader)!!.functionName
        val fn = functions[name] ?: error("No such function: $name")

        try {
            fn(moshi, dataReader, output)

        } catch (e: Throwable) {
        }
        return outputString.readString(Charsets.UTF_8)
    }
}

@JsonClass(generateAdapter = true)
internal class CallbackInfo(
    @Json(name = "fn") val functionName: String,
    @Json(name = "c") val successCallbackId: Int,
    @Json(name = "e") val errorCallbackId: Int,
)
