package com.chrnie.initializer.plugin

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.chrnie.initializer.plugin.exception.IllegalTaskException
import org.gradle.api.logging.Logger
import java.io.File
import java.io.FileInputStream
import java.util.jar.JarFile

class InitializerTransform(private val logger: Logger) : Transform() {

    override fun getName(): String = InitializerTransform::class.simpleName!!

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        TransformManager.CONTENT_CLASS

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> =
        TransformManager.SCOPE_FULL_PROJECT

    override fun isIncremental(): Boolean = false

    override fun transform(invocation: TransformInvocation) = timer {
        val inputs = invocation.inputs
        val outputProvider = invocation.outputProvider

        val classList = inputs.asSequence()
            .flatMap { input ->
                sequenceOf(
                    input.jarInputs
                        .asSequence()
                        .map { jarInput ->
                            outputProvider.getContentLocation(
                                jarInput.name,
                                jarInput.contentTypes,
                                jarInput.scopes,
                                Format.JAR
                            ).also { jarInput.file.copyTo(it, true) }
                        }.flatMap { findClassInJarFile(it) },
                    input.directoryInputs
                        .asSequence()
                        .map { directoryInput ->
                            outputProvider.getContentLocation(
                                directoryInput.name,
                                directoryInput.contentTypes,
                                directoryInput.scopes,
                                Format.DIRECTORY
                            ).also { directoryInput.file.copyRecursively(it, true) }
                        }.flatMap { findClassInDirectory(it) }
                ).flatten()
            }
            .toList()

        val taskNameList = findAllTaskName(classList)
        logger.quiet("[Initializer] task: $taskNameList")

        classList.firstOrNull { HOOK_CLASS_NAME == it.name }
            ?.let { CodeGenerator(it.outputFile, taskNameList) }
            ?.generate()
            ?: throw IllegalStateException("can not found $HOOK_CLASS_FILE_NAME")
    }

    private fun findClassInJarFile(file: File): Sequence<ClassMetadata> {
        return JarFile(file).let { jarFile ->
            jarFile.entries()
                .asSequence()
                .filter { !it.isDirectory and it.name.endsWith(".class") }
                .map { jarFile.getInputStream(it) }
                .map { ClassMetadata(it, file) }
        }
    }

    private fun findClassInDirectory(file: File): Sequence<ClassMetadata> {
        return file.walkTopDown()
            .asSequence()
            .filter { it.isFile and it.name.endsWith(".class") }
            .map { FileInputStream(it) }
            .map { ClassMetadata(it, file) }
    }

    private fun findAllTaskName(list: List<ClassMetadata>): List<String> {
        fun traversTaskMetadata(
            superName: String,
            superNameToMetadata: Map<String, List<ClassMetadata>>
        ): Sequence<ClassMetadata> {
            return superNameToMetadata[superName]
                ?.asSequence()
                ?.flatMap { traversTaskMetadata(it.name, superNameToMetadata) + it }
                ?: emptySequence()
        }

        fun ensureTaskClassValid(classMetadata: ClassMetadata) {
            if (!classMetadata.isPublic) {
                throw IllegalTaskException("task class is not public access: ${classMetadata.name}")
            }

            if (classMetadata.isAbstract) {
                throw IllegalTaskException("task class is abstract: ${classMetadata.name}")
            }

            classMetadata.methods.firstOrNull { methodMetadata ->
                val constructor = methodMetadata.name == "<init>"
                val noneParam = methodMetadata.descriptor == "()V"
                val public = methodMetadata.isPublic
                constructor and noneParam and public
            }
                ?: throw IllegalTaskException("task class has not public empty constructor: ${classMetadata.name}")
        }

        return list.filter { it.superName != null }
            .groupBy { it.superName!! }
            .let { traversTaskMetadata(TASK_CLASS_SUPER_NAME, it) }
            .filter { !it.isAbstract }
            .map { it.apply { ensureTaskClassValid(this) } }
            .map { it.name }
            .toList()
    }

    private inline fun timer(block: () -> Unit) {
        val startTime = System.currentTimeMillis()
        block()
        logger.quiet("[Initializer] consume time:${System.currentTimeMillis() - startTime}")
    }
}
