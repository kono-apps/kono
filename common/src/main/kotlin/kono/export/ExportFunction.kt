package kono.export

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExportFunction(
    val name: String = ""
)