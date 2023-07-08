package kono.codegen.util

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.NameAllocator
import com.squareup.kotlinpoet.joinToCode

/**
 * The int type preceding the default marker parameter
 */
val INT_TYPE_BLOCK = CodeBlock.of("%T::class.javaPrimitiveType", INT)

/**
 * The type representing the default marker parameter
 */
val DefaultConstructorMarkerType = CodeBlock.of("java.lang.Object::class.java")

/**
 * A utility for tracking masks for invoking functions that contain
 * default parameters
 */
class Masker(
    count: Int,
    private val nameAllocator: NameAllocator = NameAllocator(),
) {

    /**
     * Calculate how many masks we'll need. Round up if it's not
     * evenly divisible by 32
     */
    val maskCount = if (count == 0) {
        0
    } else {
        (count + 31) / 32
    }

    val maskNames = Array(maskCount) { index -> nameAllocator.newName("mask$index") }
    val maskAllSetValues = Array(maskCount) { -1 }
    var maskIndex = 0
    var maskNameIndex = 0

    fun updateMaskIndexes() {
        maskIndex++
        if (maskIndex == 32) {
            // Move to the next mask
            maskIndex = 0
            maskNameIndex++
        }
    }
}

/**
 * Adds all properties to the code block
 */
fun Masker.addToCodeBlock(code: CodeBlock.Builder) {
    for (maskName in maskNames) {
        code.addStatement("var %L = -1", maskName)
    }
}

/**
 * Update the mask value to indicate that a field is present
 */
context(CodeBlock.Builder)
fun Masker.updateMask() {
    val inverted = (1 shl maskIndex).inv()
    maskAllSetValues[maskNameIndex] = maskAllSetValues[maskNameIndex] and inverted
    addComment("\$mask = \$mask and (1 shl %L).inv()", maskIndex)
    addStatement(
        "%1L = %1L and 0x%2L.toInt()",
        maskNames[maskNameIndex],
        Integer.toHexString(inverted),
    )
}

/**
 * Creates a [CodeBlock] that represents the bitmask value that
 * all fields are passed.
 */
fun Masker.allFieldsArePassed(): CodeBlock {
    return maskNames.withIndex().map { (index, maskName) ->
        CodeBlock.of("$maskName·== 0x${Integer.toHexString(maskAllSetValues[index])}.toInt()")
    }.joinToCode("·&& ")
}