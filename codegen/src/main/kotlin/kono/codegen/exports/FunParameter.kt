package kono.codegen.exports

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName
import kono.codegen.util.asTypeBlock
import kono.codegen.util.isPrimitive

class FunParameter(
    parameter: KSValueParameter
) {
    val type: KSType = parameter.type.resolve()
    val hasDefault = parameter.hasDefault
    val name: String = parameter.name!!.asString()
    val isNullable: Boolean = type.isMarkedNullable
    val typeName = type.toTypeName()
    val isPrimitive = typeName.isPrimitive()
    val typeBlock = typeName.asTypeBlock()
}

fun FunParameter.getValueForNull(): String {
    return when (typeName) {
        BOOLEAN -> "false"
        BYTE, SHORT, INT -> "0"
        LONG -> "0L"
        DOUBLE -> "0.0"
        FLOAT -> "0.0f"
        CHAR -> "'0'"
        else -> "null"
    }
}

fun FunParameter.toParameterSpec(): ParameterSpec {
    val paramType = type.run {
        if (hasDefault && !isPrimitive)
            makeNullable()
        else
            this
    }
    val paramTypeName = paramType.toTypeName()

    val propBuilder = ParameterSpec.builder(name, paramTypeName)
    if (hasDefault)
        propBuilder.defaultValue(getValueForNull())
    return propBuilder.build()
}