package kono.codegen.exports

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.writeTo
import kono.export.ExportFunction

class FunctionProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        error(options.toString())
        val exportedFunctions = resolver.getSymbolsWithAnnotation(ExportFunction::class.java.name)
        if (exportedFunctions.none())
            return emptyList()
        val allFunctions = mutableMapOf<String, ExportedFunctionData>()
        val functionsFile = FileSpec
            .builder("kono.json.generated", "functions")
            .addImport(
                "kono.json",
                "adapterOf"
            )
            .addImport(
                "kono.json.internal",
                "InvocationJson",
                "DEFAULT_CONSTRUCTOR_MARKER"
            )
            .addImport("kono.fns", "FunctionInvocationException")
        // Masks code has been re-adapted from Moshi's codegen
        for (function in exportedFunctions) {
            if (function !is KSFunctionDeclaration)
                continue
            generateExportedFunction(
                function = function,
                resolver = resolver,
                logger = logger,
                allFunctions = allFunctions,
                addContentTo = functionsFile
            )
        }

        val functionHandler = createFunctionHandler(allFunctions)
        functionsFile.addProperty(functionHandler)

        val fileSpec = functionsFile.build()
        fileSpec.writeTo(codeGenerator, aggregating = true)

        return emptyList()
    }
}
