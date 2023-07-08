package kono.codegen.util

import com.google.devtools.ksp.processing.CodeGenerator
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo
import kono.app.KonoApplication
import kono.events.EventHandler
import kono.generated.GeneratedItem
import kotlin.reflect.KClass

/**
 * Creates a type that implements [GeneratedItem] with
 * a custom `create` function
 */
fun CodeGenerator.createGeneratedItem(
    forType: KClass<*>,
    generator: FunSpec.Builder.() -> Unit,
) {
    val generatedClassName = "Generated${forType.simpleName}"

    val createFunction = FunSpec.builder("create")
        .addModifiers(KModifier.OVERRIDE)
        .addParameter("app", KonoApplication::class)
        .returns(forType)

    createFunction.generator()

    val type = TypeSpec.classBuilder(generatedClassName)
        .addSuperinterface(GeneratedItem::class.parameterizedBy(forType))
        .addFunction(createFunction.build())
    FileSpec.builder("kono.generated", generatedClassName)
        .addType(type.build())
        .build()
        .writeTo(codeGenerator = this, aggregating = true)
}