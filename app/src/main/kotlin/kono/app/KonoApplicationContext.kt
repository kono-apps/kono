package kono.app

import kono.events.EventHandler
import kono.ipc.FunctionHandler

/**
 * Represents the environment context that the app is being run into.
 *
 * This interface is implemented by the codegen, and should mostly
 * not be implemented by hand unless very specific behavior is needed.
 *
 * See [kono.generated.GeneratedAppContext]
 */
interface KonoApplicationContext {

    fun createFunctionHandler(app: KonoApplication): FunctionHandler

    fun createEventHandler(app: KonoApplication): EventHandler

}
