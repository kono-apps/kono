package kono.codegen.util

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.*

/**
 * Package name that may not be used by listeners, events, or functions
 * as it is used internally
 */
const val RESERVED_PACKAGE = "kono.generated"

/**
 * Returns the default 'empty' value for the given type.
 *
 * - For numbers, this is zero
 * - For booleans this is false
 * - For chars this is '0',
 * - For anything else is null
 */
fun TypeName.getEmptyValue(): String {
    return when (this) {
        BOOLEAN -> "false"
        BYTE, SHORT, INT -> "0"
        LONG -> "0L"
        DOUBLE -> "0.0"
        FLOAT -> "0.0f"
        CHAR -> "'0'"
        else -> "null"
    }
}

/**
 * Returns all symbols with the given annotation
 */
inline fun <reified T : Annotation> Resolver.getSymbolsWithAnnotation(): Sequence<KSAnnotated> {
    return getSymbolsWithAnnotation(T::class.java.name)
}

/**
 * Returns whether this item is a suspend function or not
 */
fun KSDeclaration.isSuspend() = modifiers.contains(Modifier.SUSPEND)

/**
 * Tests whether the given declaration uses a reserved package name
 */
fun KSDeclaration.isReservedPackageName() = (packageName.asString().startsWith(RESERVED_PACKAGE))

/**
 * Gets the first annotation on the given element. This will
 * throw an error if the element does not have the annotation
 */
@OptIn(KspExperimental::class)
inline fun <reified T : Annotation> KSAnnotated.annotation(): T {
    return getAnnotationsByType(T::class).first()
}

/**
 * Gets the first annotation on the given element, or null
 * if the element does not have the annotation
 */
@OptIn(KspExperimental::class)
inline fun <reified T : Annotation> KSAnnotated.annotationOrNull(): T? {
    return getAnnotationsByType(T::class).firstOrNull()
}