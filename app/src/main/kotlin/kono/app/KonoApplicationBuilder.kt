package kono.app

import com.squareup.moshi.Moshi
import kono.json.parseConfig

class KonoApplicationBuilder(private val context: KonoApplicationContext) {

    private val moshiBuilder = Moshi.Builder()
    private lateinit var appConfig: KonoConfig

    fun moshi(block: Moshi.Builder.() -> Unit) = apply {
        moshiBuilder.block()
    }

    fun config(config: KonoConfig) = apply {
        this.appConfig = config
    }

    fun build(): KonoApplication {
        val moshi = moshiBuilder.build()
        if (!this::appConfig.isInitialized)
            appConfig = parseConfig(moshi)
        return KonoApplication(context, appConfig, moshi)
    }
}

