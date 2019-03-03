package com.chrnie.initializer.plugin

import org.apache.commons.io.IOUtils
import org.objectweb.asm.*

import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class MethodCodeGenerator {

    private final File jarFile
    private final List<String> taskNameList

    MethodCodeGenerator(File jarFile, List<String> taskClassNameList) {
        this.jarFile = jarFile
        this.taskNameList = taskClassNameList
    }

    def generate() {
        def optJar = new File(jarFile.parent, "${jarFile.name}.opt")
        if (optJar.exists()) {
            optJar.delete()
        }

        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))

        def file = new JarFile(jarFile)
        file.entries().each { jarEntry ->
            def entryName = jarEntry.name
            def zipEntry = new ZipEntry(entryName)
            def inputStream = file.getInputStream(zipEntry)
            jarOutputStream.putNextEntry(zipEntry)

            if (Const.HOOK_CLASS_FILE_NAME == entryName) {
                def bytes = hackMethod(inputStream)
                jarOutputStream.write(bytes)
            } else {
                IOUtils.copy(inputStream, jarOutputStream)
            }
            inputStream.close()
            jarOutputStream.closeEntry()
        }

        jarOutputStream.close()
        file.close()

        if (jarFile.exists()) {
            jarFile.delete()
        }
        optJar.renameTo(jarFile)
    }

    private byte[] hackMethod(InputStream inputStream) {
        def cr = new ClassReader(inputStream)
        def cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
        def cv = new GeneratorClassVisitor(cw)
        cr.accept(cv, 0)
        return cw.toByteArray()
    }

    private class GeneratorClassVisitor extends ClassVisitor {

        GeneratorClassVisitor(ClassVisitor cv) {
            super(Opcodes.ASM5, cv)
        }

        @Override
        MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            def mv = super.visitMethod(access, name, desc, signature, exceptions)
            if (name == Const.HOOK_METHOD_NAME) {
                mv = new GeneratorMethodVisitor(mv)
            }
            return mv
        }
    }

    private class GeneratorMethodVisitor extends MethodVisitor {

        GeneratorMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM5, mv)
        }

        @Override
        void visitInsn(int opcode) {
            if (opcode == Opcodes.RETURN) {
                insertCode()
            }
            super.visitInsn(opcode)
        }

        private void insertCode() {
            taskNameList.each { taskName ->
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitTypeInsn(Opcodes.NEW, taskName)
                mv.visitInsn(Opcodes.DUP)
                mv.visitMethodInsn(
                        Opcodes.INVOKESPECIAL,
                        taskName,
                        '<init>',
                        "()V",
                        false
                )
                mv.visitMethodInsn(
                        Opcodes.INVOKEINTERFACE,
                        'java/util/List',
                        'add',
                        '(Ljava/lang/Object;)Z',
                        true
                )
                mv.visitInsn(Opcodes.POP)
            }
        }
    }

}