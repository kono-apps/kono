package kono.generated

import kono.app.KonoApplication
import kono.app.KonoApplicationContext
import kono.events.EventHandler
import kono.ipc.FunctionHandler

//val GeneratedAppContext by lazy {
//    Class.forName("kono.generated.GeneratedKonoContext")
//        .getDeclaredConstructor()
//        .newInstance() as KonoApplicationContext
//}

class GeneratedAppContext : KonoApplicationContext {

    val eventHandler by lazy {
        Class.forName("kono.generated.GeneratedEventHandler")
            .getDeclaredConstructor()
            .newInstance() as EventHandler
    }

    override fun createFunctionHandler(app: KonoApplication): FunctionHandler {
        TODO("Not yet implemented")
    }

    override fun createEventHandler(app: KonoApplication): EventHandler {
        TODO("Not yet implemented")
    }

}