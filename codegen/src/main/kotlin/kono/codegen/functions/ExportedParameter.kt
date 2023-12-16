package kono.codegen.functions

import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toTypeName
import kono.codegen.util.addAnnotations
import kono.codegen.util.asTypeBlock
import kono.codegen.util.getEmptyValue
import kono.codegen.util.isPrimitive

/**
 * Represents a parameter in an exported function
 */
class ExportedParameter(parameter: KSValueParameter) {

    /**
     * The parameter name
     */
    val name = parameter.name!!.asString()

    /**
     * The parameter type
     */
    val type = parameter.type.resolve()

    /**
     * Whether this parameter has any default value
     */
    val hasDefault = parameter.hasDefault

    /**
     * Whether this parameter takes null values
     */
    val isNullable = type.isMarkedNullable

    /**
     * The [com.squareup.kotlinpoet.TypeName] for this parameter, used in generating
     * files
     */
    private val typeName = type.toTypeName()

    /**
     * Whether this parameter is a primitive type
     */
    val isPrimitive = typeName.isPrimitive()

    /**
     * The expression used to get the type's reflection Class,
     * for example `Object::class.java`, or `Int::class.javaPrimitiveType`.
     *
     * This will respect primitive types
     */
    val typeBlock by lazy(LazyThreadSafetyMode.NONE) { typeName.asTypeBlock() }

    /**
     * If this parameter is a context parameter (fetched from
     * a [kono.ipc.FunctionContext]), this will be the context's property
     * name
     */
    val contextItem = ContextParameter.fromType(typeName)

}

/**
 * Returns the expression for accessing this property. This will also
 * contain null-checks
 */
fun ExportedParameter.getAccessProperty() = buildCodeBlock {
    val access = if (contextItem != null)
        "context${contextItem.access}"
    else
        "functionData.$name"
    add(access)

    if (contextItem?.isNullable == true && !isNullable)
        add(" ?: error(%S)", "unable to fetch value '$name' from the context (it was null)")
    else if (hasDefault && !isNullable && !isPrimitive)
        add(" ?: error(%S)", "null was provided for non-null parameter '$name'")
}

/**
 * Returns whether this parameter is a context parameter or not.
 *
 * See [ExportedParameter.contextItem]
 */
val ExportedParameter.isFromContext get() = contextItem != null

/**
 * Returns a [ParameterSpec] that represents this parameter, respecting
 * nullable values and defaults if any.
 */
fun ExportedParameter.toJsonConstructorParameter(): ParameterSpec {
    val paramTypeName = if (hasDefault && !isPrimitive)
        type.makeNullable().toTypeName()
    else
        type.toTypeName()

    paramTypeName.addAnnotations(type.annotations.map { it.toAnnotationSpec() })

    val propBuilder = ParameterSpec.builder(name, paramTypeName)
    if (hasDefault)
        propBuilder.defaultValue(paramTypeName.getEmptyValue())

    return propBuilder.build()
}