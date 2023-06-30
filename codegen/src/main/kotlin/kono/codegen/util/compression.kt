package kono.codegen.util

import java.io.ByteArrayOutputStream
import java.util.zip.DeflaterOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import java.util.zip.InflaterInputStream

/**
 * Compresses the given string
 */
fun AssetCompression.compressString(string: String): ByteArray {
    return compress(string.toByteArray(Charsets.UTF_8))
}

/**
 * Decompresses the given string
 */
fun AssetCompression.decompressString(compressed: ByteArray): String {
    return decompress(compressed).decodeToString()
}

/**
 * An asset compression strategy
 */
interface AssetCompression {

    /**
     * Compresses this asset
     */
    fun compress(content: ByteArray): ByteArray

    /**
     * Decompresses this asset
     */
    fun decompress(content: ByteArray): ByteArray
}

/**
 * No compression. The same byte array is returned
 */
object NoCompression : AssetCompression {
    override fun compress(content: ByteArray) = content
    override fun decompress(content: ByteArray) = content
}

/**
 * GZip asset compression
 */
object GZIPCompression : AssetCompression {
    override fun compress(content: ByteArray): ByteArray {
        val output = ByteArrayOutputStream()
        GZIPOutputStream(output).use {
            it.write(content)
        }
        return output.toByteArray()
    }

    override fun decompress(content: ByteArray): ByteArray {
        content.inputStream().use { inputStream ->
            return GZIPInputStream(inputStream).use { it.readBytes() }
        }
    }
}

/**
 * ZLib asset compression
 */
object ZLibCompression : AssetCompression {

    override fun compress(content: ByteArray): ByteArray {
        val output = ByteArrayOutputStream()
        DeflaterOutputStream(output).use {
            it.write(content)
        }
        return output.toByteArray()
    }

    override fun decompress(content: ByteArray): ByteArray {
        content.inputStream().use { inputStream ->
            return InflaterInputStream(inputStream).use { it.readBytes() }
        }
    }
}