package kono.webview

/**
 * Represents an asset. This is generated (and compressed, if necessary) by the
 * codegen at compile-time.
 */
class Asset(
    val mimeType: String,
    content: () -> ByteArray,
) {

    val content by lazy { content() }
}