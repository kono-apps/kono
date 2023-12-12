package kono.sample

import kono.events.Listener
import kono.export.ExportEvent
import kotlinx.serialization.Serializable

@ExportEvent
@Serializable
data class SomeEvent(
    val name: String,
)

@Listener
fun handle(event: SomeEvent) {
    println(event)
}