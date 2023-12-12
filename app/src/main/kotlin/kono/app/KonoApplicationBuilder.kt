package kono.app

import kono.json.parseConfig

class KonoApplicationBuilder(private val context: KonoApplicationContext) {

    private lateinit var appConfig: KonoConfig

    fun config(config: KonoConfig) = apply {
        this.appConfig = config
    }

    fun build(): KonoApplication {
        if (!this::appConfig.isInitialized)
            appConfig = parseConfig()
        return KonoApplication(context, appConfig)
    }
}

