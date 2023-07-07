package kono.ipc

import com.squareup.moshi.JsonReader
import kono.app.KonoApplication
import kono.json.adapterOf
import okio.Buffer

class IpcHandler(private val app: KonoApplication) {

    fun handle(
        request: String,
        context: FunctionContext,
    ) {
        val reader = JsonReader.of(Buffer().writeUtf8(request))

        when (val requestType = app.moshi.adapterOf<IPCRequest>().fromJson(reader)!!) {
            is RunFunctionRequest -> {
                app.functions.call(requestType, context) { context.webView!!.eval(it) }
            }

            is EmitEventRequest -> {
                app.events.handleEmit(requestType, context) { context.webView!!.eval(it) }
            }

            is RegisterListenerRequest -> {
                app.events.handleRegisterListener(requestType) { context.webView!!.eval(it) }
            }
        }
    }
}