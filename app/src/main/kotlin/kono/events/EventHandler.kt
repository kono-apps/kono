package kono.events

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.rawType
import kono.app.KonoApplication
import kono.export.ExportEvent
import kono.ipc.EmitEventRequest
import kono.ipc.FunctionContext
import kono.ipc.RegisterListenerRequest
import kono.ipc.runJS
import kotlin.LazyThreadSafetyMode.NONE

fun Class<*>.inferId(): String {
    return getAnnotation(ExportEvent::class.java)?.id?.ifBlank { rawType.simpleName } ?: error {
        "Event $this does not have @Event! Either add @Event or pass the ID manually."
    }
}

class EventHandler(private val app: KonoApplication) {

    private val backendListeners = mutableMapOf<String, MutableList<EventListener<*>>>()
    private val jsListeners = mutableMapOf<String, MutableList<JsListener>>()

    private val idsToType = mutableMapOf<String, Class<*>>()
    private val typeToId = mutableMapOf<Class<*>, String>()

    fun <T> listener(eventId: String, handler: EventListener<T>) {
        backendListeners.computeIfAbsent(eventId) { ArrayList() }.add(handler)
    }

    fun addEvent(type: Class<*>, id: String) {
        if (idsToType.containsKey(id))
            error("Duplicate event ID: '$id'")
        @Suppress("NAME_SHADOWING")
        val id = id.lowercase()
        idsToType[id] = type
        typeToId[type] = id
    }

    inline fun <reified T> addEvent(id: String = "") {
        val type = T::class.java
        val eventId = id.ifBlank { type.inferId() }
        addEvent(type, eventId)
    }

    inline fun <reified T> listener(handler: EventListener<T>) {
        val id = T::class.java.id()
        listener(id, handler)
    }

    fun <T : Any> emit(event: T, context: FunctionContext = FunctionContext()) {
        val id = event.javaClass.id().lowercase()
        backendListeners.getOrDefault(id, emptyList()).forEach {
            @Suppress("UNCHECKED_CAST")
            (it as EventListener<T>).receive(event, context)
        }

        val js = jsListeners.getOrDefault(id, emptyList())
        if (js.isNotEmpty()) {
            val asJson = app.moshi.adapter(event.javaClass).toJson(event)!!
            js.forEach { it.receive(asJson, context) }
        }
    }

    internal fun handleEmit(
        request: EmitEventRequest,
        context: FunctionContext,
        eval: (String) -> Unit,
    ) {
        runJS(
            eval = eval,
            successId = request.callbackId,
            failedId = request.errorId
        ) {
            val id = request.event.lowercase()

            val type: Class<*> = idsToType[id] ?: error("Invalid event ID: $id")
            val deserialized by lazy(NONE) {
                app.moshi.adapter(type).fromJsonValue(request.data)!!
            }
            val serialized by lazy(NONE) {
                @Suppress("UNCHECKED_CAST")
                (app.moshi.adapter(type) as JsonAdapter<Any>).toJson(deserialized)
            }

            backendListeners.getOrDefault(id, emptyList()).forEach {
                @Suppress("UNCHECKED_CAST")
                (it as EventListener<Any>).receive(deserialized, context)
            }
            jsListeners.getOrDefault(id, emptyList()).forEach {
                it.receive(serialized, context)
            }
            "{}"
        }
    }

    fun handleRegisterListener(
        request: RegisterListenerRequest,
        eval: (String) -> Unit,
    ) {
        runJS(
            eval = eval,
            successId = request.registerSuccessCallbackId,
            failedId = request.registerErrorCallbackId
        ) {
            val id = request.event.lowercase()
            if (!idsToType.containsKey(id))
                error("Invalid event ID: $id")
            jsListeners.computeIfAbsent(id) { ArrayList() }.add(JsListener(request.listenerCallbackId, eval))
            "{}"
        }
    }

    fun Class<*>.id(): String {
        return typeToId[this] ?: run {
            val id = inferId()
            id
        }
    }
}
