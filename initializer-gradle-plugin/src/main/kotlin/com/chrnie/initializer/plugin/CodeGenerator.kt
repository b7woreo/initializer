package com.chrnie.initializer.plugin

import org.objectweb.asm.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class CodeGenerator(private val outputFile: File, private val taskNameList: List<String>) {

    fun generate() {
        val optJar = File(outputFile.parent, "${outputFile.name}.opt")
        if (optJar.exists()) {
            optJar.delete()
        }

        val jarOutputStream = JarOutputStream(FileOutputStream(optJar))

        val jarFile = JarFile(outputFile)
        jarFile.entries().iterator()
            .forEach { jarEntry ->
                val entryName = jarEntry.name
                val zipEntry = ZipEntry(entryName)
                val inputStream = jarFile.getInputStream(zipEntry)

                jarOutputStream.putNextEntry(zipEntry)

                if (HOOK_CLASS_FILE_NAME == entryName) {
                    val bytes = hackMethod(inputStream)
                    jarOutputStream.write(bytes)
                } else {
                    inputStream.copyTo(jarOutputStream)
                }

                inputStream.close()
                jarOutputStream.closeEntry()
            }

        jarOutputStream.close()
        jarFile.close()

        if (outputFile.exists()) {
            outputFile.delete()
        }
        optJar.renameTo(outputFile)
    }

    private fun hackMethod(inputStream: InputStream): ByteArray {
        val cr = ClassReader(inputStream)
        val cw = ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
        val cv = GeneratorClassVisitor(cw)
        cr.accept(cv, 0)
        return cw.toByteArray()
    }

    private inner class GeneratorClassVisitor(cw: ClassWriter) : ClassVisitor(Opcodes.ASM5, cw) {
        override fun visitMethod(
            access: Int,
            name: String?,
            descriptor: String?,
            signature: String?,
            exceptions: Array<out String>?
        ): MethodVisitor {
            var mv = super.visitMethod(access, name, descriptor, signature, exceptions)
            if (HOOK_METHOD_NAME == name) {
                mv = GeneratorMethodVisitor(mv)
            }
            return mv
        }
    }

    private inner class GeneratorMethodVisitor(mv: MethodVisitor) :
        MethodVisitor(Opcodes.ASM5, mv) {
        override fun visitInsn(opcode: Int) {
            if (Opcodes.RETURN == opcode) {
                insertCode()
            }
            super.visitInsn(opcode)
        }

        private fun insertCode() {
            taskNameList.forEach { taskName ->
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitTypeInsn(Opcodes.NEW, taskName)
                mv.visitInsn(Opcodes.DUP)
                mv.visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    taskName,
                    "<init>",
                    "()V",
                    false
                )
                mv.visitMethodInsn(
                    Opcodes.INVOKEINTERFACE,
                    "java/util/List",
                    "add",
                    "(Ljava/lang/Object;)Z",
                    true
                )
                mv.visitInsn(Opcodes.POP)
            }
        }
    }
}
