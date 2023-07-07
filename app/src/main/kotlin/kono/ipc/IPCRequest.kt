package kono.ipc

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kono.json.PolymorphicJsonAdapterFactory

private typealias CallbackId = Long

sealed interface IPCRequest

internal val IpcRequestTypeAdapter = PolymorphicJsonAdapterFactory.of(IPCRequest::class.java, "request")
    .withSubtype(RunFunctionRequest::class.java, "function")
    .withSubtype(EmitEventRequest::class.java, "emitEvent")
    .withSubtype(RegisterListenerRequest::class.java, "registerListener")

@JsonClass(generateAdapter = true)
class RunFunctionRequest(
    val callbackId: CallbackId,
    val errorId: CallbackId,
    val function: String,
    @Json(name = "passed") val passedParameters: MutableList<String>,
    val data: Any,
) : IPCRequest

@JsonClass(generateAdapter = true)
class EmitEventRequest(
    val event: String,
    val callbackId: CallbackId,
    val errorId: CallbackId,
    val data: Any,
) : IPCRequest

@JsonClass(generateAdapter = true)
class RegisterListenerRequest(
    val event: String,
    val listenerCallbackId: CallbackId,
    val registerErrorCallbackId: CallbackId,
    val registerSuccessCallbackId: CallbackId,
    val data: Any,
) : IPCRequest
