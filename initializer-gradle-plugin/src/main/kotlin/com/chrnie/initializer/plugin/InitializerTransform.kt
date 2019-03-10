package com.chrnie.initializer.plugin

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.logging.Logger
import java.io.File
import java.io.FileInputStream
import java.util.jar.JarFile

class InitializerTransform(val logger: Logger) : Transform() {

    override fun getName(): String = InitializerTransform::class.simpleName!!

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        TransformManager.CONTENT_CLASS

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> =
        TransformManager.SCOPE_FULL_PROJECT

    override fun isIncremental(): Boolean = false

    override fun transform(invocation: TransformInvocation) = timer {
        val inputs = invocation.inputs
        val outputProvider = invocation.outputProvider

        var hookJar: File? = null
        val taskList = ArrayList<String>()

        inputs.forEach { input ->

            input.jarInputs.forEach { jarInput ->
                val jarFile = jarInput.file
                val destFile = outputProvider.getContentLocation(
                    jarInput.name,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )
                jarFile.copyTo(destFile, true)

                val zipFile = JarFile(jarFile)
                zipFile.entries().iterator()
                    .forEach { jarEntry ->
                        if (jarEntry.isDirectory || !jarEntry.name.endsWith(".class")) {
                            return
                        }

                        val inputStream = zipFile.getInputStream(jarEntry)
                        val result = ClassScanner.scan(inputStream)
                        when (result) {
                            is ClassScanner.Result.HookClass -> hookJar = destFile
                            is ClassScanner.Result.TaskClass -> taskList.add(result.className)
                        }
                    }
            }

            input.directoryInputs.forEach { directoryInput ->
                val directoryFile = directoryInput.file
                val destFile = outputProvider.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes,
                    directoryInput.scopes,
                    Format.DIRECTORY
                )
                directoryFile.copyRecursively(destFile, true)

                destFile.walkTopDown()
                    .filter { it.isFile }
                    .forEach { classFile ->
                        if (classFile.isDirectory || !classFile.name.endsWith(".class")) {
                            return
                        }

                        val inputStream = FileInputStream(classFile)
                        val result = ClassScanner.scan(inputStream)
                        when (result) {
                            is ClassScanner.Result.TaskClass -> taskList.add(result.className)
                        }
                    }
            }

        }

        val generator = CodeGenerator(hookJar!!, taskList)
        generator.generate()

        logger.quiet("[Initializer]: task:$taskList")
    }

    private inline fun timer(block: () -> Unit) {
        val startTime = System.currentTimeMillis()
        block()
        logger.quiet("[Initializer]: consume time:${System.currentTimeMillis() - startTime}")
    }
}