package com.chrnie.initializer.plugin

import com.chrnie.initializer.plugin.exception.IllegalTaskException
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.io.InputStream

object ClassScanner {

    private val hookChecker = HookChecker()
    private val taskChecker = TaskChecker()

    fun scan(inputStream: InputStream): Result? {
        val cr = ClassReader(inputStream)

        if (hookChecker.isHookClass(cr)) {
            return ClassScanner.Result.HookClass
        }

        if (taskChecker.isTaskClass(cr)) {
            return ClassScanner.Result.TaskClass(cr.className)
        }

        return null
    }


    sealed class Result {

        object HookClass : Result()

        class TaskClass(val className: String) : Result()
    }

    private class HookChecker : ClassVisitor(Opcodes.ASM5) {

        private var hookClass = false

        override fun visit(
            version: Int,
            access: Int,
            name: String,
            signature: String?,
            superName: String?,
            interfaces: Array<out String>?
        ) {
            hookClass = HOOK_CLASS_NAME == name
            super.visit(version, access, name, signature, superName, interfaces)
        }

        fun isHookClass(cr: ClassReader): Boolean {
            hookClass = false
            cr.accept(this, 0)
            return hookClass
        }
    }

    private class TaskChecker : ClassVisitor(Opcodes.ASM5) {

        private var taskClass = false
        private var publicClass = false
        private var abstractClass = false
        private var publicNoneParamConstructor = false

        override fun visit(
            version: Int,
            access: Int,
            name: String,
            signature: String?,
            superName: String?,
            interfaces: Array<out String>?
        ) {
            taskClass = TASK_CLASS_SUPER_NAME == superName
            publicClass = access and Opcodes.ACC_PUBLIC != 0
            abstractClass = access and Opcodes.ACC_ABSTRACT != 0
        }

        override fun visitMethod(
            access: Int,
            name: String,
            descriptor: String,
            signature: String?,
            exceptions: Array<out String>?
        ): MethodVisitor? {
            if ("<init>" == name) {
                val public = access or Opcodes.ACC_PUBLIC != 0
                val noneParam = "()V" == descriptor
                publicNoneParamConstructor = (public && noneParam) || publicNoneParamConstructor
            }
            return null
        }

        fun isTaskClass(cr: ClassReader): Boolean {
            taskClass = false
            publicClass = false
            abstractClass = false
            publicNoneParamConstructor = false

            cr.accept(this, 0)

            if (!taskClass) {
                return false
            }

            if (abstractClass) {
                return false
            }

            if (!publicClass) {
                throw IllegalTaskException("none-abstract task class must be public")
            }

            if (!publicNoneParamConstructor) {
                throw IllegalTaskException("none-abstract task class must has public none-param constructor")
            }

            return true
        }
    }
}