package kono.asset

enum class MimeType(val mimeType: String, vararg val extensions: String) {
    HTML("text/html", "html", "htm"),
    JAVASCRIPT("text/javascript", "js", "mjs"),
    CSS("text/css", "css", "less", "sass", "styl"),
    CSV("text/csv", "csv"),
    MP4("video/mp4", "mp4", "m4v"),
    MP3("audio/mp3", "mp3"),
    SVG("image/svg+xml", "svg"),
    ICO("image/vnd.microsoft.icon", "ico"),
    WASM("application/wasm", "wasm"),
    BINARY("application/octet-stream", "bin"),
    RTF("application/rtf", "rtf"),
    JSON("application/json", "json"),
    JSON_LD("application/ld+json", "jsonld"),
    TEXT("text/plain", "txt"),
    PNG("image/png", "png"),
    JPEG("image/jpeg", "jpg", "jpeg"),
    GIF("image/gif", "gif"),
    BMP("image/bmp", "bmp"),
    TRUETYPE_FONT("font/ttf", "ttf"),
    OPENTYPE_FONT("font/otf", "otf"),
    PDF("application/pdf", "pdf"),
    GZIP("application/gzip", "gz");

    companion object {

        private val byExtensions = buildMap {
            MimeType.values().forEach { mimeType ->
                mimeType.extensions.forEach { ext -> put(ext, mimeType) }
            }
        }

        fun fromExtension(extension: String): MimeType {
            return byExtensions[extension] ?: BINARY
        }
    }
}

private val byMimeType = MimeType.values().associateBy { it.mimeType }

/**
 * Converts this text to the [MimeType] that represents it,
 * for example `text/html` will return [MimeType.HTML].
 */
fun String.toMimeType(): MimeType? = byMimeType[this]