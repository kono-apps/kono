package kono.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.writeTo
import kono.app.KonoApplication
import kono.app.KonoApplicationContext
import kono.codegen.exports.createFunctionHandler
import kono.codegen.exports.createEventHandler
import kono.codegen.exports.parseExportedFunctions
import kono.events.EventHandler
import kono.export.ExportEvent
import kono.ipc.FunctionHandler

class KonoCodegenProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    private var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) {
            return emptyList()
        }
        invoked = true

        val funHandler = FunSpec.builder("createFunctionHandler")
            .addParameter("app", KonoApplication::class)
            .addModifiers(KModifier.OVERRIDE)
            .returns(FunctionHandler::class)

        // Handle @ExportFunctions
        val exportedFunctions = parseExportedFunctions(resolver, logger, codeGenerator) {
            funHandler.addOriginatingKSFile(it)
        }

        // TODO: we should make the event handler in a different file. KSP
        // will mark symbols annotated with @ExportEvent/@Listen as empty
        // if they weren't changed.
        val eventsProp = FunSpec.builder("createEventHandler")
            .addParameter("app", KonoApplication::class)
            .returns(EventHandler::class)
            .addModifiers(KModifier.OVERRIDE)

        val exportedEvents = resolver.getSymbolsWithAnnotation(ExportEvent::class.qualifiedName!!)
        val eventsHandler = eventsProp.createEventHandler(exportedEvents, resolver, logger)

        // Create a FunctionHandler
        val functionHandler = funHandler.createFunctionHandler(functions = exportedFunctions)

        val generatedContext = TypeSpec.classBuilder("GeneratedKonoContext")
            .addSuperinterface(KonoApplicationContext::class)
            .addFunction(functionHandler)
            .addFunction(eventsHandler)
            .build()

        val file = FileSpec.builder("kono.generated", "context")
//            .addImport("kotlinx.coroutines", "async")
            .addType(generatedContext)
            .build()

        file.writeTo(codeGenerator, aggregating = true)

        return emptyList()
    }
}
