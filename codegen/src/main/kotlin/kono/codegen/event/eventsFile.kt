package kono.codegen.event

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.joinToCode
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.toClassName
import kono.codegen.functions.ExportedFun
import kono.codegen.functions.getAccessProperty
import kono.codegen.functions.isFromContext
import kono.codegen.util.*
import kono.events.EventHandler
import kono.events.Listener
import kono.export.ExportEvent

@OptIn(KspExperimental::class)
fun createEventsHandler(
    resolver: Resolver,
    codeGenerator: CodeGenerator,
    logger: KSPLogger,
) {
    val eventClassToId = mutableMapOf<String, String>()

    codeGenerator.createGeneratedItem(forType = EventHandler::class) {
        beginControlFlow("return %T(app).also", EventHandler::class)
        for (event in resolver.getSymbolsWithAnnotation<ExportEvent>()) {
            if (event !is KSClassDeclaration)
                continue
            registerEvent(
                event = event,
                eventClassToId = eventClassToId,
                logger = logger
            )
        }

        for (listener in resolver.getSymbolsWithAnnotation<Listener>()) {
            if (listener !is KSFunctionDeclaration)
                continue

            if (!listener.isPublic())
                logger.error("Listener function must be public!", listener)

            if (listener.isSuspend())
                logger.error("Listener function cannot be marked as suspend!", listener)

            if (listener.isReservedPackageName())
                logger.error("Package name '$RESERVED_PACKAGE' is reserved and may not be used.", listener)

            val function = ExportedFun(
                function = listener,
                containingJavaClass = resolver.getOwnerJvmClassName(listener)!!
            )

            registerListener(
                listener = function,
                eventClassToId = eventClassToId,
                logger = logger
            )
        }
        endControlFlow() // EventHandler()
    }
}

private val EventCodeBlock = CodeBlock.of("event")

/**
 * Registers the given listener to the event handler
 */
private fun FunSpec.Builder.registerListener(
    listener: ExportedFun,
    eventClassToId: Map<String, String>,
    logger: KSPLogger,
) {
    val eventType = listener.determineEventType(logger = logger)

    val id: String? = listener.function.annotation<Listener>().event.ifBlank {
        eventClassToId[eventType.canonicalName]
    }

    if (id == null)
        logger.error("No such event registered for type ${eventType.canonicalName}", listener.function)

    beginControlFlow("it.listener<%T> { event, context -> ", eventType)

    addStatement("%L(%L)", listener.qualifiedName, listener.parameters.map {
        if (it.isFromContext)
            it.getAccessProperty()
        else
            EventCodeBlock
    }.joinToCode())

    endControlFlow()
}

/**
 * Determines the event type in the function
 */
private fun ExportedFun.determineEventType(logger: KSPLogger): ClassName {
    val nonContext = parameters.count { !it.isFromContext }
    if (nonContext == 0)
        logger.error("Listener has no parameters! Unable to determine the event type.", function)
    else if (nonContext != 1)
        logger.error("Listener has more than one non-context parameter! Unable to determine the event type.", function)
    return parameters.first { !it.isFromContext }.type.toClassName()
}

/**
 * Registers the given event to the event handler
 */
private fun FunSpec.Builder.registerEvent(
    event: KSClassDeclaration,
    eventClassToId: MutableMap<String, String>,
    logger: KSPLogger,
) {
    val eventType = event.toClassName()
    val id = event.annotation<ExportEvent>().id.ifBlank { eventType.simpleName }

    if (eventClassToId.containsValue(id))
        logger.error("Duplicate event ID: $id", event)

    eventClassToId[eventType.canonicalName] = id
    addStatement("it.addEvent<%T>(id = %S)", eventType, id)
    addOriginatingKSFile(event.containingFile!!)
}
