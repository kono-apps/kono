package kono.codegen.exports

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.buildCodeBlock

fun FunSpec.Builder.createFunctionHandler(functions: Map<String, ExportedFunctionData>): FunSpec {
    addCode(buildCodeBlock {
        beginControlFlow("return kono.ipc.functionHandler(app)")
        functions.forEach { (jsName, fn) ->
            beginControlFlow("this[%S] = { m, r, o, c -> ", jsName)
//            if (fn.isSuspend) {
//                beginControlFlow("c.coroutineScope!!.async")
//                addStatement("kono.generated.${fn.jvmName}(m, r, o, c)")
//                endControlFlow() // async
//                addStatement(".await()")
//            } else
                addStatement("kono.generated.${fn.jvmName}(m, r, o, c)")
            endControlFlow() // this[]
        }
        endControlFlow() // functionHandler
    })
    return build()
}