package kono.asset

import kotlin.io.path.Path
import kotlin.io.path.invariantSeparatorsPathString

private fun String.toAssetPath(): String {
    val path = Path(this).invariantSeparatorsPathString
    return if (path.startsWith('/')) path
    else "/$path"
}

class AssetHandler(private val landingAsset: String) {

    private val assets = HashMap<String, Asset>()

    fun addAsset(path: String, asset: Asset) {
        val assetPath = path.toAssetPath()
        if (assets.containsKey(assetPath))
            error("An asset with name '$path' already exists!")
        assets[assetPath] = asset
    }

    fun getAsset(path: String): Asset {
        val assetPath = path.toAssetPath()
        return assets[assetPath] ?: error("No such asset: $assetPath")
    }

    fun getAssetOrNull(path: String): Asset? {
        val assetPath = path.toAssetPath()
        return assets[assetPath]
    }

    fun hasAsset(path: String): Boolean {
        val assetPath = path.toAssetPath()
        return assets.containsKey(assetPath)
    }

    fun loadEmbeddedAsset(path: String): Asset {
        val assetPath = (if (path == "/") landingAsset else path).toAssetPath()
        return assets.computeIfAbsent(assetPath) {
            val mimeType = MimeType.fromExtension(assetPath.substringAfterLast('.', ""))
            val stream = javaClass.getResourceAsStream(assetPath) ?: error {
                "Cannot find embedded asset $assetPath. Have you enabled the Gradle plugin?"
            }
            val content = stream.use { it.readAllBytes() }
            Asset(mimeType) { content }
        }
    }
}
