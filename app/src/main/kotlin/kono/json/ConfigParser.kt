package kono.json

import kono.app.KonoConfig
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

/**
 * The path to the config file
 */
private const val CONFIG = "kono.json"

/**
 * Parses the application's [KonoConfig] from
 */
@OptIn(ExperimentalSerializationApi::class)
fun parseConfig(): KonoConfig {
    val file = KonoConfig::class.java.getResourceAsStream("/$CONFIG")
        ?: error("Cannot find the embedded kono.json in resources!")
    return Json.decodeFromStream<KonoConfig>(file)
}