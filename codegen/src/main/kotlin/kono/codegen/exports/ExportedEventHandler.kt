package kono.codegen.exports

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import kono.events.EventHandler
import kono.events.Listener
import kono.export.ExportEvent

@OptIn(KspExperimental::class)
fun FunSpec.Builder.createEventHandler(
    exportedEvents: Sequence<KSAnnotated>,
    resolver: Resolver,
    logger: KSPLogger,
): FunSpec {

    val registeredEvents = mutableMapOf<String, String>()
    return addCode(buildCodeBlock {
        addStatement("val eventHandler = %T(app)", EventHandler::class)
        addStatement("return eventHandler")
        beginControlFlow(".also")
        for (event in exportedEvents) {
            if (event !is KSClassDeclaration) continue
            val typeName = event.toClassName()
            val id = event.getAnnotationsByType(ExportEvent::class).first().id
                .ifBlank { typeName.simpleName }
            if (registeredEvents.containsKey(id))
                logger.error("Duplicate event ID: $id", event)
            registeredEvents[typeName.canonicalName] = id
            addStatement("it.addEvent<%T>(id = %S)", typeName, id)
            addOriginatingKSFile(event.containingFile!!)
        }

        for (listener in resolver.getSymbolsWithAnnotation(Listener::class.qualifiedName!!)) {
            if (listener !is KSFunctionDeclaration) continue

            if (!listener.isPublic())
                logger.error("Listener function must be public!", listener)

            val params = listener.parameters.map { FunParameter(it) }
            if (params.count { it.contextItem == null } > 1)
                logger.error("There are more than 1 event type in the listener function", listener)

            val eventParam = params.firstOrNull { it.contextItem == null }
            if (eventParam == null) {
                logger.error(
                    "Cannot infer event class for listener. There should be at least 1 parameter for the event",
                    listener
                )
                break // <--- never called
            }

            val eventType = eventParam.type.toClassName()
            val name: String? = listener.getAnnotationsByType(Listener::class).first().event.ifBlank {
                registeredEvents[eventType.canonicalName]
            }

            if (name == null)
                logger.error("No such event registered for type ${eventType.canonicalName}", listener)

            addStatement("it.listener<%T> { event, context -> ", eventType)
            addStatement("%L(%L)", listener.qualifiedName!!.asString(), params.joinToString(",") {
                if (it.typeName == eventType)
                    "event"
                else
                    "context${it.contextItem!!.access}"
            })

            addStatement("}") // it.listener
            addOriginatingKSFile(listener.containingFile!!)
        }

        endControlFlow() // .also {}
    }).build()
}