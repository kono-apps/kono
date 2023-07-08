package kono.codegen.functions

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * Processes functions annotated with [kono.export.ExportFunction]
 */
class FunctionProcessorProvider : SymbolProcessorProvider {

    /**
     * Called by Kotlin Symbol Processing to create the processor.
     */
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return FunctionProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger
        )
    }
}