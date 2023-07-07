package kono.sample

import com.squareup.moshi.JsonClass
import kono.events.Listener
import kono.export.ExportEvent

@ExportEvent
@JsonClass(generateAdapter = true)
data class SomeEvent(
    val name: String,
)

@Listener
fun handle(event: SomeEvent) {
    println(event)
}