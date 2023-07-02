package kono.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.writeTo
import kono.app.KonoApplicationContext
import kono.codegen.exports.FunctionHandlerType
import kono.codegen.exports.createFunctionHandler
import kono.codegen.exports.parseExportedFunctions
import kono.fns.FunctionHandler

class KonoCodegenProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    private var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) {
            return emptyList()
        }
        invoked = true
        val propBuilder = PropertySpec.builder("functions", FunctionHandler::class, KModifier.OVERRIDE)

        // Handle @ExportFunctions
        val functions = parseExportedFunctions(resolver, logger, codeGenerator) {
            propBuilder.addOriginatingKSFile(it)
        }

        // Create a FunctionHandler
        val functionHandler = propBuilder.createFunctionHandler(functions = functions)

        val generatedContext = TypeSpec.classBuilder("GeneratedKonoContext")
            .addSuperinterface(KonoApplicationContext::class)
            .addProperty(functionHandler)
            .build()

        val file = FileSpec.builder("kono.generated", "context")
            .addType(generatedContext)
            .build()

        file.writeTo(codeGenerator, aggregating = true)

        return emptyList()
    }
}
