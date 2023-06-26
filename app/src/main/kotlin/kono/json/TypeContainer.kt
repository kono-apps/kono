package kono.json

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class TypeContainer<T> protected constructor() : Comparable<T> {

    val type: Type = javaClass.genericSuperclass.run {
        require(this is ParameterizedType) { "No type generic specified!" }
        actualTypeArguments[0]
    }

    override fun compareTo(other: T) = 0
}

inline fun <reified T> typeOf() = object : TypeContainer<T>() {}.type

inline fun <reified T> classOf() = T::class.java