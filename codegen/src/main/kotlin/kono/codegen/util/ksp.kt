package kono.codegen.util

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import kotlin.reflect.KClass


fun Resolver.getSymbolsWithAnnotation(annotationType: KClass<out Annotation>): Sequence<KSAnnotated> {
    return getSymbolsWithAnnotation(annotationType.java.name)
}
