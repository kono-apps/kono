package kono.codegen.exports

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import kono.export.ExportFunction
import kono.codegen.kpoet.*
import java.lang.reflect.Method

private val JsonClassType = ClassName("com.squareup.moshi", "JsonClass")
private val MoshiType = ClassName("com.squareup.moshi", "Moshi")
private val JsonReaderType = ClassName("com.squareup.moshi", "JsonReader")
private val JsonWriterType = ClassName("com.squareup.moshi", "JsonWriter")

private val INT_TYPE_BLOCK = CodeBlock.of("%T::class.javaPrimitiveType", INT)
private val DefaultConstructorMarkerType = CodeBlock.of("java.lang.Object::class.java")

private val JSON_GENERATE_ADAPTER = annotationBuilder(JsonClassType) {
    addMember("generateAdapter = true")
}

fun parseExportedFunctions(
    resolver: Resolver,
    logger: KSPLogger,
    codeGenerator: CodeGenerator,
    addOriginatingFiles: (KSFile) -> Unit
): Map<String, ExportedFunctionData> {
    val exportedFunctions = resolver.getSymbolsWithAnnotation(ExportFunction::class.java.name)
    if (exportedFunctions.none())
        return emptyMap()
    val allFunctions = mutableMapOf<String, ExportedFunctionData>()
    val functionsFile = FileSpec
        .builder("kono.generated", "functions")
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
        addOriginatingFiles(function.containingFile!!)
    }
    val fileSpec = functionsFile.build()
    fileSpec.writeTo(codeGenerator, aggregating = true)
    return allFunctions
}

@OptIn(KspExperimental::class)
fun generateExportedFunction(
    function: KSFunctionDeclaration,
    resolver: Resolver,
    logger: KSPLogger,
    allFunctions: MutableMap<String, ExportedFunctionData>,
    addContentTo: FileSpec.Builder
) {

    val jvmClassName = resolver.getOwnerJvmClassName(function)!!
    val annotation = function.getAnnotationsByType(ExportFunction::class).first()

    val jsName = annotation.name.ifBlank { resolver.getJvmName(function)!! }
    val jvmName = resolver.getJvmName(function)!!

    val returnType = function.returnType!!.resolve().toTypeName()

    if (allFunctions.containsKey(jsName))
        logger.error("Found 2 exported functions with the same name: '$jsName'. Names must be unique", function)

    if (!function.isPublic())
        logger.error("Exported functions must be public!", function)

    val parameters = function.parameters.map { FunParameter(it) }

    val functionData = ExportedFunctionData(
        function,
        parameters,
        jvmName
    )

    if (functionData.packageName == "kono.generated")
        logger.error("'kono.generated' is a preserved package name and may not be used.", function)

    allFunctions[jsName] = functionData

    if (parameters.isEmpty()) {
        val generated = functionData.generateNoArg(function.containingFile!!)
        addContentTo.addFunction(generated)
        return
    }

    val hasDefaultParams = parameters.any { it.hasDefault }

    val nameAllocator = NameAllocator()

    // Calculate how many masks we'll need. Round up if it's not evenly divisible by 32
    val maskCount = if (parameters.isEmpty()) {
        0
    } else {
        (parameters.size + 31) / 32
    }

    val maskNames = Array(maskCount) { index ->
        nameAllocator.newName("mask$index")
    }
    val maskAllSetValues = Array(maskCount) { -1 }
    var maskIndex = 0
    var maskNameIndex = 0
    val updateMaskIndexes = {
        maskIndex++
        if (maskIndex == 32) {
            // Move to the next mask
            maskIndex = 0
            maskNameIndex++
        }
    }

    val properties = parameters.map { it.toParameterSpec() }

    val wrapperJsonClass = classBuilder("FnJson_$jvmName") {
        addAnnotation(JSON_GENERATE_ADAPTER)
        primaryConstructor(properties)
    }

    if (hasDefaultParams) {
        addContentTo.addProperty(
            propertySpec = generateReflectionMethod(
                parameters,
                maskCount,
                jvmName,
                jvmClassName
            )
        )
    }

    val wrapperFunction = funBuilder(jvmName) {
        addOriginatingKSFile(function.containingFile!!)
        addParameter("moshi", MoshiType)
        addParameter("input", JsonReaderType)
        addParameter("output", JsonWriterType)
        beginControlFlow("try")
        addStatement(
            "val invocation = moshi.adapterOf<InvocationJson<${wrapperJsonClass.name}>>().fromJson(input)!!"
        )
        if (hasDefaultParams) {
            addStatement("val passedParameters = invocation.passedParameters")
            addStatement("var name = passedParameters.removeFirstOrNull()")
            // Initialize all our masks, defaulting to fully unset (-1)
            for (maskName in maskNames) {
                addStatement("var %L = -1", maskName)
            }
            beginControlFlow("while (name != null)")
            beginControlFlow("when (name)")

            for (parameter in parameters) {
                beginControlFlow("%S ->", parameter.name)

                if (parameter.hasDefault) {
                    val inverted = (1 shl maskIndex).inv()
                    maskAllSetValues[maskNameIndex] = maskAllSetValues[maskNameIndex] and inverted
                    addComment("\$mask = \$mask and (1 shl %L).inv()", maskIndex)
                    addStatement(
                        "%1L = %1L and 0x%2L.toInt()",
                        maskNames[maskNameIndex],
                        Integer.toHexString(inverted),
                    )
                }

                endControlFlow()
                updateMaskIndexes()
            }
            endControlFlow() // when
            addStatement("name = passedParameters.removeFirstOrNull()")
            endControlFlow() // while

            val allMasksAreSetBlock = maskNames.withIndex().map { (index, maskName) ->
                CodeBlock.of("$maskName·== 0x${Integer.toHexString(maskAllSetValues[index])}.toInt()")
            }.joinToCode("·&& ")

            beginControlFlow("if (%L)", allMasksAreSetBlock)
            addComment("All parameters are provided. No need to use the synthetic method!")
            invokeWithFullParams(functionData, jvmName, returnType)
            endControlFlow()

            addComment("Different parameters were used. Invoke the synthetic method")
            if (functionData.isUnit()) {
                if (parameters.isEmpty())
                    addStatement("val result = `synthetic$$jvmName`.invoke(null, mask0, DEFAULT_CONSTRUCTOR_MARKER)")
                else
                    addStatement(
                        "val result = `synthetic$$jvmName`.invoke(null, %L, mask0, DEFAULT_CONSTRUCTOR_MARKER)",
                        functionData.reflectionInvocationParameters
                    )
                addStatement("output.beginObject().endObject()")
            } else {
                if (parameters.isEmpty())
                    addStatement("val result = `synthetic$$jvmName`.invoke(null, mask0, DEFAULT_CONSTRUCTOR_MARKER)")
                else
                    addStatement(
                        "val result = `synthetic$$jvmName`.invoke(null, %L, mask0, DEFAULT_CONSTRUCTOR_MARKER) as %T",
                        functionData.reflectionInvocationParameters, returnType
                    )
                addStatement("moshi.adapterOf<%T>().toJson(output, result)", returnType)
            }
        } else {
            invokeWithFullParams(functionData, jvmName, returnType)
        }
        endControlFlow() // try
        beginControlFlow("catch (ex: com.squareup.moshi.JsonDataException)")
        addStatement(
            "throw FunctionInvocationException(ex.message ?: %S, ex.cause)",
            "failed to invoke function '$jvmName'"
        )
        endControlFlow()
    }
    addContentTo.addFunction(wrapperFunction)
    addContentTo.addType(wrapperJsonClass)
}


private fun ExportedFunctionData.generateNoArg(containingFile: KSFile) = funBuilder(jvmName) {
    addOriginatingKSFile(containingFile)
    addParameter("moshi", MoshiType)
    addParameter("input", JsonReaderType)
    addParameter("output", JsonWriterType)
    if (isUnit()) {
        addStatement(buildString {
            append(packagePrefix)
            append(jvmName)
            append("()")
        })
        addStatement("output.beginObject().endObject()")
    } else {
        addStatement("val result = " + buildString {
            append(packagePrefix)
            append(jvmName)
            append("()")
        })
        addStatement("moshi.adapterOf<%T>().toJson(output, result)", returnType)
    }
}


private fun FunSpec.Builder.invokeWithFullParams(
    functionData: ExportedFunctionData,
    name: String,
    returnType: TypeName
) {
    if (functionData.isUnit()) {
        addStatement(buildString {
            append(functionData.packagePrefix)
            append(name)
            append("(%L)")
        }, functionData.normalInvocationParameters)
        addStatement("output.beginObject().endObject()")
    } else {
        addStatement("val result = " + buildString {
            append(functionData.packagePrefix)
            append(name)
            append("(%L)")
        }, functionData.normalInvocationParameters)
        addStatement("moshi.adapterOf<%T>().toJson(output, result)", returnType)
    }
}

private fun generateReflectionMethod(
    parameters: List<FunParameter>,
    maskCount: Int,
    name: String,
    jvmClassName: String,
): PropertySpec {
    val methodArguments = mutableListOf<CodeBlock>()
    parameters.forEach { methodArguments.add(it.typeBlock) }
    val args = methodArguments
        .plus(0.until(maskCount).map { INT_TYPE_BLOCK }) // Masks, one every 32 params
        .plus(DefaultConstructorMarkerType)  // Default constructor marker is always last
        .joinToCode(", ")
    return PropertySpec.builder("synthetic$$name", Method::class, KModifier.PRIVATE)
        .delegate(buildCodeBlock {
            beginControlFlow("lazy")
            addComment("Call the synthetic method produced by the Kotlin compiler")
            addStatement("Class.forName(%S).getDeclaredMethod(%S, %L)", jvmClassName, "$name\$default", args)
            endControlFlow()
        })
        .build()
}
