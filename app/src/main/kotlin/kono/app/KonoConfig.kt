package kono.app

import kono.runtime.window.WindowTheme
import kotlinx.serialization.Serializable

@Serializable
data class KonoConfig(
    val app: AppConfig,
    val window: WindowConfig = WindowConfig(
        title = app.name
    ),
    val build: BuildConfig = BuildConfig()
)

@Serializable
data class AppConfig(
    val name: String,
    val authors: List<String>,
    val version: String = "1.0.0",
    val debug: Boolean = false
)

@Serializable
data class WindowConfig(
    val title: String,
    val fullScreen: Boolean = false,
    val resizable: Boolean = true,
    val maximized: Boolean = true,
    val closable: Boolean = true,
    val width: Int = 800,
    val height: Int = 600,
    val theme: WindowTheme? = null
)

@Serializable
data class BuildConfig(
    val landingAsset: String = "index.html"
)
