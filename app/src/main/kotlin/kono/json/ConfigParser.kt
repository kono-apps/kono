package kono.json

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import kono.app.KonoConfig
import okio.Buffer

fun parseConfig(moshi: Moshi): KonoConfig {
    val file = KonoConfig::class.java.getResourceAsStream("/kono.json")
        ?: error("Cannot find the embedded kono.json in resources!")
    val buffer = Buffer()
    file.use { buffer.readFrom(it) }
    try {
        return moshi.adapterOf<KonoConfig>().fromJson(buffer) ?: error("Failed to parse kono.json")
    } catch (e: JsonDataException) {
        throw IllegalStateException("Failed to parse kono.json: " + e.message)
    }
}