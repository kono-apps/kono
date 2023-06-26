package kono.codegen.util

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

/**
 * Walks up the hierarchy of classes to find the reflection name in the
 * form of `TopClass$BottomClass`
 */
internal val KSClassDeclaration.reflectionName: String
    get() {
        var mostTop: KSDeclaration? = this
        val string = StringBuilder()
        while (mostTop != null) {
            string.insert(0, mostTop.simpleName.asString() + '$')
            mostTop = mostTop.parentDeclaration
        }
        if (string.endsWith('$'))
            string.deleteCharAt(string.length - 1)
        if (packageName.asString().isNotBlank())
            string.insert(0, packageName.asString() + '.')
        return string.toString()
    }

/**
 * Infers the underlying JVM class name for the function. This will allow us to invoke it
 * reflectively as needed
 */
@OptIn(KspExperimental::class)
internal fun KSFunctionDeclaration.inferJVMClass(): String {
    val dec = parentDeclaration
    if (dec != null) {
        if (dec is KSClassDeclaration) // Function is inside a class.
            return dec.reflectionName
        else
            error("cannot export non-class functions or non-top-level functions")
    }

    // Function is top-level
    val file = containingFile ?: error("You can't export functions that aren't from a source file!")

    // check if it has @file:JvmName("some name")
    val jvmName = file.getAnnotationsByType(JvmName::class).firstOrNull()
    if (jvmName != null)
        return jvmName.name

    val jvmClassName = run {
        val text = file.fileName.removeSuffix(".kt").map {
            if (it.isLetterOrDigit() || it == '_')
                it
            else
                '_'
        }.toMutableList()
        text[0] = text[0].uppercaseChar()
        text.add('K')
        text.add('t')
        String(text.toCharArray())
    }

    val packageName = file.packageName.asString()
    return "$packageName.$jvmClassName"
}
