package kono.json

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import kono.app.KonoConfig
import okio.Buffer

/**
 * The path to the config file
 */
private const val CONFIG = "kono.json"

/**
 * Parses the application's [KonoConfig] from
 */
fun parseConfig(moshi: Moshi): KonoConfig {
    try {
        val file = KonoConfig::class.java.getResourceAsStream("/$CONFIG")
            ?: error("Cannot find the embedded kono.json in resources!")
        val buffer = Buffer()
        file.use { buffer.readFrom(it) }
        return moshi.adapterOf<KonoConfig>().fromJson(buffer) ?: error("Failed to parse kono.json")
    } catch (e: JsonDataException) {
        throw IllegalStateException("Failed to parse kono.json: " + e.message)
    }
}