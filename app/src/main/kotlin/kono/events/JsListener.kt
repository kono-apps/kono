package kono.events

import kono.ipc.FunctionContext

class JsListener(
    private val callbackId: Long,
    private val eval: (String) -> Unit
) : EventListener<String> {

    override fun receive(event: String, context: FunctionContext) {
        eval("window._$callbackId($event)")
    }
}