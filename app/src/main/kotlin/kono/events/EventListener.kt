package kono.events

import kono.ipc.FunctionContext

fun interface EventListener<T> {

    fun receive(event: T, context: FunctionContext)

}
