package kono.codegen.assets

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.buildCodeBlock
import kono.asset.MimeType
import kono.asset.toMimeType
import kono.codegen.config.KonoConfig
import java.io.File
import kotlin.io.path.relativeTo

private val AssetsHandlerType = ClassName("kono.asset", "AssetHandler")
private val AssetType = ClassName("kono.webview", "Asset")
private val MimeTypeClass = ClassName("kono.asset", "MimeType")

fun generateAssetsProperty(
    baseDir: File,
    config: KonoConfig
): PropertySpec {
    return PropertySpec.builder("assets", AssetsHandlerType, KModifier.OVERRIDE)
        .also {
            val assetsDir = File(baseDir, config.build.directory)
            val assetsPath = assetsDir.toPath()
            it.initializer(buildCodeBlock {
                beginControlFlow("kono.asset.assetHandler")
                for (asset in assetsDir.walkTopDown()) {
                    if (asset.isDirectory) continue
                    val path = '/' + asset.toPath().relativeTo(assetsPath).toString().replace('\\', '/')
                    val mimeType = MimeType.fromExtension(asset.extension)

                    addStatement(
                        """this[%S] = kono.asset.embeddedAsset(
                        | mimeType = %T.%L,
                        | path = %S,
                        |)""".trimMargin(),
                        path,
                        MimeTypeClass,
                        mimeType.name,
                        path,
                    )
                }
                endControlFlow() // assetHandler
            })
        }
        .build()
}