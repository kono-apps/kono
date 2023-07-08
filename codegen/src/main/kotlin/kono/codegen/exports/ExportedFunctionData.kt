package kono.codegen.exports

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ksp.toTypeName

class ExportedFunctionData(
    function: KSFunctionDeclaration,
    private val parameters: List<FunParameter>,
    val jvmName: String,
) {

    val packageName = function.packageName.asString()
    val returnType = function.returnType!!.resolve().toTypeName()

    val reflectionInvocationParameters by lazy {
        parameters.joinToString(",\n", "\n", "\n") {
            if (it.contextItem != null)
                "context${it.contextItem.access}"
            else
                "functionData.${it.name}"
        }
    }

    val normalInvocationParameters by lazy {
        parameters.joinToString(",\n", "\n", "\n") { param ->
            if (param.contextItem != null)
                "${param.name} = context${param.contextItem.access}"
            else
                buildString {
                    append("${param.name} = functionData.${param.name}")
                    if (param.hasDefault && !param.isNullable && !param.isPrimitive)
                        append("?: error(\"\"\"null was provided for non-null parameter ${param.name}\"\"\")")
                }
        }
    }

    val packagePrefix by lazy {
        when {
            packageName.isNotBlank() -> "$packageName."
            else -> ""
        }
    }
}