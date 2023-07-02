package kono.json

import com.squareup.moshi.Moshi
import kono.config.KonoConfig
import okio.Buffer

fun parseConfig(moshi: Moshi): KonoConfig {
    val file = KonoConfig::class.java.getResourceAsStream("/kono.json")
        ?: error("Cannot find the embedded kono.json in resources!")
    val buffer = Buffer()
    file.use { buffer.readFrom(it) }
    return moshi.adapterOf<KonoConfig>().fromJson(buffer) ?: error("Failed to parse kono.json")
}