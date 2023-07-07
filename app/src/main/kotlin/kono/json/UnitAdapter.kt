package kono.json

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

/**
 * Serializes results from functions that do not return anything
 */
object UnitAdapter : JsonAdapter<Unit?>() {
    override fun fromJson(reader: JsonReader) {
    }

    override fun toJson(writer: JsonWriter, value: Unit?) {
        writer.nullValue()
    }
}
