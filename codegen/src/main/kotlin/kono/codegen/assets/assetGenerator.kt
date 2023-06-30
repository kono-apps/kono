package kono.codegen.assets

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.buildCodeBlock
import kono.codegen.config.KonoConfig
import java.io.File
import kotlin.io.path.relativeTo

private val AssetsHandlerType = ClassName("kono.assets", "AssetHandler")
private val AssetType = ClassName("kono.webview", "Asset")

fun generateAssetsProperty(
    baseDir: File,
    config: KonoConfig
): PropertySpec {
    return PropertySpec.builder("assets", AssetsHandlerType, KModifier.OVERRIDE)
        .also {
            val assetsDir = File(baseDir, config.build.directory)
            val assetsPath = assetsDir.toPath()
            it.initializer(buildCodeBlock {
                beginControlFlow("kono.assets.assetHandler")
                for (asset in assetsDir.walkTopDown()) {
                    if (asset.isDirectory) continue
                    val path = '/' + asset.toPath().relativeTo(assetsPath).toString().replace('\\', '/')
                    val mimeType = when (asset.extension) {
                        "html" -> "text/html"
                        "js", "mjs" -> "text/js"
                        "css", "less", "sass", "styl" -> "text/css"
                        "csv" -> "text/csv"
                        "mp4", "m4v" -> "video/mp4"
                        "mp3" -> "audio/mp3"
                        "svg" -> "image/svg+xml"
                        "ico" -> "image/vnd.microsoft.icon"
                        "bin" -> "application/octet-stream"
                        "rtf" -> "application/rtf"
                        "json" -> "application/json"
                        "jsonld" -> "application/ld+json"
                        "txt" -> "text/plain"
                        "png" -> "image/png"
                        else -> "application/octet-stream"
                    }

                    addStatement(
                        """this[%S] = kono.assets.embeddedAsset(
                        | mimeType = %S,
                        | path = %S,
                        |)""".trimMargin(),
                        path,
                        mimeType,
                        path,
                    )
                }
                endControlFlow() // assetHandler
            })
        }
        .build()
}