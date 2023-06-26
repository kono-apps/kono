package kono.codegen.kpoet

import com.squareup.kotlinpoet.*

fun classBuilder(name: String, block: TypeSpec.Builder.() -> Unit): TypeSpec {
    val typeSpecBuilder = TypeSpec.classBuilder(name)
    typeSpecBuilder.block()
    return typeSpecBuilder.build()
}

fun CodeBlock.Builder.addComment(format: String, vararg args: Any) {
    add("//·${format.replace(' ', '·')}\n", *args)
}

fun annotationBuilder(type: ClassName, block: AnnotationSpec.Builder.() -> Unit): AnnotationSpec {
    val builder = AnnotationSpec.builder(type)
    builder.block()
    return builder.build()
}

fun funBuilder(name: String, block: FunSpec.Builder.() -> Unit): FunSpec {
    val builder = FunSpec.builder(name)
    builder.block()
    return builder.build()
}

//
//fun TypeSpec.Builder.primaryConstructor(properties: List<PropertySpec>): TypeSpec.Builder {
//    val propertySpecs = properties.map { p -> p.toBuilder().initializer(p.name).build() }
//    val parameters = propertySpecs.map { ParameterSpec.builder(it.name, it.type).build() }
//    val constructor = FunSpec.constructorBuilder()
//        .addParameters(parameters)
//        .build()
//
//    return this
//        .primaryConstructor(constructor)
//        .addProperties(propertySpecs)
//}

fun TypeSpec.Builder.primaryConstructor(parameters: List<ParameterSpec>): TypeSpec.Builder {
    val propertySpecs = parameters.map {
        PropertySpec.builder(it.name, it.type).initializer(it.name).build()
    }
    val constructor = FunSpec.constructorBuilder()
        .addParameters(parameters)
        .build()

    return this
        .primaryConstructor(constructor)
        .addProperties(propertySpecs)
}