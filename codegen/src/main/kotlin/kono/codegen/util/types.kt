package kono.codegen.util

import com.squareup.kotlinpoet.*

// From Moshi's codegen
//
// Licensed under Apache 2.0
//
// see https://github.com/square/moshi/blob/master/moshi-kotlin-codegen/src/main/java/com/squareup/moshi/kotlin/codegen/api/kotlintypes.kt#L84

internal fun TypeName.findRawType(): ClassName? {
    return when (this) {
        is ClassName -> this
        is ParameterizedTypeName -> rawType
        is LambdaTypeName -> {
            var count = parameters.size
            if (receiver != null) {
                count++
            }
            val functionSimpleName = if (count >= 23) {
                "FunctionN"
            } else {
                "Function$count"
            }
            ClassName("kotlin.jvm.functions", functionSimpleName)
        }

        else -> null
    }
}

internal fun TypeName.rawType(): ClassName {
    return findRawType() ?: throw IllegalArgumentException("Cannot get raw type from $this")
}

fun TypeName.isPrimitive(): Boolean {
    return when (this) {
        BOOLEAN, BYTE, SHORT, INT, LONG, CHAR, FLOAT, DOUBLE -> true
        else -> false
    }
}

@OptIn(DelicateKotlinPoetApi::class)
internal fun TypeName.asTypeBlock(): CodeBlock {
    if (annotations.isNotEmpty()) {
        return copy(annotations = emptyList()).asTypeBlock()
    }
    when (this) {
        is ParameterizedTypeName -> {
            return if (rawType == ARRAY) {
                val componentType = typeArguments[0]
                if (componentType is ParameterizedTypeName) {
                    // "generic" array just uses the component's raw type
                    // java.lang.reflect.Array.newInstance(<raw-type>, 0).javaClass
                    CodeBlock.of(
                        "%T.newInstance(%L, 0).javaClass",
                        Array::class.java.asClassName(),
                        componentType.rawType.asTypeBlock(),
                    )
                } else {
                    CodeBlock.of("%T::class.java", copy(nullable = false))
                }
            } else {
                rawType.asTypeBlock()
            }
        }

        is TypeVariableName -> {
            val bound = bounds.firstOrNull() ?: ANY
            return bound.asTypeBlock()
        }

        is LambdaTypeName -> return rawType().asTypeBlock()
        is ClassName -> {
            // Check against the non-nullable version for equality, but we'll keep the nullability in
            // consideration when creating the CodeBlock if needed.
            return when (copy(nullable = false)) {
                BOOLEAN, CHAR, BYTE, SHORT, INT, FLOAT, LONG, DOUBLE -> {
                    if (isNullable) {
                        // Remove nullable but keep the java object type
                        CodeBlock.of("%T::class.javaObjectType", copy(nullable = false))
                    } else {
                        CodeBlock.of("%T::class.javaPrimitiveType", this)
                    }
                }

                UNIT, Void::class.asTypeName(), NOTHING -> throw IllegalStateException("Parameter with void, Unit, or Nothing type is illegal")
                else -> CodeBlock.of("%T::class.java", copy(nullable = false))
            }
        }

        else -> throw UnsupportedOperationException("Parameter with type '${javaClass.simpleName}' is illegal. Only classes, parameterized types, or type variables are allowed.")
    }
}
