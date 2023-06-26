package kono

import kono.webview.Asset
import kono.webview.AssetCompression
import kono.webview.NoCompression

class AssetHandler {

    private val assets = mutableMapOf<String, Asset>()

    fun registerAsset(
        path: String,
        mimeType: String,
        content: ByteArray,
        compression: AssetCompression = NoCompression
    ) {
        println("zzzzzA")
        if (assets.containsKey(path))
            error("An asset with name '$path' already exists!")
        assets[path] = Asset(mimeType, content, compression)
    }

    fun getAsset(path: String): Asset {
        return assets[path] ?: error("No such asset: '$path'. Have you added the codegen?")
    }
}