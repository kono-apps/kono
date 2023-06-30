package kono.codegen

import com.akuleshov7.ktoml.Toml
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.writeTo
import kono.codegen.assets.generateAssetsProperty
import kono.codegen.config.KonoConfig
import kono.codegen.config.generateConfigProperty
import kono.codegen.exports.FunctionHandlerType
import kono.codegen.exports.createFunctionHandler
import kono.codegen.exports.parseExportedFunctions
import kotlinx.serialization.decodeFromString
import java.io.File

const val PROJECT_DIR = "kono:projectDir"
const val PROJECT_CONFIG = "kono:config"
val APP_CONTEXT = ClassName("kono", "KonoApplicationContext")

class KonoCodegenProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    private var invoked = false

    val directory = File(options[PROJECT_DIR] ?: error("Have you applied the 'kono.app' Gradle plugin?"))

    val config = run {
        val config = directory.resolve(options[PROJECT_CONFIG] ?: "kono.toml")
        Toml.decodeFromString<KonoConfig>(config.readText())
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) {
            return emptyList()
        }
        invoked = true
        val propBuilder = PropertySpec.builder("functions", FunctionHandlerType, KModifier.OVERRIDE)

        // Handle @ExportFunctions
        val functions = parseExportedFunctions(resolver, logger, codeGenerator) {
            propBuilder.addOriginatingKSFile(it)
        }

        // Create a FunctionHandler
        val functionHandler = propBuilder.createFunctionHandler(functions = functions)

        val configProp = generateConfigProperty(config)

        val assetsProp = generateAssetsProperty(directory, config)

        val generatedContext = TypeSpec.classBuilder("GeneratedKonoContext")
            .addSuperinterface(APP_CONTEXT)
            .addProperty(functionHandler)
            .addProperty(configProp)
            .addProperty(assetsProp)
            .build()

        val file = FileSpec.builder("kono.generated", "context")
            .addType(generatedContext)
            .build()

        file.writeTo(codeGenerator, aggregating = true)

        return emptyList()
    }
}
