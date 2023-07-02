package kono.app

import kono.fns.FunctionHandler

/**
 * Represents the environment context that the app is being run into.
 *
 * This interface is implemented by the codegen, and should mostly
 * not be implemented by hand unless very specific behavior is needed.
 *
 * See [kono.generated.GeneratedAppContext]
 */
interface KonoApplicationContext {
    val functions: FunctionHandler
}