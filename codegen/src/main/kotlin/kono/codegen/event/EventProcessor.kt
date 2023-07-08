package kono.codegen.event

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import kono.codegen.util.getSymbolsWithAnnotation
import kono.events.Listener
import kono.export.ExportEvent

/**
 * Processes functions annotated with [kono.events.Listener] and
 * types annotated with [kono.export.ExportEvent]
 */
class EventProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    private var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked)
            return emptyList()
        invoked = true
        createEventsHandler(
            resolver = resolver,
            codeGenerator = codeGenerator,
            logger = logger
        )
        return emptyList()
    }

}
