package kono.fns

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import kono.json.KonoMoshi
import kono.json.adapterOf
import okio.Buffer

typealias JsFunction = (Moshi, JsonReader) -> String

val GeneratedFunctionHandler = functionHandler {
    this["HELLO"] = { m, r -> kono.json.generated.reverse(m, r) }
}

internal fun functionHandler(functions: MutableMap<String, JsFunction>.() -> Unit): FunctionHandler {
    val fns = buildMap(functions)
    return FunctionHandler(fns)
}

class FunctionHandler(private val functions: Map<String, JsFunction>) {

    fun call(moshi: Moshi, json: String): String {
        val buffer = Buffer()
        buffer.writeUtf8(json)
        val dataReader = JsonReader.of(buffer)

        val nameReader = dataReader.peekJson()
        val name = KonoMoshi.adapterOf<FunctionName>().fromJson(nameReader)!!.name
        val fn = functions[name] ?: error("No such function: $name")

        return fn(moshi, dataReader)
    }
}

@JsonClass(generateAdapter = true)
internal class FunctionName(@Json(name = "fn") val name: String)
