package kono.codegen.functions

import com.google.devtools.ksp.processing.CodeGenerator
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo
import kono.codegen.util.createGeneratedItem
import kono.ipc.FunctionHandler

fun createFunctionHandler(
    codeGenerator: CodeGenerator,
    exportedFunctions: Map<String, ExportedFun>,
) {
    codeGenerator.createGeneratedItem(forType = FunctionHandler::class) {
        beginControlFlow("return kono.ipc.functionHandler(app)")
        for ((name, function) in exportedFunctions) {
            beginControlFlow("this[%S] = { r, c -> ", name)
            addStatement("${function.generatedQualifiedName}(r, c)")
            endControlFlow()
        }
        endControlFlow() // functionHandler
    }
}

fun createFunctionsFile(
    codeGenerator: CodeGenerator,
    buildFunc: FileSpec.Builder.() -> Unit,
) {
    val builder = FileSpec.builder("kono.generated", "functions")
        .addImport("kotlinx.serialization", "serializer")
        .addImport("kotlinx.serialization", "encodeToString")
        .addImport("kotlinx.serialization.json", "Json")
        .addImport("kono.json.internal", "DEFAULT_CONSTRUCTOR_MARKER")
    builder.buildFunc()
    builder.build().writeTo(codeGenerator, aggregating = true)
}
