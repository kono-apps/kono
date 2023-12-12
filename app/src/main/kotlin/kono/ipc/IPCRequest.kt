package kono.ipc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

private typealias CallbackId = Long

@Serializable
sealed interface IPCRequest

@Serializable
@SerialName("function")
class RunFunctionRequest(
    val callbackId: CallbackId,
    val errorId: CallbackId,
    val function: String,
    @SerialName("passed") val passedParameters: MutableList<String>,
    val data: JsonElement,
) : IPCRequest

@Serializable
@SerialName("emitEvent")
class EmitEventRequest(
    val event: String,
    val callbackId: CallbackId,
    val errorId: CallbackId,
    val data: JsonElement,
) : IPCRequest

@Serializable
@SerialName("registerListener")
class RegisterListenerRequest(
    val event: String,
    val listenerCallbackId: CallbackId,
    val registerErrorCallbackId: CallbackId,
    val registerSuccessCallbackId: CallbackId,
    val data: JsonElement,
) : IPCRequest
