package kono.assets

import kono.webview.Asset
import kono.webview.AssetCompression
import kono.webview.NoCompression

internal fun assetHandler(assets: MutableMap<String, Asset>.() -> Unit): AssetHandler {
    val fns = mutableMapOf<String, Asset>().also(assets)
    return AssetHandler(fns)
}

fun embeddedAsset(mimeType: String, path: String): Asset {
    return Asset(mimeType) {
        Asset::class.java.getResourceAsStream(path)!!.use { it.readAllBytes() }
    }
}

class AssetHandler(private val assets: MutableMap<String, Asset> = mutableMapOf()) {

    fun registerAsset(
        path: String,
        asset: Asset
    ) {
        if (assets.containsKey(path))
            error("An asset with name '$path' already exists!")
        assets[path] = asset
    }

    fun getAsset(path: String): Asset {
        return assets[path] ?: error("No such asset: '$path'. Have you added the codegen?")
    }
}