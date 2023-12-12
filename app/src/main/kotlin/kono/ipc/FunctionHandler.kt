package kono.ipc

import kono.app.KonoApplication

typealias JsFunction = (RunFunctionRequest, FunctionContext) -> String

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
            val runFunction = functions[request.function] ?: error("No such function: ${request.function}")
            runFunction(request, context)
        }
    }
}
