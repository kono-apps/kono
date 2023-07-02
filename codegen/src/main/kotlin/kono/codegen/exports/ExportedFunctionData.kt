package kono.codegen.exports

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.ksp.toTypeName

class ExportedFunctionData(
    function: KSFunctionDeclaration,
    private val parameters: List<FunParameter>,
    val jvmName: String,
    val jsName: String = jvmName
) {

    val packageName = function.packageName.asString()
    val returnType = function.returnType!!.resolve().toTypeName()

    fun isUnit() = returnType == UNIT

    val reflectionInvocationParameters by lazy {
        parameters.joinToString(",\n", "\n", "\n") {
            "invocation.data.${it.name}"
        }
    }

    val normalInvocationParameters by lazy {
        parameters.joinToString(",\n", "\n", "\n") { param ->
            buildString {
                append("${param.name} = invocation.data.${param.name}")
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