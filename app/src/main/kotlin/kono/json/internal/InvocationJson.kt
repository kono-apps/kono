package kono.json.internal

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class InvocationJson<T>(
    @Json(name = "p") val passedParameters: MutableList<String>,
    @Json(name = "d") val data: T
)
