package kono.codegen.functions

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import kono.codegen.util.*
import kono.export.ExportFunction

class FunctionProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    private var invoked = false

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked)
            return emptyList()
        invoked = true
        val functions = mutableMapOf<String, ExportedFun>()
        createFunctionsFile(codeGenerator = codeGenerator) {
            for (function in resolver.getSymbolsWithAnnotation<ExportFunction>()) {
                if (function !is KSFunctionDeclaration)
                    continue

                val functionName = function.simpleName.asString()
                val exportedName = function.annotation<ExportFunction>().name.ifBlank { functionName }
                val containingJavaClass = resolver.getOwnerJvmClassName(function)!!

                if (functions.containsKey(exportedName))
                    logger.error("Found multiple exported functions with name '$functionName'", function)

                if (!function.isPublic())
                    logger.error("Exported function must be public!", function)

                if (function.isSuspend())
                    logger.error("Exported functions cannot be marked as suspend!", function)

                if (function.isReservedPackageName())
                    logger.error("Package name '$RESERVED_PACKAGE' is reserved and may not be used.", function)

                val exportedFun = ExportedFun(
                    function = function,
                    containingJavaClass = containingJavaClass
                )

                exportedFun.addTo(file = this)

                functions[exportedName] = exportedFun
            }
        }

        // Create the GeneratedFunctionHandler class that contains all the
        // generated functions mapped to their exported names
        createFunctionHandler(
            codeGenerator = codeGenerator,
            exportedFunctions = functions
        )
        return emptyList()
    }
}

