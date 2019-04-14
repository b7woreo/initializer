package com.chrnie.initializer.plugin

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.InputStream

interface ClassMetadata {
    val isPublic: Boolean
    val isAbstract: Boolean
    val name: String
    val superName: String?
    val outputFile: File
    val methods: List<MethodMetadata>
}

fun ClassMetadata(inputStream: InputStream, outputFile: File): ClassMetadata {
    val cr = ClassReader(inputStream)
    return AsmClassMetadata(cr, outputFile)
}

interface MethodMetadata {
    val isPublic: Boolean
    val name: String
    val descriptor: String
}

private class AsmClassMetadata(
    private val cr: ClassReader,
    override val outputFile: File
) : ClassMetadata {
    override val isPublic: Boolean = cr.access and Opcodes.ACC_PUBLIC != 0
    override val isAbstract: Boolean = cr.access and Opcodes.ACC_ABSTRACT != 0
    override val name: String = cr.className
    override val superName: String? = cr.superName
    override val methods: List<MethodMetadata> by lazy {
        MethodMetadataClassVisitor().methods(cr)
    }

    private class MethodMetadataClassVisitor : ClassVisitor(Opcodes.ASM5) {
        private var _methods: Sequence<MethodMetadata>? = null

        fun methods(cr: ClassReader): List<MethodMetadata> {
            if (_methods != null) {
                throw RuntimeException("only call once")
            }
            cr.accept(this, 0)
            return _methods!!.toList()
        }

        override fun visitMethod(
            access: Int,
            name: String,
            descriptor: String,
            signature: String?,
            exceptions: Array<out String>?
        ): MethodVisitor? {
            _methods = (_methods ?: emptySequence()) + AsmMethodMetadata(
                access and Opcodes.ACC_PUBLIC != 0,
                name,
                descriptor
            )
            return null
        }
    }
}

private class AsmMethodMetadata(
    override val isPublic: Boolean,
    override val name: String,
    override val descriptor: String
) : MethodMetadata
