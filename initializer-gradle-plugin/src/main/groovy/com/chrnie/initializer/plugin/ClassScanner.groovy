package com.chrnie.initializer.plugin

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

import java.util.jar.JarFile

class ClassScanner {

    static void scanJar(File jarFile, Closure closure) {
        def zipFile = new JarFile(jarFile)

        zipFile.entries().each { jarEntry ->
            if (jarEntry.directory || !jarEntry.name.endsWith('.class')) {
                return
            }

            def inputSteam = zipFile.getInputStream(jarEntry)
            scan(inputSteam, closure)
            inputSteam.close()
        }
    }

    static void scanClass(File classFile, Closure closure) {
        if (classFile.directory || !classFile.name.endsWith(".class")) {
            return
        }

        def inputStream = new FileInputStream(classFile)
        scan(inputStream, closure)
        inputStream.close()
    }

    private static void scan(InputStream inputStream, Closure closure) {
        def cr = new ClassReader(inputStream)
        def cv = new ScannerClassVisitor(closure)
        cr.accept(cv, 0)
    }

    private static class ScannerClassVisitor extends ClassVisitor {

        private final Closure closure

        private ScannerClassVisitor(Closure closure) {
            super(Opcodes.ASM5)
            this.closure = closure
        }

        @Override
        void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            closure(name, superName, interfaces)
        }
    }
}
