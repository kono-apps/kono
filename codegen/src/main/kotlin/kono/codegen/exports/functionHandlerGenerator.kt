package kono.codegen.exports

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.buildCodeBlock

private val FunctionHandlerType = ClassName("kono.fns", "FunctionHandler")

fun createFunctionHandler(functions: Map<String, ExportedFunctionData>): PropertySpec {
    return PropertySpec.builder("GeneratedFunctionHandler", FunctionHandlerType)
        .initializer(buildCodeBlock {
            beginControlFlow("kono.fns.functionHandler")
            functions.forEach { (jsName, fn) ->
                addStatement("this[%S] = { m, r -> %L }", jsName, "kono.json.generated.${fn.jvmName}(m, r)")
            }
            endControlFlow()
        })
        .build()
}