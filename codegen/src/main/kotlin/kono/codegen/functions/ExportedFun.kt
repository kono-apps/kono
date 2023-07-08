package kono.codegen.functions

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.moshi.Moshi
import kono.codegen.util.*
import kono.ipc.FunctionContext
import kono.ipc.RunFunctionRequest
import java.lang.reflect.Method

/**
 * Represents the data of an exported function
 *
 * [function] the underlying KSP function
 * [containingJavaClass] The name of the JVM class that contains the
 * function
 */
class ExportedFun(
    val function: KSFunctionDeclaration,
    val containingJavaClass: String,
) {

    /**
     * The name of the function
     */
    val name = function.simpleName.asString()

    /**
     * The function's return type as a [com.squareup.kotlinpoet.TypeName]
     */
    val returnType = function.returnType!!.resolve().toTypeName()

    /**
     * The function's qualified name, i.e. `com.example.function`
     */
    val qualifiedName = function.qualifiedName!!.asString()

    /**
     * The function's parameters
     */
    val parameters = function.parameters.map { ExportedParameter(it) }

    /**
     * Whether the function has any default values
     */
    val containsDefaultParameters = parameters.any { it.hasDefault }

}

/**
 * Returns a return type that is safe to cast to Java objects.
 *
 * If a function had no return type, casting `null` to a `Unit` would
 * fail as `Unit` is not nullable. This will simply make
 * this type nullable if it returns `Unit`.
 */
private val ExportedFun.safeCastType
    get() = if (returnType == UNIT)
        returnType.copy(nullable = true)
    else
        returnType

/**
 * Returns whether the function takes any arguments
 */
val ExportedFun.takesArgs get() = parameters.isNotEmpty()

/**
 * The JSON wrapper class name
 */
val ExportedFun.jsonClassName get() = "FnJson_$name"

/**
 * The name of the generated function
 */
val ExportedFun.generatedQualifiedName get() = "kono.generated.$name"

/**
 * Returns the property that contains a java.reflect.Method
 * for the synthetic Kotlin method that invokes default parameters
 */
val ExportedFun.reflectionPropertyName get() = "`synthetic$$name`"

/**
 * The name of the synthetic Kotlin function
 */
val ExportedFun.defaultSyntheticMethod get() = "$name\$default"

/**
 * Returns the expression for accessing all arguments, with names
 */
val ExportedFun.namedInvokeArgs
    get() = parameters
        .map { CodeBlock.of("${it.name} = %L", it.getAccessProperty()) }
        .joinToCode(", ")

/**
 * Returns the expression for accessing all arguments, with names
 */
val ExportedFun.unnamedInvokeArgs
    get() = parameters
        .map { it.getAccessProperty() }
        .joinToCode()

/**
 * Adds any relevant data for this function to the file builder
 */
fun ExportedFun.addTo(file: FileSpec.Builder) {
    val funBuilder = FunSpec.builder(name)
        .addOriginatingKSFile(function.containingFile!!)
        .addParameter("moshi", Moshi::class)
        .addParameter("request", RunFunctionRequest::class)
        .addParameter("context", FunctionContext::class)
        .returns(String::class)

    val code = CodeBlock.builder()

    // Function takes no arguments. Simplest case
    if (!takesArgs) {
        callFunction(code = code)
        file.addFunction(funBuilder.addCode(code.build()).build())
        return
    }

    // Function takes arguments. Generate a wrapper JSON class
    val jsonClass = createJsonClass()
    file.addType(jsonClass)

    if (takesArgs)
        code.addStatement("val functionData = moshi.adapterOf<%L>().fromJsonValue(request.data)!!", jsonClassName)

    // No default arguments. Easy case
    if (!containsDefaultParameters) {
        callFunction(code = code)
        file.addFunction(funBuilder.addCode(code.build()).build())
        return
    }

    // We have default parameters. Track the passed parameters and
    // generate an appropriate mask value
    val masker = Masker(count = parameters.size)

    // Create the synthetic reflection method property
    val reflectionMethod = createReflectionMethodProperty(masker.maskCount)
    file.addProperty(reflectionMethod)

    masker.addToCodeBlock(code)

    code.createFunctionWithDefaultArgs(masker = masker)
    file.addFunction(funBuilder.addCode(code.build()).build())
}

/**
 * Creates a function with default parameters
 */
context(ExportedFun)
private fun CodeBlock.Builder.createFunctionWithDefaultArgs(masker: Masker) {
    addComment("Track the passed parameters")
    addStatement("val passedParameters = request.passedParameters")
    addStatement("var name = passedParameters.removeFirstOrNull()")

    beginControlFlow("while (name != null)")
    beginControlFlow("when (name)")
    for (parameter in parameters) {
        // Ignore context parameters as they're not in JSON
        if (parameter.isFromContext) continue

        if (parameter.hasDefault) {
            beginControlFlow("%S ->", parameter.name)
            masker.updateMask()
            endControlFlow()
        }

        masker.updateMaskIndexes()
    }
    endControlFlow() // when (name)

    // Fetch the next parameter
    addStatement("name = passedParameters.removeFirstOrNull()")
    endControlFlow() // while (name != null)

    // Adds an if statement that checks if all fields are passed
    // (through the masker) and invokes the method normally
    checkIfAllFieldsArePassed(masker = masker)

    // If not all parameters were passed, call the method reflectively
    // instead, with the custom marker.
    elseCallMethodReflectively()
}

/**
 * Adds a control flow branch for calling the method reflectively
 * when not all parameters are passed.
 */
context(ExportedFun)
private fun CodeBlock.Builder.elseCallMethodReflectively() {
    // Not all parameters are passed. Invoke the synthetic method
    // reflectively
    addComment("Different parameters were used. Invoke the synthetic method")
    if (takesArgs)
        addStatement(
            "val result = %L.invoke(null, %L, mask0, DEFAULT_CONSTRUCTOR_MARKER) as %T",
            reflectionPropertyName,
            unnamedInvokeArgs,
            safeCastType
        )
    else
        addStatement(
            "val result = %L.invoke(null, mask0, DEFAULT_CONSTRUCTOR_MARKER) as %T",
            reflectionPropertyName,
            safeCastType
        )
    addStatement("return moshi.adapterOf<%T>().toJson(result)", returnType)

}

/**
 * Adds a code-block that checks if all fields are passed,
 * and if they are, invoke the method normally
 */
context(ExportedFun)
private fun CodeBlock.Builder.checkIfAllFieldsArePassed(masker: Masker) {
    val allFieldsArePassed = masker.allFieldsArePassed()
    beginControlFlow("if (%L)", allFieldsArePassed)

    addComment("All parameters are provided. No need to use the synthetic method!")
    callFunction(code = this)

    endControlFlow() // if (allFieldsArePassed)
}

/**
 * Returns the arguments for fetching the synthetic method
 */
private fun ExportedFun.createSyntheticMethodArgs(maskCount: Int): CodeBlock {
    return parameters
        .map { it.typeBlock }
        .plus(0.until(maskCount).map { INT_TYPE_BLOCK }) // Masks, one every 32 params
        .plus(DefaultConstructorMarkerType)  // Default constructor marker is always last
        .joinToCode(", ")
}

/**
 * Creates the lazy property that fetches the synthetic method
 * reflectively
 */
private fun ExportedFun.createReflectionMethodProperty(maskCount: Int): PropertySpec {
    val code = buildCodeBlock {
        beginControlFlow("lazy")
        addComment("Call the synthetic method produced by the Kotlin compiler")
        addStatement(
            "Class.forName(%S).getDeclaredMethod(%S, %L)",
            containingJavaClass,
            defaultSyntheticMethod,
            createSyntheticMethodArgs(maskCount)
        )
        endControlFlow()
    }
    return PropertySpec.builder(reflectionPropertyName, Method::class, KModifier.PRIVATE)
        .delegate(code)
        .build()
}

/**
 * Creates a class that contains all the non-context parameters of
 * the exported function.
 */
private fun ExportedFun.createJsonClass(): TypeSpec {
    val builder = TypeSpec.classBuilder(jsonClassName)
        .addAnnotation(JSON_CLASS_ADAPTER)

    val properties = parameters.asSequence().filter { !it.isFromContext }
        .map { it.toJsonConstructorParameter() }
        .toList()

    builder.primaryConstructor(properties)
    return builder.build()
}

/**
 * Creates a function that takes all arguments, with no default
 * ones
 */
private fun ExportedFun.callFunction(code: CodeBlock.Builder) {
    code.addStatement("val result = ${qualifiedName}(%L)", namedInvokeArgs)
    code.addStatement("return moshi.adapterOf<%T>().toJson(result)", returnType)
}
