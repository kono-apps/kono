package kono.codegen.event

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * Processes functions annotated with [kono.events.Listener] and
 * types annotated with [kono.export.ExportEvent]
 */
class EventProcessorProvider : SymbolProcessorProvider {

    /**
     * Called by Kotlin Symbol Processing to create the processor.
     */
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return EventProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger
        )
    }
}