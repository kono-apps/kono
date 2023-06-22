package kono.webview

/**
 * Represents an asset. This is generated (and compressed, if necessary) by the
 * codegen at compile-time.
 */
class Asset(
    val mimeType: String,
    content: ByteArray,
    private val compression: AssetCompression = NoCompression
) {

    constructor(mimeType: String, content: String) : this(
        mimeType = mimeType,
        content = content.toByteArray(),
        compression = NoCompression
    )

    val content by lazy {
        compression.decompress(content)
    }
}