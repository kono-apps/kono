package kono.codegen

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * Provides instances of [KonoCodegenProcessor]
 */
class KonoProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return KonoCodegenProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger
        )
    }
}