package kono

import kono.app.runKonoApplication
import kono.export.ExportFunction

@ExportFunction
fun reverse(value: String = "Hello!"): String {
    return value.reversed()
}

fun main() = runKonoApplication()
