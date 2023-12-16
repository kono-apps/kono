package kono.export

/**
 * Exports the given event so that it can be emitted from
 * both the back-end and the front-end. The front-end must
 * use the id in [ExportEvent.id].
 *
 * If the id is not specified, it will default to the class' simple
 * name. For example, `com.example.event.SomeEvent` will have its ID
 * as simply `SomeEvent`.
 *
 * Note that exported events must be serializable, either using
 * [kotlinx.serialization.Serializable] or other forms of serializers
 *
 * See [Serializers](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serializers.md)
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExportEvent(val id: String = "")
