package kono.ipc

import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import kono.app.KonoApplication
import okio.Buffer

typealias JsFunction = (Moshi, RunFunctionRequest, JsonWriter, FunctionContext) -> Unit

fun functionHandler(
    app: KonoApplication,
    functions: MutableMap<String, JsFunction>.() -> Unit,
): FunctionHandler {
    val fns = buildMap(functions)
    return FunctionHandler(app, fns)
}

class FunctionHandler(
    private val app: KonoApplication,
    private val functions: Map<String, JsFunction>,
) {

    fun call(
        request: RunFunctionRequest,
        context: FunctionContext,
        eval: (String) -> Unit,
    ) {
        runJS(
            eval = eval,
            successId = request.callbackId,
            failedId = request.errorId
        ) {
            val outputString = Buffer()
            val output = JsonWriter.of(outputString)
            val runFunction = functions[request.function] ?: error("No such function: ${request.function}")
            runFunction(app.moshi, request, output, context)
            outputString.readString(Charsets.UTF_8)
        }
    }
}
