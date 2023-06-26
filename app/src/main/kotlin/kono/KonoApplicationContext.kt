package kono

import kono.config.KonoConfig
import kono.events.EventHandler
import kono.fns.FunctionHandler

class KonoApplicationContext(
    val config: KonoConfig
) {

    val assets = AssetHandler()
    val events = EventHandler()

}