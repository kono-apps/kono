package kono.asset

import kono.runtime.natives.nativeRuntime

/**
 * Represents an asset. This includes anything that is passed to the webview,
 * such as HTML, CSS, JS, and any other files.
 *
 * Note: This maintains a pointer to an underlying asset. If this
 * object is garbage collected, the pointer remains uncleaned. We should
 * consider using Java's new Cleaner utility to avoid memory leaks
 */
class Asset(
    private val mimeType: MimeType,
    getContent: () -> ByteArray,
) {

    private val content by lazy {
        getContent()
    }

    internal val assetPtr by lazy {
        nativeRuntime {
            createAsset(mimeType.mimeType, content, content.size)
        }
    }
}