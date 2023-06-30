package kono.codegen.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val DEFAULT_PROTOCOL = "kono"

@Serializable
data class KonoConfig(
    val app: AppConfig,
    val window: WindowConfig,
    val build: BuildConfig
)

@Serializable
data class AppConfig(
    val name: String,
    val authors: List<String>,
    val version: String
)

@Serializable
data class WindowConfig(
    val title: String,
    @SerialName("full-screen")
    val fullScreen: Boolean = false,
    val resizable: Boolean = true,
    val width: Int = 800,
    val height: Int = 600
)

@Serializable
data class BuildConfig(
    val protocol: String = DEFAULT_PROTOCOL,
    val directory: String
)