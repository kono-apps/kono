package kono.ipc

class FunctionInvocationException(message: String, cause: Throwable?) : RuntimeException(message.cleanMessage(), cause)

private fun String.cleanMessage(): String = removeSuffix(" at $.data")
