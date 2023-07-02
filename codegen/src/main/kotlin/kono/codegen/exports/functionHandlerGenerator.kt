package kono.codegen.exports

import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.buildCodeBlock

fun PropertySpec.Builder.createFunctionHandler(functions: Map<String, ExportedFunctionData>): PropertySpec {
    initializer(buildCodeBlock {
        beginControlFlow("kono.fns.functionHandler")
        functions.forEach { (jsName, fn) ->
            addStatement("this[%S] = { m, r, o -> %L }", jsName, "kono.generated.${fn.jvmName}(m, r, o)")
        }
        endControlFlow()
    })
    return build()
}