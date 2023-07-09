package kono.ipc

import kono.app.KonoApplication
import kono.json.adapterOf

class IpcHandler(private val app: KonoApplication) {

    fun handle(
        request: String,
        context: FunctionContext,
    ) {
        when (val requestType = app.moshi.adapterOf<IPCRequest>().fromJson(request)!!) {
            is RunFunctionRequest -> {
                app.functions.call(requestType, context) { context.nativeWebView!!.eval(it) }
            }

            is EmitEventRequest -> {
                app.events.handleEmit(requestType, context) { context.nativeWebView!!.eval(it) }
            }

            is RegisterListenerRequest -> {
                app.events.handleRegisterListener(requestType) { context.nativeWebView!!.eval(it) }
            }
        }
    }
}