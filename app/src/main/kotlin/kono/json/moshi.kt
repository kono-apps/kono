package kono.json

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

// A hacky way to get an adapter quickly, without kotlin-reflect
inline fun <reified T> Moshi.adapterOf(): JsonAdapter<T> {
    return adapter(typeOf<T>())
}
