package kono.config

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KonoConfig(
    val app: AppConfig,
    val window: WindowConfig,
    val build: BuildConfig
)

@JsonClass(generateAdapter = true)
data class AppConfig(
    val name: String,
    val authors: List<String>,
    val version: String,
    val debug: Boolean = false
)

@JsonClass(generateAdapter = true)
data class WindowConfig(
    val title: String,
    val fullScreen: Boolean = false,
    val resizable: Boolean = true,
    val maximized: Boolean = true,
    val width: Int = 800,
    val height: Int = 600
)

@JsonClass(generateAdapter = true)
data class BuildConfig(
    val directory: String,
    val landingAsset: String = "index.html"
)
