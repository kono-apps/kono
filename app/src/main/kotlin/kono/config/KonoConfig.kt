package kono.config

private const val DEFAULT_PROTOCOL = "kono"

data class KonoConfig(
    val app: AppConfig,
    val window: WindowConfig,
    val build: BuildConfig
)

data class AppConfig(
    val name: String,
    val authors: List<String>,
    val version: String
)

data class WindowConfig(
    val title: String,
    val fullScreen: Boolean = false,
    val resizable: Boolean = true,
    val maximized: Boolean = true,
    val width: Int = 800,
    val height: Int = 600
)

data class BuildConfig(
    val protocol: String = DEFAULT_PROTOCOL,
    val directory: String
)
