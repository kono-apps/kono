package kono

import kono.assets.AssetHandler
import kono.config.KonoConfig
import kono.fns.FunctionHandler

interface KonoApplicationContext {
    val config: KonoConfig
    val functions: FunctionHandler
    val assets: AssetHandler
}