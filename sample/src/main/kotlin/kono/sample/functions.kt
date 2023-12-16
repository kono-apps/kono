package kono.sample

import kono.export.ExportFunction
import kotlinx.serialization.Serializable

@ExportFunction
fun ping(value: String = ""): String {
    return value
}

/**
 * (A simple function)
 *
 * Reverses the given string
 */
@ExportFunction
fun reverse(name: String = "Kono"): String {
    return name.reversed()
}

/**
 * (A function with default parameters)
 *
 * Shows a greeting.
 */
@ExportFunction
fun greet(user: String, greeting: String = "Hello, "): String {
    return "$greeting $user!"
}

/**
 * An example of a complex data type. Note that it must be serializable.
 *
 * See [Serializers](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serializers.md).
 */
@Serializable
data class Greeting(
    val user: String,
    val greeting: String = "Hello, ",
)

/**
 * (A function with a complex return type and default parameters)
 *
 * Sends lots of greetings to the given user
 */
@ExportFunction
fun sendManyGreetings(
    user: String,
    times: Int,
    greeting: String = "Hello, ",
): List<Greeting> {
    return buildList {
        repeat(times) {
            add(Greeting(user, greeting))
        }
    }
}

/**
 * (A function with a complex argument)
 *
 * Receives many greetings and prints them
 */
@ExportFunction
fun receiveManyGreetings(greetings: List<Greeting> = emptyList()) {
    println("Wow! So many greetings!")
    greetings.forEach {
        println(it)
    }
}