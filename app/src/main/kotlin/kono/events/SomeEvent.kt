package kono.events

import com.squareup.moshi.JsonClass

@Event(name = "someEvent")
@JsonClass(generateAdapter = true)
data class SomeEvent(
    val name: String
)