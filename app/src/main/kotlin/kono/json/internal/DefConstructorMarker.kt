package kono.json.internal

val DCM_CLASS: Class<*> by lazy {
    Class.forName("kotlin.jvm.internal.DefaultConstructorMarker")
}

val DEFAULT_CONSTRUCTOR_MARKER: Any by lazy {
    val ctr = DCM_CLASS.getDeclaredConstructor()
    ctr.isAccessible = true
    ctr.newInstance()
}
