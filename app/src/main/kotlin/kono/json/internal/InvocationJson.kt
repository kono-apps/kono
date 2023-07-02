package kono.json.internal

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class InvocationJson<T>(
    @Json(name = "passed") val passedParameters: MutableList<String>,
    val data: T
)
