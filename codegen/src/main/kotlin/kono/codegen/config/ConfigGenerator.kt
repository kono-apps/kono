package kono.codegen.config

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.buildCodeBlock

private val ConfigType = ClassName("kono.config", "KonoConfig")
private val AppConfigType = ClassName("kono.config", "AppConfig")
private val WindowConfigType = ClassName("kono.config", "WindowConfig")
private val BuildConfigType = ClassName("kono.config", "BuildConfig")

fun generateConfigProperty(config: KonoConfig): PropertySpec {
    return PropertySpec.builder("config", ConfigType, KModifier.OVERRIDE)
        .initializer(buildCodeBlock {
            add(
                """%T(
                app = %T(
                    name = %S,
                    authors = listOf(%L),
                    version = %S
                ),
                build = %T(
                    protocol = %S,
                    directory = %S
                ),
                window = %T(
                    title = %S,
                    fullScreen = %L,
                    resizable = %L,
                    width = %L,
                    height = %L
                )
            )""",
                ConfigType,
                AppConfigType,
                config.app.name,
                config.app.authors.joinToString(", ") { "\"\"\"$it\"\"\"" },
                config.app.version,
                BuildConfigType,
                config.build.protocol,
                config.build.directory,

                WindowConfigType,
                config.window.title,
                config.window.fullScreen,
                config.window.resizable,
                config.window.width,
                config.window.height,
            )
        })
        .build()
}