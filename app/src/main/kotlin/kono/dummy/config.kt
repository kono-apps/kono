package kono.dummy

import kono.config.AppConfig
import kono.config.BuildConfig
import kono.config.KonoConfig
import kono.config.WindowConfig

val CONFIG = KonoConfig(
    app = AppConfig(
        name = "Kono",
        authors = listOf("Revxrsal"),
        version = "1.0"
    ),
    window = WindowConfig(
        title = "Kono"
    ),
    build = BuildConfig(
        directory = "../dist"
    )
)