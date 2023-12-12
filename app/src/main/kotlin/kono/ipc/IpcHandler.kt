package kono.ipc

import kono.app.KonoApplication
import kotlinx.serialization.json.Json

class IpcHandler(private val app: KonoApplication) {

    fun handle(
        request: String,
        context: FunctionContext,
    ) {
        when (val requestType: IPCRequest = Json.decodeFromString(request)) {
            is RunFunctionRequest -> {
                app.functions.call(requestType, context) { context.window.webView.eval(it) }
            }

            is EmitEventRequest -> {
                app.events.handleEmit(requestType, context) { context.window.webView.eval(it) }
            }

            is RegisterListenerRequest -> {
                app.events.handleRegisterListener(requestType) { context.window.webView.eval(it) }
            }
        }
    }
}