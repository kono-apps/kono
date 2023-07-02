package kono.sample

import com.squareup.moshi.JsonClass
import kono.export.ExportFunction

/**
 * (A simple function)
 *
 * Reverses the given string
 */
@ExportFunction
fun reverse(value: String): String {
    return value.reversed()
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

// This annotation must be added to types that want to be passed
// around in exported functions. You can also register a custom
// JSON adapter for the type in runKonoApplication's builder
// using `moshi(...)`.
//
// The class does not need to be a data class
@JsonClass(generateAdapter = true)
data class Greeting(
    val user: String,
    val greeting: String = "Hello, "
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
    greeting: String = "Hello, "
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