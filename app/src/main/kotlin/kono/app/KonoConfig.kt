package kono.app

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KonoConfig(
    val app: AppConfig,
    val window: WindowConfig = WindowConfig(
        title = app.name
    ),
    val build: BuildConfig = BuildConfig()
)

@JsonClass(generateAdapter = true)
data class AppConfig(
    val name: String,
    val authors: List<String>,
    val version: String = "1.0.0",
    val debug: Boolean = false
)

@JsonClass(generateAdapter = true)
data class WindowConfig(
    val title: String,
    val fullScreen: Boolean = false,
    val resizable: Boolean = true,
    val maximized: Boolean = true,
    val closable: Boolean = true,
    val width: Int = 800,
    val height: Int = 600
)

@JsonClass(generateAdapter = true)
data class BuildConfig(
    val landingAsset: String = "index.html"
)
